package assembler;

public class Instruction {
	private String code = "";
	private int lineNumber = 0;
	private boolean isCInstruction = false;
	private String[] codeParts = null;
	
	public Instruction() {}
	
	public Instruction(String code, int lineNumber, boolean isCInstruction, String[] codeParts) {
		this.code = code;
		this.lineNumber = lineNumber;
		this.isCInstruction = isCInstruction;
		this.codeParts = codeParts;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public int getLineNumber() {
		return lineNumber;
	}

	public void setLineNumber(int lineNumber) {
		this.lineNumber = lineNumber;
	}

	public boolean isCInstruction() {
		return isCInstruction;
	}

	public void setCInstruction(boolean isCInstruction) {
		this.isCInstruction = isCInstruction;
	}

	public String[] getCodeParts() {
		return codeParts;
	}

	public void setCodeParts(String[] codeParts) {
		this.codeParts = codeParts;
	}
	
	
}
