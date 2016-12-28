package beautician.beauty.android.fragments;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Dialog;
import beautician.beauty.android.views.ProgressDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;

import java.util.ArrayList;

import beautician.beauty.android.R;
import beautician.beauty.android.activities.MyFragmentActivity;
import beautician.beauty.android.parsers.BookAppointmentParser;
import beautician.beauty.android.parsers.ServiceDataParser;
import beautician.beauty.android.utilities.StaticData;
import beautician.beauty.android.views.FullScreenImageDialog;
import beautician.beauty.android.views.HorizontalListView;
import beautician.beauty.android.views.ReportDialogFragment;
import beautician.beauty.android.views.SharingDialogFragment;

@SuppressLint("InflateParams")
public class ProviderDetailsDataFragment extends Fragment implements OnClickListener {

    private MyFragmentActivity mActivity;
    private View rootView;
    public ImageLoader mImageLoader;
    private DisplayImageOptions mDisplayImageOptions;

    private ProgressDialog mProgressDialog;
    private String mMethodGetProviderDetails = "GetProviderDetaisl";
    private String mStringServiceID = "";
    private ServicePictureAdapter mServicePictureAdapter;
    private ServiceDataParser mServiceDataParser;

    private TextView mTextViewServiceName;
    private TextView mTextViewDesc;
    private TextView mTextViewAvgTime;
    private TextView mTextViewPrice;
    private TextView mTextViewTotalComments;
    private TextView mTextViewComment;
    private TextView mTextViewCommentBy;
    private TextView mTextViewQuentityValue;
    private ImageView mImageViewMinus;
    private ImageView mImageViewPlush;
    private ImageView mImageViewShare;
    private ImageView mImageViewReport;

    private HorizontalListView mHorizontalListView;
    private RelativeLayout mRelativeLayoutComments;
    private LinearLayout mLinearLayoutCommentSection;
    private ArrayList<String> mArryListServicePicPath;
    private int intValue = 0;

    private String mStringCategoryID="";
    private String mStringCategoryName="";

    private String mStringProviderName = "";
    private String mStringProviderPic = "";
    private String mStringProviderID = "";
    private Dialog mDialogReport;

