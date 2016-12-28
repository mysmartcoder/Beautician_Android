package beautician.beauty.android.views;


import android.app.Dialog;
import beautician.beauty.android.views.ProgressDialog;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.tweetcomposer.TweetComposer;

import java.io.File;
import java.util.Calendar;
import java.util.StringTokenizer;

import io.fabric.sdk.android.Fabric;
import beautician.beauty.android.R;
import beautician.beauty.android.activities.MyFragmentActivity;
import beautician.beauty.android.utilities.CommonMethod;


public class SharingDialogFragment extends DialogFragment {

    private MyFragmentActivity mActivity;
    private Dialog mDialog;

    private ImageView mImageViewCloase;
    private TextView mTextViewFacebook;
    private TextView mTextViewTwitter;
    private TextView mTextViewWhatsapp;
    private TextView mTextViewInstagram;

    private String mStringName="";
    private String mStringLocation="";
    private String mStringImage="";
    private String mStringId="";
    private String mStringServiceId="";
    StringBuilder mStringBuilderMessage;
    private ProgressDialog mProgressDialog;
    private CommonMethod mCommonMethod;
    private String file_name = "";
    String mStringFilePath = "";

    String mStringProviderURL = "";

    public SharingDialogFragment() {

    }


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        mActivity = (MyFragmentActivity) getActivity();
        mDialog = new Dialog(mActivity);
        View view = mActivity.getLayoutInflater().inflate(R.layout.dialog_sharing, null);

        mCommonMethod = new CommonMethod(mActivity);
        mStringName = getArguments().getString(getString(R.string.bundle_provider_name));
        mStringLocation = getArguments().getString(getString(R.string.bundle_location));
        mStringImage = getArguments().getString(getString(R.string.bundle_provider_image));
        mStringId = getArguments().getString(getString(R.string.bundle_provider_id));
        mStringServiceId = getArguments().getString(getString(R.string.bundle_service_id));

        if(mStringServiceId.length() > 0)
            mStringProviderURL = "http://beautician.com/provider?p=" + mStringId+"&s="+mStringServiceId;
        else
            mStringProviderURL = "http://beautician.com/provider?p=" + mStringId;


        mStringBuilderMessage = new StringBuilder();
        mStringBuilderMessage.append(mStringName);
        mStringBuilderMessage.append("\n");
        mStringBuilderMessage.append(mStringLocation);
        mStringBuilderMessage.append("\n");
        mStringBuilderMessage.append(mStringProviderURL);

        file_name = "_"+mStringId+".png";

        mDialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        mDialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        mDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        mDialog.setContentView(view);
        setCancelable(true);
        mDialog.show();



        mImageViewCloase = (ImageView)mDialog.findViewById(R.id.dialog_sharing_imageview_cloase);
        mTextViewFacebook = (TextView)mDialog.findViewById(R.id.dialog_sharing_textview_facebook);
        mTextViewTwitter = (TextView)mDialog.findViewById(R.id.dialog_sharing_textview_twitter);
        mTextViewWhatsapp = (TextView)mDialog.findViewById(R.id.dialog_sharing_textview_whatsapp);
        mTextViewInstagram = (TextView)mDialog.findViewById(R.id.dialog_sharing_textview_instagram);

        mImageViewCloase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mDialog.cancel();
            }
        });

        mTextViewFacebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                sharePhotoToFacebook();
                mDialog.cancel();
            }
        });

        mTextViewTwitter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                shareOnTwitter();
                mDialog.cancel();

            }
        });

        mTextViewWhatsapp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareOnWhatsApp();
                mDialog.cancel();
            }
        });

        mTextViewInstagram.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareOnInstagram();
                mDialog.cancel();
            }
        });

        mTextViewWhatsapp.setVisibility(checkAppInstalled("com.whatsapp") ? View.VISIBLE : View.GONE);
        mTextViewInstagram.setVisibility(checkAppInstalled("com.instagram.android")? View.GONE : View.GONE);

        new BackProcessGetPicture().execute("");


        return mDialog;
    }

    private void shareOnTwitter()
    {
        TweetComposer.Builder builder = new TweetComposer.Builder(mActivity)
                .text(mStringBuilderMessage.toString())
                .image(Uri.parse(mStringFilePath));
        builder.show();
    }

    private void shareOnInstagram()
    {
        Intent targetedShareIntent = new Intent(android.content.Intent.ACTION_SEND);
        targetedShareIntent.setType("image/*");
        targetedShareIntent.putExtra(Intent.EXTRA_SUBJECT, mStringName);
        targetedShareIntent.putExtra(Intent.EXTRA_TEXT, mStringBuilderMessage.toString());
        targetedShareIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse("file://"+mStringFilePath));
        targetedShareIntent.setPackage("com.instagram.android");
        mActivity.startActivity(targetedShareIntent);
    }

    private void shareOnWhatsApp()
    {
        Intent targetedShareIntent = new Intent(android.content.Intent.ACTION_SEND);
        targetedShareIntent.setType("image/*");
        targetedShareIntent.putExtra(Intent.EXTRA_TEXT, mStringBuilderMessage.toString());
        targetedShareIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse(mStringFilePath));
        targetedShareIntent.setPackage("com.whatsapp");
        mActivity.startActivity(targetedShareIntent);
    }

    private void sharePhotoToFacebook() {

        ShareLinkContent shareLinkContent = new ShareLinkContent.Builder()
                .setContentTitle(mStringName)
                .setContentDescription(mStringLocation)
                .setContentUrl(Uri.parse(mStringProviderURL))
                .setImageUrl(Uri.parse(mStringImage))
                .build();

        ShareDialog.show(mActivity, shareLinkContent);

    }

    private boolean checkAppInstalled(String packagename){
        boolean installed = false;
        try {
            ApplicationInfo info = mActivity.getPackageManager().getApplicationInfo(packagename, 0);
            installed = true;
        } catch (PackageManager.NameNotFoundException e) {
            installed = false;
        }
        return installed;
    }

    /**
     * AsyncTask for calling webservice in background.
     *
     * @author npatel
     */
    public class BackProcessGetPicture extends AsyncTask<String, Void, String> {



        @Override
        protected void onPreExecute() {
            mProgressDialog = ProgressDialog.show(mActivity, "", getString(R.string.dialog_loading), true);
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {
                File imgFile = new File(mCommonMethod.getImageDirectory(getString(R.string.folder_share)) + "/" + file_name);
                mStringFilePath = mCommonMethod.downloadFile(mStringImage, imgFile.getAbsolutePath());
            return null;

        }

        @Override
        protected void onPostExecute(String result) {

            if (mProgressDialog != null)
                mProgressDialog.dismiss();

            super.onPostExecute(result);
        }
    }

}
