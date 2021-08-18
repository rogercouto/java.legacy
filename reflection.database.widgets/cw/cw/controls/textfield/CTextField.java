package cw.controls.textfield;



import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.DragDetectListener;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.HelpListener;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.MenuDetectListener;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.events.MouseTrackListener;
import org.eclipse.swt.events.MouseWheelListener;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.events.TraverseListener;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;

import cw.interfaces.CControl;

/**
 * Campo de texto com suporte a validador e formatador
 * @author Roger
 *
 */
public class CTextField extends Composite implements CControl{

	private Text text;
	private int style;
	private boolean nullable = true;
	
	private Formatter formatter = null;  //  @jve:decl-index=0:
	private Validator validator = null;  //  @jve:decl-index=0:
	
	private NumberFormat nf = new DecimalFormat("0.00");
	private DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
	
	public CTextField(Composite parent, int style) {
		super(parent, SWT.NONE);
		this.style = style;
		initialize();
	}

	private void initialize() {
		GridData gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;
		gridData.grabExcessVerticalSpace = true;
		gridData.verticalAlignment = GridData.FILL;
		GridLayout gridLayout = new GridLayout();
		gridLayout.horizontalSpacing = 0;
		gridLayout.marginWidth = 0;
		gridLayout.marginHeight = 0;
		gridLayout.verticalSpacing = 0;
		this.setLayout(gridLayout);
		text = new Text(this, style);
		text.setLayoutData(gridData);
	}
	
	/**
	 * M�todo que escolhe se o campo pode ter valor nulo
	 * @param nullable
	 */
	public void setNullable(boolean nullable){
		this.nullable = nullable;
	}
	/**
	 * M�todo que seta o formatador do texto
	 * @param formatter
	 */
	public void setFormatter(Formatter formatter){
		if (this.formatter != null)
			this.formatter.remove();
		if (formatter != null){
			this.formatter = formatter;
			this.formatter.set(text);
		}else{
			this.formatter = formatter;
			return;
		}
		if (formatter.getClass().equals(NumberFormatter.class)){
			nf = new DecimalFormat(formatter.getPattern());
		}
		if (this.formatter.getValidator() != null)
			this.validator = formatter.getValidator();
	}
	
	/**
	 * M�todo que seta o validador do texto
	 * @param validator
	 */
	public void setValidator(Validator validator){
		this.validator = validator;
	}
	
	/**
	 * M�todo que valida o texto 
	 * @return
	 */
	public boolean validate(){
		if (isEmpty()){
			if(!nullable)
				return false;
			else
				return true;
		}
		if (validator == null)
			return true;
		return validator.verifyText(getText());
	}
	
	// TODO Getters - Setters
	@Override
	public void setEnabled(boolean enabled){
		text.setEnabled(enabled);
		super.setEnabled(enabled);
	}
	
