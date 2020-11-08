package controller;

import domain.Bill;
import domain.Product;
import repository.Inventory;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantLock;

public class Controller implements Runnable {
    private List<Bill> allSales;
    private double totalSales;
    private Inventory inventory;
    private int nrTypesProducts; // variable used to know the number of types of products in the inventory
    private final ReadWriteLock checkerLock;

    private final ReentrantLock mutex = new ReentrantLock();


    public Controller(Inventory inventory, int nrProducts, ReadWriteLock checkerLock) {
        this.allSales = new ArrayList<>();
        this.totalSales = 0d;
        this.inventory = inventory;
        this.nrTypesProducts = nrProducts;
        this.checkerLock = checkerLock;
    }

    public double getTotalSales() {
        return totalSales;
    }

    public double getSupposedSales(){
        return this.allSales.stream().map(bill -> bill.getTotalPrice()).reduce(0d, Double::sum);
    }

    public int getActualQuantity(){
        return this.inventory.getAllProducts().values().stream().map(Product::getQuantity).reduce(0, Integer::sum);
    }

    public int getSupposedQuantity(){
        return this.inventory.getInitialQuantity() -
                this.allSales.stream().map(bill -> bill.getProducts().values().stream().reduce(0, Integer::sum))
                        .reduce(0, Integer::sum);
    }

    private void doSaleWorse(long threadId){
        checkerLock.readLock().lock();
        mutex.lock();
        Bill bill = new Bill();
        Random r = new Random();

        //choose 10 random products and add them to the bill
        while(bill.getProducts().size() < 10){
            Product product = this.inventory.getProductById(r.nextInt(nrTypesProducts));

            int quantity = 10;

            if(product != null && !bill.getProducts().containsKey(product)){
                //add product to bill
                bill.getProducts().put(product, quantity);

                //decrease quantity of product
                this.inventory.decreaseQuantityOfProduct(product.getId(), quantity);

                //increase total price of bill
                bill.setTotalPrice(bill.getTotalPrice() +  product.getPrice() * quantity);
            }
        }
        //increase total profit from all sales
        this.totalSales = this.totalSales + bill.getTotalPrice();
        this.allSales.add(bill);

        mutex.unlock();
        checkerLock.readLock().unlock();
    }

    private void doSaleBetter(long threadId){
        checkerLock.readLock().lock();
        Bill bill = new Bill();
        Random r = new Random();
        boolean locked = false;

        //choose 10 random products and add them to the bill
        while(bill.getProducts().size() < 10){
            Product product = this.inventory.getProductById(r.nextInt(nrTypesProducts));
            int quantity = 10;

            if(product != null && !bill.getProducts().containsKey(product)){
                //add product to bill
                bill.getProducts().put(product, quantity);

                if(!mutex.isHeldByCurrentThread()){
                    mutex.lock();
                }
                //decrease quantity of product
                this.inventory.decreaseQuantityOfProduct(product.getId(), quantity);

                //increase total price of bill
                bill.setTotalPrice(bill.getTotalPrice() +  product.getPrice() * quantity);
            }
        }
        //increase total profit from all sales
        this.totalSales = this.totalSales + bill.getTotalPrice();
        this.allSales.add(bill);

        mutex.unlock();
        checkerLock.readLock().unlock();
    }

    private void doSaleTheBest(long threadId){
        checkerLock.readLock().lock();
        Bill bill = new Bill();
        Random r = new Random();

        //choose 10 random products and add them to the bill
        while(bill.getProducts().size() < 10){
            Product product = this.inventory.getProductById(r.nextInt(nrTypesProducts));
            int quantity = 10;

            if(product != null && !bill.getProducts().containsKey(product)){
                //add product to bill
                bill.getProducts().put(product, quantity);

                mutex.lock();
                //decrease quantity of product
                this.inventory.decreaseQuantityOfProduct(product.getId(), quantity);
                mutex.unlock();

                //increase total price of bill
                bill.setTotalPrice(bill.getTotalPrice() +  product.getPrice() * quantity);
            }
        }
        //increase total profit from all sales
        mutex.lock();
        this.totalSales = this.totalSales + bill.getTotalPrice();
        mutex.unlock();

        mutex.lock();
        this.allSales.add(bill);
        mutex.unlock();

        checkerLock.readLock().unlock();
    }

    private void doSaleUltraBest(long threadId){
        checkerLock.readLock().lock();
        Bill bill = new Bill();
        Random r = new Random();

        //choose 10 random products and add them to the bill
        while(bill.getProducts().size() < 10){
            Product product = this.inventory.getProductById(r.nextInt(nrTypesProducts));
            int quantity = 10;

            if(product != null && !bill.getProducts().containsKey(product)){
                //add product to bill
                bill.getProducts().put(product, quantity);

                synchronized (product)
                {
                    //decrease quantity of product
                    this.inventory.decreaseQuantityOfProduct(product.getId(), quantity);
                }

                //increase total price of bill
                bill.setTotalPrice(bill.getTotalPrice() +  product.getPrice() * quantity);
            }
        }
        //increase total profit from all sales
        mutex.lock();
        this.totalSales = this.totalSales + bill.getTotalPrice();
        mutex.unlock();

        mutex.lock();
        this.allSales.add(bill);
        mutex.unlock();

        checkerLock.readLock().unlock();
    }


    @Override
    public void run() {
        Thread t = Thread.currentThread();
//        this.doSaleWorse(t.getId());
//        this.doSaleBetter(t.getId());
//        this.doSaleTheBest(t.getId());
//        this.doSaleUltraBest(t.getId());
    }

    @Override
    public String toString() {
        return "controller.Controller{" +
                "allSales=" + allSales +
                ", totalSales=" + totalSales +
                ", inventory=" + inventory +
                '}';
    }
}
