package com.loovjo.ergoServer.evaluator;

import java.util.HashMap;
import java.util.Map.Entry;

public class Evaluator {

	public static boolean isTrue(String exp, HashMap<String, String> variables) {
		for (Entry<String, String> en : variables.entrySet())
			exp = exp.replaceAll(en.getKey(), en.getValue());
		return isTrue(exp);
	}

	public static int isTrueInt(String exp, HashMap<String, String> vars) {
		if (exp.isEmpty())
			return -1;
		return isTrue(exp, vars) ? 1 : 0;
	}

	public static boolean isTrue(String exp) {
		String order = "*V~";
		if (exp.startsWith("(") && exp.endsWith(")")) {
			int level = 0;
			boolean levelUnderZero = false;
			for (int i = 0; i < exp.length() - 1; i++) {
				if (exp.charAt(i) == '(')
					level++;
				else if (exp.charAt(i) == ')')
					level--;
				if (level <= 0) {
					levelUnderZero = true;
					break;
				}
			}
			if (!levelUnderZero)
				exp = exp.substring(1, exp.length() - 1);
		}
		if (exp.startsWith("~") && exp.length() == 2)
			return !isTrue(exp.substring(1));
		if (exp.equals("1"))
			return true;
		if (exp.equals("0"))
			return false;

		for (char o : order.toCharArray()) {
			int level = 0;
			boolean found = false;
			for (int i = 0; i < exp.length(); i++) {
				if (exp.charAt(i) == '(')
					level++;
				else if (exp.charAt(i) == ')')
					level--;
				if (level == 0 && exp.charAt(i) == o) {
					String firstPart = exp.substring(0, i);
					String secondPart = exp.substring(i + 1);
					found = true;
					if (o == '*')
						return isTrue(firstPart) && isTrue(secondPart);
					if (o == 'V')
						return isTrue(firstPart) || isTrue(secondPart);
					if (o == '~')
						return !isTrue(secondPart);
					return false;
				}
			}
			if (found)
				break;
		}

		return false;
	}

}
