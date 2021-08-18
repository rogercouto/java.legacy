package cw.vtextfield;


import java.util.ArrayList;
import java.util.Date;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolTip;

public class CVTextField extends Composite implements CVControl, FocusListener, ModifyListener, VerifyListener, MouseListener{
	
	/**
	 *Constantes
	 */
	private static final Color E_COLOR = Display.getCurrent().getSystemColor(SWT.COLOR_DARK_RED); 
	private static final Color N_COLOR = Display.getCurrent().getSystemColor(SWT.COLOR_WIDGET_BACKGROUND);
	private static final Color FG_COLOR = new Color(Display.getCurrent(), 255, 255, 204);
	private static final Color FL_COLOR = Display.getCurrent().getSystemColor(SWT.COLOR_WHITE);
	/**
	 * Variáveis do desenho
	 */
	protected Composite composite;
	protected Label lblAviso;
	protected Text text;
	protected ToolTip tip = null;
	protected int style = SWT.BORDER;
	/**
	 * Variáveis de controle
	 */
	private boolean notNull = true;
	private String nullWarning = "Este campo não pode ficar em branco!";
	private String invWarning = null;
	private DataType type = DataType.TEXT;
	private int digits = 2;
	private Object value = null;
	private boolean verify = true;
	private String inputMask = null;
	
	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 */
	public CVTextField(Composite parent, int style) {
		super(parent, SWT.NONE);
		this.style = style;
		initComponents();
	}
	private void initComponents() {
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 2;
		gridLayout.verticalSpacing = 0;
		gridLayout.horizontalSpacing = 2;
		gridLayout.marginHeight = 0;
		gridLayout.marginWidth = 0;
		setLayout(gridLayout);
		composite = new Composite(this, SWT.NONE);
		composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		composite.setBackground(N_COLOR);
		GridLayout gl_composite = new GridLayout(1, false);
		gl_composite.verticalSpacing = 2;
		gl_composite.horizontalSpacing = 2;
		gl_composite.marginHeight = 2;
		gl_composite.marginWidth = 2;
		composite.setLayout(gl_composite);
		text = new Text(composite, style);
		text.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		lblAviso = new Label(this, SWT.NONE);
		//lblAviso.setImage(SWTResourceManager.getImage(CVTextField.class, "/rdw/icon/aviso.png"));
		lblAviso.setVisible(false);
		tip = new ToolTip(getShell(), SWT.BALLOON);
		text.addFocusListener(this);
		text.addModifyListener(this);
		text.addVerifyListener(this);
		lblAviso.addMouseListener(this);
		tip.setAutoHide(true);
		checkOrder();
	}
	
	private void checkOrder(){
		Control[] controls = this.getParent().getChildren();
		ArrayList<CVControl> list = new ArrayList<CVControl>();
		for (Control control : controls) {
			if (control instanceof CVControl) {
				list.add( (CVControl) control);
			}
		}
		final int index = list.indexOf(this);
		if (index > 0){
			final CVControl prev = list.get(index - 1);
			prev.addListener(SWT.KeyDown, new Listener() {
				@Override
				public void handleEvent(Event arg0) {
					if (arg0.keyCode == SWT.CR || arg0.keyCode == SWT.KEYPAD_CR)
						text.setFocus();
				}
			});
		}
	}
	
