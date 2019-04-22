package basic_java_thread.demo_thinking_in_java.communication_between_threads;

/**
 * Created by guoyifeng on 4/22/19
 */

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * A simple demo of produce-consumer
 * A chef will cook a meal only when meal is eaten up
 * A customer will eat the meal only when meal is available
 */
public class Restaurant {

    Meal meal;

    ExecutorService exec = Executors.newCachedThreadPool();

    WaitPerson person = new WaitPerson(this);

    Chef chef = new Chef(this);

    public Restaurant() {
        exec.submit(person);
        exec.submit(chef);
    }

    public static void main(String[] args) {
        new Restaurant();
    }
}

class Meal {
    private final int orderNum;

    public Meal(int orderNum) {
        this.orderNum = orderNum;
    }

    @Override
    public String toString() {
        return "Meal " + orderNum;
    }
}

class WaitPerson implements Runnable {
    private Restaurant restaurant;

    public WaitPerson(Restaurant restaurant) {
        this.restaurant = restaurant;
    }

    @Override
    public void run() {
        try {
            while (!Thread.interrupted()) {
                synchronized (this) {
                    if (restaurant.meal == null) {
                        wait();  // wait for the chef to cook the meal
                    }
                }

                TimeUnit.MILLISECONDS.sleep(100);  // take some time to eat
                System.out.println("WaitPerson got " + restaurant.meal);

                synchronized (restaurant.chef) {
                    // notify that meal was consumed by the customer and chef can do next meal
                    restaurant.meal = null;
                    restaurant.chef.notifyAll();
                }
            }
        } catch (InterruptedException e) {
            System.out.println("Exiting via interrupting...");
        }
    }
}

class Chef implements  Runnable {
    private Restaurant restaurant;
    private int count;  // count for current meal

    public Chef(Restaurant restaurant) {
        this.restaurant = restaurant;
    }

    @Override
    public void run() {
        try {
            while (!Thread.interrupted()) {
                synchronized (this) {
                    if (restaurant.meal != null) { // if the meal is not consumed yet, chef should be waiting
                        wait();
                    }
                }

                // task stop condition
                if (count == 10) {
                    System.out.println("All meals are cooked, closing up....");
                    restaurant.exec.shutdownNow();
                }

                // notify customer to eat the meal
                synchronized (restaurant.person) {
                    TimeUnit.MILLISECONDS.sleep(100);  // take some time to cook the meal
                    restaurant.meal = new Meal(count++);
                    System.out.println(restaurant.meal + " Finished.");
                    restaurant.person.notifyAll();
                }
            }
        } catch (InterruptedException e) {
            System.out.println("Exiting via interrupting...");
        }
    }
}