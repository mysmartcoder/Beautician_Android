package beautician.beauty.android.activities;

import android.app.Activity;
import beautician.beauty.android.views.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;

import beautician.beauty.android.R;
import beautician.beauty.android.helper.instagram.ApplicationData;
import beautician.beauty.android.helper.instagram.InstagramApp;
import beautician.beauty.android.helper.instagram.InstagramSession;
import beautician.beauty.android.helper.instagram.JSONParser;
import beautician.beauty.android.helper.instagram.MyGridListAdapter;
import beautician.beauty.android.utilities.CommonMethod;


public class InstagramPictureActivity extends Activity {

    private TextView mTextViewBack;
    private GridView mGridViewPictures;
    private ArrayList<String> imageThumbList = new ArrayList<String>();
    private ArrayList<String> imageMainList = new ArrayList<String>();
    private Context context;
    private int WHAT_FINALIZE = 0;
    private static int WHAT_ERROR = 1;
    private ProgressDialog mProgressDialog;
    public static final String TAG_DATA = "data";
    public static final String TAG_IMAGES = "images";
    public static final String TAG_THUMBNAIL = "thumbnail";
    public static final String TAG_URL = "url";
    private Activity mActivity;

    InstagramSession mInstagramSession;
    InstagramApp mInstagramApp;
    CommonMethod mCommonMethod;

    private String mMethodGetImages = "GetImages";
    private String mMethodDownloadImage = "DownloadImage";
    private String mStringDownloadURL = "";

    String file_name = "";

    private Handler handler = new Handler(new Handler.Callback() {

        @Override
        public boolean handleMessage(Message msg) {
            if (msg.what == InstagramApp.WHAT_FINALIZE) {
                //Toast.makeText(LoginActivity.this, "Call handler", Toast.LENGTH_SHORT).show();
                new BackProcessGetInstagramPicture().execute(mMethodGetImages);
            } else if (msg.what == InstagramApp.WHAT_FINALIZE) {
                // Toast.makeText(LoginActivity.this, "Check your network.", Toast.LENGTH_SHORT).show();
            }
            return false;
        }
    });


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mActivity = this;
        mInstagramSession = new InstagramSession(this);
        mCommonMethod = new CommonMethod(this);

        setContentView(R.layout.activity_instagram_picture);

        file_name = getIntent().getExtras().getString(MediaStore.EXTRA_OUTPUT);

        mTextViewBack = (TextView) findViewById(R.id.activity_instagram_picture_textview_back);
        mTextViewBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mGridViewPictures = (GridView) findViewById(R.id.activity_instagram_picture_gridview);
        mGridViewPictures.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                mStringDownloadURL = imageMainList.get(position);
                new BackProcessGetInstagramPicture().execute(mMethodDownloadImage);

            }
        });

        context = InstagramPictureActivity.this;

        mInstagramApp = new InstagramApp(this, ApplicationData.CLIENT_ID, ApplicationData.CLIENT_SECRET, ApplicationData.CALLBACK_URL);
        mInstagramApp.setListener(new InstagramApp.OAuthAuthenticationListener() {

            @Override
            public void onSuccess() {
                mInstagramApp.fetchUserName(handler);

            }

            @Override
            public void onFail(String error) {
            }
        });
        if (mInstagramApp.hasAccessToken()) {
            new BackProcessGetInstagramPicture().execute(mMethodGetImages);
        } else {
            mInstagramApp.authorize();
        }

    }

    private void setImageGridAdapter() {
        mGridViewPictures.setAdapter(new MyGridListAdapter(context, imageThumbList));
    }


    /**
     * AsyncTask for calling webservice in background.
     *
     * @author npatel
     */
    public class BackProcessGetInstagramPicture extends AsyncTask<String, Void, String> {

        String mCurrentMethod = "";
        String mStringFilePath = "";

        @Override
        protected void onPreExecute() {
            mProgressDialog = ProgressDialog.show(mActivity, "", getString(R.string.dialog_loading), true);
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {
            mCurrentMethod = params[0];
            if (mCurrentMethod.equalsIgnoreCase(mMethodGetImages))
                getAllInstagramImages();
            else if (mCurrentMethod.equalsIgnoreCase(mMethodDownloadImage)) {
//                String file_name = String.valueOf(System.currentTimeMillis())+".png";
                File imgFile = new File(mCommonMethod.getImageDirectory(getString(R.string.folder_service_pic)) + "/" + file_name);
                mStringFilePath = mCommonMethod.downloadFile(mStringDownloadURL, imgFile.getAbsolutePath());
            }
            return null;

        }

        @Override
        protected void onPostExecute(String result) {

            if (mProgressDialog != null)
                mProgressDialog.dismiss();

            try {
                if (mCurrentMethod.equalsIgnoreCase(mMethodGetImages)) {
                    if (imageThumbList.size() > 0) {
                        setImageGridAdapter();
                    }
                }else if (mCurrentMethod.equalsIgnoreCase(mMethodDownloadImage)) {
                    if(mStringFilePath.length() > 0)
                    {
                        Intent intent = new Intent();
                        intent.putExtra(getString(R.string.bundle_path), mStringFilePath);
                        setResult(RESULT_OK, intent);
                        finish();
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

            super.onPostExecute(result);
        }
    }

    public void getAllInstagramImages() {
        try {
            JSONParser jsonParser = new JSONParser();
            JSONObject jsonObject = jsonParser
                    .getJSONFromUrlByGet("https://api.instagram.com/v1/users/"
                            + mInstagramSession.getTAGId()
                            + "/media/recent/?client_id="
                            + ApplicationData.CLIENT_ID
                            + "&count=50"
                            + "&access_token="+mInstagramApp.getTOken());

            JSONArray data = jsonObject.getJSONArray(TAG_DATA);
            for (int data_i = 0; data_i < data.length(); data_i++) {
                JSONObject data_obj = data.getJSONObject(data_i);

                JSONObject images_obj = data_obj
                        .getJSONObject(TAG_IMAGES);

                JSONObject thumbnail_obj = images_obj
                        .getJSONObject(TAG_THUMBNAIL);

                JSONObject standard_obj = images_obj
                        .getJSONObject("standard_resolution");

                // String str_height =
                // thumbnail_obj.getString(TAG_HEIGHT);
                //
                // String str_width =
                // thumbnail_obj.getString(TAG_WIDTH);

                String str_url = thumbnail_obj.getString(TAG_URL);
                imageThumbList.add(str_url);

                String standard_url = standard_obj.getString(TAG_URL);
                imageMainList.add(standard_url);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
