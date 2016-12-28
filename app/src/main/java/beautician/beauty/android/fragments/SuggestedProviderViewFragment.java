package beautician.beauty.android.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;

import beautician.beauty.android.R;
import beautician.beauty.android.activities.MyFragmentActivity;
import beautician.beauty.android.parsers.ProviderParser;
import beautician.beauty.android.parsers.UserDataParser;


public final class SuggestedProviderViewFragment extends Fragment {

    private static final String KEY_CONTENT = "Content";

    private int imageSource;
    private ImageView mImageViewUserPic1;
    private ImageView mImageViewUserPic2;
    private TextView mTextViewName1;
    private TextView mTextViewName2;
    private RatingBar mRatingBarSatisfy1;
    private RatingBar mRatingBarSatisfy2;
    private RatingBar mRatingBarCommited1;
    private RatingBar mRatingBarCommited2;
    private RelativeLayout mRelativeLayout1;
    private RelativeLayout mRelativeLayout2;


    private UserDataParser mUserDataParser1;
    private UserDataParser mUserDataParser2;
    private DisplayImageOptions mDisplayImageOptions;

    MyFragmentActivity mActivity;

    public SuggestedProviderViewFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mActivity = (MyFragmentActivity) getActivity();

        mUserDataParser1 = getArguments().getParcelable(getString(R.string.bundle_bundle_feature1));
        mUserDataParser2 = getArguments().getParcelable(getString(R.string.bundle_bundle_feature2));

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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.row_sugested_provider, null);
        mImageViewUserPic1 = (ImageView) root.findViewById(R.id.row_sugested_provider_imageview_user_dp1);
        mImageViewUserPic2 = (ImageView) root.findViewById(R.id.row_sugested_provider_imageview_user_dp2);
        mTextViewName1 = (TextView) root.findViewById(R.id.row_sugested_provider_textview_provider_name1);
        mTextViewName2 = (TextView) root.findViewById(R.id.row_sugested_provider_textview_provider_name2);
        mRatingBarSatisfy1 = (RatingBar) root.findViewById(R.id.view_rating_rating_satisfyed1);
        mRatingBarSatisfy2 = (RatingBar) root.findViewById(R.id.view_rating_rating_satisfyed2);
        mRatingBarCommited1 = (RatingBar) root.findViewById(R.id.view_rating_rating_commitment1);
        mRatingBarCommited2 = (RatingBar) root.findViewById(R.id.view_rating_rating_commitment2);
        mRelativeLayout1 = (RelativeLayout)root.findViewById(R.id.row_sugested_provider_relative_1);
        mRelativeLayout2 = (RelativeLayout)root.findViewById(R.id.row_sugested_provider_relative_2);

        mImageViewUserPic1.setImageResource(imageSource);

        if(mUserDataParser1!=null)
        {
            mTextViewName1.setText(mUserDataParser1.getUsername());
            mRatingBarSatisfy1.setRating(Float.parseFloat(mUserDataParser1.getTotalsatisfy()));
            mRatingBarCommited1.setRating(Float.parseFloat(mUserDataParser1.getTotalcommited()));
            ImageLoader.getInstance().displayImage(mUserDataParser1.getUser_image(), mImageViewUserPic1, mDisplayImageOptions);
            mRelativeLayout1.setVisibility(View.VISIBLE);
        }else {
            mRelativeLayout1.setVisibility(View.INVISIBLE);
        }

        if(mUserDataParser2!=null)
        {
            mTextViewName2.setText(mUserDataParser2.getUsername());
            mRatingBarSatisfy2.setRating(Float.parseFloat(mUserDataParser2.getTotalsatisfy()));
            mRatingBarCommited2.setRating(Float.parseFloat(mUserDataParser2.getTotalcommited()));
            ImageLoader.getInstance().displayImage(mUserDataParser2.getUser_image(), mImageViewUserPic2, mDisplayImageOptions);
            mRelativeLayout2.setVisibility(View.VISIBLE);
        }else {
            mRelativeLayout2.setVisibility(View.INVISIBLE);
        }

        mRelativeLayout1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ProviderDetailsCategoryPagerFragment mProviderDetailsFragment = new ProviderDetailsCategoryPagerFragment();
                Bundle mBundle = new Bundle();
                mBundle.putString(getString(R.string.bundle_provider_id), mUserDataParser1.getUser_id());
                mProviderDetailsFragment.setArguments(mBundle);
                mActivity.replaceFragment(mProviderDetailsFragment, true);
            }
        });

        mRelativeLayout2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ProviderDetailsCategoryPagerFragment mProviderDetailsFragment = new ProviderDetailsCategoryPagerFragment();
                Bundle mBundle = new Bundle();
                mBundle.putString(getString(R.string.bundle_provider_id), mUserDataParser2.getUser_id());
                mProviderDetailsFragment.setArguments(mBundle);
                mActivity.replaceFragment(mProviderDetailsFragment, true);
            }
        });

        return root;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(KEY_CONTENT, imageSource);
    }
}
