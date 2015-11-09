# Android Nanodegree P2: Popular Movies
I used the same repo for P2 project submission.

## Questions for Reviewer (P2 submission)
1. In tablet mode, I wanted to load the detail view from the first movie in the grid view automatically, so the detail view would not be empty. However, trying to manipulate fragments in my Loader's `onLoadFinished` method would result in an `IllegalStateException`, because apparently I should not be manipulating fragment state then. How should I get around this problem? Googling for answers revealed that it's a common problem. 

One workaround I thought of was to load the fragments first before starting my loaders, then getting the fragment from the `FragmentManager` and calling a "bind movie to view" public method on the fragment. I can then use this method to bind the view both when the fragment is newly created, or if there's a `savedInstanceState` bundle object upon view rotation. Is this doable, and is there any disadvantage to doing this?

2. My detail view actually consists of 3 fragments: `MovieDetailFragment`, `MovieReviewsFragment` and a container `MovieDetailContainerFragment` to hold the `ViewPager`. I pass the Movie's URI to each fragment, and each fragment creates its own `Loader` to retrieve the data from the database. I feel that this is quite wasteful, having to query the same object from the database 3 times, and also the amount of boilerplate code I have to write is simply annoying. 

Instead of doing that, can I `Parcelable` my Movie object, and pass them to my fragments? What are the advantages and disadvantages of doing this?

3. My code to handle the favorites button click, and to launch a trailer in an external app, they are duplicated across both my `MovieGridActivity` and `MovieDetailActivity`, because on tablet mode, the only Activity handling both grid and detail view is MovieGridActivity`. But I also need the same code in `MovieDetailActivity`, for when the app is running on a phone. How can I refactor this?

4. Coming from an iOS background, I still can't get used to the fact that the entire Activity is destroyed on an orientation change. It feels to me like programming for Android is 50% writing "hacks" to ensure that my view state remains consistent when things like these happen. It feels to me like this is definitely intended behavior for 99% of cases, so the underlying Android frameworks should be handling this for us. 

# Android Nanodegree P1: Popular Movies
Infinitely scrollable grid view of popular movies. Tap to see more details! There is also code to handle Internet connectivity status changes.

## themoviedb.org API Key
Replace the string resource in `app/src/main/res/values/api_keys.xml` with your own.

## Changelog for Project 1Submission 2

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