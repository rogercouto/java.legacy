package cw.tools;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.DateTime;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.wb.swt.SWTResourceManager;

public class Test {

	protected Shell shell;
	private DateTime dateTime;
	private Button btnNewButton;

	/**
	 * Launch the application.
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			Test window = new Test();
			window.open();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Open the window.
	 */
	public void open() {
		Display display = Display.getDefault();
		createContents();
		shell.open();
		shell.layout();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}

	/**
	 * Create contents of the window.
	 */
	protected void createContents() {
		shell = new Shell(SWT.DIALOG_TRIM | SWT.SYSTEM_MODAL);
		GridLayout gl_shell = new GridLayout();
		gl_shell.marginHeight = 25;
		gl_shell.marginWidth = 25;
		gl_shell.numColumns = 2;
		shell.setLayout(gl_shell);
		dateTime = new DateTime(shell, SWT.BORDER);
		GridData gd_dateTime = new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1);
		gd_dateTime.widthHint = 120;
		dateTime.setLayoutData(gd_dateTime);
		btnNewButton = new Button(shell, SWT.NONE);
		btnNewButton.setImage(SWTResourceManager.getImage(Test.class, "/rdw/icon/aviso.png"));
		btnNewButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				Date date = Util.getDate(dateTime);
				Date date2 = Util.incDay(date);
				System.out.println(new SimpleDateFormat("dd/MM/yyyy").format(date2));
			}
		});
		btnNewButton.setText("Tipo de dados n\u00E3o \u00E9 num\u00E9rico");
		shell.pack();
		Screen.centralize(shell);
	}

}
