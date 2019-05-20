package basic_java_thread.concurrent_utils.readwritelock;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Created by guoyifeng on 5/20/19
 *
 * Simple implementation on Java ReadWriteLock
 *
 * When accessing resources:
 *  Usually, write has higher priority than read operation to avoid "thread starvation"
 *      Read: neither any thread is doing write operation, nor any thread is requesting write operation
 *      Write: no thread is currently doing write or read operations
 *
 *  Reentrance for read and write operation:
 *          read reentrance: either there is no write operation and write request or current thread has lock already.
 *                                  use a Map to maintain mapping between a thread and the count of locks it holds
 *
 *          write reentrance: Only work when current thread has write lock already
 *
 *
 *  Read lock upgrade to Write lock: this read lock owns read lock and is the only thread who owns read lock and no write lock owned
 *                                   by any other thread
 *
 *  Write lock degrade to Read lock: always safe (current thread holding write lock means there is no other writing threads or
 *                                   reading threads)
 *
 */
public class MyReadWriteLock {

    private Map<Thread, Integer> readingThreads = new HashMap<>();

    private Thread writingThread = null; // maintain thread reference which currently holds write lock

    private int writeRequest = 0; // count of write requests which are waiting for the lock

    private int writeAccess = 0;  // count of locks which are held by write thread reentrance

    public synchronized void lockRead() throws InterruptedException {
        Thread callingThread = Thread.currentThread();
        while (!canGrantReadAccess(callingThread)) {
            wait();
        }
        readingThreads.put(callingThread, readingThreads.getOrDefault(callingThread, 0) + 1);
    }

    public synchronized void unlockRead() {
        Thread callingThread = Thread.currentThread();
        if(!isReader(callingThread)){
            throw new IllegalMonitorStateException(
                    "Calling Thread does not" +
                            " hold a read lock on this ReadWriteLock");
        }
        int accessCount = readingThreads.getOrDefault(callingThread, 0);
        if (accessCount == 0) {
            readingThreads.remove(callingThread);
        } else {
            readingThreads.put(callingThread, accessCount - 1);
        }
        notifyAll();  // instead of using notify(), the advantage of using notifyAll():
                      // 1. when a read thread is waked up while another writeRequest is not, the read thread
                      //    can do nothing while writeRequest is still waiting
                      // 2. when all waiting threads are reading threads, notifyAll() can release lock to all of them
    }

    public synchronized void lockWrite() throws InterruptedException {
        writeRequest++;
        Thread callingThread = Thread.currentThread();
        while (!canGrantWriteAccess(callingThread)) {
            wait();
        }
        writeRequest--;
        writeAccess++;
        writingThread = callingThread;
    }

    public synchronized void unlockWrite() {
        if(!isWriter(Thread.currentThread())) {
            throw new IllegalMonitorStateException(
                    "Calling Thread does not" +
                            " hold the write lock on this ReadWriteLock");
        }
        writeAccess--;
        if (writeAccess == 0) {  // only one thread can hold write lock and do reentrance, if no lock held, reset writingThread reference
            writingThread = null;
        }
        notifyAll();
    }

    private boolean canGrantReadAccess(Thread callingThread) {
        if (isWriter(callingThread)) return true;  // degrading from write lock to read lock is always safe and prior (writing thread tried to do read operation)
        if (hasWriter()) return false;
        if (isReader(callingThread)) return true;  // reentrant read has higher priority than write request
        if (hasWriteRequests()) return false;
        return true;
    }


    private boolean canGrantWriteAccess(Thread callingThread) {
        if (isOnlyReader(callingThread)) return true; // upgrade from read lock to write lock for calling thread
        if (hasReaders()) return false;  // currently there are multiple reading threads
        if (!hasWriter()) return true;
        if (!isWriter(callingThread)) return false;  // reentrance check on write
        return true;
    }

    private boolean isOnlyReader(Thread callingThread) {
        return readingThreads.size() == 1 && readingThreads.get(callingThread) != null;
    }

    private boolean isWriter(Thread callingThread) {
        return writingThread == callingThread;
    }

    private boolean hasReaders() {
        return readingThreads.size() > 0;
    }

    private boolean isReader(Thread callingThread) {  // reenrance check on read
        return readingThreads.get(callingThread) != null;
    }

    private boolean hasWriteRequests() {
        return writeAccess > 0;
    }

    private boolean hasWriter() {
        return writingThread != null;
    }

    public static void main(String[] args) throws Exception {
        MyReadWriteLock readWriteLock = new MyReadWriteLock();
        ExecutorService exec = Executors.newCachedThreadPool();
        int[] arr = new int[1];
        arr[0] = 0;
        exec.execute(new TestTask(readWriteLock, false, arr));
        exec.execute(new TestTask(readWriteLock, true, arr));
        TimeUnit.SECONDS.sleep(5);
        exec.shutdownNow();
    }
}

class TestTask implements Runnable {
    private boolean type;
    private MyReadWriteLock readWriteLock;
    private int[] arr;

    public TestTask(MyReadWriteLock readWriteLock, boolean type, int[] arr) {
        this.readWriteLock = readWriteLock;
        this.type = type;
        this.arr = arr;
    }

    @Override
    public void run() {
        if (type) { // read
            try {
                while (!Thread.interrupted()) {
                    readWriteLock.lockRead();
                    System.out.println(Thread.currentThread().getName() + " is reading target, now target is " + arr[0]);
                    readWriteLock.unlockRead();
                }
            } catch (InterruptedException e) {
                System.out.println("reading task complete");
            }
        } else { // write
            try {
                while (!Thread.interrupted()) {
                    readWriteLock.lockWrite();
                    arr[0]++;
                    System.out.println(Thread.currentThread().getName() + " is writing target, now target is " + arr[0]);
                    readWriteLock.unlockWrite();
                }
            } catch (InterruptedException e) {
                System.out.println("writing task complete");
            }
        }
    }
}
