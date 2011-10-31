package hu.vmiklos.pacman;

import java.util.Vector;

import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.Item;
import javax.microedition.lcdui.StringItem;

public class CommandHandler implements CommandListener {

	private Pacman pacman;
	private Command startCmd = new Command("Start", Command.SCREEN, 1);
	private Command stopCmd = new Command("Stop", Command.SCREEN, 1);
	private Command helpCmd = new Command("Help", Command.SCREEN, 2);
	private Command exitCmd = new Command("Exit", Command.SCREEN, 3);
	private Command cancelCmd = new Command("Cancel", Command.SCREEN, 4);
	private Command okCmd = new Command("OK", Command.SCREEN, 1);
	private Vector addedCommands;
	
	public CommandHandler(Pacman pacman) {
		this.pacman = pacman;
		addedCommands = new Vector();
	}
	
	public Command getStartCmd() {
		return startCmd;
	}
	
	public Command getStopCmd() {
		return stopCmd;
	}

	private void addCommand(Command c) {
		pacman.getGame().addCommand(c);
		addedCommands.addElement(c);
	}
	
	public void setCommands(Command first) {
		for (int i = 0; i < addedCommands.size(); i++) {
			pacman.getGame().removeCommand((Command)addedCommands.elementAt(i));
		}
		addedCommands.removeAllElements();
		addCommand(first);
		addCommand(helpCmd);
		addCommand(exitCmd);
		addCommand(cancelCmd);
	}

	public void commandAction(Command c, Displayable s) {
		if (c == exitCmd) {
			pacman.destroyApp(false);
			pacman.notifyDestroyed();
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
			Display.getDisplay(pacman).setCurrent(form);
		} else if ((c == cancelCmd) || (c == okCmd)) {
			Display.getDisplay(pacman).setCurrent(pacman.getGame());
		} else if (c == startCmd) {
			setCommands(stopCmd);
			pacman.getGame().start();
		} else if (c == stopCmd) {
			setCommands(startCmd);
			pacman.getGame().stop();
		}
	}

}
