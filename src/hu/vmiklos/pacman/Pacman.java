package hu.vmiklos.pacman;

import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Form;
import javax.microedition.midlet.MIDlet;
import javax.microedition.midlet.MIDletStateChangeException;

public class Pacman extends MIDlet implements CommandListener {

	private Form form;
	private Display display;
	private Command exitCommand;
	
	public Pacman() {
		form = new Form("Pacman");
		display = Display.getDisplay(this);
		exitCommand = new Command("Exit", Command.SCREEN, 1);
	}

	protected void destroyApp(boolean unconditional)
			throws MIDletStateChangeException {
	}

	protected void pauseApp() {
	}

	protected void startApp() throws MIDletStateChangeException {
		form.addCommand(exitCommand);
		form.setCommandListener(this);
		form.append("Hello World!");
		display.setCurrent(form);
	}

	public void commandAction(Command c, Displayable d) {
		if (c == exitCommand) {
			try {
				destroyApp(true);
			} catch (MIDletStateChangeException e) {
			}
			notifyDestroyed();
		}
	}

}
