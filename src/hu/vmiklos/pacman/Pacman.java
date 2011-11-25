package hu.vmiklos.pacman;

import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.Item;
import javax.microedition.lcdui.StringItem;
import javax.microedition.midlet.MIDlet;

/**
 * Entry point of the game.
 */
public class Pacman extends MIDlet
{
	private Game game;
	private CommandHandler commandHandler;
	
	public Pacman() {
		game = new Game(this);
		commandHandler = new CommandHandler(this);
		game.setCommandListener(commandHandler);
		Display.getDisplay(this).setCurrent(game);
		Thread myThread = new Thread(new ThreadHandler(game));
		myThread.start();
	}

	public Game getGame() {
		return game;
	}
	
	public CommandHandler getCommandHandler() {
		return commandHandler;
	}
	
	/**
	 * Noop:
	 * 1) If this is a real start, we did everything in the ctor already.
	 * 2) Else we don't resume automatically, better if the user does so.
	 */
	public void startApp() {
	}
	
	public void pauseApp() {
		if (game.isStarted())
			commandHandler.pause();
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
	
	public void score() {
		int score = Storage.getScore();
		Item[] levelItem = {
			new StringItem("",
					(score >= 0 ? "The current high score is: " + Storage.getScore() :
						"There is no high score yet."))
		};
		Form form = new Form("High score", levelItem);
		commandHandler.form(form);
		Display.getDisplay(this).setCurrent(form);
	}
}
