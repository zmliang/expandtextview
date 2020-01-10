package com.way.expand;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.DecelerateInterpolator;
import android.widget.TextView;

import androidx.core.widget.TextViewCompat;

/**
 * @author : zmliang
 * @package: com.xiao.nicevieoplayer.example
 * @date on: 2020/1/10 13:52
 * @company: qvbian
 **/
@SuppressLint("AppCompatCustomView")
public class AlignTextView extends TextView {

    private float textSize;
    private float textLineHeight;
    //顶部
    private int top;
    //y轴
    private int y;
    //线
    private int lines;
    //底部
    private int bottom;
    //右边
    private int right;
    //左边
    private int left;
    //线字
    private int lineDrawWords;
    private char[] textCharArray;
    private float singleWordWidth;
    //每个字符的空隙
    private float lineSpacingExtra;

    int maxLine;

    private boolean isFirst = true;

    public AlignTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                initTextInfo();
                return true;
            }
        });
    }

    public AlignTextView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AlignTextView(Context context) {
        this(context, null, 0);
    }


//    @Override
//    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//        int widthMode = MeasureSpec.getMode(widthMeasureSpec);   //获取宽的模式
//        int heightMode = MeasureSpec.getMode(heightMeasureSpec); //获取高的模式
//        int widthSize = MeasureSpec.getSize(widthMeasureSpec);   //获取宽的尺寸
//        int heightSize = MeasureSpec.getSize(heightMeasureSpec); //获取高的尺寸
//        int width;
//        int height;
//        if (widthMode == MeasureSpec.EXACTLY) {
//            //如果match_parent或者具体的值，直接赋值
//            width = widthSize;
//        } else {
//            //如果是wrap_content，我们要得到控件需要多大的尺寸
////            if (isOneLines) {
////                //控件的宽度就是文本的宽度加上两边的内边距。内边距就是padding值，在构造方法执行完就被赋值
////                width = (int) (getPaddingLeft() + textWidth + getPaddingRight());
////            } else {
////                //如果是多行，说明控件宽度应该填充父窗体
////                width = widthSize;
////            }
//            width = widthSize;
////            Log.v("openxu", "文本的宽度:" + textWidth + "控件的宽度：" + width);
//        }
//        //高度跟宽度处理方式一样
//        if (heightMode == MeasureSpec.EXACTLY) {
//            height = heightSize;
//        } else {
//            float textHeight = .height();
//            if (isOneLines) {
//                height = (int) (getPaddingTop() + textHeight + getPaddingBottom());
//            } else {
//                //如果是多行
//                height = (int) (getPaddingTop() + textHeight * showLines + getPaddingBottom());
//            }
//
//            Log.v("openxu", "文本的高度:" + textHeight + "控件的高度：" + height);
//        }
//        //保存测量宽度和测量高度
//        setMeasuredDimension(width, height);
//    }

    public void initTextInfo() {
        textSize = getTextSize();
        //获取线的高度
        textLineHeight = getLineHeight();
        left = 0;
        right = getRight();
        y = getTop();
        // 要画的宽度
        int drawTotalWidth = right - left;
        String text = getText().toString();
        if (!TextUtils.isEmpty(text) && isFirst) {
            textCharArray = ToDBC(text).toCharArray();
            TextPaint mTextPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
            mTextPaint.density = getResources().getDisplayMetrics().density;
            mTextPaint.setTextSize(textSize);
            // 获取单个单词的的宽度
            singleWordWidth = mTextPaint.measureText("一") + lineSpacingExtra;
            // 每行可以放多少个字符
            lineDrawWords = (int) (drawTotalWidth / singleWordWidth);
            int length = textCharArray.length;
            lines = length / lineDrawWords;
            if ((length % lineDrawWords) > 0) {
                lines = lines + 1;
            }
            drawTotalLine = lines;
            isFirst = false;
            ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) getLayoutParams();
            int totalHeight = (int) (showLines*textLineHeight+textLineHeight*2 + getPaddingBottom()+getPaddingTop()+layoutParams.bottomMargin+layoutParams.topMargin);
            setHeight(totalHeight);
        }
    }

    private int showLines = 11;
    private int drawTotalLine;



    private boolean loadCompleted = false;

    public boolean isLoadCompleted(){
        return loadCompleted;
    }
    public void showNextContent(int nextShowLines){
        if (loadCompleted){
            return;
        }
        showLines+=nextShowLines;
        if (showLines>drawTotalLine){
            loadCompleted = true;
            showLines = drawTotalLine;
            if (textStatusChangedListener!=null){
                textStatusChangedListener.onTextLoadCompleted();
            }

        }

        final ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) getLayoutParams();
        final int oldHeight = getMeasuredHeight();
        int totalHeight = (int) (showLines*textLineHeight+textLineHeight*2 + getPaddingBottom()+getPaddingTop()+layoutParams.bottomMargin+layoutParams.topMargin);
        final int from = totalHeight-oldHeight;
        final ValueAnimator animator = ValueAnimator.ofInt(from,totalHeight);
        animator.setDuration(100);
        animator.setInterpolator(new DecelerateInterpolator());
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                final int value = Integer.valueOf(animation.getAnimatedValue().toString());
                Log.e("zmliang","value:"+value);
                //layoutParams.height = value;
                //setLayoutParams(layoutParams);
                setHeight(Integer.valueOf(animation.getAnimatedValue().toString()));
            }

        });
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationCancel(Animator animation) {
                super.onAnimationCancel(animation);
                if (textStatusChangedListener!=null){
                    if (loadCompleted) {
                        textStatusChangedListener.onTextLoadCompleted();
                    } else {
                        textStatusChangedListener.onTextLoadEnd();
                    }
                }
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                if (textStatusChangedListener!=null) {
                    if (loadCompleted) {
                        textStatusChangedListener.onTextLoadCompleted();
                    } else {
                        textStatusChangedListener.onTextLoadEnd();
                    }
                }
            }

            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
                if (textStatusChangedListener!=null){
                    textStatusChangedListener.onTextLoadStart();
                }
            }
        });
        animator.start();
