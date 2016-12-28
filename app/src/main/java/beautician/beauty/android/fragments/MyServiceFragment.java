package beautician.beauty.android.fragments;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Dialog;
import beautician.beauty.android.views.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;

import beautician.beauty.android.R;
import beautician.beauty.android.activities.MyFragmentActivity;
import beautician.beauty.android.parsers.LoginParser;
import beautician.beauty.android.parsers.ServiceListParser;

@SuppressLint("InflateParams")
public class MyServiceFragment extends Fragment implements OnClickListener {

    private MyFragmentActivity mActivity;
    private View rootView;

    private BackProcessMyServices backProcessMyServices;
    private ServiceListParser mGetServiceParser;
    private LoginParser mDeleteParser;
    private ProgressDialog mProgressDialog;
    private String mMethodGetMyServices = "GetMyServices";
    private String mMethodDeletMyService = "DeleteMyService";
    private String mMethodEnableMyService = "EnableMyService";
    private String mStringServiceID = "";

    private ListView mListViewServices;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    private ImageLoader mImageLoader;
    private DisplayImageOptions mDisplayImageOptions;

    private MyServicesAdapter myServicesAdapter;

    private Dialog mDialogDelete;

    public MyServiceFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_my_service, container, false);

        mActivity = (MyFragmentActivity) getActivity();
        mActivity.setHeaderTitle(R.string.lbl_my_services);
        mActivity.setSearchIcon(R.drawable.icon_add);
        mActivity.onBackButton(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mActivity.getMyApplication().isAccountActive()) {
                    mActivity.replaceFragment(new AddBeautyServiceFragment(), true);
                }else
                {
                    mActivity.getAppAlertDialog().showDialog("", getString(R.string.alt_msg_disable_account), false);
                }

            }
        });

        mDisplayImageOptions = new DisplayImageOptions.Builder()
                .showImageForEmptyUri(R.drawable.bg_user_profile)
                .showImageOnFail(R.drawable.bg_user_profile)
                .cacheInMemory(true)
                .displayer(new RoundedBitmapDisplayer(20))
                .considerExifParams(true)
                .resetViewBeforeLoading(true)
                .cacheOnDisc(true)
                .imageScaleType(ImageScaleType.EXACTLY)
                .displayer(new FadeInBitmapDisplayer(0))
                .build();


        getWidgetRefrence(rootView);
        registerOnClick();

        mGetServiceParser = new ServiceListParser();
        mDeleteParser = new LoginParser();

        backProcessMyServices = new BackProcessMyServices();
        backProcessMyServices.execute(mMethodGetMyServices);

        return rootView;
    }

    /**
     * Method call will get IDs from xml file.
     */
    private void getWidgetRefrence(View v) {

        mListViewServices = (ListView) v.findViewById(R.id.fragment_my_service_listview);
        mSwipeRefreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.fragment_my_service_swiperefresh);
    }

    /**
     * Method call will Register OnClick() Events for widgets.
     */
    private void registerOnClick() {

        mListViewServices.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                if(mGetServiceParser.getData().get(position).getService_delete().equalsIgnoreCase("0")) {
                    mStringServiceID = mGetServiceParser.getData().get(position).getService_id();
                    Bundle mBundle = new Bundle();
                    AddBeautyServiceFragment mAddBeautyServiceFragment = new AddBeautyServiceFragment();
                    mBundle.putParcelable(getString(R.string.bundle_service_data), mGetServiceParser.getData().get(position));
                    mAddBeautyServiceFragment.setArguments(mBundle);
                    mActivity.replaceFragment(mAddBeautyServiceFragment, true);
                }

            }
        });

        mListViewServices.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int i) {


            }

            @Override
            public void onScroll(AbsListView absListView, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (firstVisibleItem == 0)
                    mSwipeRefreshLayout.setEnabled(true);
                else
                    mSwipeRefreshLayout.setEnabled(false);
            }
        });

        mSwipeRefreshLayout.setColorSchemeColors(ContextCompat.getColor(mActivity, R.color.myPrimaryColor), ContextCompat.getColor(mActivity, R.color.golden));

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mSwipeRefreshLayout.setRefreshing(true);
                backProcessMyServices = new BackProcessMyServices();
                backProcessMyServices.execute(mMethodGetMyServices);

            }
        });

    }

    /**
     * Method call OnClick Event fire.
     */
    @Override
    public void onClick(View v) {


    }


    /**
     * BaseAdapter class for load data into listview
     *
     * @author ebaraiya
     */
    public class MyServicesAdapter extends BaseAdapter {
        ViewHolder mViewHolder;

        @Override
        public int getCount() {
            return mGetServiceParser.getData().size();
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
                convertview = mActivity.getLayoutInflater().inflate(R.layout.row_my_services, null);
                mViewHolder = new ViewHolder();

                mViewHolder.mImageViewUserDp = (ImageView) convertview.findViewById(R.id.row_my_services_imageview_user_dp);
                mViewHolder.mImageViewComments = (ImageView) convertview.findViewById(R.id.row_my_services_imageview_comments);
                mViewHolder.mImageViewEdit = (ImageView) convertview.findViewById(R.id.row_my_services_imageview_edit);
                mViewHolder.mImageViewDelete = (ImageView) convertview.findViewById(R.id.row_my_services_imageview_delete);
                mViewHolder.mTextViewServiceName = (TextView) convertview.findViewById(R.id.row_my_services_textview_service_name);
                mViewHolder.mTextViewServiceDescription = (TextView) convertview.findViewById(R.id.row_my_services_textview_service_description);
                mViewHolder.mTextViewCategoryName = (TextView) convertview.findViewById(R.id.row_my_services_textview_service_category);
                mViewHolder.mRatingBarSatisfy = (RatingBar) convertview.findViewById(R.id.row_my_services_rating_satification);
                mViewHolder.mRatingBarCommitted = (RatingBar) convertview.findViewById(R.id.row_my_services_rating_commitment);
                mViewHolder.mTextViewEnable = (TextView) convertview.findViewById(R.id.row_my_services_textview_enable);

                convertview.setTag(mViewHolder);

            } else {
                mViewHolder = (ViewHolder) convertview.getTag();
            }

            mViewHolder.mTextViewServiceName.setText(mGetServiceParser.getData().get(position).getService_name());
            mViewHolder.mTextViewServiceDescription.setText(mGetServiceParser.getData().get(position).getService_desc());


            if(mActivity.getMyApplication().getCurrentLang().equalsIgnoreCase("en"))
                mViewHolder.mTextViewCategoryName.setText(mGetServiceParser.getData().get(position).getCategory_name());
            else
                mViewHolder.mTextViewCategoryName.setText(mGetServiceParser.getData().get(position).getCategory_namearebic());
//            mViewHolder.mRatingBarSatisfy.setRating(Float.parseFloat(mActivity.getMyApplication().getUserFiled(getString(R.string.sp_satisfy_rating))));
//            mViewHolder.mRatingBarCommitted.setRating(Float.parseFloat(mActivity.getMyApplication().getUserFiled(getString(R.string.sp_committed_rating))));

            if (mGetServiceParser.getData().get(position).getServiceimage() != null) {
                mImageLoader.getInstance().displayImage(mGetServiceParser.getData().get(position).getServiceimage().get(0).getServicepicture_name(), mViewHolder.mImageViewUserDp, mDisplayImageOptions);
            } else {
                mViewHolder.mImageViewUserDp.setImageResource(R.drawable.bg_user_profile);
            }


            if(mGetServiceParser.getData().get(position).getService_delete().equalsIgnoreCase("1"))
            {
                mViewHolder.mImageViewComments.setVisibility(View.INVISIBLE);
                mViewHolder.mImageViewEdit.setVisibility(View.INVISIBLE);
                mViewHolder.mImageViewDelete.setVisibility(View.INVISIBLE);
                mViewHolder.mTextViewEnable.setVisibility(View.VISIBLE);
            }else
            {
                mViewHolder.mImageViewComments.setVisibility(View.VISIBLE);
                mViewHolder.mImageViewEdit.setVisibility(View.VISIBLE);
                mViewHolder.mImageViewDelete.setVisibility(View.VISIBLE);
                mViewHolder.mTextViewEnable.setVisibility(View.GONE);
            }



            mViewHolder.mImageViewComments.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(mActivity.getMyApplication().isAccountActive()) {
                        Bundle mBundle = new Bundle();
                        CommentsFragment mCommentsFragment = new CommentsFragment();
                        mBundle.putString(getString(R.string.bundle_service_id), mGetServiceParser.getData().get(position).getService_id());
                        mCommentsFragment.setArguments(mBundle);
                        mActivity.replaceFragment(mCommentsFragment, true);
                    }else
                    {
                        mActivity.getAppAlertDialog().showDialog("", getString(R.string.alt_msg_disable_account), false);
                    }



                }
            });

            mViewHolder.mImageViewEdit.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {

                    Bundle mBundle = new Bundle();
                    AddBeautyServiceFragment mAddBeautyServiceFragment = new AddBeautyServiceFragment();
                    mBundle.putParcelable(getString(R.string.bundle_service_data), mGetServiceParser.getData().get(position));
                    mAddBeautyServiceFragment.setArguments(mBundle);
                    mActivity.replaceFragment(mAddBeautyServiceFragment, true);

                }
            });

            mViewHolder.mImageViewDelete.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    mStringServiceID = mGetServiceParser.getData().get(position).getService_id();
                    showDeleteConfirDialog(true);
                }
            });

            mViewHolder.mTextViewEnable.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(mActivity.getMyApplication().isAccountActive()) {
                        mStringServiceID = mGetServiceParser.getData().get(position).getService_id();
                        showDeleteConfirDialog(false);
                    }else
                    {
                        mActivity.getAppAlertDialog().showDialog("", getString(R.string.alt_msg_disable_account), false);
                    }

                }
            });

            return convertview;
        }

    }

    public class ViewHolder {
        ImageView mImageViewUserDp;
        ImageView mImageViewComments;
        ImageView mImageViewEdit;
        ImageView mImageViewDelete;
        TextView mTextViewServiceName;
        TextView mTextViewServiceDescription;
        TextView mTextViewCategoryName;
        TextView mTextViewEnable;
        RatingBar mRatingBarSatisfy;
        RatingBar mRatingBarCommitted;

    }

    /**
     * Method call will show delete confirm dialog
     */
    private void showDeleteConfirDialog(final boolean isForDisable) {
        mDialogDelete = new Dialog(mActivity);
        mDialogDelete.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mDialogDelete.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        mDialogDelete.setContentView(R.layout.dialog_delete);
        Window window = mDialogDelete.getWindow();
        window.setLayout(ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.MATCH_PARENT);
        final TextView mTextViewDialogConfrimd = (TextView) mDialogDelete.findViewById(R.id.dialog_delete_textview_confrim);
        final TextView mTextViewDialogCancel = (TextView) mDialogDelete.findViewById(R.id.dialog_delete_textview_cancel);
        TextView mTextViewDialogMsg = (TextView) mDialogDelete.findViewById(R.id.dialog_delete_textview_msg);

        if(isForDisable)
            mTextViewDialogMsg.setText(R.string.alt_msg_disable);
        else
            mTextViewDialogMsg.setText(R.string.alt_msg_enable);

        mTextViewDialogConfrimd.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                backProcessMyServices = new BackProcessMyServices();
                if(isForDisable)
                    backProcessMyServices.execute(mMethodDeletMyService);
                else
                    backProcessMyServices.execute(mMethodEnableMyService);
                mDialogDelete.cancel();
            }
        });
        mTextViewDialogCancel.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialogDelete.cancel();
            }
        });
        mDialogDelete.show();
    }


    /**
     * AsyncTask for calling webservice in background.
     *
     * @author ebaraiya
     */
    public class BackProcessMyServices extends AsyncTask<String, Void, String> {
        String mCurrentMethod = "";

        @Override
        protected void onPreExecute() {
            mProgressDialog = ProgressDialog.show(mActivity, "", getString(R.string.dialog_loading), true);
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {
            mCurrentMethod = params[0];
            if (mCurrentMethod.equalsIgnoreCase(mMethodGetMyServices)) {

                mGetServiceParser = (ServiceListParser) mActivity.getWebMethod().callGetMyService(mActivity.getMyApplication().getUserFiled(getString(R.string.sp_user_id)), mActivity.getMyApplication().getUserFiled(getString(R.string.sp_user_hash)), mGetServiceParser);

            } else if (mCurrentMethod.equalsIgnoreCase(mMethodDeletMyService)) {

                mDeleteParser = (LoginParser) mActivity.getWebMethod().callDeleteMyService(mActivity.getMyApplication().getUserFiled(getString(R.string.sp_user_id)), mActivity.getMyApplication().getUserFiled(getString(R.string.sp_user_hash)), mStringServiceID, mDeleteParser);
            }
            else if (mCurrentMethod.equalsIgnoreCase(mMethodEnableMyService)) {

                mDeleteParser = (LoginParser) mActivity.getWebMethod().callEnableMyService(mActivity.getMyApplication().getUserFiled(getString(R.string.sp_user_id)), mActivity.getMyApplication().getUserFiled(getString(R.string.sp_user_hash)), mStringServiceID, mDeleteParser);
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

                    if (mCurrentMethod.equalsIgnoreCase(mMethodGetMyServices)) {

                        if (mGetServiceParser.getData() != null && mGetServiceParser.getData().size() > 0) {
                            mListViewServices.setVisibility(View.VISIBLE);
                            mSwipeRefreshLayout.setVisibility(View.VISIBLE);
                            myServicesAdapter = new MyServicesAdapter();
                            mListViewServices.setAdapter(myServicesAdapter);
                        } else {
                            if (mGetServiceParser.getMessage().toString().contains(getString(R.string.alt_msg_exipred))) {
                                mActivity.getAppAlertDialog().showAlertWithSingleButton("", mGetServiceParser.getMessage().toString(),
                                        mActivity.getString(R.string.lbl_logout),
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                mActivity.logout();
                                            }
                                        });
                            } else {
                                mListViewServices.setVisibility(View.GONE);
                                mSwipeRefreshLayout.setVisibility(View.GONE);
                                mActivity.getAppAlertDialog().showDialog(getString(R.string.app_name), mGetServiceParser.getMessage(), false);
                            }
                        }


                    } else if (mCurrentMethod.equalsIgnoreCase(mMethodDeletMyService) || mCurrentMethod.equalsIgnoreCase(mMethodEnableMyService)) {

                        if (mDeleteParser.getWs_status().equalsIgnoreCase("true")) {
                            backProcessMyServices = new BackProcessMyServices();
                            backProcessMyServices.execute(mMethodGetMyServices);

                        } else {
                            if (mDeleteParser.getMessage().toString().contains(getString(R.string.alt_msg_exipred))) {
                                mActivity.getAppAlertDialog().showAlertWithSingleButton("", mDeleteParser.getMessage().toString(),
                                        mActivity.getString(R.string.lbl_logout),
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                mActivity.logout();
                                            }
                                        });
                            } else {
                                mActivity.getAppAlertDialog().showDialog(getString(R.string.app_name), mDeleteParser.getMessage().toString(), false);
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
}
