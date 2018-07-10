package cn.jsyjst.weather.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import cn.jsyjst.weather.R;

/**
 * Created by 残渊 on 2018/5/23.
 */


/**
 * 自定义气温折线图
 */
public class TempLineView extends View {
    /**
     * x轴集合
     */
    private float mX[] = new float[6];

    /**
     * 白天y轴集合
     */
    private float mYDay[] = new float[6];

    /**
     * 夜间y轴集合
     */
    private float mYNight[] = new float[6];

    /**
     * x,y轴集合数
     */
    private static final int LENGTH = 6;

    /**
     * 白天温度集合
     */
    private int mTempDay[] = new int[6];

    /**
     * 夜间温度集合
     */
    private int mTempNight[] = new int[6];

    /**
     * 控件高
     */
    private int mHeight;

    /**
     * 字体大小
     */
    private float mTextSize;

    /**
     * 圓半径
     */
    private float mRadius;

    /**
     * 圓半径今天
     */
    private float mRadiusToday;

    /**
     * 文字移动位置距离
     */
    private float mTextSpace;

    /**
     * 线的大小
     */
    private float mLineWidth;

    /**
     * 白天折线颜色
     */
    private int mColorDay;

    /**
     * 夜间折线颜色
     */
    private int mColorNight;

    /**
     * 字体颜色
     */
    private int mTextColor;

    /**
     * 屏幕密度
     */
    private float mDensity;

    /**
     * 控件边的空白空间
     */
    private float mSpace;

    public TempLineView(Context context, AttributeSet attrs) {
        super(context, attrs);

        /**
         * 获取 XML layout 中的属性值
         */
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.TempLineView);
        /**
         * 获得的屏幕密度sp单位
         */
        float densityText = getResources().getDisplayMetrics().scaledDensity;
        mTextSize = a.getDimensionPixelSize(R.styleable.TempLineView_textSize,
                (int) (14 * densityText));
        mColorDay = a.getColor(R.styleable.TempLineView_dayColor,
                getResources().getColor(R.color.colorAccent));
        mColorNight = a.getColor(R.styleable.TempLineView_nightColor,
                getResources().getColor(R.color.colorPrimary));
        mTextColor = a.getColor(R.styleable.TempLineView_textColor, Color.WHITE);
        /**
         * 回收 TypedArray,用于后续调用时可复用之。当调用该方法后，不能再操作该变量。
         */
        a.recycle();

