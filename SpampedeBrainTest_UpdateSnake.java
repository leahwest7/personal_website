package com.gradescope.spampede;

import static org.junit.Assert.*;

import org.junit.Test;

public class SpampedeBrainTest_UpdateSnake {

	@Test
	public void test_eatSpam() {
		SpampedeBrain brain = SpampedeBrain.getTestGame(TestGame.G1);
		brain.updateSnake();
		String boardString = brain.testing_toStringSpampedeData();
		String correctBoardString = "******\n" + "*BBH *\n" + "*    *\n" + "*    *\n" + "*    *\n" + "******\n";
		// Sample debugging output:
		// System.out.println("G1");
		// System.out.println("Expected:");
		// System.out.println(correctBoardString);
		// System.out.println("Actual:");
		// System.out.println(boardString);
		assertEquals(correctBoardString, boardString);
	}

	@Test
	public void test_noSpamEaten() {
		SpampedeBrain brain = SpampedeBrain.getTestGame(TestGame.G2);
		brain.updateSnake();
		String boardString = brain.testing_toStringSpampedeData();
		String correctBoardString = "******\n" + "* BH *\n" + "* X  *\n" + "*    *\n" + "*    *\n" + "******\n";
		// Sample debugging output:
		// System.out.println("G2");
		// System.out.println("Expected:");
		// System.out.println(correctBoardString);
		// System.out.println("Actual:");
		// System.out.println(boardString);
		assertEquals(correctBoardString, boardString);
	}
}
