(ns cljs.atom.view
  (:require [promesa.core :as p]))

(def startWindowTime (.now js/Date))

(def electron (js/require "electron"))

(def path (js/require "path"))

(def Module (js/require "module"))

(defn ^js getWindowLoadSettings []
  ((js/require "../src/get-window-load-settings")))

(def getReleaseChannel
  (js/require "../src/get-release-channel"))

(def StartupTime (js/require "../src/startup-time"))

(def entryPointDirPath js/__dirname)

(def blobStore nil)

(def useSnapshot false)

(def startupMarkers
  (.-startupMarkers (.getCurrentWindow (.-remote electron))))

(when startupMarkers
  (.importData StartupTime startupMarkers))

(.addMarker StartupTime "window:start" startWindowTime)

(defn setLoadTime [loadTime]
  (when (.-atom js/global)
    (aset (.-atom js/global) "loadTime" loadTime)))

(defn handleSetupError
  [error]
  (let [currentWindow (.getCurrentWindow (.-remote electron))]
    (.setSize currentWindow 800 600)
    (.center currentWindow)
    (.show currentWindow)
    (.openDevTools currentWindow)
    (.error js/console (or (.-stack error) error))))

(defn setupAtomHome
  []
  (when (.-ATOM_HOME (.-env js/process)) (js* "return"))
  (when (and (getWindowLoadSettings)
             (.-atomHome (getWindowLoadSettings)))
    (aset (.-env js/process)
          "ATOM_HOME"
          (.-atomHome (getWindowLoadSettings)))))

(def ^:private initialize
  (let [init-script-path (.relative
                          path
                          entryPointDirPath
                          (.-windowInitializationScript
                           (getWindowLoadSettings)))]
    (if useSnapshot
      (.customRequire js/snapshotResult
                      init-script-path)
      (js/require init-script-path))))

