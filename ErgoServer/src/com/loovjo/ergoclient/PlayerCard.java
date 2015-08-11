package com.loovjo.ergoclient;

import com.loovjo.ergo.card.Card;
import com.loovjo.loo2D.utils.Vector;

public class PlayerCard {
	
	private Card card;
	private Vector pos;
	
	public PlayerCard(Card card, Vector pos) {
		this.card = card;
		this.pos = pos;
	}
	public Card getCard() {
		return card;
	}
	public void setCard(Card card) {
		this.card = card;
	}
	public Vector getPos() {
		return pos;
	}
	public void setPos(Vector pos) {
		this.pos = pos;
	}
	
	
}
