package com.sargent.mark.todolist.data;

import android.provider.BaseColumns;

/**
 * Created by mark on 7/4/17.
 */

public class Category {

    public static class TABLE_CATEGORY implements BaseColumns{
        public static final String TABLE_NAME = "categories";

        public static final String COLUMN_CATEGORY_NAME = "categorytype";

    }
}
