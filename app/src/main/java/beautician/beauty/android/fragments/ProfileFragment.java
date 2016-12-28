package beautician.beauty.android.fragments;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.Dialog;
import beautician.beauty.android.views.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;

import java.io.File;

import beautician.beauty.android.R;
import beautician.beauty.android.activities.MyFragmentActivity;
import beautician.beauty.android.helper.IncomingMessageReceiver;
import beautician.beauty.android.parsers.LoginParser;
import beautician.beauty.android.utilities.CommonMethod;
import beautician.beauty.android.views.CircleImageView;
import beautician.beauty.android.views.CropImageView;

@SuppressLint("InflateParams")
public class ProfileFragment extends Fragment implements OnClickListener {

    private MyFragmentActivity mActivity;
    private CommonMethod mCommonMethod;
    private View rootView;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    private ImageLoader mImageLoader;
    private DisplayImageOptions mDisplayImageOptions;

    private BackProcessGetProfile mBackProcessGetProfile;
    private ProgressDialog mProgressDialog;

    private SharedPreferences mSharedPreferences;
    private SharedPreferences.Editor mEditor;

    private CircleImageView mCircleImageViewUserPic;
    private RatingBar mRatingBarSatisfying;
    private RatingBar mRatingBarCommited;
    private TextView mTextViewMyService;
    private TextView mTextViewUserName;
    private TextView mTextViewSetting;
    private TextView mTextViewChangePassword;
    private TextView mTextViewUpdateUserName;
    private TextView mTextViewEmail;
    private TextView mTextViewMobile;
    private TextView mTextViewWallet;

    private TextView mTextViewDialogVerfyCode;
    private EditText mEditTextVerifyCode;
    private TextView mTextViewDialogSubmit;
    private EditText mEditTextEmail;

    private Intent mIntentPictureAction = null;
    private Bitmap mBitmap = null;
    protected static final int CAMERA_REQUEST = 0;
    protected static final int GALLERY_PICTURE = 1;
    private Dialog mDialog;
    private Dialog mDialogUpdateProfile;
    private Dialog mDialogPicImage;

    private String mStringProfilePicturePath = "";
    private String mStringProfilePicName = "";
    private String mStringUpdateValue = "";
    private String mStringVarifyCode = "";
    private String mMethodUpdateProfilePic = "UpdateProfilePic";
    private String mMethodUpdateUserName = "UpdateUserName";
    private String mMethodUpdateEmail = "UpdateEmail";
    private String mMethodUpdateMobileNo = "UpdateMobileNo";
    private String mMethodSendVerifyCode = "SendVerifyCode";
    private String mMethodGetProfile = "GetProfile";
    private String mStringType = "";

    private LoginParser mLoginParserGetProfile;
    private LoginParser mLoginParser;
    private LoginParser mUpdateUserNamePrarser;
    private LoginParser mUpdateEmailOrPhone;
    private LoginParser mVerificationCodePrarser;

    IntentFilter messageFilter;
    private IncomingMessageReceiver mSMSreceiver;
    private IntentFilter mIntentFilter;

