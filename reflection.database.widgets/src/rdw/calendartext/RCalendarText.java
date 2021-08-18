package rdw.calendartext;

import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Listener;

import rde.RDE;
import rde.annotations.NotNull;
import rde.util.Logger;
import rdw.exceptions.RDWError;
import rdw.interfaces.DbControl;
import cw.controls.calendartext.CalendarText;

public class RCalendarText extends CalendarText implements DbControl {

	private Field field = null;
	private String fieldName = null;
	private Object object = null;
	//private String pattern = null;
	
	public RCalendarText(Composite parent, int style) {
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
		if (field.getType().equals(Date.class)){
			if (pattern == null)
				pattern = "dd/MM/yyyy";
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
		if (field.getType().equals(Date.class)){
			newValue = getDate();
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
			Object value = field.get(object);
			clear();
			if (value == null){
				return;
			}
			if (field.getType().equals(Date.class)){
				SimpleDateFormat dateFormat = new SimpleDateFormat(pattern);
				setText(dateFormat.format(value));
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
		text.addListener(eventType, listener);
		buttonCalendar.addListener(eventType, listener);
	}
	@Override
	public void removeListener(int eventType,Listener listener){
		text.removeListener(eventType, listener);
		buttonCalendar.addListener(eventType, listener);
	}

}
