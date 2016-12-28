package beautician.beauty.android.fragments;

import android.annotation.SuppressLint;
import beautician.beauty.android.views.ProgressDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;

import java.util.Iterator;

import beautician.beauty.android.R;
import beautician.beauty.android.activities.MyFragmentActivity;
import beautician.beauty.android.parsers.BookAppointmentParser;
import beautician.beauty.android.parsers.DepositStatusParser;
import beautician.beauty.android.utilities.CommonMethod;

@SuppressLint("InflateParams")
public class DepositFragment extends Fragment implements OnClickListener {

    private MyFragmentActivity mActivity;
    private CommonMethod mCommonMethod;
    private View rootView;

    private DisplayImageOptions mDisplayImageOptions;

    private BackProcessDeposit mBackProcessDeposit;
    private ProgressDialog mProgressDialog;
    private String mMethodSetDeposit = "SetDeposit";
    private String mMethodGetDepositDetail = "GetDepositDetail";
    private String mStringCategoryID = "";
    private DepositStatusParser mDepositStatusParser;

    private ImageView mImageViewProviderPic;
    private TextView mTextViewProviderName;
    private TextView mTextViewLocation;
    private TextView mTextViewDate;
    private TextView mTextViewTotalPrice;
    private TextView mTextViewTotalConfirmDeposit;
    private TextView mTextViewSADAD;
    private TextView mTextViewCreditCard;
    private TextView mTextViewPaypal;
    private TextView mTextViewCurrentBalance;
    private TextView mTextViewUseWalletBalance;
    private TextView mTextViewRemainBalance;
    private CheckBox mCheckBoxUseWallet;
    private TextView mTextViewPayDeposit;
    private TextView mTextViewCOD;
    private TextView mTextViewTransFees;

    private LinearLayout mLinearLayoutRow;
    private LinearLayout mLinearLayoutPaymentOption;
    private RelativeLayout mRelativeLayoutTransFees;

    private String mStringProviderName = "";
    private String mStringProviderPic = "";
    private String mStringProviderID = "";
    private String mStringLocation = "";
    private String mStringLocationPolicy = "";
    private String mStringDateTime = "";
    private String mStringAppointmentID = "";
    private String mStringPaymentType = "";

    public String mStringTransFees = "0";
    public String mStringMinApptFees = "0";

    private float mIntMinAppPrice = 0;

    private float mIntDepositePrice = 0;
    private float mIntTotalPrice = 0;
    private float mIntMyWallet = 0;
    private float mIntMyWalletUse = 0;
    private float mIntRemainBalance = 0;
    private float mIntCOD = 0;