        /**
         * 获取屏幕密度dp
         */
        mDensity = getResources().getDisplayMetrics().density;
        mRadius = 3 * mDensity;
        mRadiusToday = 5 * mDensity;
        mSpace = 3 * mDensity;
        mTextSpace = 10 * mDensity;
        mLineWidth = 2 * mDensity;
    }

    public TempLineView(Context context) {
        super(context);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mHeight == 0) {
            /**
             * 设置控件高度，x轴集合
             */
            setHeightX();
        }
        /**
         * 计算y轴集合数值
         */
        countYValues();
        /**
         *  画白天折线图
         */
        drawChart(canvas, mColorDay, mTempDay, mYDay, 0);
        /**
         *  画夜间折线图
         */
        drawChart(canvas, mColorNight, mTempNight, mYNight, 1);
    }

    /**
     * 计算y轴集合数值
     */
    private void countYValues() {
        /**
         * 存放白天最低温度
         */
        int minTempDay = mTempDay[0];
        /**
         * 存放白天最高温度
         */
        int maxTempDay = mTempDay[0];


        for (int t : mTempDay) {
            if (t < minTempDay) {
                minTempDay = t;
            }
            if (t > maxTempDay) {
                maxTempDay = t;
            }
        }
        /**
         * 晚上的情况
         */
        int minTempNight = mTempNight[0];

        int maxTempNight = mTempNight[0];
        for (int t : mTempNight) {
            if (t < minTempNight) {
                minTempNight = t;
            }
            if (t > maxTempNight) {
                maxTempNight = t;
            }
        }

        /**
         *  白天，夜间中的最低温度
         */
        int minTemp = minTempNight < minTempDay ? minTempNight : minTempDay;
        /**
         * 白天，夜间中的最高温度
         */
        int maxTemp = maxTempDay > maxTempNight ? maxTempDay : maxTempNight;

        /**
         *  份数（白天，夜间综合温差）
         */
        float parts = maxTemp - minTemp;
        /**
         * y轴一端到控件一端的距离
         */
        float length = mSpace + mTextSize + mTextSpace + mRadius;
        /**
         *  y轴高度
         */
        float yHeight = mHeight - length * 2;
        /**
         * 当温度都相同时（被除数不能为0）
         */
        if (parts == 0) {
            for (int i = 0; i < LENGTH; i++) {
                mYDay[i] = yHeight / 2 + length;
                mYNight[i] = yHeight / 2 + length;
            }
        } else {
            float partValue = yHeight / parts;
            for (int i = 0; i < LENGTH; i++) {
                mYDay[i] = mHeight - partValue * (mTempDay[i] - minTemp) - length;
                mYNight[i] = mHeight - partValue * (mTempNight[i] - minTemp) - length;
            }
        }
    }

    /**
     * 画折线图
     *
     * @param canvas 画布
     * @param color  画图颜色
     * @param temp   温度集合
     * @param yAxis  y轴集合
     * @param type   折线种类：0，白天；1，夜间
     */
    private void drawChart(Canvas canvas, int color, int temp[], float[] yAxis, int type) {
        /**
         * 线画笔
         */
        Paint linePaint = new Paint();
        /**
         * 抗锯齿
         */
        linePaint.setAntiAlias(true);
        /**
         *  线宽
         */
        linePaint.setStrokeWidth(mLineWidth);
        linePaint.setColor(color);
        linePaint.setStyle(Paint.Style.STROKE);

        /**
         * 点画笔
         */
        Paint pointPaint = new Paint();
        pointPaint.setAntiAlias(true);
        pointPaint.setColor(getResources().getColor(R.color.white));

        /**
         *  字体画笔
         */
        Paint textPaint = new Paint();
        textPaint.setAntiAlias(true);
        textPaint.setColor(mTextColor);
        textPaint.setTextSize(mTextSize);
        /**
         * 文字居中
         */
        textPaint.setTextAlign(Paint.Align.CENTER);
        for (int i = 0; i < LENGTH; i++) {
            /**
             * 画线
             */
            if (i < LENGTH - 1) {

                linePaint.setPathEffect(null);
                canvas.drawLine(mX[i], yAxis[i], mX[i + 1], yAxis[i + 1], linePaint);
            }
            /**
             *  画点，今天的点比较大
             */
            if (i == 0) {
                canvas.drawCircle(mX[i], yAxis[i], mRadiusToday, pointPaint);
            } else {
                canvas.drawCircle(mX[i], yAxis[i], mRadius, pointPaint);
            }
            /**
             * 画字
             */
            drawText(canvas, textPaint, i, temp, yAxis, type);
        }
    }

    /**
     * 绘制文字
     *
     * @param canvas    画布
     * @param textPaint 画笔
     * @param i         索引
     * @param temp      温度集合
     * @param yAxis     y轴集合
     * @param type      折线种类：0，白天；1，夜间
     */
    private void drawText(Canvas canvas, Paint textPaint, int i, int[] temp, float[] yAxis, int type) {
        switch (type) {
            case 0:
                /**
                 *  显示白天气温
                 */

                canvas.drawText(temp[i] + "°", mX[i], yAxis[i] - mRadius - mTextSpace, textPaint);
                break;
            case 1:
                /**
                 * 显示夜间气温
                 */

                canvas.drawText(temp[i] + "°", mX[i], yAxis[i] + mTextSpace + mTextSize, textPaint);
                break;
        }
    }

    /**
     * 设置高度，x轴集合
     */
    private void setHeightX() {
        mHeight = getHeight();
        // 控件宽
        int width = getWidth();
        // 每一份宽
        float w = width / 12;
        mX[0] = w;
        mX[1] = w * 3;
        mX[2] = w * 5;
        mX[3] = w * 7;
        mX[4] = w * 9;
        mX[5] = w * 11;
    }

    /**
     * 设置白天温度
     *
     * @param tempDay 温度数组集合
     */
    public void setTempDay(int[] tempDay) {
        mTempDay = tempDay;
    }

    /**
     * 设置夜间温度
     *
     * @param tempNight 温度数组集合
     */
    public void setTempNight(int[] tempNight) {
        mTempNight = tempNight;
    }

}
