package beautician.beauty.android.fragments;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import beautician.beauty.android.views.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

import beautician.beauty.android.R;
import beautician.beauty.android.activities.MyFragmentActivity;
import beautician.beauty.android.parsers.CategoryDataParser;
import beautician.beauty.android.parsers.DepositStatusParser;
import beautician.beauty.android.parsers.LoginParser;
import beautician.beauty.android.utilities.CommonMethod;
import beautician.beauty.android.utilities.CustomDatePickerFragment;
import beautician.beauty.android.utilities.StaticData;

@SuppressLint("InflateParams")
public class BuyFeatureAdsFragment extends Fragment implements OnClickListener {

    private MyFragmentActivity mActivity;
    private CommonMethod mCommonMethod;
    private View rootView;

    private BackProcessPay mBackProcessPay;
    private ProgressDialog mProgressDialog;
    private DepositStatusParser mBuyFeatureAddParser;
    private String mMethodPay = "Pay";
    private String mMethodBuyFeature = "BuyFeature";

    private TextView mDialogSpinnerCategory;
    private TextView mTextViewCategoryPage;
    private TextView mTextViewSearchResult;
    private TextView mTextViewSuggestedAds;
    private TextView mTextViewStartDate;
    private TextView mTextViewEndDate;
    private TextView mTextViewSave;
    private TextView mTextViewStartDateValue;
    private TextView mTextViewEndDateValue;
    private TextView mTextViewPrice;
    private TextView mTextViewSADAD;
    private TextView mTextViewCreditCard;
    private TextView mTextViewPaypal;
    private TextView mTextViewPay;
    private TextView mTextViewCurrentBalance;
    private TextView mTextViewUseWalletBalance;
    private TextView mTextViewRemainBalance;
    private CheckBox mCheckBoxUseWallet;

    private LinearLayout mLinearLayoutPaymentOption;
    private LinearLayout mLinearLayoutPayment;

    private String mStringStartDate = "";
    private String mStringEndDate = "";
    private String mStringType = "";
    private String mStringPaymentType = "";
    private String mStringAdsID = "";
    private float mIntDepositePrice = 0;
    private float mIntMyWallet = 0;
    private float mIntMyWalletUse = 0;
    private float mIntRemainBalance = 0;

    private String mStringCategoryID = "";
    private boolean[] selected;
    CategoryAdapter mCategoryAdapter;

