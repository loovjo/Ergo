package com.loovjo.ergoServer;

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.util.ArrayList;

import com.loovjo.ergo.card.Card;

public class Player extends ServerClient {

	public ArrayList<Card> cards = new ArrayList<Card>();

	public Player(BufferedReader in, PrintWriter out) {
		super(in, out);
	}

	public Player(ServerClient s) {
		this(s.getIn(), s.getOut());
		setName(s.getName());
		setVariable(s.getVariable());
	}

}
