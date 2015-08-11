package com.loovjo.ergoServer;

import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

import com.loovjo.ergoServer.evaluator.Evaluator;

public class ErgoServer {

	public static int PORT = 5749;
	public static ServerSocket ss;

	public static CopyOnWriteArrayList<ServerClient> clients = new CopyOnWriteArrayList<ServerClient>();

	public static boolean doneFindingPlayers = false;

	public static void main(String[] args) throws Exception {
		/*
		testEvaluator();
		System.exit(0);*/

		System.out.println("Starting server...");
		ss = new ServerSocket(PORT);
		System.out.println("Done.");
		System.out.println("Press enter to begin");

		new Thread(new Runnable() {

			@Override
			public void run() {
				// Wait for enter:
				new Scanner(System.in).nextLine();
				System.out.println("The games begin!");
				doneFindingPlayers = true;
				start();
			}
		}).start();
		new Thread(new Runnable() {

			@Override
			public void run() {
				while (true) {
					try {
						Thread.sleep(5000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					if (!doneFindingPlayers)
						for (ServerClient cl : clients) {
							cl.send("ping");
							String answear = cl.recieveLine();
							if (answear != null && answear.equals("pong")) {
							} else {
								System.out.println(cl + " DIDN'T ANSWEAR! KICKING FOR HAX");
								clients.remove(cl);
								if (doneFindingPlayers) {
									System.out.println(cl + " isn't in anymore. Aborting.");
									System.exit(0);
								}
							}
						}
				}
			}
		}).start();
		while (!doneFindingPlayers) {
			ServerClient.createNewConnectionAndAddToServerClientList();
		}

	}

	private static void testProofs() {
		for (String s : new String[] { "AV(A*B)", "A*(AVB)", "AVB*C", "D*C", "~B*A" }) {
			System.out.print(s + ": ");
			Game g = new Game(s);
			System.out.println(g.getProvenVariables());
		}

	}

	private static void testEvaluator() {
		System.out.println(Evaluator.isTrue("1V0*1"));
		System.out.println(Evaluator.isTrue("(1*0V~0)*(1*~0)"));
		System.out.println(Evaluator.isTrue("1*(1V0)"));
		System.out.println(Evaluator.isTrue("~1"));
	}

	public static void start() {
		for (ServerClient c : clients) {
			c.send("p" + ErgoServer.clients.stream().map(cl -> cl.getName() + ":" + cl.getVariable())
					.collect(Collectors.joining(",")));
		}
		new Game(clients, ss).start();
	}

	public static void connectPlayer(ServerClient client) {
		if (!doneFindingPlayers) {
			System.out.println(
					"A new player has connected. Name: " + client.getName() + ", variable: " + client.getVariable());
			clients.add(client);
		}
	}

}
