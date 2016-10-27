
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.util.ArrayList;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.mysql.jdbc.Statement;

/**
 * Servlet implementation class Search
 */
@WebServlet("/Search")
public class Search extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public Search() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		response.getWriter().append("Served at: ").append(request.getContextPath());
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doGet(request, response);

		response.setContentType("text/html");

		String url = "jdbc:mysql://localhost:3306/";
		String dbName = "purdue";
		String driver = "com.mysql.jdbc.Driver";
		String userName = "root";
		String password = "Yaw9cram";

		try {
			Class.forName(driver).newInstance();
			Connection connection = DriverManager.getConnection(url + dbName, userName, password);
			System.out.println("Connected!");
			
			
			String pid = request.getParameter("pid");

			ArrayList al = null;
			ArrayList pid_list = new ArrayList();
			String query = "select * from urls where urlid='" + pid + "' ";

			Statement statement;
			System.out.println("query " + query);
			statement = (Statement) connection.createStatement();
			ResultSet rs = statement.executeQuery(query);
			
			String rUrl = "";
			if (rs.next()) {
				rUrl = rs.getString("url");
			}
			request.setAttribute("url", rUrl);
			RequestDispatcher view = request.getRequestDispatcher("/index.jsp");
			view.forward(request, response);
			connection.close();
			System.out.println("Disconnected!");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
