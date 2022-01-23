package com.bcnetech.bcnetechlibrary.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ImageView;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by wenbin on 16/9/21.
 */

public class ImageEditView extends ImageView{

    private Bitmap bitmap;
    private Bitmap bgBitmap;
    private Paint paint;

    private int width,height,bitWidth,bitHight,srcX,srcY;
    private double viewFlag;
    private Rect canveRect,bitmapRect;


    private float rectx,recty,rectWidth,rectHight;
    private float contentPaintRelX,contentPaintRelY;//两手指中点相对图片位置
    private double startLen,endLen;//两手指间距离

    private boolean startZoom=false;//开始放大缩小
    private final static double MOVE_SPEED=0.2;//动画移动速度
    private final static double DEVEL=6;//限制大小
    private MyTimerTask mTask;
    private Timer timer;
    private int count=0;//点击次数
    private long firClick,secClick;//点击事件
    private int paddingbottom=0;

    private EditListener editListener;

    private int dw;
    private int dh;


    private boolean canZoom=true;//判断是否可以手势
    public ImageEditView(Context context) {
        super(context);
        initData();
    }

    public ImageEditView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initData();
    }

    public ImageEditView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initData();
    }
    private void initData(){
        timer = new Timer();
        paint=new Paint();
        canveRect=new Rect();
        bitmapRect=new Rect();
    }


    public void setBitmap(Bitmap bitmap,Bitmap bgBitmap){
        this.bitmap=bitmap;
        if(bgBitmap!=null) {
            this.bgBitmap = bgBitmap;
        }
        double flag=bitmap.getWidth()*1.0/bitmap.getHeight();
        bitmapRect.set(0,0,bitmap.getWidth(),bitmap.getHeight());
        if(viewFlag>flag){
            bitWidth=(int)(height*flag);
            bitHight=height;
            srcX=(width-bitWidth)/2;
            srcY=0;
        }
        else if(viewFlag==flag){
            bitWidth=width;
            bitHight=height;
            srcX=0;
            srcY=0;
        }
        else{
            bitWidth=width;
            bitHight=(int)(width/flag);
            srcX=0;
            srcY=(height-bitHight)/2;
        }
        canveRect.set(srcX,srcY,srcX+bitWidth,srcY+bitHight);
        invalidate();

    }

    public void setPaddingBottom(int dp){
        paddingbottom=dp;
    }
    public void resetBitmap(Bitmap bitmap){
        if(this.bitmap==null){
            setBitmap(bitmap,null);
            return;
        }
        if(bitmap.getWidth()==this.bitmap.getWidth()&&bitmap.getHeight()==this.bitmap.getHeight()){
            this.bitmap=bitmap;
        }
        invalidate();
    }
    /**
     * 获取原始尺寸
     * @return
     */
    public float[] getSize(){
        float[] size=new float[]{srcX,srcY,bitWidth,bitHight};
        return size;
    }

    public void setZoom(boolean canZoom){
        this.canZoom=canZoom;
        if(!canZoom){
            restartBitmap();
        }

    }

    private void restartBitmap(){
        rectx=srcX;
        recty=srcY;
        rectWidth=bitWidth;
        rectHight=bitHight;
        startAnim();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        height = MeasureSpec.getSize(heightMeasureSpec);
        width = MeasureSpec.getSize(widthMeasureSpec);

        setMeasuredDimension(width, height);
        height-=paddingbottom;
        viewFlag=width*1.0/height;


    }

    @Override
    protected void onDraw(Canvas canvas) {
        //设置背景图片
        if(bgBitmap!=null){
            canvas.drawBitmap(bgBitmap, 0, 0, paint);
        }

        if(bitmap!=null) {
            canvas.drawBitmap(bitmap, bitmapRect, canveRect, paint);

            dw = bitmap.getWidth();
            dh = bitmap.getHeight();
            ImageEditView.this.post(new Runnable() {
                @Override
                public void run() {
                    //获得ImageView中Image的变换矩阵
                    Matrix m = ImageEditView.this.getImageMatrix();
                    float[] values = new float[10];
                    m.getValues(values);

                    //Image在绘制过程中的变换矩阵，从中获得x和y方向的缩放系数
                    float sx = values[0];
                    float sy = values[4];

                    //计算Image在屏幕上实际绘制的宽高
                    int cw = (int) (dw * sx);
                    int ch = (int) (dh * sy);
                    if (editListener != null) {
                        editListener.onSize(dw, dh);
                        editListener.onRealSize(cw, ch);
                    }
                }
            });

        }
    }



    @Override
    public boolean onTouchEvent(MotionEvent event) {

        if(!canZoom){
            return super.onTouchEvent(event);
        }
        DoubleClickListener(event);
        ZoomMoveListener(event);

        return true;
    }


    /**
     * 双击事件
     * @param event
     */
    private void DoubleClickListener(MotionEvent event){
        int action = event.getAction() & MotionEvent.ACTION_MASK;
        int nCnt = event.getPointerCount();
        if(nCnt>1){
            count = 0;
            firClick = 0;
            secClick = 0;
        }else if(action == MotionEvent.ACTION_DOWN){
            count++;
            if(count == 1){
                firClick = System.currentTimeMillis();
            }
        }
        else if(action == MotionEvent.ACTION_UP){
            if (count == 2){
                secClick = System.currentTimeMillis();
                if(secClick - firClick < 500){
                    //双击事件
                    DoubleClick(event);
                }
                count = 0;
                firClick = 0;
                secClick = 0;
            }
        }
    }
    /**
     * 双击操作
     */
    private void DoubleClick(MotionEvent event){
        if (canveRect.left==srcX
                &&canveRect.top==srcY
                &&(canveRect.right-canveRect.left)==bitWidth
                &&(canveRect.bottom-canveRect.top)==bitHight){
            float x,y;
            x=event.getX();
            y=event.getY();
            rectx=-x;
            recty=-y;
            rectWidth=(float) (bitWidth*2);
            rectHight=(float) (bitHight*2);
            verificationXY();
            startAnim();
        }
        else{
            restartBitmap();
        }
    }
