package basic_java_thread.test_sleep_wait;

import basic_java_thread.thread_pool.my_thread_pool.BlockingQueue;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by guoyifeng on 10/5/19
 */
public class ProducerConsumer {
    private BlockingQueue<Product> blockingQueue;

    private static volatile int idCount;

    static class Product {
        private int id;

        public Product(int id) {
            this.id = id;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }
    }

    static class MyProducer implements Runnable {

        private BlockingQueue<Product> blockingQueue;

        private boolean flag = true;

        public MyProducer(BlockingQueue<Product> blockingQueue) {
            this.blockingQueue = blockingQueue;
        }

        @Override
        public void run() {
            while (flag) {
                idCount += 1;
                Product p = new Product(idCount);
                try {
                    blockingQueue.enqueue(p);
                    System.out.println("produced product " + p.getId());
                    TimeUnit.MILLISECONDS.sleep(50);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        public void shutdown() {
            flag = false;
        }
    }

    static class MyConsumer implements Runnable {

        private BlockingQueue<Product> blockingQueue;

        private boolean flag = true;

        public MyConsumer(BlockingQueue<Product> blockingQueue) {
            this.blockingQueue = blockingQueue;
        }

        @Override
        public void run() {
            while (flag) {
                try {
                    Product p = this.blockingQueue.dequeue();
                    System.out.println("consumed product " + p.getId());
                    TimeUnit.MILLISECONDS.sleep(50);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        public void shutdown() {
            flag = false;
        }
    }

    public static void main(String[] args) {
        ExecutorService executorService = Executors.newCachedThreadPool();
        BlockingQueue<Product> blockingQueue = new BlockingQueue<>(10);
        MyProducer myProducer = new MyProducer(blockingQueue);
        MyConsumer myConsumer = new MyConsumer(blockingQueue);
        for (int i = 0; i < 10; ++i) {
            if (i < 5) {
                executorService.submit(myProducer);
            } else {
                executorService.submit(myConsumer);
            }
        }

        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        myProducer.shutdown();
        myConsumer.shutdown();
        executorService.shutdown();
    }
}
