package hu.vmiklos.pacman;

import java.util.Vector;

import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.Item;
import javax.microedition.lcdui.StringItem;
import javax.microedition.midlet.MIDlet;

public class Pacman extends MIDlet implements CommandListener
{
	private Game game;
	private Command startCmd = new Command("Start", Command.SCREEN, 1);
	private Command stopCmd = new Command("Stop", Command.SCREEN, 1);
	private Command helpCmd = new Command("Help", Command.SCREEN, 2);
	private Command exitCmd = new Command("Exit", Command.SCREEN, 3);
	private Command cancelCmd = new Command("Cancel", Command.SCREEN, 4);
	private Command okCmd = new Command("OK", Command.SCREEN, 1);

	private Vector addedCommands;
	
	public Pacman() {
		game = new Game();
		addedCommands = new Vector();
		game.setCommandListener(this);
	}
	
	private void addCommand(Command c) {
		game.addCommand(c);
		addedCommands.addElement(c);
	}
	
	private void setCommands(Command first) {
		for (int i = 0; i < addedCommands.size(); i++) {
			game.removeCommand((Command)addedCommands.elementAt(i));
		}
		addedCommands.removeAllElements();
		addCommand(first);
		addCommand(helpCmd);
		addCommand(exitCmd);
		addCommand(cancelCmd);
	}

	public void startApp() {
		Display.getDisplay(this).setCurrent(game);
		Thread myThread = new Thread(game);
		myThread.start();
		setCommands(startCmd);
	}
	
	public void pauseApp() {
	}

	public void destroyApp(boolean unconditional) {
		Display.getDisplay(this).setCurrent(null);
	}

	public void commandAction(Command c, Displayable s) {
		if (c == exitCmd) {
			destroyApp(false);
			notifyDestroyed();
		}
		else if (c == helpCmd) {
			Item[] levelItem = {
				new StringItem("", "Guide the yellow Pacman around the maze and eat all the little black dots whilst "+
						"avoiding those nasty red ghosts! If you like the number buttons, use 2, 4, 6 and 8 to move "+
						"Pacman up, left, right and down, respectively.")
			};
			Form form = new Form("Help", levelItem);
			form.addCommand(okCmd);
			form.setCommandListener(this);
			Display.getDisplay(this).setCurrent(form);
		} else if ((c == cancelCmd) || (c == okCmd)) {
			Display.getDisplay(this).setCurrent(game);
		} else if (c == startCmd) {
			setCommands(stopCmd);
			game.start();
		} else if (c == stopCmd) {
			setCommands(startCmd);
			game.stop();
		}
	}
}
