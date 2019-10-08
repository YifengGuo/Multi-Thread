package basic_java_thread.thread_pool.simple_thread_pool;

/**
 * Created by guoyifeng on 10/8/19
 */
public interface ThreadPool<Job extends Runnable> {

    void execute(Job job);

    void shutdown();

    void addWorkers(int num);

    void removeWorkers(int num);

    // get count of waiting jobs
    int getJobSize();
}
