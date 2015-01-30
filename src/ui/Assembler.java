package ui;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import assembler.Parser;

public class Assembler {
	private static ArrayList<String> sourceFilePaths = null;
	private static ArrayList<String> destFilePaths = null;

	public static void main(String[] args) {
		System.out.println("-----------------------");
		System.out.println("Number of arguments: " + args.length);
		System.out.println("The arguments are:");
		for (String string : args) {
			System.out.println(string);
		}
		System.out.println("-----------------------");
		int returnStatus = 0;
		// Check number of arguments
		if (args.length == 0) {
			System.out.println("Error: No inputs.");
			returnStatus = 1;
		} else {
			if (args.length > 2) {
				System.out
						.println("Warning: Only the first two arguments are needed.");
			}
			// Add source file
			returnStatus = assignFilePaths(args);
		}
		System.out.println("-----------------------");
		if (returnStatus == 0) {
			returnStatus = runParser();
			if (returnStatus != 0) {
				System.out.println("Error: While parsing");
			}
		} else {
			System.out.println("Canceled!");
		}
		System.out.println("FINISHED");
		System.out.println("-----------------------");
	}

	private static int assignFilePaths(String[] args) {
		int returnStatus = 0;
		if (args[0].toLowerCase().equals("-r")) {
			if (args.length == 2) {
				sourceFilePaths = (ArrayList<String>) collectAllFilesInSubFolder(
						new ArrayList<String>(), FileSystems.getDefault()
								.getPath(args[1]));
				if (sourceFilePaths != null) {
					destFilePaths = new ArrayList<String>();
					String dest = null;
					for (int idx = 0; idx < sourceFilePaths.size(); idx++) {
						dest = checkASMFilePath(sourceFilePaths.get(idx));
						if (dest == null) {
							System.out.println("Error: File finder error");
							returnStatus = 1;
							break;
						} else {
							destFilePaths.add(dest);
						}
					}
				} else {
					System.out
							.println("Error: Couldn\'t find any *.asm files.");
				}
			} else {
				System.out
						.println("The -r option needs only one extra parameter");
			}
		} else {
			String dest = checkASMFilePath(args[0]);

			// Add dest file
			if (args.length > 1) {
				if (args[1].toLowerCase().endsWith(".hack")) {
					dest = args[1];
				} else {
					System.out.println("Error: Invalid destination file.");
					returnStatus = 1;
					return returnStatus;
				}
			}
			sourceFilePaths = new ArrayList<String>();
			sourceFilePaths.add(args[0]);
			destFilePaths = new ArrayList<String>();
			destFilePaths.add(dest);
		}

		if (returnStatus == 0) {
			System.out.println("Destination:\t" + destFilePaths);
			System.out
					.println("The destination file will be created and/or overwritten.");
		}
		return returnStatus;
	}

	private static List<String> collectAllFilesInSubFolder(
			List<String> fileNames, Path dir) {
		try {
			DirectoryStream<Path> dirStream = Files.newDirectoryStream(dir);

			for (Path currPath : dirStream) {
				if (currPath.toFile().isDirectory())
					collectAllFilesInSubFolder(fileNames, currPath);
				else {
					String pathToTest = currPath.toString();
					if (pathToTest.toLowerCase().endsWith(".asm")) {
						fileNames.add(currPath.toAbsolutePath().toString());
					}
				}
			}
			dirStream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return fileNames;
	}

	private static String checkASMFilePath(String source) {
		String dest = null;
		if (source.toLowerCase().endsWith(".asm")) {
			System.out.println("Source:\t\t" + source);
			// Add dest file
			dest = source.substring(0, source.length() - 4) + ".hack";
		}
		return dest;
	}

	private static int runParser() {
		Parser par = null;
		int retStat = 0;
		for (int idx = 0; idx < sourceFilePaths.size(); idx++) {
			try {
				par = new Parser(new FileInputStream(sourceFilePaths.get(idx)),
						new FileOutputStream(destFilePaths.get(idx), false));
			} catch (FileNotFoundException e) {
				e.printStackTrace();
				System.out.println("Error: Couldn't open Filestreams!");
			}
			if (par != null) {
				retStat = par.parse();
				if (retStat != 0) {
					System.out.println("Error: While parsing. Error-code: "
							+ retStat);
					return retStat;
				}
			}
		}
		return retStat;

	}
}
