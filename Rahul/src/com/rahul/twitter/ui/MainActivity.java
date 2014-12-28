package com.rahul.twitter.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.rahul.twitter.R;
import com.rahul.twitter.comman.Constants;

public class MainActivity extends Activity {
	private static final String TAG = "MainActivity";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
	}

	public void search(View v) {
		String searchWord;
		EditText searchEditText;
		searchEditText = (EditText) findViewById(R.id.search_text);
		searchWord = searchEditText.getText().toString();
		if (searchWord.length() > 0) {
			Log.d(TAG, "length" + searchWord.length());
			Intent intent = new Intent(this, SrearchReasultActivity.class);
			intent.putExtra(Constants.WORD_KEY, searchWord);
			startActivity(intent);
		} else {
			Toast.makeText(this, R.string.enter_search_word, Toast.LENGTH_LONG)
					.show();
		}
	}
}
