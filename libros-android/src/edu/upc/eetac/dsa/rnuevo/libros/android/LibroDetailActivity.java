package edu.upc.eetac.dsa.rnuevo.libros.android;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

import edu.upc.eetac.dsa.rnuevo.libros.android.api.Libro;
import edu.upc.eetac.dsa.rnuevo.libros.android.api.LibroAdapter;
import edu.upc.eetac.dsa.rnuevo.libros.android.api.LibroCollection;
import edu.upc.eetac.dsa.rnuevo.libros.android.api.LibrosAPI;
import edu.upc.eetac.dsa.rnuevo.libros.android.api.LibrosAndroidException;
import edu.upc.eetac.dsa.rnuevo.libros.android.api.Opinion;
import edu.upc.eetac.dsa.rnuevo.libros.android.api.OpinionAPI;
import edu.upc.eetac.dsa.rnuevo.libros.android.api.OpinionAdapter;
import edu.upc.eetac.dsa.rnuevo.libros.android.api.OpinionCollection;
import android.app.Activity;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

public class LibroDetailActivity extends ListActivity {
	private final static String TAG = LibroDetailActivity.class.getName();
	ArrayList<Opinion> opinionList;
	OpinionAdapter adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.libro_detail_layout);
		String urlLibro = (String) getIntent().getExtras().get("url");
		opinionList = new ArrayList<>();
		adapter = new OpinionAdapter(this, opinionList);
		setListAdapter(adapter);
		(new FetchStingTask()).execute(urlLibro);
	}

	private void loadLibro(Libro libro) {
		System.out.println("cargando textviews");
		TextView tvDetailSubject = (TextView) findViewById(R.id.tvDetailSubject);
		TextView tvDetailContent = (TextView) findViewById(R.id.tvDetailContent);
		TextView tvDetailUsername = (TextView) findViewById(R.id.tvDetailUsername);
		TextView tvDetailDate = (TextView) findViewById(R.id.tvDetailDate);
		System.out.println("textviews cargados");
		System.out.println(libro.getTitulo());
		tvDetailSubject.setText(libro.getTitulo());
		tvDetailContent.setText(libro.getAutor());
		tvDetailUsername.setText(libro.getEdicion());
		System.out.println("detail hasta edicion");
		tvDetailDate.setText(SimpleDateFormat.getInstance().format(
				libro.getLastModified()));
	}
	

	
	private void addOpiniones(OpinionCollection opiniones) {

		opinionList.addAll(opiniones.getOpiniones());
		adapter.notifyDataSetChanged();
	}

	private class FetchStingTask extends AsyncTask<String, Void, Libro> {
		private ProgressDialog pd;

		@Override
		protected Libro doInBackground(String... params) {
			Libro libro = null;
			try {
				libro = LibrosAPI.getInstance(LibroDetailActivity.this)
						.getLibro(params[0]);
			} catch (LibrosAndroidException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return libro;
		}
		
		

		@Override
		protected void onPostExecute(Libro result) {
			System.out.println("onpostexecute obteniendo result");
			loadLibro(result);
			System.out.println("onpostexecute cargado el libro");
			if (pd != null) {
				pd.dismiss();
			}
		}

		/*protected void onPostExecute(OpinionCollection result) {
			System.out.println("onpostexecute obteniendo result");
			addOpiniones(result);
			System.out.println("onpostexecute cargado el libro");
			if (pd != null) {
				pd.dismiss();
			}
		}*/
		
		

		@Override
		protected void onPreExecute() {
			pd = new ProgressDialog(LibroDetailActivity.this);
			System.out.println("pre loading");
			pd.setTitle("Loading...");
			System.out.println("post loading");
			pd.setCancelable(false);
			pd.setIndeterminate(true);
			System.out.println("pre show");
			pd.show();
			System.out.println("post show");
		}

	}
}