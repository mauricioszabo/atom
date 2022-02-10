(ns cljs.atom.main)

#_
(when-not (undefined? js/snapshotResult)
  (.setGlobals js/snapshotResult
               js/global
               js/process
               js/global
               #js {}
               js/console
               js/require))

(def startTime (.now js/Date))

(def StartupTime (js/require "./startup-time"))

(.setStartTime StartupTime)

(def path (js/require "path"))

(def fs (js/require "fs-plus"))

(def CSON (js/require "season"))

(def yargs (js/require "yargs"))

(def app (.-app (js/require "electron")))

(def version
  (str "Atom    : " (.getVersion app)
       "\nElectron: " (.-electron (.-versions js/process))
       "\nChrome  : " (.-chrome (.-versions js/process))
       "\nNode    : " (.-node (.-versions js/process))))

(def ^js args
  (.-argv
   (.alias
    (.alias
     (.alias (.version
              (.alias ^js (apply yargs (.-argv js/process)) "v" "version")
              version)
             "d"
             "dev")
     "t"
     "test")
    "r"
    "resource-path")))

(defn isAtomRepoPath
  [repoPath]
  (let [packageJsonPath
        (.join path repoPath "package.json")]
    (when (.statSyncNoException fs packageJsonPath)
      (try (let [packageJson
                 (.readFileSync CSON packageJsonPath)]
             (= (.-name packageJson) "atom"))
           (catch :default e false)))
    false))

; (def resourcePath nil)

(def devResourcePath nil)
;
; (if (.-resourcePath args)
;   (do (def resourcePath (.-resourcePath args))
;       (def devResourcePath resourcePath))
;   (let [stableResourcePath
;         (.dirname path (.dirname path js/__dirname))
;         defaultRepositoryPath
;         (.join path (.getPath app "home") "github" "atom")]
;     (if (.-ATOM_DEV_RESOURCE_PATH (.-env js/process))
;       (def devResourcePath
;         (.-ATOM_DEV_RESOURCE_PATH (.-env js/process)))
;       (if (isAtomRepoPath (.cwd js/process))
;         (def devResourcePath (.cwd js/process))
;         (if (.statSyncNoException fs defaultRepositoryPath)
;           (def devResourcePath defaultRepositoryPath)
;           (def devResourcePath stableResourcePath))))
;     (if (or (or (or (.-dev args) (.-test args))
;                 (.-benchmark args))
;             (.-benchmarkTest args))
;       (def resourcePath devResourcePath)
;       (def resourcePath stableResourcePath))))

;; FIXME: Remove this later
(def resourcePath (.-ATOM_DEV_RESOURCE_PATH (.-env js/process)))

(def start
  (js/require
   (.join path resourcePath "src" "main-process" "start")))

(defn start! []
  (start resourcePath devResourcePath startTime))
