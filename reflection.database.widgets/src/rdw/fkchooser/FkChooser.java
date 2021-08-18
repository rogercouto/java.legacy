/**
 *
 */
package rdw.fkchooser;

import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Listener;

import cw.controls.textfield.CTextField;
import cw.controls.textfield.MaskFormatter;
import rde.RDE;
import rde.annotations.ForeignKey;
import rde.annotations.NotNull;
import rde.annotations.PrimaryKey;
import rde.datasource.DataSource;
import rdw.exceptions.RDWError;
import rdw.find.FindDialog;
import rdw.interfaces.DbControl;

/**
 * Componente p/ selção de chaves extrangeiras
 * @author Roger
 */
public class FkChooser extends Composite implements DbControl {

	private CTextField text = null;
	private Button buttonFind = null;
	private int style;

	private DataSource dataSource = null;
	private FindDialog findDialog = null;
	private Class<?> ref = null;

	private Field key = null;
	private Object newValue = null;
	private Object object = null;
	private Object fObject = null;

	private String fieldName = null;
	private String[] displayNames = null;
	private Field[] fields = null;
	private char divider = ' ';
	private Field fkKey = null;

	private boolean nullable = true;
	/**
	 * @param parent
	 * @param style
	 */
	public FkChooser(Composite parent, int style) {
		super(parent, SWT.NONE);
		this.style = style;
		initialize();
	}

	private void initialize() {
		GridData gridData1 = new GridData();
		gridData1.heightHint = 21;
		gridData1.verticalAlignment = GridData.FILL;
		gridData1.grabExcessVerticalSpace = true;
		gridData1.horizontalAlignment = GridData.BEGINNING;
		GridData gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		gridData.grabExcessVerticalSpace = true;
		gridData.grabExcessHorizontalSpace = true;
		gridData.heightHint = -1;
		gridData.verticalAlignment = GridData.FILL;
		GridLayout gridLayout = new GridLayout();
		gridLayout.marginWidth = 0;
		gridLayout.numColumns = 2;
		gridLayout.verticalSpacing = 0;
		gridLayout.marginHeight = 0;
		gridLayout.horizontalSpacing = 0;
		text = new CTextField(this, style);
		text.setEditable(false);
		text.setBackground(new Color(Display.getCurrent(), 255, 255, 255));
		text.setLayoutData(gridData);
		buttonFind = new Button(this, SWT.NONE);
		buttonFind.setImage(new Image(Display.getCurrent(), getClass().getResourceAsStream("/rdw/icon/icon_find.png")));
		buttonFind.setLayoutData(gridData1);
		findDialog = new FindDialog(this.getShell());
		this.setLayout(gridLayout);
		this.setTabList(new Control[]{text});
		this.setBackground(new Color(Display.getCurrent(), 255, 255, 255));
		this.setEnabled(false);
	}

