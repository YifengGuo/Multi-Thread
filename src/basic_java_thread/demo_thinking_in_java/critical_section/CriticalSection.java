package basic_java_thread.demo_thinking_in_java.critical_section;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by guoyifeng on 3/29/19
 */

/**
 * performance comparison between synchronize and critical section
 * conclusion: critical section is much faster than synchronize
 *          critical section gives more unlock time to the object so that it can be accessible by more other threads
 */
public class CriticalSection {

    static void testTwoApproaches(PairManager p1, PairManager p2) {
        ExecutorService exec = Executors.newCachedThreadPool();
        PairManipulator
                pm1 = new PairManipulator(p1),
                pm2 = new PairManipulator(p2);
        PairChecker
                pc1 = new PairChecker(p1),
                pc2 = new PairChecker(p2);

        exec.submit(pm1);
        exec.submit(pm2);
        exec.submit(pc1);
        exec.submit(pc2);

        try {
            TimeUnit.MILLISECONDS.sleep(500);
        } catch (InterruptedException e) {
            System.out.println(e);
        }

        if (p1 instanceof PairManager1) {
            System.out.println("pm1 synchronize whole method: " + pm1 + " \npm2 critical section: " + pm2);
            return;
        } else {
            System.out.println("epm1 lock store(): " + pm1 + " \nepm2 do not lock store(): " + pm2);
            System.exit(0);
        }

    }

    public static void main(String[] args) {
        PairManager
                pairManager1 = new PairManager1(),
                pairManager2 = new PairManager2(),
                epm1 = new ExplicitPairManager1(),
                epm2 = new ExplicitPairManager2();


        testTwoApproaches(pairManager1, pairManager2);

        testTwoApproaches(epm1, epm2);
    }
}

class Pair {  // not thread-safe class
    private int x, y;

    public Pair(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public Pair() {this(0, 0);}

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public void incrementX() {x++;}
    public void incrementY() {y++;}

    @Override
    public String toString() {
        return "x: " + x + " y: " + y;
    }

    public class PairValuesNotEqualException extends RuntimeException {
        public PairValuesNotEqualException() {
            super("Pair not equal: " + Pair.this);
        }
    }

    public void checkState() {
        if (x != y) {
            throw new PairValuesNotEqualException();
        }
    }
}

// protect Pair inside a thread-safe class
abstract class PairManager {
    AtomicInteger checkCounter = new AtomicInteger(0);
    protected Pair pair = new Pair();
    private List<Pair> storage = Collections.synchronizedList(new ArrayList<>());

    public synchronized Pair getPair() {
        // make a copy, keep the original safe
        return new Pair(pair.getX(), pair.getY());
    }

    // assume this is a time consuming operation
    protected void store(Pair p) {
        storage.add(p);
        try {
            Thread.sleep(50);
        } catch (InterruptedException e) {
            System.out.println(e);
        }
    }

    public abstract void increment();
}

// synchronize the whole method
class PairManager1 extends PairManager {
    @Override
    public synchronized void increment() {
        pair.incrementX();
        pair.incrementY();
        store(getPair());
    }
}

// use critical section
class PairManager2 extends PairManager {
    Pair tmp;
    @Override
    public void increment() {
        synchronized (this) {
            pair.incrementX();
            pair.incrementY();
            tmp = getPair();
        }
        store(tmp);  // because storage is thread-safe data structure, we do not need to put it in the critical section
    }
}

class ExplicitPairManager1 extends PairManager {
    Lock lock = new ReentrantLock();
    @Override
    public void increment() {
        lock.lock();
        try {
            pair.incrementX();
            pair.incrementY();
            store(getPair());
        } finally {
            lock.unlock();
        }
    }
}

class ExplicitPairManager2 extends PairManager {
    Lock lock = new ReentrantLock();
    @Override
    public void increment() {
        Pair tmp;
        lock.lock();
        try {
            pair.incrementX();
            pair.incrementY();
            tmp = getPair();
        } finally {
            lock.unlock();
        }
        store(tmp);
    }
}

class PairManipulator implements Runnable {
    private PairManager pm;

    public PairManipulator(PairManager pm) {
        this.pm = pm;
    }

    @Override
    public void run() {
        while (true) {
            pm.increment();
        }
    }

    @Override
    public String toString() {
        return "Pair: " + pm.getPair() + " checkCounter: " + pm.checkCounter.get();
    }
}

class PairChecker implements Runnable {
    private PairManager pm;

    public PairChecker(PairManager pm) {
        this.pm = pm;
    }

    @Override
    public void run() {
        while (true) {
            pm.checkCounter.incrementAndGet();  // test increase times for both methods
            pm.getPair().checkState();
        }
    }
}
