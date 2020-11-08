package domain;

import java.util.HashMap;
import java.util.Map;

public class Bill {
    private Map<Product, Integer> products;
    private double totalPrice;

    public Bill(){
        this.products = new HashMap<>();
        this.totalPrice = 0d;
    };

    public Bill(Map<Product, Integer> products, double totalPrice) {
        this.products = products;
        this.totalPrice = totalPrice;
    }

    public Map<Product, Integer> getProducts() {
        return products;
    }

    public void setProducts(Map<Product, Integer> products) {
        this.products = products;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }
}
