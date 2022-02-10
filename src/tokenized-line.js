var CommentScopeRegex, Token, TokenizedLine, idCounter, isWhitespaceOnly;

Token = require('./token');

CommentScopeRegex = /(\b|\.)comment/;

idCounter = 1;

module.exports = TokenizedLine = (function() {
  class TokenizedLine {
    constructor(properties) {
      var tokens;
      this.id = idCounter++;
      if (properties == null) {
        return;
      }
      ({openScopes: this.openScopes, text: this.text, tags: this.tags, ruleStack: this.ruleStack, tokenIterator: this.tokenIterator, grammar: this.grammar, tokens} = properties);
      this.cachedTokens = tokens;
    }

    getTokenIterator() {
      return this.tokenIterator.reset(this);
    }

    tokenAtBufferColumn(bufferColumn) {
      return this.tokens[this.tokenIndexAtBufferColumn(bufferColumn)];
    }

    tokenIndexAtBufferColumn(bufferColumn) {
      var column, i, index, len, ref, token;
      column = 0;
      ref = this.tokens;
      for (index = i = 0, len = ref.length; i < len; index = ++i) {
        token = ref[index];
        column += token.value.length;
        if (column > bufferColumn) {
          return index;
        }
      }
      return index - 1;
    }

    tokenStartColumnForBufferColumn(bufferColumn) {
      var delta, i, len, nextDelta, ref, token;
      delta = 0;
      ref = this.tokens;
      for (i = 0, len = ref.length; i < len; i++) {
        token = ref[i];
        nextDelta = delta + token.bufferDelta;
        if (nextDelta > bufferColumn) {
          break;
        }
        delta = nextDelta;
      }
      return delta;
    }

    isComment() {
      var i, j, len, len1, ref, ref1, startIndex, tag;
      if (this.isCommentLine != null) {
        return this.isCommentLine;
      }
      this.isCommentLine = false;
      ref = this.openScopes;
      for (i = 0, len = ref.length; i < len; i++) {
        tag = ref[i];
        if (this.isCommentOpenTag(tag)) {
          this.isCommentLine = true;
          return this.isCommentLine;
        }
      }
      startIndex = 0;
      ref1 = this.tags;
      for (j = 0, len1 = ref1.length; j < len1; j++) {
        tag = ref1[j];
        // If we haven't encountered any comment scope when reading the first
        // non-whitespace chunk of text, then we consider this as not being a
        // comment line.
        if (tag > 0) {
          if (!isWhitespaceOnly(this.text.substr(startIndex, tag))) {
            break;
          }
          startIndex += tag;
        }
        if (this.isCommentOpenTag(tag)) {
          this.isCommentLine = true;
          return this.isCommentLine;
        }
      }
      return this.isCommentLine;
    }

    isCommentOpenTag(tag) {
      var scope;
      if (tag < 0 && (tag & 1) === 1) {
        scope = this.grammar.scopeForId(tag);
        if (CommentScopeRegex.test(scope)) {
          return true;
        }
      }
      return false;
    }

    tokenAtIndex(index) {
      return this.tokens[index];
    }

    getTokenCount() {
      var count, i, len, ref, tag;
      count = 0;
      ref = this.tags;
      for (i = 0, len = ref.length; i < len; i++) {
        tag = ref[i];
        if (tag >= 0) {
          count++;
        }
      }
      return count;
    }

  };

  Object.defineProperty(TokenizedLine.prototype, 'tokens', {
    get: function() {
      var iterator, tokens;
      if (this.cachedTokens) {
        return this.cachedTokens;
      } else {
        iterator = this.getTokenIterator();
        tokens = [];
        while (iterator.next()) {
          tokens.push(new Token({
            value: iterator.getText(),
            scopes: iterator.getScopes().slice()
          }));
        }
        return tokens;
      }
    }
  });

  return TokenizedLine;

}).call(this);

isWhitespaceOnly = function(text) {
  var char, i, len;
  for (i = 0, len = text.length; i < len; i++) {
    char = text[i];
    if (char !== '\t' && char !== ' ') {
      return false;
    }
  }
  return true;
};
