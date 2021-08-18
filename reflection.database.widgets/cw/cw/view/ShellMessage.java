package cw.view;

import java.awt.Toolkit;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.TraverseEvent;
import org.eclipse.swt.events.TraverseListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;

import cw.tools.Screen;

public class ShellMessage {

	private Shell parent = null;
	private Shell shell = null;  //  @jve:decl-index=0:visual-constraint="10,10"
	private Label labelMessage = null;
	private Composite compOk = null;
	private Button buttonOk = null;
	private Label labelIcon = null;
	private int id = 0;
	
	public ShellMessage() {
		super();
		createShell();
		inicialize();
	}
	public ShellMessage(Shell parent) {
		super();
		this.parent = parent;
		createShell();
		inicialize();
	}
	
	public void open(){
		Display display = Display.getDefault();
		shell.pack();
		Screen.centralize(shell,parent);
		shell.open();
		if (id == SWT.ICON_WARNING || id == SWT.ICON_ERROR || id == SWT.ICON_WORKING || id == SWT.ICON_INFORMATION )
			Toolkit.getDefaultToolkit().beep();
		
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
		GridData gridData1 = new GridData();
		gridData1.horizontalSpan = 2;
		gridData1.verticalAlignment = GridData.CENTER;
		gridData1.heightHint = -1;
		gridData1.grabExcessVerticalSpace = false;
		gridData1.grabExcessHorizontalSpace = true;
		gridData1.horizontalAlignment = GridData.FILL;
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 3;
		gridLayout.marginWidth = 10;
		gridLayout.marginHeight = 10;
		gridLayout.horizontalSpacing = 10;
		if (parent == null)
			shell = new Shell(SWT.TITLE | SWT.APPLICATION_MODAL);
		else 
			shell = new Shell(parent,SWT.TITLE | SWT.APPLICATION_MODAL);
		shell.setText("Message");
		shell.setLayout(gridLayout);
		labelIcon = new Label(shell, SWT.VERTICAL);
		labelIcon.setText("");
		labelMessage = new Label(shell, SWT.LEFT);
		labelMessage.setText("");
		labelMessage.setLayoutData(gridData1);
		createCompOk();
	}

	public void setText(String text){
		shell.setText(text);
	}
	public void setMessage(String message){
		labelMessage.setText(message);
	}
	public void setImage(int id){
		this.id = id;
		labelIcon.setImage(shell.getDisplay().getSystemImage(id));
	}
	public void setImage(Image image){
		labelIcon.setImage(image);
	}
	
	private void inicialize(){
		buttonOk.addSelectionListener(new SelectionAdapter (){
			public void widgetSelected(SelectionEvent e){
				shell.close();
			}
		});
		shell.addTraverseListener(new TraverseListener(){
			@Override
			public void keyTraversed(TraverseEvent e) {
				if (e.keyCode == SWT.ESC){
					shell.close();
				}
			}
		});
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
		gridData.horizontalSpan = 3;
		gridData.verticalAlignment = GridData.CENTER;
		gridData.grabExcessHorizontalSpace = false;
		gridData.horizontalAlignment = GridData.FILL;
		compOk = new Composite(shell, SWT.NONE);
		compOk.setLayout(new GridLayout());
		compOk.setLayoutData(gridData);
		buttonOk = new Button(compOk, SWT.NONE);
		buttonOk.setText("Ok");
		buttonOk.setLayoutData(gridData2);
	}
	
	public static void main(String[] args) {
		/*
		ShellMessage message = new ShellMessage();
		message.setText("Título");
		message.setMessage("Mensagem de teste agora vai"+'\r'+"Porra!!!!");
		message.setImage(SWT.ICON_WARNING);
		message.open();
		 */
		MessageBox box = new MessageBox(new Shell(),SWT.ICON_WARNING);
		box.open();
	}
}
