
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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

			String param = request.getParameter("pid");

			String query;
			List<String> urls = new ArrayList<String>();
			List<String> descriptions = new ArrayList<String>();
			List<String> images = new ArrayList<String>();
			
			if (param.matches("\\d+$")) {
				// if param is a urlid
				query = "select * from urls where urlid='" + param + "' ";
				Statement idStat = (Statement) connection.createStatement();
				ResultSet set = idStat.executeQuery(query);
				while (set.next()){
					urls.add(set.getString("url"));
					images.add(set.getString("image"));
					descriptions.add(set.getString("description"));
				}
			} else if (param.contains(".com") || param.contains("http") || param.contains("//")) {
				
			} else {
				String[] words = param.split(" ");
				query = "select * from words where word in (";
				for (int i = 0; i < words.length; i++) {
					if (i != words.length - 1) {
						query += "'" + words[i] + "', ";
					} else {
						query += "'" + words[i] + "');";
					}
				}
				// Create statement to get all urlids containg the words
				Statement wordStat = (Statement) connection.createStatement();
				ResultSet urlSet = wordStat.executeQuery(query);
				
				List<Integer> urlids = new ArrayList<Integer>();
				while (urlSet.next()) {
					urlids.add(urlSet.getInt("urlid"));
				}
				
				urls = CrawlerHelpers.getOrderedUrls(urlids);
				images = CrawlerHelpers.getOrderedImages(urlids);
				descriptions = CrawlerHelpers.getOrderedDescriptions(urlids);
				
			}


			request.setAttribute("urls", urls);
			request.setAttribute("descriptions", descriptions);
			request.setAttribute("images", images);
			
			RequestDispatcher view = request.getRequestDispatcher("/index.jsp");
			view.forward(request, response);
			connection.close();
			System.out.println("Disconnected!");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
