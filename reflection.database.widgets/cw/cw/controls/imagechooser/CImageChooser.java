package cw.controls.imagechooser;

import java.io.File;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
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
import org.eclipse.swt.events.TraverseListener;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;

import cw.controls.filechooser.CFileChooser;
import cw.controls.textfield.Validator;
import cw.tools.Util;

public class CImageChooser extends Composite {

	private static final String[] FILTER_EXT = {"*.bmp;*jpg;*.gif;*.png;"};
	private static final String[] FILTER_NAMES = {"Imagens(*.BMP,*.JPG,*.GIF,*.PNG)"};
	
	protected CFileChooser fileChooser = null;
	private Composite compositeImage = null;
	private Composite compOptions = null;
	private Label labelImage = null;
	private ToolBar toolBar = null;
	protected Image image;
	
	public CImageChooser(Composite parent, int style) {
		super(parent, style);
		initialize();
	}

	/**
	 * This method initializes compositeImage	
	 *
	 */
	private void createCompositeImage() {
		GridLayout gridLayout2 = new GridLayout();
		gridLayout2.horizontalSpacing = 0;
		gridLayout2.marginWidth = 0;
		gridLayout2.marginHeight = 5;
		gridLayout2.verticalSpacing = 0;
		GridData gridData3 = new GridData();
		gridData3.horizontalAlignment = GridData.FILL;
		gridData3.grabExcessHorizontalSpace = true;
		gridData3.grabExcessVerticalSpace = true;
		gridData3.verticalAlignment = GridData.FILL;
		GridData gridData1 = new GridData();
		gridData1.horizontalAlignment = GridData.FILL;
		gridData1.grabExcessHorizontalSpace = true;
		gridData1.grabExcessVerticalSpace = true;
		gridData1.verticalAlignment = GridData.FILL;
		compositeImage = new Composite(this, SWT.NONE);
		compositeImage.setLayoutData(gridData1);
		compositeImage.setLayout(gridLayout2);
		labelImage = new Label(compositeImage, SWT.BORDER | SWT.CENTER);
		labelImage.setLayoutData(gridData3);
	}

	/**
	 * This method initializes compOptions	
	 *
	 */
	private void createCompOptions() {
		GridLayout gridLayout1 = new GridLayout();
		gridLayout1.horizontalSpacing = 0;
		gridLayout1.marginWidth = 0;
		gridLayout1.marginHeight = 0;
		gridLayout1.verticalSpacing = 0;
		GridData gridData2 = new GridData();
		gridData2.grabExcessHorizontalSpace = true;
		gridData2.verticalAlignment = GridData.CENTER;
		gridData2.horizontalAlignment = GridData.FILL;
		compOptions = new Composite(this, SWT.NONE);
		createToolBar();
		compOptions.setLayout(gridLayout1);
		compOptions.setLayoutData(gridData2);
	}

