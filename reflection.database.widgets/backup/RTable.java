package sdw.table;

import java.io.File;
import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

import rde.RDE;
import rde.annotations.ForeignKey;
import rde.annotations.PrimaryKey;
import rde.module.DataModule;

public class RTable extends Composite {

	protected ArrayList<RTableColumn> columns = new ArrayList<RTableColumn>();  //  @jve:decl-index=0:
	protected Table table = null;
	private int style = SWT.NONE;
	
	private Class<?> c = null;
	private Field[] pks = null;
	private String sql = null;
	private DataModule dataModule = null;
	private Object[][] data = null;
	private boolean sortColumns = true;

	public RTable(Composite parent, int style) {
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
		GridData gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;
		gridData.grabExcessVerticalSpace = true;
		gridData.verticalAlignment = GridData.FILL;
		table = new Table(this, style);
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		table.setEnabled(false);
		table.setLayoutData(gridData);
		this.setLayout(gridLayout);
	}
	
	public void addColumn(String text,String fieldName,int width,int alignment){
		RTableColumn column = new RTableColumn(this,SWT.NONE);
		column.setText(text);
		column.setFieldName(fieldName);
		column.setWidth(width);
		column.setAlignment(alignment);
	}
	public void addColumn(String text,String[] fieldNames,int width,int alignment){
		RTableColumn column = new RTableColumn(this,SWT.NONE);
		column.setText(text);
		column.setFieldNames(fieldNames);
		column.setWidth(width);
		column.setAlignment(alignment);
	}
	public void addColumn(String text,String fieldName,int width){
		RTableColumn column = new RTableColumn(this,SWT.NONE);
		column.setText(text);
		column.setFieldName(fieldName);
		column.setWidth(width);
	}
	public void addColumn(String text,String[] fieldNames,int width){
		RTableColumn column = new RTableColumn(this,SWT.NONE);
		column.setText(text);
		column.setFieldNames(fieldNames);
		column.setWidth(width);
	}
	public RTableColumn getColumn(int index){
		return columns.get(index);
	}
	public void setColumnDivisor(int column,char divisor){
		columns.get(column).divisor = divisor;
	}
	public void setColumnPattern(int column,String pattern){
		columns.get(column).pattern = pattern;
	}
	public void setColumnNullValue(int column,String nullValue){
		columns.get(column).nullValue = nullValue;
	}
	public void setClass(Class<?> c) {
		this.c = c;
	}
	public void setDataModule(DataModule dataModule) {
		this.dataModule = dataModule;
	}
	private void setKeys(){
		ArrayList<Field> keys = new ArrayList<Field>();
		ArrayList<String> fieldNames = new ArrayList<String>();
		for (RTableColumn column : columns) {
			if (column.fieldNames.length == 1)
				fieldNames.add(column.fieldNames[0]);
		}
		for (Field field : c.getDeclaredFields()) {
			for (int i = 0; i < fieldNames.size(); i++) {
				if (fieldNames.get(i).compareTo(field.getName())==0
				&&(field.isAnnotationPresent(PrimaryKey.class)))
					keys.add(field);
			}
		}
		pks = new Field[keys.size()];
		for (int i = 0; i < pks.length; i++) {
			pks[i] = keys.get(i);
		}
		table.addListener(SWT.Selection, new Listener(){
			@Override
			public void handleEvent(Event arg0) {
				int index = table.getSelectionIndex();
				Object object = getObject(index);
				RDE.printFields(object);
			}
		});
	}

	public void addObject(Object object){
		
	}
	public void setObject(int index,Object object){
		
		
	}
	public Object getObject(int index){
		if (index < 0)
			return null;
		if (pks.length == 0)
			throw new UnsupportedOperationException("No primary key assigned");
		Object[] values = new Object[pks.length];
		A:for (int i = 0; i < pks.length; i++) {
			for (int j = 0; j < columns.size(); j++) {
				if (columns.get(j).fieldNames[0].compareTo(pks[i].getName())==0){
					values[i] = data[index][j];
					continue A;
				}
			}
		}
		return dataModule.getObject(c, values);
	}
	
	public int getSelectionIndex(){
		return table.getSelectionIndex();
	}
	public int[] getSelectionIndices(){
		return table.getSelectionIndices();
	}
	
	//TODO Código pra busca e preenchimento dos dados
	
