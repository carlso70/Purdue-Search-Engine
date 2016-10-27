
import java.io.FileInputStream;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;
import java.sql.Connection;

import org.jsoup.*;
import org.jsoup.select.Elements;

public class WordTableThread extends Thread {

	boolean terminate = false;
	int urlid;
	Elements body;
	Connection conneciton;

	public WordTableThread(Elements body, int urlid) {
		this.body = body;
		this.urlid = urlid;

		try {
			Properties props = new Properties();
			FileInputStream in = new FileInputStream("database.properties");
			props.load(in);

			String drivers = props.getProperty("jdbc.drivers");
			if (drivers != null)
				System.setProperty("jdbc.drivers", drivers);

			String url = props.getProperty("jdbc.url");
			String username = props.getProperty("jdbc.username");
			String password = props.getProperty("jdbc.password");

			this.conneciton = DriverManager.getConnection(url, username, password);
			in.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void run() {
			try {
				String b = body.first().text().toString();
				b = b.replaceAll("[^A-Za-z0-9 ]", "");
				Statement stat = conneciton.createStatement();
				String[] words = b.split(" ");

				for (int i = 0; i < words.length; i++) {
					if (words[i].length() > 1) {
						String word = words[i].toLowerCase();
						String query = "INSERT INTO words(word, urlid) value ('" + word + "'," + "'"
								+ Integer.toString(this.urlid) + "');";
						System.out.println(query);
						stat.executeUpdate(query);
					}
				}
				
				terminate = true;
			} catch (Exception e) {
				terminate = true;
				e.printStackTrace();
			}
	}

	public boolean done() {
		return terminate;
	}
}