	public void setEditable(boolean editable){
		text.setEditable(editable);
	}
	public void append(String string){
		if (formatter != null && (formatter instanceof NumberFormatter))
			text.setSelection(text.getText().length());
		text.append(string);
	}
	public void insert(String string){
		text.insert(string);
	}
	public void clearSelection(){
		if (formatter != null){
			Point sel = text.getSelection();
			formatter.clear(sel);
		}else
			text.clearSelection();
	}
	public void copy(){
		text.copy();
	}
	public void cut(){
		text.cut();
	}
	public void paste(){
		text.paste();
	}
	public int getCharCount(){
		return text.getCharCount();
	}
	public boolean isDoubleClickEnebled(){
		return text.getDoubleClickEnabled();
	}
	public void setDoubleClickEnabled(boolean doubleClick){
		if (formatter != null){
			System.err.println("Cannot change doubleclick");
			return;
		}
		text.setDoubleClickEnabled(doubleClick);
	}
	public char getEchoChar(){
		return text.getEchoChar();
	}
	public void setEchoChar(char echo){
		text.setEchoChar(echo);
	}
	public boolean isEditable(){
		return text.getEditable();
	}
	@Override
	public boolean isEnabled(){
		return text.getEnabled();
	}
	public int getLineCount(){
		return text.getLineCount();
	}
	public int getLineHeight(){
		return text.getLineHeight();
	}
	public String getLineDelimiter(){
		return text.getLineDelimiter();
	}
	public String getMessage(){
		return text.getMessage();
	}
	public void setMessage(String message){
		text.setMessage(message);
	}
	public int getOrientation(){
		return text.getOrientation(); 
	}
	public void setOrientation(int orientation){
		text.setOrientation(orientation);
	}
	public String getSelectionText(){
		return text.getSelectionText();
	}
	public int getTabs(){
		return text.getTabs();
	}
	public void setTabs(int tabs){
		text.setTabs(tabs);
	}
	public Point getCaretLocation(){
		return text.getCaretLocation();
	}
	public int getCaretPosition(){
		return text.getCaretPosition();
	}
	public int getCaretLineNumber(){
		return text.getCaretLineNumber();
	}
	@Override
	public Point computeSize(int wHint, int hHint, boolean changed){
		return text.computeSize(wHint, hHint, changed);
	}
	@Override
	public Point computeSize(int wHint, int hHint){
		return text.computeSize(wHint, hHint);
	}
	@Override
	public Rectangle computeTrim(int x, int y, int width, int height){
		return text.computeTrim(x, y, width, height);
	}
	public void setRedraw(boolean redraw){
		text.setRedraw(redraw);
	}
	/**
	 * M�todo que seta o texto do campo
	 * @param string
	 */
	public void setText(String string){
		if (string == null)
			return;
		if (formatter != null){
			if (string.trim().length() == 0){
				formatter.clear();
				return;
			}
			formatter.setText(string);
		}else{
			text.setText(string);
		}
		text.setSelection(text.getText().length());
	}
	/**
	 * M�todo que retorna o texto do campo
	 * @return
	 */
	public String getText(){
		if (formatter != null){
			return formatter.getText();
		}
		return text.getText();
	}
	
	public String getFullText(){
		return text.getText();
	}
	
	public void setTextLimit(int limit){
		text.setTextLimit(limit);
	}
	public int getTextLimit(){
		return text.getTextLimit();
	}
	@Override
	public void setToolTipText(String string){
		super.setToolTipText(string);
		text.setToolTipText(string);
	}
	@Override
	public String getToolTipText(){
		return text.getToolTipText();
	}
	
	public void setSelection(int start){
		text.setSelection(start);
	}
	public void setSelection(int start,int end){
		text.setSelection(start,end);
	}
	public void setSelection(Point selection){
		text.setSelection(selection);
	}
	public Point getSelection(){
		return text.getSelection();
	}
	
	//TODO Listeners
	@Override
	public void addListener(int eventType,Listener listener){
		text.addListener(eventType, listener);
	}
	@Override
	public void addControlListener(ControlListener listener){
		text.addControlListener(listener);
	}
	@Override
	public void addDisposeListener(DisposeListener listener){
		if (text == null)
			return;
		text.addDisposeListener(listener);
	}
	@Override
	public void addDragDetectListener(DragDetectListener listener){
		text.addDragDetectListener(listener);
	}
	@Override
	public void addFocusListener(FocusListener listener){
		text.addFocusListener(listener);
	}
	@Override
	public void addHelpListener(HelpListener listener){
		text.addHelpListener(listener);
	}
	@Override
	public void addKeyListener(KeyListener listener){
		text.addKeyListener(listener);
	}
	@Override
	public void addMenuDetectListener(MenuDetectListener listener){
		text.addMenuDetectListener(listener);
	}
	public void addModifyListener(ModifyListener listener){
		text.addModifyListener(listener);
	}
	@Override
	public void addMouseListener(MouseListener listener){
		text.addMouseListener(listener);
	}
	@Override
	public void addMouseMoveListener(MouseMoveListener listener){
		text.addMouseMoveListener(listener);
	}
	@Override
	public void addMouseTrackListener(MouseTrackListener listener){
		text.addMouseTrackListener(listener);
	}
	@Override
	public void addMouseWheelListener(MouseWheelListener listener){
		text.addMouseWheelListener(listener);
	}
	@Override
	public void addPaintListener(PaintListener listener){
		text.addPaintListener(listener);
	}
	public void addSelectionListener(SelectionListener listener){
		text.addSelectionListener(listener);
	}
	@Override
	public void addTraverseListener(TraverseListener listener){
		text.addTraverseListener(listener);
	}
	public void addVerifyListener(VerifyListener listener){
		text.addVerifyListener(listener);
	}
	
