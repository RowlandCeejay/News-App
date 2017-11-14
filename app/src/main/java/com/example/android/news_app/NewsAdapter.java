package com.example.android.news_app;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;


public class NewsAdapter extends ArrayAdapter<News> {

    NewsAdapter(Context context, List<News> newsList){

        super(context, 0, newsList);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {

        // Check if there is an existing list item view (called convertView) that we can reuse,
        // otherwise, if convertView is null, then inflate a new list item layout.
        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.news_list_item, parent, false);
        }
        // Find the news at the given position in the list of news
        News currentNews = getItem(position);

        // Find the TextView with view ID news type
        TextView typeView = (TextView) listItemView.findViewById(R.id.news_type);
        // Display the type of the current news in that TextView
        typeView.setText(currentNews.getNewsType());

        // Find the TextView with view web title
        TextView webTitleView = (TextView) listItemView.findViewById(R.id.web_title);
        // Display the web title of the current news in that TextView
        webTitleView.setText(currentNews.getNewsWebTitle());

        // Find the TextView with view ID section id
        TextView sectionView = (TextView) listItemView.findViewById(R.id.section_id);
        sectionView.setText(currentNews.getNewsSection());

        // Find the TextView with view ID date
        TextView dateView = (TextView) listItemView.findViewById(R.id.date);
        // Display the date of the current news in that TextView
        dateView.setText(formatDate(currentNews.getWebPublicationDate()));

        // Find the TextView with view ID time
        TextView timeView = (TextView) listItemView.findViewById(R.id.time);
        // Display the time of the current news in that TextView
        timeView.setText(formatTime(currentNews.getWebPublicationDate()));

        // Return the list item view that is now showing the appropriate data
        return listItemView;
    }

    /**
     * Return the formatted date string (i.e. "Oct 3, 1984") from a Date object.
     */
    private String formatDate(String publishedDate) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'kk:mm:ss", Locale.ENGLISH);
        Date dateObject = null;

        try {
            dateObject = simpleDateFormat.parse(publishedDate);
        } catch (Exception e) {
            e.printStackTrace();
        }

        simpleDateFormat = new SimpleDateFormat("MMM dd,yyyy",Locale.ENGLISH);

        return simpleDateFormat.format(dateObject);
    }

    /**
     * Return the formatted date string (i.e. "4:30 PM") from a Date object.
     */

    private String formatTime(String publishedTime) {
        SimpleDateFormat timeFormat = new SimpleDateFormat("yyyy-MM-dd'T'kk:mm:ss",Locale.ENGLISH);
        Date dateObject = null;

        try {
            dateObject = timeFormat.parse(publishedTime);
        } catch (Exception e) {
            e.printStackTrace();
        }

        timeFormat = new SimpleDateFormat(" hh:mm a",Locale.ENGLISH);

        return timeFormat.format(dateObject);
    }
}
