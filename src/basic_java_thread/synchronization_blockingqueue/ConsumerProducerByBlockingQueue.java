package basic_java_thread.synchronization_blockingqueue;

/**
 * Created by guoyifeng on 5/25/18.
 */

import java.util.concurrent.*;

/**
 * BlockingQueue is an important data structure in the Java util.concurrent.
 * BlockingQueue offers a thread-safe way to access queue: If the queue is
 * full when inserting data into it, the queue will be blocked and wait until
 * the queue is not full. If the queue is empty when trying to get data from
 * it, the queue will be blocked and wait until the queue is not empty.
 *
 * The operation on the BlockingQueue:
 *               throw exception          return special value         blocked         time-out
 *  insertion    add(o)                   offer(o)                     put(o)          offer(o, timeout, timeunit)
 *  deletion     remove(o)                poll(o)                      take(o)         poll(o, timeout, timeunit)
 *  check        element()                peek()
 *
 * Explanation for 4 operations:
 *    1. Throw Exception: When current operation cannot be executed immediately, throw a certain exception
 *    2. Return special value: When current operation cannot be executed immediately, return true or false
 *    3. Blocked: When current operation cannot be executed immediately, this method will be blocked until available
 *    4. Time-out: When current operation cannot be executed immediately, this method will be blocked until available.
 *                 But the waiting time will not be exceed the given time. A special value (true or false)will be
 *                 returned to inform if the operation succeeded or not.
 *
 * It is prohibited to add null into the BlockingQueue. NullPointerException will be thrown if attempting to do so.
 *
 * BlockingQueue (and BlockingDeque) is an interface. There are several implementations of this interface including
 * ArrayBlockingQueue, PriorityBlockingQueue, LinkedBlockingQueue, DelayQueue, SynchronousQueue.
 *
 * ArrayBlockingQueue:
 *    1. FIFO: The head of the queue is that element that has been on the
 *       queue the longest time.  The tail of the queue is that
 *       element that has been on the queue the shortest time. New elements
 *       are inserted at the tail of the queue, and the queue retrieval
 *       operations obtain elements at the head of the queue.
 *
 *    2. Fix size: Once created, the capacity cannot be changed
 *
 *    3. Producer-and-Consumer: Attempts to put an element into a full queue
 *       will result in the operation blocking; attempts to take an
 *       element from an empty queue will similarly block.
 *
 * PriorityBlockingQueue:
 *    1. Similar with PriorityQueue
 *    2. All the elements in this PriorityBlockingQueue must implement comparable or we must
 *       offer a comparator for this PriorityBlockingQueue
 *
 * LinkedBlockingQueue:
 *     1. Similar with LinkedList.
 *     2. Can initialize with a upper bound. If not, the default bound is Integer.MAX_VALUE
 *
 * DelayQueue:
 *     1. This queue will hold elements with a certain period. If time out, the queue will no longer
 *     hold the element.
 *     2. All the elements inserted into this queue must implement java.util.concurrent.Delayed interface
 *
 * SynchronousQueue:
 *    1. This is a special kind of queue.
 *    2. SynchronousQueue only holds one element.
 *        2.1 If the queue has one element and then it will be blocked and all the other threads which try to put
 *            new elements will fail until some thread take the element from this queue.
 *        2.2 If the queue has NO element and then it will be blocked and all the other threads which try to take
 *            element will fail until some thread put the element into this queue.
 */
public class ConsumerProducerByBlockingQueue {
}
