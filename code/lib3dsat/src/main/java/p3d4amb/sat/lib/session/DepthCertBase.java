package p3d4amb.sat.lib.session;

public abstract class DepthCertBase {

	/**
	 * The enum Solution
	 */
	public enum Solution {
		/** The right */
		RIGHT,
		/** The wrong */
		WRONG,
		/** The null */
		NULL
	}

	/** The certifier status */
	protected CertifierStatus certifierStatus;

	/**
	 * The class CertifierStatus
	 */
	static public class CertifierStatus {

		/** The current depth */
		public int currentDepth;

		/** The current result */
		public TestSession.Result currentResult;

		@Override
		public String toString() {
			switch (currentResult) {
			case FINISH_CERTIFIED:
				return "CERTIFICATE at level: " + currentDepth;
			case FINISH_NOT_CERTIFIED:
				return "FINISHED but NOT CERTIFIED until level: " + currentDepth;
			case CONTINUE:
				return "NOT COMPLETED (Testing " + currentDepth + ")";
			}
			return "";
		}
	}

	/**
	 * Get current depth
	 *
	 * @return the current depth
	 */
	public int getCurrentDepth() {
		return certifierStatus.currentDepth;
	}

	abstract void computeNextDepth(Solution sol);

	/**
	 * Get current status
	 * 
	 * @return
	 */
	abstract public CertifierStatus getCurrentStatus();
}