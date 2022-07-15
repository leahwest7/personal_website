package com.gradescope.spampede;

import java.awt.event.KeyEvent;
import java.util.LinkedList;
import java.util.Queue;

/**
 * The "controller" in MVC that is responsible for the logic of the game, e.g.
 * deciding how to move the snake, as well as handling keystrokes and
 * controlling the timesteps that move the snake forward.
 * 
 * @author CS60 instructors
 */
public class SpampedeBrain extends SpampedeBrainParent {

	/** The "view" in MVC. */
	private SpampedeDisplay theDisplay;

	/** The "model" in MVC. */
	private SpampedeData theData;

	/** The number of animated frames displayed so far. */
	private int cycleNum = 0;

	/** The mappings between direction (names) and keys. */
	private static final char REVERSE = 'r';
	private static final char UP = 'i';
	private static final char DOWN = 'k';
	private static final char LEFT = 'j';
	private static final char RIGHT = 'l';
	private static final char AI_MODE = 'a';
	private static final char PLAY_SPAM_NOISE = 's';

	/** Starts a new game. */
	public void startNewGame() {
		this.theData = new SpampedeData();
		this.theData.placeSnakeAtStartLocation();
		this.theData.setStartDirection();

		this.theDisplay = new SpampedeDisplay(this.theData, this.screen, this.getSize().width, getSize().height);
		this.theDisplay.updateGraphics();

		this.playSound_spam();

		/**
		 * Hack because pictures have a delay in loading, and we do not redraw the
		 * screen again until the game actually starts, which means we would not see the
		 * image until the game does start. Wait a fraction of a second (200 ms), by
		 * which time the picture should have been fetched from disk, and redraw.
		 */
		try {
			Thread.sleep(200);
		} catch (InterruptedException e) {
		}
		;

		this.theDisplay.updateGraphics();
	}

	/** Declares the game over. */
	public void gameOver() {
		super.pause(); // pause the game
		this.theData.setGameOver(); // tell the model that the game is over
		if (this.audioMeow != null) {
			this.audioMeow.play(); // play a sound
		}
	}

	/* -------- */
	/* Gameplay */
	/* -------- */

	/**
	 * Moves the game forward one step.
	 * 
	 * One step is one frame of animation, which occurs every Preferences.SLEEP_TIME
	 * milliseconds.
	 */
	public void cycle() {
		// move the snake
		this.updateSnake();

		// update the list of spam
		this.updateSpam();

		// draw the board
		this.theDisplay.updateGraphics();

		// send the new drawing to the screen
		this.repaint();

		// update the cycle counter
		this.cycleNum++;
	}

	/**
	 * Reacts to characters typed by the user.
	 * 
	 * <p>
	 * SpampedeBrainParent registers SpampedeBrain as an "observer" for key presses
	 * on the keyboard. So, whenever the user presses a key, Java automatically
	 * calls this keyPressed method and passes it a KeyEvent describing the specific
	 * key press.
	 * </p>
	 */
	public void keyPressed(KeyEvent evt) {

		switch (evt.getKeyChar()) {
		// get the char of the pressed key
		case UP:
			this.theData.setDirectionNorth();
			break;
		case DOWN:
			this.theData.setDirectionSouth();
			break;
		case LEFT:
			this.theData.setDirectionWest();
			break;
		case RIGHT:
			this.theData.setDirectionEast();
			break;
		case REVERSE:
			this.reverseSnake();
			break;
		case AI_MODE:
			this.theData.setMode_AI();
			break;
		case PLAY_SPAM_NOISE:
			this.playSound_spam();
			break;
		default:
			this.theData.setDirectionEast();
		}
	}

	/**
	 * Moves the snake.
	 * 
	 * <p>
	 * This method is called once every REFRESH_RATE cycles, either in the current
	 * direction, or as directed by the AI's breadth-first search.
	 * 
	 * Called by cycle method
	 * <p>
	 */
	public void updateSnake() {
		if (this.cycleNum % Preferences.REFRESH_RATE == 0) {
			BoardCell nextCell;
			if (this.theData.inAImode()) {
				nextCell = this.getNextCellFromBFS();
			} else {
				nextCell = this.theData.getNextCellInDir();
			}
			this.advanceTheSnake(nextCell);
		}
	}

