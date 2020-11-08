package controller;

import java.util.TimerTask;
import java.util.concurrent.locks.ReadWriteLock;


public class Checker extends TimerTask {
    private Controller controller;
    private final ReadWriteLock checkerLock;

    public Checker(Controller controller, ReadWriteLock checkerLock) {
        this.controller = controller;
        this.checkerLock = checkerLock;
    }

    @Override
    public void run() {
        checkerLock.writeLock().lock();
        System.out.println("I'm checking stuff; hold my beer ...");

            int supposedQuantity = this.controller.getSupposedQuantity();
            int actualQuantity = this.controller.getActualQuantity();
            assert (supposedQuantity == actualQuantity) : "supposedQuantity="+supposedQuantity + " ; actualQuantity="+actualQuantity;

            double supposedSales = this.controller.getSupposedSales();
            double actualSales = this.controller.getTotalSales();
            assert (supposedSales == actualSales) : "supposedSales="+supposedSales + " ; actualSales="+actualSales;

        System.out.println("All good boy ;)");
        checkerLock.writeLock().unlock();
    }
}
