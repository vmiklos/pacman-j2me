package hu.vmiklos.pacman;

import java.util.Random;
import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;

/**
 * The model and controller of the game.
 */
public class Game extends Canvas {
	private int devWidth;
	private int devHeight;
	private float ratio;
	private Graphics graphics;
	private Image image;
	private Random random = null;

	// status machine
	private boolean started = false;
	private boolean paused = false; // if started, paused or resumed?
	private boolean scared = false;
	private boolean dying = false;

	// rules
	private final int maxGhosts = 12;
	private final int minScaredTime = 20;
	private final int maxSpeed = 6;
	// valid speeds of ghosts
	private final int validSpeeds[] = { 1, 2, 3, 3, 4, 4 };
	private final int xBlocknum = 15;
	private final int yBlocknum = 13;
	private final int blockSize = 24;

	// generated values
	private final int width;
	private final int height;
	
	// defaults
	private int ghostNum = 6;
	private int currentSpeed = 4;

	// status variables
	private int pacsLeft, score, deathCounter;
	private int scaredCount, scaredTime;
	private int[] ghostSpeed;

	// positions
	private int[] ghostX, ghostY, ghostDx, ghostDy;
	private int pacmanX, pacmanY, pacmandX, pacmandY, reqDx, reqDy;

	// the maze
	private final short levelData[] = { 
			19,26,26,22, 9,12,19,26,22, 9,12,19,26,26,22,
			37,11,14,17,26,26,20,15,17,26,26,20,11,14,37,
			17,26,26,20,11, 6,17,26,20, 3,14,17,26,26,20,
			21, 3, 6,25,22, 5,21, 7,21, 5,19,28, 3, 6,21,
			21, 9, 8,14,21,13,21, 5,21,13,21,11, 8,12,21,
			25,18,26,18,24,18,28, 5,25,18,24,18,26,18,28,
			6,21, 7,21, 7,21,11, 8,14,21, 7,21, 7,21,03,
			19,24,26,24,26,16,26,18,26,16,26,24,26,24,22,
			21, 3, 2, 2, 6,21,15,21,15,21, 3, 2, 2,06,21,
			21, 9, 8, 8, 4,17,26, 8,26,20, 1, 8, 8,12,21,
			17,26,26,22,13,21,11, 2,14,21,13,19,26,26,20,
			37,11,14,17,26,24,22,13,19,24,26,20,11,14,37,
			25,26,26,28, 3, 6,25,26,28, 3, 6,25,26,26,28
	};
	private short[] screenData;
	private Font font;
	private Pacman pacman;
	private String hint;

	public Game(Pacman pacman) {
		hint = "App started!";
		this.pacman = pacman;
		screenData = new short[xBlocknum*yBlocknum];
		ghostX = new int[maxGhosts];
		ghostDx = new int[maxGhosts];
		ghostY = new int[maxGhosts];
		ghostDy = new int[maxGhosts];
		ghostSpeed = new int[maxGhosts];
		width = blockSize * xBlocknum;
		height = blockSize * yBlocknum;
		devWidth = getWidth();
		devHeight = getHeight();
		font = Font.getFont(Font.FONT_STATIC_TEXT);
		ratio = (float)Math.min(devHeight-font.getHeight(), devWidth) / Math.max(height, width);
		init();
	}
	
	public boolean isPaused() {
		return paused;
	}
	
	public boolean isStarted() {
		return started;
	}

	public void start() {
		hint = "Game started!";
		started = true;
		init();
	}
	
	public void stop() {
		hint = "Game ended.";
		started = false;
		initLevel();
	}
	
	public void pause() {
		hint = "Game paused.";
		paused = true;
		repaint();
	}
	
	public void resume() {
		hint = "Game resumed!";
		paused = false;
	}

	/**
	 * Resets the state of the level (after a death, etc).
	 */
	public void initLevel() {
		short i;
		int dx = 1;

		for (i = 0; i < ghostNum; i++) {
			ghostX[i] = 7 * blockSize;
			ghostY[i] = 6 * blockSize;
			ghostDx[i] = dx;
			ghostDy[i] = 0;
			dx = - dx;
			ghostSpeed[i] = validSpeeds[currentSpeed - 3];
		}
		pacmanX = 7 * blockSize;
		pacmanY = 9 * blockSize;
		pacmandX = 0;
		pacmandY = 0;
		reqDx = 0;
		reqDy = 0;
		dying = false;
		scared = false;
	}

	/**
	 * Handles the keyboard.
	 */
	protected void keyPressed(int key) {
		if(started) {
			if(key == Canvas.KEY_NUM4 || key == Canvas.LEFT || key == -3) {
				reqDx = -1;
				reqDy = 0;
			} else if(key == Canvas.KEY_NUM6 || key == Canvas.RIGHT || key == -4) {
				reqDx = 1;
				reqDy = 0;
			} else if(key == Canvas.KEY_NUM2 || key == Canvas.UP || key == -1) {
				reqDx = 0;
				reqDy = -1;
			} else if(key == Canvas.KEY_NUM8 || key == Canvas.DOWN || key == -2) {
				reqDx = 0;
				reqDy = 1;
			}
		}
	}
	
