package com.loovjo.ergoServer;

import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Scanner;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

import com.loovjo.ergo.card.AndCard;
import com.loovjo.ergo.card.Card;
import com.loovjo.ergo.card.ErgoCard;
import com.loovjo.ergo.card.LeftParenthesesCard;
import com.loovjo.ergo.card.NotCard;
import com.loovjo.ergo.card.OrCard;
import com.loovjo.ergo.card.RightParenthesesCard;
import com.loovjo.ergo.card.ThenCard;
import com.loovjo.ergo.card.VariableCard;
import com.loovjo.ergoServer.evaluator.Evaluator;

public class Game {

	public static final int PREMESIS_SIZE = 4;

	public CopyOnWriteArrayList<Player> players;
	public ServerSocket socket;

	public int subTurn = 0;

	public int turn;

	public ArrayList<ArrayList<Card>> premises = new ArrayList<ArrayList<Card>>(PREMESIS_SIZE);

	public ArrayList<Card> deck = new ArrayList<Card>();

	public Game(CopyOnWriteArrayList<ServerClient> clients, ServerSocket socket) {

		setupPlayableCards();

		this.players = new CopyOnWriteArrayList<Player>();
		for (ServerClient s : clients) {
			this.players.add(new Player(s));
		}
		for (int i = 0; i < PREMESIS_SIZE; i++)
			premises.add(new ArrayList<Card>());

		this.socket = socket;

		// Place cards in deck
		for (int i = 0; i < 4; i++) {
			for (char c : Card.variables.toCharArray())
				deck.add(new VariableCard(c));
		}
		for (int i = 0; i < 4; i++) {
			deck.add(new RightParenthesesCard());
		}
		for (int i = 0; i < 4; i++) {
			deck.add(new LeftParenthesesCard());
		}
		for (int i = 0; i < 4; i++) {
			deck.add(new AndCard());
		}
		for (int i = 0; i < 4; i++) {
			deck.add(new OrCard());
		}
		for (int i = 0; i < 4; i++) {
			deck.add(new ThenCard());
		}
		for (int i = 0; i < 6; i++) {
			deck.add(new NotCard());
		}
		for (int i = 0; i < 3; i++) {
			deck.add(new ErgoCard());
		}
		Collections.shuffle(deck);
	}

	private void setupPlayableCards() {

	}

	public Game(String premises) {
		setupPlayableCards();

		for (String premis : premises.split("\\|")) {
			this.premises.add(new ArrayList<Card>());
			for (char chr : premis.toCharArray()) {
				for (Card card : Card.cardTypes) {
					if (card.getShortName().equals(chr + ""))
						this.premises.get(this.premises.size() - 1).add(card);
				}
			}
		}

	}

	public void start() {
		System.out.println("Game starting.");
		System.out.println("Dealing cards...");
		for (Player p : players) {
			for (int i = 0; i < 5; i++)
				p.cards.add(deck.remove(deck.size() - 1));
		}
		new Thread(new Runnable() {

			@Override
			public void run() {
				while (true)
					evaluateCommand(new Scanner(System.in).nextLine());
			}
		}).start();
		while (true) {
			for (int i = 0; i < players.size(); i++)
				if (i != turn)
					sendGameState(players.get(i));
			if (playTurn()) {
				subTurn++;
				if (subTurn >= 2) {
					subTurn = 0;
					turn++;
				}
			}
		}
	}

	private void sendGameState(Player current) {
		current.send("gs");
		String send = "";
		for (ArrayList<Card> premis : premises) {
			send += premis.stream().map(c -> c.getShortName()).collect(Collectors.joining("")) + "|";
		}
		send = send.substring(0, send.length() - 1);
		System.out.println(send);
		current.send(current.cards.stream().map(c -> c.getShortName()).collect(Collectors.joining(",")));
		current.send(send);

	}

