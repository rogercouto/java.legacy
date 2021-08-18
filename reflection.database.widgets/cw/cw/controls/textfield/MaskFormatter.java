package cw.controls.textfield;

import java.util.ArrayList;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import cw.controls.calendartext.CalendarText;

public class MaskFormatter implements Formatter {

	private int[] validCodes = {99,118,120};
	
	private static final char DIGIT = '#';
	private static final char LETTER = '?';
	private static final char LC = 'L';
	private static final char UC = 'U';
	private static final char ANY = 'A';
	private static final char BLANK_CHAR = ' ';
	private char blankChar = BLANK_CHAR;
	
	private boolean vFlag = true;
	
	private KeyListener textKeyListener;
	private VerifyListener textVerifyListener;
	private MouseListener textMouseListener;
	private FocusListener textFocusListener; 
	
	protected Text text;
	private int textLimit;
	private boolean dClick;
	private String pattern;
	
	
	public MaskFormatter() {
		super();
	}
	public MaskFormatter(String pattern) {
		super();
		this.pattern = pattern;
	}
	public MaskFormatter(String pattern,char blankChar) {
		super();
		this.pattern = pattern;
		this.blankChar = blankChar;
	}

	public char getBlankChar() {
		return blankChar;
	}
	public void setBlankChar(char blankChar) {
		if (text == null)
			this.blankChar = blankChar;
	}
	public String getPattern() {
		return pattern;
	}
	public void setPattern(String pattern) {
		if (text == null)
			this.pattern = pattern;
	}
	
	@Override
	public void set(Text text) {
		if (this.text != null)
			remove();
		this.text = text;
		if (text != null)
			initialize();
	}

	private void initialize(){
		textLimit = text.getTextLimit();
		dClick = text.getDoubleClickEnabled();
		text.setTextLimit(pattern.length());
		text.setText(pattern.replaceAll("["+DIGIT+","+LETTER+","+LC+","+UC+","+ANY+"]",
				String.valueOf(blankChar)));
		addListeners();
	}
	
	private void addListeners(){
		textKeyListener = new KeyAdapter(){
			@Override
			public void keyPressed(KeyEvent e) {
				Point sel = text.getSelection();
				switch (e.keyCode) {
				case SWT.DEL:
					//Ação quando a tecla delete é pressionada
					e.doit = false;
					if (sel.x == pattern.length()) 
						return;
					if (sel.y - sel.x > 0){
						if (delete(sel))
							text.setSelection(sel.x);
					}else{
						if (delete(sel.x))
							text.setSelection(sel.x + 1);
					}
					break;
				case SWT.BS:
					//Ação quando a tecla Backspace é pressionada
					e.doit = false;
					if ((sel.x == 0) && (sel.y == 0))
						return;
					if (sel.y - sel.x > 0){
						if (delete(sel))
							text.setSelection(sel.x);						
					}else{
						backspace(sel.x);
					}
					break;
				default:
					break;
				}
				if (!checkCharacter(e))
					e.doit = false;
			}
			
		};
		textVerifyListener = new VerifyListener(){
			@Override
			public void verifyText(VerifyEvent e) {
				if (!vFlag)return;
				if (Character.isLetterOrDigit(e.character))
					e.doit = false;
					
				if (e.text.compareTo(EMPTY_STR)==0){
					e.doit = false;
					cut();
					return;
				}
				Point sel = text.getSelection();
				replace(sel.x, e.text);
			}
		};
		textMouseListener = new MouseAdapter(){
			@Override
			public void mouseDoubleClick(MouseEvent arg0) {
				if (pattern == null)return;
				Point p = text.getSelection();
				char[] cp = pattern.toCharArray();
				if ((p.y - p.x == 0) && ((p.x < cp.length)&&(!isVar(cp[p.x])))){
					text.setSelection(new Point(p.x,p.x+1));
					return;
				}
				if (p.x == cp.length)p.x--;
				Point sel = new Point(0,pattern.length());
				for (int i = p.x; i >= 0; i--) {
					if (!isVar(cp[i])){
						sel.x = i + 1;
						break;
					}					
				}
				for (int i = p.x; i < cp.length; i++) {
					if (pattern == null)return;
					if (!isVar(cp[i])){
						sel.y = i;
						break;
					}
				}
				text.setSelection(sel);
			}
			@Override
			public void mouseDown(MouseEvent arg0){
				char[] ca = text.getText().toCharArray();
				for (int i = 0; i < ca.length; i++) {
					if (ca[i] == blankChar){
						text.setSelection(i);
						return;
					}
				}
			}
		};
		textFocusListener = new FocusAdapter() {
			@Override
			public void focusGained(FocusEvent arg0){
				char[] ca = text.getText().toCharArray();
				for (int i = 0; i < ca.length; i++) {
					if (ca[i] == blankChar){
						text.setSelection(i);
						return;
					}
				}
			}
		};
		text.addKeyListener(textKeyListener);
		text.addVerifyListener(textVerifyListener);
		text.addMouseListener(textMouseListener);
		text.addFocusListener(textFocusListener);
		text.setDoubleClickEnabled(false);
	}
	
