package beautician.beauty.android.fragments;

import android.annotation.SuppressLint;
import beautician.beauty.android.views.ProgressDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import beautician.beauty.android.R;
import beautician.beauty.android.activities.MyFragmentActivity;
import beautician.beauty.android.helper.GridCellAdapter;
import beautician.beauty.android.parsers.AppointmentDataParser;
import beautician.beauty.android.parsers.AppointmentListParser;
import beautician.beauty.android.parsers.LoginParser;
import beautician.beauty.android.utilities.CommonMethod;
import beautician.beauty.android.utilities.StaticData;
import beautician.beauty.android.views.calendarview.CalendarListener;
import beautician.beauty.android.views.calendarview.CustomCalendarView;


@SuppressLint("InflateParams")
@SuppressWarnings("unused")
public class MyCalendarFragment extends Fragment {

    private MyFragmentActivity mActivity;
    private CommonMethod mCommonMethod;
    private AppointmentAdapter mAppointmentAdapter;

    private ImageView mImageViewNext;
    private ImageView mImageViewPrevious;

    private TextView mTextViewTitle;

    private GridView mGridViewCalendar;
    private ListView mListViewAppointment;

    public Calendar mCalendarMonth;
    public Handler mHandler;
    public ArrayList<String> mListEvent;

    private GridCellAdapter mGridCellAdapter;

    private Date mDateSelected;
    private Date mDateMonthSelected;
    private static final String dateTemplate = "MMMM yyyy";

    private String mStringUserName = "";

