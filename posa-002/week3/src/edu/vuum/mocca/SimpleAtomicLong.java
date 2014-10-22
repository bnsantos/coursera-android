package edu.vuum.mocca;


import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * @class SimpleAtomicLong
 *
 * @brief This class implements a subset of the
 *        java.util.concurrent.atomic.SimpleAtomicLong class using a
 *        ReentrantReadWriteLock to illustrate how they work.
 */
class SimpleAtomicLong
{
    /**
     * The value that's manipulated atomically via the methods.
     */
    private long mValue;


    /**
     * The ReentrantReadWriteLock used to serialize access to mValue.
     */
    private ReentrantReadWriteLock readWriteLock;

    /**
     * Creates a new SimpleAtomicLong with the given initial value.
     */
    public SimpleAtomicLong(long initialValue) {
    	readWriteLock = new ReentrantReadWriteLock();
    	mValue = initialValue;
    }

    /**
     * @brief Gets the current value
     * 
     * @returns The current value
     */
    public long get() {
    	readWriteLock.readLock().lock();
    	long aux = mValue; 
    	readWriteLock.readLock().unlock();
    	return aux;
    }

    /**
     * @brief Atomically decrements by one the current value
     *
     * @returns the updated value
     */
    public long decrementAndGet() {
    	readWriteLock.writeLock().lock();
    	mValue--;
    	long aux = mValue; 
    	readWriteLock.writeLock().unlock();
    	return aux;
    }

    /**
     * @brief Atomically increments by one the current value
     *
     * @returns the previous value
     */
    public long getAndIncrement() {
    	readWriteLock.writeLock().lock();
    	long aux = mValue; 
    	mValue++;
    	readWriteLock.writeLock().unlock();
    	return aux;
    }

    /**
     * @brief Atomically decrements by one the current value
     *
     * @returns the previous value
     */
    public long getAndDecrement() {
    	readWriteLock.writeLock().lock();
    	long aux = mValue; 
    	mValue--;
    	readWriteLock.writeLock().unlock();
    	return aux;
    }

    /**
     * @brief Atomically increments by one the current value
     *
     * @returns the updated value
     */
    public long incrementAndGet() {
    	readWriteLock.writeLock().lock();
    	mValue++;
    	long aux = mValue; 
    	readWriteLock.writeLock().unlock();
    	return aux;
    }
}