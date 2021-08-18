package rdw.combo;

import java.lang.reflect.Field;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;

import rde.RDE;
import rde.annotations.NotNull;
import rde.util.Logger;
import rdw.exceptions.RDWError;
import rdw.interfaces.DbControl;
import cw.controls.combobox.CComboBox;

public class RCombo extends CComboBox implements DbControl {

	private Field field = null;
	private String fieldName = null;
	private Object object = null;
	
	public RCombo(Composite parent, int style) {
		super(parent, style);
		GridData gridData = (GridData) combo.getLayoutData();
		gridData.horizontalSpan = 2;
		gridData.grabExcessVerticalSpace = true;
		gridData.verticalAlignment = SWT.FILL;
		this.setEnabled(false);
	}

	@Override
	public String getErrorMessage() {
		if (field.isAnnotationPresent(NotNull.class)){
			if (isEmpty())
				return field.getAnnotation(NotNull.class).errorMessage();
		}
		return null;
	}

	@Override
	public Field getField() {
		return field;
	}

	@Override
	public void open() {
		if (fieldName == null || (object == null))
			return;
		try {
			field = object.getClass().getDeclaredField(fieldName);
			field.setAccessible(true);
		} catch (SecurityException e) {
			if (RDE.logErrors)
				Logger.log(e);

			e.printStackTrace();
			return;
		} catch (NoSuchFieldException e) {
			if (RDE.logErrors)
				Logger.log(e);
			e.printStackTrace();
			return;
		}
		if (!field.getType().equals(String.class) && (!field.getType().equals(Integer.class) 
		&& (!field.getType().equals(Integer.TYPE)))){
			RDWError error = new RDWError("Inválid data type!");
			if (RDE.logErrors)
				Logger.log(error);
			throw error;
		}
		this.setEnabled(true);
		reset();
	}

	@Override
	public boolean post() {
		Object newValue = null;
		if (isEmpty()){
			try {
				field.set(object, newValue);
			} catch (IllegalArgumentException e) {
				if (RDE.logErrors)
					Logger.log(e);
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				if (RDE.logErrors)
					Logger.log(e);
				e.printStackTrace();
			}
			return true;
		}
		if (field.getType().equals(String.class)){
			newValue = getText();
		}else if (field.getType().equals(Integer.class)){
			if (values != null){
				int index = combo.getSelectionIndex();
				if (index < 0)
					return false;
				newValue = values[index];
			}else
				newValue = Integer.parseInt(getText());
		}
		try {
			field.set(object, newValue);
			return true;
		} catch (IllegalArgumentException e) {
			if (RDE.logErrors)
				Logger.log(e);
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			if (RDE.logErrors)
				Logger.log(e);
			e.printStackTrace();
		}
		return false;
	}

	@Override
	public void reset() {
		if (field == null || object == null)
			return;
		try {
			Object value = field.get(object);
			deselectAll();
			if (value == null){
				return;
			}
			if (field.getType().equals(String.class)||
			(field.getType().equals(Integer.class)||
			(field.getType().equals(Integer.TYPE)))){
				if (values == null){
					for (int i = 0; i < getItemCount(); i++) {
						if (value.toString().trim().compareTo(getItem(i).trim())==0){
							select(i);
							break;
						}
					}
				}else{
					for (int i = 0; i < getItemCount(); i++) {
						Integer iVal = (Integer)value;
						if (iVal.compareTo(values[i])==0){
							select(i);
							break;
						}
					}
				}
			}
		} catch (IllegalArgumentException e) {
			if (RDE.logErrors)
				Logger.log(e);
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			if (RDE.logErrors)
				Logger.log(e);
			e.printStackTrace();
		}
	}

	
	@Override
	public void set(Object object) {
		this.object = object;
	}

	@Override
	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}
	
	@Override
	public boolean validate(){
		if (field.isAnnotationPresent(NotNull.class)){
			if (isEmpty())
				return false;
		}
		return true;
	}

	public Integer[] values = null; 
	public String[] options;
	
	@Override
	public void add(String[] options){
		this.options = options;
		super.add(options);
		reset();
	}
	
	public void addValues(Integer[] values){
		if (values.length != options.length){
			RDWError error = new RDWError("Número de valores diferentes!");
			if (RDE.logErrors)
				Logger.log(error);
			throw error;
		}
		this.values = values;	
	}
}
