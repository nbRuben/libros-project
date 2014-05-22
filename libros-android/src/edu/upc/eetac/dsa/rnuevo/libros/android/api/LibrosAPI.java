package edu.upc.eetac.dsa.rnuevo.libros.android.api;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;
import java.util.Properties;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;

public class LibrosAPI {
	private final static String TAG = LibrosAPI.class.getName();
	private static LibrosAPI instance = null;
	private URL url;

	private LibrosRootAPI rootAPI = null;

	private LibrosAPI(Context context) throws IOException,
			LibrosAndroidException {
		super();

		AssetManager assetManager = context.getAssets();
		Properties config = new Properties();
		config.load(assetManager.open("config.properties"));
		String serverAddress = config.getProperty("server.address");
		String serverPort = config.getProperty("server.port");
		url = new URL("http://" + serverAddress + ":" + serverPort
				+ "/libros-api");

		Log.d("LINKS", url.toString());
		System.out.println("llegamos hasta rootapi");
		getRootAPI();
	}

	public final static LibrosAPI getInstance(Context context)
			throws LibrosAndroidException {
		if (instance == null)
			try {
				instance = new LibrosAPI(context);
			} catch (IOException e) {
				throw new LibrosAndroidException(
						"Can't load configuration file");
			}
		return instance;
	}

	private void getRootAPI() throws LibrosAndroidException {
		Log.d(TAG, "getRootAPI()");
		rootAPI = new LibrosRootAPI();
		HttpURLConnection urlConnection = null;
		try {
			urlConnection = (HttpURLConnection) url.openConnection();
			urlConnection.setRequestMethod("GET");
			urlConnection.setDoInput(true);
			urlConnection.connect();
		} catch (IOException e) {
			throw new LibrosAndroidException(
					"Can't connect to Libros API Web Service");
		}

		BufferedReader reader;
		try {
			reader = new BufferedReader(new InputStreamReader(
					urlConnection.getInputStream()));
			StringBuilder sb = new StringBuilder();
			String line = null;
			while ((line = reader.readLine()) != null) {
				sb.append(line);
			}

			JSONObject jsonObject = new JSONObject(sb.toString());
			JSONArray jsonLinks = jsonObject.getJSONArray("links");
			parseLinks(jsonLinks, rootAPI.getLinks());
		} catch (IOException e) {
			throw new LibrosAndroidException(
					"Can't get response from Libros API Web Service");
		} catch (JSONException e) {
			throw new LibrosAndroidException("Error parsing Libros Root API");
		}

	}

	public LibroCollection getLibros() throws LibrosAndroidException {
		System.out.println("llegamos a get libros");
		Log.d(TAG, "getLibros()");
		LibroCollection libros = new LibroCollection();

		HttpURLConnection urlConnection = null;
		try {
			urlConnection = (HttpURLConnection) new URL(rootAPI.getLinks()
					.get("libros").getTarget()).openConnection();
			urlConnection.setRequestMethod("GET");
			urlConnection.setDoInput(true);
			urlConnection.connect();
			System.out.println("hemos conectado");
		} catch (IOException e) {
			throw new LibrosAndroidException(
					"Can't connect to Beeter API Web Service");
		}

		BufferedReader reader;
		try {
			reader = new BufferedReader(new InputStreamReader(
					urlConnection.getInputStream()));
			StringBuilder sb = new StringBuilder();
			String line = null;
			while ((line = reader.readLine()) != null) {
				sb.append(line);
			}

			JSONObject jsonObject = new JSONObject(sb.toString());
			JSONArray jsonLinks = jsonObject.getJSONArray("links");
			parseLinks(jsonLinks, libros.getLinks());
			System.out.println("llegamos al parse?");
			libros.setNewestTimestamp(jsonObject.getLong("newestTimestamp"));
			libros.setOldestTimestamp(jsonObject.getLong("oldestTimestamp"));
			JSONArray jsonLibros = jsonObject.getJSONArray("libros");
			System.out.println("antes del for");
			for (int i = 0; i < jsonLibros.length(); i++) {
				Libro libro = new Libro();
				JSONObject jsonLibro = jsonLibros.getJSONObject(i);
				libro.setId(jsonLibro.getString("id"));
				libro.setTitulo(jsonLibro.getString("titulo"));
				System.out.println("otenemos titulo");
				libro.setAutor(jsonLibro.getString("autor"));
				System.out.println("otenemos autor");
				libro.setEdicion(jsonLibro.getString("edicion"));
				System.out.println("otenemos edicion");
				libro.setIdioma(jsonLibro.getString("idioma"));
				System.out.println("otenemos idioma");
				//libro.setFechaEdicion(jsonLibro.getString("fecha_edicion"));
				//libro.setFechaImpresion(jsonLibro.getString("fecha_impresion"));
				libro.setEditorial(jsonLibro.getString("editorial"));
				System.out.println("otenemos editorial");
				//libro.setLastModified(jsonLibro.getLong("lastModified"));
				System.out.println("obtenemos todos los campos");
				jsonLinks = jsonLibro.getJSONArray("links");
				parseLinks(jsonLinks, libro.getLinks());
				libros.getLibros().add(libro);
			}
		} catch (IOException e) {
			throw new LibrosAndroidException(
					"Can't get response from Beeter API Web Service");
		} catch (JSONException e) {
			throw new LibrosAndroidException("Error parsing Beeter Root API");
		}

		return libros;
	}

