package com.smartnerd.newsapp;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity"; //logt is the shortcut
    private ListView listApps;
    private String feedUrl = "https://www.thehindu.com/news/national/feeder/default.rss";
   // private int feedLimit = 10;
    private String feedCacheUrl = "INVALIDATED";
    //psf and the enter is shortcut for public static final
    public static final String STATE_URL = "feedUrl";
    public static final String STATE_LIMIT = "feedLimit";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        listApps = (ListView) findViewById(R.id.xmlListView);
        if (savedInstanceState != null) {
            feedUrl = savedInstanceState.getString(STATE_URL);
           // feedLimit = savedInstanceState.getInt(STATE_LIMIT);
        }
        downloadURL(feedUrl);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // return super.onCreateOptionsMenu(menu);

        getMenuInflater().inflate(R.menu.feeds_menu, menu); //feeds_menu is the feed_menu.xml file

        return true;
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.mnuFree:
                feedUrl = "https://www.thehindu.com/news/national/feeder/default.rss";
                break;
            case R.id.mnuPaid:
                feedUrl = "https://www.thehindu.com/sport/feeder/default.rss";
                break;
            case R.id.mnuMovies:
                feedUrl = "https://www.thehindu.com/news/international/feeder/default.rss";
                break;
            case R.id.mnuRefresh:
                feedCacheUrl = "INVALIDATE";
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        downloadURL(feedUrl);
        return true;
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putString(STATE_URL, feedUrl);
        //outState.putInt(STATE_LIMIT, feedLimit);
        super.onSaveInstanceState(outState);
    }


    private void downloadURL(String feedUrl) {
        if (!feedUrl.equals(feedCacheUrl)) {
            Log.d(TAG, "downloadURL: Starting Asynctask");
            DownloadData d = new DownloadData();
            d.execute(feedUrl);//invokes asychtask class
            feedCacheUrl = feedUrl;
            Log.d(TAG, "downloadURL: done!");
        } else {
            Log.d(TAG, "downloadURL: URL not changed");
        }
    }

    private class DownloadData extends AsyncTask<String, Void, String> {
        //1st param for url (String)
        //2nd param for progress bar(URL)
        //3rd param for type of result we want to get back
        private static final String TAG = "DownloadData";


        @Override
        protected void onPostExecute(String s) { //runs on main UI thread after the
            //background stuff is done
            //string s parameter is the returned string from doInBackground method
            super.onPostExecute(s);
            //Log.d(TAG, "onPostExecute: parameter is " + s);//logd is the shortcut
            ParseApplications parseApplications = new ParseApplications();
            parseApplications.parse(s);

//            ArrayAdapter<FeedEntry> arrayAdapter = new ArrayAdapter<>(
//                    MainActivity.this, R.layout.list_item, parseApplications.getApplications()
//            ); //1st param is context, 2nd param is the resource containing textview that arrayadapter will use to put the data into
//            //3rd param is list of objects to display
//            listApps.setAdapter(arrayAdapter);

            FeedAdapter feedAdapter = new FeedAdapter(MainActivity.this, R.layout.list_record
                    , parseApplications.getApplications());
            listApps.setAdapter(feedAdapter);
        }

        @Override
        protected String doInBackground(String... strings) { //parameter means we can pass any number of string type
            //thread that actually does stuff in the background
            //Log.d(TAG, "doInBackground: starts with " + strings[0]);
            String rssFeed = downloadXML(strings[0]);
            if (strings[0] == null) {
                Log.e(TAG, "doInBackground: Error Downdloading");
            }
            return rssFeed; //returned to onPostExecute
        }

        private String downloadXML(String urlPath) {
            StringBuilder xmlResult = new StringBuilder();

            try {
                URL url = new URL(urlPath);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                int response = connection.getResponseCode();
                Log.d(TAG, "downloadXML: The response code was " + response);
                //InputStream inputStream=connection.getInputStream(); //ctrl+ /  to comment multiple lines
                //InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                //BufferedReader reader =  new BufferedReader(inputStreamReader);
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));

                int charsRead;
                char[] inpbuffer = new char[500];
                while (true) {
                    charsRead = reader.read(inpbuffer);
                    if (charsRead < 0) {
                        break;
                    }
                    if (charsRead > 0) {
                        xmlResult.append(String.copyValueOf(inpbuffer, 0, charsRead));
                    }
                }
                reader.close();

                return xmlResult.toString();
            } catch (MalformedURLException e) {
                Log.e(TAG, "downloadXML: invalid URL " + e.getMessage());
            } catch (IOException e) {
                Log.e(TAG, "downloadXML: IOexception reading data " + e.getMessage());
            } catch (SecurityException e) {
                Log.e(TAG, "downloadXML: Security Exception. needs perm? " + e.getMessage());
                // e.printStackTrace();
                //to give internet perm go to manifest.xml and type <uses and leave it to autocorrect
            }
            return null;
        }

    }

}