//-------------------------------------------------------------------

    /**
     * 两只手指 放大缩小移动
     * @param event
     */
    private void ZoomMoveListener(MotionEvent event){
        int action = event.getAction() & MotionEvent.ACTION_MASK;
        int nCnt = event.getPointerCount();
        //最后一个点抬起或者取消，结束所有模式
        if(action == MotionEvent.ACTION_UP || action == MotionEvent.ACTION_CANCEL) {
            if(startZoom){
                startZoom=false;
                endZoomMove();
            }

        }//多个手指情况下抬起一个手指,此时需要是缩放模式才触发
        else if(action == MotionEvent.ACTION_POINTER_UP) {
            if(nCnt<2&&startZoom){
                startZoom=false;
                endZoomMove();
            }
        } //第一个点按下，开启滚动模式，记录开始滚动的点
        else if (action == MotionEvent.ACTION_DOWN) {

        }//非第一个点按下，关闭滚动模式，开启缩放模式，记录缩放模式的一些初始数据
        else if (action == MotionEvent.ACTION_POINTER_DOWN) {
            if(nCnt==2){
                startZoom=true;
                startZoomMove(event);
            }
        }
        else if (action == MotionEvent.ACTION_MOVE) {
            if(nCnt>=2&&startZoom){
                zoomMove(event);
            }
        }
    }


    /**
     * 开启缩放移动
     * @param event
     */
    private void startZoomMove(MotionEvent event){
        float contentPaintx,contentPainty;
        float startx,starty,startx2,starty2;
        startx=event.getX(0);
        startx2=event.getX(1);
        starty=event.getY(0);
        starty2=event.getY(1);
        startLen=Math.sqrt((startx2-startx)*(startx2-startx)+(starty2-starty)*(starty2-starty));
        rectx=canveRect.left;
        recty=canveRect.top;
        rectWidth= canveRect.right-canveRect.left;
        rectHight= canveRect.bottom-canveRect.top;

        contentPaintx=startx>startx2?(startx-startx2)/2+startx2:(startx2-startx)/2+startx;
        contentPainty=starty>starty2?(starty-starty2)/2+starty2:(starty2-starty)/2+starty;
        contentPaintRelX=(contentPaintx-rectx)/rectWidth;
        contentPaintRelY=(contentPainty-recty)/rectHight;
    }


    /**
     *
     * @param event
     */
    private void zoomMove(MotionEvent event){
        float contentPaintx2,contentPainty2;
        float endx=event.getX(0);
        float endy=event.getY(0);
        float endx2=event.getX(1);
        float endy2=event.getY(1);

        endLen=Math.sqrt((endx2-endx)*(endx2-endx)+(endy2-endy)*(endy2-endy));
        contentPaintx2=endx>endx2?(endx-endx2)/2+endx2:(endx2-endx)/2+endx;
        contentPainty2=endy>endy2?(endy-endy2)/2+endy2:(endy2-endy)/2+endy;
        double proportion=endLen/startLen;
        double endW=rectWidth*proportion;
        double endH=rectHight*proportion;
        if(endW<bitWidth/2){
            endW=bitWidth/2;
            endH=bitHight/2;
        }
        canveRect.set((int)(contentPaintx2-(endW*contentPaintRelX)),
                (int)(contentPainty2-(endH*contentPaintRelY)),
                (int)endW+(int)(contentPaintx2-(endW*contentPaintRelX)),
                (int)endH+(int)(contentPainty2-(endH*contentPaintRelY)));

        invalidate();
    }

    /**
     *
     *
     */
    private void endZoomMove(){
        rectx=canveRect.left;
        recty=canveRect.top;
        rectWidth= canveRect.right-canveRect.left;
        rectHight= canveRect.bottom-canveRect.top;
        verificationXY();
        startAnim();
    }
