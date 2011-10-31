package hu.vmiklos.pacman;

import javax.microedition.lcdui.Display;
import javax.microedition.midlet.MIDlet;

public class Pacman extends MIDlet
{
	private Game game;
	private CommandHandler commandHandler;
	
	public Pacman() {
		game = new Game();
		commandHandler = new CommandHandler(this);
		game.setCommandListener(commandHandler);
	}

	public Game getGame() {
		return game;
	}
	
	public void startApp() {
		Display.getDisplay(this).setCurrent(game);
		Thread myThread = new Thread(game);
		myThread.start();
		commandHandler.setCommands(commandHandler.getStartCmd());
	}
	
	public void pauseApp() {
	}

	public void destroyApp(boolean unconditional) {
		Display.getDisplay(this).setCurrent(null);
	}

}
