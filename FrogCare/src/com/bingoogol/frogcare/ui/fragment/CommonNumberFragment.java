package com.bingoogol.frogcare.ui.fragment;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.TextView;

import com.bingoogol.frogcare.R;
import com.bingoogol.frogcare.db.dao.CommonNumberDao;
import com.bingoogol.frogcare.ui.AdvanceToolActivity;
import com.bingoogol.frogcare.util.Logger;

public class CommonNumberFragment extends Fragment implements OnClickListener {
	private static final String TAG = "CommonNumberFragment";
	private AdvanceToolCallback mAdvanceToolCallback;
	private ExpandableListView elv_number;

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			mAdvanceToolCallback = (AdvanceToolCallback) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString() + " must implement AdvanceToolCallback");
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_common_number, container, false);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		getView().findViewById(R.id.btn_back).setOnClickListener(this);
		elv_number = (ExpandableListView) getView().findViewById(R.id.elv_number);
		elv_number.setAdapter(new ExpandAdapter());
		elv_number.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {

			@Override
			public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
				TextView tv = (TextView) v;
				String number = tv.getText().toString().trim().split("\n")[1];
				Logger.i(TAG, number);
				Intent intent = new Intent();
				intent.setAction(Intent.ACTION_DIAL);
				intent.setData(Uri.parse("tel:" + number));
				startActivity(intent);
				return false;
			}
		});
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.btn_back) {
			mAdvanceToolCallback.changeFragment(AdvanceToolActivity.ADVANCE_TOOL);
		}
	}
	
	private class ExpandAdapter extends BaseExpandableListAdapter {
		private List<String> groupNames;
		private Map<Integer, List<String>> childrenInfos = new HashMap<Integer, List<String>>();
		
		@Override
        public void onGroupExpanded(int groupPosition) {
            elv_number.setSelection(groupPosition);
            super.onGroupExpanded(groupPosition);
        }


		/**
		 * 返回有多个组
		 */
		@Override
		public int getGroupCount() {
			groupNames = CommonNumberDao.getGroupNames(getActivity());
			return groupNames.size();
		}

		/**
		 * 返回每个组有多少个孩子
		 */
		@Override
		public int getChildrenCount(int groupPosition) {
			List<String> childNames = null;
			// 集合里面已经有缓存数据了.
			if (childrenInfos.containsKey(groupPosition)) {
				childNames = childrenInfos.get(groupPosition);
			} else {
				// 集合里面还没有缓存数据 ,获取数据放在集合里面
				childNames = CommonNumberDao.getChildrenNamesByPosition(getActivity(), groupPosition);
				childrenInfos.put(groupPosition, childNames);
			}
			return childNames.size();
		}

		/**
		 * 返回某个位置组对应的对象
		 */
		@Override
		public Object getGroup(int groupPosition) {
			return null;
		}

		/**
		 * 返回某个组里面某个孩子的对象
		 */
		@Override
		public Object getChild(int groupPosition, int childPosition) {
			return null;
		}

		/**
		 * 获取分组的id
		 */
		@Override
		public long getGroupId(int groupPosition) {
			return groupPosition;
		}

		/**
		 * 获取孩子的id
		 */

		@Override
		public long getChildId(int groupPosition, int childPosition) {
			return childPosition;
		}

		@Override
		public boolean hasStableIds() {
			return false;
		}

		/**
		 * 返回某个位置 分组对应的view对象
		 */
		@Override
		public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
			TextView view;
			if (convertView != null) {
				view = (TextView) convertView;
			} else {
				view = new TextView(getActivity());
				view.setTextColor(Color.GREEN);
				int padding = (int) getResources().getDimension(R.dimen.size_normal);
				view.setPadding(padding, padding, padding, padding);
				view.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.textSize_middle));
			}
			// tv.setText("        "+CommonNumDao.getGroupNameByPosition(groupPosition));
			// 查询数据库
			// 查询内存
			view.setText("        " + groupNames.get(groupPosition));
			return view;
		}

		/**
		 * 返回某个位置的孩子信息
		 */
		@Override
		public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
			TextView view;
			if (convertView != null) {
				view = (TextView) convertView;
			} else {
				view = new TextView(getActivity());
				view.setTextColor(Color.WHITE);
				int padding = (int) getResources().getDimension(R.dimen.size_normal);
				view.setPadding(padding, padding, padding, padding);
				view.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.textSize_small));
			}
			view.setText(CommonNumberDao.getChildNameByPosition(getActivity(), groupPosition, childPosition));
			return view;
		}

		@Override
		public boolean isChildSelectable(int groupPosition, int childPosition) {
			return true;
		}

	}
}
