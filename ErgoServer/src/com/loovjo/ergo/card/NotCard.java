package com.loovjo.ergo.card;

public class NotCard implements Card {

	@Override
	public String getName() {
		return "Not";
	}

	@Override
	public String getShortName() {
		return "~";
	}
	
	public String toString() {
		return getClass().getSimpleName();
	}

}
