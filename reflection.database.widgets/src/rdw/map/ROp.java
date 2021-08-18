package rdw.map;

public class ROp {

	private Object object;
	private int op;
	
	public ROp(Object object,int op) {
		super();
		this.object = object;
		this.op = op;
	}
	public Object getObject() {
		return object;
	}
	public void setObject(Object object) {
		this.object = object;
	}
	public int getOp() {
		return op;
	}
	public void setOp(int op) {
		this.op = op;
	}
	
}
