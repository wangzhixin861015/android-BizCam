// Generated by data binding compiler. Do not edit!
package com.bcnetech.hyphoto.databinding;

import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.opengl.GLSurfaceView;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.bcnetech.bcnetechlibrary.view.ListviewWaitView;
import com.bcnetech.hyphoto.R;
import java.lang.Deprecated;
import java.lang.Object;

public abstract class BlueToothListNewPopBinding extends ViewDataBinding {
  @NonNull
  public final RelativeLayout captureCropView;

  @NonNull
  public final ImageView captureScanLine;

  @NonNull
  public final ListviewWaitView empty;

  @NonNull
  public final ImageView ivCoboxQr;

  @NonNull
  public final ImageView ivContent;

  @NonNull
  public final ImageView ivLeft;

  @NonNull
  public final ImageView ivListChoice;

  @NonNull
  public final ImageView ivRight;

  @NonNull
  public final ImageView ivScanChoice;

  @NonNull
  public final ImageView ivTop;

  @NonNull
  public final ListView listView;

  @NonNull
  public final LinearLayout llBlueTooth;

  @NonNull
  public final LinearLayout llChoice;

  @NonNull
  public final LinearLayout llListChoice;

  @NonNull
  public final RelativeLayout llMain;

  @NonNull
  public final RelativeLayout llQrhelp;

  @NonNull
  public final LinearLayout llScanChoice;

  @NonNull
  public final LinearLayout llTop;

  @NonNull
  public final RelativeLayout qrcodeContent;

  @NonNull
  public final RelativeLayout rlBottom;

  @NonNull
  public final RelativeLayout rlBtClose;

  @NonNull
  public final RelativeLayout rlConfirm;

  @NonNull
  public final RelativeLayout rlContent;

  @NonNull
  public final GLSurfaceView surfaceview;

  @NonNull
  public final TextView tvBlueTooth;

  @NonNull
  public final TextView tvBtClose;

  @NonNull
  public final TextView tvCoboxHelp;

  @NonNull
  public final TextView tvDisConnection;

  @NonNull
  public final TextView tvListChoice;

  @NonNull
  public final TextView tvQrcodeHelp;

  @NonNull
  public final TextView tvScanChoice;

  protected BlueToothListNewPopBinding(Object _bindingComponent, View _root, int _localFieldCount,
      RelativeLayout captureCropView, ImageView captureScanLine, ListviewWaitView empty,
      ImageView ivCoboxQr, ImageView ivContent, ImageView ivLeft, ImageView ivListChoice,
      ImageView ivRight, ImageView ivScanChoice, ImageView ivTop, ListView listView,
      LinearLayout llBlueTooth, LinearLayout llChoice, LinearLayout llListChoice,
      RelativeLayout llMain, RelativeLayout llQrhelp, LinearLayout llScanChoice, LinearLayout llTop,
      RelativeLayout qrcodeContent, RelativeLayout rlBottom, RelativeLayout rlBtClose,
      RelativeLayout rlConfirm, RelativeLayout rlContent, GLSurfaceView surfaceview,
      TextView tvBlueTooth, TextView tvBtClose, TextView tvCoboxHelp, TextView tvDisConnection,
      TextView tvListChoice, TextView tvQrcodeHelp, TextView tvScanChoice) {
    super(_bindingComponent, _root, _localFieldCount);
    this.captureCropView = captureCropView;
    this.captureScanLine = captureScanLine;
    this.empty = empty;
    this.ivCoboxQr = ivCoboxQr;
    this.ivContent = ivContent;
    this.ivLeft = ivLeft;
    this.ivListChoice = ivListChoice;
    this.ivRight = ivRight;
    this.ivScanChoice = ivScanChoice;
    this.ivTop = ivTop;
    this.listView = listView;
    this.llBlueTooth = llBlueTooth;
    this.llChoice = llChoice;
    this.llListChoice = llListChoice;
    this.llMain = llMain;
    this.llQrhelp = llQrhelp;
    this.llScanChoice = llScanChoice;
    this.llTop = llTop;
    this.qrcodeContent = qrcodeContent;
    this.rlBottom = rlBottom;
    this.rlBtClose = rlBtClose;
    this.rlConfirm = rlConfirm;
    this.rlContent = rlContent;
    this.surfaceview = surfaceview;
    this.tvBlueTooth = tvBlueTooth;
    this.tvBtClose = tvBtClose;
    this.tvCoboxHelp = tvCoboxHelp;
    this.tvDisConnection = tvDisConnection;
    this.tvListChoice = tvListChoice;
    this.tvQrcodeHelp = tvQrcodeHelp;
    this.tvScanChoice = tvScanChoice;
  }

  @NonNull
  public static BlueToothListNewPopBinding inflate(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup root, boolean attachToRoot) {
    return inflate(inflater, root, attachToRoot, DataBindingUtil.getDefaultComponent());
  }

  /**
   * This method receives DataBindingComponent instance as type Object instead of
   * type DataBindingComponent to avoid causing too many compilation errors if
   * compilation fails for another reason.
   * https://issuetracker.google.com/issues/116541301
   * @Deprecated Use DataBindingUtil.inflate(inflater, R.layout.blue_tooth_list_new_pop, root, attachToRoot, component)
   */
  @NonNull
  @Deprecated
  public static BlueToothListNewPopBinding inflate(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup root, boolean attachToRoot, @Nullable Object component) {
    return ViewDataBinding.<BlueToothListNewPopBinding>inflateInternal(inflater, R.layout.blue_tooth_list_new_pop, root, attachToRoot, component);
  }

  @NonNull
  public static BlueToothListNewPopBinding inflate(@NonNull LayoutInflater inflater) {
    return inflate(inflater, DataBindingUtil.getDefaultComponent());
  }

  /**
   * This method receives DataBindingComponent instance as type Object instead of
   * type DataBindingComponent to avoid causing too many compilation errors if
   * compilation fails for another reason.
   * https://issuetracker.google.com/issues/116541301
   * @Deprecated Use DataBindingUtil.inflate(inflater, R.layout.blue_tooth_list_new_pop, null, false, component)
   */
  @NonNull
  @Deprecated
  public static BlueToothListNewPopBinding inflate(@NonNull LayoutInflater inflater,
      @Nullable Object component) {
    return ViewDataBinding.<BlueToothListNewPopBinding>inflateInternal(inflater, R.layout.blue_tooth_list_new_pop, null, false, component);
  }

  public static BlueToothListNewPopBinding bind(@NonNull View view) {
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
  public static BlueToothListNewPopBinding bind(@NonNull View view, @Nullable Object component) {
    return (BlueToothListNewPopBinding)bind(component, view, R.layout.blue_tooth_list_new_pop);
  }
}