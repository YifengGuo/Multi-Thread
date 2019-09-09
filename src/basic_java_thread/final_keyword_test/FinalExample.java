package basic_java_thread.final_keyword_test;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by guoyifeng on 9/9/19
 */

/**
 * final keyword can guarantee final variable of some referenced object has been initialized before any threads
 * try to read it from this object
 */
public class FinalExample {
    int i;
    final int j;
    static FinalExample obj;

    public FinalExample() {
        i = 1;
        j = 2;
    }

    public static void writer() {
        obj = new FinalExample();
    }

    public static void reader() {
        FinalExample fe = obj;
        int a = fe.i;
        int b = fe.j;
        System.out.println(a + " " + b);
    }

    public static void main(String[] args) {
        Task1 task1 = new Task1();
        Task2 task2 = new Task2();
        Thread t1 = new Thread(task1);
        Thread t2 = new Thread(task2);
        t1.start();
        t2.start();
    }
}

class Task1 implements Runnable {
    @Override
    public void run() {
        FinalExample.writer();
    }
}

class Task2 implements Runnable {
    @Override
    public void run() {
        FinalExample.reader();
    }
}
