package cw.controls.textfield;

import java.text.DecimalFormat;
import java.text.ParseException;

import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Text;

import cw.listeners.NumberFocusLost;
import cw.listeners.VerifyNumber;

public class NumberFormatter implements Formatter {
	
	private String pattern;
	private Text text;
	
	private VerifyNumber verifyListener;
	private NumberFocusLost focusListener;
	
	public NumberFormatter() {
		super();
	}
	public NumberFormatter(String pattern) {
		super();
		this.pattern = pattern;
		verifyListener = new VerifyNumber(pattern);
		focusListener = new NumberFocusLost(pattern);
	}
	
	public String getPattern() {
		return pattern;
	}
	/**
	 * Select the Numric pattern
	 * @param pattern
	 */
	public void setPattern(String pattern) {
		if (text == null){
			this.pattern = pattern;
			verifyListener = new VerifyNumber(pattern);
			focusListener = new NumberFocusLost(pattern);
		}
	}
	public void setUnsigned(boolean unsigned){
		if (verifyListener != null)
			verifyListener.setUnsigned(unsigned);
	}
	public void blockDecimalPlaces(boolean block){
		if (verifyListener != null)
			verifyListener.blockDecimalPlaces(block);
	}
	
	@Override
	public void set(Text text) {
		if (this.text != null)
			remove();
		this.text = text;
		initialize();
	}

	private void initialize(){
		text.addVerifyListener(verifyListener);
		text.addFocusListener(focusListener);
	}
	
	@Override
	public void remove(){
		if (verifyListener != null)
			text.removeVerifyListener(verifyListener);
		if (focusListener != null)
			text.removeFocusListener(focusListener);
	}

	@Override
	public void clear() {
		text.setText(EMPTY_STR);		
	}

	@Override
	public String getText() {
		return text.getText();
	}
	@Override
	public String unmaskString(){
		return text.getText();
	}
	
	public Number getNumber(){
		Number number = null;
		DecimalFormat df = new DecimalFormat(pattern);
		try {
			number = df.parse(getText());
		} catch (ParseException e) {
			number = 0;
		}
		return number;
	}
	@Override
	public void clear(Point sel) {
		text.setSelection(sel);
		text.clearSelection();
	}
	@Override
	public Validator getValidator() {
		return null;
	}
	@Override
	public boolean isEmpty() {
		return (text.getText().trim().length() == 0);
	}
	@Override
	public void setText(String string) {
		text.setText(EMPTY_STR);
		text.setText(string);		
	}
	
}
