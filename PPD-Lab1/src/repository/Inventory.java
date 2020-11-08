package repository;

import domain.Product;

import java.util.*;

public class Inventory {
    private Map<Integer, Product> products;
    private int initialQuantity = 0;

    public int getInitialQuantity() {
        return initialQuantity;
    }

    public Inventory(Integer nrTypesProducts) {
        this.products = new HashMap<>();
        // generate products; (with price 1 and quantity 1.000.000)
        for (int i = 0; i < nrTypesProducts; i++) {
            double price = 1d;
            int quantity = 1000000;
            Product p = new Product(i, price, quantity);
            products.put(p.getId(), p);
            this.initialQuantity += quantity;
        }
    }

    public Product getProductById(Integer id){
        return this.products.get(id);
    }

    public void decreaseQuantityOfProduct(Integer id, int quantity){
        Product product = this.products.get(id);
        if(product != null){
            product.setQuantity(product.getQuantity() - quantity);
        }
    }

    public Map<Integer, Product> getAllProducts(){
        return this.products;
    }

    public int getNrTypesOfProductsInInventory(){
        return this.products.size();
    }


    @Override
    public String toString() {
        return "repository.Inventory{" +
                "products=" + products +
                '}';
    }
}