	/**
	 * This method initializes toolBar	
	 *
	 */
	private void createToolBar() {
		GridData gridData4 = new GridData();
		gridData4.horizontalAlignment = GridData.END;
		gridData4.grabExcessHorizontalSpace = true;
		gridData4.verticalAlignment = GridData.CENTER;
		toolBar = new ToolBar(compOptions, SWT.FLAT);
		toolBar.setLayoutData(gridData4);
		ToolItem buttonView = new ToolItem(toolBar, SWT.PUSH);
		buttonView.setImage(new Image(Display.getCurrent(), getClass().getResourceAsStream("/rdw/icon/icon_find.png")));
		buttonView.setToolTipText("Visualizar imagem");
		buttonView.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
			public void widgetSelected(org.eclipse.swt.events.SelectionEvent e) {
				showImage();
			}
		});
		ToolItem buttonRem = new ToolItem(toolBar, SWT.PUSH);
		buttonRem.setImage(new Image(Display.getCurrent(), getClass().getResourceAsStream("/rdw/icon/icon_delete.png")));
		buttonRem.setToolTipText("Excluir imagem");
		buttonRem.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
			public void widgetSelected(org.eclipse.swt.events.SelectionEvent e) {
				fileChooser.clear();
				removeImage();
			}
		});
		fileChooser.addSelectionListener(new SelectionAdapter(){
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				setImage();
			}
		});
		fileChooser.addKeyListener(new KeyAdapter(){
			@Override
			public void keyPressed(KeyEvent arg0) {
				if (arg0.keyCode == SWT.DEL)
					removeImage();
				else
					setImage();
			}
		});
		labelImage.addControlListener(new ControlAdapter(){
			@Override
			public void controlResized(ControlEvent arg0) {
				if (image != null){
					labelImage.setImage(
						Util.resizeProp(image, labelImage.getSize()));
				}else{
					labelImage.setImage(null);
				}
			}
		});
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
		Display display = Display.getDefault();
		Shell shell = new Shell(display);
		shell.setLayout(new FillLayout());
		shell.setSize(new Point(300, 200));
		new CImageChooser(shell, SWT.NONE);
		shell.open();

		while (!shell.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}
		display.dispose();
	}

	private void initialize() {
		GridLayout gridLayout = new GridLayout();
		gridLayout.horizontalSpacing = 0;
		gridLayout.marginHeight = 0;
		gridLayout.marginWidth = 0;
		gridLayout.verticalSpacing = 0;
		GridData gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;
		gridData.grabExcessVerticalSpace = false;
		gridData.heightHint = 17;
		gridData.verticalAlignment = GridData.FILL;
		fileChooser = new CFileChooser(this, SWT.BORDER);
		fileChooser.setFilterExtensions(FILTER_EXT);
		fileChooser.setFilterName(FILTER_NAMES);
		fileChooser.setToolTipText("");
		fileChooser.setLayoutData(gridData);
		createCompositeImage();
		this.setLayout(gridLayout);
		createCompOptions();
		for (int i = 0; i < toolBar.getItemCount(); i++) {
			toolBar.getItem(i).setEnabled(false);
		}
		setSize(new Point(300, 200));
	}

	private void showImage(){
		if (image == null)
			return;
		CImageShower shower = new CImageShower(getShell(),image);
		shower.open();
	}
	
	private void setImage(){
		File file = fileChooser.getFile();
		if (file != null)
			image = new Image(this.getDisplay(),file.getAbsolutePath());
		else
			image = null;
		if (image != null){
			labelImage.setImage(
				Util.resizeProp(image, labelImage.getSize()));
			for (int i = 0; i < toolBar.getItemCount(); i++) {
				toolBar.getItem(i).setEnabled(true);
			}
		}else{
			labelImage.setImage(null);
		}
	}
	
	private void removeImage(){
		image = null;
		labelImage.setImage(null);
		for (int i = 0; i < toolBar.getItemCount(); i++) {
			toolBar.getItem(i).setEnabled(false);
		}
	}
	
	public void setImage(Image image){
		if (image != null){
			labelImage.setImage(
				Util.resizeProp(image, labelImage.getSize()));
			for (int i = 0; i < toolBar.getItemCount(); i++) {
				toolBar.getItem(i).setEnabled(true);
			}
		}else{
			labelImage.setImage(null);
		}
		this.image = image;
	}
	
	public Image getImage(){
		return image;
	}
	public File getFile(){
		return fileChooser.getFile();
	}
	
	public void setNullable(boolean nullable){
		fileChooser.setNullable(nullable);
	}
	public boolean isNullable(){
		return fileChooser.isNullable();
	}
	
	public boolean validate(){
		return fileChooser.validate();
	}
	
	@Override
	public void setEnabled(boolean enabled){
		if (fileChooser != null)
			fileChooser.setEnabled(enabled);
		toolBar.getItem(1).setEnabled(enabled);
	}
	
	public void insert(String string){
		fileChooser.insert(string);
	}
	public void clearSelection(){
		fileChooser.clearSelection();
	}
	public void copy(){
		fileChooser.copy();
	}
	public void cut(){
		fileChooser.cut();
	}
	public void paste(){
		fileChooser.paste();
	}
	public int getCharCount(){
		return fileChooser.getCharCount();
	}
	public boolean isDoubleClickEnebled(){
		return fileChooser.isDoubleClickEnebled();
	}
	public void setDoubleClickEnabled(boolean doubleClick){
		fileChooser.setDoubleClickEnabled(doubleClick);
	}
	public char getEchoChar(){
		return fileChooser.getEchoChar();
	}
	public void setEchoChar(char echo){
		fileChooser.setEchoChar(echo);
	}
	@Override
	public boolean isEnabled(){
		return fileChooser.getEnabled();
	}
	public int getLineCount(){
		return fileChooser.getLineCount();
	}
	public int getLineHeight(){
		return fileChooser.getLineHeight();
	}
	public String getLineDelimiter(){
		return fileChooser.getLineDelimiter();
	}
	public String getMessage(){
		return fileChooser.getMessage();
	}
	public void setMessage(String message){
		fileChooser.setMessage(message);
	}
	public int getOrientation(){
		return fileChooser.getOrientation(); 
	}
	public void setOrientation(int orientation){
		fileChooser.setOrientation(orientation);
	}
	public String getSelectionText(){
		return fileChooser.getSelectionText();
	}
	public int getTabs(){
		return fileChooser.getTabs();
	}
	public void setTabs(int tabs){
		fileChooser.setTabs(tabs);
	}
	public Point getCaretLocation(){
		return fileChooser.getCaretLocation();
	}
	public int getCaretPosition(){
		return fileChooser.getCaretPosition();
	}
	public int getCaretLineNumber(){
		return fileChooser.getCaretLineNumber();
	}
	@Override
	public Point computeSize(int wHint, int hHint, boolean changed){
		return fileChooser.computeSize(wHint, hHint, changed);
	}
	@Override
	public Point computeSize(int wHint, int hHint){
		return fileChooser.computeSize(wHint, hHint);
	}
	@Override
	public Rectangle computeTrim(int x, int y, int width, int height){
		return fileChooser.computeTrim(x, y, width, height);
	}
	public void setRedraw(boolean redraw){
		fileChooser.setRedraw(redraw);
	}
	public void setfileChooser(String string){
		fileChooser.setText(string);
		fileChooser.setSelection(0);
	}
	public String getText(){
		return fileChooser.getText();
	}
	
	public void setTextlimit(int limit){
		fileChooser.setTextLimit(limit);
	}
	public int getTextlimit(){
		return fileChooser.getTextLimit();
	}
	@Override
	public void setToolTipText(String string){
		super.setToolTipText(string);
		fileChooser.setToolTipText(string);
	}
	@Override
	public String getToolTipText(){
		return fileChooser.getToolTipText();
	}
	
	public void setSelection(int start){
		fileChooser.setSelection(start);
	}
	public void setSelection(int start,int end){
		fileChooser.setSelection(start,end);
	}
	public void setSelection(Point selection){
		fileChooser.setSelection(selection);
	}
	public Point getSelection(){
		return fileChooser.getSelection();
	}
	
	//TODO Listeners
	@Override
	public void addListener(int eventType,Listener listener){
		fileChooser.addListener(eventType, listener);
	}
	@Override
	public void addControlListener(ControlListener listener){
		fileChooser.addControlListener(listener);
	}
	@Override
	public void addDisposeListener(DisposeListener listener){
		if (fileChooser == null)
			return;
		fileChooser.addDisposeListener(listener);
	}
	@Override
	public void addDragDetectListener(DragDetectListener listener){
		fileChooser.addDragDetectListener(listener);
	}
	@Override
	public void addFocusListener(FocusListener listener){
		fileChooser.addFocusListener(listener);
	}
	@Override
	public void addHelpListener(HelpListener listener){
		fileChooser.addHelpListener(listener);
	}
	@Override
	public void addKeyListener(KeyListener listener){
		fileChooser.addKeyListener(listener);
	}
	@Override
	public void addMenuDetectListener(MenuDetectListener listener){
		fileChooser.addMenuDetectListener(listener);
	}
	public void addModifyListener(ModifyListener listener){
		fileChooser.addModifyListener(listener);
	}
	@Override
	public void addMouseListener(MouseListener listener){
		fileChooser.addMouseListener(listener);
	}
	@Override
	public void addMouseMoveListener(MouseMoveListener listener){
		fileChooser.addMouseMoveListener(listener);
	}
	@Override
	public void addMouseTrackListener(MouseTrackListener listener){
		fileChooser.addMouseTrackListener(listener);
	}
	@Override
	public void addMouseWheelListener(MouseWheelListener listener){
		fileChooser.addMouseWheelListener(listener);
	}
	@Override
	public void addPaintListener(PaintListener listener){
		fileChooser.addPaintListener(listener);
	}
	public void addSelectionListener(SelectionListener listener){
		fileChooser.addSelectionListener(listener);
	}
	@Override
	public void addTraverseListener(TraverseListener listener){
		fileChooser.addTraverseListener(listener);
	}
	public void addVerifyListener(VerifyListener listener){
		fileChooser.addVerifyListener(listener);
	}
	
	@Override
	public void removeListener(int eventType,Listener listener){
		fileChooser.removeListener(eventType, listener);
	}
	@Override
	public void removeControlListener(ControlListener listener){
		fileChooser.removeControlListener(listener);
	}
	@Override
	public void removeDisposeListener(DisposeListener listener){
		fileChooser.removeDisposeListener(listener);
	}
	@Override
	public void removeDragDetectListener(DragDetectListener listener){
		fileChooser.removeDragDetectListener(listener);
	}
	@Override
	public void removeFocusListener(FocusListener listener){
		fileChooser.removeFocusListener(listener);
	}
	@Override
	public void removeHelpListener(HelpListener listener){
		fileChooser.removeHelpListener(listener);
	}
	@Override
	public void removeKeyListener(KeyListener listener){
		fileChooser.removeKeyListener(listener);
	}
	@Override
	public void removeMenuDetectListener(MenuDetectListener listener){
		fileChooser.removeMenuDetectListener(listener);
	}
	public void removeModifyListener(ModifyListener listener){
		fileChooser.removeModifyListener(listener);
	}
	@Override
	public void removeMouseListener(MouseListener listener){
		fileChooser.removeMouseListener(listener);
	}
	@Override
	public void removeMouseMoveListener(MouseMoveListener listener){
		fileChooser.removeMouseMoveListener(listener);
	}
	@Override
	public void removeMouseTrackListener(MouseTrackListener listener){
		fileChooser.removeMouseTrackListener(listener);
	}
	@Override
	public void removeMouseWheelListener(MouseWheelListener listener){
		fileChooser.removeMouseWheelListener(listener);
	}
	@Override
	public void removePaintListener(PaintListener listener){
		fileChooser.removePaintListener(listener);
	}
	public void removeSelectionListener(SelectionListener listener){
		fileChooser.removeSelectionListener(listener);
	}
	@Override
	public void removeTraverseListener(TraverseListener listener){
		fileChooser.removeTraverseListener(listener);
	}
	public void removeVerifyListener(VerifyListener listener){
		fileChooser.removeVerifyListener(listener);
	}
	public boolean isEmpty(){
		return (getText().trim().length() == 0);
	}
	public void clear(){
		fileChooser.setText("");
	}
	
	//Styles
	@Override
	public void setForeground(Color color){
		fileChooser.setForeground(color);
	}
	@Override
	public Color getForeground(){
		return fileChooser.getForeground();
	}
	@Override
	public void setBackground(Color color){
		fileChooser.setBackground(color);
	}
	@Override
	public Color getBackground(){
		return fileChooser.getBackground();
	}
	@Override
	public void setFont(Font font){
		fileChooser.setFont(font);
	}
	@Override
	public Font getFont(){
		return fileChooser.getFont();
	}
	
	public void setValidator(Validator validator){
		fileChooser.setValidator(validator);
	}
	
	@SuppressWarnings("unused")
	public void setFile(File file){
		if (file != null && (file.exists())){
			setfileChooser(file.getAbsolutePath());
			if (file != null)
				image = new Image(this.getDisplay(),file.getAbsolutePath());
			else
				image = null;
			if (image != null){
				labelImage.setImage(
					Util.resizeProp(image, labelImage.getSize()));
				for (int i = 0; i < toolBar.getItemCount(); i++) {
					toolBar.getItem(i).setEnabled(true);
				}
			}else{
				labelImage.setImage(null);
			}
		}else
			clear();
	}
}
