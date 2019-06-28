package p3d4amb.sat.lib.session;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static p3d4amb.sat.lib.session.TestSession.Result.CONTINUE;
import static p3d4amb.sat.lib.session.TestSession.Result.FINISH_CERTIFIED;
import static p3d4amb.sat.lib.session.TestSession.Result.FINISH_NOT_CERTIFIED;

import org.junit.Test;

import p3d4amb.sat.lib.session.DepthCertBase.Solution;
import p3d4amb.sat.lib.session.TestSession.Result;

/**
 * Class PestDepthCertifierTest
 * 
 * Method checkToGo indicate as second parameter the successive depth to do (so for the last line is irrelevant)
 */
public class PestDepthCertifierTest {
	
	/**
	 * 01: Test scenario with initDepth negative, throw an exception
	 */
	@Test
	public void test01() {
		try {
			new PestDepthCertifier(-12);
			fail("Should throw exception");
		} catch (IllegalArgumentException e) {
			// Exception expected
		}
	}
	
	/**
	 * 02: Test scenario all right, ask twice the depth 1; also test the NULL (SKIP BUTTON)
	 */
	@Test
	public void test02() {
		PestDepthCertifier dp = new PestDepthCertifier(12);
		checkGoTo(dp, Solution.NULL, 12, CONTINUE); // NULL (SKIP BUTTON)
		checkGoTo(dp, Solution.RIGHT, 6, CONTINUE); // OK 12
		checkGoTo(dp, Solution.RIGHT, 3, CONTINUE); // OK 6
		checkGoTo(dp, Solution.RIGHT, 2, CONTINUE); // OK 3
		checkGoTo(dp, Solution.RIGHT, 1, CONTINUE); // OK 2

		checkGoTo(dp, Solution.RIGHT, 1, CONTINUE); // OK 1
		checkGoTo(dp, Solution.RIGHT, 1, FINISH_CERTIFIED); // OK 1, FINISH_CERTIFIED
	}

	/**
	 * 03: Test scenario immediately not certified, two initial consecutive mistakes
	 */
	@Test
	public void test03() {
		PestDepthCertifier dp = new PestDepthCertifier(12);
		checkGoTo(dp, Solution.WRONG, 12, CONTINUE); // NO 12 (chance = 0)
		checkGoTo(dp, Solution.WRONG, 12, FINISH_NOT_CERTIFIED); // NO 12, FINISH_NOT_CERTIFIED
	}

	/**
	 * 04: Test scenario with double consecutive mistakes
	 */
	@Test
	public void test04() {
		PestDepthCertifier dp = new PestDepthCertifier(12);
		checkGoTo(dp, Solution.RIGHT, 6, CONTINUE); // OK 12
		checkGoTo(dp, Solution.RIGHT, 3, CONTINUE); // OK 6
		checkGoTo(dp, Solution.RIGHT, 2, CONTINUE); // OK 3
		checkGoTo(dp, Solution.WRONG, 2, CONTINUE); // NO 2
		
		checkGoTo(dp, Solution.WRONG, 3, CONTINUE); // NO 2 (HEAVY -> -4), SHIFT TO 3
		checkGoTo(dp, Solution.RIGHT, 3, FINISH_CERTIFIED); // OK 3, FINISH_CERTIFIED
	}
	
	/**
	 * 05: Test scenario with several consecutive mistakes that provoke a FINISH_NOT_CERTIFIED due to fail even the highest depth
	 */
	@Test
	public void test05() {
		PestDepthCertifier dp = new PestDepthCertifier(12);
		checkGoTo(dp, Solution.RIGHT, 6, CONTINUE); // OK 12
		checkGoTo(dp, Solution.WRONG, 9, CONTINUE); // NO 6
		checkGoTo(dp, Solution.WRONG, 11, CONTINUE); // NO 9
		checkGoTo(dp, Solution.WRONG, 11, CONTINUE); // NO 11

		checkGoTo(dp, Solution.WRONG, 12, CONTINUE); // NO 11 (HEAVY -> -4), SHIFT TO 12
		checkGoTo(dp, Solution.WRONG, 12, CONTINUE); // NO 12
		checkGoTo(dp, Solution.WRONG, 12, FINISH_NOT_CERTIFIED); // NO 12 (HEAVY -> -3) AND MAXDEPTH REACHED, FINISH_NOT_CERTIFIED
	}

	/**
	 * 06: Test scenario where the part one find the range [4,5] and in the part two there are 
	 * consecutive mistakes on depth 4 that provoke the shift to depth 5 and certify it
	 */
	@Test
	public void test06() {
		PestDepthCertifier dp = new PestDepthCertifier(12);
		checkGoTo(dp, Solution.RIGHT, 6, CONTINUE); // OK 12
		checkGoTo(dp, Solution.RIGHT, 3, CONTINUE); // OK 6
		checkGoTo(dp, Solution.WRONG, 5, CONTINUE); // NO 3
		checkGoTo(dp, Solution.RIGHT, 4, CONTINUE); // OK 5
		checkGoTo(dp, Solution.WRONG, 4, CONTINUE); // NO 4

		checkGoTo(dp, Solution.WRONG, 5, CONTINUE); // NO 4 (HEAVY -> -4), SHIFT TO 5
		checkGoTo(dp, Solution.RIGHT, 5, FINISH_CERTIFIED); // OK 5, FINISH_CERTIFIED
	}
	