	/**
	 * Paints the canvas, incl. stepping the models.
	 */
	protected void paint(Graphics g) {
		int x, y;
		short i = 0;

		if (graphics == null && width > 0 && height > 0) {
			image = Image.createImage(devWidth, devHeight);
			graphics = image.getGraphics();
		}
		if(graphics == null || image == null) {
			return;
		}

		graphics.setColor(255, 255, 255);
		graphics.fillRect(0, 0, devWidth, devHeight);
		// draw the maze
		for(y = 0; y < blockSize*yBlocknum; y += blockSize) {
			for(x = 0; x < blockSize*xBlocknum; x += blockSize) {
				// borders
				if(!scared)
					graphics.setColor(0, 0, 0);
				else
					graphics.setColor(255, 0, 0);
				/*  2
				 * 1 4
				 *  8
				 */
				if((screenData[i]&1) != 0)
					graphics.drawLine(toPixel(x),toPixel(y),toPixel(x),toPixel(y+blockSize-1));
				if((screenData[i]&2) != 0)
					graphics.drawLine(toPixel(x),toPixel(y),toPixel(x+blockSize-1),toPixel(y));
				if((screenData[i]&4) != 0)
					graphics.drawLine(toPixel(x+blockSize),toPixel(y),toPixel(x+blockSize),toPixel(y+blockSize));
				if ((screenData[i]&8) != 0)
					graphics.drawLine(toPixel(x),toPixel(y+blockSize),toPixel(x+blockSize-1),toPixel(y+blockSize));
				if ((screenData[i]&16) != 0) {
					graphics.setColor(0, 0, 0);
					graphics.fillRect(toPixel(x+blockSize/2),toPixel(y+blockSize/2),1,1);
				}
				if ((screenData[i]&32) != 0) {
					graphics.setColor(0, 0, 255);
					graphics.fillRect(toPixel(x+1),toPixel(y+1),toPixel(blockSize-1),toPixel(blockSize-1));
				}
				i++;
			}
		}
		if(started) {
			// play the game
			if (dying) {
				deathCounter--;
				if((deathCounter%8)<4) {
					graphics.setColor(255, 255, 255);
					graphics.fillRect(toPixel(pacmanX+1), toPixel(pacmanY+1), toPixel(blockSize-1), toPixel(blockSize-1));
				}
				else if((deathCounter%8)>=4) {
					graphics.setColor(255, 255, 0);
					graphics.fillRect(toPixel(pacmanX+1), toPixel(pacmanY+1), toPixel(blockSize-1), toPixel(blockSize-1));
				} if(deathCounter == 0) {
					pacsLeft--;
					if(pacsLeft == 0) {
						started = false;
						hint = "Game over!";
						pacman.getCommandHandler().stop();
					}
					initLevel();
				}
			} else {
				updateWalls();
				// if we are not dying, we can move pacman
				int     pos;
				short   ch;

				if (reqDx==-pacmandX && reqDy==-pacmandY) {
					pacmandX=reqDx;
					pacmandY=reqDy;
				} if (pacmanX%blockSize==0 && pacmanY%blockSize==0) {
					pos=pacmanX/blockSize+xBlocknum*(int)(pacmanY/blockSize);
					ch=screenData[pos];
					if ((ch&16)!=0) {
						screenData[pos]=(short)(ch&15);
						score++;
					} 
					if ((ch&32)!=0) {
						scared=true;
						scaredCount=scaredTime;
						screenData[pos]=(short)(ch&15);
						score+=5;
					}

					if (reqDx!=0 || reqDy!=0) {
						if (!( (reqDx==-1 && reqDy==0 && (ch&1)!=0) ||
								(reqDx==1 && reqDy==0 && (ch&4)!=0) ||
								(reqDx==0 && reqDy==-1 && (ch&2)!=0) ||
								(reqDx==0 && reqDy==1 && (ch&8)!=0))) {
							pacmandX=reqDx;
							pacmandY=reqDy;
						}
					}

					// check if we should stop pacman
					if ( (pacmandX==-1 && pacmandY==0 && (ch&1)!=0) ||
							(pacmandX==1 && pacmandY==0 && (ch&4)!=0) ||
							(pacmandX==0 && pacmandY==-1 && (ch&2)!=0) ||
							(pacmandX==0 && pacmandY==1 && (ch&8)!=0)) {
						pacmandX=0;
						pacmandY=0;
					}
				}
				pacmanX=pacmanX+currentSpeed*pacmandX;
				pacmanY=pacmanY+currentSpeed*pacmandY;
				graphics.setColor(255, 255, 0);
				graphics.fillRect(toPixel(pacmanX+1), toPixel(pacmanY+1), toPixel(blockSize-1), toPixel(blockSize-1));
				checkMaze();
			}
		} else {
			// demo
			updateWalls();
		}
		if(!dying) {
			// if we're not dying, we should move the ghosts (demo or game)
			int pos;
			int count;
			int j;

			for (i=0; i<ghostNum; i++) {
				if (ghostX[i]%blockSize==0 && ghostY[i]%blockSize==0) {
					pos=ghostX[i]/blockSize+xBlocknum*(int)(ghostY[i]/blockSize);
					count=0;
					int[] dx = new int[4], dy = new int[4];
					// no direction by default
					for(j=0;j<4;j++) {
						dx[j]=0;
						dy[j]=0;
					}
					if ((screenData[pos]&1)==0 && ghostDx[i]!=1) {
						dx[count]=-1;
						dy[count]=0;
						count++;
					}
					if ((screenData[pos]&2)==0 && ghostDy[i]!=1) {
						dx[count]=0;
						dy[count]=-1;
						count++;
					}
					if ((screenData[pos]&4)==0 && ghostDx[i]!=-1) {
						dx[count]=1;
						dy[count]=0;
						count++;
					}
					if ((screenData[pos]&8)==0 && ghostDy[i]!=-1) {
						dx[count]=0;
						dy[count]=1;
						count++;
					}
					if (count==0) {
						if ((screenData[pos]&15)==15) {
							ghostDx[i]=0;
							ghostDy[i]=0;
						} else {
							ghostDx[i]=-ghostDx[i];
							ghostDy[i]=-ghostDy[i];
						}
					} else {
						if(random == null)
							random = new Random();
						while(true) {
							// find a possible direction
							count = Math.abs(random.nextInt()%4);
							if(dx[count]==0 && dy[count]==0)
								continue;
							else
								break;
						}
						ghostDx[i]=dx[count]; // random: 0-3
						ghostDy[i]=dy[count];
					}
				}
				ghostX[i]=ghostX[i]+(ghostDx[i]*ghostSpeed[i]);
				ghostY[i]=ghostY[i]+(ghostDy[i]*ghostSpeed[i]);
				graphics.setColor(255, 0, 0);
				graphics.fillRect(toPixel(ghostX[i]+1), toPixel(ghostY[i]+1), toPixel(blockSize-1), toPixel(blockSize-1));

				if (pacmanX>(ghostX[i]-(blockSize/2)) && pacmanX<(ghostX[i]+(blockSize/2)) &&
						pacmanY>(ghostY[i]-(blockSize/2)) && pacmanY<(ghostY[i]+(blockSize/2)) && started) {
					if (scared) {
						score+=10;
						ghostX[i]=7*blockSize;
						ghostY[i]=6*blockSize;
					} else {
						dying=true;
						deathCounter=64;
					}
				}
			}
		}
		graphics.setColor(0, 0, 0);
		int bottomPos = toPixel(height)+((devHeight-toPixel(height)-font.getHeight())/2);
		graphics.drawString("Score: " + score, devWidth, bottomPos, Graphics.TOP | Graphics.RIGHT);
		graphics.drawString(hint, 0, bottomPos, Graphics.TOP | Graphics.LEFT);
		g.drawImage(image, 0, 0, 0);
	}

