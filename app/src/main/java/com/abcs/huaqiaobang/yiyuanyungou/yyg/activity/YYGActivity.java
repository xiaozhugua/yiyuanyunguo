package com.abcs.huaqiaobang.yiyuanyungou.yyg.activity;

import android.content.ComponentName;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;


import com.abcs.huaqiaobang.yiyuanyungou.BaseFragmentActivity;
import com.abcs.huaqiaobang.yiyuanyungou.MyApplication;
import com.abcs.huaqiaobang.yiyuanyungou.R;
import com.abcs.huaqiaobang.yiyuanyungou.broadcast.MyBroadCastReceiver;
import com.abcs.huaqiaobang.yiyuanyungou.broadcast.MyUpdateUI;
import com.abcs.huaqiaobang.yiyuanyungou.util.HttpRequest;
import com.abcs.huaqiaobang.yiyuanyungou.util.HttpRevMsg;
import com.abcs.huaqiaobang.yiyuanyungou.util.TLUrl;
import com.abcs.huaqiaobang.yiyuanyungou.wxapi.LoginResultReceiver;
import com.abcs.huaqiaobang.yiyuanyungou.wxapi.WXEntryActivity;
import com.abcs.huaqiaobang.yiyuanyungou.yyg.fragment.MyYYGFragment;
import com.abcs.huaqiaobang.yiyuanyungou.yyg.fragment.YYGGoodsFragment;
import com.abcs.huaqiaobang.yiyuanyungou.yyg.fragment.YYGLotteryFragment;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class YYGActivity extends BaseFragmentActivity {

    public static final String GOODS = "goods";
    public static final String LOTTERY = "lottery";
    @InjectView(R.id.seperate)
    View seperate;
    @InjectView(R.id.relative_back)
    RelativeLayout relativeBack;
    @InjectView(R.id.rb_goods)
    RadioButton rbGoods;
    @InjectView(R.id.rb_lottery)
    RadioButton rbLottery;
    @InjectView(R.id.content)
    FrameLayout content;
    @InjectView(R.id.radioGroup)
    RadioGroup radioGroup;
    @InjectView(R.id.relative_my)
    RelativeLayout relativeMy;
    @InjectView(R.id.rb_my)
    RadioButton rbMy;

    private FragmentManager manager;
    private FragmentTransaction transaction;
    private YYGGoodsFragment goodsFragment;
    private YYGLotteryFragment lotteryFragment;
    public MyYYGFragment myYYGFragment;
    public String type;

    boolean isBind;
    MyBroadCastReceiver myBroadCastReceiver;
    private LoginResultReceiver resultReceiver;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.hwg_activity_yygacitivity);
        ButterKnife.inject(this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().setFlags(
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            seperate.setVisibility(View.VISIBLE);
        }
        init();
        myBroadCastReceiver=new MyBroadCastReceiver(this,updateUI);
        myBroadCastReceiver.register();
        manager = getSupportFragmentManager();
        relativeMy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent = null;
//                if (MyApplication.getInstance().self == null) {
//                    intent = new Intent(YYGActivity.this, WXEntryActivity.class);
//                } else {
//                    intent = new Intent(YYGActivity.this, MyYYGActivity.class);
//                }
//                startActivity(intent);
//                new ShowMessageDialog(v,YYGActivity.this, Util.WIDTH * 4 / 5
//                        ,getResources().getString(R.string.yyg_rule),"一元幸运购说明");
                Intent intent=new Intent(YYGActivity.this,YYGMessageActivity.class);
                startActivity(intent);
            }
        });
        relativeBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        initRadioGroup();
        initNum();

    }
    private ServiceConnection conn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {

        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };
    private void init() {
        Intent intent = new Intent("com.abct.occft.hq.notifychart");
        intent.setPackage(getPackageName());
        bindService(intent, conn, BIND_AUTO_CREATE);
        resultReceiver = new LoginResultReceiver(this);
        IntentFilter filter = new IntentFilter();
        filter.addAction("com.abct.occft.hq.login");
        registerReceiver(resultReceiver, filter);
        isBind = true;
    }

    MyBroadCastReceiver.UpdateUI updateUI=new MyBroadCastReceiver.UpdateUI() {
        @Override
        public void updateShopCar(Intent intent) {

        }

        @Override
        public void updateCarNum(Intent intent) {

        }

        @Override
        public void updateCollection(Intent intent) {
            if (intent.getStringExtra("type").equals(MyUpdateUI.YYGBUY)) {
                Log.i("zjz", "YYG支付成功");
                initNum();
            }
        }

        @Override
        public void update(Intent intent) {

        }
    };
    private void initNum() {
        HttpRequest.sendGet(TLUrl.URL_yyg_lottery_num, null,
                new HttpRevMsg() {

                    @Override
                    public void revMsg(final String msg) {
                        Log.i("zjz", "yyg_lottery_msg=" + msg);
                        handler.post(new Runnable() {

                            @Override
                            public void run() {
                                if (msg.length() == 0) {
//                                    showToast("请求失败,请重试");
                                    return;
                                }
                                try {
                                    JSONObject result = new JSONObject(msg);
                                    if (result.optInt("status") == 1) {
                                        JSONObject jsonObject = result.optJSONObject("msg");
                                        if (rbLottery != null)
                                            rbLottery.setText("揭晓" + "(" + jsonObject.optString("actConut0") + ")");
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                            }
                        });
                    }
                });
    }

    private void initRadioGroup() {
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                transaction = manager.beginTransaction();
                hintFragment();
                switch (checkedId) {
                    case R.id.rb_goods:
                        type = GOODS;
                        if (goodsFragment == null) {
                            goodsFragment = new YYGGoodsFragment();
                            transaction.add(R.id.content, goodsFragment);
                        } else {
                            transaction.show(goodsFragment);
                        }
                        rbGoods.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
                        break;
                    case R.id.rb_lottery:
                        type = LOTTERY;
                        if (lotteryFragment == null) {
                            lotteryFragment = new YYGLotteryFragment();
                            transaction.add(R.id.content, lotteryFragment);
                        } else {
                            transaction.show(lotteryFragment);
                        }
                        rbLottery.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
                        break;
                    case R.id.rb_my:
                        if (MyApplication.getInstance().self == null) {
                            Intent intent = new Intent(YYGActivity.this, WXEntryActivity.class);
                            startActivity(intent);
                            if (type.equals(GOODS)) {
                                ((RadioButton) radioGroup.findViewById(R.id.rb_goods)).setChecked(true);
                            } else if (type.equals(LOTTERY)) {
                                ((RadioButton) radioGroup.findViewById(R.id.rb_lottery)).setChecked(true);
                            }
                            return;
                        }

                        if (myYYGFragment == null) {
                            myYYGFragment = new MyYYGFragment();
                            transaction.add(R.id.content, myYYGFragment);
                        } else {
                            transaction.show(myYYGFragment);
                        }

                        rbMy.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
                        break;
                }
                transaction.commit();
            }
        });
        ((RadioButton) radioGroup.findViewById(R.id.rb_goods)).setChecked(true);
        type = GOODS;
    }

    private void hintFragment() {

        rbGoods.setTextColor(getResources().getColor(R.color.white));
        rbLottery.setTextColor(getResources().getColor(R.color.white));
        rbMy.setTextColor(getResources().getColor(R.color.white));
        if (goodsFragment != null)
            transaction.hide(goodsFragment);
        if (lotteryFragment != null)
            transaction.hide(lotteryFragment);
        if (myYYGFragment != null)
            transaction.hide(myYYGFragment);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.reset(this);
        if (isBind) {
            unbindService(conn);
            unregisterReceiver(resultReceiver);
        }
        myBroadCastReceiver.unRegister();
    }
}
