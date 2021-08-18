package cw.view;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.TraverseEvent;
import org.eclipse.swt.events.TraverseListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.wb.swt.SWTResourceManager;

import cw.tools.Screen;

public class ShellImputSmart extends Dialog{

	private Shell shell = null;  //  @jve:decl-index=0:visual-constraint="10,10"
	private Label labelMessage = null;
	private Text textInput = null;
	private Button buttonOk = null;
	
	private String result;

	public ShellImputSmart(Shell parent) {
		super(parent);
		createShell();
		inicialize();
	}
	
	public String open(){
		Display display = getParent().getDisplay();
		Screen.centralize(shell,getParent());
		textInput.setSelection(textInput.getText().length());
		textInput.setFocus();
		shell.pack();
		shell.open();
		while (!shell.isDisposed()){
			if (!display.readAndDispatch()){
				display.sleep();
			}
		}
		return result;
	}
	
	/**
	 * This method initializes sShell
	 */
	private void createShell() {
		GridData gridData2 = new GridData();
		gridData2.horizontalAlignment = GridData.END;
		gridData2.grabExcessHorizontalSpace = true;
		gridData2.widthHint = 65;
		gridData2.verticalAlignment = GridData.CENTER;
		GridData gridData1 = new GridData();
		gridData1.verticalAlignment = GridData.CENTER;
		gridData1.horizontalAlignment = GridData.FILL;
		GridLayout gridLayout = new GridLayout();
		shell = new Shell(getParent(),SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
		shell.setText("");
		shell.setLayout(gridLayout);
		shell.setSize(new Point(312, 111));
		labelMessage = new Label(shell, SWT.NONE);
		labelMessage.setText("");
		labelMessage.setLayoutData(gridData1);
		GridData gridData = new GridData();
		gridData.verticalAlignment = GridData.CENTER;
		gridData.grabExcessHorizontalSpace = true;
		gridData.widthHint = 250;
		gridData.horizontalAlignment = GridData.FILL;
		textInput = new Text(shell, SWT.BORDER);
		textInput.setLayoutData(gridData);
		buttonOk = new Button(shell, SWT.NONE);
		buttonOk.setImage(SWTResourceManager.getImage(ShellImputSmart.class, "/rdw/icon/icon_accept.png"));
		buttonOk.setText("Ok");
		buttonOk.setLayoutData(gridData2);
	}

	public void setText(String text){
		shell.setText(text);
	}
	public void setMessage(String message){
		labelMessage.setText(message);
	}
	public void setDefaultText(String defText){
		textInput.setText(defText);
	}
	
	private void inicialize(){
		buttonOk.addSelectionListener(new SelectionAdapter (){
			public void widgetSelected(SelectionEvent e){
				result = textInput.getText().trim();
				shell.close();
			}
		});
		textInput.addKeyListener(new KeyAdapter(){
			@Override
			public void keyPressed(KeyEvent e) {
				if (e.keyCode == SWT.CR || (e.keyCode == SWT.KEYPAD_CR)){
					result = textInput.getText().trim();
					shell.close();
				}
			}
		});
		shell.addTraverseListener(new TraverseListener(){
			@Override
			public void keyTraversed(TraverseEvent e) {
				if (e.keyCode == SWT.ESC){
					result = null;
					shell.close();
				}
			}
		});
	}
	
}
