package edu.upc.eetac.dsa.rnuevo.libros.android.api;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

import edu.upc.eetac.dsa.rnuevo.libros.android.R;
import edu.upc.eetac.dsa.rnuevo.libros.android.api.Libro;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class LibroAdapter extends BaseAdapter {
	private ArrayList<Libro> data;
	LayoutInflater inflater;

	public LibroAdapter(Context context, ArrayList<Libro> data) {
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
		System.out.println("llego al parse looooong");
		return Long.parseLong(((Libro) getItem(position)).getId());
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder = null;
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.list_row_libro, null);
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
		String titulo = data.get(position).getTitulo();
		String autor = data.get(position).getAutor();
		String date = SimpleDateFormat.getInstance().format(
				data.get(position).getLastModified());
		viewHolder.tvSubject.setText(titulo);
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
