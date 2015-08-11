package com.loovjo.ergo.card;

public class AndCard implements Card {

	@Override
	public String getName() {
		return "And";
	}

	@Override
	public String getShortName() {
		return "*";
	}
	
	public String toString() {
		return getClass().getSimpleName();
	}
	
}
