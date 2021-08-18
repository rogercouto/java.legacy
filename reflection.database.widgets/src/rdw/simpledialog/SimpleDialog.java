package rdw.simpledialog;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TableItem;

import rde.annotations.ForeignKey;
import rde.annotations.NotNull;
import rde.connection.ConnectionManager;
import rde.datasource.DataSource;
import rde.exceptions.ValidationException;
import rde.persistence.Persistence;
import rdw.calctext.RCalcText;
import rdw.calendartext.RCalendarText;
import rdw.checkbox.RCheckBox;
import rdw.fkcombo.FKCombo;
import rdw.map.RDMap;
import rdw.textfield.RTextField;
import cw.controls.textfield.Formatter;
import cw.tools.Dialog;
import cw.tools.Screen;
import cw.tools.Util;

public class SimpleDialog extends ShellDialog {

	private Object object = new Object();
	private ConnectionManager manager = null;
	private List<String> fieldNames = new ArrayList<String>();
	private List<String> refNames = new ArrayList<String>();
	private List<String> displayNames = new ArrayList<String>();
	private List<Field> fields = new ArrayList<Field>();
	private List<Integer> widths = new ArrayList<Integer>();
	private List<Formatter> formatters = new ArrayList<Formatter>();
	private RDMap map = null; 
	protected Object result;
	private int fontSize = 11;
	private int shellWidth;
	private int shellHeight;
	private List<Integer> customStyles = new ArrayList<Integer>();
	
	public SimpleDialog(Shell parent, Object obj, int op) {
		super(parent);
		this.object = obj;
		map = new RDMap(op);
	}
	
	public void allFields(){
		Field[] af = object.getClass().getDeclaredFields();
		for (Field field : af) {
			fields.add(field);
			fieldNames.add(field.getName());
			displayNames.add(field.getName());
			refNames.add(null);
			widths.add(200);
			formatters.add(null);
			customStyles.add(null);
		}
	}
	
	public void addField(String fieldName, String displayName){
		try {
			
			Field[] af = object.getClass().getDeclaredFields();
			for (Field f : af) {
				if (f.getName().compareTo(fieldName) == 0){
					fields.add(f);
					fieldNames.add(f.getName());
					displayNames.add(displayName);
					refNames.add(null);
					widths.add(200);
					formatters.add(null);
					customStyles.add(null);
					break;
				}
			}
		} catch (SecurityException e) {
			e.printStackTrace();
		}
	}
	
	public void addField(String fieldName, String displayName, String refName){
		try {
			Field[] af = object.getClass().getDeclaredFields();
			for (Field f : af) {
				if (f.getName().compareTo(fieldName) == 0){
					fields.add(f);
					fieldNames.add(f.getName());
					displayNames.add(displayName);
					refNames.add(refName);
					widths.add(200);
					formatters.add(null);
					customStyles.add(null);
					break;
				}
			}
		} catch (SecurityException e) {
			e.printStackTrace();
		}
	}
	
	public void addField(String fieldName, String displayName, String refName, int width){
		try {
			Field[] af = object.getClass().getDeclaredFields();
			for (Field f : af) {
				if (f.getName().compareTo(fieldName) == 0){
					fields.add(f);
					fieldNames.add(f.getName());
					displayNames.add(displayName);
					refNames.add(refName);
					widths.add(width);
					formatters.add(null);
					customStyles.add(null);
					break;
				}
			}
		} catch (SecurityException e) {
			e.printStackTrace();
		}
	}
	
	public void addField(String fieldName, String displayName, int width){
		try {
			Field[] af = object.getClass().getDeclaredFields();
			for (Field f : af) {
				if (f.getName().compareTo(fieldName) == 0){
					fields.add(f);
					fieldNames.add(f.getName());
					displayNames.add(displayName);
					refNames.add(null);
					widths.add(width);
					formatters.add(null);
					customStyles.add(null);
					break;
				}
			}
		} catch (SecurityException e) {
			e.printStackTrace();
		}
	}
	
