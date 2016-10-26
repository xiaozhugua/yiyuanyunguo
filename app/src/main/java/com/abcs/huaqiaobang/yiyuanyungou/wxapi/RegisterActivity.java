package com.abcs.huaqiaobang.yiyuanyungou.wxapi;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;


import com.abcs.huaqiaobang.yiyuanyungou.BaseActivity;
import com.abcs.huaqiaobang.yiyuanyungou.MyApplication;
import com.abcs.huaqiaobang.yiyuanyungou.R;
import com.abcs.huaqiaobang.yiyuanyungou.dialog.ProgressDlgUtil;
import com.abcs.huaqiaobang.yiyuanyungou.util.HttpRequest;
import com.abcs.huaqiaobang.yiyuanyungou.util.HttpRevMsg;
import com.abcs.huaqiaobang.yiyuanyungou.util.TLUrl;
import com.abcs.huaqiaobang.yiyuanyungou.util.Util;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegisterActivity extends BaseActivity {
    private View mMainView;
    private boolean isChooseJiGou;
    private Button btn_reg;
    private RadioButton r_geRen, r_jiGou;
    private EditText et_jiGou, et_name, et_pwd, et_rePwd;
    private static final int NEEDNAME = -2001;
    private static final int NEEDPWD = -2002;
    private static final int NEEDREPWD = -2003;
    private static final int TWOPWD = -2004;
    private static final int NEEDMOBILE = -2005;
    private static final int NEEDJIGOU = -2006;
    private static final int REGERROR = -2007;
    private static final int NAMELENGTH = -2008;
    private static final int PWDLENGTH = -2009;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mMainView = getLayoutInflater().inflate(
                R.layout.occft_activity_register, null);
        this.setContentView(mMainView);
        r_geRen = (RadioButton) mMainView
                .findViewById(R.id.tljr_reg_rbtn_lgeren);
        r_jiGou = (RadioButton) mMainView
                .findViewById(R.id.tljr_reg_rbtn_ljigou);
        et_jiGou = (EditText) mMainView.findViewById(R.id.tljr_reg_et_jigou);
        et_name = (EditText) mMainView.findViewById(R.id.tljr_reg_et_lname);
        et_pwd = (EditText) mMainView.findViewById(R.id.tljr_reg_et_lpwd);
        et_rePwd = (EditText) mMainView.findViewById(R.id.tljr_reg_et_repwd);
        btn_reg = (Button) mMainView.findViewById(R.id.tljr_reg_btn_zhuce);
        initListener();

    }


    private void initListener() {
        r_geRen.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                isChooseJiGou = false;
                et_jiGou.setVisibility(View.GONE);
            }
        });

        r_jiGou.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                isChooseJiGou = true;
                et_jiGou.setVisibility(View.VISIBLE);
            }
        });

        mMainView.findViewById(R.id.tljr_reg_btn_lfanhui).setOnClickListener(
                new OnClickListener() {

                    @Override
                    public void onClick(View arg0) {
                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.showSoftInput(getCurrentFocus(), InputMethodManager.SHOW_FORCED);
                        imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                        RegisterActivity.this.finish();
                        // overridePendingTransition(R.anim.move_right_in_activity,
                        // R.anim.move_right_out_activity);
                    }
                });
        btn_reg.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                String name = et_name.getText().toString().trim();
                String pwd = et_pwd.getText().toString().trim();
                String rePwd = et_rePwd.getText().toString().trim();
                String jiGou = et_jiGou.getText().toString().trim();
                if (name.length() == 0) {
                    showToast(NEEDNAME);
                    et_name.requestFocus();
                    return;
                }
                if (pwd.length() == 0) {
                    et_pwd.requestFocus();
                    showToast(NEEDPWD);
                    return;
                }
                if (rePwd.length() == 0) {
                    showToast(NEEDREPWD);
                    et_rePwd.requestFocus();
                    return;
                }

                if (name.length() < 6) {
                    showToast(NAMELENGTH);
                    et_name.requestFocus();
                    return;
                }
                if (pwd.length() < 6 || pwd.length() > 12) {
                    showToast(PWDLENGTH);
                    et_pwd.requestFocus();
                    return;
                }

                if (!Util.checkPassword(et_pwd.getText().toString().trim())) {
//                    Toast.makeText(RegisterActivity.this, "密码过于简单", Toast.LENGTH_SHORT).show();
                    showToast("密码过于简单");
                    et_pwd.requestFocus();
                    return;
                }

                if (!pwd.equals(rePwd)) {
                    showToast(TWOPWD);
                    et_rePwd.requestFocus();
                    return;
                }
                // if (!isMobile(name)) {
                // showToast(NEEDMOBILE);
                // et_name.requestFocus();
                // return;
                // }

                if (isChooseJiGou) {
                    if (jiGou.length() == 0) {
                        showToast(NEEDJIGOU);
                        et_jiGou.requestFocus();
                        return;
                    }
                }
                Util.isThirdLogin = false;
                sendRegist();
            }
        });
    }

    public static boolean isMobile(String str) {
        Pattern p = null;
        Matcher m = null;
        boolean b = false;
        p = Pattern.compile("^[1][3,4,5,8][0-9]{9}$"); // 验证手机号
        m = p.matcher(str);
        b = m.matches();
        return b;
    }

    private void sendRegist() {

        ProgressDlgUtil.showProgressDlg("注册中...", RegisterActivity.this);
        String param = "uname=" + et_name.getText().toString().trim()
                + "&upass=" + et_pwd.getText().toString().trim() + "&repass="
                + et_rePwd.getText().toString().trim() + "&from=华侨帮";
        if (isChooseJiGou) {
            param += "&organization=" + et_jiGou.getText().toString().trim();
        }
        HttpRequest.sendPost(TLUrl.URL_regist, param, new HttpRevMsg() {
            @Override
            public void revMsg(final String msg) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        ProgressDlgUtil.stopProgressDlg();
                        if (msg.length() == 0) {
                            showToast(REGERROR);
                            return;
                        }
                        try {
                            JSONObject json = new JSONObject(msg);
                            if (json != null && json.has("code")) {
                                int code = json.getInt("code");
                                if (code == 1) {
                                    showToast("注册成功");
                                    String token = json.getString("result");
                                    Util.token = token;
                                    loginForToken(token);
                                } else {
                                    showToast(code);
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });


    }

    private void loginForToken(String token) {
        ProgressDlgUtil.showProgressDlg("登录中...", RegisterActivity.this);
        HttpRequest.sendGet(TLUrl.URL_oauth, "token=" + token + "&iou=1",
                new HttpRevMsg() {
                    @Override
                    public void revMsg(final String msg) {
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                if (msg.length() <= 0) {
                                    showToast(-1005);
                                    return;
                                }
                                try {
                                    final JSONObject json = new JSONObject(msg);
                                    if (json != null && json.has("code")) {
                                        int code = json.getInt("code");
                                        if (code == 1) {
//                                            showToast("注册成功，将为您自动登陆");
                                            loginSuccess(json.getString("result"));
                                        } else {
                                            showToast(code);
                                        }
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        });

                    }
                });
    }

    private void loginSuccess(String msg) {
        // Intent intent = new Intent(this, MainActivity.class);
        // intent.putExtra("login", msg);
        // startActivity(intent);
        ProgressDlgUtil.stopProgressDlg();
        Intent intent = new Intent("com.abct.occft.hq.login");
        intent.putExtra("type", "login");
        intent.putExtra("msg", msg);
        sendBroadcast(intent);
        Activity activity = MyApplication.getInstance().activityMap.get("com.abcs.huaqiaobang.yiyuanyungou.wxapi.WXEntryActivity");
        if (activity != null) {
            activity.finish();
        }
        finish();
        if (msg.length() > 0) {
            Editor editor = Util.preference.edit();
            editor.putBoolean("lizai_auto", true);
            editor.putString("lizai_pwd", et_pwd.getText().toString().trim());
            editor.commit();
        }


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
                msg = "登录失败";
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
            case -2001:
                msg = "请输入用户名";
                break;
            case -2002:
                msg = "请输入密码";
                break;
            case -2003:
                msg = "请输入确认密码";
                break;
            case -2004:
                msg = "两次密码不一致";
                break;
            case -2005:
                msg = "请输入正确的手机号码";
                break;
            case -2006:
                msg = "请输入机构名称";
                break;
            case -2007:
                msg = "注册失败";
                break;
            case -2008:
                msg = "用户名长度不少于6位";
                break;
            case -2009:
                msg = "密码长度6-12位";
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

    Handler handler = new Handler();
}
