// Generated by data binding compiler. Do not edit!
package com.bcnetech.hyphoto.databinding;

import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.opengl.GLSurfaceView;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import com.bcnetech.bcnetechlibrary.view.LineImageView2;
import com.bcnetech.bcnetechlibrary.view.SublineImageView;
import com.bcnetech.hyphoto.R;
import com.bcnetech.hyphoto.ui.view.BlueToothListNewView;
import com.bcnetech.hyphoto.ui.view.CameraBottomView;
import com.bcnetech.hyphoto.ui.view.CameraParamAdjustView;
import com.bcnetech.hyphoto.ui.view.CameraSettingView;
import com.bcnetech.hyphoto.ui.view.CameraWelcomeView;
import com.bcnetech.hyphoto.ui.view.CoBoxChooseTopView;
import com.bcnetech.hyphoto.ui.view.DottedLineView;
import com.bcnetech.hyphoto.ui.view.FocusView;
import com.bcnetech.hyphoto.ui.view.PresetBottomView;
import com.bcnetech.hyphoto.ui.view.verticalscrollview.VerticalScaleNewView;
import java.lang.Deprecated;
import java.lang.Object;

public abstract class SurfviewCameraNewLayoutBinding extends ViewDataBinding {
  @NonNull
  public final BlueToothListNewView blueToothListView;

  @NonNull
  public final ConstraintLayout bottomControl;

  @NonNull
  public final CameraBottomView cameraBottomView;

  @NonNull
  public final ImageView cameraCloseBtn;

  @NonNull
  public final ConstraintLayout cameraLayout;

  @NonNull
  public final CameraParamAdjustView cameraParamAdjustView;

  @NonNull
  public final ImageView cameraSettingBtn;

  @NonNull
  public final CameraSettingView cameraSettingView;

  @NonNull
  public final CameraWelcomeView cameraWelcomeView;

  @NonNull
  public final ConstraintLayout clAdjust;

  @NonNull
  public final ConstraintLayout clMcontent;

  @NonNull
  public final CoBoxChooseTopView coBoxChooseTopView;

  @NonNull
  public final DottedLineView dottedLine;

  @NonNull
  public final ImageView fakeViewBottom;

  @NonNull
  public final ImageView fakeViewTop;

  @NonNull
  public final FocusView focusview;

  @NonNull
  public final SublineImageView ivSubline;

  @NonNull
  public final RecyclerView lightratioList;

  @NonNull
  public final LineImageView2 lineImageView;

  @NonNull
  public final PresetBottomView presetBottomView;

  @NonNull
  public final GLSurfaceView rectView;

  @NonNull
  public final VerticalScaleNewView scaleView;

  @NonNull
  public final GLSurfaceView surfaceview;

  @NonNull
  public final ConstraintLayout titleLayout;

  @NonNull
  public final ConstraintLayout topLayout;

  protected SurfviewCameraNewLayoutBinding(Object _bindingComponent, View _root,
      int _localFieldCount, BlueToothListNewView blueToothListView, ConstraintLayout bottomControl,
      CameraBottomView cameraBottomView, ImageView cameraCloseBtn, ConstraintLayout cameraLayout,
      CameraParamAdjustView cameraParamAdjustView, ImageView cameraSettingBtn,
      CameraSettingView cameraSettingView, CameraWelcomeView cameraWelcomeView,
      ConstraintLayout clAdjust, ConstraintLayout clMcontent, CoBoxChooseTopView coBoxChooseTopView,
      DottedLineView dottedLine, ImageView fakeViewBottom, ImageView fakeViewTop,
      FocusView focusview, SublineImageView ivSubline, RecyclerView lightratioList,
      LineImageView2 lineImageView, PresetBottomView presetBottomView, GLSurfaceView rectView,
      VerticalScaleNewView scaleView, GLSurfaceView surfaceview, ConstraintLayout titleLayout,
      ConstraintLayout topLayout) {
    super(_bindingComponent, _root, _localFieldCount);
    this.blueToothListView = blueToothListView;
    this.bottomControl = bottomControl;
    this.cameraBottomView = cameraBottomView;
    this.cameraCloseBtn = cameraCloseBtn;
    this.cameraLayout = cameraLayout;
    this.cameraParamAdjustView = cameraParamAdjustView;
    this.cameraSettingBtn = cameraSettingBtn;
    this.cameraSettingView = cameraSettingView;
    this.cameraWelcomeView = cameraWelcomeView;
    this.clAdjust = clAdjust;
    this.clMcontent = clMcontent;
    this.coBoxChooseTopView = coBoxChooseTopView;
    this.dottedLine = dottedLine;
    this.fakeViewBottom = fakeViewBottom;
    this.fakeViewTop = fakeViewTop;
    this.focusview = focusview;
    this.ivSubline = ivSubline;
    this.lightratioList = lightratioList;
    this.lineImageView = lineImageView;
    this.presetBottomView = presetBottomView;
    this.rectView = rectView;
    this.scaleView = scaleView;
    this.surfaceview = surfaceview;
    this.titleLayout = titleLayout;
    this.topLayout = topLayout;
  }

  @NonNull
  public static SurfviewCameraNewLayoutBinding inflate(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup root, boolean attachToRoot) {
    return inflate(inflater, root, attachToRoot, DataBindingUtil.getDefaultComponent());
  }

  /**
   * This method receives DataBindingComponent instance as type Object instead of
   * type DataBindingComponent to avoid causing too many compilation errors if
   * compilation fails for another reason.
   * https://issuetracker.google.com/issues/116541301
   * @Deprecated Use DataBindingUtil.inflate(inflater, R.layout.surfview_camera_new_layout, root, attachToRoot, component)
   */
  @NonNull
  @Deprecated
  public static SurfviewCameraNewLayoutBinding inflate(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup root, boolean attachToRoot, @Nullable Object component) {
    return ViewDataBinding.<SurfviewCameraNewLayoutBinding>inflateInternal(inflater, R.layout.surfview_camera_new_layout, root, attachToRoot, component);
  }

  @NonNull
  public static SurfviewCameraNewLayoutBinding inflate(@NonNull LayoutInflater inflater) {
    return inflate(inflater, DataBindingUtil.getDefaultComponent());
  }

  /**
   * This method receives DataBindingComponent instance as type Object instead of
   * type DataBindingComponent to avoid causing too many compilation errors if
   * compilation fails for another reason.
   * https://issuetracker.google.com/issues/116541301
   * @Deprecated Use DataBindingUtil.inflate(inflater, R.layout.surfview_camera_new_layout, null, false, component)
   */
  @NonNull
  @Deprecated
  public static SurfviewCameraNewLayoutBinding inflate(@NonNull LayoutInflater inflater,
      @Nullable Object component) {
    return ViewDataBinding.<SurfviewCameraNewLayoutBinding>inflateInternal(inflater, R.layout.surfview_camera_new_layout, null, false, component);
  }

  public static SurfviewCameraNewLayoutBinding bind(@NonNull View view) {
    return bind(view, DataBindingUtil.getDefaultComponent());
  }

  /**
   * This method receives DataBindingComponent instance as type Object instead of
   * type DataBindingComponent to avoid causing too many compilation errors if
   * compilation fails for another reason.
   * https://issuetracker.google.com/issues/116541301
   * @Deprecated Use DataBindingUtil.bind(view, component)
   */
  @Deprecated
  public static SurfviewCameraNewLayoutBinding bind(@NonNull View view,
      @Nullable Object component) {
    return (SurfviewCameraNewLayoutBinding)bind(component, view, R.layout.surfview_camera_new_layout);
  }
}
