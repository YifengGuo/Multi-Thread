package basic_java_thread.thread_pool.simple_connection_pool;

import com.google.common.base.Preconditions;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.concurrent.TimeUnit;

/**
 * Created by guoyifeng on 10/8/19
 */
public class SimpleConnectionPool {
    private Deque<Connection> pool = new ArrayDeque<>();

    public SimpleConnectionPool(int initialSize) {
        Preconditions.checkArgument(initialSize > 0, "initial size must be positive");
        for (int i = 0; i < initialSize; ++i) {
            pool.addLast(ConnectionDriver.createConnection());
        }
    }

    public void releaseConnection(Connection connection) {
        Preconditions.checkNotNull(connection);
        synchronized (pool) {
            // return connection to deque and notify all waiting threads
            pool.addLast(connection);
            pool.notifyAll();
        }
    }

    /**
     * timeout mechanism
     * @param millis waiting milliseconds before timeout
     * @return Connection for current ops
     * @throws InterruptedException
     */
    public Connection fetchConnection(long millis) throws InterruptedException {
        synchronized (pool) {
            // do not wait
            if (millis <= 0) {
                while (pool.isEmpty()) {
                    pool.wait();
                }
                return pool.removeFirst();
            } else {
                long future = System.currentTimeMillis() + millis;
                long remaining = millis;
                while (pool.isEmpty() && remaining > 0) {
                    pool.wait();
                    remaining = future - System.currentTimeMillis();
                }
                Connection result = null;
                if (!pool.isEmpty()) {
                    result = pool.removeFirst();
                }
                return result;
            }
        }
    }

    static class Connection {

    }

    static class ConnectionDriver {
        static class ConnectionHandler implements InvocationHandler {
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                if (method.getName().equals("commit")) {
                    TimeUnit.MILLISECONDS.sleep(100);
                }
                return null;
            }
        }

        public static final Connection createConnection() {
            return (Connection) Proxy.newProxyInstance(
                    ConnectionDriver.class.getClassLoader(),
                    new Class[]{Connection.class},
                    new ConnectionHandler());
        }
    }
}
