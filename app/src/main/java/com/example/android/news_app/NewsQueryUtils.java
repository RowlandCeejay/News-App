package com.example.android.news_app;

import android.text.TextUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;


/**
 * Helper methods related to requesting and receiving news data from GUARDIAN NEWS WEBSITE.
 */
final class NewsQueryUtils {

    /** Tag for the log messages */
    private static final String LOG_TAG = NewsQueryUtils.class.getSimpleName();


    private NewsQueryUtils() {
    }

    /**
     * Query the GUARDIAN NEWS WEBSITE and return a list of {@link News} objects.
     */
    static List<News> fetchNewsData(String requestUrl) {
        Log.i ( LOG_TAG, "TEST: fetchNewsData() called..." );

        try {
            Thread.sleep(2000 /* milliseconds */);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Create URL object
        URL url = createUrl(requestUrl);

        // Perform HTTP request to the URL and receive a JSON response back
        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem making the HTTP request.", e);
        }

        // Return the list of {@link News}s
        return extractFeatureFromJson(jsonResponse);
    }

    private static List<News> extractFeatureFromJson(String newsJSON) {
        // If the JSON string is empty or null, then return early.
        if (TextUtils.isEmpty(newsJSON)) {
            return null;
        }

        // Create an empty ArrayList that we can start adding news to
        List<News> newsList = new ArrayList<>();

        // Try to parse the JSON response string. If there's a problem with the way the JSON
        // is formatted, a JSONException exception object will be thrown.
        // Catch the exception so the app doesn't crash, and print the error message to the logs.
        try {

            // Create a JSONObject from the JSON response string
            JSONObject baseJsonResponse = new JSONObject(newsJSON);

            JSONObject jsonResults = baseJsonResponse.getJSONObject("response");

            // Extract the JSONArray associated with the key called "results",
            // which represents a list of results .
            if (jsonResults.has("results")) {
                JSONArray resultsArray = jsonResults.getJSONArray("results");
                for (int i = 0; i < resultsArray.length(); i++) {
                    // Get a single news article at position i within the list of news
                    JSONObject results = resultsArray.getJSONObject ( i );
                    // For any given news, extract the JSONObject associated with the
                    // key called "results", which represents a list of all the results
                    // for that news.
                    // Extract the value for the key called "type"
                    String type = results.getString("type");

                    // Extract the value for the key called "sectionId"
                    String sectionId  = results.getString("sectionId");

                    // Extract the value for the key called "webPublicationDate"
                    String webPublicationDate  = results.getString("webPublicationDate");

                    // Extract the value for the key called "webTitle"
                    String webTitle = results.getString("webTitle");

                    // Extract the value for the key called "webUrl"
                    String webUrl = results.getString("webUrl");

                    // Create a new {@link News} object with the type, section, time, webTitle,
                    // and url from the JSON response.
                    News news = new News(type, sectionId, webPublicationDate, webTitle, webUrl);

                    // Add the new {@link News} to the list of newsList.
                    newsList.add(news);

                }
            }

            else
                Log.d(LOG_TAG, "extractFeatureFromJson: HAS NO RESULTS");

        } catch (JSONException e) {
            // If an error is thrown when executing any of the above statements in the "try" block,
            // catch the exception here, so the app doesn't crash. Print a log message
            // with the message from the exception.
            Log.e("QueryUtils", "Problem parsing the news JSON results", e);
        }

        // Return the list of news
        return newsList;
    }

    /**
     * Returns new URL object from the given string URL.
     */
    private static URL createUrl(String stringUrl) {
        URL url = null;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Problem building the URL ", e);
        }
        return url;
    }

    /**
     * Make an HTTP request to the given URL and return a String as the response.
     */
    private static String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = "";

        // If the URL is null, then return early.
        if (url == null) {
            return jsonResponse;
        }

        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(10000 /* milliseconds */);
            urlConnection.setConnectTimeout(15000 /* milliseconds */);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // If the request was successful (response code 200),
            // then read the input stream and parse the response.
            if (urlConnection.getResponseCode() == 200) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                Log.e(LOG_TAG, "Error response code: " + urlConnection.getResponseCode());
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem retrieving the earthquake JSON results.", e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                // Closing the input stream could throw an IOException, which is why
                // the makeHttpRequest(URL url) method signature specifies than an IOException
                // could be thrown.
                inputStream.close();
            }
        }
        return jsonResponse;
    }

    /**
     * Convert the {@link InputStream} into a String which contains the
     * whole JSON response from the server.
     */
    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null) {
                output.append(line);
                line = reader.readLine();
            }
        }
        return output.toString();
    }


}