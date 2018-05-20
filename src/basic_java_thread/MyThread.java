package basic_java_thread;

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

        thread1.run();
        thread2.run();
        thread3.run();

        System.out.println("Main thread ends");
    }
}