    public ProfileFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_profile, container, false);

        mActivity = (MyFragmentActivity) getActivity();
        mActivity.setHeaderTitle(R.string.lbl_profile);
        mActivity.setSearchIcon(R.drawable.icon_search);
        mActivity.onBackButton(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mActivity.replaceFragment(new SearchFragment(), true);
            }
        });
        mCommonMethod = new CommonMethod(getActivity());

        mSharedPreferences = mActivity.getSharedPreferences(getString(R.string.sp_file_name), Context.MODE_PRIVATE);
        mEditor = mSharedPreferences.edit();


        getWidgetRefrence(rootView);
        registerOnClick();

        mDisplayImageOptions = new DisplayImageOptions.Builder()
                .showImageForEmptyUri(R.drawable.bg_user_profile)
                .showImageOnFail(R.drawable.bg_user_profile)
                .cacheInMemory(true)
                .displayer(new RoundedBitmapDisplayer(20))
                .considerExifParams(true)
                .resetViewBeforeLoading(true)
                .cacheOnDisc(true)
                .imageScaleType(ImageScaleType.EXACTLY)
                .displayer(new FadeInBitmapDisplayer(200))
                .build();

        mLoginParser = new LoginParser();
        mUpdateUserNamePrarser = new LoginParser();
        mUpdateEmailOrPhone = new LoginParser();
        mVerificationCodePrarser = new LoginParser();
        mLoginParserGetProfile = new LoginParser();

        new BackProcessGetProfile().execute(mMethodGetProfile);

        registerBroadcast();

        return rootView;
    }

    /**
     * Method call will set data..
     */
    private void setData() {

        mTextViewUpdateUserName.setText(mActivity.getMyApplication().getUserFiled(getString(R.string.sp_user_name)));
        ImageLoader.getInstance().displayImage(mActivity.getMyApplication().getUserFiled(getString(R.string.sp_user_pic)), mCircleImageViewUserPic);
        mTextViewUserName.setText(mActivity.getMyApplication().getUserFiled(getString(R.string.sp_user_name)));
        mTextViewEmail.setText(mActivity.getMyApplication().getUserFiled(getString(R.string.sp_user_email)));
        mTextViewMobile.setText(mActivity.getMyApplication().getUserFiled(getString(R.string.sp_user_mobile)));
        try {
            mRatingBarSatisfying.setRating(Float.parseFloat(mActivity.getMyApplication().getUserFiled(getString(R.string.sp_satisfy_rating))));
            mRatingBarCommited.setRating(Float.parseFloat(mActivity.getMyApplication().getUserFiled(getString(R.string.sp_committed_rating))));
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }

        if(mActivity.getMyApplication().getUserProfile()!=null && mActivity.getMyApplication().getUserProfile().getData()!=null) {

            if (mActivity.getMyApplication().getUserProfile().getData().getUser_type().equalsIgnoreCase("provider")) {
                mTextViewMyService.setText(R.string.lbl_my_services);
                mTextViewSetting.setVisibility(View.VISIBLE);
            } else {
                mTextViewMyService.setText(R.string.lbl_become_a_provider);
                mTextViewSetting.setVisibility(View.GONE);
            }
            int wallet = (int) (Double.parseDouble(mActivity.getMyApplication().getUserProfile().getData().getTotalcredit()));
            mTextViewWallet.setText("SR " + String.valueOf(wallet));
        }
    }


    /**
     * Method call will get IDs from xml file.
     *
     * @param v
     */
    private void getWidgetRefrence(View v) {
        mSwipeRefreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.fragment_profile_swiperefresh);
        mCircleImageViewUserPic = (CircleImageView) v.findViewById(R.id.fragment_profile_imageview_userpic);
        mRatingBarSatisfying = (RatingBar) v.findViewById(R.id.view_rating_rating_satification);
        mRatingBarCommited = (RatingBar) v.findViewById(R.id.view_rating_rating_commitment);
        mTextViewMyService = (TextView) v.findViewById(R.id.fragment_profile_textview_select_service);
        mTextViewUserName = (TextView) v.findViewById(R.id.fragment_profile_textview_username);
        mTextViewSetting = (TextView) v.findViewById(R.id.fragment_profile_textview_setting);
        mTextViewChangePassword = (TextView) v.findViewById(R.id.fragment_profile_textview_change_password);
        mTextViewUpdateUserName = (TextView) v.findViewById(R.id.fragment_profile_edittext_username);
        mTextViewEmail = (TextView) v.findViewById(R.id.fragment_profile_edittext_email);
        mTextViewMobile = (TextView) v.findViewById(R.id.fragment_profile_edittext_mobile);
        mTextViewWallet = (TextView) v.findViewById(R.id.fragment_profile_textview_wallet);

        setData();
    }

    /**
     * Method call will Register OnClick() Events for widgets.
     */
    private void registerOnClick() {

        mStringProfilePicName = String.valueOf(System.currentTimeMillis()) + ".png";
        mCircleImageViewUserPic.setOnClickListener(this);
        mTextViewSetting.setOnClickListener(this);
        mTextViewMyService.setOnClickListener(this);
        mTextViewChangePassword.setOnClickListener(this);
        mTextViewUpdateUserName.setOnClickListener(this);
        mTextViewEmail.setOnClickListener(this);
        mTextViewMobile.setOnClickListener(this);
        mSwipeRefreshLayout.setColorSchemeColors(ContextCompat.getColor(mActivity, R.color.myPrimaryColor), ContextCompat.getColor(mActivity, R.color.golden));
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mSwipeRefreshLayout.setRefreshing(true);
                new BackProcessGetProfile().execute(mMethodGetProfile);
            }
        });
    }

    /**
     * Method call OnClick Event fire.
     */
    @Override
    public void onClick(View v) {

        if (v == mCircleImageViewUserPic) {

            showProfilePicImageDialgo();

        } else if (v == mTextViewMyService) {

            if (mActivity.getMyApplication().getUserProfile().getData().getUser_type().equalsIgnoreCase("provider"))
                mActivity.replaceFragment(new MyServiceFragment(), true);
            else
                mActivity.replaceFragment(new ProviderSettingsFragment(), true);

        } else if (v == mTextViewSetting) {

            mActivity.replaceFragment(new ProviderSettingsFragment(), true);

        } else if (v == mTextViewChangePassword) {

            mActivity.replaceFragment(new ChangePasswordFragment(), true);

        } else if (v == mTextViewUpdateUserName) {

            showUpdateEmai(getString(R.string.lbl_edit_username), mMethodUpdateUserName);

        } else if (v == mTextViewEmail) {

            showUpdateEmai(getString(R.string.lbl_edit_emial), mMethodUpdateEmail);

        } else if (v == mTextViewMobile) {
            int permissionCheck = ContextCompat.checkSelfPermission(mActivity, Manifest.permission.RECEIVE_SMS);
            if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
                showUpdateEmai(getString(R.string.lbl_edit_mobile), mMethodUpdateMobileNo);
            } else {
                ActivityCompat.requestPermissions(mActivity, new String[]{Manifest.permission.RECEIVE_SMS}, 123);
            }
        }
    }


    /**
     * Method call will show update email dialog
     */

    private void showUpdateEmai(String mStringTitle, final String mStringFrom) {
        mDialogUpdateProfile = new Dialog(mActivity);
        mDialogUpdateProfile.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mDialogUpdateProfile.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        mDialogUpdateProfile.setContentView(R.layout.dialog_update_email);
        Window window = mDialogUpdateProfile.getWindow();
        window.setLayout(ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.MATCH_PARENT);
        final TextView mTextViewDialogTitle = (TextView) mDialogUpdateProfile.findViewById(R.id.dialog_update_email_textview_title);
        mTextViewDialogSubmit = (TextView) mDialogUpdateProfile.findViewById(R.id.dialog_update_email_textview_submit);
        mTextViewDialogVerfyCode = (TextView) mDialogUpdateProfile.findViewById(R.id.dialog_update_email_textview_rquest_code);
        mEditTextEmail = (EditText) mDialogUpdateProfile.findViewById(R.id.dialog_update_email_edittext_email);
        mEditTextVerifyCode = (EditText) mDialogUpdateProfile.findViewById(R.id.dialog_update_email_edittext_verify_code);
        mTextViewDialogTitle.setText(mStringTitle);

        if (mStringFrom.equalsIgnoreCase(mMethodUpdateEmail)) {

            mEditTextEmail.setHint(getString(R.string.hint_enter_new_emailid));
            mTextViewDialogVerfyCode.setVisibility(View.GONE);
            mTextViewDialogSubmit.setVisibility(View.VISIBLE);
            mStringType = "email";

        } else if (mStringFrom.equalsIgnoreCase(mMethodUpdateMobileNo)) {

            mEditTextEmail.setHint(getString(R.string.hint_enter_mobile_no));
            mEditTextEmail.setInputType(InputType.TYPE_CLASS_PHONE);
            mTextViewDialogVerfyCode.setVisibility(View.VISIBLE);
            mTextViewDialogSubmit.setVisibility(View.GONE);
            mStringType = "phone";

        } else if (mStringFrom.equalsIgnoreCase(mMethodUpdateUserName)) {

            mEditTextEmail.setHint(getString(R.string.hint_enter_new_username));
            mEditTextEmail.setText(mActivity.getMyApplication().getUserFiled(getString(R.string.sp_user_name)));
            mEditTextVerifyCode.setVisibility(View.GONE);
            mTextViewDialogVerfyCode.setVisibility(View.GONE);

        }
        int txtPosition = mEditTextEmail.getText().length();
        mEditTextEmail.setSelection(txtPosition);

        mTextViewDialogSubmit.setOnClickListener(new OnClickListener() {
                                                     @Override
                                                     public void onClick(View v) {

                                                         mStringUpdateValue = mEditTextEmail.getText().toString().trim();
                                                         mStringVarifyCode = mEditTextVerifyCode.getText().toString().trim();

                                                         if (mStringFrom.equalsIgnoreCase(mMethodUpdateUserName)) {
                                                             if (mActivity.getAppAlertDialog().validateBlankField(mEditTextEmail, mActivity, getString(R.string.validation_usename))) {

                                                                 if(!mStringUpdateValue.toLowerCase().contains("beautician")) {
                                                                     mBackProcessGetProfile = new BackProcessGetProfile();
                                                                     mBackProcessGetProfile.execute(mMethodUpdateUserName);
                                                                 }else
                                                                 {
                                                                     mActivity.getAppAlertDialog().showDialog(getString(R.string.app_name), getString(R.string.validation_usename_beautician), false);
                                                                 }
                                                             }

                                                         } else if (mStringFrom.equalsIgnoreCase(mMethodUpdateEmail)) {

//                                                             if (mActivity.getAppAlertDialog().validateBlankField(mEditTextVerifyCode, mActivity, getString(R.string.validation_verify_code))) {
//                                                                 mBackProcessGetProfile = new BackProcessGetProfile();
//                                                                 mBackProcessGetProfile.execute(mMethodSendVerifyCode);
//                                                             }
                                                             if (mActivity.getAppAlertDialog().checkValidEmail(mStringUpdateValue, mActivity, mEditTextEmail)) {
                                                                 mBackProcessGetProfile = new BackProcessGetProfile();
                                                                 mBackProcessGetProfile.execute(mMethodUpdateEmail);
                                                             }

                                                         } else if (mStringFrom.equalsIgnoreCase(mMethodUpdateMobileNo)) {

                                                             if (mActivity.getAppAlertDialog().validateBlankField(mEditTextVerifyCode, mActivity, getString(R.string.validation_verify_code))) {
                                                                 mBackProcessGetProfile = new BackProcessGetProfile();
                                                                 mBackProcessGetProfile.execute(mMethodSendVerifyCode);
                                                             }
                                                         }

                                                     }
                                                 }

        );
        mTextViewDialogVerfyCode.setOnClickListener(new OnClickListener() {
                                                        @Override
                                                        public void onClick(View v) {

                                                            mStringUpdateValue = mEditTextEmail.getText().toString().trim();

                                                            if (mStringFrom.equalsIgnoreCase(mMethodUpdateEmail)) {

                                                                if (mActivity.getAppAlertDialog().checkValidEmail(mStringUpdateValue, mActivity, mEditTextEmail)) {
                                                                    mBackProcessGetProfile = new BackProcessGetProfile();
                                                                    mBackProcessGetProfile.execute(mMethodUpdateEmail);
                                                                }

                                                            } else if (mStringFrom.equalsIgnoreCase(mMethodUpdateMobileNo)) {

                                                                if (mActivity.getAppAlertDialog().validateBlankField(mEditTextEmail, mActivity, getString(R.string.validation_mobile_no))) {
                                                                    mBackProcessGetProfile = new BackProcessGetProfile();
                                                                    mBackProcessGetProfile.execute(mMethodUpdateMobileNo);
                                                                }
                                                            }

                                                        }
                                                    }

        );
        mDialogUpdateProfile.show();
    }


    /**
     * Method call will display image picker dialog.
     */
    private void showProfilePicImageDialgo() {

        mDialogPicImage = new Dialog(mActivity);
        mDialogPicImage.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mDialogPicImage.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        mDialogPicImage.setContentView(R.layout.dialog_select_picture);
        Window window = mDialogPicImage.getWindow();
        window.setLayout(ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.MATCH_PARENT);
        mDialogPicImage.show();
        final TextView mTextViewInstagram = (TextView) mDialogPicImage.findViewById(R.id.dialog_select_picture_textview_instagram);
        final TextView mTextViewGallery = (TextView) mDialogPicImage.findViewById(R.id.dialog_select_picture_textview_gallery);
        final TextView mTextViewCamera = (TextView) mDialogPicImage.findViewById(R.id.dialog_select_picture_textview_camera);
        final ImageView mTextViewClose = (ImageView) mDialogPicImage.findViewById(R.id.dialog_select_picture_imageview_cancel);
        mTextViewInstagram.setVisibility(View.GONE);
        mTextViewGallery.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                mIntentPictureAction = new Intent(Intent.ACTION_GET_CONTENT, null);
                mIntentPictureAction.setType("image/*");
                mIntentPictureAction.putExtra("return-data", true);
                startActivityForResult(mIntentPictureAction, GALLERY_PICTURE);
                mDialogPicImage.cancel();
            }
        });
        mTextViewCamera.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                File mFile = new File(mCommonMethod.getImageDirectory(getString(R.string.folder_profile_pic)) + "/" + mStringProfilePicName);
                mIntentPictureAction = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                mIntentPictureAction.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(mFile));
                startActivityForResult(mIntentPictureAction, CAMERA_REQUEST);
                mDialogPicImage.cancel();
            }
        });
        mTextViewClose.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                mDialogPicImage.cancel();
            }
        });
    }

    /**
     * Method call will display image picker dialog.
     */
    private void showProfilePicImageDialgo1() {
        AlertDialog.Builder myAlertDialog = new AlertDialog.Builder(getActivity());
        myAlertDialog.setTitle(getString(R.string.alt_msg_Profile_pic));
        myAlertDialog.setMessage(getString(R.string.alt_msg_set_profile_picture));

        myAlertDialog.setPositiveButton(getString(R.string.lbl_gallery), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {
                mIntentPictureAction = new Intent(Intent.ACTION_GET_CONTENT, null);
                mIntentPictureAction.setType("image/*");
                mIntentPictureAction.putExtra("return-data", true);
                startActivityForResult(mIntentPictureAction, GALLERY_PICTURE);
            }
        });

        myAlertDialog.setNegativeButton(getString(R.string.lbl_camera), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {
                File mFile = new File(mCommonMethod.getImageDirectory(getString(R.string.folder_profile_pic)) + "/" + mStringProfilePicName);
                mIntentPictureAction = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                mIntentPictureAction.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(mFile));
                startActivityForResult(mIntentPictureAction, CAMERA_REQUEST);

            }
        });
        myAlertDialog.show();
    }

    /**
     * Methdo call will user get picture from gallery or camera.
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */

    @SuppressWarnings("static-access")
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GALLERY_PICTURE) {
            try {
                if (resultCode == getActivity().RESULT_OK) {
                    if (data != null) {
                        try {
                            mBitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), data.getData());
                            mBitmap = mCommonMethod.resizeBitmap(mBitmap);
                            if (mBitmap != null) {
                                mCommonMethod.getImageDirectory(getString(R.string.folder_profile_pic));
                                mCommonMethod.copyImageFile(mCommonMethod.getImageDirectory(getString(R.string.folder_profile_pic)) + "/" + mStringProfilePicName, mBitmap);
                                showImageDisplayDialog();
                            } else {
                                mCircleImageViewUserPic.setImageResource(R.drawable.bg_user_profile);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    } else {
                        Toast.makeText(getActivity(), getString(R.string.lbl_cancelled), Toast.LENGTH_SHORT).show();
                    }
                } else if (resultCode == getActivity().RESULT_CANCELED) {
                    Toast.makeText(getActivity(), getString(R.string.lbl_cancelled), Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {

            }
        } else if (requestCode == CAMERA_REQUEST) {
            if (resultCode == getActivity().RESULT_OK) {
                File imgFile = new File(mCommonMethod.getImageDirectory(getString(R.string.folder_profile_pic)) + "/" + mStringProfilePicName);
                mBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                mBitmap = mCommonMethod.resizeBitmap(mBitmap);
                showImageDisplayDialog();
            } else if (resultCode == getActivity().RESULT_CANCELED) {
                Toast.makeText(getActivity(), getString(R.string.lbl_cancelled), Toast.LENGTH_SHORT).show();
            }

        }
    }

    /**
     * Method will display image after pick up from gallery or camera
     */
    public void showImageDisplayDialog() {
        mDialog = new Dialog(mActivity, android.R.style.Theme_Translucent_NoTitleBar);
        mDialog.setContentView(R.layout.view_image);
        final CropImageView mImageViewRotate = (CropImageView) mDialog.findViewById(R.id.view_image_imageview_image);
        mImageViewRotate.setImageBitmap(mBitmap);
        TextView mTextViewDone = (TextView) mDialog.findViewById(R.id.view_image_textview_done);

        mTextViewDone.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                mDialog.dismiss();
                mBitmap = mImageViewRotate.getCroppedImage();
                mCommonMethod.copyImageFile(mCommonMethod.getImageDirectory(getString(R.string.folder_profile_pic)) + "/" + mStringProfilePicName, mBitmap);
                mStringProfilePicturePath = mCommonMethod.getImageDirectory(getString(R.string.folder_profile_pic)) + "/" + mStringProfilePicName;
                mCircleImageViewUserPic.setImageBitmap(mBitmap);

                mBackProcessGetProfile = new BackProcessGetProfile();
                mBackProcessGetProfile.execute(mMethodUpdateProfilePic);

            }
        });

        TextView mTextViewRotate = (TextView) mDialog.findViewById(R.id.view_image_textview_rotate);
        mTextViewRotate.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                mImageViewRotate.rotateImage(90);
            }
        });

        mDialog.show();
    }


    /**
     * AsyncTask for calling webservice in background.
     *
     * @author ebaraiya
     */
    public class BackProcessGetProfile extends AsyncTask<String, Void, String> {
        String mCurrentMethod = "";

        @Override
        protected void onPreExecute() {
            mProgressDialog = ProgressDialog.show(mActivity, "", getString(R.string.dialog_loading), true);
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {
            mCurrentMethod = params[0];
            if (mCurrentMethod.equalsIgnoreCase(mMethodUpdateProfilePic)) {
                mLoginParser = (LoginParser) mActivity.getWebMethod().callUploadPic(mActivity.getMyApplication().getUserFiled(getString(R.string.sp_user_id)), mActivity.getMyApplication().getUserFiled(getString(R.string.sp_user_hash)), mStringProfilePicturePath, mLoginParser);

            } else if (mCurrentMethod.equalsIgnoreCase(mMethodUpdateUserName)) {

                mUpdateUserNamePrarser = (LoginParser) mActivity.getWebMethod().callUpdateUserName(mActivity.getMyApplication().getUserFiled(getString(R.string.sp_user_id)), mActivity.getMyApplication().getUserFiled(getString(R.string.sp_user_hash)), mStringUpdateValue, mUpdateUserNamePrarser);

            } else if (mCurrentMethod.equalsIgnoreCase(mMethodUpdateEmail)) {

                mUpdateEmailOrPhone = (LoginParser) mActivity.getWebMethod().callUpdateEmailAndMobileNo(mActivity.getMyApplication().getUserFiled(getString(R.string.sp_user_id)), mActivity.getMyApplication().getUserFiled(getString(R.string.sp_user_hash)), mStringType, mStringUpdateValue, mUpdateEmailOrPhone);

            } else if (mCurrentMethod.equalsIgnoreCase(mMethodUpdateMobileNo)) {

                mUpdateEmailOrPhone = (LoginParser) mActivity.getWebMethod().callUpdateEmailAndMobileNo(mActivity.getMyApplication().getUserFiled(getString(R.string.sp_user_id)), mActivity.getMyApplication().getUserFiled(getString(R.string.sp_user_hash)), mStringType, mStringUpdateValue, mUpdateEmailOrPhone);

            } else if (mCurrentMethod.equalsIgnoreCase(mMethodSendVerifyCode)) {

                mVerificationCodePrarser = (LoginParser) mActivity.getWebMethod().callSendEmailAndMobileVerifyCode(mActivity.getMyApplication().getUserFiled(getString(R.string.sp_user_id)), mActivity.getMyApplication().getUserFiled(getString(R.string.sp_user_hash)), mStringType, mStringUpdateValue, mStringVarifyCode, mVerificationCodePrarser);

            } else if (mCurrentMethod.equalsIgnoreCase(mMethodGetProfile)) {

                mLoginParserGetProfile = (LoginParser) mActivity.getWebMethod().callGetProfile(mActivity.getMyApplication().getUserFiled(getString(R.string.sp_user_id)), mActivity.getMyApplication().getUserFiled(getString(R.string.sp_user_hash)), mLoginParserGetProfile);

            }

            return null;

        }

        @Override
        protected void onPostExecute(String result) {

            mSwipeRefreshLayout.setRefreshing(false);
            if (mProgressDialog != null)
                mProgressDialog.dismiss();

            if (mActivity.getWebMethod().isNetError) {
                mActivity.getAppAlertDialog().showDialog("", getString(R.string.validation_no_internet), false);
            } else if (mActivity.getWebMethod().isError) {
                mActivity.getAppAlertDialog().showDialog("", getString(R.string.validation_failed), false);
            } else {
                try {
                    if (mCurrentMethod.equalsIgnoreCase(mMethodUpdateProfilePic)) {

                        if (mLoginParser.getWs_status().equalsIgnoreCase("true")) {
                            Toast.makeText(mActivity, mLoginParser.getMessage().toString(), Toast.LENGTH_SHORT).show();
                            mEditor.putString(getString(R.string.sp_user_pic), mLoginParser.getImage_url());
                            mEditor.commit();
                        } else {
                            mActivity.getAppAlertDialog().showDialog(getString(R.string.app_name), mLoginParser.getMessage().toString(), false);
                        }
                        mActivity.updatedUserData();


                    } else if (mCurrentMethod.equalsIgnoreCase(mMethodUpdateUserName)) {

                        if (mUpdateUserNamePrarser.getWs_status().equalsIgnoreCase("true")) {
                            Toast.makeText(mActivity, mUpdateUserNamePrarser.getMessage().toString(), Toast.LENGTH_SHORT).show();
                            mEditor.putString(getString(R.string.sp_user_name), mStringUpdateValue);
                            mEditor.commit();
                        } else {
                            mActivity.getAppAlertDialog().showDialog(getString(R.string.app_name), mUpdateUserNamePrarser.getMessage().toString(), false);
                        }
                        setData();
                        mActivity.updatedUserData();
                        mDialogUpdateProfile.dismiss();

                    } else if (mCurrentMethod.equalsIgnoreCase(mMethodUpdateEmail)) {

                        if (mUpdateEmailOrPhone.getWs_status().equalsIgnoreCase("true")) {
//                            mActivity.getAppAlertDialog().showDialog(getString(R.string.app_name), mUpdateEmailOrPhone.getMessage().toString(), false);
//                            mTextViewDialogSubmit.setVisibility(View.VISIBLE);
//                            mEditTextVerifyCode.setVisibility(View.VISIBLE);
//                            mEditTextEmail.setEnabled(false);

                            Toast.makeText(mActivity, mUpdateEmailOrPhone.getMessage().toString(), Toast.LENGTH_SHORT).show();
                            mEditor.putString(getString(R.string.sp_user_email), mStringUpdateValue);
                            mEditor.commit();
                            setData();
                            mActivity.updatedUserData();
                            mDialogUpdateProfile.dismiss();

                        } else {
                            mActivity.getAppAlertDialog().showDialog(getString(R.string.app_name), mUpdateEmailOrPhone.getMessage().toString(), false);
                        }


                    } else if (mCurrentMethod.equalsIgnoreCase(mMethodUpdateMobileNo)) {

                        if (mUpdateEmailOrPhone.getWs_status().equalsIgnoreCase("true")) {
                            mActivity.getAppAlertDialog().showDialog(getString(R.string.app_name), mUpdateEmailOrPhone.getMessage().toString(), false);
                            mTextViewDialogSubmit.setVisibility(View.VISIBLE);
                            mEditTextVerifyCode.setVisibility(View.VISIBLE);
                            mEditTextEmail.setEnabled(false);
                        } else {
                            mActivity.getAppAlertDialog().showDialog(getString(R.string.app_name), mUpdateEmailOrPhone.getMessage().toString(), false);
                        }


                    } else if (mCurrentMethod.equalsIgnoreCase(mMethodSendVerifyCode)) {

                        if (mVerificationCodePrarser.getWs_status().equalsIgnoreCase("true")) {
                            Toast.makeText(mActivity, mVerificationCodePrarser.getMessage().toString(), Toast.LENGTH_SHORT).show();
                            if (mStringType.equalsIgnoreCase("email")) {
                                mEditor.putString(getString(R.string.sp_user_email), mStringUpdateValue);
                            } else {
                                mEditor.putString(getString(R.string.sp_user_mobile), mStringUpdateValue);
                            }
                            mEditor.commit();
                        } else {
                            mActivity.getAppAlertDialog().showDialog(getString(R.string.app_name), mVerificationCodePrarser.getMessage().toString(), false);
                        }
                        setData();
                        mActivity.updatedUserData();
                        mDialogUpdateProfile.dismiss();
                    }
                    if (mCurrentMethod.equalsIgnoreCase(mMethodGetProfile)) {

                        if (mLoginParserGetProfile.getWs_status().equalsIgnoreCase("true") && mLoginParserGetProfile.getData() != null) {
                            mActivity.getMyApplication().setUserProfile(mLoginParserGetProfile);
                            mActivity.updatedUserData();
                            setData();
                        } else {
                            if (mLoginParserGetProfile.getMessage().toString().contains(getString(R.string.alt_msg_exipred))) {
                                mActivity.getAppAlertDialog().showAlertWithSingleButton("", mLoginParserGetProfile.getMessage().toString(),
                                        mActivity.getString(R.string.lbl_logout),
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                mActivity.logout();
                                            }
                                        });
                            }
                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void registerBroadcast() {
        messageFilter = new IntentFilter();
        messageFilter.addAction("RECEIVE_MESSAGE_ACTION_NEW");
        mActivity.registerReceiver(broadcastReceiver, messageFilter);

        mSMSreceiver = new IncomingMessageReceiver();
        mIntentFilter = new IntentFilter();
        mIntentFilter.addAction("android.provider.Telephony.SMS_RECEIVED");
        mActivity.registerReceiver(mSMSreceiver, mIntentFilter);
    }

    public BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            String broadcastMessage = intent.getExtras().getString("senderNum");
            String verificationCode = intent.getExtras().getString("verificationCode");

            if (mEditTextVerifyCode != null)
                mEditTextVerifyCode.setText(verificationCode);

        }
    };

    @Override
    public void onDestroyView() {
        mActivity.unregisterReceiver(broadcastReceiver);
        mActivity.unregisterReceiver(mSMSreceiver);
        super.onDestroyView();
    }
}
