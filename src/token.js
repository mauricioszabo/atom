var StartDotRegex, Token, _;

_ = require('underscore-plus');

StartDotRegex = /^\.?/;

// Represents a single unit of text as selected by a grammar.
module.exports = Token = (function() {
  class Token {
    constructor(properties) {
      ({value: this.value, scopes: this.scopes} = properties);
    }

    isEqual(other) {
      // TODO: scopes is deprecated. This is here for the sake of lang package tests
      return this.value === other.value && _.isEqual(this.scopes, other.scopes);
    }

    isBracket() {
      return /^meta\.brace\b/.test(_.last(this.scopes));
    }

    matchesScopeSelector(selector) {
      var targetClasses;
      targetClasses = selector.replace(StartDotRegex, '').split('.');
      return _.any(this.scopes, function(scope) {
        var scopeClasses;
        scopeClasses = scope.split('.');
        return _.isSubset(targetClasses, scopeClasses);
      });
    }

  };

  Token.prototype.value = null;

  Token.prototype.scopes = null;

  return Token;

}).call(this);
