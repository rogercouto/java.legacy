package cw.controls.calendartext;

import java.text.DecimalFormat;
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
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.DateTime;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;

import cw.controls.textfield.CTextField;
import cw.controls.textfield.DateFormatter;
import cw.controls.textfield.Validator;
import cw.interfaces.CControl;

public class CalendarText extends Composite implements CControl {

	private static final String DEF_PATTERN = "dd/MM/yyyy";
	private static final char BLANK_CHAR = '_';
	protected CTextField text = null;
	private int style;
	protected Button buttonCalendar = null;
	private Shell calendar = null;  //  @jve:decl-index=0:visual-constraint="11,49"
	private Composite compCal = null;
	private DateTime dtCalendar = null;
	protected String pattern = null;  //  @jve:decl-index=0:
	
	private Composite composite = null;
	private Button buttonOk = null;
	private Button buttonCancel = null;
	
	public CalendarText(Composite parent, int style, String pattern) {
		super(parent, SWT.NONE);
		this.style = style;
		checkPattern(pattern);
		this.pattern = pattern;
		initialize();
	}
	public CalendarText(Composite parent, int style) {
		super(parent, SWT.NONE);
		this.style = style;
		pattern = DEF_PATTERN;
		checkPattern(pattern);
		initialize();
	}
	
	public String getPattern() {
		return pattern;
	}

	private void checkPattern(String pattern){
		char[] pc = pattern.toCharArray();
		for (char c : pc) {
			if ((c == 'H') || (c == 'm') || (c == 's'))
				throw new UnsupportedOperationException("Time pattern is not supported!");
		}
	}
	
	private void showCalendar(){
		checkDate();
		Rectangle parentRect = this.getDisplay().map (getParent (), null, getBounds ());
		Point loc = new Point(parentRect.x,parentRect.y);
		Point textSize = text.getSize();
		Point inc = new Point(0,0);
		inc.x = this.getBorderWidth() != 0 ? this.getBorderWidth() + 2 : 0;
		inc.y = this.getBorderWidth() != 0 ? this.getBorderWidth() + 1 : 0;
		loc.x += textSize.x - calendar.getSize().x;
		loc.x += buttonCalendar.getSize().x + inc.x;
		loc.y += textSize.y + inc.y; 
		calendar.setLocation(loc);
		calendar.open();
	}
	
	private void checkDate(){
		if (text.getFormatter() instanceof DateFormatter) {
			DateFormatter df = (DateFormatter) text.getFormatter();
			Date date = df.getDate();
			if (date != null)
				setCalDate(date);
		}
	}
	
	public void setCalDate(Date date){
		SimpleDateFormat sdf = new SimpleDateFormat();
		sdf.applyPattern("dd");
		dtCalendar.setDay(new Integer(sdf.format(date)).intValue());
		sdf.applyPattern("MM");
		dtCalendar.setMonth(new Integer(sdf.format(date)).intValue()-1);
		sdf.applyPattern("yyyy");
		dtCalendar.setYear(new Integer(sdf.format(date)).intValue());
	}
	
