package basic_java_thread.singleton;

/**
 * Created by guoyifeng on 5/5/18.
 */


/*
 * unsafe wrong way:
 * This way is lazy-load mode, but this implementation is NOT thread safe.
 *
 */
class UnsafeLazySingleton {
    private static UnsafeLazySingleton INSTANCE;
    private UnsafeLazySingleton() {}

    public static UnsafeLazySingleton getInstance() {
        if (INSTANCE == null) {  // Thread A
            INSTANCE = new UnsafeLazySingleton();  // problems happens here:
                                                   // Thread B, because 1-2-3 or 1-3-2 reorder during Thread A is initializing class,
                                                   // may see an uninitialized class  ("3" instance = memory so INSTANCE is not null but "2" ctorInstance(memory) is on the way)
        }
        return INSTANCE;
    }
}

/*
 * thread safe way with synchronized
 * make critical section to guarantee thread safe
 * However this implementation is not efficient
 * because the synchronization is needed when first time
 * to create singleton instance, after that synchronization
 * is useless
 */
class SafeLazySingleton {
    private static SafeLazySingleton INSTANCE;
    private SafeLazySingleton() {}

    public static synchronized SafeLazySingleton getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new SafeLazySingleton();
        }
        return INSTANCE;
    }
}

/*
 * third way: try to update SafeLazySingleton performance but still have problem
 * Double-checked locking pattern, if INSTANCE is NOT null, then return INSTANCE directly, no need to enter critical section
 * In this way, everything is good except:
 *              INSTANCE = new DoubleCheckedLockingSingleton();
 * This line may have problem because this assignment code is not atomic
 * Actually in JVM, three things happened here:
 *     1. allocate memory space for INSTANCE   memory = allocate()
 *     2. initialize INSTANCE    ctorInstance(memory)
 *     3. make INSTANCE point to the memory space allocated for it (after this INSTANCE is NOT null any more)    instance = memory
 *
 * However there could be optimization because of JIT JVM (to reorder instructions)
 * So JVM could not guarantee the execution order of step2 and step3.
 * The final execution order could be 1-2-3 or 1-3-2, no one knows.
 * And if the execution order is 1-3-2, then after step3 is executed and before step2 is executed,
 * there is a thread2 to use the critical section, and at this moment, INSTANCE is not null but
 * it has not been initialized by constructor() and then be returned by thread2 which will throw exception without any doubt
 */
class DoubleCheckedLockingSingleton {
    private static DoubleCheckedLockingSingleton INSTANCE;
    private DoubleCheckedLockingSingleton() {}

    public static DoubleCheckedLockingSingleton getInstance() {
        if (INSTANCE == null) {                  // single checked
            synchronized(DoubleCheckedLockingSingleton.class) {
                if (INSTANCE == null) {          // double checked
                    INSTANCE = new DoubleCheckedLockingSingleton();  // problem may happen here
                }
            }
        }
        return INSTANCE;
    }
}

/**
 * Two ideas to solve 1-3-2 INSTANCE initialization problem
 * 1. disable reorder on 1-3-2 -> volatile keyword
 * 2. allow step 2 and step 3 reorder but prohibit any other threads perceive this reorder -> Initialization On Demand Holder idiom
 */


/*
 * volatile way:  disable instruction reorder
 */
class VolatileSingleton {
    private static volatile VolatileSingleton INSTANCE;
    private VolatileSingleton() {}

    public static VolatileSingleton getInstance() {
        if (INSTANCE == null) {                  // single checked
            synchronized(VolatileSingleton.class) {
                if (INSTANCE == null) {          // double checked
                    INSTANCE = new VolatileSingleton();
                }
            }
        }
        return INSTANCE;
    }
}

/*
 * Initialization On Demand Holder idiom way:
 * JVM initializes class at Initialization stage (after class was loaded and before it can be used by threads)
 * for details {@link https://github.com/YifengGuo/JVM-Study/blob/master/JVM_General.md}
 *
 * For each class or interface C, there must be a lock LC to be acquired for C during initialization stage,
 * each thread would acquire this LC at least once to make sure C has been initialized
 *
 * 5 stages during class initialization at multiple threads environment:
 *  stage 1: Thread synchronize on C (acquire LC), change C's state from noInitialization to initializing
 *                time                       thread A                                                    thread B
 *                t1                       try to acquire LC                                            LC was acquired by A, then wait LC to be released
 *                t2                       set class state: noInitialization ->  initializing
 *                t3                       release LC
 *
 *  stage 2: Thread A completes initialization of C, Thread B waiting for C to be initialized
 *                t4                       class static initialization and static fields                acquire LC of class
 *                                         (1-3-2 or 1-2-3, but other threads cannot perceive it)
 *
 *                                  T4 is essential, this moment guarantees class has been initialized only once and by only one thread, and
 *                                  reorder of instructions is transparent to any other threads
 *
 *                t5                                                                                     found state = initializing
 *                t6                                                                                     release LC
 *                t7                                                                                     waiting for Condition signal of LC
 *
 *  stage 3: Thread A set state = initialized, Condition signalAll()
 *                t8                       acquire LC of class
 *                t9                       set state = initialized
 *                t10                      Condition signalAll() all waiting threads
 *                t11                      release LC
 *                t12                      class initialization by Thread A completes
 *
 *  stage 4: Thread B completes class initialization "for its sight"
 *                t13                                                                                    acquire LC
 *                t14                                                                                    found state = initialized
 *                t15                                                                                    release LC
 *                t16                                                                                    Thread completes class initialization "for its sight"
 *
 *  stage 5: Thread C tries to initialize class, then repeats stage 4 and does not need to wait on LC condition
 *
 */
class Singleton0 {
    private static class SingletonHolder {
        public static final Singleton0 INSTANCE = new Singleton0();  // initialization of class, state = initializing, other threads does not hold lock and are waiting for lock condition signalAll()
    }
    private Singleton0 () {}

    public static final Singleton0 getInstance() {
        return SingletonHolder.INSTANCE;  // cause initialization of class
    }
}



/*
 * a little potential problem of this way is that if the constructor of Singleton depends on some
 * parameter or configuration file which means it need invoke some method to get the parameter before
 * invoking getInstance(), this way cannot work
 *
 * to solve this, we can use static nested class: Singleton0
 */
public class Singleton {
    private static final Singleton INSTANCE = new Singleton();//make sure only one instance in the class Singleton

    private Singleton() {}//explicitly declare constructor which is private to prohibit other constructors

    public static Singleton getInstance() { // always return the only existed instance INSTANCE which is shared by
        //all the instances of this class
        return INSTANCE;
    }

}



/*
 * easiest way: Enum
 * Enum is thread safe by default
 * and it can also avoid recreate INSTANCE object
 * by deserialization
 */
enum EasySingleton {
    INSTANCE;
}