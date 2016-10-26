package com.abcs.huaqiaobang.yiyuanyungou;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.database.Cursor;
import android.graphics.Bitmap.CompressFormat;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.TrafficStats;
import android.os.Build;
import android.support.multidex.MultiDex;
import android.util.Log;

import com.abcs.huaqiaobang.yiyuanyungou.beans.Constent;
import com.abcs.huaqiaobang.yiyuanyungou.beans.Options;
import com.abcs.huaqiaobang.yiyuanyungou.beans.User;
import com.abcs.huaqiaobang.yiyuanyungou.dialog.ProgressDlgUtil;
import com.abcs.huaqiaobang.yiyuanyungou.util.Util;
import com.android.volley.RequestQueue;
import com.lidroid.xutils.BitmapUtils;
import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.db.sqlite.Selector;
import com.lidroid.xutils.util.LogUtils;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.LruMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.download.BaseImageDownloader;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

//import io.realm.Realm;
//import io.realm.RealmConfiguration;
//import io.realm.exceptions.RealmMigrationNeededException;

public class MyApplication extends Application {
    public User self;
    public static Map<String, Activity> activityMap = new HashMap<String, Activity>();
    public static boolean isupdate = false;
    private static MyApplication instance;
    public static ImageLoader imageLoader;
    public static DisplayImageOptions options;
    public static RequestQueue requestQueue;
    public static String cardname;
    public static String carddesc;
    public String location;
    ProgressDlgUtil dlgUtil = new ProgressDlgUtil();

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public User user;

    public String mykey;
    public boolean userChange = false;

    public String getMykey() {
        return mykey;
    }

    public void setMykey(String mykey) {
        this.mykey = mykey;
    }


