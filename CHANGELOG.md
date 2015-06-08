# Change Log
All notable changes to this project will be documented in this file.

## [0.12.2] - 2015-06-08
### Fixed
- Adding quote signs to prefix search, fixes missing search results
- Updated GitHub site plugin

## [0.12.1] - 2015-03-04
### Added
- Allow access to public API of sandbox.orcid.org

### Changed
- Deprecated constructor in OAuth client
- Deprecated setters in SearchKey class
- Using persistent ORCID entries in test cases

## [0.12.0] - 2015-03-02
### Added
- New methods for reading profiles and biographical data

### Changed
- Client is now using API 1.2
- Inform user of missing properties file during tests

### Fixed
- Moved test resources to correct directory
- Fixed NPE when Restlet context is null
- Set source encoding to remove Maven warnings
