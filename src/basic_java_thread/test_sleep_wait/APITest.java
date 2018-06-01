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
