package com.bcnetech.hyphoto.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bcnetech.bcnetechlibrary.view.NumberSeekBar;
import com.bcnetech.hyphoto.imageinterface.BizImageMangage;
import com.bcnetech.hyphoto.sql.dao.PictureProcessingData;
import com.bcnetech.hyphoto.R;

import java.util.List;

/**
 * Created by a1234 on 16/9/12.
 */
public class ImageParmsAdapter extends RecyclerView.Adapter<ImageParmsAdapter.ViewHolder> {

    private Context context;
    private LayoutInflater inflater;
    private List<PictureProcessingData> list;

    public ImageParmsAdapter(Context context, List list) {
        this.context = context;
        this.list = list;
        this.inflater = LayoutInflater.from(context);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private View view;
        private TextView tv_name;
        private NumberSeekBar sb_name;

        public ViewHolder(View view) {
            super(view);
            this.view = view;
            this.tv_name = (TextView) view.findViewById(R.id.tv_name);
            this.sb_name = (NumberSeekBar) view.findViewById(R.id.sb_name);
        }
    }

    public void setData(List list) {
        this.list = list;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = this.inflater.inflate(R.layout.image_parms_item, null);
        return new ViewHolder(view);
    }

    /**
     * 设置值
     * <p>
     * private int type;//图片处理类型
     * private String imageUrl;
     * private int num;//参数处理值
     */
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        PictureProcessingData data = list.get(position);
        int type = data.getType();
        int num = data.getNum();
        String mtype = setTypeAndNums(type, num);
        int x = num;
        double s = (double) x / 2;
        viewHolder.sb_name.setMyProgress(s);
        viewHolder.tv_name.setText(mtype + "");
    }

    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();
    }

    /**
     * 根据type来确定类型
     *
     * @param type
     * @param num
     */
    private String setTypeAndNums(int type, int num) {
        String text = "";
        switch (type) {
            case BizImageMangage.BRIGHTNESS:
                text = context.getResources().getString(R.string.brightness);
                break;
            case BizImageMangage.CONTRAST:
                text = context.getResources().getString(R.string.contrast);
                break;
            case BizImageMangage.DEFINITION:
                text = context.getResources().getString(R.string.defintion);
                break;
            case BizImageMangage.HIGHLIGHT:
                text = context.getResources().getString(R.string.high_light);
                break;
            case BizImageMangage.NATURALSATURATION:
                text = context.getResources().getString(R.string.nature_saturation);
                break;
            case BizImageMangage.SATURATION:
                text = context.getResources().getString(R.string.saturation);
                break;
            case BizImageMangage.SHADOW:
                text = context.getResources().getString(R.string.shadow);
                break;
            case BizImageMangage.WARMANDCOOLCOLORS:
                text =context.getResources().getString(R.string.clod_hot);
                break;
            case BizImageMangage.SHARPEN:
                text = context.getResources().getString(R.string.sharpen);
                break;
            case BizImageMangage.EXPOSURE:
                text = context.getResources().getString(R.string.exposire);
                break;
        }
        return text;
    }

}

