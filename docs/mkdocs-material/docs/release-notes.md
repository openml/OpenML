# Release notes

## Upgrading

To upgrade Material to the latest version, use pip:

``` sh
pip install --upgrade mkdocs-material
```

To determine the currently installed version, use the following command:

``` sh
pip show mkdocs-material | grep -E ^Version
# Version 2.6.0
```

### Material 1.x to 2.x

* Material for MkDocs 2.x requires MkDocs 0.17.1, as this version introduced
  changes to the way themes can define options. The following variables inside
  your project's `mkdocs.yml` need to be renamed:

    * `extra.feature` becomes `theme.feature`
    * `extra.palette` becomes `theme.palette`
    * `extra.font` becomes `theme.font`
    * `extra.logo` becomes `theme.logo`

* Favicon support has been dropped by MkDocs, it must now be defined in
  `theme.favicon` (previously `site_favicon`).

* Localization is now separated into theme language and search language. While
  there can only be a single language on theme-level, the search supports
  multiple languages which can be separated by commas. See the getting started
  guide for more guidance.

* The search tokenizer can now be set through `extra.search.tokenizer`.

## Changelog

### 2.6.0 <small>_ February 2, 2018</small>

* Moved default search configuration to default translation (English)
* Added support to automatically set text direction from translation
* Added support to disable search stop word filter in translation
* Added support to disable search trimmer in translation
* Added Persian translations
* Fixed support for Polish search
* Fixed disappearing GitHub, GitLab and Bitbucket repository icons

### 2.5.5 <small>_ January 31, 2018</small>

* Added Hungarian translations

### 2.5.4 <small>_ January 29, 2018</small>

* Fixed #683: `gh-deploy` fails inside Docker

### 2.5.3 <small>_ January 25, 2018</small>

* Added Ukrainian translations

### 2.5.2 <small>_ January 22, 2018</small>

