package rdw.spinner;

import java.lang.reflect.Field;
import java.text.DecimalFormat;

import org.eclipse.swt.widgets.Composite;

import rde.annotations.NotNull;
import rde.annotations.PrimaryKey;
import rdw.interfaces.DbControl;
import cw.controls.spinner.CSpinner;
import cw.controls.textfield.NumberFormatter;

public class RSpinner extends CSpinner implements DbControl {

	private Field field = null;
	private String fieldName = null;
	private Object object = null;
	private String pattern = null;
	
	private boolean nullable = true;
	
	public RSpinner(Composite parent, int style) {
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
		if ((field.getType().equals(Integer.TYPE))
		||(field.getType().equals(Integer.class))
		||(field.getType().equals(Long.TYPE))
		||(field.getType().equals(Long.class))
		){
			if (pattern == null)
				pattern = "0";
			setPattern(pattern);
		}else if ((field.getType().equals(Float.TYPE))
		||(field.getType().equals(Float.class))
		||(field.getType().equals(Double.TYPE))
		||(field.getType().equals(Double.class))
		){
			if (pattern == null)
				pattern = "0.00";
			setPattern(pattern);
		}else{
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
		Object newValue = null;
		NumberFormatter nf = (NumberFormatter)getFormatter();
		if (!isEmpty()){
			if((field.getType().equals(Integer.TYPE))
					||(field.getType().equals(Integer.class))){
				newValue = nf.getNumber().intValue();
			}else if((field.getType().equals(Long.TYPE))
					||(field.getType().equals(Long.class))){
				newValue = nf.getNumber().longValue();
			}else if((field.getType().equals(Float.TYPE))
					||(field.getType().equals(Float.class))){
				newValue = nf.getNumber().floatValue();
			}else if ((field.getType().equals(Double.TYPE))
					||(field.getType().equals(Double.class))){
				newValue = nf.getNumber().doubleValue();
			}
		}
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
			if ((field.getType().equals(Integer.TYPE))
			||(field.getType().equals(Integer.class))
			||(field.getType().equals(Long.TYPE))
			||(field.getType().equals(Long.class))
			||(field.getType().equals(Float.TYPE))
			||(field.getType().equals(Float.class))
			||(field.getType().equals(Double.TYPE))
			||(field.getType().equals(Double.class))){
				DecimalFormat decimalFormat = new DecimalFormat(pattern);
				setText(decimalFormat.format(value));
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
		throw new UnsupportedOperationException("Opera��o n�o permitida!");
	}
	
}
