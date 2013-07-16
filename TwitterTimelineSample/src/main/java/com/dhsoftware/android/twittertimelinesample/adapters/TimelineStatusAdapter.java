package com.dhsoftware.android.twittertimelinesample.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.dhsoftware.android.twittertimelinesample.R;
import com.dhsoftware.android.twittertimelinesample.wrappers.StatusItemWrapper;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import twitter4j.Status;


/**
 * A very simple {@link android.widget.ArrayAdapter Adapter} to handle the Timeline. This time there's no fancy inversion going on; and the thing is
 * that the Adapter doesn't truly have a hold on the data, but a copy of it. The data in this sample is held by the Loader;
 * the {@link android.widget.ArrayAdapter Adapter} simply links to it in its private list in order to display it. That's why I made the case of
 * compositing the {@link android.widget.ArrayAdapter Adapter} with the {@link com.dhsoftware.android.TwitterTimelineSample.loaders.TwitterTimelineLoader TwitterTimelineLoader} for
 * better cohesion and design, but alas, it can be left for another day.
 * <br></br>
 * <br></br>
 * User: Dinesh Harjani (email: goldrunner18725@gmail.com) (github: the7thgoldrunner) (Twitter: @dinesharjani)
 * <br></br>
 * Date: 7/3/13
 */
public class TimelineStatusAdapter extends ArrayAdapter<Status> {

    private LayoutInflater mLayoutInflater;
    private ImageLoader mImageLoader;

    public TimelineStatusAdapter(Context context) {
        super(context, R.layout.timeline_listviewitem);
        init();
    }

    private void init() {
        mLayoutInflater = LayoutInflater.from(getContext());
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(getContext()).build();
        mImageLoader = ImageLoader.getInstance();
        mImageLoader.init(config);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View row = convertView;
        StatusItemWrapper wrapper;

        if (row == null) {
            row = mLayoutInflater.inflate(R.layout.timeline_listviewitem, null);
            wrapper = new StatusItemWrapper(row, mImageLoader);
            row.setTag(wrapper);
        }
        else {
            wrapper = (StatusItemWrapper) row.getTag();
        }

        wrapper.setForItem(getItem(position));

        return row;
    }

}