	/**
	 * Método que gera uma consulta sql dinâmica com os campos necessários
	 */
	private void setQuery(){
		sql = "SELECT";
		ArrayList<String> fieldNames = new ArrayList<String>();
		for (RTableColumn column : columns) {
			String[] names = column.fieldNames;
			for (String string : names) {
				fieldNames.add(string);
			}
		}
		ArrayList<Class<?>> classes = new ArrayList<Class<?>>();
		classes.add(c);
		for (Field field : c.getDeclaredFields()) {
			if (field.isAnnotationPresent(ForeignKey.class))
				classes.add(field.getAnnotation(ForeignKey.class).reference());
		}
		ArrayList<Class<?>> uClasses = new ArrayList<Class<?>>();
		int count = 0;
		A:for (String string : fieldNames) {
			for (int i = 0; i < classes.size(); i++) {
				Field[] f = classes.get(i).getDeclaredFields();
				for (int j = 0; j < f.length; j++) {
					if (string.compareTo(f[j].getName())==0){
						boolean in = false;
						for (Class<?> class1 : uClasses) {
							if (class1.equals(classes.get(i)))
								in = true;
						}
						if (!in)
							uClasses.add(classes.get(i));
						if (count > 0)
							sql += ",";
						sql += " c"+i+"."+string;
						count++;
						continue A;
					}
				}
			}
		}
		sql += " FROM ";
		for (int i = 0; i < uClasses.size(); i++) {
			if (i == 0)
				sql += uClasses.get(i).getSimpleName().toLowerCase()+" c"+i;
			else{
				sql += " LEFT OUTER JOIN ";
				sql += uClasses.get(i).getSimpleName().toLowerCase()+" c"+i;
				sql += " ON ";
				ArrayList<Field> keys = new ArrayList<Field>();
				for (Field field : uClasses.get(i).getDeclaredFields()) {
					if (field.isAnnotationPresent(PrimaryKey.class)){
						keys.add(field);
					}
				}
				for (Field key : keys) {
					if (!key.equals(keys.get(0)))
						sql += " AND ";
					sql += "c0."+key.getName()+" = c"+i+"."+key.getName();
				}
			}
		}
		for (int i = 0; i < columns.size(); i++) {
			RTableColumn column = columns.get(i);
			if (column.fieldNames.length == 1){
				for (Class<?> class1 : uClasses) {
					Field[] field = class1.getDeclaredFields();
					for (int j = 0; j < field.length; j++) {
						if (column.fieldNames[0].compareTo(field[j].getName())==0){
							column.type = field[j].getType();
						}
					}
				}
			}else{
				column.type = String.class;
			}
		}
	}
	