(defn- init-editor! []
  (p/do!
    (initialize #js {:blobStore blobStore})
    (.addMarker StartupTime "window:initialize:end")
    (.send (.-ipcRenderer electron) "window-command" "window:loaded")))

(defn setupWindow
  []
  (let [CompileCache (if useSnapshot
                       (.customRequire js/snapshotResult "../src/compile-cache.js")
                       (js/require "../src/compile-cache"))
        ModuleCache (if useSnapshot
                      (.customRequire js/snapshotResult "../src/module-cache.js")
                      (js/require "../src/module-cache"))
        startCrashReporter
        (if useSnapshot
          (.customRequire js/snapshotResult "../src/crash-reporter-start.js")
          (js/require "../src/crash-reporter-start"))
        Grim (if useSnapshot
               (.customRequire js/snapshotResult "../node_modules/grim/lib/grim.js")
               (js/require "grim"))
        documentRegisterElement (.-registerElement js/document)
        --temp1 (getWindowLoadSettings)
        userSettings (.-userSettings --temp1)
        appVersion (.-appVersion --temp1)
        uploadToServer (and userSettings
                            (.-core userSettings)
                            (= (.-telemetryConsent (.-core userSettings)) "limited"))
        releaseChannel (getReleaseChannel appVersion)
        CSON (if useSnapshot
               (.customRequire
                js/snapshotResult
                "../node_modules/season/lib/cson.js")
               (js/require "season"))]
    (.setAtomHomeDirectory CompileCache
                           (.-ATOM_HOME (.-env js/process)))
    (.install CompileCache
              (.-resourcesPath js/process)
              js/require)
    (.register ModuleCache (getWindowLoadSettings))
    (if useSnapshot
      (.customRequire
       js/snapshotResult
       "../node_modules/document-register-element/build/document-register-element.node.js")
      (js/require "document-register-element"))
    (aset
     js/document
     "registerElement"
     (fn [type options]
       (.deprecate
        Grim
        "Use `customElements.define` instead of `document.registerElement` see https://javascript.info/custom-elements")
       (documentRegisterElement type options)))
    (startCrashReporter #js
                         {:uploadToServer uploadToServer,
                          :releaseChannel releaseChannel})
    (.setCacheDir
     CSON
     (.join path (.getCacheDirectory CompileCache) "cson"))
    (.addMarker StartupTime "window:initialize:start")
    (init-editor!)))

(defn profileStartup [initialTime]
  (let [webContents (.-webContents (.getCurrentWindow (.-remote electron)))
        profile (fn []
                  (let [startTime (.now js/Date)]
                    (.profile js/console "startup")
                    (.then
                     (setupWindow)
                     (fn []
                       (setLoadTime (+ (- (.now js/Date) startTime)
                                       initialTime))
                       (.profileEnd js/console "startup")
                       (.log
                        js/console
                        "Switch to the Profiles tab to view the created startup profile")))))]
    (if (.-devToolsWebContents webContents)
      (profile)
      (do (.once webContents
                 "devtools-opened"
                 (fn [] (js/setTimeout profile 1000)))
        (.openDevTools webContents)))))

(defn start! []
  (prn :HELLO)
  (aset
   js/window
   "onload"
   (fn []
     (try
       (let [startTime (.now js/Date)
             devMode (or (.-devMode (getWindowLoadSettings))
                         (not (.startsWith
                               (.-resourcePath (getWindowLoadSettings))
                               (+ (.-resourcesPath js/process)
                                  (.-sep path)))))
             FileSystemBlobStore
             (if useSnapshot
               (.customRequire
                js/snapshotResult
                "../src/file-system-blob-store.js")
               (js/require "../src/file-system-blob-store"))
             NativeCompileCache
             (if useSnapshot
               (.customRequire
                js/snapshotResult
                "../src/native-compile-cache.js")
               (js/require "../src/native-compile-cache"))]
         (.addMarker StartupTime "window:onload:start")
         (.on
          js/process
          "unhandledRejection"
          (fn [error promise]
            (.error
             js/console
             "Unhandled promise rejection %o with error: %o"
             promise
             error)))
         (aset js/process
               "resourcesPath"
               (.normalize path (.-resourcesPath js/process)))
         (setupAtomHome)
         (set! useSnapshot
           (and (not devMode)
                (not (undefined? js/snapshotResult))))
         (if devMode
           (let [metadata (js/require "../package.json")]
             (when (not (.-_deprecatedPackages metadata))
               (try
                 (aset metadata
                       "_deprecatedPackages"
                       (js/require
                        "../script/deprecated-packages.json"))
                 (catch :default requireError
                   (.error
                    js/console
                    "Failed to setup deprecated packages list"
                    (.-stack requireError))))))
           (when useSnapshot
             (do (aset
                  (.-prototype Module)
                  "require"
                  (fn [module]
                    (let [absoluteFilePath (._resolveFilename
                                            Module
                                            module
                                            (js* "this")
                                            false)
                          relativeFilePath (.relative
                                            path
                                            entryPointDirPath
                                            absoluteFilePath)
                          cachedModule
                          (aget (.-cache (.-customRequire
                                          js/snapshotResult))
                                relativeFilePath)]
                      (when (= (.-platform js/process) "win32")
                        (def relativeFilePath
                          (.replace relativeFilePath
                                    (js* "/\\\\/g")
                                    "/")))
                      (when (not cachedModule)
                        (def cachedModule
                          #js
                          {:exports (._load Module
                                            module
                                            (js* "this")
                                            false)})
                        (aset (.-cache (.-customRequire js/snapshotResult))
                              relativeFilePath
                              cachedModule))
                      (.-exports cachedModule))))
               (.setGlobals js/snapshotResult
                            js/global
                            js/process
                            js/window
                            js/document
                            js/console
                            js/require))))
         (def FileSystemBlobStore FileSystemBlobStore)
         (set! blobStore
           (.load FileSystemBlobStore
                  (.join path
                         (.-ATOM_HOME (.-env js/process))
                         "blob-store")))
         (.setCacheStore NativeCompileCache blobStore)
         (.setV8Version NativeCompileCache
                        (.-v8 (.-versions js/process)))
         (.install NativeCompileCache)
         (if (.-profileStartup (getWindowLoadSettings))
           (profileStartup (- (.now js/Date) startTime))
           (do
             (.addMarker StartupTime
                         "window:setup-window:start")
             (.then (setupWindow)
                    (fn []
                      (.addMarker StartupTime
                                  "window:setup-window:end")))
             (setLoadTime (- (.now js/Date) startTime)))))
       (catch :default error (handleSetupError error)))
     (.addMarker StartupTime "window:onload:end"))))

; (defn start! [])
; (start!)

(defn ^:dev/before-load stop []
  (js/console.log "stop"))

(defn ^:dev/before-load start []
  (js/console.log "start"))

(defn ^:dev/after-load after-load []
  ; (p/do!
   ; (init-editor!)
   (println "Reloaded the Atom Editor"))
   ; (done)))

; (cljs.atom.view/after-load)
