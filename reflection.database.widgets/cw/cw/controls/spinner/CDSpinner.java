package cw.controls.spinner;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Spinner;

public class CDSpinner extends Composite {
	
	private Spinner spinner;
	private NumberFormat nf = new DecimalFormat("#.##");

	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 */
	public CDSpinner(Composite parent, int style) {
		super(parent, style);
		setLayout(new FillLayout(SWT.HORIZONTAL));
		spinner = new Spinner(this, SWT.NONE);
		spinner.setPageIncrement(1);
		spinner.setDigits(2);
		spinner.setMaximum(100000);
		spinner.setMinimum(-100000);
		spinner.addFocusListener(new FocusAdapter() {
			@Override
			public void focusLost(FocusEvent arg0) {
				refresh();
			}
		});
	}
	
	private void refresh(){
		String text = spinner.getText();
		try {
			Number n = nf.parse(text);
			Double d2 = n.doubleValue() *100;
			int num = d2.intValue();
			spinner.setSelection(num);
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}
	
	private double getNValue(){
		Number n;
		try {
			n = nf.parse(spinner.getText());
			return n.doubleValue();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return 0;
	}
	
	/**
	 * Retorna o double
	 * @return
	 */
	public Double getValue(){
		double d = getNValue();
		if (d != 0)
			return d;
		return null;
	}

	public boolean isEmpty(){
		return (getValue() == 0);
	}
	
	public void setValue(double d){
		Double d2 = d *100;
		int num = d2.intValue();
		spinner.setSelection(num);
	}
	
	public void clear(){
		setValue(0);
	}
	
	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}

	public void setMinimun(int minimun){
		spinner.setMinimum(minimun);
	}
	
	public void setMaximun(int maximun){
		spinner.setMaximum(maximun);
	}
}
