package rdw.simpledialog;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.wb.swt.SWTResourceManager;

public class ShellDialog extends Dialog {

	protected Shell shell;
	protected Table table;
	protected Composite composite;
	protected Button btnSalvar;
	protected Button btnCancelar;
	protected Label lblCamposObrigatrios;
	protected TableColumn tblclmnNewColumn;
	protected TableColumn tblclmnNewColumn_1;

	/**
	 * Create the dialog.
	 * @param parent
	 * @param style
	 */
	public ShellDialog(Shell parent) {
		super(parent, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
		createContents();
	}

	/**
	 * Open the dialog.
	 * @return the result
	 */
	public Object open() {
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

	/**
	 * Create contents of the dialog.
	 */
	private void createContents() {
		shell = new Shell(getParent(), getStyle());
		shell.setSize(450, 300);
		shell.setLayout(new GridLayout());
		new Label(shell, SWT.NONE);
		table = new Table(shell, SWT.FULL_SELECTION);
		table.setBackground(SWTResourceManager.getColor(SWT.COLOR_WIDGET_BACKGROUND));
		table.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		table.setLinesVisible(true);
		tblclmnNewColumn = new TableColumn(table, SWT.NONE);
		tblclmnNewColumn.setWidth(100);
		tblclmnNewColumn_1 = new TableColumn(table, SWT.NONE);
		tblclmnNewColumn_1.setWidth(100);
		composite = new Composite(shell, SWT.NONE);
		composite.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		composite.setLayout(new GridLayout(3, false));
		lblCamposObrigatrios = new Label(composite, SWT.NONE);
		lblCamposObrigatrios.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		lblCamposObrigatrios.setText("* Campos obrigat\u00F3rios:");
		btnSalvar = new Button(composite, SWT.NONE);
		btnSalvar.setImage(SWTResourceManager.getImage(ShellDialog.class, "/rdw/icon/icon_accept.png"));
		GridData gd_btnSalvar = new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1);
		gd_btnSalvar.widthHint = 90;
		btnSalvar.setLayoutData(gd_btnSalvar);
		btnSalvar.setText("Salvar");
		btnCancelar = new Button(composite, SWT.NONE);
		btnCancelar.setImage(SWTResourceManager.getImage(ShellDialog.class, "/rdw/icon/icon_cancel.png"));
		GridData gd_btnCancelar = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_btnCancelar.widthHint = 90;
		btnCancelar.setLayoutData(gd_btnCancelar);
		btnCancelar.setText("Cancelar");
	}

}
