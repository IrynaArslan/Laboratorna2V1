/*
 * File: Breakout.java
 * -------------------
 * Name:
 * Section Leader:
 * 
 * This file will eventually implement the game of Breakout.
 */

import acm.graphics.*;
import acm.program.*;
import acm.util.*;

import java.applet.*;
import java.awt.*;
import java.awt.event.*;

public class Breakout extends GraphicsProgram {

	/** Width and height of application window in pixels */
	public static final int APPLICATION_WIDTH = 400;
	public static final int APPLICATION_HEIGHT = 600;

	/** Dimensions of game board (usually the same) */
	private static final int WIDTH = APPLICATION_WIDTH;
	private static final int HEIGHT = APPLICATION_HEIGHT;

	/** Dimensions of the paddle */
	private static final int PADDLE_WIDTH = 60;
	private static final int PADDLE_HEIGHT = 10;

	/** Offset of the paddle up from the bottom */
	private static final int PADDLE_Y_OFFSET = 30;

	/** Number of bricks per row */
	private static final int NBRICKS_PER_ROW = 10;

	/** Number of rows of bricks */
	private static final int NBRICK_ROWS = 10;

	/** Separation between bricks */
	private static final int BRICK_SEP = 4;

	/** Width of a brick */
	private static final int BRICK_WIDTH = (WIDTH - (NBRICKS_PER_ROW - 1)
			* BRICK_SEP)
			/ NBRICKS_PER_ROW;

	/** Height of a brick */
	private static final int BRICK_HEIGHT = 8;

	/** Radius of the ball in pixels */
	private static final int BALL_RADIUS = 10;

	/** Offset of the top brick row from the top */
	private static final int BRICK_Y_OFFSET = 70;

	/** Number of turns */
	private static final int NTURNS = 3;

	/** Offset of the 1st column of bricks from left */
	private static final int BRICK_X_OFFSET = (WIDTH
			- (NBRICKS_PER_ROW * BRICK_WIDTH) - ((NBRICKS_PER_ROW - 1) * BRICK_SEP)) / 2;

	private GRect brick[][] = new GRect[NBRICK_ROWS][NBRICKS_PER_ROW];

	private static final int DELAY = 10;

	private static final int RACKET_SPEED = 10;

	private RandomGenerator rgen = RandomGenerator.getInstance();
	private double vx, vy;
	private GOval bullet;
	private GObject collider;
	private int count;
	private boolean exit = false;
	private boolean over = true;
	private boolean win = false;
	private GImage racket;
	private GImage winner;
	private GImage gameover;
	private boolean pointTouch1, pointTouch2, pointTouch3, pointTouch4;
	private GObject point1, point2, point3, point4;

	/* Method: run() */
	/** Runs the Breakout program. */
	public void run() {
		/* You fill this in, along with any subsidiary methods */
		setup();
		while (true) {
			if (!exit) {
				moveBullet();
				checkBarriers();
				collisions();
				pause(DELAY);
			}
			if (exit && over && count != 100) {
				showPictureGameOver();
				over = false;
			} else if (count == 100 && win) {
				showPictureWinOver();
				win = true;
			}
		}
	}

	private void setup() {
		count = 0;
		racketCreate();
		winner = new GImage("win.png");
		gameover = new GImage("game_over.png");
		vx = speedBullet(vx);
		vy = -3.0;
		field(APPLICATION_WIDTH, APPLICATION_HEIGHT);
		this.setSize(APPLICATION_WIDTH, APPLICATION_HEIGHT);
		bullet = new GOval(BALL_RADIUS, BALL_RADIUS);
		bullet.setFilled(true);
		bullet.setColor(Color.GREEN);
		add(bullet, APPLICATION_WIDTH / 2, APPLICATION_HEIGHT / 2);
		addKeyListeners();
		addMouseListeners();
	}

	private void restart() {
		count = 0;
		exit = false;
		over = true;
		win = false;
		remove(gameover);
		remove(winner);
		vx = speedBullet(vx);
		vy = -3.0;
		for (int a = 0; a < NBRICK_ROWS; a++) {
			for (int b = 0; b < NBRICKS_PER_ROW; b++) {
				if (brick[a][b] != null || !brick[a][b].equals(null))
					remove(brick[a][b]);
			}
		}
		field(APPLICATION_WIDTH, APPLICATION_HEIGHT);
		add(bullet, APPLICATION_WIDTH / 2, APPLICATION_HEIGHT / 2);
	}

	private GImage racketCreate() {
		racket = new GImage("racket.GIF");
		racket.scale(1, 0.7);
		// racket.scale(1, 3);
		racket.sendToBack();
		add(racket, APPLICATION_WIDTH / 2 - racket.getWidth() / 2,
				APPLICATION_HEIGHT - racket.getHeight() - 10);
		return racket;
	}

	private void moveBullet() {
		if (bullet != null) {
			bullet.move(vx, vy);
		}
	}

	private void collisions() {
		pointTouch1 = false;
		pointTouch2 = false;
		pointTouch3 = false;
		pointTouch4 = false;
		collider = getCollidingObject();
		if (collider != null && collider != racket) {
			changeDirection();
			remove(collider);
			count++;
			if (count == 100) {
				win = true;
				exit = true;
			}
		}
		if (checkRacket(bullet.getX() + BALL_RADIUS / 2, bullet.getY())) {
			pointTouch1 = true;
			changeDirection();
		} else if (checkRacket(bullet.getX() + BALL_RADIUS, bullet.getY()
				+ BALL_RADIUS / 2)) {
			pointTouch2 = true;
			changeDirection();
		} else if (checkRacket(bullet.getX() + BALL_RADIUS / 2, bullet.getY()
				+ BALL_RADIUS)) {
			pointTouch3 = true;
			changeDirection();
		} else if (checkRacket(bullet.getX(), bullet.getY() + BALL_RADIUS / 2)) {
			pointTouch4 = true;
			changeDirection();
		}
	}

