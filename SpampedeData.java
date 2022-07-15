package com.gradescope.spampede;

import java.awt.Color;
import java.lang.Math;
import java.util.LinkedList;
import java.util.Iterator;

/**
 * The "model" in MVC that is responsible for storing all the data for the
 * board.
 * 
 * @author CS60 instructors
 */
class SpampedeData {
	/**
	 * The collection of all the BoardCells in the program, indexed by row and
	 * column.
	 * 
	 * <p>
	 * All BoardCells needed by the program are created by the SpampedeData
	 * constructor, so you do not need to create any new BoardCells in your code.
	 * Instead, you will pass around (references to) existing cells, and change the
	 * contents of some of these cells.
	 * </p>
	 */
	private final BoardCell[][] boardCells2D;

	/**
	 * The number of non-wall cells in the initial board.
	 */
	private int freeSpots = 0;

	/**
	 * The current movement "mode" of the snake, i.e. whether it is headed in a
	 * particular direction or in AI mode.
	 */
	private SnakeMode currentMode = SnakeMode.GOING_EAST;

	/**
	 * A list of (references to) cells that currently contain spam, ordered from
	 * oldest (first) to youngest (last).
	 */
	private LinkedList<BoardCell> spamCells = new LinkedList<BoardCell>();

	/**
	 * A list of (references to) the cells that contain the snake. The head is the
	 * last element of the list.
	 */
	private LinkedList<BoardCell> snakeCells = new LinkedList<BoardCell>();

	/**
	 * Whether the game is over.
	 */
	private boolean gameOver = false;

	/* -------------------------------------- */
	/* Constructor and initialization methods */
	/* -------------------------------------- */

	/**
	 * Creates a new "board" with walls on the boundary and open in the interior.
	 */
	public SpampedeData() {
		int height = Preferences.NUM_CELLS_TALL;
		int width = Preferences.NUM_CELLS_WIDE;
		this.boardCells2D = new BoardCell[height][width];

		// Place walls around the outside
		this.addWalls();

		// Fill the remaining cells not already filled!
		this.fillRemainingCells();
	}

	/**
	 * Adds walls around the edges of this board.
	 */
	private void addWalls() {
		int height = this.getNumRows();
		int width = this.getNumColumns();

		// Add left and right walls
		for (int row = 0; row < height; row++) {
			this.boardCells2D[row][0] = new BoardCell(row, 0, CellType.WALL);
			this.boardCells2D[row][width - 1] = new BoardCell(row, width - 1, CellType.WALL);
		}
		// Add top and bottom walls
		for (int column = 0; column < width; column++) {
			this.boardCells2D[0][column] = new BoardCell(0, column, CellType.WALL);
			this.boardCells2D[height - 1][column] = new BoardCell(height - 1, column, CellType.WALL);
		}
	}

	/**
	 * Adds open cells to the interior of this board.
	 */
	private void fillRemainingCells() {
		int height = this.getNumRows();
		int width = this.getNumColumns();

		this.freeSpots = 0;
		for (int row = 0; row < height; row++) {
			for (int column = 0; column < width; column++) {
				if (this.boardCells2D[row][column] == null) {
					this.boardCells2D[row][column] = new BoardCell(row, column, CellType.OPEN);
					this.freeSpots++;
				}
			}
		}
	}

	/**
	 * Puts the snake in the upper-left corner of the walls, facing east.
	 */
	public void placeSnakeAtStartLocation() {
		BoardCell body = this.getCell(1, 1);
		BoardCell head = this.getCell(1, 2);
		this.snakeCells.addLast(body);
		this.snakeCells.addLast(head);
		head.becomeHead();
		body.becomeBody();
	}

	/* ---------------------------------------------- */
	/* Methods to access information about this board */
	/* ---------------------------------------------- */

	/**
	 * Returns true if we are in AI mode.
	 */
	public boolean inAImode() {
		return this.currentMode == SnakeMode.AI_MODE;
	}

	/**
	 * Returns the height of this board (including walls) in cells.
	 */
	public int getNumRows() {
		return this.boardCells2D.length;
	}

