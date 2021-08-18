package rdw.spinner;

import java.lang.reflect.Field;

import org.eclipse.swt.widgets.Composite;

import rde.annotations.NotNull;
import rde.annotations.PrimaryKey;
import rdw.interfaces.DbControl;
import cw.controls.spinner.CDSpinner;

public class RDSpinner extends CDSpinner implements DbControl {

	private Field field = null;
	private String fieldName = null;
	private Object object = null;
	
	private boolean nullable = true;
	
	public RDSpinner(Composite parent, int style) {
		super(parent, style);
		this.setEnabled(false);
	}

	@Override
	public String getErrorMessage() {
		if (field.isAnnotationPresent(NotNull.class) && (isEmpty()))
			return field.getAnnotation(NotNull.class).errorMessage();
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
			e.printStackTrace();
			return;
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
			return;
		}
		if ((!field.getType().equals(Float.TYPE))
		&&(!field.getType().equals(Float.class))
		&&(!field.getType().equals(Double.TYPE))
		&&(!field.getType().equals(Double.class))
		){
			throw new UnsupportedOperationException("Invalid data type");
		}
		clear();
		this.setEnabled(true);
		if (field.isAnnotationPresent(PrimaryKey.class)|| field.isAnnotationPresent(NotNull.class))
			nullable = false;
		reset();
	}

	@Override
	public boolean post() {
		Object newValue = getValue();
		try {
			field.set(object, newValue);
			return true;
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		return false;
	}

	@Override
	public void reset() {
		try {
			Object value = field.get(object);
			clear();
			if (value == null){
				return;
			}
			if ((field.getType().equals(Float.TYPE))
			||(field.getType().equals(Float.class))
			||(field.getType().equals(Double.TYPE))
			||(field.getType().equals(Double.class))){
				if (value instanceof Double) {
					Double d = (Double) value;
					setValue(d);
				}
			}
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
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
		if (!nullable && isEmpty())
			return false;
		return true;
	}
	
	@Override
	public boolean isNullable() {
		return nullable;
	}

	@Deprecated
	@Override
	public void setNullable(boolean nullable) {
		throw new UnsupportedOperationException("Operação não permitida!");
	}
	
}