* Added default search language mappings for all localizations
* Fixed [#673][673]: Error loading non-existent search language
* Fixed [#675][675]: Uncaught reference error when search plugin disabled

  [673]: https://github.com/squidfunk/mkdocs-material/issues/673
  [675]: https://github.com/squidfunk/mkdocs-material/issues/675

### 2.5.1 <small>_ January 20, 2018</small>

* Fixed permalink for main headline
* Improved missing translation handling with English as a fallback
* Improved accessibility with skip-to-content link

### 2.5.0 <small>_ January 13, 2018</small>

* Added support for right-to-left languages

### 2.4.0 <small>_ January 11, 2018</small>

* Added focus state for clipboard buttons
* Fixed [#400][400]: Search bar steals tab focus
* Fixed search not closing on ++enter++ when result is selected
* Fixed search not closing when losing focus due to ++tab++
* Fixed collapsed navigation links getting focus
* Fixed `outline` being cut off on ++tab++ focus of navigation links
* Fixed bug with first search result navigation being ignored
* Removed search result navigation via ++tab++ (use ++up++ and ++down++)
* Removed `outline` resets for links
* Improved general tabbing behavior on desktop

  [400]: https://github.com/squidfunk/mkdocs-material/issues/400

### 2.3.0 <small>_ January 9, 2018</small>

* Added `example` (synonym: `snippet`) style for Admonition
* Added synonym `abstract` for `summary` style for Admonition

### 2.2.6 <small>_ December 27, 2017</small>

* Added Turkish translations
* Fixed unclickable area below header in case JavaScript is not available

### 2.2.5 <small>_ December 18, 2017</small>

* Fixed [#639][639]: Broken default favicon

  [639]: https://github.com/squidfunk/mkdocs-material/issues/639

### 2.2.4 <small>_ December 18, 2017</small>

* Fixed [#638][638]: Build breaks with Jinja < 2.9

  [638]: https://github.com/squidfunk/mkdocs-material/issues/638

### 2.2.3 <small>_ December 13, 2017</small>

* Fixed [#630][630]: Admonition sets padding on any last child
* Adjusted Chinese (Traditional) translations

  [630]: https://github.com/squidfunk/mkdocs-material/issues/630

### 2.2.2 <small>_ December 8, 2017</small>

* Added Dutch translations
* Adjusted targeted link and footnote offsets
* Simplified Admonition styles and fixed padding bug

### 2.2.1 <small>_ December 2, 2017</small>

* Fixed [#616][616]: Minor styling error with title-only admonition blocks
* Removed border for table of contents and improved spacing

  [616]: https://github.com/squidfunk/mkdocs-material/issues/616

### 2.2.0 <small>_ November 22, 2017</small>

* Added support for hero teaser
* Added Portuguese translations
* Fixed [#586][586]: Footnote backref target offset regression
* Fixed [#605][605]: Search stemmers not correctly loaded

  [586]: https://github.com/squidfunk/mkdocs-material/issues/586
  [605]: https://github.com/squidfunk/mkdocs-material/issues/605

### 2.1.1 <small>_ November 21, 2017</small>

* Replaced deprecated `babel-preset-es2015` with `babel-preset-env`
* Refactored Gulp build pipeline with Webpack
* Removed right border on sidebars
* Fixed broken color transition on header

### 2.1.0 <small>_ November 19, 2017</small>

* Added support for `white` as a primary color
* Added support for sliding site name and title
* Fixed redundant clipboard button when using line numbers on code blocks
* Improved header appearance by making it taller
* Improved tabs appearance
* Improved CSS customizability by leveraging inheritance
* Removed scroll shadows via `background-attachment`

### 2.0.4 <small>_ November 5, 2017</small>

* Fixed `details` not opening with footnote reference

### 2.0.3 <small>_ November 5, 2017</small>

* Added Japanese translations
* Fixed [#540][540]: Jumping to anchor inside `details` doesn't open it
* Fixed active link colors in footer

  [540]: https://github.com/squidfunk/mkdocs-material/issues/540

### 2.0.2 <small>_ November 1, 2017</small>

* Added Russian translations
* Fixed [#542][542]: Horizontal scrollbar between `1220px` and `1234px`
* Fixed [#553][553]: Metadata values only rendering first character
* Fixed [#558][558]: Flash of unstyled content
* Fixed favicon regression caused by deprecation upstream

  [542]: https://github.com/squidfunk/mkdocs-material/issues/542
  [553]: https://github.com/squidfunk/mkdocs-material/issues/553
  [558]: https://github.com/squidfunk/mkdocs-material/issues/558

### 2.0.1 <small>_ October 31, 2017</small>

* Fixed error when initializing search
* Fixed styles for link to edit the current page
* Fixed styles on nested admonition in details

### 2.0.0 <small>_ October 31, 2017</small>

* Added support for MkDocs 0.17.1 theme configuration options
* Added support for easier configuration of search tokenizer
* Added support to disable search
* Added Korean translations
* Removed support for MkDocs 0.16.x

### 1.12.2 <small>_ October 26, 2017</small>

* Added Italian, Norwegian, French and Chinese translations

### 1.12.1 <small>_ October 22, 2017</small>

* Added Polish, Swedish and Spanish translations
* Improved downward compatibility with custom partials
* Temporarily pinned MkDocs version within Docker image to 0.16.3
* Fixed [#519][519]: Missing theme configuration file

  [519]: https://github.com/squidfunk/mkdocs-material/issues/519

### 1.12.0 <small>_ October 20, 2017</small>

* Added support for setting language(s) via `mkdocs.yml`
* Added support for default localization
* Added German and Danish translations
* Fixed [#374][374]: Search bar misalignment on big screens

  [374]: https://github.com/squidfunk/mkdocs-material/issues/374

### 1.11.0 <small>_ October 19, 2017</small>

* Added localization to clipboard
* Refactored localization logic

### 1.10.4 <small>_ October 18, 2017</small>

* Improved print styles of code blocks
* Improved search UX (don't close on enter if no selection)
* Fixed [#495][495]: Vertical scrollbar on short pages

  [495]: https://github.com/squidfunk/mkdocs-material/issues/495

### 1.10.3 <small>_ October 11, 2017</small>

* Fixed [#484][484]: Vertical scrollbar on some MathJax formulas
* Fixed [#483][483]: Footnote backref target offset regression

  [483]: https://github.com/squidfunk/mkdocs-material/issues/483
  [484]: https://github.com/squidfunk/mkdocs-material/issues/484

### 1.10.2 <small>_ October 6, 2017</small>

* Fixed [#468][468]: Sidebar shows scrollbar if content is shorter (in Safari)

  [468]: https://github.com/squidfunk/mkdocs-material/issues/468

### 1.10.1 <small>_ September 14, 2017</small>

* Fixed [#455][455]: Bold code blocks rendered with normal font weight

  [455]: https://github.com/squidfunk/mkdocs-material/issues/455

### 1.10.0 <small>_ September 1, 2017</small>

* Added support to make logo default icon configurable
* Fixed uninitialized overflow scrolling on main pane for iOS
* Fixed error in mobile navigation in case JavaScript is not available
* Fixed incorrect color transition for nested panes in mobile navigation
* Improved checkbox styles for Tasklist from PyMdown Extension package

### 1.9.0 <small>_ August 29, 2017</small>

* Added `info` (synonym: `todo`) style for Admonition
* Added `question` (synonym: `help`, `faq`) style for Admonition
* Added support for Details from PyMdown Extensions package
* Improved Admonition styles to match Details
* Improved styles for social links in footer
* Replaced ligatures with Unicode code points to avoid broken layout
* Upgraded PyMdown Extensions package dependency to >= 3.4

### 1.8.1 <small>_ August 7, 2017</small>

* Fixed [#421][421]: Missing pagination for GitHub API

  [421]: https://github.com/squidfunk/mkdocs-material/issues/421

### 1.8.0 <small>_ August 2, 2017</small>

* Added support for lazy-loading of search results for better performance
* Added support for customization of search tokenizer/separator
* Fixed [#424][424]: Search doesn't handle capital letters anymore
* Fixed [#419][419]: Search doesn't work on whole words

  [419]: https://github.com/squidfunk/mkdocs-material/issues/419
  [424]: https://github.com/squidfunk/mkdocs-material/issues/424

### 1.7.5 <small>_ July 25, 2017</small>

* Fixed [#398][398]: Forms broken due to search shortcuts
* Improved search overall user experience
* Improved search matching and highlighting
* Improved search accessibility

  [398]: https://github.com/squidfunk/mkdocs-material/issues/398

### 1.7.4 <small>_ June 21, 2017</small>

* Fixed functional link colors in table of contents for active palette
* Fixed [#368][368]: Compatibility issues with IE11

  [368]: https://github.com/squidfunk/mkdocs-material/issues/368

### 1.7.3 <small>_ June 7, 2017</small>

* Fixed error when setting language to Japanese for site search

### 1.7.2 <small>_ June 6, 2017</small>

* Fixed offset of search box when `repo_url` is not set
* Fixed non-disappearing tooltip

### 1.7.1 <small>_ June 1, 2017</small>

* Fixed wrong `z-index` order of header, overlay and drawer
* Fixed wrong offset of targeted footnote back references

### 1.7.0 <small>_ June 1, 2017</small>

* Added "copy to clipboard" buttons to code blocks
* Added support for multilingual site search
* Fixed search term highlighting for non-latin languages

### 1.6.4 <small>_ May 24, 2017</small>

* Fixed [#337][337]: JavaScript error for GitHub organization URLs

  [337]: https://github.com/squidfunk/mkdocs-material/issues/337

### 1.6.3 <small>_ May 16, 2017</small>

* Fixed [#329][329]: Broken source stats for private or unknown GitHub repos

  [329]: https://github.com/squidfunk/mkdocs-material/issues/329

### 1.6.2 <small>_ May 15, 2017</small>

* Fixed [#316][316]: Fatal error for git clone on Windows
* Fixed [#320][320]: Chrome 58 creates double underline for `abbr` tags
* Fixed [#323][323]: Ligatures rendered inside code blocks
* Fixed miscalculated sidebar height due to missing margin collapse
* Changed deprecated MathJax CDN to Cloudflare

  [316]: https://github.com/squidfunk/mkdocs-material/issues/316
  [320]: https://github.com/squidfunk/mkdocs-material/issues/320
  [323]: https://github.com/squidfunk/mkdocs-material/issues/323

### 1.6.1 <small>_ April 23, 2017</small>

* Fixed following of active/focused element if search input is focused
* Fixed layer order of search component elements

### 1.6.0 <small>_ April 22, 2017</small>

* Added build test for Docker image on Travis
* Added search overlay for better user experience (focus)
* Added language from localizations to `html` tag
* Fixed [#270][270]: source links broken for absolute URLs
* Fixed missing top spacing for first targeted element in content
* Fixed too small footnote divider when using larger font sizes

  [270]: https://github.com/squidfunk/mkdocs-material/issues/270

### 1.5.5 <small>_ April 20, 2017</small>

* Fixed [#282][282]: Browser search (<kbd>Meta</kbd>+<kbd>F</kbd>) is hijacked

  [282]: https://github.com/squidfunk/mkdocs-material/issues/282

### 1.5.4 <small>_ April 8, 2017</small>

* Fixed broken highlighting for two or more search terms
* Fixed missing search results when only a `h1` is present
* Fixed unresponsive overlay on Android

### 1.5.3 <small>_ April 7, 2017</small>

* Fixed deprecated calls for template variables
* Fixed wrong palette color for focused search result
* Fixed JavaScript errors on 404 page
* Fixed missing top spacing on 404 page
* Fixed missing right spacing on overflow of source container

### 1.5.2 <small>_ April 5, 2017</small>

* Added requirements as explicit dependencies in `setup.py`
* Fixed non-synchronized transitions in search form

### 1.5.1 <small>_ March 30, 2017</small>

* Fixed rendering and offset of targetted footnotes
* Fixed [#238][238]: Link on logo is not set to `site_url`

  [238]: https://github.com/squidfunk/mkdocs-material/issues/238

### 1.5.0 <small>_ March 24, 2017</small>

* Added support for localization of search placeholder
* Added keyboard events for quick access of search
* Added keyboard events for search control
* Added opacity on hover for search buttons
* Added git hook to skip CI build on non-src changes
* Fixed non-resetting search placeholder when input is cleared
* Fixed error for unescaped parentheses in search term
* Fixed [#229][229]: Button to clear search missing
* Fixed [#231][231]: Escape key doesn't exit search
* Removed old-style figures from font feature settings

  [229]: https://github.com/squidfunk/mkdocs-material/issues/229
  [231]: https://github.com/squidfunk/mkdocs-material/issues/231

### 1.4.1 <small>_ March 16, 2017</small>

* Fixed invalid destructuring attempt on NodeList (in Safari, Edge, IE)

### 1.4.0 <small>_ March 16, 2017</small>

* Added support for grouping searched sections by documents
* Added support for highlighting of search terms
* Added support for localization of search results
* Fixed [#216][216]: table of contents icon doesn't show if `h1` is not present
* Reworked style and layout of search results for better usability

  [216]: https://github.com/squidfunk/mkdocs-material/issues/216

### 1.3.0 <small>_ March 11, 2017</small>

* Added support for page-specific title and description using metadata
* Added support for linking source files to documentation
* Fixed jitter and offset of sidebar when zooming browser
* Fixed incorrectly initialized tablet sidebar height
* Fixed regression for [#1][1]: GitHub stars break if `repo_url` ends with a `/`
* Fixed undesired white line below copyright footer due to base font scaling
* Fixed issue with whitespace in path for scripts
* Fixed [#205][205]: support non-fixed (static) header
* Refactored footnote references for better visibility
* Reduced repaints to a minimum for non-tabs configuration
* Reduced contrast of edit button (slightly)

  [205]: https://github.com/squidfunk/mkdocs-material/issues/205

### 1.2.0 <small>_ March 3, 2017</small>

* Added `quote` (synonym: `cite`) style for Admonition
* Added help message to build pipeline
* Fixed wrong navigation link colors when applying palette
* Fixed [#197][197]: Link missing in tabs navigation on deeply nested items
* Removed unnecessary dev dependencies

  [197]: https://github.com/squidfunk/mkdocs-material/issues/197

### 1.1.1 <small>_ February 26, 2017</small>

* Fixed incorrectly displayed nested lists when using tabs

### 1.1.0 <small>_ February 26, 2017</small>

* Added tabs navigation feature (optional)
* Added Disqus integration (optional)
* Added a high resolution Favicon with the new logo
* Added static type checking using Facebook's Flow
* Fixed [#173][173]: Dictionary elements have no bottom spacing
* Fixed [#175][175]: Tables cannot be set to 100% width
* Fixed race conditions in build related to asset revisioning
* Fixed accidentally re-introduced Permalink on top-level headline
* Fixed alignment of logo in drawer on IE11
* Refactored styles related to tables
* Refactored and automated Docker build and PyPI release
* Refactored build scripts

  [173]: https://github.com/squidfunk/mkdocs-material/issues/173
  [175]: https://github.com/squidfunk/mkdocs-material/issues/175

### 1.0.5 <small>_ February 18, 2017</small>

* Fixed [#153][153]: Sidebar flows out of constrained area in Chrome 56
* Fixed [#159][159]: Footer jitter due to JavaScript if content is short

  [153]: https://github.com/squidfunk/mkdocs-material/issues/153
  [159]: https://github.com/squidfunk/mkdocs-material/issues/159

### 1.0.4 <small>_ February 16, 2017</small>

* Fixed [#142][142]: Documentation build errors if `h1` is defined as raw HTML
* Fixed [#164][164]: PyPI release does not build and install
* Fixed offsets of targeted headlines
* Increased sidebar font size by `0.12rem`

  [142]: https://github.com/squidfunk/mkdocs-material/issues/142
  [164]: https://github.com/squidfunk/mkdocs-material/issues/164

### 1.0.3 <small>_ January 22, 2017</small>

* Fixed [#117][117]: Table of contents items don't blur on fast scrolling
* Refactored sidebar positioning logic
* Further reduction of repaints

  [117]: https://github.com/squidfunk/mkdocs-material/issues/117

### 1.0.2 <small>_ January 15, 2017</small>

* Fixed [#108][108]: Horizontal scrollbar in content area

  [108]: https://github.com/squidfunk/mkdocs-material/issues/108

### 1.0.1 <small>_ January 14, 2017</small>

* Fixed massive repaints happening when scrolling
* Fixed footer back reference positions in case of overflow
* Fixed header logo from showing when the menu icon is rendered
* Changed scrollbar behavior to only show when content overflows

### 1.0.0 <small>_ January 13, 2017</small>

* Introduced Webpack for more sophisticated JavaScript bundling
* Introduced ESLint and Stylelint for code style checks
* Introduced more accurate Material Design colors and shadows
* Introduced modular scales for harmonic font sizing
* Introduced git-hooks for better development workflow
* Rewrite of CSS using the BEM methodology and SassDoc guidelines
* Rewrite of JavaScript using ES6 and Babel as a transpiler
* Rewrite of Admonition, Permalinks and CodeHilite integration
* Rewrite of the complete typographical system
* Rewrite of Gulp asset pipeline in ES6 and separation of tasks
* Removed Bower as a dependency in favor of NPM
* Removed custom icon build in favor of the Material Design iconset
* Removed `_blank` targets on links due to vulnerability: http://bit.ly/1Mk2Rtw
* Removed unversioned assets from build directory
* Restructured templates into base templates and partials
* Added build and watch scripts in `package.json`
* Added support for Metadata and Footnotes Markdown extensions
* Added support for PyMdown Extensions package
* Added support for collapsible sections in navigation
* Added support for separate table of contents
* Added support for better accessibility through REM-based layout
* Added icons for GitHub, GitLab and BitBucket integrations
* Added more detailed documentation on specimen, extensions etc.
* Added a `404.html` error page for deployment on GitHub Pages
* Fixed live reload chain in watch mode when saving a template
* Fixed variable references to work with MkDocs 0.16

### 0.2.4 <small>_ June 26, 2016</small>

* Fixed improperly set default favicon
* Fixed [#33][33]: Protocol relative URL for webfonts doesn't work with
  `file://`
* Fixed [#34][34]: IE11 on Windows 7 doesn't honor `max-width` on `main` tag
* Fixed [#35][35]: Add styling for blockquotes

  [33]: https://github.com/squidfunk/mkdocs-material/issues/25
  [34]: https://github.com/squidfunk/mkdocs-material/issues/26
  [35]: https://github.com/squidfunk/mkdocs-material/issues/30

### 0.2.3 <small>_ May 16, 2016</small>

* Fixed [#25][25]: Highlight inline fenced blocks
* Fixed [#26][26]: Better highlighting for keystrokes
* Fixed [#30][30]: Suboptimal syntax highlighting for PHP

  [25]: https://github.com/squidfunk/mkdocs-material/issues/25
  [26]: https://github.com/squidfunk/mkdocs-material/issues/26
  [30]: https://github.com/squidfunk/mkdocs-material/issues/30

### 0.2.2 <small>_ March 20, 2016</small>

* Fixed [#15][15]: Document Pygments dependency for CodeHilite
* Fixed [#16][16]: Favicon could not be set through `mkdocs.yml`
* Fixed [#17][17]: Put version into own container for styling
* Fixed [#20][20]: Fix rounded borders for tables

  [15]: https://github.com/squidfunk/mkdocs-material/issues/15
  [16]: https://github.com/squidfunk/mkdocs-material/issues/16
  [17]: https://github.com/squidfunk/mkdocs-material/issues/17
  [20]: https://github.com/squidfunk/mkdocs-material/issues/20

### 0.2.1 <small>_ March 12, 2016</small>

* Fixed [#10][10]: Invisible header after closing search bar with
  <kbd>ESC</kbd> key
* Fixed [#13][13]: Table cells don't wrap
* Fixed empty list in table of contents when no headline is defined
* Corrected wrong path for static asset monitoring in Gulpfile.js
* Set up tracking of site search for Google Analytics

  [10]: https://github.com/squidfunk/mkdocs-material/issues/10
  [13]: https://github.com/squidfunk/mkdocs-material/issues/13

### 0.2.0 <small>_ February 24, 2016</small>

* Fixed [#6][6]: Include multiple color palettes via `mkdocs.yml`
* Fixed [#7][7]: Better colors for links inside admonition notes and warnings
* Fixed [#9][9]: Text for prev/next footer navigation should be customizable
* Refactored templates (replaced `if`/`else` with modifiers where possible)

  [6]: https://github.com/squidfunk/mkdocs-material/issues/6
  [7]: https://github.com/squidfunk/mkdocs-material/issues/7
  [9]: https://github.com/squidfunk/mkdocs-material/issues/9

### 0.1.3 <small>_ February 21, 2016</small>

* Fixed [#3][3]: Ordered lists within an unordered list have `::before` content
* Fixed [#4][4]: Click on Logo/Title without Github-Repository: `"None"`
* Fixed [#5][5]: Page without headlines renders empty list in table of contents
* Moved Modernizr to top to ensure basic usability in IE8

  [3]: https://github.com/squidfunk/mkdocs-material/issues/3
  [4]: https://github.com/squidfunk/mkdocs-material/issues/4
  [5]: https://github.com/squidfunk/mkdocs-material/issues/5

### 0.1.2 <small>_ February 16, 2016</small>

* Fixed styles for deep navigational hierarchies
* Fixed webfont delivery problem when hosted in subdirectories
* Fixed print styles in mobile/tablet configuration
* Added option to configure fonts in `mkdocs.yml` with fallbacks
* Changed styles for admonition notes and warnings
* Set download link to latest version if available
* Set up tracking of outgoing links and actions for Google Analytics

### 0.1.1 <small>_ February 11, 2016</small>

* Fixed [#1][1]: GitHub stars don't work if the repo_url ends with a `/`
* Updated NPM and Bower dependencies to most recent versions
* Changed footer/copyright link to Material theme to GitHub pages
* Made MkDocs building/serving in build process optional
* Set up continuous integration with [Travis][2]

  [1]: https://github.com/squidfunk/mkdocs-material/issues/1
  [2]: https://travis-ci.org

### 0.1.0 <small>_ February 9, 2016</small>

* Initial release