	/**
	 * Returns the width of this board (including walls) in cells.
	 */
	public int getNumColumns() {
		return this.boardCells2D[0].length;
	}

	/**
	 * Accesses a cell at a particular location.
	 * 
	 * <p>
	 * This method should really be private. We make it public to allow our unit
	 * tests to use it, but it should not be called from SpampedeBrain or
	 * SpampedeDisplay.
	 * </p>
	 * 
	 * @param r - the row to access, between 0 and numRows-1 inclusive
	 * @param c - the column to access, between 0 and numCols-1 inclusive
	 * @return the cell in row r and column c
	 */
	protected BoardCell getCell(int r, int c) {
		if (r >= this.getNumRows() || c >= this.getNumColumns() || r < 0 || c < 0) {
			System.err.println("Trying to access cell outside of the Board:");
			System.err.println("row: " + r + " col: " + c);
			System.exit(0);
		}
		return this.boardCells2D[r][c];
	}

	/* ------------------------------ */
	/* Helper method used by the view */
	/* ------------------------------ */

	/**
	 * Gets the color of the cell at a particular location.
	 * 
	 * @param r - the row to access, between 0 and numRows-1 inclusive
	 * @param c - the column to access, between 0 and numCols-1 inclusive
	 * @return the color of cell at row r and column c
	 */
	public Color getCellColor(int row, int col) {
		BoardCell cell = getCell(row, col);
		return cell.getCellColor();
	}

	/* ---------------- */
	/* Game over status */
	/* ---------------- */

	/**
	 * Sets the game status as game over.
	 */
	public void setGameOver() {
		this.gameOver = true;
	}

	/**
	 * Returns true if the game over message should be displayed.
	 */
	public boolean getGameOver() {
		return this.gameOver;
	}

	/* -------------------- */
	/* Spam-related methods */
	/* -------------------- */

	/**
	 * Returns true if there is zero spam.
	 */
	public boolean noSpam() {
		return this.spamCells.isEmpty();
	}

	/**
	 * Adds spam to a random open spot.
	 */
	public void addSpam() {
		// Pick a random cell
		int row = (int) (this.getNumRows() * Math.random());
		int column = (int) (this.getNumColumns() * Math.random());
		BoardCell cell = this.getCell(row, column);

		if (cell.isOpen()) {
			// If the random cell is open, put spam there
			cell.becomeSpam();
			spamCells.addLast(cell);
		} else {
			// If the random cell is occupied and this board is not already
			// too full of spam, try to place spam again
			double totalSize = this.getNumColumns() * this.getNumRows();
			double currentFreeSpots = this.freeSpots - this.snakeCells.size() - this.spamCells.size();
			double ratioFree = currentFreeSpots / totalSize;
			if (ratioFree < 0.2) {
				System.err.println("Not adding more spam");
			} else {
				addSpam();
			}
		}
	}

	/**
	 * Removes the oldest piece of un-eaten spam.
	 * 
	 * <p>
	 * The function is not used in the given code, but it might be useful if you
	 * want to extend the game.
	 * </p>
	 */
	@SuppressWarnings("unused")
	private void removeSpam() {
		if (!spamCells.isEmpty()) {
			spamCells.peekFirst().becomeOpen();
			spamCells.removeFirst();
		}
	}

	/* -------------------- */
	/* Snake access methods */
	/* -------------------- */

	/**
	 * Returns the cell containing the snake's head.
	 */
	public BoardCell getSnakeHead() {
		return this.snakeCells.peekLast();
	}

	/**
	 * Returns the cell containing the snake's tail.
	 */
	public BoardCell getSnakeTail() {
		return this.snakeCells.peekFirst();
	}

	/**
	 * Returns the cell contains the snake body adjacent to the head.
	 */
	public BoardCell getSnakeNeck() {
		int lastSnakeCellIndex = this.snakeCells.size() - 1;
		return this.snakeCells.get(lastSnakeCellIndex - 1);
	}