	/**
	 * 07: Test scenario where in part two from depth 8 after several mistakes the depth certified
	 * is 12, the easiest level
	 */
	@Test
	public void test07() {
		PestDepthCertifier dp = new PestDepthCertifier(12);
		checkGoTo(dp, Solution.RIGHT, 6, CONTINUE); // OK 12
		checkGoTo(dp, Solution.WRONG, 9, CONTINUE); // NO 6
		checkGoTo(dp, Solution.RIGHT, 7, CONTINUE); // OK 9
		checkGoTo(dp, Solution.WRONG, 8, CONTINUE); // NO 7
		checkGoTo(dp, Solution.WRONG, 8, CONTINUE); // NO 8
		
		checkGoTo(dp, Solution.WRONG, 9, CONTINUE); // NO 8 (HEAVY -> -4), SHIFT TO 9
		checkGoTo(dp, Solution.WRONG, 9, CONTINUE); // NO 9
		checkGoTo(dp, Solution.WRONG, 10, CONTINUE); // NO 9 (HEAVY -> -3), SHIFT TO 10
		checkGoTo(dp, Solution.WRONG, 10, CONTINUE); // NO 10
		checkGoTo(dp, Solution.WRONG, 11, CONTINUE); // NO 10 (HEAVY -> -4), SHIFT TO 11
		checkGoTo(dp, Solution.WRONG, 11, CONTINUE); // NO 11
		checkGoTo(dp, Solution.WRONG, 12, CONTINUE); // NO 11 (HEAVY -> -4), SHIFT TO 12
		checkGoTo(dp, Solution.RIGHT, 12, FINISH_CERTIFIED); // OK 12, FINISH_CERTIFIED (Two times right, at the start and now)
	}	

	/**
	 * 08: Test scenario where in the part two after one mistake, two consecutive right answer 
	 * permit to certify the depth 4
	 */
	@Test
	public void test08() {
		PestDepthCertifier dp = new PestDepthCertifier(12);
		checkGoTo(dp, Solution.RIGHT, 6, CONTINUE); // OK 12
		checkGoTo(dp, Solution.RIGHT, 3, CONTINUE); // OK 6
		checkGoTo(dp, Solution.WRONG, 5, CONTINUE); // NO 3
		checkGoTo(dp, Solution.RIGHT, 4, CONTINUE); // OK 5
		checkGoTo(dp, Solution.WRONG, 4, CONTINUE); // NO 4

		checkGoTo(dp, Solution.RIGHT, 4, CONTINUE); // OK 4
		checkGoTo(dp, Solution.RIGHT, 4, CONTINUE); // OK 4
		checkGoTo(dp, Solution.RIGHT, 4, FINISH_CERTIFIED); // OK 4, FINISH_CERTIFIED
	}
	
	/**
	 * 09: Test scenario with a mix of right and wrong answers
	 */
	@Test
	public void test09() {
		PestDepthCertifier dp = new PestDepthCertifier(12);
		checkGoTo(dp, Solution.RIGHT, 6, CONTINUE); // OK 12
		checkGoTo(dp, Solution.RIGHT, 3, CONTINUE); // OK 6
		checkGoTo(dp, Solution.WRONG, 5, CONTINUE); // NO 3
		checkGoTo(dp, Solution.RIGHT, 4, CONTINUE); // OK 5
		checkGoTo(dp, Solution.WRONG, 4, CONTINUE); // NO 4

		checkGoTo(dp, Solution.RIGHT, 4, CONTINUE); // OK 4
		checkGoTo(dp, Solution.WRONG, 5, CONTINUE); // NO 4 (HEAVY -> -3), SHIFT TO 5
		checkGoTo(dp, Solution.RIGHT, 5, FINISH_CERTIFIED); // OK 5, FINISH_CERTIFIED
	}

	/**
	 * 10: Test scenario with one mistake at the initial depth (chance = 0) and all right to depth 1
	 */
	@Test
	public void test10() {
		PestDepthCertifier dp = new PestDepthCertifier(12);
		// One wrong is tolerated
		checkGoTo(dp, Solution.WRONG, 12, CONTINUE); // NO 12 (chance = 0)
		checkGoTo(dp, Solution.RIGHT, 6, CONTINUE); // OK 12
		checkGoTo(dp, Solution.RIGHT, 3, CONTINUE); // OK 6
		checkGoTo(dp, Solution.RIGHT, 2, CONTINUE); // OK 3
		checkGoTo(dp, Solution.RIGHT, 1, CONTINUE); // OK 2
		
		checkGoTo(dp, Solution.RIGHT, 1, CONTINUE); // OK 1
		checkGoTo(dp, Solution.RIGHT, 1, FINISH_CERTIFIED); // OK 1, FINISH_CERTIFIED
	}
	
	/**
	 * 11: Test scenario with weight because the rightLimit was wrong
	 */
	@Test
	public void test11() {
		PestDepthCertifier dp = new PestDepthCertifier(12);
		checkGoTo(dp, Solution.RIGHT, 6, CONTINUE); // OK 12
		checkGoTo(dp, Solution.WRONG, 9, CONTINUE); // NO 6
		checkGoTo(dp, Solution.RIGHT, 7, CONTINUE); // OK 9
		checkGoTo(dp, Solution.RIGHT, 6, CONTINUE); // OK 7
		
		checkGoTo(dp, Solution.WRONG, 7, CONTINUE); // NO 6
		checkGoTo(dp, Solution.RIGHT, 7, FINISH_CERTIFIED); // OK 7, FINISH_CERTIFIED
	}
	
	/**
	 * CheckGoTo
	 */
	static void checkGoTo(DepthCertBase dp, DepthCertBase.Solution sol, int nextDepth, Result result) {
		dp.computeNextDepth(sol);
		assertEquals(nextDepth, dp.getCurrentDepth());
		assertEquals(result, dp.getCurrentStatus().currentResult);
	}
}