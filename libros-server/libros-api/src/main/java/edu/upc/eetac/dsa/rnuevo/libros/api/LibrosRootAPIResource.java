package edu.upc.eetac.dsa.rnuevo.libros.api;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

import edu.upc.eetac.dsa.rnuevo.libros.api.model.LibrosRootAPI;

@Path("/")
public class LibrosRootAPIResource {
	@GET
	public LibrosRootAPI getRootAPI() {
		LibrosRootAPI api = new LibrosRootAPI();
		return api;
	}
}