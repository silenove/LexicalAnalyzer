package bit.minicc.scanner;

import java.util.ArrayList;
import java.util.List;

public class Word {
	
	private int number;
	private String value;
	private String type;
	private int line;
	private boolean valid;
	
	public static List<String> keyword;
	public static List<String> separator;
	public static List<String> operator;
	public Word(int number, String value, String type, int line, boolean valid) {
		super();
		this.number = number;
		this.value = value;
		this.type = type;
		this.line = line;
		this.valid = valid;
	}
	public Word() {
		super();
	}
	public int getNumber() {
		return number;
	}
	public void setNumber(int number) {
		this.number = number;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public int getLine() {
		return line;
	}
	public void setLine(int line) {
		this.line = line;
	}
	public boolean isValid() {
		return valid;
	}
	public void setValid(boolean valid) {
		this.valid = valid;
	}
	
	//判断是否为关键字
	public static boolean isKeyword(String word){
		return keyword.contains(word);
	}
	
	//判断是否为操作符
	public static boolean isOperator(String word){
		return operator.contains(word);
	}
	
	//判断是否为分隔符
	public static boolean isSeparator(String word){
		return separator.contains(word);
	}
	
	public static String getType(String word){
		if(isKeyword(word)){
			return new String("keyword");
		}else if(isOperator(word)){
			return new String("operator");
		}else if(isSeparator(word)){
			return new String("separator");
		}else if(word.equals("#")){
			return new String("#");
		}else{
			return "identifier";
		}
	}
	
	
	static{
		keyword = new ArrayList<>();
		separator = new ArrayList<>();
		operator = new ArrayList<>();
		
		operator.add("+");
		operator.add("-");
		operator.add("++");
		operator.add("--");
		operator.add("*");
		operator.add("/");
		operator.add(">");
		operator.add("<");
		operator.add(">=");
		operator.add("<=");
		operator.add("==");
		operator.add("!=");
		operator.add("=");
		operator.add("&&");
		operator.add("||");
		operator.add("!");
		operator.add(".");
		operator.add("?");
		operator.add("|");
		operator.add("&");
		operator.add("%");
		separator.add("(");
		separator.add(")");
		separator.add("{");
		separator.add("}");
		separator.add(";");
		separator.add(",");
		keyword.add("void");
		keyword.add("int");
		keyword.add("char");
		keyword.add("if");
		keyword.add("else");
		keyword.add("while");
		keyword.add("for");
		keyword.add("printf");
		keyword.add("scanf");
		keyword.add("float");
		keyword.add("return");
	}
	
}
