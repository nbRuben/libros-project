package edu.upc.eetac.dsa.rnuevo.libros.android.api;

import java.util.HashMap;
import java.util.Map;

public class Libro {
	private Map<String, Link> links = new HashMap<>();
	private String id;
	private String titulo;
	private String autor;
	private String idioma;
	private String edicion;
	private String fechaEdicion;
	private String fechaImpresion;
	private String editorial;
	private long lastModified;
	
	public Map<String, Link> getLinks() {
		return links;
	}
	public void setLinks(Map<String, Link> links) {
		this.links = links;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getTitulo() {
		return titulo;
	}
	public void setTitulo(String titulo) {
		this.titulo = titulo;
	}
	public String getAutor() {
		return autor;
	}
	public void setAutor(String autor) {
		this.autor = autor;
	}
	public String getIdioma() {
		return idioma;
	}
	public void setIdioma(String idioma) {
		this.idioma = idioma;
	}
	public String getEdicion() {
		return edicion;
	}
	public void setEdicion(String edicion) {
		this.edicion = edicion;
	}
	public String getFechaEdicion() {
		return fechaEdicion;
	}
	public void setFechaEdicion(String fechaEdicion) {
		this.fechaEdicion = fechaEdicion;
	}
	public String getFechaImpresion() {
		return fechaImpresion;
	}
	public void setFechaImpresion(String fechaImpresion) {
		this.fechaImpresion = fechaImpresion;
	}
	public String getEditorial() {
		return editorial;
	}
	public void setEditorial(String editorial) {
		this.editorial = editorial;
	}
	public long getLastModified() {
		return lastModified;
	}
	public void setLastModified(long lastModified) {
		this.lastModified = lastModified;
	}
}
