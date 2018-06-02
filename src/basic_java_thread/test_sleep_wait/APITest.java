package basic_java_thread.test_sleep_wait;

/**
 * Created by guoyifeng on 5/31/18.
 */

/**
 * 1. synchronized keyword is used for exclusive accessing.
   2. To make a method synchronized, simply add the synchronized keyword to its declaration.
      Then no two invocations of synchronized methods on the same object can interleave with each other.
   3. Synchronized statements must specify the object that provides the intrinsic lock.
      When synchronized(this) is used, you have to avoid to synchronizing invocations of other objects' methods.
   4. wait() tells the calling thread to give up the monitor and go to sleep until some other thread enters the
      same monitor and calls notify( ).
   5. notify() wakes up the first thread that called wait() on the same object.
 */

/**
 * wait() method
 *  1. Defined in class Object. Inherited by all objects.
 *  2. A thread invoking wait() will suspend the thread.
 *  3. A thread invoking wait() must own the intrinsic lock of the object it is calling wait() from
 *  4. If we are going to call this.wait(), it has to be in synchronized(this) block.
 *  5. wait() method must be in a try-catch block that catches InterruptedException
 *  6. All threads that call wait() on an object are placed in a pool of waiting threads for that object
 */

/**
 * notify() method
 *  1. Defined in class Object. Inherited by all objects.
 *  2. Execution resumes when another thread calls the notify() method of the object our first thread is waiting on
 *  3. When the notify method of an object is called, then a single waiting thread ob that object is signaled to get
 *     ready to resume execution
 *  4. After our Producer releases the lock, our Consumer thread gets the lock once again and resumes its execution
 */

/**
 * notifyAll() method
 *  1. Defined in class Object. Inherited by all objects.
 *  2. Notifies all the waiting threads.
 *  3. These waiting threads would then compete to see which single thread resumes execution, the rest of the threads
 *     would once again wait
 */
public class APITest {
    public static void main(String[] args) {
        ThreadB threadB = new ThreadB();
        ThreadC threadC = new ThreadC();
        /**
         * The result is that two threads are running concurrently: the
         * current thread (which returns from the call to the
         * start method) and the other thread (which executes its
         * run method).
         * It is never legal to start a thread more than once.
         * In particular, a thread may not be restarted once it has completed
         * execution.
         */
        threadB.start();
        /**
         * threadB is synchronized. threadB completes the calculation before Main thread outputs its total value.
         */
        synchronized (threadB) {
            try {
                System.out.println("Waiting for ThreadB completed...");
                threadB.wait(); // will be notified when calculation done in the run() of threadB
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("Calculation of threadB is done.");
            System.out.println("threadB's total is " + threadB.total);
        }

        /**
         * threadC.total is unknown because main thread may have chance to execute this
         * block of code before or after the calculation of threadC.run() done
         */
        threadC.start();
        System.out.println("threadC's total is " + threadC.total);
    }
}


class ThreadB extends Thread {
    int total;

    @Override
    public void run() {
        synchronized (this) {
            for (int i = 0; i < 100; i++) {
                total += i;
            }
            this.notify();
        }
    }
}

class ThreadC extends Thread {
    int total;
    @Override
    public void run() {
        for (int i = 0; i < 100; i++) {
            total += i;
        }
    }
}
