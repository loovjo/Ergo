package com.loovjo.ergo.card;

public class OrCard implements Card {

	@Override
	public String getName() {
		return "Or";
	}

	@Override
	public String getShortName() {
		return "V";
	}
	
	public String toString() {
		return getClass().getSimpleName();
	}

}
