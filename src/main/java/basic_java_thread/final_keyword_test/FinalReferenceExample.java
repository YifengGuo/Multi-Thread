package basic_java_thread.final_keyword_test;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Created by guoyifeng on 9/9/19
 */
public class FinalReferenceExample {
    final int[] arr;
    static FinalReferenceExample obj;

    public FinalReferenceExample() {
        arr = new int[1];  // 1
        arr[0] = 1;  // 2
    }

    public static void writeOne() { // thread 1
        obj = new FinalReferenceExample();  // 3
    }

    public static void writeTwo() { // thread 2
        obj.arr[0] = 2;  // 4
    }

    public static void read() {  // thread 3
        if (obj != null) {  // 5
            int a = obj.arr[0]; // 6
            System.out.println(a);
        }
    }

    public static void main(String[] args) throws Exception {
        Task3 task3 = new Task3();
        Task4 task4 = new Task4();
        Task5 task5 = new Task5();


        ExecutorService exec = Executors.newCachedThreadPool();
        exec.submit(task3);
        exec.submit(task4);
        exec.submit(task5);
        TimeUnit.SECONDS.sleep(1);
        exec.shutdownNow();

    }
}

class Task3 implements Runnable {
    @Override
    public void run() {
        FinalReferenceExample.writeOne();
    }
}

class Task4 implements Runnable {
    @Override
    public void run() {
        FinalReferenceExample.writeTwo();
    }
}

class Task5 implements Runnable {
    @Override
    public void run() {
        FinalReferenceExample.read();
    }
}
