package cs601.concurrent;

import java.util.HashMap;
import java.util.Map;

/**
 * A reentrant read/write lock that allows: 1) Multiple readers (when there is
 * no writer). 2) One writer (when nobody else is writing or reading). 3) A
 * writer is allowed to acquire a read lock while holding the write lock. The
 * assignment is based on the assignment of Prof. Rollins (original author).
 */
public class ReentrantReadWriteLock {

	// TODO: Add instance variables : you need to keep track of the read lock
	// holders and the write lock holders.
	// We should be able to find the number of read locks and the number of
	// write locks
	// // a thread with the given threadId is holding
	private int writerRequest = 0, writeThreadCount = 0;
	private Map<Thread, Integer> readerThread = new HashMap<Thread, Integer>();
	private Thread writerThread = null;

	/**
	 * Constructor for ReentrantReadWriteLock
	 */
	public ReentrantReadWriteLock() {
		
	}

	public ReentrantReadWriteLock(int writerRequest, int writeThreadCount,
			Map<Thread, Integer> readerThread, Thread writerThread) {
		super();
		this.writerRequest = writerRequest;
		this.writeThreadCount = writeThreadCount;
		this.readerThread = readerThread;
		this.writerThread = writerThread;
	}

	/**
	 * Returns true if the current thread holds a read lock.
	 * 
	 * @return
	 */
	public synchronized boolean isReadLockHeldByCurrentThread() {
		return readerThread.get(Thread.currentThread()) != null;

	}

	/**
	 * Returns true if the current thread holds a write lock.
	 * 
	 * @return
	 */
	public synchronized boolean isWriteLockHeldByCurrentThread() {
		return writerThread == Thread.currentThread();

	}

	/**
	 * Non-blocking method that tries to acquire the read lock. Returns true if
	 * successful.
	 * Thread will get read lock when current thread is holding write lock, 
	 * there are readers threads are holding lock, current thread is holding read lock
	 * @return
	 */
	public synchronized boolean tryAcquiringReadLock()
			  {
		Thread current_thread = Thread.currentThread();
		if (isWriteLockHeldByCurrentThread())
			return true;
		if (hasWriterThread())
			return false;
		if (hasReadersThread())
			return true;
		if (isReadLockHeldByCurrentThread())
			return true;
		if (hasWriterRequests())
			return false;

		readerThread.put(current_thread, readerThread.get(current_thread));

		return true;

	}

	/**
	 * Non-blocking method that tries to acquire the write lock. Returns true if
	 * successful.
	 * Thread will get write lock only when there is no writer or reader thread is holding lock
	 * @return
	 */
	public synchronized boolean tryAcquiringWriteLock() 
	{
		if (onlyReader(Thread.currentThread()))
				return false;

			if (hasReadersThread())
				return false;
			if (writerThread == null) {
				writerThread = Thread.currentThread();
				return true;
			}
			if (!isWriter(Thread.currentThread()))
				return false;
		
		writerThread = Thread.currentThread();
		return true;
		
		

	}

	/**
	 * Blocking method - calls tryAcquiringReadLock and returns only when the
	 * read lock has been acquired, otherwise waits.
	 * Multiple read locks can be acquired by multiple threads at the same time,
	 * Thread cannot have a read and a write lock at the same time.
	 * @throws InterruptedException
	 */
	public synchronized void lockRead() {

		Thread current_thread = Thread.currentThread();
		try {
			while (!tryAcquiringReadLock()) {
				wait();
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		readerThread.put(current_thread,
				(getReadThreadCount(current_thread) + 1));

	}

	/**
	 * Releases the read lock held by the current thread.
	 */
	public synchronized void unlockRead()   {

		Thread current_thread = Thread.currentThread();
		if (!isReadLockHeldByCurrentThread()) {
			throw new IllegalMonitorStateException(
					"current thread does not hold read lock");
		}
		int readThreadCount = getReadThreadCount(current_thread);
		if (readThreadCount == 1) {
			readerThread.remove(current_thread);
		} else {
			readerThread.put(current_thread, (readThreadCount - 1));
		}
		notifyAll();

	}

	/**
	 * Blocking method that calls tryAcquiringWriteLock and returns only when
	 * the write lock has been acquired, otherwise waits.
	 * This means that multiple threads can read the data in parallel but an exclusive lock is needed for writing or modifying data.
	 * 
	 * @throws InterruptedException
	 */
	public synchronized void lockWrite() {

		writerRequest++;
		Thread current_thread = Thread.currentThread();
		try {
			while (!tryAcquiringWriteLock()) {
				wait();
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		writerRequest--;
		writeThreadCount++;
		writerThread = current_thread;
	}

	/**
	 * Releases the write lock held by the current thread.
	 */

	public synchronized void unlockWrite()   {
		if (!isWriteLockHeldByCurrentThread()) {
			throw new IllegalMonitorStateException(
					"Current Thread does not hold the write lock");
		}
		writeThreadCount--;
		if (writeThreadCount == 0) {
			writerThread = null;
		}
		notifyAll();
	}

	private int getReadThreadCount(Thread current_thread) {

		Integer readthreadcount = readerThread.get(current_thread);
		if (readthreadcount == null)
			return 0;
		return readthreadcount.intValue();
	}

	private boolean hasReadersThread() {
		return readerThread.size() > 0;
	}

	private boolean onlyReader(Thread current_thread) {
		return readerThread.size() == 1
				&& readerThread.get(current_thread) != null;
	}

	private boolean hasWriterThread() {
		return writerThread != null;
	}

	private boolean hasWriterRequests() {
		return this.writerRequest > 0;
	}

	private boolean isWriter(Thread current_thread) {
		return writerThread == current_thread;
	}

}