	private boolean checkCharacter(KeyEvent e){
		for (int i = 0; i < validCodes.length; i++) {
			if (e.keyCode == validCodes[i])
				return true;
		}
		if (Character.isLetterOrDigit(e.character))
			return true;
		if ((e.keyCode == SWT.ARROW_LEFT)
			||(e.keyCode == SWT.ARROW_RIGHT)	
			||(e.keyCode == SWT.ARROW_UP)
			||(e.keyCode == SWT.ARROW_DOWN)
			||(e.keyCode == SWT.HOME)
			||(e.keyCode == SWT.END)){
			return true;
		}
		return false;
	}
	
	private void cut(){
		vFlag = false;
		text.copy();
		delete(text.getSelection());
		vFlag = true;
	}
	
	@Override
	public void remove(){
		String string = text.getText();
		if (textKeyListener != null)
			text.removeKeyListener(textKeyListener);
		if (textVerifyListener != null)
			text.removeVerifyListener(textVerifyListener);
		if (textMouseListener != null)
			text.removeMouseListener(textMouseListener);
		if (textFocusListener != null)
			text.removeFocusListener(textFocusListener);
		textKeyListener = null;
		textVerifyListener = null;
		textMouseListener = null;
		text.setDoubleClickEnabled(dClick);
		if (string != null)
			text.setText(string);
		text.setTextLimit(textLimit);
	}
	
	//TODO Text modifiers
	
	private void replace(int index,char c){
		if (!isVar(index))return;
		char[] buffer = text.getText().toCharArray();
		buffer[index] = c;
		text.setText(String.valueOf(buffer));
	}
	private void replace(int start,String string){
		string = checkString(start, string);
		if (string == null)
			return;
		vFlag = false;
		char[] buffer = text.getText().toCharArray();
		char[] ps = pattern.substring(start,pattern.length()).toCharArray();
		char[] ss = string.toCharArray();
		if (ss.length == 0)
			return;
		int k = 0;
		int sel = start;
		for (int i = 0; i < ps.length; i++) {
			if (isVar(ps[i])){
				switch (ps[i]) {
				case ANY:
					buffer[start + i] = ss[k];
					break;
				case UC:
					if (Character.isLetter(ss[k]))
						buffer[start + i] = Character.toUpperCase(ss[k]);
					else{
						vFlag = true;						
						return;
					}
					break;
				case LC:
					if (Character.isLetter(ss[k]))
						buffer[start + i] = Character.toLowerCase(ss[k]);
					else{
						vFlag = true;						
						return;
					}
					break;
				case LETTER:
					if (Character.isLetter(ss[k]))
						buffer[start + i] = ss[k];
					else{
						vFlag = true;						
						return;
					}
					break;
				case DIGIT:
					if (Character.isDigit(ss[k]))
						buffer[start + i] = ss[k];
					else{
						vFlag = true;						
						return;
					}
					break;
				default:
					break;
				}
				k++;
			}
			sel++;
			if (ss.length == k)
				break;
		}		
		
		text.setText(String.valueOf(buffer));
		text.setSelection(sel);
		vFlag = true;
	}
	
