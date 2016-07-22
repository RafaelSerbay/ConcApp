package com.example.rafael.concapp;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.nostra13.universalimageloader.utils.StorageUtils;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by Rafael on 02/07/2016.
 * taken from https://guides.codepath.com/android/Using-an-ArrayAdapter-with-ListView#creating-the-view-template
 */
public class TeamAdapter extends ArrayAdapter<Team> implements Filterable {
    ArrayList<Team> teamsList;
    ArrayList<Team> mList;
    private ArrayList<Team> filteredTeamsList;
    TeamsFilter teamsFilter;
    ImageLoader imageLoader;
    DisplayImageOptions options;
    Context mContext;
    //String img_url= "http://10.0.2.2/ConcApp/Team_Logo/Example1.png";
    // View lookup cache
    private static class ViewHolder {
        TextView Team_Name;
        ImageView Team_Logo;
    }

    public TeamAdapter(Context context, ArrayList<Team> teams) {
        super(context, R.layout.item_team, teams);
        mContext=context;
        //---------------IMG-----
        File cacheDir = StorageUtils.getOwnCacheDirectory(mContext, "http://10.0.2.2/ConcApp/Team_Logo/");//for caching
        try {
            // Get singletone instance of ImageLoader
            imageLoader = ImageLoader.getInstance();
            // Create configuration for ImageLoader (all options are optional)
            ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(mContext).build();
// Initialize ImageLoader with created configuration. Do it once.
            imageLoader.init(config);
            options = new DisplayImageOptions.Builder()
                    .cacheInMemory(true)
                    .cacheOnDisk(true)
                    .showImageOnLoading(R.mipmap.ic_launcher)//display stub image until image is loaded
                    .displayer(new RoundedBitmapDisplayer(20))
                    .build();
            //---------------/IMG----
            teamsList = teams;
            mList = teams;
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position

        //Log.d("Data TeamAdap", "Receiving: Filtered");
       // team = getItem(position);
        Team team = mList.get(position);

        // Check if an existing view is being reused, otherwise inflate the view
        ViewHolder viewHolder; // view lookup cache stored in tag
        if (convertView == null) {
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.item_team, parent, false);
            viewHolder.Team_Name = (TextView) convertView.findViewById(R.id.list_item_team);
            viewHolder.Team_Logo = (ImageView) convertView.findViewById(R.id.list_item_teamImage);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        // Populate the data into the template view using the data object
        viewHolder.Team_Name.setText(team.getTeam_Name());
        try {
            imageLoader.displayImage("http://10.0.2.2/ConcApp/Team_Logo/" + team.getTeam_Logo(), viewHolder.Team_Logo, options);
        }catch (Exception e){
            e.printStackTrace();
        }
        // Return the completed view to render on screen
        return convertView;
    }

    @Override
    public Filter getFilter() {
        if (teamsFilter == null)
            teamsFilter = new TeamsFilter();

        return teamsFilter;
    }

    // Filter

    private class TeamsFilter extends Filter {

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            // Create a FilterResults object
            FilterResults results = new FilterResults();

            // If the constraint (search string/pattern) is null
            // or its length is 0, i.e., its empty then
            // we just set the `values` property to the
            // original contacts list which contains all of them
            if (constraint == null || constraint.length() == 0) {
                results.values = teamsList;
                results.count = teamsList.size();
            }
            else {
                // Some search constraint has been passed
                // so let's filter accordingly
                ArrayList<Team> filteredContacts = new ArrayList<Team>();

                // We'll go through all the contacts and see
                // if they contain the supplied string
                for (Team t : teamsList) {
                    if (t.getTeam_Name().toUpperCase().contains( constraint.toString().toUpperCase() )) {
                        // if `contains` == true then add it
                        // to our filtered list
                        filteredContacts.add(t);
                    }
                }

                // Finally set the filtered values and size/count
                results.values = filteredContacts;
                results.count = filteredContacts.size();
            }

            // Return our FilterResults object
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            mList = (ArrayList<Team>) results.values;
            notifyDataSetChanged();
        }
    }
}
