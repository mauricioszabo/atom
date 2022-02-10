var DefaultDirectoryProvider, Directory, fs, path, url;

({Directory} = require('pathwatcher'));

fs = require('fs-plus');

path = require('path');

url = require('url');

module.exports = DefaultDirectoryProvider = class DefaultDirectoryProvider {
  // Public: Create a Directory that corresponds to the specified URI.

  // * `uri` {String} The path to the directory to add. This is guaranteed not to
  // be contained by a {Directory} in `atom.project`.

  // Returns:
  // * {Directory} if the given URI is compatible with this provider.
  // * `null` if the given URI is not compatible with this provider.
  directoryForURISync(uri) {
    var directory, directoryPath, host, normalizedPath;
    normalizedPath = this.normalizePath(uri);
    ({host} = url.parse(uri));
    directoryPath = host ? uri : !fs.isDirectorySync(normalizedPath) && fs.isDirectorySync(path.dirname(normalizedPath)) ? path.dirname(normalizedPath) : normalizedPath;
    // TODO: Stop normalizing the path in pathwatcher's Directory.
    directory = new Directory(directoryPath);
    if (host) {
      directory.path = directoryPath;
      if (fs.isCaseInsensitive()) {
        directory.lowerCasePath = directoryPath.toLowerCase();
      }
    }
    return directory;
  }

  // Public: Create a Directory that corresponds to the specified URI.

  // * `uri` {String} The path to the directory to add. This is guaranteed not to
  // be contained by a {Directory} in `atom.project`.

  // Returns a {Promise} that resolves to:
  // * {Directory} if the given URI is compatible with this provider.
  // * `null` if the given URI is not compatible with this provider.
  directoryForURI(uri) {
    return Promise.resolve(this.directoryForURISync(uri));
  }

  // Public: Normalizes path.

  // * `uri` {String} The path that should be normalized.

  // Returns a {String} with normalized path.
  normalizePath(uri) {
    var matchData, pathWithNormalizedDiskDriveLetter;
    // Normalize disk drive letter on Windows to avoid opening two buffers for the same file
    pathWithNormalizedDiskDriveLetter = process.platform === 'win32' && (matchData = uri.match(/^([a-z]):/)) ? `${matchData[1].toUpperCase()}${uri.slice(1)}` : uri;
    return path.normalize(pathWithNormalizedDiskDriveLetter);
  }

};
