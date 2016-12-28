package beautician.beauty.android.views;


import android.app.Dialog;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;
import com.twitter.sdk.android.tweetcomposer.TweetComposer;

import java.io.File;

import beautician.beauty.android.R;
import beautician.beauty.android.activities.MyFragmentActivity;
import beautician.beauty.android.utilities.CommonMethod;


public class ChangeLanguageDialogFragment extends DialogFragment {

    private MyFragmentActivity mActivity;
    private Dialog mDialog;

    private ImageView mImageViewCloase;
    private TextView mTextViewTitle;
    private TextView mTextViewFacebook;
    private TextView mTextViewTwitter;
    private TextView mTextViewWhatsapp;
    private TextView mTextViewInstagram;

    int mCurrentLangPosition = -1;


    public ChangeLanguageDialogFragment() {

    }


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        mActivity = (MyFragmentActivity) getActivity();
        mDialog = new Dialog(mActivity);



        View view = mActivity.getLayoutInflater().inflate(R.layout.dialog_sharing, null);

        mDialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        mDialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        mDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        mDialog.setContentView(view);
        setCancelable(true);
        mDialog.show();


        mImageViewCloase = (ImageView)mDialog.findViewById(R.id.dialog_sharing_imageview_cloase);
        mTextViewTitle = (TextView)mDialog.findViewById(R.id.dialog_sharing_textview_title);
        mTextViewFacebook = (TextView)mDialog.findViewById(R.id.dialog_sharing_textview_facebook);
        mTextViewTwitter = (TextView)mDialog.findViewById(R.id.dialog_sharing_textview_twitter);
        mTextViewWhatsapp = (TextView)mDialog.findViewById(R.id.dialog_sharing_textview_whatsapp);
        mTextViewInstagram = (TextView)mDialog.findViewById(R.id.dialog_sharing_textview_instagram);
        mTextViewTitle.setText(R.string.app_name);
        mTextViewFacebook.setText("English");
        mTextViewTwitter.setText("Arabic");

        if (mActivity.getMyApplication().getCurrentLang().equalsIgnoreCase("en")) {
            mCurrentLangPosition = 0;
            mTextViewFacebook.setCompoundDrawablesWithIntrinsicBounds(ContextCompat.getDrawable(mActivity, R.drawable.icon_radio_btn_selected), null, null, null);
            mTextViewTwitter.setCompoundDrawablesWithIntrinsicBounds(ContextCompat.getDrawable(mActivity, R.drawable.icon_radio_btn_unselect), null, null, null);
        }
        else {
            mCurrentLangPosition = 1;
            mTextViewTwitter.setCompoundDrawablesWithIntrinsicBounds(ContextCompat.getDrawable(mActivity, R.drawable.icon_radio_btn_selected), null, null, null);
            mTextViewFacebook.setCompoundDrawablesWithIntrinsicBounds(ContextCompat.getDrawable(mActivity, R.drawable.icon_radio_btn_unselect), null, null, null);
        }

        mImageViewCloase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                mDialog.cancel();
            }
        });

        mTextViewFacebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeLanguage(0);
                mDialog.cancel();
            }
        });

        mTextViewTwitter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeLanguage(1);
                mDialog.cancel();
            }
        });



        mTextViewWhatsapp.setVisibility(View.GONE);
        mTextViewInstagram.setVisibility(View.GONE);


        return mDialog;
    }

    public void changeLanguage(int pos)
    {
        if (pos == 0) {
            mActivity.getMyApplication().changeLanguage("en");
        } else if (pos == 1) {
            mActivity.getMyApplication().changeLanguage("ar");
        }
        if (mCurrentLangPosition != pos) {
            Intent intent = mActivity.getIntent();
            mActivity.finish();
            startActivity(intent);
        }
    }
}
