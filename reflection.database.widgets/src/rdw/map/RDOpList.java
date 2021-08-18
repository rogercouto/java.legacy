package rdw.map;

import java.util.ArrayList;

public class RDOpList {
	
	ArrayList<ROp> opList = null;

	public RDOpList() {
		super();
		this.opList = new ArrayList<ROp>();
	}

	public void addOp(ROp op){
		opList.add(op);
	}
	public void addOp(Object obj,int op){
		opList.add(new ROp(obj,op));
	}
	public void setOp(int index,ROp op){
		opList.set(index,op);
	}
	public void setOp(int index,Object obj,int op){
		opList.set(index,new ROp(obj,op));
	}
	public void removeOp(int index){
		opList.remove(index);
	}
	
	public ROp getOp(int index){
		return opList.get(index);
	}
	
}
