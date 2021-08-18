package cw.controls.calctext;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class CompCalc extends Composite {

	protected Shell shellCalc = null;  //  @jve:decl-index=0:visual-constraint="12,48"
	protected Text textCalc = null;
	protected Button button7 = null;
	protected Button button8 = null;
	protected Button button9 = null;
	protected Button button4 = null;
	protected Button button5 = null;
	protected Button button6 = null;
	protected Button button1 = null;
	protected Button button2 = null;
	protected Button button3 = null;
	protected Button buttonOp = null;
	protected Button button0 = null;
	protected Button buttonComma = null;
	protected Button buttonC = null;
	protected Button buttonBs = null;
	protected Button buttonResult = null;
	protected Button buttonDivide = null;
	protected Button buttonMultiply = null;
	protected Button buttonSubtract = null;
	protected Button buttonAdd = null;
	protected Button buttonCalc = null;
	protected int style;
	
	public CompCalc(Composite parent, int style) {
		super(parent, SWT.NONE);
		this.style = style;
		initialize();
	}

	/**
	 * This method initializes shellCalc	
	 *
	 */
	private void createShellCalc() {
		GridData gridData19 = new GridData();
		gridData19.horizontalAlignment = GridData.FILL;
		gridData19.verticalAlignment = GridData.CENTER;
		GridData gridData18 = new GridData();
		gridData18.horizontalAlignment = GridData.FILL;
		gridData18.verticalAlignment = GridData.CENTER;
		GridData gridData17 = new GridData();
		gridData17.horizontalAlignment = GridData.FILL;
		gridData17.verticalAlignment = GridData.CENTER;
		GridData gridData16 = new GridData();
		gridData16.horizontalAlignment = GridData.FILL;
		gridData16.verticalAlignment = GridData.CENTER;
		GridData gridData15 = new GridData();
		gridData15.horizontalAlignment = GridData.FILL;
		gridData15.verticalAlignment = GridData.CENTER;
		GridData gridData14 = new GridData();
		gridData14.horizontalAlignment = GridData.FILL;
		gridData14.verticalAlignment = GridData.CENTER;
		GridData gridData13 = new GridData();
		gridData13.horizontalAlignment = GridData.FILL;
		gridData13.verticalAlignment = GridData.CENTER;
		GridData gridData12 = new GridData();
		gridData12.horizontalAlignment = GridData.FILL;
		gridData12.verticalAlignment = GridData.CENTER;
		GridData gridData11 = new GridData();
		gridData11.horizontalAlignment = GridData.FILL;
		gridData11.verticalAlignment = GridData.CENTER;
		GridData gridData10 = new GridData();
		gridData10.horizontalAlignment = GridData.FILL;
		gridData10.verticalAlignment = GridData.CENTER;
		GridData gridData9 = new GridData();
		gridData9.horizontalAlignment = GridData.FILL;
		gridData9.verticalAlignment = GridData.CENTER;
		GridData gridData8 = new GridData();
		gridData8.horizontalAlignment = GridData.FILL;
		gridData8.verticalAlignment = GridData.CENTER;
		GridData gridData7 = new GridData();
		gridData7.horizontalAlignment = GridData.FILL;
		gridData7.verticalAlignment = GridData.CENTER;
		GridData gridData6 = new GridData();
		gridData6.horizontalAlignment = GridData.FILL;
		gridData6.verticalAlignment = GridData.CENTER;
		GridData gridData5 = new GridData();
		gridData5.horizontalAlignment = GridData.FILL;
		gridData5.verticalAlignment = GridData.CENTER;
		GridData gridData4 = new GridData();
		gridData4.horizontalAlignment = GridData.FILL;
		gridData4.verticalAlignment = GridData.CENTER;
		GridData gridData3 = new GridData();
		gridData3.horizontalAlignment = GridData.FILL;
		gridData3.verticalAlignment = GridData.CENTER;
		GridData gridData2 = new GridData();
		gridData2.horizontalAlignment = GridData.FILL;
		gridData2.verticalAlignment = GridData.CENTER;
		GridData gridData1 = new GridData();
		gridData1.horizontalSpan = 2;
		gridData1.verticalAlignment = GridData.CENTER;
		gridData1.horizontalAlignment = GridData.FILL;
		GridLayout gridLayout1 = new GridLayout();
		gridLayout1.numColumns = 4;
		gridLayout1.horizontalSpacing = 0;
		gridLayout1.verticalSpacing = 0;
		gridLayout1.marginWidth = 0;
		gridLayout1.marginHeight = 0;
		gridLayout1.makeColumnsEqualWidth = true;
		shellCalc = new Shell(this.getShell(),SWT.NO_TRIM | SWT.MODELESS);
		shellCalc.setLayout(gridLayout1);
		shellCalc.addShellListener(new org.eclipse.swt.events.ShellAdapter() {
			public void shellDeactivated(org.eclipse.swt.events.ShellEvent e) {
				shellCalc.setVisible(false);
				buttonCalc.setSelection(false);
				textCalc.setFocus();
			}
		});
		button7 = new Button(shellCalc, SWT.NONE);
		button7.setText("7");
		button7.setLayoutData(gridData2);
		button8 = new Button(shellCalc, SWT.NONE);
		button8.setText("8");
		button8.setLayoutData(gridData3);
		button9 = new Button(shellCalc, SWT.NONE);
		button9.setText("9");
		button9.setLayoutData(gridData4);
		buttonDivide = new Button(shellCalc, SWT.NONE);
		buttonDivide.setText("/");
		buttonDivide.setLayoutData(gridData5);
		button4 = new Button(shellCalc, SWT.NONE);
		button4.setText("4");
		button4.setLayoutData(gridData6);
		button5 = new Button(shellCalc, SWT.NONE);
		button5.setText("5");
		button5.setLayoutData(gridData7);
		button6 = new Button(shellCalc, SWT.NONE);
		button6.setText("6");
		button6.setLayoutData(gridData8);
		buttonMultiply = new Button(shellCalc, SWT.NONE);
		buttonMultiply.setText("*");
		buttonMultiply.setLayoutData(gridData9);
		button1 = new Button(shellCalc, SWT.NONE);
		button1.setText("1");
		button1.setLayoutData(gridData10);
		button2 = new Button(shellCalc, SWT.NONE);
		button2.setText("2");
		button2.setLayoutData(gridData11);
		button3 = new Button(shellCalc, SWT.NONE);
		button3.setText("3");
		button3.setLayoutData(gridData12);
		buttonSubtract = new Button(shellCalc, SWT.NONE);
		buttonSubtract.setText("-");
		buttonSubtract.setLayoutData(gridData13);
		buttonOp = new Button(shellCalc, SWT.NONE);
		buttonOp.setText("-+");
		buttonOp.setLayoutData(gridData14);
		button0 = new Button(shellCalc, SWT.NONE);
		button0.setText("0");
		button0.setLayoutData(gridData15);
		buttonComma = new Button(shellCalc, SWT.NONE);
		buttonComma.setText(",");
		buttonComma.setLayoutData(gridData16);
		buttonAdd = new Button(shellCalc, SWT.NONE);
		buttonAdd.setText("+");
		buttonAdd.setLayoutData(gridData17);
		buttonC = new Button(shellCalc, SWT.NONE);
		buttonC.setText("C");
		buttonC.setLayoutData(gridData18);
		buttonBs = new Button(shellCalc, SWT.NONE);
		buttonBs.setText("<");
		buttonBs.setLayoutData(gridData19);
		buttonResult = new Button(shellCalc, SWT.NONE);
		buttonResult.setText("=");
		buttonResult.setLayoutData(gridData1);
		shellCalc.pack();
	}

	private void initialize() {
		GridData gridData110 = new GridData();
		gridData110.grabExcessVerticalSpace = true;
		gridData110.verticalAlignment = GridData.FILL;
		gridData110.heightHint = 20;
		gridData110.horizontalAlignment = GridData.BEGINNING;
		GridLayout gridLayout = new GridLayout();
		gridLayout.horizontalSpacing = 0;
		gridLayout.marginWidth = 0;
		gridLayout.marginHeight = 0;
		gridLayout.numColumns = 2;
		gridLayout.verticalSpacing = 0;
		GridData gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;
		gridData.grabExcessVerticalSpace = true;
		gridData.verticalAlignment = GridData.FILL;
		textCalc = new Text(this, style);
		textCalc.setLayoutData(gridData);
		buttonCalc = new Button(this, SWT.TOGGLE);
		buttonCalc.setImage(new Image(Display.getCurrent(), getClass().getResourceAsStream("/rdw/icon/icon_calc.png")));
		buttonCalc.setLayoutData(gridData110);
		buttonCalc.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
			public void widgetSelected(org.eclipse.swt.events.SelectionEvent e) {
				showCalc();
			}
		});
		this.setLayout(gridLayout);
		this.pack();
		createShellCalc();
		this.setTabList(new Control[]{textCalc});
	}

	protected void showCalc(){
		Rectangle parentRect = this.getDisplay().map (getParent (), null, getBounds ());
		Point loc = new Point(parentRect.x,parentRect.y);
		Point textSize = textCalc.getSize();
		Point inc = new Point(0,0);
		inc.x = this.getBorderWidth() != 0 ? this.getBorderWidth() + 2 : 0;
		inc.y = this.getBorderWidth() != 0 ? this.getBorderWidth() + 1 : 0;
		loc.x += textSize.x - shellCalc.getSize().x;
		loc.x += buttonCalc.getSize().x + inc.x;
		loc.y += textSize.y + inc.y; 
		shellCalc.setLocation(loc);
		shellCalc.open();
	}
	
}  //  @jve:decl-index=0:visual-constraint="10,10"
