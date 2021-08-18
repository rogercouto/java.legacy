package cw.tabmanager;

import java.util.ArrayList;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;

public class CTabManager extends Composite {

	private int style;
	protected CTabFolder tabFolder = null;
	protected ArrayList<CTab> tabs = new ArrayList<CTab>();  //  @jve:decl-index=0:
	
	public CTabManager(Composite parent, int style) {
		super(parent, SWT.NONE);
		this.style = style;
		initialize();
	}

	private void initialize() {
		createTabFolder();
		setLayout(new FillLayout());
		pack();
	}
	
	/**
	 * This method initializes tabFolder	
	 *
	 */
	private void createTabFolder() {
		tabFolder = new CTabFolder(this, style);
		tabFolder.setVisible(false);
		tabFolder.addSelectionListener(new SelectionAdapter(){
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				CTab tab = tabs.get(tabFolder.getSelectionIndex());
				tab.refresh();
			}
		});
	}

	public void openTab(CTab tab){
		int index = tabs.indexOf(tab);
		openTab(index);
	}
	
	private void openTab(int index){
		final CTab tab = tabs.get(index);
		tab.refresh();
		if (tab.index < 0){
			tab.index = tabFolder.getItemCount();
			CTabItem item = new CTabItem(tabFolder,SWT.CLOSE);
			item.setText(tab.title);
			item.setControl(tab);
			if (tab.image != null)
				item.setImage(tab.image);
			item.addDisposeListener(new DisposeListener(){
				@Override
				public void widgetDisposed(DisposeEvent arg0) {
					tab.index = -1;
					if (tabFolder.getItemCount() == 0)
						tabFolder.setVisible(false);
				}
			});
			tabFolder.setSelection(item);
			tabFolder.setVisible(true);
		}else{
			tabFolder.setSelection(tab.index);
		}
	}
	
	public void closeTab(int index){
		CTab tab = tabs.get(index);
		tabFolder.getItem(tab.index).dispose();
	}
	
	public CTab getActiveTab(){
		int index = tabFolder.getSelectionIndex();
		if (index < 0)
			return null;
		CTab tab = tabs.get(index);
		CTabItem item = tabFolder.getItem(tab.index);
		return (CTab)item.getControl();
	}
	
	//TODO Getters // Setters
	public void setFont(Font font){
		tabFolder.setFont(font);
	}
	public void setBackground(Color color){
		super.setBackground(color);
	}
	public void setForeground(Color color){
		super.setForeground(color);
	}
	public void setTabBackground(Color color){
		tabFolder.setBackground(color);
	}
	public void setTabForeground(Color color){
		tabFolder.setForeground(color);
	}
	public void setSelectionBackground(Color color){
		tabFolder.setSelectionBackground(color);
	}
	public void setSelectionForeground(Color color){
		tabFolder.setSelectionForeground(color);
	}
	public int getSelectionIndex(){
		return tabFolder.getSelectionIndex();
	}
	public int getTabCount(){
		return tabs.size();
	}
	public int getVisibleTabCount(){
		return tabFolder.getItemCount();
	}
	public void setSimple(boolean b) {
		tabFolder.setSimple(b);
	}

	public void closeTabs() {
		for (CTabItem item : tabFolder.getItems()) {
			item.dispose();
		}
	}

}
