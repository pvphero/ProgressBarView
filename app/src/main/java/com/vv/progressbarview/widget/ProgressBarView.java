package com.vv.progressbarview.widget;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.view.animation.LinearInterpolator;

import com.vv.progressbarview.interfaces.ProgressListener;
import com.vv.progressbarview.R;

import java.text.DecimalFormat;

/**
 * Created by ShenZhenWei on 17/8/3.
 * 自定义酷炫进度条
 */

public class ProgressBarView extends View{

    private static final String TAG = "ProgressBarView";
    //进度条相关paint
    private Paint bgPaint;
    private Paint progressPaint;
    //提示相关paint 边
    private Paint tipPaint;
    private Paint tipSidePaint;
    private Paint tipRectPaint;
    private Paint textPaint;

    private int mWidth;
    private int mHeight;
    private int mViewHeight;

    private float mProgress;
    private float currentProgress;
    private ValueAnimator progressAnimator;
    private int duration=1*1000;//持续时间
    private int startDelay=500;
    private int progressPaintWidth;
    private int tipPaintWidth;
    private int tipHeight;
    private int tipWidth;
    private Path path=new Path();//triangle
    private int triangleHeight;
    private int progressMarginTop;
    private float moveDistance;
    private Rect textRect=new Rect();
    private String textString="0";
    private int textPaintSize;
    private RectF rectF=new RectF();
    private RectF rectF2=new RectF();
    private int roundRectRadius;
    private ProgressListener progressListener;
    private Context mContext;
    private int defaultProgressPaintWidth=4;//默认的进度条画笔宽度
    private int defaultTipHeight=15;//默认的提示框的高度
    private int defaultTipWidth=30;//默认的提示框的宽度
    private int defaultTriangleHeight=3;//默认的三角形的高度
    private int defaultRoundRectRadius=2;//默认的标签圆角矩形的圆角半径
    private int defaultTextPaintSize=10;//tip的textSize
    private int defaultProgressMarginTop=8;//默认的进度条离的距离Tip
    private int bgColor=0xFFe1e5e8;//progress背景颜色
    private int progressColor=0xFFf66b12;//progrss颜色
    private int tipColor=0xFFf66b12;//tip的颜色
    private int textColor=Color.WHITE;//text颜色
    public ProgressBarView(Context context) {
        super(context);
    }

