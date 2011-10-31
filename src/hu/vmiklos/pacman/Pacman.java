package hu.vmiklos.pacman;

import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.Item;
import javax.microedition.lcdui.StringItem;
import javax.microedition.midlet.MIDlet;

public class Pacman extends MIDlet
{
	private Game game;
	private CommandHandler commandHandler;
	
	public Pacman() {
		game = new Game();
		commandHandler = new CommandHandler(this);
		game.setCommandListener(commandHandler);
		Display.getDisplay(this).setCurrent(game);
		Thread myThread = new Thread(game);
		myThread.start();
		commandHandler.setCommands(commandHandler.getStartCmd(), null);
	}

	public Game getGame() {
		return game;
	}
	
	public void startApp() {
		/**
		 * Noop:
		 * 1) If this is a real start, we did everything in the ctor already.
		 * 2) Else we don't resume automatically, better if the user does so.
		 */
	}
	
	public void pauseApp() {
		commandHandler.commandAction(commandHandler.getPauseCmd(), null);
	}

	public void destroyApp(boolean unconditional) {
		Display.getDisplay(this).setCurrent(null);
	}

	public void exit() {
		destroyApp(false);
		notifyDestroyed();
	}
	
	public void help() {
		Item[] levelItem = {
			new StringItem("", "Guide the yellow Pacman around the maze and eat all the little black dots whilst " +
				"avoiding those nasty red ghosts! If you like the number buttons, use 2, 4, 6 and 8 to move Pacman " +
				"up, left, right and down, respectively.")
		};
		Form form = new Form("Help", levelItem);
		commandHandler.form(form);
		Display.getDisplay(this).setCurrent(form);
	}
}
