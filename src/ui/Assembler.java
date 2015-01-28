package ui;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import assembler.Parser;

public class Assembler {
	private static String sourceFilePath = null;
	private static String destFilePath = null;

	public static void main(String[] args) {
		System.out.println("-----------------------");
		System.out.println("Number of arguments: " + args.length);
		System.out.println("The arguments are:");
		for (String string : args) {
			System.out.println(string);
		}
		System.out.println("-----------------------");
		int retStat = assignFilePaths(args);
		System.out.println("-----------------------");
		if (retStat == 0) {
			Parser par = null;
			try {
				par = new Parser(new FileInputStream(sourceFilePath), new FileOutputStream(destFilePath, false));
			} catch (FileNotFoundException e) {
				e.printStackTrace();
				System.out.println("Error: Couldn't open Filestreams!");
			}
			if(par != null){
				retStat = par.parse();
				if(retStat == 0){
					
				}else{
					System.out.println("Error: While parsing. Error-code: " + retStat);
				}
			}
		} else {
			System.out.println("Canceled!");
		}
		System.out.println("-----------------------");
	}

	private static int assignFilePaths(String[] args) {
		int returnStatus = 0;

		//Check number of arguments
		if (args.length == 0) {
			System.out.println("Error: No inputs.");
			returnStatus = 1;
		} else {
			// Add source file
			if (args[0].toLowerCase().endsWith(".asm")) {
				sourceFilePath = args[0];
				System.out.println("Source:\t\t" + sourceFilePath);
				if (args.length > 2) {
					System.out
							.println("Warning: Only the first two arguments get used.");
				}

				// Add dest file
				if (args.length > 1 && args[1].toLowerCase().endsWith(".hack")) {
					destFilePath = args[1];
				} else {
					if (args.length == 1) {
						destFilePath = sourceFilePath.substring(0,
								sourceFilePath.length() - 4) + ".hack";
					} else {
						System.out.println("Error: Invalid destination file.");
						returnStatus = 1;
					}
				}

				if (returnStatus == 0) {
					System.out.println("Destination:\t" + destFilePath);
					System.out
							.println("The destination file will be created and/or overwritten.");
				}
			} else {
				System.out.println("Error: Please enter a *.asm file.");
				returnStatus = 1;
			}
		}

		return returnStatus;
	}
}
