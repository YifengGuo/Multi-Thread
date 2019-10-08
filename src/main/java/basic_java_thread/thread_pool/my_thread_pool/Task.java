package basic_java_thread.thread_pool.my_thread_pool;

/**
 * Created by guoyifeng on 7/24/18
 */
public class Task implements Runnable {
    private int taskId;

    public Task(int taskId) {
        this.taskId = taskId;
    }

    @Override
    public void run() {
        System.out.println("Start executing task number " + taskId);
        try {
            // Simulating processing time
            // Perform tasks
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("End executing task number" + taskId);
    }
}
