package cw.tabmanager;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;

import rdw.interfaces.TabControl;

public class CTab extends Composite implements TabControl{

	public CTab(CTabManager parent) {
		super(parent.tabFolder, SWT.NONE);
		parent.tabs.add(this);
	}
	
	protected TabControl control;
	protected Image image;
	protected String title;
	protected int index = -1;
	
	@Override
	public void refresh() {
		
	}
	
	
}
