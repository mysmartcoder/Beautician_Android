package beautician.beauty.android.fragments;

import android.annotation.SuppressLint;
import beautician.beauty.android.views.ProgressDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;

import beautician.beauty.android.R;
import beautician.beauty.android.activities.MyFragmentActivity;
import beautician.beauty.android.parsers.CommentListParser;
import beautician.beauty.android.parsers.ProviderParser;
import beautician.beauty.android.parsers.ServiceDataParser;
import beautician.beauty.android.utilities.CommonMethod;
import beautician.beauty.android.utilities.StaticData;
import beautician.beauty.android.views.ReportDialogFragment;

@SuppressLint("InflateParams")
public class CommentsFragment extends Fragment implements OnClickListener {

    private MyFragmentActivity mActivity;
    private CommonMethod mCommonMethod;
    private View rootView;
    private ListView mListView;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    private CommentsAdapter mCommentsAdapter;
    private ImageLoader mImageLoader;
    private DisplayImageOptions mDisplayImageOptions;


    private BackProcessGetCommentsList mBackProcessGetCommentsList;
    private ProgressDialog mProgressDialog;
    private CommentListParser mCommentListParser;
    private String mMethodGetCommentsList = "GetCommentsList";
    private String mStringServiceID = "";


    public CommentsFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_comments, container, false);

        mActivity = (MyFragmentActivity) getActivity();
        mActivity.setHeaderTitle(R.string.lbl_comments_);
        mActivity.setSearchIcon(R.drawable.icon_search);
        mActivity.onBackButton(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mActivity.replaceFragment(new SearchFragment(), true);
            }
        });

        mCommonMethod = new CommonMethod(mActivity);
        mStringServiceID = getArguments().getString(getString(R.string.bundle_service_id));

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


        mCommentListParser = new CommentListParser();

        mBackProcessGetCommentsList = new BackProcessGetCommentsList();
        mBackProcessGetCommentsList.execute(mMethodGetCommentsList);

        return rootView;
    }


    /**
     * Method call will get IDs from xml file.
     *
     * @param v
     */
    private void getWidgetRefrence(View v) {
        mSwipeRefreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.fragment_comments_swiperefresh);
        mListView = (ListView) v.findViewById(R.id.fragment_comments_listview);


    }

    /**
     * Method call will Register OnClick() Events for widgets.
     */
    private void registerOnClick() {
        mSwipeRefreshLayout.setColorSchemeColors(ContextCompat.getColor(mActivity, R.color.myPrimaryColor), ContextCompat.getColor(mActivity, R.color.golden));
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mSwipeRefreshLayout.setRefreshing(true);
                mBackProcessGetCommentsList = new BackProcessGetCommentsList();
                mBackProcessGetCommentsList.execute(mMethodGetCommentsList);
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
     */
    public class CommentsAdapter extends BaseAdapter {
        ViewHolder mViewHolder;

        @Override
        public int getCount() {
            return mCommentListParser.getData().size();
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
                convertview = mActivity.getLayoutInflater().inflate(R.layout.row_comments, null);
                mViewHolder = new ViewHolder();

                mViewHolder.mTextViewName = (TextView) convertview.findViewById(R.id.row_comments_textview_name);
                mViewHolder.mTextViewDate = (TextView) convertview.findViewById(R.id.row_comments_textview_date);
                mViewHolder.mTextViewDesc = (TextView) convertview.findViewById(R.id.row_comments_textview_description);
                mViewHolder.mTextViewService1 = (TextView) convertview.findViewById(R.id.row_comments_textview_service1);
                mViewHolder.mTextViewService2 = (TextView) convertview.findViewById(R.id.row_comments_textview_service2);
                mViewHolder.mTextViewMore = (TextView) convertview.findViewById(R.id.row_comments_textview_service_more);
                mViewHolder.mImageViewProviderPic = (ImageView) convertview.findViewById(R.id.row_comments_imageview_user_dp);
                mViewHolder.mImageViewReport = (ImageView) convertview.findViewById(R.id.frow_comments_imageview_report);


                convertview.setTag(mViewHolder);

            } else {
                mViewHolder = (ViewHolder) convertview.getTag();
            }

            mViewHolder.mTextViewName.setText(mCommentListParser.getData().get(position).getUsername());
            mViewHolder.mTextViewDate.setText(mCommonMethod.getDateInFormate(mCommentListParser.getData().get(position).getUserrating_created(), StaticData.DATE_FORMAT_6,StaticData.DATE_FORMAT_3));
            mViewHolder.mTextViewDesc.setText(mCommentListParser.getData().get(position).getUserrating_comment());
            mImageLoader.getInstance().displayImage(mCommentListParser.getData().get(position).getUser_image(),mViewHolder.mImageViewProviderPic,mDisplayImageOptions);

            if (mCommentListParser.getData().get(position).getCatarr().size() == 1) {
                mViewHolder.mTextViewService1.setText(mCommentListParser.getData().get(position).getCatarr().get(0).getCategory_name());
                mViewHolder.mTextViewService2.setVisibility(View.INVISIBLE);
                mViewHolder.mTextViewMore.setVisibility(View.INVISIBLE);

            } else if (mCommentListParser.getData().get(position).getCatarr().size() == 2) {

                mViewHolder.mTextViewService1.setText(mCommentListParser.getData().get(position).getCatarr().get(0).getCategory_name());
                mViewHolder.mTextViewService2.setText(mCommentListParser.getData().get(position).getCatarr().get(1).getCategory_name());
                mViewHolder.mTextViewService2.setVisibility(View.VISIBLE);
                mViewHolder.mTextViewMore.setVisibility(View.INVISIBLE);

            } else if (mCommentListParser.getData().get(position).getCatarr().size() > 2) {

                mViewHolder.mTextViewService1.setText(mCommentListParser.getData().get(position).getCatarr().get(0).getCategory_name());
                mViewHolder.mTextViewService2.setText(mCommentListParser.getData().get(position).getCatarr().get(1).getCategory_name());
                mViewHolder.mTextViewService2.setVisibility(View.VISIBLE);
                mViewHolder.mTextViewMore.setVisibility(View.VISIBLE);
            } else {
                mViewHolder.mTextViewService2.setVisibility(View.INVISIBLE);
                mViewHolder.mTextViewMore.setVisibility(View.INVISIBLE);
            }

            mViewHolder.mImageViewReport.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    ReportDialogFragment mSharingDialogFragment = new ReportDialogFragment();
                    Bundle mBundle = new Bundle();
                    mBundle.putString(getString(R.string.bundle_report_userid), mCommentListParser.getData().get(position).getUserrating_userby());
                    mBundle.putString(getString(R.string.bundle_report_contentid), mCommentListParser.getData().get(position).getUserrating_id());
                    mBundle.putString(getString(R.string.bundle_report_type), StaticData.REPORT_COMMENT);
                    mSharingDialogFragment.setArguments(mBundle);
                    mSharingDialogFragment.show(getChildFragmentManager(), "");
                }
            });

            return convertview;
        }

    }

    public class ViewHolder {
        TextView mTextViewName;
        TextView mTextViewDate;
        TextView mTextViewDesc;
        TextView mTextViewService1;
        TextView mTextViewService2;
        TextView mTextViewMore;
        ImageView mImageViewProviderPic;
        ImageView mImageViewReport;

    }

    /**
     * AsyncTask for calling webservice in background.
     *
     * @author ebaraiya
     */
    public class BackProcessGetCommentsList extends AsyncTask<String, Void, String> {
        String mCurrentMethod = "";

        @Override
        protected void onPreExecute() {
            mProgressDialog = ProgressDialog.show(mActivity, "", getString(R.string.dialog_loading), true);
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {
            mCurrentMethod = params[0];
            if (mCurrentMethod.equalsIgnoreCase(mMethodGetCommentsList)) {

                mCommentListParser = (CommentListParser) mActivity.getWebMethod().callGetComments(mActivity.getMyApplication().getUserFiled(getString(R.string.sp_user_id)), mActivity.getMyApplication().getUserFiled(getString(R.string.sp_user_hash)), mStringServiceID, mCommentListParser);

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
                    if (mCurrentMethod.equalsIgnoreCase(mMethodGetCommentsList)) {

                        if (mCommentListParser.getWs_status().equalsIgnoreCase("true") && mCommentListParser.getData() != null) {

                            mCommentsAdapter = new CommentsAdapter();
                            mListView.setAdapter(mCommentsAdapter);

                        } else {
                            if (mCommentListParser.getMessage().toString().contains(getString(R.string.alt_msg_exipred))) {
                                mActivity.getAppAlertDialog().showAlertWithSingleButton("", mCommentListParser.getMessage().toString(),
                                        mActivity.getString(R.string.lbl_logout),
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                mActivity.logout();
                                            }
                                        });
                            }else{

                                mActivity.getAppAlertDialog().showDialog(getString(R.string.app_name),mCommentListParser.getMessage(),true);
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