	@Override
	public void removeListener(int eventType,Listener listener){
		text.removeListener(eventType, listener);
	}
	@Override
	public void removeControlListener(ControlListener listener){
		text.removeControlListener(listener);
	}
	@Override
	public void removeDisposeListener(DisposeListener listener){
		text.removeDisposeListener(listener);
	}
	@Override
	public void removeDragDetectListener(DragDetectListener listener){
		text.removeDragDetectListener(listener);
	}
	@Override
	public void removeFocusListener(FocusListener listener){
		text.removeFocusListener(listener);
	}
	@Override
	public void removeHelpListener(HelpListener listener){
		text.removeHelpListener(listener);
	}
	@Override
	public void removeKeyListener(KeyListener listener){
		text.removeKeyListener(listener);
	}
	@Override
	public void removeMenuDetectListener(MenuDetectListener listener){
		text.removeMenuDetectListener(listener);
	}
	public void removeModifyListener(ModifyListener listener){
		text.removeModifyListener(listener);
	}
	@Override
	public void removeMouseListener(MouseListener listener){
		text.removeMouseListener(listener);
	}
	@Override
	public void removeMouseMoveListener(MouseMoveListener listener){
		text.removeMouseMoveListener(listener);
	}
	@Override
	public void removeMouseTrackListener(MouseTrackListener listener){
		text.removeMouseTrackListener(listener);
	}
	@Override
	public void removeMouseWheelListener(MouseWheelListener listener){
		text.removeMouseWheelListener(listener);
	}
	@Override
	public void removePaintListener(PaintListener listener){
		text.removePaintListener(listener);
	}
	public void removeSelectionListener(SelectionListener listener){
		text.removeSelectionListener(listener);
	}
	@Override
	public void removeTraverseListener(TraverseListener listener){
		text.removeTraverseListener(listener);
	}
	public void removeVerifyListener(VerifyListener listener){
		text.removeVerifyListener(listener);
	}
	/**
	 * Verifica se o texto est� vazio
	 * @return
	 */
	public boolean isEmpty(){
		if (formatter != null)
			return formatter.isEmpty();
		return (getText().trim().length() == 0);
	}
	
	/**
	 * Limpa o campo de texto
	 */
	public void clear(){
		if (formatter != null)
			formatter.clear();
		else
			text.setText(Formatter.EMPTY_STR);
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
	public Formatter getFormatter() {
		return formatter;
	}
	public Validator getValidator() {
		return validator;
	}
	public boolean isNullable() {
		return nullable;
	}
	@Override
	public boolean isFocusControl(){
		return text.isFocusControl();
	}
	
	public int getInt(){
		try {
			return nf.parse(text.getText()).intValue();
		} catch (ParseException e) {
		}
		return 0;
	}
	public void setInt(int i){
		text.setText("");
		text.setText(nf.format(i));
		text.setSelection(text.getText().length());
	}
	public long getLong(){
		try {
			return nf.parse(text.getText()).longValue();
		} catch (ParseException e) {
		}
		return 0;
	}
	public void setLong(long l){
		text.setText("");
		text.setText(nf.format(l));
		text.setSelection(text.getText().length());
	}
	public float getFloat(){
		try {
			return nf.parse(text.getText()).floatValue();
		} catch (ParseException e) {
		}
		return 0;
	}
	public void setFloat(float f){
		text.setText("");
		text.setText(nf.format(f));
		text.setSelection(text.getText().length());
	}
	public double getDouble(){
		try {
			return nf.parse(text.getText()).doubleValue();
		} catch (ParseException e) {
		}
		return 0;
	}
	public void setDouble(double d){
		text.setText("");
		text.setText(nf.format(d));
		text.setSelection(text.getText().length());
	}
	public void setDate(Date date){
		text.setText("");
		text.setText(df.format(date));
		text.setSelection(text.getText().length());
	}
	public Date getDate(){
		try {
			return df.parse(text.getText());
		} catch (ParseException e) {
		}
		return null;
	}
	//end Getters - Setters
}
