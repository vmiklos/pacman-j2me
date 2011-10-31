package hu.vmiklos.pacman;

import java.util.Vector;

import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Form;

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
			pacman.exit();
		} else if (c == helpCmd) {
			pacman.help();
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
	
	public void form(Form form) {
		form.addCommand(okCmd);
		form.setCommandListener(this);
	}

}
