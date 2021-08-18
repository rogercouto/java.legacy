package cw.controls.combobox;

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
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Listener;

import cw.interfaces.CControl;

public class CComboBox extends Composite implements CControl{

	protected Combo combo = null;
	private int style;
	private boolean nullable = true;

	public CComboBox(Composite parent, int style) {
		super(parent, SWT.NONE);
		this.style = style;
		initialize();
	}

	private void initialize() {
		GridLayout gridLayout = new GridLayout();
		gridLayout.horizontalSpacing = 0;
		gridLayout.marginWidth = 0;
		gridLayout.marginHeight = 0;
		gridLayout.verticalSpacing = 0;
		gridLayout.numColumns = 2;
		createCombo();
		this.setLayout(gridLayout);
	}

	/**
	 * This method initializes combo	
	 *
	 */
	private void createCombo() {
		GridData gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;
		gridData.verticalAlignment = GridData.CENTER;
		combo = new Combo(this, style);
		combo.setLayoutData(gridData);
		combo.addKeyListener(new org.eclipse.swt.events.KeyAdapter() {
			@Override
			public void keyPressed(org.eclipse.swt.events.KeyEvent e) {
				if (e.keyCode == SWT.DEL && (style == SWT.READ_ONLY))
					combo.deselectAll();
			}
		});
	}
	
	public boolean validate(){
		if (!nullable && (combo.getSelectionIndex() < 0))
			return false;
		return true;
	}

	@Override
	public void setEnabled(boolean enabled){
		combo.setBackground(enabled?
				this.getDisplay().getSystemColor(SWT.COLOR_WHITE):
					this.getDisplay().getSystemColor(SWT.COLOR_WIDGET_BACKGROUND));
		super.setEnabled(enabled);
	}
	
	public void add(String string){
		combo.add(string);
	}
	public void add(String string, int index){ 
		combo.add(string, index);
	}
	public void add(String[] strings){
		for (String string : strings) {
			combo.add(string);
		}
	}
	
	public void setItem(int index,String string){
		combo.setItem(index, string);
	}
	public void remove(int index){
		combo.remove(index);
	}
	public void remove(int start,int end){
		combo.remove(start,end);
	}
	public void remove(String string){
		combo.remove(string);
	}
	public void removeAll(){
		combo.removeAll();
	}
	public void deselect(int index){
		combo.deselect(index);
	}
	public void deselectAll(){
		combo.deselectAll();
	}
	public String getItem(int index){
		return combo.getItem(index);
	}
	public int getItemCount(){
		return combo.getItemCount();
	}
	public int getItemHeight() {
		return combo.getItemHeight();
	}
	public String[] getItems(){
		return combo.getItems();
	}
	public int getorientation(){
		return combo.getOrientation();
	}
	public int getSelectionIndex(){
		return combo.getSelectionIndex();
	}
	public int getTextHeigth(){
		return combo.getTextHeight();
	}
	public int getVisibleItemCount(){
		return combo.getVisibleItemCount();
	}
	public int indexOf(String string){
		return combo.indexOf(string);
	}
	public int indexOf(String string,int start){
		return combo.indexOf(string,start);
	}
	public void select(int index){
		combo.select(index);
	}
	public void setVisibleItemCount(int count){
		combo.setVisibleItemCount(count);
	}
	//TODO Text Getters - Setters
	
	public void clearSelection(){
		combo.clearSelection();
	}
	public void copy(){
		combo.copy();
	}
	public void cut(){
		combo.cut();
	}
	public void paste(){
		combo.paste();
	}
	@Override
	public boolean isEnabled(){
		return combo.getEnabled();
	}
	public int getOrientation(){
		return combo.getOrientation(); 
	}
	public void setOrientation(int orientation){
		combo.setOrientation(orientation);
	}
	@Override
	public Point computeSize(int wHint, int hHint, boolean changed){
		return combo.computeSize(wHint, hHint, changed);
	}
	@Override
	public Point computeSize(int wHint, int hHint){
		return combo.computeSize(wHint, hHint);
	}
	@Override
	public Rectangle computeTrim(int x, int y, int width, int height){
		return combo.computeTrim(x, y, width, height);
	}
	public void setRedraw(boolean redraw){
		combo.setRedraw(redraw);
	}
	/**
	 * M�todo que seta o texto do campo
	 * @param string
	 */
	public void setText(String string){
		combo.setText(string);
	}
	/**
	 * M�todo que retorna o texto do campo
	 * @return
	 */
	public String getText(){
		return combo.getText();
	}
	
	public void setTextLimit(int limit){
		combo.setTextLimit(limit);
	}
	public int getTextLimit(){
		return combo.getTextLimit();
	}
	@Override
	public void setToolTipText(String string){
		super.setToolTipText(string);
		combo.setToolTipText(string);
	}
	@Override
	public String getToolTipText(){
		return combo.getToolTipText();
	}
	
