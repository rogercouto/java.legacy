package cw.listeners;


import java.text.DecimalFormat;
import java.text.ParseException;

import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.widgets.Text;

public class NumberFocusLost extends FocusAdapter {

	private DecimalFormat df = null;
	private double minimun = Double.MIN_VALUE;
	private double maximun = Double.MAX_VALUE;
	
	public NumberFocusLost(String pattern) {
		super();
		df = new DecimalFormat(pattern);
	}
	
	public NumberFocusLost(String pattern,double min,double max) {
		super();
		this.minimun = min;
		this.maximun = max;
		df = new DecimalFormat(pattern);
	}
	
	@Override
	public void focusLost(FocusEvent e){
		Text text = (Text)e.widget;
		try {
			double d = df.parse(text.getText()).doubleValue();
			if (d < minimun)
				d = minimun;
			else if (d > maximun)
				d = maximun;
			String s = df.format(d);
			if (text.getText().compareTo(s) != 0){
				text.setText("");
				text.setText(s);
			}
		} catch (ParseException e1) {
		}
	}
	
}
