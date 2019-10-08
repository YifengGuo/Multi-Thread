package basic_java_thread.thread_pool.simple_thread_pool;

import com.google.common.base.Preconditions;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by guoyifeng on 10/8/19
 */
public class DefaultThreadPool<Job extends Runnable> implements ThreadPool<Job> {

    private static final int MAX_WORKER_NUMBERS = 10;

    private static final int DEFAULT_WORKER_NUMBERS = 5;

    private static final int MIN_WORKER_NUMBERS = 1;

    // task queue
    private final Deque<Job> jobDeque = new ArrayDeque<>();

    // workers thread list
    private final List<Worker> workerList = Collections.synchronizedList(new ArrayList<>());

    private int workerNum = DEFAULT_WORKER_NUMBERS;

    private AtomicLong threadNum = new AtomicLong();

    public DefaultThreadPool() {
        initializeWorkers(DEFAULT_WORKER_NUMBERS);
    }

    public DefaultThreadPool(int num) {
        workerNum = num > MAX_WORKER_NUMBERS ? MAX_WORKER_NUMBERS : num < MIN_WORKER_NUMBERS ? MIN_WORKER_NUMBERS : num;
        initializeWorkers(workerNum);
    }

    private void initializeWorkers(int workerNum) {
        for (int i = 0; i < workerNum; ++i) {
            Worker worker = new Worker();
            workerList.add(worker);
            Thread thread = new Thread(worker, "ThreadPool-Worker-" + threadNum.incrementAndGet());
            thread.start();
        }
    }

    @Override
    public void execute(Job job) {
        Preconditions.checkNotNull(job, "job cannot be null");
        synchronized (jobDeque) {
            jobDeque.addLast(job);
            jobDeque.notify();  // get better performance by not using notifyAll();
        }
    }

    @Override
    public void shutdown() {
        for (Worker worker : workerList) {
            worker.shutdown();
        }
    }

    @Override
    public void addWorkers(int num) {
        if (this.workerNum + num > MAX_WORKER_NUMBERS) {
            num = MAX_WORKER_NUMBERS - this.workerNum;
        }
        initializeWorkers(num);
        this.workerNum += num;
    }

    @Override
    public void removeWorkers(int num) {
        Preconditions.checkArgument(num < this.workerNum, "beyond worker size");
        if (this.workerNum - num < MIN_WORKER_NUMBERS) {
            num = this.workerNum - MIN_WORKER_NUMBERS;
        }
        int count = 0;
        while (count < num) {
            Worker worker = workerList.get(count);
            if (workerList.remove(worker)) {
                worker.shutdown();
                count++;
            }
        }
        this.workerNum -= count;
    }

    @Override
    public int getJobSize() {
        return jobDeque.size();
    }

    class Worker implements Runnable {

        private volatile boolean isRunning = true;

        @Override
        public void run() {
            while (isRunning) {
                Job job = null;
                // classic wait/notify template
                synchronized (jobDeque) {
                    while (jobDeque.isEmpty()) { // wait if there's no task
                        try {
                            jobDeque.wait();
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                            return;
                        }
                        // get one job from task deque
                        job = jobDeque.removeFirst();
                        if (job != null) {
                            try {
                                job.run();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }
        }

        public void shutdown() {
            isRunning = false;
        }
    }

    public static void main(String[] args) {
        DefaultThreadPool<Task> threadPool = new DefaultThreadPool<>(6);
        for (int i = 0; i < 10; ++i) {
            Task t = new Task();
            threadPool.execute(t);
        }
        threadPool.shutdown();
    }

    static class Task implements Runnable {
        @Override
        public void run() {
            System.out.println(ThreadLocalRandom.current().nextLong(1000));
        }
    }
}
