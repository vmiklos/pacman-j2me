package hu.vmiklos.pacman;

import java.util.Vector;

import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Form;

public class CommandHandler implements CommandListener {

	private Pacman pacman;
	private Command pauseCmd = new Command("Pause", Command.SCREEN, 1);
	private Command resumeCmd = new Command("Resume", Command.SCREEN, 1);
	private Command startCmd = new Command("Start", Command.SCREEN, 2);
	private Command stopCmd = new Command("Stop", Command.SCREEN, 2);
	private Command exitCmd = new Command("Exit", Command.SCREEN, 3);
	private Command helpCmd = new Command("Help", Command.SCREEN, 4);
	private Command cancelCmd = new Command("Cancel", Command.SCREEN, 5);
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
	
	public Command getPauseCmd() {
		return pauseCmd;
	}

	private void addCommand(Command c) {
		pacman.getGame().addCommand(c);
		addedCommands.addElement(c);
	}
	
	public void setCommands(Command first, Command second) {
		for (int i = 0; i < addedCommands.size(); i++) {
			pacman.getGame().removeCommand((Command)addedCommands.elementAt(i));
		}
		addedCommands.removeAllElements();
		addCommand(first);
		if (second != null)
			addCommand(second);
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
			setCommands(pauseCmd, stopCmd);
			pacman.getGame().start();
		} else if (c == stopCmd) {
			setCommands(startCmd, null);
			pacman.getGame().stop();
		} else if (c == pauseCmd) {
			setCommands(resumeCmd, stopCmd);
			pacman.getGame().pause();
		} else if (c == resumeCmd) {
			setCommands(pauseCmd, stopCmd);
			pacman.getGame().resume();
		}
	}
	
	public void form(Form form) {
		form.addCommand(okCmd);
		form.setCommandListener(this);
	}

}
