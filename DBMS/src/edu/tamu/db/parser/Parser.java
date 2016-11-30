package edu.tamu.db.parser;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class Parser {
	public static void main(String[] args) {
		Parser P = new Parser();
		System.out.println(P.columnNameCheck("arhu.bL"));
	}
	private static final String LETTER_PATTERN  = "[a-zA-Z]";
	private static final String DIGIT_PATTERN = "[0-9]";
	private static final String START_PATTERN = "^";
	private static final String END_PATTERN = "$";
	private static final String COMP_OP_PATTERN = "<|>|=";
	private static final String ZERO_OR_MORE_PATTERN = "+";
	private static final String OR_PATTERN = "|";
	private static final String ONE_OR_MORE_PATTERN = "*";
	private static final String DOT = ".";
	boolean letterCheck(String query){
		Pattern letterRegex = Pattern.compile(START_PATTERN+LETTER_PATTERN+END_PATTERN);
		Matcher m = letterRegex.matcher(query);
		if (m.find())
        {
			return true;
        }
		return false;
	}
	
	boolean digitCheck(String query){
		Pattern digitRegex = Pattern.compile(START_PATTERN+DIGIT_PATTERN+END_PATTERN);
		Matcher m = digitRegex.matcher(query);
		if (m.find())
        {
			return true;
        }
		return false;
	}
	
	boolean booleanegerCheck(String query){
		Pattern digitRegex = Pattern.compile(START_PATTERN+DIGIT_PATTERN+ZERO_OR_MORE_PATTERN+END_PATTERN);
		Matcher m = digitRegex.matcher(query);
		if (m.find())
        {
			return true;
        }
		return false;
	}
	
	boolean comparatorOperatorCheck(String query){
		Pattern digitRegex = Pattern.compile(START_PATTERN+COMP_OP_PATTERN+END_PATTERN);
		Matcher m = digitRegex.matcher(query);
		if (m.find())
        {
			return true;
        }
		return false;
	}
	
	boolean tableNameCheck(String query){
		Pattern digitRegex = Pattern.compile(START_PATTERN+LETTER_PATTERN+DOT+"["+DIGIT_PATTERN+OR_PATTERN+LETTER_PATTERN+"]"+ONE_OR_MORE_PATTERN+END_PATTERN);
		Matcher m = digitRegex.matcher(query);
		if (m.find())
        {
			return true;
        }
		return false;
	}
	
	boolean attributeNameCheck(String query){
		Pattern digitRegex = Pattern.compile(START_PATTERN+LETTER_PATTERN+DOT+"["+DIGIT_PATTERN+OR_PATTERN+LETTER_PATTERN+"]"+ONE_OR_MORE_PATTERN+END_PATTERN);
		Matcher m = digitRegex.matcher(query);
		if (m.find())
        {
			return true;
        }
		return false;
	}
	
	boolean columnNameCheck (String query){
		if(query.contains(".")){
			return (tableNameCheck(query.split("\\.")[0])) && (attributeNameCheck(query.split("\\.")[1]));
		}
		else{
			return attributeNameCheck(query);
		}
	}
	
	boolean literalCheck (String query){
		return true;
	}
}
