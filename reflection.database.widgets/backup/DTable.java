package rdw.table;

import java.io.File;
import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;

import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTError;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

import rde.RDE;
import rde.annotations.PrimaryKey;
import rde.database.Database;
import rde.datasource.DataSource;
import rdw.exceptions.SDWError;

public class DTable extends Composite {

	protected ArrayList<DTableColumn> columns = new ArrayList<DTableColumn>();  //  @jve:decl-index=0:
	protected Table table = null;
	private int style = SWT.NONE;
	
	private Class<?> c = null;
	private DataSource dataSource = null;
	private String sql = null;
	private ArrayList<String> filter = new ArrayList<String>();
	protected ArrayList<DTableFilter> dtf = new ArrayList<DTableFilter>();
	
	private Object[][] data = null;
	private boolean sortColumns = true;
	private boolean open = false;

	public DTable(Composite parent, int style) {
		super(parent, SWT.NONE);
		this.style = style;
		initialize();
	}

	private void initialize() {
		table = new Table(this, style);
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		table.setEnabled(false);
		this.setLayout(new FillLayout());
	}
	
	public void addColumn(String text,String fieldName,int width,int alignment){
		DTableColumn column = new DTableColumn(this,SWT.NONE);
		column.setText(text);
		column.setFieldName(fieldName);
		column.setWidth(width);
		column.setAlignment(alignment);
	}
	public void addColumn(String text,String[] fieldNames,int width,int alignment){
		DTableColumn column = new DTableColumn(this,SWT.NONE);
		column.setText(text);
		column.setFieldNames(fieldNames);
		column.setWidth(width);
		column.setAlignment(alignment);
	}
	public void addColumn(String text,String fieldName,int width){
		DTableColumn column = new DTableColumn(this,SWT.NONE);
		column.setText(text);
		column.setFieldName(fieldName);
		column.setWidth(width);
	}
	public void addColumn(String text,String[] fieldNames,int width){
		DTableColumn column = new DTableColumn(this,SWT.NONE);
		column.setText(text);
		column.setFieldNames(fieldNames);
		column.setWidth(width);
	}
	public DTableColumn getColumn(int index){
		return columns.get(index);
	}
	public void setColumnDivider(int column,char divider){
		columns.get(column).divider = divider;
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
	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}
	public DataSource getDataSource() {
		return dataSource;
	}
	public boolean isOpen() {
		return open;
	}

	public void addFilter(String filter){
		this.filter.add(filter);
		if (open)
			refresh();
	}
	public void setFilter(int index,String filter){
		this.filter.set(index,filter);
		if (open)
			refresh();
	}
	public void removeFilter(int index){
		this.filter.remove(index);
		if (open)
			refresh();
	}
	public void removeFilters(){
		filter = new ArrayList<String>();
		if (open)
			refresh();
	}
	public DTableFilter getDTableFilter(int index){
		if (index <0 || index >= dtf.size())
			return null;
		return dtf.get(index);
	}
	public void addDFilter(int column,Object value){
		new DTableFilter(this,column,value);
		if (open)
			refresh();
	}
	public void addDFilter(int column,Object arg0,Object arg1){
		new DTableFilter(this,column,arg0,arg1);
		if (open)
			refresh();
	}
	public void setDFilter(int index,int column,Object value){
		DTableFilter filter = dtf.get(index);
		filter.setColumn(column);
		filter.setArg0(value);
		filter.setArg1(null);
		if (open)
			refresh();
	}
	public void setDFilter(int index,int column,Object arg0,Object arg1){
		DTableFilter filter = dtf.get(index);
		filter.setColumn(column);
		filter.setArg0(arg0);
		filter.setArg1(arg1);
		if (open)
			refresh();
	}
	public void removeDFilter(int index){
		dtf.remove(index);
		if (open)
			refresh();
	}
	public void removeDFilters(){
		dtf = new ArrayList<DTableFilter>();
		if (open)
			refresh();
	}
	