	private void parseLinks(JSONArray jsonLinks, Map<String, Link> map)
			throws LibrosAndroidException, JSONException {
		for (int i = 0; i < jsonLinks.length(); i++) {
			Link link = SimpleLinkHeaderParser
					.parseLink(jsonLinks.getString(i));
			String rel = link.getParameters().get("rel");
			String rels[] = rel.split("\\s");
			for (String s : rels)
				map.put(s, link);
		}
	}

	public Libro getLibro(String urlLibro) throws LibrosAndroidException {
		Libro libro = new Libro();

		HttpURLConnection urlConnection = null;
		try {
			URL url = new URL(urlLibro);
			urlConnection = (HttpURLConnection) url.openConnection();
			urlConnection.setRequestMethod("GET");
			urlConnection.setDoInput(true);
			urlConnection.connect();
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					urlConnection.getInputStream()));
			StringBuilder sb = new StringBuilder();
			String line = null;
			while ((line = reader.readLine()) != null) {
				sb.append(line);
			}
			JSONObject jsonLibro = new JSONObject(sb.toString());
			libro.setTitulo(jsonLibro.getString("titulo"));
			libro.setAutor(jsonLibro.getString("autor"));
			libro.setEdicion(jsonLibro.getString("edicion"));
			libro.setIdioma(jsonLibro.getString("idioma"));
			libro.setFechaEdicion(jsonLibro.getString("fechaEdicion"));
			libro.setFechaImpresion(jsonLibro.getString("fechaImpresion"));
			libro.setEditorial(jsonLibro.getString("editorial"));
			JSONArray jsonLinks = jsonLibro.getJSONArray("links");
			parseLinks(jsonLinks, libro.getLinks());
		} catch (MalformedURLException e) {
			Log.e(TAG, e.getMessage(), e);
			throw new LibrosAndroidException("Bad sting url");
		} catch (IOException e) {
			Log.e(TAG, e.getMessage(), e);
			throw new LibrosAndroidException("Exception when getting the sting");
		} catch (JSONException e) {
			Log.e(TAG, e.getMessage(), e);
			throw new LibrosAndroidException("Exception parsing response");
		}

		return libro;
	}

	public Libro createLibro(String titulo, String autor)
			throws LibrosAndroidException {
		Libro libro = new Libro();
		libro.setTitulo(titulo);
		libro.setAutor(autor);
		HttpURLConnection urlConnection = null;
		try {
			JSONObject jsonLibro = createJsonLibro(libro);
			URL urlPostLibros = new URL(rootAPI.getLinks().get("create-libros")
					.getTarget());
			urlConnection = (HttpURLConnection) urlPostLibros.openConnection();
			urlConnection.setRequestProperty("Accept",
					MediaType.LIBROS_API_LIBRO);
			urlConnection.setRequestProperty("Content-Type",
					MediaType.LIBROS_API_LIBRO);
			urlConnection.setRequestMethod("POST");
			urlConnection.setDoInput(true);
			urlConnection.setDoOutput(true);
			urlConnection.connect();
			PrintWriter writer = new PrintWriter(
					urlConnection.getOutputStream());
			writer.println(jsonLibro.toString());
			writer.close();
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					urlConnection.getInputStream()));
			StringBuilder sb = new StringBuilder();
			String line = null;
			while ((line = reader.readLine()) != null) {
				sb.append(line);
			}
			jsonLibro = new JSONObject(sb.toString());

			libro.setTitulo(jsonLibro.getString("titulo"));
			libro.setAutor(jsonLibro.getString("autor"));
			libro.setEdicion(jsonLibro.getString("Edicion"));
			libro.setIdioma(jsonLibro.getString("idioma"));
			libro.setFechaEdicion(jsonLibro.getString("fecha_edicion"));
			libro.setFechaImpresion(jsonLibro.getString("fecha_impresion"));
			libro.setEditorial(jsonLibro.getString("editorial"));
			JSONArray jsonLinks = jsonLibro.getJSONArray("links");
			parseLinks(jsonLinks, libro.getLinks());
		} catch (JSONException e) {
			Log.e(TAG, e.getMessage(), e);
			throw new LibrosAndroidException("Error parsing response");
		} catch (IOException e) {
			Log.e(TAG, e.getMessage(), e);
			throw new LibrosAndroidException("Error getting response");
		} finally {
			if (urlConnection != null)
				urlConnection.disconnect();
		}
		return libro;
	}

	private JSONObject createJsonLibro(Libro libro) throws JSONException {
		JSONObject jsonLibro = new JSONObject();
		jsonLibro.put("titulo", libro.getTitulo());
		jsonLibro.put("autor", libro.getAutor());

		return jsonLibro;
	}
}