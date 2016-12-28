package beautician.beauty.android.views;

import android.annotation.SuppressLint;
import android.app.ActionBar.LayoutParams;
import android.app.Activity;
import android.app.Dialog;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;

import java.util.ArrayList;
import java.util.List;

import beautician.beauty.android.R;

@SuppressLint("InflateParams")
public class FullScreenImageDialog {

    private Activity mActivity;

    private TouchImageView mTouchImageView;
    private String mStringURL = "";

    private ImageLoader imageLoader;
    private DisplayImageOptions options;
    private ArrayList<String> mArryListServicePicPath;
    private ImageView mImageViewClose;
    private int mIntCurrentPos = -1;
    private boolean isSingleImage = false;

    Dialog dialog;

    public FullScreenImageDialog(Activity activity, String mStringURL, ArrayList<String> mArryListServicePicPath, int pos, boolean isSigle) {
        this.mStringURL = mStringURL;
        this.mArryListServicePicPath = mArryListServicePicPath;
        this.mIntCurrentPos = pos;
        this.isSingleImage = isSigle;
        mActivity = activity;

        prepareDiaog();
    }

    public void prepareDiaog() {
        dialog = new Dialog(mActivity);
        View view = mActivity.getLayoutInflater().inflate(R.layout.fragment_fullscreen_image, null);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setContentView(view);
        Window window = dialog.getWindow();
        window.setLayout(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);

        dialog.show();

        mTouchImageView = (TouchImageView) view.findViewById(R.id.fragment_fullscreen_image_imageview);
        imageLoader = ImageLoader.getInstance();

        options = new DisplayImageOptions.Builder().displayer(new FadeInBitmapDisplayer(0)).cacheInMemory(true).cacheOnDisk(true)
                .showImageForEmptyUri(R.drawable.ic_launcher).showImageOnFail(R.drawable.ic_launcher).considerExifParams(true)
                .bitmapConfig(Bitmap.Config.RGB_565).build();


        mImageViewClose = (ImageView) view.findViewById(R.id.dialog_city_imageview_close);
        mImageViewClose.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                dialog.cancel();
            }
        });

        if (isSingleImage) {
            imageLoader.displayImage(mStringURL, mTouchImageView, options);
        }else {
            List<TouchImageView> images = new ArrayList<TouchImageView>();
            for (int i = 0; i < mArryListServicePicPath.size(); i++) {
                TouchImageView imageView = new TouchImageView(mActivity);
                imageLoader.displayImage(mArryListServicePicPath.get(i).toString(), imageView, options);
                images.add(imageView);
            }

            ImagePagerAdapter pageradapter = new ImagePagerAdapter(images);
            ViewPager viewpager = (ViewPager) view.findViewById(R.id.pager);
            viewpager.setAdapter(pageradapter);
            viewpager.setCurrentItem(mIntCurrentPos);
        }

    }


    /**
     * BaseAdapter class for load data into listview
     *
     * @author ebaraiya
     */
    public class ImagePagerAdapter extends PagerAdapter {

        private List<TouchImageView> images;

        public ImagePagerAdapter(List<TouchImageView> images) {
            this.images = images;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            TouchImageView imageView = images.get(position);
            container.addView(imageView);
            return imageView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView(images.get(position));
        }

        @Override
        public int getCount() {
            return images.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object o) {
            return view == o;
        }
    }


}
