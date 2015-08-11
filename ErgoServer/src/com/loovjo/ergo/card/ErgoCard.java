package com.loovjo.ergo.card;

public class ErgoCard implements Card {

	@Override
	public String getName() {
		return "Ergo";
	}

	@Override
	public String getShortName() {
		return "∴";
	}
	
	public String toString() {
		return getClass().getSimpleName();
	}

}
