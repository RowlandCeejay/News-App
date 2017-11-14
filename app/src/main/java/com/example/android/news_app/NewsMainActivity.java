package com.example.android.news_app;

import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;


public class NewsMainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<News>> {

    LoaderManager loaderManager;
    ConnectivityManager connMgr;

    View loadingIndicator;

    private static final String LOG_TAG = NewsMainActivity.class.getName();

    /** URL for News data from the GUARDIAN NEWS dataset */
    private static final String GUARDIAN_REQUEST_URL = "http://content.guardianapis.com/search?";

    private static final String GUARDIAN_API_KEY ="test";

    /** Adapter for the list of News */
    private NewsAdapter nAdapter;

    /**
     * Constant value for the news loader ID.
     *
     */
    private static final int NEWS_LOADER_ID = 10;

    /** TextView that is displayed when the list is empty */
    private TextView newsEmptyTextView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i ( LOG_TAG, "TEST: News Main Activity onCreate() called" );
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_main);

        // Find a reference to the {@link ListView} in the layout
        ListView newsListView = (ListView) findViewById(R.id.list);

        newsEmptyTextView = (TextView) findViewById(R.id.empty_view);
        newsListView.setEmptyView(newsEmptyTextView);

        // Create a new adapter that takes an empty list of News as input
        nAdapter = new NewsAdapter(this, new ArrayList<News>());

        // Set the adapter on the {@link ListView}
        // so the list can be populated in the user interface
        newsListView.setAdapter(nAdapter);

        // Set an item click listener on the ListView, which sends an intent to a web browser
        // to open a website with more information about the selected News.
        newsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                // Find the current news that was clicked on
                News currentNews = nAdapter.getItem(position);

                // Convert the String URL into a URI object (to pass into the Intent constructor)
                Uri newsUri = null;
                if (currentNews != null) {
                    newsUri = Uri.parse(currentNews.getNewsWebUrl());
                }

                // Create a new intent to view the news URI
                Intent newsWebsiteIntent = new Intent(Intent.ACTION_VIEW, newsUri);

                // Send the intent to launch a new activity
                startActivity(newsWebsiteIntent);
            }
        });

        // Get a reference to the ConnectivityManager to check state of network connectivity
        connMgr = (ConnectivityManager)
                getSystemService( Context.CONNECTIVITY_SERVICE);

        // Get details on the currently active default data network
        NetworkInfo networkInfo = null;
        if (connMgr != null) {
            networkInfo = connMgr.getActiveNetworkInfo();
        }

        // If there is a network connection, fetch data
        if (networkInfo != null && networkInfo.isConnected()) {
            // Get a reference to the LoaderManager, in order to interact with loaders.
            loaderManager = getLoaderManager();

            // Initialize the loader. Pass in the int ID constant defined above and pass in null for
            // the bundle. Pass in this activity for the LoaderCallbacks parameter (which is valid
            // because this activity implements the LoaderCallbacks interface).
            loaderManager.initLoader(NEWS_LOADER_ID, null, this);
        } else {
            // Otherwise, display error
            // First, hide loading indicator so error message will be visible
            View loadingIndicator = findViewById(R.id.loading_indicator);

            loadingIndicator.setVisibility(View.GONE);

            // Update empty state with no connection error message
            newsEmptyTextView.setText(R.string.no_internet_connection);

        }

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent newsSettingsIntent = new Intent(this, NewsSettingsActivity.class);
            startActivity(newsSettingsIntent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<List<News>> onCreateLoader(int i, Bundle bundle) {

        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);

        String useDate = sharedPrefs.getString(
                getString(R.string.settings_use_date_key),
                getString(R.string.settings_use_date_default));


        String searchSection = sharedPrefs.getString(
                getString(R.string.settings_search_section_key),
                getString(R.string.settings_search_section_default));

        String orderBy = sharedPrefs.getString(
                getString(R.string.settings_orderBy_key),
                getString(R.string.settings_orderBy_newest_default)
        );

        Uri baseUri = Uri.parse(GUARDIAN_REQUEST_URL);
        Uri.Builder uriBuilder = baseUri.buildUpon();

        uriBuilder.appendQueryParameter("use-date",useDate);
        uriBuilder.appendQueryParameter("order-by", orderBy);
        uriBuilder.appendQueryParameter("section", searchSection);
        uriBuilder.appendQueryParameter("api-key",GUARDIAN_API_KEY);


        return new NewsLoader(this, uriBuilder.toString());
    }

    @Override
    public void onLoadFinished(Loader<List<News>> loader, List<News> newsList) {

        Log.i ( LOG_TAG, "TEST: onLoadFinished() called..." );
        // Clear the adapter of previous news data
        nAdapter.clear();

        // Hide loading indicator because the data has been loaded
        loadingIndicator= findViewById(R.id.loading_indicator);
        loadingIndicator.setVisibility(View.GONE);

        // If there is a valid list of {@link News}s, then add them to the adapter's
        // data set. This will trigger the ListView to update.
        if (newsList != null && !newsList.isEmpty()) {
            nAdapter.addAll(newsList);
        } else {

            // Set empty state text to display "No news found."
            newsEmptyTextView.setText(R.string.no_news);

        }
    }

    @Override
    public void onLoaderReset(Loader<List<News>> loader) {
        Log.i ( LOG_TAG, "TEST: onLoaderReset() called..." );
        // Loader reset, so we can clear out our existing data.
        nAdapter.clear();
    }

}
