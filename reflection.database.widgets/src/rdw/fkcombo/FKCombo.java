package rdw.fkcombo;

import java.lang.reflect.Field;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

import rde.RDE;
import rde.annotations.ForeignKey;
import rde.annotations.NotNull;
import rde.annotations.PrimaryKey;
import rde.datasource.DataSource;
import rdw.exceptions.RDWError;
import rdw.interfaces.DbControl;

public class FKCombo extends Composite implements DbControl {

	private Combo combo = null;
	private int style;
	
	private DataSource dataSource = null;
	private Class<?> ref = null;
	
	private Field key = null;
	private Object newValue = null;
	private Object object = null;
	private Object fObject = null;
	
	private String[] fieldNames = null;
	private Field[] fields = null;
	private char divider = ' ';
	private String[] filter = null;
	private Field fkKey = null;
	private List<Object> list = null;
	
	private boolean nullable = true;
	private Listener insertListener = null;
	private Listener deselectListener = null;
	
	public FKCombo(Composite parent,int style) {
		super(parent, SWT.READ_ONLY);
		this.style = style;
		initialize();
	}

	private void initialize() {
		GridLayout gridLayout = new GridLayout();
		gridLayout.horizontalSpacing = 0;
		gridLayout.marginWidth = 0;
		gridLayout.marginHeight = 0;
		gridLayout.verticalSpacing = 0;
		createCombo();
		this.setLayout(gridLayout);
	}

	/**
	 * This method initializes combo	
	 *
	 */
	private void createCombo() {
		GridData gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		gridData.grabExcessVerticalSpace = true;
		gridData.grabExcessHorizontalSpace = true;
		gridData.verticalAlignment = GridData.FILL;
		combo = new Combo(this, style);
		combo.setLayoutData(gridData);
		combo.addListener(SWT.KeyDown, new Listener(){
			@Override
			public void handleEvent(Event arg0) {
				if (arg0.keyCode == SWT.DEL)
					deselect();
			}
		});
		combo.setEnabled(false);
	}

	public void setDataSource(DataSource dataSource){
		this.dataSource = dataSource;
	}
	
	@Override
	public String getErrorMessage() {
		String errorMessage = "";
		for (int i = 0; i < fields.length; i++) {
			if (fields[0].isAnnotationPresent(NotNull.class)){
				if (i == 0)
					errorMessage += '\r';
				if (combo.getSelectionIndex() < 0)
					errorMessage += fields[i].getAnnotation(NotNull.class).errorMessage();
			}
		}
		return errorMessage.trim().length() != 0 ? errorMessage:null;
	}

	@Override
	public Field getField() {
		if (fields.length == 0)
			return fields[0];
		return null;
	}
	
	public Field[] getFields(){
		return fields;
	}

