var LayerDecoration, idCounter, nextId;

idCounter = 0;

nextId = function() {
  return idCounter++;
};

// Essential: Represents a decoration that applies to every marker on a given
// layer. Created via {TextEditor::decorateMarkerLayer}.
module.exports = LayerDecoration = class LayerDecoration {
  constructor(markerLayer, decorationManager, properties1) {
    this.markerLayer = markerLayer;
    this.decorationManager = decorationManager;
    this.properties = properties1;
    this.id = nextId();
    this.destroyed = false;
    this.markerLayerDestroyedDisposable = this.markerLayer.onDidDestroy(() => {
      return this.destroy();
    });
    this.overridePropertiesByMarker = null;
  }

  // Essential: Destroys the decoration.
  destroy() {
    if (this.destroyed) {
      return;
    }
    this.markerLayerDestroyedDisposable.dispose();
    this.markerLayerDestroyedDisposable = null;
    this.destroyed = true;
    return this.decorationManager.didDestroyLayerDecoration(this);
  }

  // Essential: Determine whether this decoration is destroyed.

  // Returns a {Boolean}.
  isDestroyed() {
    return this.destroyed;
  }

  getId() {
    return this.id;
  }

  getMarkerLayer() {
    return this.markerLayer;
  }

  // Essential: Get this decoration's properties.

  // Returns an {Object}.
  getProperties() {
    return this.properties;
  }

  // Essential: Set this decoration's properties.

  // * `newProperties` See {TextEditor::decorateMarker} for more information on
  //   the properties. The `type` of `gutter` and `overlay` are not supported on
  //   layer decorations.
  setProperties(newProperties) {
    if (this.destroyed) {
      return;
    }
    this.properties = newProperties;
    return this.decorationManager.emitDidUpdateDecorations();
  }

  // Essential: Override the decoration properties for a specific marker.

  // * `marker` The {DisplayMarker} or {Marker} for which to override
  //   properties.
  // * `properties` An {Object} containing properties to apply to this marker.
  //   Pass `null` to clear the override.
  setPropertiesForMarker(marker, properties) {
    if (this.destroyed) {
      return;
    }
    if (this.overridePropertiesByMarker == null) {
      this.overridePropertiesByMarker = new Map();
    }
    marker = this.markerLayer.getMarker(marker.id);
    if (properties != null) {
      this.overridePropertiesByMarker.set(marker, properties);
    } else {
      this.overridePropertiesByMarker.delete(marker);
    }
    return this.decorationManager.emitDidUpdateDecorations();
  }

  getPropertiesForMarker(marker) {
    var ref;
    return (ref = this.overridePropertiesByMarker) != null ? ref.get(marker) : void 0;
  }

};
