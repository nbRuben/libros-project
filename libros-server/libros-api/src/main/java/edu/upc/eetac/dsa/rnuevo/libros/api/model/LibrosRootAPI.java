package edu.upc.eetac.dsa.rnuevo.libros.api.model;

import java.util.List;

import javax.ws.rs.core.Link;

import org.glassfish.jersey.linking.InjectLink;
import org.glassfish.jersey.linking.InjectLink.Style;
import org.glassfish.jersey.linking.InjectLinks;

import edu.upc.eetac.dsa.rnuevo.libros.api.LibrosRootAPIResource;
import edu.upc.eetac.dsa.rnuevo.libros.api.MediaType;
import edu.upc.eetac.dsa.rnuevo.libros.api.LibroResource;

public class LibrosRootAPI {
	@InjectLinks({
		@InjectLink(resource = LibrosRootAPIResource.class, style = Style.ABSOLUTE, rel = "self bookmark home", title = "Libros Root API", method = "getRootAPI"),
		@InjectLink(resource = LibroResource.class, style = Style.ABSOLUTE, rel = "libros", title = "Latest libros", type = MediaType.LIBROS_API_LIBRO_COLLECTION),
		@InjectLink(resource = LibroResource.class, style = Style.ABSOLUTE, rel = "create-libros", title = "Latest libros", type = MediaType.LIBROS_API_LIBRO) })
	private List<Link> links;

	public List<Link> getLinks() {
		return links;
	}

	public void setLinks(List<Link> links) {
		this.links = links;
	}
}