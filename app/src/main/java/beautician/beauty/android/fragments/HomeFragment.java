package beautician.beauty.android.fragments;

import android.annotation.SuppressLint;
import beautician.beauty.android.views.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
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
import beautician.beauty.android.parsers.CategoryListParser;
import beautician.beauty.android.utilities.RegisterGCM;

@SuppressLint("InflateParams")
public class HomeFragment extends Fragment implements OnClickListener {

    private MyFragmentActivity mActivity;
    private View rootView;
    private ListView mListView;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    private CategoryListAdapter mCategoryListAdapter;
    private ImageLoader mImageLoader;
    private DisplayImageOptions mDisplayImageOptions;

    private BackProcessCategory mBackProcessCategory;
    private ProgressDialog mProgressDialog;
    private CategoryListParser mCategoryListParser;
    private String mStringCategoryID = "";

    public HomeFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_home, container, false);

        mActivity = (MyFragmentActivity) getActivity();
        mActivity.setHeaderTitle(R.string.app_name);
        mActivity.setSearchIcon(R.drawable.icon_search);
        mActivity.onBackButton(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mActivity.replaceFragment(new SearchFragment(), true);
            }
        });


        getWidgetRefrence(rootView);
        registerOnClick();

        mDisplayImageOptions = new DisplayImageOptions.Builder()
//                .showImageForEmptyUri(R.drawable.beautician_logo)
//                .showImageOnFail(R.drawable.beautician_logo)
//                .showImageOnLoading(R.drawable.beautician_logo)
                .cacheInMemory(true)
                .displayer(new RoundedBitmapDisplayer(20))
                .considerExifParams(true)
                .resetViewBeforeLoading(true)
                .cacheOnDisc(true)
                .imageScaleType(ImageScaleType.EXACTLY)
                .displayer(new FadeInBitmapDisplayer(200))
                .build();

        if (mActivity.getMyApplication().getCategoryListParser() != null && mActivity.getMyApplication().getCategoryListParser().getData()!=null) {
            mCategoryListParser = mActivity.getMyApplication().getCategoryListParser();
            mCategoryListAdapter = new CategoryListAdapter();
            mListView.setAdapter(mCategoryListAdapter);
        } else {
            mCategoryListParser = new CategoryListParser();
            mBackProcessCategory = new BackProcessCategory();
            mBackProcessCategory.execute("");
        }

        new RegisterGCM(mActivity).initGCM();

        return rootView;
    }

    /**
     * Method call will get IDs from xml file.
     *
     * @param v
     */
    private void getWidgetRefrence(View v) {
        mListView = (ListView) v.findViewById(R.id.fragment_home_listview);
        mSwipeRefreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.fragment_home_swiperefresh);

    }

    /**
     * Method call will Register OnClick() Events for widgets.
     */
    private void registerOnClick() {

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(mActivity.getMyApplication().isAccountActive()) {
                    mStringCategoryID = mCategoryListParser.getData().get(position).getCategory_id();
                    String mStringCategoryName="";
                    if(mActivity.getMyApplication().getCurrentLang().equalsIgnoreCase("en")) {
                        mStringCategoryName = mCategoryListParser.getData().get(position).getCategory_name();
                    }else
                    {
                        mStringCategoryName = mCategoryListParser.getData().get(position).getCategory_namearebic();
                    }
                    ProviderByCategoryFragment mProviderByCategoryFragment = new ProviderByCategoryFragment();
                    Bundle mBundle = new Bundle();
                    mBundle.putString(getString(R.string.bundle_category_id), mStringCategoryID);
                    mBundle.putString(getString(R.string.bundle_category_name), mStringCategoryName);
                    mProviderByCategoryFragment.setArguments(mBundle);
                    mActivity.replaceFragment(mProviderByCategoryFragment, true);
                }else
                {
                    mActivity.getAppAlertDialog().showDialog("", getString(R.string.alt_msg_disable_account), false);
                }


            }
        });
        mListView.setOnScrollListener(new AbsListView.OnScrollListener() {
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
                mBackProcessCategory = new BackProcessCategory();
                mBackProcessCategory.execute("");
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
     * @author naptel
     */
    public class CategoryListAdapter extends BaseAdapter {
        ViewHolder mViewHolder;

        @Override
        public int getCount() {
            return mCategoryListParser.getData().size();
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
                convertview = mActivity.getLayoutInflater().inflate(R.layout.row_home_category, null);
                mViewHolder = new ViewHolder();

                mViewHolder.mTextViewCategoryName = (TextView) convertview.findViewById(R.id.row_home_category_textview_name);
                mViewHolder.mImageViewWaterMark = (ImageView) convertview.findViewById(R.id.row_home_category_imageview_watermark);


                convertview.setTag(mViewHolder);

            } else {
                mViewHolder = (ViewHolder) convertview.getTag();
            }

            if(mActivity.getMyApplication().getCurrentLang().equalsIgnoreCase("en")) {
                mViewHolder.mTextViewCategoryName.setText(mCategoryListParser.getData().get(position).getCategory_name());
            }
            else {
                mViewHolder.mTextViewCategoryName.setText(mCategoryListParser.getData().get(position).getCategory_namearebic());
            }
            mImageLoader.getInstance().displayImage(mCategoryListParser.getData().get(position).getCategory_watermark(), mViewHolder.mImageViewWaterMark, mDisplayImageOptions);


//            if(position %2 ==0)
//                mImageLoader.getInstance().displayImage("http://khoobsurati.com/wp-content/uploads/2012/09/Hair-cut.jpg", mViewHolder.mImageViewWaterMark, mDisplayImageOptions);
//            else
//                mImageLoader.getInstance().displayImage("https://s-media-cache-ak0.pinimg.com/236x/12/4e/ec/124eec0f658a516a905687469c5fa018.jpg", mViewHolder.mImageViewWaterMark, mDisplayImageOptions);


            return convertview;
        }

    }

    public class ViewHolder {
        TextView mTextViewCategoryName;
        ImageView mImageViewWaterMark;

    }

    /**
     * AsyncTask for calling webservice in background.
     *
     * @author npatel
     */
    public class BackProcessCategory extends AsyncTask<String, Void, String> {


        @Override
        protected void onPreExecute() {
//            mProgressDialog = ProgressDialog.show(mActivity, "", getString(R.string.dialog_loading), true);
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {

            mCategoryListParser = (CategoryListParser) mActivity.getWebMethod().callGetCategories(mCategoryListParser);

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
                    if (mCategoryListParser.getData() != null && mCategoryListParser.getData().size() > 0) {
                        mActivity.getMyApplication().setCategoryListParser(mCategoryListParser);
                        mCategoryListAdapter = new CategoryListAdapter();
                        mListView.setAdapter(mCategoryListAdapter);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            super.onPostExecute(result);
        }
    }

    @Override
    public void onDestroyView() {
        if (mBackProcessCategory != null)
            mBackProcessCategory.cancel(false);
        super.onDestroyView();
    }
}