	/**
	 * Seta os dados da Matriz data p/ preenchimento da tabela
	 */
	private void setData(){
		try {
			Connection connection = dataModule.openConnection();
			Statement stmt = connection.createStatement();
			ResultSet res = stmt.executeQuery(sql);
			ArrayList<Object> list = new ArrayList<Object>();
			while (res.next()) {
				Object[] line = new Object[columns.size()];
				for (int i = 0; i < columns.size(); i++) {
					RTableColumn column = columns.get(i);
					if (column.fieldNames.length == 1){
						if (column.type.equals(Date.class)){
							if (dataModule.getDatePattern() != null){
								line[i] = new SimpleDateFormat(dataModule.getDatePattern()).parse(res.getString(column.fieldNames[0]));
							}else{
								java.sql.Date sqlDate = res.getDate(column.fieldNames[0]);
								long l = sqlDate.getTime();
								line[i] = new Date(l);
							}
						}else if(column.type.equals(Boolean.class) && (dataModule.getBooleanPattern() != null)){
							String[] tf = dataModule.getBooleanPattern().split("[;]");
							if (tf.length == 2){
								String bool = res.getString(column.fieldNames[0]);
								if (bool.compareTo(tf[0])==0)
									line[i] = true;
								else if (bool.compareTo(tf[1])==0)
									line[i] = false;
							}
						}else
							line[i] = res.getObject(column.fieldNames[0]);
					}else{
						String tmp = "";
						for (int j = 0; j < column.fieldNames.length; j++) {
							if (j > 0)
								tmp += column.getSeparator();
							tmp += res.getObject(column.fieldNames[j]);
						}
						line[i] = tmp;
					}
				}
				list.add(line);
			}
			if (list.size() == 0){
				data = new Object[][]{};
				return;
			}
			data = new Object[list.size()][columns.size()+2];
			for (int i = 0; i < data.length; i++) {
				Object[] line = (Object[])list.get(i);
				for (int j = 0; j < columns.size(); j++) {
					data[i][j] = line[j];
				}
				data[i][columns.size()] = false;
				data[i][columns.size()+1] = false;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Coloca os itens na tabela
	 * TIPOS ACEITOS: Integer,Long,Float,Double,String,Date,Boolean,Character
	 */
	private void setItems(){
		table.removeAll();
		for (int i = 0; i < data.length; i++) {
			TableItem item = new TableItem(table,SWT.NONE);
			setItem(item);
		}	
	}
	private void resetItems(){
		ArrayList<Integer> sel = new ArrayList<Integer>();
		for (int i = 0; i < data.length; i++) {
			TableItem item = table.getItem(i);
			setItem(item);
			if ((Boolean)data[i][columns.size()])
				sel.add(i);
			item.setChecked((Boolean)data[i][columns.size()+1]);
		}	
		int[] indices = new int[sel.size()];
		for (int i = 0; i < indices.length; i++) {
			indices[i] = sel.get(i).intValue();
		}
		table.setSelection(indices);
	}
	
	//TODO Métodos para reordenar os itens da tabela
	//listeners
	private Listener dataListener;
	private Listener columnListener;
		
	private void setDataListner(){
		if (dataListener != null)
			table.removeListener(SWT.SetData,dataListener);
		if (columnListener != null){
			for (TableColumn column : table.getColumns()) {
				column.removeListener(SWT.Selection,columnListener);
			}
		}
		if (!sortColumns)
			return;
		dataListener = new Listener(){
			@Override
			public void handleEvent(Event e) {
				TableItem item = (TableItem) e.item;
				setItem(item);
			}
		};
		columnListener = new Listener(){
			@Override
			public void handleEvent(Event e) {
				if (data == null || data.length == 0)
					return;
				table.setVisible(false);
				TableColumn sortColumn = table.getSortColumn();
				TableColumn currentColumn = (TableColumn) e.widget;
				int dir = table.getSortDirection();
				if (sortColumn == currentColumn) {
					dir = dir == SWT.UP ? SWT.DOWN : SWT.UP;
				} else {
					table.setSortColumn(currentColumn);
					dir = SWT.UP;
				}
				final int index = table.indexOf(currentColumn);
				final int mult = dir == SWT.UP ? 1 : -1;
				int[] sel = table.getSelectionIndices();
				for (int i = 0; i < data.length; i++) {
					data[i][columns.size()] = in(i,sel);
					data[i][columns.size()+1] = table.getItem(i).getChecked(); 
				}
				Arrays.sort(data, new Comparator<Object>() {
					public int compare(Object arg0, Object arg1) {
						Object[] row1 = (Object[])arg0;
						Object[] row2 = (Object[])arg1;
						Object obj1 = row1[index];
						Object obj2 = row2[index];
						return compareObject(obj1, obj2) * mult;
					}
				});
				table.setSortDirection(dir);
				resetItems();
				table.setVisible(true);
				table.setFocus();
			}
		};
		table.addListener(SWT.SetData, dataListener);
		for (TableColumn column : table.getColumns()) {
			column.addListener(SWT.Selection,columnListener);
		}
	}
	
	//TODO Ferramentas
	/**
	 * Adiciona os dados em um item da tabela
	 */
	private void setItem(TableItem item){
		int index = table.indexOf(item);
		for (int j = 0; j < columns.size(); j++) {
			RTableColumn column = columns.get(j);
			Object object = data[index][j];
			if (object == null){
				item.setText(j,column.nullValue);
				continue;
			}
			if (column.type.equals(Integer.TYPE)
			||(column.type.equals(Integer.class))
			||(column.type.equals(Long.TYPE))
			||(column.type.equals(Long.class)))
			{
				if (column.pattern != null){
					item.setText(j, new DecimalFormat(column.pattern).format(object));
				}else{
					item.setText(j, object.toString());
				}
			}else  if(column.type.equals(Integer.class)
			||(column.type.equals(Float.TYPE))
			||(column.type.equals(Float.class))
			||(column.type.equals(Double.TYPE))
			||(column.type.equals(Double.class)))
			{
				if (column.pattern != null){
					item.setText(j, new DecimalFormat(column.pattern).format(object));
				}else{
					item.setText(j, new DecimalFormat(RTableColumn.FLOAT_PATTERN).format(object));
				}
			}else if((column.type.equals(Boolean.TYPE)) || (column.type.equals(Boolean.class))){
				String[] boolValues = column.pattern!= null?
						column.pattern.split("[;]") :
						RTableColumn.BOOL_PATTERN.split("[;]");
				if (boolValues.length != 2)
					throw new UnsupportedOperationException("Invalid pattern");
				Boolean bool = (Boolean)object;		
				item.setText(j,bool.booleanValue() ? boolValues[0] : boolValues[1]);		
			}else if (column.type == Date.class){
				if (column.pattern != null){
					item.setText(j, new SimpleDateFormat(column.pattern).format(object));
				}else{
					item.setText(j, new SimpleDateFormat(RTableColumn.DATE_PATTERN).format(object));
				}
			}else if (column.type == String.class){
				if (column.pattern != null)
					item.setText(j,maskString((String)object, column.pattern));
				else
					item.setText(j, object.toString());
			}else
				item.setText(j, object.toString());
		}
	}
	/**
	 * Metodoo que compara 2 objetos
	 */
	protected int compareObject(Object obj1,Object obj2){
		if ((obj1 == null) && (obj2 == null))return 0;
		if ((obj1 != null)&&(obj2 == null))return -1;
		if ((obj1 == null)&&(obj2 != null))return 1;
		if ((obj1 instanceof Integer)&&(obj2 instanceof Integer)){
			Integer o1 = (Integer)obj1;
			Integer o2 = (Integer)obj2;
			return o1.compareTo(o2);
		}else if ((obj1 instanceof Long)&&(obj2 instanceof Long)){
			Long o1 = (Long)obj1;
			Long o2 = (Long)obj2;
			return o1.compareTo(o2);
		}else if ((obj1 instanceof Float)&&(obj2 instanceof Float)){
			Float o1 = (Float)obj1;
			Float o2 = (Float)obj2;
			return o1.compareTo(o2);
		}else if ((obj1 instanceof Double)&&(obj2 instanceof Double)){
			Double o1 = (Double)obj1;
			Double o2 = (Double)obj2;
			return o1.compareTo(o2);
		}else if ((obj1 instanceof Character)&&(obj2 instanceof Character)){
			Character o1 = (Character)obj1;
			Character o2 = (Character)obj2;
			return o1.compareTo(o2);
		}else if ((obj1 instanceof String)&&(obj2 instanceof String)){
			String o1 = (String)obj1;
			String o2 = (String)obj2;
			return o1.compareTo(o2);
		}else if ((obj1 instanceof Date)&&(obj2 instanceof Date)){
			Date o1 = (Date)obj1;
			Date o2 = (Date)obj2;
			return o1.compareTo(o2);
		}else if ((obj1 instanceof Boolean)&&(obj2 instanceof Boolean)){
			Boolean o1 = (Boolean)obj1;
			Boolean o2 = (Boolean)obj2;
			return o1.compareTo(o2);
		}else if ((obj1 instanceof File)&&(obj2 instanceof File)){
			File o1 = (File)obj1;
			File o2 = (File)obj2;
			return o1.getAbsolutePath().compareTo(o2.getAbsolutePath());
		}
		return 0;
	}
	/**
	 * Retorna um  texto formatado com mascara ou o mesmo texto caso o tamanho seja diferente do padrão
	 * @param string Texto a ser formatado
	 * @param pattern Padrão da Mascara de entrada ((##)####-####)
	 * @return texto com maskara
	 */
	private String maskString(String string,String pattern){
		String[] masks = pattern.split(";");
		for (String mask : masks) {
			int varLength = 0;
			char[] ma = mask.toCharArray();
			char[] va = {'#','?','L','U','A'};
			B:for (char c : ma) {
				for (char v : va) {
					if (c == v){
						varLength++;
						continue B;
					}
				}
			}
			if (string.length() == varLength){
				int index = 0;
				char[] ta = string.toCharArray();
				for (int j = 0; j < ma.length; j++) {
					for (char v : va) {
						if (ma[j] == v){
							ma[j] = ta[index];
							index++;
							break;
						}
					}
				}
				return String.valueOf(ma);
			}
		}
		return string;
	}
	
	private boolean in(int i,int[] indices){
		for (int index : indices) {
			if (i == index)
				return true;
		}
		return false;
	}
	
	public void printData(){
		for (int i = 0; i < data.length; i++) {
			for (int j = 0; j < data[i].length; j++) {
				System.out.print(" "+data[i][j]);
			}
			System.out.println();
		}
		System.out.println();
	}
	/**
	 * Realiza a consulta e preenche a tabela
	 */
	public void open(){
		if (c == null)
			return;
		setKeys();
		setQuery();
		setData();
		setItems();
		setDataListner();
		table.setEnabled(true);
	}
		
}