//        setHeight(totalHeight);
//        invalidate();
    }





    @Override
    protected void onDraw(Canvas canvas) {
        bottom = getBottom();
    //    drawTotalLine = lines;

        if(maxLine!=0&&drawTotalLine>maxLine){
            drawTotalLine = maxLine;
        }

        for (int i = 0; (i < drawTotalLine); i++) {
            try {
                int length = textCharArray.length;
                int mLeft = left;
                // 第i+1行开始的字符index
                int startIndex = (i * 1) * lineDrawWords;
                // 第i+1行结束的字符index
                int endTextIndex = startIndex + lineDrawWords;
                if (endTextIndex > length) {
                    endTextIndex = length;
                    y += textLineHeight;
                } else {
                    y += textLineHeight;
                }
                for (; startIndex < endTextIndex; startIndex++) {
                    char c = textCharArray[startIndex];
                    canvas.drawText(String.valueOf(c), mLeft, y, getPaint());
                    mLeft += singleWordWidth;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }



    @Override
    public void setMaxLines(int max){
        this.maxLine = max;
    }

    public void setLineSpacingExtra(int lineSpacingExtra){
        this.lineSpacingExtra = lineSpacingExtra;
    }

    /**
     * 判断是否为中文
     * @return
     */
    public static boolean containChinese(String string){
        boolean flag = false;
        for (int i = 0; i < string.length(); i++) {
            char c = string.charAt(i);
            if ((c >= 0x4e00) && (c <= 0x9FA5)) {
                flag = true;
            }
        }
        return flag;
    }

    public static String ToDBC(String input) {
        // 导致TextView异常换行的原因：安卓默认数字、字母不能为第一行以后每行的开头字符，因为数字、字母为半角字符
        // 所以我们只需要将半角字符转换为全角字符即可
        char c[] = input.toCharArray();
        for (int i = 0; i < c.length; i++) {
            if (c[i] == ' ') {
                c[i] = '\u3000';
            } else if (c[i] < '\177') {
                c[i] = (char) (c[i] + 65248);
            }
        }
        return new String(c);
    }

    private OnTextStatusChangedListener textStatusChangedListener;


    public void setOnTextStatusChangedListener(OnTextStatusChangedListener listener){
        this.textStatusChangedListener = listener;
    }


    public interface OnTextStatusChangedListener{

        void onTextLoadEnd();

        void onTextLoadStart();

        void onTextLoadCompleted();
    }

}
