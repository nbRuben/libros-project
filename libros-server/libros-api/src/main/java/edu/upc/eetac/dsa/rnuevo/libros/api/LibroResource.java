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
import edu.upc.eetac.dsa.rnuevo.libros.api.DataSourceSPA;
import edu.upc.eetac.dsa.rnuevo.libros.api.model.LibroCollection;

@Path("/libros")
public class LibroResource {
	private DataSource ds = DataSourceSPA.getInstance().getDataSource();
	@Context
	private SecurityContext security;

	@GET
	@Produces(MediaType.LIBROS_API_LIBRO_COLLECTION)
	public LibroCollection getLibros(@QueryParam("length") int length,
			@QueryParam("before") long before, @QueryParam("after") long after) {

		LibroCollection libros = new LibroCollection();

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
			stmt = conn.prepareStatement(buildGetLibrosQuery(updateFromLast));
			if (updateFromLast) {
				stmt.setTimestamp(1, new Timestamp(after));
			} else {
				if (before > 0)
					stmt.setTimestamp(1, new Timestamp(before));
				else
					stmt.setTimestamp(1, null);
				length = (length <= 0) ? 20 : length;
				stmt.setInt(2, length);
			}
			ResultSet rs = stmt.executeQuery();
			boolean first = true;
			long oldestTimestamp = 0;
			while (rs.next()) {
				Libro libro = new Libro();
				libro.setId(rs.getInt("id"));
				libro.setTitulo(rs.getString("titulo"));
				libro.setAutor(rs.getString("autor"));
				libro.setIdioma(rs.getString("idioma"));
				libro.setEdicion(rs.getString("edicion"));
				libro.setFechaEdicion(rs.getDate(6));
				libro.setFechaImpresion(rs.getDate(7));
				libro.setEditorial(rs.getString("editorial"));
				oldestTimestamp = rs.getTimestamp("last_modified").getTime();
				libro.setLastModified(oldestTimestamp);
				if (first) {
					first = false;
					libros.setNewestTimestamp(libro.getLastModified());
				}
				libros.addLibro(libro);
			}
			libros.setOldestTimestamp(oldestTimestamp);
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

		return libros;

	}

	private String buildGetLibrosQuery(boolean updateFromLast) {
		if (updateFromLast)
			return "SELECT * FROM libros WHERE last_modified > ? ORDER BY last_modified DESC";
		else
			return "SELECT * FROM libros WHERE last_modified < ifnull(?, now()) ORDER BY last_modified DESC LIMIT ?";
	}

	@GET
	@Path("/{idLibro}")
	@Produces(MediaType.LIBROS_API_LIBRO)
	public Response getLibro(@PathParam("idLibro") String idLibro,
			@Context Request request) {
		// Create CacheControl
		CacheControl cc = new CacheControl();

		Libro libro = getLibroFromDatabase(idLibro);

		// Calculate the ETag on last modified date of user resource
		EntityTag eTag = new EntityTag(Long.toString(libro.getLastModified()));

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
		rb = Response.ok(libro).cacheControl(cc).tag(eTag);

		return rb.build();
	}