	public void setEnabled(boolean enabled){
		if (fields == null && (enabled))
			return;
		text.setEnabled(enabled);
		buttonFind.setEnabled(enabled);
		if (!enabled)
			text.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WIDGET_BACKGROUND));
		else
			text.setBackground(new Color(Display.getCurrent(), 255, 255, 255));
	}

	private boolean editable = true;

	public void setEditable(boolean editable){
		if (fields == null && (editable))
			return;
		text.setEditable(editable);
		buttonFind.setEnabled(editable);
		if (!editable)
			text.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WIDGET_BACKGROUND));
		else
			text.setBackground(new Color(Display.getCurrent(), 255, 255, 255));
		this.editable = editable;
	}

	public void setInputMask(String pattern){
		String string = text.getText();
		text.setFormatter(new MaskFormatter(pattern));
		text.setText(string);
	}

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
		findDialog.setDataSource(dataSource);
	}
	public void addColumn(String text,String fieldName,int width,int alignment,boolean findable){
		findDialog.addColumn(text, fieldName, width, alignment, findable);
	}
	public void addColumn(String text,String[] fieldNames,int width,int alignment,boolean findable){
		findDialog.addColumn(text, fieldNames, width, alignment, findable);
	}
	public void addColumn(String text,String fieldName,int width,boolean findable){
		findDialog.addColumn(text, fieldName, width, findable);
	}
	public void addColumn(String text,String[] fieldNames,int width,boolean findable){
		findDialog.addColumn(text, fieldNames, width, findable);
	}

	public void setFindIndex(int index){
		findDialog.setFindIndex(index);
	}
	public int getFindIndex(){
		return findDialog.getFindIndex();
	}

	public void addFilter(String filter){
		findDialog.addFilter(filter);
	}
	public void addDFilter(int column,Object value){
		findDialog.addDFilter(column, value);
	}
	public void addDFilter(int column,Object arg0,Object arg1){
		findDialog.addDFilter(column, arg0, arg1);
	}
	public void setFilter(int index,String filter){
		findDialog.setFilter(index, filter);
	}
	public void setDFilter(int index,int column,Object value){
		findDialog.setDFilter(index, column, value);
	}
	public void setDFilter(int index,int column,Object arg0,Object arg1){
		findDialog.setDFilter(index, column, arg0, arg1);
	}
	public void removeFilter(int index){
		findDialog.removeFilter(index);
	}
	public void removeFilters(){
		findDialog.removeFilters();
	}
	public void removeDFilter(int index){
		findDialog.removeDFilter(index);
	}
	public void removeDFilters(){
		findDialog.removeDFilters();
	}

	public void hideColumn(int column){
		findDialog.hideColumn(column);
	}
	public void setColumnPattern(int column,String pattern){
		findDialog.setColumnPattern(column, pattern);
	}
	public void setColumnDivider(int column,char divider){
		findDialog.setColumnDivider(column, divider);
	}
	public void setColumnNullValue(int column,String nullValue){
		findDialog.setColumnNullValue(column, nullValue);
	}

	public void setFindListener(Listener listener){
		findDialog.setFindListener(listener);
	}
	/**
	 * Adiciona um texto no campo de buttonBusca
	 * @param value
	 */
	public void setValue(String value){
		findDialog.setValue(value);
	}

	public void addInsertListener(Listener listener){
		findDialog.addInsertListener(listener);
	}

	public void addUpdateListener(Listener listener){
		findDialog.addUpdateListener(listener);
	}

	public void find(){
		findDialog.find();
	}

	/* (non-Javadoc)
	 * @see rdw.interfaces.DbControl#getErrorMessage()
	 */
	@Override
	public String getErrorMessage() {
		String errorMessage = "";
		for (int i = 0; i < fields.length; i++) {
			if (fields[0].isAnnotationPresent(NotNull.class)){
				if (i == 0)
					errorMessage += '\r';
				if (text.isEmpty())
					errorMessage += fields[i].getAnnotation(NotNull.class).errorMessage();
			}
		}
		return errorMessage.trim().length() != 0 ? errorMessage:null;
	}

	/* (non-Javadoc)
	 * @see rdw.interfaces.DbControl#getField()
	 */
	@Override
	public Field getField() {
		if (fields.length == 0)
			return fields[0];
		return null;
	}

	public Field[] getFields(){
		return fields;
	}

	public void open(){
		text.clear();
		if (object == null || dataSource == null || fieldName == null || displayNames == null)
			return;
		Class<?> oClass = object.getClass();
		Field[] cFields = oClass.getDeclaredFields();
		for (int i = 0; i < cFields.length; i++) {
			if (cFields[i].getName().compareTo(fieldName) != 0)
				continue;
			if (cFields[i].isAnnotationPresent(ForeignKey.class)){
				Field key = cFields[i];
				Class<?> ref = key.getAnnotation(ForeignKey.class).reference();
				Field[] keys = RDE.getPrimaryKeys(ref);
				Field fkKey = null;
				if (keys.length == 1){
					fkKey = keys[0];
				}
				Field[] rFields = ref.getDeclaredFields();
				ArrayList<Field> listaFields = new ArrayList<Field>();
				for (Field rField : rFields) {
					for (String fieldName : this.displayNames) {
						if (rField.getName().compareTo(fieldName) == 0){
							listaFields.add(rField);
							break;
						}
					}
				}
				if (listaFields.size() > 0){
					this.key = key;
					this.key.setAccessible(true);
					this.ref = ref;
					this.fkKey = fkKey;
					this.fkKey.setAccessible(true);
					Field[] fields = new Field[listaFields.size()];
					for (int j = 0; j < fields.length; j++) {
						fields[j] = listaFields.get(j);
						fields[j].setAccessible(true);
					}
					this.fields = fields;
					break;
				}
			}
		}
		if (key == null || (fkKey == null))
			return;
		findDialog.setClass(ref);
		findDialog.setDataSource(dataSource);
		setEnabled(true);
		buttonFind.addSelectionListener(new SelectionAdapter(){
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				actFind();
			}
		});
		text.addKeyListener(new KeyAdapter(){
			@Override
			public void keyPressed(KeyEvent arg0) {
				if (!editable)
					return;
				if (arg0.keyCode == SWT.DEL
				|| (arg0.keyCode == SWT.BS
				&& (text.getSelection().x == text.getText().length()+1))){
					newValue = null;
					fObject = null;
					text.clear();
					return;
				}
				if (Character.isLetterOrDigit(arg0.character)){
					findDialog.setValue(String.valueOf(arg0.character));
					actFind();
				}else if ((arg0.keyCode == SWT.CR || arg0.keyCode == SWT.KEYPAD_CR)
				&& (fObject == null)){
					actFind();
				}
			}
		});
		for (Field field : fields) {
			if (field.isAnnotationPresent(PrimaryKey.class)|| field.isAnnotationPresent(NotNull.class))
				nullable = false;
		}
		reset();
	}


	private void actFind(){
		Object fkObject = findDialog.open();
		if (fkObject == null)
			return;
		fObject = fkObject;
		try {
			newValue = fkKey.get(fObject);
			String string = "";
			for (Field field : fields) {
				field.setAccessible(true);
				Object value = field.get(fkObject);
				if (value != null){
					if (string.trim().length() > 0)
						string += divider != ' ' ? " "+divider+" ": ' ';
					string += getValue(field, value);
				}
			}
			text.setSelection(0);
			text.setText(string);
			text.setSelection(text.getText().trim().length());
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
	}

	public void setFObject(Object object){
		this.fObject = object;
		try {
			newValue = fkKey.get(fObject);
			String string = "";
			for (Field field : fields) {
				field.setAccessible(true);
				Object value = field.get(object);
				if (value != null){
					if (string.trim().length() > 0)
						string += divider != ' ' ? " "+divider+" ": ' ';
					string += getValue(field, value);
				}
			}
			text.setSelection(0);
			text.setText(string);
			text.setSelection(text.getText().trim().length());
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
	}

	public void clear(){
		fObject = null;
		try {
			key.set(object, null);
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		text.clear();
	}


	/* (non-Javadoc)
	 * @see rdw.interfaces.DbControl#post()
	 */
	@Override
	public boolean post() {
		try {
			Object id = fObject != null ? fkKey.get(fObject) : null;
			key.set(object, id);
			return true;
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		return false;
	}

	private static String getValue(Field field, Object object){
		if (field.getType().equals(Date.class)){
			return new SimpleDateFormat("dd/MM/yyyy").format((Date)object);
		}
		return object.toString();
	}
	/* (non-Javadoc)
	 * @see rdw.interfaces.DbControl#refresh()
	 */
	@Override
	public void reset() {
		try {
			newValue = key.get(object);
			fObject = dataSource.getObject(ref, new Object[]{newValue});
			if (fObject == null)
				return;
			String string = "";
			for (Field field : fields) {
				Object value = field.get(fObject);
				if (value != null){
					if (string.trim().length() > 0)
						string += divider != ' ' ? " "+divider+" ": ' ';
					string += getValue(field, value);
				}
			}
			text.setText(string);
			text.setSelection(0);
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
	}

	public void hideDialog(){
		findDialog.hide();
	}

	@Override
	public void set(Object object) {
		this.object = object;
		text.clear();
	}

	public void setFieldName(String fieldName){
		this.fieldName = fieldName;
	}

	public String getFieldName(){
		return fieldName;
	}

	public void setDisplayName(String display) {
		displayNames = new String[]{display};
	}

	public void setDisplayNames(String[] fieldNames){
		this.displayNames = fieldNames;
	}

	public void setDisplayNames(String[] displayNames,char divider){
		this.displayNames = displayNames;
		this.divider = divider;
	}
	public void setDivider(char divider){
		this.divider = divider;
	}

	/* (non-Javadoc)
	 * @see cw.interfaces.CControl#validate()
	 */
	@Override
	public boolean validate() {
		for (Field field : fields) {
			if (field.isAnnotationPresent(NotNull.class)){
				if (text.isEmpty())
					return false;
			}
		}
		return true;
	}

	@Override
	public void addListener(int eventType,Listener listener){
		if (text == null)
			return;
		text.addListener(eventType, listener);
	}
	@Override
	public void removeListener(int eventType,Listener listener){
		text.removeListener(eventType, listener);
	}

	public void addButtonListener(int eventType,Listener listener){
		buttonFind.addListener(eventType, listener);
	}
	public void removeButtonListener(int eventType,Listener listener){
		buttonFind.removeListener(eventType, listener);
	}

	//Styles
	@Override
	public void setForeground(Color color){
		text.setForeground(color);
	}
	@Override
	public Color getForeground(){
		return text.getForeground();
	}
	@Override
	public void setBackground(Color color){
		text.setBackground(color);
	}
	@Override
	public Color getBackground(){
		return text.getBackground();
	}
	@Override
	public void setFont(Font font){
		text.setFont(font);
	}
	@Override
	public Font getFont(){
		return text.getFont();
	}

	public Object getFkObject(){
		return fObject;
	}
	public void setButtonImage(Image image){
		buttonFind.setImage(image);
	}

	@Override
	public boolean isNullable() {
		return nullable;
	}

	@Deprecated
	@Override
	public void setNullable(boolean nullable) {
		throw new RDWError("Operação não permitida!");
	}

	public Object getSelectedObject() {
		return fObject;
	}
	public Object getObject(int index){
		return findDialog.getObject(index);
	}
	public int getSelectionIndex(){
		return findDialog.getSelectionIndex();
	}

	public void setTitle(String string) {
		findDialog.setText(string);
	}

	public FindDialog getFindDialog(){
		return findDialog;
	}

	public void setSize(Point size){
		findDialog.setSize(size);
	}

	public boolean isEmpty(){
		return text.isEmpty();
	}

	public void setDialogSize(Point size){
		findDialog.setSize(size);
	}

	public void setCustomQuery(String sql){
		findDialog.setCustomQuery(sql);
	}

}
