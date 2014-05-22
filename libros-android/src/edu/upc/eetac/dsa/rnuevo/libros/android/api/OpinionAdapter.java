package edu.upc.eetac.dsa.rnuevo.libros.android.api;


import java.text.SimpleDateFormat;
import java.util.ArrayList;

import edu.upc.eetac.dsa.rnuevo.libros.android.R;
import edu.upc.eetac.dsa.rnuevo.libros.android.api.Opinion;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class OpinionAdapter extends BaseAdapter {
	private ArrayList<Opinion> data;
	LayoutInflater inflater;

	public OpinionAdapter(Context context, ArrayList<Opinion> data) {
		super();
		inflater = LayoutInflater.from(context);
		this.data = data;
	}

	@Override
	public int getCount() {
		return data.size();
	}

	@Override
	public Object getItem(int position) {
		return data.get(position);
	}

	@Override
	public long getItemId(int position) {
		return Long.parseLong(((Opinion) getItem(position)).getId());
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder = null;
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.list_row_opinion, null);
			viewHolder = new ViewHolder();
			viewHolder.tvSubject = (TextView) convertView
					.findViewById(R.id.tvSubject);
			viewHolder.tvUsername = (TextView) convertView
					.findViewById(R.id.tvUsername);
			viewHolder.tvDate = (TextView) convertView
					.findViewById(R.id.tvDate);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		String content = data.get(position).getContenido();
		String autor = data.get(position).getUsername();
		String date = SimpleDateFormat.getInstance().format(
				data.get(position).getLastModified());
		viewHolder.tvSubject.setText(content);
		viewHolder.tvUsername.setText(autor);
		viewHolder.tvDate.setText(date);
		return convertView;
	}

	private static class ViewHolder {
		TextView tvSubject;
		TextView tvUsername;
		TextView tvDate;
	}

}