	private boolean checkRacket(double x, double y) {
		if (x >= racket.getX() && x <= (racket.getX() + racket.getWidth())) {
			if (y >= racket.getY() && y <= (racket.getY() + racket.getHeight())) {
				return true;
			}
		}
		return false;
	}

	private GObject getCollidingObject() {
		point1 = getElementAt(bullet.getX() + BALL_RADIUS / 2,
				bullet.getY() - 1);
		point2 = getElementAt(bullet.getX() + BALL_RADIUS, bullet.getY()
				+ BALL_RADIUS / 2 + 1);
		point3 = getElementAt(bullet.getX() + BALL_RADIUS / 2, bullet.getY()
				+ BALL_RADIUS + 1);
		point4 = getElementAt(bullet.getX() - 1, bullet.getY() + BALL_RADIUS
				/ 2);

		if (point1 != null) {
			pointTouch1 = true;
			return point1;
		} else if (point2 != null) {
			pointTouch2 = true;
			return point2;
		} else if (point3 != null) {
			pointTouch3 = true;
			return point3;
		} else if (point4 != null) {
			pointTouch4 = true;
			return point4;
		}
		return null;
	}

	private void showPictureGameOver() {
		add(gameover, (APPLICATION_WIDTH - winner.getWidth()) / 2,
				(APPLICATION_HEIGHT - winner.getHeight()) * 0.7);
	}

	private void showPictureWinOver() {
		add(winner, (APPLICATION_WIDTH - winner.getWidth()) / 2,
				(APPLICATION_HEIGHT - winner.getHeight()) * 0.5);
	}

	public void checkBarriers() {
		if (bullet.getY() + BALL_RADIUS > APPLICATION_HEIGHT) {
			if (vy > 0 && vx < 0)
				vy = -vy;
			if (vy > 0 && vx > 0)
				vy = -vy;
			// умова виходу мяча за нижню стінку
			exit = true;
		} else if (bullet.getX() + BALL_RADIUS > APPLICATION_WIDTH) {
			if (vy > 0 && vx > 0)
				vx = -vx;
			if (vy < 0 && vx > 0)
				vx = -vx;
		} else if (bullet.getY() < 0) {
			if (vy < 0 && vx > 0)
				vy = -vy;
			if (vy < 0 && vx < 0)
				vy = -vy;
		} else if (bullet.getX() < 0) {
			if (vy > 0 && vx < 0)
				vx = -vx;
			if (vy < 0 && vx < 0)
				vx = -vx;
		}
	}

	public void changeDirection() {
		if (pointTouch1 || pointTouch3) {
			vy = (-1) * vy;
		} else if (pointTouch2 || pointTouch4) {
			vx = (-1) * vx;
		}
	}

	public double speedBullet(double d) {
		d = rgen.nextDouble(1.0, 3.0);
		if (rgen.nextBoolean(0.5))
			d = -d;
		return d;
	}

	/* Method: painting rectangles on the field game */
	private void field(int width, int height) {
		this.setSize(width, height);
		int x = BRICK_X_OFFSET;
		int y = BRICK_Y_OFFSET;
		for (int a = 0; a < NBRICK_ROWS; a++) {
			for (int b = 0; b < NBRICKS_PER_ROW; b++) {
				brick[a][b] = new GRect(x, y, BRICK_WIDTH, BRICK_HEIGHT);
				if (a == 0 || a == 1) {
					brick[a][b].setFilled(true);
					brick[a][b].setFillColor(Color.RED);
				} else if (a == 2 || a == 3) {
					brick[a][b].setFilled(true);
					brick[a][b].setFillColor(Color.ORANGE);
				} else if (a == 4 || a == 5) {
					brick[a][b].setFilled(true);
					brick[a][b].setFillColor(Color.YELLOW);
				} else if (a == 6 || a == 7) {
					brick[a][b].setFilled(true);
					brick[a][b].setFillColor(Color.GREEN);
				} else if (a == 8 || a == 9) {
					brick[a][b].setFilled(true);
					brick[a][b].setFillColor(Color.CYAN);
				}
				add(brick[a][b]);
				x += (BRICK_WIDTH + BRICK_SEP);
			}
			y += (BRICK_HEIGHT + BRICK_SEP);
			x = BRICK_X_OFFSET;
		}
	}

	/** Press "left button" to move racket left, "right" - to right */
	public void keyPressed(KeyEvent e) {
		// System.out.println(e.getKeyCode());
		if (e.getKeyCode() == 37) {
			moveRacketLeft();
		} else if (e.getKeyCode() == 39) {
			moveRacketRight();
		}
	}

	public void mousePressed(MouseEvent e) {
		// GPoint has X and Y coordinate
		if (exit) {
			restart();
		}
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		if (e.getX() - racket.getWidth() / 2 > 0
				&& e.getX() + racket.getWidth() / 2 < APPLICATION_WIDTH) {
			racket.move(e.getX() - racket.getX() - racket.getWidth() / 2, 0);
		}

	}

	private void moveRacketLeft() {
		if (racket.getX() > 0 + RACKET_SPEED) {
			racket.move(-RACKET_SPEED, 0);
		}
	}

	private void moveRacketRight() {
		if (racket.getX() < APPLICATION_WIDTH - racket.getWidth()
				- RACKET_SPEED) {
			racket.move(RACKET_SPEED, 0);
		}

	}
}