	private String getFilter(int column,Object arg0,Object arg1){
		DTableColumn tableColumn = columns.get(column);
		String filter =  arg1 == null ? 
			tableColumn.fieldNames[0] +" = " : 
			"("+tableColumn.fieldNames[0] +" >= ";
		boolean numeric = false;
		if ((tableColumn.type.equals(Integer.TYPE))
		||(tableColumn.type.equals(Integer.class))
		||(tableColumn.type.equals(Long.TYPE))
		||(tableColumn.type.equals(Long.class))){
			numeric = true;
		}
		if (!numeric)
			filter += "'";
		Database database = dataSource.getDatabase();
		if (tableColumn.type.equals(Date.class)){
			String datePattern = database.getDatePattern()!= null? database.getDatePattern():"yyyy-MM-dd";
			SimpleDateFormat sdf = new SimpleDateFormat(datePattern);
			filter += sdf.format(arg0);
			if (arg1 != null){
				if (!numeric)
					filter += "'";
				filter += " AND "+tableColumn.fieldNames[0] +" <= ";
				if (!numeric)
					filter += "'";
				filter += sdf.format(arg1);
			}
		}else if (tableColumn.type.equals(Boolean.TYPE)||(tableColumn.type.equals(Boolean.class))){
			String[] booleanPattern = database.getBooleanPattern() != null? database.getBooleanPattern().split("[;]"):new String[]{"1","0"};
			filter += (Boolean)arg0 ? booleanPattern[0] : booleanPattern[1];
			if (arg1 != null){
				if (!numeric)
					filter += "'";
				filter += " AND "+tableColumn.fieldNames[0] +" <= ";
				if (!numeric)
					filter += "'";
				filter += (Boolean)arg1 ? booleanPattern[0] : booleanPattern[1];
			}
		}else{
			filter += arg0.toString();
			if (arg1 != null){
				if (!numeric)
					filter += "'";
				filter += " AND "+tableColumn.fieldNames[0] +" <= ";
				if (!numeric)
					filter += "'";
				filter += arg1.toString();
			}
		}
		if (arg1 != null)
			filter += numeric? ")" : "')";
		else if (!numeric)
			filter += "'";
		return filter;
	}
	public int getFilterCount(){
		return filter.size();
	}
	
	public void hideColumn(int column){
		if (column >= columns.size())
			throw new SWTError("Index out of bounds");
		TableColumn tableColumn = table.getColumn(column);
		tableColumn.setWidth(0);
		tableColumn.setResizable(false);
	}
	
	public void showColumn(int column){
		if (column >= columns.size())
			throw new SWTError("Index out of bounds");
		TableColumn tableColumn = table.getColumn(column);
		tableColumn.setWidth(columns.get(column).width);
		tableColumn.setResizable(true);
	}
	
	public void setLinesVisible(boolean show){
		table.setLinesVisible(show);
	}
	
	public void setHeaderVisible(boolean show){
		table.setHeaderVisible(show);
	}
	/**
	 * Adiciona um  objeto na lista, o mesmo ja deve estar dentro do DB
	 * @param object
	 */
	public void addObject(Object object){
		Object[] line = getObjectData(object);
		if (line != null){
			Object[][] newData = new Object[data.length+1][line.length];
			for (int i = 0; i < data.length; i++) {
				for (int j = 0; j < data[i].length; j++) {
					newData[i][j] = data[i][j];
				}
			}
			for (int j = 0; j < line.length; j++) {
				newData[data.length][j] = line[j];
			};
			data = newData;
			TableItem item = new TableItem(table,SWT.NONE);
			setItem(item);
			if (sortColumns)
				sortData();
		}
	}
	
	/**
	 * Altera um objeto da lista, o mesmo ja deve ter sido alterado no DB
	 * @param index
	 * @param object
	 */
	public void setObject(int index,Object object){
		Object[] line = getObjectData(object);
		if (line != null){
			for (int j = 0; j < data[index].length; j++) 
				data[index][j] = line[j];
			TableItem item = table.getItem(index);
			setItem(item);
			if (sortColumns)
				sortData();
		}
	}
	
	/**
	 * Remove um objeto da lista, a remoção do mesmo do db é opcional
	 * @param index
	 */
	public void removeObject(int index){
		Object[][] newData = new Object[data.length-1][data[index].length];
		A:for (int i = 0; i < data.length; i++) {
			for (int j = 0; j < data[i].length; j++) {
				if (i == index)
					continue A;
				newData[i<index ? i : i-1][j] = data[i][j];
			}
		}
		data = newData;
		table.remove(index);
	}
	
