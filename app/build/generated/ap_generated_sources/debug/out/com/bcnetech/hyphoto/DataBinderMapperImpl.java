package com.bcnetech.hyphoto;

import android.databinding.DataBinderMapper;
import android.databinding.DataBindingComponent;
import android.databinding.ViewDataBinding;
import android.util.SparseArray;
import android.util.SparseIntArray;
import android.view.View;
import com.bcnetech.hyphoto.databinding.AblumNewLayoutBindingImpl;
import com.bcnetech.hyphoto.databinding.ActivityAi360BindingImpl;
import com.bcnetech.hyphoto.databinding.ActivityAipreviewBindingImpl;
import com.bcnetech.hyphoto.databinding.ActivityCameraLayoutBindingImpl;
import com.bcnetech.hyphoto.databinding.ActivityCamerasettingBindingImpl;
import com.bcnetech.hyphoto.databinding.ActivityCutBindingImpl;
import com.bcnetech.hyphoto.databinding.ActivityGoodsMarketBindingImpl;
import com.bcnetech.hyphoto.databinding.ActivityImageSelectBindingImpl;
import com.bcnetech.hyphoto.databinding.ActivityWatermarksettingBindingImpl;
import com.bcnetech.hyphoto.databinding.AihintLayoutBindingImpl;
import com.bcnetech.hyphoto.databinding.BlueToothListC2ViewBindingImpl;
import com.bcnetech.hyphoto.databinding.BlueToothListNewPopBindingImpl;
import com.bcnetech.hyphoto.databinding.CameraSettingNewLayoutBindingImpl;
import com.bcnetech.hyphoto.databinding.CameraWelcomeLayoutBindingImpl;
import com.bcnetech.hyphoto.databinding.CamerasettingItemLayoutBindingImpl;
import com.bcnetech.hyphoto.databinding.CardDialogLayoutBindingImpl;
import com.bcnetech.hyphoto.databinding.CloudNewTopSelectViewBindingImpl;
import com.bcnetech.hyphoto.databinding.FilterwaitViewBindingImpl;
import com.bcnetech.hyphoto.databinding.MainAblumNewLayoutBindingImpl;
import com.bcnetech.hyphoto.databinding.PanoramaEditLayoutBindingImpl;
import com.bcnetech.hyphoto.databinding.PartParmsLayoutBindingImpl;
import com.bcnetech.hyphoto.databinding.ProgressDialogLayoutBindingImpl;
import com.bcnetech.hyphoto.databinding.RecoderLayoutBindingImpl;
import com.bcnetech.hyphoto.databinding.SurfviewCameraLayoutBindingImpl;
import com.bcnetech.hyphoto.databinding.SurfviewCameraNewLayoutBindingImpl;
import java.lang.IllegalArgumentException;
import java.lang.Integer;
import java.lang.Object;
import java.lang.Override;
import java.lang.RuntimeException;
import java.lang.String;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DataBinderMapperImpl extends DataBinderMapper {
  private static final int LAYOUT_ABLUMNEWLAYOUT = 1;

  private static final int LAYOUT_ACTIVITYAI360 = 2;

  private static final int LAYOUT_ACTIVITYAIPREVIEW = 3;

  private static final int LAYOUT_ACTIVITYCAMERALAYOUT = 4;

  private static final int LAYOUT_ACTIVITYCAMERASETTING = 5;

  private static final int LAYOUT_ACTIVITYCUT = 6;

  private static final int LAYOUT_ACTIVITYGOODSMARKET = 7;

  private static final int LAYOUT_ACTIVITYIMAGESELECT = 8;

  private static final int LAYOUT_ACTIVITYWATERMARKSETTING = 9;

  private static final int LAYOUT_AIHINTLAYOUT = 10;

  private static final int LAYOUT_BLUETOOTHLISTC2VIEW = 11;

  private static final int LAYOUT_BLUETOOTHLISTNEWPOP = 12;

  private static final int LAYOUT_CAMERASETTINGNEWLAYOUT = 13;

  private static final int LAYOUT_CAMERAWELCOMELAYOUT = 14;

  private static final int LAYOUT_CAMERASETTINGITEMLAYOUT = 15;

  private static final int LAYOUT_CARDDIALOGLAYOUT = 16;

  private static final int LAYOUT_CLOUDNEWTOPSELECTVIEW = 17;

  private static final int LAYOUT_FILTERWAITVIEW = 18;

  private static final int LAYOUT_MAINABLUMNEWLAYOUT = 19;

  private static final int LAYOUT_PANORAMAEDITLAYOUT = 20;

  private static final int LAYOUT_PARTPARMSLAYOUT = 21;

  private static final int LAYOUT_PROGRESSDIALOGLAYOUT = 22;

  private static final int LAYOUT_RECODERLAYOUT = 23;

  private static final int LAYOUT_SURFVIEWCAMERALAYOUT = 24;

  private static final int LAYOUT_SURFVIEWCAMERANEWLAYOUT = 25;

  private static final SparseIntArray INTERNAL_LAYOUT_ID_LOOKUP = new SparseIntArray(25);

  static {
    INTERNAL_LAYOUT_ID_LOOKUP.put(com.bcnetech.hyphoto.R.layout.ablum_new_layout, LAYOUT_ABLUMNEWLAYOUT);
    INTERNAL_LAYOUT_ID_LOOKUP.put(com.bcnetech.hyphoto.R.layout.activity_ai360, LAYOUT_ACTIVITYAI360);
    INTERNAL_LAYOUT_ID_LOOKUP.put(com.bcnetech.hyphoto.R.layout.activity_aipreview, LAYOUT_ACTIVITYAIPREVIEW);
    INTERNAL_LAYOUT_ID_LOOKUP.put(com.bcnetech.hyphoto.R.layout.activity_camera_layout, LAYOUT_ACTIVITYCAMERALAYOUT);
    INTERNAL_LAYOUT_ID_LOOKUP.put(com.bcnetech.hyphoto.R.layout.activity_camerasetting, LAYOUT_ACTIVITYCAMERASETTING);
    INTERNAL_LAYOUT_ID_LOOKUP.put(com.bcnetech.hyphoto.R.layout.activity_cut, LAYOUT_ACTIVITYCUT);
    INTERNAL_LAYOUT_ID_LOOKUP.put(com.bcnetech.hyphoto.R.layout.activity_goods_market, LAYOUT_ACTIVITYGOODSMARKET);
    INTERNAL_LAYOUT_ID_LOOKUP.put(com.bcnetech.hyphoto.R.layout.activity_image_select, LAYOUT_ACTIVITYIMAGESELECT);
    INTERNAL_LAYOUT_ID_LOOKUP.put(com.bcnetech.hyphoto.R.layout.activity_watermarksetting, LAYOUT_ACTIVITYWATERMARKSETTING);
    INTERNAL_LAYOUT_ID_LOOKUP.put(com.bcnetech.hyphoto.R.layout.aihint_layout, LAYOUT_AIHINTLAYOUT);
    INTERNAL_LAYOUT_ID_LOOKUP.put(com.bcnetech.hyphoto.R.layout.blue_tooth_list_c2_view, LAYOUT_BLUETOOTHLISTC2VIEW);
    INTERNAL_LAYOUT_ID_LOOKUP.put(com.bcnetech.hyphoto.R.layout.blue_tooth_list_new_pop, LAYOUT_BLUETOOTHLISTNEWPOP);
    INTERNAL_LAYOUT_ID_LOOKUP.put(com.bcnetech.hyphoto.R.layout.camera_setting_new_layout, LAYOUT_CAMERASETTINGNEWLAYOUT);
    INTERNAL_LAYOUT_ID_LOOKUP.put(com.bcnetech.hyphoto.R.layout.camera_welcome_layout, LAYOUT_CAMERAWELCOMELAYOUT);
    INTERNAL_LAYOUT_ID_LOOKUP.put(com.bcnetech.hyphoto.R.layout.camerasetting_item_layout, LAYOUT_CAMERASETTINGITEMLAYOUT);
    INTERNAL_LAYOUT_ID_LOOKUP.put(com.bcnetech.hyphoto.R.layout.card_dialog_layout, LAYOUT_CARDDIALOGLAYOUT);
    INTERNAL_LAYOUT_ID_LOOKUP.put(com.bcnetech.hyphoto.R.layout.cloud_new_top_select_view, LAYOUT_CLOUDNEWTOPSELECTVIEW);
    INTERNAL_LAYOUT_ID_LOOKUP.put(com.bcnetech.hyphoto.R.layout.filterwait_view, LAYOUT_FILTERWAITVIEW);
    INTERNAL_LAYOUT_ID_LOOKUP.put(com.bcnetech.hyphoto.R.layout.main_ablum_new_layout, LAYOUT_MAINABLUMNEWLAYOUT);
    INTERNAL_LAYOUT_ID_LOOKUP.put(com.bcnetech.hyphoto.R.layout.panorama_edit_layout, LAYOUT_PANORAMAEDITLAYOUT);
    INTERNAL_LAYOUT_ID_LOOKUP.put(com.bcnetech.hyphoto.R.layout.part_parms_layout, LAYOUT_PARTPARMSLAYOUT);
    INTERNAL_LAYOUT_ID_LOOKUP.put(com.bcnetech.hyphoto.R.layout.progress_dialog_layout, LAYOUT_PROGRESSDIALOGLAYOUT);
    INTERNAL_LAYOUT_ID_LOOKUP.put(com.bcnetech.hyphoto.R.layout.recoder_layout, LAYOUT_RECODERLAYOUT);
    INTERNAL_LAYOUT_ID_LOOKUP.put(com.bcnetech.hyphoto.R.layout.surfview_camera_layout, LAYOUT_SURFVIEWCAMERALAYOUT);
    INTERNAL_LAYOUT_ID_LOOKUP.put(com.bcnetech.hyphoto.R.layout.surfview_camera_new_layout, LAYOUT_SURFVIEWCAMERANEWLAYOUT);
  }

  @Override
  public ViewDataBinding getDataBinder(DataBindingComponent component, View view, int layoutId) {
    int localizedLayoutId = INTERNAL_LAYOUT_ID_LOOKUP.get(layoutId);
    if(localizedLayoutId > 0) {
      final Object tag = view.getTag();
      if(tag == null) {
        throw new RuntimeException("view must have a tag");
      }
      switch(localizedLayoutId) {
        case  LAYOUT_ABLUMNEWLAYOUT: {
          if ("layout/ablum_new_layout_0".equals(tag)) {
            return new AblumNewLayoutBindingImpl(component, view);
          }
          throw new IllegalArgumentException("The tag for ablum_new_layout is invalid. Received: " + tag);
        }
        case  LAYOUT_ACTIVITYAI360: {
          if ("layout/activity_ai360_0".equals(tag)) {
            return new ActivityAi360BindingImpl(component, view);
          }
          throw new IllegalArgumentException("The tag for activity_ai360 is invalid. Received: " + tag);
        }
        case  LAYOUT_ACTIVITYAIPREVIEW: {
          if ("layout/activity_aipreview_0".equals(tag)) {
            return new ActivityAipreviewBindingImpl(component, view);
          }
          throw new IllegalArgumentException("The tag for activity_aipreview is invalid. Received: " + tag);
        }
        case  LAYOUT_ACTIVITYCAMERALAYOUT: {
          if ("layout/activity_camera_layout_0".equals(tag)) {
            return new ActivityCameraLayoutBindingImpl(component, view);
          }
          throw new IllegalArgumentException("The tag for activity_camera_layout is invalid. Received: " + tag);
        }
        case  LAYOUT_ACTIVITYCAMERASETTING: {
          if ("layout/activity_camerasetting_0".equals(tag)) {
            return new ActivityCamerasettingBindingImpl(component, view);
          }
          throw new IllegalArgumentException("The tag for activity_camerasetting is invalid. Received: " + tag);
        }
        case  LAYOUT_ACTIVITYCUT: {
          if ("layout/activity_cut_0".equals(tag)) {
            return new ActivityCutBindingImpl(component, view);
          }
          throw new IllegalArgumentException("The tag for activity_cut is invalid. Received: " + tag);
        }
        case  LAYOUT_ACTIVITYGOODSMARKET: {
          if ("layout/activity_goods_market_0".equals(tag)) {
            return new ActivityGoodsMarketBindingImpl(component, view);
          }
          throw new IllegalArgumentException("The tag for activity_goods_market is invalid. Received: " + tag);
        }
        case  LAYOUT_ACTIVITYIMAGESELECT: {
          if ("layout/activity_image_select_0".equals(tag)) {
            return new ActivityImageSelectBindingImpl(component, view);
          }
          throw new IllegalArgumentException("The tag for activity_image_select is invalid. Received: " + tag);
        }
        case  LAYOUT_ACTIVITYWATERMARKSETTING: {
          if ("layout/activity_watermarksetting_0".equals(tag)) {
            return new ActivityWatermarksettingBindingImpl(component, view);
          }
          throw new IllegalArgumentException("The tag for activity_watermarksetting is invalid. Received: " + tag);
        }
        case  LAYOUT_AIHINTLAYOUT: {
          if ("layout/aihint_layout_0".equals(tag)) {
            return new AihintLayoutBindingImpl(component, view);
          }
          throw new IllegalArgumentException("The tag for aihint_layout is invalid. Received: " + tag);
        }
        case  LAYOUT_BLUETOOTHLISTC2VIEW: {
          if ("layout/blue_tooth_list_c2_view_0".equals(tag)) {
            return new BlueToothListC2ViewBindingImpl(component, view);
          }
          throw new IllegalArgumentException("The tag for blue_tooth_list_c2_view is invalid. Received: " + tag);
        }
        case  LAYOUT_BLUETOOTHLISTNEWPOP: {
          if ("layout/blue_tooth_list_new_pop_0".equals(tag)) {
            return new BlueToothListNewPopBindingImpl(component, view);
          }
          throw new IllegalArgumentException("The tag for blue_tooth_list_new_pop is invalid. Received: " + tag);
        }
        case  LAYOUT_CAMERASETTINGNEWLAYOUT: {
          if ("layout/camera_setting_new_layout_0".equals(tag)) {
            return new CameraSettingNewLayoutBindingImpl(component, view);
          }
          throw new IllegalArgumentException("The tag for camera_setting_new_layout is invalid. Received: " + tag);
        }
        case  LAYOUT_CAMERAWELCOMELAYOUT: {
          if ("layout/camera_welcome_layout_0".equals(tag)) {
            return new CameraWelcomeLayoutBindingImpl(component, view);
          }
          throw new IllegalArgumentException("The tag for camera_welcome_layout is invalid. Received: " + tag);
        }
        case  LAYOUT_CAMERASETTINGITEMLAYOUT: {
          if ("layout/camerasetting_item_layout_0".equals(tag)) {
            return new CamerasettingItemLayoutBindingImpl(component, view);
          }
          throw new IllegalArgumentException("The tag for camerasetting_item_layout is invalid. Received: " + tag);
        }
        case  LAYOUT_CARDDIALOGLAYOUT: {
          if ("layout/card_dialog_layout_0".equals(tag)) {
            return new CardDialogLayoutBindingImpl(component, view);
          }
          throw new IllegalArgumentException("The tag for card_dialog_layout is invalid. Received: " + tag);
        }
        case  LAYOUT_CLOUDNEWTOPSELECTVIEW: {
          if ("layout/cloud_new_top_select_view_0".equals(tag)) {
            return new CloudNewTopSelectViewBindingImpl(component, view);
          }
          throw new IllegalArgumentException("The tag for cloud_new_top_select_view is invalid. Received: " + tag);
        }
        case  LAYOUT_FILTERWAITVIEW: {
          if ("layout/filterwait_view_0".equals(tag)) {
            return new FilterwaitViewBindingImpl(component, view);
          }
          throw new IllegalArgumentException("The tag for filterwait_view is invalid. Received: " + tag);
        }
        case  LAYOUT_MAINABLUMNEWLAYOUT: {
          if ("layout/main_ablum_new_layout_0".equals(tag)) {
            return new MainAblumNewLayoutBindingImpl(component, view);
          }
          throw new IllegalArgumentException("The tag for main_ablum_new_layout is invalid. Received: " + tag);
        }
        case  LAYOUT_PANORAMAEDITLAYOUT: {
          if ("layout/panorama_edit_layout_0".equals(tag)) {
            return new PanoramaEditLayoutBindingImpl(component, view);
          }
          throw new IllegalArgumentException("The tag for panorama_edit_layout is invalid. Received: " + tag);
        }
        case  LAYOUT_PARTPARMSLAYOUT: {
          if ("layout/part_parms_layout_0".equals(tag)) {
            return new PartParmsLayoutBindingImpl(component, view);
          }
          throw new IllegalArgumentException("The tag for part_parms_layout is invalid. Received: " + tag);
        }
        case  LAYOUT_PROGRESSDIALOGLAYOUT: {
          if ("layout/progress_dialog_layout_0".equals(tag)) {
            return new ProgressDialogLayoutBindingImpl(component, view);
          }
          throw new IllegalArgumentException("The tag for progress_dialog_layout is invalid. Received: " + tag);
        }
        case  LAYOUT_RECODERLAYOUT: {
          if ("layout/recoder_layout_0".equals(tag)) {
            return new RecoderLayoutBindingImpl(component, view);
          }
          throw new IllegalArgumentException("The tag for recoder_layout is invalid. Received: " + tag);
        }
        case  LAYOUT_SURFVIEWCAMERALAYOUT: {
          if ("layout/surfview_camera_layout_0".equals(tag)) {
            return new SurfviewCameraLayoutBindingImpl(component, view);
          }
          throw new IllegalArgumentException("The tag for surfview_camera_layout is invalid. Received: " + tag);
        }
        case  LAYOUT_SURFVIEWCAMERANEWLAYOUT: {
          if ("layout/surfview_camera_new_layout_0".equals(tag)) {
            return new SurfviewCameraNewLayoutBindingImpl(component, view);
          }
          throw new IllegalArgumentException("The tag for surfview_camera_new_layout is invalid. Received: " + tag);
        }
      }
    }
    return null;
  }

  @Override
  public ViewDataBinding getDataBinder(DataBindingComponent component, View[] views, int layoutId) {
    if(views == null || views.length == 0) {
      return null;
    }
    int localizedLayoutId = INTERNAL_LAYOUT_ID_LOOKUP.get(layoutId);
    if(localizedLayoutId > 0) {
      final Object tag = views[0].getTag();
      if(tag == null) {
        throw new RuntimeException("view must have a tag");
      }
      switch(localizedLayoutId) {
      }
    }
    return null;
  }

  @Override
  public int getLayoutId(String tag) {
    if (tag == null) {
      return 0;
    }
    Integer tmpVal = InnerLayoutIdLookup.sKeys.get(tag);
    return tmpVal == null ? 0 : tmpVal;
  }

  @Override
  public String convertBrIdToString(int localId) {
    String tmpVal = InnerBrLookup.sKeys.get(localId);
    return tmpVal;
  }

  @Override
  public List<DataBinderMapper> collectDependencies() {
    ArrayList<DataBinderMapper> result = new ArrayList<DataBinderMapper>(1);
    result.add(new com.android.databinding.library.baseAdapters.DataBinderMapperImpl());
    return result;
  }

  private static class InnerBrLookup {
    static final SparseArray<String> sKeys = new SparseArray<String>(2);

    static {
      sKeys.put(0, "_all");
    }
  }

  private static class InnerLayoutIdLookup {
    static final HashMap<String, Integer> sKeys = new HashMap<String, Integer>(25);

    static {
      sKeys.put("layout/ablum_new_layout_0", com.bcnetech.hyphoto.R.layout.ablum_new_layout);
      sKeys.put("layout/activity_ai360_0", com.bcnetech.hyphoto.R.layout.activity_ai360);
      sKeys.put("layout/activity_aipreview_0", com.bcnetech.hyphoto.R.layout.activity_aipreview);
      sKeys.put("layout/activity_camera_layout_0", com.bcnetech.hyphoto.R.layout.activity_camera_layout);
      sKeys.put("layout/activity_camerasetting_0", com.bcnetech.hyphoto.R.layout.activity_camerasetting);
      sKeys.put("layout/activity_cut_0", com.bcnetech.hyphoto.R.layout.activity_cut);
      sKeys.put("layout/activity_goods_market_0", com.bcnetech.hyphoto.R.layout.activity_goods_market);
      sKeys.put("layout/activity_image_select_0", com.bcnetech.hyphoto.R.layout.activity_image_select);
      sKeys.put("layout/activity_watermarksetting_0", com.bcnetech.hyphoto.R.layout.activity_watermarksetting);
      sKeys.put("layout/aihint_layout_0", com.bcnetech.hyphoto.R.layout.aihint_layout);
      sKeys.put("layout/blue_tooth_list_c2_view_0", com.bcnetech.hyphoto.R.layout.blue_tooth_list_c2_view);
      sKeys.put("layout/blue_tooth_list_new_pop_0", com.bcnetech.hyphoto.R.layout.blue_tooth_list_new_pop);
      sKeys.put("layout/camera_setting_new_layout_0", com.bcnetech.hyphoto.R.layout.camera_setting_new_layout);
      sKeys.put("layout/camera_welcome_layout_0", com.bcnetech.hyphoto.R.layout.camera_welcome_layout);
      sKeys.put("layout/camerasetting_item_layout_0", com.bcnetech.hyphoto.R.layout.camerasetting_item_layout);
      sKeys.put("layout/card_dialog_layout_0", com.bcnetech.hyphoto.R.layout.card_dialog_layout);
      sKeys.put("layout/cloud_new_top_select_view_0", com.bcnetech.hyphoto.R.layout.cloud_new_top_select_view);
      sKeys.put("layout/filterwait_view_0", com.bcnetech.hyphoto.R.layout.filterwait_view);
      sKeys.put("layout/main_ablum_new_layout_0", com.bcnetech.hyphoto.R.layout.main_ablum_new_layout);
      sKeys.put("layout/panorama_edit_layout_0", com.bcnetech.hyphoto.R.layout.panorama_edit_layout);
      sKeys.put("layout/part_parms_layout_0", com.bcnetech.hyphoto.R.layout.part_parms_layout);
      sKeys.put("layout/progress_dialog_layout_0", com.bcnetech.hyphoto.R.layout.progress_dialog_layout);
      sKeys.put("layout/recoder_layout_0", com.bcnetech.hyphoto.R.layout.recoder_layout);
      sKeys.put("layout/surfview_camera_layout_0", com.bcnetech.hyphoto.R.layout.surfview_camera_layout);
      sKeys.put("layout/surfview_camera_new_layout_0", com.bcnetech.hyphoto.R.layout.surfview_camera_new_layout);
    }
  }
}
