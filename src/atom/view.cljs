(ns atom.view
  (:require [promesa.core :as p]
            [atom.text-editor :refer [TextEditor]]
            ["electron" :as electron]
            ["path" :as path]
            ["module" :as Module]
            ["../startup-time" :as startup-time]
            ["../get-release-channel" :as get-release-channel]
            ["../get-window-load-settings" :as get-load-fn]))

(prn :LOL TextEditor)
(def startWindowTime (.now js/Date))

(defn ^js getWindowLoadSettings []
  (get-load-fn))

(def entryPointDirPath js/__dirname)

(def blobStore nil)

(def startupMarkers
  (.-startupMarkers (.getCurrentWindow (.-remote electron))))

(when startupMarkers
  (.importData startup-time startupMarkers))

(.addMarker startup-time "window:start" startWindowTime)

(defn setLoadTime [loadTime]
  (when (.-atom js/global)
    (set! (.. js/global -atom  -loadTime) loadTime)))

(defn handleSetupError [error]
  (let [currentWindow (.getCurrentWindow (.-remote electron))]
    (.setSize currentWindow 800 600)
    (.center currentWindow)
    (.show currentWindow)
    (.openDevTools currentWindow)
    (.error js/console (or (.-stack error) error))))

(defn setupAtomHome []
  (when-not (.-ATOM_HOME (.-env js/process))
    (when-let [home (and (getWindowLoadSettings)
                         (.-atomHome (getWindowLoadSettings)))]
      (set! (.. js/process -env -ATOM_HOME) home))))

(def ^:private initialize
  (delay
   (let [init-script-path (.relative
                           path
                           entryPointDirPath
                           (.-windowInitializationScript
                            (getWindowLoadSettings)))]
     (js/require init-script-path))))

(defn init-editor []
  (p/do!
   (@initialize #js {:blobStore blobStore})
   (.addMarker startup-time "window:initialize:end")
   (.send (.-ipcRenderer electron) "window-command" "window:loaded")))

(defn setupWindow []
  (let [CompileCache (js/require "../src/compile-cache")
        ModuleCache (js/require "../src/module-cache")
        startCrashReporter (js/require "../src/crash-reporter-start")
        Grim (js/require "grim")
        documentRegisterElement (.-registerElement js/document)
        --temp1 (getWindowLoadSettings)
        userSettings (.-userSettings --temp1)
        appVersion (.-appVersion --temp1)
        uploadToServer (and userSettings
                            (.-core userSettings)
                            (= (.-telemetryConsent (.-core userSettings)) "limited"))
        releaseChannel (get-release-channel "9.4.4")
        CSON (js/require "season")]
    (.setAtomHomeDirectory CompileCache
                           (.-ATOM_HOME (.-env js/process)))
    (.install CompileCache
              (.-resourcesPath js/process)
              js/require)
    (.register ModuleCache (getWindowLoadSettings))
    (js/require "document-register-element")
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
    (.addMarker startup-time "window:initialize:start")
    (init-editor)))

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

(defn start []
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
             FileSystemBlobStore (js/require "../src/file-system-blob-store")
             NativeCompileCache (js/require "../src/native-compile-cache")]
         (.addMarker startup-time "window:onload:start")
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
         ;; FIXME - devmode?
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
                    (.-stack requireError)))))))
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
           (p/do!
             (.addMarker startup-time "window:setup-window:start")
             (setupWindow)
             (.addMarker startup-time "window:setup-window:end")
             (setLoadTime (- js/Date.now startTime)))))
       (catch :default error (handleSetupError error)))
     (.addMarker startup-time "window:onload:end"))))

#_
(defn ^:dev/after-load-async after-load [done]
  (p/do!
   (init-editor)
   (println "Reloaded the Atom Editor")
   (done)))