	/**
	 * Busca no banco de dados os campos do objeto para ser adicionados na tabela
	 * @param object
	 * @return
	 */
	private Object[] getObjectData(Object object){
		try {
			Object[] values = new Object[pks.length];
			for (int i = 0; i < values.length; i++) {
				values[i] = pks[i].get(object);
			}
			String sql = this.sql + " WHERE ";
			for (int i = 0; i < pks.length; i++) {
				if (i > 0)
					sql += " AND ";
				if (values.getClass().equals(Integer.TYPE)
						||(values.getClass().equals(Integer.class))
						||(values.getClass().equals(Long.TYPE))
						||(values.getClass().equals(Long.class))){
					sql += pks[i].getName()+" = "+values[i]+"";
				}
				sql += pks[i].getName()+" = '"+values[i]+"'";
			}
			boolean unconnect = false;
			if (!dataSource.isConnected()){
				dataSource.connect();
				unconnect = true;
			}
			ResultSet res = dataSource.executeQuery(sql);
			Object[] line = null;
			if (res.next()){
				line = getLine(res);
				line[line.length-1] = values;
			}
			res.close();
			if (unconnect)
				dataSource.unconnect();
			return line;
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * Retrona um objeto do banco de dados cfme o índice na tabela
	 * @param index
	 * @return Objecto do banco de dados
	 */
	public Object getObject(int index){
		if (index < 0)
			return null;
		Object[] values = (Object[])data[index][columns.size()+2];
		return dataSource.getObject(c, values);
	}

	/**
	 * Retorna o índice do primeiro item selecionado
	 * @return indíce resultante
	 */
	public int getSelectionIndex(){
		return table.getSelectionIndex();
	}
	/**
	 * Retorna um vetor com os índices selecionados
	 * @return vetor de inteiros
	 */
	public int[] getSelectionIndices(){
		return table.getSelectionIndices();
	}
	
	public Object getValueAt(int line,int column){
		if (line >= data.length || (line >= data[line].length))
			return null;
		return data[line][column];
	}
	//TODO Código pra busca e preenchimento dos dados
	
	/**
	 * Método que gera uma consulta sql dinâmica com os campos necessários
	 */
	private void setQuery(){
		sql = "SELECT";
		ArrayList<String> fieldNames = new ArrayList<String>();
		for (DTableColumn column : columns) {
			String[] names = column.fieldNames;
			for (String string : names) {
				boolean in = false;
				for (String currentName : fieldNames) {
					if (string.compareTo(currentName) == 0){
						in = true;
					}
				}
				if (!in)
					fieldNames.add(string);
			}
		}
		ArrayList<Class<?>> classes = RDE.getReferences(c);
		ArrayList<Class<?>> uClasses = new ArrayList<Class<?>>();
		int count = 0;
		A:for (String string : fieldNames) {
			boolean haveField = false;
			for (int i = 0; i < classes.size(); i++) {
				Class<?> c1 = classes.get(i);
				Field[] f = c1.getDeclaredFields();
				for (int j = 0; j < f.length; j++) {
					if (string.compareTo(f[j].getName())==0){
						haveField = true;
						boolean in = false;
						for (Class<?> class1 : uClasses) {
							if (class1.equals(c1))
								in = true;
						}
						if (!in)
							uClasses.add(c1);
						if (count > 0)
							sql += ",";
						sql += " c"+uClasses.indexOf(c1)+"."+string;
						count++;
						continue A;
					}
				}
			}
			
			if (!haveField)
				throw new SDWError("Field "+string+" not exists");
		}
		pks = getPrimaryKeys();
		for (int i = 0; i < pks.length; i++) {
			pks[i].setAccessible(true);
			boolean in = false;
			for (int j = 0; j < fieldNames.size(); j++) {
				if (fieldNames.get(j).compareTo(pks[i].getName())==0)
					in = true;
			}
			if (!in){
				if (fieldNames.size() > 0)
					sql += ", ";
				sql += "c0."+pks[i].getName();
			}
		}
		sql += " FROM ";
		for (int i = 0; i < uClasses.size(); i++) {
			Class<?> uClass = uClasses.get(i);
			if (i == 0)
				sql += uClass.getSimpleName().toLowerCase()+" c"+i;
			else{
				//sql += (i > 1)? " , " : " LEFT OUTER JOIN ";
				sql += " LEFT OUTER JOIN ";
				sql += uClass.getSimpleName().toLowerCase()+" c"+i;
				sql += " ON ";
				ArrayList<Field> keys = new ArrayList<Field>();
				for (Field field : uClass.getDeclaredFields()) {
					if (field.isAnnotationPresent(PrimaryKey.class)){
						keys.add(field);
					}
				}
				A:for (Field key : keys) {
					if (!key.equals(keys.get(0)))
						sql += " AND ";
					sql += "c"+i+"."+key.getName()+" = ";
					
					B:for (int j = 0; j < uClasses.size(); j++) {
						if (uClass.equals(uClasses.get(j)))
							continue B;
						for (Field field : uClasses.get(j).getDeclaredFields()) {
							if (field.getName().compareTo(key.getName())==0){
								sql += "c"+j+"."+key.getName();
								continue A;
							}
						}
					}
				}
			}
		}
		for (int i = 0; i < columns.size(); i++) {
			DTableColumn column = columns.get(i);
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
		System.out.println(sql);
	}
	
	/**
	 * Seta os dados da Matriz data p/ preenchimento da tabela
	 */
	private void setData(){
		try {
			boolean unconnect = false;
			if (!dataSource.isConnected()){
				dataSource.connect();
				unconnect = true;
			}
			String fullSql = sql;
			if (filter.size() > 0){
				fullSql += " WHERE ";
				for (int i = 0; i < filter.size(); i++) {
					if (i > 0)
						fullSql += " AND ";
					fullSql += filter.get(i);
					
				}
			}
			if (dtf.size() > 0){
				if (filter.size() ==0)
					fullSql += " WHERE ";
				else
					fullSql += " AND ";
				for (int i = 0; i < dtf.size(); i++) {
					if (i > 0)
						fullSql += " AND ";
					DTableFilter filter = dtf.get(i);
					fullSql += getFilter(filter.getColumn(), filter.getArg0(), filter.getArg1());
				}
			}
			ResultSet res = dataSource.executeQuery(fullSql);
			ArrayList<Object> list = new ArrayList<Object>();
			Object[] values = null;
			while (res.next()) {
				Object[] line = getLine(res);
				values = new Object[pks.length];
				for (int j = 0; j < values.length; j++) {
					values[j] = res.getObject(pks[j].getName());
				}
				line[line.length-1] = values;
				list.add(line);
			}
			res.close();
			if (unconnect)
				dataSource.unconnect();
			if (list.size() == 0){
				data = new Object[][]{};
				return;
			}
			data = new Object[list.size()][columns.size()+3];
			for (int i = 0; i < data.length; i++) {
				Object[] line = (Object[])list.get(i);
				for (int j = 0; j < line.length; j++) {
					data[i][j] = line[j];
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} 
	}
	
	/**
	 * Retorna uma linha do DB
	 * @param res - ResultSet resultado da consulta
	 * @return Object[] - linha resultante
	 */
	public Object[] getLine(ResultSet res){
		try {
			Object[] line = new Object[columns.size()+3];
			Database database = dataSource.getDatabase();
			for (int i = 0; i < columns.size(); i++) {
				DTableColumn column = columns.get(i);
				if (column.fieldNames.length == 1){
					if (column.type.equals(Date.class)){
						if (database.getDatePattern() != null){
							line[i] = new SimpleDateFormat(database.getDatePattern()).parse(res.getString(column.fieldNames[0]));
						}else{
							java.sql.Date sqlDate = res.getDate(column.fieldNames[0]);
							long l = sqlDate.getTime();
							line[i] = new Date(l);
						}
					}else if((column.type.equals(Boolean.class)||(column.type.equals(Boolean.TYPE))) 
					&& (database.getBooleanPattern() != null)){
						String[] tf = database.getBooleanPattern().split("[;]");
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
							tmp += column.getDivider() != ' '?" "+column.getDivider()+" ":column.getDivider();
						tmp += res.getObject(column.fieldNames[j]);
					}
					line[i] = tmp;
				}
			}
			line[columns.size()] = false;
			line[columns.size()+1] = false;
			line[columns.size()+2] = null;
			return line;
		} catch (ParseException e) {
			e.printStackTrace();
		}catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
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
	/**
	 * Recoloca os itens na tabela depois da ordenação
	 */
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
	
	public void refresh(){
		setData();
		setItems();
	}
	//TODO Métodos para reordenar os itens da tabela
	//listeners
	private Listener mouseListener;
	private Listener dataListener;
	private Listener columnListener;
	private Field[] pks;
	
	public void deselectColumns(boolean deselect){
		if (mouseListener != null){
			table.removeListener(SWT.MouseDown, mouseListener);
			mouseListener = null;
		}
		if (deselect){
			mouseListener = new Listener(){
				@Override
				public void handleEvent(Event arg0) {
					Point p = new Point(arg0.x,arg0.y);
					if (table.getItem(p) == null)
						table.deselectAll();
				}
			};
			table.addListener(SWT.MouseDown, mouseListener);
		}
	}
		
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
				TableColumn currentColumn = (TableColumn) e.widget;
				TableColumn sortColumn = table.getSortColumn();
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
		if (sortColumns){
			table.setSortColumn(table.getColumn(0));
			table.setSortDirection(SWT.UP);
			sortData();
		}
	}
	
	private void sortData(){
		table.setVisible(false);
		TableColumn sortColumn = table.getSortColumn();
		int dir = table.getSortDirection();
		final int index = table.indexOf(sortColumn);
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
	
	public void sortColumns(boolean sort){
		sortColumns = sort;
		setDataListner();
	}
	
	//TODO Ferramentas
	/**
	 * Adiciona os dados em um item da tabela
	 */
	private void setItem(TableItem item){
		int index = table.indexOf(item);
		Database database = dataSource.getDatabase();
		for (int j = 0; j < columns.size(); j++) {
			DTableColumn column = columns.get(j);
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
					item.setText(j, new DecimalFormat(DTableColumn.FLOAT_PATTERN).format(object));
				}
			}else if((column.type.equals(Boolean.TYPE)) || (column.type.equals(Boolean.class))){
				String[] boolValues = column.pattern!= null?
						column.pattern.split("[;]") :
						DTableColumn.BOOL_PATTERN.split("[;]");
				if (boolValues.length != 2)
					throw new SWTError("Invalid pattern");
				if (database.getBooleanPattern() != null){
					String[] pattern = database.getBooleanPattern().split("[;]");
					if (object.toString().compareTo(pattern[0])==0)
						item.setText(j,boolValues[0]);
					else
						item.setText(j,boolValues[1]);
				}else{
					Boolean bool = (Boolean)object;		
					item.setText(j,bool.booleanValue() ? boolValues[0] : boolValues[1]);		
				}
			}else if (column.type == Date.class){
				if (column.pattern != null){
					item.setText(j, new SimpleDateFormat(column.pattern).format(object));
				}else{
					item.setText(j, new SimpleDateFormat(DTableColumn.DATE_PATTERN).format(object));
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
	 * Retorna um vetor com as chaves primárias
	 * @return
	 */
	private Field[] getPrimaryKeys(){
		ArrayList<Field> list = new ArrayList<Field>();
		Field[] fields = c.getDeclaredFields();
		for (Field field : fields) {
			if (field.isAnnotationPresent(PrimaryKey.class))
				list.add(field);
		}
		Field[] keys = new Field[list.size()];
		for (int i = 0; i < keys.length; i++) {
			keys[i] = list.get(i);
		}
		return keys;
	}
	/**
	 * Realiza a consulta e preenche a tabela
	 */
	public void open(){
		if (c == null)
			return;
		setQuery();
		setData();
		setItems();
		setDataListner();
		table.setEnabled(true);
		open = true;
	}
	
	//TODO Listeners
	@Override
	public void addListener(int eventType,Listener listener){
		table.addListener(eventType, listener);
	}
	@Override
	public void removeListener(int eventType,Listener listener){
		table.removeListener(eventType, listener);
	}
	
	//TODO Getters - Setters
	public int getItemCount(){
		return table.getItemCount();
	}
	public void setSelection(int index){
		table.setSelection(index);
	}
	public void setSelection(int[] indices){
		table.setSelection(indices);
	}
}
