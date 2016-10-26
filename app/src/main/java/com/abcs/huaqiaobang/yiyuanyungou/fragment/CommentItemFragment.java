package com.abcs.huaqiaobang.yiyuanyungou.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.abcs.huaqiaobang.yiyuanyungou.BaseFragment;
import com.abcs.huaqiaobang.yiyuanyungou.R;
import com.abcs.huaqiaobang.yiyuanyungou.beans.Comment;
import com.abcs.huaqiaobang.yiyuanyungou.dialog.ProgressDlgUtil;
import com.abcs.huaqiaobang.yiyuanyungou.fragment.adapter.CommentItemFragmentAdapter;
import com.abcs.huaqiaobang.yiyuanyungou.fragment.viewHolder.CommentItemFragmentViewHolder;
import com.abcs.huaqiaobang.yiyuanyungou.util.MyString;
import com.abcs.huaqiaobang.yiyuanyungou.util.TLUrl;
import com.abcs.huaqiaobang.yiyuanyungou.view.recyclerview.EndlessRecyclerOnScrollListener;
import com.abcs.huaqiaobang.yiyuanyungou.view.recyclerview.FullyLinearLayoutManager;
import com.abcs.huaqiaobang.yiyuanyungou.view.recyclerview.HeaderAndFooterRecyclerViewAdapter;
import com.abcs.huaqiaobang.yiyuanyungou.view.recyclerview.LoadingFooter;
import com.abcs.huaqiaobang.yiyuanyungou.view.recyclerview.NetworkUtils;
import com.abcs.huaqiaobang.yiyuanyungou.view.recyclerview.RecyclerViewAndSwipeRefreshLayout;
import com.abcs.huaqiaobang.yiyuanyungou.view.recyclerview.RecyclerViewStateUtils;
import com.abcs.huaqiaobang.yiyuanyungou.yyg.beans.Goods;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.ButterKnife;

/**
 * Created by zjz on 2016/1/16.
 */
public class CommentItemFragment extends BaseFragment implements RecyclerViewAndSwipeRefreshLayout.SwipeRefreshLayoutRefresh, CommentItemFragmentViewHolder.ItemOnClick {

    //    GoodsDetailCommentActivity commentactivity;
    Activity activity;
    View rootView;
    ArrayList<Goods> goodsList = new ArrayList<Goods>();
    RecyclerView recyclerView;
    SwipeRefreshLayout swipeRefreshLayout;
    ImageView imgNull;
    TextView tvNull;
    RelativeLayout layoutNull;


    private View view;
    int totalPage;
    int currentPage = 1;
    boolean isLoadMore = false;
    boolean first = false;
    private HeaderAndFooterRecyclerViewAdapter mHeaderAndFooterRecyclerViewAdapter = null;
    RecyclerViewAndSwipeRefreshLayout recyclerViewAndSwipeRefreshLayout;
    CommentItemFragmentAdapter commentItemFragmentAdapter;
    private RequestQueue mRequestQueue;
    String type;
    String goods_id;
    /**
     * 标志位，标志已经初始化完成
     */
    private boolean isPrepared;
    /**
     * 是否已被加载过一次，第二次就不再去请求数据了
     */
    private boolean mHasLoadedOnce;
    boolean isShow;

    public static CommentItemFragment newInstance(String type,String goods_id,boolean isShow){
        Bundle bundle = new Bundle();
        bundle.putSerializable("type", type);
        bundle.putSerializable("goods_id", goods_id);
        bundle.putSerializable("isShow", isShow);
        CommentItemFragment commentItemFragment=new CommentItemFragment();
        commentItemFragment.setArguments(bundle);
        return commentItemFragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = getActivity();
//        commentactivity= (GoodsDetailCommentActivity) getActivity();
        view = activity.getLayoutInflater().inflate(
                R.layout.hwg_fragment_comment_item, null);
        mRequestQueue = Volley.newRequestQueue(activity);
//        goods_id = (String) getArguments().getSerializable("goods_id");
//        isShow = (boolean) getArguments().getSerializable("isShow");
        Bundle bundle = getArguments();
        if (bundle != null) {
            type = bundle.getString("type");
            goods_id = bundle.getString("goods_id");
            isShow = bundle.getBoolean("isShow");
        }
        initView();
        if (isShow)
            initRecycler();
    }

