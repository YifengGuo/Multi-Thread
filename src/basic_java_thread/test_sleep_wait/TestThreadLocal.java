package basic_java_thread.test_sleep_wait;

import java.util.concurrent.TimeUnit;

/**
 * Created by guoyifeng on 10/7/19
 */

/**
 * ThreadLocal is a data structure which uses ThreadLocal object as key and any kinds of object as value
 * This structure is stuck to thread which means a thread could search a value that was stuck on it
 * based on ThreadLocal object. So this variable is only accessible to its own current thread.
 *
 * methods in this class can be used in AOP, begin() invoked during cutting and end() invoked after method was invoked
 * this time counting function can be achieved in different methods or class.
 */
public class TestThreadLocal {
    private static final ThreadLocal<Long> TIME_THREADLOCAL = ThreadLocal.withInitial(() -> System.currentTimeMillis());

    public static final void begin() {
        TIME_THREADLOCAL.set(System.currentTimeMillis());
    }

    public static final long end() {
        return System.currentTimeMillis() - TIME_THREADLOCAL.get();
    }

    public static void main(String[] args) throws Exception {
        begin();
        TimeUnit.SECONDS.sleep(1);
        System.out.println(end());
    }
}