	/**
	 * Converts our device independent pixels to real ones.
	 * 
	 * Note that the target canvas is smaller than the one of the device to allow space for the status bar.
	 */
	private int toPixel(float p) {
		return (int)(p * ratio);
	}

	/**
	 * Resets the status to start a demo or a game.
	 */
	private void init() {
		pacsLeft = 3;
		score = 0;
		scaredTime = 120;
		initLevel();
		ghostNum = 6;
		currentSpeed = 4;
		resetMaze();
	}

	/**
	 * Reset maze status.
	 */
	private void resetMaze() {
		for (int i = 0; i<xBlocknum*yBlocknum; i++) {
			screenData[i]=levelData[i];
		}
	}

	/**
	 * Checks if this is the end of the current level or not.
	 */
	private void checkMaze() {
		short i=0;
		boolean finished=true;

		while (i < xBlocknum * yBlocknum && finished) {
			if ((screenData[i]&48)!=0)
				finished = false;
			i++;
		}
		if (finished) {
			score += 50;
			try { 
				Thread.sleep(3000);
			} catch (InterruptedException e) {
			}
			if (ghostNum < maxGhosts)
				ghostNum++; 
			if (currentSpeed<maxSpeed)
				currentSpeed+=2;
			scaredTime=scaredTime-20;
			if (scaredTime<minScaredTime)
				scaredTime=minScaredTime;
			initLevel();
			resetMaze();
		}
	}

	/**
	 * Lock / unlock the ghosts.
	 */
	private void updateWalls() {
		scaredCount--;
		if (scaredCount<=0)
			scared=false;

		if (scared) {
			screenData[6*xBlocknum+6]=11;
			screenData[6*xBlocknum+8]=14;
		} else {
			screenData[6*xBlocknum+6]=10;
			screenData[6*xBlocknum+8]=10;
		}
	}
}