    private void initView() {
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new FullyLinearLayoutManager(activity));
        layoutNull = (RelativeLayout) view.findViewById(R.id.layout_null);
    }

    public void initRecycler() {
        recyclerView.addOnScrollListener(mOnScrollListener);
        initAllDates();
        commentItemFragmentAdapter = new CommentItemFragmentAdapter(activity, this);
        mHeaderAndFooterRecyclerViewAdapter = new HeaderAndFooterRecyclerViewAdapter(commentItemFragmentAdapter);
        recyclerViewAndSwipeRefreshLayout = new RecyclerViewAndSwipeRefreshLayout(activity, view, mHeaderAndFooterRecyclerViewAdapter, this, MyString.TYPE_ACOMMENT);
    }

    private void initAllDates() {
        Log.i("zjz", "goods_id=" + goods_id);
        Log.i("zjz", "type=" + type);
        if (!first) {
            ProgressDlgUtil.showProgressDlg("Loading...", activity);
        }
        final ArrayList<Comment> dataList = new ArrayList<>();
        JsonObjectRequest jr = new JsonObjectRequest(Request.Method.POST, TLUrl.URL_hwg_goods_comment2 + "&goods_id=" + goods_id + "&type="+type + "&curpage=" + currentPage, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    if (response.getInt("code") == 200) {
                        Log.i("zjz", "all_comment:连接成功");
                        totalPage = response.getInt("page_total");
                        Log.i("zjz", "totalpage=" + totalPage);
                        Log.i("zjz", "response=" + response);
                        if (response.getInt("page_total") != 0) {
                            JSONObject object = response.getJSONObject("datas");
                            JSONArray groupArray = object.getJSONArray("goodsevallist");
                            if (isLoadMore) {
                                int currentSize = commentItemFragmentAdapter.getItemCount();
                                for (int i = 0; i < groupArray.length(); i++) {
                                    Comment orders = new Comment();
                                    orders.setId(currentSize + i);
                                    JSONObject object1 = groupArray.getJSONObject(i);
                                    orders.setGeval_id(object1.optString("geval_id"));
                                    orders.setGeval_orderid(object1.optString("geval_orderid"));
                                    orders.setGeval_orderno(object1.optString("geval_orderno"));
                                    orders.setGeval_ordergoodsid(object1.optString("geval_ordergoodsid"));
                                    orders.setGeval_goodsid(object1.optString("geval_goodsid"));
                                    orders.setGeval_goodsname(object1.optString("geval_goodsname"));
                                    orders.setGeval_goodsprice(object1.optDouble("geval_goodsprice"));
                                    orders.setGeval_goodsimage(object1.optString("geval_goodsimage"));
                                    orders.setGeval_scores(object1.optInt("geval_scores"));
                                    orders.setGeval_content(object1.optString("geval_content"));
                                    orders.setGeval_isanonymous(object1.optString("geval_isanonymous").equals("0") ? false : true);
                                    orders.setGeval_addtime(object1.optLong("geval_addtime"));
                                    orders.setGeval_storeid(object1.optString("geval_storeid"));
                                    orders.setGeval_storename(object1.optString("geval_storename"));
                                    orders.setGeval_frommemberid(object1.optString("geval_frommemberid"));
                                    orders.setGeval_frommembername(object1.optString("geval_frommembername"));
                                    orders.setGeval_image(object1.optString("geval_image"));
                                    orders.setGeval_explain(object1.optString("geval_explain"));

                                    dataList.add(orders);
                                }
                                addItems(dataList);

                            } else {
                                dataList.clear();
                                for (int i = 0; i < groupArray.length(); i++) {
                                    Comment orders = new Comment();
                                    orders.setId(i);
                                    JSONObject object1 = groupArray.getJSONObject(i);
                                    orders.setGeval_id(object1.optString("geval_id"));
                                    orders.setGeval_orderid(object1.optString("geval_orderid"));
                                    orders.setGeval_orderno(object1.optString("geval_orderno"));
                                    orders.setGeval_ordergoodsid(object1.optString("geval_ordergoodsid"));
                                    orders.setGeval_goodsid(object1.optString("geval_goodsid"));
                                    orders.setGeval_goodsname(object1.optString("geval_goodsname"));
                                    orders.setGeval_goodsprice(object1.optDouble("geval_goodsprice"));
                                    orders.setGeval_scores(object1.optInt("geval_scores"));
                                    orders.setGeval_content(object1.optString("geval_content"));
                                    orders.setGeval_goodsimage(object1.optString("geval_goodsimage"));
                                    orders.setGeval_isanonymous(object1.optString("geval_isanonymous").equals("0") ? false : true);
                                    orders.setGeval_addtime(object1.optLong("geval_addtime"));
                                    orders.setGeval_storeid(object1.optString("geval_storeid"));
                                    orders.setGeval_storename(object1.optString("geval_storename"));
                                    orders.setGeval_frommemberid(object1.optString("geval_frommemberid"));
                                    orders.setGeval_frommembername(object1.optString("geval_frommembername"));
                                    orders.setGeval_image(object1.optString("geval_image"));
                                    orders.setGeval_explain(object1.optString("geval_explain"));
                                    dataList.add(orders);
                                }
                                mCurrentCounter = dataList.size();
                                commentItemFragmentAdapter.addItems(dataList);
                                commentItemFragmentAdapter.notifyDataSetChanged();
                            }
                        }

                        if (layoutNull != null){
                            layoutNull.setVisibility(commentItemFragmentAdapter.getList().size() == 0 ? View.VISIBLE : View.GONE);
//                            GoodsDetailActivity.setIsVisible(GoodsDetailActivity.COMMENT);
                        }
                        recyclerViewAndSwipeRefreshLayout.getSwipeRefreshLayout().setRefreshing(false);
                        mHasLoadedOnce = true;
                        ProgressDlgUtil.stopProgressDlg();

                    } else {
                        recyclerViewAndSwipeRefreshLayout.getSwipeRefreshLayout().setRefreshing(false);
                        Log.i("zjz", "goodsActivity解析失败");
                    }
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    Log.i("zjz", e.toString());
                    recyclerViewAndSwipeRefreshLayout.getSwipeRefreshLayout().setRefreshing(false);
                    e.printStackTrace();
                } finally {
                    ProgressDlgUtil.stopProgressDlg();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        });
        mRequestQueue.add(jr);
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        ViewGroup p = (ViewGroup) view.getParent();
        if (p != null)
            p.removeAllViewsInLayout();
        ButterKnife.inject(this, view);

        isPrepared = true;
        lazyLoad();
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
    }

    @Override
    public void swipeRefreshLayoutOnRefresh() {
//        recyclerViewAndSwipeRefreshLayout.getSwipeRefreshLayout().setRefreshing(false);
        first = true;
        recyclerViewAndSwipeRefreshLayout.getSwipeRefreshLayout().setRefreshing(true);
        isLoadMore = false;
        currentPage = 1;
        initAllDates();
    }

    @Override
    public void onItemRootViewClick(int position) {

    }

    @Override
    public void onImgClick(int position) {

    }


    @Override
    protected void lazyLoad() {
        if (!isPrepared || !isVisible || mHasLoadedOnce) {
            return;
        }

        initRecycler();
    }

    private MyHandler handler = new MyHandler();
    private int mCurrentCounter = 0;

    private void notifyDataSetChanged() {
        mHeaderAndFooterRecyclerViewAdapter.notifyDataSetChanged();
    }

    private void addItems(ArrayList<Comment> list) {

        commentItemFragmentAdapter.addItems(list);
        mCurrentCounter += list.size();
    }

    private static final int REQUEST_COUNT = 15;
    private EndlessRecyclerOnScrollListener mOnScrollListener = new EndlessRecyclerOnScrollListener() {

        @Override
        public void onLoadNextPage(View view) {
            super.onLoadNextPage(view);

            LoadingFooter.State state = RecyclerViewStateUtils.getFooterViewState(recyclerView);
            if (state == LoadingFooter.State.Loading) {
                Log.d("@Cundong", "the state is Loading, just wait..");
                return;
            }

            if (currentPage < totalPage) {
                // loading more
                RecyclerViewStateUtils.setFooterViewState(getActivity(), recyclerView, REQUEST_COUNT, LoadingFooter.State.Loading, null);
                requestData();
            } else {
                //the end
                RecyclerViewStateUtils.setFooterViewState(getActivity(), recyclerView, REQUEST_COUNT, LoadingFooter.State.TheEnd, null);
            }
        }

    };



    private class MyHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case -1:
                    isLoadMore = true;
                    Log.i("zjz", "totalPage=" + totalPage);
                    if (currentPage < totalPage && isLoadMore) {
                        currentPage += 1;
                        Log.i("zjz", "current=" + currentPage);
                        RecyclerViewStateUtils.setFooterViewState(recyclerView, LoadingFooter.State.Normal);
                        initAllDates();
                    }
                    break;
                case -2:
                    notifyDataSetChanged();
                    break;
                case -3:
                    RecyclerViewStateUtils.setFooterViewState(activity, recyclerView, REQUEST_COUNT, LoadingFooter.State.NetWorkError, mFooterClick);
                    break;
            }
        }
    }


    private View.OnClickListener mFooterClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            RecyclerViewStateUtils.setFooterViewState(getActivity(), recyclerView, REQUEST_COUNT, LoadingFooter.State.Loading, null);
            requestData();
        }
    };

    /**
     * 模拟请求网络
     */
    private void requestData() {

        new Thread() {

            @Override
            public void run() {
                super.run();

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                //模拟一下网络请求失败的情况
                if (NetworkUtils.isNetAvailable(getContext())) {
                    Log.i("zjz", "有网络");
//                    mHandler.sendEmptyMessage(-1);
                    handler.sendEmptyMessage(-1);

                } else {
//                    mHandler.sendEmptyMessage(-3);
                    handler.sendEmptyMessage(-3);
                }
            }
        }.start();
    }
}
