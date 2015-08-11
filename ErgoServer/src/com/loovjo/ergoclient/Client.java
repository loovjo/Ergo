package com.loovjo.ergoclient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;

public class Client {

	private Socket socket;
	private BufferedReader in;
	private PrintWriter out;

	public char variable;
	public String name;

	public static boolean autoAnswear = true;

	public Client(String ip, String name, char variable) {
		this(name, variable);
		try {
			socket = new Socket(ip, ClientMain.PORT);
			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			out = new PrintWriter(socket.getOutputStream());
		} catch (IOException e) {
			e.printStackTrace();
		}
		setupInformation();
	}

	public Client(String name, char variable) {
		this.name = name;
		this.variable = variable;
	}

	public Socket getSocket() {
		return socket;
	}

	public void setSocket(Socket socket) {
		this.socket = socket;
	}

	public char getVariable() {
		return variable;
	}

	public void setVariable(char variable) {
		this.variable = variable;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	private void setupInformation() {
		while (true) {
			String packet = recieveLine();
			if (packet.equals("n"))
				send(name);
			if (packet.equals("a"))
				send("" + variable);
			if (packet.equals(";"))
				break;
		}
	}

	public void send(String message) {
		out.println(message);
		out.flush();

	}

	public String recieveLine() {
		try {
			String line = in.readLine();
			if (line.equals("ping")) {
				send("pong");
				return recieveLine();
			}

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

}
