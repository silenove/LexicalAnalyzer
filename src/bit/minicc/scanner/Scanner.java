package bit.minicc.scanner;

import java.util.List;
import java.util.Map;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;


import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

public class Scanner {

	private Set<String> keyword; // 关键字集合
	private int next;
	private int state;// 记录自动机的状态
	private int count;
	
	private List<Word> wordList;
	private List<Error> errorList;

	public Scanner() {

		// 指向代码开始的位置
		next = 0;
		
		// 状态初始为0
		state = 0;
		
		count = 1;
		
		wordList = new ArrayList<>();
		errorList = new ArrayList<>();
	}


	

	private void untread() {// 回退一个以读进的字符
		next--;
	}
	
	private boolean isChar(char c){
		if(c >= 'a' && c <='z' || c >= 'A' && c <= 'Z'){
			return true;
		}else{
			return false;
		}
	}
	
	// 输出词法分析结果 
	public void outputWordList(String output) throws IOException{
		Element root = (new Element("ScanResult"));
		Document Doc = new Document(root);
		Element tokens = new Element("tokens");
		Element errors = new Element("Errors");
		root.addContent(tokens);
		root.addContent(errors);
		
		//输出识别单词信息
		for(int i = 0; i < wordList.size(); i++){
		
			 Word word = (Word)wordList.get(i);
			 Element elements = new Element("token");
			 elements.addContent((new Element("number")).setText((new Integer(word.getNumber())).toString()));
			 elements.addContent((new Element("value")).setText(word.getValue()));
			 elements.addContent((new Element("type")).setText(word.getType()));
			 elements.addContent((new Element("line")).setText((new Integer(word.getLine())).toString()));
			 elements.addContent((new Element("valid")).setText((new Boolean(word.isValid())).toString()));
			 tokens.addContent(elements);
		}

		if(true)
		{
			//输出错误信息
			for(int i = 0; i < errorList.size(); i++){	
			    Error error = (Error)errorList.get(i);
			    Element elements = new Element("error");
			    elements.addContent((new Element("number")).setText((new Integer(error.getNumber())).toString()));
			    elements.addContent((new Element("info")).setText(error.info));
			    elements.addContent((new Element("line")).setText((new Integer(error.line)).toString()));
			    elements.addContent((new Element("value")).setText(error.getValue()));
			    errors.addContent(elements);
			}

		}
		Format format = Format.getPrettyFormat();
		format.setEncoding("utf-8");
		XMLOutputter XMLOut = new XMLOutputter(format);
		XMLOut.output(Doc, new FileOutputStream(output));
	}
	
	private boolean isNumber(char c){
		if(c >= '0' && c <= '9'){
			return true;
		}else{
			return false;
		}
	}
	
	
		public void analyzeWord(String filename) throws IOException
		{
			// 从文件中读取数据,并进行分析
			BufferedReader readstr = new BufferedReader(new InputStreamReader(new BufferedInputStream(new FileInputStream(filename)), "utf-8"));
			String str = "";
			int line;
			for (line = 1; (str = readstr.readLine()) != null; line++) 
			{
				next = 0;
				scanning(str.trim()+" ", line); // 进行词法分析
				
			
			}
			Word word = new Word(count,"#","#",line - 1,true);
			wordList.add(word);
			readstr.close();
			
			
			System.out.println("Finished");
		}

