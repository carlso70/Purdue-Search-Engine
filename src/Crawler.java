import java.io.*;
import java.net.*;
import java.util.regex.*;
import java.sql.*;
import java.sql.Connection;
import java.util.*;

import org.jsoup.Jsoup;
import org.jsoup.Connection.Response;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class Crawler {

	Connection connection;
	int urlID;
	int maxurls;
	public Properties props;
	String domain;
	String root;
	String current;
	static int nextUrlID;
	static int nextUrlIDScanned;

	Crawler() {
		maxurls = 10000;
		domain = null;
		root = null;
		current = null;
		nextUrlID = 0;
		nextUrlIDScanned = 0;
		urlID = 0;

	}

	public void readProperties() throws IOException {
		props = new Properties();
		FileInputStream in = new FileInputStream("database.properties");
		props.load(in);
		this.domain = props.getProperty("crawler.domain");
		this.root = props.getProperty("crawler.root");
		this.maxurls = Integer.parseInt(props.getProperty("crawler.maxurls"));
		this.current = this.root;
		nextUrlIDScanned++;
		in.close();
	}

	public void openConnection() throws SQLException, IOException {
		String drivers = props.getProperty("jdbc.drivers");
		if (drivers != null)
			System.setProperty("jdbc.drivers", drivers);

		String url = props.getProperty("jdbc.url");
		String username = props.getProperty("jdbc.username");
		String password = props.getProperty("jdbc.password");

		connection = DriverManager.getConnection(url, username, password);
	}

	public void createDB() throws SQLException, IOException {
		openConnection();

		Statement stat = connection.createStatement();

		// Delete the table first if any
		try {
			stat.executeUpdate("DROP TABLE URLS");
			stat.executeUpdate("DROP TABLE WORDS");
		} catch (Exception e) {
		}

		// Create the table
		stat.executeUpdate(
				"CREATE TABLE URLS (urlid INT, url VARCHAR(512), description VARCHAR(200), image VARCHAR(512))");
		stat.executeUpdate("CREATE TABLE WORDS (word VARCHAR(100), urlid INT)");
	}

	public void setCurrentURLInDB(int urlid) {
		try {
			Statement stat = connection.createStatement();
			String query = "select url from URLS where urlid = " + urlid + ";";
			System.out.println(query);
			ResultSet result = stat.executeQuery(query);

			if (result.next()) {
				this.current = result.getString("url");
			} else {
				this.current = null;
			}

			System.out.println("This.current = " + this.current);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public boolean urlInDB(String urlFound) throws SQLException, IOException {
		Statement stat = connection.createStatement();
		ResultSet result = stat.executeQuery("SELECT * FROM URLS WHERE url LIKE '" + urlFound + "'");

		if (result.next()) {
			System.out.println("URL " + urlFound + " already in DB");
			return true;
		}

		return false;
	}

	public void insertURLDescription(String des, int urlid) throws SQLException, IOException {
		Statement stat = connection.createStatement();
		String query = "UPDATE URLS SET description = \"" + des + "\" WHERE urlid = " + urlid + ";";
		System.out.println(query);
		stat.executeUpdate(query);
	}

	public void insertURLInDB(String url) throws SQLException, IOException {
		Statement stat = connection.createStatement();
		String query = "INSERT INTO URLS(urlid, url) VALUES ('" + urlID + "','" + url + "');";
		System.out.println("Executing " + query);
		stat.executeUpdate(query);
		urlID++;
	}

	public void insertImageInDB(String src, int urlid) throws SQLException, IOException {
		Statement stat = connection.createStatement();
		String query = "UPDATE URLS SET image ='" + src + "' WHERE urlid ='" + urlid + "';";
		System.out.println("Executing " + query);
		stat.executeUpdate(query);
	}

	// Switched to urlid input instead of url
	public void deleteURLInDB(int urlid) throws SQLException, IOException {
		Statement stat = connection.createStatement();
		try {
			String query = "DELETE FROM URLS WHERE urlid='" + urlid + "'";
			stat.executeQuery(query);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void addUrls(Elements hrefs) {
		// Loop through links and valid ones into db
		for (Element link : hrefs) {
			String[] urlArr = link.attr("abs:href").split("\\?");
			try {
				if (!urlInDB(urlArr[0]) && urlArr[0].contains(this.domain) && urlArr[0].contains("http")) {
					insertURLInDB(urlArr[0]);
					nextUrlIDScanned++;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public void addImages(Elements images) {
		Element img = images.first();
		if (img != null) {
			String src = img.absUrl("src");
			// try to add the img that isn't the purdont logo
			if (src.contains("logo") || src.contains("brand") && images.size() > 1) {
				if (images.size() > 2) {
					img = images.get(2);
					if (img != null) {
						src = img.absUrl("src");
					}
				} else {
					if (img != null && images.size() > 1) {
						img = images.get(1);
						src = img.absUrl("src");
					}else {
						src = images.get(0).absUrl("src");
					}
				}
			}

			try {
				insertImageInDB(src, nextUrlID);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public void addWordTable(Elements body) throws SQLException {
		String b = body.first().text().toString();
		b = b.replaceAll("[^A-Za-z0-9 ]", "");
		Statement stat = connection.createStatement();
		String[] words = b.split(" ");
		// Build a massive query of all the words

		String query = "INSERT INTO words(word, urlid) value ";

		for (int i = 0; i < words.length; i++) {
			if (words[i].length() > 1 && i < words.length - 1) {
				String word = words[i].toLowerCase();
				query += "('" + word + "'," + "'" + Integer.toString(nextUrlID) + "'), ";
			} else if (i == words.length - 1) {
				String word = words[i].toLowerCase();
				query += "('" + word + "'," + "'" + Integer.toString(nextUrlID) + "');";
			}
		}

		System.out.println(query);
		stat.executeUpdate(query);
	}
	/*
	 * public void addWordTable(Elements body) { WordTableThread thread = new
	 * WordTableThread(body, nextUrlID); thread.run(); thread.interrupt(); }
	 */

	public void addDescription(Elements titles, Elements ps, Elements bodys) {
		Element t = titles.first();
		String title = t.text().toString();
		Element p = ps.first();
		String description = null;
		if (p != null) {
			description = p.text().toString();
		}
		Element body = bodys.first();
		String btext = body.text().toString();
		if (description == null || (description.length() < 50 && btext != null)) {
			description = btext;
		}

		title = title.replaceAll("\\|", " ");
		// Must use substring to prevent descriptions going over length

		String add = title + " " + description;
		try{
			int len = add.length();
			if (len > 199){
				insertURLDescription(add.substring(0, 199), nextUrlID);
			}else {
				insertURLDescription(add, nextUrlID);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void fetchURL(String urlScanned) {
		try {
			urlScanned = urlScanned.replace("\\", "");
			Response jsoupResponse = Jsoup.connect(urlScanned).execute();
			if (!jsoupResponse.contentType().contains("text/plain")
					&& !jsoupResponse.contentType().contains("text/html")) {
				System.out.println("Not an html doc is a " + jsoupResponse.contentType());
				deleteURLInDB(nextUrlID - 1);
			}

			Document doc = Jsoup.connect(urlScanned).get();
			// Adds all the suburls, words and images on the page
			if (this.maxurls > nextUrlIDScanned) {
				addUrls(doc.select("a[href]"));
			}

			addImages(doc.select("img"));
			addDescription(doc.select("title"), doc.select("p"), doc.select("body"));
			addWordTable(doc.select("body"));

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		Crawler crawler = new Crawler();

		try {
			crawler.readProperties();
			crawler.openConnection();
			crawler.createDB();
			while (nextUrlIDScanned > nextUrlID) {
				if (crawler.current != null) {
					crawler.fetchURL(crawler.current);
					System.out.println("next " + nextUrlID + " Scanned " + nextUrlIDScanned);
					crawler.setCurrentURLInDB(++nextUrlID);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
