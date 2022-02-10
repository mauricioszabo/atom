var CSON, ContextMenuItemSet, ContextMenuManager, Disposable, MenuHelpers, _, calculateSpecificity, fs, path, platformContextMenu, ref, ref1, remote, sortMenuItems, validateSelector;

path = require('path');

CSON = require('season');

fs = require('fs-plus');

({calculateSpecificity, validateSelector} = require('clear-cut'));

({Disposable} = require('event-kit'));

({remote} = require('electron'));

MenuHelpers = require('./menu-helpers');

({sortMenuItems} = require('./menu-sort-helpers'));

_ = require('underscore-plus');

platformContextMenu = (ref = require('../package.json')) != null ? (ref1 = ref._atomMenu) != null ? ref1['context-menu'] : void 0 : void 0;

// Extended: Provides a registry for commands that you'd like to appear in the
// context menu.

// An instance of this class is always available as the `atom.contextMenu`
// global.

// ## Context Menu CSON Format

// ```coffee
// 'atom-workspace': [{label: 'Help', command: 'application:open-documentation'}]
// 'atom-text-editor': [{
//   label: 'History',
//   submenu: [
//     {label: 'Undo', command:'core:undo'}
//     {label: 'Redo', command:'core:redo'}
//   ]
// }]
// ```

// In your package's menu `.cson` file you need to specify it under a
// `context-menu` key:

// ```coffee
// 'context-menu':
//   'atom-workspace': [{label: 'Help', command: 'application:open-documentation'}]
//   ...
// ```

