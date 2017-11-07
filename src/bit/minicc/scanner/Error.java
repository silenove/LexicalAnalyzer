package bit.minicc.scanner;

public class Error {
	public int number;
    public String info;
    public int line;
    public String value;
    
    public Error(int number, String info, int line, String value)
    {
        this.number = number;
        this.info = info;
        this.line = line;
        this.value = value;
    }

	public int getNumber() {
		return number;
	}

	public void setNumber(int number) {
		this.number = number;
	}

	public String getInfo() {
		return info;
	}

	public void setInfo(String info) {
		this.info = info;
	}

	public int getLine() {
		return line;
	}

	public void setLine(int line) {
		this.line = line;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	

}
