package com.abcs.huaqiaobang.yiyuanyungou.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.abcs.huaqiaobang.yiyuanyungou.BaseActivity;
import com.abcs.huaqiaobang.yiyuanyungou.MyApplication;
import com.abcs.huaqiaobang.yiyuanyungou.R;
import com.abcs.huaqiaobang.yiyuanyungou.broadcast.MyUpdateUI;
import com.abcs.huaqiaobang.yiyuanyungou.dialog.ProgressDlgUtil;
import com.abcs.huaqiaobang.yiyuanyungou.dialog.PromptDialog;
import com.abcs.huaqiaobang.yiyuanyungou.util.Complete;
import com.abcs.huaqiaobang.yiyuanyungou.util.HttpRequest;
import com.abcs.huaqiaobang.yiyuanyungou.util.HttpRevMsg;
import com.abcs.huaqiaobang.yiyuanyungou.util.NumberUtils;
import com.abcs.huaqiaobang.yiyuanyungou.util.TLUrl;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.tencent.mm.sdk.modelpay.PayReq;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class PayWayActivity extends BaseActivity implements View.OnClickListener {

    public static final String YH = "yhk";
    public static final String WX = "wx";
    public static final String ZFB = "zfb";
    @InjectView(R.id.relative_back)
    RelativeLayout relativeBack;
    @InjectView(R.id.tljr_txt_country_title)
    TextView tljrTxtCountryTitle;
    @InjectView(R.id.seperate)
    View seperate;
    @InjectView(R.id.tljr_grp_payway_title)
    RelativeLayout tljrGrpPaywayTitle;
    @InjectView(R.id.t_goods_total_money)
    TextView tGoodsTotalMoney;
    @InjectView(R.id.image2111)
    ImageView image2111;
    @InjectView(R.id.image211)
    ImageView image211;
    @InjectView(R.id.yh_check)
    CheckBox yhCheck;
    @InjectView(R.id.img_yl)
    ImageView imgYl;
    @InjectView(R.id.image22161)
    ImageView image22161;
    @InjectView(R.id.relative_yl)
    RelativeLayout relativeYl;
    @InjectView(R.id.wx_check)
    CheckBox wxCheck;
    @InjectView(R.id.img_wx)
    ImageView imgWx;
    @InjectView(R.id.image2211)
    ImageView image2211;
    @InjectView(R.id.relative_wx)
    RelativeLayout relativeWx;
    @InjectView(R.id.zfb_check)
    CheckBox zfbCheck;
    @InjectView(R.id.img_zfb)
    ImageView imgZfb;
    @InjectView(R.id.image22131)
    ImageView image22131;
    @InjectView(R.id.relative_zfb)
    RelativeLayout relativeZfb;
    @InjectView(R.id.tv_pay)
    Button tvPay;
    @InjectView(R.id.btn_pay)
    RelativeLayout btnPay;
    @InjectView(R.id.RelativeLayout1)
    LinearLayout RelativeLayout1;
    @InjectView(R.id.balance_check)
    CheckBox balanceCheck;
    @InjectView(R.id.t_balance)
    TextView tBalance;
    @InjectView(R.id.relative_balance)
    RelativeLayout relativeBalance;
    @InjectView(R.id.t_need)
    TextView tNeed;
    @InjectView(R.id.linear_need)
    LinearLayout linearNeed;
    @InjectView(R.id.rc_balance_check)
    CheckBox rcBalanceCheck;
    @InjectView(R.id.t_rc_balance)
    TextView tRcBalance;
    @InjectView(R.id.relative_rc_balance)
    RelativeLayout relativeRcBalance;
    @InjectView(R.id.ed_pwd)
    EditText edPwd;
    @InjectView(R.id.t_use)
    TextView tUse;
    @InjectView(R.id.linear_pay_pdw)
    LinearLayout linearPayPdw;
    @InjectView(R.id.t_set_paypwd)
    TextView tSetPaypwd;
    @InjectView(R.id.linear_no_paypwd)
    RelativeLayout linearNoPaypwd;
    private Handler handler = new Handler();
    double total;
    int num;
    String activity_id;
    String pay_sn;
    boolean isYungou;
    boolean isFromOrder;
    boolean isYH;
    boolean isWX;
    boolean isZFB;
    boolean isYE = false;
    boolean needMore = false;
    boolean isRC = false;
    boolean isPay = false;
    String type;
    IWXAPI iwxapi;
    private RequestQueue mRequestQueue;
    private double yygBalance;
    boolean isBindEmail;//是否绑定邮箱
    boolean isBindPhone;//是否绑定手机
    boolean isBindSuccess;//是否绑定成功
    String rcbNum;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.hwg_activity_pay_way);
        ButterKnife.inject(this);
        AliPay.getInstance().init(this);
        mRequestQueue = Volley.newRequestQueue(this);

        //AppID：wxe134ccc9e61300a0
        iwxapi = WXAPIFactory.createWXAPI(this, "wxe134ccc9e61300a0");
        iwxapi.registerApp("wxe134ccc9e61300a0");
        setOnListener();
        isYungou = getIntent().getBooleanExtra("yungou", false);
        isFromOrder = getIntent().getBooleanExtra("isFromOrder", false);
        if (!isYungou) {
            if(isFromOrder){
                initUser();
            }
            total = (double) getIntent().getSerializableExtra("total_money");
            pay_sn = getIntent().getStringExtra("pay_sn");
            tGoodsTotalMoney.setText(NumberUtils.formatPrice(total));
        } else {
            type = ZFB;
            isZFB = true;
            zfbCheck.setChecked(true);
            if (MyApplication.getInstance().self != null)
                initMyBalance();
            num = getIntent().getIntExtra("num", 0);
            activity_id = getIntent().getStringExtra("act_id");
            total = (double) getIntent().getSerializableExtra("yungou_money");
            relativeYl.setVisibility(View.GONE);
//            relativeWx.setVisibility(View.GONE);
            tGoodsTotalMoney.setText(NumberUtils.formatPrice(total));
        }

    }

    private void initUser() {
        ProgressDlgUtil.showProgressDlg("Loading...",this);
        HttpRequest.sendPost(TLUrl.URL_hwg_member, "&key=" + MyApplication.getInstance().getMykey(), new HttpRevMsg() {
            @Override
            public void revMsg(final String msg) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            JSONObject object = new JSONObject(msg);
                            if (object.getInt("code") == 200) {
                                Log.i("zjz", "msg=" + msg);
                                JSONObject data = object.getJSONObject("datas");
                                if (data.optString("member_email_bind").equals("1")) {
                                    isBindEmail = true;
                                } else if (!data.optString("member_email").equals("null")) {
                                    isBindSuccess = true;
                                } else {
                                    isBindSuccess = false;
                                    isBindEmail = false;
                                }
                                if (data.optString("member_mobile_bind").equals("1")) {
                                    isBindPhone = true;
                                } else {
                                    isBindPhone = false;
                                }
                                if (relativeRcBalance != null && tRcBalance != null)
                                    if (data.optDouble("available_rc_balance") > 0) {
                                        tRcBalance.setText(NumberUtils.formatPrice(data.optDouble("available_rc_balance")));
                                        rcbNum = data.optString("available_rc_balance");
                                        relativeRcBalance.setVisibility(View.VISIBLE);
                                        if ((data.optString("member_paypwd").equals("null") || data.optString("member_paypwd").equals("")) && linearNoPaypwd != null) {
                                            linearNoPaypwd.setVisibility(View.VISIBLE);
                                        }
                                    } else {
                                        relativeRcBalance.setVisibility(View.GONE);
                                    }

                            }
                        } catch (JSONException e) {
                            // TODO Auto-generated catch block
                            Log.i("zjz", e.toString());
                            Log.i("zjz", msg);
                            e.printStackTrace();
                        }finally {
                            ProgressDlgUtil.stopProgressDlg();
                        }
                    }
                });

            }
        });
    }

    private void initMyBalance() {
        ProgressDlgUtil.showProgressDlg("Loading...",this);
        HttpRequest.sendGet(TLUrl.URL_yyg_my_balance, "userId=" + MyApplication.getInstance().self.getId(),
                new HttpRevMsg() {

                    @Override
                    public void revMsg(final String msg) {
                        Log.i("zjz", "yyg_my_code_msg=" + msg);
                        handler.post(new Runnable() {

                            @Override
                            public void run() {
                                if (msg.length() == 0) {
                                    showToast("请求失败,请重试");
                                    return;
                                }
                                try {
                                    JSONObject result = new JSONObject(msg);
                                    if (result.optInt("status") == 1) {
                                        JSONObject jsonObject = result.optJSONObject("msg");
                                        if (jsonObject != null) {
                                            yygBalance = jsonObject.optDouble("userMoney");
                                            Log.i("zjz", "yygBalance=" + yygBalance);
                                            if (yygBalance != 0) {
                                                if (relativeBalance != null)
                                                    relativeBalance.setVisibility(View.VISIBLE);
                                                if (tBalance != null)
                                                    tBalance.setText(NumberUtils.formatPrice(yygBalance));
                                            }

                                        }
                                    } else if(result.optInt("status")==-1){
                                        Intent intent=new Intent(PayWayActivity.this,LoginActivity.class);
                                        intent.putExtra("desc",result.optString("msg"));
                                        startActivity(intent);
                                        showToast(result.optString("msg"));
                                    }else if(result.optInt("status")==0){
                                        showToast(result.optString("msg"));
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                } finally {
                                    ProgressDlgUtil.stopProgressDlg();
                                }

                            }
                        });
                    }
                });
    }

    private void setOnListener() {
        relativeBack.setOnClickListener(this);
        relativeWx.setOnClickListener(this);
        relativeZfb.setOnClickListener(this);
        relativeYl.setOnClickListener(this);
        relativeBalance.setOnClickListener(this);
        tvPay.setOnClickListener(this);
        tUse.setOnClickListener(this);
        tSetPaypwd.setOnClickListener(this);
        relativeRcBalance.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Intent intent=null;
        switch (v.getId()) {
            case R.id.relative_back:
                finish();
                break;
            case R.id.relative_wx:
                isWX = !isWX;
                type = isWX ? WX : null;
                isZFB = false;
                isYH = false;
                wxCheck.setChecked(isWX);
                yhCheck.setChecked(isYH);
                zfbCheck.setChecked(isZFB);
                break;
            case R.id.relative_zfb:
                isZFB = !isZFB;
                type = isZFB ? ZFB : null;
                isYH = false;
                isWX = false;
                wxCheck.setChecked(isWX);
                yhCheck.setChecked(isYH);
                zfbCheck.setChecked(isZFB);
//                if (isYungou) {
//                    yungouPay();
//                } else {
//                }
                break;
            case R.id.relative_yl:
                isYH = !isYH;
                type = isYH ? YH : null;
                isZFB = false;
                isWX = false;
                wxCheck.setChecked(isWX);
                yhCheck.setChecked(isYH);
                zfbCheck.setChecked(isZFB);
                break;

            case R.id.tv_pay:
                isPay=false;
                pay();
                break;
            case R.id.relative_balance:
                isYE = !isYE;
                balanceCheck.setChecked(isYE);
                if (isYE) {
                    linearNeed.setVisibility(View.VISIBLE);
                    if (total <= yygBalance) {
                        needMore = false;
                        tNeed.setText(NumberUtils.formatPrice(0));
                    } else {
                        needMore = true;
                        tNeed.setText(NumberUtils.formatPrice((total - yygBalance)));
                    }
                } else {
                    linearNeed.setVisibility(View.GONE);
                    needMore = false;
                }
                break;
            case R.id.t_use:
                confirmPwd();
                break;
            case R.id.t_set_paypwd:
                if (!isBindEmail || isBindPhone) {
                    startActivity(new Intent(PayWayActivity.this, NoticeDialogActivity.class).putExtra("msg",
                            "您还未绑定手机或邮箱，请先到个人界面->更多中进行手机或邮箱的绑定！"));
                } else {
                    intent = new Intent(this, BindPhoneActivity.class);
                    intent.putExtra("isFirst", true);
                    intent.putExtra("title", "设置支付密码");
                    intent.putExtra("type", BindPhoneActivity.BINDPAYPWD);
                    startActivity(intent);
                }
                break;
            case R.id.relative_rc_balance:
                isRC=!isRC;
                if(isRC){
                    rcBalanceCheck.setChecked(true);
                    linearPayPdw.setVisibility(View.VISIBLE);
                }else {
                    rcBalanceCheck.setChecked(false);
                    linearPayPdw.setVisibility(View.GONE);
                }
                break;
        }
    }


    private void confirmPwd() {
        if (edPwd.getText().toString().equals("")) {
            showToast("请输入支付密码！");
            return;
        }
        ProgressDlgUtil.showProgressDlg("Loading...", this);
        HttpRequest.sendPost(TLUrl.URL_hwg_confirm_pwd, "&key=" + MyApplication.getInstance().getMykey() + "&password=" + edPwd.getText().toString(), new HttpRevMsg() {
            @Override
            public void revMsg(final String msg) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            JSONObject object = new JSONObject(msg);
                            if (object.getInt("code") == 200) {
                                Log.i("zjz", "msg_paypwd=" + msg);
                                if (object.optInt("datas") == 1) {
                                    new PromptDialog(PayWayActivity.this, "确认使用充值卡余额?(此操作无法恢复)", new Complete() {
                                        @Override
                                        public void complete() {
                                            useRcBalance();
                                        }
                                    }).show();
                                } else {
                                    showToast("密码错误！");
                                }
                            }
                        } catch (JSONException e) {
                            // TODO Auto-generated catch block
                            Log.i("zjz", e.toString());
                            Log.i("zjz", msg);
                            e.printStackTrace();
                        }finally {
                            ProgressDlgUtil.stopProgressDlg();
                        }
                    }
                });

            }
        });
    }

    private void useRcBalance() {
        HttpRequest.sendGet(TLUrl.URL_hwg_head, "act=test_cy&op=edit_order_rcb"+"&order_sn=" + pay_sn+ "&rcb_amount=" + rcbNum, new HttpRevMsg() {
            @Override
            public void revMsg(final String msg) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            JSONObject object = new JSONObject(msg);
                            Log.i("zjz", "use_rcb=" + msg);
                            if (object.getInt("code") == 200) {
                                if(object.has("datas")&&tGoodsTotalMoney!=null&&linearPayPdw!=null){
                                    if(object.optString("datas").contains("success")){
                                        MyUpdateUI.sendUpdateCollection(PayWayActivity.this, MyUpdateUI.ORDER);
                                        finish();
                                    }else {
                                        tGoodsTotalMoney.setText(NumberUtils.formatPrice(object.optDouble("datas")));
                                        linearPayPdw.setVisibility(View.GONE);
                                        MyUpdateUI.sendUpdateCollection(PayWayActivity.this, MyUpdateUI.ORDER);
                                        initUser();
                                    }
                                }
                            }
                        } catch (JSONException e) {
                            // TODO Auto-generated catch block
                            Log.i("zjz", e.toString());
                            Log.i("zjz", msg);
                            e.printStackTrace();
                        } finally {
                            ProgressDlgUtil.stopProgressDlg();
                        }
                    }
                });

            }
        });
    }

    private void pay() {
        tvPay.setEnabled(false);
        if (isYungou) {
            if (isYE && needMore) {
                yygBalancePay();
            } else {
                yungouPay();
            }
        } else if (type == null) {
            showToast("请选择支付方式！");
        } else if (type.equals(WX)) {
//            String url = "http://www.huaqiaobang.com/mobile/index.php?act=member_payment&op=pay" +
//                    "&key=" + MyApplication.getInstance().getMykey() +
//                    "&pay_sn=" + pay_sn +
//                    "&payment_code=wxpay";
//            Intent intent = new Intent(this, FuyouPayActivity.class);
//            intent.putExtra("id", "1");
//            intent.putExtra("url", url);
//            startActivity(intent);
//            finish();
            weixinPay();
        } else if (type.equals(YH)) {
            fuyouPay();
        } else if (type.equals(ZFB)) {
            String url = "http://www.huaqiaobang.com/mobile/index.php?act=member_payment&op=pay" +
                    "&key=" + MyApplication.getInstance().getMykey() +
                    "&pay_sn=" + pay_sn +
                    "&payment_code=alipay";
            Log.i("zjz", "pay_url=" + url);
            Intent intent = new Intent(this, FuyouPayActivity.class);
            intent.putExtra("id", "1");
            intent.putExtra("url", url);
            startActivity(intent);
            finish();
        }

    }


    private void fuyouPay() {
        ProgressDlgUtil.showProgressDlg("", this);
        Log.i("zjz", "fuyou_url=" + TLUrl.URL_hwg_fuyou_pay + "&key=" + MyApplication.getInstance().getMykey() + "&pay_sn=" + pay_sn + "&payment_code=fuyou" + "&uid=" + MyApplication.getInstance().self.getId());

        JsonObjectRequest jr = new JsonObjectRequest(Request.Method.GET, TLUrl.URL_hwg_fuyou_pay + "&key=" + MyApplication.getInstance().getMykey() + "&pay_sn=" + pay_sn + "&payment_code=fuyou" + "&uid=" + MyApplication.getInstance().self.getId(), null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    Log.i("zjz", "fuyou_msg=" + response);
                    if (response.getInt("status") == 1) {
                        Log.i("zjz", "成功跳转到富友支付");
                        String url = response.optString("msg");
                        Log.i("zjz", "pay_url=" + url);
                        Intent intent = new Intent(PayWayActivity.this, FuyouPayActivity.class);
                        intent.putExtra("id", "1");
                        intent.putExtra("url", url);
                        startActivity(intent);
                        finish();
                    } else {
                        Log.i("zjz", "解析失败");
                    }

                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    Log.i("zjz", e.toString());
                    e.printStackTrace();
                } finally {
                    if(tvPay!=null&&!isPay)
                        tvPay.setEnabled(true);
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

    private void weixinPay() {
        Log.i("zjz", "weixin_url=" + TLUrl.URL_hwg_fuyou_pay + "&key=" + MyApplication.getInstance().getMykey() + "&pay_sn=" + pay_sn + "&payment_code=weixin" + "&uid=" + MyApplication.getInstance().self.getId());
        JsonObjectRequest jr = new JsonObjectRequest(Request.Method.GET, TLUrl.URL_hwg_fuyou_pay + "&key=" + MyApplication.getInstance().getMykey() + "&pay_sn=" + pay_sn + "&payment_code=weixin" + "&uid=" + MyApplication.getInstance().self.getId(), null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    Log.i("zjz", "weixin_msg=" + response);
                    if (response.getInt("status") == 1) {
                        isPay=true;
                        Log.i("zjz", "成功跳转到微信支付");
                        JSONObject json = response.getJSONObject("msg");
                        PayReq req = new PayReq();
                        req.appId = json.getString("appid");
                        req.partnerId = json.getString("partnerid");
                        req.prepayId = json.getString("prepayid");
                        req.nonceStr = json.getString("noncestr");
                        req.timeStamp = json.getString("timestamp");
                        req.packageValue = json.getString("package");
                        req.sign = json.getString("sign");
                        req.extData = "app data"; // optional
                        Log.i("zjz", "req=" + req);
                        iwxapi.sendReq(req);
                        finish();
                    } else {
                        Log.i("zjz", "解析失败");
                        showToast("微信支付请求失败！请重试");
                    }

                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    Log.i("zjz", e.toString());
                    e.printStackTrace();
                } finally {
                    if(tvPay!=null&&!isPay)
                        tvPay.setEnabled(true);
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

    private void yygBalancePay() {
        String payType = "";
        if (type == null) {
            showToast("一元幸运余额不足，请选择第三方支付！");
            return;
        } else if (type.equals(ZFB)) {
            payType = "0";
        } else if (type.equals(WX)) {
            payType = "1";
        }
        ProgressDlgUtil.showProgressDlg("Loading...", this);
        HttpRequest.sendPost(TLUrl.URL_yyg_pay, "userId=" + MyApplication.getInstance().self.getId() + "&activityId=" + activity_id + "&buyNum=" + num + "&payType=" + payType + "&virtualMoney=" + yygBalance, new HttpRevMsg() {
            @Override
            public void revMsg(final String msg) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Log.i("zjz", "balance_msg" + msg);
                            JSONObject object = new JSONObject(msg);
                            if (object.getInt("status") == 1) {
                                isPay=true;
                                if (object.has("payInfo")) {
                                    Log.i("zjz", "成功跳转到支付宝界面");
                                    //跳转至支付宝
                                    AliPay.getInstance().pay(object.getString("payInfo"));
                                    finish();
                                } else {
                                    Log.i("zjz", "成功跳转到微信支付");
                                    JSONObject json = object.getJSONObject("msg");
                                    PayReq req = new PayReq();
                                    req.appId = json.getString("appid");
                                    req.partnerId = json.getString("partnerid");
                                    req.prepayId = json.getString("prepayid");
                                    req.nonceStr = json.getString("noncestr");
                                    req.timeStamp = json.getString("timestamp");
                                    req.packageValue = json.getString("package");
                                    req.sign = json.getString("sign");
                                    req.extData = "app data"; // optional
                                    Log.i("zjz", "req=" + req);
                                    iwxapi.sendReq(req);
                                    finish();
                                }
                            } else if (object.getInt("status") == -1) {
                                showToast(object.optString("msg"));
                                Intent intent=new Intent(PayWayActivity.this,LoginActivity.class);
                                intent.putExtra("desc",object.optString("msg"));
                                startActivity(intent);
                            }else if(object.getInt("status")==0){
                                showToast(object.optString("msg"));
                            }

                        } catch (JSONException e) {
                            // TODO Auto-generated catch block
                            Log.i("zjz", e.toString());
                            e.printStackTrace();
                        } finally {
                            ProgressDlgUtil.stopProgressDlg();
                            if(tvPay!=null&&!isPay)
                                tvPay.setEnabled(true);
                        }
                    }
                });
            }
        });
    }


    private void yungouPay() {

        String payType = "";
        if (isYE && !needMore) {
            payType = "2";
        } else if (type == null) {
            showToast("请选择支付方式！");
            return;
        } else if (type.equals(ZFB)) {
            payType = "0";
        } else if (type.equals(WX)) {
            payType = "1";
        }
        ProgressDlgUtil.showProgressDlg("Loading...", this);
        HttpRequest.sendPost(TLUrl.URL_yyg_pay, "userId=" + MyApplication.getInstance().self.getId() + "&activityId=" + activity_id + "&buyNum=" + num + "&payType=" + payType, new HttpRevMsg() {
            @Override
            public void revMsg(final String msg) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Log.i("zjz", "yungou_msg" + msg);
                            JSONObject object = new JSONObject(msg);
                            if (object.getInt("status") == 1) {
                                isPay=true;
                                if (object.optString("msg").contains("幸运币支付成功")) {
                                    showToast("余额支付成功！");
                                    MyUpdateUI.sendUpdateCollection(PayWayActivity.this, MyUpdateUI.YYGBUY);
                                    finish();
                                } else if (object.has("payInfo")) {
                                    Log.i("zjz", "成功跳转到支付宝界面");
                                    //跳转至支付宝
                                    AliPay.getInstance().pay(object.getString("payInfo"));
                                    finish();
                                } else {
                                    Log.i("zjz", "成功跳转到微信支付");
                                    JSONObject json = object.getJSONObject("msg");
                                    PayReq req = new PayReq();
                                    req.appId = json.getString("appid");
                                    req.partnerId = json.getString("partnerid");
                                    req.prepayId = json.getString("prepayid");
                                    req.nonceStr = json.getString("noncestr");
                                    req.timeStamp = json.getString("timestamp");
                                    req.packageValue = json.getString("package");
                                    req.sign = json.getString("sign");
                                    req.extData = "app data"; // optional
                                    Log.i("zjz", "req=" + req);
                                    iwxapi.sendReq(req);
                                    finish();
                                }

                            } else if (object.getInt("status") == -1) {
                                showToast(object.optString("msg"));
                                Intent intent=new Intent(PayWayActivity.this,LoginActivity.class);
                                intent.putExtra("desc",object.optString("msg"));
                                startActivity(intent);
                            }else if(object.getInt("status")==0){
                                showToast(object.optString("msg"));
                            }

                        } catch (JSONException e) {
                            // TODO Auto-generated catch block
                            Log.i("zjz", e.toString());
                            e.printStackTrace();
                        } finally {
                            if(tvPay!=null&&!isPay)
                                tvPay.setEnabled(true);
                            ProgressDlgUtil.stopProgressDlg();
                        }
                    }
                });
            }
        });
    }

}