	@GET
	@Path("/search")
	@Produces(MediaType.LIBROS_API_LIBRO_COLLECTION)
	public LibroCollection searchLibro(@QueryParam("titulo") String titulo,
			@QueryParam("autor") String autor, @QueryParam("length") int length) {

		LibroCollection libros = new LibroCollection();

		Connection conn = null;
		try {
			conn = ds.getConnection();
		} catch (SQLException e) {
			throw new ServerErrorException("Could not connect to the database",
					Response.Status.SERVICE_UNAVAILABLE);
		}

		PreparedStatement stmt = null;

		try {
			stmt = conn.prepareStatement(searchGetLibroByIdQuery());
			stmt.setString(1, "%" + titulo + "%");
			stmt.setString(2, "%" + autor + "%");
			stmt.setInt(3, length);

			ResultSet rs = stmt.executeQuery();
			long oldestTimestamp = 0;
			while (rs.next()) {
				Libro libro = new Libro();
				libro.setId(rs.getInt("id"));
				libro.setTitulo(rs.getString("titulo"));
				libro.setAutor(rs.getString("autor"));
				libro.setIdioma(rs.getString("idioma"));
				libro.setEdicion(rs.getString("edicion"));
				libro.setFechaEdicion(rs.getDate(6));
				libro.setFechaImpresion(rs.getDate(7));
				libro.setEditorial(rs.getString("editorial"));
				libro.setLastModified(rs.getTimestamp("last_modified")
						.getTime());
				libros.addLibro(libro);
			}
			libros.setOldestTimestamp(oldestTimestamp);
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

		return libros;

	}

	private String searchGetLibroByIdQuery() {
		return "SELECT * FROM libros WHERE titulo LIKE ? OR autor LIKE ?  LIMIT ?";
	}

	private Libro getLibroFromDatabase(String idLibro) {
		Libro libro = new Libro();

		Connection conn = null;
		try {
			conn = ds.getConnection();
		} catch (SQLException e) {
			throw new ServerErrorException("Could not connect to the database",
					Response.Status.SERVICE_UNAVAILABLE);
		}

		PreparedStatement stmt = null;
		try {
			stmt = conn.prepareStatement(buildGetLibroByIdQuery());
			stmt.setInt(1, Integer.valueOf(idLibro));
			ResultSet rs = stmt.executeQuery();
			if (rs.next()) {
				libro.setId(rs.getInt("id"));
				libro.setTitulo(rs.getString("titulo"));
				libro.setAutor(rs.getString("autor"));
				libro.setIdioma(rs.getString("idioma"));
				libro.setEdicion(rs.getString("edicion"));
				libro.setFechaEdicion(rs.getDate(6));
				libro.setFechaImpresion(rs.getDate(7));
				libro.setEditorial(rs.getString("editorial"));
				libro.setLastModified(rs.getTimestamp("last_modified")
						.getTime());
			} else {
				throw new NotFoundException("No se encuentra ningun libro con ID ="
						+ idLibro);
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

		return libro;
	}

	private String buildGetLibroByIdQuery() {
		return "SELECT * FROM libros WHERE id=?";
	}

	@POST
	@Consumes(MediaType.LIBROS_API_LIBRO)
	@Produces(MediaType.LIBROS_API_LIBRO)
	public Libro createLibro(Libro libro) {
		validateLibro(libro);
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

			stmt.setString(1, libro.getTitulo());
			stmt.setString(2, libro.getAutor());
			stmt.setString(3, libro.getIdioma());
			stmt.setString(4, libro.getEdicion());
			// //FALLAN FECHASSSSSSSSSSSSSSSSSSSSSSSSSs
			stmt.setDate(5, (Date)libro.getFechaEdicion());
			stmt.setDate(6, libro.getFechaImpresion());
			stmt.setString(7, libro.getEditorial());
			stmt.executeUpdate();
			ResultSet rs = stmt.getGeneratedKeys();
			if (rs.next()) {
				int idLibro = rs.getInt(1);
				libro = getLibroFromDatabase(Integer.toString(idLibro));

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

		return libro;
	}

	private String buildInsertLibro() {
		return "INSERT INTO Libros(Titulo, Autor, Idioma, Edicion, Fecha_Edicion, Fecha_Impresion, Editorial) VALUES(?, ?, ?, ?, ?, ?, ?)";
	}

	@DELETE
	@Path("/{idLibro}")
	public void deleteLibro(@PathParam("idLibro") String idLibro) {
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
			stmt.setInt(1, Integer.valueOf(idLibro));

			int rows = stmt.executeUpdate();
			if (rows == 0)
				throw new NotFoundException("No hay ningun libro con ID = "
						+ idLibro);
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
		return "DELETE FROM libros WHERE id=?";
	}

	@PUT
	@Path("/{idLibro}")
	@Consumes(MediaType.LIBROS_API_LIBRO)
	@Produces(MediaType.LIBROS_API_LIBRO)
	public Libro updateLibro(@PathParam("idLibro") String idLibro, Libro libro) {
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
			String sql = buildUpdateLibro();
			stmt = conn.prepareStatement(sql);
			stmt.setString(1, libro.getTitulo());
			stmt.setString(2, libro.getAutor());
			stmt.setString(3, libro.getIdioma());
			stmt.setString(4, libro.getEdicion());
			// //FALLAN FECHASSSSSSSSSSSSSSSSSSSSSSSSSs
			stmt.setDate(5, libro.getFechaEdicion());
			stmt.setDate(6, libro.getFechaImpresion());
			stmt.setString(7, libro.getEditorial());
			stmt.setInt(8, Integer.valueOf(idLibro));
			int rows = stmt.executeUpdate();
			if (rows == 1)
				libro = getLibroFromDatabase(idLibro);
			else {
				throw new NotFoundException("No existe ningun libro con ID = "
						+ idLibro);
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

		return libro;
	}

	private String buildUpdateLibro() {
		return "UPDATE libros SET titulo=ifnull(?, titulo), autor=ifnull(?, autor), idioma=ifnull(?, idioma), edicion=ifnull(?, edicion), fecha_edicion=ifnull(?, fecha_edicion), fecha_impresion=ifnull(?, fecha_impresion), editorial=ifnull(?, editorial)  WHERE id=?";
	}

	private void validateLibro(Libro libro) {
		if (libro.getTitulo() == null)
			throw new BadRequestException("Titulo no puede ser nulo.");
		if (libro.getAutor() == null)
			throw new BadRequestException("Autor no puede ser nulo.");
	}

}
