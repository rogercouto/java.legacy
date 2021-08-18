package rdw.simpledialog;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;

import cw.tools.Util;


public class ShellTest {

	protected Shell shell;
	protected Table table;
	protected TableColumn c1;
	protected TableColumn c2;
	protected Label lblNewLabel;

	/**
	 * Launch the application.
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			ShellTest window = new ShellTest();
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
		shell = new Shell();
		shell.setSize(450, 300);
		shell.setText("SWT Application");
		GridLayout gl_shell = new GridLayout();
		shell.setLayout(gl_shell);
		table = new Table(shell, SWT.BORDER);
		table.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		Util.setFontSize(11, table);
		c1 = new TableColumn(table, SWT.NONE | SWT.RIGHT);
		c1.setWidth(169);
		c1.setText("Label");
		c2 = new TableColumn(table, SWT.NONE | SWT.RIGHT);
		c2.setWidth(207);
		c2.setText("Editor");
		TableItem item = new TableItem(table, SWT.NONE);
		Label label = new Label(table, SWT.RIGHT | SWT.BORDER);
		label.setText("Nome*:");
		
		TableEditor labelEditor = new TableEditor(table);
		labelEditor.horizontalAlignment = SWT.LEFT;
		labelEditor.verticalAlignment = SWT.BOTTOM;
		labelEditor.grabHorizontal = true;
		labelEditor.grabVertical = true;
		labelEditor.setEditor(label, item, 0);
		Text text = new Text(table, SWT.BORDER);
		TableEditor editor = new TableEditor(table);
		editor.horizontalAlignment = SWT.LEFT;
	    editor.grabHorizontal = true;
	    editor.minimumWidth = 50;
	    editor.setEditor(text, item, 1);
	    lblNewLabel = new Label(shell, SWT.NONE);
	    lblNewLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
	    lblNewLabel.setText("New Label");
	}

}
