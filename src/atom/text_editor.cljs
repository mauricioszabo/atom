(ns atom.text-editor
  (:require [shadow.cljs.modern :as modern]))
            ; ["../text-editor" :as AtomTextEditor]))

(defonce AtomTextEditor (atom nil))

(set! *warn-on-infer* false)
(modern/defclass TextEditor
  (constructor [this params]
    ; (js/console.log "underef")
    ; (js/console.log AtomTextEditor)
    (let [AtomTextEditor @AtomTextEditor]
      ; (js/console.log AtomTextEditor)
      (set! (.-editor this) (new AtomTextEditor (or params #js {})))))

  Object
  (onDidChangeTitle [this callback]
    (.onDidChangeTitle ^js (.-editor this) callback))
  (onDidChangePath [this callback]
    (.onDidChangePath ^js (.-editor this) callback))
  (onDidChange [this callback]
    (.onDidChange ^js (.-editor this) callback))
  (onDidStopChanging [this callback]
    (.onDidStopChanging ^js (.-editor this) callback))
  (onDidChangeCursorPosition [this callback]
    (.onDidChangeCursorPosition ^js (.-editor this) callback))
  (onDidChangeSelectionRange [this callback]
    (.onDidChangeSelectionRange ^js (.-editor this) callback))
  (onDidSave [this callback]
    (.onDidSave ^js (.-editor this) callback))
  (onDidDestroy [this callback]
    (.onDidDestroy ^js (.-editor this) callback))
  (observeGutters [this callback]
    (.observeGutters ^js (.-editor this) callback))
  (onDidAddGutter [this callback]
    (.onDidAddGutter ^js (.-editor this) callback))
  (onDidRemoveGutter [this callback]
    (.onDidRemoveGutter ^js (.-editor this) callback))
  (onDidChangeSoftWrapped [this callback]
    (.onDidChangeSoftWrapped ^js (.-editor this) callback))
  (onDidChangeEncoding [this callback]
    (.onDidChangeEncoding ^js (.-editor this) callback))
  (observeGrammar [this callback]
    (.observeGrammar ^js (.-editor this) callback))
  (onDidChangeGrammar [this callback]
    (.onDidChangeGrammar ^js (.-editor this) callback))
  (onDidChangeModified [this callback]
    (.onDidChangeModified ^js (.-editor this) callback))
  (onDidConflict [this callback]
    (.onDidConflict ^js (.-editor this) callback))
  (onWillInsertText [this callback]
    (.onWillInsertText ^js (.-editor this) callback))
  (onDidInsertText [this callback]
    (.onDidInsertText ^js (.-editor this) callback))
  (observeCursors [this callback]
    (.observeCursors ^js (.-editor this) callback))
  (onDidAddCursor [this callback]
    (.onDidAddCursor ^js (.-editor this) callback))
  (onDidRemoveCursor [this callback]
    (.onDidRemoveCursor ^js (.-editor this) callback))
  (observeSelections [this callback]
    (.observeSelections ^js (.-editor this) callback))
  (onDidAddSelection [this callback]
    (.onDidAddSelection ^js (.-editor this) callback))
  (onDidRemoveSelection [this callback]
    (.onDidRemoveSelection ^js (.-editor this) callback))
  (observeDecorations [this callback]
    (.observeDecorations ^js (.-editor this) callback))
  (onDidAddDecoration [this callback]
    (.onDidAddDecoration ^js (.-editor this) callback))
  (onDidRemoveDecoration [this callback]
    (.onDidRemoveDecoration ^js (.-editor this) callback))
  (onDidChangePlaceholderText [this callback]
    (.onDidChangePlaceholderText ^js (.-editor this) callback))
  (onDidTokenize [this callback]
    (.onDidTokenize ^js (.-editor this) callback))
  (getBuffer [this]
    (.getBuffer ^js (.-editor this)))
  (getTitle [this]
    (.getTitle ^js (.-editor this)))
  (getLongTitle [this]
    (.getLongTitle ^js (.-editor this)))
  (getPath [this]
    (.getPath ^js (.-editor this)))
  (isModified [this]
    (.isModified ^js (.-editor this)))
  (isEmpty [this]
    (.isEmpty ^js (.-editor this)))
  (getEncoding [this]
    (.getEncoding ^js (.-editor this)))
  (getElement [this]
    (.getElement ^js (.-editor this)))
  (setEncoding [this encoding]
    (.setEncoding ^js (.-editor this) encoding))
  (save [this]
    (.save ^js (.-editor this)))
  (saveAs [this filePath]
    (.saveAs ^js (.-editor this) filePath))
  (getText [this]
    (.getText ^js (.-editor this)))
  ; (getTextInBufferRange [this range]
  ;   (.getTextInBufferRange ^js (.-editor this) range))
  (getLineCount [this]
    (.getLineCount ^js (.-editor this)))
  (getScreenLineCount [this]
    (.getScreenLineCount ^js (.-editor this)))
  (getLastBufferRow [this]
    (.getLastBufferRow ^js (.-editor this)))
  (getLastScreenRow [this]
    (.getLastScreenRow ^js (.-editor this)))
  (lineTextForBufferRow [this bufferRow]
    (.lineTextForBufferRow ^js (.-editor this) bufferRow))
  (lineTextForScreenRow [this screenRow]
    (.lineTextForScreenRow ^js (.-editor this) screenRow))
  (getCurrentParagraphBufferRange [this]
    (.getCurrentParagraphBufferRange ^js (.-editor this)))
  (setText [this text, options]
    (.setText ^js (.-editor this) text, options))
  (setTextInBufferRange [this range, text, options]
    (.setTextInBufferRange ^js (.-editor this) range, text, options))
  (insertText [this text, options]
    (.insertText ^js (.-editor this) text, options))
  (insertNewline [this options]
    (.insertNewline ^js (.-editor this) options))
  (delete [this options]
    (.delete ^js (.-editor this) options))
  (backspace [this options]
    (.backspace ^js (.-editor this) options))
  (mutateSelectedText [this fn]
    (.mutateSelectedText ^js (.-editor this) fn))
  (transpose [this options]
    (.transpose ^js (.-editor this) options))
  (upperCase [this options]
    (.upperCase ^js (.-editor this) options))
  (lowerCase [this options]
    (.lowerCase ^js (.-editor this) options))
  (toggleLineCommentsInSelection [this options]
    (.toggleLineCommentsInSelection ^js (.-editor this) options))
  (insertNewlineBelow [this options]
    (.insertNewlineBelow ^js (.-editor this) options))
  (insertNewlineAbove [this options]
    (.insertNewlineAbove ^js (.-editor this) options))
  (deleteToBeginningOfWord [this options]
    (.deleteToBeginningOfWord ^js (.-editor this) options))
  (deleteToPreviousWordBoundary [this options]
    (.deleteToPreviousWordBoundary ^js (.-editor this) options))
  (deleteToNextWordBoundary [this options]
    (.deleteToNextWordBoundary ^js (.-editor this) options))
  (deleteToBeginningOfSubword [this options]
    (.deleteToBeginningOfSubword ^js (.-editor this) options))
  (deleteToEndOfSubword [this options]
    (.deleteToEndOfSubword ^js (.-editor this) options))
  (deleteToBeginningOfLine [this options]
    (.deleteToBeginningOfLine ^js (.-editor this) options))
  (deleteToEndOfLine [this options]
    (.deleteToEndOfLine ^js (.-editor this) options))
  (deleteToEndOfWord [this options]
    (.deleteToEndOfWord ^js (.-editor this) options))
  (deleteLine [this options]
    (.deleteLine ^js (.-editor this) options))
  (undo [this options]
    (.undo ^js (.-editor this) options))
  (redo [this options]
    (.redo ^js (.-editor this) options))
  (transact [this groupingInterval, fn]
    (.transact ^js (.-editor this) groupingInterval, fn))
  (abortTransaction [this]
    (.abortTransaction ^js (.-editor this)))
  (createCheckpoint [this]
    (.createCheckpoint ^js (.-editor this)))
  (revertToCheckpoint [this checkpoint]
    (.revertToCheckpoint ^js (.-editor this) checkpoint))
  (groupChangesSinceCheckpoint [this checkpoint]
    (.groupChangesSinceCheckpoint ^js (.-editor this) checkpoint))
  (screenPositionForBufferPosition [this bufferPosition, options]
    (.screenPositionForBufferPosition ^js (.-editor this) bufferPosition, options))
  (bufferPositionForScreenPosition [this bufferPosition, options]
    (.bufferPositionForScreenPosition ^js (.-editor this) bufferPosition, options))
  (screenRangeForBufferRange [this bufferRange]
    (.screenRangeForBufferRange ^js (.-editor this) bufferRange))
  (bufferRangeForScreenRange [this screenRange]
    (.bufferRangeForScreenRange ^js (.-editor this) screenRange))
  (clipBufferPosition [this bufferPosition]
    (.clipBufferPosition ^js (.-editor this) bufferPosition))
  (clipBufferRange [this range]
    (.clipBufferRange ^js (.-editor this) range))
  (clipScreenPosition [this screenPosition, options]
    (.clipScreenPosition ^js (.-editor this) screenPosition, options))
  (clipScreenRange [this range, options]
    (.clipScreenRange ^js (.-editor this) range, options))
  (decorateMarker [this marker, decorationParams]
    (.decorateMarker ^js (.-editor this) marker, decorationParams))
  (decorateMarkerLayer [this markerLayer, decorationParams]
    (.decorateMarkerLayer ^js (.-editor this) markerLayer, decorationParams))
  (getDecorations [this propertyFilter]
    (.getDecorations ^js (.-editor this) propertyFilter))
  (getLineDecorations [this propertyFilter]
    (.getLineDecorations ^js (.-editor this) propertyFilter))
  (getLineNumberDecorations [this propertyFilter]
    (.getLineNumberDecorations ^js (.-editor this) propertyFilter))
  (getHighlightDecorations [this propertyFilter]
    (.getHighlightDecorations ^js (.-editor this) propertyFilter))
  (getOverlayDecorations [this propertyFilter]
    (.getOverlayDecorations ^js (.-editor this) propertyFilter))
  (markBufferRange [this range, properties]
    (.markBufferRange ^js (.-editor this) range, properties))
  (markScreenRange [this range, properties]
    (.markScreenRange ^js (.-editor this) range, properties))
  (markBufferPosition [this bufferPosition, options]
    (.markBufferPosition ^js (.-editor this) bufferPosition, options))
  (markScreenPosition [this screenPosition, options]
    (.markScreenPosition ^js (.-editor this) screenPosition, options))
  (findMarkers [this properties]
    (.findMarkers ^js (.-editor this) properties))
  (addMarkerLayer [this options]
    (.addMarkerLayer ^js (.-editor this) options))
  (getMarkerLayer [this id]
    (.getMarkerLayer ^js (.-editor this) id))
  (getDefaultMarkerLayer [this]
    (.getDefaultMarkerLayer ^js (.-editor this)))
  (getMarker [this id]
    (.getMarker ^js (.-editor this) id))
  (getMarkers [this]
    (.getMarkers ^js (.-editor this)))
  (getMarkerCount [this]
    (.getMarkerCount ^js (.-editor this)))
  (getCursorBufferPosition [this]
    (.getCursorBufferPosition ^js (.-editor this)))
  (getCursorBufferPositions [this]
    (.getCursorBufferPositions ^js (.-editor this)))
  (setCursorBufferPosition [this position, options]
    (.setCursorBufferPosition ^js (.-editor this) position, options))
  (getCursorAtScreenPosition [this position]
    (.getCursorAtScreenPosition ^js (.-editor this) position))
  (getCursorScreenPosition [this]
    (.getCursorScreenPosition ^js (.-editor this)))
  (getCursorScreenPositions [this]
    (.getCursorScreenPositions ^js (.-editor this)))
  (setCursorScreenPosition [this position, options]
    (.setCursorScreenPosition ^js (.-editor this) position, options))
  (addCursorAtBufferPosition [this bufferPosition]
    (.addCursorAtBufferPosition ^js (.-editor this) bufferPosition))
  (addCursorAtScreenPosition [this screenPosition]
    (.addCursorAtScreenPosition ^js (.-editor this) screenPosition))
  (hasMultipleCursors [this]
    (.hasMultipleCursors ^js (.-editor this)))
  (moveUp [this lineCount]
    (.moveUp ^js (.-editor this) lineCount))
  (moveDown [this lineCount]
    (.moveDown ^js (.-editor this) lineCount))
  (moveLeft [this columnCount]
    (.moveLeft ^js (.-editor this) columnCount))
  (moveRight [this columnCount]
    (.moveRight ^js (.-editor this) columnCount))
  (moveToBeginningOfLine [this]
    (.moveToBeginningOfLine ^js (.-editor this)))
  (moveToBeginningOfScreenLine [this]
    (.moveToBeginningOfScreenLine ^js (.-editor this)))
  (moveToFirstCharacterOfLine [this]
    (.moveToFirstCharacterOfLine ^js (.-editor this)))
  (moveToEndOfLine [this]
    (.moveToEndOfLine ^js (.-editor this)))
  (moveToEndOfScreenLine [this]
    (.moveToEndOfScreenLine ^js (.-editor this)))
  (moveToBeginningOfWord [this]
    (.moveToBeginningOfWord ^js (.-editor this)))
  (moveToEndOfWord [this]
    (.moveToEndOfWord ^js (.-editor this)))
  (moveToTop [this]
    (.moveToTop ^js (.-editor this)))
  (moveToBottom [this]
    (.moveToBottom ^js (.-editor this)))
  (moveToBeginningOfNextWord [this]
    (.moveToBeginningOfNextWord ^js (.-editor this)))
  (moveToPreviousWordBoundary [this]
    (.moveToPreviousWordBoundary ^js (.-editor this)))
  (moveToNextWordBoundary [this]
    (.moveToNextWordBoundary ^js (.-editor this)))
  (moveToPreviousSubwordBoundary [this]
    (.moveToPreviousSubwordBoundary ^js (.-editor this)))
  (moveToNextSubwordBoundary [this]
    (.moveToNextSubwordBoundary ^js (.-editor this)))
  (moveToBeginningOfNextParagraph [this]
    (.moveToBeginningOfNextParagraph ^js (.-editor this)))
  (moveToBeginningOfPreviousParagraph [this]
    (.moveToBeginningOfPreviousParagraph ^js (.-editor this)))
  (getLastCursor [this]
    (.getLastCursor ^js (.-editor this)))
  (getWordUnderCursor [this options]
    (.getWordUnderCursor ^js (.-editor this) options))
  (getCursors [this]
    (.getCursors ^js (.-editor this)))
  (getCursorsOrderedByBufferPosition [this]
    (.getCursorsOrderedByBufferPosition ^js (.-editor this)))
  (getSelectedText [this]
    (.getSelectedText ^js (.-editor this)))
  (getSelectedBufferRange [this]
    (.getSelectedBufferRange ^js (.-editor this)))
  (getSelectedBufferRanges [this]
    (.getSelectedBufferRanges ^js (.-editor this)))
  (setSelectedBufferRange [this bufferRange, options]
    (.setSelectedBufferRange ^js (.-editor this) bufferRange, options))
  (setSelectedBufferRanges [this bufferRanges, options]
    (.setSelectedBufferRanges ^js (.-editor this) bufferRanges, options))
  (getSelectedScreenRange [this]
    (.getSelectedScreenRange ^js (.-editor this)))
  (getSelectedScreenRanges [this]
    (.getSelectedScreenRanges ^js (.-editor this)))
  (setSelectedScreenRange [this screenRange, options]
    (.setSelectedScreenRange ^js (.-editor this) screenRange, options))
  (setSelectedScreenRanges [this screenRanges, options]
    (.setSelectedScreenRanges ^js (.-editor this) screenRanges, options))
  (addSelectionForBufferRange [this bufferRange, options]
    (.addSelectionForBufferRange ^js (.-editor this) bufferRange, options))
  (addSelectionForScreenRange [this screenRange, options]
    (.addSelectionForScreenRange ^js (.-editor this) screenRange, options))
  (selectToBufferPosition [this position]
    (.selectToBufferPosition ^js (.-editor this) position))
  (selectToScreenPosition [this position]
    (.selectToScreenPosition ^js (.-editor this) position))
  (selectUp [this rowCount]
    (.selectUp ^js (.-editor this) rowCount))
  (selectDown [this rowCount]
    (.selectDown ^js (.-editor this) rowCount))
  (selectLeft [this columnCount]
    (.selectLeft ^js (.-editor this) columnCount))
  (selectRight [this columnCount]
    (.selectRight ^js (.-editor this) columnCount))
  (selectToTop [this]
    (.selectToTop ^js (.-editor this)))
  (selectToBottom [this]
    (.selectToBottom ^js (.-editor this)))
  (selectAll [this]
    (.selectAll ^js (.-editor this)))
  (selectToBeginningOfLine [this]
    (.selectToBeginningOfLine ^js (.-editor this)))
  (selectToFirstCharacterOfLine [this]
    (.selectToFirstCharacterOfLine ^js (.-editor this)))
  (selectToEndOfLine [this]
    (.selectToEndOfLine ^js (.-editor this)))
  (selectToBeginningOfWord [this]
    (.selectToBeginningOfWord ^js (.-editor this)))
  (selectToEndOfWord [this]
    (.selectToEndOfWord ^js (.-editor this)))
  (selectLinesContainingCursors [this]
    (.selectLinesContainingCursors ^js (.-editor this)))
  (selectWordsContainingCursors [this]
    (.selectWordsContainingCursors ^js (.-editor this)))
  (selectToPreviousSubwordBoundary [this]
    (.selectToPreviousSubwordBoundary ^js (.-editor this)))
  (selectToNextSubwordBoundary [this]
    (.selectToNextSubwordBoundary ^js (.-editor this)))
  (selectToPreviousWordBoundary [this]
    (.selectToPreviousWordBoundary ^js (.-editor this)))
  (selectToNextWordBoundary [this]
    (.selectToNextWordBoundary ^js (.-editor this)))
  (selectToBeginningOfNextWord [this]
    (.selectToBeginningOfNextWord ^js (.-editor this)))
  (selectToBeginningOfNextParagraph [this]
    (.selectToBeginningOfNextParagraph ^js (.-editor this)))
  (selectToBeginningOfPreviousParagraph [this]
    (.selectToBeginningOfPreviousParagraph ^js (.-editor this)))
  (selectLargerSyntaxNode [this]
    (.selectLargerSyntaxNode ^js (.-editor this)))
  (selectSmallerSyntaxNode [this]
    (.selectSmallerSyntaxNode ^js (.-editor this)))
  (selectMarker [this marker]
    (.selectMarker ^js (.-editor this) marker))
  (getLastSelection [this]
    (.getLastSelection ^js (.-editor this)))
  (getSelections [this]
    (.getSelections ^js (.-editor this)))
  (getSelectionsOrderedByBufferPosition [this]
    (.getSelectionsOrderedByBufferPosition ^js (.-editor this)))
  (selectionIntersectsBufferRange [this bufferRange]
    (.selectionIntersectsBufferRange ^js (.-editor this) bufferRange))
  (scan [this regex, options, iterator]
    (.scan ^js (.-editor this) regex, options, iterator))
  (scanInBufferRange [this regex, range, iterator]
    (.scanInBufferRange ^js (.-editor this) regex, range, iterator))
  (backwardsScanInBufferRange [this regex, range, iterator]
    (.backwardsScanInBufferRange ^js (.-editor this) regex, range, iterator))
  (getSoftTabs [this]
    (.getSoftTabs ^js (.-editor this)))
  (setSoftTabs [this softTabs]
    (.setSoftTabs ^js (.-editor this) softTabs))
  (toggleSoftTabs [this]
    (.toggleSoftTabs ^js (.-editor this)))
  (getTabLength [this]
    (.getTabLength ^js (.-editor this)))
  (setTabLength [this tabLength]
    (.setTabLength ^js (.-editor this) tabLength))
  (usesSoftTabs [this]
    (.usesSoftTabs ^js (.-editor this)))
  (getTabText [this]
    (.getTabText ^js (.-editor this)))
  (isSoftWrapped [this]
    (.isSoftWrapped ^js (.-editor this)))
  (setSoftWrapped [this softWrapped]
    (.setSoftWrapped ^js (.-editor this) softWrapped))
  (toggleSoftWrapped [this]
    (.toggleSoftWrapped ^js (.-editor this)))
  (getSoftWrapColumn [this]
    (.getSoftWrapColumn ^js (.-editor this)))
  (indentationForBufferRow [this bufferRow]
    (.indentationForBufferRow ^js (.-editor this) bufferRow))
  (setIndentationForBufferRow [this bufferRow, newLevel, options]
    (.setIndentationForBufferRow ^js (.-editor this) bufferRow, newLevel, options))
  (indentSelectedRows [this options]
    (.indentSelectedRows ^js (.-editor this) options))
  (outdentSelectedRows [this options]
    (.outdentSelectedRows ^js (.-editor this) options))
  (indentLevelForLine [this line]
    (.indentLevelForLine ^js (.-editor this) line))
  (autoIndentSelectedRows [this options]
    (.autoIndentSelectedRows ^js (.-editor this) options))
  (getGrammar [this]
    (.getGrammar ^js (.-editor this)))
  (getRootScopeDescriptor [this]
    (.getRootScopeDescriptor ^js (.-editor this)))
  (scopeDescriptorForBufferPosition [this bufferPosition]
    (.scopeDescriptorForBufferPosition ^js (.-editor this) bufferPosition))
  (syntaxTreeScopeDescriptorForBufferPosition [this bufferPosition]
    (.syntaxTreeScopeDescriptorForBufferPosition ^js (.-editor this) bufferPosition))
  (bufferRangeForScopeAtCursor [this scopeSelector]
    (.bufferRangeForScopeAtCursor ^js (.-editor this) scopeSelector))
  (isBufferRowCommented [this]
    (.isBufferRowCommented ^js (.-editor this)))
  (copySelectedText [this]
    (.copySelectedText ^js (.-editor this)))
  (cutSelectedText [this options]
    (.cutSelectedText ^js (.-editor this) options))
  (pasteText [this options]
    (.pasteText ^js (.-editor this) options))
  (cutToEndOfLine [this options]
    (.cutToEndOfLine ^js (.-editor this) options))
  (cutToEndOfBufferLine [this options]
    (.cutToEndOfBufferLine ^js (.-editor this) options))
  (foldCurrentRow [this]
    (.foldCurrentRow ^js (.-editor this)))
  (unfoldCurrentRow [this]
    (.unfoldCurrentRow ^js (.-editor this)))
  (foldBufferRow [this bufferRow]
    (.foldBufferRow ^js (.-editor this) bufferRow))
  (unfoldBufferRow [this bufferRow]
    (.unfoldBufferRow ^js (.-editor this) bufferRow))
  (foldSelectedLines [this]
    (.foldSelectedLines ^js (.-editor this)))
  (foldAll [this]
    (.foldAll ^js (.-editor this)))
  (unfoldAll [this]
    (.unfoldAll ^js (.-editor this)))
  (foldAllAtIndentLevel [this level]
    (.foldAllAtIndentLevel ^js (.-editor this) level))
  (isFoldableAtBufferRow [this bufferRow]
    (.isFoldableAtBufferRow ^js (.-editor this) bufferRow))
  (isFoldableAtScreenRow [this bufferRow]
    (.isFoldableAtScreenRow ^js (.-editor this) bufferRow))
  (toggleFoldAtBufferRow [this]
    (.toggleFoldAtBufferRow ^js (.-editor this)))
  (isFoldedAtCursorRow [this]
    (.isFoldedAtCursorRow ^js (.-editor this)))
  (isFoldedAtBufferRow [this bufferRow]
    (.isFoldedAtBufferRow ^js (.-editor this) bufferRow))
  (isFoldedAtScreenRow [this screenRow]
    (.isFoldedAtScreenRow ^js (.-editor this) screenRow))
  (addGutter [this options]
    (.addGutter ^js (.-editor this) options))
  (getGutters [this]
    (.getGutters ^js (.-editor this)))
  (gutterWithName [this]
    (.gutterWithName ^js (.-editor this)))
  (scrollToCursorPosition [this options]
    (.scrollToCursorPosition ^js (.-editor this) options))
  (scrollToBufferPosition [this bufferPosition, options]
    (.scrollToBufferPosition ^js (.-editor this) bufferPosition, options))
  (scrollToScreenPosition [this screenPosition, options]
    (.scrollToScreenPosition ^js (.-editor this) screenPosition, options))
  (getPlaceholderText [this]
    (.getPlaceholderText ^js (.-editor this)))
  (setPlaceholderText [this placeholderText]
    (.setPlaceholderText ^js (.-editor this) placeholderText))

  ;; Private methods?
  (isDestroyed [this]
    (.isDestroyed ^js (.-editor this)))
  (update [this params]
    (.update ^js (.-editor this) params))
  (getTextInRange [this range] (.. ^js this getBuffer (getTextInRange range)))
  (getTextInBufferRange [this range]
    (js/console.log (.. ^js this getBuffer))
    (.. ^js this getBuffer (getTextInRange range))))
(set! *warn-on-infer* true)

(set! (.-setClipboard TextEditor) #(do
                                     (prn :CLIP %1 ^js @AtomTextEditor)
                                     (.setClipboard ^js @AtomTextEditor %1)))

(set! (.-setScheduler TextEditor) #(.setScheduler ^js @AtomTextEditor %1))
(set! (.-didUpdateStyles TextEditor) #(.didUpdateStyles ^js @AtomTextEditor))
(set! (.-didUpdateScrollbarStyles TextEditor) #(.didUpdateScrollbarStyles ^js @AtomTextEditor))
(set! (.-viewForItem TextEditor) #(.viewForItem ^js @AtomTextEditor %1))
(set! (.-deserialize TextEditor) #(.deserialize ^js @AtomTextEditor %1 %2))
(prn :FOURTH)

(defn set-editor! [^js editor]
  (prn :TE editor)
  (reset! AtomTextEditor editor))
  ; (.setClipboard editor (new Clipboard)))
