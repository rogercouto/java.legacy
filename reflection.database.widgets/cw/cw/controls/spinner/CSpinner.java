package cw.controls.spinner;

import java.text.DecimalFormat;
import java.text.ParseException;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.DragDetectListener;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.HelpListener;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
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
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Listener;

import cw.controls.textfield.CTextField;
import cw.controls.textfield.Formatter;
import cw.controls.textfield.NumberFormatter;
import cw.controls.textfield.Validator;

/**
 * Floating point spinner
 * @author Roger
 *
 */
public class CSpinner extends Composite {

	private static final String DEF_PATTERN = "0";  //  @jve:decl-index=0:
	private static final double DEF_MIN = Double.MAX_VALUE * -1;
	private static final double DEF_MAX = Double.MAX_VALUE;
	private static final double DEF_INC = 1;
	
	private int style;
	private NumberFormatter formatter = null;
	private CTextField text = null;
	private Button buttonUp = null;
	private Button buttonDown = null;
	private DecimalFormat df = null;  //  @jve:decl-index=0:
	private double increment = DEF_INC;
	private double minimun = DEF_MIN;
	private double maximun = DEF_MAX;
	
	public CSpinner(Composite parent, int style) {
		super(parent, SWT.NONE);
		this.style = style;
		formatter = new NumberFormatter(DEF_PATTERN);
		initialize();
	}

	private void initialize() {
		df = new DecimalFormat(DEF_PATTERN);
		GridData gridData2 = new GridData();
		gridData2.grabExcessVerticalSpace = true;
		gridData2.verticalAlignment = GridData.FILL;
		gridData2.widthHint = 16;
		gridData2.heightHint = 10;
		gridData2.horizontalAlignment = GridData.BEGINNING;
		GridData gridData1 = new GridData();
		gridData1.grabExcessVerticalSpace = true;
		gridData1.verticalAlignment = GridData.FILL;
		gridData1.widthHint = 16;
		gridData1.heightHint = 10;
		gridData1.horizontalAlignment = GridData.BEGINNING;
		GridData gridData = new GridData();
		gridData.grabExcessVerticalSpace = true;
		gridData.grabExcessHorizontalSpace = true;
		gridData.verticalSpan = 2;
		gridData.horizontalAlignment = GridData.FILL;
		gridData.verticalAlignment = GridData.FILL;
		gridData.heightHint = -1;
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 2;
		gridLayout.verticalSpacing = 0;
		gridLayout.marginWidth = 0;
		gridLayout.marginHeight = 0;
		gridLayout.horizontalSpacing = 0;
		text = new CTextField(this, style);
		text.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
		text.setFormatter(formatter);
		text.setLayoutData(gridData);
		buttonUp = new Button(this, SWT.ARROW);
		buttonUp.setBackground(new Color(Display.getCurrent(), 255, 255, 255));
		buttonUp.setLayoutData(gridData1);
		buttonDown = new Button(this, SWT.ARROW | SWT.DOWN);
		buttonDown.setBackground(new Color(Display.getCurrent(), 255, 255, 255));
		buttonDown.setLayoutData(gridData2);
		this.setLayout(gridLayout);
		setValue(0);
		super.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
		this.setSize(new Point(94, 21));
		this.setTabList(new Control[]{text});
		addListeners();
	}