//------------------------------------------------------------------------------------

    /**
     * 验证坐标
     */
    private void verificationXY(){
        if(rectWidth>=width){
            if(rectx>0){
                rectx=0;
            }
            if(rectx+rectWidth<width){
                rectx=width-rectWidth;

            }
        }else if(rectWidth>=bitWidth){
            if(rectx>(width-rectWidth)/2){
                rectx=(width-rectWidth)/2;
            }
            if(canveRect.right<(width-rectWidth)/2+rectWidth){
                rectx=(width-rectWidth)/2;
            }
        }
        else{
            rectx=(width-bitWidth)/2;
            rectWidth=bitWidth;
        }


        if(rectHight>=height){
            if(recty>0){
                recty=0;
            }
            if(recty+rectHight<height){
                recty=height-rectHight;
            }
        }
        else if(rectHight>=bitHight){
            if(recty>(height-rectHight)/2){
                recty=(height-rectHight)/2;
            }
            if(canveRect.bottom<(height-rectHight)/2+rectHight){
                recty=(height-rectHight)/2;
            }
        }
        else{
            recty=(height-bitHight)/2;
            rectHight=bitHight;
        }
    }

    /**
     * 开始动画(还原或放大)
     */
    private void startAnim(){
        if (mTask != null)
        {
            mTask.cancel();
            mTask = null;
            //  performSelect();//每次选择完成后,对接口方法里要传递的数据进行设置
        }
        mTask = new MyTimerTask(updateHandlerUp);
        timer.schedule(mTask, 0, 10);
        invalidate();
    }

    public interface EditListener {
        void onSize(int width, int height);//原图大小

        void onMaskbit(Bitmap bitmap);//画笔划出的mask图

        void onRealSize(int w, int h);//加载imageview的大小
    }

    public void setEditListener(EditListener editListener) {
        this.editListener = editListener;
    }

    Handler updateHandlerUp = new Handler() {

        @Override
        public void handleMessage(Message msg) {

            if(Math.abs(rectx-canveRect.left)<DEVEL&&Math.abs(recty-canveRect.top)<DEVEL&&
                    Math.abs((rectx+rectWidth)-canveRect.right)<DEVEL&&Math.abs((recty+rectHight)-canveRect.bottom)<DEVEL){
                canveRect.left=(int)rectx;
                canveRect.top=(int)recty;
                canveRect.right=(int)(rectx+rectWidth);
                canveRect.bottom=(int)(recty+rectHight);
                if (mTask != null)
                {
                    mTask.cancel();
                    mTask = null;
                  //  performSelect();//每次选择完成后,对接口方法里要传递的数据进行设置
                }
            }
            else{
                canveRect.left = (int) ((rectx - canveRect.left) * MOVE_SPEED + canveRect.left);
                canveRect.top = (int) ((recty - canveRect.top) * MOVE_SPEED + canveRect.top);
                canveRect.right = (int) (((rectx + rectWidth) - canveRect.right) * MOVE_SPEED + canveRect.right);
                canveRect.bottom = (int) (((recty + rectHight) - canveRect.bottom) * MOVE_SPEED + canveRect.bottom);
            }
            invalidate();
        }
    };
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



}
