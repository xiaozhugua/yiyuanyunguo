package com.abcs.huaqiaobang.yiyuanyungou.activity;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;


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

import butterknife.ButterKnife;
import butterknife.InjectView;

public class LoginActivity extends BaseActivity {

    @InjectView(R.id.t_desc)
    TextView tDesc;
    @InjectView(R.id.et_username)
    EditText etUsername;
    @InjectView(R.id.et_pwd)
    EditText etPwd;
    @InjectView(R.id.btn_login)
    Button btnLogin;

    private Handler mHandler=new Handler();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.hwg_activity_login);
        ButterKnife.inject(this);
        initView();
    }

    private void initView() {
        tDesc.setText(getIntent().getStringExtra("desc"));
        String userName = Util.preference.getString("lizai_userName", "");
        String pwd = Util.preference.getString("lizai_pwd", "");
        if (userName.length() > 0 && pwd.length() > 0&& MyApplication.getInstance().self!=null) {
            etUsername.setText(userName);
            etPwd.setText(pwd);
            btnLogin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    loginForYYG();
                }
            });
        }

    }


    private void loginForYYG() {
        ProgressDlgUtil.showProgressDlg("Loading...", this);
        HttpRequest.sendPost(TLUrl.URL_yyg_login, "nickname=" + MyApplication.getInstance().self.getNickName() + "&userId="
                + MyApplication.getInstance().self.getId() + "&avator=" + MyApplication.getInstance().self.getAvatarUrl() + "&userName=" + MyApplication.getInstance().self.getUserName()
                +"&alias="+MyApplication.getInstance().self.getId(), new HttpRevMsg() {
            @Override
            public void revMsg(final String msg) {
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (msg == null) {
                            ProgressDlgUtil.stopProgressDlg();
                            return;
                        }
                        Log.i("zjz", "login_for_yyg=" + msg);
                        try {
                            JSONObject json = new JSONObject(msg);
                            if (json.optInt("status") == 1) {
                                Util.isYYGLogin = true;
                                showToast("登录成功！");
                                finish();
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
}
