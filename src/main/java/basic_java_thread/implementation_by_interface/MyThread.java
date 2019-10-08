package basic_java_thread.implementation_by_interface;

/**
 * Created by guoyifeng on 5/20/18.
 * Create Java Thread by implementing Runnable() interface
 * This way is desired if we need the thread to inherit from other class instead of Thread
 */
@SuppressWarnings("Duplicates")
public class MyThread implements Runnable {
    private int i;

    public MyThread(int i) {
        this.i = i;
    }
    @Override
    public void run() {
        for (int j = 0; j < i; j++) {
            System.out.println(j);

            /*
             * Test for pausing threads
             * Thread.sleep() is a static method of class
             * thread and can be invoked from any threads,
             * including main thread
             */
            try {
                Thread.sleep(500); // pause the current thread for 500 millis
            } catch (InterruptedException e) {
                /*
                 * The InterruptedException is an unchecked exception that is
                 * thrown by code to stop a thread from running
                 *
                 * An InterruptedException is thrown when a thread is waiting, sleeping
                 * or otherwise paused for a long time and another thread interrupts
                 * it using the interrupt() method in Thread class
                 */
                e.printStackTrace();
            }
        }
        System.out.println("Current thread ends");
    }

    public static void main(String[] args) {
        Thread t1 = new Thread(new MyThread(5));
        t1.start();
    }
}