	/* ------------------------------------------ */
	/* Methods to set the snake's (movement) mode */
	/* ------------------------------------------ */

	/**
	 * Makes the snake head north.
	 */
	public void setDirectionNorth() {
		this.currentMode = SnakeMode.GOING_NORTH;
	}

	/**
	 * Makes the snake head south.
	 */
	public void setDirectionSouth() {
		this.currentMode = SnakeMode.GOING_SOUTH;
	}

	/**
	 * Makes the snake head east.
	 */
	public void setDirectionEast() {
		this.currentMode = SnakeMode.GOING_EAST;
	}

	/**
	 * Makes the snake head west.
	 */
	public void setDirectionWest() {
		this.currentMode = SnakeMode.GOING_WEST;
	}

	/**
	 * Makes the snake switch to AI mode.
	 */
	public void setMode_AI() {
		this.currentMode = SnakeMode.AI_MODE;
	}

	/**
	 * Picks an initial movement mode for the snake.
	 */
	public void setStartDirection() {
		this.setDirectionEast();
	}

	/* ---------------------- */
	/* Snake movement methods */
	/* ---------------------- */

	/**
	 * Makes next cell the new snakeHead 
	 * Current head becomes body
	 */
	public void updateHead(BoardCell nextCell) {
		this.getSnakeHead().becomeBody();
		this.snakeCells.addLast(nextCell);
		nextCell.becomeHead();
	}

	/**
	 * Makes previous snakeTail an open cell 
	 *  
	 */
	public void updateTail() {
		BoardCell snakeTailCell = this.getSnakeTail();
		this.snakeCells.removeFirst();
		snakeTailCell.becomeOpen();
	}

	/* -------------------------------------- */
	/* Methods to support movement without AI */
	/* -------------------------------------- */

	/**
	 * These methods should really be private. We make them public to allow access
	 * by our unit tests, but the methods should not be called from SpampedeBrain or
	 * SpampedeDisplay.
	 */

	/**
	 * Returns the cell north of the specified cell, which must not be on the
	 * boundary.
	 */
	protected BoardCell getNorthNeighbor(BoardCell cell) {
		return this.getCell(cell.getRow() - 1, cell.getColumn());
	}

	/**
	 * Returns the cell south of the specified cell, which must not be on the
	 * boundary.
	 */
	protected BoardCell getSouthNeighbor(BoardCell cell) {
		return this.getCell(cell.getRow() + 1, cell.getColumn());

	}

	/**
	 * Returns the cell east of the specified cell, which must not be on the
	 * boundary.
	 */
	protected BoardCell getEastNeighbor(BoardCell cell) {
		return this.getCell(cell.getRow(), cell.getColumn() + 1);
	}

	/**
	 * Returns the cell west of the specified cell, which must not be on the
	 * boundary.
	 */
	protected BoardCell getWestNeighbor(BoardCell cell) {
		return this.getCell(cell.getRow(), cell.getColumn() - 1);
	}

	/**
	 * Returns the cell north of the snake's head.
	 */
	protected BoardCell getNorthNeighbor() {
		return this.getNorthNeighbor(this.getSnakeHead());
	}

	/**
	 * Returns the cell south of the snake's head.
	 */
	protected BoardCell getSouthNeighbor() {
		return this.getSouthNeighbor(this.getSnakeHead());
	}

	/**
	 * Returns the cell east of the snake's head.
	 */
	protected BoardCell getEastNeighbor() {
		return this.getEastNeighbor(this.getSnakeHead());
	}

	/**
	 * Returns the cell west of the snake's head.
	 */
	protected BoardCell getWestNeighbor() {
		return this.getWestNeighbor(this.getSnakeHead());
	}

	/**
	 * Returns the cell north, south, east, or west of the snake head based on the
	 * current direction of travel. This method should not be called when in AI
	 * mode, though Java requires the method to return a value regardless.
	 */
	public BoardCell getNextCellInDir() {
		if (this.currentMode == SnakeMode.GOING_NORTH) {
			return this.getNorthNeighbor();
		}
		if (this.currentMode == SnakeMode.GOING_EAST) {
			return this.getEastNeighbor();
		}
		if (this.currentMode == SnakeMode.GOING_SOUTH) {
			return this.getSouthNeighbor();
		} else {
			return this.getWestNeighbor();
		}

	}

