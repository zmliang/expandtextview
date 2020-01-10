package com.way.expand;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

/**
 * @author : zmliang
 * @package: com.xiao.nicevieoplayer.example
 * @date on: 2020/1/10 15:33
 * @company: qvbian
 **/
public class XExpandTextView extends FrameLayout {

    private AlignTextView alignTextView;
    private View shadowView;
    private TextView bottomTv;

    public XExpandTextView(@NonNull Context context) {
        super(context);
        init(context);
    }


    public XExpandTextView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }


    public XExpandTextView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public XExpandTextView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }




    public void init(Context context){
        final View child = LayoutInflater.from(context).inflate(R.layout.custom_expand_text_view,this,true);
        alignTextView = (AlignTextView) child.findViewById(R.id.custom_align_text_view_1);
        shadowView = child.findViewById(R.id.shadow_view);
        bottomTv = (TextView) child.findViewById(R.id.continue_read);

        alignTextView.setOnTextStatusChangedListener(new AlignTextView.OnTextStatusChangedListener() {
            @Override
            public void onTextLoadEnd() {
                bottomTv.setVisibility(VISIBLE);
                shadowView.setVisibility(VISIBLE);
            }

            @Override
            public void onTextLoadStart() {
                bottomTv.setVisibility(INVISIBLE);
                shadowView.setVisibility(INVISIBLE);
            }

            @Override
            public void onTextLoadCompleted() {
                bottomTv.setVisibility(VISIBLE);
                bottomTv.setText("继续阅读下一章");
                updateArrow();
                shadowView.setVisibility(INVISIBLE);
            }
        });

        bottomTv.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                alignTextView.showNextContent(50);
                if (expandedListener!=null){
                    expandedListener.onExpandedCompleted(alignTextView.isLoadCompleted());
                }
            }
        });
    }


    private void updateArrow(){
        Drawable dwRight;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            dwRight = (getContext().getDrawable(R.mipmap.expand_icon_arrow_right)) ;
        } else {
            dwRight = (getContext().getResources().getDrawable(R.mipmap.expand_icon_arrow_right)) ;
        }
        if (dwRight == null) {
            return;
        }
        dwRight.setBounds(0, 0, dwRight.getMinimumWidth(), dwRight.getMinimumHeight());
        bottomTv.setCompoundDrawables(null, null, dwRight, null);
    }

    private OnTextExpandedListener expandedListener;
    public void setExpandedListener(OnTextExpandedListener expandedListener){
        this.expandedListener = expandedListener;
    }



    public interface OnTextExpandedListener{
        /**
         * 文本是否展开完毕
         * @param completed
         */
        void onExpandedCompleted(boolean completed);
    }

}
