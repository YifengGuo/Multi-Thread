package basic_java_thread.intro_to_synchronized_and_lock;

/**
 * Created by guoyifeng on 6/1/18.
 */

/**
 * Intro to synchronized
 *  1. concurrent access to the data: data might be primary type or data structure like (Map, List, Set etc.)
 *  2. critical section: a block of code that can obly be executed safely by one thread at a time
 *  3. Lock: an object that is "held" by one thread at a time, then "released"
 *
 *                     ******                 IMPORTANT                 *******
 *                     Only a single thread can run any synchronized method of an object
 *
 * Lock of object:
 *  intrinsic locks:
 *      1. Every object in Java has an intrinsic lock
 *      2. When a thread tries to run a synchronized method, first it will try to get the intrinsic lock on this object
 *      3. If successfully done, the thread owns the lock and executes the synchronized method
 *      4. Other threads cannot run the synchronized method because the lock currently is not available
 *      5. The other thread can enter only the lock-holding thread leaves the synchronized method
 *      6. If there are multiple threads waiting on this lock, they would compete and then decide who to take over the
 *         lock   (ReentrantLock is unfair lock and synchronized does not guarantee order as well)
 *
 * Tips:
 *      1. If an object has both synchronized methods and regular methods, then only one thread can
 *      run the synchronized method while regular methods can be run by multiple threads
 *
 *      2. Threads that you want to have synchronized must share the same monitor object (MyPrinter) in this example
 *
 *
 */
public class TestSynchronized {
    /**
     * all the threads share the same MyPrinter object
     * so the synchronized method of printer will be accessed by thread 1 by 1 and
     * the synchronization can be guaranteed
     * @param args
     */
    public static void main(String[] args) {
        MyPrinter printer = new MyPrinter();
        MyThread thread1 = new MyThread(printer, 1);
        MyThread thread2 = new MyThread(printer, 2);
        MyThread thread3 = new MyThread(printer, 3);

        thread1.start();
        thread2.start();
        thread3.start();
    }
}

class MyThread extends Thread {
    MyPrinter printer;
    int i;

    // constructor
    // each thread object must share the same copy of MyPrinter object
    // to guarantee the synchronization on the critical section
    // for only a single thread can run any synchronized method of an object
    public MyThread(MyPrinter printer, int i) {
        this.printer = printer;
        this.i = i;
    }

    @Override
    public void run() {
        for (int j = 0; j < 2; j++) {
            printer.print10(i);
        }
    }
}

class MyPrinter {
    public synchronized void print10(int value) {
        for (int i = 0; i < 10; i++) {
            System.out.print(value);
        }
        System.out.println(); // newline after print value for 10 times
    }
}

/**
 * synchronized blocks
 * advantage than declare in the method declaration:
 *  1. flexibility: in case we want the intrinsic lock comes from an object other than current object
 *     class MyThread {
 *         public void run() {
 *             for (int j = 0; j < 2; j++) {
 *                 synchronized(printer) {    // get the intrinsic lock of MyPrinter other than the thread itself
 *                     printer.print10(i);
 *                 }
 *             }
 *         }
 *     }
 */
class MyPrinter2 {
   public void print10(int value) {
       synchronized (this) { // specify what object the intrinsic lock comes from
           for (int i = 0; i < 10; i++) {
               System.out.print(value);
           }
           System.out.println();
       }
   }
}

/**
 * Multiple locks in Java
 * The blocks inside print10() and square() can run at the same time
 * because they use different objects for their locking mechanism
 *
 * Synchronization still applies.For example only a single thread can 
 * run the synchronized block in square() at any point in time
 *
 * Tips:
 *     1. Any object in Java can be used as a lock
 *     2. Synchronized blocks with different locks can be run by differnt threads at the same time
 *     3. Only one thread in synchronized block. As many threads everywhere else
 */
class MyPrinterMultipleLocks {
    Object lock1 = new Object();
    Object lock2 = new Object();

    public void print10(int value) {
        synchronized (lock1) {
            for (int i = 0; i < 10; i++) {
                System.out.print(value);
            }
            System.out.println();
        }
    }

    public int squre(int value) {
        synchronized (lock2) {
            return value * value;
        }
    }
}
