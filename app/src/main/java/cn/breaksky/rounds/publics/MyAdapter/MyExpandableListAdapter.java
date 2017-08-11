package cn.breaksky.rounds.publics.MyAdapter;


import java.util.List;

import cn.breaksky.rounds.publics.R;
import cn.breaksky.rounds.publics.bean.RoundGroup;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

public class MyExpandableListAdapter extends BaseExpandableListAdapter {
	private Context context;
	private List<String> groupids;
	private List<List<GroupUser>> child;
	private List<RoundGroup> allgroup;

	public MyExpandableListAdapter(Context context, List<String> groupids,
			List<List<GroupUser>> child,List<RoundGroup> allgroup) {
		this.context = context;
		this.groupids = groupids;
		this.child = child;
		this. allgroup= allgroup;
	}

	@Override
	public int getGroupCount() {
		return groupids.size();
	}

	@Override
	public int getChildrenCount(int groupPosition) {
		return child.get(groupPosition).size();
	}

	@Override
	public Object getGroup(int groupPosition) {
		return groupids.get(groupPosition);
	}

	@Override
	public Object getChild(int groupPosition, int childPosition) {
		return child.get(groupPosition).get(childPosition);
	}

	@Override
	public long getGroupId(int groupPosition) {
		return groupPosition;
	}
 
	@Override
	public long getChildId(int groupPosition, int childPosition) {
		return childPosition;
	}

	@Override
	public boolean hasStableIds() {
		return false;
	}
	
	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		return true;
	}
	
	
	@Override
	public View getGroupView(int groupPosition, boolean isExpanded,
			View convertView, ViewGroup parent) {
		ViewHolder holder;
		if (convertView == null) {
			convertView = LayoutInflater.from(context).inflate(
					R.layout.item_list, null);
			holder = new ViewHolder();
			holder.textView = (TextView) convertView
					.findViewById(R.id.txt_name);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		
		String groupid=groupids.get(groupPosition);
		String groupname=groupid;
		for(int m=0;m<allgroup.size();m++)
		{
			String destid=allgroup.get(m).getGroupid().toString();
			if(destid.equalsIgnoreCase(groupid))
			{
				groupname=allgroup.get(m).getGroupname();
				break;
			}
		}
		
		
		holder.textView.setText(groupname);
		holder.textView.setTextSize(20);
		holder.textView.setPadding(36, 10, 0, 10);
		return convertView;

	}
	
	
	@Override
	public View getChildView(int groupPosition, int childPosition,
			boolean isLastChild, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if (convertView == null) {
			convertView = LayoutInflater.from(context).inflate(
					R.layout.item_list, null);
			holder = new ViewHolder();
			holder.textView = (TextView) convertView
					.findViewById(R.id.txt_name);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		GroupUser g=child.get(groupPosition).get(childPosition);
		holder.textView.setText(g.getDispname());
		holder.textView.setTextSize(15);
		holder.textView.setPadding(72, 10, 0, 10);
		return convertView;
	}

	class ViewHolder {
		TextView textView;
	}

	

}