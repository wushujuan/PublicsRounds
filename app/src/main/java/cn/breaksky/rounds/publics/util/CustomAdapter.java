package cn.breaksky.rounds.publics.util;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import cn.breaksky.rounds.publics.R;

public class CustomAdapter extends ArrayAdapter<CItem> {
	private Context context;

	// private int resource;

	public CustomAdapter(Context context, int textViewResourceId, List<CItem> item) {
		super(context, textViewResourceId, item);
		this.context = context;
	}

	public View getView1(int position, View convertView, ViewGroup parent) {
		CItem data = super.getItem(position);
		View view = super.getView(position, convertView, parent);
		if (data.getNoRead() != 0) {
			view.setBackgroundColor(this.context.getResources().getColor(R.color.red));
		}
		return view;
	}
}
