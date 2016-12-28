package beautician.beauty.android.fragments;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.Dialog;
import beautician.beauty.android.views.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;

import java.io.File;
import java.util.ArrayList;

import beautician.beauty.android.R;
import beautician.beauty.android.activities.InstagramPictureActivity;
import beautician.beauty.android.activities.MyFragmentActivity;
import beautician.beauty.android.parsers.CategoryListParser;
import beautician.beauty.android.parsers.LoginParser;
import beautician.beauty.android.parsers.ServiceDataParser;
import beautician.beauty.android.parsers.ServiceListParser;
import beautician.beauty.android.utilities.CommonMethod;
import beautician.beauty.android.views.CropImageView;
import beautician.beauty.android.views.FullScreenImageDialog;
import beautician.beauty.android.views.HorizontalListView;

@SuppressLint("InflateParams")
public class AddBeautyServiceFragment extends Fragment implements OnClickListener {

    private MyFragmentActivity mActivity;
    private CommonMethod mCommonMethod;
    private View rootView;

    private BackProcessAddService mBackProcessAddService;
    private ProgressDialog mProgressDialog;

    private AddPictureAdapter mAddPictureAdapter;
    private CategoryListParser mServiceTimeParser;
    private LoginParser mAddServiceParser;
    private ServiceDataParser mServiceDataParser;
    private ServiceListParser mServiceListParser;

    private String mMethodGetServiceTime = "GetServiceTime";
    private String mMethodAddBeautyService = "AddBeautyService";
    private String mMethodEditBeautyService = "EditBeautyService";
    private String mMethodDeleteBeautyServicePicture = "DeleteBeautyServicePicture";
    private String mMethodEditBeautyServicePicture = "EditBeautyServicePicture";

    private String mStringName = "";
    private String mStringDescription = "";
    private String mStringCategoryID = "";
    private String mStringServiceAvgTime = "";
    private String mStringServiceAvgPrice = "";
    private String mStringTotalImage = "0";
    private String mStringServiceID = "";
    private String mStringPictureID = "";
    private String mStringPictureType = "edit";

    private Spinner mSpinnerCategory;
    private Spinner mSpinnerServiceTime;

    private TextView mTextViewAddPicture;
    private TextView mTextViewAddService;
    private EditText mEditTextServiceName;
    private EditText mEditTextServiceDescription;
    private EditText mEditTextServicePrice;
    private HorizontalListView mHorizontalListView;
    private ImageLoader mImageLoader;
    private DisplayImageOptions mDisplayImageOptions;


    private Intent mIntentPictureAction = null;
    private Bitmap mBitmap = null;
    protected int CAMERA_REQUEST = 0;
    protected int GALLERY_PICTURE = 1;
    protected int INSTAGRAM_PICTURE = 2;
    private Dialog mDialog;
    private Dialog mDialogPicImage;
    private String mStringServicePicPath = "";
    private String mStringServicePicName = "";
    private ArrayList<String> mArryListServicePicPath;
    private boolean isEdit = false;
    private boolean isLocalPicEdit = false;
    private int mIntImagePosition = -1;


