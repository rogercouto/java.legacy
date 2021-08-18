package cw.controls.filechooser;

import java.io.File;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.DragDetectListener;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
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
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;

import cw.controls.textfield.CTextField;
import cw.controls.textfield.Validator;
import cw.interfaces.CControl;

public class CFileChooser extends Composite implements CControl {

	private final Color NORM_TEXT = new Color(getDisplay(),240,240,240);  //  @jve:decl-index=0:
	private final Color DISABLED_TEXT = getDisplay().getSystemColor(SWT.COLOR_WIDGET_BACKGROUND);  //  @jve:decl-index=0:
	
	private CTextField text = null;
	private int style;
	private Button buttonFind = null;
	private FileDialog dialog = null;
	
	public CFileChooser(Composite parent, int style) {
		super(parent, SWT.NONE);
		this.style = style;
		initialize();
	}

	private void addListeners(){
		buttonFind.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
			@Override
			public void widgetSelected(org.eclipse.swt.events.SelectionEvent e) {
				String patch = dialog.open();
				if (patch != null)
					text.setText(patch);
			}
		});
		this.addFocusListener(new FocusAdapter(){
			@Override
			public void focusGained(FocusEvent arg0) {
				text.setFocus();
			}
		});
		text.addListener(SWT.MouseDown, new Listener(){
			@Override
			public void handleEvent(Event arg0) {
				text.setSelection(0,text.getText().length());
			}
		});
		text.addListener(SWT.FocusIn, new Listener(){
			@Override
			public void handleEvent(Event arg0) {
				text.setSelection(0,text.getText().length());
			}
		});
		text.addListener(SWT.KeyDown, new Listener(){
			@Override
			public void handleEvent(Event arg0) {
				if (arg0.keyCode == SWT.DEL)
					text.clear();
				else{
					String patch = dialog.open();
					if (patch != null){
						text.setText(patch);
					}
				}
					
			}
		});
	}
	
	public void setFilterExtensions(String[] extensions){
		dialog.setFilterExtensions(extensions);
	}
	public void setFilterName(String[] names){
		dialog.setFilterNames(names);
	}
	
	public void setFilterExtension(String extension){
		String[] string = {extension};
		dialog.setFilterExtensions(string);
	}
	public void setFilterName(String name){
		String[] string = {name};
		dialog.setFilterNames(string);
	}
	
	public void setReadOnly(boolean readOnly){
		Color color = text.getBackground();
		setEditable(!readOnly);
		setBackground(color);
	}
	
	public File getFile(){
		File file = new File(text.getText());
		if (file.exists())
			return file;
		return null;
	}
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		/* Before this is run, be sure to set up the launch configuration (Arguments->VM Arguments)
		 * for the correct SWT library path in order to run with the SWT dlls. 
		 * The dlls are located in the SWT plugin jar.  
		 * For example, on Windows the Eclipse SWT 3.1 plugin jar is:
		 *       installation_directory\plugins\org.eclipse.swt.win32_3.1.0.jar
		 */
		GridData gridData = new GridData();
		gridData.horizontalAlignment = SWT.FILL;
		gridData.grabExcessHorizontalSpace = true;
		//gridData.grabExcessVerticalSpace= true;
		
		Display display = Display.getDefault();
		Shell shell = new Shell(display);
		shell.setLayout(new GridLayout());
		shell.setSize(new Point(100, 65));
		//shell.pack();
		CFileChooser calendarText = new CFileChooser(shell,SWT.BORDER);
		calendarText.setReadOnly(true);
		calendarText.setLayoutData(gridData);
		
		shell.open();

		while (!shell.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}
		display.dispose();
	}

	private void initialize() {
		GridData gridData1 = new GridData();
		gridData1.heightHint = 23;
		gridData1.grabExcessVerticalSpace = true;
		gridData1.horizontalAlignment = GridData.BEGINNING;
		gridData1.verticalAlignment = GridData.FILL;
		gridData1.widthHint = -1;
		GridData gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;
		gridData.grabExcessVerticalSpace = true;
		gridData.verticalAlignment = GridData.FILL;
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 2;
		gridLayout.verticalSpacing = 0;
		gridLayout.marginWidth = 0;
		gridLayout.marginHeight = 0;
		gridLayout.horizontalSpacing = 0;
		text = new CTextField(this, style);
		text.setBackground(NORM_TEXT);
		setEditable(false);
		text.setLayoutData(gridData);
		buttonFind = new Button(this, SWT.NONE);
		buttonFind.setImage(new Image(Display.getCurrent(), getClass().getResourceAsStream("/rdw/icon/icon_find.png")));
		buttonFind.setToolTipText("Localizar arquivo no computador");
		buttonFind.setText("Localizar");
		buttonFind.setLayoutData(gridData1);
		dialog = new FileDialog(getShell(),SWT.OPEN);
		this.setLayout(gridLayout);
		this.setTabList(new Control[]{text});
		addListeners();
	}

	@Override
	public void setEnabled(boolean enabled){
		text.setEnabled(enabled);
		if (enabled)
			text.setBackground(NORM_TEXT);
		else
			text.setBackground(DISABLED_TEXT);
		buttonFind.setEnabled(enabled);
		super.setEnabled(enabled);
	}
	
	private void setEditable(boolean editable){
		text.setEditable(editable);
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
		buttonFind.addSelectionListener(listener);
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
		buttonFind.removeSelectionListener(listener);
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
		return (getText().trim().length() == 0);
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
	
	public void setFile(File file){
		if (file != null && (file.exists()))
			setText(file.getAbsolutePath());
		else
			clear();
	}

}
