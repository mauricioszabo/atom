(ns cljs.atom.core
  (:require [cljs.atom.text-editor-component :as editor-comp]))

(defn- ^:dev/after-load  reload []
  (prn :LOL editor-comp/TextEditorComponent))
  ; (:require ["../../text-editor" :as te]))

(defn- start! []
  (prn :HELLO-WORLD))

(def main #js {:start start!
               :TextEditorComponent editor-comp/TextEditorComponent})
