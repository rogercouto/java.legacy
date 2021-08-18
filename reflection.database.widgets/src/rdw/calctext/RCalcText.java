package rdw.calctext;

import java.lang.reflect.Field;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Listener;

import rde.RDE;
import rde.annotations.NotNull;
import rde.util.Logger;
import rdw.exceptions.RDWError;
import rdw.interfaces.DbControl;
import cw.controls.calctext.CalcText;

public class RCalcText extends CalcText implements DbControl {

	private Field field = null;
	private String fieldName = null;
	private Object object = null;
	private String pattern = null;
	
	public RCalcText(Composite parent, int style) {
		super(parent, style);
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
			setPattern(pattern);
		}else if ((field.getType().equals(Long.TYPE))
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
		if (!isEmpty()){
			if((field.getType().equals(Float.TYPE))
					||(field.getType().equals(Float.class))){
				newValue = getFloat();
			}else if ((field.getType().equals(Double.TYPE))
					||(field.getType().equals(Double.class))){
				newValue = getDouble();
			}
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
		try {
			Number value = (Number)field.get(object);
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
				setNumber(value);				
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

	@Override
	public void addListener(int eventType,Listener listener){
		super.addListener(eventType, listener);
		textCalc.addListener(eventType, listener);
		buttonCalc.addListener(eventType, listener);
	}
	@Override
	public void removeListener(int eventType,Listener listener){
		super.addListener(eventType, listener);
		textCalc.removeListener(eventType, listener);
		buttonCalc.removeListener(eventType, listener);
	}
	
}
