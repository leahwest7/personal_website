package com.gradescope.spampede;

import static org.junit.Assert.*;

import org.junit.Test;

public class SpampedeBrainTest_Reverse {
	/**
	 * Pictures of test boards at http://tinyurl.com/spampedeTestBoards
	 */
	
	@Test
	public void test_ReverseNorth() {
		SpampedeBrain brain = SpampedeBrain.getTestGame(TestGame.G14);
		brain.reverseSnake();
		BoardCell nextCell = brain.testing_getNextCellInDir();
		assertEquals("[1, 2,  ]", nextCell.toString());
	}

	@Test
	public void test_ReverseSouth() {
		SpampedeBrain brain = SpampedeBrain.getTestGame(TestGame.G13);
		brain.reverseSnake();
		BoardCell nextCell = brain.testing_getNextCellInDir();
		assertEquals("[4, 2,  ]", nextCell.toString());
	}

	@Test
	public void test_ReverseEast() {
		SpampedeBrain brain = SpampedeBrain.getTestGame(TestGame.G12);
		brain.reverseSnake();
		BoardCell nextCell = brain.testing_getNextCellInDir();
		assertEquals("[2, 4,  ]", nextCell.toString());
	}

	@Test
	public void test_ReverseWest() {
		SpampedeBrain brain = SpampedeBrain.getTestGame(TestGame.G15);
		brain.reverseSnake();
		BoardCell nextCell = brain.testing_getNextCellInDir();
		assertEquals("[3, 1,  ]", nextCell.toString());
	}

}
