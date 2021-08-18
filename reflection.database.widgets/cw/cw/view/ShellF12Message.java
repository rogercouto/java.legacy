package cw.view;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.TraverseEvent;
import org.eclipse.swt.events.TraverseListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;

public class ShellF12Message {

	protected Shell parent = null;
	protected Shell shell = null;  //  @jve:decl-index=0:visual-constraint="10,10"
	protected Label labelMessage = null;
	protected Composite compOk = null;
	protected Button buttonOk = null;
	private Label labelTitle = null;
	
	public ShellF12Message() {
		createShell();
		inicialize();
	}
	public ShellF12Message(Shell parent) {
		this.parent = parent;
		createShell();
		inicialize();
	}
	
	public void open(){
		Display display = Display.getDefault();
		shell.pack();
		centralize(shell);
		shell.open();
		while (!shell.isDisposed()){
			if (!display.readAndDispatch()){
				display.sleep();
			}
		}
	}
	
	/**
	 * This method initializes sShell
	 */
	private void createShell() {
		GridData gridData3 = new GridData();
		gridData3.horizontalAlignment = GridData.FILL;
		gridData3.grabExcessHorizontalSpace = true;
		gridData3.horizontalSpan = 2;
		gridData3.verticalAlignment = GridData.CENTER;
		GridData gridData1 = new GridData();
		gridData1.horizontalSpan = 2;
		gridData1.verticalAlignment = GridData.END;
		gridData1.heightHint = -1;
		gridData1.horizontalAlignment = GridData.FILL;
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 2;
		gridLayout.verticalSpacing = 0;
		gridLayout.marginWidth = 0;
		gridLayout.marginHeight = 0;
		gridLayout.horizontalSpacing = 0;
		if (parent == null)
			shell = new Shell(SWT.NO_TRIM | SWT.APPLICATION_MODAL);
		else 
			shell = new Shell(parent,SWT.NO_TRIM | SWT.APPLICATION_MODAL);
		shell.setText("Message");
		shell.setLayout(gridLayout);
		shell.setMinimumSize(new Point(248, 70));
		labelTitle = new Label(shell, SWT.NONE);
		labelTitle.setText("Message");
		labelTitle.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_LIST_SELECTION));
		labelTitle.setForeground(new Color(Display.getCurrent(), 255, 255, 255));
		labelTitle.setFont(new Font(Display.getDefault(), "Tahoma", 10, SWT.BOLD));
		labelTitle.setLayoutData(gridData3);
		labelMessage = new Label(shell, SWT.CENTER);
		labelMessage.setText("");
		labelMessage.setFont(new Font(Display.getDefault(), "Tahoma", 10, SWT.NORMAL));
		labelMessage.setLayoutData(gridData1);
		createCompOk();
	}

	/**
	 * This method initializes compOk	
	 *
	 */
	private void createCompOk() {
		GridData gridData2 = new GridData();
		gridData2.widthHint = 75;
		gridData2.verticalAlignment = GridData.CENTER;
		gridData2.grabExcessHorizontalSpace = true;
		gridData2.horizontalAlignment = GridData.CENTER;
		GridData gridData = new GridData();
		gridData.horizontalSpan = 2;
		gridData.verticalAlignment = GridData.CENTER;
		gridData.grabExcessHorizontalSpace = true;
		gridData.horizontalAlignment = GridData.FILL;
		compOk = new Composite(shell, SWT.NONE);
		compOk.setLayout(new GridLayout());
		compOk.setLayoutData(gridData);
		buttonOk = new Button(compOk, SWT.NONE);
		buttonOk.setText("F12: Ok");
		buttonOk.setFont(new Font(Display.getDefault(), "Tahoma", 10, SWT.NORMAL));
		buttonOk.setLayoutData(gridData2);
	}
	
	public void setText(String text){
		labelTitle.setText(text);
	}
	
	public void setMessage(String message){
		labelMessage.setText(message);
	}
	
	private void inicialize(){
		compOk.setEnabled(false);
		buttonOk.addSelectionListener(new SelectionAdapter (){
			public void widgetSelected(SelectionEvent e){
				shell.close();
			}
		});
		shell.addKeyListener(new KeyAdapter(){
			@Override
			public void keyPressed(KeyEvent e) {
				if (e.keyCode == SWT.F12)
					shell.close();
			}
		});
		shell.addTraverseListener(new TraverseListener(){
			@Override
			public void keyTraversed(TraverseEvent e) {
				if (e.keyCode == SWT.ESC){
					e.doit = false;
				}
			}
		});
	}
	
	/**
	 * Função que posiciona um shell no centro da tela
	 * @param shell
	 */
	private static synchronized void centralize(Shell shell) {  
		try {  
			if (!shell.isDisposed()) {  
				Rectangle r = Display.getCurrent().getClientArea();  
				int sW = r.width; // largura da tela  
				int sH = r.height; // altura da tela  
				
				r = shell.getBounds();  
				int w = r.width; // largura da janela  
				int h = r.height; // altura da janela  
				
				int x = (sW - w) / 2; // novo ponto x  
				int y = (sH - h) / 2; // novo ponto y  
				
				shell.setLocation(x, y);  
			}  
		} catch (Exception e) {  
			e.printStackTrace();  
		}  
	}
}
