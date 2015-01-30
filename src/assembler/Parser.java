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
	private boolean doExtraOutput;
	
	private String[] code;
	private int[] codeLine;
	private ArrayList<Instruction> instructions;
	private SymbolTable symbolTable;
	
	public class ReturnStatus {
		public static final int SUCCESS = 0;
		
		public static final int INSTREAM_READ_ERROR = 10;
		public static final int OUTSTREAM_READ_ERROR = 10;
		
		public static final int DUPLICATE_SYMBOL = 20;
		public static final int SYMBOL_IN_LAST_LINE = 21;
		
		public static final int WRONG_C_INSTRUCTION = 30;
		public static final int WRONG_JUMP_INSTRUCTION = 31;
		public static final int WRONG_DEST_INSTRUCTION = 32;
		public static final int WRONG_COMP_INSTRUCTION = 33;
	}
	
	public Parser(InputStream source, OutputStream dest, boolean doExtraOutput) {
		this(new InputStreamReader(source), new OutputStreamWriter(dest),
				doExtraOutput);
	}
	
	public Parser(InputStreamReader source, OutputStreamWriter dest,
			boolean doExtraOutput) {
		inStream = source;
		outStream = dest;
		code = null;
		codeLine = null;
		symbolTable = new SymbolTable();
		this.doExtraOutput = doExtraOutput;
	}
	
	public int parse() {
		int retStat = readInStream();
		if (retStat != 0) { return retStat; }
		if (doExtraOutput) System.out.println("-----------------------");
		
		retStat = removeCommentsWhitespaces();
		if (retStat != 0) { return retStat; }
		if (doExtraOutput) System.out.println("-----------------------");
		
		retStat = initInstructions();
		if (retStat != 0) { return retStat; }
		if (doExtraOutput) System.out.println("-----------------------");
		
		retStat = Coder.convert(instructions, symbolTable);
		if (doExtraOutput) {
			for (Instruction instr : instructions) {
				System.out.println(instr);
			}
			System.out.println("-----------------------");
		}
		if (retStat != 0) { return retStat; }
		
		try {
			for (Instruction instr : instructions) {
				outStream.write(instr.getBinaryCode() + "\n");
			}
			outStream.flush();
			outStream.close();
		} catch (IOException e) {
			e.printStackTrace();
			return ReturnStatus.OUTSTREAM_READ_ERROR;
		}

		System.out.println("Output file contains " + instructions.size() + " instructions.");
		System.out.println("Wrote data to outstream.");
		System.out.println("-----------------------");
		
		return ReturnStatus.SUCCESS;
	}
	
	private int initInstructions() {
		instructions = new ArrayList<Instruction>();
		instructions.ensureCapacity(code.length);
		for (int idx = 0; idx < code.length; idx++) {
			String currStr = code[idx];
			// if a instruction
			if (currStr.charAt(0) == '@') {
				instructions.add(new Instruction(currStr, codeLine[idx], false,
						new String[] { currStr.substring(1, currStr.length()),
										"", "" }));
				if (doExtraOutput)
					System.out.println(currStr + "\t---> "
							+ instructions.get(instructions.size() - 1));
			} else {
				// if is symbol
				if (currStr.charAt(0) == '(') {
					String symbol = currStr.substring(1, currStr.length() - 1);
					if (symbolTable.isKey(symbol)) {
						return ReturnStatus.DUPLICATE_SYMBOL;
					} else {
						if (idx == code.length - 1) {
							return ReturnStatus.SYMBOL_IN_LAST_LINE;
						} else {
							symbolTable.addSymbol(symbol, instructions.size());
							if (doExtraOutput)
								System.out.println(currStr + "\t---> Symbol: "
										+ symbol + " Address:"
										+ instructions.size());
						}
					}
				} else { // if is c-instruction
					String[] instrParts = new String[3];
					String tmpStr = currStr;
					// get jump instruction
					int idxFound = tmpStr.indexOf(';');
					if (idxFound != -1) {
						instrParts[2] =
								tmpStr.substring(idxFound + 1, tmpStr.length());
						tmpStr = tmpStr.substring(0, idxFound);
					} else {
						instrParts[2] = "";
					}
					
					idxFound = tmpStr.indexOf('=');
					if (idxFound != -1) {
						instrParts[0] = tmpStr.substring(0, idxFound);
						instrParts[1] =
								tmpStr.substring(idxFound + 1, tmpStr.length());
					} else {
						instrParts[0] = "";
						instrParts[1] = tmpStr;
					}
					
					for (String string : instrParts) {
						if (string.indexOf('=') != -1
								|| string.indexOf(';') != -1) { return ReturnStatus.WRONG_C_INSTRUCTION; }
					}
					instructions.add(new Instruction(currStr, codeLine[idx],
							true, instrParts));
					if (doExtraOutput)
						System.out.println(currStr + "\t---> "
								+ instructions.get(instructions.size() - 1));
				}
			}
		}
		instructions.trimToSize();
		return ReturnStatus.SUCCESS;
	}
	
	private int removeCommentsWhitespaces() {
		// remove comments in code
		System.out.println("File contains " + code.length + " lines.");
		int numNonEmpty = 0;
		for (int idx = 0; idx < code.length; idx++) {
			String str = code[idx];
			int idxOf = str.indexOf("//");
			if (idxOf != -1) {
				str = str.substring(0, idxOf);
				code[idx] = str;
			}
			
			if (str.length() > 1) numNonEmpty++;
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
		
		if (doExtraOutput) {
			System.out.println("Pure code:");
			for (String string : code) {
				System.out.println(string);
			}
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
		if (doExtraOutput)
			System.out
					.println("Source file contents:\n" + stringBuf.toString());
		return ReturnStatus.SUCCESS;
	}
	
}
