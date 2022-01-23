package com.lansosdk.videoeditor;

/**
 * 编码器改变的通知
 *
 * 杭州蓝松科技有限公司
 * www.lansongtech.com
 */
public interface onVideoEditorEncodeChangedListener {
	/**
	 *
	 * @param v
	 * @param isSoftencoder 当前修改后, 是否是软件编码;
	 */
    void onChanged(VideoEditor v, boolean isSoftencoder);
}