    public ProviderDetailsDataFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_provider_details_data, container, false);

        mActivity = (MyFragmentActivity) getActivity();
        mActivity.setHeaderTitle(R.string.lbl_provider_detail);
        mActivity.setSearchIcon(R.drawable.icon_search);
        mActivity.onBackButton(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mActivity.replaceFragment(new SearchFragment(),true);
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
                .displayer(new FadeInBitmapDisplayer(200))
                .build();

//        mStringProviderID = getArguments().getString(getString(R.string.bundle_provider_id));
//        mGetProviderListParser = new ProviderParser();




        getWidgetRefrence(rootView);
        registerOnClick();

        setData();
        return rootView;
    }


    /**
     * Method call will get IDs from xml file.
     * @param v
     */
    private void getWidgetRefrence(View v) {
        mHorizontalListView = (HorizontalListView) v.findViewById(R.id.fragment_provider_details_data_listview_picture);
        mTextViewServiceName = (TextView) v.findViewById(R.id.fragment_provider_details_data_textview_service_name);
        mTextViewDesc = (TextView) v.findViewById(R.id.fragment_provider_details_data_textview_service_dec);
        mTextViewAvgTime = (TextView) v.findViewById(R.id.fragment_provider_details_data_textview_service_avg_time);
        mTextViewPrice = (TextView) v.findViewById(R.id.fragment_provider_details_data_textview_service_price);
        mTextViewTotalComments = (TextView) v.findViewById(R.id.fragment_provider_details_data_textview_comments);
        mTextViewComment = (TextView) v.findViewById(R.id.fragment_provider_details_data_comments);
        mTextViewCommentBy = (TextView) v.findViewById(R.id.fragment_provider_details_data_textview_comments_by);
        mTextViewQuentityValue = (TextView) v.findViewById(R.id.fragment_provider_details_data_textview_quentity_value);
        mImageViewMinus = (ImageView) v.findViewById(R.id.fragment_provider_details_data_imageview_minus);
        mImageViewPlush = (ImageView) v.findViewById(R.id.fragment_provider_details_data_imageview_plush);
        mImageViewShare = (ImageView) v.findViewById(R.id.fragment_provider_details_data_imageview_share);
        mImageViewReport = (ImageView) v.findViewById(R.id.fragment_provider_details_data_imageview_report);
        mRelativeLayoutComments = (RelativeLayout) v.findViewById(R.id.fragment_provider_details_data_relative_comment);
        mLinearLayoutCommentSection = (LinearLayout)v.findViewById(R.id.fragment_provider_details_data_linear_comment);
    }

    /**
     * Method call will Register OnClick() Events for widgets.
     */
    private void registerOnClick() {
        mImageViewMinus.setOnClickListener(this);
        mImageViewPlush.setOnClickListener(this);
        mImageViewShare.setOnClickListener(this);
        mImageViewReport.setOnClickListener(this);
        mRelativeLayoutComments.setOnClickListener(this);
        mHorizontalListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                new FullScreenImageDialog(mActivity, mArryListServicePicPath.get(position), mArryListServicePicPath, position, false);
            }
        });
    }

    /**
     * Method call OnClick Event fire.
     */
    @Override
    public void onClick(View v) {


        if (v == mRelativeLayoutComments) {

            Bundle mBundle = new Bundle();
            CommentsFragment mCommentsFragment = new CommentsFragment();
            mBundle.putString(getString(R.string.bundle_service_id), mStringServiceID);
            mCommentsFragment.setArguments(mBundle);
            mActivity.replaceFragment(mCommentsFragment, true);

        } else if (v == mImageViewMinus) {

            intValue = intValue - 1;

            if (intValue > 0) {
                mTextViewQuentityValue.setText(String.valueOf(intValue));
                setQuantityForBookAppointment();
            }else{
                intValue = 0;
                mTextViewQuentityValue.setText(String.valueOf(intValue));
                mActivity.getMyApplication().clearnBookAppointment(mStringServiceID);
            }


        } else if (v == mImageViewPlush) {

            intValue = intValue + 1;
            mTextViewQuentityValue.setText(String.valueOf(intValue));
            setQuantityForBookAppointment();
        }
        else if (v == mImageViewShare) {

            StringBuilder mStringBuilderServiceDetails = new StringBuilder();
            mStringBuilderServiceDetails.append(mServiceDataParser.getService_name());
            mStringBuilderServiceDetails.append("\n");
            mStringBuilderServiceDetails.append(mServiceDataParser.getService_desc());
            mStringBuilderServiceDetails.append("\n");
            mStringBuilderServiceDetails.append(mTextViewAvgTime.getText().toString());
            mStringBuilderServiceDetails.append("\n");
            mStringBuilderServiceDetails.append(mTextViewPrice.getText().toString());

            SharingDialogFragment mSharingDialogFragment = new SharingDialogFragment();
            Bundle mBundle = new Bundle();
            mBundle.putString(getString(R.string.bundle_provider_id),mStringProviderID);
            mBundle.putString(getString(R.string.bundle_service_id), mStringCategoryID+"-"+mStringServiceID);
            mBundle.putString(getString(R.string.bundle_provider_name),mStringProviderName);
            mBundle.putString(getString(R.string.bundle_location), mStringBuilderServiceDetails.toString());
            if (mServiceDataParser.getServiceimage() != null && mServiceDataParser.getServiceimage().size()>0) {
                mBundle.putString(getString(R.string.bundle_provider_image), mServiceDataParser.getServiceimage().get(0).getServicepicture_name());
            }else {
                mBundle.putString(getString(R.string.bundle_provider_image), mStringProviderPic);
            }
            mSharingDialogFragment.setArguments(mBundle);
            mSharingDialogFragment.show(getChildFragmentManager(), "");

        }
        else if (v == mImageViewReport) {

            showReportDialog();

        }
    }

    /**
     * Method will open report dialog.
     */
    public void showReportDialog()
    {
        mDialogReport = new Dialog(mActivity);
        mDialogReport.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mDialogReport.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        mDialogReport.setContentView(R.layout.dialog_sharing);
//        Window window = mDialogReport.getWindow();
        mDialogReport.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        final TextView mTextViewDialogTitle = (TextView) mDialogReport.findViewById(R.id.dialog_sharing_textview_title);
        mTextViewDialogTitle.setText(R.string.lbl_report);

        ImageView mImageViewCloase = (ImageView)mDialogReport.findViewById(R.id.dialog_sharing_imageview_cloase);
        mImageViewCloase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mDialogReport.cancel();
            }
        });

        TextView mTextViewProvider = (TextView)mDialogReport.findViewById(R.id.dialog_sharing_textview_facebook);
        mTextViewProvider.setText(R.string.lbl_report_on_provider);
        mTextViewProvider.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                ReportDialogFragment mSharingDialogFragment = new ReportDialogFragment();
                Bundle mBundle = new Bundle();
                mBundle.putString(getString(R.string.bundle_report_userid), mStringProviderID);
                mBundle.putString(getString(R.string.bundle_report_contentid), mStringProviderID);
                mBundle.putString(getString(R.string.bundle_report_type), StaticData.REPORT_USER);
                mSharingDialogFragment.setArguments(mBundle);
                mSharingDialogFragment.show(getChildFragmentManager(), "");
                mDialogReport.cancel();
            }
        });

        TextView mTextViewService = (TextView)mDialogReport.findViewById(R.id.dialog_sharing_textview_twitter);
        mTextViewService.setText(R.string.lbl_report_on_service);
        mTextViewService.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                ReportDialogFragment mSharingDialogFragment = new ReportDialogFragment();
                Bundle mBundle = new Bundle();
                mBundle.putString(getString(R.string.bundle_report_userid), mStringProviderID);
                mBundle.putString(getString(R.string.bundle_report_contentid), mStringServiceID);
                mBundle.putString(getString(R.string.bundle_report_type), StaticData.REPORT_SERVICE);
                mSharingDialogFragment.setArguments(mBundle);
                mSharingDialogFragment.show(getChildFragmentManager(), "");
                mDialogReport.cancel();
            }
        });

        TextView mTextViewWhatsapp = (TextView)mDialogReport.findViewById(R.id.dialog_sharing_textview_whatsapp);
        mTextViewWhatsapp.setVisibility(View.GONE);
        TextView mTextViewInstagram = (TextView)mDialogReport.findViewById(R.id.dialog_sharing_textview_instagram);
        mTextViewInstagram.setVisibility(View.GONE);

