package cw.tools;

public class Flag {

	private boolean arg = false;
	
	public Flag(boolean arg) {
		super();
		this.arg = (arg);
	}
	public Flag(){
		super();
	}
	public void setArg(boolean arg) {
		this.arg = arg;
	}
	public boolean getArg() {
		return arg;
	}
}
