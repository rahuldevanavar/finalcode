package com.rahul.twitter.net;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.ParseException;
import java.util.ArrayList;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;
import android.util.Base64;
import android.util.Log;

import com.google.gson.Gson;
import com.rahul.twitter.comman.CalTime;
import com.rahul.twitter.comman.Constants;
import com.rahul.twitter.comman.TweetItems;

// Uses an AsyncTask to download a Twitter twitter search data
public class DownloadTwitterData extends AsyncTask<String, Void, String> {

	private static final String TAG = "DownloadTwitterData";
	private OnResultListener mListener;

	public DownloadTwitterData(OnResultListener listener) {
		mListener = listener;
	}

	@Override
	protected String doInBackground(String... searchWord) {
		String result = null;
		if (searchWord.length > 0) {
			result = getTwitterStream(searchWord[0]);
		}
		return result;
	}

	// onPostExecute convert the JSON results into a Twitter object (which
	// is an Array list of tweets
	@Override
	protected void onPostExecute(String result) {
		ArrayList<TweetItems> items;
		items = getParsedData(result);
		mListener.onResultComplete(items);
	}

	private ArrayList<TweetItems> getParsedData(String result) {
		ArrayList<TweetItems> items = new ArrayList<TweetItems>();
		try {
			JSONObject mainJson = new JSONObject(result);
			JSONArray jsonArray = mainJson.getJSONArray(Constants.STATUS_TAG);
			Log.d("TAG", "Array length" + jsonArray.length());
			for (int i = 0; i < jsonArray.length(); i++) {
				JSONObject objJson = jsonArray.getJSONObject(i);
				TweetItems tweetItems = new TweetItems();
				String date = objJson.getString(Constants.CREATED_AT_TAG);
				tweetItems.mDate = new CalTime().getTime(date);
				tweetItems.mText = objJson.getString(Constants.TEXT_TAG);
				tweetItems.mUserName = objJson
						.getJSONObject(Constants.USER_TAG).getString(
								Constants.NAME_TAG);
				tweetItems.mUserProfilePicUrl = objJson.getJSONObject(
						Constants.USER_TAG).getString(Constants.IMAGE_URL);
				Log.i(TAG, "Date  " + tweetItems.mDate);
				Log.i(TAG, "Text  " + tweetItems.mText);
				Log.i(TAG, "User " + tweetItems.mUserProfilePicUrl);
				items.add(tweetItems);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return items;
	}

	// convert a JSON authentication object into an Authenticated object
	private Authenticated jsonToAuthenticated(String rawAuthorization) {
		Authenticated auth = null;
		if (rawAuthorization != null && rawAuthorization.length() > 0) {
			try {
				Gson gson = new Gson();
				auth = gson.fromJson(rawAuthorization, Authenticated.class);
				Log.d("TAG", "" + auth.token_type);
			} catch (IllegalStateException ex) {
			}
		}
		return auth;
	}

	private String getResponseBody(HttpRequestBase request) {
		StringBuilder sb = new StringBuilder();
		try {

			DefaultHttpClient httpClient = new DefaultHttpClient(
					new BasicHttpParams());
			HttpResponse response = httpClient.execute(request);
			int statusCode = response.getStatusLine().getStatusCode();
			String reason = response.getStatusLine().getReasonPhrase();
			Log.d("TAG", "resone" + reason);
			if (statusCode == 200) {

				HttpEntity entity = response.getEntity();
				InputStream inputStream = entity.getContent();

				BufferedReader bReader = new BufferedReader(
						new InputStreamReader(inputStream, "UTF-8"), 8);
				String line = null;
				while ((line = bReader.readLine()) != null) {
					sb.append(line);
				}
			} else {
				sb.append(reason);
			}
		} catch (UnsupportedEncodingException ex) {
			Log.d(TAG, ex.getMessage());
		} catch (ClientProtocolException ex) {
			Log.d(TAG, ex.getMessage());
		} catch (IOException ex) {
			Log.d(TAG, ex.getMessage());
		}
		return sb.toString();
	}

	private String getTwitterStream(String screenName) {
		String results = null;

		// Step 1: Encode consumer key and secret
		try {
			// URL encode the consumer key and secret
			String urlApiKey = URLEncoder.encode(Constants.CONSUMER_KEY,
					"UTF-8");
			String urlApiSecret = URLEncoder.encode(Constants.CONSUMER_SECRET,
					"UTF-8");

			// Concatenate the encoded consumer key, a colon character, and
			// the
			// encoded consumer secret
			String combined = urlApiKey + ":" + urlApiSecret;

			// Base64 encode the string
			String base64Encoded = Base64.encodeToString(combined.getBytes(),
					Base64.NO_WRAP);

			// Step 2: Obtain a bearer token
			HttpPost httpPost = new HttpPost(Constants.TwitterTokenURL);
			httpPost.setHeader("Authorization", "Basic " + base64Encoded);
			httpPost.setHeader("Content-Type",
					"application/x-www-form-urlencoded;charset=UTF-8");
			httpPost.setEntity(new StringEntity("grant_type=client_credentials"));
			Log.d("TAG", "uri " + httpPost.getURI());
			String rawAuthorization = getResponseBody(httpPost);
			Authenticated auth = jsonToAuthenticated(rawAuthorization);

			// Applications should verify that the value associated with the
			// token_type key of the returned object is bearer
			if (auth != null && auth.token_type.equals("bearer")) {

				// Step 3: Authenticate API requests with bearer token
				HttpGet httpGet = new HttpGet(Constants.TwitterStreamURL
						+ URLEncoder.encode(screenName)
						+ "&result_type=recent&count=30");

				// construct a normal HTTPS request and include an
				// Authorization
				// header with the value of Bearer <>
				httpGet.setHeader("Authorization", "Bearer "
						+ auth.access_token);
				httpGet.setHeader("Content-Type", "application/json");
				// update the results with the body of the response
				results = getResponseBody(httpGet);
			}
		} catch (UnsupportedEncodingException ex) {
			Log.d(TAG, ex.getMessage());
		} catch (IllegalStateException ex) {
			Log.d(TAG, ex.getMessage());
		}
		return results;
	}

	public interface OnResultListener {
		void onResultComplete(ArrayList<TweetItems> items);
	}
}