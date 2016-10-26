package com.abcs.huaqiaobang.yiyuanyungou.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.abcs.huaqiaobang.yiyuanyungou.BaseFragment;
import com.abcs.huaqiaobang.yiyuanyungou.R;
import com.abcs.huaqiaobang.yiyuanyungou.activity.GoodsCommentActivity;
import com.abcs.huaqiaobang.yiyuanyungou.broadcast.MyBroadCastReceiver;
import com.abcs.huaqiaobang.yiyuanyungou.broadcast.MyUpdateUI;
import com.abcs.huaqiaobang.yiyuanyungou.util.LogUtil;
import com.abcs.huaqiaobang.yiyuanyungou.util.TLUrl;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by zjz on 2016/6/22 0022.
 */
public class GoodsCommentFragment extends BaseFragment implements View.OnClickListener {
    GoodsCommentActivity activity;
    @InjectView(R.id.t_percent)
    TextView tPercent;
    @InjectView(R.id.t_all)
    TextView tAll;
    @InjectView(R.id.t_good)
    TextView tGood;
    @InjectView(R.id.t_middle)
    TextView tMiddle;
    @InjectView(R.id.t_bad)
    TextView tBad;
    @InjectView(R.id.t_image)
    TextView tImage;
    @InjectView(R.id.frame_comment)
    FrameLayout frameComment;
    @InjectView(R.id.linear_top)
    LinearLayout linearTop;
    private View view;

    /**
     * 标志位，标志已经初始化完成
     */
    private boolean isPrepared;
    /**
     * 是否已被加载过一次，第二次就不再去请求数据了
     */
    private boolean mHasLoadedOnce;
    private RequestQueue mRequestQueue;
    private String goods_id;
    private String all_comment = "0";
    private String good_percent = "0";
    private String good_comment = "0";
    private String middle_comment = "0";
    private String bad_comment = "0";
    private String image_comment = "0";
    private MyBroadCastReceiver myBroadCastReceiver;

    CommentItemFragment allComment;
    CommentItemFragment goodsComment;
    CommentItemFragment middleComment;
    CommentItemFragment badComment;
    CommentItemFragment imgComment;
    public static GoodsCommentFragment newInstance(String goods_id) {
        Bundle bundle = new Bundle();
        bundle.putSerializable("goods_id", goods_id);
        GoodsCommentFragment fragment = new GoodsCommentFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    private FragmentManager manager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        activity = (GoodsCommentActivity) getActivity();

        mRequestQueue = Volley.newRequestQueue(activity);
        manager = activity.getSupportFragmentManager();
        myBroadCastReceiver = new MyBroadCastReceiver(activity, updateUI);
        myBroadCastReceiver.register();
        if (view == null) {
            view = activity.getLayoutInflater().inflate(
                    R.layout.hwg_goods_fragment_comment, null);
            ButterKnife.inject(this, view);
            Bundle bundle = getArguments();
            if (bundle != null) {
                goods_id = bundle.getString("goods_id");
            }
            isPrepared = true;
            lazyLoad();
        }
        setOnListener();
        ButterKnife.inject(this, view);
        ViewGroup p = (ViewGroup) view.getParent();
        if (p != null)
            p.removeView(view);

        return view;
    }

    MyBroadCastReceiver.UpdateUI updateUI = new MyBroadCastReceiver.UpdateUI() {
        @Override
        public void updateShopCar(Intent intent) {

        }

        @Override
        public void updateCarNum(Intent intent) {

        }

        @Override
        public void updateCollection(Intent intent) {
            if (intent.getStringExtra("type").equals(MyUpdateUI.SHOWCOMMENTTITLE)) {
                if (linearTop != null) {
                    linearTop.setVisibility(View.VISIBLE);
                }
            } else if (intent.getStringExtra("type").equals(MyUpdateUI.HIDECOMMENTTITLE)) {
                if (linearTop != null) {
                    linearTop.setVisibility(View.GONE);
                }
            }
        }

        @Override
        public void update(Intent intent) {

        }
    };

    private void setOnListener() {
        tAll.setOnClickListener(this);
        tGood.setOnClickListener(this);
        tMiddle.setOnClickListener(this);
        tBad.setOnClickListener(this);
        tImage.setOnClickListener(this);
    }