	/* -------------------------------------------------- */
	/* Public methods to get all or one (random) neighbor */
	/* -------------------------------------------------- */

	/**
	 * Returns an array of the four neighbors of the specified cell.
	 */
	public BoardCell[] getNeighbors(BoardCell center) {
		BoardCell[] neighborsArray = { getNorthNeighbor(center), getSouthNeighbor(center), getEastNeighbor(center),
				getWestNeighbor(center) };
		return neighborsArray;
	}

	/**
	 * Returns a random open neighbor of the specified cell (or some other neighbor
	 * if there are no open neighbors).
	 */
	public BoardCell getRandomNeighboringCell(BoardCell start) {
		BoardCell[] neighborsArray = getNeighbors(start);
		for (BoardCell mc : neighborsArray) {
			if (mc.isOpen()) {
				return mc;
			}
		}
		// if we did not find an open space, return the first neighbor
		return neighborsArray[0];
	}

	/* ---------------------------- */
	/* Helper method(s) for reverse */
	/* ---------------------------- */

	/**
	 * Unlabels the head of the snake
	 */
	
	public void unlabel() {
		this.getSnakeHead().becomeBody();
	}

	/**
	 * reverse the body parts of the snake 
	 */
	public void reverse() {
		LinkedList<BoardCell> newlist = new LinkedList<BoardCell>(); 
		//iterates through newlist
		Iterator<BoardCell> iter = this.snakeCells.iterator(); 
		while (iter.hasNext()) {
			newlist.addFirst(iter.next());
		}
		this.snakeCells = newlist;
	}

	/**
	 * relabel the tail to be the head of the snake
	 */
	public void relabelHead() {
		this.getSnakeTail().becomeHead();
	}

	/**
	 * Calculates the new direction after reversing. 
	 * It does so by looking at the direction made between the neck and head of the snake.
	 */
	public void newDirection() {
		BoardCell snakeNeckCell = this.getSnakeNeck();
		if (this.getNorthNeighbor(snakeNeckCell) == this.getSnakeHead()) {
			this.currentMode = SnakeMode.GOING_NORTH;
		}
		if (this.getSouthNeighbor(snakeNeckCell) == this.getSnakeHead()) {
			this.currentMode = SnakeMode.GOING_SOUTH;
		}
		if (this.getEastNeighbor(snakeNeckCell) == this.getSnakeHead()) {
			this.currentMode = SnakeMode.GOING_EAST;
		}
		if (this.getWestNeighbor(snakeNeckCell) == this.getSnakeHead()) {
			this.currentMode = SnakeMode.GOING_WEST;
		}
	}

	/* ------------------------------------- */
	/* Methods to reset the model for search */
	/* ------------------------------------- */

	/**
	 * Clears the search-related fields in all the cells, in preparation for a new
	 * breadth-first search.
	 */
	public void resetCellsForNextSearch() {
		for (BoardCell[] row : this.boardCells2D) {
			for (BoardCell cell : row) {
				cell.clear_RestartSearch();
			}
		}
	}

	/* -------------------------------------------------------------------- */
	/* Testing Infrastructure - You do not need to understand these methods */
	/* -------------------------------------------------------------------- */

	/**
	 * Pictures of test boards at http://tinyurl.com/spampedeTestBoards
	 */

	// Constructor used exclusively for testing!
	public SpampedeData(TestGame gameNum) {
		this.boardCells2D = new BoardCell[6][6];
		this.addWalls();
		this.fillRemainingCells();
		if (gameNum.snakeAtStart()) {
			this.testing_snakeAtStartLocation(gameNum);
			this.setDirectionEast();
		} else {
			this.testing_snakeNotAtStartLocation(gameNum);
		}

	}

