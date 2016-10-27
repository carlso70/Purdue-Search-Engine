
import java.io.IOException;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.util.ArrayList;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.mysql.jdbc.Connection;
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

		Connection connection = null;
		String url = "jdbc:mysql://localhost:3306/";
		String dbName = "purdue";
		String driver = "com.mysql.jdbc.Driver";
		String userName = "root";
		String password = "Yaw9cram";

		Statement st;
		try {
			Class.forName(driver).newInstance();
			connection = DriverManager.getConnection(url + dbName, userName, password);
			System.out.println("Connected!");
			String pid = request.getParameter("pid");

			ArrayList al = null;
			ArrayList pid_list = new ArrayList();
			String query = "select * from urls where url='" + pid + "' ";

			System.out.println("query " + query);
			st = connection.createStatement();
			ResultSet rs = st.executeQuery(query);

			while (rs.next()) {
				al = new ArrayList();

				// out.println(rs.getString(1));
				// out.println(rs.getString(2));
				// out.println(rs.getString(3));
				// out.println(rs.getString(4));
				al.add(rs.getString(1));
				al.add(rs.getString(2));
				al.add(rs.getString(3));
				al.add(rs.getString(4));

				System.out.println("al :: " + al);
				pid_list.add(al);
			}

			request.setAttribute("piList", pid_list);
			RequestDispatcher view = request.getRequestDispatcher("/searchview.jsp");
			view.forward(request, response);
			connection.close();
			System.out.println("Disconnected!");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
