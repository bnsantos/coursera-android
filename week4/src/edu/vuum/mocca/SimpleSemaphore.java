package edu.vuum.mocca;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @class SimpleSemaphore
 * 
 * @brief This class provides a simple counting semaphore implementation using
 *        Java a ReentrantLock and a ConditionObject (which is accessed via a
 *        Condition). It must implement both "Fair" and "NonFair" semaphore
 *        semantics, just liked Java Semaphores.
 */
public class SimpleSemaphore {
	/**
	 * Define a Lock to protect the critical section.
	 */
	private final Lock mLock;

	/**
	 * Define a Condition that waits while the number of permits is 0.
	 */
	private final Condition mPermitAvailable;

	/**
	 * Define a count of the number of available permits.
	 */
	private volatile int mPermits;

	public SimpleSemaphore(int permits, boolean fair) {
		mPermits = permits;
		mLock = new ReentrantLock(fair);
		mPermitAvailable = mLock.newCondition();
	}

	/**
	 * Acquire one permit from the semaphore in a manner that can be
	 * interrupted.
	 */
	public void acquire() throws InterruptedException {
		mLock.lockInterruptibly();
		try{
			while(mPermits<=0){
				mPermitAvailable.await();
			}
			mPermits--;
		}finally {
			mLock.unlock();
		}
	}

	/**
	 * Acquire one permit from the semaphore in a manner that cannot be
	 * interrupted.
	 * 
	 * @throws InterruptedException
	 */
	public void acquireUninterruptibly() {
		mLock.lock();
		try{
			while (mPermits <= 0) {
				mPermitAvailable.awaitUninterruptibly();
			}
			mPermits--;
		}finally{
			mLock.unlock();
		}
	}

	/**
	 * Return one permit to the semaphore.
	 */
	public void release() {
		mLock.lock();
		try{
			mPermits++;
			if(availablePermits()>0){
				mPermitAvailable.signal();
			}
		}finally{
			mLock.unlock();
		}
	}

	/**
	 * Return the number of permits available.
	 */
	public int availablePermits() {
		return mPermits;
	}
}
