package edu.vuum.mocca;

//Import the necessary Java synchronization and scheduling classes.
import java.util.concurrent.CountDownLatch;

/**
 * @class PingPongRight
 * 
 * @brief This class implements a Java program that creates two instances of the
 *        PlayPingPongThread and start these thread instances to correctly
 *        alternate printing "Ping" and "Pong", respectively, on the console
 *        display.
 */
public class PingPongRight {
	/**
	 * Number of iterations to run the test program.
	 */
	public final static int mMaxIterations = 10;

	/**
	 * Latch that will be decremented each time a thread exits.
	 */
	public static CountDownLatch mLatch = null;

	/**
	 * @class PlayPingPongThread
	 * 
	 * @brief This class implements the ping/pong processing algorithm using the
	 *        SimpleSemaphore to alternate printing "ping" and "pong" to the
	 *        console display.
	 */
	public static class PlayPingPongThread extends Thread {
		/**
		 * Maximum number of loop iterations.
		 */
		private final int mMaxLoopIterations;

		/**
		 * String to print (either "ping!" or "pong"!) for each iteration.
		 */
		private String mPrint;

		/**
		 * Two SimpleSemaphores use to alternate pings and pongs. You can use an
		 * array of SimpleSemaphores or just define them as two data members.
		 */
		private SimpleSemaphore mPingSemaphore;
		private SimpleSemaphore mPongSemaphore;

		/**
		 * Constructor initializes the data member(s).
		 */
		public PlayPingPongThread(String stringToPrint,
				SimpleSemaphore semaphoreOne, SimpleSemaphore semaphoreTwo,
				int maxIterations) {
			mPingSemaphore = semaphoreOne;
			mPongSemaphore = semaphoreTwo;
			mPrint = stringToPrint;
			mMaxLoopIterations = maxIterations;
		}

		/**
		 * Main event loop that runs in a separate thread of control and
		 * performs the ping/pong algorithm using the SimpleSemaphores.
		 */
		public void run() {
			for(int i=0;i<mMaxLoopIterations;i++){
				acquire();
				System.out.println(mPrint + "(" + (i+1) + ")");
				release();
			}
            mLatch.countDown();
		}
		
		private void acquire() {
			/*System.out.println("Acquiring " + mPrint);*/
			mPingSemaphore.acquireUninterruptibly();
		}
		
		private void release() {
			/*System.out.println("Releasing " + mPrint);*/
			mPongSemaphore.release();
		}
	}

	/**
	 * The method that actually runs the ping/pong program.
	 */
	public static void process(String startString, String pingString,
			String pongString, String finishString, int maxIterations)
			throws InterruptedException {
		mLatch = new CountDownLatch(2);

		// Create the ping and pong SimpleSemaphores that control
		// alternation between threads.

		SimpleSemaphore pingSema = new SimpleSemaphore(1, true);
		SimpleSemaphore pongSema = new SimpleSemaphore(0, true);

		System.out.println(startString);

		PlayPingPongThread ping = new PlayPingPongThread(pingString, pingSema, pongSema, maxIterations);
		PlayPingPongThread pong = new PlayPingPongThread(pongString, pongSema, pingSema, maxIterations);

		ping.start();
		pong.start();

		mLatch.await();

		System.out.println(finishString);
	}

	/**
	 * The main() entry point method into PingPongRight program.
	 * 
	 * @throws InterruptedException
	 */
	public static void main(String[] args) throws InterruptedException {
		process("Ready...Set...Go!", "Ping!  ", " Pong! ", "Done!", mMaxIterations);
	}
}
