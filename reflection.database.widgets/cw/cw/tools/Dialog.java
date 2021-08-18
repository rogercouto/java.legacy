package cw.tools;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;

import cw.view.ShellF12Message;
import cw.view.ShellImputSmart;
import cw.view.ShellInput;
import cw.view.ShellMessage;
import cw.view.ShellQuestion;



public class Dialog {

	private static final String TITLE_CONFIRM = "Confirma\u00e7\u00e3o";
	private static final String TITLE_WARNING = "Aten\u00e7\u00e3o";
	private static final String TITLE_ERROR = "Erro";
	private static final String MESSAGE_EXCLUDE = "Confirma exclus\u00e3o?";

	public static void message(String message){
		ShellMessage shell = new ShellMessage();
		shell.setMessage(message);
		shell.open();
	}
	public static void message(String title,String message){
		ShellMessage shell = new ShellMessage();
		shell.setText(title);
		shell.setMessage(message);
		shell.open();
	}
	public static void message(Shell parent,String title,String message){
		ShellMessage shell = new ShellMessage(parent);
		shell.setText(title);
		shell.setMessage(message);
		shell.open();
	}
	public static void f12Message(Shell parent,String title,String message){
		ShellF12Message dialog = new ShellF12Message(parent);
		dialog.setText(title);
		dialog.setMessage(message);
		dialog.open();
	}
	public static void confirmation(Shell parent,String message){
		ShellMessage shell = new ShellMessage(parent);
		shell.setText(TITLE_CONFIRM);
		shell.setMessage(message);
		shell.setImage(SWT.ICON_WORKING);
		shell.open();
	}
	public static void confirmation(Shell parent,String title,String message){
		ShellMessage shell = new ShellMessage(parent);
		shell.setText(title);
		shell.setMessage(message);
		shell.setImage(SWT.ICON_WORKING);
		shell.open();
	}
	public static void warning(Shell parent,String message){
		ShellMessage shell = new ShellMessage(parent);
		shell.setText(TITLE_WARNING);
		shell.setMessage(message);
		shell.setImage(SWT.ICON_WARNING);
		shell.open();
	}
	public static void warning(Shell parent,String title,String message){
		ShellMessage shell = new ShellMessage(parent);
		shell.setText(title);
		shell.setMessage(message);
		shell.setImage(SWT.ICON_WARNING);
		shell.open();
	}
	public static void error(Shell parent,String message){
		ShellMessage shell = new ShellMessage(parent);
		shell.setText(TITLE_ERROR);
		shell.setMessage(message);
		shell.setImage(SWT.ICON_ERROR);
		shell.open();
	}
	public static void error(Shell parent,String title,String message){
		ShellMessage shell = new ShellMessage(parent);
		shell.setText(title);
		shell.setMessage(message);
		shell.setImage(SWT.ICON_ERROR);
		shell.open();
	}
	@Deprecated
	public static boolean question(Shell parent,String title,String message){
		MessageBox box = new MessageBox(parent,SWT.YES|SWT.NO|SWT.ICON_QUESTION);
		box.setText(title);
		box.setMessage(message);
		if (box.open()==SWT.YES)
			return true;
		return false;
	}
	public static boolean question(Shell parent,String message){
		ShellQuestion shell = new ShellQuestion(parent);
		shell.setTitle(TITLE_CONFIRM);
		shell.setMessage(message);
		return shell.open();
	}
	public static boolean questionExclude(Shell parent){
		ShellQuestion shellExclude = new ShellQuestion(parent);
		shellExclude.setMessage(MESSAGE_EXCLUDE);
		return shellExclude.open();
	}
	public static boolean questionExclude(){
		ShellQuestion shellExclude = new ShellQuestion();
		shellExclude.setMessage(MESSAGE_EXCLUDE);
		return shellExclude.open();
	}
	@Deprecated
	public static int questionExit(Shell parent,String title,String message){
		MessageBox box = new MessageBox(parent,SWT.YES|SWT.NO|SWT.CANCEL|SWT.ICON_QUESTION);
		box.setText(title);
		box.setMessage(message);
		return box.open();
	}
	public static String input(Shell parent,String title,String message){
		//ShellInput input = new ShellInput(parent);
		ShellImputSmart input = new ShellImputSmart(parent);
		input.setText(title);
		input.setMessage(message);
		return input.open();
	}
	public static String input(Shell parent,String title,String message,String defaultText){
		ShellInput input = new ShellInput(parent);
		input.setText(title);
		input.setMessage(message);
		input.setDefaultText(defaultText);
		return input.open();
	}
}
