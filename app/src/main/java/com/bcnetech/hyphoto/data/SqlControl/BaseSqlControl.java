package com.bcnetech.hyphoto.data.SqlControl;

import android.app.Activity;

/**
 * Created by wenbin on 16/11/3.
 */

public class BaseSqlControl {


    protected static final int QUERY_TOKEN = 0;
    protected static final int INSERT_TOKEN = 1;
    protected static final int UPDATE_TOKEN = 2;
    protected static final int DELETE_TOKEN = 3;
    protected static final int DELETE_TOKEN_ALL = 4;
    protected static final int SEARCH_TOKEN = 5;
    public static final int QUERY_COUNT=6;
    public Activity activity;
    public SqlControlListener listener;

    public BaseSqlControl(Activity activity){
        this.activity=activity;

    }


    public SqlControlListener getListener() {
        return listener;
    }

    public void setListener(SqlControlListener listener) {
        this.listener = listener;
    }

    public interface SqlControlListener{
        void queryListener(Object...parms);
        void insertListener(Object...parms);
        void deletListener(Object...parms);
        void upDataListener(Object...parms);
    }
}
