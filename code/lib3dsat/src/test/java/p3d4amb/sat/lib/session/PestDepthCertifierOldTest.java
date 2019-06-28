package p3d4amb.sat.lib.session;

import static org.junit.Assert.assertEquals;
import static p3d4amb.sat.lib.session.TestSession.Result.CONTINUE;
import static p3d4amb.sat.lib.session.TestSession.Result.FINISH_CERTIFIED;
import static p3d4amb.sat.lib.session.TestSession.Result.FINISH_NOT_CERTIFIED;

import org.junit.Test;

import p3d4amb.sat.lib.session.DepthCertBase.Solution;
import p3d4amb.sat.lib.session.TestSession.Result;

/**
 * Class PestDepthCertifierOldTest (Old)
 * 
 * Method checkToGo indicate as second parameter the successive depth to do (so for the last line is irrelevant)
 */
public class PestDepthCertifierOldTest {

	/**
	 * 01: Test scenario all right to 1
	 */
	@Test
	public void test01() {
		PestDepthCertifierOld dp = new PestDepthCertifierOld(12);
		assertEquals(12, dp.getCurrentDepth());
		checkGoTo(dp, Solution.RIGHT, 6, CONTINUE);
		checkGoTo(dp, Solution.RIGHT, 3, CONTINUE);
		checkGoTo(dp, Solution.RIGHT, 1, CONTINUE);
		checkGoTo(dp, Solution.RIGHT, 1, FINISH_CERTIFIED);
	}

	/**
	 * 02: Test scenario immediately not certified
	 */
	@Test
	public void test02() {
		PestDepthCertifierOld dp = new PestDepthCertifierOld(12);
		assertEquals(12, dp.getCurrentDepth());
		checkGoTo(dp, Solution.WRONG, 12, CONTINUE);
		checkGoTo(dp, Solution.WRONG, 12, FINISH_NOT_CERTIFIED);
	}

	/**
	 * 03: Test scenario one chance to mistake first depth reaching to 1
	 */
	@Test
	public void test03() {
		PestDepthCertifierOld dp = new PestDepthCertifierOld(12);
		assertEquals(12, dp.getCurrentDepth());
		checkGoTo(dp, Solution.WRONG, 12, CONTINUE);
		checkGoTo(dp, Solution.RIGHT, 6, CONTINUE);
		checkGoTo(dp, Solution.RIGHT, 3, CONTINUE);
		checkGoTo(dp, Solution.RIGHT, 1, CONTINUE);
		checkGoTo(dp, Solution.RIGHT, 1, FINISH_CERTIFIED);
	}

	/**
	 * 04: Test scenario double consecutive mistake
	 */
	@Test
	public void test04() {
		PestDepthCertifierOld dp = new PestDepthCertifierOld(12);
		assertEquals(12, dp.getCurrentDepth());
		checkGoTo(dp, Solution.RIGHT, 6, CONTINUE);
		checkGoTo(dp, Solution.RIGHT, 3, CONTINUE);
		checkGoTo(dp, Solution.RIGHT, 1, CONTINUE);
		checkGoTo(dp, Solution.WRONG, 2, CONTINUE);
		checkGoTo(dp, Solution.WRONG, 3, FINISH_CERTIFIED);
	}

	/**
	 * 05: Test scenario certified but not consecutive right
	 */
	@Test
	public void test05() {
		PestDepthCertifierOld dp = new PestDepthCertifierOld(12);
		checkGoTo(dp, Solution.RIGHT, 6, CONTINUE);
		checkGoTo(dp, Solution.RIGHT, 3, CONTINUE);
		checkGoTo(dp, Solution.WRONG, 5, CONTINUE);
		checkGoTo(dp, Solution.WRONG, 6, FINISH_CERTIFIED);
	}

	/**
	 * 06: Test scenario double mistake not consecutive
	 */
	@Test
	public void test06() {
		PestDepthCertifierOld dp = new PestDepthCertifierOld(12);
		checkGoTo(dp, Solution.RIGHT, 6, CONTINUE);
		checkGoTo(dp, Solution.WRONG, 9, CONTINUE);
		checkGoTo(dp, Solution.RIGHT, 7, CONTINUE);
		checkGoTo(dp, Solution.WRONG, 8, CONTINUE);
		checkGoTo(dp, Solution.RIGHT, 8, FINISH_CERTIFIED);
	}

	/**
	 * 07: Test scenario several mistakes
	 */
	@Test
	public void test07() {
		PestDepthCertifierOld dp = new PestDepthCertifierOld(12);
		assertEquals(12, dp.getCurrentDepth());
		checkGoTo(dp, Solution.RIGHT, 6, CONTINUE);
		checkGoTo(dp, Solution.WRONG, 9, CONTINUE);
		checkGoTo(dp, Solution.WRONG, 11, CONTINUE);
		checkGoTo(dp, Solution.WRONG, 12, FINISH_CERTIFIED);
	}

	/**
	 * 08: Test scenario with two errors not consecutive
	 */
	@Test
	public void test08() {
		PestDepthCertifierOld dp = new PestDepthCertifierOld(12);
		checkGoTo(dp, Solution.RIGHT, 6, CONTINUE);
		checkGoTo(dp, Solution.WRONG, 9, CONTINUE);
		checkGoTo(dp, Solution.RIGHT, 7, CONTINUE);
		checkGoTo(dp, Solution.WRONG, 8, CONTINUE);
		checkGoTo(dp, Solution.RIGHT, 8, FINISH_CERTIFIED);
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