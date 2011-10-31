package hu.vmiklos.pacman;

public class ThreadHandler implements Runnable {
	private Game game;
	
	public ThreadHandler(Game game) {
		this.game = game;
	}

	public void run() {
		long  starttime;

		game.initLevel();
		while(true) {
			starttime=System.currentTimeMillis();
			try {
				if (!game.isPaused())
					game.repaint();
				// 25fps -> wait at least 40ms if repaint() was faster
				starttime += 40;
				Thread.sleep(Math.max(0, starttime-System.currentTimeMillis()));
			} catch(java.lang.InterruptedException ie) {
				break;
			}
		}
	}
}
