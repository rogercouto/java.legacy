package rdw.find;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;

import rdw.table.DTable;
import cw.tools.Screen;
import org.eclipse.wb.swt.SWTResourceManager;

public class ShellFind extends org.eclipse.swt.widgets.Dialog {

	protected static final Color NORM_COLOR = Display.getDefault().getSystemColor(SWT.COLOR_BLACK);  //  @jve:decl-index=0:
	protected static final Color ERROR_COLOR = Display.getDefault().getSystemColor(SWT.COLOR_RED);  //  @jve:decl-index=0:
	protected static final Font NORM_FONT = new Font(Display.getDefault(), "Tahoma", 8, SWT.NORMAL);  //  @jve:decl-index=0:
	protected static final Font ERROR_FONT = new Font(Display.getDefault(), "Tahoma", 8, SWT.BOLD);  //  @jve:decl-index=0:
	
	protected Shell shell = null;  //  @jve:decl-index=0:visual-constraint="10,10"
	protected Composite compBusca = null;
	protected ToolBar findToolBar = null;
	protected Text textBusca = null;
	protected Label labelImage = null;
	protected DTable table = null;
	protected Composite compButtons = null;
	protected Label labelMessage = null;
	protected Menu popUp = null;
	protected ToolItem buttonBusca;
	protected Button buttonInsert = null;
	protected Button buttonSelect = null;
	protected Button buttonCancel = null;
	protected Label filler = null;
	protected Button buttonUpdate;
	
	public ShellFind(Shell parent) {
		super(parent);
		createShell();
	}

