package com.lansosdk.videoeditor;

/**
 * VideoEditor进度执行回调;
 *
 * 杭州蓝松科技有限公司
 * www.lansongtech.com
 */
public interface onVideoEditorProgressListener {
	/**
	 * 
	 * @param v
	 * @param percent  正在处理进度的百分比;
	 */
    void onProgress(VideoEditor v, int percent);
}
