package com.abcs.huaqiaobang.yiyuanyungou.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;


import com.abcs.huaqiaobang.yiyuanyungou.BaseFragment;
import com.abcs.huaqiaobang.yiyuanyungou.R;
import com.abcs.huaqiaobang.yiyuanyungou.activity.GoodsDetailActivity;
import com.abcs.huaqiaobang.yiyuanyungou.dialog.ProgressDlgUtil;
import com.abcs.huaqiaobang.yiyuanyungou.util.HttpRequest;
import com.abcs.huaqiaobang.yiyuanyungou.util.HttpRevMsg;
import com.abcs.huaqiaobang.yiyuanyungou.util.TLUrl;
import com.abcs.huaqiaobang.yiyuanyungou.view.recyclerview.FullyGridLayoutManager;
import com.abcs.huaqiaobang.yiyuanyungou.view.recyclerview.NetworkUtils;
import com.abcs.huaqiaobang.yiyuanyungou.view.recyclerview.SpacesItemDecoration;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by zjz on 2016/1/16.
 */
public class DetailFragment extends BaseFragment {
    GoodsDetailActivity activity;
    RelativeLayout view;

    private WebView webView;
    private ScrollView scrollView;
    private RecyclerView recyclerView;
    private ImageView imageView;
    /**
     * 标志位，标志已经初始化完成
     */
    private boolean isPrepared;
    /**
     * 是否已被加载过一次，第二次就不再去请求数据了
     */
    private boolean mHasLoadedOnce;
    private Handler handler = new Handler();
    private String goods_id;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = (GoodsDetailActivity) getActivity();
        view = (RelativeLayout) activity.getLayoutInflater().inflate(
                R.layout.hwg_fragment_detail, null);
        goods_id = (String) getArguments().getSerializable("goods_id");
        webView = (WebView) view.findViewById(R.id.web_view);
        scrollView = (ScrollView) view.findViewById(R.id.news_scroll);
        recyclerView = (RecyclerView) view.findViewById(R.id.recommend_recyclerView);
        imageView= (ImageView) view.findViewById(R.id.img_commend);
        WebSettings settings = webView.getSettings();
        settings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        settings.setJavaScriptEnabled(true);
        settings.setAllowFileAccess(true);
        //设置可放大缩小
        settings.setBuiltInZoomControls(true);
        //设置加载自适应屏幕大小
        settings.setUseWideViewPort(true);
        settings.setLoadWithOverviewMode(true);

    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        ViewGroup p = (ViewGroup) view.getParent();
        isPrepared = true;
        lazyLoad();
        if (p != null)
            p.removeAllViewsInLayout();

        return view;
    }

    @Override
    protected void lazyLoad() {
        if (isVisible) {
            GoodsDetailActivity.setIsVisible(GoodsDetailActivity.DETAIL);

        }
        if (!isPrepared || !isVisible || mHasLoadedOnce) {
            return;
        }

        if (NetworkUtils.isNetAvailable(activity)) {
//            initRecommend();
            webView.loadUrl(activity.getDetail_url());
        }



    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

}
