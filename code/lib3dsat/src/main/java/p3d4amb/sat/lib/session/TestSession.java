package p3d4amb.sat.lib.session;

import java.util.ArrayList;
import java.util.List;

import p3d4amb.sat.lib.Position;
import p3d4amb.sat.lib.SATTest.ChoiceResult;
import p3d4amb.sat.lib.session.DepthCertBase.CertifierStatus;
import p3d4amb.sat.lib.shapes.NullShape;
import p3d4amb.sat.lib.shapes.Shape;

/**
 * A test is actually a session of choices made by the patient
 * 
 * This class stores the choices
 */
public class TestSession {

	private PestDepthCertifier dpcert; // CHANGE ALGORITHM TYPE

	List<SingleAnswer> sessionStory = new ArrayList<>();

	// Single answer given by the user
	public static class SingleAnswer {
		String answer;

		/**
		 * Build the single representation for each answer
		 * 
		 * @param timeTaken
		 */
		public SingleAnswer(Shape chosenShape, Shape currentShape, int currentDepth, double angle, long timeTaken) {
			String chosenShapeS = (chosenShape == null) ? "skip" : chosenShape.toString();
			answer = chosenShapeS + "," + currentShape.toString() + "," + currentDepth + "," + angle + "," + timeTaken;
		}

		@Override
		public String toString() {
			return answer;
		}
	}

	/**
	 * The Enum Result of the session at the end
	 */
	public enum Result {
		/** The continue */
		CONTINUE,
		/** The finish certified */
		FINISH_CERTIFIED,
		/** The finish not certified */
		FINISH_NOT_CERTIFIED // No depth could be certified
	}

	public TestSession(int initialDepth) {
		dpcert = new PestDepthCertifier(initialDepth); // CHANGE ALGORITHM TYPE
	}

	/**
	 * Return the current depth (as established by the certifier); be careful the
	 * certifier may have decided to stop
	 *
	 * @return the current depth
	 */
	public int getCurrentDepth() {
		return dpcert.getCurrentDepth();
	}

	/**
	 * Gets the current status
	 *
	 * @return the current status
	 */
	public CertifierStatus getCurrentStatus() {
		return dpcert.getCurrentStatus();
	}

	/**
	 * Check the solution and compute the next depth unless
	 *
	 * @param chosenShape  the s can be null: it corresponds to skip by the user
	 * @param currentShape the current shape shown to the user (the session does not
	 *                     know)
	 * @param timeTaken    the time taken in milliseconds
	 * @param angle        able in seconds of arc
	 * @return the choice result
	 */
	public ChoiceResult solutionChosen(Shape chosenShape, Shape currentShape, long timeTaken, double angle) {
		ChoiceResult res;
		int currentDepth = dpcert.getCurrentDepth(); // Initialized before computeNextDepth()

		if (chosenShape == null) {
			dpcert.computeNextDepth(DepthCertBase.Solution.NULL);
			res = ChoiceResult.SKIP;
		} else if (chosenShape == currentShape) {
			if (chosenShape instanceof NullShape)
				dpcert.computeNextDepth(DepthCertBase.Solution.NULL);
			else
				dpcert.computeNextDepth(DepthCertBase.Solution.RIGHT);
			res = ChoiceResult.RIGHT;
		} else {
			assert (chosenShape != currentShape && chosenShape != null);
			dpcert.computeNextDepth(DepthCertBase.Solution.WRONG);
			res = ChoiceResult.WRONG;
		}

		sessionStory.add(new SingleAnswer(chosenShape, currentShape, currentDepth, angle, timeTaken));

		return res;
	}

	/**
	 * Check solution (position) and set new depth unless in demo: in that case the
	 * solution is not counted
	 *
	 * @param chosenPostion the p
	 * @param currentPos
	 * @return the choice result
	 */
	public ChoiceResult positionChosen(Position chosenPostion, Position currentPos) {
		if (chosenPostion == currentPos) {
			dpcert.computeNextDepth(DepthCertBase.Solution.NULL);
			return ChoiceResult.RIGHT;
		} else {
			assert (chosenPostion != currentPos);
			dpcert.computeNextDepth(DepthCertBase.Solution.WRONG);
			return ChoiceResult.WRONG;
		}
	}

	/**
	 * Return the list of strings representing the session
	 * 
	 * @return result
	 */
	public List<String> getSessionResults() {
		List<String> result = new ArrayList<>();
		for (SingleAnswer sa : sessionStory) {
			result.add(sa.toString());
		}
		return result;
	}

	/**
	 * Gets the session answers
	 *
	 * @return the session answers
	 */
	public List<SingleAnswer> getSessionAnswers() {
		return java.util.Collections.unmodifiableList(sessionStory);
	}
}