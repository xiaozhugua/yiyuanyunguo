package com.abcs.huaqiaobang.yiyuanyungou.view.zjzbanner;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;


import com.abcs.huaqiaobang.yiyuanyungou.R;
import com.abcs.huaqiaobang.yiyuanyungou.view.zjzbanner.adapter.LBaseAdapter;
import com.abcs.huaqiaobang.yiyuanyungou.view.zjzbanner.transformer.LMPageTransformer;
import com.abcs.huaqiaobang.yiyuanyungou.view.zjzbanner.transformer.TransitionEffect;
import com.abcs.huaqiaobang.yiyuanyungou.view.zjzbanner.utils.ScreenUtils;
import com.abcs.huaqiaobang.yiyuanyungou.view.zjzbanner.utils.ViewPagerScroller;
import com.abcs.huaqiaobang.yiyuanyungou.view.zjzbanner.viewpager.HorizonVerticalViewPager;
import com.abcs.huaqiaobang.yiyuanyungou.view.zjzbanner.viewpager.MyViewPager;

import java.util.ArrayList;
import java.util.List;


/**
 * 轮播图
 */
public class LMBanners<T> extends FrameLayout implements ViewPager.OnPageChangeListener {
    private static String TAG = "LBanners";
    private List<T> mList = new ArrayList<>();
    private Context context;
    private int totalCount = 100;
    private int showCount;
    private int currentPosition = 0;
    private RelativeLayout mLayout;
    private HorizonVerticalViewPager viewPager;
    private LinearLayout indicatorLayout;
    private LBaseAdapter adapter;
    private int pageItemWidth;
    private ViewPagerScroller mScroller;
    private ViewPagerAdapter mAdapter;

    private TransitionEffect mTransitionEffect;//过渡动画
    private int pageTransFormerIndex = 0;

    private IndicaTorPosition mIndicaTorLocation;
    private int indicatoreIndex = 0;

    private boolean canLoop = true;//是否支持循环播放
    private boolean autoPlay = true;//是否支持自动播放
    private boolean isVertical = false;//是否支持纵向滚动
    private int durtion = 0;//自动播放间隔时间，默认3S一次
    private int mScrollDuration = 0;//两页之间切换所需要的时间
    private int mSlectIndicatorRes = R.drawable.img_hwg_indicator_select;//选中指示器
    private int mUnSlectIndicatorRes = R.drawable.img_hwg_indicator_unselect;//未选中指示器
    private int mTextColor = Color.WHITE;//文字颜色
    private int mTipTextSize;//文字大小


    public LMBanners(Context context) {
        super(context);
        this.context = context;
    }

    public LMBanners(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        initStyle(attrs, 0);
    }

    public LMBanners(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        initStyle(attrs, defStyleAttr);
    }

