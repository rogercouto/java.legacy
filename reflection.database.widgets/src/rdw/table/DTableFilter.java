package rdw.table;

public class DTableFilter {

	private int column;
	private Object arg0;
	private Object arg1 = null;
	
	public DTableFilter(DTable parent,int column, Object arg0, Object arg1) {
		super();
		this.column = column;
		this.arg0 = arg0;
		this.arg1 = arg1;
		parent.dtf.add(this);
	}
	
	public DTableFilter(DTable parent,int column, Object arg0) {
		super();
		this.column = column;
		this.arg0 = arg0;
		parent.dtf.add(this);
	}

	public int getColumn() {
		return column;
	}
	public void setColumn(int column) {
		this.column = column;
	}
	public Object getArg0() {
		return arg0;
	}
	public void setArg0(Object arg0) {
		this.arg0 = arg0;
	}
	public Object getArg1() {
		return arg1;
	}
	public void setArg1(Object arg1) {
		this.arg1 = arg1;
	}
	
}
