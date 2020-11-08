import controller.Checker;
import controller.Controller;
import repository.Inventory;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class Main {

    public static void main(String[] args) {
        int nrTypesProducts = 100;
        int nrThreads = 200000;
        final ReadWriteLock checkerLock = new ReentrantReadWriteLock();


        Inventory inventory = new Inventory(nrTypesProducts);
        Controller controller = new Controller(inventory, nrTypesProducts, checkerLock);

        //start checker
        Timer timer = new Timer();
        Checker checker = new Checker(controller, checkerLock);
        timer.schedule(checker, 2000, 2000);

        //start threads
        Instant startTime = Instant.now();

        List<Thread> myClients = new ArrayList<>();
        for (int i = 0; i < nrThreads; i++) {
            Thread t = new Thread(controller);
            myClients.add(t);
            t.start();
        }



        //wait threads
        for (int i = 0; i < nrThreads; i++) {
            try {
                myClients.get(i).join();
            } catch (InterruptedException e) {
                e.printStackTrace();
                System.out.println("Error in thread!");
            }
        }
        System.out.println("<<<<<<<<<<<<< Total price " + controller.getTotalSales() + ">>>>>>>>>>>>>> \n");
        timer.cancel();
        timer.purge();
        System.out.println("Doing the last check now...........................");
        checker.run();

        System.out.println("\n Time took to complete: " + Duration.between(startTime, Instant.now()).getSeconds() + " seconds");
    }
}
