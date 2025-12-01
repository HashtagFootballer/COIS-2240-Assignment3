import java.util.List;
import java.time.LocalDate;
import java.util.ArrayList;
import java.io.*;
import java.nio.file.*; 

public class RentalSystem {
	private static RentalSystem instance; //added to reflect singleton design
    private List<Vehicle> vehicles = new ArrayList<>();
    private List<Customer> customers = new ArrayList<>();
    private RentalHistory rentalHistory = new RentalHistory();
    
    private RentalSystem() { //added to reflect singleton design (empty constructor)
    	loadData(); // added for task 1.3
    }
    //all following code is for task 1.3
    private void loadData() {
        loadVehicles();
        loadCustomers();
        loadRentalRecords();
    }
    private void loadVehicles() {
        try {
            if (!Files.exists(Paths.get("vehicles.txt"))) {
                return; // File doesn't exist, nothing to load
            }
            
            List<String> lines = Files.readAllLines(Paths.get("vehicles.txt"));
            for (String line : lines) {
                String[] parts = line.split(",");
                if (parts.length >= 5) {
                    String licensePlate = parts[0];
                    String make = parts[1];
                    String model = parts[2];
                    int year = Integer.parseInt(parts[3]);
                    Vehicle.VehicleStatus status = Vehicle.VehicleStatus.valueOf(parts[4]);
                    
                    // Create a basic Car (simplified for loading - in real system you'd store vehicle type)
                    Car vehicle = new Car(make, model, year, 5); // Default seats
                    vehicle.setLicensePlate(licensePlate);
                    vehicle.setStatus(status);
                    vehicles.add(vehicle);
                }
            }
        } catch (IOException e) {
            System.out.println("Error loading vehicles from file: " + e.getMessage());
        }
    }
    private void loadCustomers() {
        try {
            if (!Files.exists(Paths.get("customers.txt"))) {
                return; // File doesn't exist, nothing to load
            }
            
            List<String> lines = Files.readAllLines(Paths.get("customers.txt"));
            for (String line : lines) {
                String[] parts = line.split(",");
                if (parts.length >= 2) {
                    int customerId = Integer.parseInt(parts[0]);
                    String name = parts[1];
                    customers.add(new Customer(customerId, name));
                }
            }
        } catch (IOException e) {
            System.out.println("Error loading customers from file: " + e.getMessage());
        }
    }
    private void loadRentalRecords() {
        try {
            if (!Files.exists(Paths.get("rental_records.txt"))) {
                return; // File doesn't exist, nothing to load
            }
            
            List<String> lines = Files.readAllLines(Paths.get("rental_records.txt"));
            for (String line : lines) {
                String[] parts = line.split(",");
                if (parts.length >= 5) {
                    String licensePlate = parts[0];
                    int customerId = Integer.parseInt(parts[1]);
                    LocalDate recordDate = LocalDate.parse(parts[2]);
                    double totalAmount = Double.parseDouble(parts[3]);
                    String recordType = parts[4];
                    
                    Vehicle vehicle = findVehicleByPlate(licensePlate);
                    Customer customer = findCustomerById(customerId);
                    
                    if (vehicle != null && customer != null) {
                        RentalRecord record = new RentalRecord(vehicle, customer, recordDate, totalAmount, recordType);
                        rentalHistory.addRecord(record);
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("Error loading rental records from file: " + e.getMessage());
        }
    }
    //unchanged from here
    public static RentalSystem getInstance() { //the new method
        if (instance == null) {
            instance = new RentalSystem();
        }
        return instance; //returns a single instance of the class
    }

    public void addVehicle(Vehicle vehicle) {
        vehicles.add(vehicle);
        saveVehicle(vehicle);
        
    }

    public void addCustomer(Customer customer) {
        customers.add(customer);
        saveCustomer(customer);
    }

    public void rentVehicle(Vehicle vehicle, Customer customer, LocalDate date, double amount) {
        if (vehicle.getStatus() == Vehicle.VehicleStatus.Available) {
            vehicle.setStatus(Vehicle.VehicleStatus.Rented);
            rentalHistory.addRecord(new RentalRecord(vehicle, customer, date, amount, "RENT"));
            System.out.println("Vehicle rented to " + customer.getCustomerName());
            RentalRecord record = new RentalRecord(vehicle, customer, date, amount, "RENT");
            rentalHistory.addRecord(record);
            saveRecord(record);
        }
        else {
            System.out.println("Vehicle is not available for renting.");
        }
    }

    public void returnVehicle(Vehicle vehicle, Customer customer, LocalDate date, double extraFees) {
        if (vehicle.getStatus() == Vehicle.VehicleStatus.Rented) {
            vehicle.setStatus(Vehicle.VehicleStatus.Available);
            rentalHistory.addRecord(new RentalRecord(vehicle, customer, date, extraFees, "RETURN"));
            System.out.println("Vehicle returned by " + customer.getCustomerName());
            RentalRecord record = new RentalRecord(vehicle, customer, date, extraFees, "RETURN");
            rentalHistory.addRecord(record);
            saveRecord(record);
        }
        else {
            System.out.println("Vehicle is not rented.");
        }
    }    

    public void displayVehicles(Vehicle.VehicleStatus status) {
        // Display appropriate title based on status
        if (status == null) {
            System.out.println("\n=== All Vehicles ===");
        } else {
            System.out.println("\n=== " + status + " Vehicles ===");
        }
        
        // Header with proper column widths
        System.out.printf("|%-16s | %-12s | %-12s | %-12s | %-6s | %-18s |%n", 
            " Type", "Plate", "Make", "Model", "Year", "Status");
        System.out.println("|--------------------------------------------------------------------------------------------|");
    	  
        boolean found = false;
        for (Vehicle vehicle : vehicles) {
            if (status == null || vehicle.getStatus() == status) {
                found = true;
                String vehicleType;
                if (vehicle instanceof Car) {
                    vehicleType = "Car";
                } else if (vehicle instanceof Minibus) {
                    vehicleType = "Minibus";
                } else if (vehicle instanceof PickupTruck) {
                    vehicleType = "Pickup Truck";
                } else {
                    vehicleType = "Unknown";
                }
                System.out.printf("| %-15s | %-12s | %-12s | %-12s | %-6d | %-18s |%n", 
                    vehicleType, vehicle.getLicensePlate(), vehicle.getMake(), vehicle.getModel(), vehicle.getYear(), vehicle.getStatus().toString());
            }
        }
        if (!found) {
            if (status == null) {
                System.out.println("  No Vehicles found.");
            } else {
                System.out.println("  No vehicles with Status: " + status);
            }
        }
        System.out.println();
    }

    public void displayAllCustomers() {
        for (Customer c : customers) {
            System.out.println("  " + c.toString());
        }
    }
    
    public void displayRentalHistory() {
        if (rentalHistory.getRentalHistory().isEmpty()) {
            System.out.println("  No rental history found.");
        } else {
            // Header with proper column widths
            System.out.printf("|%-10s | %-12s | %-20s | %-12s | %-12s |%n", 
                " Type", "Plate", "Customer", "Date", "Amount");
            System.out.println("|-------------------------------------------------------------------------------|");
            
            for (RentalRecord record : rentalHistory.getRentalHistory()) {                
                System.out.printf("| %-9s | %-12s | %-20s | %-12s | $%-11.2f |%n", 
                    record.getRecordType(), 
                    record.getVehicle().getLicensePlate(),
                    record.getCustomer().getCustomerName(),
                    record.getRecordDate().toString(),
                    record.getTotalAmount()
                );
            }
            System.out.println();
        }
    }
    
    public Vehicle findVehicleByPlate(String plate) {
        for (Vehicle v : vehicles) {
            if (v.getLicensePlate().equalsIgnoreCase(plate)) {
                return v;
            }
        }
        return null;
    }
    
    public Customer findCustomerById(int id) {
        for (Customer c : customers)
            if (c.getCustomerId() == id)
                return c;
        return null;
    }
    //Added code for task 1.2
 // Save vehicle to file (called inside addVehicle)
    private void saveVehicle(Vehicle vehicle) {
        try {
            FileWriter writer = new FileWriter("vehicles.txt", true);
            writer.write(vehicle.getLicensePlate() + "," + vehicle.getMake() + "," + 
                        vehicle.getModel() + "," + vehicle.getYear() + "," + 
                        vehicle.getStatus() + "\n");
            writer.close();
        } catch (IOException e) {
            System.out.println("Error saving vehicle: " + e.getMessage());
        }
    }

    // Save customer to file (called inside addCustomer)
    private void saveCustomer(Customer customer) {
        try {
            FileWriter writer = new FileWriter("customers.txt", true);
            writer.write(customer.getCustomerId() + "," + customer.getCustomerName() + "\n");
            writer.close();
        } catch (IOException e) {
            System.out.println("Error saving customer: " + e.getMessage());
        }
    }

    // Save rental record to file (called in rentVehicle and returnVehicle)
    private void saveRecord(RentalRecord record) {
        try {
            FileWriter writer = new FileWriter("rental_records.txt", true);
            writer.write(record.getVehicle().getLicensePlate() + "," + 
                        record.getCustomer().getCustomerId() + "," + 
                        record.getRecordDate() + "," + 
                        record.getTotalAmount() + "," + 
                        record.getRecordType() + "\n");
            writer.close();
        } catch (IOException e) {
            System.out.println("Error saving rental record: " + e.getMessage());
        }
    }
}
