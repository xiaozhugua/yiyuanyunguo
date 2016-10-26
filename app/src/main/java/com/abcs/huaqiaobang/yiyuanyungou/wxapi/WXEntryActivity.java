package com.abcs.huaqiaobang.yiyuanyungou.wxapi;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.abcs.huaqiaobang.yiyuanyungou.BaseActivity;
import com.abcs.huaqiaobang.yiyuanyungou.MyApplication;
import com.abcs.huaqiaobang.yiyuanyungou.R;
import com.abcs.huaqiaobang.yiyuanyungou.activity.ForgetPwdActivity;
import com.abcs.huaqiaobang.yiyuanyungou.dialog.NoticeDialog;
import com.abcs.huaqiaobang.yiyuanyungou.dialog.ProgressDlgUtil;
import com.abcs.huaqiaobang.yiyuanyungou.util.HttpRequest;
import com.abcs.huaqiaobang.yiyuanyungou.util.HttpRevMsg;
import com.abcs.huaqiaobang.yiyuanyungou.util.ServerUtils;
import com.abcs.huaqiaobang.yiyuanyungou.util.TLUrl;
import com.abcs.huaqiaobang.yiyuanyungou.util.Util;
import com.abcs.huaqiaobang.yiyuanyungou.wxapi.loginmodule.TencentAuthHandler;
import com.abcs.huaqiaobang.yiyuanyungou.wxapi.loginmodule.imp.HttpCallbackListener;
import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.modelmsg.SendAuth;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * Created by zhou on 2016/5/16.
 */
public class WXEntryActivity extends BaseActivity implements IWXAPIEventHandler {

    @InjectView(R.id.et_username)
    EditText etUsername;
    @InjectView(R.id.et_pwd)
    EditText etPwd;

    public static Activity other;
    @InjectView(R.id.t_cancel)
    TextView tCancel;
    @InjectView(R.id.t_email)
    TextView tEmail;
    @InjectView(R.id.t_phone)
    TextView tPhone;
    @InjectView(R.id.relative_bottom)
    RelativeLayout relativeBottom;
    @InjectView(R.id.img_clear_uname)
    ImageView imgClearUname;
    @InjectView(R.id.img_clear_upwd)
    ImageView imgClearUpwd;
    private TencentAuthHandler mTencentAuthHandler;
    private IWXAPI api;
    public static String appid = "wx9906731d4fadfdaa";
    private boolean isLogin = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.inject(this);
        api = WXAPIFactory.createWXAPI(this, appid, false);
        api.registerApp(appid);