    public AddBeautyServiceFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_add_beauty_service, container, false);



        mActivity = (MyFragmentActivity) getActivity();
        mActivity.setHeaderTitle(R.string.lbl_add_beauty_service);
        mActivity.setSearchIcon(R.drawable.icon_search);
        mActivity.onBackButton(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mActivity.replaceFragment(new SearchFragment(), true);
            }
        });

        mActivity.setTouchForHideKeyboard(rootView);
        mCommonMethod = new CommonMethod(getActivity());

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


        getWidgetRefrence(rootView);
        registerOnClick();

        mServiceTimeParser = new CategoryListParser();
        mAddServiceParser = new LoginParser();
        mServiceListParser = new ServiceListParser();
        mArryListServicePicPath = new ArrayList<String>();

        mAddPictureAdapter = new AddPictureAdapter();
        mHorizontalListView.setAdapter(mAddPictureAdapter);

        mBackProcessAddService = new BackProcessAddService();
        mBackProcessAddService.execute(mMethodGetServiceTime);

        setCategoryData();

        setData();

        return rootView;
    }


    /**
     * Method call will set data for edit.
     */
    private void setData() {

        if (getArguments() != null) {
            isEdit = true;
            mServiceDataParser = new ServiceDataParser();
            mServiceDataParser = getArguments().getParcelable(getString(R.string.bundle_service_data));
            mTextViewAddService.setText(R.string.lbl_update_service);
            mEditTextServiceName.setText(mServiceDataParser.getService_name());
            mEditTextServiceDescription.setText(mServiceDataParser.getService_desc());
            mEditTextServicePrice.setText(mServiceDataParser.getService_avgprice());
            mStringServiceAvgTime = mServiceDataParser.getServicetime_id();
            mStringCategoryID = mServiceDataParser.getCategory_id();
            mStringServiceID = mServiceDataParser.getService_id();
            if (mServiceDataParser.getServiceimage() != null) {
                for (int i = 0; i < mServiceDataParser.getServiceimage().size(); i++) {
                    mArryListServicePicPath.add(mServiceDataParser.getServiceimage().get(i).getServicepicture_name());
                }
                mAddPictureAdapter = new AddPictureAdapter();
                mHorizontalListView.setAdapter(mAddPictureAdapter);
                if (mArryListServicePicPath.size() > 0) {
                    mHorizontalListView.setVisibility(View.VISIBLE);
                }
            }
            setCategoryData();
        }

    }

    /**
     * Method call will get IDs from xml file.
     */
    private void getWidgetRefrence(View v) {
        mSpinnerCategory = (Spinner) v.findViewById(R.id.fragment_add_beauty_service_spinner_category);
        mSpinnerServiceTime = (Spinner) v.findViewById(R.id.fragment_add_beauty_service_spinner_service_time);
        mTextViewAddPicture = (TextView) v.findViewById(R.id.fragment_add_beauty_service_textview_add_picture);
        mTextViewAddService = (TextView) v.findViewById(R.id.fragment_add_beauty_service_textview_add_service);
        mEditTextServiceName = (EditText) v.findViewById(R.id.fragment_add_beauty_service_edittext_service_name);
        mEditTextServiceDescription = (EditText) v.findViewById(R.id.fragment_add_beauty_service_edittext_service_description);
        mEditTextServicePrice = (EditText) v.findViewById(R.id.fragment_add_beauty_service_edittext_service_pirce);
        mHorizontalListView = (HorizontalListView) v.findViewById(R.id.fragment_add_beauty_service_listview_picture);

    }

    /**
     * Method call will Register OnClick() Events for widgets.
     */
    private void registerOnClick() {
        mTextViewAddPicture.setOnClickListener(this);
        mTextViewAddService.setOnClickListener(this);

        mSpinnerCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long arg3) {

                try {
                    if (pos == 0) {
                        mStringCategoryID = getString(R.string.hint_service_category);
                        ((TextView) parent.getChildAt(0)).setTextColor(ContextCompat.getColor(mActivity, R.color.dialog_bg));
                    } else {
                        mStringCategoryID = mActivity.getMyApplication().getCategoryListParser().getData().get(pos - 1).getCategory_id();
                        ((TextView) parent.getChildAt(0)).setTextColor(ContextCompat.getColor(mActivity, R.color.myPrimaryColor));
                    }


                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {

            }

        });

        mSpinnerServiceTime.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long arg3) {

                try {
                    if (position == 0) {
                        mStringServiceAvgTime = getString(R.string.hint_service_average_time);
                        ((TextView) parent.getChildAt(0)).setTextColor(ContextCompat.getColor(mActivity, R.color.dialog_bg));
                    } else {
                        mStringServiceAvgTime = mServiceTimeParser.getData().get(position - 1).getServicetime_id();
                        ((TextView) parent.getChildAt(0)).setTextColor(ContextCompat.getColor(mActivity, R.color.myPrimaryColor));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {

            }
        });

        mHorizontalListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }
        });

    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        menu.add(0, v.getId(), 0, getString(R.string.cm_view));
        menu.add(0, v.getId(), 0, getString(R.string.cm_edit));
        menu.add(0, v.getId(), 0, getString(R.string.cm_delete));
        menu.add(0, v.getId(), 0, getString(R.string.cm_cancel));
        super.onCreateContextMenu(menu, v, menuInfo);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {

        if (item.getTitle().toString().equalsIgnoreCase(getString(R.string.cm_edit))) {

            if (isEdit) {
                mStringPictureType = "edit";
                mStringPictureID = mServiceDataParser.getServiceimage().get(mIntImagePosition).getServicepicture_id();
                showPicServicePicDialog();
            } else {
                isLocalPicEdit = true;
                showPicServicePicDialog();
            }
            return true;

        } else if (item.getTitle().toString().equalsIgnoreCase(getString(R.string.cm_delete))) {

            if (isEdit) {
                mStringPictureID = mServiceDataParser.getServiceimage().get(mIntImagePosition).getServicepicture_id();
                mArryListServicePicPath.remove(mIntImagePosition);
                mBackProcessAddService = new BackProcessAddService();
                mBackProcessAddService.execute(mMethodDeleteBeautyServicePicture);
            } else {
                mArryListServicePicPath.remove(mIntImagePosition);
                mAddPictureAdapter.notifyDataSetChanged();
                if (mArryListServicePicPath.size() > 0) {
                    mHorizontalListView.setVisibility(View.VISIBLE);
                }
            }
            return true;

        } else if (item.getTitle().toString().equalsIgnoreCase(getString(R.string.cm_view))) {

            if (isEdit) {

                new FullScreenImageDialog(mActivity, mArryListServicePicPath.get(mIntImagePosition),mArryListServicePicPath,mIntImagePosition,false);

            } else {

                new FullScreenImageDialog(mActivity,"file:///"+mArryListServicePicPath.get(mIntImagePosition),mArryListServicePicPath,mIntImagePosition,true);
            }


            return true;

        } else if (item.getTitle().toString().equalsIgnoreCase(getString(R.string.cm_cancel))) {

            return true;
        }
        return super.onContextItemSelected(item);

    }


    /**
     * Method call OnClick Event fire.
     */
    @Override
    public void onClick(View v) {

        if (v == mTextViewAddPicture) {

            if (Integer.parseInt(mActivity.getMyApplication().getUserProfile().getData().getUser_maxpicture()) > mArryListServicePicPath.size()) {
                mStringPictureType = "add";
                mStringPictureID = mStringServiceID;
                showPicServicePicDialog();
            } else {
                mActivity.getAppAlertDialog().showDialog(getString(R.string.app_name), getString(R.string.alt_msg_max_add_service_image), false);
            }

        } else if (v == mTextViewAddService) {

            callAddBeautyService(isEdit);

        }


    }

    /**
     * Method call will check validation and call add beauty service.
     */
    private void callAddBeautyService(boolean isEdit) {

        mStringName = mEditTextServiceName.getText().toString().trim();
        mStringDescription = mEditTextServiceDescription.getText().toString().trim();
        mStringServiceAvgPrice = mEditTextServicePrice.getText().toString().trim();

        if (mActivity.getAppAlertDialog().validateSelectkField(mStringCategoryID, mActivity, getString(R.string.hint_service_category), getString(R.string.validation_service_category))
                && mActivity.getAppAlertDialog().validateBlankField(mEditTextServiceName, mActivity, getString(R.string.validation_service_name))
                && mActivity.getAppAlertDialog().validateBlankField(mEditTextServiceDescription, mActivity, getString(R.string.validation_service_description))
                && mActivity.getAppAlertDialog().validateBlankField(mEditTextServiceName, mActivity, getString(R.string.validation_service_price))
                && mActivity.getAppAlertDialog().validateSelectkField(mStringServiceAvgTime, mActivity, getString(R.string.hint_service_average_time), getString(R.string.validation_service_time))) {

            if (isEdit) {
                mBackProcessAddService = new BackProcessAddService();
                mBackProcessAddService.execute(mMethodEditBeautyService);
            } else {
                mBackProcessAddService = new BackProcessAddService();
                mBackProcessAddService.execute(mMethodAddBeautyService);
            }
        }
    }


    /**
     * Method will display set category data
     */
    public void setCategoryData() {
        int position = 0;
        final String[] mStringsCategory;
        int size = mActivity.getMyApplication().getCategoryListParser().getData().size();
        mStringsCategory = new String[(size) + 1];
        mStringsCategory[0] = getString(R.string.hint_service_category);
        for (int i = 1; i < mStringsCategory.length; i++) {
            if(mActivity.getMyApplication().getCurrentLang().equalsIgnoreCase("en"))
                mStringsCategory[i] = mActivity.getMyApplication().getCategoryListParser().getData().get(i - 1).getCategory_name();
            else
                mStringsCategory[i] = mActivity.getMyApplication().getCategoryListParser().getData().get(i - 1).getCategory_namearebic();
            if (mStringCategoryID.equalsIgnoreCase(mActivity.getMyApplication().getCategoryListParser().getData().get(i - 1).getCategory_id()))
                position = i;
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(mActivity, R.layout.row_spinner_selected, mStringsCategory);
        adapter.setDropDownViewResource(R.layout.row_spinner_text);
        mSpinnerCategory.setAdapter(adapter);
        mSpinnerCategory.setSelection(position);

    }


    /**
     * Method will display set category data
     */
    public void getServiceTime() {
        int position = 0;
        final String[] mStringsArrayTime;
        int size = mServiceTimeParser.getData().size();
        mStringsArrayTime = new String[(size) + 1];
        mStringsArrayTime[0] = getString(R.string.hint_service_average_time);
        for (int i = 1; i < mStringsArrayTime.length; i++) {
            mStringsArrayTime[i] = mServiceTimeParser.getData().get(i - 1).getServicetime_value();
            if (mStringServiceAvgTime.equalsIgnoreCase(mServiceTimeParser.getData().get(i - 1).getServicetime_id()))
                position = i;
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(mActivity, R.layout.row_spinner_selected, mStringsArrayTime);
        adapter.setDropDownViewResource(R.layout.row_spinner_text);
        mSpinnerServiceTime.setAdapter(adapter);
        mSpinnerServiceTime.setSelection(position);

    }


    /**
     * Method call will display image picker dialog.
     */
    private void showPicServicePicDialog() {

        mStringServicePicName = String.valueOf(System.currentTimeMillis()) + ".png";
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
        mTextViewInstagram.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent mIntentInstagram = new Intent(mActivity, InstagramPictureActivity.class);
                mIntentInstagram.putExtra(MediaStore.EXTRA_OUTPUT, mStringServicePicName);
                startActivityForResult(mIntentInstagram, INSTAGRAM_PICTURE);
                mDialogPicImage.cancel();
            }
        });
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

                File mFile = new File(mCommonMethod.getImageDirectory(getString(R.string.folder_service_pic)) + "/" + mStringServicePicName);
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
                mCommonMethod.copyImageFile(mCommonMethod.getImageDirectory(getString(R.string.folder_service_pic)) + "/" + mStringServicePicName, mBitmap);
                mStringServicePicPath = mCommonMethod.getImageDirectory(getString(R.string.folder_service_pic)) + "/" + mStringServicePicName;

                if (isEdit) {
                    mBackProcessAddService = new BackProcessAddService();
                    mBackProcessAddService.execute(mMethodEditBeautyServicePicture);
                } else {
                    if (isLocalPicEdit) {
                        mArryListServicePicPath.set(mIntImagePosition, mStringServicePicPath);
                        isLocalPicEdit = false;
                    } else {
                        mArryListServicePicPath.add(mStringServicePicPath);
                    }
                    mAddPictureAdapter.notifyDataSetChanged();
                }
                if (mArryListServicePicPath.size() > 0) {
                    mHorizontalListView.setVisibility(View.VISIBLE);
                }

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
     * BaseAdapter class for load data into listview
     *
     * @author ebaraiya
     */
    public class AddPictureAdapter extends BaseAdapter {
        ViewHolder mViewHolder;

        @Override
        public int getCount() {
            return mArryListServicePicPath.size();
        }

        @Override
        public Object getItem(int arg0) {
            return null;
        }

        @Override
        public long getItemId(int arg0) {
            return 0;
        }

        @Override
        public View getView(final int position, View convertview, ViewGroup arg2) {
            if (convertview == null) {
                convertview = mActivity.getLayoutInflater().inflate(R.layout.row_service_picture, null);
                mViewHolder = new ViewHolder();

                mViewHolder.mImageView = (ImageView) convertview.findViewById(R.id.row_service_picture);
                mViewHolder.mImageViewOption = (ImageView) convertview.findViewById(R.id.row_service_picture_option);
                convertview.setTag(mViewHolder);

            } else {
                mViewHolder = (ViewHolder) convertview.getTag();
            }

            if (isEdit) {
                mImageLoader.getInstance().displayImage(mArryListServicePicPath.get(position), mViewHolder.mImageView, mDisplayImageOptions);
            } else {
                mImageLoader.getInstance().displayImage("file:///" + mArryListServicePicPath.get(position), mViewHolder.mImageView, mDisplayImageOptions);
            }

            mViewHolder.mImageViewOption.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    mIntImagePosition = position;
                    registerForContextMenu(mViewHolder.mImageView);
                    mActivity.openContextMenu(mViewHolder.mImageView);
                }
            });

            return convertview;
        }

    }

    public class ViewHolder {
        ImageView mImageView;
        ImageView mImageViewOption;

    }

    /**
     * AsyncTask for calling webservice in background.
     *
     * @author ebaraiya
     */
    public class BackProcessAddService extends AsyncTask<String, Void, String> {
        String mCurrentMethod = "";

        @Override
        protected void onPreExecute() {
            mProgressDialog = ProgressDialog.show(mActivity, "", getString(R.string.dialog_loading), true);
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {
            mCurrentMethod = params[0];

            if (mCurrentMethod.equalsIgnoreCase(mMethodGetServiceTime)) {

                mServiceTimeParser = (CategoryListParser) mActivity.getWebMethod().callGetServiceTime(mServiceTimeParser);

            } else if (mCurrentMethod.equalsIgnoreCase(mMethodAddBeautyService)) {

                mAddServiceParser = (LoginParser) mActivity.getWebMethod().callAddBeautyService(mActivity.getMyApplication().getUserFiled(getString(R.string.sp_user_id)), mActivity.getMyApplication().getUserFiled(getString(R.string.sp_user_hash)), mStringCategoryID, mStringName, mStringDescription, mStringServiceAvgTime, mStringServiceAvgPrice, mArryListServicePicPath, mAddServiceParser);

            } else if (mCurrentMethod.equalsIgnoreCase(mMethodEditBeautyService)) {

                mAddServiceParser = (LoginParser) mActivity.getWebMethod().callEditBeautyService(mActivity.getMyApplication().getUserFiled(getString(R.string.sp_user_id)), mActivity.getMyApplication().getUserFiled(getString(R.string.sp_user_hash)), mStringCategoryID, mStringName, mStringDescription, mStringServiceAvgTime, mStringServiceAvgPrice, mStringServiceID, mAddServiceParser);

            } else if (mCurrentMethod.equalsIgnoreCase(mMethodDeleteBeautyServicePicture)) {

                mAddServiceParser = (LoginParser) mActivity.getWebMethod().callDeleteMyServicePicture(mActivity.getMyApplication().getUserFiled(getString(R.string.sp_user_id)), mActivity.getMyApplication().getUserFiled(getString(R.string.sp_user_hash)), mStringPictureID, mAddServiceParser);

            } else if (mCurrentMethod.equalsIgnoreCase(mMethodEditBeautyServicePicture)) {

                mServiceListParser = (ServiceListParser) mActivity.getWebMethod().callEditBeautyServicePicture(mActivity.getMyApplication().getUserFiled(getString(R.string.sp_user_id)), mActivity.getMyApplication().getUserFiled(getString(R.string.sp_user_hash)), mStringPictureID, mStringPictureType, mStringServicePicPath, mServiceListParser);

            }

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

                    if (mCurrentMethod.equalsIgnoreCase(mMethodGetServiceTime)) {

                        if (mServiceTimeParser.getData() != null && mServiceTimeParser.getData().size() > 0) {
                            getServiceTime();
                        } else {
                            if (mServiceTimeParser.getMessage().toString().contains(getString(R.string.alt_msg_exipred))) {
                                tokenExpire();
                                ;
                            } else {
                                mActivity.getAppAlertDialog().showDialog(getString(R.string.app_name), mServiceTimeParser.getMessage(), false);
                            }
                        }

                    } else if (mCurrentMethod.equalsIgnoreCase(mMethodAddBeautyService)) {

                        if (mAddServiceParser.getWs_status().equalsIgnoreCase("true")) {
                            mActivity.onBackPressed();
                        } else {
                            if (mAddServiceParser.getMessage().toString().contains(getString(R.string.alt_msg_exipred))) {
                                tokenExpire();
                            } else {
                                mActivity.getAppAlertDialog().showDialog(getString(R.string.app_name), mAddServiceParser.getMessage().toString(), false);
                            }
                        }
                    } else if (mCurrentMethod.equalsIgnoreCase(mMethodEditBeautyService)) {

                        if (mAddServiceParser.getWs_status().equalsIgnoreCase("true")) {
                            mActivity.onBackPressed();
                        } else {
                            if (mAddServiceParser.getMessage().toString().contains(getString(R.string.alt_msg_exipred))) {
                                tokenExpire();
                            } else {
                                mActivity.getAppAlertDialog().showDialog(getString(R.string.app_name), mAddServiceParser.getMessage().toString(), false);
                            }
                        }

                    } else if (mCurrentMethod.equalsIgnoreCase(mMethodDeleteBeautyServicePicture)) {

                        if (mAddServiceParser.getWs_status().equalsIgnoreCase("true")) {
                            mAddPictureAdapter.notifyDataSetChanged();
                        } else {
                            if (mAddServiceParser.getMessage().toString().contains(getString(R.string.alt_msg_exipred))) {
                                tokenExpire();
                            } else {
                                mActivity.getAppAlertDialog().showDialog(getString(R.string.app_name), mAddServiceParser.getMessage().toString(), false);
                            }
                        }

                    } else if (mCurrentMethod.equalsIgnoreCase(mMethodEditBeautyServicePicture)) {

                        if (mServiceListParser.getWs_status().equalsIgnoreCase("true")) {


                            mServiceDataParser.setServiceimage(mServiceListParser.getServiceimage());

                            if (mServiceDataParser.getServiceimage() != null) {
                                mArryListServicePicPath.clear();
                                for (int i = 0; i < mServiceDataParser.getServiceimage().size(); i++) {
                                    mArryListServicePicPath.add(mServiceDataParser.getServiceimage().get(i).getServicepicture_name());
                                }
                                mAddPictureAdapter = new AddPictureAdapter();
                                mHorizontalListView.setAdapter(mAddPictureAdapter);
                                if (mArryListServicePicPath.size() > 0) {
                                    mHorizontalListView.setVisibility(View.VISIBLE);
                                }
                            }

                        } else {
                            if (mAddServiceParser.getMessage().toString().contains(getString(R.string.alt_msg_exipred))) {
                                tokenExpire();
                            } else {
                                mActivity.getAppAlertDialog().showDialog(getString(R.string.app_name), mAddServiceParser.getMessage().toString(), false);
                            }
                        }

                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            super.onPostExecute(result);
        }
    }

    /**
     * Method call when token expired...
     */
    private void tokenExpire() {
        mActivity.getAppAlertDialog().showAlertWithSingleButton("", mAddServiceParser.getMessage().toString(),
                mActivity.getString(R.string.lbl_logout),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        mActivity.logout();
                    }
                });
    }

    /**
     * Method will call from other app.
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
                                mCommonMethod.getImageDirectory(getString(R.string.folder_service_pic));
                                mCommonMethod.copyImageFile(mCommonMethod.getImageDirectory(getString(R.string.folder_service_pic)) + "/" + mStringServicePicName, mBitmap);
                                showImageDisplayDialog();
                            } else {
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
                e.printStackTrace();
            }
        } else if (requestCode == CAMERA_REQUEST) {
            try {
                if (resultCode == getActivity().RESULT_OK) {
                    File imgFile = new File(mCommonMethod.getImageDirectory(getString(R.string.folder_service_pic)) + "/" + mStringServicePicName);
                    mBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                    mBitmap = mCommonMethod.resizeBitmap(mBitmap);
                    showImageDisplayDialog();
                } else if (resultCode == getActivity().RESULT_CANCELED) {
                    Toast.makeText(getActivity(), getString(R.string.lbl_cancelled), Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }catch (OutOfMemoryError e) {
                e.printStackTrace();
            }

        } else if (requestCode == INSTAGRAM_PICTURE) {
            Log.d("ADD ", "COME IN INSTAGRAM_PICTURE");
            if (resultCode == getActivity().RESULT_OK) {
                Log.d("ADD ", "COME IN OK");
                File imgFile = new File(data.getStringExtra(getString(R.string.bundle_path)));
                mBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
//                mBitmap = mCommonMethod.resizeBitmap(mBitmap);
                showImageDisplayDialog();
            } else if (resultCode == getActivity().RESULT_CANCELED) {
                Toast.makeText(getActivity(), getString(R.string.lbl_cancelled), Toast.LENGTH_SHORT).show();
            }

        }
    }
}
