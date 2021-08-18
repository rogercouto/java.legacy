package rdw.imagechooser;

import java.io.File;
import java.lang.reflect.Field;

import org.eclipse.swt.widgets.Composite;

import rde.annotations.NotNull;
import rdw.interfaces.DbControl;
import cw.controls.imagechooser.CImageChooser;

public class RImageChooser extends CImageChooser implements DbControl {

	private Field field = null;
	private String fieldName = null;
	private Object object = null;
	
	public RImageChooser(Composite parent, int style) {
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
		if (!field.getType().equals(File.class)){
			throw new UnsupportedOperationException("Invalid data type");
		}
		this.setEnabled(true);
		reset();
	}

	@Override
	public boolean post() {
		Object newValue = null;
		if (field.getType().equals(File.class)){
			newValue = getFile();
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
			if (field.getType().equals(File.class)){
				setFile((File)value);
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
		if (field.isAnnotationPresent(NotNull.class)){
			if (isEmpty())
				return false;
		}
		return true;
	}

}
