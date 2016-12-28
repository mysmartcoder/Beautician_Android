package beautician.beauty.android.views;


import android.app.Dialog;
import beautician.beauty.android.views.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import beautician.beauty.android.R;
import beautician.beauty.android.activities.MyFragmentActivity;
import beautician.beauty.android.parsers.LoginParser;
import beautician.beauty.android.utilities.StaticData;


public class ReportDialogFragment extends DialogFragment {

    private MyFragmentActivity mActivity;
    private Dialog mDialog;
    private Dialog mDialogReportOther;


    private ImageView mImageViewCloase;
    private TextView mTextViewOffensive;
    private TextView mTextViewOutofTopic;
    private TextView mTextViewOther;
    private TextView mTextViewInstagram;

    private String mStringReportUserId ="";
    private String mStringReportType ="";
    private String mStringReportContentId ="";
    private String mStringReportContentType ="";
    private String mStringReportComment ="";

    private ProgressDialog mProgressDialog;
    private LoginParser mLoginParser;

    public ReportDialogFragment() {

    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        mActivity = (MyFragmentActivity) getActivity();
        mDialog = new Dialog(mActivity);
        View view = mActivity.getLayoutInflater().inflate(R.layout.dialog_sharing, null);

        mStringReportUserId = getArguments().getString(getString(R.string.bundle_report_userid));
        mStringReportType = getArguments().getString(getString(R.string.bundle_report_type));
        mStringReportContentId = getArguments().getString(getString(R.string.bundle_report_contentid));

        mLoginParser = new LoginParser();

        mDialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        mDialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        mDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        mDialog.setContentView(view);
        setCancelable(true);
        mDialog.show();


        TextView mTextViewDialogTitle = (TextView)mDialog.findViewById(R.id.dialog_sharing_textview_title);
        mTextViewDialogTitle.setText(R.string.lbl_report);

        mImageViewCloase = (ImageView)mDialog.findViewById(R.id.dialog_sharing_imageview_cloase);
        mTextViewOffensive = (TextView)mDialog.findViewById(R.id.dialog_sharing_textview_facebook);
        mTextViewOffensive.setText(R.string.lbl_report_offensive);
        mTextViewOutofTopic = (TextView)mDialog.findViewById(R.id.dialog_sharing_textview_twitter);
        mTextViewOutofTopic.setText(R.string.lbl_report_out_topic);
        mTextViewOther = (TextView)mDialog.findViewById(R.id.dialog_sharing_textview_whatsapp);
        mTextViewOther.setText(R.string.lbl_report_other);
        mTextViewInstagram = (TextView)mDialog.findViewById(R.id.dialog_sharing_textview_instagram);
        mTextViewInstagram.setVisibility(View.GONE);

        mImageViewCloase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mDialog.cancel();
            }
        });

        mTextViewOffensive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mStringReportContentType = StaticData.REPORT_OFFENSIVE;
                mStringReportComment = "";
                new BackProcessReport().execute("");
            }
        });

        mTextViewOutofTopic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mStringReportContentType = StaticData.REPORT_OUT_OF_TOPIC;
                mStringReportComment = "";
                new BackProcessReport().execute("");

            }
        });

        mTextViewOther.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mStringReportContentType = StaticData.REPORT_OTHER;
                mStringReportComment = "";
                showReportOtherDialog();

            }
        });

        return mDialog;
    }


    /**
     * Method will open report dialog.
     */
    public void showReportOtherDialog()
    {
        mDialogReportOther = new Dialog(mActivity);
        mDialogReportOther.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mDialogReportOther.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        mDialogReportOther.setContentView(R.layout.dialog_report_other);
//        Window window = mDialogReportOther.getWindow();
        mDialogReportOther.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        ImageView mImageViewCloase = (ImageView) mDialogReportOther.findViewById(R.id.dialog_report_other_imageview_cloase);
        mImageViewCloase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mDialogReportOther.cancel();
            }
        });

        final EditText mEditTextComment = (EditText) mDialogReportOther.findViewById(R.id.dialog_report_other_edittext_comment);
        TextView mTextViewProvider = (TextView) mDialogReportOther.findViewById(R.id.dialog_report_other_textview_submit);
        mTextViewProvider.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mStringReportComment = mEditTextComment.getText().toString().trim();
                if(mStringReportComment.length() > 0)
                {
                    new BackProcessReport().execute("");
                    mDialogReportOther.cancel();
                }
            }
        });
        mDialogReportOther.show();
    }


    /**
     * AsyncTask for calling webservice in background.
     * @author npatel
     */
    public class BackProcessReport extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            mProgressDialog = ProgressDialog.show(mActivity, "", getString(R.string.dialog_loading), true);
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {

            mLoginParser = (LoginParser)mActivity.getWebMethod().callReport(mActivity.getMyApplication().getUserFiled(getString(R.string.sp_user_id)),
                    mActivity.getMyApplication().getUserFiled(getString(R.string.sp_user_hash)),
                    mStringReportUserId,
                    mStringReportType,
                    mStringReportContentType,
                    mStringReportContentId,
                    mStringReportComment,
                    mLoginParser);

            return null;

        }

        @Override
        protected void onPostExecute(String result) {

            if (mProgressDialog != null)
                mProgressDialog.dismiss();

            mDialog.cancel();
            if (mActivity.getWebMethod().isNetError) {
                mActivity.getAppAlertDialog().showDialog("", getString(R.string.validation_no_internet), false);
            } else if (mActivity.getWebMethod().isError) {
                mActivity.getAppAlertDialog().showDialog("", getString(R.string.validation_failed), false);
            } else {
                if (mLoginParser.getWs_status().equalsIgnoreCase("true")) {
                    mActivity.getAppAlertDialog().showDialog(getString(R.string.app_name), mLoginParser.getMessage().toString(), false);
                } else {
                    if (mLoginParser.getMessage().toString().contains(getString(R.string.alt_msg_exipred))) {
                        mActivity.getAppAlertDialog().showAlertWithSingleButton("", mLoginParser.getMessage().toString(),
                                mActivity.getString(R.string.lbl_logout),
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        mActivity.logout();
                                    }
                                });
                    } else {
                        mActivity.getAppAlertDialog().showDialog(getString(R.string.app_name), mLoginParser.getMessage().toString(), false);
                    }
                }
            }

            super.onPostExecute(result);
        }
    }

}
