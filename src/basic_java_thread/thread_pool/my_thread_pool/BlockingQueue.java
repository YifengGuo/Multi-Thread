package basic_java_thread.thread_pool.my_thread_pool;

import java.util.LinkedList;
import java.util.Queue;

/**
 * Created by guoyifeng on 7/24/18
 */

/**
 * Customized BlockingQueue implemented by queue
 * @param <T> task type
 */
public class BlockingQueue<T> {
    private Queue<T> queue = new LinkedList<>();
    private final int EMPTY = 0;
    private int MAX_TASK_IN_QUEUE = -1;

    public BlockingQueue(int size) {
        this.MAX_TASK_IN_QUEUE = size;
    }

    /**
     * enqueue (push) Task to the queue
     * @param task
     * @throws InterruptedException
     */
    public synchronized void enqueue(T task) throws InterruptedException {
        // block the queue if queue is full
        while (queue.size() == this.MAX_TASK_IN_QUEUE) {
            wait();
        }
        // if queue is empty, notify all other waiting threads
        if (queue.size() == this.EMPTY) {
            notifyAll();
        }
        queue.offer(task);
    }

    /**
     * takes (pop) the task from the queue.
     * @return
     * @throws InterruptedException
     */
    public synchronized T dequeue() throws InterruptedException {
        // block the queue if queue is empty
        while (queue.size() == this.EMPTY) {
            wait();
        }
        // if queue is full, notify all other waiting threads
        if (queue.size() == this.MAX_TASK_IN_QUEUE) {
            notifyAll();
        }
        return queue.poll();
    }
}
