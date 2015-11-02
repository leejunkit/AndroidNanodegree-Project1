package co.x22media.popularmovies.helpers;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import co.x22media.popularmovies.R;
import co.x22media.popularmovies.provider.MovieProvider;

/**
 * Created by kit on 2/11/15.
 */
public class SharedPreferencesUtility {
    public static String getCurrentSortSetting(Context c) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(c);
        return prefs.getString(c.getString(R.string.pref_sort_setting_key), null);
    }

    public static void setSortSetting(Context c, String sortSetting) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(c);
        SharedPreferences.Editor e = prefs.edit();
        e.putString(c.getString(R.string.pref_sort_setting_key), sortSetting);
        e.commit();

        c.getContentResolver().delete(MovieProvider.getMovieDirUri(), null, null);
    }

    public static void resetAllPreferences(Context c) {
        c.getContentResolver().delete(MovieProvider.getMovieDirUri(), null, null);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(c);
        prefs.edit().clear().commit();
    }

    public static void bootstrapSortSetting(Context c) {
        // don't do anything if the sort setting is already set
        if (null != SharedPreferencesUtility.getCurrentSortSetting(c)) {
            return;
        }

        SharedPreferencesUtility.setSortSetting(c,
                c.getString(R.string.pref_sort_setting_values_default));
    }

    public static boolean moviesCachedForSortSetting(Context c, String sortSetting) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(c);
        return prefs.getBoolean("cached." + sortSetting, false);
    }

    public static void setMoviesCachedForSortSetting(Context c, String sortSetting) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(c);
        SharedPreferences.Editor e = prefs.edit();
        e.putBoolean("cached." + sortSetting, true);
        e.commit();
    }

    public static String getCursorSortOrderForCurrentSortSetting(Context c) {
        String sortOrder;
        String defaultSortSetting = c.getString(R.string.pref_sort_setting_values_default);
        String currentSortSetting = SharedPreferencesUtility.getCurrentSortSetting(c);
        if (defaultSortSetting.equals(currentSortSetting)) {
            sortOrder = MovieProvider.Movie.KEY_POPULARITY;
        }

        else {
            sortOrder = MovieProvider.Movie.KEY_USER_RATING;
        }

        sortOrder += " DESC";

        return sortOrder;
    }
}
