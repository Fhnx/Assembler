package assembler;

import java.util.HashMap;

public class SymbolTable {
	private HashMap<String, Integer> symbols;
	
	public SymbolTable() {
		symbols = new HashMap<String, Integer>();
		for (int num = 0; num < 16; num++){
			symbols.put("R" + num, num);
		}
		symbols.put("SCREEN",	16384);
		symbols.put("KBD",		24576);
		symbols.put("SP",		0);
		symbols.put("LCL",		1);
		symbols.put("ARG",		2);
		symbols.put("THIS",		3);
		symbols.put("THAT",		4);
		symbols.put("WRITE",	18);
		symbols.put("END",		22);
		symbols.put("i",		16);
		symbols.put("sum",		17);
	}
	
	public void addSymbol(String key, int value){
		symbols.put(key, value);
	}
	
	public int getValue(String key){
		return symbols.get(key).intValue();
	}
	
	public boolean isKey(String key) {
		return symbols.containsKey(key);
	}
}