    private void initAllDates() {

        Bundle bundle = new Bundle();
        bundle.putSerializable("goods_id", goods_id);
        bundle.putSerializable("isShow", true);
//        allFragment = new CommentItemFragment();
//        allFragment.setArguments(bundle);
        allComment=CommentItemFragment.newInstance("all",goods_id,true);
        manager.beginTransaction().add(R.id.frame_comment,allComment)
//                .add(R.id.frame_comment, goodFragment).add(R.id.frame_comment, middleFragment)
//                .add(R.id.frame_comment, badFragment).add(R.id.frame_comment, commentImageFragment)
//                .hide(goodFragment).hide(middleFragment).hide(badFragment).hide(commentImageFragment)
                .show(allComment)
                .commitAllowingStateLoss();
        Log.i("zjz", "goods_id=" + goods_id);
        JsonObjectRequest jr = new JsonObjectRequest(Request.Method.POST, TLUrl.URL_hwg_goods_comment2 + "&goods_id=" + goods_id + "&type=1" + "&curpage=" + 1, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    if (response.getInt("code") == 200) {
                        LogUtil.e("zjz", "comment_title:连接成功");
                        LogUtil.e("zjz", "response=" + response);
                        if (response.getInt("page_total") != 0) {
                            JSONObject object = response.getJSONObject("datas");
                            JSONObject jsonObject = object.getJSONObject("goods_evaluate_info");
                            good_percent = jsonObject.optString("good_percent");
                            all_comment = jsonObject.optString("all");
                            good_comment = jsonObject.optString("good");
                            middle_comment = jsonObject.optString("normal");
                            bad_comment = jsonObject.optString("bad");
                            image_comment = jsonObject.optString("imagecount");
                        }
                        LogUtil.e("zjz", "好评率:" + good_percent + "全部：" + all_comment + "好评：" + good_comment + "中评：" + middle_comment + "差评：" + bad_comment + "有图：" + image_comment);
                        initView();
                        mHasLoadedOnce = true;
                    } else {
                        Log.i("zjz", "goodsActivity解析失败");
                    }
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    Log.i("zjz", e.toString());
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        });
        mRequestQueue.add(jr);
    }

    private void initView() {
        if (tPercent != null)
            tPercent.setText(good_percent + "%");
        if (tAll != null){
            tAll.setText("全部(" + all_comment + ")");
            tAll.setTextColor(activity.getResources().getColor(R.color.white));
//            tAll.setBackground(activity.getResources().getDrawable(R.drawable.img_hwg_commet_bg3));
            tAll.setBackgroundResource(R.drawable.img_hwg_commet_bg3);
        }
        if (tGood != null)
            tGood.setText("好评(" + good_comment + ")");
        if (tMiddle != null)
            tMiddle.setText("中评(" + middle_comment + ")");
        if (tBad != null)
            tBad.setText("差评(" + bad_comment + ")");
        if (tImage != null)
            tImage.setText("晒图(" + image_comment + ")");


//        goodFragment = new CommentGoodFragment();
//        goodFragment.setArguments(bundle);
//        middleFragment = new CommentMiddleFragment();
//        middleFragment.setArguments(bundle);
//        badFragment = new CommentBadFragment();
//        badFragment.setArguments(bundle);
//        commentImageFragment = new CommentImageFragment();
//        commentImageFragment.setArguments(bundle);



    }

    @Override
    protected void lazyLoad() {
//        if (isVisible) {
//            GoodsDetailActivity.setIsVisible(GoodsDetailActivity.COMMENT);
//        }
        if (!isPrepared || !isVisible || mHasLoadedOnce) {
            return;
        }

        initAllDates();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
        myBroadCastReceiver.unRegister();
    }

    private void hideFragment(FragmentTransaction transaction) {
        if (allComment != null)
            transaction.hide(allComment);
        if (goodsComment!= null)
            transaction.hide(goodsComment);
        if (middleComment != null)
            transaction.hide(middleComment);
        if (badComment != null)
            transaction.hide(badComment);
        if (imgComment != null)
            transaction.hide(imgComment);
    }

    @Override
    public void onClick(View v) {
        Bundle bundle = new Bundle();
        bundle.putSerializable("goods_id", goods_id);
        bundle.putSerializable("isShow", true);
        FragmentTransaction transaction = activity.getSupportFragmentManager().beginTransaction();
        hideFragment(transaction);
        tAll.setTextColor(activity.getResources().getColor(R.color.noselect));
        tAll.setBackgroundResource(R.drawable.img_hwg_commet_bg2);
        tGood.setTextColor(activity.getResources().getColor(R.color.noselect));
        tGood.setBackgroundResource(R.drawable.img_hwg_commet_bg2);
        tMiddle.setTextColor(activity.getResources().getColor(R.color.noselect));
        tMiddle.setBackgroundResource(R.drawable.img_hwg_commet_bg2);
        tBad.setTextColor(activity.getResources().getColor(R.color.noselect));
        tBad.setBackgroundResource(R.drawable.img_hwg_commet_bg2);
        tImage.setTextColor(activity.getResources().getColor(R.color.noselect));
        tImage.setBackgroundResource(R.drawable.img_hwg_commet_bg2);
        switch (v.getId()) {
            case R.id.t_all:
                tAll.setTextColor(activity.getResources().getColor(R.color.white));
//                tAll.setBackground(activity.getResources().getDrawable(R.drawable.img_hwg_commet_bg3));
                tAll.setBackgroundResource(R.drawable.img_hwg_commet_bg3);
                if (allComment!= null) {
                    transaction.show(allComment);
                } else {
                    Log.i("zjz", "all为空");
//                    allFragment = new CommentItemFragment();
//                    allFragment.setArguments(bundle);
                    allComment=CommentItemFragment.newInstance("all",goods_id,true);
                    transaction.add(R.id.frame_comment,allComment);
                }
                break;
            case R.id.t_good:
                tGood.setTextColor(activity.getResources().getColor(R.color.white));
//                tGood.setBackground(activity.getResources().getDrawable(R.drawable.img_hwg_commet_bg3));
                tGood.setBackgroundResource(R.drawable.img_hwg_commet_bg3);
                if (goodsComment != null) {
                    transaction.show(goodsComment);
                } else {
                    Log.i("zjz", "goods为空");
//                    goodFragment = new CommentGoodFragment();
//                    goodFragment.setArguments(bundle);
                    goodsComment=CommentItemFragment.newInstance("1",goods_id,true);
                    transaction.add(R.id.frame_comment, goodsComment);
                }
                break;
            case R.id.t_middle:
                tMiddle.setTextColor(activity.getResources().getColor(R.color.white));
//                tMiddle.setBackground(activity.getResources().getDrawable(R.drawable.img_hwg_commet_bg3));
                tMiddle.setBackgroundResource(R.drawable.img_hwg_commet_bg3);
                if (middleComment != null) {
                    transaction.show(middleComment);
                } else {
                    Log.i("zjz", "middle为空");
//                    middleFragment = new CommentMiddleFragment();
//                    middleFragment.setArguments(bundle);
                    middleComment=CommentItemFragment.newInstance("2",goods_id,true);
                    transaction.add(R.id.frame_comment, middleComment);
                }

                break;
            case R.id.t_bad:
                tBad.setTextColor(activity.getResources().getColor(R.color.white));
//                tBad.setBackground(activity.getResources().getDrawable(R.drawable.img_hwg_commet_bg3));
                tBad.setBackgroundResource(R.drawable.img_hwg_commet_bg3);
                if (badComment != null) {
                    transaction.show(badComment);
                } else {
                    Log.i("zjz", "bad为空");
//                    badFragment = new CommentBadFragment();
//                    badFragment.setArguments(bundle);
                    badComment=CommentItemFragment.newInstance("3",goods_id,true);
                    transaction.add(R.id.frame_comment, badComment);
                }
                break;
            case R.id.t_image:
                tImage.setTextColor(activity.getResources().getColor(R.color.white));
//                tImage.setBackground(activity.getResources().getDrawable(R.drawable.img_hwg_commet_bg3));
                tImage.setBackgroundResource(R.drawable.img_hwg_commet_bg3);
                if (imgComment != null) {
                    transaction.show(imgComment);
                } else {
                    Log.i("zjz", "image为空");
//                    commentImageFragment = new CommentImageFragment();
//                    commentImageFragment.setArguments(bundle);
                    imgComment=CommentItemFragment.newInstance("4",goods_id,true);
                    transaction.add(R.id.frame_comment,imgComment);
                }
                break;

        }
        transaction.commitAllowingStateLoss();
    }
}
