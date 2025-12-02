
package shopmanagement;

// Milagro and Robert

import java.io.*;
import java.util.Scanner;
import java.io.FileWriter;
import java.io.IOException;

 abstract class Product {
    private String id;
    private String name;
    private String category;
    private double price;
    private int quantity;
    
        public Product(String i, String n, String c, double p, int q) {
        id = i;
        name = n;
        category = c;
        price = p;
        quantity = q;
    }
    public void setQuantity(int q){ 
        quantity = q; }
    
    public void setPrice(double p) {
       price = p; }
    
    
    public String getId() { 
        return id; 
    }
        public String getName() { 
            return name;
        }
        public String getCategory() {
            return category;
        }
        public double getPrice() {
            return price; 
        }
        public int getQuantity() {
            return quantity;
        }

     public void displayDetails() {
        System.out.printf("ID: %s  Name: %s  Category: %s  Price: %.2f  Quantity: %d\n",
                id, name, category, price, quantity);
    }
     
 }

class FoodProduct extends Product {
    public FoodProduct(String id, String name, double price, int quantity) {
        super(id, name, "FoodProduct", price, quantity);
    }
}

class ElectronicsProduct extends Product {
    public ElectronicsProduct(String id, String name, double price, int quantity) {
        super(id, name, "ElectronicsProduct", price, quantity);
    }
}

class UtilityProduct extends Product {
    public UtilityProduct(String id, String name, double price, int quantity) {
        super(id, name, "UtilityProduct", price, quantity);
    }
}

class PharmacyProduct extends Product {
    public PharmacyProduct(String id, String name, double price, int quantity) {
        super(id, name, "PharmacyProduct", price, quantity);
    }
}

abstract class MethodManager {
    
    public abstract void addItem(Product p);
    public abstract void removeItem(String id);
    public abstract void displayAllItems();
    public abstract void displayByCategory(String category);
    public abstract boolean itemExists(String id);
    public abstract void getItemDetails(String id);
    
}


class InventoryManager extends MethodManager{ 
    
      private final String filePath = "inventory.txt";

    
    public void addItem(Product p) {
        try (PrintWriter out = new PrintWriter(new FileWriter(filePath, true))) {
            out.printf("%s %s %s %.2f %d\n", p.getId(), p.getName(), p.getCategory(), p.getPrice(), p.getQuantity());
            System.out.println("Product added to file.");
        } catch (IOException e) {
            System.out.println("Error writing to file: " + e.getMessage());
        }
    }

       public void removeItem(String id) {
        File inputFile = new File(filePath);
        File tempFile = new File("temp.txt");

        try (Scanner scanner = new Scanner(inputFile); PrintWriter writer = new PrintWriter(new FileWriter(tempFile))) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                if (!line.startsWith(id)) {
                    writer.println(line); 
                }
            }
        } catch (IOException e) {
            System.out.println("Error processing files.");
        }

        inputFile.delete();
        tempFile.renameTo(inputFile);
    }

    public void displayAllItems() {
        try (Scanner input = new Scanner(new File(filePath))) {
            while (input.hasNextLine()) {
                System.out.println(input.nextLine());
            }
        } catch (FileNotFoundException e) {
            System.out.println("Inventory file not found.");
        }
    }

    public void displayByCategory(String category) {
        System.out.println("\n=== " + category + " ===");
        try (Scanner input = new Scanner(new File(filePath))) {
            while (input.hasNextLine()) {
                String line = input.nextLine();
                if (line.contains(category)) {
                    System.out.println(line);
                }
            }
        } catch (FileNotFoundException e) {
            System.out.println("Inventory file not found.");
        }
    }

    public boolean itemExists(String id) {
        try (Scanner scanner = new Scanner(new File(filePath))) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                if (line.startsWith(id)) {
                    return true;
                }
            }
        } catch (FileNotFoundException e) {
            System.out.println("Inventory file not found.");
        }
        return false;
    }

    public void getItemDetails(String id) {
        String infoPath = "product_info.txt";
        String category = "", name = "";
        double price = 0.0;
        int quantity = 0;
        String extraInfo = "Not available";

        try (Scanner invScanner = new Scanner(new File(filePath))) {
            while (invScanner.hasNextLine()) {
                Scanner lineScanner = new Scanner(invScanner.nextLine());
                if (lineScanner.hasNext() && lineScanner.next().equals(id)) {
                    name = lineScanner.next();
                    category = lineScanner.next();
                    price = lineScanner.nextDouble();
                    quantity = lineScanner.nextInt();
                    break;
                }
            }
        } catch (FileNotFoundException e) {
            System.out.println("Inventory file not found.");
            return;
        }

        try (Scanner infoScanner = new Scanner(new File(infoPath))) {
            while (infoScanner.hasNextLine()) {
                Scanner lineScanner = new Scanner(infoScanner.nextLine());
                if (lineScanner.hasNext() && lineScanner.next().equals(id)) {
                    extraInfo = lineScanner.nextLine().trim();
                    break;
                }
            }
        } catch (FileNotFoundException e) {
            System.out.println("Product info file not found.");
        }

        System.out.printf("ID: %s | Name: %s | Category: %s | Price: %.2f | Quantity: %d\n",
                id, name, category, price, quantity);

        if (category.equalsIgnoreCase("FoodProduct")) {
            extraInfo = "Best before: " + extraInfo;
        } else if (category.equalsIgnoreCase("ElectronicsProduct")) {
            extraInfo = "Voltage: " + extraInfo;
        } else if (category.equalsIgnoreCase("UtilityProduct")) {
            extraInfo = "Purpose: " + extraInfo;
        } else if (category.equalsIgnoreCase("PharmacyProduct")) {
            extraInfo = "Description: " + extraInfo;
        }

        System.out.println("Extra Info: " + extraInfo);
    }
}



