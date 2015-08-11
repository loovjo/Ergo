package com.loovjo.ergoServer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.stream.Collectors;

public class ServerClient {

	private BufferedReader in;
	private PrintWriter out;

	private String name;
	private char variable;

	public ServerClient(BufferedReader in, PrintWriter out) {
		this.in = in;
		this.out = out;
	}

	public static ServerClient createNewConnectionAndAddToServerClientList() {
		Socket socket;
		BufferedReader in = null;
		PrintWriter out = null;
		try {
			socket = ErgoServer.ss.accept();
			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			out = new PrintWriter(socket.getOutputStream());
		} catch (IOException e) {
			e.printStackTrace();
		}
		ServerClient client = new ServerClient(in, out);
		client.setupInformation();
		ErgoServer.connectPlayer(client);
		return client;
	}

	private void setupInformation() {
		send("n");
		name = recieveLine();
		send("a");
		variable = recieveLine().charAt(0);
		send(";");
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public char getVariable() {
		return variable;
	}

	public void setVariable(char variable) {
		this.variable = variable;
	}

	public void send(String message) {
		out.println(message);
		out.flush();
	}

	public String recieveLine() {
		try {
			String line = in.readLine();
			return line;
		} catch (IOException e) {
			return "";
		}
	}

	public String waitForLine() {
		while (true) {
			String line = recieveLine();
			if (!line.isEmpty())
				return line;
		}
	}

	public BufferedReader getIn() {
		return in;
	}

	public void setIn(BufferedReader in) {
		this.in = in;
	}

	public PrintWriter getOut() {
		return out;
	}

	public void setOut(PrintWriter out) {
		this.out = out;
	}

	public String toString() {
		return "ServerClient(" + name + ", " + variable + ")";
	}

}
