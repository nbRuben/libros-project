package edu.upc.eetac.dsa.rnuevo.libros.api;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;

import javax.sql.DataSource;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.ForbiddenException;
import javax.ws.rs.GET;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.ServerErrorException;
import javax.ws.rs.core.CacheControl;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.EntityTag;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

import edu.upc.eetac.dsa.rnuevo.libros.api.MediaType;
import edu.upc.eetac.dsa.rnuevo.libros.api.model.Libro;
import edu.upc.eetac.dsa.rnuevo.libros.api.model.Opinion;
import edu.upc.eetac.dsa.rnuevo.libros.api.model.OpinionCollection;
import edu.upc.eetac.dsa.rnuevo.libros.api.DataSourceSPA;
import edu.upc.eetac.dsa.rnuevo.libros.api.model.LibroCollection;

@Path("/libros/{idLibro}/opiniones")
public class OpinionResource {
	private DataSource ds = DataSourceSPA.getInstance().getDataSource();
	@Context
	private SecurityContext security;
	
	@GET
	@Produces(MediaType.LIBROS_API_OPINION_COLLECTION)
	public OpinionCollection getOpiniones(@QueryParam("length") int length,
			@QueryParam("before") long before, @QueryParam("after") long after, @PathParam("idLibro") String idLibro) {
		
		OpinionCollection opiniones = new OpinionCollection();

		Connection conn = null;
		try {
			conn = ds.getConnection();
		} catch (SQLException e) {
			throw new ServerErrorException("Could not connect to the database",
					Response.Status.SERVICE_UNAVAILABLE);
		}

		PreparedStatement stmt = null;
		try {
			boolean updateFromLast = after > 0;
			stmt = conn.prepareStatement(buildGetOpinionQuery(updateFromLast));
			stmt.setInt(1, Integer.valueOf(idLibro));
			if (updateFromLast) {
				stmt.setTimestamp(2, new Timestamp(after));
			} else {
				if (before > 0)
					stmt.setTimestamp(2, new Timestamp(before));
				else
					stmt.setTimestamp(2, null);
				length = (length <= 0) ? 20 : length;
				stmt.setInt(3, length);
			}
			ResultSet rs = stmt.executeQuery();
			boolean first = true;
			long oldestTimestamp = 0;
			while (rs.next()) {
				Opinion opinion = new Opinion();
				opinion.setId(rs.getInt("id"));
				opinion.setUsername(rs.getString("username"));
				opinion.setFecha(rs.getDate(3));
				opinion.setContenido(rs.getString("contenido"));
				opinion.setIdLibro(rs.getInt("id_libro"));
				oldestTimestamp = rs.getTimestamp("last_modified").getTime();
				opinion.setLastModified(oldestTimestamp);
				if (first) {
					first = false;
					opiniones.setNewestTimestamp(opinion.getLastModified());
				}
				opiniones.addOpinion(opinion);
			}
			opiniones.setOldestTimestamp(oldestTimestamp);
		} catch (SQLException e) {
			throw new ServerErrorException(e.getMessage(),
					Response.Status.INTERNAL_SERVER_ERROR);
		} finally {
			try {
				if (stmt != null)
					stmt.close();
				conn.close();
			} catch (SQLException e) {
			}
		}
		
		return opiniones;
		
	}

	private String buildGetOpinionQuery(boolean updateFromLast) {
		if (updateFromLast)
			return "SELECT * FROM opinion WHERE id_libro=? AND last_modified > ? ORDER BY last_modified DESC";
		else
			return "SELECT * FROM opinion WHERE id_libro=? AND last_modified < ifnull(?, now()) ORDER BY last_modified DESC LIMIT ?";
	}
	


	@POST
	@Consumes(MediaType.LIBROS_API_OPINION)
	@Produces(MediaType.LIBROS_API_OPINION)
	public Opinion createOpinion(Opinion opinion, @PathParam("idLibro") String idLibro) {
		//validateLibro(libro);
		Connection conn = null;
		try {
			conn = ds.getConnection();
		} catch (SQLException e) {
			throw new ServerErrorException("Could not connect to the database",
					Response.Status.SERVICE_UNAVAILABLE);
		}

		PreparedStatement stmt = null;
		try {
			String sql = buildInsertLibro();
			stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

			stmt.setString(1, opinion.getUsername());
			// //FALLAN FECHASSSSSSSSSSSSSSSSSSSSSSSSSs
			stmt.setDate(2, opinion.getFecha());
			stmt.setString(3, opinion.getContenido());
			stmt.setInt(4, Integer.valueOf(idLibro));
			stmt.executeUpdate();
			ResultSet rs = stmt.getGeneratedKeys();
			if (rs.next()) {
				int id = rs.getInt(1);
				opinion = getOpinionFromDatabase(Integer.toString(id));

			} else {
				// Something has failed...

			}
		} catch (SQLException e) {
			throw new ServerErrorException(e.getMessage(),
					Response.Status.INTERNAL_SERVER_ERROR);
		} finally {
			try {
				if (stmt != null)
					stmt.close();
				conn.close();
			} catch (SQLException e) {
			}
		}

		return opinion;
	}

	private String buildInsertLibro() {
		return "INSERT INTO opinion(username, fecha, contenido, id_libro) VALUES(?, ?, ?, ?)";
	}

