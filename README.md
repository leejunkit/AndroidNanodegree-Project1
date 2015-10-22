# Android Nanodegree P1: Popular Movies
Infinitely scrollable grid view of popular movies. Tap to see more details! There is also code to handle Internet connectivity status changes.

## themoviedb.org API Key
Replace the string resource in `app/src/main/res/values/api_keys.xml` with your own.

## Questions for Reviewer
1. Instead of global variables, I made it such that my `AsyncTask` accepts a callback in the constructor. Is that ok? Is the way I'm doing it acceptable?

2. My `GridViewFragment` class is somewhat messy as I attempt to coordinate between the `EndlessScrollListener` class with preference changes and connectivity changes. I introduced some state into the fragment as well, in the form of `Boolean` instance variables. Is there a better way to go about doing it?

3. I refactored the actual HTTP request/response code into `JSONHTTPHelper`. It's strange that on Android we have to manually deal with streams and buffers to read the HTTP response, whereas on other platforms HTTP code operates on a higher level of abstraction. Is this usual practice for Android development, or do most developers use a 3rd party HTTP library _(okHttp)_ or wrapper?

## Citations
#### Movie Poster Placeholder image
Taken from http://l.yimg.com/os/mit/media/m/entity/images/movieperson_placeholder-103642.png

#### Infinite Scrolling
Implementation taken from https://github.com/codepath/android_guides/wiki/Endless-Scrolling-with-AdapterViews.