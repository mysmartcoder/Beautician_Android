package beautician.beauty.android.utilities;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.util.Log;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

import beautician.beauty.android.R;

/**
 * Created by npatel on 12/24/2015.
 */
public class WebMethod {

    public boolean isError = false;
    public boolean isNetError = false;

    Context mContext;
    Gson mGson;

    // String mStringWSMainURL = "http://nexuslink.in/beautician/api/index.php?api=";
//    String mStringWSMainURL = "http://nexuslink.in/beautician/api/index.php?api=";

//    public static String mStringURLAppointment = "http://nexuslink.in/beautician/api/index.php?api=paymentform&";
//    public static String mStringURLBuyAds = "http://nexuslink.in/beautician/api/index.php?api=buyadspaymentform&";
//    public static String mStringURLTerms = "http://nexuslink.in/beautician/api/privacypolicy.php?language=";

    String mStringWSMainURL = "http://beauticianapp.com/api/index.php?api=";

    public static String mStringURLAppointment = "http://beauticianapp.com/api/index.php?api=paymentform&";
    public static String mStringURLBuyAds = "http://beauticianapp.com/api/index.php?api=buyadspaymentform&";
    public static String mStringURLTerms = "http://beauticianapp.com/api/privacypolicy.php?language=";

    String TAG = "WEB METHOD";

    public WebMethod(Context activity) {
        mContext = activity;
        mGson = new Gson();
    }

