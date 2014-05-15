package edu.upc.eetac.dsa.rnuevo.libros.api.model;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.core.Link;

import org.glassfish.jersey.linking.Binding;
import org.glassfish.jersey.linking.InjectLink;
import org.glassfish.jersey.linking.InjectLink.Style;
import org.glassfish.jersey.linking.InjectLinks;

import edu.upc.eetac.dsa.rnuevo.libros.api.model.Opinion;
import edu.upc.eetac.dsa.rnuevo.libros.api.MediaType;
import edu.upc.eetac.dsa.rnuevo.libros.api.LibroResource;


public class OpinionCollection {
	
	private List<Link> links;
	private List<Opinion> opiniones;
	private long newestTimestamp;
	private long oldestTimestamp;
	
	public OpinionCollection (){
		super();
		opiniones = new ArrayList<>();
	}
	
	public List<Link> getLinks() {
		return links;
	}
	public void setLinks(List<Link> links) {
		this.links = links;
	}
	
	public void addOpinion(Opinion opinion) {
		opiniones.add(opinion);
	}
	public List<Opinion> getOpiniones() {
		return opiniones;
	}
	public void setOpiniones(List<Opinion> opiniones) {
		this.opiniones = opiniones;
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
