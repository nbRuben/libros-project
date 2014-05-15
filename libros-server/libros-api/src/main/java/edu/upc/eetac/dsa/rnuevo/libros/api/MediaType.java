package edu.upc.eetac.dsa.rnuevo.libros.api;

public interface MediaType {
	public final static String LIBROS_API_OPINION = "application/vnd.libros.api.opinion+json";
	public final static String LIBROS_API_OPINION_COLLECTION = "application/vnd.libros.api.opinon.collection+json";
	public final static String LIBROS_API_LIBRO = "application/vnd.libros.api.libro+json";
	public final static String LIBROS_API_LIBRO_COLLECTION = "application/vnd.libros.api.libro.collection+json";
	public final static String LIBROS_API_ERROR = "application/vnd.dsa.libros.error+json";
}