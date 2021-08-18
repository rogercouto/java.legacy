package cw.listeners;

import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Text;

import cw.tools.Util;

public class VerifyNumber implements VerifyListener {

	private int digits;
	
	private boolean verify = true;
	private boolean block = true;
	private boolean unsigned = false;
	
	public VerifyNumber(String pattern) {
		super();
		digits = Util.countDigits(pattern);
	}

	public boolean isBlockDecimalPlaces(){
		return block;
	}
	public void blockDecimalPlaces(boolean block){
		this.block = block;
	}
	public boolean isUnsigned() {
		return unsigned;
	}
	public void setUnsigned(boolean unsigned) {
		this.unsigned = unsigned;
	}

	protected boolean isVerify() {
		return verify;
	}
	protected void setVerify(boolean verify) {
		this.verify = verify;
	}

	@Override
	public void verifyText(VerifyEvent e) {
		if (!verify)return;
		e.text = e.text.replace(Util.POINT, Util.COMMA);
		
		Text text = (Text)e.widget;
		Point sel = text.getSelection();
		String t1 = text.getText().substring(0, sel.x);
		String t2 = text.getText().substring(
						sel.y,
						text.getText().length()
					);
		String decimal = t1 + e.text + t2;
		
		char[] es = e.text.toCharArray();
		
		if (digits == 0){
			String newText = "";
			for (int i = 0; i < es.length; i++) {
				if (es[i] != Util.COMMA)
					newText += es[i];
			}
			e.text = newText;
		}
		//Valida o número
		if (!Util.isDecimal(decimal)){
			e.doit = false;
			return;
		}
		//Bloqueia a inserção de numeros após a virgula
		if (block && (Util.countDigits(decimal) > digits)){
			e.doit = false;
			return;
		}
		if (e.text.length() == 0)return;
		
		//Verifica se o campo não permite numeros negativos
		for (int i = 0; i < es.length; i++) {
			if (unsigned && (es[i]==Util.SIGNAL)){
				e.doit = false;
				return;
			}
		}
		//Coloca um zero na frente da vírgula caso não tenha nenhum dígito antes dela 
		if (es[0] == Util.COMMA){
			if ((t1.length() == 0) || (t1.compareTo(String.valueOf(Util.SIGNAL))== 0)){
				e.text = 0 + e.text;
			}
		}else if (es.length > 1 && ((es[0] == Util.SIGNAL) && (es[1] == Util.COMMA))){
			if (t1.length() == 0) {
				e.text = "";
				e.text += es[0];
				e.text += 0;
				for (int i = 1; i < es.length; i++) {
					e.text += es[i];
				}
			}
		}
			
	}
	
}