	/**
	 * This method initializes shell
	 */
	private void createShell() {
		try {
			GridData gridData4 = new GridData();
			gridData4.horizontalAlignment = GridData.FILL;
			gridData4.grabExcessHorizontalSpace = true;
			gridData4.grabExcessVerticalSpace = true;
			gridData4.verticalAlignment = GridData.FILL;
			shell = new Shell(getParent(),SWT.APPLICATION_MODAL | SWT.DIALOG_TRIM);
			shell.setText("Busca");
			createCompBusca();
			shell.setLayout(new GridLayout());
			shell.setSize(new Point(450, 300));
			table = new DTable(shell, SWT.BORDER | SWT.FULL_SELECTION);
			table.setLinesVisible(false);
			table.setLayoutData(gridData4);
			popUp = new Menu(shell,SWT.POP_UP);
			createCompButtons();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * This method initializes compBusca	
	 *
	 */
	private void createCompBusca() {
		GridData gridData2 = new GridData();
		gridData2.horizontalAlignment = GridData.BEGINNING;
		gridData2.grabExcessVerticalSpace = true;
		gridData2.widthHint = 20;
		gridData2.verticalAlignment = GridData.FILL;
		GridData gridData1 = new GridData();
		gridData1.horizontalAlignment = GridData.FILL;
		gridData1.grabExcessVerticalSpace = false;
		gridData1.grabExcessHorizontalSpace = true;
		gridData1.verticalAlignment = GridData.CENTER;
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 3;
		gridLayout.verticalSpacing = 0;
		gridLayout.marginWidth = 0;
		gridLayout.marginHeight = 0;
		gridLayout.horizontalSpacing = 0;
		GridData gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;
		gridData.verticalAlignment = GridData.CENTER;
		compBusca = new Composite(shell, SWT.BORDER);
		compBusca.setBackground(new Color(Display.getCurrent(), 255, 255, 255));
		labelImage = new Label(compBusca, SWT.RIGHT);
		labelImage.setImage(new Image(Display.getCurrent(), getClass().getResourceAsStream("/rdw/icon/icon_find.png")));
		labelImage.setBackground(new Color(Display.getCurrent(), 255, 255, 255));
		labelImage.setLayoutData(gridData2);
		labelImage.addMouseListener(new org.eclipse.swt.events.MouseAdapter() {
			public void mouseDown(org.eclipse.swt.events.MouseEvent e) {
				textBusca.setSelection(textBusca.getText().length());
				textBusca.setFocus();
			}
		});
		textBusca = new Text(compBusca, SWT.NONE);
		textBusca.setFont(NORM_FONT);
		textBusca.setForeground(NORM_COLOR);
		textBusca.setLayoutData(gridData1);
		createFindToolBar();
		compBusca.setLayout(gridLayout);
		compBusca.setLayoutData(gridData);
	}

	/**
	 * This method initializes findToolBar	
	 *
	 */
	private void createFindToolBar() {
		GridData gridData3 = new GridData();
		gridData3.heightHint = -1;
		findToolBar = new ToolBar(compBusca, SWT.FLAT);
		findToolBar.setLayoutData(gridData3);
		buttonBusca = new ToolItem(findToolBar, SWT.DROP_DOWN);
		buttonBusca.setImage(new Image(Display.getCurrent(), getClass().getResourceAsStream("/rdw/icon/icon_find.png")));
		buttonBusca.setToolTipText("Buscar");
	}

	/**
	 * This method initializes compButtons	
	 *
	 */
	private void createCompButtons() {
		GridData gridData9 = new GridData();
		gridData9.widthHint = 85;
		GridData gridData8 = new GridData();
		gridData8.widthHint = 85;
		GridData gridData6 = new GridData();
		gridData6.grabExcessHorizontalSpace = true;
		gridData6.verticalAlignment = GridData.CENTER;
		gridData6.horizontalAlignment = GridData.FILL;
		GridData gridData5 = new GridData();
		gridData5.horizontalAlignment = GridData.FILL;
		gridData5.grabExcessHorizontalSpace = true;
		gridData5.verticalAlignment = GridData.CENTER;
		GridLayout gridLayout1 = new GridLayout();
		gridLayout1.numColumns = 8;
		gridLayout1.verticalSpacing = 0;
		gridLayout1.marginWidth = 0;
		gridLayout1.marginHeight = 0;
		gridLayout1.horizontalSpacing = 2;
		compButtons = new Composite(shell, SWT.NONE);
		compButtons.setLayout(gridLayout1);
		compButtons.setLayoutData(gridData6);
		new Label(compButtons, SWT.NONE);
		buttonInsert = new Button(compButtons, SWT.NONE);
		buttonInsert.setToolTipText("Inserir");
		buttonInsert.setImage(SWTResourceManager.getImage(ShellFind.class, "/rdw/icon/icon_ok_add.png"));
		buttonInsert.setVisible(false);
		buttonUpdate = new Button(compButtons, SWT.NONE);
		buttonUpdate.setToolTipText("Editar");
		buttonUpdate.setVisible(false);
		buttonUpdate.setImage(SWTResourceManager.getImage(ShellFind.class, "/rdw/icon/icon_edit.png"));
		GridData gridData10 = new GridData();
		gridData10.widthHint = 10;
		filler = new Label(compButtons, SWT.NONE);
		filler.setText("");
		filler.setLayoutData(gridData10);
		labelMessage = new Label(compButtons, SWT.NONE);
		labelMessage.setText("Nenhum registro encontrado!");
		labelMessage.setVisible(false);
		labelMessage.setLayoutData(gridData5);
		new Label(compButtons, SWT.NONE);
		buttonSelect = new Button(compButtons, SWT.NONE);
		buttonSelect.setImage(new Image(Display.getCurrent(), getClass().getResourceAsStream("/rdw/icon/icon_accept.png")));
		buttonSelect.setEnabled(false);
		buttonSelect.setLayoutData(gridData8);
		buttonSelect.setText("Selecionar");
		buttonCancel = new Button(compButtons, SWT.NONE);
		buttonCancel.setImage(new Image(Display.getCurrent(), getClass().getResourceAsStream("/rdw/icon/icon_cancel.png")));
		buttonCancel.setLayoutData(gridData9);
		buttonCancel.setText("Cancelar");
	}

	/**
	 * Troca a cor e o tipo da fonte quando a buttonBusca não encontra nenhum resultado
	 * @param error - especifica se houve erro na buttonBusca (buttonBusca sem resultads)
	 */
	protected void haveResults(boolean error){
		if (!error){
			textBusca.setFont(ERROR_FONT);
			textBusca.setForeground(ERROR_COLOR);
		}else{
			textBusca.setFont(NORM_FONT);
			textBusca.setForeground(NORM_COLOR);
		}
		labelMessage.setVisible(!error);
		buttonSelect.setEnabled(error);
	}

	protected void modify(){
		labelMessage.setVisible(false);
		buttonSelect.setEnabled(false);
		textBusca.setFont(NORM_FONT);
		textBusca.setForeground(NORM_COLOR);
	}
	
	//TODO Filters
	
	public void addFilter(String filter){
		table.addFilter(filter);
	}
	public void addDFilter(int column,Object value){
		table.addDFilter(column, value);
	}
	public void addDFilter(int column,Object arg0,Object arg1){
		table.addDFilter(column, arg0, arg1);
	}
	public void setFilter(int index,String filter){
		table.setFilter(index, filter);
	}
	public void setDFilter(int index,int column,Object value){
		table.setDFilter(index, column, value);
	}
	public void setDFilter(int index,int column,Object arg0,Object arg1){
		table.setDFilter(index, column, arg0, arg1);
	}
	public void removeFilter(int index){
		table.removeFilter(index);
	}
	public void removeFilters(){
		table.removeFilters();
	}
	public void removeDFilter(int index){
		table.removeDFilter(index);
	}
	public void removeDFilters(){
		table.removeDFilters();
	}
	public void hideColumn(int column){
		table.hideColumn(column);
	}
	public void setColumnPattern(int column,String pattern){
		table.setColumnPattern(column, pattern);
	}
	public void setColumnDivider(int column,char divider){
		table.setColumnDivider(column, divider);
	}
	public void setColumnNullValue(int column,String nullValue){
		table.setColumnNullValue(column, nullValue);
	}
	/**
	 * Troca a mensagem do campo de buttonBusca
	 * @param message
	 */
	public void setMessage(String message){
		labelMessage.setText(message);
	}

	public void setFindlabelIcon(Image image){
		labelImage.setImage(image);
	}
	public void setFindIcon(Image image){
		buttonBusca.setImage(image);
	}
	public void setInsertIcon(Image image){
		buttonInsert.setImage(image);
	}
	public void setSelectIcon(Image image){
		buttonSelect.setImage(image);
	}
	public void setCancelIcon(Image image){
		buttonCancel.setImage(image);
	}
	public void setImage(Image image){
		shell.setImage(image);
	}

	public Object open() {
		Screen.centralize(shell, getParent());
		shell.open();
		shell.layout();
		Display display = getParent().getDisplay();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
		return null;
	}
	
}
