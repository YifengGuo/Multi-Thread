package basic_java_thread.implementation_by_interface;

/**
 * Created by guoyifeng on 5/20/18.
 * Create Java Thread by implementing Runnable() interface
 * This way is desired if we need the thread to inherit from other class instead of Thread
 */
public class MyThread implements Runnable {
    private int i;

    public MyThread(int i) {
        this.i = i;
    }
    @Override
    public void run() {
        for (int j = 0; j < i; j++) {
            System.out.println(j);
        }
        System.out.println("Current thread ends");
    }

    public static void main(String[] args) {
        Thread t1 = new Thread(new MyThread(5));
        t1.start();
    }
}
