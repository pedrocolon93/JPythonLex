import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class FileTaker {
	private static class Tuple{
		private String keyword, classification;

		public Tuple(String lexic, String classification) {
			super();
			this.keyword = lexic;
			this.classification = classification;
		}

		public String getClassification() {
			return classification;
		}

		public String getKeyword() {
			return keyword;
		}

		public void setClassification(String classification) {
			this.classification = classification;
		}

		public void setKeyword(String keyword) {
			this.keyword = keyword;
		}

	}


	private static ArrayList<Tuple> indents = new ArrayList<Tuple>();
	private static ArrayList<String> keywordslist = new ArrayList<>();
	private static ArrayList<Tuple> lexics,currentline = new ArrayList<>();
	private static Queue<Tuple> comments = new LinkedList<>(), delimiters= new LinkedList<>(), 
			identifiers= new LinkedList<>(), keywords= new LinkedList<>(), floatlit= new LinkedList<>(), longlit = new LinkedList<>(), intlit = new LinkedList<>(),
			operators= new LinkedList<>(), stringlits= new LinkedList<>(), imaginarylit = new LinkedList<>();
			private static ArrayList<Tuple> lexics2 = new ArrayList<>();
			//IDENTIFIERS DELIMITERS Y OPERATORS
			/**
			 * Analyzes before sending to procesor
			 * @param nextLine
			 */
			private static void analyzer(String nextLine) {
				// TODO Auto-generated method stub
				if(nextLine.equals("    for str in string.split(line):")){
					System.out.println();
				}
				System.out.println("Next line is:"+nextLine+"\n");
				String s1 = indentation(nextLine);

				//Remueve string de la linea
				String s2 = stringLiteral(s1);

				//Remove comments

				String s3 = comment(s2);
				//System.out.println("So far:"+linewithoutstrings);

				String s4 = numLiteral(s3);

				String s5 = operator(s4);
				//System.out.println("So far:"+linewithoutiso);



				String s6  = delimiter(s5);
				//		System.out.println("So far:"+linewithoutisod);

				String s7 = identifier(s6);

				//				System.out.println("So far:"+s7);


				processor(s7, nextLine);

			}


			private static void processor(String analyzedLine, String originalLine) {
				Matcher m = Pattern.compile("(\\$(c|fi|il|li|d|o|s|ii|kw|id|i))").matcher(analyzedLine);
				while(m.find()) {
					//					System.out.println("Meta found");
					//					System.out.println(m.group(1));
					String metachar = m.group(1);
					//c|fi|li|i|d|o|s|ii
					if(metachar.trim().equals("$c")){
						lexics2.add(comments.poll());
					}
					else if (metachar.trim().equals("$fi")){
						lexics2.add(floatlit.poll());

					}
					else if (metachar.trim().equals("$il")){
						lexics2.add(imaginarylit.poll());

					}
					else if (metachar.trim().equals("$li")){
						lexics2.add(longlit.poll());

					}
					else if (metachar.trim().equals("$i")){
						lexics2.add(intlit.poll());

					}
					else if (metachar.trim().equals("$d")){
						lexics2.add(delimiters.poll());

					}
					else if (metachar.trim().equals("$o")){
						lexics2.add(operators.poll());

					}
					else if (metachar.trim().equals("$s")){
						lexics2.add(stringlits.poll());

					}
					else if (metachar.trim().equals("$ii")){
						lexics2.add(imaginarylit.poll());

					}
					else if (metachar.trim().equals("$kw")){
						lexics2.add(keywords.poll());

					}
					else if (metachar.trim().equals("$id")){
						lexics2.add(identifiers.poll());

					}
					else{
						lexics2.add(new Tuple("UNIDENTIFIED", "DUNNO"));
					}
				}
				if(delimiters.size()>0||identifiers.size()>0||operators.size()>0){
					System.out.println("Problem here");
				}
				for(Tuple t:currentline){
					System.out.println(t.getClassification()+" "+t.getKeyword());
				}
				currentline.clear();
				for(Tuple t:lexics2){
					if(t!=null){
						System.out.println(t.getClassification()+" "+t.getKeyword());						
					}
				}
				lexics2.clear();
				System.out.println("Line done\n");
			}


			public static void main(String[] args) {
				lexics=new ArrayList<>();
				for(String s:args){
					//					System.out.println(s);
				}

				Scanner keywordscanner = null;
				try {
					keywordscanner = new Scanner(new File("./files/keywords.txt"));
				} catch (FileNotFoundException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				while(keywordscanner.hasNextLine()){
					keywordslist.add(keywordscanner.nextLine());
				}
				//String filename = args[0];

				String filename = "./files/lexemes.txt";

				File inputprogram = new File(filename);
				Scanner filescanner = null;
				try {
					filescanner = new Scanner(inputprogram);
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				while(filescanner.hasNextLine()){
					analyzer(filescanner.nextLine());
				}
				//Se supone que aparezcan por linea
				System.out.println();

			}


			private static String stringLiteral(String s){
				//		stringliteral   ::=  [stringprefix](shortstring | longstring)
				//		stringprefix    ::=  "r" | "u" | "ur" | "R" | "U" | "UR" | "Ur" | "uR"
				//		                     | "b" | "B" | "br" | "Br" | "bR" | "BR"
				//		shortstring     ::=  "'" shortstringitem* "'" | '"' shortstringitem* '"'
				//		longstring      ::=  "'''" longstringitem* "'''"
				//		                     | '"""' longstringitem* '"""'
				//		shortstringitem ::=  shortstringchar | escapeseq
				//		longstringitem  ::=  longstringchar | escapeseq
				//		shortstringchar ::=  <any source character except "\" or newline or the quote>
				//		longstringchar  ::=  <any source character except "\">
				//		escapeseq       ::=  "\" <any ASCII character>\

				String stringprefix =  "(r|u|ur|R|U|UR|Ur|uR)";//OK
				String shortstringchar= "([^\"\\r\\n\\])";//ok
				String longstringchar = "([^\\])";//ok
				String escapeseq = "([\\][ -~])";//ok


				String shortstringitem = "([^\"\r\n\\])|([\\][ -~])";
				String longstringitem = "([^\\])|([\\][ -~])";

				String longstring = "('''(([^\\\\])|([\\\\][ -~]))*'''|\"\"\"(([^\\\\])|([\\\\][ -~]))*\"\"\")";
				///Regex es ('''(([^\\])|([\\][ -~]))*'''|"""(([^\\])|([\\][ -~]))*""")
				String shortstring = "\"(([^\"\\r\\n\\\\])|([\\\\][ -~]))*\"|'(([^\"\\r\\n\\\\])|([\\\\][ -~]))*'";
				//Regex es ("(([^"\r\n\\])|([\\][ -~]))*"|'(([^"\r\n\\])|([\\][ -~]))*')
				//original working without ur u etc pregfix String lit = "(('''(([^\\\\])|([\\\\][ -~]))*'''|\"\"\"(([^\\\\])|([\\\\][ -~]))*\"\"\")|(\"(([^\"\\r\\n\\\\])|([\\\\][ -~]))*\"|'(([^\"\\r\\n\\\\])|([\\\\][ -~]))*'))";
				//regex es (r|u|ur|R|U|UR|Ur|uR)?('''(([^\\])|([\\][ -~]))*'''|"""(([^\\])|([\\][ -~]))*""")|("(([^"\r\n\\])|([\\][ -~]))*"|'(([^"\r\n\\])|([\\][ -~]))*')
				String lit = "((r|u|ur|R|U|UR|Ur|uR)?('''(([^\\\\])|([\\\\][ -~]))*'''|\"\"\"(([^\\\\])|([\\\\][ -~]))*\"\"\")|(\"(([^\"\\r\\n\\\\])|([\\\\][ -~]))*\"|'(([^\"\\r\\n\\\\])|([\\\\][ -~]))*'))";



				//Ahora a pushiar la data procesada y devolver string con cosas que le falten
				Matcher m = Pattern.compile(lit).matcher(s);
				while (m.find()) {
					//System.out.println("Comment found");
					//System.out.println(m.group(1));
					stringlits.add(new Tuple(m.group(1),"String literal"));
				}
				//		System.out.println("S is " + s);
				//		System.out.println("Matches"+s.matches(lit));

				return s.replaceAll(lit, "\\$s");
			}
			/**
			 * Returns the line without any comments
			 * @param linewithoutindentation
			 * @return line without comments and indentations
			 */
			private static String comment(String linewithoutindentation) {
				//System.out.println("Input"+linewithoutindentation);
				Matcher m = Pattern.compile("(#.*$)").matcher(linewithoutindentation);
				if (m.find()) {
					//System.out.println("Comment found");
					//System.out.println(m.group(1));
					//lexics.add(new Tuple(m.group(1),"Comment"));
					comments.add(new Tuple(m.group(1),"Comment"));
				}
				String linewithoutcomment = linewithoutindentation.replaceAll("(#.*$)", "\\$c");
				//System.out.println("Returning:"+linewithoutcomment);
				return linewithoutcomment;
			}
			/**
			 * 
			 * @param input Whole string
			 * @return String without indentations
			 * 
			 */
			private static String indentation(String input){
				String spacetabpattern = "( {4}|\\\\t)";
				input = input.replaceAll(spacetabpattern, "\t");
				return indentHelper(input,1);		
			}

			private static String indentHelper(String input, int times) {
				//No hay mas indents y llegue al final
				if(input.length()<2){
					return input;
				}
				String s = input.substring(0,1);
				String tab = "\t";

				if(s.equals(tab)){
					if(times>indents.size()){
						//Lo anado a indents
						indents.add(new Tuple("\t","indentacion"));
						lexics.add(new Tuple("\t","indentacion"));
						currentline.add(new Tuple("\t","indentacion"));
						return indentHelper(input.substring(1),times+1);
					}
					else if(!input.substring(1).substring(0,1).equals(tab)&&times<indents.size()){
						//Tengo una de indentacion
						//remuevo un indent
						indents.remove(indents.size()-1);

						currentline.add(new Tuple("\t","indentacion"));

						lexics.add(new Tuple("\\-t","deindent"));
						currentline.add(new Tuple("\\-t","deindent"));

					}
					else if(!lexics.get(lexics.size()-1).getClassification().equals("deindent")){
						lexics.add(new Tuple("\t","indentacion"));
						currentline.add(new Tuple("\t","indentacion"));

					}
					else{
						currentline.add(new Tuple("\t","indentacion"));
					}
					return indentHelper(input.substring(1), times+1);


				}
				else{
					if(indents.size()==1&&times<2){
						lexics.add(new Tuple("\\-t","deindent"));
						currentline.add(new Tuple("\\-t","deindent"));
						indents.remove(0);
					}
					return input;
				}		
			}

			private static String numLiteral(String s){

				//		decimalinteger ::=  nonzerodigit digit* | "0"
				String decimalinteger = "(([1-9][0-9]*)|0)";
				//		octinteger     ::=  "0" ("o" | "O") octdigit+ | "0" octdigit+
				String ocinteger = "((0([oO])([0-7])+)|([0][0-7]+))";
				//		hexinteger     ::=  "0" ("x" | "X") hexdigit+
				String hexinteger = "(0[xX]([a-fA-F0-9]+))";
				//		bininteger     ::=  "0" ("b" | "B") bindigit+
				String binteger = "(0[bB]([01]+))";
				//		integer        ::=  decimalinteger | octinteger | hexinteger | bininteger
				String integer = "(((0([oO])([0-7])+)|([0][0-7]+))|(0[xX]([a-fA-F0-9]+))|(0[bB]([01]+))|(([1-9][0-9]*)|0))";
				//		longinteger    ::=  integer ("l" | "L")
				String longinteger = integer+"([lL])";

				//		floatnumber   ::=  pointfloat | exponentfloat
				//		pointfloat    ::=  [intpart] fraction | intpart "."
				//		exponentfloat ::=  (intpart | pointfloat) exponent
				//		intpart       ::=  digit+
				//		fraction      ::=  "." digit+
				//		exponent      ::=  ("e" | "E") ["+" | "-"] digit+

				//imagnumber ::=  (floatnumber | intpart) ("j" | "J")
				String imaginarynumber = "((((([0-9]+)|([0-9]*\\.[0-9]+))((e|E)(\\+|-)?[0-9]+)|(([0-9]*\\.[0-9]+)|([0-9]*[0-9]\\.)))|([0-9]+))(j|J))";
				Matcher ipm  = Pattern.compile(imaginarynumber).matcher(s);
				while(ipm.find()) {
					//					System.out.println("Imaginary int found");
					//					System.out.println(ipm.group(1));
					imaginarylit.add(new Tuple(ipm.group(1), "Imaginary literal"));
				}
				String noimag = s.replaceAll(imaginarynumber,"\\$il");

				String exponent = "((e|E)(\\+|-)?[0-9]+)",
						fraction = "(\\.[0-9]+)",
						intpart = "([0-9]+)",
						pointfloat = "(([0-9]*\\.[0-9]+)|([0-9]*[0-9]\\.))",
						exponentfloat = "(([0-9]+)|([0-9]*\\.[0-9]+))((e|E)(\\+|-)?[0-9]+)",
						floatnumber = "((([0-9]+)|([0-9]*\\.[0-9]+))((e|E)(\\+|-)?[0-9]+)|(([0-9]*\\.[0-9]+)|([0-9]*[0-9]\\.)))";

				Matcher fpm  = Pattern.compile(floatnumber).matcher(noimag);
				while(fpm.find()) {
					//					System.out.println("Float int found");
					//					System.out.println(fpm.group(1));
					floatlit.add(new Tuple(fpm.group(1), "Float literal"));
				}
				String nofloat = noimag.replaceAll(floatnumber,"\\$fi");

				Matcher m = Pattern.compile(longinteger).matcher(nofloat);
				while(m.find()) {
					//					System.out.println("Long int found");
					//					System.out.println(m.group(1));
					longlit.add(new Tuple(m.group(1),"Long int"));
				}
				String longint = nofloat.replaceAll(longinteger,"\\$li");

				Matcher mli= Pattern.compile(integer).matcher(longint);
				while(mli.find()) {
					//					System.out.println("Integer found");
					//					System.out.println(mli.group(1));
					intlit.add(new Tuple(mli.group(1),"Int literal"));
				}
				String rep = longint.replaceAll(integer,"\\$i");
				return rep;

			}
			private static String delimiter(String word){
				/**
				 * &=|\|=|\^=|>>=|<<=|\*\*=|(|)|[|]|{|}|@|+=|-=|\*=|/=|//=|%=|,|:|.|`|=|;
				 * (&=|\\|=|\^=|>>=|<<=|\*\*=|:|\.|'|=|;|\+=|-=|\*=|/=|//=|%=|,|\(|\)|\{|\}|@|\[|\])

				 */
				String delregex = "(&=|\\\\|=|\\^=|>>=|<<=|\\*\\*=|:|\\.|'|=|;|\\+=|-=|\\*=|/=|//=|%=|,|\\(|\\)|\\{|\\}|@|\\[|\\])";
				Matcher m = Pattern.compile(delregex).matcher(word);
				while(m.find()) {
					//					System.out.println("Delimiter found");
					//					System.out.println(m.group(1));
					delimiters.add(new Tuple(m.group(1), "Delimiter found"));
				}
				return word.replaceAll(delregex,"\\$d");

			}
			private static String identifier(String word){
				//		identifier ::=  (letter|"_") (letter | digit | "_")*
				//		letter     ::=  lowercase | uppercase
				//		lowercase  ::=  "a"..."z"
				//		uppercase  ::=  "A"..."Z"
				//		digit      ::=  "0"..."9"

				String lowercase = "([a-z])";
				String uppercase = "([A-Z])";
				String digit = "([0-9])";
				String letter = "("+lowercase+"|"+uppercase+")";

				String identifier = "(("+letter+"|_)("+letter+"|"+digit+"|_)*)";
				//		System.out.println("Identifier regex "+identifier);

				String[] breakdown = word.split("\\s+");
				for(String item:breakdown){

					item = item.replaceAll(("\\$(c|fi|li|il|i|d|o|s)"), " ");
					String[] splititem = item.split("\\s+");
					for(String split:splititem){
						Matcher mli= Pattern.compile(identifier).matcher(split);

						if(mli.find()) {
							//							System.out.println("Identifier found");
							//							System.out.println(mli.group(1));
							//lexics.add(new Tuple(m.group(1),"Operator"));
							String s = mli.group(1);
							if(isKeyword(mli.group(1))){
								word = word.replaceFirst(mli.group(1), "\\$kw");
								keywords.add(new Tuple(mli.group(1),"Keyword"));
							}
							else{
								word = word.replaceFirst(mli.group(1), "\\$id");
								identifiers.add(new Tuple(mli.group(1),"Identifier"));
							}
						}
					}
				}
				return word;
			}
			private static boolean isKeyword(String word){
				return keywordslist.contains(word);
			}
			private static String operator(String word){
				/**
				 * +|-|\*|\*\*|/|//|%|<<|>>|&|||^|~|<|>|<=|>=|==|!=|<>
				 */
				String opregex = "(\\+|-|\\*|\\*\\*|/|//|%|<<|>>|&|\\||\\^|~|<|>|<=|>=|==|!=|<>)";
				Matcher m = Pattern.compile(opregex).matcher(word);
				while(m.find()) {
					//					System.out.println("Operator found");
					//					System.out.println(m.group(1));
					operators.add(new Tuple(m.group(1),"Operator"));
				}
				return word.replaceAll(opregex,"\\$o");
			}
}