        api.handleIntent(getIntent(), this);
        // HttpRequest.sendGet("http://120.24.214.108:3000/api/logout/wechat",
        // "");
        Thread.setDefaultUncaughtExceptionHandler(new CustomExceptionHandler());
        mTencentAuthHandler = new TencentAuthHandler(this);
        mTencentAuthHandler.logout(this);
        initUI();
        initTextChange();
    }

    private void initTextChange() {
        imgClearUname.setVisibility(etUsername.getText().toString().length()!=0?View.VISIBLE:View.INVISIBLE);
        imgClearUpwd.setVisibility(etPwd.getText().toString().length()!=0?View.VISIBLE:View.INVISIBLE);
        etUsername.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().length() == 0) {
                    imgClearUname.setVisibility(View.INVISIBLE);
                }else {
                    imgClearUname.setVisibility(View.VISIBLE);
                }
            }
        });
        etPwd.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().length() == 0) {
                    imgClearUpwd.setVisibility(View.INVISIBLE);
                }else {
                    imgClearUpwd.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    private void initUI() {
        Log.i("zjz", "打开");
        if (Util.preference != null
                && Util.preference.getString("lizai_userName", "").length() > 0) {
            etUsername.setText(Util.preference.getString("lizai_userName", ""));
        }
    }

    @OnClick({R.id.img_login_qq, R.id.img_login_wx, R.id.rl_register,
            R.id.rl_back, R.id.btn_login, R.id.tljr_txt_wjmm,
            R.id.t_cancel, R.id.t_phone, R.id.t_email, R.id.relative_bottom,
            R.id.img_clear_uname, R.id.img_clear_upwd
    })
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.img_login_qq:
                Util.isThirdLogin = true;
//                sendQQ();
                break;
            case R.id.img_login_wx:
//                MobclickAgent.onEvent(this, "WXLogin");
//                if (!api.isWXAppInstalled()) {
//                    showToast("请先安装微信");
//                    return;
//                }
//                ProgressDlgUtil.showProgressDlg("登录中",
//                        this);
//                isLogin = true;
//                SendAuth.Req req = new SendAuth.Req();
//                req.scope = "snsapi_userinfo";
//                req.state = "text";
//                Util.isThirdLogin = true;
//                other = this;
//                api.sendReq(req);
                break;
            case R.id.rl_register:
                startActivity(new Intent(this, RegisterActivity.class));
                break;
            case R.id.rl_back:
                finish();
                break;
            case R.id.tljr_txt_wjmm:
                forgetPwd();
//                wanJiMiMa();
                break;
            case R.id.btn_login:
                if (etUsername.getText().toString().trim().equals("")
                        || etPwd.getText().toString().trim().equals("")) {
                    showToast("请输入用户名或密码");
                } else {
                    Util.isThirdLogin = false;
                    Util.preference.edit().putString("logintype", "").commit();
                    sendLogin();
                }
                break;
            case R.id.t_cancel:
                relativeBottom.setVisibility(View.GONE);
                break;
            case R.id.t_phone:
                findpwd(false);
                relativeBottom.setVisibility(View.GONE);
                break;
            case R.id.t_email:
                findpwd(true);
                relativeBottom.setVisibility(View.GONE);
                break;
            case R.id.relative_bottom:
                view.setVisibility(View.GONE);
                break;
            case R.id.img_clear_uname:
                etUsername.getText().clear();
                break;
            case R.id.img_clear_upwd:
                etPwd.getText().clear();
                break;
        }
    }

    protected void wanJiMiMa() {
        final String userName = etUsername.getText().toString().trim();
        final String method = "email";
        if (userName.length() <= 0) {
            showToast("请输入你要找回密码的帐号");
            etUsername.requestFocus();
            return;
        }

        ProgressDlgUtil.showProgressDlg("", this);

        HttpRequest.sendPost(TLUrl.URL_getbindinfo, "uname=" + userName,
                new HttpRevMsg() {

                    @Override
                    public void revMsg(final String msg) {
                        System.err.println("msg:" + msg);
                        handler.post(new Runnable() {

                            @Override
                            public void run() {
                                ProgressDlgUtil.stopProgressDlg();
                                if (msg.length() == 0) {
                                    showToast("请求失败,请检查网络重试");
                                    return;
                                }
                                com.alibaba.fastjson.JSONObject result = com.alibaba.fastjson.JSONObject
                                        .parseObject(msg);
                                int code = result.getIntValue("code");
                                if (code == 1) {
                                    com.alibaba.fastjson.JSONObject obj = result
                                            .getJSONObject("result");
                                    if (obj.containsKey("email")) {
                                        String email = obj.getString("email");
                                        if (obj.getBooleanValue("emailVerify")) {
                                            // 已验证
                                            final View view = WXEntryActivity.this
                                                    .getLayoutInflater()
                                                    .inflate(
                                                            R.layout.occft_dialog_userpwd,
                                                            null);
                                            ((TextView) view
                                                    .findViewById(R.id.tljr_wangji_email))
                                                    .setText(email);
                                            AlertDialog.Builder builder = new AlertDialog.Builder(
                                                    WXEntryActivity.this);
                                            builder.setTitle("找回密码")
                                                    .setIcon(
                                                            android.R.drawable.ic_dialog_info)
                                                    .setView(view)
                                                    .setPositiveButton("取消",
                                                            null);

                                            builder.setNegativeButton(
                                                    "通过邮箱找回",
                                                    new DialogInterface.OnClickListener() {
                                                        public void onClick(
                                                                final DialogInterface dialog,
                                                                int which) {
                                                            ProgressDlgUtil
                                                                    .showProgressDlg(
                                                                            "找回密码中",
                                                                            WXEntryActivity.this);
                                                            HttpRequest
                                                                    .sendPost(
                                                                            TLUrl.URL_forgotpwd,
                                                                            "company=huaqiao&uname="
                                                                                    + userName
                                                                                    + "&method="
                                                                                    + method,
                                                                            new HttpRevMsg() {
                                                                                @Override
                                                                                public void revMsg(
                                                                                        String msg) {
                                                                                    ProgressDlgUtil
                                                                                            .stopProgressDlg();
                                                                                    if (msg.length() == 0) {
                                                                                        showToast("找回密码失败");
                                                                                        return;
                                                                                    } else {
                                                                                        com.alibaba.fastjson.JSONObject result = com.alibaba.fastjson.JSONObject
                                                                                                .parseObject(msg);
                                                                                        int code = result
                                                                                                .getIntValue("code");
                                                                                        if (code == 1) {
                                                                                            String _result = result
                                                                                                    .getString("result");
                                                                                            showToast(_result);
                                                                                        } else {
                                                                                            showToast(code);
                                                                                        }
                                                                                    }
                                                                                }
                                                                            });
                                                        }
                                                    });
                                            builder.show();
                                        } else {
                                            // 未验证
                                            showToast("您的邮箱未通过验证,无法找回密码");
                                        }
                                    } else {
                                        showToast("您未绑定邮箱,如需找回密码,请联系客服!");
                                    }
                                } else {
                                    showToast(code);
                                }

                            }
                        });
                    }
                });

    }

    Handler handler = new Handler();


    private JSONObject jsonObject;

    protected void forgetPwd() {
        final String userName = etUsername.getText().toString().trim();
        if (userName.length() <= 0) {
            showToast("请输入你要找回密码的帐号");
            etUsername.requestFocus();
            return;
        }
        ProgressDlgUtil.showProgressDlg("", this);

        HttpRequest.sendPost(TLUrl.URL_getbindinfo, "uname=" + userName,
                new HttpRevMsg() {

                    @Override
                    public void revMsg(final String msg) {
                        System.err.println("msg:" + msg);
                        handler.post(new Runnable() {

                            @Override
                            public void run() {
                                ProgressDlgUtil.stopProgressDlg();
                                if (msg.length() == 0) {
                                    showToast("请求失败,请检查网络重试");
                                    return;
                                }

                                try {
                                    JSONObject object = new JSONObject(msg);
                                    int code = object.optInt("code");
                                    if (code == 1) {
                                        jsonObject = object.getJSONObject("result");
                                        if (jsonObject.has("email") && jsonObject.has("phone")) {
                                            if (jsonObject.optBoolean("emailVerify") && jsonObject.optBoolean("phoneVerify")) {
                                                relativeBottom.setVisibility(View.VISIBLE);
                                            } else if (jsonObject.optBoolean("emailVerify") && !jsonObject.optBoolean("phoneVerify")) {
                                                //通过邮箱找回
                                                findpwd(true);
                                            } else if (!jsonObject.optBoolean("emailVerify") && jsonObject.optBoolean("phoneVerify")) {
                                                //通过手机找回
                                                findpwd(false);
                                            } else {
                                                // 未验证
                                                showToast("您的邮箱和手机皆未通过验证,无法找回密码");
                                            }
                                        } else if (jsonObject.has("email")) {
                                            //通过邮箱找回
                                            findpwd(true);
                                        } else if (jsonObject.has("phone")) {
                                            //通过手机找回
                                            findpwd(false);
                                        } else {
                                            showToast("未绑定邮箱、手机或者三方登录,如需找回密码,请联系客服!");
                                        }

                                    } else {
                                        showToast(code);
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
//                                com.alibaba.fastjson.JSONObject result = com.alibaba.fastjson.JSONObject.parseObject(msg);
//                                int code = result.getIntValue("code");
//                                if (code == 1) {
//                                    com.alibaba.fastjson.JSONObject obj = result.getJSONObject("result");
//                                    if (obj.containsKey("email") && obj.containsKey("phone")) {
//                                        if (obj.getBooleanValue("emailVerify") && obj.getBooleanValue("phoneVerify")) {
//                                            relativeBottom.setVisibility(View.VISIBLE);
//                                        } else if (obj.getBooleanValue("emailVerify") && !obj.getBooleanValue("phoneVerify")) {
//                                            //通过邮箱找回
//                                            findpwd(true, obj);
//                                        } else if (!obj.getBooleanValue("emailVerify") && obj.getBooleanValue("phoneVerify")) {
//                                            //通过手机找回
//                                            findpwd(false, obj);
//                                        } else {
//                                            // 未验证
//                                            showToast("您的邮箱和手机皆未通过验证,无法找回密码");
//                                        }
//                                    } else if (obj.containsKey("email")) {
//                                        //通过邮箱找回
//                                        findpwd(true, obj);
//                                    } else if (obj.containsKey("phone")) {
//                                        //通过手机找回
//                                        findpwd(false, obj);
//                                    } else {
//                                        showToast("未绑定邮箱、手机或者三方登录,如需找回密码,请联系客服!");
//                                    }
//
//                                } else {
//                                    showToast(code);
//                                }

                            }
                        });
                    }
                });
    }

    public void findpwd(boolean isemail) {
        try {
            if (isemail) {
                String email = jsonObject.optString("email");
                if (jsonObject.optBoolean("emailVerify")) {
                    Intent intent = new Intent(WXEntryActivity.this, ForgetPwdActivity.class);
                    intent.putExtra("userName", etUsername.getText().toString().trim());
                    intent.putExtra("method", "email");
                    intent.putExtra("email", email);
                    startActivity(intent);
                } else {
                    // 未验证
                    showToast("您的邮箱未通过验证,无法找回密码");
                }
            } else {
                String phone = jsonObject.optString("phone");
                if (jsonObject.optBoolean("phoneVerify")) {
                    Intent intent = new Intent(WXEntryActivity.this, ForgetPwdActivity.class);
                    intent.putExtra("userName", etUsername.getText().toString().trim());
                    intent.putExtra("method", "phone");
                    intent.putExtra("phone", phone);
                    startActivity(intent);
                } else {
                    // 未验证
                    showToast("您的手机未通过验证,无法找回密码");
                }
            }
        } catch (Exception e) {

        }
    }

    private void sendLogin() {
        if (!ServerUtils.isConnect(this)) {
            handler.post(new Runnable() {

                @Override
                public void run() {
                    // TODO Auto-generated method stub
                    showToast("登录失败,请检查您的网络");
                }
            });
            return;
        }
        NoticeDialog.showNoticeDlg("登录中", this);
        String param = "uname=" + etUsername.getText().toString().trim() + "&upass="
                + etPwd.getText().toString().trim() + "&from=华侨帮";
        HttpRequest.sendPost(TLUrl.URL_login, param, new HttpRevMsg() {
            @Override
            public void revMsg(String msg) {
                // TODO Auto-generated method stub

                if (msg.length() == 0) {
                    showToast("服务器异常，请稍后再试");
                    return;
                }
                try {
                    JSONObject json = new JSONObject(msg);
                    if (json != null && json.has("code")) {
                        int code = json.getInt("code");
                        if (code == 1) {
                            String token = json.getString("result");
                            Util.token = token;
                            WXLoginForToken(token);
                        } else if (code == -1000) {
//                            sendRegist(name.getText().toString().trim(), pwd.getText().toString().trim());
                            showToast(code);
                        } else {
                            showToast(code);
                        }
                    }
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } finally {
                    NoticeDialog.stopNoticeDlg();
                }
            }
        });
    }

    private void WXLoginForToken(final String token) {
        Log.e("WECHAT", "自己服务器的" + token);
        HttpRequest.sendPost(TLUrl.URL_oauth + "?iou=1", "token=" + token,
                new HttpRevMsg() {
                    @Override
                    public void revMsg(String msg) {

                        Log.e("WX", msg.toString());
                        // TODO Auto-generated method stub
                        try {
                            final JSONObject json = new JSONObject(msg);
                            if (json != null && json.has("code")) {
                                int code = json.getInt("code");
                                Log.e("WECHAT", "自己服务器的code" + code);
                                if (code == 1) {
                                    Util.token = token;
                                    Util.preference.edit().putString("token", Util.token).commit();
                                    loginSuccess(json.getString("result"));

                                    Log.e("WECHAT", "Util.token =" + Util.token);
                                } else {
                                    showToast(code);
                                }
                            }
                        } catch (JSONException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    }
                });
    }

    private void showToast(int code) {
        String msg = "";
        switch (code) {
            case -1:
                msg = "参数非法";
                break;
            case -1000:
                msg = "用户不存在";
                break;
            case -1001:
                msg = "用户名或密码错误";
                break;
            case -1002:
                msg = "帐号停权";
                break;
            case -1003:
                msg = "用户详情未找到";
                break;
            case -1004:
                msg = "密码不一样";
                break;
            case -1005:
                msg = "注册失败";
                break;
            case -1006:
                msg = "用户已存在";
                break;
            case -1007:
                msg = "微信配置文件未找到";
                break;
            case -1008:
                msg = "非法的Code";
                break;
            case -1009:
                msg = "请重新登录";// "非法的token"
                break;
            case -1014:
                msg = "您未绑定邮箱,如需找回密码,请联系客服!";
                break;
            case -1017:
                msg = "邮箱未验证";
                break;
            case -1018:
                msg = "手机未验证";
                break;
            case -1019:
                msg = "验证方式未找到";
                break;
            case -2001:
                msg = "分享成功";
                break;
            case -2002:
                msg = "分享取消";
                System.out.println("分享取消");
                break;
            case -2003:
                msg = "分享拒绝";
                break;
            default:
                break;
        }
        final String tishi = msg;
        handler.post(new Runnable() {

            @Override
            public void run() {
                // TODO Auto-generated method stub
                showToast(tishi);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        mTencentAuthHandler.callOnActivityResult(requestCode, resultCode, data);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event != null
                && event.getRepeatCount() == 0) {
            loginSuccess("");
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private static final int SHOW_CONTENT = 0;

    private void sendQQ() {
        // TODO Auto-generated method stub
        ProgressDlgUtil.showProgressDlg("登录中", this);
        mTencentAuthHandler.login(this, new HttpCallbackListener() {
            @Override
            public void onFinish(String response) {
                /**
                 * <pre>
                 * {
                 *   "id": "55474453a835b8965c000001",
                 *   "avatar": "http://q.qlogo.cn/qqapp/222222/826130D0401E93B7617C316520CD1B38/100",
                 *   "last": {
                 *     "__v": 0,
                 *     "platform": "qq",
                 *     "time": 1430979785988,
                 *     "_id": "554b04c9956c164b7800000c",
                 *     "user": "55474453a835b8965c000001",
                 *     "ip": "59.40.98.63"
                 *   },
                 *   "nickname": "MitKey",
                 *   "uname": "222222::826130D0401E93B7617C316520CD1B38"
                 * }
                 * </pre>
                 */

                ProgressDlgUtil.stopProgressDlg();
                Log.i("QQ", "msg" + response);
                loginSuccess(response);
                // try {
                // final org.json.JSONObject jsonObject = new
                // org.json.JSONObject(
                // response);
                // Message msg = new Message();
                // msg.what = SHOW_CONTENT;
                // msg.obj = jsonObject;
                // mHandler.sendMessage(msg);
                //
                // // 头像
                // if (jsonObject.has("avatar")) {
                // new Thread() {
                // @Override
                // public void run() {
                // Bitmap bitmap = null;
                // try {
                // bitmap = getbitmap(jsonObject
                // .getString("avatar"));
                // } catch (JSONException e) {
                // }
                // Message msg = new Message();
                // msg.obj = bitmap;
                // msg.what = 2;
                // mHandler.sendMessage(msg);
                // }
                // }.start();
                // }
                // Log.e("MainActivity.onClickQQLogin", jsonObject.toString());
                // } catch (org.json.JSONException e1) {
                // // TODO Auto-generated catch block
                // e1.printStackTrace();
                // }
            }

            @Override
            public void onError(String exception) {
                ProgressDlgUtil.stopProgressDlg();

                Message msg = new Message();
                msg.what = SHOW_CONTENT;
                msg.obj = exception;
                handler.sendMessage(msg);
            }

            @Override
            public void onCancel() {
                ProgressDlgUtil.stopProgressDlg();

                Message msg = new Message();
                msg.what = SHOW_CONTENT;
                msg.obj = "取消qq登录授权";
                handler.sendMessage(msg);
            }
        });
    }

    private void loginSuccess(String msg) {
        if (msg.length() > 0 && !Util.isThirdLogin) {
            SharedPreferences.Editor editor = Util.preference.edit();
            editor.putBoolean("lizai_auto", true);
//			editor.putString("lizai_userName", );
            editor.putString("lizai_pwd", etPwd.getText().toString().trim());
            editor.commit();
        } else if (msg.length() > 0 && Util.isThirdLogin) {
            SharedPreferences.Editor editor = Util.preference.edit();
            editor.putBoolean("lizai_auto", false);
            editor.commit();
        }
        Intent intent = new Intent("com.abct.occft.hq.login");
        intent.putExtra("type", "login");
        intent.putExtra("msg", msg);
        sendBroadcast(intent);

        // Intent intent = new Intent(this, MainActivity.class);
        // intent.putExtra("login", msg);
        // startActivity(intent);
        finish();
        overridePendingTransition(R.anim.move_left_in_activity,
                R.anim.move_right_out_activity);
        ProgressDlgUtil.stopProgressDlg();

        if (WXEntryActivity.other != null) {
            WXEntryActivity.other.finish();
            WXEntryActivity.other = null;
        }

    }


    @Override
    public void onReq(BaseReq baseReq) {
        finish();
    }

    @Override
    public void onResp(BaseResp resp) {
        Log.e("WXerrCode", resp.errCode + "");
        if (MyApplication.getInstance().self != null && !isLogin) {
            // 分享s
            switch (resp.errCode) {
                case BaseResp.ErrCode.ERR_OK:
                    // 分享成功
                    showToast("分享成功");
                    break;
                case BaseResp.ErrCode.ERR_USER_CANCEL:
                    showToast("用户已取消");
                    // 分享取消
                    break;
                case BaseResp.ErrCode.ERR_AUTH_DENIED:
                    // 分享拒绝
                    showToast("用户取消权限");
                    break;
            }
            // Intent intent = new Intent(WXEntryActivity.this,
            // ShareActivity.class);
            // startActivity(intent);
            finish();
            return;
        }

        ProgressDlgUtil.stopProgressDlg();
        if (resp.errCode == 0 && resp instanceof SendAuth.Resp) {
            ProgressDlgUtil.showProgressDlg("登录中", this);
            String wxcode = ((SendAuth.Resp) resp).code;
            Log.e("WECHAT", "官方" + wxcode);
            HttpRequest.sendGet(TLUrl.URL_wechat, "code=" + wxcode + "&appid="
                    + appid + "&iou=1", new HttpRevMsg() {

                @Override
                public void revMsg(String msg) {
                    // TODO Auto-generated method stub
                    try {
                        JSONObject json = new JSONObject(msg);
                        if (json != null && json.has("code")) {
                            int code = json.getInt("code");
                            if (code == 1) {
                                Util.preference.edit().putString("logintype", "WX").commit();
                                WXLoginForToken(json.getString("result"));
                                isLogin = false;
                            } else {
                                showToast("网络不好,请稍后重试");
                            }
                        }
                    } catch (JSONException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            });
        }
    }
}
