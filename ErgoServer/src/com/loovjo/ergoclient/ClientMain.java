package com.loovjo.ergoclient;

import java.util.ArrayList;
import java.util.Scanner;

import javax.swing.JOptionPane;

import com.loovjo.loo2D.MainWindow;
import com.loovjo.loo2D.utils.Vector;

public class ClientMain extends MainWindow {

	public ClientMain(ClientScene s) {
		super("Ergo", s, new Vector(500, 500), true);
		new Thread(s).start();
	}

	public static int PORT = 5749;

	public static ArrayList<Client> players = new ArrayList<Client>();

	public static void main(String[] args) {
		if (args.length == 0) {
			String name = JOptionPane.showInputDialog("Name: ");
			String variable = JOptionPane.showInputDialog("Variable: ");
			String address = JOptionPane.showInputDialog("Address: (optional)");
			main(new String[] { name, variable, address });
			return;
		}
		String ip = "localhost";
		if (args.length == 3)
			ip = args[2];
		Client cl = new Client(ip, args[0], args[1].charAt(0));
		new Thread(new Runnable() {

			@Override
			public void run() {
				Scanner s = new Scanner(System.in);
				while (true) {
					cl.send(s.nextLine());
				}
			}
		}).start();
		new ClientMain(new ClientScene(cl));
	}
}
