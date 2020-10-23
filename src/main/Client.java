package main;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Client implements Runnable {
	private Socket socket;
	private Thread t;
	private String username = "";
	private String password = "";
	private int id = 0;
	// receving and sending messages
	private BufferedReader in;
	private PrintWriter out;
	private String message = "";
	// database
	Connection conn = null;
	final String hostname = "localhost";
	final String port = "3306";
	final String dbname = "RTTSG";
	final String dbuser = "admin";
	final String dbpassword = "Hirschgeweih1";

//Constructor ------------------------------------------------------------------------------------------
	public Client(Socket socket) {
		this.socket = socket;
		t = new Thread(this);
		t.start();
	}

//run --------------------------------------------------------------------------------------------------
	@Override
	public void run() {
		try {
			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
		} catch (IOException e) {
			System.out.println("error while creating in and outputstreams | Class: Client");
			e.printStackTrace();
		}
		askForLoginCredentials();
		waitForCredentials();
		createDatabaseConnection();
		checkLoginCredentials();

		try {
			in.close();
			out.close();
		} catch (IOException e) {
			System.out.println("error while closeing in and outputstreams | Class: Client");
			e.printStackTrace();
		}
	}

//methods ----------------------------------------------------------------------------------------------
	public void waitForCredentials() {
		try {
			System.out.println("waiting for username and password...");
			while (!in.ready()) {
				try {
					Thread.sleep(250); //waiting before checking for credentials again(lower power usage).
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			username = in.readLine();
			password = in.readLine();
		} catch (IOException e) {
			System.out.println("error while receving credentials | Class: Client");
			e.printStackTrace();
		} 
	}

	public void checkLoginCredentials() {
		String statement = "select id, password from User where username like '" + username + "';";
		try {
			ResultSet rs = conn.createStatement().executeQuery(statement);
			rs.next();
			if (rs.getString("password").equals(password)) {
				System.out.println("logged in");
				id = rs.getInt("id");
				password = rs.getString("password");
			} else {
				System.out.println("wrong login credentials");
				waitForCredentials();
			}
		} catch (SQLException e) {
			System.out.println("wrong login credentials error | Class: Client");
			waitForCredentials();
			e.printStackTrace();
		}

	}

	public void createDatabaseConnection() {
		String url = "jdbc:mysql://" + hostname + ":" + port + "/" + dbname
				+ "?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=Europe/Berlin";
		try {
			conn = DriverManager.getConnection(url, dbuser, dbpassword);
			System.out.println("connected to db");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void askForLoginCredentials() {
		out.write("ready for login");
		out.flush();
	}

//getter-setter ----------------------------------------------------------------------------------------

}
