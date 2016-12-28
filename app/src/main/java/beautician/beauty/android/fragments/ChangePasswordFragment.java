package beautician.beauty.android.fragments;

import android.annotation.SuppressLint;
import beautician.beauty.android.views.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import beautician.beauty.android.R;
import beautician.beauty.android.activities.MyFragmentActivity;
import beautician.beauty.android.parsers.LoginParser;

@SuppressLint("InflateParams")
public class ChangePasswordFragment extends Fragment implements OnClickListener {

    private MyFragmentActivity mActivity;
    private View rootView;

    private BackProcessChangePassword mBackProcessChangePassword;
    private ProgressDialog mProgressDialog;
    private LoginParser mLoginParserChangePassword;


    private EditText mEditTextOldPassword;
    private EditText mEditTextNewPassword;
    private EditText mEditTextConfirmPassword;
    private TextView mTextViewSubmit;

    private String mStringOldPassword="";
    private String mStringPassword="";
    private String mStringConfirmPassword="";

    public ChangePasswordFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_change_password, container, false);

        mActivity = (MyFragmentActivity) getActivity();
        mActivity.setHeaderTitle(R.string.lbl_change_password);
        mActivity.setSearchIcon(R.drawable.icon_search);
        mActivity.onBackButton(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mActivity.replaceFragment(new SearchFragment(),true);
            }
        });
        mActivity.setTouchForHideKeyboard(rootView);
        mLoginParserChangePassword = new LoginParser();
        getWidgetRefrence(rootView);
        registerOnClick();

        return rootView;
    }

    /**
     * Method call will get IDs from xml file.
     *
     * @param v
     */
    private void getWidgetRefrence(View v) {
        mEditTextOldPassword = (EditText)v. findViewById(R.id.fragment_change_password_edittext_old_password);
        mEditTextNewPassword = (EditText)v. findViewById(R.id.fragment_change_password_edittext_password);
        mEditTextConfirmPassword = (EditText)v. findViewById(R.id.fragment_change_password_edittext_confirm_password);
        mTextViewSubmit = (TextView)v. findViewById(R.id.fragment_change_password_textview_save);
    }

    /**
     * Method call will Register OnClick() Events for widgets.
     */
    private void registerOnClick() {
        mTextViewSubmit.setOnClickListener(this);
    }

    /**
     * Method call OnClick Event fire.
     */
    @Override
    public void onClick(View v) {
        if (v == mTextViewSubmit) {

            callChangePassword();

        }
    }

    /**
     * Method call will check validation of black field.
     */
    public void callChangePassword() {

        mStringOldPassword = mEditTextOldPassword.getText().toString().trim();
        mStringPassword = mEditTextNewPassword.getText().toString().trim();
        mStringConfirmPassword = mEditTextConfirmPassword.getText().toString().trim();


        if (mActivity.getAppAlertDialog().validateBlankField(mEditTextOldPassword, mActivity, getString(R.string.validation_password))
                && mActivity.getAppAlertDialog().validateBlankField(mEditTextNewPassword, mActivity, getString(R.string.validation_password))
                && mActivity.getAppAlertDialog().validateBlankField(mEditTextConfirmPassword, mActivity, getString(R.string.validation_confirm_password))) {
            if (mStringPassword.equalsIgnoreCase(mStringConfirmPassword)) {
                mActivity.getAppAlertDialog().HideKeyboard(mEditTextConfirmPassword);
                mBackProcessChangePassword = new BackProcessChangePassword();
                mBackProcessChangePassword.execute("");
            } else {
                mActivity.getAppAlertDialog().showDialog(getString(R.string.app_name), getString(R.string.validation_confirm_password_not_match), false);
            }
        } else {
            mActivity.getAppAlertDialog().showKeyboard(mEditTextOldPassword);
        }

    }


    /**
     * AsyncTask for calling webservice in background.
     *
     * @author npatel
     */
    public class BackProcessChangePassword extends AsyncTask<String, Void, String> {


        @Override
        protected void onPreExecute() {
            mProgressDialog = ProgressDialog.show(mActivity, "", getString(R.string.dialog_loading), true);
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {

            mLoginParserChangePassword = (LoginParser)mActivity.getWebMethod().callChangePasswordWithOld(mActivity.getMyApplication().getUserFiled(getString(R.string.sp_user_id)),
                    mActivity.getMyApplication().getUserFiled(getString(R.string.sp_user_hash)),
                    mStringOldPassword,
                    mStringPassword,
                    mLoginParserChangePassword);


            return null;

        }

        @Override
        protected void onPostExecute(String result) {


            if (mProgressDialog != null)
                mProgressDialog.dismiss();

            if (mActivity.getWebMethod().isNetError) {
                mActivity.getAppAlertDialog().showDialog("", getString(R.string.validation_no_internet), false);
            } else if (mActivity.getWebMethod().isError) {
                mActivity.getAppAlertDialog().showDialog("", getString(R.string.validation_failed), false);
            } else {
                try {
                    if (mLoginParserChangePassword.getWs_status().equalsIgnoreCase("true")) {

                        mActivity.getAppAlertDialog().showDialog("", mLoginParserChangePassword.getMessage(), true);

                    } else {
                        mActivity.getAppAlertDialog().showDialog("", mLoginParserChangePassword.getMessage(), false);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            super.onPostExecute(result);
        }
    }
}