	private void initialize(){
		map.setDataSource(new DataSource(manager));
		map.setPersistence(new Persistence(manager));
		map.set(object);
		for (int i = 0; i < fields.size(); i++) {
			Control control = null;
			if (fields.get(i).isAnnotationPresent(ForeignKey.class)){
				FKCombo combo = new FKCombo(table, SWT.READ_ONLY);
				if (refNames.get(i) != null)
					combo.setFieldName(refNames.get(i));
				else
					combo.setFieldName(fieldNames.get(i));
				control = combo;
				map.add(combo);
			}else if (fields.get(i).getType() == String.class ||
					fields.get(i).getType() == Integer.class ||
					fields.get(i).getType() == Integer.TYPE || 
					fields.get(i).getType() == Long.class ||
					fields.get(i).getType() == Long.TYPE
			){
				RTextField text = null;
				if (customStyles.get(i) == null)
					text = new RTextField(table, SWT.BORDER);
				else
					text = new RTextField(table, customStyles.get(i));
				text.setFieldName(fieldNames.get(i));
				if (formatters.get(i) != null)
					text.setFormatter(formatters.get(0));
				map.add(text);
				control = text;
			}else if (fields.get(i).getType() == Boolean.class || fields.get(i).getType() == Boolean.TYPE){
				RCheckBox check = new RCheckBox(table, SWT.NONE);
				check.setFieldName(fieldNames.get(i));
				check.setText("");
				map.add(check);
				control = check;
			}else if (
					fields.get(i).getType() == Float.class ||
					fields.get(i).getType() == Float.TYPE || 
					fields.get(i).getType() == Double.class ||
					fields.get(i).getType() == Double.TYPE
			){
				RCalcText text = new RCalcText(table, SWT.BORDER);
				text.setFieldName(fieldNames.get(i));
				text.open();
				map.add(text);
				control = text;
			}else if (fields.get(i).getType() == Date.class){
				RCalendarText text = new RCalendarText(table, SWT.BORDER);
				text.setFieldName(fieldNames.get(i));
				map.add(text);
				control = text;
			}else{
				Label label = new Label(table, SWT.BORDER);
				label.setText("Item não editavel");
				control = label;
			}
			TableItem item = new TableItem(table, SWT.NONE);
			StringBuilder builder = new StringBuilder();
			builder.append(displayNames.get(i));
			if (fields.get(i).isAnnotationPresent(NotNull.class)){
				builder.append("*");
			}
			builder.append(": ");
			item.setText(0, builder.toString());
			item.setText(1, "");
			TableEditor editor = new TableEditor(table);
			editor.setEditor(control, item, 1);
			editor.horizontalAlignment = SWT.LEFT;
			editor.grabHorizontal = true;
			editor.minimumWidth = widths.get(i);
			TableEditor editor0 = new TableEditor(table);
			editor0.horizontalAlignment = SWT.RIGHT;
			editor0.grabHorizontal = true;
			editor0.grabVertical = true;
			Label label = new Label(table, SWT.RIGHT);
			label.setText(builder.toString());
			editor0.setEditor(label, item, 0);
			table.getColumn(0).pack();
			table.getColumn(0).setAlignment(SWT.RIGHT);
			table.getColumn(1).setWidth(0);
			//table.getColumn(1).setWidth(widths.get(i));
		}
		map.open();
		Point point = shell.getSize();
		if (point.y > 600)
			point.y = 600;
		shell.setSize(point);
		Screen.centralize(shell, getParent());
		btnSalvar.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				try {
					map.post();
					map.save();
					result = map.getObject();
					shell.close();
				} catch (ValidationException e) {
					Dialog.error(shell, e.getMessage());
				}
			}
		});
		btnCancelar.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				shell.close();
			}
		});
		Util.setFontSize(fontSize, table);
		table.getColumn(0).pack();
		shell.pack();
		Point size = shell.getSize();
		if (shellWidth > 0)
			size.x = shellWidth;
		if (shellHeight > 0)
			size.y = shellHeight;
		shell.setSize(size);
	}
	
	public void setFontSize(int size){
		fontSize = size;
		Util.setFontSize(fontSize, table);
		table.getColumn(0).pack();
	}
	
	public Object open() {
		initialize();
		shell.open();
		shell.layout();
		Display display = getParent().getDisplay();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
		return result;
	}

	public ConnectionManager getManager() {
		return manager;
	}

	public void setManager(ConnectionManager manager) {
		this.manager = manager;
	}
	
	public void setTitle(String title){
		shell.setText(title);
	}
	
	public void setFormatter(int index, Formatter formatter){
		formatters.set(index, formatter);
	}
	
	public void setSize(int x, int y){
		shell.setSize(x, y);
	}

	public void setShellWidth(int width){
		shellWidth = width;
	}
	
	public void setShellHeight(int height){
		shellHeight = height;
	}
	
	public void setCustomStyle(int index, int style){
		this.customStyles.set(index, style);
	}
}
