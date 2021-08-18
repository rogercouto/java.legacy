package rdw.find;

import java.util.ArrayList;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;

import rde.RDE;
import rde.database.Database;
import rde.datasource.DataSource;
import rdw.table.DTableColumn;
import cw.tools.Screen;

public class FindDialog extends ShellFind {

	private ArrayList<String> columnNames = new ArrayList<String>();
	private Object object = null;
	private int findMethod = RDE.FIND_START_WITH;
	private boolean caseSensitive = true;
	private boolean initialized = false;
	private int startIndex = 0;
	private int findIndex = -1;
	private Listener findListener = null;
	
	public FindDialog(Shell parent) {
		super(parent);
	}
	public void addColumn(String text,String fieldName,int width,int alignment,boolean findable){
		table.addColumn(text, fieldName, width, alignment);
		if (findable)
			columnNames.add(text);
	}
	public void addColumn(String text,String[] fieldNames,int width,int alignment,boolean findable){
		table.addColumn(text, fieldNames, width, alignment);
		if (findable)
			columnNames.add(text);
	}
	public void addColumn(String text,String fieldName,int width,boolean findable){
		table.addColumn(text, fieldName, width);
		if (findable)
			columnNames.add(text);
	}
	public void addColumn(String text,String[] fieldNames,int width,boolean findable){
		table.addColumn(text, fieldNames, width);
		if (findable)
			columnNames.add(text);
	}
	public void setDataSource(DataSource dataSource){
		table.setDataSource(dataSource);
	}
	public void setClass(Class<?> c){
		table.setClass(c);
	}
	public void setLinesVisibe(boolean show){
		table.setLinesVisible(show);
	}
	
	public void setFindIndex(int index){
		this.startIndex = index;
		if (!initialized)
			return;
		MenuItem[] items = popUp.getItems();
		if (index >= items.length)
			return;
		for (int i = 0; i < items.length; i++) {
			items[i].setSelection(i == index);
		}
	}
	
	public int getFindIndex(){
		MenuItem[] items = popUp.getItems();
		for (int i = 0; i < items.length; i++) {
			if (items[i].getSelection())
				return i;
		}
		return -1;
	}
	
	/**
	 * Listener com ação a ser executada antes da buttonBusca
	 * @param listener listener com a ação a ser excutada
	 * obs: arg0.data vai conter a string para ser modificada antes da buttonBusca
	 */
	public void setFindListener(Listener listener){
		findListener = listener;
	}
	/**
	 * Adiciona um texto no campo de buttonBusca
	 * @param value
	 */
	public void setValue(String value){
		textBusca.setText(value);
		textBusca.setSelection(value.length());
		textBusca.setFocus();
	}
		
	public void setSize(Point size){
		shell.setSize(size);
		Screen.centralize(shell,getParent());
	}
	
	public void setText(String title){
		shell.setText(title);
	}
	
	public void addInsertListener(Listener listener){
		buttonInsert.addListener(SWT.Selection, listener);
		if (listener != null)
			buttonInsert.setVisible(true);
	}
	
	public void addUpdateListener(Listener listener){
		buttonUpdate.addListener(SWT.Selection, listener);
		if (listener != null)
			buttonUpdate.setVisible(true);
		buttonUpdate.setEnabled(table.getSelectionIndex() < 0);
	}
	