	private boolean delete(int index){
		if (!text.getEditable())
			return false;
		replace(index,blankChar);
		return true;
	}
	private boolean delete(Point sel){
		if (!text.getEditable())
			return false;
		for (int i = sel.x; i < sel.y; i++) {
			replace(i,blankChar);
		}
		return true;
	}
	private void deleteAll(){
		for (int i = 0; i < pattern.length(); i++) {
			replace(i,blankChar);
		}
	}
	
	private void backspace(int sel){
		sel--;
		do{
			if (isVar(sel)){
				if (delete(sel))
					text.setSelection(sel);
				return;
			}else{
				sel--;
			}
		}while (sel > 0);
	}
	
	public void clear(){
		vFlag = false;
		deleteAll();
		vFlag = true;
	}
	
	//TODO checkers
	private boolean isVar(int index){
		char c = pattern.toCharArray()[index];
		if ((c == DIGIT)|| (c == LETTER) || (c == UC) || (c == LC) || (c == ANY)){
			return true;
		}
		return false;
	}
	private static boolean isVar(char c){
		char[] va = {DIGIT,LETTER,UC,LC,ANY};
		for (char v : va) {
			if (c == v)
				return true;
		}
		return false;
	}
	
	protected int getVarLength(){
		char[] ps = pattern.toCharArray();
		int k = 0;
		for (int i = 0; i < ps.length; i++) {
			if (isVar(ps[i]))
				k++;
		}
		return k;
	}
	
	private String checkString(int start,String string){
		char[] ps = pattern.toCharArray();
		ArrayList<Character> oc = new ArrayList<Character>();
		for (int i = 0; i < ps.length; i++) {
			if (!isVar(ps[i])){
				for (int j = 0; j < oc.size(); j++) {
					if (oc.get(j) == ps[i])
						continue;
				}
				oc.add(ps[i]);
			}
		}
		char[] ss = string.toCharArray();
		for (int i = 0; i < ss.length; i++) {
			if (!Character.isLetterOrDigit(ss[i]) || (ss[i] == blankChar)){
				boolean ver = false;
				for (int j = 0; j < oc.size(); j++) {
					if (ss[i] == oc.get(j))
						ver = true;
				}
				if (!ver)return null;
			}
		}
		String newString = "";
		for (int i = 0; i < ss.length; i++) {
			if (Character.isLetterOrDigit(ss[i]))
				newString += ss[i];
		}
		return newString;
	}
	@Override
	public String getText() {
		String string = text.getText();
		char[] sc = string.toCharArray();
		String result = "";
		for (int i = 0; i < sc.length; i++) {
			if (isVar(i) && (sc[i] != blankChar))
				result += sc[i];
		}
		return result;
	}
	public String unmaskString(){
		String string = text.getText();
		char[] sc = string.toCharArray();
		String result = "";
		for (int i = 0; i < sc.length; i++) {
			if (isVar(i) && (sc[i] != blankChar))
				result += sc[i];
		}
		return result;
	}
	
	@Override
	public void clear(Point sel) {
		vFlag = false;
		delete(sel);
		vFlag = true;
	}
	@Override
	public Validator getValidator() {
		return new Validator() {
			@Override
			public boolean verifyText(String string) {
				return (string.length() == getVarLength());
			}
		};
	}
	
	public boolean isEmpty(){
		return (getText().trim().length() == 0);
	}
	@Override
	public void setText(String string) {
		int start = text.getSelection().x;
		replace(start, string);
	}
	@Override
	public Number getNumber() {
		return null;
	}
	
	public static String mask(String string,String pattern){
		char[] pa = pattern.toCharArray();
		char[] sa = string.toCharArray();
		int count = 0;
		String mString = "";
		for (char c : pa) {
			if (sa.length <= count){
				mString += BLANK_CHAR;
				break;
			}
			if (isVar(c)){
				mString += sa[count];
				count++;
			}else
				mString += c;
		}
		return mString;
	}
	
	public static void main(String[] args) {
		Display display = new Display();
		Shell shell = new Shell(display);
		CTextField text = new CTextField(shell, SWT.BORDER);
		text.setFormatter(new MaskFormatter("UUU-####"));
		new CalendarText(shell, SWT.BORDER);
		shell.setLayout(new FillLayout());
		shell.pack();
		shell.open();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}
		display.dispose();
	}
	
}