    /**
     * function will check internet avail or not
     *
     * @return boolean
     */
    public boolean check_Internet() {
        ConnectivityManager cm = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            return true;
        }
        return false;
    }


    /**
     * Method will call webservice
     *
     * @param mStringUrl
     * @param post_data
     * @return
     */
    public Object callWebService(String mStringUrl, String post_data, Object mObject) {

        Object mFillObject = null;
        String responseData = "";
        isError = false;
        isNetError = false;
        if (!check_Internet()) {
            isNetError = true;
            isError = true;
            return mObject;
        }

        try {
            mFillObject = mObject.getClass().newInstance();

            BufferedReader reader = null;
            URL url = new URL(mStringUrl);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();

//            con.setRequestProperty("Content-Type", "application/json; charset=utf-8");
            con.setRequestMethod("POST");
            con.setDoOutput(true);

//			post_data = URLEncoder.encode(post_data, "utf-8");

            Log.e("Web : I/O ", post_data);
            if (post_data.length() > 0) {
                OutputStreamWriter writer = new OutputStreamWriter(con.getOutputStream());
                writer.write(post_data);
                writer.flush();
                writer.close();
            }

            StringBuilder sb = new StringBuilder();
            reader = new BufferedReader(new InputStreamReader(con.getInputStream()));

            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
            line = sb.toString();
            responseData = sb.toString();
            System.out.print("RESPONSE==" + line);
//            writeLogToFile(line, mStringUrl.substring(40, 50));

            mFillObject = mGson.fromJson(responseData, mFillObject.getClass());

            con.disconnect();

        } catch (Exception e) {
            isError = true;
            e.printStackTrace();
        }
        return mFillObject;
    }

    public void writeLogToFile(String e, String filename) {
        try {
            File SDCardRoot = Environment.getExternalStorageDirectory();
            File folder = new File(SDCardRoot, "beautician");
            if (!folder.exists())
                folder.mkdirs();

            filename = filename + ".txt";

            BufferedWriter bos = new BufferedWriter(new FileWriter(folder.getAbsolutePath() + "/" + filename));
            bos.write(e);
            bos.flush();
            bos.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }


    /**
     * Method will call webservice
     *
     * @param mStringUrl
     * @return
     */
    public Object callGetWebService(String mStringUrl, Object mObject) {

        Object mFillObject = null;
        String responseData = "";
        isError = false;
        isNetError = false;
        if (!check_Internet()) {
            isNetError = true;
            isError = true;
            return mObject;
        }

        try {
            mFillObject = mObject.getClass().newInstance();

            BufferedReader reader = null;
            URL url = new URL(mStringUrl);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();

//			con.setRequestProperty("Content-Type", "application/json; charset=utf-8");
            con.setRequestMethod("GET");
//			con.setDoOutput(true);
//			con.setDoInput(true);

            StringBuilder sb = new StringBuilder();
            reader = new BufferedReader(new InputStreamReader(con.getInputStream()));

            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
            line = sb.toString();
            responseData = sb.toString();
            System.out.print("RESPONSE==" + line);

            mFillObject = mGson.fromJson(responseData, mFillObject.getClass());

        } catch (Exception e) {
            isError = true;
            e.printStackTrace();
        }
        return mFillObject;
    }

    private String getEncodedData(Map<String, String> data) {
        StringBuilder sb = new StringBuilder();

        SharedPreferences mSharedPreferences = mContext.getSharedPreferences(mContext.getString(R.string.sp_file_name), Context.MODE_PRIVATE);
        data.put("language", mSharedPreferences.getString(mContext.getString(R.string.sp_sp_lang), "en"));
        TimeZone tz = TimeZone.getDefault();
        data.put("timezone", tz.getID());

        for (String key : data.keySet()) {
            String value = null;
            try {
                value = URLEncoder.encode(data.get(key), "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }catch (Exception e) {
                e.printStackTrace();
            }

            if (sb.length() > 0)
                sb.append("&");

            sb.append(key + "=" + value);
        }
        return sb.toString();
    }


    /**
     * Method will call login API
     *
     * @param email
     * @param password
     * @return API response
     */
    public Object callLogin(String email, String password, Object mObject) {
        String mStringURL = mStringWSMainURL + "login";
        Map<String, String> dataToSend = new HashMap<>();
        dataToSend.put("email", email);
        dataToSend.put("password", password);
        String encodedStr = getEncodedData(dataToSend);

        return callWebService(mStringURL, encodedStr, mObject);
    }


    /**
     * Method will call Register API
     *
     * @param email
     * @param password
     * @param username
     * @return API response
     */
    public Object callRegister(String username, String mobile, String email, String password, Object mObject) {
        String mStringURL = mStringWSMainURL + "signup";
        Map<String, String> dataToSend = new HashMap<>();
        dataToSend.put("username", username);
        dataToSend.put("phone", mobile);
        dataToSend.put("email", email);
        dataToSend.put("password", password);
        String encodedStr = getEncodedData(dataToSend);

        return callWebService(mStringURL, encodedStr, mObject);
    }


    /**
     * Method will call sign in by social Web API
     *
     * @param social_id
     * @param social_type
     * @param mObject
     * @return API Response
     */
    public Object callSignInBySocial(String social_id, String social_type, Object mObject) {
        String mStringURL = mStringWSMainURL + "signinbysocial";
        Map<String, String> dataToSend = new HashMap<>();
        dataToSend.put("social_id", social_id);
        dataToSend.put("social_type", social_type);
        String encodedStr = getEncodedData(dataToSend);

        return callWebService(mStringURL, encodedStr, mObject);
    }

    /**
     * Method will call signup by social Wen API
     *
     * @param social_id
     * @param social_type
     * @param username
     * @param email
     * @param password
     * @param profile_pic
     * @param mObject
     * @return API Response
     */
    public Object callSignUpBySocial(String social_id, String social_type, String username, String email,
                                     String password, String profile_pic, Object mObject) {
        String mStringURL = mStringWSMainURL + "signupbysocial";
        Map<String, String> dataToSend = new HashMap<>();
        dataToSend.put("social_id", social_id);
        dataToSend.put("social_type", social_type);
        dataToSend.put("username", username);
        dataToSend.put("email", email);
        dataToSend.put("password", password);
        dataToSend.put("profile_pic", profile_pic);
        String encodedStr = getEncodedData(dataToSend);

        System.out.println("RESPONSE==" + social_type);
        return callWebService(mStringURL, encodedStr, mObject);
    }

    /**
     * Method will call verify code Web API
     *
     * @param user_id
     * @param code
     * @param mObject
     * @return API Response
     */
    public Object callVerifyCode(String user_id, String code, Object mObject) {
        String mStringURL = mStringWSMainURL + "varifycode";
        Map<String, String> dataToSend = new HashMap<>();
        dataToSend.put("user_id", user_id);
        dataToSend.put("code", code);
        String encodedStr = getEncodedData(dataToSend);

        return callWebService(mStringURL, encodedStr, mObject);
    }

    /**
     * Method will call request code Web API
     *
     * @param user_id
     * @param mObject
     * @return API Response
     */
    public Object callRequestVerifyCodeForPassword(String user_id, Object mObject) {
        String mStringURL = mStringWSMainURL + "RequestCodeForgotPassword";
        Map<String, String> dataToSend = new HashMap<>();
        dataToSend.put("email", user_id);
        String encodedStr = getEncodedData(dataToSend);

        return callWebService(mStringURL, encodedStr, mObject);
    }

    /**
     * Method will cal change password Web API
     *
     * @param user_id
     * @param code
     * @param password
     * @param mObject
     * @return API Response
     */
    public Object callChangePassword(String user_id, String code, String password, Object mObject) {
        String mStringURL = mStringWSMainURL + "SetNewPassword";
        Map<String, String> dataToSend = new HashMap<>();
        dataToSend.put("user_id", user_id);
        dataToSend.put("code", code);
        dataToSend.put("password", password);

        String encodedStr = getEncodedData(dataToSend);

        return callWebService(mStringURL, encodedStr, mObject);
    }

    /**
     * Method will call change password Web API
     *
     * @param user_id
     * @param token
     * @param old_password
     * @param password
     * @param mObject
     * @return API response.
     */
    public Object callChangePasswordWithOld(String user_id, String token, String old_password, String password, Object mObject) {
        String mStringURL = mStringWSMainURL + "changepassword";
        Map<String, String> dataToSend = new HashMap<>();
        dataToSend.put("user_id", user_id);
        dataToSend.put("token", token);
        dataToSend.put("oldpassword", old_password);
        dataToSend.put("newpassword", password);

        String encodedStr = getEncodedData(dataToSend);

        return callWebService(mStringURL, encodedStr, mObject);
    }

    /**
     * Method will cal upload pic Web API
     *
     * @param user_id
     * @param token
     * @param image_path
     * @param mObject
     * @return API Response
     */
    public Object callUploadPic(String user_id, String token, String image_path, Object mObject) {
        String charset = "UTF-8";
        String mStringURL = mStringWSMainURL + "uploaduserpic";
        Object mFillObject = null;
        try {

            mFillObject = mObject.getClass().newInstance();

            MultipartUtility multipart = new MultipartUtility(mStringURL, charset);

//            multipart.addHeaderField("User-Agent", "CodeJava");
//            multipart.addHeaderField("Test-Header", "Header-Value");

            multipart.addFormField("user_id", user_id);
            multipart.addFormField("token", token);

            File uploadFile1 = new File(image_path);
            multipart.addFilePart("image", uploadFile1);

            String response = multipart.finish();

            System.out.println("SERVER REPLIED: " + response);

            mFillObject = mGson.fromJson(response, mFillObject.getClass());

            return mFillObject;
        } catch (Exception e) {
            isError = true;
            e.printStackTrace();
        }

        return mFillObject;
    }

    /**
     * Method will call GetCategories API
     *
     * @return API response
     */
    public Object callGetCategories(Object mObject) {
        String mStringURL = mStringWSMainURL + "listCategory";
        return callWebService(mStringURL, "", mObject);
    }

    /**
     * Method will call get Profile Web API
     *
     * @param user_id
     * @param token
     * @param mObject
     * @return API Response
     */
    public Object callGetProfile(String user_id, String token, Object mObject) {
        String mStringURL = mStringWSMainURL + "getprofile";
        Map<String, String> dataToSend = new HashMap<>();
        dataToSend.put("user_id", user_id);
        dataToSend.put("token", token);

        String encodedStr = getEncodedData(dataToSend);

        return callWebService(mStringURL, encodedStr, mObject);
    }

    /**
     * Method will call provider update setting Web API
     *
     * @param user_id
     * @param token
     * @param location
     * @param locationpolicy
     * @param city
     * @param country
     * @param lat
     * @param lng
     * @param mon_start
     * @param mon_end
     * @param tue_start
     * @param tue_end
     * @param wed_start
     * @param wed_end
     * @param thu_start
     * @param thu_end
     * @param fri_start
     * @param fri_end
     * @param sat_start
     * @param sat_end
     * @param sun_start
     * @param sun_end
     * @param mObject
     * @return API Response
     */
    public Object callUpdateProviderSettings(String user_id, String token,
                                             String location, String locationpolicy,
                                             String transport_fee, String min_appt_fee,
                                             String city, String country,
                                             String lat, String lng,
                                             String satisfyrating, String commitedrating,
                                             String mon_start, String mon_end,
                                             String tue_start, String tue_end,
                                             String wed_start, String wed_end,
                                             String thu_start, String thu_end,
                                             String fri_start, String fri_end,
                                             String sat_start, String sat_end,
                                             String sun_start, String sun_end,
                                             String mindays, String maxdays,
                                             Object mObject) {
        String mStringURL = mStringWSMainURL + "updateprovidersettings";
        Map<String, String> dataToSend = new HashMap<>();
        dataToSend.put("user_id", user_id);
        dataToSend.put("token", token);
        dataToSend.put("location", location);
        dataToSend.put("locationpolicy", locationpolicy);
        dataToSend.put("user_transportfee", transport_fee);
        dataToSend.put("user_minapptfee", min_appt_fee);
        dataToSend.put("city", city);
        dataToSend.put("country", country);
        dataToSend.put("lat", lat);
        dataToSend.put("lng", lng);
        dataToSend.put("satisfyrating", satisfyrating);
        dataToSend.put("commitedrating", commitedrating);
        dataToSend.put("mon_start", mon_start);
        dataToSend.put("mon_end", mon_end);
        dataToSend.put("tue_start", tue_start);
        dataToSend.put("tue_end", tue_end);
        dataToSend.put("wed_start", wed_start);
        dataToSend.put("wed_end", wed_end);
        dataToSend.put("thu_start", thu_start);
        dataToSend.put("thu_end", thu_end);
        dataToSend.put("fri_start", fri_start);
        dataToSend.put("fri_end", fri_end);
        dataToSend.put("sat_start", sat_start);
        dataToSend.put("sat_end", sat_end);
        dataToSend.put("sun_start", sun_start);
        dataToSend.put("sun_end", sun_end);
        dataToSend.put("mindays", mindays);
        dataToSend.put("maxdays", maxdays);

        String encodedStr = getEncodedData(dataToSend);

        return callWebService(mStringURL, encodedStr, mObject);
    }

    /**
     * Method will call update username Web API
     *
     * @param user_id
     * @param token
     * @param new_username
     * @param mObject
     * @return
     */
    public Object callUpdateUserName(String user_id, String token, String new_username, Object mObject) {
        String mStringURL = mStringWSMainURL + "updateprofile";
        Map<String, String> dataToSend = new HashMap<>();
        dataToSend.put("user_id", user_id);
        dataToSend.put("token", token);
        dataToSend.put("username", new_username);

        String encodedStr = getEncodedData(dataToSend);

        return callWebService(mStringURL, encodedStr, mObject);
    }

    /**
     * Method will call update email and mobile no.
     *
     * @param user_id
     * @param token
     * @param type
     * @param value
     * @param mObject
     * @return
     */
    public Object callUpdateEmailAndMobileNo(String user_id, String token, String type, String value, Object mObject) {
        String mStringURL = mStringWSMainURL + "changeemailorphone";
        Map<String, String> dataToSend = new HashMap<>();
        dataToSend.put("user_id", user_id);
        dataToSend.put("token", token);
        dataToSend.put("type", type);
        dataToSend.put("value", value);
        String encodedStr = getEncodedData(dataToSend);

        return callWebService(mStringURL, encodedStr, mObject);
    }

    /**
     * Method will call send verify code.
     *
     * @param user_id
     * @param token
     * @param type
     * @param value
     * @param mObject
     * @return
     */
    public Object callSendEmailAndMobileVerifyCode(String user_id, String token, String type, String value, String code, Object mObject) {
        String mStringURL = mStringWSMainURL + "varifyemailorphone";
        Map<String, String> dataToSend = new HashMap<>();
        dataToSend.put("user_id", user_id);
        dataToSend.put("token", token);
        dataToSend.put("type", type);
        dataToSend.put("value", value);
        dataToSend.put("code", code);
        String encodedStr = getEncodedData(dataToSend);

        return callWebService(mStringURL, encodedStr, mObject);
    }


    /**
     * Method will call GetServiceTime API
     *
     * @return API response
     */
    public Object callGetServiceTime(Object mObject) {
        String mStringURL = mStringWSMainURL + "getservicetime";
        return callWebService(mStringURL, "", mObject);
    }


    /**
     * Method will call add Beauty Services..
     *
     * @param user_id
     * @param token
     * @param category_id
     * @param name
     * @param description
     * @param avgtime
     * @param avgprice
     * @param image_path
     * @param mObject
     * @return
     */
    public Object callAddBeautyService(String user_id, String token, String category_id, String name,
                                       String description, String avgtime, String avgprice,
                                       ArrayList<String> image_path, Object mObject) {
        String charset = "UTF-8";
        String mStringURL = mStringWSMainURL + "addbeautyservices";
        Object mFillObject = null;
        try {

            mFillObject = mObject.getClass().newInstance();

            MultipartUtility multipart = new MultipartUtility(mStringURL, charset);
            multipart.addFormField("user_id", user_id);
            multipart.addFormField("token", token);
            multipart.addFormField("category_id", category_id);
            multipart.addFormField("name", name);
            multipart.addFormField("description", description);
            multipart.addFormField("avgtime", avgtime);
            multipart.addFormField("avgprice", avgprice);
            multipart.addFormField("totalimage", String.valueOf(image_path.size()));

            for (int i = 0; i < image_path.size(); i++) {
                File uploadFile1 = new File(image_path.get(i).toString());
                multipart.addFilePart("image" + String.valueOf(i + 1), uploadFile1);
                System.out.println("PATH==");
            }
            String response = multipart.finish();
            System.out.println("SERVER REPLIED: " + response);

            mFillObject = mGson.fromJson(response, mFillObject.getClass());

            return mFillObject;
        } catch (Exception e) {
            isError = true;
            e.printStackTrace();
        }

        return mFillObject;
    }

    /**
     * Method will call get My Services list.
     *
     * @param user_id
     * @param token
     * @param mObject
     * @return
     */
    public Object callGetMyService(String user_id, String token, Object mObject) {
        String mStringURL = mStringWSMainURL + "getmybeautyservices";
        Map<String, String> dataToSend = new HashMap<>();
        dataToSend.put("user_id", user_id);
        dataToSend.put("token", token);
        String encodedStr = getEncodedData(dataToSend);
        return callWebService(mStringURL, encodedStr, mObject);
    }

    /**
     * Method will call delete my beauty services.
     * @param user_id
     * @param token
     * @param service_id
     * @param mObject
     * @return
     */
    public Object callDeleteMyService(String user_id, String token, String service_id, Object mObject) {
        String mStringURL = mStringWSMainURL + "deletebeautyservice";
        Map<String, String> dataToSend = new HashMap<>();
        dataToSend.put("user_id", user_id);
        dataToSend.put("token", token);
        dataToSend.put("service_id", service_id);
        String encodedStr = getEncodedData(dataToSend);
        return callWebService(mStringURL, encodedStr, mObject);
    }

    /**
     * Method will  call enable my beauty services.
     * @param user_id
     * @param token
     * @param service_id
     * @param mObject
     * @return
     */
    public Object callEnableMyService(String user_id, String token, String service_id, Object mObject) {
        String mStringURL = mStringWSMainURL + "enablebeautyservice";
        Map<String, String> dataToSend = new HashMap<>();
        dataToSend.put("user_id", user_id);
        dataToSend.put("token", token);
        dataToSend.put("service_id", service_id);
        String encodedStr = getEncodedData(dataToSend);
        return callWebService(mStringURL, encodedStr, mObject);
    }

    /**
     * Method will call update my beauty service.
     *
     * @param user_id
     * @param token
     * @param category_id
     * @param name
     * @param description
     * @param avgtime
     * @param avgprice
     * @param service_id
     * @param mObject
     * @return
     */
    public Object callEditBeautyService(String user_id, String token, String category_id, String name,
                                        String description, String avgtime, String avgprice,
                                        String service_id, Object mObject) {
        String mStringURL = mStringWSMainURL + "updatebeautyservices";
        Map<String, String> dataToSend = new HashMap<>();
        dataToSend.put("user_id", user_id);
        dataToSend.put("token", token);
        dataToSend.put("category_id", category_id);
        dataToSend.put("name", name);
        dataToSend.put("description", description);
        dataToSend.put("avgtime", avgtime);
        dataToSend.put("avgprice", avgprice);
        dataToSend.put("service_id", service_id);
        String encodedStr = getEncodedData(dataToSend);
        return callWebService(mStringURL, encodedStr, mObject);
    }

    /**
     * Method will call delete service picture..
     *
     * @param user_id
     * @param token
     * @param picture_id
     * @param mObject
     * @return
     */
    public Object callDeleteMyServicePicture(String user_id, String token, String picture_id, Object mObject) {
        String mStringURL = mStringWSMainURL + "deletebeautyservicepicture";
        Map<String, String> dataToSend = new HashMap<>();
        dataToSend.put("user_id", user_id);
        dataToSend.put("token", token);
        dataToSend.put("picture_id", picture_id);
        String encodedStr = getEncodedData(dataToSend);
        return callWebService(mStringURL, encodedStr, mObject);
    }

    /**
     * Method will call edit beauty service picture..
     *
     * @param user_id
     * @param token
     * @param picture_id
     * @param image_path
     * @param mObject
     * @return
     */
    public Object callEditBeautyServicePicture(String user_id, String token, String picture_id, String type, String image_path, Object mObject) {
        String charset = "UTF-8";
        String mStringURL = mStringWSMainURL + "editbeautyservicepicture";
        Object mFillObject = null;
        try {

            mFillObject = mObject.getClass().newInstance();
            MultipartUtility multipart = new MultipartUtility(mStringURL, charset);
            multipart.addFormField("user_id", user_id);
            multipart.addFormField("token", token);
            multipart.addFormField("picture_id", picture_id);
            multipart.addFormField("type", type);
            File uploadFile1 = new File(image_path);
            multipart.addFilePart("image", uploadFile1);
            String response = multipart.finish();
            System.out.println("SERVER REPLIED: " + response);

            mFillObject = mGson.fromJson(response, mFillObject.getClass());

            return mFillObject;
        } catch (Exception e) {
            isError = true;
            e.printStackTrace();
        }

        return mFillObject;
    }


    /**
     * Method will call get provider list by category id..
     *
     * @param user_id
     * @param token
     * @param category_id
     * @param user_lat
     * @param user_lng
     * @param mObject
     * @return
     */
    public Object callGetProviderByCategory(String user_id, String token, String category_id, String user_lat, String user_lng, Object mObject) {
        String mStringURL = mStringWSMainURL + "getproviderbycategory";
        Map<String, String> dataToSend = new HashMap<>();
        dataToSend.put("user_id", user_id);
        dataToSend.put("token", token);
        dataToSend.put("category_id", category_id);
        dataToSend.put("user_lat", user_lat);
        dataToSend.put("user_lng", user_lng);
        String encodedStr = getEncodedData(dataToSend);
        return callWebService(mStringURL, encodedStr, mObject);
    }


    /**
     * Method will call get provider details..
     *
     * @param user_id
     * @param token
     * @param provider_id
     * @param mObject
     * @return
     */
    public Object callGetProviderDetails(String user_id, String token, String provider_id, Object mObject) {
        String mStringURL = mStringWSMainURL + "getproviderdetails";
        Map<String, String> dataToSend = new HashMap<>();
        dataToSend.put("user_id", user_id);
        dataToSend.put("token", token);
        dataToSend.put("provider_id", provider_id);
        String encodedStr = getEncodedData(dataToSend);
        return callWebService(mStringURL, encodedStr, mObject);
    }

    /**
     * Method will call check Available user...
     *
     * @param user_id
     * @param token
     * @param provider_id
     * @param starttime
     * @param appointment_services
     * @param location
     * @param lat
     * @param lng
     * @param location_policy
     * @param checktype
     * @param mObject
     * @return
     */
    public Object callCheckAvailabilityUser(String user_id, String token, String provider_id, String starttime, String appointment_services, String location, String lat, String lng, String location_policy, String checktype, Object mObject) {
        String mStringURL = mStringWSMainURL + "checkavailability";
        Map<String, String> dataToSend = new HashMap<>();
        dataToSend.put("user_id", user_id);
        dataToSend.put("token", token);
        dataToSend.put("provider_id", provider_id);
        dataToSend.put("starttime", starttime);
        dataToSend.put("appointment_services", appointment_services);
        dataToSend.put("location", location);
        dataToSend.put("lat", lat);
        dataToSend.put("lng", lng);
        dataToSend.put("location_policy", location_policy);
        dataToSend.put("checktype", checktype);



        String encodedStr = getEncodedData(dataToSend);
        return callWebService(mStringURL, encodedStr, mObject);
    }

    /**
     * Method will call getAppointment Web API
     * @param user_id
     * @param token
     * @param month
     * @param mObject
     * @return API response
     */
    public Object callGetAppointment(String user_id, String token, String month, Object mObject) {
        String mStringURL = mStringWSMainURL + "listappointmentmonthly";
        Map<String, String> dataToSend = new HashMap<>();
        dataToSend.put("user_id", user_id);
        dataToSend.put("token", token);
        dataToSend.put("month", month);
        String encodedStr = getEncodedData(dataToSend);
        return callWebService(mStringURL, encodedStr, mObject);
    }

    /**
     * Method will call appointment details Web API
     * @param user_id
     * @param token
     * @param app_id
     * @param mObject
     * @return API response
     */
    public Object callGetAppointmentByID(String user_id, String token, String app_id, Object mObject) {
        String mStringURL = mStringWSMainURL + "appointmentdetails";
        Map<String, String> dataToSend = new HashMap<>();
        dataToSend.put("user_id", user_id);
        dataToSend.put("token", token);
        dataToSend.put("appointment_id", app_id);
        String encodedStr = getEncodedData(dataToSend);
        return callWebService(mStringURL, encodedStr, mObject);
    }

    /**
     * Method will call add comments of user..
     *
     * @param user_id
     * @param token
     * @param appointment_id
     * @param to_user
     * @param catids
     * @param serviceids
     * @param answer
     * @param comment
     * @param mObject
     * @return
     */
    public Object callAddCommentUser(String user_id, String token, String appointment_id, String to_user, String catids, String serviceids, String answer, String comment, Object mObject) {
        String mStringURL = mStringWSMainURL + "addcomment";
        Map<String, String> dataToSend = new HashMap<>();
        dataToSend.put("user_id", user_id);
        dataToSend.put("token", token);
        dataToSend.put("appointment_id", appointment_id);
        dataToSend.put("to_user", to_user);
        dataToSend.put("catids", catids);
        dataToSend.put("serviceids", serviceids);
        dataToSend.put("answer", answer);
        dataToSend.put("comment", comment);
        String encodedStr = getEncodedData(dataToSend);
        return callWebService(mStringURL, encodedStr, mObject);
    }


    /**
     * Method will call cancel appointment web API
     *
     * @param user_id
     * @param token
     * @param appointment_id
     * @param mObject
     * @return Web API Response
     */
    public Object callCancelAppointment(String user_id, String token, String appointment_id, Object mObject) {
        String mStringURL = mStringWSMainURL + "cancelappointment";
        Map<String, String> dataToSend = new HashMap<>();
        dataToSend.put("user_id", user_id);
        dataToSend.put("token", token);
        dataToSend.put("appointment_id", appointment_id);
        String encodedStr = getEncodedData(dataToSend);
        return callWebService(mStringURL, encodedStr, mObject);
    }

    /**
     * Method will call confirm show up appoinment.
     *
     * @param user_id
     * @param token
     * @param appointment_id
     * @param lat1
     * @param lng1
     * @param lat2
     * @param lng2
     * @param mObject
     * @return
     */
    public Object callConfirmShowUp(String user_id, String token, String appointment_id, String lat1, String lng1, String lat2, String lng2, Object mObject) {
        String mStringURL = mStringWSMainURL + "updateappointmentstatus";
        Map<String, String> dataToSend = new HashMap<>();
        dataToSend.put("user_id", user_id);
        dataToSend.put("token", token);
        dataToSend.put("appointment_id", appointment_id);
        dataToSend.put("lat1", lat1);
        dataToSend.put("lng1", lng1);
        dataToSend.put("lat2", lat2);
        dataToSend.put("lng2", lng2);
        String encodedStr = getEncodedData(dataToSend);
        return callWebService(mStringURL, encodedStr, mObject);
    }


    /**
     * Method will call getSuggestedProvider Web API
     * @param user_id
     * @param token
     * @param user_lat
     * @param user_lng
     * @param mObject
     * @return Web API Response
     */
    public Object callGetFeatureSearch(String user_id, String token, String user_lat, String user_lng, Object mObject) {
        String mStringURL = mStringWSMainURL + "getsuggestedprovidersearch";
        Map<String, String> dataToSend = new HashMap<>();
        dataToSend.put("user_id", user_id);
        dataToSend.put("token", token);
        dataToSend.put("user_lat", user_lat);
        dataToSend.put("user_lng", user_lng);

        String encodedStr = getEncodedData(dataToSend);
        return callWebService(mStringURL, encodedStr, mObject);
    }

    /**
     * Method will call search provider web API
     *
     * @param user_id
     * @param token
     * @param name
     * @param mObject
     * @return API Response
     */
    public Object callSearchByName(String user_id, String token, String name, String user_lat, String user_lng, Object mObject) {
        String mStringURL = mStringWSMainURL + "searchbyprovidername";
        Map<String, String> dataToSend = new HashMap<>();
        dataToSend.put("user_id", user_id);
        dataToSend.put("token", token);
        dataToSend.put("name", name);
        dataToSend.put("user_lat", user_lat);
        dataToSend.put("user_lng", user_lng);

        String encodedStr = getEncodedData(dataToSend);
        return callWebService(mStringURL, encodedStr, mObject);
    }

    /**
     * Method will call Advance searching..
     *
     * @param user_id
     * @param token
     * @param userorservicename
     * @param category_id
     * @param city
     * @param location_policy
     * @param minprice
     * @param maxprice
     * @param satisfyrating
     * @param commitedrating
     * @param startdatetime
     * @param user_lat
     * @param user_lng
     * @param mObject
     * @return
     */
    public Object callAdvanceSearch(String user_id, String token, String userorservicename, String category_id,
                                    String city, String country, String distance,
                                    String location_policy, String minprice, String maxprice, String satisfyrating,
                                    String commitedrating, String startdatetime, String user_lat, String user_lng, Object mObject) {
        String mStringURL = mStringWSMainURL + "advancesearch";
        Map<String, String> dataToSend = new HashMap<>();
        dataToSend.put("user_id", user_id);
        dataToSend.put("token", token);
        dataToSend.put("userorservicename", userorservicename);
        dataToSend.put("category_id", category_id);
        dataToSend.put("city", city);
        dataToSend.put("country", country);
        dataToSend.put("distance", distance);
        dataToSend.put("location_policy", location_policy);
        dataToSend.put("minprice", minprice);
        dataToSend.put("maxprice", maxprice);
        dataToSend.put("satisfyrating", satisfyrating);
        dataToSend.put("commitedrating", commitedrating);
        dataToSend.put("startdatetime", startdatetime);
        dataToSend.put("user_lat", user_lat);
        dataToSend.put("user_lng", user_lng);

        String encodedStr = getEncodedData(dataToSend);
        return callWebService(mStringURL, encodedStr, mObject);
    }

    /**
     * Method will call get Comments provider web API
     *
     * @param user_id
     * @param token
     * @param service_id
     * @param mObject
     * @return API Response
     */
    public Object callGetComments(String user_id, String token, String service_id, Object mObject) {
        String mStringURL = mStringWSMainURL + "allcommentlisting";
        Map<String, String> dataToSend = new HashMap<>();
        dataToSend.put("user_id", user_id);
        dataToSend.put("token", token);
        dataToSend.put("service_id", service_id);
        String encodedStr = getEncodedData(dataToSend);
        return callWebService(mStringURL, encodedStr, mObject);
    }

    /**
     * Method will call Check Expire Date..
     *
     * @param mObject
     * @return
     */
    public Object callExpireDate(Object mObject) {
        String mStringURL = "http://nexuslinkservices.com/webapi/index.php";
        Map<String, String> dataToSend = new HashMap<>();
        dataToSend.put("app", "beautician");
        String encodedStr = getEncodedData(dataToSend);
        return callWebService(mStringURL, encodedStr, mObject);
    }


    /**
     * Method will call buy feature ads..
     * @param user_id
     * @param token
     * @param type
     * @param startdate
     * @param enddate
     * @param mObject
     * @return
     */
    public Object callBuyFeatureAds(String user_id, String token, String cat_ids, String type, String startdate, String enddate, Object mObject) {
        String mStringURL = mStringWSMainURL + "buyfeaturedads";
        Map<String, String> dataToSend = new HashMap<>();
        dataToSend.put("user_id", user_id);
        dataToSend.put("token", token);
        dataToSend.put("categoryids", cat_ids);
        dataToSend.put("type", type);
        dataToSend.put("startdate", startdate);
        dataToSend.put("enddate", enddate);
        String encodedStr = getEncodedData(dataToSend);
        return callWebService(mStringURL, encodedStr, mObject);
    }

    /**
     * Method will call report Web API
     * @param user_id
     * @param token
     * @param to_user
     * @param type
     * @param contenttype
     * @param contentid
     * @param comment
     * @param mObject
     * @return Web API Response
     */
    public Object callReport(String user_id, String token, String to_user,  String type, String contenttype,
                             String contentid, String comment, Object mObject) {
        String mStringURL = mStringWSMainURL + "abusecontent";
        Map<String, String> dataToSend = new HashMap<>();
        dataToSend.put("user_id", user_id);
        dataToSend.put("token", token);
        dataToSend.put("type", type);
        dataToSend.put("to_user", to_user);
        dataToSend.put("contenttype", contenttype);
        dataToSend.put("contentid", contentid);
        dataToSend.put("comment", comment);
        String encodedStr = getEncodedData(dataToSend);
        return callWebService(mStringURL, encodedStr, mObject);
    }


    /**
     * Method will call get payment info Web API
     * @param user_id
     * @param token
     * @param appointment_id
     * @param totalamt
     * @param mObject
     * @return Web API Response
     */
    public Object callGetPaymentInfoForAppointment(String user_id, String token, String appointment_id,  String totalamt,  Object mObject) {
        String mStringURL = mStringWSMainURL + "getpaymentinfobookappointment";
        Map<String, String> dataToSend = new HashMap<>();
        dataToSend.put("user_id", user_id);
        dataToSend.put("token", token);
        dataToSend.put("appointment_id", appointment_id);
        dataToSend.put("totalamt", totalamt);
        String encodedStr = getEncodedData(dataToSend);
        return callWebService(mStringURL, encodedStr, mObject);
    }

    /**
     * Method will call Get Country Web API
     * @param mObject
     * @return Web API Response
     */
    public Object callGetCountry(Object mObject) {
        String mStringURL = mStringWSMainURL + "getcountry";
        Map<String, String> dataToSend = new HashMap<>();
        String encodedStr = getEncodedData(dataToSend);
        return callWebService(mStringURL, encodedStr, mObject);
    }

    /**
     * Method will call Get City Web API
     * @param country_id
     * @param lat
     * @param lon
     * @param mObject
     * @return Web API Response
     */
    public Object callGetCity(String country_id, String lat, String lon, Object mObject) {
        String mStringURL = mStringWSMainURL + "getcity";
        Map<String, String> dataToSend = new HashMap<>();
        dataToSend.put("country_id", country_id);
        dataToSend.put("lat", lat);
        dataToSend.put("lng", lon);
        String encodedStr = getEncodedData(dataToSend);
        return callWebService(mStringURL, encodedStr, mObject);
    }

    /**
     * Method will call update device token Web API
     * @param userid
     * @param device_token
     * @param mObject
     * @return Web API Response.
     */
    public Object callUpdateDeviceToken(String userid, String token, String device_token, Object mObject) {
        String mStringURL = mStringWSMainURL + "updatedevicetoken";
        Map<String, String> dataToSend = new HashMap<>();
        dataToSend.put("user_id", userid);
        dataToSend.put("token", token);
        dataToSend.put("device_token", device_token);
        dataToSend.put("device", "android");

        String encodedStr = getEncodedData(dataToSend);

        return callWebService(mStringURL, encodedStr, mObject);
    }

    /**
     * Method will call check version API
     * @param userid
     * @param token
     * @param app_version
     * @param mObject
     * @return
     */
    public Object callCheckVersion(String userid, String token, String app_version, Object mObject) {
        String mStringURL = mStringWSMainURL + "checkversion";
        Map<String, String> dataToSend = new HashMap<>();
        dataToSend.put("user_id", userid);
        dataToSend.put("token", token);
        dataToSend.put("app_version", app_version);
        dataToSend.put("device", "android");

        String encodedStr = getEncodedData(dataToSend);

        return callWebService(mStringURL, encodedStr, mObject);
    }
}

