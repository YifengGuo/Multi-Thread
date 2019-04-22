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

    volatile Meal meal;

    ExecutorService exec = Executors.newCachedThreadPool();

    WaitPerson person = new WaitPerson(this);

    BusBoy busBoy = new BusBoy(this);

    Chef chef = new Chef(this);

    public Restaurant() {
        exec.submit(person);
        exec.submit(busBoy);
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
    public boolean mealFinished;

    public WaitPerson(Restaurant restaurant) {
        this.restaurant = restaurant;
    }

    public void finishMeal() {
        mealFinished = true;
    }

    @Override
    public void run() {
        try {
            while (!Thread.interrupted()) {
                synchronized (this) {
                    if (restaurant.meal == null || !restaurant.chef.mealCooked || !restaurant.busBoy.isCleanedUp) {
                        wait();  // wait for the chef to cook the meal
                    }
                }

                System.out.println("WaitPerson got " + restaurant.meal);
                restaurant.busBoy.isCleanedUp = false;  // reset meal clean flag
                finishMeal();

                synchronized (restaurant.busBoy) {
                    // notify that meal was consumed by the customer and busBoy can clean the meal
                    restaurant.busBoy.notify();
                }
            }
        } catch (InterruptedException e) {
            System.out.println("WaitPerson exiting via interrupting...");
        }
    }
}

class Chef implements  Runnable {
    private Restaurant restaurant;
    private int count;  // count for current meal
    public boolean mealCooked;

    public Chef(Restaurant restaurant) {
        this.restaurant = restaurant;
    }

    private void cookMeal() {
        mealCooked = true;
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
                    restaurant.meal = new Meal(count++);
                    System.out.println(restaurant.meal + " cooked.");
                    cookMeal();
                    restaurant.person.notify();
                }
            }
        } catch (InterruptedException e) {
            System.out.println("Chef exiting via interrupting...");
        }
    }
}

class BusBoy implements Runnable {
    private Restaurant restaurant;

    public boolean isCleanedUp;

    public BusBoy(Restaurant restaurant) {
        this.restaurant = restaurant;
    }

    @Override
    public void run() {
        try {
            while (!Thread.interrupted()) {
                synchronized (this) {
                    if (restaurant.meal == null || !restaurant.person.mealFinished) {
                        wait(); // wait for the chef to cook the meal for the customer
                    }
                }

                // notify the chef to cook the next meal
                synchronized (restaurant.chef) {
                    System.out.println(restaurant.meal + " is cleaned up");
                    clearMeal();
                    restaurant.chef.notify();
                }
            }
        } catch (InterruptedException e) {
            System.out.println("BusBoy exiting via interrupting...");
        }
    }

    private void clearMeal() {
        restaurant.meal = null;
        isCleanedUp = true;
    }
}