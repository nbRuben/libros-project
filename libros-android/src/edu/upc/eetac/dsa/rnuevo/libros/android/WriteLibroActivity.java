package edu.upc.eetac.dsa.rnuevo.libros.android;


import edu.upc.eetac.dsa.rnuevo.libros.android.api.Libro;
import edu.upc.eetac.dsa.rnuevo.libros.android.api.LibrosAPI;
import edu.upc.eetac.dsa.rnuevo.libros.android.api.LibrosAndroidException;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class WriteLibroActivity extends Activity {
	private final static String TAG = WriteLibroActivity.class.getName();

	private class PostStingTask extends AsyncTask<String, Void, Libro> {
		private ProgressDialog pd;

		@Override
		protected Libro doInBackground(String... params) {
			Libro libro = null;
			try {
				libro = LibrosAPI.getInstance(WriteLibroActivity.this)
						.createLibro(params[0], params[1]);
			} catch (LibrosAndroidException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return libro;
		}

		@Override
		protected void onPostExecute(Libro result) {
			showLibros();
			if (pd != null) {
				pd.dismiss();
			}
		}

		@Override
		protected void onPreExecute() {
			pd = new ProgressDialog(WriteLibroActivity.this);

			pd.setCancelable(false);
			pd.setIndeterminate(true);
			pd.show();
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.write_libro_layout);

	}
	

	public void cancel(View v) {
		finish();
	}

	public void postSting(View v) {
		EditText etSubject = (EditText) findViewById(R.id.etSubject);
		EditText etContent = (EditText) findViewById(R.id.etContent);

		String subject = etSubject.getText().toString();
		String content = etContent.getText().toString();

		(new PostStingTask()).execute(subject, content);
	}

	private void showLibros() {
		Intent intent = new Intent(this, LibrosMainActivity.class);
		startActivity(intent);
	}

}