// The format for use in {::add} is the same minus the `context-menu` key. See
// {::add} for more information.
module.exports = ContextMenuManager = class ContextMenuManager {
  constructor({keymapManager}) {
    this.keymapManager = keymapManager;
    this.definitions = {
      '.overlayer': [] // TODO: Remove once color picker package stops touching private data
    };
    this.clear();
    this.keymapManager.onDidLoadBundledKeymaps(() => {
      return this.loadPlatformItems();
    });
  }

  initialize({resourcePath, devMode}) {
    this.resourcePath = resourcePath;
    this.devMode = devMode;
  }

  loadPlatformItems() {
    var map, menusDirPath, platformMenuPath, ref2;
    if (platformContextMenu != null) {
      return this.add(platformContextMenu, (ref2 = this.devMode) != null ? ref2 : false);
    } else {
      menusDirPath = path.join(this.resourcePath, 'menus');
      platformMenuPath = fs.resolve(menusDirPath, process.platform, ['cson', 'json']);
      map = CSON.readFileSync(platformMenuPath);
      return this.add(map['context-menu']);
    }
  }

  // Public: Add context menu items scoped by CSS selectors.

  // ## Examples

  // To add a context menu, pass a selector matching the elements to which you
  // want the menu to apply as the top level key, followed by a menu descriptor.
  // The invocation below adds a global 'Help' context menu item and a 'History'
  // submenu on the editor supporting undo/redo. This is just for example
  // purposes and not the way the menu is actually configured in Atom by default.

  // ```coffee
  // atom.contextMenu.add {
  //   'atom-workspace': [{label: 'Help', command: 'application:open-documentation'}]
  //   'atom-text-editor': [{
  //     label: 'History',
  //     submenu: [
  //       {label: 'Undo', command:'core:undo'}
  //       {label: 'Redo', command:'core:redo'}
  //     ]
  //   }]
  // }
  // ```

  // ## Arguments

  // * `itemsBySelector` An {Object} whose keys are CSS selectors and whose
  //   values are {Array}s of item {Object}s containing the following keys:
  //   * `label` (optional) A {String} containing the menu item's label.
  //   * `command` (optional) A {String} containing the command to invoke on the
  //     target of the right click that invoked the context menu.
  //   * `enabled` (optional) A {Boolean} indicating whether the menu item
  //     should be clickable. Disabled menu items typically appear grayed out.
  //     Defaults to `true`.
  //   * `submenu` (optional) An {Array} of additional items.
  //   * `type` (optional) If you want to create a separator, provide an item
  //      with `type: 'separator'` and no other keys.
  //   * `visible` (optional) A {Boolean} indicating whether the menu item
  //     should appear in the menu. Defaults to `true`.
  //   * `created` (optional) A {Function} that is called on the item each time a
  //     context menu is created via a right click. You can assign properties to
  //    `this` to dynamically compute the command, label, etc. This method is
  //    actually called on a clone of the original item template to prevent state
  //    from leaking across context menu deployments. Called with the following
  //    argument:
  //     * `event` The click event that deployed the context menu.
  //   * `shouldDisplay` (optional) A {Function} that is called to determine
  //     whether to display this item on a given context menu deployment. Called
  //     with the following argument:
  //     * `event` The click event that deployed the context menu.

  //   * `id` (internal) A {String} containing the menu item's id.
  // Returns a {Disposable} on which `.dispose()` can be called to remove the
  // added menu items.
  add(itemsBySelector, throwOnInvalidSelector = true) {
    var addedItemSets, itemSet, items, selector;
    addedItemSets = [];
    for (selector in itemsBySelector) {
      items = itemsBySelector[selector];
      if (throwOnInvalidSelector) {
        validateSelector(selector);
      }
      itemSet = new ContextMenuItemSet(selector, items);
      addedItemSets.push(itemSet);
      this.itemSets.push(itemSet);
    }
    return new Disposable(() => {
      var i, len;
      for (i = 0, len = addedItemSets.length; i < len; i++) {
        itemSet = addedItemSets[i];
        this.itemSets.splice(this.itemSets.indexOf(itemSet), 1);
      }
    });
  }

  templateForElement(target) {
    return this.templateForEvent({target});
  }

  templateForEvent(event) {
    var currentTarget, currentTargetItems, i, item, itemForEvent, itemSet, j, k, len, len1, len2, matchingItemSets, ref2, template;
    template = [];
    currentTarget = event.target;
    while (currentTarget != null) {
      currentTargetItems = [];
      matchingItemSets = this.itemSets.filter(function(itemSet) {
        return currentTarget.webkitMatchesSelector(itemSet.selector);
      });
      for (i = 0, len = matchingItemSets.length; i < len; i++) {
        itemSet = matchingItemSets[i];
        ref2 = itemSet.items;
        for (j = 0, len1 = ref2.length; j < len1; j++) {
          item = ref2[j];
          itemForEvent = this.cloneItemForEvent(item, event);
          if (itemForEvent) {
            MenuHelpers.merge(currentTargetItems, itemForEvent, itemSet.specificity);
          }
        }
      }
      for (k = 0, len2 = currentTargetItems.length; k < len2; k++) {
        item = currentTargetItems[k];
        MenuHelpers.merge(template, item, false);
      }
      currentTarget = currentTarget.parentElement;
    }
    this.pruneRedundantSeparators(template);
    this.addAccelerators(template);
    return this.sortTemplate(template);
  }

  // Adds an `accelerator` property to items that have key bindings. Electron
  // uses this property to surface the relevant keymaps in the context menu.
  addAccelerators(template) {
    var id, item, keymaps, keystrokes, ref2, results;
    results = [];
    for (id in template) {
      item = template[id];
      if (item.command) {
        keymaps = this.keymapManager.findKeyBindings({
          command: item.command,
          target: document.activeElement
        });
        keystrokes = keymaps != null ? (ref2 = keymaps[0]) != null ? ref2.keystrokes : void 0 : void 0;
        if (keystrokes) {
          // Electron does not support multi-keystroke accelerators. Therefore,
          // when the command maps to a multi-stroke key binding, show the
          // keystrokes next to the item's label.
          if (keystrokes.includes(' ')) {
            item.label += ` [${_.humanizeKeystroke(keystrokes)}]`;
          } else {
            item.accelerator = MenuHelpers.acceleratorForKeystroke(keystrokes);
          }
        }
      }
      if (Array.isArray(item.submenu)) {
        results.push(this.addAccelerators(item.submenu));
      } else {
        results.push(void 0);
      }
    }
    return results;
  }

  pruneRedundantSeparators(menu) {
    var index, keepNextItemIfSeparator, results;
    keepNextItemIfSeparator = false;
    index = 0;
    results = [];
    while (index < menu.length) {
      if (menu[index].type === 'separator') {
        if (!keepNextItemIfSeparator || index === menu.length - 1) {
          results.push(menu.splice(index, 1));
        } else {
          results.push(index++);
        }
      } else {
        keepNextItemIfSeparator = true;
        results.push(index++);
      }
    }
    return results;
  }

  sortTemplate(template) {
    var id, item;
    template = sortMenuItems(template);
    for (id in template) {
      item = template[id];
      if (Array.isArray(item.submenu)) {
        item.submenu = this.sortTemplate(item.submenu);
      }
    }
    return template;
  }

  // Returns an object compatible with `::add()` or `null`.
  cloneItemForEvent(item, event) {
    if (item.devMode && !this.devMode) {
      return null;
    }
    item = Object.create(item);
    if (typeof item.shouldDisplay === 'function') {
      if (!item.shouldDisplay(event)) {
        return null;
      }
    }
    if (typeof item.created === "function") {
      item.created(event);
    }
    if (Array.isArray(item.submenu)) {
      item.submenu = item.submenu.map((submenuItem) => {
        return this.cloneItemForEvent(submenuItem, event);
      }).filter(function(submenuItem) {
        return submenuItem !== null;
      });
    }
    return item;
  }

  showForEvent(event) {
    var menuTemplate;
    this.activeElement = event.target;
    menuTemplate = this.templateForEvent(event);
    if (!((menuTemplate != null ? menuTemplate.length : void 0) > 0)) {
      return;
    }
    remote.getCurrentWindow().emit('context-menu', menuTemplate);
  }

  clear() {
    var inspectElement;
    this.activeElement = null;
    this.itemSets = [];
    inspectElement = {
      'atom-workspace': [
        {
          label: 'Inspect Element',
          command: 'application:inspect',
          devMode: true,
          created: function(event) {
            var pageX,
        pageY;
            ({pageX,
        pageY} = event);
            return this.commandDetail = {
              x: pageX,
              y: pageY
            };
          }
        }
      ]
    };
    return this.add(inspectElement, false);
  }

};

ContextMenuItemSet = class ContextMenuItemSet {
  constructor(selector1, items1) {
    this.selector = selector1;
    this.items = items1;
    this.specificity = calculateSpecificity(this.selector);
  }

};
