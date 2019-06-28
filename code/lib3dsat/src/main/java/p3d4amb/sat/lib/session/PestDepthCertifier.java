package p3d4amb.sat.lib.session;

import static p3d4amb.sat.lib.session.TestSession.Result.CONTINUE;
import static p3d4amb.sat.lib.session.TestSession.Result.FINISH_CERTIFIED;
import static p3d4amb.sat.lib.session.TestSession.Result.FINISH_NOT_CERTIFIED;

/**
 * PEST Algorithm
 */

public class PestDepthCertifier extends DepthCertBase {

	private int maxDepth;
	private int leftLimit;
	private int rightLimit;
	private int chance;
	private int vector[];
	private int weight;
	private boolean partone;
	private int nextDepth;
	private double value;

	public PestDepthCertifier(int initDepth) {
		certifierStatus = new CertifierStatus();

		if (initDepth >= 1)
			certifierStatus.currentDepth = initDepth;
		else
			throw new IllegalArgumentException();

		maxDepth = initDepth;
		leftLimit = initDepth;
		rightLimit = 1;
		chance = 1;
		vector = new int[initDepth];
		weight = 1;
		partone = true;

		certifierStatus.currentResult = CONTINUE;
	}

	@Override
	public
	void computeNextDepth(DepthCertBase.Solution solution) {
		int savedCurrentDepth = certifierStatus.currentDepth;

		// PART ONE
		// Quickly and approximately identify the range in which the level can be contained
		if (partone && (solution != DepthCertBase.Solution.NULL)) {
			// System.out.print("[PART ONE]");

			if (solution == DepthCertBase.Solution.WRONG && chance > 0 && certifierStatus.currentDepth == maxDepth) {
				// First wrong attempt to maxDepth
				chance--;
			} else if (solution == DepthCertBase.Solution.WRONG && chance == 0
					&& certifierStatus.currentDepth == maxDepth) {
				// Second wrong attempt to maxDepth
				certifierStatus.currentResult = FINISH_NOT_CERTIFIED;
			} else if (solution == DepthCertBase.Solution.RIGHT) {
				leftLimit = certifierStatus.currentDepth;

				// Numerical rounding (Floor: round down)
				value = ((double) leftLimit + rightLimit) / 2;
				nextDepth = (int) (Math.floor(value));

				certifierStatus.currentDepth = nextDepth;
				vector[leftLimit - 1] += 1;

				if ((leftLimit - rightLimit) == 1) {
					partone = false;
					nextDepth = rightLimit;
					certifierStatus.currentDepth = nextDepth;
					
					// Weight because the rightLimit was wrong
					if (rightLimit != 1)
						weight = weight*3;
				}
			} else if (solution == DepthCertBase.Solution.WRONG) {
				rightLimit = certifierStatus.currentDepth;

				// Numerical rounding (Ceil: round up)
				value = ((double) leftLimit + rightLimit) / 2;
				nextDepth = (int) (Math.ceil(value));

				certifierStatus.currentDepth = nextDepth;
				vector[rightLimit - 1] -= 1;

				if ((leftLimit - rightLimit) == 1) {
					partone = false;
					nextDepth = rightLimit;
					certifierStatus.currentDepth = nextDepth;
					
					// Weight because the rightLimit was wrong
					if (rightLimit != 1)
						weight = weight*3;
				}
			}
			
			// PART TWO
			// Focus on the lower level identified [nextDepth = rigthLimit] until it reaches
			// a +2 that certifies the level, or a -2 that shift to an easier level
			// (nextDepth++). Added weight to avoid loop like [OK | NO | OK | NO]
		} else if (!partone && (solution != DepthCertBase.Solution.NULL)) {
			// System.out.print("[PART TWO]");

			if (solution == DepthCertBase.Solution.RIGHT) {
				vector[nextDepth - 1] += 1;
				certifierStatus.currentDepth = nextDepth;
			} else if (solution == DepthCertBase.Solution.WRONG) {
				// Subtract with weight
				vector[nextDepth - 1] -= weight;
				weight = weight * 3;
				certifierStatus.currentDepth = nextDepth;
			}

			// Vector control and maxDepth control
			if (vector[nextDepth - 1] >= 2) {
				// Reached the certification
				certifierStatus.currentResult = FINISH_CERTIFIED;
				certifierStatus.currentDepth = nextDepth;
			} else if ((vector[nextDepth - 1] <= -2) && (nextDepth < maxDepth)) {
				// Shift the nextDepth and reset weight
				nextDepth++;
				weight = 1;
				certifierStatus.currentDepth = nextDepth;
			} else if ((vector[nextDepth - 1] <= -2) && (nextDepth == maxDepth)) {
				// Returned to the initial maxDepth without certification
				certifierStatus.currentResult = FINISH_NOT_CERTIFIED;
				certifierStatus.currentDepth = nextDepth;
			}
		} else {
			System.out.print("[NULL (SKIP BUTTON)]");
			assert solution == DepthCertBase.Solution.NULL;
		}

		// DEBUG
		String vet = " [";

		for (int i = 0; i < vector.length; i++) {
			if (i != 0)
				vet = vet.concat(",");

			vet = vet.concat("" + vector[i]);
		}

		vet = vet.concat("]");

		@SuppressWarnings("unused")
		String string = vet + " [CURR:" + savedCurrentDepth + " " + solution + " NEXT:" + nextDepth + " L:" + leftLimit
				+ " R:" + rightLimit + "]";

		/*System.out.println(string);*/
	}

	@Override
	public CertifierStatus getCurrentStatus() {
		return certifierStatus;
	}
}