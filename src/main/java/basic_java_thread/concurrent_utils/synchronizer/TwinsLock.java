package basic_java_thread.concurrent_utils.synchronizer;

/**
 * @author yifengguo
 */

import com.google.common.base.Preconditions;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.AbstractQueuedSynchronizer;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

/**
 * 一个简单的自定义同步器实现
 * 功能：同一时刻只允许至多2个线程同时获取到锁
 *  1. 访问模式：共享模式
 *  2. 资源数(resourceCount)： 2
 *      当一个进程获取到锁则resourceCount减一,释放则加一. 0表示无可用资源了
 *      即status合法范围0, 1, 2
 *  3. 同步状态变更时应注意使用CAS保证操作的原子性
 *  4. 内聚同步器实现具体的获取、释放逻辑, 对外暴露的TwinsLock接口利用Sync代理
 *
 *
 *  tryAcquire(), tryAcquireShared, tryRelease(), tryReleaseShared()等方法交由用户的AQS实现,
 *  Lock类对外提供的方法实际调用AQS的acquire(), acquireShared(), release(), releaseShared()方法, 因为在其各自内部
 *  会调用用户AQS实现的try...方法, 根据其返回决定同步资源是否获取成功,是否进入同步队列
 */
public class TwinsLock implements Lock {

    private static final class Sync extends AbstractQueuedSynchronizer {
        /**
         * initialize resourceCount for Sync
         * @param resourceCount
         */
        public Sync(int resourceCount) {
            Preconditions.checkArgument(resourceCount > 0, "resourceCount cannot be zero or negative");
            setState(resourceCount);
        }

        /**
         * 如果结果小于0:
         *          则AbstractQueuedSynchronizer会调用doAcquireShared(),该方法会将当前线程封装为node加入同步队列
         *          利用spin尝试获取lock或失败后进入阻塞并等待唤醒
         * 如果结果大于等于0:
         *          则表示资源够用,获取同步状态成功
         * @param resourceOccupiedCount
         * @return
         */
        @Override
        protected int tryAcquireShared(int resourceOccupiedCount) {
            // spin to acquire synchronized state
            for (;;) {
                int currentResourceCount = getState();
                int newResourceCount = currentResourceCount - resourceOccupiedCount;
                if (newResourceCount < 0 || compareAndSetState(currentResourceCount,newResourceCount)) {
                    return newResourceCount;  // 决定了同步器是否会将当前线程加入同步队列
                }
            }
        }

        @Override
        protected boolean tryReleaseShared(int resourceReturnCount) {
            for (;;) {
                int currentResourceCount = getState();
                int newResourceCount = currentResourceCount + resourceReturnCount;
                // return true after CAS resourceCount
                if (compareAndSetState(currentResourceCount, newResourceCount)) {
                    return true;
                }
            }
        }

        // 由于本工具是共享模式,独占方法获取不需要重写
//        @Override
//        protected boolean tryAcquire(int resourceOccupiedCount) {
//            int currentResourceCount = getState();
//            if (currentResourceCount == 0) {
//                throw new IllegalMonitorStateException();
//            }
//            int newResourceCount = currentResourceCount - resourceOccupiedCount;
//            if (compareAndSetState(currentResourceCount, newResourceCount)) {
//                return true;
//            }
//            return false;
//        }
//
//        @Override
//        protected boolean tryRelease(int resourceReturnCount) {
//            if (getState() == 2) {
//                throw new IllegalMonitorStateException();
//            }
//            int currentResourceCount = getState();
//            int newResourceCount = currentResourceCount + resourceReturnCount;
//            setState(newResourceCount);
//            return true;
//        }
    }

    private final Sync sync = new Sync(2);

    @Override
    public void lock() {
        sync.acquireShared(1);
    }

    @Override
    public void unlock() {
        sync.releaseShared(1);
    }

    @Override
    public void lockInterruptibly() throws InterruptedException {

    }

    @Override
    public boolean tryLock() {
        return false;
    }

    @Override
    public boolean tryLock(long time, TimeUnit unit) throws InterruptedException {
        return false;
    }

    @Override
    public Condition newCondition() {
        return null;
    }
}
