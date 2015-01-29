package assembler;

import java.util.ArrayList;
import java.util.HashMap;

public class Coder {
	private static HashMap<String, String> jumpBits = null;
	private static HashMap<String, String> destBits = null;
	private static HashMap<String, String> compBits = null;
	private static final String cInstrStartBits = "111";

	public static int convert(ArrayList<Instruction> instructions,
			SymbolTable symbolTable) {
		if (jumpBits == null || destBits == null || compBits == null) {
			initHashMaps();
		}

		for (int idx = 0; idx < instructions.size(); idx++) {
			Instruction instr = instructions.get(idx);

			// if is C instruction
			if (instr.isCInstruction()) {
				StringBuilder strBui = new StringBuilder(cInstrStartBits);
				String str = null;

				// compute instruction
				str = compBits.get(instr.getCodeParts()[1]);
				if (str == null) {
					return Parser.ReturnStatus.WRONG_COMP_INSTRUCTION;
				}
				strBui.append(str);

				// destination instruction
				str = destBits.get(instr.getCodeParts()[0]);
				if (str == null) {
					return Parser.ReturnStatus.WRONG_DEST_INSTRUCTION;
				}
				strBui.append(str);

				// jump instruction
				str = jumpBits.get(instr.getCodeParts()[2]);
				if (str == null) {
					return Parser.ReturnStatus.WRONG_JUMP_INSTRUCTION;
				}
				strBui.append(str);
				instr.setBinaryCode(strBui.toString());
			} else { // is A instruction
				int value = 0;
				String instrPart = instr.getCodeParts()[0];
				try{
					value = Integer.parseInt(instrPart);
				} catch (NumberFormatException nfe) {
					if (symbolTable.isKey(instrPart)) {
						value = symbolTable.getValue(instrPart);
					} else {
						value = symbolTable.addVariable(instrPart);
					}
				}
				instr.setBinaryCode(int2BitString(value));
			}

			instructions.set(idx, instr);
		}
		return Parser.ReturnStatus.SUCCESS;
	}

	public static String int2BitString(int value) {
		StringBuilder str = new StringBuilder();
		int modVal = 0;
		while (value != 0) {
			modVal = value % 2;
			str.insert(0, modVal);
			value = (value - modVal) / 2;
		}

		while (str.length() < 16) {
			str.insert(0, 0);
		}
		return str.toString();
	}

	private static void initHashMaps() {
		jumpBits = new HashMap<String, String>();
		jumpBits.put("", "000");
		jumpBits.put("JGT", "001");
		jumpBits.put("JEQ", "010");
		jumpBits.put("JGE", "011");
		jumpBits.put("JLT", "100");
		jumpBits.put("JNE", "101");
		jumpBits.put("JLE", "110");
		jumpBits.put("JMP", "111");

		destBits = new HashMap<String, String>();
		destBits.put("", "000");
		destBits.put("M", "001");
		destBits.put("D", "010");
		destBits.put("MD", "011");
		destBits.put("A", "100");
		destBits.put("AM", "101");
		destBits.put("AD", "110");
		destBits.put("AMD", "111");

		compBits = new HashMap<String, String>();
		compBits.put("0", "0101010");
		compBits.put("1", "0111111");
		compBits.put("-1", "0111010");
		compBits.put("D", "0001100");
		compBits.put("A", "0110000");
		compBits.put("!D", "0001101");
		compBits.put("!A", "0110001");
		compBits.put("-D", "0001111");
		compBits.put("-A", "0110011");
		compBits.put("D+1", "0011111");
		compBits.put("1+D", "0011111");
		compBits.put("A+1", "0110111");
		compBits.put("1+A", "0110111");
		compBits.put("D-1", "0001110");
		compBits.put("A-1", "0110010");
		compBits.put("D+A", "0000010");
		compBits.put("A+D", "0000010");
		compBits.put("D-A", "0010011");
		compBits.put("A-D", "0000111");
		compBits.put("D&A", "0000000");
		compBits.put("A&D", "0000000");
		compBits.put("D|A", "0010101");
		compBits.put("A|D", "0010101");
		compBits.put("M", "1110000");
		compBits.put("!M", "1110001");
		compBits.put("-M", "1110011");
		compBits.put("M+1", "1110111");
		compBits.put("1+M", "1110111");
		compBits.put("M-1", "1110010");
		compBits.put("D+M", "1000010");
		compBits.put("M+D", "1000010");
		compBits.put("D-M", "1010011");
		compBits.put("M-D", "1000111");
		compBits.put("D&M", "1000000");
		compBits.put("M&D", "1000000");
		compBits.put("D|M", "1010101");
		compBits.put("M|D", "1010101");
	}
}
