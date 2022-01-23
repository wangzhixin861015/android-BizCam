package com.bcnetech.bcnetechlibrary.dialog;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bcnetech.bcnetechlibrary.R;
import com.bcnetech.bcnetechlibrary.util.AnimFactory;
import com.bcnetech.bcnetechlibrary.view.LoadingAnimLibView;

/**
 * 自定义加载框
 * 
 * @author yhf
 */
public class DGProgressDialog3 extends ProgressDialog {
	private String message;
	private TextView define_progress_msg;
	private ValueAnimator valueAnimator;
	private ImageView iv_type;
	private RelativeLayout rl_content;
	private boolean pressBack;
	private LoadingAnimLibView loadingAnimView;
	private boolean showAnimation=true;



	public DGProgressDialog3(Context context, String message) {
		super(context);
		this.message = message;
	}

	public DGProgressDialog3(Context context, boolean pressBack, String message) {
		super(context);
		this.message = message;
		this.pressBack=pressBack;
	}

	public DGProgressDialog3(Context context, boolean pressBack, String message, boolean showAnimation) {
		super(context);
		this.message = message;
		this.pressBack=pressBack;
		this.showAnimation=false;
	}




	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dg_dialog_view_lib_progressdialog3);
		getWindow().getDecorView().setBackground(null);
		WindowManager.LayoutParams lp=getWindow().getAttributes();
		lp.dimAmount=0.0f;
		getWindow().setAttributes(lp);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
		define_progress_msg = (TextView) findViewById(R.id.tv_content);
		define_progress_msg.setText(message);
		rl_content= (RelativeLayout) findViewById(R.id.rl_content);
		iv_type= (ImageView) findViewById(R.id.iv_type);
		setCanceledOnTouchOutside(false);
		loadingAnimView= (LoadingAnimLibView) findViewById(R.id.loading_view);

	}

	public void setTitle(String message) {
		this.message = message;
		define_progress_msg.setText(message);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK&&pressBack) {
			return true;
		}
		return false;
	}


	public void startFloatAnim(AnimFactory.FloatListener floatListener) {
		if (valueAnimator != null && valueAnimator.isRunning()) {
			valueAnimator.cancel();
		}
		valueAnimator = AnimFactory.rotationCricleAnim(floatListener, 500);
		valueAnimator.addListener(new Animator.AnimatorListener() {
			@Override
			public void onAnimationStart(Animator animation) {

			}

			@Override
			public void onAnimationEnd(Animator animation) {
				ValueAnimator valueAnimatorX = AnimFactory.rotationCricleAnim(null, 1500);
					valueAnimatorX.addListener(new Animator.AnimatorListener() {
						@Override
						public void onAnimationStart(Animator animation) {

						}

						@Override
						public void onAnimationEnd(Animator animation) {
							mNetAsyncTask.animationEnd();
						}

						@Override
						public void onAnimationCancel(Animator animation) {

						}

						@Override
						public void onAnimationRepeat(Animator animation) {

						}
					});
					valueAnimatorX.start();
			}

			@Override
			public void onAnimationCancel(Animator animation) {

			}

			@Override
			public void onAnimationRepeat(Animator animation) {

			}
		});
		valueAnimator.start();
	}

	public void setNetAsyncTask(NetAsyncTask netAsyncTask,int code){
		this.mNetAsyncTask=netAsyncTask;
		if(showAnimation){
			if(code==0){
				iv_type.setImageResource(R.drawable.net_timeout);
				define_progress_msg.setText(getContext().getResources().getString(R.string.network_disconn));
			}else {
				iv_type.setImageResource(R.drawable.net_error);
				define_progress_msg.setText(getContext().getResources().getString(R.string.server_error));
			}
			startFloatAnim(new AnimFactory.FloatListener() {
				@Override
				public void floatValueChang(float f) {
					loadingAnimView.setTranslationY(rl_content.getMeasuredHeight() * f);

					iv_type.setTranslationY(rl_content.getMeasuredHeight() * (f-1));
				}
			});
		}else {
			mNetAsyncTask.animationEnd();
		}
	}


	public void setShowListener(OnShowListener onShowListener){
		this.setOnShowListener(onShowListener);
	}

	private NetAsyncTask mNetAsyncTask;

	public interface NetAsyncTask{
		void animationEnd();
	}

	public void showDialog(){
		this.show();
	}

	public void setShowHintText(String hint){
		define_progress_msg.setText(hint);
	}





}