    public BuyFeatureAdsFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_buy_feature_ads, container, false);

        mActivity = (MyFragmentActivity) getActivity();
        mActivity.setHeaderTitle(R.string.lbl_buy_feature_ads);
        mActivity.setSearchIcon(R.drawable.icon_search);
        mActivity.onBackButton(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mActivity.replaceFragment(new SearchFragment(), true);
            }
        });

        mCommonMethod = new CommonMethod(mActivity);
        mBuyFeatureAddParser = new DepositStatusParser();



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

        mDialogSpinnerCategory = (TextView) v.findViewById(R.id.fragment_buy_ads_spinner_category);
        mTextViewCategoryPage = (TextView) v.findViewById(R.id.fragment_buy_ads_textview_category_page);
        mTextViewSearchResult = (TextView) v.findViewById(R.id.fragment_buy_ads_textview_search_result);
        mTextViewSuggestedAds = (TextView) v.findViewById(R.id.fragment_buy_ads_textview_suggested_ads);
        mTextViewStartDate = (TextView) v.findViewById(R.id.fragment_buy_ads_textview_start_date);
        mTextViewEndDate = (TextView) v.findViewById(R.id.fragment_buy_ads_textview_end_date);
        mTextViewSave = (TextView) v.findViewById(R.id.fragment_buy_ads_textview_save);
        mTextViewStartDateValue = (TextView) v.findViewById(R.id.fragment_buy_ads_textview_start_date_value);
        mTextViewEndDateValue = (TextView) v.findViewById(R.id.fragment_buy_ads_textview_end_date_value);
        mTextViewPrice = (TextView) v.findViewById(R.id.fragment_buy_ads_textview_price);
        mTextViewSADAD = (TextView) v.findViewById(R.id.fragment_buy_ads_textview_sadad);
        mTextViewCreditCard = (TextView) v.findViewById(R.id.fragment_buy_ads_textview_credit_card);
        mTextViewPaypal = (TextView) v.findViewById(R.id.fragment_buy_ads_textview_paypal);
        mTextViewPay = (TextView) v.findViewById(R.id.fragment_buy_ads_textview_pay);

        mTextViewCurrentBalance = (TextView) v.findViewById(R.id.fragment_buy_ads_textview_current_balance);
        mTextViewUseWalletBalance = (TextView) v.findViewById(R.id.fragment_buy_ads_textview_wallet_amount);
        mTextViewRemainBalance = (TextView) v.findViewById(R.id.fragment_buy_ads_textview_current_remain_balance);
        mCheckBoxUseWallet = (CheckBox) v.findViewById(R.id.fragment_buy_ads_checkbox_wallet);
        mLinearLayoutPaymentOption = (LinearLayout)v.findViewById(R.id.fragment_buy_ads_linear_payment_option);
        mLinearLayoutPayment = (LinearLayout)v.findViewById(R.id.fragment_buy_ads_linear_payment);

        mStringType = StaticData.BUY_FEATURE_ADS_TYPE_CATEGORYPAGE;
        mTextViewCategoryPage.setCompoundDrawablesWithIntrinsicBounds(ContextCompat.getDrawable(mActivity, R.drawable.icon_radio_btn_selected), null, null, null);
    }

    /**
     * Method call will Register OnClick() Events for widgets.
     */
    private void registerOnClick() {

        mTextViewCategoryPage.setOnClickListener(this);
        mTextViewSearchResult.setOnClickListener(this);
        mTextViewSuggestedAds.setOnClickListener(this);
        mTextViewStartDate.setOnClickListener(this);
        mTextViewEndDate.setOnClickListener(this);
        mTextViewSave.setOnClickListener(this);
        mTextViewSADAD.setOnClickListener(this);
        mTextViewCreditCard.setOnClickListener(this);
        mTextViewPaypal.setOnClickListener(this);
        mTextViewPay.setOnClickListener(this);
        mCheckBoxUseWallet.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                setWalletAmount();
            }
        });
        mDialogSpinnerCategory.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {


                if (mActivity.getMyApplication().getUserProfile().getData().getCategories() != null) {
                    setCategoryData();
                } else {
                    mActivity.getAppAlertDialog().showDialog("", getString(R.string.validation_add_service), false);
                }
            }
        });
    }

    /**
     * Method call OnClick Event fire.
     */
    @Override
    public void onClick(View v) {

        if (v == mTextViewCategoryPage) {

            setType();
            mStringType = StaticData.BUY_FEATURE_ADS_TYPE_CATEGORYPAGE;
            mTextViewCategoryPage.setCompoundDrawablesWithIntrinsicBounds(ContextCompat.getDrawable(mActivity, R.drawable.icon_radio_btn_selected), null, null, null);
            mDialogSpinnerCategory.setVisibility(View.VISIBLE);

        } else if (v == mTextViewSearchResult) {

            setType();
            mStringType = StaticData.BUY_FEATURE_ADS_TYPE_SEARCH_RESULT;
            mTextViewSearchResult.setCompoundDrawablesWithIntrinsicBounds(ContextCompat.getDrawable(mActivity, R.drawable.icon_radio_btn_selected), null, null, null);
            mDialogSpinnerCategory.setVisibility(View.GONE);

        } else if (v == mTextViewSuggestedAds) {

            setType();
            mStringType = StaticData.BUY_FEATURE_ADS_TYPE_SUGGESTEDADS;
            mTextViewSuggestedAds.setCompoundDrawablesWithIntrinsicBounds(ContextCompat.getDrawable(mActivity, R.drawable.icon_radio_btn_selected), null, null, null);
            mDialogSpinnerCategory.setVisibility(View.GONE);

        } else if (v == mTextViewStartDate) {

            new CustomDatePickerFragment(mTextViewStartDate, mActivity);


        } else if (v == mTextViewEndDate) {

            new CustomDatePickerFragment(mTextViewEndDate, mActivity);


        } else if (v == mTextViewSave) {

            callBuyAds();

        } else if (v == mTextViewSADAD) {

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

        } else if (v == mTextViewPay) {

            if(mLinearLayoutPaymentOption.getVisibility()==View.GONE)
                mStringPaymentType = "redeem";

            if(mStringPaymentType.length() > 0) {

                BuyAdsDepositFragment mBuyAdsDepositFragment = new BuyAdsDepositFragment();
                Bundle mBundle = new Bundle();
                mBundle.putString(getString(R.string.bundle_ads_id), mStringAdsID);
                mBundle.putString(getString(R.string.bundle_pay_amount), String.valueOf(mIntRemainBalance));
                mBundle.putString(getString(R.string.bundle_reedem_amount), String.valueOf(mIntMyWalletUse));
                mBundle.putString(getString(R.string.bundle_pay_type), mStringPaymentType);
                mBuyAdsDepositFragment.setArguments(mBundle);
                mActivity.replaceFragment(mBuyAdsDepositFragment, true);
            }
            else
            {
                mActivity.getAppAlertDialog().showDialog("", getString(R.string.validation_select_payment_option), false);
            }
        }
    }

    /**
     * Method will display set category data
     */
    public void setCategoryData() {

        selected = new boolean[mActivity.getMyApplication().getCategoryListParser().getData().size()];
        for (int i = 0; i < selected.length; i++)
            selected[i] = false;

        /*ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(),
                android.R.layout.simple_spinner_item, new String[]{allText});
        setAdapter(adapter);*/

        mCategoryAdapter = new CategoryAdapter(getContext(),android.R.layout.simple_spinner_item,
                mActivity.getMyApplication().getUserProfile().getData().getCategories());

        final AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(mActivity, android.R.style.Theme_DeviceDefault_Light_Dialog));
