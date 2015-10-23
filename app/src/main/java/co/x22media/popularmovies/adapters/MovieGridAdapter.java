package co.x22media.popularmovies.adapters;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.List;

import co.x22media.popularmovies.R;
import co.x22media.popularmovies.models.Movie;

/**
 * Created by kit on 21/10/15.
 */
public class MovieGridAdapter extends ArrayAdapter<Movie> {
    public MovieGridAdapter(Activity context, List<Movie> movies) {
        super(context, 0, movies);
    }

    /**
     * Provides a view for an AdapterView (ListView, GridView, etc.)
     *
     * @param position    The AdapterView position that is requesting a view
     * @param convertView The recycled view to populate.
     *                    (search online for "android view recycling" to learn more)
     * @param parent The parent ViewGroup that is used for inflation.
     * @return The View for the position in the AdapterView.
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        // get the associated Movie object
        Movie m = getItem(position);

        if (null == convertView) {
            // get the LayoutInflater
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.grid_item_movie, parent, false);

            ViewHolder holder = new ViewHolder();
            holder.iv = (ImageView) convertView.findViewById(R.id.imageView);
            convertView.setTag(holder);
        }

        // find the ImageView
        ViewHolder holder = (ViewHolder)convertView.getTag();
        ImageView iv = holder.iv;
        Picasso.with(getContext())
                .load(m.getPosterURL())
                .placeholder(R.drawable.poster_placeholder)
                .into(iv);

        return convertView;
    }

    static class ViewHolder {
        ImageView iv;
    }
}

