package edu.upc.eetac.dsa.rnuevo.libros.api.model;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.core.Link;

import org.glassfish.jersey.linking.Binding;
import org.glassfish.jersey.linking.InjectLink;
import org.glassfish.jersey.linking.InjectLink.Style;
import org.glassfish.jersey.linking.InjectLinks;

import edu.upc.eetac.dsa.rnuevo.libros.api.model.Libro;
import edu.upc.eetac.dsa.rnuevo.libros.api.MediaType;
import edu.upc.eetac.dsa.rnuevo.libros.api.LibroResource;

public class LibroCollection {
	@InjectLinks({
			@InjectLink(resource = LibroResource.class, style = Style.ABSOLUTE, rel = "create-libro", title = "Create libro", type = MediaType.LIBROS_API_LIBRO),
			@InjectLink(value = "/libros?before={before}", style = Style.ABSOLUTE, rel = "previous", title = "Previous libros", type = MediaType.LIBROS_API_LIBRO_COLLECTION, bindings = { @Binding(name = "before", value = "${instance.oldestTimestamp}") }),
			@InjectLink(value = "/libros?after={after}", style = Style.ABSOLUTE, rel = "current", title = "Newest libros", type = MediaType.LIBROS_API_LIBRO_COLLECTION, bindings = { @Binding(name = "after", value = "${instance.newestTimestamp}") }) })
	private List<Link> links;
	private List<Libro> libros;
	private long newestTimestamp;
	private long oldestTimestamp;
	
	public LibroCollection() {
		super();
		libros = new ArrayList<>();
	}
	
	public List<Link> getLinks() {
		return links;
	}
	public void setLinks(List<Link> links) {
		this.links = links;
	}
	
	public void addLibro(Libro libro) {
		libros.add(libro);
	}
	
	public List<Libro> getLibros() {
		return libros;
	}
	public void setLibros(List<Libro> libros) {
		this.libros = libros;
	}
	public long getNewestTimestamp() {
		return newestTimestamp;
	}
	public void setNewestTimestamp(long newestTimestamp) {
		this.newestTimestamp = newestTimestamp;
	}
	public long getOldestTimestamp() {
		return oldestTimestamp;
	}
	public void setOldestTimestamp(long oldestTimestamp) {
		this.oldestTimestamp = oldestTimestamp;
	}
	


	
}