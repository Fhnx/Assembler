package assembler;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

public class Parser {
	private InputStreamReader inStream;
	private OutputStreamWriter outStream;

	private String[] code;

	public Parser(InputStream source, OutputStream dest) {
		this(new InputStreamReader(source), new OutputStreamWriter(dest));
	}

	public Parser(InputStreamReader source, OutputStreamWriter dest) {
		inStream = source;
		outStream = dest;
		code = null;
	}

	public int parse() {
		StringBuilder stringBuf = new StringBuilder();
		int ch;
		try {
			while ((ch = inStream.read()) > -1) {
				stringBuf.append((char) ch);
			}
		} catch (IOException e) {
			e.printStackTrace();
			return 1;
		}
		System.out.println("Source file contents:\n" + stringBuf.toString());
		System.out.println("-----------------------");
		
		code = stringBuf.toString().split("\n");
		System.out.println("Num lines: " + code.length);
		int numNonEmpty = 0;
		for (int idx = 0; idx < code.length; idx++) {
			String str = code[idx];
			System.out.println(str);
			int idxOf = str.indexOf("//");
			if(idxOf != -1){
				str = str.substring(0, idxOf);
				code[idx] = str;
			}
			
			if( str.length() > 1)
				numNonEmpty++;
		}
		int idxTmpArr = 0;
		String[] tmpArr = new String[numNonEmpty];
		for (String string : code) {
			if(string.length() > 1){
				tmpArr[idxTmpArr] = string;
				idxTmpArr++;
			}
		}
		code = tmpArr;
		tmpArr = null;
		
		
		System.out.println("Pure code:");
		for (String string : code) {
			System.out.println(string);
		}
		System.out.println("-----------------------");
		return 0;
	}

}
