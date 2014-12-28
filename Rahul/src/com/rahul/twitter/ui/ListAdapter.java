package com.rahul.twitter.ui;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.rahul.twitter.R;
import com.rahul.twitter.comman.TweetItems;
import com.rahul.twitter.net.VolleySingleton;

public class ListAdapter extends ArrayAdapter<TweetItems> {
	private ArrayList<TweetItems> mItem;
	private ImageLoader mImageLoader;
	private Context mContext;

	public ListAdapter(Context context, int resource, int textViewResourceId,
			ArrayList<TweetItems> objects) {
		super(context, resource, textViewResourceId, objects);
		mItem = objects;
		mContext = context;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder;
		if (convertView == null) {
			LayoutInflater layoutInflater = LayoutInflater.from(mContext);
			convertView = layoutInflater.inflate(R.layout.twit_list_items,
					parent, false);
			viewHolder = new ViewHolder();
			viewHolder.mUerProfilePic = (NetworkImageView) convertView
					.findViewById(R.id.networkImageView);
			viewHolder.mUserNameTextview = (TextView) convertView
					.findViewById(R.id.user_name);
			viewHolder.mDateTextview = (TextView) convertView
					.findViewById(R.id.date);
			viewHolder.mDescripTextview = (TextView) convertView
					.findViewById(R.id.text);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		viewHolder.mUserNameTextview.setText(mItem.get(position).mUserName);
		viewHolder.mDateTextview.setText(mItem.get(position).mDate);
		viewHolder.mDescripTextview.setText(mItem.get(position).mText);
		mImageLoader = VolleySingleton.getInstance(getContext())
				.getImageLoader();
		viewHolder.mUerProfilePic.setImageUrl(
				mItem.get(position).mUserProfilePicUrl, mImageLoader);
		return convertView;
	}

	private static class ViewHolder {
		private NetworkImageView mUerProfilePic;
		private TextView mUserNameTextview;
		private TextView mDateTextview;
		private TextView mDescripTextview;
	}
}