abstract class Transaction {
    protected String orderCode;
    protected String itemName;
    protected int quantity;
    protected double totalPrice;
    protected String paymentMethod;
    
    protected String customerName;
    protected String cardNumber;
    protected String cardType;

    public Transaction(String oc, String iname, int qt, double unitPrice, String paymeth) {
        orderCode = oc;
        itemName = iname;
        paymentMethod = paymeth;

         double availability = checkAvailableStock(iname, qt);

    if (availability != -1) {
        
        quantity = qt;
        totalPrice = calculateOrder(availability);
        updateInventory(iname, qt);
        writeToFile();
        System.out.printf("Added %d %s for $%.2f\n",qt , iname, totalPrice);
        
    } else {
        System.out.println("Transaction failed: Not enough stock available for " + iname);
        
        quantity = 0;
        totalPrice = 0;
        

    }
}

    public abstract double calculateOrder(double unitPrice);

    public void writeToFile() {
        if (quantity == 0) return; 

        try {
            FileWriter fw = new FileWriter("sales.txt", true);
            fw.write("Order Code: " + orderCode + "\n");
            fw.write("Item: " + itemName + ", Quantity: " + quantity + "\n");
            fw.write("Payment Method: " + paymentMethod + ", Total Price: " + String.format("%.2f", totalPrice) + "\n");
            fw.write("------------------------------------------------\n");
            fw.close();
        } catch (IOException e) {
            System.out.println("Error writing to sales file: " + e.getMessage());
        }
    }
    
    public void writeCustomerToFile() {
        if (paymentMethod.equalsIgnoreCase("CARD")) {
            
            try {
                FileWriter customerWriter = new FileWriter("customers.txt", true);
                customerWriter.write("Customer Name: " + customerName + "\n");
                customerWriter.write("Card Number: " + cardNumber + "\n");
                customerWriter.write("Card Type: " + cardType + "\n");
                customerWriter.write("Order Code: " + orderCode + "\n");
                customerWriter.write("------------------------------------------------\n");
                customerWriter.close();
                
            } catch (IOException e) {
                System.out.println("Error writing to customer file: " + e.getMessage());
            }
        }
    }

    public static double checkAvailableStock(String itemName, int requestedQuantity) {
        try (Scanner scanner = new Scanner(new File("inventory.txt"))) {
            while (scanner.hasNextLine()) {
                
                Scanner lineScanner = new Scanner(scanner.nextLine());
                
                String id = lineScanner.next();
                String name = lineScanner.next();
                String category = lineScanner.next();
                double price = lineScanner.nextDouble();
                int qty = lineScanner.nextInt();
                
                if (name.equalsIgnoreCase(itemName)) {
                if (requestedQuantity <= qty) {
                    return price; 
                } else {
                    System.out.println("Insufficient stock. Available quantity: " + qty);
                    return -1; 
                }
            }
                lineScanner.close();
            }
            
        } catch (IOException e) {
            System.out.println("Error reading inventory: " + e.getMessage());
        }
        System.out.println("Item not found in inventory.");
        return -1;
    }

    private void updateInventory(String itemName, int purchasedQuantity) {
        File inputFile = new File("inventory.txt");
        File tempFile = new File("temp_inventory.txt");

        try (
            Scanner scanner = new Scanner(inputFile);
            PrintWriter writer = new PrintWriter(new FileWriter(tempFile))
        ) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                Scanner lineScanner = new Scanner(line);

                if (!lineScanner.hasNext()) continue;

                String id = lineScanner.next();
                String name = lineScanner.next();
                String category = lineScanner.next();
                double price = lineScanner.nextDouble();
                int qty = lineScanner.nextInt();

                if (name.equalsIgnoreCase(itemName)) {
                    qty -= purchasedQuantity;
                    if (qty < 0) qty = 0;
                }

                writer.printf("%s %s %s %.2f %d%n", id, name, category, price, qty);
                lineScanner.close();
            }
        } catch (IOException e) {
            System.out.println("Error updating inventory: " + e.getMessage());
        }

        inputFile.delete();
        tempFile.renameTo(inputFile);
    }
}
    


