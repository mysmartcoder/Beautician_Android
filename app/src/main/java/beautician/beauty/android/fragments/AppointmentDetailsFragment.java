package beautician.beauty.android.fragments;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Dialog;

import beautician.beauty.android.utilities.InterfaceDialogClickListener;
import beautician.beauty.android.views.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.login.LoginManager;
import com.google.android.gms.maps.model.LatLng;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import beautician.beauty.android.R;
import beautician.beauty.android.activities.MyFragmentActivity;
import beautician.beauty.android.parsers.AppointmentDataParser;
import beautician.beauty.android.parsers.AppointmentListParser;
import beautician.beauty.android.parsers.LoginParser;
import beautician.beauty.android.utilities.CommonMethod;
import beautician.beauty.android.utilities.StaticData;
import beautician.beauty.android.views.ReportDialogFragment;

@SuppressLint("InflateParams")
public class AppointmentDetailsFragment extends Fragment implements OnClickListener {

    private MyFragmentActivity mActivity;
    private View rootView;
    private Dialog mDialogRate;
    private ServicesAdapter mServicesAdapter;
    private BackProcessGetAppointmentDetails mBackProcessGetAppointmentDetails;
    private ProgressDialog mProgressDialog;
    private String mMethodGetDetails = "GetDetails";
    private String mMethodGiveRates = "GiveRates";
    private String mMethodConfirmShowUp = "ConfirmShowUp";
    private String mMethodCancelAppointment = "CancelAppointment";


    private TextView mTextViewReferenceNo;
    private TextView mTextViewDate;
    private TextView mTextViewStatus;
    private TextView mTextViewProviderName;
    private TextView mTextViewProviderContact;
//    private TextView mTextViewRateUser;
    private TextView mTextViewRateSeeker;
    private TextView mTextViewRateProvider;
    private TextView mTextViewReportSeeker;
    private TextView mTextViewReportProvider;

    private TextView mTextViewSeekerName;
    private TextView mTextViewSeekerContact;
    private TextView mTextViewConfirmShowUp;
    private TextView mTextViewTotalPrice;
    private TextView mTextViewAddress;
    private TextView mTextViewCancelAppointment;
    private LinearLayout mLinearLayoutServices;
    private RelativeLayout mRelativeLayoutLocation;
    private RelativeLayout mRelativeLayoutDeposite;
    private TextView mTextViewDepositAmount;
    private TextView mTextViewCOD;

    private EditText mDialogEditTextComment;
    private TextView mDialogTextViweYse;
    private TextView mDialogTextViweMayBe;
    private TextView mDialogTextViweNo;
    private TextView mDialogTextViweSubmit;
    private TextView mTextViewTransFees;

    private RelativeLayout mRelativeLayoutTransFees;

    private String mStringAppointmentID = "";
    private String mStringToUser = "";
    private String mStringCategoryID = "";
    private String mStringServiceID = "";
    private String mStringAnswer = "";
    private String mStringComments = "";
    private String mStringAppointmentStatus = "";

    private LoginParser mRateUserParser;
    private LoginParser mCancelAppParser;
    private LoginParser mConfirmShowParser;
    private AppointmentDataParser mAppointmentDataParser;
    private AppointmentListParser mAppointmentListParser;

    private float mIntTotalPrice = 0;
    private float mIntCOD = 0;
    private double mDoubleDistance = 0;

    private CommonMethod mCommonMethod;

