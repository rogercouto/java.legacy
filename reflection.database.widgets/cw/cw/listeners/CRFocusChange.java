package cw.listeners;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.widgets.Control;

public class CRFocusChange extends KeyAdapter {
	
	private Control next = null;
	
	public CRFocusChange() {
		super();
	}

	public CRFocusChange(Control next) {
		super();
		this.next = next;
	}

	@Override
	public void keyPressed(KeyEvent e) {
		if (e.keyCode == SWT.CR || e.keyCode == SWT.KEYPAD_CR){
			if (next != null){
				next.setFocus();
			}else{
				Control thisControl = (Control)e.widget;
				Control[] controls = thisControl.getParent().getTabList();
				for (int i = 0; i < controls.length; i++) {
					if (thisControl.equals(controls[i]) && (i < controls.length-1)){
						controls[i+1].setFocus();
					}
				}
			}
		}
	}

}