	//利用有限自动机进行单词识别
	public void scanning(String strCode,int line) {
		char c;
		Word getWord = null;
		Error error = null;
		String word = "";
		while (next < strCode.length()) {
			c = strCode.charAt(next++);
			System.out.println(c);
			switch (state) {
			case 0:	
				word = word + c;
				word = word.trim();
				if (isChar(c) || (c == '_')) {
					state = 1;
					break;
				} else if (isNumber(c)) {
					state = 3;
					break;
				} else {
					switch (c) {
					case '=':
						state = 5;
						break;
					case '+':
						state = 8;
						break;
					case '-':
						state = 12;
						break;
					case '*':
						state = 16;
						break;
					case '/':
						state = 19;
						break;
					case '(':
						state = 22;
						break;
					case ')':
						state = 23;
						break;
					case '{':
						state = 24;
						break;
					case '}':
						state = 25;
						break;
					case '!':
						state = 26;
						break;
					case '&':
						state = 27;
						break;
					case '|':
						state = 30;
						break;
					case '\'':
						state = 33;
						break;
					case '"':
						state = 35;
						break;
					case ';':
						state = 34;
						break;
					case ',':
						state = 38;
						break;
					case '>':
						state = 39;
						break;
					case '<':
						state = 40;
						break;

					}
				}
				break;
			case 1:
				if (isChar(c) || isNumber(c) || (c == '_')) {
					state = 1;
					word = word + c;
					break;
				} else {
					state = 2;
					untread();
					break;
				}
			case 2:
				if(Word.isKeyword(word)){ //关键字
					getWord = new Word();
					getWord.setNumber(count++);
					getWord.setValue(word);
					getWord.setType(Word.getType(word));
					getWord.setLine(line);
					getWord.setValid(true);
					wordList.add(getWord);
				}else{
					getWord = new Word();  //标示符
					getWord.setNumber(count++);
					getWord.setValue(word);
					getWord.setType(Word.getType(word));
					getWord.setLine(line);
					getWord.setValid(true);
					wordList.add(getWord);
				}
				untread();
				word = "";
				state = 0;
				break;
			case 3:
				if(isNumber(c)||(c == '.')){
					state = 3;
					word = word + c;
				}else if(isChar(c)){
					state = 50;
					word =word + c;
				}else{
					state = 4;
					untread();
				}
				break;
			case 4:
				if(word.contains(".")){ //float常量
					getWord = new Word();
					getWord.setNumber(count++);
					getWord.setValue(word);
					getWord.setType("float");
					getWord.setLine(line);
					getWord.setValid(true);
					wordList.add(getWord);
				}else{
					getWord = new Word(); //int常量
					getWord.setNumber(count++);
					getWord.setValue(word);
					getWord.setType("int");
					getWord.setLine(line);
					getWord.setValid(true);
					wordList.add(getWord);
				}
				untread();
				word = "";
				state = 0;
				break;
			case 5:
				if(c == '='){
					state = 7;
					word = word + c;
				}else{
					state = 6;
					untread();
				}
				break;
			case 6: // =
				getWord = new Word();
				getWord.setNumber(count++);
				getWord.setValue(word);
				getWord.setType(Word.getType(word));
				getWord.setLine(line);
				getWord.setValid(true);
				wordList.add(getWord);
				untread();
				word = "";
				state = 0;
				break;
			case 7: // ==
				getWord = new Word();
				getWord.setNumber(count++);
				getWord.setValue(word);
				getWord.setType(Word.getType(word));
				getWord.setLine(line);
				getWord.setValid(true);
				wordList.add(getWord);
				untread();
				word = "";
				state = 0;
				break;
			case 8:
				if(c == '+'){
					state = 10;
					word = word + c;
				}else if(c == '='){
					state = 11;
					word = word + c;
				}else{
					state = 9;
					untread();
				}
				break;
			case 9:// +
				getWord = new Word();
				getWord.setNumber(count++);
				getWord.setValue(word);
				getWord.setType(Word.getType(word));
				getWord.setLine(line);
				getWord.setValid(true);
				wordList.add(getWord);
				untread();
				word = "";
				state = 0;
				break;
			case 10:// ++
				getWord = new Word();
				getWord.setNumber(count++);
				getWord.setValue(word);
				getWord.setType(Word.getType(word));
				getWord.setLine(line);
				getWord.setValid(true);
				wordList.add(getWord);
				untread();
				word = "";
				state = 0;
				break;
			case 11:// +=
				getWord = new Word();
				getWord.setNumber(count++);
				getWord.setValue(word);
				getWord.setType(Word.getType(word));
				getWord.setLine(line);
				getWord.setValid(true);
				wordList.add(getWord);
				untread();
				word = "";
				state = 0;
				break;
			case 12:
				if(c == '-'){
					state = 14;
					word = word + c;
				}else if(c == '='){
					state = 15;
					word = word + c;
				}else{
					state = 13;
					untread();
				}
				break;
			case 13:// -
				getWord = new Word();
				getWord.setNumber(count++);
				getWord.setValue(word);
				getWord.setType(Word.getType(word));
				getWord.setLine(line);
				getWord.setValid(true);
				wordList.add(getWord);
				untread();
				word = "";
				state = 0;
				break;
			case 14:// --
				getWord = new Word();
				getWord.setNumber(count++);
				getWord.setValue(word);
				getWord.setType(Word.getType(word));
				getWord.setLine(line);
				getWord.setValid(true);
				wordList.add(getWord);
				untread();
				word = "";
				state = 0;
				break;
			case 15:// -=
				getWord = new Word();
				getWord.setNumber(count++);
				getWord.setValue(word);
				getWord.setType(Word.getType(word));
				getWord.setLine(line);
				getWord.setValid(true);
				wordList.add(getWord);
				untread();
				word = "";
				state = 0;
				break;
			case 16:
				if(c == '='){
					state = 18;
					word = word + c;
				}else{
					state = 17;
					untread();
				}
				break;
			case 17:// *
				getWord = new Word();
				getWord.setNumber(count++);
				getWord.setValue(word);
				getWord.setType(Word.getType(word));
				getWord.setLine(line);
				getWord.setValid(true);
				wordList.add(getWord);
				untread();
				word = "";
				state = 0;
				break;
			case 18:// *=
				getWord = new Word();
				getWord.setNumber(count++);
				getWord.setValue(word);
				getWord.setType(Word.getType(word));
				getWord.setLine(line);
				getWord.setValid(true);
				wordList.add(getWord);
				untread();
				word = "";
				state = 0;
				break;
			case 19:
				if(c == '/'){
					state = 21;
					word = word + c;
				}else{
					state = 20;
					untread();
				}
				break;
			case 20: // /
				getWord = new Word();
				getWord.setNumber(count++);
				getWord.setValue(word);
				getWord.setType(Word.getType(word));
				getWord.setLine(line);
				getWord.setValid(true);
				wordList.add(getWord);
				untread();
				word = "";
				state = 0;
				break;
			case 21:// /=
				getWord = new Word();
				getWord.setNumber(count++);
				getWord.setValue(word);
				getWord.setType(Word.getType(word));
				getWord.setLine(line);
				getWord.setValid(true);
				wordList.add(getWord);
				untread();
				word = "";
				state = 0;
				break;
			case 22:// (
				getWord = new Word();
				getWord.setNumber(count++);
				getWord.setValue(word);
				getWord.setType(Word.getType(word));
				getWord.setLine(line);
				getWord.setValid(true);
				wordList.add(getWord);
				untread();
				word = "";
				state = 0;
				break;
			case 23:// )
				getWord = new Word();
				getWord.setNumber(count++);
				getWord.setValue(word);
				getWord.setType(Word.getType(word));
				getWord.setLine(line);
				getWord.setValid(true);
				wordList.add(getWord);
				untread();
				word = "";
				state = 0;
				break;
			case 24:// {
				getWord = new Word();
				getWord.setNumber(count++);
				getWord.setValue(word);
				getWord.setType(Word.getType(word));
				getWord.setLine(line);
				getWord.setValid(true);
				wordList.add(getWord);
				untread();
				word = "";
				state = 0;
				break;
			case 25:// }
				getWord = new Word();
				getWord.setNumber(count++);
				getWord.setValue(word);
				getWord.setType(Word.getType(word));
				getWord.setLine(line);
				getWord.setValid(true);
				wordList.add(getWord);
				untread();
				word = "";
				state = 0;
				break;
			case 26:
				if(c == '='){
					state = 27;
					word = word + c;
				}else{
					state = 41;
					untread();
				}
				break;
			case 27:// !=
				getWord = new Word();
				getWord.setNumber(count++);
				getWord.setValue(word);
				getWord.setType(Word.getType(word));
				getWord.setLine(line);
				getWord.setValid(true);
				wordList.add(getWord);
				untread();
				word = "";
				state = 0;
				break;
			case 28:
				if(c == '&'){
					state = 30;
					word = word + c;
				}else{
					state = 29;
					untread();
				}
				break;
			case 29:// &
				getWord = new Word();
				getWord.setNumber(count++);
				getWord.setValue(word);
				getWord.setType(Word.getType(word));
				getWord.setLine(line);
				getWord.setValid(true);
				wordList.add(getWord);
				untread();
				word = "";
				state = 0;
				break;
			case 30:// &&
				getWord = new Word();
				getWord.setNumber(count++);
				getWord.setValue(word);
				getWord.setType(Word.getType(word));
				getWord.setLine(line);
				getWord.setValid(true);
				wordList.add(getWord);
				untread();
				word = "";
				state = 0;
				break;
			case 31:
				if(c == '|'){
					state = 33;
					word = word + c;
				}else{
					state = 32;
					untread();
				}
				break;
			case 32:// |
				getWord = new Word();
				getWord.setNumber(count++);
				getWord.setValue(word);
				getWord.setType(Word.getType(word));
				getWord.setLine(line);
				getWord.setValid(true);
				wordList.add(getWord);
				untread();
				word = "";
				state = 0;
				break;
			case 33:// ||
				getWord = new Word();
				getWord.setNumber(count++);
				getWord.setValue(word);
				getWord.setType(Word.getType(word));
				getWord.setLine(line);
				getWord.setValid(true);
				wordList.add(getWord);
				untread();
				word = "";
				state = 0;
				break;
			case 34:// ;
				getWord = new Word();
				getWord.setNumber(count++);
				getWord.setValue(word);
				getWord.setType(Word.getType(word));
				getWord.setLine(line);
				getWord.setValid(true);
				wordList.add(getWord);
				untread();
				word = "";
				state = 0;
				break;
			case 35:
				if(c == '"'){
					word = word + c;
					state = 37;
				}
				else{
					word = word + c;
					state = 35;
				}
				break;
			case 37://字符串	
				getWord = new Word();
				getWord.setNumber(count++);
				getWord.setValue(word);
				getWord.setType("string");
				getWord.setLine(line);
				getWord.setValid(true);
				wordList.add(getWord);
				untread();
				state = 0;
				word = "";
				break;
			case 38:// ,
				getWord = new Word();
				getWord.setNumber(count++);
				getWord.setValue(word);
				getWord.setType(Word.getType(word));
				getWord.setLine(line);
				getWord.setValid(true);
				wordList.add(getWord);
				
				word = "";
				state = 0;
				untread();
				break;
			case 39:
				if(c == '='){
					state = 42;
					word = word + c;
				}else{
					state = 43;
					untread();
				}
				break;
			case 40:
				if(c == '='){
					state = 44;
					word = word + c;
				}else{
					state = 45;	
					untread();
				
				}
				break;
			case 41:// !
				getWord = new Word();
				getWord.setNumber(count++);
				getWord.setValue(word);
				getWord.setType(Word.getType(word));
				getWord.setLine(line);
				getWord.setValid(true);
				wordList.add(getWord);
				
				untread();
				word = "";
				state = 0;
				break;
			case 42:// >=
				getWord = new Word();
				getWord.setNumber(count++);
				getWord.setValue(word);
				getWord.setType(Word.getType(word));
				getWord.setLine(line);
				getWord.setValid(true);
				wordList.add(getWord);
				
				untread();
				word = "";
				state = 0;
				break;
			case 43:// >
				getWord = new Word();
				getWord.setNumber(count++);
				getWord.setValue(word);
				getWord.setType(Word.getType(word));
				getWord.setLine(line);
				getWord.setValid(true);
				wordList.add(getWord);
				
				untread();
				word = "";
				state = 0;
				break;
			case 44:// <=
				getWord = new Word();
				getWord.setNumber(count++);
				getWord.setValue(word);
				getWord.setType(Word.getType(word));
				getWord.setLine(line);
				getWord.setValid(true);
				wordList.add(getWord);
				
				untread();
				word = "";
				state = 0;
				break;
			case 45:// <
				getWord = new Word();
				getWord.setNumber(count++);
				getWord.setValue(word);
				getWord.setType(Word.getType(word));
				getWord.setLine(line);
				getWord.setValid(true);
				wordList.add(getWord);
				
				untread();
				word = "";
				state = 0;
				break;
				
			case 50:// 错误处理
				if(c != ' '){
					word = word + c;
					state = 50;
				}else{
					getWord = new Word();
					getWord.setNumber(count++);
					getWord.setValue(word);
					getWord.setType(Word.getType(word));
					getWord.setLine(line);
					getWord.setValid(true);
					wordList.add(getWord);
					
					error = new Error(getWord.getNumber(), "非法字符", line, getWord.getValue());
					errorList.add(error);
					
				}
						
			default:
				break;
			}
		}
		
		
	}


	public static void main(String[] args) throws IOException {
		
		Scanner scanner = new Scanner();
		scanner.analyzeWord("input.c");
		scanner.outputWordList("outputScanner.xml");
	}

}
