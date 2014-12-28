package com.rahul.twitter.ui;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
import android.widget.Toast;

import com.rahul.twitter.R;
import com.rahul.twitter.comman.Constants;
import com.rahul.twitter.comman.TweetItems;
import com.rahul.twitter.net.DownloadTwitterData;
import com.rahul.twitter.net.DownloadTwitterData.OnResultListener;

public class SrearchReasultActivity extends Activity implements
		OnResultListener {

	private static final String TAG = "SearchActitvity";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.twit_list);
		String word = getIntent().getExtras().getString(Constants.WORD_KEY);
		Log.i(TAG, word);
		downloadTweets(word);
	}

	// download twitter timeline after first checking to see if there is a
	// network connection
	public void downloadTweets(String word) {
		ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

		if (networkInfo != null && networkInfo.isConnected()) {
			new DownloadTwitterData(this).execute(word);
		} else {
			Log.d(TAG, "No intenet connection");
			Toast.makeText(this, R.string.check_netwok, Toast.LENGTH_LONG)
					.show();
		}
	}

	@Override
	public void onResultComplete(ArrayList<TweetItems> items) {
		if (items != null) {
			ListAdapter adapter = new ListAdapter(SrearchReasultActivity.this,
					0, 0, items);
			ListView twittList = (ListView) findViewById(R.id.twit_list);
			twittList.setAdapter(adapter);
		} else {
			Toast.makeText(this, R.string.no_data_available, Toast.LENGTH_LONG)
					.show();
		}
	}
}