	@Override
	public void open() {
		if (object == null || dataSource == null)
			return;
		this.fields = new Field[fieldNames.length];
		A:for (int i = 0; i < fields.length; i++) {
			Field[] cFields = object.getClass().getDeclaredFields();
			for (int j = 0; j < cFields.length; j++) {
				if (cFields[j].isAnnotationPresent(ForeignKey.class)){
					Class<?> ref = cFields[j].getAnnotation(ForeignKey.class).reference();
					Field[] rFields = ref.getDeclaredFields();
					for (int k = 0; k < rFields.length; k++) {
						if (rFields[k].getName().compareTo(fieldNames[i])==0){
							this.fields[i] = rFields[k];
							this.fields[i].setAccessible(true);
							if ((cFields[j].getName().compareTo(rFields[i].getName())==0)
							&&	rFields[i].isAnnotationPresent(PrimaryKey.class)){
								this.ref = ref;
								key = cFields[j];
								key.setAccessible(true);
								fkKey = rFields[i];
								fkKey.setAccessible(true);
							}
							continue A;
						}
					}
				}
			}
			return;
		}
		try {
			list = dataSource.getList(ref,filter);
			for (Object object : list) {
				if (object == null)
					continue;
				String string = "";
				for (Field field : fields) {
					if (field == null)
						return;
					if (string.trim().length() > 0)
						string += divider != ' ' ? " "+divider+" ": ' ';
					string += field.get(object).toString();
				}
				combo.add(string);
			}
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		combo.setEnabled(true);
		fkKey.setAccessible(true);
		key.setAccessible(true);
		combo.setEnabled(true);
		for (Field field : fields) {
			if (field.isAnnotationPresent(PrimaryKey.class)|| field.isAnnotationPresent(NotNull.class))
				nullable = false;
		}
		reset();
	}

	@Override
	public boolean post() {
		int index = combo.getSelectionIndex();
		try {
			String text = null;
			if (index < 0 && combo.getText().trim().length() != 0){
				text = combo.getText();
				for (int i = 0; i < combo.getItemCount(); i++) {
					if (combo.getItem(i).compareTo(text) == 0){
						combo.select(i);
						index = i;
						text = null;
						break;
					}
				}
			}
			if (index < 0 && text != null){
				if (insertListener != null){
					Event event = new Event();
					event.text = text;
					insertListener.handleEvent(event);
					Object obj = event.data;
					if (obj != null)
						newValue = fkKey.get(obj);
					else
						throw new RDWError();
				}
			}else if (index >= 0){
				Object obj = list.get(index);
				newValue = fkKey.get(obj);
			}
			key.set(object, newValue);
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
			newValue = key.get(object);
			fObject = dataSource.getObject(ref, new Object[]{newValue});
			if (fObject == null)
				return;
			for (int i = 0; i < list.size(); i++) {
				if (fkKey.get(fObject).equals(fkKey.get(list.get(i)))){
					combo.select(i);
					break;
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
		fieldNames = new String[]{fieldName};
	}

	public void setFieldNames(String[] fieldNames){
		this.fieldNames = fieldNames;
	}
	public void setFieldNames(String[] fieldNames,char divider){
		this.fieldNames = fieldNames;
		this.divider = divider;
	}
	public void setDivider(char divider){
		this.divider = divider;
	}

	@Override
	public boolean validate() {
		if (!nullable && (combo.getSelectionIndex() < 0))
			return false;
		return true;
	}
	
	//TODO Getters - Setters
	@Override
	public void setEnabled(boolean enabled){
		//setBackground(getDisplay().getSystemColor(enabled? SWT.COLOR_WHITE : SWT.COLOR_WIDGET_BACKGROUND));
		combo.setEnabled(enabled);
		super.setEnabled(enabled);
	}
	
	public void deselect(){
		newValue = null;
		combo.deselectAll();
		if (deselectListener != null){
			deselectListener.handleEvent(new Event());
		}
		
	}
	public int getItemCount(){
		return list.size();
	}
	public int getItemHeight() {
		return combo.getItemHeight();
	}
	public int getSelectionIndex(){
		return combo.getSelectionIndex();
	}
	public int getVisibleItemCount(){
		return combo.getVisibleItemCount();
	}
	public void select(int index){
		combo.select(index);
		try {
			newValue = key.get(list.get(index));
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
	}
	public void setVisibleItemCount(int count){
		combo.setVisibleItemCount(count);
	}
	//TODO Text Getters - Setters
	
	@Override
	public boolean isEnabled(){
		return combo.getEnabled();
	}
	public int getOrientation(){
		return combo.getOrientation(); 
	}
	public void setOrientation(int orientation){
		combo.setOrientation(orientation);
	}
	@Override
	public Point computeSize(int wHint, int hHint, boolean changed){
		return combo.computeSize(wHint, hHint, changed);
	}
	@Override
	public Point computeSize(int wHint, int hHint){
		return combo.computeSize(wHint, hHint);
	}
	@Override
	public Rectangle computeTrim(int x, int y, int width, int height){
		return combo.computeTrim(x, y, width, height);
	}
	public void setRedraw(boolean redraw){
		combo.setRedraw(redraw);
	}
	/**
	 * M�todo que retorna o texto do campo
	 * @return
	 */
	public String getText(){
		return combo.getText();
	}
	public void setTextLimit(int limit){
		combo.setTextLimit(limit);
	}
	public int getTextLimit(){
		return combo.getTextLimit();
	}
	@Override
	public void setToolTipText(String string){
		super.setToolTipText(string);
		combo.setToolTipText(string);
	}
	@Override
	public String getToolTipText(){
		return combo.getToolTipText();
	}
	@Override
	public void addListener(int eventType,Listener listener){
		if (combo == null)
			return;
		combo.addListener(eventType, listener);
	}
	@Override
	public void removeListener(int eventType,Listener listener){
		combo.removeListener(eventType, listener);
	}
		
	//Styles
	@Override
	public void setForeground(Color color){
		combo.setForeground(color);
	}
	@Override
	public Color getForeground(){
		return combo.getForeground();
	}
	@Override
	public void setBackground(Color color){
		combo.setBackground(color);
	}
	@Override
	public Color getBackground(){
		return combo.getBackground();
	}
	@Override
	public void setFont(Font font){
		combo.setFont(font);
	}
	@Override
	public Font getFont(){
		return combo.getFont();
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

	public Object getSelectedObject() {
		int index = combo.getSelectionIndex();
		if (index < 0)
			return null;
		return list.get(index);
	}
	
	public void refreshList(){
		if (object == null || dataSource == null)
			return;
		try {
			combo.removeAll();
			list = dataSource.getList(ref,filter);
			for (Object object : list) {
				if (object == null)
					continue;
				String string = "";
				for (Field field : fields) {
					if (field == null)
						return;
					if (string.trim().length() > 0)
						string += divider != ' ' ? " "+divider+" ": ' ';
					string += field.get(object).toString();
				}
				combo.add(string);
			}
		} catch (IllegalArgumentException e) {
			rde.util.Logger.log(e);
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			rde.util.Logger.log(e);
			e.printStackTrace();
		}
	}
	
	public void select(Object object){
		try {
			int index = -1;
			for (Object item : list) {
				if (!object.getClass().equals(item.getClass()))
					continue;
				Field[] pks = RDE.getPrimaryKeys(item.getClass());
				boolean equals = true;
				for (Field field : pks) {
					field.setAccessible(true);
					if (!field.get(object).equals(field.get(item)))
						equals = false;
				}
				if (equals)
					index = list.indexOf(item);
			}
			if (index >= 0)
				combo.select(index);
		} catch (IllegalArgumentException e) {
			rde.util.Logger.log(e);
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			rde.util.Logger.log(e);
			e.printStackTrace();
		}
	}

	public void setFilter(String[] filter) {
		this.filter = filter;
		refreshList();
	}
	public void setFilter(String filter) {
		this.filter = new String[]{filter};
		refreshList();
	}
	public String[] getFilter() {
		return filter;
	}
	public Object getFkObject(){
		int index = combo.getSelectionIndex();
		if (index < 0)
			return null;
		return list.get(index);
	}

	public void setInsertListener(Listener insertListener) {
		if (style == SWT.READ_ONLY)
			throw new RDWError("Combo is read only");
		this.insertListener = insertListener;
	}

	public Listener getInsertListener() {
		if (style == SWT.READ_ONLY)
			throw new RDWError("Combo is read only");
		return insertListener;
	}
	
	public void setDeselectListener(Listener deselectListener){
		this.deselectListener = deselectListener;
	}
	
	public Listener getDeselectListener(){
		return deselectListener;
	}

}
