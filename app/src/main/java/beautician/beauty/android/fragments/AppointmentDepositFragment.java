package beautician.beauty.android.fragments;

import android.annotation.SuppressLint;
import beautician.beauty.android.views.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.ads.internal.request.LargeParcelTeleporter;

import beautician.beauty.android.R;
import beautician.beauty.android.activities.MyFragmentActivity;
import beautician.beauty.android.utilities.CommonMethod;
import beautician.beauty.android.utilities.WebMethod;


@SuppressLint("InflateParams")
public class AppointmentDepositFragment extends Fragment implements OnClickListener {

    private MyFragmentActivity mActivity;
    private View rootView;

    private CommonMethod mCommonMethod;
//    private String mStringURL = "http://beauticianapp.com/api/index.php?api=paymentform&";
    private String mStringURL = WebMethod.mStringURLAppointment;
    private String mStringAppointmentID = "";
    private String mStringReedemAmount = "";
    private String mStringPayAmount = "";
    private String mStringPayType = "";
    private String mStringDateTime = "";
    private String mStringProviderName = "";

    private WebView mWebView;
    private ProgressBar mProgress;
    private ProgressDialog mProgressDialog;

    public AppointmentDepositFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_webview, container, false);

        mActivity = (MyFragmentActivity) getActivity();
        mActivity.setHeaderTitle(R.string.lbl_deposit);
        mActivity.setSearchIcon(R.drawable.icon_search);
        mActivity.onBackButton(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mActivity.replaceFragment(new SearchFragment(), true);
            }
        });

        mCommonMethod = new CommonMethod(mActivity);

        mStringProviderName = getArguments().getString(getString(R.string.bundle_provider_name));
        mStringPayAmount = getArguments().getString(getString(R.string.bundle_pay_amount));
        mStringReedemAmount = getArguments().getString(getString(R.string.bundle_reedem_amount));
        mStringPayType = getArguments().getString(getString(R.string.bundle_pay_type));
        mStringDateTime = getArguments().getString(getString(R.string.bundle_time));
        mStringAppointmentID = getArguments().getString(getString(R.string.bundle_appointment_id));

        StringBuilder mStringBuilder = new StringBuilder();
        mStringBuilder.append(mStringURL);
        mStringBuilder.append("user_id=");
        mStringBuilder.append(mActivity.getMyApplication().getUserFiled(getString(R.string.sp_user_id)));
        mStringBuilder.append("&token=");
        mStringBuilder.append(mActivity.getMyApplication().getUserFiled(getString(R.string.sp_user_hash)));
        mStringBuilder.append("&appointmentid=");
        mStringBuilder.append(mStringAppointmentID);
        mStringBuilder.append("&redeemamt=");
        mStringBuilder.append(mStringReedemAmount);
        mStringBuilder.append("&amt=");
        mStringBuilder.append(mStringPayAmount);
        mStringBuilder.append("&type=");
        mStringBuilder.append(mStringPayType);


        mStringURL = mStringBuilder.toString();

//        mStringURL = "http://nexuslink.in/paypalexpress/";

        getWidgetRefrence(rootView);
        registerOnClick();

        mProgressDialog = ProgressDialog.show(mActivity, "", getString(R.string.dialog_loading), true);
        mProgressDialog.setCancelable(true);
        return rootView;
    }

    /**
     * Method call will get IDs from xml file.
     * @param v
     */
    private void getWidgetRefrence(View v) {

        mProgress = (ProgressBar) v.findViewById(R.id.fragment_statastics_progressbar);
        mProgress.setMax(100);
        mProgress.setProgress(0);

        mWebView = (WebView) v.findViewById(R.id.fragment_statastics_webview);
//        mWebView.addJavascriptInterface(new PaymentJavascriptInterface(mActivity), "AndroidFunction");
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.getSettings().setUserAgentString("");
        mWebView.setWebChromeClient(new MyWebCromeClient());
        mWebView.setWebViewClient(new MyWebViewClient());
        mWebView.loadUrl(mStringURL);
        Log.e("DEP : ", mStringURL);
        if (Build.VERSION.SDK_INT >= 11) {
            mWebView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }
    }

    /**
     * Method call will Register OnClick() Events for widgets.
     */
    private void registerOnClick() {

    }

    /**
     * Method call OnClick Event fire.
     */
    @Override
    public void onClick(View v) {

    }

    private class MyWebCromeClient extends WebChromeClient {
        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            setValue(newProgress);
            super.onProgressChanged(view, newProgress);
        }
    }

    private class MyWebViewClient extends WebViewClient {

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {

            if (url.contains("thankyou.php")) {
                finishProcess(true);
                return true;
            }else if(url.contains("form.php"))
            {
                mActivity.getAppAlertDialog().showAlertWithSingleButton(getString(R.string.app_name), getString(R.string.alt_msg_appointment_failed),
                        getString(R.string.lbl_ok), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                finishProcess(false);
                            }
                        });
                return true;
            }
            view.loadUrl(url);
            return true;
        }
    }

    public void setValue(int progress) {
        this.mProgress.setProgress(progress);
        if (progress == 100) {
            this.mProgress.setVisibility(View.GONE);
            if (mProgressDialog != null)
                mProgressDialog.dismiss();
        } else {
            this.mProgress.setVisibility(View.VISIBLE);
            if(!mProgressDialog.isShowing()) {
                mProgressDialog = ProgressDialog.show(mActivity, "", getString(R.string.dialog_loading), true);
                mProgressDialog.setCancelable(true);
            }
        }
    }

    public void finishProcess(boolean isDone) {
        mActivity.removePreviousFragment();
        mActivity.replaceFragment(new HomeFragment(), false);
        if(isDone)
            mCommonMethod.setNotificationForAppointment(mStringAppointmentID, mStringDateTime, mStringProviderName);
    }

    public class PaymentJavascriptInterface {
        Context mContext;

        PaymentJavascriptInterface(Context c) {
            mContext = c;
        }

        @JavascriptInterface
        public void onPaymentDone(String msg) {
            mActivity.getAppAlertDialog().showAlertWithSingleButton(getString(R.string.app_name), msg,
                    getString(R.string.lbl_ok), new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    finishProcess(true);
                }
            });
        }
    }

}
