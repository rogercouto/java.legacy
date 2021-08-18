package rdw.textfield;

import java.lang.reflect.Field;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.eclipse.swt.widgets.Composite;

import rde.annotations.NotNull;
import rde.annotations.PrimaryKey;
import rdw.interfaces.DbControl;
import cw.controls.textfield.DateFormatter;
import cw.controls.textfield.Formatter;
import cw.controls.textfield.MaskFormatter;
import cw.controls.textfield.NumberFormatter;
import cw.controls.textfield.CTextField;

public class RTextField extends CTextField implements DbControl {

	private Field field = null;
	private String fieldName = null;
	private Object object = null;
	private String pattern = null;
	
	public RTextField(Composite parent, int style) {
		super(parent, style);
		this.setEnabled(false);
	}

	@Override
	public String getErrorMessage() {
		if (field.isAnnotationPresent(NotNull.class)){
			if (isEmpty())
				return field.getAnnotation(NotNull.class).errorMessage();
		}else if (field.isAnnotationPresent(PrimaryKey.class)){
			if (isEmpty())
				return field.getName()+" não pode ficar em branco";
		}
		return field.getName()+" inválido(a)";
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
		||(field.getType().equals(Integer.class)))
		{
			if (pattern == null)
				pattern = "0";
			setFormatter(new NumberFormatter(pattern));
			setTextLimit(10);
		}else if ((field.getType().equals(Long.TYPE))
		||(field.getType().equals(Long.class))
		){
			if (pattern == null)
				pattern = "0";
			setFormatter(new NumberFormatter(pattern));
			setTextLimit(19);
		}else if ((field.getType().equals(Float.TYPE))
		||(field.getType().equals(Float.class))
		||(field.getType().equals(Double.TYPE))
		||(field.getType().equals(Double.class))
		){
			if (pattern == null)
				pattern = "0.00";
			this.setFormatter(new NumberFormatter(pattern));
		}else if (field.getType().equals(Date.class)){
			if (pattern == null)
				pattern = "dd/MM/yyyy";
			this.setFormatter(new DateFormatter(pattern));
		}else if (field.getType().equals(String.class)){
			if (pattern != null)
				this.setFormatter(new MaskFormatter(pattern));
		}else{
			throw new UnsupportedOperationException("Invalid data type");
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
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
			return true;
		}
		if ((field.getType().equals(Integer.TYPE))
		||(field.getType().equals(Integer.class))){
			NumberFormatter nf = (NumberFormatter)getFormatter();
			newValue = nf.getNumber().intValue(); 
		}else if((field.getType().equals(Long.TYPE))
		||(field.getType().equals(Long.class))){
			NumberFormatter nf = (NumberFormatter)getFormatter();
			newValue = nf.getNumber().longValue();
		}else if((field.getType().equals(Float.TYPE))
		||(field.getType().equals(Float.class))){
			NumberFormatter nf = (NumberFormatter)getFormatter();
			newValue = nf.getNumber().floatValue();
		}else if ((field.getType().equals(Double.TYPE))
		||(field.getType().equals(Double.class))){
			NumberFormatter nf = (NumberFormatter)getFormatter();
			newValue = nf.getNumber().doubleValue();
		}else if (field.getType().equals(Date.class)){
			DateFormatter df = (DateFormatter)getFormatter();
			newValue = df.getDate();
		}else if (field.getType().equals(String.class)){
			newValue = getText();
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
			||((field.getType().equals(Float.TYPE))
			||(field.getType().equals(Float.class))
			||(field.getType().equals(Double.TYPE))
			||(field.getType().equals(Double.class)))){
				DecimalFormat decimalFormat = null;
				Formatter formatter = getFormatter();
				if (formatter instanceof NumberFormatter) 
					decimalFormat = new DecimalFormat(formatter.getPattern());					
				else
					decimalFormat = new DecimalFormat(pattern);
				setText(decimalFormat.format(value));
			}else if (field.getType().equals(Date.class)){
				SimpleDateFormat dateFormat = null;
				
				Formatter formatter = getFormatter();
				if (formatter instanceof DateFormatter) 
					dateFormat = new SimpleDateFormat(formatter.getPattern());
				else
					dateFormat = new SimpleDateFormat(pattern);
				setText(dateFormat.format(value));
			}else if (field.getType().equals(String.class)){
				setText(value.toString());
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
		if (getValidator() != null){
			return super.validate();
		}
		if (field.isAnnotationPresent(PrimaryKey.class)|| (field.isAnnotationPresent(NotNull.class))){
			return !isEmpty();
		}
		return true;
	}
	
	public void setPattern(String pattern){
		this.pattern = pattern;
	}

}
