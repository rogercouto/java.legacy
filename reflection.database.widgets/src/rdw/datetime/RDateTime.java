package rdw.datetime;

import java.lang.reflect.Field;
import java.util.Date;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DateTime;
import org.eclipse.swt.widgets.Listener;

import rde.RDE;
import rde.util.Logger;
import rdw.interfaces.DbControl;
import cw.tools.Util;

public class RDateTime extends Composite implements DbControl{

	private DateTime dateTime = null;
	private int style;
	
	private Field field = null;
	private String fieldName = null;
	private Object object = null;

	public RDateTime(Composite parent, int style) {
		super(parent, SWT.NONE);
		this.style = style;
		initialize();
	}

	private void initialize() {
		GridLayout gridLayout = new GridLayout();
		gridLayout.horizontalSpacing = 0;
		gridLayout.marginWidth = 0;
		gridLayout.marginHeight = 0;
		gridLayout.verticalSpacing = 0;
		createDateTime();
		this.setLayout(gridLayout);
	}

	/**
	 * This method initializes dateTime	
	 *
	 */
	private void createDateTime() {
		GridData gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;
		gridData.grabExcessVerticalSpace = true;
		gridData.verticalAlignment = GridData.FILL;
		dateTime = new DateTime(this, style);
		dateTime.setLayoutData(gridData);
	}

	@Override
	public String getErrorMessage() {
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
		this.setEnabled(true);
		reset();
	}

	@Override
	public boolean post() {
		Object newValue = null;
		if (field.getType().equals(Date.class)){
			newValue = Util.getDate(dateTime);
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
			if (value == null){
				return;
			}
			if (field.getType().equals(Date.class)){
				Date date = (Date)value;
				Util.setDate(dateTime, date);
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
		return true;
	}
	
	@Override
	public void setEnabled(boolean enabled){
		dateTime.setEnabled(enabled);
	}
	
	@Override
	public boolean getEnabled(){
		return dateTime.getEnabled();
	}
	@Override
	public void setForeground(Color color){
		dateTime.setForeground(color);
	}
	@Override
	public Color getForeground(){
		return dateTime.getForeground();
	}
	@Override
	public void setBackground(Color color){
		dateTime.setBackground(color);
	}
	@Override
	public Color getBackground(){
		return dateTime.getBackground();
	}
	@Override
	public void setFont(Font font){
		dateTime.setFont(font);
	}
	@Override
	public Font getFont(){
		return dateTime.getFont();
	}
	@Override
	public void addListener(int eventType,Listener listener){
		dateTime.addListener(eventType, listener);
	}
	@Override
	public void removeListener(int eventType,Listener listener){
		dateTime.removeListener(eventType, listener);
	}
	
	@Override
	public boolean isNullable() {
		return false;
	}

	@Deprecated
	@Override
	public void setNullable(boolean nullable) {
		throw new UnsupportedOperationException("Operação não permitida!");		
	}

	public Date getDate() {
		return Util.getDate(dateTime);
	}
	
	public int getDia(){
		return dateTime.getDay();
	}
	
	public int getMes(){
		return dateTime.getMonth()+1;
	}
	
	public int getAno(){
		return dateTime.getYear();
	}
	
}