	private void checkType(){
		if (type == DataType.DATE && invWarning == null){
			invWarning = "Data inválida!";
		}else if (type == DataType.TIME && invWarning == null){
			invWarning = "Hora inválida!";
		}else if (type == DataType.TEXT && invWarning == null){
			invWarning = "Campo incompleto!";
		}
	}
	
	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}
	
	@Override
	public void verifyText(VerifyEvent arg0) {
		if (!verify)
			return;
		if (type == DataType.TEXT){
			if (inputMask != null){
				Point p = text.getSelection();
				String txt = text.getText();
				if (p.x == p.y){
					if (txt.length() == p.x && arg0.character == SWT.BS){
						char[] ima = inputMask.toCharArray();
						if (txt.length() < ima.length){
							if (ima[txt.length()-1] != '0' && ima[txt.length()-1] != '#'){
								changeText(txt.substring(0, txt.length()-1));
								return;
							}
						}
					}else if (p.x < txt.length() && arg0.character == SWT.DEL){
						arg0.doit = false;
						return;
					}else if (arg0.character == SWT.BS){
						p.x -= 1;
					}else if (arg0.character == SWT.DEL){
						p.y += 1;
					}
				}
				String newText = text.getText().substring(0, p.x);
				newText += arg0.text;
				newText += text.getText().substring(p.y, text.getText().length());
				char[] nta = newText.toCharArray();
				char[] ma = inputMask.toCharArray();
				if (nta.length > ma.length){
					arg0.doit = false;
					return;
				}
				for (int i = 0; i < nta.length; i++) {
					if (ma[i] == '0'){
						if (!Character.isDigit(nta[i])){
							arg0.doit = false;
							return;
						}
					}else if (ma[i] == '#'){
						if (!Character.isLetterOrDigit(nta[i])){
							arg0.doit = false;
							return;
						}
					}else{
						if (nta[i] != ma[i]){
							arg0.doit = false;
							return;
						}
					}
				}
			}
		}else if (type == DataType.INTEGER){
			if (!CVerify.integer(arg0.text))
				arg0.text = "";
		}else if (type == DataType.DECIMAL){
			if (arg0.text.length() == 1){
				if (arg0.text.compareTo(",") == 0 || arg0.text.compareTo(".") == 0){
					if (CVerify.haveComma(text.getText()))
						arg0.text = "";
					else{
						if (text.getText().trim().length() == 0)
							arg0.text = "0,";
						else
							arg0.text = ",";
					}
				}else if (!CVerify.integer(arg0.text)){
					arg0.text = "";
				}
			}else{
				Point p = text.getSelection();
				String newText = text.getText().substring(0, p.x);
				newText += arg0.text;
				newText += text.getText().substring(p.y, text.getText().length());
				if (!CVerify.decimal(newText))
					arg0.text = "";
			}
		}else if (type == DataType.DATE){
			String txt = text.getText();
			if (arg0.character == SWT.BS){
				if (txt.length() == 3 || txt.length() == 6)
					changeText(txt.substring(0, txt.length()-1));
				return;
			}
			if (arg0.text.length() == 1){
				if (!CVerify.integer(arg0.text))
					arg0.text = "";
			}
			Point p = text.getSelection();
			String newText = txt.substring(0, p.x);
			newText += arg0.text;
			newText += txt.substring(p.y, text.getText().length());
			if (!CVerify.date(newText))
				arg0.text = "";
		}else if (type == DataType.TIME){
			String txt = text.getText();
			if (arg0.character == SWT.BS){
				if (txt.length() == 3)
					changeText(txt.substring(0, txt.length()-1));
			}
			if (arg0.text.length() == 1){
				if (!CVerify.integer(arg0.text))
					arg0.text = "";
			}
			Point p = text.getSelection();
			String newText = txt.substring(0, p.x);
			newText += arg0.text;
			newText += txt.substring(p.y, text.getText().length());
			if (!CVerify.time(newText))
				arg0.text = "";
		}
	}
	
	@Override
	public void modifyText(ModifyEvent arg0) {
		if (!verify)
			return;
		lblAviso.setVisible(false);
		composite.setBackground(N_COLOR);
		tip.setVisible(false);
		if (type == DataType.DATE){
			String txt = text.getText();
			if (txt.length() == 2 || txt.length() == 5)
				changeText(txt + "/");
		}else if (type == DataType.TIME){
			String txt = text.getText();
			if (txt.length() == 2)
				changeText(txt + ":");
		}else if (type == DataType.TEXT && inputMask != null){
			String txt = text.getText();
			int lenght = txt.length();
			if (lenght == inputMask.length())
				return;
			char[] ta = inputMask.toCharArray();
			if (ta[lenght] != '0' && ta[lenght] != '#'){
				changeText(txt+ta[lenght]);
			}
		}
	}
	
	@Override
	public void focusGained(FocusEvent arg0) {
		text.setBackground(FG_COLOR);
		lblAviso.setVisible(false);
		composite.setBackground(N_COLOR);
		tip.setVisible(false);
		if (inputMask == null)
			return;
		if (text.getText().length() == 0){
			String newText = "";
			for (char c : inputMask.toCharArray()) {
				if (c != '0' && c != '#')
					newText += c;
				else
					break;
			}
			if (newText.length() > 0)
				changeText(newText);
		}
	}
	
	@Override
	public void focusLost(FocusEvent arg0) {
		text.setBackground(FL_COLOR);
		String txt = text.getText().trim();
		if (notNull){
			lblAviso.setVisible(txt.length() == 0);
			composite.setBackground(txt.length() == 0 ? E_COLOR : N_COLOR);
			if (txt.length() == 0)
				tip.setMessage(nullWarning);
		}
		/*
		if (txt.trim().length() == 0){
			value = null;
			return;
		}
		*/
		if (type == DataType.TEXT){
			value = txt;
			if (inputMask != null && txt.length() != inputMask.length()){
				lblAviso.setVisible(true);
				composite.setBackground(E_COLOR);
				tip.setMessage(invWarning);
				return;
			}
		}if (type == DataType.INTEGER){
			value = Integer.valueOf(txt);
		}else if (type == DataType.DECIMAL){
			value = CVUtil.getDouble(txt, digits);
			changeText(CVUtil.numberToString((Number) value, digits));
		}else if (type == DataType.DATE){
			value = txt;
			if (txt.length() != 10 || !CVerify.validDate((String)value)){
				lblAviso.setVisible(true);
				composite.setBackground(E_COLOR);
				tip.setMessage(invWarning);
			}
		}else if (type == DataType.TIME){
			value = txt;
			if (txt.length() != 5 || !CVerify.validTime((String)value)){
				lblAviso.setVisible(true);
				composite.setBackground(E_COLOR);
				tip.setMessage(invWarning);
			}
		}
	}
	
	/**
	 * 
	 */
	public void changeText(String txt){
		verify = false;
		text.setText(txt);
		text.setSelection(txt.length());
		verify = true;
	}
	
	@Override
	public void mouseDoubleClick(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void mouseDown(MouseEvent arg0) {
		tip.setVisible(true);
	}
	@Override
	public void mouseUp(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	
	/**
	 * Getters e Setters
	 */
	public boolean validate(){
		String txt = text.getText().trim();
		if (txt.length() > 0)
			value = text.getText().trim();
		else
			value = null;
		if (notNull && value == null)
			return false;
		if (type == DataType.TEXT){
			if (inputMask != null && txt.length() != inputMask.length()){
				return false;
			}
		}if (type == DataType.INTEGER){
			value = Integer.valueOf(txt);
			if (value == null)
				return false;
		}else if (type == DataType.DECIMAL){
			value = CVUtil.getDouble(txt, digits);
			if (value == null)
				return false;
		}else if (type == DataType.DATE){
			value = txt;
			if (txt.length() != 10 || !CVerify.validDate((String)value)){
				return false;
			}
		}else if (type == DataType.TIME){
			value = txt;
			if (txt.length() != 5 || !CVerify.validTime((String)value)){
				return false;
			}
		}
		return true;
	}
	
	public void setType(DataType type){
		this.type = type;
		checkType();
	}
	public DataType getType(){
		return type;
	}
	public void setNotNull(boolean notNull){
		this.notNull = notNull;
	}
	public void setNullWarning(String warning){
		this.nullWarning = warning;
	}
	public void setInvWarning(String warning){
		this.invWarning = warning;
	}
	public void setInputMask(String mask){
		if (type == null)
			type = DataType.TEXT;
		checkType();
		this.inputMask = mask;
	}
	
	public String getText(){
		if (type != DataType.TEXT)
			throw new UnsupportedOperationException("O tipo do campo deve ser texto!");
		if (value == null)
			return null;
		return (String)value;
	}
	public Integer getInteger(){
		if (type != DataType.INTEGER)
			throw new UnsupportedOperationException("O tipo do campo deve ser inteiro!");
		if (value == null)
			return null;
		return (Integer)value;
	}
	public Double getDecimal(){
		if (type != DataType.DECIMAL)
			throw new UnsupportedOperationException("O tipo do campo deve ser decimal!");
		if (value == null)
			return null;
		return (Double)value;
	}
	public Date getDate(){
		if (type != DataType.DATE)
			throw new UnsupportedOperationException("O tipo do campo deve ser data!");
		if (value == null)
			return null;
		return CVUtil.txtToDate((String)value);
	}
	public Date getTime(){
		if (type != DataType.TIME)
			throw new UnsupportedOperationException("O tipo do campo deve ser hora!");
		if (value == null)
			return null;
		return CVUtil.txtToDate((String)value);
	}
	
	public String getUnmaskedText(){
		if (inputMask == null)
			return getText();
		char[] ta = getText().toCharArray();
		char[] ma = inputMask.toCharArray();
		if (ta.length > ma.length){
			throw new UnsupportedOperationException("Por alguma razão o texto está com o tamanho errado");
		}
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < ta.length; i++) {
			if (ma[i] == '#' || ma[i] == '0')
				sb.append(ta[i]);
		}
		return sb.toString();
	}
	
	public void addListener(int eventType, Listener listener){
		text.addListener(eventType, listener);
	}
	
	public void setEnabled(boolean enabled){
		text.setBackground(FL_COLOR);
		text.setEnabled(enabled);
		composite.setBackground(N_COLOR);
		lblAviso.setVisible(false);
		tip.setVisible(false);
	}

	public boolean isEnabled(){
		return text.getEnabled();
	}
	
	public boolean isEmpty(){
		return text.getText().trim().length() == 0;
	}
}