	private void addListeners(){
		buttonUp.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
			public void widgetSelected(org.eclipse.swt.events.SelectionEvent e) {
				actUp();
			}
		});
		buttonDown.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
			public void widgetSelected(org.eclipse.swt.events.SelectionEvent e) {
				actDown();
			}
		});
		text.addKeyListener(new KeyAdapter(){
			@Override
			public void keyPressed(KeyEvent arg0) {
				switch (arg0.keyCode) {
				case SWT.ARROW_UP:
					arg0.doit = false;
					actUp();
					break;
				case SWT.ARROW_DOWN:
					arg0.doit = false;
					actDown();
					break;
				default:
					break;
				}
			}
		});
		text.addFocusListener(new FocusAdapter(){
			@Override
			public void focusGained(FocusEvent arg0) {
				text.setSelection(text.getText().length());
			}
			@Override
			public void focusLost(FocusEvent arg0) {
				refresh();
			}
		});
	}
	
	public void setValue(double value){
		text.clear();
		text.setText(df.format(value));
	}
	public double getDouble(){
		try {
			double d = df.parse(text.getText()).doubleValue();
			if (d > maximun)
				return maximun;
			else if (d < minimun)
				return minimun;
			else
				return d;
		} catch (ParseException e) {
		}
		return 0;
	}
	
	public int getInt(){
		try {
			double d = df.parse(text.getText()).doubleValue();
			if (d > maximun)
				return new Double(maximun).intValue();
			else if (d < minimun)
				return new Double(minimun).intValue();
			else
				return new Double(d).intValue();
		} catch (ParseException e) {
		}
		return 0;
	}
	
	private void refresh(){
		try {
			
			Number number = df.parse(text.getText());
			text.clear();
			if (number.doubleValue() > maximun){
				text.setText(df.format(maximun));
			}else if (number.doubleValue() < minimun){
				text.setText(df.format(minimun));
			}else{
				setText(df.format(number));
			}
		} catch (ParseException e) {
		}
		
	}
	private void actUp(){
		if (isEmpty()){
			setValue(1);
			return;
		}
		double d = getDouble() + increment;
		if (d <= maximun)
			setValue(d);
	}
	private void actDown(){
		if (isEmpty()){
			setValue(0);
			return;
		}
		double d = getDouble() - increment;
		if (d >= minimun)
			setValue(d);
	}
	
	// TODO Getters - Setters
	@Override
	public void setEnabled(boolean enabled){
		text.setEnabled(enabled);
		super.setEnabled(enabled);
	}
	
	public void setEditable(boolean editable){
		text.setEditable(editable);
		text.setBackground(getDisplay().getSystemColor(editable? SWT.COLOR_WHITE :SWT.COLOR_WIDGET_BACKGROUND));
		buttonUp.setEnabled(false);
		buttonDown.setEnabled(false);
	}
	public void append(String string){
		text.append(string);
	}
	public void insert(String string){
		text.insert(string);
	}
	public void clearSelection(){
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
		return text.isDoubleClickEnebled();
	}
	public void setDoubleClickEnabled(boolean doubleClick){
		text.setDoubleClickEnabled(doubleClick);
	}
	public char getEchoChar(){
		return text.getEchoChar();
	}
	public void setEchoChar(char echo){
		text.setEchoChar(echo);
	}
	public boolean isEditable(){
		return text.isEditable();
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
		return super.computeSize(wHint, hHint, changed);
	}
	@Override
	public Point computeSize(int wHint, int hHint){
		return super.computeSize(wHint, hHint);
	}
	@Override
	public Rectangle computeTrim(int x, int y, int width, int height){
		return super.computeTrim(x, y, width, height);
	}

	public void setRedraw(boolean redraw){
		text.setRedraw(redraw);
	}
	/**
	 * M�todo que seta o texto do campo
	 * @param string
	 */
	public void setText(String string){
		text.setText(string);
	}
	/**
	 * M�todo que retorna o texto do campo
	 * @return
	 */
	public String getText(){
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
		return text.isEmpty();
	}
	
	/**
	 * Limpa o campo de texto
	 */
	public void clear(){
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
		return text.getFormatter();
	}
	public Validator getValidator() {
		return text.getValidator();
	}
	public boolean isNullable() {
		return text.isNullable();
	}
	
	public void setPattern(String pattern){
		Double d = getDouble();
		text.clear();
		this.formatter = new NumberFormatter(pattern);
		text.setFormatter(formatter);
		df.applyPattern(formatter.getPattern());
		text.setText(df.format(d));
		
	}
	public String getPattern(){
		return formatter.getPattern();
	}
	public double getIncrement() {
		return increment;
	}
	public void setIncrement(double increment) {
		this.increment = increment;
	}
	public double getMinimun() {
		return minimun;
	}
	public void setMinimun(double minimun) {
		this.minimun = minimun;
		refresh();
	}
	public double getMaximun() {
		return maximun;
	}
	public void setMaximun(double maximun) {
		this.maximun = maximun;
		refresh();
	}
	@Override
	public boolean isFocusControl(){
		return (buttonUp.isFocusControl() || buttonDown.isFocusControl() || text.isFocusControl());
	}
	
	//end Getters - Setters
}  //  @jve:decl-index=0:visual-constraint="10,10"
