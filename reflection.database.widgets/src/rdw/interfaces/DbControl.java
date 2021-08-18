package rdw.interfaces;

import java.lang.reflect.Field;

import cw.interfaces.CControl;

/**
 * Interface para implementar controles p/ transição com o banco de dados 
 * @author Sup. Compras
 */
public interface DbControl extends CControl{

	void set(Object object);
	void setFieldName(String fieldName);
	Field getField();
	void open();
	String getErrorMessage();
	boolean post();
	boolean setFocus();
	void reset();
	
}
