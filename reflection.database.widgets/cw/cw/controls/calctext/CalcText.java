package cw.controls.calctext;

import java.text.DecimalFormat;
import java.text.ParseException;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.DragDetectListener;
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
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.events.TraverseEvent;
import org.eclipse.swt.events.TraverseListener;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Listener;

import cw.controls.textfield.Validator;
import cw.interfaces.CControl;
import cw.listeners.NumberFocusLost;
import cw.listeners.VerifyNumber;
import cw.tools.Dialog;

public class CalcText extends CompCalc implements CControl{

	private String pattern = "0.00";
	private double minimun = Double.MIN_VALUE;
	private double maximun = Double.MAX_VALUE;
	
	private NumberFocusLost focusListener = new NumberFocusLost(pattern,minimun,maximun);
	private VerifyNumber verifyListener = new VerifyNumber(pattern);
	private DecimalFormat df = new DecimalFormat(pattern);
	private Validator validator = null;
	private boolean nullable = true;
	
	
	public CalcText(Composite parent, int style) {
		super(parent, style);
		initialize();
	}

	private void initialize(){
		verifyListener.blockDecimalPlaces(false);
		textCalc.addFocusListener(focusListener);
		textCalc.addVerifyListener(verifyListener);
		button0.addSelectionListener(new SelectionAdapter(){
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				actAppend('0');
			}
		});
		button1.addSelectionListener(new SelectionAdapter(){
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				actAppend('1');
			}
		});
		button2.addSelectionListener(new SelectionAdapter(){
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				actAppend('2');
			}
		});
		button3.addSelectionListener(new SelectionAdapter(){
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				actAppend('3');
			}
		});
		button4.addSelectionListener(new SelectionAdapter(){
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				actAppend('4');
			}
		});
		button5.addSelectionListener(new SelectionAdapter(){
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				actAppend('5');
			}
		});
		button6.addSelectionListener(new SelectionAdapter(){
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				actAppend('6');
			}
		});
		button7.addSelectionListener(new SelectionAdapter(){
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				actAppend('7');
			}
		});
		button8.addSelectionListener(new SelectionAdapter(){
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				actAppend('8');
			}
		});
		button9.addSelectionListener(new SelectionAdapter(){
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				actAppend('9');
			}
		});
		buttonComma.addSelectionListener(new SelectionAdapter(){
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				actAppend(',');
			}
		});
		buttonOp.addSelectionListener(new SelectionAdapter(){
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				actOp();
			}
		});
		buttonC.addSelectionListener(new SelectionAdapter(){
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				actClear();
			}
		});
		buttonBs.addSelectionListener(new SelectionAdapter(){
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				actBs();
			}
		});
		buttonDivide.addSelectionListener(new SelectionAdapter(){
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				actDivide();
			}
		});
		buttonMultiply.addSelectionListener(new SelectionAdapter(){
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				actMultiply();
			}
		});
		buttonSubtract.addSelectionListener(new SelectionAdapter(){
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				actSubtract();
			}
		});
		buttonAdd.addSelectionListener(new SelectionAdapter(){
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				actAdd();
			}
		});
		buttonResult.addSelectionListener(new SelectionAdapter(){
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				actResult();
			}
		});
		Control[] controls = shellCalc.getChildren();
		for (Control control : controls) {
			control.addKeyListener(new KeyAdapter(){
				@Override
				public void keyPressed(KeyEvent arg0) {
					calcKeyPressed(arg0);
				}
			});
		}
		shellCalc.addKeyListener(new KeyAdapter(){
			@Override
			public void keyPressed(KeyEvent arg0) {
				calcKeyPressed(arg0);
			}
		});
		shellCalc.addTraverseListener(new TraverseListener(){
			@Override
			public void keyTraversed(TraverseEvent arg0) {
				switch (arg0.keyCode) {
				case SWT.CR:
					actResult();
					return;
				case SWT.KEYPAD_CR:
					actResult();
					return;
				case SWT.ESC:
					arg0.doit = false;
					actClear();
					return;
				}
			}
		});
		textCalc.addKeyListener(new KeyAdapter(){
			@Override
			public void keyPressed(KeyEvent arg0) {
				if (!textCalc.getEditable())
					return;
				switch (arg0.keyCode) {
				case SWT.KEYPAD_DIVIDE:
					op = OP_DIVIDE;
					waitForClear = true;
					memory = getDouble();
					buttonCalc.setSelection(true);
					showCalc();
					break;
				case SWT.KEYPAD_MULTIPLY:
					op = OP_MULTIPLY;
					waitForClear = true;
					memory = getDouble();
					buttonCalc.setSelection(true);
					showCalc();
					break;
				case SWT.KEYPAD_SUBTRACT:
					op = OP_SUBTRACT;
					waitForClear = true;
					memory = getDouble();
					buttonCalc.setSelection(true);
					showCalc();
					break;
				case SWT.KEYPAD_ADD:
					op = OP_ADD;
					waitForClear = true;
					memory = getDouble();
					buttonCalc.setSelection(true);
					showCalc();
					break;
				default:
					break;
				}
			}
		});
	}
	
	private void calcKeyPressed(KeyEvent e){
		switch (e.keyCode) {
		case SWT.BS:
			buttonBs.setFocus();
			actBs();
			return;
		case SWT.KEYPAD_DIVIDE:
			buttonDivide.setFocus();
			actDivide();
			return;
		case SWT.KEYPAD_MULTIPLY:
			buttonMultiply.setFocus();
			actMultiply();
			return;
		case SWT.KEYPAD_SUBTRACT:
			buttonSubtract.setFocus();
			actSubtract();
			return;
		case SWT.KEYPAD_ADD:
			buttonAdd.setFocus();
			actAdd();
			return;
		default:
			break;
		}
		switch (e.character) {
		case '0':
			button0.setFocus();
			actAppend('0');
			break;
		case '1':
			button1.setFocus();
			actAppend('1');
			break;
		case '2':
			button2.setFocus();
			actAppend('2');
			break;	
		case '3':
			button3.setFocus();
			actAppend('3');
			break;	
		case '4':
			button4.setFocus();
			actAppend('4');
			break;	
		case '5':
			button5.setFocus();
			actAppend('5');
			break;	
		case '6':
			button6.setFocus();
			actAppend('6');
			break;	
		case '7':
			button7.setFocus();
			actAppend('7');
			break;	
		case '8':
			button8.setFocus();
			actAppend('8');
			break;	
		case '9':
			button9.setFocus();
			actAppend('9');
			break;	
		case ',':
			buttonComma.setFocus();
			actAppend(',');
			break;	
		case '.':
			buttonComma.setFocus();
			actAppend(',');
			break;
		default:
			break;
		}
	}

	public float getFloat(){
		try {
			return df.parse(textCalc.getText()).floatValue();
		} catch (ParseException e) {
		}
		return 0;
	}
	public void setFloat(float f){
		textCalc.setText("");
		textCalc.setText(df.format(f));
		textCalc.setSelection(textCalc.getText().length());
	}
	public double getDouble(){
		try {
			return df.parse(textCalc.getText()).doubleValue();
		} catch (ParseException e) {
		}
		return 0;
	}
	public void setDouble(double d){
		textCalc.setText("");
		textCalc.setText(df.format(d));
		textCalc.setSelection(textCalc.getText().length());
	}
	//TODO Calc Code start here
	private static final int OP_NONE = 0;
	private static final int OP_ADD = 1;
	private static final int OP_SUBTRACT = 2;
	private static final int OP_MULTIPLY = 3;
	private static final int OP_DIVIDE = 4;
	
	private boolean waitForClear = false;
	private double memory = 0;
	private int op = OP_NONE;
	
	private void actAppend(char c){
		if (waitForClear){
			textCalc.setText("");
			waitForClear = false;
		}
		textCalc.setSelection(textCalc.getText().length());
		textCalc.append(String.valueOf(c));
	}
	
	private void actBs(){
		String text = textCalc.getText();
		if (text.trim().length() == 0)
			return;
		textCalc.setText("");
		textCalc.setText(text.substring(0,text.length()-1));
	}
	
	private void actOp(){
		double d = getDouble() * -1;
		setDouble(d);
	}
	
	private void actClear(){
		textCalc.setText("");
		memory = 0;
		op = OP_NONE;
	}
	
	private void actDivide(){
		store();
		op = OP_DIVIDE;
	}
	
	private void actMultiply(){
		store();
		op = OP_MULTIPLY;
	}
	
	private void actSubtract(){
		store();
		op = OP_SUBTRACT;
	}
	
	private void actAdd(){
		store();
		op = OP_ADD;
	}
	
	private void actResult(){
		store();
		setDouble(memory);
		memory = 0;
		op = OP_NONE;
		waitForClear = false;
		shellCalc.setVisible(false);
		textCalc.setFocus();
	}
	
	private void store(){
		switch (op) {
		case OP_DIVIDE:
			Double d = getDouble();
			if (d != 0)
				memory /= d;
			else{
				memory = 0;
				op = OP_NONE;
				textCalc.setFocus();
				Dialog.warning(this.getShell(), "Aten��o", "Imposs�vel dividir por zero!");
			}
			break;
		case OP_MULTIPLY:
			memory *= getDouble();
			break;
		case OP_SUBTRACT:
			memory -= getDouble();
			break;
		case OP_ADD:
			memory += getDouble();
			break;
		case OP_NONE:
			memory = getDouble();
			break;
		default:
			break;
		}
		textCalc.setText("");
		setDouble(memory);
		waitForClear = true;
	}
	
	//TODO Getters - Setters
	@Override
	public void addListener(int eventType,Listener listener){
		textCalc.addListener(eventType, listener);
	}
	@Override
	public void addControlListener(ControlListener listener){
		textCalc.addControlListener(listener);
	}
	@Override
	public void addDisposeListener(DisposeListener listener){
		if (textCalc == null)
			return;
		textCalc.addDisposeListener(listener);
	}
	@Override
	public void addDragDetectListener(DragDetectListener listener){
		textCalc.addDragDetectListener(listener);
	}
	@Override
	public void addFocusListener(FocusListener listener){
		textCalc.addFocusListener(listener);
	}
	@Override
	public void addHelpListener(HelpListener listener){
		textCalc.addHelpListener(listener);
	}
	@Override
	public void addKeyListener(KeyListener listener){
		textCalc.addKeyListener(listener);
	}
	@Override
	public void addMenuDetectListener(MenuDetectListener listener){
		textCalc.addMenuDetectListener(listener);
	}
	public void addModifyListener(ModifyListener listener){
		textCalc.addModifyListener(listener);
	}
	@Override
	public void addMouseListener(MouseListener listener){
		textCalc.addMouseListener(listener);
	}
	@Override
	public void addMouseMoveListener(MouseMoveListener listener){
		textCalc.addMouseMoveListener(listener);
	}
	@Override
	public void addMouseTrackListener(MouseTrackListener listener){
		textCalc.addMouseTrackListener(listener);
	}
	@Override
	public void addMouseWheelListener(MouseWheelListener listener){
		textCalc.addMouseWheelListener(listener);
	}
	@Override
	public void addPaintListener(PaintListener listener){
		textCalc.addPaintListener(listener);
	}
	public void addSelectionListener(SelectionListener listener){
		textCalc.addSelectionListener(listener);
	}
	@Override
	public void addTraverseListener(TraverseListener listener){
		textCalc.addTraverseListener(listener);
	}
	public void addVerifyListener(VerifyListener listener){
		textCalc.addVerifyListener(listener);
	}
	
	@Override
	public void removeListener(int eventType,Listener listener){
		textCalc.removeListener(eventType, listener);
	}
	@Override
	public void removeControlListener(ControlListener listener){
		textCalc.removeControlListener(listener);
	}
	@Override
	public void removeDisposeListener(DisposeListener listener){
		textCalc.removeDisposeListener(listener);
	}
	@Override
	public void removeDragDetectListener(DragDetectListener listener){
		textCalc.removeDragDetectListener(listener);
	}
	@Override
	public void removeFocusListener(FocusListener listener){
		textCalc.removeFocusListener(listener);
	}
	@Override
	public void removeHelpListener(HelpListener listener){
		textCalc.removeHelpListener(listener);
	}
	@Override
	public void removeKeyListener(KeyListener listener){
		textCalc.removeKeyListener(listener);
	}
	@Override
	public void removeMenuDetectListener(MenuDetectListener listener){
		textCalc.removeMenuDetectListener(listener);
	}
	public void removeModifyListener(ModifyListener listener){
		textCalc.removeModifyListener(listener);
	}
	@Override
	public void removeMouseListener(MouseListener listener){
		textCalc.removeMouseListener(listener);
	}
	@Override
	public void removeMouseMoveListener(MouseMoveListener listener){
		textCalc.removeMouseMoveListener(listener);
	}
	@Override
	public void removeMouseTrackListener(MouseTrackListener listener){
		textCalc.removeMouseTrackListener(listener);
	}
	@Override
	public void removeMouseWheelListener(MouseWheelListener listener){
		textCalc.removeMouseWheelListener(listener);
	}
	@Override
	public void removePaintListener(PaintListener listener){
		textCalc.removePaintListener(listener);
	}
	public void removeSelectionListener(SelectionListener listener){
		textCalc.removeSelectionListener(listener);
	}
	@Override
	public void removeTraverseListener(TraverseListener listener){
		textCalc.removeTraverseListener(listener);
	}
	public void removeVerifyListener(VerifyListener listener){
		textCalc.removeVerifyListener(listener);
	}
	/**
	 * Verifica se o textCalco est� vazio
	 * @return
	 */
	public boolean isEmpty(){
		return (textCalc.getText().trim().length() == 0);
	}
	
	/**
	 * Limpa o campo de textCalco
	 */
	public void clear(){
		textCalc.setText("");
	}
	
	//Styles
	@Override
	public void setForeground(Color color){
		textCalc.setForeground(color);
	}
	@Override
	public Color getForeground(){
		return textCalc.getForeground();
	}
	@Override
	public void setBackground(Color color){
		textCalc.setBackground(color);
	}
	@Override
	public Color getBackground(){
		return textCalc.getBackground();
	}
	@Override
	public void setFont(Font font){
		textCalc.setFont(font);
	}
	@Override
	public Font getFont(){
		return textCalc.getFont();
	}
	
	public void setValidator(Validator validator){
		this.validator = validator;
	}
	public Validator getValidator(){
		return validator;
	}
	
	@Override
	public boolean validate() {
		if (isEmpty()){
			if(!nullable)
				return false;
			else
				return true;
		}
		if (validator == null)
			return true;
		return validator.verifyText(textCalc.getText());
	}
	
	public void setNullable(boolean nullable){
		this.nullable = nullable;
	}
	public boolean isNullable(){
		return nullable;
	}
	
	public void setPattern(String pattern){
		this.pattern = pattern;
		df = new DecimalFormat(this.pattern);
		textCalc.removeFocusListener(focusListener);
		textCalc.removeVerifyListener(verifyListener);
		focusListener = new NumberFocusLost(pattern);
		verifyListener = new VerifyNumber(pattern);
		verifyListener.blockDecimalPlaces(false);
		textCalc.addFocusListener(focusListener);
		textCalc.addVerifyListener(verifyListener);
	}
	
	public String getPattern(){
		return pattern;
	}
	
	protected Number getNumber(){
		try {
			return df.parse(textCalc.getText());
		} catch (ParseException e) {
		}
		return 0;
	}
	
	protected void setNumber(Number number){
		textCalc.setText(df.format(number));
		textCalc.setSelection(textCalc.getText().length());
	}
	
	public void setEnabled(boolean enabled){
		textCalc.setEnabled(enabled);
		buttonCalc.setEnabled(enabled);
	}
	
	public boolean getEnabled(){
		return textCalc.getEnabled();
	}

	public void setEditable(boolean b) {
		textCalc.setEditable(b);
		buttonCalc.setEnabled(b);
	}
	public boolean getEditable() {
		return textCalc.getEditable();
	}
	
	@Override
	public boolean isFocusControl(){
		return textCalc.isFocusControl();
	}
	
	public double getMinimun() {
		return minimun;
	}
	public void setMinimun(double minimun) {
		this.minimun = minimun;
		textCalc.removeFocusListener(focusListener);
		focusListener = new NumberFocusLost(pattern,minimun,maximun);
		textCalc.addFocusListener(focusListener);
	}
	public double getMaximun() {
		return maximun;
	}
	public void setMaximun(double maximun) {
		this.maximun = maximun;
		textCalc.removeFocusListener(focusListener);
		focusListener = new NumberFocusLost(pattern,minimun,maximun);
		textCalc.addFocusListener(focusListener);
	}
	
}


