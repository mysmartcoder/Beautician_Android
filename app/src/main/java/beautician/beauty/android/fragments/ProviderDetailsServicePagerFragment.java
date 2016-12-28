package beautician.beauty.android.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import beautician.beauty.android.R;
import beautician.beauty.android.activities.MyFragmentActivity;
import beautician.beauty.android.parsers.CategoryDataParser;

@SuppressLint("InflateParams")
public class ProviderDetailsServicePagerFragment extends Fragment implements OnClickListener {

    private MyFragmentActivity mActivity;
    private View rootView;


    private ViewPager mViewPager;


    private MyPagerAdapter adapter;
    private CategoryDataParser mCategoryListParser;
    private String mStringProviderName = "";
    private String mStringProviderPic = "";
    private String mStringProviderID = "";
    private String mStringServiceID = "";
    private int mIntPagePosition = 0;

    public ProviderDetailsServicePagerFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_provider_details_viewpager, container, false);

        mActivity = (MyFragmentActivity) getActivity();
        mActivity.setHeaderTitle(R.string.lbl_provider_detail);
        mActivity.setSearchIcon(R.drawable.icon_search);
        mActivity.onBackButton(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mActivity.replaceFragment(new SearchFragment(),true);
            }
        });


        getWidgetRefrence(rootView);
        registerOnClick();
        setData();

        return rootView;
    }


    /**
     * Method call will get IDs from xml file.
     *
     * @param v
     */
    private void getWidgetRefrence(View v) {


        mViewPager = (ViewPager) v.findViewById(R.id.fragment_provider_details_viewpager_pager);


    }

    /**
     * Method call will Register OnClick() Events for widgets.
     */
    private void registerOnClick() {

    }

    /**
     * Method call OnClick Event fire.
     */
    @Override
    public void onClick(View v) {

    }

    /**
     * Method call will set data..
     */
    private void setData() {

        mCategoryListParser = new CategoryDataParser();
        mCategoryListParser = getArguments().getParcelable(getString(R.string.bundle_category_data));
        mStringProviderName = getArguments().getString(getString(R.string.bundle_provider_name));
        mStringProviderPic = getArguments().getString(getString(R.string.bundle_provider_image));
        mStringProviderID = getArguments().getString(getString(R.string.bundle_provider_id));
        mStringServiceID = getArguments().getString(getString(R.string.bundle_service_id));

        if(mCategoryListParser.getServices()!=null) {
            for (int i = 0; i < mCategoryListParser.getServices().size(); i++) {
                if (mCategoryListParser.getServices().get(i).getService_id().equalsIgnoreCase(mStringServiceID)) {
                    mIntPagePosition = i;
                }
            }
        }

        adapter = new MyPagerAdapter(getChildFragmentManager());
        mViewPager.setAdapter(adapter);
        final int pageMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4, getResources().getDisplayMetrics());
        mViewPager.setPageMargin(pageMargin);
        mViewPager.setCurrentItem(mIntPagePosition);

    }


    public class MyPagerAdapter extends FragmentPagerAdapter {
        int pos = -1;


        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            pos = position;
            return "";
        }

        @Override
        public int getCount() {
            return mCategoryListParser.getServices().size();
        }


        @Override
        public Fragment getItem(int arg0) {

//            if(arg0 == 0)
//            {
//                ProviderDetailsViewPagerFragment mProviderDetailsViewPagerFragment = new ProviderDetailsViewPagerFragment();
//                Bundle mBundle = new Bundle();
//                mBundle.putParcelable(getString(R.string.bundle_category_data), mGetProviderListParser.getData().getCategories().get(arg0));
//                mProviderDetailsViewPagerFragment.setArguments(mBundle);
//
//                return mProviderDetailsViewPagerFragment;
//            }else {
//                ProviderDetailsDataFragment mProviderDetailsDataFragment = new ProviderDetailsDataFragment();
//                Bundle mBundle = new Bundle();
//                mBundle.putParcelable(getString(R.string.bundle_service_data), mGetProviderListParser.getData().getCategories().get(arg0));
//                mProviderDetailsDataFragment.setArguments(mBundle);
//                return mProviderDetailsDataFragment;
//
//            }

            ProviderDetailsDataFragment mProviderDetailsDataFragment = new ProviderDetailsDataFragment();
            Bundle mBundle = new Bundle();
            mBundle.putParcelable(getString(R.string.bundle_service_data), mCategoryListParser.getServices().get(arg0));
            mBundle.putString(getString(R.string.bundle_category_id), mCategoryListParser.getCategory_id());
            mBundle.putString(getString(R.string.bundle_category_name), mCategoryListParser.getCategory_name());
            mBundle.putString(getString(R.string.bundle_provider_name), mStringProviderName);
            mBundle.putString(getString(R.string.bundle_provider_image), mStringProviderPic);
            mBundle.putString(getString(R.string.bundle_provider_id), mStringProviderID);
            mProviderDetailsDataFragment.setArguments(mBundle);
            return mProviderDetailsDataFragment;

        }

    }

}
