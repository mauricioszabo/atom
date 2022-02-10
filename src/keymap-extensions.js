var CSON, KeymapManager, bundledKeymaps, fs, path, ref;

fs = require('fs-plus');

path = require('path');

KeymapManager = require('atom-keymap');

CSON = require('season');

bundledKeymaps = (ref = require('../package.json')) != null ? ref._atomKeymaps : void 0;

KeymapManager.prototype.onDidLoadBundledKeymaps = function(callback) {
  return this.emitter.on('did-load-bundled-keymaps', callback);
};

KeymapManager.prototype.onDidLoadUserKeymap = function(callback) {
  return this.emitter.on('did-load-user-keymap', callback);
};

KeymapManager.prototype.canLoadBundledKeymapsFromMemory = function() {
  return bundledKeymaps != null;
};

KeymapManager.prototype.loadBundledKeymaps = function() {
  var keymap, keymapName, keymapPath, keymapsPath, ref1;
  if (bundledKeymaps != null) {
    for (keymapName in bundledKeymaps) {
      keymap = bundledKeymaps[keymapName];
      keymapPath = `core:${keymapName}`;
      this.add(keymapPath, keymap, 0, (ref1 = this.devMode) != null ? ref1 : false);
    }
  } else {
    keymapsPath = path.join(this.resourcePath, 'keymaps');
    this.loadKeymap(keymapsPath);
  }
  return this.emitter.emit('did-load-bundled-keymaps');
};

KeymapManager.prototype.getUserKeymapPath = function() {
  var userKeymapPath;
  if (this.configDirPath == null) {
    return "";
  }
  if (userKeymapPath = CSON.resolve(path.join(this.configDirPath, 'keymap'))) {
    return userKeymapPath;
  } else {
    return path.join(this.configDirPath, 'keymap.cson');
  }
};

KeymapManager.prototype.loadUserKeymap = function() {
  var detail, error, message, stack, userKeymapPath;
  userKeymapPath = this.getUserKeymapPath();
  if (!fs.isFileSync(userKeymapPath)) {
    return;
  }
  try {
    this.loadKeymap(userKeymapPath, {
      watch: true,
      suppressErrors: true,
      priority: 100
    });
  } catch (error1) {
    error = error1;
    if (error.message.indexOf('Unable to watch path') > -1) {
      message = `Unable to watch path: \`${path.basename(userKeymapPath)}\`. Make sure you
have permission to read \`${userKeymapPath}\`.

On linux there are currently problems with watch sizes. See
[this document][watches] for more info.
[watches]:https://github.com/atom/atom/blob/master/docs/build-instructions/linux.md#typeerror-unable-to-watch-path`;
      this.notificationManager.addError(message, {
        dismissable: true
      });
    } else {
      detail = error.path;
      stack = error.stack;
      this.notificationManager.addFatalError(error.message, {
        detail,
        stack,
        dismissable: true
      });
    }
  }
  return this.emitter.emit('did-load-user-keymap');
};

KeymapManager.prototype.subscribeToFileReadFailure = function() {
  return this.onDidFailToReadFile((error) => {
    var detail, message, userKeymapPath;
    userKeymapPath = this.getUserKeymapPath();
    message = `Failed to load \`${userKeymapPath}\``;
    detail = error.location != null ? error.stack : error.message;
    return this.notificationManager.addError(message, {
      detail,
      dismissable: true
    });
  });
};

module.exports = KeymapManager;