	private boolean playTurn() {

		Player current = players.get(turn % players.size());
		System.out.println(current + "'s turn.");
		while (current.cards.size() < 7 - subTurn)
			current.cards.add(deck.remove(deck.size() - 1));

		System.out.println("Cards: " + current.cards);

		sendGameState(current);

		current.send("P");

		String c = current.waitForLine();
		
		if (c.startsWith("opcommand ")) {
			String command = c.substring(c.indexOf(" ") + 1);
			System.out.println("Command: " + command);
			evaluateCommand(command);
			return false;
		}

		if (c.startsWith("discard ")) {
			String[] split = c.split(" ");
			String cardName = split[1];
			Card card = null;
			for (Card carD : current.cards)
				if (carD.getName().toLowerCase().equals(cardName.toLowerCase())
						|| carD.getShortName().toLowerCase().equals(cardName.toLowerCase()))
					card = carD;
			if (card == null)
				return false;
			current.cards.remove(card);
		} else {
			String[] split = c.split(",");
			String cardName = split[0];
			Card card = null;
			for (Card carD : current.cards)
				if (carD.getName().toLowerCase().equals(cardName.toLowerCase())
						|| carD.getShortName().toLowerCase().equals(cardName.toLowerCase()))
					card = carD;
			if (card == null)
				return false;
			int y = Integer.parseInt(split[1]);
			int x = Integer.parseInt(split[2]);
			premises.get(y).add(x, card);
			current.cards.remove(card);
		}

		for (ArrayList<Card> premis : premises) {
			System.out.println(premis.stream().map(card -> card.getShortName()).collect(Collectors.joining()));
		}

		System.out.println("Truth:");
		for (Entry<String, Integer> truths : getProvenVariables().entrySet()) {
			System.out.println(truths.getKey() + ": "
					+ (truths.getValue() == 0 ? "False" : truths.getValue() == 1 ? "Unsure" : "True"));
		}
		return true;
	}

	private void evaluateCommand(String command) {
		if (command.startsWith("kick")) {
			Player pl = players.stream().filter(player -> player.getName().equals(command.split(" ")[1])).findFirst()
					.get();
			pl.send("You got kicked!");
			players.remove(pl);
		}
		if (command.startsWith("giveCard")) {
			players.stream().filter(player -> player.getName().equals(command.split(" ")[1])).findFirst().get().cards
					.add(Card.getCardFromSign(command.split(" ")[2]));
		}
	}

	public HashMap<String, Integer> getProvenVariables() {
		boolean debug = false;

		HashMap<String, ArrayList<Integer>> proofs = new HashMap<String, ArrayList<Integer>>();
		for (char c : Card.variables.toCharArray())
			proofs.put(c + "", new ArrayList<Integer>());

		for (int i = 0; i < 16; i++) {
			String bin = Integer.toBinaryString(i);
			while (bin.length() < 4)
				bin = "0" + bin;

			boolean isTrue = true;
			HashMap<String, String> vars = new HashMap<String, String>();

			for (int j = 0; j < Card.variables.length(); j++)
				vars.put(Card.variables.charAt(j) + "", bin.charAt(j) + "");
			if (debug) {
				System.out.println(vars);
				System.out.println(Card.variables);
				System.out.println(bin);
			}
			for (ArrayList<Card> premis : premises) {
				String e = premis.stream().map(card -> card.getShortName()).collect(Collectors.joining(""));
				if (debug)
					System.out.println(e);
				if (e.isEmpty())
					continue;

				isTrue = isTrue && Evaluator.isTrue(e, vars);
			}
			if (isTrue) {
				if (debug)
					System.out.println("True");
				for (int j = 0; j < bin.length(); j++) {
					char variable = Card.variables.charAt(j);
					proofs.get(variable + "").add(Integer.parseInt(bin.charAt(j) + ""));
				}
			}
		}
		if (debug)
			System.out.println(proofs);
		// 0 = false
		// 1 = unsure
		// 2 = proven
		HashMap<String, Integer> truth = new HashMap<String, Integer>();
		for (Entry<String, ArrayList<Integer>> variable : proofs.entrySet()) {
			int value = -1;
			boolean containsZero = variable.getValue().contains(0);
			boolean containsOne = variable.getValue().contains(1);
			if (containsZero && !containsOne)
				value = 0;
			if (containsZero && containsOne)
				value = 1;
			if (!containsZero && containsOne)
				value = 2;
			truth.put(variable.getKey(), value);
		}
		return truth;
	}

}
