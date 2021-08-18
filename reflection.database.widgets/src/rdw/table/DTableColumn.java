package rdw.table;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.widgets.TableColumn;


public class DTableColumn {

	protected static final String DATE_PATTERN = "dd/MM/yyyy";
	protected static final String FLOAT_PATTERN = "#,##0.00";
	protected static final String BOOL_PATTERN = "Sim;Não";
	
	protected int style;
	protected DTable parent;
	protected TableColumn column;
	protected String[] fieldNames;
	protected String pattern = null;
	protected Character divider = ' ';
	protected String nullValue = "";
	protected Type type;
	protected int width;
	protected Class<?> c;//vai idicar de qual classe ele vai puxar
	
	protected List<DTableMask> maskOptions = new ArrayList<DTableMask>(); 
	
	public DTableColumn(DTable parent,int style) {
		super();
		column = new TableColumn(parent.table,style);
		parent.columns.add(this);
	}
	public void setText(String string){
		column.setText(string);
	}
	public void setAlignment(int alignment){
		column.setAlignment(alignment);
	}
	public void setWidth(int width){
		this.width = width;
		column.setWidth(width);
	}
	public void setFieldName(String fieldName){
		this.fieldNames = new String[]{fieldName};
	}
	public void setFieldNames(String[] fieldNames){
		this.fieldNames = fieldNames;
	}
	public void setDivider(Character divider) {
		this.divider = divider;
	}
	public void setPattern(String pattern){
		this.pattern = pattern;
	}
	public void setNullValue(String nullValue){
		this.nullValue = nullValue;
	}
	public String[] getFieldNames() {
		return fieldNames;
	}
	public String getFieldName(int index){
		return fieldNames[index];
	}
	public String getPattern() {
		return pattern;
	}
	public Character getDivider() {
		return divider;
	}
	public String getNullValue() {
		return nullValue;
	}
	public Type getType() {
		return type;
	}
	public int getWidth() {
		return width;
	}
	public Class<?> getC() {
		return c;
	}
	public void setC(Class<?> c) {
		this.c = c;
	}
	
	public String getValue(int key){
		for (DTableMask mask : maskOptions) {
			if (mask.getKey() == key)
				return mask.getValue();
		}
		return null;
	}
}
