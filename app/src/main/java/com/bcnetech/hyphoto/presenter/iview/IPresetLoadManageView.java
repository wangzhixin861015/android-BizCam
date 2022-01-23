package com.bcnetech.hyphoto.presenter.iview;

import com.bcnetech.bcnetchhttp.bean.response.PresetDownManageData;
import com.bcnetech.hyphoto.sql.dao.PresetParm;

import java.util.List;

/**
 * Created by yhf on 2017/3/27.
 */

public interface IPresetLoadManageView extends BaseIView{

    void getPresetLoadManageSuccess(List<PresetDownManageData> datas);

    void downLoadPresetSuccess(PresetParm presetParm);
}
