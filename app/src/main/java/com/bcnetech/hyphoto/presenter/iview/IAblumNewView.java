package com.bcnetech.hyphoto.presenter.iview;

import com.bcnetech.hyphoto.data.GridItem;
import com.bcnetech.hyphoto.data.UploadBean;
import com.bcnetech.hyphoto.sql.dao.ImageData;

import java.util.List;

/**
 * Created by yhf on 2017/10/16.
 */

public interface IAblumNewView extends BaseIView {


    void setGridViewFirstVisItem(int firstVisItem);


    void initAnim(float x, float y);

    void inFooterSetting(int type);

    void outFooterSetting();

    void showBigImage(int position,GridItem gridItem);

    void setFooterType(int type);

    ImageData getResultData();

    void updataImageUtils(List<GridItem> list);
    void  showBackground(List<GridItem> mGirdList);

    //选择动画
    void inTopSelect();
    void outTopSelect();

    /**
     *
     * @param canClick
     */
    void setTopSelectCanClick(boolean canClick);

    /**
     * 分享按钮能否点击
     * @param canClick
     */
    void setShareCanClick(boolean canClick);

    void refreshBigImage(GridItem gridItem,int position,String type);

    int getPosition();


    void refreshGridView(UploadBean uploadBean);

    void setPanelClick (boolean isPanelClick);

    void setApplyBlur(boolean isBlur);

    void onConnectBlueTooth(boolean isConnect,String deviceName,boolean isAI360);

    void setBlueTouchType(int blueTouchType);


}