    public ProgressBarView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext=context;
        TypedArray attrsArray = context.obtainStyledAttributes(attrs, R.styleable.ProgressBarView);
        getAttr(attrsArray);
        init();
        initPaint();
    }


    private void getAttr(TypedArray typedArray) {

        //获取attrs里的控件的宽高等
        progressPaintWidth = typedArray.getDimensionPixelOffset(R.styleable.ProgressBarView_progressPaintWidth, dp2px(defaultProgressPaintWidth));
        tipHeight = typedArray.getDimensionPixelOffset(R.styleable.ProgressBarView_tipHeight, dp2px(defaultTipHeight));
        tipWidth = typedArray.getDimensionPixelOffset(R.styleable.ProgressBarView_tipWidth, dp2px(defaultTipWidth));
        triangleHeight = typedArray.getDimensionPixelOffset(R.styleable.ProgressBarView_triangleHeight, dp2px(defaultTriangleHeight));
        roundRectRadius = typedArray.getDimensionPixelOffset(R.styleable.ProgressBarView_roundRectRadius, dp2px(defaultRoundRectRadius));
        textPaintSize = typedArray.getDimensionPixelOffset(R.styleable.ProgressBarView_textSize, sp2px(defaultTextPaintSize));
        progressMarginTop = typedArray.getDimensionPixelOffset(R.styleable.ProgressBarView_progressMarginTop, dp2px(defaultProgressMarginTop));

        progressColor = typedArray.getColor(R.styleable.ProgressBarView_progressColor, progressColor);
        tipColor = typedArray.getColor(R.styleable.ProgressBarView_tipColor, tipColor);
        bgColor = typedArray.getColor(R.styleable.ProgressBarView_progressBgColor, bgColor);
        textColor = typedArray.getColor(R.styleable.ProgressBarView_textColor, textColor);

        duration = typedArray.getInteger(R.styleable.ProgressBarView_duration, duration);

        typedArray.recycle();
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int width = MeasureSpec.getSize(widthMeasureSpec);

        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);

        setMeasuredDimension(measureWidth(widthMode, width), measureHeight(heightMode, height));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawLine(getPaddingLeft(),
                tipHeight+progressMarginTop,
                getWidth(),
                tipHeight+progressMarginTop,
                bgPaint);

        canvas.drawLine(getPaddingLeft(),
                tipHeight+progressMarginTop,
                currentProgress,
                tipHeight+progressMarginTop,
                progressPaint);
        drawTipView(canvas);
        drawText(canvas,textString);

    }

    private void init() {

        tipPaintWidth = dp2px(1);

        mViewHeight = tipHeight + tipPaintWidth + triangleHeight + progressPaintWidth + progressMarginTop;

    }

    private void initPaint() {
        bgPaint=getPaint(progressPaintWidth,bgColor,Paint.Style.STROKE);
        progressPaint=getPaint(progressPaintWidth,progressColor, Paint.Style.STROKE);
        tipPaint=getPaint(tipPaintWidth,tipColor, Paint.Style.FILL);
        tipRectPaint=getPaint(tipPaintWidth,progressColor, Paint.Style.STROKE);

        initTextPaint();
    }


    private void initTextPaint() {
        textPaint=new Paint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setTextSize(textPaintSize);
        textPaint.setColor(textColor);
        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setAntiAlias(true);
    }

    private Paint getPaint(int strokeWidth,int color,Paint.Style style){
        Paint paint=new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setStrokeWidth(strokeWidth);
        paint.setColor(color);
        paint.setAntiAlias(true);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setStyle(style);
        return paint;
    }

    private void drawTipView(Canvas canvas){
        drawRoundRect(canvas);
        drawTriangle(canvas);
    }

    /**
     * 圆角矩形
     * @param canvas
     */
    private void drawRoundRect(Canvas canvas){
        rectF.set(moveDistance,0,tipWidth+moveDistance,tipHeight);
        rectF2.set(moveDistance,0,tipWidth+moveDistance,tipHeight);
        canvas.drawRoundRect(rectF,roundRectRadius,roundRectRadius,tipPaint);
        canvas.drawRoundRect(rectF2,roundRectRadius,roundRectRadius,tipRectPaint);
    }

    /**
     * 绘制三角形
     * @param canvas
     */
    private void drawTriangle(Canvas canvas) {
        path.moveTo(tipWidth/2-triangleHeight+moveDistance,tipHeight);
        path.lineTo(tipWidth/2+moveDistance,tipHeight+triangleHeight);
        path.lineTo(tipWidth/2+triangleHeight+moveDistance,tipHeight);
        canvas.drawPath(path,tipRectPaint);
        path.reset();
    }

    private void drawText(Canvas canvas, String textString) {
        textRect.left= (int) moveDistance;
        textRect.top=0;
        textRect.right= (int) (tipWidth+moveDistance);
        textRect.bottom=tipHeight;
        Paint.FontMetricsInt fontMetrics=textPaint.getFontMetricsInt();
        int baseLine=(textRect.bottom+textRect.top-fontMetrics.bottom-fontMetrics.top)/2;

        canvas.drawText(textString+"%",textRect.centerX(),baseLine,textPaint);
    }

    /**
     * dp 2 px
     *
     * @param dpVal
     */
    protected int dp2px(int dpVal) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                dpVal, getResources().getDisplayMetrics());
    }

    /**
     * sp 2 px
     *
     * @param spVal
     * @return
     */
    protected int sp2px(int spVal) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP,
                spVal, getResources().getDisplayMetrics());

    }

    /**
     * 格式化数字(保留一位小数)
     *
     * @param money
     * @return
     */
    public static String formatNum(int money) {
        DecimalFormat format = new DecimalFormat("0");
        return format.format(money);
    }

    public static int format2Int(double i) {
        return (int) i;
    }

    /**
     * 测量宽度
     * @param mode
     * @param width
     * @return
     */
    private int measureWidth(int mode,int width){
        switch (mode){
            case MeasureSpec.UNSPECIFIED:
            case MeasureSpec.AT_MOST:
                break;
            case MeasureSpec.EXACTLY:
                mWidth=width;
                break;
        }
        return mWidth;
    }

    /**
     * 测量高度
     * @param mode
     * @param height
     * @return
     */
    private int measureHeight(int mode,int height){
        switch (mode){
            case MeasureSpec.UNSPECIFIED:
            case MeasureSpec.AT_MOST:
                mHeight=mViewHeight;
                break;
            case MeasureSpec.EXACTLY:
                mHeight=height;
                break;
        }
        return mHeight;
    }

    /**
     * 设置进度条的动画效果
     * @param progress
     * @return
     */
    public ProgressBarView setProgressWithAnimation(float progress){
        mProgress=progress;
        initAnimation();
        return this;
    }

    public ProgressBarView setCurrentProgress(float progress){
        mProgress=progress;
        currentProgress=progress*mWidth/100;
        textString=formatNum(format2Int(progress));
        invalidate();
        return this;
    }

    /**
     * 开启动画
     */
    public void startProgressAnimation(){
        if (progressAnimator!=null && !progressAnimator.isRunning() && !progressAnimator.isStarted()){
            progressAnimator.start();
        }
    }

    /**
     * 暂停动画
     */
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void pauseProgressAnimation() {
        if (progressAnimator != null) {
            progressAnimator.pause();
        }
    }
    /**
     * 恢复动画
     */
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void resumeProgressAnimation() {
        if (progressAnimator != null)
            progressAnimator.resume();
    }

    /**
     * 停止动画
     */
    public void stopProgressAnimation() {
        if (progressAnimator != null) {
            progressAnimator.end();
        }
    }


    private void initAnimation() {
        progressAnimator=ValueAnimator.ofFloat(0,mProgress);
        progressAnimator.setDuration(duration);
        progressAnimator.setStartDelay(startDelay);
        progressAnimator.setInterpolator(new LinearInterpolator());
        progressAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                float value= (float) valueAnimator.getAnimatedValue();
                textString=formatNum(format2Int(value));
                currentProgress=value*mWidth/100;
                if (progressListener!=null){
                    progressListener.currentProgressListener(value);
                }
                if (currentProgress>=(tipWidth/2)&&currentProgress<=(mWidth-tipWidth/2)){
                    moveDistance=currentProgress-tipWidth/2;
                }
                invalidate();
            }
        });
        progressAnimator.start();

    }

    public ProgressListener getProgressListener() {
        return progressListener;
    }

    public void setProgressListener(ProgressListener progressListener) {
        this.progressListener = progressListener;
    }
}