	private Opinion getOpinionFromDatabase(String idOpinion) {
		Opinion opinion = new Opinion();

		Connection conn = null;
		try {
			conn = ds.getConnection();
		} catch (SQLException e) {
			throw new ServerErrorException("Could not connect to the database",
					Response.Status.SERVICE_UNAVAILABLE);
		}

		PreparedStatement stmt = null;
		try {
			stmt = conn.prepareStatement(buildGetOpinionByIdQuery());
			stmt.setInt(1, Integer.valueOf(idOpinion));
			ResultSet rs = stmt.executeQuery();
			if (rs.next()) {
				opinion.setId(rs.getInt("id"));
				opinion.setUsername(rs.getString("username"));
				opinion.setContenido(rs.getString("contenido"));
				opinion.setFecha(rs.getDate(3));
				opinion.setIdLibro(rs.getInt("id_libro"));
				opinion.setLastModified(rs.getTimestamp("last_modified")
						.getTime());
			} else {
				throw new NotFoundException("No existe ninguna reseña con ID="
						+ idOpinion);
			}

		} catch (SQLException e) {
			throw new ServerErrorException(e.getMessage(),
					Response.Status.INTERNAL_SERVER_ERROR);
		} finally {
			try {
				if (stmt != null)
					stmt.close();
				conn.close();
			} catch (SQLException e) {
			}
		}

		return opinion;
	}
	
	private String buildGetOpinionByIdQuery() {
		return "SELECT * FROM opinion WHERE id=?";
	}
	

	@GET
	@Path("/{idOpinion}")
	@Produces(MediaType.LIBROS_API_OPINION)
	public Response getLibro(@PathParam("idOpinion") String idOpinion,
			@Context Request request) {
		// Create CacheControl
		CacheControl cc = new CacheControl();

		Opinion opinion = getOpinionFromDatabase(idOpinion);

		// Calculate the ETag on last modified date of user resource
		EntityTag eTag = new EntityTag(Long.toString(opinion.getLastModified()));

		// Verify if it matched with etag available in http request
		Response.ResponseBuilder rb = request.evaluatePreconditions(eTag);

		// If ETag matches the rb will be non-null;
		// Use the rb to return the response without any further processing
		if (rb != null) {
			return rb.cacheControl(cc).tag(eTag).build();
		}

		// If rb is null then either it is first time request; or resource is
		// modified
		// Get the updated representation and return with Etag attached to it
		rb = Response.ok(opinion).cacheControl(cc).tag(eTag);

		return rb.build();
	}
	
	@DELETE
	@Path("/{idOpinion}")
	public void deleteLibro(@PathParam("idOpinion") String idOpinion) {
		// ////FALTA AQUI VALIDAR USUARIO PARA QUE SOLO LO PUEDA BORRAR EL ADMIN
		Connection conn = null;
		try {
			conn = ds.getConnection();
		} catch (SQLException e) {
			throw new ServerErrorException("Could not connect to the database",
					Response.Status.SERVICE_UNAVAILABLE);
		}

		PreparedStatement stmt = null;
		try {
			String sql = buildDeleteLibro();
			stmt = conn.prepareStatement(sql);
			stmt.setInt(1, Integer.valueOf(idOpinion));

			int rows = stmt.executeUpdate();
			if (rows == 0)
				throw new NotFoundException("No existe ninguna reseña con ID ="
						+ idOpinion);
		} catch (SQLException e) {
			throw new ServerErrorException(e.getMessage(),
					Response.Status.INTERNAL_SERVER_ERROR);
		} finally {
			try {
				if (stmt != null)
					stmt.close();
				conn.close();
			} catch (SQLException e) {
			}
		}
	}

	private String buildDeleteLibro() {
		return "DELETE FROM opinion WHERE id=?";
	}

	@PUT
	@Path("/{idOpinion}")
	@Consumes(MediaType.LIBROS_API_OPINION)
	@Produces(MediaType.LIBROS_API_OPINION)
	public Opinion updateOpinion(@PathParam("idOpinion") String idOpinion, Opinion opinion, @PathParam("idLibro") String idLibro) {
		// ////FALTA AQUI VALIDAR USUARIO PARA QUE SOLO LO PUEDA ACTUALIZAR EL
		// ADMIN
		Connection conn = null;
		try {
			conn = ds.getConnection();
		} catch (SQLException e) {
			throw new ServerErrorException("Could not connect to the database",
					Response.Status.SERVICE_UNAVAILABLE);
		}

		PreparedStatement stmt = null;
		try {
			String sql = buildUpdateOpinion();
			stmt = conn.prepareStatement(sql);
			stmt.setString(1, opinion.getUsername());
			// //FALLAN FECHASSSSSSSSSSSSSSSSSSSSSSSSSs
			stmt.setDate(2, opinion.getFecha());
			stmt.setString(3, opinion.getContenido());
			stmt.setInt(4, Integer.valueOf(idLibro));
			stmt.setInt(5, Integer.valueOf(idOpinion));
			stmt.executeUpdate();
			int rows = stmt.executeUpdate();
			if (rows == 1)
				opinion = getOpinionFromDatabase(idOpinion);
			else {
				throw new NotFoundException("No existe ninguna reseña con ID = "
						+ idOpinion);
			}

		} catch (SQLException e) {
			throw new ServerErrorException(e.getMessage(),
					Response.Status.INTERNAL_SERVER_ERROR);
		} finally {
			try {
				if (stmt != null)
					stmt.close();
				conn.close();
			} catch (SQLException e) {
			}
		}

		return opinion;
	}

	private String buildUpdateOpinion() {
		return "UPDATE opinion SET username=ifnull(?, username), fecha=ifnull(?, fecha), contenido=ifnull(?, contenido), id_libro=ifnull(?, id_libro) WHERE id=?";
	}

}
