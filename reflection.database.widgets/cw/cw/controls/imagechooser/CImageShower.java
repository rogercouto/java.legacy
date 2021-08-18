package cw.controls.imagechooser;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

import cw.tools.Screen;

public class CImageShower {

	private Shell parent;
	private Shell shell = null;  //  @jve:decl-index=0:visual-constraint="10,10"
	private ScrolledComposite scrolledComposite = null;
	private Label labelImage = null;
	private Image image = null;
	private Point sSize = null;  //  @jve:decl-index=0:
	
	private Point minimunSize = new Point(640,480);  //  @jve:decl-index=0:
	private Point maximunSize = Screen.getSize();
	
	public CImageShower(Shell parent,Image image) {
		super();
		this.parent = parent;
		this.image = image;
		maximunSize.x -= 100;
		maximunSize.y -= 100;
		createShell();
	}
	
	public CImageShower(Image image) {
		super();
		this.image = image;
		maximunSize.x -= 100;
		maximunSize.y -= 100;
		createShell();
	}
	/**
	 * This method initializes scrolledComposite	
	 *
	 */
	private void createScrolledComposite() {
		GridData gridData = new GridData();
		gridData.horizontalAlignment = GridData.CENTER;
		gridData.grabExcessHorizontalSpace = true;
		gridData.grabExcessVerticalSpace = true;
		gridData.verticalAlignment = GridData.CENTER;
		scrolledComposite = new ScrolledComposite(shell, SWT.H_SCROLL | SWT.V_SCROLL);
		scrolledComposite.setLayout(new GridLayout());
		scrolledComposite.setLayoutData(gridData);
		labelImage = new Label(scrolledComposite, SWT.NONE);
		labelImage.setText("Label");
		if (image != null){
			ImageData id = image.getImageData();
			Point size = new Point(id.width,id.height);
			labelImage.setImage(image);
			sSize = new Point(0,0);
			if (size.x < minimunSize.x)
				sSize.x = minimunSize.x;
			else if (size .x > maximunSize.x)
				sSize.x = maximunSize.x;
			else 
				sSize.x = size.x;
			if (size.y < minimunSize.y)
				sSize.y = minimunSize.y;
			else if (size .y > maximunSize.y)
				sSize.y = maximunSize.y;
			else 
				sSize.y = size.y;
			sSize.x += 50;
			sSize.y += 50;
			labelImage.setBounds(0,0,size.x,size.y);
		}
		scrolledComposite.setContent(labelImage);
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Display display = Display.getDefault();
		
		Image image = new Image(display,"C:/Documents and Settings/Roger/Meus documentos/Minhas imagens/Screenshots/komo eu sou mau.PNG");
		CImageShower thisClass = new CImageShower(image);
		thisClass.createShell();
		thisClass.shell.open();

		while (!thisClass.shell.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}
		display.dispose();
	}
	 
	/**
	 * This method initializes shell
	 */
	private void createShell() {
		if (parent == null)
			shell = new Shell(SWT.SHELL_TRIM | SWT.PRIMARY_MODAL);
		else 
			shell = new Shell(parent,SWT.SHELL_TRIM | SWT.PRIMARY_MODAL);
		shell.setText("Visualizando imagem...");
		shell.setImage(new Image(Display.getCurrent(), getClass().getResourceAsStream("/rdw/icon/icon_find.png")));
		createScrolledComposite();
		shell.setLayout(new GridLayout());
		shell.setSize(sSize);
		Screen.centralize(shell);
	}

	public void open() {
		Display display = shell.getDisplay();
		shell.open();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}
	}
	
}
