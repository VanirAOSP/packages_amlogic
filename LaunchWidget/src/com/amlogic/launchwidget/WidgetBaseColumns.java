/*
 * Copyright (C) 2007 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.amlogic.launchwidget;

import android.net.Uri;
import android.provider.BaseColumns;

public final class WidgetBaseColumns {
    public static final String AUTHORITY = "com.google.provider.LaunchWidget";

    private WidgetBaseColumns() {
    }

    /**
     * Widget table contract
     */
    public static final class Columns implements BaseColumns {

        private Columns() {}      

        public static final String TABLE_NAME = "tb_widget";
 
        private static final String SCHEME = "content://"; 

        private static final String PATH_WIDGET = "/launchwidgets";

        private static final String PATH_WIDGET_ID = "/launchwidgets/";

        public static final int NOTE_ID_PATH_POSITION = 1;

        private static final String PATH_LIVE_FOLDER = "/live_folders/launchwidgets";

        public static final Uri CONTENT_URI =  Uri.parse(SCHEME + AUTHORITY + PATH_WIDGET);

        public static final Uri CONTENT_ID_URI_BASE= Uri.parse(SCHEME + AUTHORITY + PATH_WIDGET_ID);

        public static final Uri CONTENT_ID_URI_PATTERN = Uri.parse(SCHEME + AUTHORITY + PATH_WIDGET_ID + "/#");

        public static final Uri LIVE_FOLDER_URI  = Uri.parse(SCHEME + AUTHORITY + PATH_LIVE_FOLDER);

        public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.google.launchwidget";

        public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.google.launchwidget";

        public static final String DEFAULT_SORT_ORDER = "modified DESC";
        public static final String SORT_ORDER_ASC = "modified ASC";
        
        public static final String COLUMN_ACTION_INTENT = "intent";

        public static final String COLUMN_CLASS_NAME = "classname";

        public static final String COLUMN_APP_NAME = "appname";

        public static final String COLUMN_MODIFY = "modified";
        
        public static final String COLUMN_PREVIEW_ICON= "preview_icon";
    }
}
