package assembler;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

public class Parser {
	private InputStreamReader inStream;
	private OutputStreamWriter outStream;

	private String[] code;
	private int[] codeLine;
	private ArrayList<Instruction> instructions;
	private SymbolTable symbolTable;

	public class ReturnStatus {
		public static final int SUCCESS = 0;

		public static final int INSTREAM_READ_ERROR = 10;
		
		public static final int PARSER_DUPLICATE_SYMBOL = 20;
		public static final int PARSER_SYMBOL_IN_LAST_LINE = 21;
	}

	public Parser(InputStream source, OutputStream dest) {
		this(new InputStreamReader(source), new OutputStreamWriter(dest));
	}

	public Parser(InputStreamReader source, OutputStreamWriter dest) {
		inStream = source;
		outStream = dest;
		code = null;
		codeLine = null;
		symbolTable = new SymbolTable();
	}

	public int parse() {
		int retStat = readInStream();
		if (retStat != 0) { return retStat; }
		System.out.println("-----------------------");

		retStat = removeCommentsWhitespaces();
		if (retStat != 0) { return retStat; }
		System.out.println("-----------------------");
		
		retStat = initInstructions();
		if (retStat != 0) { return retStat; }
		System.out.println("-----------------------");
		
		return ReturnStatus.SUCCESS;
	}

	private int initInstructions() {
		instructions = new ArrayList<Instruction>();
		instructions.ensureCapacity(code.length);
		for(int idx = 0; idx < code.length; idx++){
			String currStr = code[idx];
			// if a instruction
			if(currStr.charAt(0) == '@'){
				System.out.println(currStr + "\t---> A-instr");
				instructions.add(new Instruction(currStr, codeLine[idx], false, new String[]{currStr.substring(1, currStr.length())}));
			} else {
				// if is symbol
				if(currStr.charAt(0) == '('){
					String symbol = currStr.substring(1, currStr.length() - 1);
					if( symbolTable.isKey(symbol)){
						return ReturnStatus.PARSER_DUPLICATE_SYMBOL;
					} else {
						if (idx == code.length - 1){
							return ReturnStatus.PARSER_SYMBOL_IN_LAST_LINE;
						} else {
							symbolTable.addSymbol(symbol, instructions.size());
							System.out.println(currStr + "\t---> Symbol: " + symbol + " Address:" + instructions.size());
						}
					}
				} else {
					System.out.println(currStr + "\t---> C-instr");
					
				}				
			}
		}	
		instructions.trimToSize();		
		return ReturnStatus.SUCCESS;
	}

	private int removeCommentsWhitespaces() {
		// remove comments in code
		System.out.println("Num lines: " + code.length);
		int numNonEmpty = 0;
		for (int idx = 0; idx < code.length; idx++) {
			String str = code[idx];
			int idxOf = str.indexOf("//");
			if (idxOf != -1) {
				str = str.substring(0, idxOf);
				code[idx] = str;
			}

			if (str.length() > 1)
				numNonEmpty++;
		}

		// remove whitespaces
		for (int idx = 0; idx < code.length; idx++) {
			code[idx] = code[idx].replaceAll("\\s", "");
		}

		// remove empty lines of code and save code line
		int idxTmpArr = 0;
		String[] tmpArr = new String[numNonEmpty];
		codeLine = new int[numNonEmpty];
		for (int idx = 0; idx < code.length; idx++) {
			String string = code[idx];
			if (string.length() > 1) {
				tmpArr[idxTmpArr] = string;
				codeLine[idxTmpArr] = idx + 1;
				idxTmpArr++;
			}
		}
		code = tmpArr;
		tmpArr = null;

		System.out.println("Pure code:");
		for (String string : code) {
			System.out.println(string);
		}
		return 0;
	}

	private int readInStream() {
		// read file
		StringBuilder stringBuf = new StringBuilder();
		int ch;
		try {
			while ((ch = inStream.read()) > -1) {
				stringBuf.append((char) ch);
			}
			inStream.close();
		} catch (IOException e) {
			e.printStackTrace();
			return ReturnStatus.INSTREAM_READ_ERROR;
		}
		code = stringBuf.toString().split("\n");
		System.out.println("Source file contents:\n" + stringBuf.toString());
		return ReturnStatus.SUCCESS;
	}

}
