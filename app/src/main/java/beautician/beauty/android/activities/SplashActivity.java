package beautician.beauty.android.activities;

import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.app.FragmentActivity;
import android.util.Base64;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import beautician.beauty.android.MyApplication;
import beautician.beauty.android.R;


public class SplashActivity extends FragmentActivity {
    protected int _splashTime = 4000;
    final String TAG = "SplashActivity";
    public MyFragmentActivity mActivity;

    MyApplication myApplication;
    private ProgressBar mProgress;
    private ImageView mImageViewLogo;
    int wait = 0;
//    private AnimatorSet mAnimatorSet;

    public void enableStrictMode() {
        StrictMode.setThreadPolicy(
                new StrictMode.ThreadPolicy.Builder()
                        .detectDiskReads()
                        .detectDiskWrites()
                        .detectNetwork()
                        .penaltyLog()
                        .build());
        StrictMode.setVmPolicy(
                new StrictMode.VmPolicy.Builder()
                        .detectLeakedSqlLiteObjects()
                        .penaltyLog()
                        .build());
    }

    @SuppressLint("NewApi")
    public void onCreate(Bundle savedInstanceState) {
        enableStrictMode();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = SplashActivity.this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            //window.setStatusBarColor(getResources().getColor(R.color.theme_reminder_dark));
        }
        facebookHashKey();

        mProgress = (ProgressBar)findViewById(R.id.activity_splash_progressbar);
        mProgress.setMax(_splashTime);
        mProgress.setProgress(0);

        myApplication = (MyApplication)getApplication();

        mImageViewLogo = (ImageView)findViewById(R.id.activity_splash_image_logo);
//        mAnimatorSet = (AnimatorSet) AnimatorInflater.loadAnimator(getApplicationContext(), R.animator.flip_logo_anim);
//        mAnimatorSet.setTarget(mImageViewLogo);
//        mAnimatorSet.start();

        final Thread splashThread = new Thread() {
            public void run() {
                try {

                    while ((_splashTime > wait)) { // will show
                        sleep(5);

                        wait += 5;
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mProgress.setProgress(wait);
                            }
                        });
                    }
                } catch (InterruptedException e) {
                    Log.d(TAG, e.getMessage());

                } finally {

                    Intent mainActivity;
                    if(myApplication.isLogin())
                    {
                        mainActivity = new Intent(SplashActivity.this, MainActivity.class);
                    }else
                    {
                        mainActivity = new Intent(SplashActivity.this, LoginActivity.class);
                    }
                    startActivity(mainActivity);
                    finish();

                }
            }
        };
        splashThread.start();
    }


    private void facebookHashKey() {

        try {
            PackageInfo info = getPackageManager().getPackageInfo("beautician.beauty.android", PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                String hashCode  = Base64.encodeToString(md.digest(), Base64.DEFAULT);
                System.out.println("Print the hashKey for Facebook :"+hashCode);
                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {

        } catch (NoSuchAlgorithmException e) {

        }
    }


    @Override
    protected void onPause() {

        super.onPause();
        finish();
    }

    @Override
    protected void onDestroy() {

        super.onDestroy();
    }

}