package co.x22media.popularmovies.adapters;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import co.x22media.popularmovies.R;
import co.x22media.popularmovies.helpers.ExternalURLBuilder;

/**
 * Created by kit on 4/11/15.
 */
public class MovieVideosAdapter extends ArrayAdapter<JSONObject> {
    private final String LOG_TAG = MovieVideosAdapter.class.getSimpleName();
    private JSONArray mObjects;
    private Context mContext;

    public MovieVideosAdapter(Context context, JSONArray objects) {
        super(context, 0, 0);
        mContext = context;
        mObjects = objects;
    }

    @Override
    public int getCount() {
        return mObjects.length();
    }

    @Override
    public JSONObject getItem(int position) {
        try {
            return mObjects.getJSONObject(position);
        }

        catch (JSONException e) {
            Log.e(LOG_TAG, "JSONException: ", e);
            return null;
        }

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        JSONObject obj;
        try {
            obj = mObjects.getJSONObject(position);
        }

        catch (JSONException e) {
            Log.e(LOG_TAG, "Shit.", e);
            return null;
        }

        if (null == convertView) {
            View v = LayoutInflater.from(getContext()).inflate(R.layout.list_item_trailer, parent, false);
            TrailerViewTag holder = new TrailerViewTag();
            holder.iv = (ImageView) v.findViewById(R.id.trailer_image_view);
            v.setTag(holder);

            convertView = v;
        }

        TrailerViewTag vh = (TrailerViewTag)convertView.getTag();

        try {
            String youtubeID = obj.getString("key");
            vh.youtubeID = obj.getString("key");
            Picasso.with(getContext())
                    .load(ExternalURLBuilder.buildYoutubeThumbnailURLWithYoutubeId(youtubeID))
                    .placeholder(R.drawable.grey)
                    .error(R.drawable.grey)
                    .into(vh.iv);
        }

        catch (JSONException e) {
            Log.e(LOG_TAG, "JSONException: ", e);
            vh.youtubeID = null;

            Drawable d = mContext.getResources().getDrawable(R.drawable.grey);
            vh.iv.setImageDrawable(d);
        }

        return convertView;
    }


    public void renderVideosIntoLinearLayout(LinearLayout layout) {
        layout.removeAllViewsInLayout();
        int count = getCount();
        for (int i = 0; i < count; i++) {
            View v = getView(i, null, layout);
            layout.addView(v);
        }
    }
}
