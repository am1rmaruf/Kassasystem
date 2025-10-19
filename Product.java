public class Product {
    // Enkel klass för en produkt

    // Namn på varan
    private String name;

    // Pris i kronor, heltal räcker här
    private int price;

    // Standardkonstruktor
    public Product() {}

    // Namn
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    // Pris
    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    // Användbar om jag vill skriva ut produkten i test
    @Override
    public String toString() {
        return name + " - " + price + " kr";
    }
}