	/**
	 * Moves the snake to the next cell (and possibly eat spam).
	 * 
	 * @param nextCell - the new location of the snake head (which must be
	 *                 horizontally or vertically adjacent to the old location of
	 *                 the snake head)
	 */
	private void advanceTheSnake(BoardCell nextCell) {
		// DO NOT MODIFY THE PROVIDED CODE
		if (nextCell.isWall() || nextCell.isBody()) {
			// oops...we hit something
			this.gameOver();
			return;
		} else if (nextCell.isSpam()) {
			// the snake ate spam!
			this.playSound_spamEaten();
			// Tell theData the snake ate spam!
			this.theData.updateHead(nextCell);
		} else {
			// Snake did NOT eat spam
			this.theData.updateHead(nextCell);
			this.theData.updateTail();

		}
	}

	/**
	 * Adds more spam every SPAM_ADD_RATE cycles.
	 */
	void updateSpam() {
		if (this.theData.noSpam()) {
			this.theData.addSpam();
		} else if (this.cycleNum % Preferences.SPAM_ADD_RATE == 0) {
			this.theData.addSpam();
		}
	}

	/**
	 * Searches for the spam closest to the snake head using BFS.
	 * 
	 * @return the cell to move the snake head to, if the snake moves *one step*
	 *         along the shortest path to (the nearest) spam cell
	 */
	public BoardCell getNextCellFromBFS() {
		// initialize the search
		theData.resetCellsForNextSearch();

		// initialize the cellsToSearch queue with the snake head;
		// as with any cell, we mark the head cells as having been added
		// to the queue
		Queue<BoardCell> cellsToSearch = new LinkedList<BoardCell>();
		BoardCell snakeHead = theData.getSnakeHead();
		snakeHead.setAddedToSearchList();
		cellsToSearch.add(snakeHead);
		// BoardCell closestSpamCell = null;
		// search!
		while (!cellsToSearch.isEmpty()) {
			BoardCell removeCell = cellsToSearch.remove();
			if (removeCell.isSpam()) {
				return this.getFirstCellInPath(removeCell);
			}
			// neighbor in get Neighbors list
			for (BoardCell neighbor : this.theData.getNeighbors(removeCell)) {
				// if not in search and it's empty
				if (neighbor.isOpen() && !neighbor.inSearchListAlready()) { 
					// add to queue
					cellsToSearch.add(neighbor);
					// set to parent
					neighbor.setParent(removeCell);
					// add to search list
					neighbor.setAddedToSearchList();
				}
			}
		}
		return this.theData.getRandomNeighboringCell(snakeHead);
	}

	/**
	 * Follows the traceback pointers from the closest spam cell to decide where the
	 * head should move. Specifically, follows the parent pointers back from the
	 * spam until we find the cell whose parent is the snake head (and which must
	 * therefore be adjacent to the previous snake head location).
	 * 
	 * @param start - the cell from which to start following pointers, typically the
	 *              location of the spam closest to the snake head
	 * @return the cell to move the snake head to, which should be a neighbor of the
	 *         head
	 */
	private BoardCell getFirstCellInPath(BoardCell start) {
		BoardCell cell = start;
		// recurses from start cell until snakeHead
		while (cell != this.theData.getSnakeHead()) {
			BoardCell cell2 = cell.getParent();
			if (cell2 == this.theData.getSnakeHead()) {
				return cell;
			}
			cell = cell2;
		}
		return null;
	}

	/**
	 * Reverses the snake back-to-front and updates the movement mode appropriately.
	 */
	public void reverseSnake() {
		this.theData.unlabel();
		this.theData.reverse();
		this.theData.relabelHead();
		this.theData.newDirection();
	}

	/* ------ */
	/* Sounds */
	/* ------ */

	/** Plays crunch noise. */
	public void playSound_spamEaten() {
		if (this.audioCrunch != null) {
			this.audioCrunch.play();
		}
	}

	/** Plays spam noise. */
	public void playSound_spam() {
		if (this.audioSpam != null) {
			this.audioSpam.play();
		}
	}

	/** Plays meow noise. */
	public void playSound_meow() {
		if (this.audioMeow != null) {
			this.audioMeow.play();
		}
	}

	/** Added to avoid a warning - not used! */
	private static final long serialVersionUID = 1L;

	/* ---------------------- */
	/* Testing Infrastructure */
	/* ---------------------- */

	public static SpampedeBrain getTestGame(TestGame gameNum) {
		SpampedeBrain brain = new SpampedeBrain();
		brain.theData = new SpampedeData(gameNum);
		return brain;
	}

	public String testing_toStringParent() {
		return this.theData.toStringParents();
	}

	public BoardCell testing_getNextCellInDir() {
		return this.theData.getNextCellInDir();
	}

	public String testing_toStringSpampedeData() {
		return this.theData.toString();
	}
}
