package com.loovjo.ergo.card;

public class VariableCard implements Card {
	
	public char variable;
	
	public VariableCard(char variable) {
		this.variable = variable;
	}
	
	
	@Override
	public String getName() {
		return variable + " variable";
	}

	@Override
	public String getShortName() {
		return variable + "";
	}
	
	public String toString() {
		return getName();
	}
	
}
