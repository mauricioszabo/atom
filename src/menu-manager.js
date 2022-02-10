var CSON, Disposable, MenuHelpers, MenuManager, _, fs, ipcRenderer, path, platformMenu, ref, ref1;

path = require('path');

_ = require('underscore-plus');

({ipcRenderer} = require('electron'));

CSON = require('season');

fs = require('fs-plus');

({Disposable} = require('event-kit'));

MenuHelpers = require('./menu-helpers');

platformMenu = (ref = require('../package.json')) != null ? (ref1 = ref._atomMenu) != null ? ref1.menu : void 0 : void 0;

// Extended: Provides a registry for menu items that you'd like to appear in the
// application menu.

// An instance of this class is always available as the `atom.menu` global.

// ## Menu CSON Format

// Here is an example from the [tree-view](https://github.com/atom/tree-view/blob/master/menus/tree-view.cson):

// ```coffee
// [
//   {
//     'label': 'View'
//     'submenu': [
//       { 'label': 'Toggle Tree View', 'command': 'tree-view:toggle' }
//     ]
//   }
//   {
//     'label': 'Packages'
//     'submenu': [
//       'label': 'Tree View'
//       'submenu': [
//         { 'label': 'Focus', 'command': 'tree-view:toggle-focus' }
//         { 'label': 'Toggle', 'command': 'tree-view:toggle' }
//         { 'label': 'Reveal Active File', 'command': 'tree-view:reveal-active-file' }
//         { 'label': 'Toggle Tree Side', 'command': 'tree-view:toggle-side' }
//       ]
//     ]
//   }
// ]
// ```

// Use in your package's menu `.cson` file requires that you place your menu
// structure under a `menu` key.

// ```coffee
// 'menu': [
//   {
//     'label': 'View'
//     'submenu': [
//       { 'label': 'Toggle Tree View', 'command': 'tree-view:toggle' }
//     ]
//   }
// ]
// ```

