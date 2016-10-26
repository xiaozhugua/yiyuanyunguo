package com.abcs.huaqiaobang.yiyuanyungou;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.abcs.huaqiaobang.yiyuanyungou.util.AppManager;
import com.abcs.huaqiaobang.yiyuanyungou.wxapi.WXEntryActivity;
import com.readystatesoftware.systembartint.SystemBarTintManager;
import com.umeng.analytics.MobclickAgent;

import java.io.File;
import java.util.Iterator;

/**
 * @author xbw
 * @version 创建时间：2015年8月31日 下午4:42:57
 */
public class BaseActivity extends Activity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        MyApplication.getInstance().addActivity(this);
        if (savedInstanceState != null) {
            final Intent intent = getPackageManager().getLaunchIntentForPackage(getPackageName());
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            android.os.Process.killProcess(android.os.Process.myPid());
        }
        AppManager.getAppManager().addActivity(this);
//        initSystemBar();

    }

    public void initSystemBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            setTranslucentStatus(true);
//            tintManager.setStatusBarTintColor(Color.parseColor("#000000"));
//            setSystembarColor(Color.parseColor("#f22828"));
        }
    }

    public void setSystembarColor(int color) {
        SystemBarTintManager tintManager = new SystemBarTintManager(this);
        tintManager.setStatusBarTintEnabled(true);
        tintManager.setStatusBarTintColor(color);
    }

    public void login() {
        MobclickAgent.onEvent(BaseActivity.this, "Login");
        Intent intent = new Intent(BaseActivity.this, WXEntryActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.move_left_in_activity,
                R.anim.move_right_out_activity);
    }

    @TargetApi(19)
    public void setTranslucentStatus(boolean on) {
        Window win = getWindow();
        WindowManager.LayoutParams winParams = win.getAttributes();
        final int bits = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
        if (on) {
            winParams.flags |= bits;
        } else {
            winParams.flags &= ~bits;
        }
        win.setAttributes(winParams);
    }

    public int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height",
                "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        MyApplication.getInstance().stopKillService();
    }

    public Toast toast;

    public void showToast(String msg) {

        if (toast != null) {
            toast.setText(msg);
            toast.setDuration(Toast.LENGTH_SHORT);
            toast.show();
        } else {
            toast = Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        AppManager.getAppManager().finishActivity(this);
        MyApplication.getInstance().removeActivity(this);
    }

    public String getFileRoot(Context context) {
        if (Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            File external = context.getExternalFilesDir(null);
            if (external != null) {
                return external.getAbsolutePath();
            }
        }
        return context.getFilesDir().getAbsolutePath();
    }

    public void finishOthers() {
        Iterator<Activity> i = AppManager.activityStack.iterator();
        while (i.hasNext()) {
            Activity a = i.next();
            i.remove();
            a.finish();
        }
    }
}
