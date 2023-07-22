import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

// Abstract class representing a user
abstract class User {
    private String username;
    private String password;

    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }
}

// Class representing the owner
class Owner extends User {
    public Owner(String username, String password) {
        super(username, password);
    }

    public void addItem(Menu menu, String name, double price) {
        MenuItem item = new MenuItem(name, price);
        menu.addItem(item);
        System.out.println("Item added to the menu: " + item);
    }
}

// Class representing a customer
class Customer extends User {
    public Customer(String username, String password) {
        super(username, password);
    }
}

// Class representing an item on the menu
class MenuItem {
    private String name;
    private double price;

    public MenuItem(String name, double price) {
        this.name = name;
        this.price = price;
    }

    public String getName() {
        return name;
    }

    public double getPrice() {
        return price;
    }

    @Override
    public String toString() {
        return name + " - TK" + price;
    }
}

// Class representing the menu
class Menu {
    private List<MenuItem> items;

    public Menu() {
        items = new ArrayList<>();
    }

    public void addItem(MenuItem item) {
        items.add(item);
    }

    public List<MenuItem> getItems() {
        return items;
    }

    public void saveMenuToFile(String filename) {
        try (PrintWriter writer = new PrintWriter(filename)) {
            for (MenuItem item : items) {
                writer.println(item.getName() + "," + item.getPrice());
            }
            System.out.println("Menu saved to " + filename);
        } catch (FileNotFoundException e) {
            System.out.println("Failed to save menu to file: " + e.getMessage());
        }
    }

    public void loadMenuFromFile(String filename) {
        try (Scanner scanner = new Scanner(new File(filename))) {
            items.clear();
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                String[] parts = line.split(",");
                if (parts.length == 2) {
                    String name = parts[0];
                    double price = Double.parseDouble(parts[1]);
                    addItem(new MenuItem(name, price));
                }
            }
            System.out.println("Menu loaded from " + filename);
        } catch (FileNotFoundException e) {
            System.out.println("Failed to load menu from file: " + e.getMessage());
        }
    }
}

// Class representing the food ordering system
public class FoodOrderingSystem {
    private static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        Menu menu = new Menu();
        Owner owner = new Owner("admin", "admin123");
        Customer customer = new Customer("customer", "customer123");

        // Load menu from file (if available)
        menu.loadMenuFromFile("menu.txt");

        while (true) {
            System.out.println("Welcome to the Food Ordering System!");
            System.out.println("1. Owner Login");
            System.out.println("2. Customer Login");
            System.out.println("3. Exit");
            System.out.print("Enter your choice: ");
            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline character

            switch (choice) {
                case 1:
                    if (loginAsOwner(owner)) {
                        handleOwnerMenu(owner, menu);
                    } else {
                        System.out.println("Invalid username or password!");
                    }
                    break;
                case 2:
                    if (loginAsCustomer(customer)) {
                        handleCustomerMenu(customer, menu);
                    } else {
                        System.out.println("Invalid username or password!");
                    }
                    break;
                case 3:
                    // Save menu to file before exiting
                    menu.saveMenuToFile("menu.txt");
                    System.out.println("Thank you for using the Food Ordering System. Goodbye!");
                    return;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    public static boolean loginAsOwner(Owner owner) {
        System.out.print("Enter username: ");
        String username = scanner.nextLine();
        System.out.print("Enter password: ");
        String password = scanner.nextLine();

        return username.equals(owner.getUsername()) && password.equals(owner.getPassword());
    }

    public static boolean loginAsCustomer(Customer customer) {
        System.out.print("Enter username: ");
        String username = scanner.nextLine();
        System.out.print("Enter password: ");
        String password = scanner.nextLine();

        return username.equals(customer.getUsername()) && password.equals(customer.getPassword());
    }

    public static void handleOwnerMenu(Owner owner, Menu menu) {
        while (true) {
            System.out.println("\n--- Owner Menu ---");
            System.out.println("1. Add Item to Menu");
            System.out.println("2. View Menu");
            System.out.println("3. Logout");
            System.out.print("Enter your choice: ");
            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline character

            switch (choice) {
                case 1:
                    System.out.print("Enter item name: ");
                    String itemName = scanner.nextLine();
                    System.out.print("Enter item price: ");
                    double itemPrice = scanner.nextDouble();
                    scanner.nextLine(); // Consume newline character
                    owner.addItem(menu, itemName, itemPrice);
                    break;
                case 2:
                    System.out.println("\n--- Menu ---");
                    List<MenuItem> items = menu.getItems();
                    if (items.isEmpty()) {
                        System.out.println("No items in the menu.");
                    } else {
                        for (MenuItem item : items) {
                            System.out.println(item);
                        }
                    }
                    break;
                case 3:
                    System.out.println("Logged out of owner account.");
                    return;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    public static void handleCustomerMenu(Customer customer, Menu menu) {
        List<MenuItem> items = menu.getItems();
        List<MenuItem> order = new ArrayList<>();

        while (true) {
            System.out.println("\n--- Customer Menu ---");
            System.out.println("1. View Menu");
            System.out.println("2. Add Item to Order");
            System.out.println("3. View Order");
            System.out.println("4. Place Order");
            System.out.println("5. Logout");
            System.out.print("Enter your choice: ");
            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline character

            switch (choice) {
                case 1:
                    System.out.println("\n--- Menu ---");
                    if (items.isEmpty()) {
                        System.out.println("No items in the menu.");
                    } else {
                        for (MenuItem item : items) {
                            System.out.println(item);
                        }
                    }
                    break;
                case 2:
                    if (items.isEmpty()) {
                        System.out.println("No items in the menu.");
                    } else {
                        System.out.print("Enter item name to add to order: ");
                        String itemName = scanner.nextLine();
                        MenuItem selectedItem = null;
                        for (MenuItem item : items) {
                            if (item.getName().equalsIgnoreCase(itemName)) {
                                selectedItem = item;
                                break;
                            }
                        }
                        if (selectedItem != null) {
                            order.add(selectedItem);
                            System.out.println("Item added to order: " + selectedItem);
                        } else {
                            System.out.println("Invalid item name.");
                        }
                    }
                    break;
                case 3:
                    System.out.println("\n--- Order ---");
                    if (order.isEmpty()) {
                        System.out.println("No items in the order.");
                    } else {
                        double total = 0.0;
                        for (MenuItem item : order) {
                            System.out.println(item);
                            total += item.getPrice();
                        }
                        System.out.println("Total: TK" + total);
                    }
                    break;
                case 4:
                    if (order.isEmpty()) {
                        System.out.println("No items in the order. Cannot place an empty order.");
                    } else {
                        double total = 0.0;
                        for (MenuItem item : order) {
                            total += item.getPrice();
                        }
                        System.out.println("Order placed! Total amount: TK" + total);
                        order.clear();
                    }
                    break;
                case 5:
                    System.out.println("Logged out of customer account.");
                    return;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }
}
