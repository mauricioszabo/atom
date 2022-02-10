var Model, nextInstanceId;

nextInstanceId = 1;

module.exports = Model = (function() {
  class Model {
    static resetNextInstanceId() {
      return nextInstanceId = 1;
    }

    constructor(params) {
      this.assignId(params != null ? params.id : void 0);
    }

    assignId(id) {
      if (this.id == null) {
        this.id = id != null ? id : nextInstanceId++;
      }
      if (id >= nextInstanceId) {
        return nextInstanceId = id + 1;
      }
    }

    destroy() {
      if (!this.isAlive()) {
        return;
      }
      this.alive = false;
      return typeof this.destroyed === "function" ? this.destroyed() : void 0;
    }

    isAlive() {
      return this.alive;
    }

    isDestroyed() {
      return !this.isAlive();
    }

  };

  Model.prototype.alive = true;

  return Model;

}).call(this);
