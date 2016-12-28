package beautician.beauty.android.fragments;

import android.annotation.SuppressLint;
import beautician.beauty.android.views.ProgressDialog;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import beautician.beauty.android.R;
import beautician.beauty.android.activities.MyFragmentActivity;
import beautician.beauty.android.utilities.WebMethod;


@SuppressLint("InflateParams")
public class BuyAdsDepositFragment extends Fragment implements OnClickListener {

    private MyFragmentActivity mActivity;
    private View rootView;


    private String mStringURL = WebMethod.mStringURLBuyAds;
//    private String mStringURL = "http://beauticianapp.com/api/index.php?api=buyadspaymentform&";


    private String mStringBuyAdsID = "";
    private String mStringReedemAmount = "";
    private String mStringPayAmount = "";
    private String mStringPayType = "";

    private WebView mWebView;
    private ProgressBar mProgress;
    private ProgressDialog mProgressDialog;

    public BuyAdsDepositFragment() {

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


        mStringPayAmount = getArguments().getString(getString(R.string.bundle_pay_amount));
        mStringReedemAmount = getArguments().getString(getString(R.string.bundle_reedem_amount));
        mStringPayType = getArguments().getString(getString(R.string.bundle_pay_type));
        mStringBuyAdsID = getArguments().getString(getString(R.string.bundle_ads_id));

        StringBuilder mStringBuilder = new StringBuilder();
        mStringBuilder.append(mStringURL);
        mStringBuilder.append("user_id=");
        mStringBuilder.append(mActivity.getMyApplication().getUserFiled(getString(R.string.sp_user_id)));
        mStringBuilder.append("&token=");
        mStringBuilder.append(mActivity.getMyApplication().getUserFiled(getString(R.string.sp_user_hash)));
        mStringBuilder.append("&featureid=");
        mStringBuilder.append(mStringBuyAdsID);
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
     *
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

            Log.e("DEP : ", url);
            if (url.contains("thankyou.php")) {
                finishProcess();
                return true;
            }else if(url.contains("form.php"))
            {
                mActivity.getAppAlertDialog().showAlertWithSingleButton(getString(R.string.app_name), getString(R.string.alt_msg_buyads_failed),
                        getString(R.string.lbl_ok), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                finishProcess();
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
        }
        else {
            this.mProgress.setVisibility(View.VISIBLE);
            if(!mProgressDialog.isShowing()) {
                mProgressDialog = ProgressDialog.show(mActivity, "", getString(R.string.dialog_loading), true);
                mProgressDialog.setCancelable(true);
            }
        }
    }

    public void finishProcess() {
        mActivity.removePreviousFragment();
        mActivity.replaceFragment(new HomeFragment(), false);
    }

}
