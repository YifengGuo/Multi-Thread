package basic_java_thread.thread_pool.my_thread_pool;

/**
 * Created by guoyifeng on 7/24/18
 */

/**
 * TaskExecutor class implements Runnable interface.
 * The method of TaskExecutor class dequeue the task from the queue (BlockingQueue)
 * TaskExecutor class executes the task.
 */
public class TaskExecutor implements Runnable {
    private BlockingQueue blockingQueue;

    public TaskExecutor(BlockingQueue blockingQueue) {
        this.blockingQueue = blockingQueue;
    }

    @Override
    public void run() {
        try {
            while (true) {
                String name = Thread.currentThread().getName();
                Runnable task = (Runnable) blockingQueue.dequeue();
                System.out.println("Task Started By Thread " + name);
                task.run();
                System.out.println("Task Finished By Thread " + name);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