	public void setSelection(Point selection){
		combo.setSelection(selection);
	}
	public Point getSelection(){
		return combo.getSelection();
	}
	
	//TODO Listeners
	@Override
	public void addListener(int eventType,Listener listener){
		combo.addListener(eventType, listener);
	}
	@Override
	public void addControlListener(ControlListener listener){
		combo.addControlListener(listener);
	}
	@Override
	public void addDisposeListener(DisposeListener listener){
		if (combo == null)
			return;
		combo.addDisposeListener(listener);
	}
	@Override
	public void addDragDetectListener(DragDetectListener listener){
		combo.addDragDetectListener(listener);
	}
	@Override
	public void addFocusListener(FocusListener listener){
		combo.addFocusListener(listener);
	}
	@Override
	public void addHelpListener(HelpListener listener){
		combo.addHelpListener(listener);
	}
	@Override
	public void addKeyListener(KeyListener listener){
		combo.addKeyListener(listener);
	}
	@Override
	public void addMenuDetectListener(MenuDetectListener listener){
		combo.addMenuDetectListener(listener);
	}
	public void addModifyListener(ModifyListener listener){
		combo.addModifyListener(listener);
	}
	@Override
	public void addMouseListener(MouseListener listener){
		combo.addMouseListener(listener);
	}
	@Override
	public void addMouseMoveListener(MouseMoveListener listener){
		combo.addMouseMoveListener(listener);
	}
	@Override
	public void addMouseTrackListener(MouseTrackListener listener){
		combo.addMouseTrackListener(listener);
	}
	@Override
	public void addMouseWheelListener(MouseWheelListener listener){
		combo.addMouseWheelListener(listener);
	}
	@Override
	public void addPaintListener(PaintListener listener){
		combo.addPaintListener(listener);
	}
	public void addSelectionListener(SelectionListener listener){
		combo.addSelectionListener(listener);
	}
	@Override
	public void addTraverseListener(TraverseListener listener){
		combo.addTraverseListener(listener);
	}
	public void addVerifyListener(VerifyListener listener){
		combo.addVerifyListener(listener);
	}
	
	@Override
	public void removeListener(int eventType,Listener listener){
		combo.removeListener(eventType, listener);
	}
	@Override
	public void removeControlListener(ControlListener listener){
		combo.removeControlListener(listener);
	}
	@Override
	public void removeDisposeListener(DisposeListener listener){
		combo.removeDisposeListener(listener);
	}
	@Override
	public void removeDragDetectListener(DragDetectListener listener){
		combo.removeDragDetectListener(listener);
	}
	@Override
	public void removeFocusListener(FocusListener listener){
		combo.removeFocusListener(listener);
	}
	@Override
	public void removeHelpListener(HelpListener listener){
		combo.removeHelpListener(listener);
	}
	@Override
	public void removeKeyListener(KeyListener listener){
		combo.removeKeyListener(listener);
	}
	@Override
	public void removeMenuDetectListener(MenuDetectListener listener){
		combo.removeMenuDetectListener(listener);
	}
	public void removeModifyListener(ModifyListener listener){
		combo.removeModifyListener(listener);
	}
	@Override
	public void removeMouseListener(MouseListener listener){
		combo.removeMouseListener(listener);
	}
	@Override
	public void removeMouseMoveListener(MouseMoveListener listener){
		combo.removeMouseMoveListener(listener);
	}
	@Override
	public void removeMouseTrackListener(MouseTrackListener listener){
		combo.removeMouseTrackListener(listener);
	}
	@Override
	public void removeMouseWheelListener(MouseWheelListener listener){
		combo.removeMouseWheelListener(listener);
	}
	@Override
	public void removePaintListener(PaintListener listener){
		combo.removePaintListener(listener);
	}
	public void removeSelectionListener(SelectionListener listener){
		combo.removeSelectionListener(listener);
	}
	@Override
	public void removeTraverseListener(TraverseListener listener){
		combo.removeTraverseListener(listener);
	}
	public void removeVerifyListener(VerifyListener listener){
		combo.removeVerifyListener(listener);
	}
	
	/**
	 * Verifica se o texto est� vazio
	 * @return
	 */
	public boolean isEmpty(){
		return (getText().trim().length() == 0);
	}
	
	//Styles
	@Override
	public void setForeground(Color color){
		combo.setForeground(color);
	}
	@Override
	public Color getForeground(){
		return combo.getForeground();
	}
	@Override
	public void setBackground(Color color){
		combo.setBackground(color);
	}
	@Override
	public Color getBackground(){
		return combo.getBackground();
	}
	@Override
	public void setFont(Font font){
		combo.setFont(font);
	}
	@Override
	public Font getFont(){
		return combo.getFont();
	}

	public boolean isNullable() {
		return nullable;
	}

	public void setNullable(boolean nullable) {
		this.nullable = nullable;
	}
	
}  //  @jve:decl-index=0:visual-constraint="10,10"