// See {::add} for more info about adding menu's directly.
module.exports = MenuManager = class MenuManager {
  constructor({resourcePath, keymapManager, packageManager}) {
    this.resourcePath = resourcePath;
    this.keymapManager = keymapManager;
    this.packageManager = packageManager;
    this.initialized = false;
    this.pendingUpdateOperation = null;
    this.template = [];
    this.keymapManager.onDidLoadBundledKeymaps(() => {
      return this.loadPlatformItems();
    });
    this.packageManager.onDidActivateInitialPackages(() => {
      return this.sortPackagesMenu();
    });
  }

  initialize({resourcePath}) {
    this.resourcePath = resourcePath;
    this.keymapManager.onDidReloadKeymap(() => {
      return this.update();
    });
    this.update();
    return this.initialized = true;
  }

  // Public: Adds the given items to the application menu.

  // ## Examples
  // ```coffee
  //   atom.menu.add [
  //     {
  //       label: 'Hello'
  //       submenu : [{label: 'World!', id: 'World!', command: 'hello:world'}]
  //     }
  //   ]
  // ```

  // * `items` An {Array} of menu item {Object}s containing the keys:
  //   * `label` The {String} menu label.
  //   * `submenu` An optional {Array} of sub menu items.
  //   * `command` An optional {String} command to trigger when the item is
  //     clicked.

  //   * `id` (internal) A {String} containing the menu item's id.
  // Returns a {Disposable} on which `.dispose()` can be called to remove the
  // added menu items.
  add(items) {
    var i, item, len;
    items = _.deepClone(items);
    for (i = 0, len = items.length; i < len; i++) {
      item = items[i];
      if (item.label == null) {
        continue; // TODO: Should we emit a warning here?
      }
      this.merge(this.template, item);
    }
    this.update();
    return new Disposable(() => {
      return this.remove(items);
    });
  }

  remove(items) {
    var i, item, len;
    for (i = 0, len = items.length; i < len; i++) {
      item = items[i];
      this.unmerge(this.template, item);
    }
    return this.update();
  }

  clear() {
    this.template = [];
    return this.update();
  }

  // Should the binding for the given selector be included in the menu
  // commands.

  // * `selector` A {String} selector to check.

  // Returns a {Boolean}, true to include the selector, false otherwise.
  includeSelector(selector) {
    var element, error, testBody, testDocument, testWorkspace, workspaceClasses;
    try {
      if (document.body.webkitMatchesSelector(selector)) {
        return true;
      }
    } catch (error1) {
      error = error1;
      // Selector isn't valid
      return false;
    }
    // Simulate an atom-text-editor element attached to a atom-workspace element attached
    // to a body element that has the same classes as the current body element.
    if (this.testEditor == null) {
      // Use new document so that custom elements don't actually get created
      testDocument = document.implementation.createDocument(document.namespaceURI, 'html');
      testBody = testDocument.createElement('body');
      testBody.classList.add(...this.classesForElement(document.body));
      testWorkspace = testDocument.createElement('atom-workspace');
      workspaceClasses = this.classesForElement(document.body.querySelector('atom-workspace'));
      if (workspaceClasses.length === 0) {
        workspaceClasses = ['workspace'];
      }
      testWorkspace.classList.add(...workspaceClasses);
      testBody.appendChild(testWorkspace);
      this.testEditor = testDocument.createElement('atom-text-editor');
      this.testEditor.classList.add('editor');
      testWorkspace.appendChild(this.testEditor);
    }
    element = this.testEditor;
    while (element) {
      if (element.webkitMatchesSelector(selector)) {
        return true;
      }
      element = element.parentElement;
    }
    return false;
  }

  // Public: Refreshes the currently visible menu.
  update() {
    if (!this.initialized) {
      return;
    }
    if (this.pendingUpdateOperation != null) {
      clearTimeout(this.pendingUpdateOperation);
    }
    return this.pendingUpdateOperation = setTimeout(() => {
      var binding, i, j, keystrokesByCommand, len, len1, name, ref2, ref3, unsetKeystrokes;
      unsetKeystrokes = new Set();
      ref2 = this.keymapManager.getKeyBindings();
      for (i = 0, len = ref2.length; i < len; i++) {
        binding = ref2[i];
        if (binding.command === 'unset!') {
          unsetKeystrokes.add(binding.keystrokes);
        }
      }
      keystrokesByCommand = {};
      ref3 = this.keymapManager.getKeyBindings();
      for (j = 0, len1 = ref3.length; j < len1; j++) {
        binding = ref3[j];
        if (!this.includeSelector(binding.selector)) {
          continue;
        }
        if (unsetKeystrokes.has(binding.keystrokes)) {
          continue;
        }
        if (process.platform === 'darwin' && /^alt-(shift-)?.$/.test(binding.keystrokes)) {
          continue;
        }
        if (process.platform === 'win32' && /^ctrl-alt-(shift-)?.$/.test(binding.keystrokes)) {
          continue;
        }
        if (keystrokesByCommand[name = binding.command] == null) {
          keystrokesByCommand[name] = [];
        }
        keystrokesByCommand[binding.command].unshift(binding.keystrokes);
      }
      return this.sendToBrowserProcess(this.template, keystrokesByCommand);
    }, 1);
  }

  loadPlatformItems() {
    var menu, menusDirPath, platformMenuPath;
    if (platformMenu != null) {
      return this.add(platformMenu);
    } else {
      menusDirPath = path.join(this.resourcePath, 'menus');
      platformMenuPath = fs.resolve(menusDirPath, process.platform, ['cson', 'json']);
      ({menu} = CSON.readFileSync(platformMenuPath));
      return this.add(menu);
    }
  }

  // Merges an item in a submenu aware way such that new items are always
  // appended to the bottom of existing menus where possible.
  merge(menu, item) {
    return MenuHelpers.merge(menu, item);
  }

  unmerge(menu, item) {
    return MenuHelpers.unmerge(menu, item);
  }

  sendToBrowserProcess(template, keystrokesByCommand) {
    return ipcRenderer.send('update-application-menu', template, keystrokesByCommand);
  }

  // Get an {Array} of {String} classes for the given element.
  classesForElement(element) {
    var classList;
    if (classList = element != null ? element.classList : void 0) {
      return Array.prototype.slice.apply(classList);
    } else {
      return [];
    }
  }

  sortPackagesMenu() {
    var packagesMenu;
    packagesMenu = _.find(this.template, function({id}) {
      return MenuHelpers.normalizeLabel(id) === 'Packages';
    });
    if ((packagesMenu != null ? packagesMenu.submenu : void 0) == null) {
      return;
    }
    packagesMenu.submenu.sort(function(item1, item2) {
      if (item1.label && item2.label) {
        return MenuHelpers.normalizeLabel(item1.label).localeCompare(MenuHelpers.normalizeLabel(item2.label));
      } else {
        return 0;
      }
    });
    return this.update();
  }

};
