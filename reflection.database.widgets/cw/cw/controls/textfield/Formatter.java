package cw.controls.textfield;

import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Text;

public interface Formatter {
	
	public static final String EMPTY_STR = "";
	
	public void set(Text text);
	
	public void setText(String string);
	
	public String getText();
	public String unmaskString();
	public Number getNumber();
	
	public Validator getValidator();
	public String getPattern();
	
	public void clear();
	public void clear(Point sel);
	
	public boolean isEmpty();
	
	public void remove();
	
}