//        final AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
        builder.setAdapter(mCategoryAdapter, null);
        builder.setPositiveButton(getString(R.string.lbl_done), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                setSelectedCategory();
                dialog.cancel();
            }
        });
//        builder.setOnCancelListener(mActivity);

        AlertDialog alertDialog = builder.create();

        ListView listView = alertDialog.getListView();
        listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener(){

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                CategoryDataParser producto = (CategoryDataParser) parent.getItemAtPosition(position);

                if (producto.isChecked())
                    producto.setChecked(false);
                else
                    producto.setChecked(true);


                mCategoryAdapter.notifyDataSetChanged();

            }
        });

        //alertDialog
        alertDialog.show();
    }

    public void setSelectedCategory()
    {
        List<CategoryDataParser> productos = mCategoryAdapter.getProductos();
        StringBuilder mStringBuilder = new StringBuilder();
        mStringCategoryID = "";
        for (CategoryDataParser producto : productos){
            if (producto.isChecked()){
                if(mStringBuilder.length() > 0) {
                    mStringBuilder.append(", ");
                }
                if(mActivity.getMyApplication().getCurrentLang().equalsIgnoreCase("en"))
                    mStringBuilder.append(producto.getCategory_name());
                else
                    mStringBuilder.append(producto.getCategory_namearebic());

                mStringCategoryID = mStringCategoryID.length()>0 ? mStringCategoryID+","+producto.getCategory_id() :producto.getCategory_id();
            }
        }
        if(mStringBuilder.length() > 0) {
            mDialogSpinnerCategory.setText(mStringBuilder);
            mDialogSpinnerCategory.setTextColor(ContextCompat.getColor(mActivity, R.color.myPrimaryColor));
        }
        else {
            mDialogSpinnerCategory.setText(R.string.hint_service_category);
            mDialogSpinnerCategory.setTextColor(ContextCompat.getColor(mActivity, R.color.dialog_bg));
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

        mTextViewCurrentBalance.setText(getString(R.string.lbl_your_current_balance, mIntMyWallet));
        mTextViewPrice.setText("SR "+String.valueOf(mIntDepositePrice));

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
            mTextViewRemainBalance.setText("SR "+String.valueOf(mIntRemainBalance));
            mTextViewUseWalletBalance.setText("SR " + String.valueOf(mIntMyWalletUse));
        }else
        {
            mLinearLayoutPaymentOption.setVisibility(View.VISIBLE);
            mIntMyWalletUse = 0;
            mIntRemainBalance = mIntDepositePrice;
            mTextViewRemainBalance.setText("SR "+String.valueOf(mIntRemainBalance));
            mTextViewUseWalletBalance.setText("SR "+String.valueOf(mIntMyWalletUse));
        }
    }

    /**
     * Method call will buy feature ads..
     */
    private void callBuyAds() {


        if(mStringType.equalsIgnoreCase(StaticData.BUY_FEATURE_ADS_TYPE_CATEGORYPAGE) && mStringCategoryID.length() == 0)
        {
            mActivity.getAppAlertDialog().showDialog("", getString(R.string.validation_service_category), false);
            return;
        }

        mStringStartDate = mTextViewStartDate.getText().toString().trim();
        mStringEndDate = mTextViewEndDate.getText().toString().trim();


        if (mActivity.getAppAlertDialog().validateSelectkField(mStringStartDate, mActivity, getString(R.string.lbl_start_date), getString(R.string.validation_select_start_date))
                && mActivity.getAppAlertDialog().validateSelectkField(mStringEndDate, mActivity, getString(R.string.lbl_end_date), getString(R.string.validation_select_end_date))) {

            mStringStartDate = mCommonMethod.getDateInFormate(mStringStartDate, StaticData.DATE_FORMAT_1, StaticData.DATE_FORMAT_10);
            mStringEndDate = mCommonMethod.getDateInFormate(mStringEndDate,StaticData.DATE_FORMAT_1,StaticData.DATE_FORMAT_10);

            mBackProcessPay = new BackProcessPay();
            mBackProcessPay.execute(mMethodBuyFeature);
        }

    }

    /**
     * Method call will set Ads type
     */
    private void setType() {

        mTextViewCategoryPage.setCompoundDrawablesWithIntrinsicBounds(ContextCompat.getDrawable(mActivity, R.drawable.icon_radio_btn_unselect), null, null, null);
        mTextViewSearchResult.setCompoundDrawablesWithIntrinsicBounds(ContextCompat.getDrawable(mActivity, R.drawable.icon_radio_btn_unselect), null, null, null);
        mTextViewSuggestedAds.setCompoundDrawablesWithIntrinsicBounds(ContextCompat.getDrawable(mActivity, R.drawable.icon_radio_btn_unselect), null, null, null);

    }


    /**
     * Method call will set pay method selection..
     */
    private void setPayMethod() {
        mTextViewSADAD.setCompoundDrawablesWithIntrinsicBounds(ContextCompat.getDrawable(mActivity, R.drawable.icon_radio_btn_unselect), null, null, null);
        mTextViewCreditCard.setCompoundDrawablesWithIntrinsicBounds(ContextCompat.getDrawable(mActivity, R.drawable.icon_radio_btn_unselect), null, null, null);
        mTextViewPaypal.setCompoundDrawablesWithIntrinsicBounds(ContextCompat.getDrawable(mActivity, R.drawable.icon_radio_btn_unselect), null, null, null);
    }

    public class CategoryAdapter extends ArrayAdapter<CategoryDataParser> {

        Context context;
        List<CategoryDataParser> productos;
        LayoutInflater inflater;
        CategoryDataParser modelProducto;

        private class ViewHolder{
            TextView txtPresentacion;
            CheckBox checkBox;

        }
        public CategoryAdapter(Context context, int resource, List<CategoryDataParser> objects) {
            super(context, resource, objects);

            this.context = context;
            this.productos = objects;
            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            View row = convertView;
            ViewHolder viewHolder;
            if (row == null){
                row = inflater.inflate(R.layout.row_mutiple_spinner, parent, false);
                viewHolder = new ViewHolder();
                viewHolder.txtPresentacion = (TextView) row.findViewById(R.id.txt_presenta_producto);
                viewHolder.checkBox = (CheckBox) row.findViewById(R.id.checkBoxPro);
                row.setTag(viewHolder);


            }else{
                viewHolder = (ViewHolder) row.getTag();
            }

            modelProducto = productos.get(position);

            if(mActivity.getMyApplication().getCurrentLang().equalsIgnoreCase("en"))
                viewHolder.txtPresentacion.setText(modelProducto.getCategory_name());
            else
                viewHolder.txtPresentacion.setText(modelProducto.getCategory_namearebic());
            viewHolder.checkBox.setChecked(modelProducto.isChecked());



            return row;

        }

        public List<CategoryDataParser> getProductos(){
            return productos;
        }
    }

    /**
     * AsyncTask for calling webservice in background.
     * @author ebaraiya
     */
    public class BackProcessPay extends AsyncTask<String, Void, String> {
        String mCurrentMethod = "";

        @Override
        protected void onPreExecute() {
            mProgressDialog = ProgressDialog.show(mActivity, "", getString(R.string.dialog_loading), true);
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {
            mCurrentMethod = params[0];
            if (mCurrentMethod.equalsIgnoreCase(mMethodBuyFeature)) {

                mBuyFeatureAddParser = (DepositStatusParser) mActivity.getWebMethod().callBuyFeatureAds(mActivity.getMyApplication().getUserFiled(getString(R.string.sp_user_id)),
                        mActivity.getMyApplication().getUserFiled(getString(R.string.sp_user_hash)),
                        mStringCategoryID,
                        mStringType,
                        mStringStartDate,
                        mStringEndDate,
                        mBuyFeatureAddParser);

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
                    if (mCurrentMethod.equalsIgnoreCase(mMethodBuyFeature)) {

                        if (mBuyFeatureAddParser.getWs_status().equalsIgnoreCase("true")) {

//                            mActivity.getAppAlertDialog().showDialog(getString(R.string.app_name),mBuyFeatureAddParser.getMessage(),false);
                            mTextViewStartDateValue.setText(mCommonMethod.getDateInFormate(mStringStartDate, StaticData.DATE_FORMAT_10, StaticData.DATE_FORMAT_1));
                            mTextViewEndDateValue.setText(mCommonMethod.getDateInFormate(mStringEndDate, StaticData.DATE_FORMAT_10, StaticData.DATE_FORMAT_1));

                            if(mBuyFeatureAddParser.getPaymentstatus().equalsIgnoreCase("true"))
                            {
                                mStringAdsID = mBuyFeatureAddParser.getData().getFeatureads_id();
                                mIntDepositePrice = Float.parseFloat(mBuyFeatureAddParser.getData().getDepositamt());
                                mIntMyWallet = Float.parseFloat(mBuyFeatureAddParser.getData().getTotalcredit());
                                setWalletAmount();

                                mTextViewPaypal.setVisibility(mBuyFeatureAddParser.getData().getPaypal().equalsIgnoreCase("No") ? View.GONE : View.VISIBLE);
                                mTextViewSADAD.setVisibility(mBuyFeatureAddParser.getData().getSadad().equalsIgnoreCase("No") ? View.GONE : View.VISIBLE);
                                mTextViewCreditCard.setVisibility(mBuyFeatureAddParser.getData().getCreditcard().equalsIgnoreCase("No") ? View.GONE :  View.VISIBLE);
                                mLinearLayoutPayment.setVisibility(View.VISIBLE);

                            }else
                            {
//                                mActivity.getAppAlertDialog().showAlertWithSingleButton(getString(R.string.app_name), mBuyFeatureAddParser.getMessage()
//                                        , getString(R.string.lbl_ok), new DialogInterface.OnClickListener() {
//                                    public void onClick(DialogInterface dialog, int id) {
//                                        mActivity.removePreviousFragment();
//                                        mActivity.replaceFragment(new HomeFragment(), false);
//                                    }
//                                });

                                ThankYouFragment mThankYouFragment = new ThankYouFragment();
                                Bundle mBundle = new Bundle();
                                mBundle.putString(getString(R.string.bundle_url), mBuyFeatureAddParser.getUrl());
                                mThankYouFragment.setArguments(mBundle);
                                mActivity.replaceFragment(mThankYouFragment, true);

                            }


                        } else {
                            if (mBuyFeatureAddParser.getMessage().toString().contains(getString(R.string.alt_msg_exipred))) {
                                mActivity.getAppAlertDialog().showAlertWithSingleButton("", mBuyFeatureAddParser.getMessage().toString(),
                                        mActivity.getString(R.string.lbl_logout),
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                mActivity.logout();
                                            }
                                        });
                            }else{

                                mActivity.getAppAlertDialog().showDialog(getString(R.string.app_name),mBuyFeatureAddParser.getMessage(), false);
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
