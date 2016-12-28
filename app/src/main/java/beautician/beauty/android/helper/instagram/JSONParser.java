package beautician.beauty.android.helper.instagram;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;

public class JSONParser {

	static InputStream is = null;
	static JSONObject jObj = null;
	static String json = "";

	// constructor
	public JSONParser() {

	}

//	public JSONObject getJSONFromUrlByPost(String url,
//			List<NameValuePair> nameValuePairs) {
//
//		// Making HTTP request
//		try {
//			// defaultHttpClient
//			DefaultHttpClient httpClient = new DefaultHttpClient();
//			HttpPost httpPost = new HttpPost(url);
//			httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
//			HttpResponse httpResponse = httpClient.execute(httpPost);
//			HttpEntity httpEntity = httpResponse.getEntity();
//			is = httpEntity.getContent();
//
//		} catch (UnsupportedEncodingException e) {
//			e.printStackTrace();
//		} catch (ClientProtocolException e) {
//			e.printStackTrace();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//
//		try {
//			BufferedReader reader = new BufferedReader(new InputStreamReader(
//					is, "iso-8859-1"), 8);
//			StringBuilder sb = new StringBuilder();
//			String line = null;
//			while ((line = reader.readLine()) != null) {
//				sb.append(line + "\n");
//			}
//			is.close();
//			json = sb.toString();
//		} catch (Exception e) {
//			Log.e("Buffer Error", "Error converting result " + e.toString());
//		}
//
//		// try parse the string to a JSON object
//		try {
//			jObj = new JSONObject(json);
//		} catch (JSONException e) {
//			Log.e("JSON Parser", "Error parsing data " + e.toString());
//		}
//
//		// return JSON String
//		return jObj;
//
//	}

	public JSONObject getJSONFromUrlByGet(String mStringUrl) {

		// Making HTTP request
		try {
			// defaultHttpClient
//			DefaultHttpClient httpClient = new DefaultHttpClient();
//			HttpGet httpGet = new HttpGet(url);
//
//			HttpResponse httpResponse = httpClient.execute(httpGet);
//			HttpEntity httpEntity = httpResponse.getEntity();
//			is = httpEntity.getContent();

			URL url = new URL(mStringUrl);
			HttpURLConnection con = (HttpURLConnection) url.openConnection();
			con.setRequestMethod("GET");

			StringBuilder sb = new StringBuilder();
			BufferedReader reader = new BufferedReader(new InputStreamReader(con.getInputStream()));

			String line;
			while ((line = reader.readLine()) != null) {
				sb.append(line + "\n");
			}
			line = sb.toString();
			json = sb.toString();

		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}  catch (IOException e) {
			e.printStackTrace();
		}

//		try {
//			BufferedReader reader = new BufferedReader(new InputStreamReader(
//					is, "iso-8859-1"), 8);
//			StringBuilder sb = new StringBuilder();
//			String line = null;
//			while ((line = reader.readLine()) != null) {
//				sb.append(line + "\n");
//			}
//			is.close();
//			json = sb.toString();
//		} catch (Exception e) {
//			Log.e("Buffer Error", "Error converting result " + e.toString());
//		}

		// try parse the string to a JSON object
		try {
			jObj = new JSONObject(json);
		} catch (JSONException e) {
			Log.e("JSON Parser", "Error parsing data " + e.toString());
		}

		// return JSON String
		return jObj;

	}
}
