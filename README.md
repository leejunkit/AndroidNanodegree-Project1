# Android Nanodegree P1: Popular Movies
Infinitely scrollable grid view of popular movies. Tap to see more details! There is also code to handle Internet connectivity status changes.

## themoviedb.org API Key
Replace the string resource in `app/src/main/res/values/api_keys.xml` with your own.

## Changelog for Submission 2

* Save fragment and activity state for `MovieGridActivity` to prevent data reloading on orientation change, as suggested by my first reviewer
* Implement the `ViewHolder` pattern
* Update to API level 23, to use `AppCompatActivity` instead of `ActionBarActivity`
* Use `PreferenceFragment` instead of `PreferenceActivity`

## Questions for Reviewer (1st submission)
1. Instead of global variables, I made it such that my `AsyncTask` accepts a callback in the constructor. Is that ok? Is the way I'm doing it acceptable?
 > There is nothing wrong in the way you've done things.

2. My `GridViewFragment` class is somewhat messy as I attempt to coordinate between the `EndlessScrollListener` class with preference changes and connectivity changes. I introduced some state into the fragment as well, in the form of `Boolean` instance variables. Is there a better way to go about doing it?
 > Although the code might look a bit messy, the idea is not entirely wrong. One other work around would be to have a new and old preference value, and see if they match in `onResume` and proceed accordingly.

3. I refactored the actual HTTP request/response code into `JSONHTTPHelper`. It's strange that on Android we have to manually deal with streams and buffers to read the HTTP response, whereas on other platforms HTTP code operates on a higher level of abstraction. Is this usual practice for Android development, or do most developers use a 3rd party HTTP library _(okHttp)_ or wrapper?
 > Yes, this is how it is in Android. You could use `OkHttp` or even `Retrofit`. Or anything else you find online.

## Citations
#### Movie Poster Placeholder image
Taken from http://l.yimg.com/os/mit/media/m/entity/images/movieperson_placeholder-103642.png

#### Infinite Scrolling
Implementation taken from https://github.com/codepath/android_guides/wiki/Endless-Scrolling-with-AdapterViews.