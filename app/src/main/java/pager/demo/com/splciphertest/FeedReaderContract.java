package pager.demo.com.splciphertest;

/**
 * Created by user2 on 17-02-2018.
 */

import android.provider.BaseColumns;

public final class FeedReaderContract {
    public FeedReaderContract() {
    }

    /* Inner class that defines the table contents */
    public static abstract class FeedEntry implements BaseColumns {
        public static final String TABLE_NAME = "news";
        public static final String COLUMN_NAME_ENTRY_ID = "news_id";
        public static final String COLUMN_NAME_TITLE = "title";
        public static final String COLUMN_NAME_SUBTITLE = "subtitle";
    }
}