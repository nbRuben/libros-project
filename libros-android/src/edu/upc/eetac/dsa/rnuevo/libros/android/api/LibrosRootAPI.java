package edu.upc.eetac.dsa.rnuevo.libros.android.api;

import java.util.HashMap;
import java.util.Map;

public class LibrosRootAPI {

	private Map<String, Link> links;

	public LibrosRootAPI() {
		links = new HashMap<>();
	}

	public Map<String, Link> getLinks() {
		return links;
	}

}