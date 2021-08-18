package cw.interfaces;

import org.eclipse.swt.widgets.Listener;

public interface CControl {

	void setNullable(boolean nullable);
	boolean isNullable();
	
	boolean validate();
	
	void addListener(int eventType,Listener listener);
	void removeListener(int eventType,Listener listener);
	
}
