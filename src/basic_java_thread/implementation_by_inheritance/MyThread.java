package basic_java_thread.implementation_by_inheritance;

/**
 * Created by guoyifeng on 5/20/18.
 * Create Java Thread by extending from Thread class
 */
public class MyThread extends Thread {
    private int i;
    public MyThread(int i) {
        this.i = i;
    }

    /**
     * The function of MyThread is to print 0 to i
     */
    @Override
    public void run() {
        for (int j = 0; j < i; j++) {
            System.out.println(j);
        }
        System.out.println("Current Thread ends");
    }

    public static void main(String[] args) {
        MyThread thread1 = new MyThread(10);
        MyThread thread2 = new MyThread(10);
        MyThread thread3 = new MyThread(10);

        /*
        By invoking run(), the run() method of its thread is invoked by main thread one by one
        when former thread ends, the latter thread's run() will be invoked
         */
//        thread1.run();
//        thread2.run();
//        thread3.run();

        /*
        By invoking start(), each thread is executing concurrently.
        There is no certain order which will ends first or last.
        (thread1 - thread3 and main thread are all executing concurrently)
         */
        thread1.start();
        thread2.start();
        thread3.start();

        System.out.println("Main thread ends");
    }
}