class CashTransaction extends Transaction {
    
    public CashTransaction(String orderCode, String itemName, int quantity, double unitPrice) {
   
    super(orderCode, itemName, quantity, unitPrice, "CASH");
    
    }

    public double calculateOrder(double unitPrice) {
    return unitPrice * quantity * 1.15; 
    }
}

class CardTransaction extends Transaction {
    
    public CardTransaction(String orderCode, String itemName, int quantity, double unitPrice,
                           String custname, String cardNum, String Type) {
        super(orderCode, itemName, quantity, unitPrice, "CARD");
        customerName = custname;
        cardNumber = cardNum;
        cardType = Type;
    }

    
    public double calculateOrder(double unitPrice) {
    return unitPrice * quantity * 1.18;
    }
}



public class ShopManagement {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        InventoryManager manager = new InventoryManager();
     
        int choice;
        
        do {
            System.out.println("=-=-=Shop Management Menu=-=-=");
            System.out.println("1. Add Item");
            System.out.println("2. Remove Item");
            System.out.println("3. Display All Items");
            System.out.println("4. Display Items by Category");
            System.out.println("5. Get Item Details");
            System.out.println("6. Process Cash Transaction");
            System.out.println("7. Process Card Transaction");
            System.out.println("8. Exit");
            System.out.print("Enter your choice: ");
            choice = scanner.nextInt();
            scanner.nextLine();
            
            switch (choice) {
                case 1:
                    
                    System.out.print("Enter product ID\n(First letter of category name\n+ four numbers eg. F1234): ");
                    String id = scanner.nextLine();
                    
                    System.out.print("Enter product name\n(No spaces between words): ");
                    String name = scanner.nextLine();
                    
                    System.out.print("Enter product category: ");
                    String category = scanner.nextLine();
                    
                    System.out.print("Enter product price: ");
                    double price = scanner.nextDouble();
                    
                    System.out.print("Enter product quantity: ");
                    int quantity = scanner.nextInt();
                    
                    Product newProduct = switch (category) {
                        case "FoodProduct" -> new FoodProduct(id, name, price, quantity);
                        case "ElectronicsProduct" -> new ElectronicsProduct(id, name, price, quantity);
                        case "UtilityProduct" -> new UtilityProduct(id, name, price, quantity);
                        case "PharmacyProduct" -> new PharmacyProduct(id, name, price, quantity);
                        default -> null;
                    };
                    if (newProduct != null) {
                        manager.addItem(newProduct);
                    } else {
                        System.out.println("Invalid category.");
                    }
                    break;
                
                    
                    
                case 2:
                   
                    System.out.print("Enter product ID to remove: ");
                    String removeId = scanner.nextLine();
                    manager.removeItem(removeId);
                    break;
                
                    
                    
                case 3:
                    
                    manager.displayAllItems();
                    break;
                
                    
                    
                case 4:
                    
                    System.out.print("Enter category to display: ");
                    String displayCategory = scanner.nextLine();
                    manager.displayByCategory(displayCategory);
                    break;
                
                    
                    
                case 5:
                    
                    System.out.print("Enter product ID to get details: ");
                    String detailsId = scanner.nextLine();
                    manager.getItemDetails(detailsId);
                    break;
                
                    
                    
                case 6:
                    
                    System.out.print("Enter order code: ");
                    String cashOrderCode = scanner.nextLine();
                    
                    System.out.print("Enter product name for transaction: ");
                    String cashItemName = scanner.nextLine();
                    
                    System.out.print("Enter quantity: ");
                    int cashQuantity = scanner.nextInt();
                    
                    double cashUnitPrice = Transaction.checkAvailableStock(cashItemName, cashQuantity);
                    if (cashUnitPrice == -1) { continue; }
                    
                    CashTransaction cashTransaction = new CashTransaction(cashOrderCode, cashItemName, cashQuantity, cashUnitPrice);
                    
                    cashTransaction.writeToFile();
                    break;
                
                    
                    
                case 7:
                    
                    System.out.print("Enter order code: ");
                    String cardOrderCode = scanner.nextLine();
                    
                    System.out.print("Enter product name for transaction: ");
                    String cardItemName = scanner.nextLine();

                    System.out.print("Enter quantity: ");
                    int cardQuantity = scanner.nextInt();

                    double cardUnitPrice = Transaction.checkAvailableStock(cardItemName, cardQuantity);

                    if (cardUnitPrice == -1) {   
                        continue; 
                    }

                    CardTransaction ct = new CardTransaction(cardOrderCode, cardItemName, cardQuantity, cardUnitPrice, "Jane Doe", "1234-5678-9012", "Visa");
                    ct.writeToFile();
                    ct.writeCustomerToFile();
                    break;
                
                    
                    
                case 8:
                    
                    System.out.println("Exiting...");
                    break;
                
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        } while (choice != 8);
        
        scanner.close();
        
        
        
    }
}