	public void setTextDate(){
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		DecimalFormat df = new DecimalFormat("00");
		String strDate = df.format(dtCalendar.getDay())+"/";
		strDate += df.format(dtCalendar.getMonth()+1)+"/";
		df.applyPattern("0000");
		strDate += df.format(dtCalendar.getYear());
		try {
			Date date = sdf.parse(strDate);
			sdf.applyPattern(pattern);
			text.clear();
			text.setText(sdf.format(date));
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}
	
	private void actSelectDate(){
		text.setSelection(0);
		setTextDate();
		calendar.setVisible(false);
		buttonCalendar.setSelection(false);
		text.setFocus();
	}
	private void actCancel(){
		calendar.setVisible(false);
		buttonCalendar.setSelection(false);
		text.setFocus();
	}
	
	private void addListeners(){
		text.addListener(SWT.FocusIn, new Listener(){
			@Override
			public void handleEvent(Event e) {
				if (!text.isEmpty())
					text.setSelection(text.getFormatter().getText().length());
			}
		});
		buttonCalendar.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
			@Override
			public void widgetSelected(org.eclipse.swt.events.SelectionEvent e) {
				showCalendar();
			}
		});
		calendar.addShellListener(new org.eclipse.swt.events.ShellAdapter() {
			@Override
			public void shellDeactivated(org.eclipse.swt.events.ShellEvent e) {
				calendar.setVisible(false);
				buttonCalendar.setSelection(false);
				text.setFocus();
			}
		});
		calendar.addTraverseListener(new org.eclipse.swt.events.TraverseListener() {
			@Override
			public void keyTraversed(org.eclipse.swt.events.TraverseEvent e) {
				switch (e.keyCode) {
				case SWT.CR:
					actSelectDate();
					break;
				case SWT.KEYPAD_CR:
					actSelectDate();
					break;
				case SWT.ESC:
					actCancel();
					break;
				default:
					break;
				}
			}
		});
		buttonOk.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
			@Override
			public void widgetSelected(org.eclipse.swt.events.SelectionEvent e) {
				actSelectDate();
			}
		});
		buttonCancel.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
			@Override
			public void widgetSelected(org.eclipse.swt.events.SelectionEvent e) {
				actCancel();
			}
		});
	}
	/**
	 * This method initializes calendar	
	 *
	 */
	private void createCalendar() {
		GridLayout gridLayout1 = new GridLayout();
		gridLayout1.horizontalSpacing = 0;
		gridLayout1.marginWidth = 0;
		gridLayout1.marginHeight = 0;
		gridLayout1.verticalSpacing = 0;
		calendar = new Shell(this.getShell(),SWT.NO_TRIM | SWT.ON_TOP);
		createCompCal();
		calendar.setVisible(false);
		calendar.setLayout(gridLayout1);
		calendar.pack();
	}

	/**
	 * This method initializes compCal	
	 *
	 */
	private void createCompCal() {
		GridData gridData3 = new GridData();
		gridData3.horizontalSpan = 2;
		GridLayout gridLayout2 = new GridLayout();
		gridLayout2.horizontalSpacing = 0;
		gridLayout2.marginWidth = 0;
		gridLayout2.marginHeight = 0;
		gridLayout2.numColumns = 2;
		gridLayout2.makeColumnsEqualWidth = true;
		gridLayout2.verticalSpacing = 0;
		GridData gridData2 = new GridData();
		gridData2.horizontalAlignment = GridData.FILL;
		gridData2.grabExcessHorizontalSpace = true;
		gridData2.grabExcessVerticalSpace = true;
		gridData2.verticalAlignment = GridData.FILL;
		compCal = new Composite(calendar, SWT.BORDER);
		compCal.setBackground(new Color(Display.getCurrent(), 240, 240, 240));
		compCal.setLayoutData(gridData2);
		compCal.setLayout(gridLayout2);
		dtCalendar = new DateTime(compCal,SWT.CALENDAR);
		dtCalendar.setLayoutData(gridData3);
		createComposite();
	}

	/**
	 * This method initializes composite	
	 *
	 */
	private void createComposite() {
		GridData gridData4 = new GridData();
		gridData4.horizontalAlignment = GridData.FILL;
		gridData4.grabExcessHorizontalSpace = true;
		gridData4.verticalAlignment = GridData.CENTER;
		GridLayout gridLayout3 = new GridLayout();
		gridLayout3.numColumns = 2;
		gridLayout3.horizontalSpacing = 0;
		gridLayout3.verticalSpacing = 0;
		gridLayout3.marginWidth = 0;
		gridLayout3.marginHeight = 0;
		gridLayout3.makeColumnsEqualWidth = true;
		GridData gridData6 = new GridData();
		gridData6.horizontalSpan = 2;
		gridData6.verticalAlignment = GridData.CENTER;
		gridData6.grabExcessHorizontalSpace = false;
		gridData6.horizontalAlignment = GridData.CENTER;
		composite = new Composite(compCal, SWT.NONE);
		composite.setLayoutData(gridData6);
		composite.setLayout(gridLayout3);
		buttonOk = new Button(composite, SWT.NONE);
		buttonOk.setBackground(new Color(Display.getCurrent(), 240, 240, 240));
		buttonOk.setText("Ok");
		buttonOk.setLayoutData(gridData4);
		buttonOk.setImage(new Image(Display.getCurrent(), getClass().getResourceAsStream("/rdw/icon/icon_accept.png")));
		buttonCancel = new Button(composite, SWT.NONE);
		buttonCancel.setBackground(new Color(Display.getCurrent(), 240, 240, 240));
		buttonCancel.setText("Cancel");
		buttonCancel.setImage(new Image(Display.getCurrent(), getClass().getResourceAsStream("/rdw/icon/icon_cancel.png")));
	}

	private void initialize() {
		GridData gridData1 = new GridData();
		gridData1.grabExcessVerticalSpace = true;
		gridData1.verticalAlignment = GridData.FILL;
		gridData1.heightHint = 20;
		gridData1.horizontalAlignment = GridData.BEGINNING;
		GridData gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;
		gridData.grabExcessVerticalSpace = true;
		gridData.verticalAlignment = GridData.FILL;
		GridLayout gridLayout = new GridLayout();
		gridLayout.horizontalSpacing = 0;
		gridLayout.marginWidth = 0;
		gridLayout.marginHeight = 0;
		gridLayout.numColumns = 2;
		gridLayout.verticalSpacing = 0;
		text = new CTextField(this, style);
		text.setFormatter(new DateFormatter(pattern,BLANK_CHAR));
		text.setLayoutData(gridData);
		buttonCalendar = new Button(this, SWT.TOGGLE);
		buttonCalendar.setImage(new Image(Display.getCurrent(), getClass().getResourceAsStream("/rdw/icon/icon_calendar.png")));
		buttonCalendar.setLayoutData(gridData1);
		this.setLayout(gridLayout);
		this.setTabList(new Control[]{text});
		createCalendar();
		addListeners();
	}

	//TODO Getters - Setters
	public Date getDate(){
		if (!validate())
			return null;
		SimpleDateFormat sdf = new SimpleDateFormat(pattern);
		try {
			return sdf.parse(text.getText());
		} catch (ParseException e) {
		}
		return null;
	}
	public void setDate(Date date){
		if (date == null)
			return;
		SimpleDateFormat sdf = new SimpleDateFormat(pattern);
		//text.setText("");
		text.setText(sdf.format(date));
	}
	
	@Deprecated
	public void setReadOnly(boolean readOnly){
		Color color = text.getBackground();
		setEditable(!readOnly);
		setBackground(color);
		if (readOnly && (text.getFormatter().isEmpty())){
			SimpleDateFormat sdf = new SimpleDateFormat(pattern);
			text.setText(sdf.format(new Date()));
			text.setSelection(text.getText().length());	
		}
	}
	
	@Override
	public void setEnabled(boolean enabled){
		text.setEnabled(enabled);
		buttonCalendar.setEnabled(enabled);
		super.setEnabled(enabled);
	}
	public void setEditable(boolean editable){
		text.setEditable(editable);
		buttonCalendar.setEnabled(editable);
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
		text.setText(string);
		text.setSelection(0,text.getText().length());
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
		char[] ca = getText().toCharArray();
		int count = 0;
		for (char c : ca) {
			if (Character.isDigit(c))
				count++;
		}
		return (count == 0);
	}
	
	/**
	 * Limpa o campo de texto
	 */
	public void clear(){
		text.setText("");
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
	
	public void setValidator(Validator validator){
		text.setValidator(validator);
	}

	@Override
	public boolean validate() {
		return text.validate();
	}
	
	public void setNullable(boolean nullable){
		text.setNullable(nullable);
	}
	public boolean isNullable(){
		return text.isNullable();
	}

	public void setPattern(String pattern) {
		this.pattern = pattern;
		text.setFormatter(new DateFormatter(pattern,BLANK_CHAR));
	}
}