    //ytbt
    private static MyApplication myAplication;
    public static boolean isShake, isSound = false;
    public static ConcurrentHashMap<String,User> friends;
    public static HashMap<String, String> contacts = new HashMap<>();
    public static ArrayList<User> users;
    private String file;
    private String ownernickname, avater;
    private int uid;
    public static String device_token;
    public static DbUtils dbUtils;
    public static BitmapUtils bitmapUtils;
    public static List<Activity> list = new ArrayList<>();
    public static HashSet<BaseActivity> REGISTERACTIVITYS = new HashSet();

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
//        if (!quickStart() && Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {//>=5.0的系统默认对dex进行oat优化
//            if (needWait(base)){
//                waitForDexopt(base);
//            }
            MultiDex.install (this );
//        } else {
//            return;
//        }
    }
    public boolean quickStart() {
        if (getCurProcessName(this).indexOf(":mini")>-1) {
            Log.d("loadDex", ":mini start!");
            return true;
        }
        return false ;
    }
    private boolean needWait(Context context){
        String flag = get2thDexSHA1(context);
        Log.d( "loadDex", "dex2-sha1 "+flag);
        SharedPreferences sp = context.getSharedPreferences(
                getPackageInfo(context). versionName, MODE_MULTI_PROCESS);
        String saveValue = sp.getString(KEY_DEX2_SHA1, "");
        return !flag.equals(saveValue);
    }
    /**
     * Get classes.dex file signature
     * @param context
     * @return
     */
    private String get2thDexSHA1(Context context) {
        ApplicationInfo ai = context.getApplicationInfo();
        String source = ai.sourceDir;
        try {
            JarFile jar = new JarFile(source);
            Manifest mf = jar.getManifest();
            Map<String, Attributes> map = mf.getEntries();
            Attributes a = map.get("classes2.dex");
            return a.getValue("SHA1-Digest");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null ;
    }

    public static final String KEY_DEX2_SHA1 = "dex2-SHA1-Digest";
    // optDex finish
    public void installFinish(Context context){
        SharedPreferences sp = context.getSharedPreferences(
                getPackageInfo(context).versionName, MODE_MULTI_PROCESS);
        sp.edit().putString(KEY_DEX2_SHA1,get2thDexSHA1(context)).commit();
    }
    public static PackageInfo getPackageInfo(Context context){
        PackageManager pm = context.getPackageManager();
        try {
            return pm.getPackageInfo(context.getPackageName(), 0);
        } catch (NameNotFoundException e) {
            LogUtils.e(e.getLocalizedMessage());
        }
        return  new PackageInfo();
    }


    public static String getCurProcessName(Context context) {
        try {
            int pid = android.os.Process.myPid();
            ActivityManager mActivityManager = (ActivityManager) context
                    .getSystemService(Context. ACTIVITY_SERVICE);
            for (ActivityManager.RunningAppProcessInfo appProcess : mActivityManager
                    .getRunningAppProcesses()) {
                if (appProcess.pid == pid) {
                    return appProcess. processName;
                }
            }
        } catch (Exception e) {
            // ignore
        }
        return null ;
    }




    @Override
    public void onCreate() {
        super.onCreate();

        // 设置默认的Reanlm配置
//        RealmConfiguration realmconfig = new RealmConfiguration.Builder(this).name("tljr_Default.realm").build();
//        try {
//            Realm.setDefaultConfiguration(realmconfig);
//        } catch (RealmMigrationNeededException e) {
//            try {
//                Realm.deleteRealm(realmconfig);
//                // Realm file has been deleted.
//                Realm.setDefaultConfiguration(realmconfig);
//            } catch (Exception ex) {
//                // No Realm file to remove.
//            }
//        }

//        CrashHandler crashHandler = CrashHandler.getInstance();
//        crashHandler.init(getApplicationContext());

//        MultiDex.install(this);
        instance = this;
        Util.init();
        imageLoader = ImageLoader.getInstance();
        options = Options.getListOptions();

        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
                getApplicationContext())
                .memoryCacheExtraOptions(480, 800)
                        // max width, max height，即保存的每个缓存文件的最大长宽
                .discCacheExtraOptions(480, 800, CompressFormat.PNG, 75, null)
                        // Can slow ImageLoader, use it carefully (Better don't use
                        // it)/设置缓存的详细信息，最好不要设置这个
                .threadPoolSize(3)
                        // 线程池内加载的数量
                .threadPriority(Thread.NORM_PRIORITY - 2)
                .denyCacheImageMultipleSizesInMemory()
//                .memoryCache(new WeakMemoryCache())
                .memoryCache(new LruMemoryCache((int) Runtime.getRuntime().maxMemory() / 4))
                        // You can pass your own memory cache
                        // implementation/你可以通过自己的内存缓存实现
//                .memoryCacheSize(2 * 1024 * 1024)
                .discCacheSize(50 * 1024 * 1024)
                .discCacheFileNameGenerator(new Md5FileNameGenerator())
                        // 将保存的时候的URI名称用MD5 加密
                .tasksProcessingOrder(QueueProcessingType.LIFO)
                        // .discCacheFileCount(200)
                        // 缓存的文件数量
                        // .discCache(new UnlimitedDiscCache(cacheDir))//自定义缓存路径
                .imageDownloader(
                        new BaseImageDownloader(getApplicationContext(),
                                5 * 1000, 30 * 1000)) // connectTimeout (5 s),
                        // readTimeout (30
                        // s)超时时间

                .writeDebugLogs() // Remove for release app
                .build();// 开始构建

        imageLoader.init(config);
        registerReceiver(receiver3, new IntentFilter(
                Intent.ACTION_CLOSE_SYSTEM_DIALOGS));

        // 注册蒲公英132

//        PgyCrashManager.register(this);

        //注册jPush

        isShake = getSharedPreferences("user", MODE_PRIVATE).getBoolean(
                "isShake", false);
        isSound = getSharedPreferences("user", MODE_PRIVATE).getBoolean(
                "isSound", true);
//        myAplication = this;
//        String processName = getProcessName(this,
//                android.os.Process.myPid());
//        if (processName != null) {
//            boolean defaultProcess = processName.equals("com.abcs.huaqiaobang");
//            if (defaultProcess) {
                myAplication = this;
                Log.i("info", "Application_onCreate");
                isShake = getSharedPreferences("user", MODE_PRIVATE)
                        .getBoolean("isShake", false);
                isSound = getSharedPreferences("user", MODE_PRIVATE)
                        .getBoolean("isSound", true);
                getSharedPreferences("user", Context.MODE_PRIVATE).edit()
                        .putBoolean("isReload", true).commit();
                dbUtils = DbUtils.create(this, "xUtils.db", 4,
                        new DbUtils.DbUpgradeListener() {
                            @Override
                            public void onUpgrade(DbUtils db, int oldversion,
                                                  int newVersion) {
                                if (newVersion > oldversion) {
//                                    updateDb(db, "ConversationBean");
//                                    updateDb(db, "TopConversationBean");
                                }
                            }
                        });
                bitmapUtils = new BitmapUtils(this);
//                new QueryContactTask(this).execute();
//                FileAccessor.initFileAccess();
//        }
//        bitmapUtils = new BitmapUtils(this);
//        bitmapUtils.configMemoryCacheEnabled(true);
//        bitmapUtils.configDiskCacheEnabled(true);
//        bitmapUtils.configDefaultLoadFailedImage(R.drawable.default_head);
//		PushAgent.sendSoTimeout(this, 600); // 设置护保进程间隔时间
//        getSharedPreferences("user", Context.MODE_PRIVATE).edit().putBoolean("isReload", true).commit();
////        new QueryContactTask(this).execute();
//        try {
//            dbUtils.createTableIfNotExist(AddFriendRequestBean.class);
//            List<AddFriendRequestBean> list = dbUtils.findAll(Selector.from(AddFriendRequestBean.class).orderBy("time", true));
//            if (list != null)
//                requests.addAll(list);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    }

    public boolean checkNetWork() {
        ConnectivityManager manager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        // 检查网络连接，如果无网络可用，就不需要进行连网操作等
        NetworkInfo info = manager.getActiveNetworkInfo();
        if (info == null || !manager.getBackgroundDataSetting()) {
            return false;
        } else {
            return true;
        }
    }

    public void stopKillService() {
        Intent intent = new Intent();
        intent.setAction("stopKillService");
        sendBroadcast(intent);
    }

    public static String getProcessName(Context cxt, int pid) {
        ActivityManager am = (ActivityManager) cxt.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> runningApps = am.getRunningAppProcesses();
        if (runningApps == null) {
            return null;
        }
        for (ActivityManager.RunningAppProcessInfo procInfo : runningApps) {
            if (procInfo.pid == pid) {
                return procInfo.processName;
            }
        }
        return null;
    }


    private static boolean isExist(List<String> dbFildsList, String fildName) {
        for (String string : dbFildsList) {
            if (string.equals(fildName)) {
                return true;
            }
        }
        return false;
    }

    BroadcastReceiver receiver3 = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // TODO Auto-generated method stub
            String action = intent.getAction();
            if (action.equals(Intent.ACTION_CLOSE_SYSTEM_DIALOGS)) {
                String reason = intent.getStringExtra("reason");
                if (reason != null) {
                    if (reason.equals("homekey")) {
//                        Intent intent1 = new Intent(
//                                MyApplication.getInstance(), KillService.class);
//                        MyApplication.getInstance().startService(intent1);
                    } else if (reason.equals("recentapps")) {
                    }
                }
            }
        }
    };

    public static MyApplication getInstance() {
        if (null == instance) {
            instance = new MyApplication();
        }
        return instance;
    }




    private String name, token;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }


    public String getAvater() {
        return avater;
    }

    public void setAvater(String avater) {
        this.avater = avater;
    }

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public String getOwnernickname() {
        return ownernickname;
    }

    public void setOwnernickname(String ownernickname) {
        this.ownernickname = ownernickname;
    }

    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
    }


    private Activity nowActivity;

    public Activity getNowActivity() {
        return nowActivity;
    }

    // 添加Activity到容器中
    public void addActivity(Activity activity) {
        nowActivity = activity;
        activityMap.put(activity.getClass().getName(), activity);
    }

    public void removeActivity(Activity activity) {
        activityMap.remove(activity);
    }


    // 遍历所有Activity并finish
    public static void exit() {

//        ECDevice.is
        dbUtils.close();
        bitmapUtils.cancel();
//            AppManager.getAppManager().AppExit();
//				mPushAgent.disable();
        for (Activity activity : activityMap.values()) {
            activity.finish();
        }
        Log.i("zjz","退出程序");
        Util.preference.edit().putBoolean("isTotalExit",true).commit();
        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(0);
    }

    public class getByteThread extends Thread {
        @Override
        public void run() {
            while (true) {
                getUidByte();
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }
    }

    ;


    public static long first1 = 0, first2 = 0;

    public void getUidByte() {
        try {
            PackageManager pm = getPackageManager();
            ApplicationInfo ai = pm.getApplicationInfo(Constent.packageName,
                    PackageManager.GET_ACTIVITIES);
            if (first1 == 0) {
                first1 = TrafficStats.getUidRxBytes(ai.uid);
            }
            if (first2 == 0) {
                first2 = TrafficStats.getUidTxBytes(ai.uid);
            }
            double s1 = (TrafficStats.getUidRxBytes(ai.uid) - first1) / 1024;
            double s2 = (TrafficStats.getUidTxBytes(ai.uid) - first2) / 1024;
            Constent.Liuliang = (int) s1 + (int) s2;
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    // public void resultandLogin() {
    // ((MainActivity) umengPushServer.activityMap.get(Constent.packageName
    // + ".main.MainActivity")).mHandler.sendEmptyMessage(201);
    // }

    private static int sTheme = 1;

    public final static int THEME_DEFAULT = 0;
    public final static int THEME_RED = 1;
    public final static int THEME_BLUE = 2;
    public static int StatusBarColor;
    public static float turnIn_money;

    public static void changeToTheme(Activity activity, int theme) {
        sTheme = theme;
        activity.finish();
        activity.startActivity(new Intent(activity, activity.getClass()));
    }


    /**
     * 初始化极光推送
     */
//    private void initJpush() {
//        //开启日志模式，正式发布可以取消
//
//        JPushInterface.setDebugMode(true);
//        try {
//            //初始化激光推送
//            JPushInterface.init(this);
//        } catch (Exception e) {
//            e.printStackTrace();
//            //极光推送初始化失败
//        }
//    }

}