//        mTextViewDialogTitle.setText(mStringTitle);

        mDialogReport.show();
    }

    public void setQuantityForBookAppointment()
    {

        BookAppointmentParser mBookAppointmentParser = new BookAppointmentParser();
        mBookAppointmentParser.setCategory_id(mStringCategoryID);
        mBookAppointmentParser.setCategory_name(mStringCategoryName);
        mBookAppointmentParser.setQuantity(String.valueOf(intValue));
        mBookAppointmentParser.setService_id(mStringServiceID);
        mBookAppointmentParser.setService_name(mServiceDataParser.getService_name());
        double totalPrice = intValue * (Double.parseDouble(mServiceDataParser.getService_avgprice()));
        mBookAppointmentParser.setTotal_price(String.valueOf(totalPrice));
        mActivity.getMyApplication().addServiceForBookAppointment(mStringServiceID, mBookAppointmentParser);
    }

    /**
     * Method call will set data for edit.
     */
    private void setData() {

        if (getArguments() != null) {
            mArryListServicePicPath = new ArrayList<String>();
            mServiceDataParser = new ServiceDataParser();
            mServiceDataParser = getArguments().getParcelable(getString(R.string.bundle_service_data));

            mStringCategoryID = getArguments().getString(getString(R.string.bundle_category_id));
            mStringCategoryName = getArguments().getString(getString(R.string.bundle_category_name));
            mStringServiceID = mServiceDataParser.getService_id();

            mStringProviderName = getArguments().getString(getString(R.string.bundle_provider_name));
            mStringProviderPic = getArguments().getString(getString(R.string.bundle_provider_image));
            mStringProviderID = getArguments().getString(getString(R.string.bundle_provider_id));

            mTextViewServiceName.setText(mServiceDataParser.getService_name());
            mTextViewDesc.setText(mServiceDataParser.getService_desc());
            mTextViewPrice.setText(getString(R.string.lbl_average_price, mServiceDataParser.getService_avgprice()));
            mTextViewAvgTime.setText(getString(R.string.lbl_average_time, mServiceDataParser.getServicetime_value()));

            if(mServiceDataParser.getComment()!=null) {
                mLinearLayoutCommentSection.setVisibility(View.VISIBLE);
                mTextViewTotalComments.setText(getString(R.string.lbl_comments)+mServiceDataParser.getComment().getTotalcomment());
                mTextViewComment.setText(mServiceDataParser.getComment().getUserrating_comment());
                mTextViewCommentBy.setText(getString(R.string.lbl_by)+mServiceDataParser.getComment().getUsername());
            }else{
                mLinearLayoutCommentSection.setVisibility(View.GONE);
            }


            mTextViewQuentityValue.setText(String.valueOf(intValue));
            mArryListServicePicPath.clear();
            if (mServiceDataParser.getServiceimage() != null) {
                for (int i = 0; i < mServiceDataParser.getServiceimage().size(); i++) {
                    mArryListServicePicPath.add(mServiceDataParser.getServiceimage().get(i).getServicepicture_name());
                }
                mServicePictureAdapter = new ServicePictureAdapter();
                mHorizontalListView.setAdapter(mServicePictureAdapter);
                if (mArryListServicePicPath.size() > 0) {
                    mHorizontalListView.setVisibility(View.VISIBLE);
                } else {
                    mHorizontalListView.setVisibility(View.GONE);
                }
            } else {
                mHorizontalListView.setVisibility(View.GONE);
            }
        }

    }

    /**
     * BaseAdapter class for load data into listview
     *
     * @author ebaraiya
     */
    public class ServicePictureAdapter extends BaseAdapter {
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

            mImageLoader.getInstance().displayImage(mArryListServicePicPath.get(position), mViewHolder.mImageView, mDisplayImageOptions);

            mViewHolder.mImageViewOption.setVisibility(View.GONE);


            return convertview;
        }

    }

    public class ViewHolder {
        ImageView mImageView;
        ImageView mImageViewOption;

    }

}



