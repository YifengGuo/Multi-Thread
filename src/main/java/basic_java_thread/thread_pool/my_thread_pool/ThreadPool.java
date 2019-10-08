package basic_java_thread.thread_pool.my_thread_pool;

/**
 * Created by guoyifeng on 7/24/18
 */

/**
 * ThreadPool class creates numbers of TaskExecutor instances.
 *     TaskExecutor class will be responsible for executing the tasks
 * ThreadPool class exposes one method submitTask.
 *     submitTask method will be called by task generating program, to submit a task to threadPool.
 */
public class ThreadPool {
    private BlockingQueue<Runnable> blockingQueue;

    public ThreadPool(int queueSize, int nThreads) {
        blockingQueue = new BlockingQueue<>(queueSize);
        String threadName = null;
        TaskExecutor  taskExecutor = null;
        for (int i = 0; i < nThreads; i++) {
            threadName = "Thread-" + i;
            taskExecutor = new TaskExecutor(blockingQueue); // each TaskExecutor share one blockingqueue
            Thread thread = new Thread(taskExecutor, threadName);
            thread.start();
        }
    }

    public void submitTask(Runnable task) throws InterruptedException {
        blockingQueue.enqueue(task);
    }
}