    public AppointmentDetailsFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_appointment_details, container, false);

        mActivity = (MyFragmentActivity) getActivity();
        mActivity.setHeaderTitle(R.string.lbl_appointment_details);
        mActivity.setSearchIcon(R.drawable.icon_search);
        mActivity.onBackButton(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mActivity.replaceFragment(new SearchFragment(), true);
            }
        });

        mAppointmentListParser = new AppointmentListParser();
        mRateUserParser = new LoginParser();
        mCancelAppParser = new LoginParser();
        mConfirmShowParser = new LoginParser();
        mCommonMethod = new CommonMethod(mActivity);

        getWidgetRefrence(rootView);
        registerOnClick();

        if (getArguments() != null) {
            String mStringFrom = getArguments().getString(getString(R.string.bundle_from));
            if (mStringFrom.equalsIgnoreCase(getString(R.string.bundle_from_list))) {
                mAppointmentDataParser = getArguments().getParcelable(getString(R.string.bundle_appointment_data));
                setData();
            }else
            {
                mStringAppointmentID = getArguments().getString(getString(R.string.bundle_appointment_id));
                mBackProcessGetAppointmentDetails = new BackProcessGetAppointmentDetails();
                mBackProcessGetAppointmentDetails.execute(mMethodGetDetails);
            }
        }

        if(mActivity.getMyApplication().isAccountActive()) {

        }else
        {
            mActivity.getAppAlertDialog().showDialog("", getString(R.string.alt_msg_disable_account), true);
        }


        return rootView;
    }


    /**
     * Method call will get IDs from xml file.
     *
     * @param v
     */
    private void getWidgetRefrence(View v) {

        mTextViewReferenceNo = (TextView) v.findViewById(R.id.fragment_appointment_detail_textview_reference_no);
        mTextViewDate = (TextView) v.findViewById(R.id.fragment_appointment_detail_textview_date);
        mTextViewStatus = (TextView) v.findViewById(R.id.fragment_appointment_detail_textview_status);
        mTextViewProviderName = (TextView) v.findViewById(R.id.fragment_appointment_detail_textview_provider_name);
        mTextViewProviderContact = (TextView) v.findViewById(R.id.fragment_appointment_detail_textview_provider_contact);
        mTextViewRateSeeker = (TextView) v.findViewById(R.id.fragment_appointment_detail_textview_rate_seeker_user);
        mTextViewRateProvider = (TextView) v.findViewById(R.id.fragment_appointment_detail_textview_rate_user);
        mTextViewReportSeeker = (TextView) v.findViewById(R.id.fragment_appointment_detail_textview_report_seeker_user);
        mTextViewReportProvider = (TextView) v.findViewById(R.id.fragment_appointment_detail_textview_report_user);
        mTextViewSeekerName = (TextView) v.findViewById(R.id.fragment_appointment_detail_textview_seeker_name);
        mTextViewSeekerContact = (TextView) v.findViewById(R.id.fragment_appointment_detail_textview_seeker_contact);
        mTextViewConfirmShowUp = (TextView) v.findViewById(R.id.fragment_appointment_detail_textview_confirm_show_up);
        mTextViewTotalPrice = (TextView) v.findViewById(R.id.fragment_appointment_detail_textview_total_price);
        mTextViewAddress = (TextView) v.findViewById(R.id.fragment_appointment_detail_textview_address);
        mTextViewCancelAppointment = (TextView) v.findViewById(R.id.fragment_appointment_detail_textview_cancel_appointment);
        mLinearLayoutServices = (LinearLayout) v.findViewById(R.id.fragment_appointment_detail_linear_row);
        mRelativeLayoutLocation = (RelativeLayout) v.findViewById(R.id.fragment_appointment_detail_relative_pick_location);
        mRelativeLayoutDeposite = (RelativeLayout) v.findViewById(R.id.fragment_appointment_detail_relative_deposit);
        mTextViewDepositAmount = (TextView) v.findViewById(R.id.fragment_appointment_detail_textview_total_confirm_deposit);
        mTextViewCOD = (TextView) v.findViewById(R.id.fragment_appointment_detail_textview_total_cod);

        mTextViewTransFees = (TextView) v.findViewById(R.id.fragment_appointment_detail_textview_trans_fee);
        mRelativeLayoutTransFees = (RelativeLayout) v.findViewById(R.id.fragment_appointment_detail_relative_trans_fees);

    }

    /**
     * Method call will Register OnClick() Events for widgets.
     */
    private void registerOnClick() {

        mTextViewRateSeeker.setOnClickListener(this);
        mTextViewRateProvider.setOnClickListener(this);
        mTextViewReportSeeker.setOnClickListener(this);
        mTextViewReportProvider.setOnClickListener(this);
        mTextViewConfirmShowUp.setOnClickListener(this);
        mTextViewCancelAppointment.setOnClickListener(this);
        mRelativeLayoutLocation.setOnClickListener(this);

    }

    /**
     * Method call OnClick Event fire.
     */
    @Override
    public void onClick(View v) {

        if (v == mTextViewRateSeeker) {

            showRateUserDialog(mAppointmentDataParser.getSeekername());

        }
        else if (v == mTextViewRateProvider ) {

            showRateUserDialog(mAppointmentDataParser.getProvidername());

        }
        else if (v == mTextViewConfirmShowUp) {

            mBackProcessGetAppointmentDetails = new BackProcessGetAppointmentDetails();
            mBackProcessGetAppointmentDetails.execute(mMethodConfirmShowUp);

        } else if (v == mTextViewCancelAppointment) {

//            mActivity.getAppAlertDialog().showDeleteAlert(mActivity, getString(R.string.app_name), getString(R.string.validation_cancel_appointment), new DialogInterface.OnClickListener() {
//                @Override
//                public void onClick(DialogInterface dialog, int which) {
//                    mBackProcessGetAppointmentDetails = new BackProcessGetAppointmentDetails();
//                    mBackProcessGetAppointmentDetails.execute(mMethodCancelAppointment);
//                }
//            });

            mActivity.getAppAlertDialog().showDeleteAlert(getString(R.string.app_name), getString(R.string.validation_cancel_appointment),
                    getString(R.string.lbl_no), getString(R.string.lbl_yes), new InterfaceDialogClickListener() {
                        @Override
                        public void onClick() {
                            mBackProcessGetAppointmentDetails = new BackProcessGetAppointmentDetails();
                            mBackProcessGetAppointmentDetails.execute(mMethodCancelAppointment);
                        }
                    });

        } else if (v == mRelativeLayoutLocation) {

//            CommonMapViewFragment mCommonMapViewFragment = new CommonMapViewFragment();
//            Bundle mBundle = new Bundle();
//            mBundle.putString(getString(R.string.bundle_lat), mAppointmentDataParser.getAppointment_lat());
//            mBundle.putString(getString(R.string.bundle_lng), mAppointmentDataParser.getAppointment_lng());
//            mBundle.putString(getString(R.string.bundle_address), mAppointmentDataParser.getAppointment_location());
//            mCommonMapViewFragment.setArguments(mBundle);
//            mActivity.replaceFragment(mCommonMapViewFragment, true);

            Uri gmmIntentUri = Uri.parse("http://maps.google.com/maps?saddr="+mActivity.getMyApplication().getCurrentLatitude()+","+mActivity.getMyApplication().getCurrentLongitude()+"&daddr="+mAppointmentDataParser.getAppointment_lat()+","+mAppointmentDataParser.getAppointment_lng());
//            Uri gmmIntentUri = Uri.parse("google.navigation:q="+mAppointmentDataParser.getAppointment_lat()+","+mAppointmentDataParser.getAppointment_lng());
            Intent intent = new Intent(android.content.Intent.ACTION_VIEW, gmmIntentUri);
            intent.setPackage("com.google.android.apps.maps");
            startActivity(intent);

        }else if (v == mTextViewReportSeeker) {

            ReportDialogFragment mSharingDialogFragment = new ReportDialogFragment();
            Bundle mBundle = new Bundle();
            mBundle.putString(getString(R.string.bundle_report_userid), mStringToUser);
            mBundle.putString(getString(R.string.bundle_report_contentid), mStringToUser);
            mBundle.putString(getString(R.string.bundle_report_type), StaticData.REPORT_USER);
            mSharingDialogFragment.setArguments(mBundle);
            mSharingDialogFragment.show(getChildFragmentManager(), "");

        }else if (v == mTextViewReportProvider) {
            ReportDialogFragment mSharingDialogFragment = new ReportDialogFragment();
            Bundle mBundle = new Bundle();
            mBundle.putString(getString(R.string.bundle_report_userid), mStringToUser);
            mBundle.putString(getString(R.string.bundle_report_contentid), mStringToUser);
            mBundle.putString(getString(R.string.bundle_report_type), StaticData.REPORT_USER);
            mSharingDialogFragment.setArguments(mBundle);
            mSharingDialogFragment.show(getChildFragmentManager(), "");
        }
    }

    public void setData() {
        mTextViewReferenceNo.setText(mAppointmentDataParser.getAppointment_refnumber());
        mTextViewDate.setText(mAppointmentDataParser.getAppointment_starttime());
        mTextViewAddress.setText(mAppointmentDataParser.getAppointment_location());

        mStringAppointmentID = mAppointmentDataParser.getAppointment_id();

        if (mAppointmentDataParser.getAppointment_status().equalsIgnoreCase(StaticData.APPOINTMENT_STATUS_CANCEL)) {
            mStringAppointmentStatus = mAppointmentDataParser.getAppointment_status();
            mTextViewConfirmShowUp.setVisibility(View.GONE);
            mTextViewRateSeeker.setVisibility(View.GONE);
            mTextViewRateProvider.setVisibility(View.GONE);
            mTextViewCancelAppointment.setVisibility(View.GONE);
            mTextViewStatus.setTextColor(Color.RED);
            mTextViewStatus.setText(mStringAppointmentStatus);
            if (mAppointmentDataParser.getUser_type().equalsIgnoreCase(getString(R.string.bundle_seeker)))
            {
                mTextViewReportProvider.setVisibility(View.VISIBLE);
                mTextViewReportSeeker.setVisibility(View.GONE);
                mStringToUser = mAppointmentDataParser.getAppointment_providerid();
            }else {
                mTextViewReportProvider.setVisibility(View.GONE);
                mTextViewReportSeeker.setVisibility(View.VISIBLE);
                mStringToUser = mAppointmentDataParser.getAppointment_seekerid();
            }

        } else {
            if (mAppointmentDataParser.getUser_type().equalsIgnoreCase(getString(R.string.bundle_seeker))) {
                mStringAppointmentStatus = mAppointmentDataParser.getAppointment_status_seeker();
                mTextViewStatus.setText(mStringAppointmentStatus);
                mStringToUser = mAppointmentDataParser.getAppointment_providerid();
                mTextViewRateSeeker.setVisibility(View.GONE);
            } else {
                mStringAppointmentStatus = mAppointmentDataParser.getAppointment_status_provider();
                mTextViewStatus.setText(mStringAppointmentStatus);
                mStringToUser = mAppointmentDataParser.getAppointment_seekerid();
                mTextViewRateProvider.setVisibility(View.GONE);
            }
        }

        if(mStringAppointmentStatus.equalsIgnoreCase(StaticData.APPOINTMENT_USER_STATUS_1) || mStringAppointmentStatus.equalsIgnoreCase(StaticData.APPOINTMENT_USER_STATUS_2))
        {
            if (mAppointmentDataParser.getUser_type().equalsIgnoreCase(getString(R.string.bundle_seeker))) {
                if(mAppointmentDataParser.getSeekerhascommented().equalsIgnoreCase("0"))
                    mTextViewRateProvider.setVisibility(View.VISIBLE);
                else
                    mTextViewRateProvider.setVisibility(View.GONE);

                mTextViewReportProvider.setVisibility(View.VISIBLE);
                mTextViewReportSeeker.setVisibility(View.GONE);
            }else{
                if(mAppointmentDataParser.getProviderhascommented().equalsIgnoreCase("0"))
                    mTextViewRateSeeker.setVisibility(View.VISIBLE);
                else
                    mTextViewRateSeeker.setVisibility(View.GONE);

                mTextViewReportProvider.setVisibility(View.GONE);
                mTextViewReportSeeker.setVisibility(View.VISIBLE);
            }
            mTextViewCancelAppointment.setVisibility(View.GONE);
        }else
        {
            mTextViewRateSeeker.setVisibility(View.GONE);
            mTextViewRateProvider.setVisibility(View.GONE);
        }

        mTextViewProviderName.setText(mAppointmentDataParser.getProvidername());
        mTextViewSeekerName.setText(mAppointmentDataParser.getSeekername());
        if (mAppointmentDataParser.getProviderphone().length() > 0)
            mTextViewProviderContact.setText(mAppointmentDataParser.getProviderphone());
        else
            mTextViewProviderContact.setText(mAppointmentDataParser.getProvideremail());

        if (mAppointmentDataParser.getSeekerphone().length() > 0)
            mTextViewSeekerContact.setText(mAppointmentDataParser.getSeekerphone());
        else
            mTextViewSeekerContact.setText(mAppointmentDataParser.getSeekeremail());


        setServices();

        LatLng mStart = new LatLng(Double.parseDouble(mAppointmentDataParser.getAppointment_lat()), Double.parseDouble(mAppointmentDataParser.getAppointment_lng()));
        LatLng mEnd = new LatLng(Double.parseDouble(mActivity.getMyApplication().getCurrentLatitude()), Double.parseDouble(mActivity.getMyApplication().getCurrentLongitude()));
        mDoubleDistance = mCommonMethod.getDistance(mStart, mEnd);

        if (mAppointmentDataParser.getAppointment_status().equalsIgnoreCase(StaticData.APPOINTMENT_STATUS_CANCEL)) {
            mTextViewConfirmShowUp.setVisibility(View.GONE);
        }else{
            if (mStringAppointmentStatus.equalsIgnoreCase(StaticData.APPOINTMENT_STATUS_CONFIRMED)) {
                SimpleDateFormat dateFormat = new SimpleDateFormat(StaticData.DATE_FORMAT_6);
                Date myDate = null;
                try {
                    myDate = dateFormat.parse(mAppointmentDataParser.getAppointment_starttime());
                } catch (java.text.ParseException e) {
                    e.printStackTrace();
                }
                Long currentTime = System.currentTimeMillis();
                Long appointmentTime = myDate.getTime() ;
                if(currentTime > appointmentTime && currentTime < (appointmentTime+1800000))
                {
                    if (mDoubleDistance > 2000) {
                        mTextViewConfirmShowUp.setVisibility(View.GONE);
                    }else{
                        mTextViewConfirmShowUp.setVisibility(View.VISIBLE);
                    }
                }else if(currentTime > (appointmentTime+1800000)) {
                    mTextViewConfirmShowUp.setVisibility(View.VISIBLE);
                    mTextViewConfirmShowUp.setText(R.string.lbl_you_missed_appointment);
                    mTextViewConfirmShowUp.setBackgroundResource(R.drawable.cancel_button_selector);
                    mTextViewConfirmShowUp.setEnabled(false);
                }else {
                    mTextViewConfirmShowUp.setVisibility(View.GONE);
                }

                String currentDate = mCommonMethod.getFormateFromCalendar(Calendar.getInstance(), StaticData.DATE_FORMAT_10);
                if(mAppointmentDataParser.getAppointment_starttime().contains(currentDate))
                {
                    mTextViewStatus.setText(StaticData.APPOINTMENT_STATUS_TODAY);
                }

                if(currentTime > appointmentTime)
                {
                    mTextViewCancelAppointment.setVisibility(View.GONE);
                }

            }else
            {
                mTextViewConfirmShowUp.setVisibility(View.GONE);
            }
        }

    }

    public void setServices() {
        mLinearLayoutServices.removeAllViews();
        if(mAppointmentDataParser.getAppointmentinfo()==null)
            return;
        for (int i = 0; i < mAppointmentDataParser.getAppointmentinfo().size(); i++) {
            View convertView = mActivity.getLayoutInflater().inflate(R.layout.row_book_appointment_services, null);

            TextView mTextViewName = (TextView) convertView.findViewById(R.id.row_book_appointment_service_name);
            TextView mTextViewPrice = (TextView) convertView.findViewById(R.id.row_book_appointment_service_price);
            TextView mTextViewCateName = (TextView) convertView.findViewById(R.id.row_book_appointment_category_name);

            // mTextViewName.setText(mAppointmentDataParser.getAppointmentinfo().get(i).getService_name());
            mTextViewPrice.setText(getString(R.string.lbl_sr) + " " + mCommonMethod.getTwodigitValue(Float.parseFloat(mAppointmentDataParser.getAppointmentinfo().get(i).getAppointmentdet_price())));

            if(mActivity.getMyApplication().getCurrentLang().equalsIgnoreCase("en"))
                mTextViewCateName.setText(mAppointmentDataParser.getAppointmentinfo().get(i).getCategory_name() + " (" + mAppointmentDataParser.getAppointmentinfo().get(i).getAppointmentdet_quantity() + ")");
            else
                mTextViewCateName.setText(mAppointmentDataParser.getAppointmentinfo().get(i).getCategory_namearebic() + " (" + mAppointmentDataParser.getAppointmentinfo().get(i).getAppointmentdet_quantity() + ")");

            if (mAppointmentDataParser.getAppointmentinfo().get(i).getService_name() != null) {

                mTextViewCateName.setVisibility(View.VISIBLE);
                mTextViewName.setText(mAppointmentDataParser.getAppointmentinfo().get(i).getService_name());

            } else {
                mTextViewName.setText(R.string.lbl_service_has_been_deleted);
                mTextViewName.setTextColor(Color.RED);
                mTextViewCateName.setVisibility(View.INVISIBLE);
            }

            mLinearLayoutServices.addView(convertView);
            mIntTotalPrice = Float.parseFloat(mAppointmentDataParser.getAppointmentinfo().get(i).getAppointmentdet_price()) + mIntTotalPrice;

            if (mStringServiceID != null) {
                if (mStringServiceID.length() > 0)
                    mStringServiceID = mStringServiceID + "," + mAppointmentDataParser.getAppointmentinfo().get(i).getService_id();
                else
                    mStringServiceID = mAppointmentDataParser.getAppointmentinfo().get(i).getService_id();

                if (mStringCategoryID.length() > 0)
                    mStringCategoryID = mStringCategoryID + "," + mAppointmentDataParser.getAppointmentinfo().get(i).getService_categoryid();
                else
                    mStringCategoryID = mAppointmentDataParser.getAppointmentinfo().get(i).getService_categoryid();
            }
        }
        mTextViewTotalPrice.setText(getString(R.string.lbl_sr) + " " + String.valueOf(mCommonMethod.getTwodigitValue(mIntTotalPrice)));
        //mTextViewTotalPrice.setText(getString(R.string.lbl_sr) + " " + String.valueOf(mIntTotalPrice));

        //set deposite amount
        mTextViewDepositAmount.setText(getString(R.string.lbl_sr) + " " + mAppointmentDataParser.getAppointment_depositamt());
        mIntCOD = mIntTotalPrice - Float.parseFloat(mAppointmentDataParser.getAppointment_depositamt());
        mTextViewCOD.setText(getString(R.string.lbl_sr) + " " + String.valueOf(mCommonMethod.getTwodigitValue(mIntCOD)));

        //Set Trans Fees
        if(!mAppointmentDataParser.getAppointment_transportfee().equalsIgnoreCase("0"))
        {
            mRelativeLayoutTransFees.setVisibility(View.VISIBLE);
            mTextViewTransFees.setText(getString(R.string.lbl_sr) + " " + mCommonMethod.getTwodigitValue(Float.parseFloat(mAppointmentDataParser.getAppointment_transportfee())));
        }else
        {
            mRelativeLayoutTransFees.setVisibility(View.GONE);
        }
    }


    /**
     * Method call will show rate user dialog
     */

    private void showRateUserDialog(String message) {
        mDialogRate = new Dialog(mActivity);
        mDialogRate.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mDialogRate.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        mDialogRate.setContentView(R.layout.dialog_rate_user);
        Window window = mDialogRate.getWindow();
        window.setLayout(ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.MATCH_PARENT);
        mDialogRate.show();
        mDialogEditTextComment = (EditText) mDialogRate.findViewById(R.id.dialog_rate_user_edittext_comments);
        TextView mDialogTextViweHeader = (TextView) mDialogRate.findViewById(R.id.dialog_rate_user_textview_msg);
        if(mActivity.getMyApplication().getCurrentLang().equalsIgnoreCase("en")) {
            mDialogTextViweHeader.setText(getString(R.string.alt_msg_rate_user) + " " + message + " ?");
        }else
        {
            mDialogTextViweHeader.setText( " ? "+message +" " + getString(R.string.alt_msg_rate_user) );
        }
        mDialogTextViweYse = (TextView) mDialogRate.findViewById(R.id.dialog_rate_user_textview_yes);
        mDialogTextViweMayBe = (TextView) mDialogRate.findViewById(R.id.dialog_rate_user_textview_may_be);
        mDialogTextViweNo = (TextView) mDialogRate.findViewById(R.id.dialog_rate_user_textview_no);
        mDialogTextViweSubmit = (TextView) mDialogRate.findViewById(R.id.dialog_rate_user_textview_submit);
        mDialogTextViweYse.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                mStringAnswer = "yes";
                setRateStatus();
                mDialogTextViweYse.setCompoundDrawablesWithIntrinsicBounds(ContextCompat.getDrawable(mActivity, R.drawable.icon_radio_btn_selected), null, null, null);
            }
        });
        mDialogTextViweMayBe.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                mStringAnswer = "maybe";
                setRateStatus();
                mDialogTextViweMayBe.setCompoundDrawablesWithIntrinsicBounds(ContextCompat.getDrawable(mActivity, R.drawable.icon_radio_btn_selected), null, null, null);
            }
        });
        mDialogTextViweNo.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                mStringAnswer = "no";
                setRateStatus();
                mDialogTextViweNo.setCompoundDrawablesWithIntrinsicBounds(ContextCompat.getDrawable(mActivity, R.drawable.icon_radio_btn_selected), null, null, null);
            }
        });
        mDialogTextViweSubmit.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                if (mStringAnswer.equalsIgnoreCase("no")) {
                    if (mDialogEditTextComment.getText().length() > 0) {
                        mDialogRate.cancel();
                        mStringComments = mDialogEditTextComment.getText().toString().trim();

                        mBackProcessGetAppointmentDetails = new BackProcessGetAppointmentDetails();
                        mBackProcessGetAppointmentDetails.execute(mMethodGiveRates);
                    } else {
                        mActivity.getAppAlertDialog().showDialog(getString(R.string.app_name), getString(R.string.validation_comment), false);
                    }
                } else {
                    mDialogRate.cancel();
                    mStringComments = mDialogEditTextComment.getText().toString().trim();

                    mBackProcessGetAppointmentDetails = new BackProcessGetAppointmentDetails();
                    mBackProcessGetAppointmentDetails.execute(mMethodGiveRates);
                }
            }
        });
    }

    /**
     * Method call will set Yes, No and May Be rates.
     */
    private void setRateStatus() {
        mDialogTextViweYse.setCompoundDrawablesWithIntrinsicBounds(ContextCompat.getDrawable(mActivity, R.drawable.icon_radio_btn_unselect), null, null, null);
        mDialogTextViweNo.setCompoundDrawablesWithIntrinsicBounds(ContextCompat.getDrawable(mActivity, R.drawable.icon_radio_btn_unselect), null, null, null);
        mDialogTextViweMayBe.setCompoundDrawablesWithIntrinsicBounds(ContextCompat.getDrawable(mActivity, R.drawable.icon_radio_btn_unselect), null, null, null);
    }

    /**
     * BaseAdapter class for load data into listview
     */
    public class ServicesAdapter extends BaseAdapter {
        ViewHolder mViewHolder;

        @Override
        public int getCount() {
            return 10;
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
                convertview = mActivity.getLayoutInflater().inflate(R.layout.row_book_appointment_services, null);
                mViewHolder = new ViewHolder();


                convertview.setTag(mViewHolder);

            } else {
                mViewHolder = (ViewHolder) convertview.getTag();
            }

            return convertview;
        }

    }

    public class ViewHolder {
        TextView mTextViewName;

    }

    /**
     * AsyncTask for calling webservice in background.
     * @author ebaraiya
     */
    public class BackProcessGetAppointmentDetails extends AsyncTask<String, Void, String> {
        String mCurrentMethod = "";

        @Override
        protected void onPreExecute() {
            mProgressDialog = ProgressDialog.show(mActivity, "", getString(R.string.dialog_loading), true);
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {
            mCurrentMethod = params[0];
            if (mCurrentMethod.equalsIgnoreCase(mMethodGetDetails)) {
                mAppointmentListParser = (AppointmentListParser)mActivity.getWebMethod().callGetAppointmentByID(mActivity.getMyApplication().getUserFiled(getString(R.string.sp_user_id)),
                        mActivity.getMyApplication().getUserFiled(getString(R.string.sp_user_hash)),
                        mStringAppointmentID, mAppointmentListParser);
            } else if (mCurrentMethod.equalsIgnoreCase(mMethodGiveRates)) {

                mRateUserParser = (LoginParser) mActivity.getWebMethod().callAddCommentUser(mActivity.getMyApplication().getUserFiled(getString(R.string.sp_user_id)),
                        mActivity.getMyApplication().getUserFiled(getString(R.string.sp_user_hash)),
                        mStringAppointmentID,
                        mStringToUser,
                        mStringCategoryID,
                        mStringServiceID,
                        mStringAnswer,
                        mStringComments,
                        mRateUserParser);

            } else if (mCurrentMethod.equalsIgnoreCase(mMethodCancelAppointment)) {

                mCancelAppParser = (LoginParser) mActivity.getWebMethod().callCancelAppointment(mActivity.getMyApplication().getUserFiled(getString(R.string.sp_user_id)),
                        mActivity.getMyApplication().getUserFiled(getString(R.string.sp_user_hash)),
                        mStringAppointmentID,
                        mCancelAppParser);

            } else if (mCurrentMethod.equalsIgnoreCase(mMethodConfirmShowUp)) {


                mConfirmShowParser = (LoginParser) mActivity.getWebMethod().callConfirmShowUp(mActivity.getMyApplication().getUserFiled(getString(R.string.sp_user_id)),
                        mActivity.getMyApplication().getUserFiled(getString(R.string.sp_user_hash)),
                        mStringAppointmentID, mAppointmentDataParser.getAppointment_lat(),
                        mAppointmentDataParser.getAppointment_lng(),
                        mActivity.getMyApplication().getUserFiled(getString(R.string.sp_user_lat)),
                        mActivity.getMyApplication().getUserFiled(getString(R.string.sp_user_lon)),
                        mConfirmShowParser);

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

                    if (mCurrentMethod.equalsIgnoreCase(mMethodGetDetails)) {
                        if (mAppointmentListParser.getWs_status().equalsIgnoreCase("true") && mAppointmentListParser.getData() != null) {
                            mAppointmentDataParser = mAppointmentListParser.getData().get(0);
                            setData();
                        } else {
                            if (mAppointmentListParser.getMessage().toString().contains(getString(R.string.alt_msg_exipred))) {
                                mActivity.getAppAlertDialog().showAlertWithSingleButton("", mAppointmentListParser.getMessage().toString(),
                                        mActivity.getString(R.string.lbl_logout),
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                mActivity.logout();
                                            }
                                        });
                            }
                        }

                    } else if (mCurrentMethod.equalsIgnoreCase(mMethodGiveRates)) {

                        if (mRateUserParser.getWs_status().equalsIgnoreCase("true")) {
                            mActivity.getAppAlertDialog().showDialog(getString(R.string.app_name), mRateUserParser.getMessage().toString(), false);
                            mTextViewRateProvider.setVisibility(View.GONE);
                            mTextViewRateSeeker.setVisibility(View.GONE);
                        } else {
                            if (mRateUserParser.getMessage().toString().contains(getString(R.string.alt_msg_exipred))) {
                                mActivity.getAppAlertDialog().showAlertWithSingleButton("", mRateUserParser.getMessage().toString(),
                                        mActivity.getString(R.string.lbl_logout),
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                mActivity.logout();
                                            }
                                        });
                            } else {
                                mActivity.getAppAlertDialog().showDialog(getString(R.string.app_name), mRateUserParser.getMessage().toString(), false);
                            }
                        }

                    } else if (mCurrentMethod.equalsIgnoreCase(mMethodCancelAppointment)) {

                        if (mCancelAppParser.getWs_status().equalsIgnoreCase("true")) {
//                            mActivity.getAppAlertDialog().showDialog(getString(R.string.app_name), mCancelAppParser.getMessage().toString(), true);

                            mActivity.getAppAlertDialog().showAlertWithSingleButton("", mCancelAppParser.getMessage().toString(),
                                    mActivity.getString(R.string.lbl_ok),
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            mCommonMethod.cancelNotification(mStringAppointmentID);
                                            mActivity.onBackPressed();
                                            mActivity.onBackPressed();
                                            mActivity.replaceFragment(new MyCalendarFragment(), true);
                                        }
                                    });

                        } else {
                            if (mCancelAppParser.getMessage().toString().contains(getString(R.string.alt_msg_exipred))) {
                                mActivity.getAppAlertDialog().showAlertWithSingleButton("", mCancelAppParser.getMessage().toString(),
                                        mActivity.getString(R.string.lbl_logout),
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                mActivity.logout();
                                            }
                                        });
                            } else {
                                mActivity.getAppAlertDialog().showDialog(getString(R.string.app_name), mCancelAppParser.getMessage().toString(), false);
                            }
                        }

                    } else if (mCurrentMethod.equalsIgnoreCase(mMethodConfirmShowUp)) {


                        if (mConfirmShowParser.getWs_status().equalsIgnoreCase("true")) {
//                            mActivity.getAppAlertDialog().showDialog(getString(R.string.app_name), mRateUserParser.getMessage().toString(), false);
                            mTextViewConfirmShowUp.setVisibility(View.GONE);

                            mBackProcessGetAppointmentDetails = new BackProcessGetAppointmentDetails();
                            mBackProcessGetAppointmentDetails.execute(mMethodGetDetails);

                        } else {
                            if (mConfirmShowParser.getMessage().toString().contains(getString(R.string.alt_msg_exipred))) {
                                mActivity.getAppAlertDialog().showAlertWithSingleButton("", mConfirmShowParser.getMessage().toString(),
                                        mActivity.getString(R.string.lbl_logout),
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                mActivity.logout();
                                            }
                                        });
                            } else {
                                mActivity.getAppAlertDialog().showDialog(getString(R.string.app_name), mConfirmShowParser.getMessage().toString(), false);
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

    @Override
    public void onResume() {
        mActivity.registerReceiver(broadcastReceiver, new IntentFilter(StaticData.ACTION_CANCEL_APPOINTMENT));
        super.onResume();
    }

    @Override
    public void onPause() {
        mActivity.unregisterReceiver(broadcastReceiver);
        super.onPause();
    }

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                if(intent.getStringExtra(getString(R.string.bundle_appointment_id)).equalsIgnoreCase(mStringAppointmentID))
                {
                    mBackProcessGetAppointmentDetails = new BackProcessGetAppointmentDetails();
                    mBackProcessGetAppointmentDetails.execute(mMethodGetDetails);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };
}
