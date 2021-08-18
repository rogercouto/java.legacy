package cw.view;

import java.awt.Toolkit;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

import cw.tools.Screen;

public class ShellQuestion {

	private Shell parent = null;
	private Shell shell = null;  //  @jve:decl-index=0:visual-constraint="10,10"
	private boolean exclude = false;
	private Composite composite1 = null;
	private Label icon = null;
	private Label label = null;
	private Composite composite2 = null;
	private Button buttonOk = null;
	private Button buttonCancelar = null;
	
	public ShellQuestion() {
		super();
		createShell();
	}

	public ShellQuestion(Shell parent) {
		super();
		this.parent = parent;
		createShell();
	}

	/**
	 * This method initializes shell
	 */
	private void createShell() {
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 1;
		if (parent == null)
			shell = new Shell(SWT.TITLE | SWT.APPLICATION_MODAL);
		else
			shell = new Shell(parent,SWT.TITLE | SWT.APPLICATION_MODAL);
		shell.setText("Confirmação");
		shell.setLayout(gridLayout);
		createComposite1();
		createComposite2();
	}

	public boolean open(){
		Display display = shell.getDisplay();
		shell.pack();
		Screen.centralize(shell, parent);
		buttonCancelar.setFocus();
		Toolkit.getDefaultToolkit().beep();
		shell.open();
		while (!shell.isDisposed()){
			if (!display.readAndDispatch()){
				display.sleep();
			}
		}
		return exclude;
	}

	/**
	 * This method initializes composite1	
	 *
	 */
	private void createComposite1() {
		GridData gridData1 = new GridData();
		gridData1.horizontalAlignment = GridData.CENTER;
		gridData1.grabExcessHorizontalSpace = true;
		gridData1.verticalAlignment = GridData.CENTER;
		GridData gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;
		gridData.verticalAlignment = GridData.CENTER;
		GridLayout gridLayout1 = new GridLayout();
		gridLayout1.numColumns = 2;
		composite1 = new Composite(shell, SWT.NONE);
		composite1.setLayout(gridLayout1);
		composite1.setLayoutData(gridData);
		icon = new Label(composite1, SWT.NONE);
		icon.setImage(shell.getDisplay().getSystemImage(SWT.ICON_QUESTION));
		label = new Label(composite1, SWT.NONE);
		label.setText("Confirma exclusão?");
		label.setLayoutData(gridData1);
	}

	/**
	 * This method initializes composite2	
	 *
	 */
	private void createComposite2() {
		GridData gridData4 = new GridData();
		gridData4.widthHint = 80;
		gridData4.horizontalAlignment = GridData.BEGINNING;
		gridData4.verticalAlignment = GridData.CENTER;
		gridData4.grabExcessHorizontalSpace = true;
		GridData gridData3 = new GridData();
		gridData3.widthHint = 80;
		gridData3.horizontalAlignment = GridData.END;
		gridData3.verticalAlignment = GridData.CENTER;
		gridData3.grabExcessHorizontalSpace = true;
		GridData gridData2 = new GridData();
		gridData2.grabExcessHorizontalSpace = true;
		gridData2.verticalAlignment = GridData.CENTER;
		gridData2.horizontalAlignment = GridData.FILL;
		GridLayout gridLayout2 = new GridLayout();
		gridLayout2.numColumns = 2;
		composite2 = new Composite(shell, SWT.NONE);
		composite2.setLayout(gridLayout2);
		composite2.setLayoutData(gridData2);
		buttonOk = new Button(composite2, SWT.NONE);
		buttonOk.setText("Sim");
		buttonOk.setLayoutData(gridData3);
		buttonOk.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
			public void widgetSelected(org.eclipse.swt.events.SelectionEvent e) {
				exclude = true;
				shell.close();
			}
		});
		buttonCancelar = new Button(composite2, SWT.NONE);
		buttonCancelar.setText("Não");
		buttonCancelar.setLayoutData(gridData4);
		buttonCancelar.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
			public void widgetSelected(org.eclipse.swt.events.SelectionEvent e) {
				exclude = false;
				shell.close();
			}
		});
	}
	
	public void setMessage(String message){
		label.setText(message);
	}
	
	public void setTitle(String title){
		shell.setText(title);
	}
	
}
