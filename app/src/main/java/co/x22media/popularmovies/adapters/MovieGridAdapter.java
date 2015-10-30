package co.x22media.popularmovies.adapters;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import co.x22media.popularmovies.R;
import co.x22media.popularmovies.models.Movie;

/**
 * Created by kit on 21/10/15.
 */
public class MovieGridAdapter extends CursorAdapter {
    public MovieGridAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View v = LayoutInflater.from(context).inflate(R.layout.grid_item_movie, parent, false);

        ViewHolder holder = new ViewHolder();
        holder.iv = (ImageView) v.findViewById(R.id.imageView);
        v.setTag(holder);

        return v;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        Movie m = Movie.fromCursor(cursor);

        // parse the poster URL
        String basePosterURL = "http://image.tmdb.org/t/p/w185";
        String posterURLString = basePosterURL + m.getPosterPath();

        ViewHolder holder = (ViewHolder)view.getTag();
        ImageView iv = holder.iv;
        Picasso.with(context)
                .load(posterURLString)
                .placeholder(R.drawable.poster_placeholder)
                .into(iv);
    }

    static class ViewHolder {
        ImageView iv;
    }
}

