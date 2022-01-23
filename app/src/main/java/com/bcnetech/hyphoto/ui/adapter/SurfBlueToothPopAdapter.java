package com.bcnetech.hyphoto.ui.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bcnetech.hyphoto.R;
import com.bcnetech.bluetoothlibarary.data.BlueToothItemData;

import java.util.List;

/**
 * Created by yhf on 2017/3/3.
 */
public class SurfBlueToothPopAdapter extends EBizBaseAdapter {

    public SurfBlueToothPopAdapter(Activity activity, List listData) {
        super(activity, listData);
    }

    public SurfBlueToothPopAdapter(Activity activity) {
        super(activity);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Item item;
        if (convertView == null) {
            item = new Item();
            convertView = LayoutInflater.from(activity).inflate(R.layout.surf_blue_tooth_item, null);
            item.name = (TextView) convertView.findViewById(R.id.name);
            item.type = (TextView) convertView.findViewById(R.id.type);
            item.line = convertView.findViewById(R.id.line);
            convertView.setTag(item);
        } else {
            item = (Item) convertView.getTag();
        }

        if (position == 0) {
            item.line.setVisibility(View.GONE);
        } else {
            item.line.setVisibility(View.VISIBLE);
        }
        if (listData.size() == 0) {
            return convertView;
        }
        BlueToothItemData blueToothItemData = (BlueToothItemData) listData.get(position);
        item.name.setText(blueToothItemData.getName());

        if (blueToothItemData.getType() == 0) {
            item.name.setTextColor(activity.getResources().getColor(R.color.shape_grey_color));
            item.type.setTextColor(activity.getResources().getColor(R.color.shape_grey_color));
            item.type.setText(activity.getResources().getString(R.string.available));

        } else if (blueToothItemData.getType() == 1) {
            item.name.setTextColor(activity.getResources().getColor(R.color.sing_in_color));
            item.type.setTextColor(activity.getResources().getColor(R.color.sing_in_color));
            item.type.setText(activity.getResources().getString(R.string.connecting));
        } else {
            item.name.setTextColor(activity.getResources().getColor(R.color.sing_in_color));
            item.type.setTextColor(activity.getResources().getColor(R.color.sing_in_color));
            item.type.setText(activity.getResources().getString(R.string.connected));
        }
        return convertView;
    }

    class Item {
        private TextView name;
        private TextView type;
        private View line;
    }
}
