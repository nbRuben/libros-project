package edu.upc.eetac.dsa.rnuevo.libros.api.model;

import java.sql.Date;
import java.util.List;

import javax.ws.rs.core.Link;

import org.glassfish.jersey.linking.Binding;
import org.glassfish.jersey.linking.InjectLink;
import org.glassfish.jersey.linking.InjectLink.Style;
import org.glassfish.jersey.linking.InjectLinks;

import edu.upc.eetac.dsa.rnuevo.libros.api.MediaType;
import edu.upc.eetac.dsa.rnuevo.libros.api.LibroResource;
import edu.upc.eetac.dsa.rnuevo.libros.api.OpinionResource;

public class Libro {
	@InjectLinks({
			@InjectLink(resource = LibroResource.class, style = Style.ABSOLUTE, rel = "libros", title = "Latest libros", type = MediaType.LIBROS_API_LIBRO_COLLECTION),
			@InjectLink(resource = LibroResource.class, style = Style.ABSOLUTE, rel = "self edit", title = "Libro", type = MediaType.LIBROS_API_LIBRO, method = "getLibro", bindings = @Binding(name = "idLibro", value = "${instance.id}")),
			@InjectLink(resource = OpinionResource.class, style = Style.ABSOLUTE, rel = "opinion", title = "Opinion", type = MediaType.LIBROS_API_OPINION_COLLECTION, method = "getOpiniones", bindings = @Binding(name = "idLibro", value = "${instance.id}")) })
	private List<Link> links;
	private int id;
	private String titulo;
	private String autor;
	private String idioma;
	private String edicion;
	private Date fechaEdicion;
	private Date fechaImpresion;
	private String editorial;
	private long lastModified;

	public List<Link> getLinks() {
		return links;
	}

	public void setLinks(List<Link> links) {
		this.links = links;
	}

	public String getTitulo() {
		return titulo;
	}

	public void setTitulo(String titulo) {
		this.titulo = titulo;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
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

	public Date getFechaEdicion() {
		return fechaEdicion;
	}

	public void setFechaEdicion(Date fechaEdicion) {
		this.fechaEdicion = fechaEdicion;
	}

	public Date getFechaImpresion() {
		return fechaImpresion;
	}

	public void setFechaImpresion(Date fechaImpresion) {
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
