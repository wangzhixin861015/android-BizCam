package com.bcnetech.hyphoto.ui.adapter;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bcnetech.hyphoto.R;

import java.util.ArrayList;

/**
 * Created by a1234 on 17/1/9.
 */

public class GridViewRationAdapter extends BaseAdapter {
        private LayoutInflater mInflater;
        ArrayList<Name> name;
        private Context mContext;
        LinearLayout.LayoutParams params;

        public GridViewRationAdapter(Context context, ArrayList<Name> name) {
            this.name = name;
            mContext = context;
            mInflater = LayoutInflater.from(context);

            params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            params.gravity = Gravity.CENTER;
        }

        public int getCount() {
            return name.size();
        }

        public Object getItem(int position) {
            return name.get(position);
        }

        public long getItemId(int position) {
            return position;
        }

        @Override
        public boolean isEnabled(int position) {
            return false;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
           ItemViewTag viewTag;

            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.la_layout, null, true);
                viewTag = new ItemViewTag((TextView) convertView.findViewById(R.id.ration_type), (TextView) convertView.findViewById(R.id.ration_num));
                convertView.setTag(viewTag);
            } else {
                viewTag = (ItemViewTag) convertView.getTag();
            }
            name.get(position);

            // set name
            viewTag.mName.setText(name.get(position).getNum() + "");
            // set icon
            viewTag.mIcon.setText(name.get(position).getName());

            return convertView;
        }

    public static class ItemViewTag {
            TextView mIcon;
            TextView mName;

            public ItemViewTag(TextView type, TextView name) {
                this.mName = name;
                this.mIcon = type;
            }
        }

   public static class Name {
        String name;
        String num;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

       public String getNum() {
           return num;
       }

       public void setNum(String num) {
           this.num = num;
       }
   }
}