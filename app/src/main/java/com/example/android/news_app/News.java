package com.example.android.news_app;


class News {

    private String nNewsTYpe;

    private String nNewsWebTitle;

    private String nNewsSection;

    private String nWebPublicationDate;

    private String nNewsWebUrl;


    /**
     * Constructs a new {@link News} object.
     * @param newsType is the type of the news
     * @param newsWebTitle is the web title of the news
     * @param newsSection is the section where the news is displayed
     * @param webPublicationDate is the time in milliseconds (from the Epoch) when the
     *                           news was published
     * @param newsWebUrl is the website URL to find more details about the news
     */
    News(String newsType, String newsSection, String webPublicationDate, String newsWebTitle, String newsWebUrl) {

        nNewsTYpe = newsType;
        nNewsSection = newsSection;
        nWebPublicationDate = webPublicationDate;
        nNewsWebTitle = newsWebTitle;
        nNewsWebUrl = newsWebUrl;
    }

    /**
     * Returns the newstype of the news.
     */
    String getNewsType() {
        return nNewsTYpe;
    }

    /**
     * Returns the section of the news.
     */
    String getNewsSection() {
        return nNewsSection;
    }

    /**
     * Returns the time and date of the published news.
     */
    String getWebPublicationDate() {
        return nWebPublicationDate;
    }

    /**
     * Returns the web title of the news.
     */
    String getNewsWebTitle() {
        return nNewsWebTitle;
    }

    /**
     * Returns the website URL to find more information about the news.
     */
    String getNewsWebUrl() {return nNewsWebUrl;
    }
}

