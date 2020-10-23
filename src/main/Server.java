package main;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;

public class Server implements Runnable {
	private Thread t;
	private ServerSocket server;
	private int port = 3455;
	private ArrayList<Client> clients = new ArrayList<>();
	
//Constructor ------------------------------------------------------------------------------------------
	public Server() {
		t = new Thread(this);
		t.start();
	}

//run --------------------------------------------------------------------------------------------------
	@Override
	public void run() {
		startServer();
		System.out.println("Server started");
		while (true) {
			System.out.println("waiting for new Client to connect...");
			waitforClients();
			System.out.println("client connected");
		}
	}
//methods ----------------------------------------------------------------------------------------------
	private void startServer() {
		try {
			server = new ServerSocket(port);
		} catch (IOException e) {
			System.out.println("error while creating server | Class: Server");
			e.printStackTrace();
		}
	}
	
	//if client accepted the client gets to do its stuff in its own thread
	private void waitforClients() {
		try {
			clients.add(new Client(server.accept()));
		} catch (IOException e) {
			System.out.println("error while adding new Client | Class: Server");
			e.printStackTrace();
		}
	}
//getter-setter ----------------------------------------------------------------------------------------

}
