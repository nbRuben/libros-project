package edu.upc.eetac.dsa.rnuevo.libros.api.model;


import java.sql.Date;
import java.util.List;

import javax.ws.rs.core.Link;

import org.glassfish.jersey.linking.Binding;
import org.glassfish.jersey.linking.InjectLink;
import org.glassfish.jersey.linking.InjectLink.Style;
import org.glassfish.jersey.linking.InjectLinks;

import edu.upc.eetac.dsa.rnuevo.libros.api.MediaType;
import edu.upc.eetac.dsa.rnuevo.libros.api.OpinionResource;

public class Opinion {
	@InjectLinks({
		@InjectLink(resource = OpinionResource.class, style = Style.ABSOLUTE, rel = "opinion", title = "Latest opinion", type = MediaType.LIBROS_API_OPINION_COLLECTION),
		@InjectLink(resource = OpinionResource.class, style = Style.ABSOLUTE, rel = "self edit", title = "Opinion", type = MediaType.LIBROS_API_OPINION, method = "getOpinion", bindings = @Binding(name = "idOpinion", value = "${instance.id}")) })
	private List<Link> links;
	private int id;
	private String username;
	private Date fecha;
	private String contenido;
	private int idLibro;
	private long lastModified;
	
	public List<Link> getLinks() {
		return links;
	}
	public void setLinks(List<Link> links) {
		this.links = links;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public Date getFecha() {
		return fecha;
	}
	public void setFecha(Date fecha) {
		this.fecha = fecha;
	}
	public String getContenido() {
		return contenido;
	}
	public void setContenido(String contenido) {
		this.contenido = contenido;
	}
	public int getIdLibro() {
		return idLibro;
	}
	public void setIdLibro(int idLibro) {
		this.idLibro = idLibro;
	}
	public long getLastModified() {
		return lastModified;
	}
	public void setLastModified(long lastModified) {
		this.lastModified = lastModified;
	}

}