	private void testing_snakeAtStartLocation(TestGame gameNum) {
		this.placeSnakeAtStartLocation();
		if (gameNum == TestGame.G1) {
			this.getCell(1, 3).becomeSpam();
		} else if (gameNum == TestGame.G2) {
			this.getCell(2, 2).becomeSpam();
		} else if (gameNum == TestGame.G3) {
			this.getCell(1, 4).becomeSpam();
		} else if (gameNum == TestGame.G4) {
			this.getCell(2, 1).becomeSpam();
		} else if (gameNum == TestGame.G5) {
			this.getCell(4, 1).becomeSpam();
		} else if (gameNum == TestGame.G6) {
			this.getCell(1, 3).becomeSpam();
			this.getCell(3, 1).becomeSpam();
		} else if (gameNum == TestGame.G7) {
			this.getCell(2, 2).becomeSpam();
			this.getCell(1, 4).becomeSpam();
		} else if (gameNum == TestGame.G8) {
			this.getCell(1, 4).becomeSpam();
			this.getCell(4, 2).becomeSpam();
		} else if (gameNum == TestGame.G9) {
			this.getCell(2, 1).becomeSpam();
			this.getCell(2, 4).becomeSpam();
		} else if (gameNum == TestGame.G10) {
			this.getCell(4, 1).becomeSpam();
			this.getCell(4, 4).becomeSpam();
		} else if (gameNum == TestGame.G11) {
			// No spam :)
		}
		// Add all spam to the spam cells
		int height = this.getNumRows();
		int width = this.getNumColumns();
		for (int row = 0; row < height; row++) {
			for (int column = 0; column < width; column++) {
				BoardCell cell = this.getCell(row, column);
				if (cell.isSpam()) {
					this.spamCells.add(cell);
				}
			}
		}
	}

	private void testing_snakeNotAtStartLocation(TestGame gameNum) {
		if (gameNum == TestGame.G12) {
			BoardCell body2 = this.getCell(2, 3);
			BoardCell body1 = this.getCell(2, 2);
			BoardCell head = this.getCell(2, 1);
			this.snakeCells.add(body2);
			this.snakeCells.add(body1);
			this.snakeCells.add(head);
			head.becomeHead();
			body2.becomeBody();
			body1.becomeBody();
		} else if (gameNum == TestGame.G13) {
			BoardCell body2 = this.getCell(3, 2);
			BoardCell body1 = this.getCell(2, 2);
			BoardCell head = this.getCell(2, 1);
			this.snakeCells.add(body2);
			this.snakeCells.add(body1);
			this.snakeCells.add(head);
			head.becomeHead();
			body2.becomeBody();
			body1.becomeBody();
		} else if (gameNum == TestGame.G14) {
			BoardCell body2 = this.getCell(2, 2);
			BoardCell body1 = this.getCell(3, 2);
			BoardCell head = this.getCell(3, 1);
			this.snakeCells.add(body2);
			this.snakeCells.add(body1);
			this.snakeCells.add(head);
			head.becomeHead();
			body2.becomeBody();
			body1.becomeBody();
		} else if (gameNum == TestGame.G15) {
			BoardCell body2 = this.getCell(3, 2);
			BoardCell body1 = this.getCell(3, 3);
			BoardCell head = this.getCell(3, 4);
			this.snakeCells.add(body2);
			this.snakeCells.add(body1);
			this.snakeCells.add(head);
			head.becomeHead();
			body2.becomeBody();
			body1.becomeBody();
		}
	}

	public String toString() {
		String result = "";
		for (int r = 0; r < this.getNumRows(); r++) {
			for (int c = 0; c < this.getNumColumns(); c++) {
				BoardCell cell = this.getCell(r, c);
				result += cell.toStringType();
			}
			result += "\n";
		}
		return result;
	}

	public String toStringParents() {
		String result = "";
		for (int r = 0; r < this.getNumRows(); r++) {
			for (int c = 0; c < this.getNumColumns(); c++) {
				BoardCell cell = this.getCell(r, c);
				result += cell.toStringParent() + "\t";
			}
			result += "\n";
		}
		return result;
	}

}
