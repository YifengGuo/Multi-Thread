package basic_java_thread.test_sleep_wait;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * @author guoyifeng on 2019-09-17
 */
public class WaitNotify {

    static boolean flag = true;

    static Object lock = new Object();

    public static void main(String[] args) throws Exception {
        Thread waitThread = new Thread(new Wait(), "WaitThread");
        waitThread.start();
        TimeUnit.SECONDS.sleep(1);
        Thread notifyThread = new Thread(new Notify(), "NotifyThread");
        notifyThread.start();
    }

    static class Wait implements Runnable {
        @Override
        public void run() {
            // acquire lock, get Monitor of lock
            synchronized (lock) {
                // keep thread waited and release lock as long as flag is true
                while (flag) {
                    try {
                        System.out.println(Thread.currentThread() + " flag is true. Wait and release lock @ " +
                                new SimpleDateFormat("HH:mm:ss").format(new Date()));
                        lock.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                // when flag is not true
                System.out.println(Thread.currentThread() + " flag is false. Running @ " +
                        new SimpleDateFormat("HH:mm:ss").format(new Date()));
            }
        }
    }
    static class Notify implements Runnable {
        @Override
        public void run() {
            synchronized (lock) {
                // acquire the lock, then notify waiting threads on "lock" obj
                // current thread will not release lock during notify() all notifyAll()
                // WaitThread can return from wait() unless current thread completely releases the lock
                // WaitThread's state will change from WAITING to BLOCKED
                // because WaitThread is moved from waiting queue to synchronized queue
                // and tries to acquire the lock again
                try {
                    System.out.println(Thread.currentThread() + " holds lock. Notify @ " +
                            new SimpleDateFormat("HH:mm:ss").format(new Date()));
                    lock.notifyAll();
                    flag = false;
                    TimeUnit.SECONDS.sleep(5);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            // get lock again
            synchronized (lock) {
               try {
                   System.out.println(Thread.currentThread() + " holds lock again. Sleep @ " +
                           new SimpleDateFormat("HH:mm:ss").format(new Date()));
                   TimeUnit.SECONDS.sleep(5);
               } catch (InterruptedException e) {
                   e.printStackTrace();
               }
            }
        }
    }
}
