package com.bcnetech.bcnetechlibrary.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Paint.FontMetricsInt;
import android.graphics.Paint.Style;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class PickerViewV extends View
{

	public static final int LEFT_FLING=-1;
	public static final int RIGHT_FLING=1;

	private boolean canTouch;//支持手动左右滑动view
	private boolean canLoop;//支持循环
	/**
	 * 传进来的·高度值，决定了绘制的字体大小，
	 */

	public static final String TAG = "PickerView";
	/**
	 * text之间间距和minTextSize之比
	 */
	public static final float MARGIN_ALPHA = 9f;

	/**
	 * 自动回滚到中间的速度
	 */
	public static final float SPEED = 3;

	private List<Item> mDataList;
	/**
	 * 选中的位置，这个位置是mDataList的中心位置，一直不变
	 */
	private int mCurrentSelected;
	private Paint mPaint;

	private float mMaxTextSize ;// 最大的字号
	private float mMinTextSize ;// 最小的字号

	private float mMaxTextAlpha = 250;
	private float mMinTextAlpha = 100;
	private int mColorText = 0xffffff;
	private int mViewHeight;
	private int mViewWidth;
	private float mLastDownX;
	private int direction=1;
	private boolean canClick=true;
	/**
	 * 滑动的距离
	 */
	private float mMoveLen = 0;
	private boolean isInit = false;
	private onSelectListener mSelectListener;
	private Timer timer,timerup;
	private MyTimerTask mTask,mTaskup;
	private static final int animTime=5;//动画执行时间

	private float baseline ;
	Handler updateHandler = new Handler()
	{

		@Override
		public void handleMessage(Message msg)
		{
			
			if (Math.abs(mMoveLen) < SPEED)
			{
				mMoveLen = 0;
				if (mTask != null)
				{
					mTask.cancel();
					mTask = null;
					canClick=true;
					performSelect();//每次选择完成后,对接口方法里要传递的数据进行设置
				}
			} else
				// 这里mMoveLen / Math.abs(mMoveLen)是为了保有mMoveLen的正负号，以实现上滚或下滚
				mMoveLen = mMoveLen - mMoveLen / Math.abs(mMoveLen) * SPEED;
			invalidate();
		}
	};
	Handler updateHandlerUp = new Handler()
	{

		@Override
		public void handleMessage(Message msg)
		{

			if((direction<0&&mMoveLen>0)||(direction>0&&mMoveLen<0)){
				if (mTaskup != null)
				{
					mTaskup.cancel();
					mTaskup = null;
					mTask = new MyTimerTask(updateHandler);
					timer.schedule(mTask, 0, animTime);
				}
			}
			else{
				mMoveLen += (direction*SPEED*3);
				System.out.println("second:"+mMoveLen);
				if (mMoveLen > MARGIN_ALPHA * mMinTextSize / 2)
				{
					// 往下滑超过离开距离
					moveTailToHead();
					mMoveLen = mMoveLen - MARGIN_ALPHA * mMinTextSize;
				} else if (mMoveLen < -MARGIN_ALPHA * mMinTextSize / 2)
				{
					// 往上滑超过离开距离
					moveHeadToTail();
					mMoveLen = mMoveLen + MARGIN_ALPHA * mMinTextSize;
				}
			}

			invalidate();
		}
	};


	public void startNext(int direction){
		if(!canLoop) {
			if (mCurrentSelected == mDataList.size() - 1 && direction == LEFT_FLING) {
				return;
			}
			if (mCurrentSelected == 0 && direction == RIGHT_FLING) {
				return;
			}
		}
		this.direction=direction;
		performSelect(direction);
		mMoveLen=0;
		if(canClick) {
			canClick=false;

			mTaskup = new MyTimerTask(updateHandlerUp);
			timerup.schedule(mTaskup, 0, animTime);
		}
	}

	public PickerViewV(Context context)
	{
		super(context);
		init();
	}

	public PickerViewV(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		init();
	}
	
	/**
	 * 暴露一个方法，让外界可以获得选中的值
	 * @param listener
	 */
	public void setOnSelectListener(onSelectListener listener)
	{
		mSelectListener = listener;
	}

	private void performSelect()
	{
		if (mSelectListener != null)
			mSelectListener.onSelect(mDataList.get(mCurrentSelected));
	}
	public Item getCurrentItem(){
		return mDataList.get(mCurrentSelected);
	}

	private void performSelect(int direction){
		int current=mCurrentSelected;
		if(direction<0){
			current++;
		}
		else{
			current--;
		}
		if(current>=mDataList.size()){
			current=0;
		}
		else if(current<0){
			current=mDataList.size()-1;
		}
		/*if (mSelectListener != null)
			mSelectListener.onSelect(mDataList.get(current));*/
	}
	public void setData(List<Item> datas) {
		if(datas==null) datas=new ArrayList<>();
		for(int i=0;i<datas.size();i++){
			datas.get(i).setIndex(i);
		}
		mDataList = datas;
		mCurrentSelected = datas.size() / 2;
		invalidate();
	}

	/**
	 * 是否可以直接滑动
	 * @param b
     */
	public void setCanTouch(boolean b){
		this.canTouch=b;
	}

	public void setSelected(int selected)
	{
		mCurrentSelected = selected;
	}

	private void moveHeadToTail()
	{

		if(canLoop) {
			Item head = mDataList.get(0);
			mDataList.remove(0);
			mDataList.add(head);
		}
		else{
			mCurrentSelected += 1;
			if (mCurrentSelected > mDataList.size() - 1) {
				mCurrentSelected = mDataList.size() - 1;
			}
		}
	}

	private void moveTailToHead()
	{

		if(canLoop){
			Item tail = mDataList.get(mDataList.size() - 1);
			mDataList.remove(mDataList.size() - 1);
			mDataList.add(0, tail);
		}
		else{
			mCurrentSelected-=1;
			if(mCurrentSelected<0){
				mCurrentSelected=0;
			}
		}

	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
	{
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		mViewHeight = getMeasuredHeight();
		mViewWidth = getMeasuredWidth();
		// 按照View的高度计算字体大小
		mMaxTextSize = mViewHeight / 2.0f;
		mMinTextSize = mMaxTextSize / 2.0f;
		isInit = true;
		invalidate();
	}

	private void init()
	{
		canLoop=false;
		canTouch=false;
		timer = new Timer();
		timerup=new Timer();
		mDataList = new ArrayList<Item>();
		mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		mPaint.setStyle(Style.FILL);
		mPaint.setTextAlign(Align.CENTER);
		mPaint.setColor(mColorText);
	}

	@Override
	protected void onDraw(Canvas canvas)
	{
		super.onDraw(canvas);
		// 根据index绘制view
		if (isInit)
			drawData(canvas);
	}

	private void drawData(Canvas canvas)
	{
		// 先绘制选中的text再往上往下绘制其余的text
		float scale = parabola(mViewWidth / 2.0f, mMoveLen);
		float size = (mMaxTextSize - mMinTextSize) * scale + mMinTextSize;
		// 设置字体的大小
		mPaint.setTextSize(size);
		// 设置字体的清晰度
		mPaint.setAlpha((int) ((mMaxTextAlpha - mMinTextAlpha) * scale + mMinTextAlpha));// 设置字体的清晰度
		// text居中绘制，注意baseline的计算才能达到居中，y值是text中心坐标
		float y = (float) (mViewHeight / 2.0);
		float x = (float) (mViewWidth / 2.0 + mMoveLen);
		FontMetricsInt fmi = mPaint.getFontMetricsInt();
	    baseline = (float) (y - (fmi.bottom / 2.0 + fmi.top / 2.0));
		
		canvas.drawText(mDataList.get(mCurrentSelected).getName(), x, baseline, mPaint);
		for (int i = 1; (mCurrentSelected - i) >= 0; i++)
		{	//System.out.println(i);
			//System.out.println("上");
			drawOtherText(canvas, i, -1);
		}
		// 绘制下方data
		for (int i = 1; (mCurrentSelected + i) < mDataList.size(); i++)
		{
			//System.out.println("下");
			drawOtherText(canvas, i, 1);
		}

	}

	/**
	 * @param canvas
	 * @param position
	 *            距离mCurrentSelected的差值
	 * @param type
	 *            1表示向下绘制，-1表示向上绘制
	 */
	private void drawOtherText(Canvas canvas, int position, int type)
	{
		//scale都为0
		float d = (float) (MARGIN_ALPHA * mMinTextSize * position + type
				* mMoveLen);
		float scale = parabola(mViewWidth / 2.0f, d);
		float size = (mMaxTextSize - mMinTextSize) * scale + mMinTextSize;
		mPaint.setTextSize(size);
		mPaint.setAlpha((int) ((mMaxTextAlpha - mMinTextAlpha) * scale ));
		float x = (float) (mViewWidth / 2.0 + type * d);
		//float y = (float)(mViewHeight / 2.0);
		FontMetricsInt fmi = mPaint.getFontMetricsInt();
		//System.out.println(fmi.toString());
		//float baseline = (float) (y - (fmi.bottom / 2.0 + fmi.top / 2.0));
		canvas.drawText(mDataList.get(mCurrentSelected + type * position).getName(),
				 x,baseline , mPaint);
	}

	/**
	 * 抛物线
	 * 
	 * @param zero
	 *            零点坐标
	 * @param x
	 *            偏移量
	 * @return scale
	 */
	private float parabola(float zero, float x)
	{
		float f = (float) (1 - Math.pow(x / zero, 2));
		return f < 0 ? 0 : f;
	}



	@Override
	public boolean onTouchEvent(MotionEvent event)
	{
		if(canTouch) {
			switch (event.getActionMasked()) {
				case MotionEvent.ACTION_DOWN:
					doDown(event);
					break;
				case MotionEvent.ACTION_MOVE:
					doMove(event);
					break;
				case MotionEvent.ACTION_UP:
					doUp(event);
					break;
			}
		}
		return true;
	}

	private void doDown(MotionEvent event)
	{
		if (mTask != null)
		{
			mTask.cancel();
			mTask = null;
		}
		mLastDownX = event.getX();
	}

	private void doMove(MotionEvent event)
	{
		//System.out.println("first:"+mMoveLen);
		mMoveLen += (event.getX() - mLastDownX);
		if(mCurrentSelected==mDataList.size()-1&&mMoveLen<-10){
			mMoveLen=-10;
		}
		if(mCurrentSelected==0&&mMoveLen>10){
			mMoveLen=10;
		}
		if (mMoveLen > MARGIN_ALPHA * mMinTextSize / 2)
		{

			if(mCurrentSelected!=0) {
				// 往下滑超过离开距离
				moveTailToHead();
				mMoveLen = mMoveLen - MARGIN_ALPHA * mMinTextSize;
			}
			else{
				mMoveLen=0;
			}
		} else if (mMoveLen < -MARGIN_ALPHA * mMinTextSize / 2)
		{
			if(mCurrentSelected!=mDataList.size()-1){
				// 往上滑超过离开距离
				moveHeadToTail();
				mMoveLen = mMoveLen + MARGIN_ALPHA * mMinTextSize;
			}
			else{
				mMoveLen=0;
			}

		}

		mLastDownX = event.getX();
		invalidate();
	}

	private void doUp(MotionEvent event)
	{
		// 抬起手后mCurrentSelected的位置由当前位置move到中间选中位置
		if (Math.abs(mMoveLen) < 0.0001)
		{
			mMoveLen = 0;
			return;
		}
		if (mTask != null)
		{
			mTask.cancel();
			mTask = null;
		}
		mTask = new MyTimerTask(updateHandler);
		timer.schedule(mTask, 0, 10);
	}

	class MyTimerTask extends TimerTask
	{
		Handler handler;

		public MyTimerTask(Handler handler)
		{
			this.handler = handler;
		}

		@Override
		public void run()
		{
			handler.sendMessage(handler.obtainMessage());
		}

	}

	public interface onSelectListener
	{
		void onSelect(Item text);
	}

	public static class Item{
		private String name;
		private int index;

		public Item(){}
		public Item(String name){
			this.name=name;
		}
		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public int getIndex() {
			return index;
		}

		public void setIndex(int index) {
			this.index = index;
		}
	}
}
