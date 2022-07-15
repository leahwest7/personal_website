package com.gradescope.spampede;

import static org.junit.Assert.*;

import org.junit.Test;

public class SpampedeBrainTest_GetNextCellFromBFS {
	/**
	 * Pictures of test boards at http://tinyurl.com/spampedeTestBoards
	 */

	@Test
	public void testG1_BFS() {
		SpampedeBrain brain = SpampedeBrain.getTestGame(TestGame.G1);
		BoardCell nextCell = brain.getNextCellFromBFS();
		assertEquals("[1, 3, X]", nextCell.toString());
	}

	@Test
	public void testG2_BFS() {
		SpampedeBrain brain = SpampedeBrain.getTestGame(TestGame.G2);
		BoardCell nextCell = brain.getNextCellFromBFS();
		assertEquals("[2, 2, X]", nextCell.toString());
	}

	@Test
	public void testG3_BFS() {
		SpampedeBrain brain = SpampedeBrain.getTestGame(TestGame.G3);
		BoardCell nextCell = brain.getNextCellFromBFS();
		assertEquals("[1, 3,  ]", nextCell.toString());
	}

	@Test
	public void testG4_BFS() {
		SpampedeBrain brain = SpampedeBrain.getTestGame(TestGame.G4);
		BoardCell nextCell = brain.getNextCellFromBFS();
		assertEquals("[2, 2,  ]", nextCell.toString());
	}

	@Test
	public void testG5_BFS() {
		SpampedeBrain brain = SpampedeBrain.getTestGame(TestGame.G5);
		BoardCell nextCell = brain.getNextCellFromBFS();
		assertEquals("[2, 2,  ]", nextCell.toString());
	}

	@Test
	public void testG6_BFS() {
		SpampedeBrain brain = SpampedeBrain.getTestGame(TestGame.G6);
		BoardCell nextCell = brain.getNextCellFromBFS();
		assertEquals("[1, 3, X]", nextCell.toString());
	}

	@Test
	public void testG7_BFS() {
		SpampedeBrain brain = SpampedeBrain.getTestGame(TestGame.G7);
		BoardCell nextCell = brain.getNextCellFromBFS();
		assertEquals("[2, 2, X]", nextCell.toString());
	}

	@Test
	public void testG8_BFS() {
		SpampedeBrain brain = SpampedeBrain.getTestGame(TestGame.G8);
		BoardCell nextCell = brain.getNextCellFromBFS();
		assertEquals("[1, 3,  ]", nextCell.toString());
	}

	@Test
	public void testG9_BFS() {
		SpampedeBrain brain = SpampedeBrain.getTestGame(TestGame.G9);
		BoardCell nextCell = brain.getNextCellFromBFS();
		assertEquals("[2, 2,  ]", nextCell.toString());
	}

	@Test
	public void testG10_BFS() {
		SpampedeBrain brain = SpampedeBrain.getTestGame(TestGame.G10);
		BoardCell nextCell = brain.getNextCellFromBFS();
		assertEquals("[2, 2,  ]", nextCell.toString());
		System.out.println(brain.testing_toStringParent());
	}

	@Test
	public void testG11_BFS() {
		SpampedeBrain brain = SpampedeBrain.getTestGame(TestGame.G11);
		BoardCell nextCell = brain.getNextCellFromBFS();
		// NEED AN OR!
		String possibleResult1 = "[1, 3,  ]";
		String possibleResult2 = "[2, 2,  ]";
		String nextCellString = nextCell.toString();
		assertTrue(possibleResult1.equals(nextCellString) || possibleResult2.equals(nextCellString));
	}

}