    private void initStyle(AttributeSet attrs, int defStyleAttr) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.LMBanners, defStyleAttr, 0);
        canLoop = typedArray.getBoolean(R.styleable.LMBanners_canLoop, true);
        durtion = typedArray.getInteger(R.styleable.LMBanners_durtion, 3000);
        mScrollDuration = typedArray.getInteger(R.styleable.LMBanners_scroll_duration, 0);//为0时为默认滚动速度
        autoPlay = typedArray.getBoolean(R.styleable.LMBanners_auto_play, true);//默认可以自动播放
        mSlectIndicatorRes = typedArray.getResourceId(R.styleable.LMBanners_indicator_select, R.drawable.img_hwg_indicator_select);
        mUnSlectIndicatorRes = typedArray.getResourceId(R.styleable.LMBanners_indicator_unselect, R.drawable.img_hwg_indicator_unselect);
        pageTransFormerIndex = typedArray.getInt(R.styleable.LMBanners_horizontal_transitionEffect, TransitionEffect.Default.ordinal());
        mTransitionEffect = TransitionEffect.values()[pageTransFormerIndex];
        isVertical = typedArray.getBoolean(R.styleable.LMBanners_isVertical, false);
        indicatoreIndex = typedArray.getInt(R.styleable.LMBanners_indicator_position, IndicaTorPosition.BOTTOM_MID.ordinal());
        mIndicaTorLocation = IndicaTorPosition.values()[indicatoreIndex];
        Log.e("Test", String.valueOf(isVertical));
        typedArray.recycle();
    }

    /**
     * 初始化
     */
    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        View view = LayoutInflater.from(context).inflate(R.layout.banner_layout, null);
        this.mLayout = (RelativeLayout) view.findViewById(R.id.layout);
        this.viewPager = (HorizonVerticalViewPager) view.findViewById(R.id.gallery);
        this.indicatorLayout = (LinearLayout) view.findViewById(R.id.CarouselLayoutPage);
        pageItemWidth = ScreenUtils.dip2px(context, 5);
        this.viewPager.addOnPageChangeListener(this);
        //如果是纵向滑动重新进行初始化
        checkIsVertical(isVertical);
       //设置指示器的位置
        setIndicatorPosition(mIndicaTorLocation);
        this.viewPager.setOnViewPagerTouchEventListener(new MyViewPager.OnViewPagerTouchEvent() {
            @Override
            public void onTouchDown() {
                stopImageTimerTask();
            }

            @Override
            public void onTouchUp() {
           startImageTimerTask();
            }
        });
        addView(view);

    }

    private void init() {
        viewPager.setAdapter(null);
        indicatorLayout.removeAllViews();

        if (null == adapter) {
            return;
        }
        showCount = mList.size();
        if (showCount == 1) {
            viewPager.setScrollEnabled(false);
        } else {
            viewPager.setScrollEnabled(true);
        }

        for (int i = 0; i < showCount; i++) {
            View view = new View(context);
            if (currentPosition == i) {
                view.setPressed(true);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(pageItemWidth + ScreenUtils.dip2px(context, 3), pageItemWidth + ScreenUtils.dip2px(context, 3));
                params.setMargins(pageItemWidth, 0, 0, 0);
                view.setLayoutParams(params);
                view.setBackgroundResource(mSlectIndicatorRes);
            } else {
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(pageItemWidth, pageItemWidth);
                params.setMargins(pageItemWidth, 0, 0, 0);
                view.setLayoutParams(params);
                view.setBackgroundResource(mUnSlectIndicatorRes);
            }
            indicatorLayout.addView(view);
        }

        setCanLoop(canLoop);
        //设置滚动速率
        setScrollDurtion(mScrollDuration);

        viewPager.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                    case MotionEvent.ACTION_MOVE:
                        stopImageTimerTask();
                        Log.e(TAG, "ACTION_MOVE");
                        break;
                    case MotionEvent.ACTION_UP:
                        startImageTimerTask();
                        Log.e(TAG, "ACTION_UP");
                        break;
                    default:

                        break;
                }
                return false;
            }
        });
        startImageTimerTask();
    }



    public void setAdapter(LBaseAdapter adapter, List<T> list) {
        this.adapter = adapter;
        if (adapter != null) {
            this.mList = list;
            init();
        }
    }


    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        Log.d("CarouselView", "onPageScrolled was invoke()");

    }

    @Override
    public void onPageSelected(int position) {
        currentPosition = position;
        int count = indicatorLayout.getChildCount();
        for (int i = 0; i < count; i++) {
            View view = indicatorLayout.getChildAt(i);
            if (position % showCount == i) {
                view.setSelected(true);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(pageItemWidth + ScreenUtils.dip2px(context, 3), pageItemWidth + ScreenUtils.dip2px(context, 3));
                params.setMargins(pageItemWidth, 0, 0, 0);
                view.setLayoutParams(params);
                view.setBackgroundResource(mSlectIndicatorRes);

            } else {
                view.setSelected(false);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(pageItemWidth, pageItemWidth);
                params.setMargins(pageItemWidth, 0, 0, 0);
                view.setLayoutParams(params);
                view.setBackgroundResource(mUnSlectIndicatorRes);

            }
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {
        Log.d("CarouselView", "onPageScrollStateChanged was invoke()");

    }

    /**
     * 判断横向还是纵向进行设置
     *
     * @param b
     */
    private void checkIsVertical(boolean b) {
        this.isVertical = b;
        if (isVertical) {
            Log.e("Test2", String.valueOf(isVertical));
            viewPager.setIsVertical(isVertical);
            viewPager.removeAllViews();
            viewPager.init();
        } else {
            viewPager.setPageTransformer(true, LMPageTransformer.getPageTransformer(mTransitionEffect));

        }
    }

    /**
     * 选中喜欢的的切换动画
     *
     * @param effect
     */
    public void setHoriZontalTransitionEffect(TransitionEffect effect) {
        mTransitionEffect = effect;
        if (viewPager != null && isVertical == false) {
            viewPager.setPageTransformer(true, LMPageTransformer.getPageTransformer(effect));
        }
    }

    /**
     * 设置指示器的位置
     */

    public  void  setIndicatorPosition(IndicaTorPosition mIndicaTorLocation){
        if (mIndicaTorLocation == IndicaTorPosition.BOTTOM_MID) {
            indicatorLayout.setGravity(Gravity.CENTER);
        } else {
            indicatorLayout.setGravity(Gravity.CENTER | Gravity.RIGHT);
        }


    }
    /**
     * 设置自定义的切换动画
     *
     * @param customTransformer
     */
    public void setHoriZontalCustomTransformer(ViewPager.PageTransformer customTransformer) {
        if (viewPager != null && isVertical == false) {
            viewPager.setPageTransformer(true, customTransformer);
        }
    }

    /**
     * 设置轮播图切换时间
     *
     * @param durtion
     */
    public void setDurtion(int durtion) {
        this.durtion = durtion;
    }

    /**
     * 设置两页切换需要的时间
     *
     * @param mScrollDuration
     */
    public void setScrollDurtion(int mScrollDuration) {
        this.mScrollDuration = mScrollDuration;
        if (mScrollDuration >= 0) {
            mScroller = new ViewPagerScroller(context);
            mScroller.setScrollDuration(mScrollDuration);
            mScroller.initViewPagerScroll(viewPager);//这个是设置切换过渡时间为2秒
        }
    }

    /**
     * 是否可以循环滑动
     *
     * @param canLoop
     */
    public void setCanLoop(boolean canLoop) {
        this.canLoop = canLoop;
        mAdapter = new ViewPagerAdapter();
        viewPager.setAdapter(mAdapter);
        viewPager.setCurrentItem(0);
    }

    /**
     * 是否可以自动播放
     *
     * @param autoPlay
     */
    public void setAutoPlay(boolean autoPlay) {
        this.autoPlay = autoPlay;
    }

    /**
     * 是否可以竖直播放
     *
     * @param isVertical
     */
    public void setVertical(boolean isVertical) {
        this.isVertical = isVertical;
        checkIsVertical(isVertical);
    }

    /**
     * 设置选中的原点图片
     *
     * @param res
     */
    public void setSelectIndicatorRes(int res) {
        this.mSlectIndicatorRes = res;
    }

    /**
     * 设置未选中的原点图片
     *
     * @param res
     */
    public void setUnSelectUnIndicatorRes(int res) {
        this.mUnSlectIndicatorRes = res;
    }

    /**
     * 隐藏原点
     */
    public void hideIndicatorLayout() {
        indicatorLayout.setVisibility(View.GONE);
    }

    /**
     * 显示原点
     */
    public void showIndicatorLayout() {
        indicatorLayout.setVisibility(View.VISIBLE);
    }

    /**
     * 开始图片滚动任务
     */
    public void startImageTimerTask() {

        stopImageTimerTask();
        if (autoPlay) {
            // 图片每3秒滚动一次
            if (mList.size() > 1) {
                mHandler.sendEmptyMessageDelayed(1, durtion);
            }
        }

    }

    /**
     * 停止图片滚动任务
     */
    public void stopImageTimerTask() {
        if (mHandler != null) {
            mHandler.removeMessages(1);

        }
    }

    /**
     * 退出时清楚所有消息
     */
    public void clearImageTimerTask() {
        mHandler.removeCallbacksAndMessages(null);
    }

    private Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    int allCount = 0;
                    if (canLoop) {
                        allCount = totalCount;
                    } else {
                        allCount = mList.size();

                    }
                    currentPosition = (currentPosition + 1) % allCount;
                    if (currentPosition == totalCount - 1) {//最后一个过渡到第一个不需要动画
                        viewPager.setCurrentItem(showCount - 1, false);
                    } else {
                        viewPager.setCurrentItem(currentPosition);
                    }
                    // 每3秒钟发送一个message，用于切换viewPager中的图片
                    this.sendEmptyMessageDelayed(1, durtion);


                    break;
            }
        }
    };


    class ViewPagerAdapter extends PagerAdapter {


        @Override
        public int getCount() {
            if (canLoop) {
                return totalCount;
            } else {
                return mList.size();
            }


        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            position %= showCount;
            View view = adapter.getView(LMBanners.this, context, position, mList.get(position));


            container.addView(view);
            return view;


        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        @Override
        public int getItemPosition(Object object) {
            return super.getItemPosition(object);
        }

        @Override
        public void finishUpdate(ViewGroup container) {
            super.finishUpdate(container);
            if (canLoop) {
                int position = viewPager.getCurrentItem();
                if (position == 0) {
                    position = showCount;
                    viewPager.setCurrentItem(position, false);
                } else if (position == totalCount - 1) {
                    position = showCount - 1;
                    viewPager.setCurrentItem(position, false);
                }
            }

        }
    }


    public enum IndicaTorPosition {
        BOTTOM_MID,
        BOTTOM_RIGHT
    }
}
