package p3d4amb.sat.lib.session;

import static p3d4amb.sat.lib.session.TestSession.Result.CONTINUE;
import static p3d4amb.sat.lib.session.TestSession.Result.FINISH_CERTIFIED;
import static p3d4amb.sat.lib.session.TestSession.Result.FINISH_NOT_CERTIFIED;

/**
 * PEST Algorithm (Old)
 */

class PestDepthCertifierOld extends DepthCertBase {

	private int nextDepth;
	private int maxDepth;
	private int leftLimit;
	private int rightLimit;
	private int chance;
	private double value;

	public PestDepthCertifierOld(int initDepth) {
		certifierStatus = new CertifierStatus();

		if (initDepth >= 1)
			certifierStatus.currentDepth = initDepth;
		else
			throw new IllegalArgumentException();

		maxDepth = initDepth;
		leftLimit = initDepth;
		rightLimit = 0;
		chance = 1;

		certifierStatus.currentResult = CONTINUE;
	}

	@Override
	void computeNextDepth(DepthCertBase.Solution solution) {
		if (solution == DepthCertBase.Solution.WRONG && chance > 0 && certifierStatus.currentDepth == maxDepth) {
			chance--;
		} else if (solution == DepthCertBase.Solution.WRONG && chance == 0
				&& certifierStatus.currentDepth == maxDepth) {
			certifierStatus.currentResult = FINISH_NOT_CERTIFIED;
		} else if (solution == DepthCertBase.Solution.NULL) {
			// Nothing (Skip button)
		} else if (solution == DepthCertBase.Solution.RIGHT) {
			leftLimit = certifierStatus.currentDepth;

			// Numerical rounding (Floor: round down)
			value = ((double) leftLimit + rightLimit) / 2;
			nextDepth = (int) (Math.floor(value));

			// Next depth
			certifierStatus.currentDepth = nextDepth;

			if ((leftLimit - rightLimit) == 1) {
				certifierStatus.currentResult = FINISH_CERTIFIED;
				certifierStatus.currentDepth = leftLimit;
			}
		} else if (solution == DepthCertBase.Solution.WRONG) {
			rightLimit = certifierStatus.currentDepth;

			// Numerical rounding (Ceil: round up)
			value = ((double) leftLimit + rightLimit) / 2;
			nextDepth = (int) (Math.ceil(value));

			// Next depth
			certifierStatus.currentDepth = nextDepth;

			if ((leftLimit - rightLimit) == 1) {
				certifierStatus.currentResult = FINISH_CERTIFIED;
				certifierStatus.currentDepth = leftLimit;
			}
		}
	}

	@Override
	public CertifierStatus getCurrentStatus() {
		return certifierStatus;
	}
}