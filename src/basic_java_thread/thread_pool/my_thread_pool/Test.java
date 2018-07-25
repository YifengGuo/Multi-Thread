package basic_java_thread.thread_pool.my_thread_pool;

/**
 * Created by guoyifeng on 7/24/18
 */
public class Test {
    public static void main(String[] args) throws Exception {
        // create queue size - 3
        // Number of threads - 4
        int queueSize = 3;
        int threadCount = 4;
        ThreadPool threadPool = new ThreadPool(3, 4);
        // Created 15 Tasks and submit to pool
        for(int taskNumber = 1 ; taskNumber <= 15; taskNumber++) {
            Task task = new Task(taskNumber);
            threadPool.submitTask(task);
        }
    }
}
