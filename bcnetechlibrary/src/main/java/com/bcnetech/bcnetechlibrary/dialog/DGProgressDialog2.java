package com.bcnetech.bcnetechlibrary.dialog;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.bcnetech.bcnetechlibrary.R;
import com.bcnetech.bcnetechlibrary.view.CustomStatusView;

/**
 * 自定义加载框
 * 
 * @author xuan
 */
public class DGProgressDialog2 extends ProgressDialog {
/*	private String message;
	private TextView define_progress_msg;*/
	private CustomStatusView custom_status;
	private TextView tv_content;
	private ImageView gif_loading;
	public boolean isGif = false;
	public static final int TYPE_LOADING =112;
	public static final int TYPE_SUCCESS =113;
	public static final int TYPE_FAIL =114;
	public static final int TYPE_REGISTER_SUCCESS =115;
	public static final int TYPE_ABLUM_SELECT=116;
	public static final int TYPE_IMAGE_SELECT=117;
	public static final int TYPE_UPLOAD=118;
	private DGProgressListener dgProgressListener;

	public DGProgressDialog2(Context context) {
		super(context);
		//message = context.getResources().getString(R.string.loading);
	}

	public DGProgressDialog2(Context context, String message) {
		super(context);
		//this.message = message;
	}

	public DGProgressDialog2(Context context, DGProgressListener dgProgressListener, boolean isGif) {
		super(context);
		this.isGif = isGif;
		this.dgProgressListener = dgProgressListener;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dg_dialog_view_lib_progressdialog);
		getWindow().getDecorView().setBackground(null);
		WindowManager.LayoutParams lp=getWindow().getAttributes();
		lp.dimAmount=0.0f;
		getWindow().setAttributes(lp);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
		custom_status= (CustomStatusView)findViewById(R.id.custom_status);
		tv_content = (TextView)findViewById(R.id.tv_content);
		gif_loading = (ImageView)findViewById(R.id.gif_loading);
		/*define_progress_msg = (TextView) findViewById(R.id.define_progress_msg);
		define_progress_msg.setText(message);*/

		setIsGif(isGif);


		custom_status.setCustomLister(new CustomStatusView.CustomLister() {
			@Override
			public void onAnimStart() {
				dgProgressListener.onAnim();
			}

			@Override
			public void onSuccessAnimEnd() {
				dgProgressListener.onSuccessAnimed();
			}

			@Override
			public void onFailAnimEnd() {
				dgProgressListener.onFailAnimed();
			}
		});
	}

	private void setIsGif(boolean isGif){
		if (isGif){
			gif_loading.setVisibility(View.VISIBLE);
			custom_status.setVisibility(View.GONE);
		}else{
			gif_loading.setVisibility(View.GONE);
			custom_status.setVisibility(View.VISIBLE);
		}
	}

	public void setTitle(String message) {
		//this.message = message;
	}

	public void setGifBitmap(Bitmap bitmap){
		gif_loading.setImageBitmap(bitmap);
	}

	public void setText(boolean isSuccess,String text){
		if (isSuccess) {
			tv_content.setTextColor(Color.GREEN);
		}else{
			tv_content.setTextColor(Color.RED);
		}
		tv_content.setText(text);
	}

	public void setText(String text,int type){
		switch (type){
			case TYPE_ABLUM_SELECT:
				tv_content.setText(text);
				tv_content.setTextColor(Color.BLACK);
			break;
		}

	}

	public void setType(int type){
		switch (type){
			case TYPE_LOADING:
				custom_status.loadLoading();
				this.setCanceledOnTouchOutside(false);
				break;
			case TYPE_SUCCESS:
				custom_status.loadSuccess();
				this.setCanceledOnTouchOutside(false);
				break;
			case TYPE_REGISTER_SUCCESS:
				InputMethodManager imm = (InputMethodManager) getContext().getSystemService(getContext().INPUT_METHOD_SERVICE);
				imm.toggleSoftInput(InputMethodManager.HIDE_NOT_ALWAYS, 0);
				custom_status.loadSuccess();
				this.setCanceledOnTouchOutside(false);
				break;
			case TYPE_FAIL:
				custom_status.loadFailure();
				this.setCanceledOnTouchOutside(false);
				break;
			case TYPE_ABLUM_SELECT:
				gif_loading.setImageResource(R.drawable.select_hint);
				this.setCanceledOnTouchOutside(true);
				break;
			case TYPE_UPLOAD:
				gif_loading.setImageResource(R.drawable.syn_upload);
				this.setCanceledOnTouchOutside(true);
				break;
		}
	}

	public interface DGProgressListener{
		void onSuccessAnimed();
		void onFailAnimed();
		void onAnim();
	}

	/**
	 * 创建一个实例
	 * 
	 * @param context
	 * @return
	 */
	public static DGProgressDialog2 createInstance(Context context) {
		return new DGProgressDialog2(context);
	}

}
