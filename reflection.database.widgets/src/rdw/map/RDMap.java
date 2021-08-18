package rdw.map;

import java.lang.reflect.Field;
import java.util.ArrayList;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.ToolTip;

import rde.RDE;
import rde.datasource.DataSource;
import rde.exceptions.ValidationException;
import rde.persistence.Persistence;
import rdw.fkchooser.FkChooser;
import rdw.fkcombo.FKCombo;
import rdw.interfaces.DbControl;
import cw.tools.Util;

/**
 * Classe que mapeia os controles de um formulário 
 * @author Roger
 */
public class RDMap {

	private ArrayList<DbControl> list = new ArrayList<DbControl>();
	private ArrayList<Label> tList = new ArrayList<Label>();
	private DataSource dataSource = null;
	private Persistence persistence = null;
	private Object object;
	private int operation;
	private ToolTip toolTip = null;
	private boolean saved = false;
	
	public RDMap() {
		super();
	}
	public RDMap(int operation) {
		super();
		this.operation = operation;
	}
	public RDMap(int operation,DbControl[] controls) {
		super();
		for (DbControl dbControl : controls) {
			list.add(dbControl);
			tList.add(null);
		}
		this.operation = operation;
	}
	
	public void post(){
		for (DbControl control : list) {
			control.post();
		}
	}
	public void reset(){
		for (DbControl control : list) {
			control.reset();
		}
	}
	public void open(){
		for (DbControl control : list) {
			if (object != null)
				control.set(object);
			if (dataSource != null){
				if (control instanceof FkChooser) {
					FkChooser chooser = (FkChooser) control;
					chooser.setDataSource(dataSource);
				}else if (control instanceof FKCombo) {
					FKCombo combo = (FKCombo) control;
					combo.setDataSource(dataSource);
				} 	
			}
			control.open();
		}
		for (int i = 0; i < list.size(); i++) {
			if (i < list.size()-1){
				final int next = i + 1;
				list.get(i).addListener(SWT.KeyDown, new Listener(){
					@Override
					public void handleEvent(Event arg0) {
						if ((arg0.keyCode == SWT.CR || arg0.keyCode == SWT.KEYPAD_CR)){
							arg0.doit = false;
							list.get(next).setFocus();
						}
					}
				});
			}
		}
	}
	
	public void createValidation(Shell parent){
		toolTip = new ToolTip(parent,SWT.BALLOON | SWT.ICON_WARNING);
		toolTip.setText("Atenção");
		toolTip.setAutoHide(true);
		for (int i = 0; i < list.size(); i++) {
			Label label = tList.get(i);
			if (label != null)
				createValidator(list.get(i), label);
		}
	}
	
	private void createValidator(final DbControl control,final Label label){
		label.addListener(SWT.MouseDown, new Listener(){
			@Override
			public void handleEvent(Event arg0) {
				toolTip.setVisible(false);
				toolTip.setMessage(control.getErrorMessage()+"!");
				toolTip.setVisible(true);
			}
		});
		control.addListener(SWT.Selection, new Listener(){
			@Override
			public void handleEvent(Event arg0) {
				toolTip.setVisible(false);
			}
		});
		control.addListener(SWT.Modify, new Listener(){
			@Override
			public void handleEvent(Event arg0) {
				label.setVisible(false);
			}
		});
		control.addListener(SWT.FocusIn, new Listener(){
			@Override
			public void handleEvent(Event arg0) {
				label.setVisible(false);
			}
		});
		control.addListener(SWT.MouseDown, new Listener(){
			@Override
			public void handleEvent(Event arg0) {
				toolTip.setVisible(false);
			}
		});
		control.addListener(SWT.FocusOut, new Listener(){
			@Override
			public void handleEvent(Event arg0) {
				label.setVisible(!control.validate());
			}
		});
	}
	
	public void hideToolTip(){
		if (!toolTip.isDisposed())
			toolTip.setVisible(false);
	}
	public void showToolTip(String message){
		toolTip.setVisible(false);
		toolTip.setMessage(message);
		toolTip.setVisible(true);
	}
	
	public void print(){
		if (object != null)
			Util.printFields(object);
		else
			System.out.println(object);
	}
	
	public void set(Object object){
		this.object = object;
	}
	public void setDataSource(DataSource dataSource){
		this.dataSource = dataSource;
	}
	public void setPersistence(Persistence persistence){
		this.persistence = persistence;
	}
	public void add(DbControl control){
		list.add(control);
		tList.add(null);
	}
	public void setLabel(int index,Label label){
		if (index >= list.size())
			throw new UnsupportedOperationException("Incompatible size!");
		tList.set(index, label);
	}
	public void add(DbControl[] controls){
		for (DbControl control : controls) {
			list.add(control);
			tList.add(null);
		}
	}
	
	public void set(int index,DbControl control){
		list.set(index,control);
		tList.set(index,null);
	}
	public void remove(int index){
		list.remove(index);
		tList.remove(index);
	}
	
	public void save() throws ValidationException{
		post();
		if (persistence == null)
			throw new UnsupportedOperationException("Persistence not alowed");
		switch (operation) {
		case RDE.OP_INSERT:
			persistence.insert(object);
			saved = true;
			operation = RDE.OP_UPDATE;
			break;
		case RDE.OP_UPDATE:
			persistence.update(object);
			saved = true;
			break;	
		case RDE.OP_NONE:
			return;
		default:
			break;
		}
	}
	
	public void setFieldValue(String fieldName,Object value){
		try {
			Field[] fields = object.getClass().getDeclaredFields();
			for (Field field : fields) {
				field.setAccessible(true);
				if (field.getName().compareTo(fieldName)==0)
					field.set(object, value);
			}
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
	}
	
	public int getOperation() {
		return operation;
	}
	public void setOperation(int operation) {
		this.operation = operation;
	}
	public DataSource getDataSource() {
		return dataSource;
	}
	public Object getObject() {
		return object;
	}
	
	public void addListener(int eventType,Listener listener){
		for (DbControl control : list) {
			control.addListener(eventType, listener);
		}
	}
	public void removeListener(int eventType,Listener listener){
		for (DbControl control : list) {
			control.removeListener(eventType, listener);
		}
	}
	public void validateAll() throws ValidationException {
		String error = "";
		for (DbControl control : list) {
			if (!control.validate()){
				if (error.trim().length() > 0)
					error += ",\r";
				error += control.getErrorMessage();
			}
		}
		if (error.trim().length() > 0){
			error += "!";
			throw new ValidationException(error);
		}
	}
	public boolean isSaved() {
		return saved;
	}
	public void setSaved(boolean saved) {
		this.saved = saved;
	}
}
