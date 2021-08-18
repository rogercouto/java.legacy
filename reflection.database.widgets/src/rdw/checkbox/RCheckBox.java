package rdw.checkbox;

import java.lang.reflect.Field;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Listener;

import rde.RDE;
import rde.util.Logger;
import rdw.exceptions.RDWError;
import rdw.interfaces.DbControl;

public class RCheckBox extends Composite implements DbControl{

	private Field field = null;  //  @jve:decl-index=0:
	private String fieldName = null;
	private Object object = null;  //  @jve:decl-index=0:
	private Button checkBox = null;
	private String text = null;

	public RCheckBox(Composite parent, int style) {
		super(parent, style);
		initialize();
	}

	private void initialize() {
		GridLayout gridLayout = new GridLayout();
		gridLayout.horizontalSpacing = 0;
		gridLayout.marginWidth = 0;
		gridLayout.marginHeight = 0;
		gridLayout.verticalSpacing = 0;
		this.setLayout(gridLayout);
		GridData gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;
		gridData.grabExcessVerticalSpace = true;
		gridData.verticalAlignment = GridData.FILL;
		checkBox = new Button(this, SWT.CHECK);
		checkBox.setLayoutData(gridData);
		checkBox.setEnabled(false);
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
			if (text == null)
				checkBox.setText(field.getName());
			else
				checkBox.setText(text);
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
		if (!field.getType().equals(Boolean.TYPE)
		&&(!field.getType().equals(Boolean.class))){
			RDWError error = new RDWError("Inv�lid data type!");
			if (RDE.logErrors)
				Logger.log(error);
			throw error;
		}
		reset();
		checkBox.setEnabled(true);
	}

	@Override
	public boolean post() {
		if (!field.getType().equals(Boolean.TYPE)
		&&(!field.getType().equals(Boolean.class))){
			return false;
		}
		try {
			field.set(object, checkBox.getSelection());
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
		if (!field.getType().equals(Boolean.TYPE)
		&&(!field.getType().equals(Boolean.class))){
			return;
		}
		if (checkBox == null)
			return;
		try {
			Boolean b = (Boolean)field.get(object);
			if (b == null)
				return;	
			checkBox.setSelection(b);
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
	public boolean validate() {
		return true;
	}

	public void setText(String string){
		text = string;
	}
	public String getText(){
		return text;
	}
	@Override
	public void setForeground(Color color){
		checkBox.setForeground(color);
	}
	@Override
	public Color getForeground(){
		return checkBox.getForeground();
	}
	@Override
	public void setBackground(Color color){
		checkBox.setBackground(color);
	}
	@Override
	public Color getBackground(){
		return checkBox.getBackground();
	}
	@Override
	public void setFont(Font font){
		checkBox.setFont(font);
	}
	@Override
	public Font getFont(){
		return checkBox.getFont();
	}
	
	@Override
	public void addListener(int eventType,Listener listener){
		if (checkBox == null)
			return;
		checkBox.addListener(eventType, listener);
	}
	@Override
	public void removeListener(int eventType,Listener listener){
		checkBox.removeListener(eventType, listener);
	}

	@Override
	public boolean isNullable() {
		return false;
	}

	@Deprecated
	@Override
	public void setNullable(boolean nullable) {
		throw new UnsupportedOperationException("Opera��o n�o permitida!");		
	}
	
	@Override
	public void setEnabled(boolean enabled){
		checkBox.setEnabled(enabled);
	}
	
	public boolean isSelected(){
		return checkBox.getSelection();
	}
	
	public void setSelected(boolean selected){
		checkBox.setSelection(selected);
	}
	
	public void setLabel(String text){
		checkBox.setText(text);
	}
	
}
