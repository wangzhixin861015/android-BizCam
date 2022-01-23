package com.bcnetech.hyphoto.ui.view.swipemenu.view;

import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bcnetech.hyphoto.ui.view.swipemenu.bean.SwipeMenu;
import com.bcnetech.hyphoto.ui.view.swipemenu.bean.SwipeMenuItem;

import java.util.List;


public class SwipeMenuView extends LinearLayout implements View.OnClickListener {

    private SwipeMenuLayout mLayout;
    private SwipeMenu mMenu;
    private OnMenuItemClickListener mOnMenuItemClickListener;
    private int position;

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public SwipeMenuView(SwipeMenu menu) {
        super(menu.getContext());
        setOrientation(LinearLayout.HORIZONTAL);
        mMenu = menu;
        List<SwipeMenuItem> items = mMenu.getMenuItems();
        int id = 0;
        for (SwipeMenuItem item : items) {
            addItem(item, id++);
        }
    }

    private void addItem(SwipeMenuItem item, int id) {
        LayoutParams params = new LayoutParams(item.getWidth(),
                LayoutParams.MATCH_PARENT);
        LinearLayout parent = new LinearLayout(getContext());
        params.rightMargin=item.getMargin_right();
        params.bottomMargin=item.getMargin_bottom();
        parent.setId(id);
        parent.setGravity(Gravity.CENTER);
        parent.setOrientation(LinearLayout.VERTICAL);
        parent.setLayoutParams(params);
        parent.setBackgroundDrawable(item.getBackground());
        parent.setOnClickListener(this);
        addView(parent);

        if (item.getIcon() != null) {
            parent.addView(createIcon(item));
        }
        if (!TextUtils.isEmpty(item.getTitle())) {
            parent.addView(createTitle(item));
        }

    }

    private ImageView createIcon(SwipeMenuItem item) {
        ImageView iv = new ImageView(getContext());
        iv.setImageDrawable(item.getIcon());
        iv.setLayoutParams(new LayoutParams(item.getIconWidth(),item.getIconHeight()));
        return iv;
    }

    private TextView createTitle(SwipeMenuItem item) {
        TextView tv = new TextView(getContext());
        tv.setText(item.getTitle());
        tv.setGravity(Gravity.CENTER);
        tv.setTextSize(item.getTitleSize());
        tv.setTextColor(item.getTitleColor());
        LayoutParams layoutParams=new LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
        layoutParams.topMargin=item.getText_topMargin();
        tv.setLayoutParams(layoutParams);
        return tv;
    }

    @Override
    public void onClick(View v) {
        if (mOnMenuItemClickListener != null && mLayout.isOpen()) {
            mOnMenuItemClickListener.onMenuItemClick(position, mMenu, v.getId(),mLayout);
        }
    }

    public interface OnMenuItemClickListener {
        void onMenuItemClick(int position, SwipeMenu menu, int index,SwipeMenuLayout layout);
    }

    public void setOnMenuItemClickListener(
            OnMenuItemClickListener mOnMenuItemClickListener) {
        this.mOnMenuItemClickListener = mOnMenuItemClickListener;
    }

    public void setLayout(SwipeMenuLayout mLayout) {
        this.mLayout = mLayout;
    }


}
