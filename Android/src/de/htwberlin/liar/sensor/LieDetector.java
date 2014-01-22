package de.htwberlin.liar.sensor;


/**
 * An abstract class for a lie detector. Contains methods for a observer pattern. 
 * Only one observer is allowed at any time. Also contains the two necessary methods 
 * to start the calibration, measurement of the data and a method to get result of the
 *  measurement (truth or lie).
 * 
 * @author Jens Grundmann
 *
 */
public abstract class LieDetector {
	
	/**The observer of this class */
	private TaskListener listener;
	
	/**
	 * The Interface for the observer. It will be notified, when the
	 * calibration has anded as well when the time to answer a question is up.
	 * 
	 * @author Jens Grundmann
	 *
	 */
	public interface TaskListener{
		
		/**
		 * Method of the observer called, when the calibration finished.
		 */
		public void finishedCalibration();
		
		/**
		 * Method of the observer called, when the time for an answer is up or
		 * the question was answered.
		 */
		public void finishedMeasurement();
	}
	
	/**
	 * Adds a observer to this class.
	 * 
	 * @param listener the observer to add
	 */
	public void addTaskListener(TaskListener listener){
		this.listener = listener;
	}
	
	/**
	 * Calls the observers {@link TaskListener#finishedCalibration()} method.
	 */
	protected void finishedCalibration() {
		if(listener != null){
			listener.finishedCalibration();
		}
		
	}
	
	/**
	 * Calls the observers {@link TaskListener#finishedMeasurement()} method.
	 */
	protected void finishedMeasurement() {
		if(listener != null){
			listener.finishedMeasurement();
		}
	}
	
	/**
	 * Starts the calibration process. Call {@link LieDetector#finishedCalibration()}
	 * when the process is done.
	 */
	public abstract void startCalibration();
	
	/**
	 * Starts the measurement. Please call {@link #finishedMeasurement()}, when the
	 * it stops due to an time limit.
	 */
	public abstract void startMeasurement();
	
	/**
	 * Stops the measurement. Please call {@link #finishedMeasurement()} after
	 * stopping the measurement.
	 */
	public abstract void stopMeasurement();
	
	/**
	 * Gets if the answer was the truth or a lie.
	 * 
	 * @return true, if the answer was the truth, false otherwise
	 */
	public abstract boolean getResult();

}
