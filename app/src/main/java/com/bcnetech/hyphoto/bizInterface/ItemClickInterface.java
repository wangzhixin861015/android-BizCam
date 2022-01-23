package com.bcnetech.hyphoto.bizInterface;

import android.view.View;

import com.bcnetech.hyphoto.ui.adapter.StickyGridNewAdapter;

/**
 * Created by wenbin on 2016/12/12.
 */

public interface ItemClickInterface {
    void itemClick(View view, int position);

    boolean itemLongClick(View view, int position );

    void headClick(View view, int position, StickyGridNewAdapter.HeaderViewHolder headerViewHolder);

    void refreshUpload(View view, int position, StickyGridNewAdapter.HeaderViewHolder headerViewHolder);
}