    public DepositFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_deposit, container, false);

        mActivity = (MyFragmentActivity) getActivity();
        mActivity.setHeaderTitle(R.string.lbl_deposit);
        mActivity.setSearchIcon(R.drawable.icon_search);
        mActivity.onBackButton(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mActivity.replaceFragment(new SearchFragment(), true);
            }
        });

        mCommonMethod = new CommonMethod(mActivity);
        mDepositStatusParser = new DepositStatusParser();

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

        mStringPaymentType = "";

        setData();

        mBackProcessDeposit = new BackProcessDeposit();
        mBackProcessDeposit.execute(mMethodGetDepositDetail);

        return rootView;
    }


    /**
     * Method call will set data..
     */
    public void setData() {

        if (getArguments() != null) {

            mStringProviderName = getArguments().getString(getString(R.string.bundle_provider_name));
            mStringProviderPic = getArguments().getString(getString(R.string.bundle_provider_image));
            mStringProviderID = getArguments().getString(getString(R.string.bundle_provider_id));
            mStringLocation = getArguments().getString(getString(R.string.bundle_location));
            mStringLocationPolicy = getArguments().getString(getString(R.string.bundle_location_policy));
            mStringDateTime = getArguments().getString(getString(R.string.bundle_time));
            mStringAppointmentID = getArguments().getString(getString(R.string.bundle_appointment_id));

            mStringMinApptFees = getArguments().getString(getString(R.string.bundle_min_appt_price));
            mIntMinAppPrice = Float.parseFloat(mStringMinApptFees);
            mStringTransFees = getArguments().getString(getString(R.string.bundle_trans_fees));

            ImageLoader.getInstance().displayImage(mStringProviderPic, mImageViewProviderPic, mDisplayImageOptions);
            mTextViewProviderName.setText(mStringProviderName);
            mTextViewDate.setText(mStringDateTime);
            mTextViewLocation.setText(mStringLocation);

            if(mStringLocationPolicy.equalsIgnoreCase("seeker"))
            {
                mRelativeLayoutTransFees.setVisibility(View.VISIBLE);
                mTextViewTransFees.setText(getString(R.string.lbl_sr) + " " +  mCommonMethod.getTwodigitValue(Float.parseFloat(mStringTransFees)));
            }else
            {
                mRelativeLayoutTransFees.setVisibility(View.GONE);
            }
        }

        mLinearLayoutRow.removeAllViews();
        Iterator myVeryOwnIterator = mActivity.getMyApplication().getBookedAppoinmentData().keySet().iterator();

        mIntTotalPrice = 0;

        while (myVeryOwnIterator.hasNext()) {
            String key = (String) myVeryOwnIterator.next();
            BookAppointmentParser value = (BookAppointmentParser) mActivity.getMyApplication().getBookedAppoinmentData().get(key);
           // Toast.makeText(mActivity, value.getService_name() + " : (" + value.getCategory_name() + ") Price : " + value.getTotal_price(), Toast.LENGTH_LONG).show();

            View convertView = mActivity.getLayoutInflater().inflate(R.layout.row_book_appointment_services, null);

            TextView mTextViewName = (TextView) convertView.findViewById(R.id.row_book_appointment_service_name);
            TextView mTextViewPrice = (TextView) convertView.findViewById(R.id.row_book_appointment_service_price);
            TextView mTextViewCateName = (TextView) convertView.findViewById(R.id.row_book_appointment_category_name);

            mTextViewName.setText(value.getService_name());
            mTextViewPrice.setText(getString(R.string.lbl_sr) + " " + mCommonMethod.getTwodigitValue(Float.parseFloat(value.getTotal_price())));
            mTextViewCateName.setText(value.getCategory_name() + " (" + value.getQuantity() + ")");
            mLinearLayoutRow.addView(convertView);

            mIntTotalPrice = Float.parseFloat(value.getTotal_price()) + mIntTotalPrice;

        }

        if(mIntMinAppPrice > mIntTotalPrice)
            mIntTotalPrice = mIntMinAppPrice;

        mTextViewTotalPrice.setText(getString(R.string.lbl_sr) + " " +  mCommonMethod.getTwodigitValue(mIntTotalPrice));

        setWalletAmount();

    }


    /**
     * Method call will get IDs from xml file.
     *
     * @param v
     */
    private void getWidgetRefrence(View v) {

        mImageViewProviderPic = (ImageView) v.findViewById(R.id.fragment_deposit_imageview_userpic);
        mTextViewProviderName = (TextView) v.findViewById(R.id.fragment_deposit_textview_name);
        mTextViewLocation = (TextView) v.findViewById(R.id.fragment_deposit_textview_place);
        mTextViewDate = (TextView) v.findViewById(R.id.fragment_deposit_textview_time);
        mTextViewTotalPrice = (TextView) v.findViewById(R.id.fragment_deposit_textview_total);
        mTextViewTotalConfirmDeposit = (TextView) v.findViewById(R.id.fragment_deposit_textview_total_confirm_deposit);
        mTextViewCOD = (TextView) v.findViewById(R.id.fragment_deposit_textview_total_cod);
        mTextViewSADAD = (TextView) v.findViewById(R.id.fragment_deposit_textview_sadad);
        mTextViewCreditCard = (TextView) v.findViewById(R.id.fragment_deposit_textview_credit_card);
        mTextViewPaypal = (TextView) v.findViewById(R.id.fragment_deposit_textview_paypal);
        mTextViewPayDeposit = (TextView) v.findViewById(R.id.fragment_deposit_textview_pay_deposit);
        mTextViewCurrentBalance = (TextView) v.findViewById(R.id.fragment_deposit_textview_current_balance);
        mTextViewUseWalletBalance = (TextView) v.findViewById(R.id.fragment_deposit_textview_wallet_amount);
        mTextViewRemainBalance = (TextView) v.findViewById(R.id.fragment_deposit_textview_current_remain_balance);
        mTextViewTransFees = (TextView) v.findViewById(R.id.fragment_deposit_textview_trans_fee);
        mRelativeLayoutTransFees = (RelativeLayout) v.findViewById(R.id.fragment_deposit_relative_trans_fees);

        mCheckBoxUseWallet = (CheckBox) v.findViewById(R.id.fragment_deposit_checkbox_wallet);
        mLinearLayoutRow = (LinearLayout) v.findViewById(R.id.fragment_deposit_row);
        mLinearLayoutPaymentOption = (LinearLayout) v.findViewById(R.id.fragment_deposit_linear_payment_option);

    }

    /**
     * Method call will Register OnClick() Events for widgets.
     */
    private void registerOnClick() {

        mTextViewSADAD.setOnClickListener(this);
        mTextViewCreditCard.setOnClickListener(this);
        mTextViewPaypal.setOnClickListener(this);
        mTextViewPayDeposit.setOnClickListener(this);
        mCheckBoxUseWallet.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                setWalletAmount();
            }
        });
    }

    /**
     * Method call OnClick Event fire.
     */
    @Override
    public void onClick(View v) {

        if (v == mTextViewSADAD) {
            mStringPaymentType = "sadad";
            setPayMethod();
            mTextViewSADAD.setCompoundDrawablesWithIntrinsicBounds(ContextCompat.getDrawable(mActivity, R.drawable.icon_radio_btn_selected), null, null, null);

        } else if (v == mTextViewCreditCard) {
            mStringPaymentType = "creditcard";
            setPayMethod();
            mTextViewCreditCard.setCompoundDrawablesWithIntrinsicBounds(ContextCompat.getDrawable(mActivity, R.drawable.icon_radio_btn_selected), null, null, null);

        } else if (v == mTextViewPaypal) {
            mStringPaymentType = "paypal";
            setPayMethod();
            mTextViewPaypal.setCompoundDrawablesWithIntrinsicBounds(ContextCompat.getDrawable(mActivity, R.drawable.icon_radio_btn_selected), null, null, null);

        } else if (v == mTextViewPayDeposit) {

//            mActivity.getAppAlertDialog().showAlertWithSingleButton("", "Payment will be coming soon..",
//                    mActivity.getString(R.string.lbl_ok),
//                    new DialogInterface.OnClickListener() {
//                        public void onClick(DialogInterface dialog, int id) {
//                            mActivity.removePreviousFragment();
//                            mActivity.replaceFragment(new HomeFragment(), false);
//                            mCommonMethod.setNotificationForAppointment(mStringAppointmentID, mStringDateTime, mStringProviderName);
//                        }
//                    });

            if(mLinearLayoutPaymentOption.getVisibility()==View.GONE)
                mStringPaymentType = "redeem";

            if(mStringPaymentType.length() > 0) {

                AppointmentDepositFragment mAppointmentDepositFragment = new AppointmentDepositFragment();
                Bundle mBundle = new Bundle();
                mBundle.putString(getString(R.string.bundle_appointment_id), mStringAppointmentID);
                mBundle.putString(getString(R.string.bundle_time), mStringDateTime);
                mBundle.putString(getString(R.string.bundle_provider_name), mStringProviderName);
                mBundle.putString(getString(R.string.bundle_pay_amount), String.valueOf(mIntRemainBalance));
                mBundle.putString(getString(R.string.bundle_reedem_amount), String.valueOf(mIntMyWalletUse));
                mBundle.putString(getString(R.string.bundle_pay_type), mStringPaymentType);
                mAppointmentDepositFragment.setArguments(mBundle);
                mActivity.replaceFragment(mAppointmentDepositFragment, true);
            }else
            {
                mActivity.getAppAlertDialog().showDialog("", getString(R.string.validation_select_payment_option), false);
            }


        }

    }

    public void setWalletAmount()
    {
//        mIntDepositePrice = mIntTotalPrice;
//        mIntMyWallet = Float.parseFloat(mActivity.getMyApplication().getUserProfile().getData().getTotalcredit());

        if(mIntMyWallet > 0)
            mCheckBoxUseWallet.setEnabled(true);
        else
            mCheckBoxUseWallet.setEnabled(false);

        mTextViewCurrentBalance.setText(getString(R.string.lbl_your_current_balance, mCommonMethod.getTwodigitValue(mIntMyWallet)));
        mTextViewTotalConfirmDeposit.setText(getString(R.string.lbl_sr) + " " + mCommonMethod.getTwodigitValue(mIntDepositePrice));

        mIntCOD = mIntTotalPrice - mIntDepositePrice;
        mTextViewCOD.setText(getString(R.string.lbl_sr) + " " + String.valueOf(mCommonMethod.getTwodigitValue(mIntCOD)));

        if(mCheckBoxUseWallet.isChecked())
        {
            if(mIntMyWallet >= mIntDepositePrice)
            {
                mIntMyWalletUse = mIntDepositePrice;
                mLinearLayoutPaymentOption.setVisibility(View.GONE);
            }else
            {
                mIntMyWalletUse = mIntMyWallet;
                mLinearLayoutPaymentOption.setVisibility(View.VISIBLE);
            }
            mIntRemainBalance = mIntDepositePrice - mIntMyWalletUse;
            mTextViewRemainBalance.setText(getString(R.string.lbl_sr)+" "+ mCommonMethod.getTwodigitValue(mIntRemainBalance));
            mTextViewUseWalletBalance.setText(getString(R.string.lbl_sr)+" "+mCommonMethod.getTwodigitValue(mIntMyWalletUse));
        }else
        {
            mLinearLayoutPaymentOption.setVisibility(View.VISIBLE);
            mIntMyWalletUse = 0;
            mIntRemainBalance = mIntDepositePrice;
            mTextViewRemainBalance.setText(getString(R.string.lbl_sr)+" "+mCommonMethod.getTwodigitValue(mIntRemainBalance));
            mTextViewUseWalletBalance.setText(getString(R.string.lbl_sr)+" "+mCommonMethod.getTwodigitValue(mIntMyWalletUse));
        }
    }


    /**
     * Method call will set pay method selection..
     */
    private void setPayMethod() {
        mTextViewSADAD.setCompoundDrawablesWithIntrinsicBounds(ContextCompat.getDrawable(mActivity, R.drawable.icon_radio_btn_unselect), null, null, null);
        mTextViewCreditCard.setCompoundDrawablesWithIntrinsicBounds(ContextCompat.getDrawable(mActivity, R.drawable.icon_radio_btn_unselect), null, null, null);
        mTextViewPaypal.setCompoundDrawablesWithIntrinsicBounds(ContextCompat.getDrawable(mActivity, R.drawable.icon_radio_btn_unselect), null, null, null);
    }

    /**
     * AsyncTask for calling webservice in background.
     *
     * @author ebaraiya
     */
    public class BackProcessDeposit extends AsyncTask<String, Void, String> {
        String mCurrentMethod = "";

        @Override
        protected void onPreExecute() {
            mProgressDialog = ProgressDialog.show(mActivity, "", getString(R.string.dialog_loading), true);
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {
            mCurrentMethod = params[0];
            if (mCurrentMethod.equalsIgnoreCase(mMethodGetDepositDetail)) {

                mDepositStatusParser = (DepositStatusParser)mActivity.getWebMethod().callGetPaymentInfoForAppointment(mActivity.getMyApplication().getUserFiled(getString(R.string.sp_user_id)),
                        mActivity.getMyApplication().getUserFiled(getString(R.string.sp_user_hash)),
                        mStringAppointmentID,
                        String.valueOf(mIntTotalPrice),
                        mDepositStatusParser);

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
                    if (mCurrentMethod.equalsIgnoreCase(mMethodGetDepositDetail)) {
                        if (mDepositStatusParser.getWs_status().equalsIgnoreCase("true")) {

                            if(mDepositStatusParser.getPaymentstatus().equalsIgnoreCase("true"))
                            {
                                mIntDepositePrice = Float.parseFloat(mDepositStatusParser.getData().getDepositamt());
                                mIntMyWallet = Float.parseFloat(mDepositStatusParser.getData().getTotalcredit());
                                setWalletAmount();

                                mTextViewPaypal.setVisibility(mDepositStatusParser.getData().getPaypal().equalsIgnoreCase("No") ? View.GONE : View.VISIBLE);
                                mTextViewSADAD.setVisibility(mDepositStatusParser.getData().getSadad().equalsIgnoreCase("No") ? View.GONE : View.VISIBLE);
                                mTextViewCreditCard.setVisibility(mDepositStatusParser.getData().getCreditcard().equalsIgnoreCase("No") ? View.GONE :  View.VISIBLE);

                            }else
                            {
                                mCommonMethod.setNotificationForAppointment(mStringAppointmentID, mStringDateTime, mStringProviderName);
                                ThankYouFragment mThankYouFragment = new ThankYouFragment();
                                Bundle mBundle = new Bundle();
                                mBundle.putString(getString(R.string.bundle_url), mDepositStatusParser.getUrl());
                                mThankYouFragment.setArguments(mBundle);
                                mActivity.replaceFragment(mThankYouFragment, true);


//                                mActivity.getAppAlertDialog().showAlertWithSingleButton(getString(R.string.app_name), mDepositStatusParser.getMessage()
//                                        , getString(R.string.lbl_ok), new DialogInterface.OnClickListener() {
//                                    public void onClick(DialogInterface dialog, int id) {
//                                        mActivity.removePreviousFragment();
//                                        mActivity.replaceFragment(new HomeFragment(), false);
//                                        mCommonMethod.setNotificationForAppointment(mStringAppointmentID, mStringDateTime, mStringProviderName);
//                                    }
//                                });
                            }

                        }else {

                            if (mDepositStatusParser.getMessage().toString().contains(getString(R.string.alt_msg_exipred))) {
                                mActivity.getAppAlertDialog().showAlertWithSingleButton("", mDepositStatusParser.getMessage().toString(),
                                        mActivity.getString(R.string.lbl_logout),
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                mActivity.logout();
                                            }
                                        });
                            } else {
                                mActivity.getAppAlertDialog().showDialog(getString(R.string.app_name), mDepositStatusParser.getMessage(), false);
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
