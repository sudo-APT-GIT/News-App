package com.smartnerd.newsapp;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class FeedAdapter extends ArrayAdapter {

    private static final String TAG = "FeedAdapter";
    private final int layoutResource;
    private final LayoutInflater layoutInflater;
    private List<FeedEntry> applications;

    public FeedAdapter(Context context, int resource, List<FeedEntry> applications) {
        super(context, resource);
        this.layoutResource = resource;
        this.layoutInflater = LayoutInflater.from(context);
        this.applications = applications;
    }

    //listview scrolls data up or down and displays new data by asking the getView() method
    //listView needs to know the number of items . this is done by calling the getCount() method

    //use ctrl+o to override
    @Override
    public int getCount() {
        return applications.size();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder viewHolder;

        if (convertView == null) {
            Log.d(TAG, "getView: called with null convertView");

            //convertView is null means that we have to create a new view else we can reuse the same view
            convertView = layoutInflater.inflate(layoutResource, parent, false);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);

        } else {
            Log.d(TAG, "getView: provided with convertView");
            viewHolder = (ViewHolder) convertView.getTag();
        }

//        TextView tvName = (TextView) convertView.findViewById(R.id.tvName); //since we are using constraintLayout we are inflating the view
//        TextView tvArtist = (TextView) convertView.findViewById(R.id.tvArtist);//so we are picking text widgets from this view
//        TextView tvSummary = (TextView) convertView.findViewById(R.id.tvSummary);

        FeedEntry currentApp = applications.get(position);

        viewHolder.tvName.setText(currentApp.getName());
        viewHolder.tvArtist.setText(currentApp.getArtist());
        viewHolder.tvSummary.setText(currentApp.getSummary());
        Log.d(TAG, "getView: "+currentApp.getName()+"  "+currentApp.getArtist()+"  "+currentApp.getSummary());
        return convertView;
    }

    private class ViewHolder {
        final TextView tvName;
        final TextView tvArtist;
        final TextView tvSummary;

        ViewHolder(View v) {

            this.tvName = v.findViewById(R.id.tvName);
            this.tvArtist = v.findViewById(R.id.tvArtist);
            this.tvSummary = v.findViewById(R.id.tvSummary);
        }
    }
}