	private void initialize(){
		if (initialized)
			return;
		for (String string : columnNames) {
			MenuItem item = new MenuItem(popUp,SWT.RADIO);
			item.setText(string);
			if (popUp.indexOf(item)==startIndex)
				item.setSelection(true);
			item.addListener(SWT.Selection, new Listener(){
				@Override
				public void handleEvent(Event arg0) {
					actFind();
				}
			});
		}
		buttonBusca.addSelectionListener (new SelectionAdapter () {
			public void widgetSelected (SelectionEvent e) {
				if (e.detail == SWT.ARROW) {
					Rectangle rect = buttonBusca.getBounds ();
					Point pt = new Point (rect.x, rect.y + rect.height);
					pt = findToolBar.toDisplay (pt);
					popUp.setLocation (pt.x, pt.y);
					popUp.setVisible (true);
				}else
					actFind();
			}
		});
		textBusca.addKeyListener(new org.eclipse.swt.events.KeyAdapter() {
			public void keyPressed(org.eclipse.swt.events.KeyEvent e) {
				if (e.keyCode == SWT.CR || (e.keyCode == SWT.KEYPAD_CR)){
					e.doit = false;
					actFind();
				}
			}
		});
		textBusca.addModifyListener(new org.eclipse.swt.events.ModifyListener() {
			@Override
			public void modifyText(org.eclipse.swt.events.ModifyEvent e) {
				labelMessage.setVisible(false);
				textBusca.setFont(NORM_FONT);
				textBusca.setForeground(NORM_COLOR);
			}
		});
		buttonSelect.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
			@Override
			public void widgetSelected(org.eclipse.swt.events.SelectionEvent e) {
				actSelect();
			}
		});
		buttonCancel.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
			@Override
			public void widgetSelected(org.eclipse.swt.events.SelectionEvent e) {
				shell.close();
			}
		});
		table.addListener(SWT.KeyDown, new Listener(){
			@Override
			public void handleEvent(Event arg0) {
				if (arg0.keyCode == SWT.CR || (arg0.keyCode == SWT.KEYPAD_CR)){
					actSelect();
				}
			}
		});
		table.addListener(SWT.Selection, new Listener(){
			@Override
			public void handleEvent(Event arg0) {
				buttonSelect.setEnabled(true);
				buttonUpdate.setEnabled(true);
			}
		});
		table.addListener(SWT.MouseDoubleClick, new Listener(){
			@Override
			public void handleEvent(Event arg0) {
				int index = table.getSelectionIndex();
				if (index >= 0)
					actSelect();
			}
		});
		shell.addShellListener(new org.eclipse.swt.events.ShellAdapter() {
			public void shellClosed(org.eclipse.swt.events.ShellEvent e) {
				object = null;
				e.doit = false;
				shell.setVisible(false);
			}
		});
		initialized = true;
	}
	
	private void actSelect(){
		int index = table.getSelectionIndex();
		if (index < 0)
			return;
		object = table.getObject(index);
		shell.setVisible(false);
	}
	
	private void actFind(){
		initialize();
		buttonSelect.setEnabled(false);
		int index = -1;
		MenuItem[] items = popUp.getItems();
		for (int i = 0; i < items.length; i++) {
			if (items[i].getSelection()){
				index = i;
				break;
			}
		}
		if (index < 0)
			return;
		Database database = table.getDataSource().getDatabase();
		String string = textBusca.getText().replaceAll("[']", "''");
		if (findListener != null){
			Event arg0 = new Event();
			arg0.data = string;
			findListener.handleEvent(arg0);
			string = (String)arg0.data;
		}
		String filter = null;
		if (database.getStringType() != null){
			filter = caseSensitive ? "CAST(" : "UPPER(CAST(";
			DTableColumn column = table.getColumn(index);
			String[] fieldNames = column.getFieldNames();
			char divider = column.getDivider(); 
			for (int i = 0; i < fieldNames.length; i++) {
				if (i > 0){
					filter += "||'";
					filter += (divider != ' ')?" "+divider+" ": divider;
					filter += "'||";
				}
				filter += fieldNames[i];
			}
			filter += " AS "+database.getStringType()+")";
		}else{
			filter = caseSensitive ? "" : "UPPER(";
			DTableColumn column = table.getColumn(index);
			String[] fieldNames = column.getFieldNames();
			char divider = column.getDivider(); 
			String function = database.getConcatFunction();
			if (fieldNames.length > 0 && function != null)
				filter += database.getConcatFunction()+"(";
			for (int i = 0; i < fieldNames.length; i++) {
				if (i > 0){
					filter += function != null? ",'":"||'";
					filter += (divider != ' ')?" "+divider+" ": divider;
					filter += function != null? "',":"'||";
				}
				filter += fieldNames[i];
			}
			if (function != null)
				filter += ")";
			//System.out.println(filter);
		}
		switch (findMethod) {
		case RDE.FIND_EQUALS:
			filter += caseSensitive ? " = '"+string+"'" :" = UPPER('"+string+"')"  ;
			break;
		case RDE.FIND_START_WITH:
			filter += caseSensitive ? " LIKE '"+string+"%'":" LIKE UPPER('"+string+"%')" ;
			break;
		case RDE.FIND_END_WITH:
			filter += caseSensitive ? "LIKE '%"+string+"'":" LIKE UPPER('%"+string+"')" ;
			break;
		case RDE.FIND_CONTAIN:
			filter += caseSensitive ? " LIKE '%"+string+"%'":" LIKE UPPER('%"+string+"%')" ;
			break;
		default:
			break;
		}
		if (findIndex < 0){
			findIndex = table.getFilterCount();
			table.addFilter(filter);
		}else
			table.setFilter(findIndex, filter);
		if (!table.isOpen())
			table.open();
		if (table.getItemCount() > 0){
			table.setSelection(0);
			table.setFocus();
			buttonSelect.setEnabled(true);
			labelMessage.setVisible(false);
			textBusca.setForeground(shell.getDisplay().getSystemColor(SWT.COLOR_BLACK));
		}else{
			labelMessage.setVisible(true);
			textBusca.setForeground(shell.getDisplay().getSystemColor(SWT.COLOR_RED));
		}
	}
	
	public void find(){
		initialize();
		actFind();
	}
	
	public Object open() {
		initialize();
		Display display = shell.getDisplay();
		Screen.centralize(shell,getParent());
		shell.open();
		textBusca.setFocus();
		while (!shell.isDisposed() && (shell.isVisible())) {
			if (!display.readAndDispatch())
				display.sleep();
		}
		return object;
	}
	public Shell getShell() {
		return shell;
	}
	public void setShowSql(boolean showSql) {
		this.table.setShowSql(showSql);
	}
	public boolean isShowSql() {
		return table.isShowSql();
	}
	
	public void hide(){
		this.shell.setVisible(false);
	}
	
	public void setCustomQuery(String sql){
		table.setCustomQuery(sql);
	}
	
	public Object getObject(int index){
		return table.getObject(index);
	}
	public int getSelectionIndex(){
		return table.getSelectionIndex();
	}
	
}