    CustomCalendarView calendarView;
    private ProgressDialog mProgressDialog;
    private AppointmentListParser mAppointmentListParser;
    private String mStringCurrentMonthYear = "";
    private BackProcessMyAppointment mBackProcessMyAppointment;
    private List<AppointmentDataParser> mListAppointmentDataParsers;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    public MyCalendarFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_calendar, container, false);

        mActivity = (MyFragmentActivity) getActivity();
        mActivity.setHeaderTitle(R.string.lbl_my_calender);
        mActivity.onBackButton(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mActivity.replaceFragment(new SearchFragment(), true);
            }
        });
        mCommonMethod = new CommonMethod(mActivity);
        mCalendarMonth = Calendar.getInstance();

        mSwipeRefreshLayout = (SwipeRefreshLayout)rootView.findViewById(R.id.fragment_calendar_swiperefresh);

        mListViewAppointment = (ListView) rootView.findViewById(R.id.fragment_calendar_listview);

        mListViewAppointment.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                AppointmentDetailsFragment mAppointmentDetailsFragment = new AppointmentDetailsFragment();
                Bundle mBundle = new Bundle();
                mBundle.putParcelable(getString(R.string.bundle_appointment_data), mListAppointmentDataParsers.get(position));
                mBundle.putString(getString(R.string.bundle_from), getString(R.string.bundle_from_list));
                mAppointmentDetailsFragment.setArguments(mBundle);
                mActivity.replaceFragment(mAppointmentDetailsFragment, true);
            }
        });

        mListViewAppointment.setOnScrollListener(new AbsListView.OnScrollListener() {
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
                mBackProcessMyAppointment = new BackProcessMyAppointment();
                mBackProcessMyAppointment.execute("");
            }
        });


        //CUSTOM CALENDAR..
        calendarView = (CustomCalendarView) rootView.findViewById(R.id.calendar_view);
        Calendar currentCalendar = Calendar.getInstance(Locale.getDefault());
        if(mDateMonthSelected!=null)
            currentCalendar.setTime(mDateMonthSelected);
        calendarView.setFirstDayOfWeek(Calendar.SUNDAY);
        calendarView.setShowOverflowDate(true);

        calendarView.setCalendarListener(new CalendarListener() {
            @Override
            public void onDateSelected(Date date) {
                mDateSelected = date;
                filterAppointment(date);
            }

            @Override
            public void onMonthChanged(Date date) {
                mDateMonthSelected = date;
                mStringCurrentMonthYear = mCommonMethod.getDateInFormateFromDate(date, StaticData.DATE_FORMAT_7);
                mListAppointmentDataParsers = new ArrayList<AppointmentDataParser>();
                refreshAppointmentData();
                mBackProcessMyAppointment = new BackProcessMyAppointment();
                mBackProcessMyAppointment.execute("");
            }
        });


        calendarView.refreshCalendar(currentCalendar);
        mStringCurrentMonthYear = mCommonMethod.getFormateFromCalendar(calendarView.getCurrentCalendar(), StaticData.DATE_FORMAT_7);

        if(mListAppointmentDataParsers==null) {
            mAppointmentListParser = new AppointmentListParser();
            mListAppointmentDataParsers = new ArrayList<AppointmentDataParser>();
            try {
                mBackProcessMyAppointment = new BackProcessMyAppointment();
                mBackProcessMyAppointment.execute("");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }else
        {
            refreshAppointmentData();
        }

        if(mDateSelected!=null)
            calendarView.markDayAsSelectedDay(mDateSelected);

        return rootView;
    }


    /**
     * Method will refresh appointment data
     */
    public void refreshAppointmentData()
    {

        mAppointmentAdapter = new AppointmentAdapter();
        mListViewAppointment.setAdapter(mAppointmentAdapter);

        mListEvent = new ArrayList<String>();
        if(mAppointmentListParser.getData()!=null) {
            for (int i = 0; i < mAppointmentListParser.getData().size(); i++) {
                String start_time = mAppointmentListParser.getData().get(i).getAppointment_starttime();
                mListEvent.add(mCommonMethod.getDateInFormate(start_time, StaticData.DATE_FORMAT_6, StaticData.DATE_FORMAT_1));
            }
            calendarView.setAppointment(mAppointmentListParser.getData());
        }
        calendarView.setEvent(mListEvent);
        calendarView.refreshCalendar(calendarView.getCurrentCalendar());
    }

    public void filterAppointment(Date mCurrentDate)
    {
        mListAppointmentDataParsers = new ArrayList<AppointmentDataParser>();
        if(mAppointmentListParser.getData()!=null) {
            for (int i = 0; i < mAppointmentListParser.getData().size(); i++) {
                String start_time = mAppointmentListParser.getData().get(i).getAppointment_starttime();
                String mDate = mCommonMethod.getDateInFormate(start_time, StaticData.DATE_FORMAT_6, StaticData.DATE_FORMAT_1);
                if (mDate.equalsIgnoreCase(mCommonMethod.getDateInFormateFromDate(mCurrentDate, StaticData.DATE_FORMAT_1))) {
                    mListAppointmentDataParsers.add(mAppointmentListParser.getData().get(i));
                }
            }
            mAppointmentAdapter.notifyDataSetChanged();
        }
    }

    /**
     * BaseAdapter class for load data into listview
     */
    public class AppointmentAdapter extends BaseAdapter {
        ViewHolder mViewHolder;

        @Override
        public int getCount() {
            return mListAppointmentDataParsers.size();
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
                convertview = mActivity.getLayoutInflater().inflate(R.layout.row_appoinment_date, null);
                mViewHolder = new ViewHolder();
                mViewHolder.mTextViewName = (TextView) convertview.findViewById(R.id.row_appointment_textview_name);
                mViewHolder.mTextViewPlace = (TextView) convertview.findViewById(R.id.row_appointment_textview_place);
                mViewHolder.mTextViewDate = (TextView) convertview.findViewById(R.id.row_appointment_textview_date);
                mViewHolder.mTextViewTime = (TextView) convertview.findViewById(R.id.row_appointment_textview_time);

                mViewHolder.mTextViewService1 = (TextView) convertview.findViewById(R.id.row_appointment_textview_service1);
                mViewHolder.mTextViewService2 = (TextView) convertview.findViewById(R.id.row_appointment_textview_service2);
                mViewHolder.mTextViewMore = (TextView) convertview.findViewById(R.id.row_appointment_textview_service_more);

                convertview.setTag(mViewHolder);

            } else {
                mViewHolder = (ViewHolder) convertview.getTag();
            }
            if(mListAppointmentDataParsers.get(position).getUser_type().equalsIgnoreCase(getString(R.string.bundle_seeker)))
            {
                mViewHolder.mTextViewName.setText(mListAppointmentDataParsers.get(position).getProvidername());
            }else
            {
                mViewHolder.mTextViewName.setText(mListAppointmentDataParsers.get(position).getSeekername());
            }

            String mStringDate = mListAppointmentDataParsers.get(position).getAppointment_starttime();

            mViewHolder.mTextViewDate.setText(mCommonMethod.getDateInFormate(mStringDate, StaticData.DATE_FORMAT_6, StaticData.DATE_FORMAT_8));
            mViewHolder.mTextViewTime.setText(mCommonMethod.getDateInFormate(mStringDate, StaticData.DATE_FORMAT_6, StaticData.DATE_FORMAT_9));

            mViewHolder.mTextViewPlace.setText(mListAppointmentDataParsers.get(position).getAppointment_location());

            if (mListAppointmentDataParsers.get(position).getAppointment_status().equalsIgnoreCase(StaticData.APPOINTMENT_STATUS_CANCEL)) {
                mViewHolder.mTextViewDate.setBackgroundResource(R.drawable.circle_red);
            }else {
                mViewHolder.mTextViewDate.setBackgroundResource(R.drawable.circle_blue);
            }

//            mViewHolder.mTextViewName.setText(mGetProviderListParser.getServicedata().get(position).getUsername());

            if(mListAppointmentDataParsers.get(position).getAppointmentinfo()!=null) {

                if(mActivity.getMyApplication().getCurrentLang().equalsIgnoreCase("en")) {

                    if (mListAppointmentDataParsers.get(position).getAppointmentinfo().size() == 1) {

                        mViewHolder.mTextViewService1.setText(mListAppointmentDataParsers.get(position).getAppointmentinfo().get(0).getCategory_name());
                        mViewHolder.mTextViewService2.setVisibility(View.GONE);
                        mViewHolder.mTextViewMore.setVisibility(View.GONE);
                    } else if (mListAppointmentDataParsers.get(position).getAppointmentinfo().size() == 2) {

                        mViewHolder.mTextViewService1.setText(mListAppointmentDataParsers.get(position).getAppointmentinfo().get(0).getCategory_name());
                        if (mListAppointmentDataParsers.get(position).getAppointmentinfo().get(1).getCategory_name() != null) {
                            mViewHolder.mTextViewService2.setVisibility(View.VISIBLE);
                            mViewHolder.mTextViewMore.setVisibility(View.GONE);
                            mViewHolder.mTextViewService2.setText(mListAppointmentDataParsers.get(position).getAppointmentinfo().get(1).getCategory_name());
                        } else {
                            mViewHolder.mTextViewService2.setVisibility(View.GONE);
                        }


                    } else if (mListAppointmentDataParsers.get(position).getAppointmentinfo().size() > 2) {

                        mViewHolder.mTextViewService1.setText(mListAppointmentDataParsers.get(position).getAppointmentinfo().get(0).getCategory_name());
                        if (mListAppointmentDataParsers.get(position).getAppointmentinfo().get(1).getCategory_name() != null) {
                            mViewHolder.mTextViewService2.setVisibility(View.VISIBLE);
                            mViewHolder.mTextViewMore.setVisibility(View.VISIBLE);
                            mViewHolder.mTextViewService2.setText(mListAppointmentDataParsers.get(position).getAppointmentinfo().get(1).getCategory_name());
                        } else {
                            mViewHolder.mTextViewService2.setVisibility(View.GONE);
                        }


                    } else {
                        mViewHolder.mTextViewService2.setVisibility(View.GONE);
                        mViewHolder.mTextViewMore.setVisibility(View.GONE);
                    }


                    if (mListAppointmentDataParsers.get(position).getAppointmentinfo().get(0).getCategory_name() != null) {
                        mViewHolder.mTextViewService1.setText(mListAppointmentDataParsers.get(position).getAppointmentinfo().get(0).getCategory_name());
                    } else {
                        mViewHolder.mTextViewService1.setVisibility(View.GONE);
                    }
                }else {
                    if (mListAppointmentDataParsers.get(position).getAppointmentinfo().size() == 1) {

                        mViewHolder.mTextViewService1.setText(mListAppointmentDataParsers.get(position).getAppointmentinfo().get(0).getCategory_namearebic());
                        mViewHolder.mTextViewService2.setVisibility(View.GONE);
                        mViewHolder.mTextViewMore.setVisibility(View.GONE);
                    } else if (mListAppointmentDataParsers.get(position).getAppointmentinfo().size() == 2) {

                        mViewHolder.mTextViewService1.setText(mListAppointmentDataParsers.get(position).getAppointmentinfo().get(0).getCategory_namearebic());
                        if (mListAppointmentDataParsers.get(position).getAppointmentinfo().get(1).getCategory_name() != null) {
                            mViewHolder.mTextViewService2.setVisibility(View.VISIBLE);
                            mViewHolder.mTextViewMore.setVisibility(View.GONE);
                            mViewHolder.mTextViewService2.setText(mListAppointmentDataParsers.get(position).getAppointmentinfo().get(1).getCategory_namearebic());
                        } else {
                            mViewHolder.mTextViewService2.setVisibility(View.GONE);
                        }


                    } else if (mListAppointmentDataParsers.get(position).getAppointmentinfo().size() > 2) {

                        mViewHolder.mTextViewService1.setText(mListAppointmentDataParsers.get(position).getAppointmentinfo().get(0).getCategory_namearebic());
                        if (mListAppointmentDataParsers.get(position).getAppointmentinfo().get(1).getCategory_name() != null) {
                            mViewHolder.mTextViewService2.setVisibility(View.VISIBLE);
                            mViewHolder.mTextViewMore.setVisibility(View.VISIBLE);
                            mViewHolder.mTextViewService2.setText(mListAppointmentDataParsers.get(position).getAppointmentinfo().get(1).getCategory_namearebic());
                        } else {
                            mViewHolder.mTextViewService2.setVisibility(View.GONE);
                        }


                    } else {
                        mViewHolder.mTextViewService2.setVisibility(View.GONE);
                        mViewHolder.mTextViewMore.setVisibility(View.GONE);
                    }


                    if (mListAppointmentDataParsers.get(position).getAppointmentinfo().get(0).getCategory_name() != null) {
                        mViewHolder.mTextViewService1.setText(mListAppointmentDataParsers.get(position).getAppointmentinfo().get(0).getCategory_namearebic());
                    } else {
                        mViewHolder.mTextViewService1.setVisibility(View.GONE);
                    }
                }


            }
            return convertview;
        }

    }

    public class ViewHolder {
        TextView mTextViewDate;
        TextView mTextViewTime;
        TextView mTextViewName;
        TextView mTextViewPlace;
        TextView mTextViewService1;
        TextView mTextViewService2;
        TextView mTextViewMore;

    }


    /**
     * AsyncTask for calling webservice in background.
     *
     * @author npatel
     */
    public class BackProcessMyAppointment extends AsyncTask<String, Void, String> {


        @Override
        protected void onPreExecute() {
            mProgressDialog = ProgressDialog.show(mActivity, "", getString(R.string.dialog_loading), true);
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {

            mAppointmentListParser = (AppointmentListParser) mActivity.getWebMethod().callGetAppointment(mActivity.getMyApplication().getUserFiled(getString(R.string.sp_user_id)),
                    mActivity.getMyApplication().getUserFiled(getString(R.string.sp_user_hash)),
                    mStringCurrentMonthYear,
                    mAppointmentListParser);



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
                    if (mAppointmentListParser.getWs_status().equalsIgnoreCase("true") && mAppointmentListParser.getData() != null) {
                        mListAppointmentDataParsers = mAppointmentListParser.getData();
                        refreshAppointmentData();
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

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            super.onPostExecute(result);
        }
    }


}
