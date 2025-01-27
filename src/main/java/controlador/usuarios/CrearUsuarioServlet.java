package controlador.usuarios;

import java.io.IOException;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.Servlet;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import modelo.Genero;
import modelo.Usuario;
import servicios.ServicioGenero;
import servicios.ServicioGuardarImagen;
import servicios.ServicioUsuario;
import utilidades.Validacion;

@WebServlet("/crearUsuario.ad")
@MultipartConfig(fileSizeThreshold = 1024 * 1024 * 1, maxRequestSize = 1024 * 1024 * 100 // 100 MB
)
public class CrearUsuarioServlet extends HttpServlet implements Servlet {

	private static final long serialVersionUID = -6626649526922232487L;
	private ServicioUsuario servUsuario;
	private ServicioGenero servGenero;
	private Validacion validarDatos;
	private ServicioGuardarImagen servGuardarImagen;

	@Override
	public void init() throws ServletException {
		super.init();
		this.servUsuario = new ServicioUsuario();
		this.servGenero = new ServicioGenero();
		this.validarDatos = new Validacion();
		this.servGuardarImagen = new ServicioGuardarImagen();
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

		String nombre = req.getParameter("nombre");
		String usuario = req.getParameter("usuario");
		String contrasena = validarDatos.esContrasenaValida(req.getParameter("contrasena"));
		String admin = req.getParameter("admin");
		double dineroDisponible = validarDatos.esNumeroDoubleValido(req.getParameter("dinero"));
		int tiempoDisponible = validarDatos.esNumeroEnteroValido(req.getParameter("tiempo"));
		String urlPerfil = req.getPart("urlPerfil").getSubmittedFileName();
		String preferenciaNombre = req.getParameter("genero");
		boolean esAdmin = false;
		Genero preferencia = servGenero.buscarPor(preferenciaNombre);

		if (admin.equals("admin")) {
			esAdmin = true;
			dineroDisponible = 0;
			tiempoDisponible = 0;
		}

		if (urlPerfil == null || urlPerfil.equals("")) {
			urlPerfil = "imagenes/fotoPerfilDefecto.jpg";
		}

		if (!urlPerfil.equals("")) {
			String nombreArchivo = req.getPart("urlPerfil").getSubmittedFileName();
			if (!servGuardarImagen.guardarFotoPerfilUsuario(nombreArchivo, req.getPart("urlPerfil"))) {
				Usuario usuariotemp = new Usuario(nombre, usuario, contrasena, dineroDisponible, tiempoDisponible,
						preferencia, urlPerfil, esAdmin);
				req.setAttribute("errorImagen", servGuardarImagen.getErrores());
				req.setAttribute("usuarioCrear", usuariotemp);

				RequestDispatcher dispatcher = getServletContext().getRequestDispatcher("/listarUsuarios.ad");
				dispatcher.forward(req, resp);
				return;
			}
			urlPerfil = "imagenes/perfiles/" + urlPerfil;
		}

		Usuario usuarioTemp = servUsuario.crear(nombre, usuario, contrasena, dineroDisponible, tiempoDisponible,
				preferencia, urlPerfil, esAdmin);

		if (usuarioTemp.esValido()) {
			req.setAttribute("success", "¡Usuario creado correctamente!");

			RequestDispatcher dispatcher = getServletContext().getRequestDispatcher("/listarUsuarios.ad");
			dispatcher.forward(req, resp);
		} else {
			req.setAttribute("usuarioCrear", usuarioTemp);
			req.setAttribute("serv", "crear");

			RequestDispatcher dispatcher = getServletContext().getRequestDispatcher("/listarUsuarios.ad");
			dispatcher.forward(req, resp);
		}
	}
}
