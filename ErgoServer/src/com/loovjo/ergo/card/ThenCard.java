package com.loovjo.ergo.card;

public class ThenCard implements Card {

	@Override
	public String getName() {
		return "Then";
	}

	@Override
	public String getShortName() {
		return "⊃";
	}
	
	public String toString() {
		return getClass().getSimpleName();
	}

}
