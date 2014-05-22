package edu.upc.eetac.dsa.rnuevo.libros.android;

import java.net.Authenticator;
import java.net.PasswordAuthentication;
import java.util.ArrayList;

import edu.upc.eetac.dsa.rnuevo.libros.android.api.Libro;
import edu.upc.eetac.dsa.rnuevo.libros.android.api.LibroAdapter;
import edu.upc.eetac.dsa.rnuevo.libros.android.api.LibroCollection;
import edu.upc.eetac.dsa.rnuevo.libros.android.api.LibrosAPI;
import edu.upc.eetac.dsa.rnuevo.libros.android.api.LibrosAndroidException;
import android.app.Activity;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

public class LibrosMainActivity extends ListActivity {
	private final static String TAG = LibrosMainActivity.class.toString();

	ArrayList<Libro> libroList;
	LibroAdapter adapter;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.libros_layout);
		/*SharedPreferences prefs = getSharedPreferences("libros-profile",
				Context.MODE_PRIVATE);
		final String username = prefs.getString("username", null);
		final String password = prefs.getString("password", null);*/

		Authenticator.setDefault(new Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication("test", "test"
						.toCharArray());
			}
		});
		//Log.d(TAG, "authenticated with " + username + ":" + password);

		libroList = new ArrayList<>();
		adapter = new LibroAdapter(this, libroList);
		setListAdapter(adapter);

		(new FetchStingsTask()).execute();
	}

	private void addLibros(LibroCollection libros) {

		libroList.addAll(libros.getLibros());
		adapter.notifyDataSetChanged();
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		System.out.println("he clicado un libro");
		Libro libro = libroList.get(position);
		Log.d(TAG, libro.getLinks().get("self").getTarget());

		Intent intent = new Intent(this, LibroDetailActivity.class);
		intent.putExtra("url", libro.getLinks().get("self").getTarget());
		System.out.println("ahora toca startactivity");
		startActivity(intent);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.libros_actions, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		System.out.println("llego al optionitemselected");
		switch (item.getItemId()) {
		case R.id.miWrite:
			Intent intent = new Intent(this, WriteLibroActivity.class);
			startActivity(intent);
			return true;

		default:
			return super.onOptionsItemSelected(item);
		}

	}

	private class FetchStingsTask extends
			AsyncTask<Void, Void, LibroCollection> {
		private ProgressDialog pd;

		@Override
		protected LibroCollection doInBackground(Void... params) {
			LibroCollection libros = null;
			try {
				libros = LibrosAPI.getInstance(LibrosMainActivity.this)
						.getLibros();
			} catch (LibrosAndroidException e) {
				e.printStackTrace();
			}
			return libros;
		}

		@Override
		protected void onPostExecute(LibroCollection result) {
			addLibros(result);
			if (pd != null) {
				pd.dismiss();
			}
		}

		@Override
		protected void onPreExecute() {
			pd = new ProgressDialog(LibrosMainActivity.this);
			pd.setTitle("Searching...");
			pd.setCancelable(false);
			pd.setIndeterminate(true);
			pd.show();
		}

	}

}