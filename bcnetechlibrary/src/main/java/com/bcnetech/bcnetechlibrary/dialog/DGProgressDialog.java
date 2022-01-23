package com.bcnetech.bcnetechlibrary.dialog;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.TextView;

import com.bcnetech.bcnetechlibrary.R;


/**
 * 自定义加载框
 *
 * @author xuan
 */
public class DGProgressDialog extends ProgressDialog {
	private String message;
	private TextView define_progress_msg;


	public DGProgressDialog(Context context) {
		super(context);
		message = context.getResources().getString(R.string.loading);
	}

	public DGProgressDialog(Context context, String message) {
		super(context);
		this.message = message;
	}


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dg_dialog_view_progressdialog1);
		getWindow().getDecorView().setBackground(null);
		WindowManager.LayoutParams lp=getWindow().getAttributes();
		lp.dimAmount=0.0f;
		getWindow().setAttributes(lp);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
		define_progress_msg = (TextView) findViewById(R.id.define_progress_msg);
		define_progress_msg.setText(message);
		setCanceledOnTouchOutside(false);
	}

	public void setTitle(String message) {
		this.message = message;
	}



	/**
	 * 创建一个实例
	 *
	 * @param context
	 * @return
	 */
	public static DGProgressDialog createInstance(Context context) {
		return new DGProgressDialog(context);
	}

}
