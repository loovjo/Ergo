package com.loovjo.ergo.card;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.function.Supplier;

public interface Card {

	static String variables = "ABCD";

	static ArrayList<Card> cardTypes = new ArrayList<Card>(Arrays.asList(new Card[] { new AndCard(), new OrCard(),
			new NotCard(), new LeftParenthesesCard(), new ErgoCard(), new RightParenthesesCard(), new ThenCard(),
			new VariableCard(variables.charAt(0)), new VariableCard(variables.charAt(1)),
			new VariableCard(variables.charAt(2)), new VariableCard(variables.charAt(3)) }));

	float UUID = (float) Math.random();
	
	public static Card getCardFromSign(String sign) {
		return cardTypes.stream().filter(c -> c.getShortName().equals(sign) || c.getName().equals(sign)).findFirst()
				.orElseGet(new Supplier<Card>() {
					@Override
					public Card get() {
						return null;
					}
				});
	}
	
	public boolean equals(Object obj);
	
	public String getName();

	public String getShortName();
}
