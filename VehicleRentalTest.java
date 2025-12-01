import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;
import java.time.LocalDate;

public class VehicleRentalTest {
    
    private RentalSystem rentalSystem;
    private Car testVehicle;
    private Customer testCustomer;

    @BeforeEach
    public void setUp() {
        rentalSystem = RentalSystem.getInstance();
        
        // Create test vehicle with VALID license plate
        testVehicle = new Car("Toyota", "Camry", 2022, 5);
        
        // Use a definitely valid license plate format
        testVehicle.setLicensePlate("ABC123"); // This should definitely work
        rentalSystem.addVehicle(testVehicle);
        
        // Create test customer
        testCustomer = new Customer(100, "Test Customer");
        rentalSystem.addCustomer(testCustomer);
    }

    @Test
    public void testRentAndReturnVehicle() {
        System.out.println("Testing rent/return vehicle...");
        
        // Step 1: Ensure vehicle is initially available
        assertEquals(Vehicle.VehicleStatus.Available, testVehicle.getStatus(), 
            "Vehicle should start as Available");
        System.out.println("✓ Vehicle initial status: " + testVehicle.getStatus());
        
        // Step 2: First rental - should succeed
        boolean firstRentResult = rentalSystem.rentVehicle(testVehicle, testCustomer, LocalDate.now(), 50.0);
        assertTrue(firstRentResult, "First rental should return true");
        assertEquals(Vehicle.VehicleStatus.Rented, testVehicle.getStatus(), 
            "Vehicle should be RENTED after rental");
        System.out.println("✓ First rental successful, status: " + testVehicle.getStatus());
        
        // Step 3: Second rental - should fail (already rented)
        boolean secondRentResult = rentalSystem.rentVehicle(testVehicle, testCustomer, LocalDate.now(), 50.0);
        assertFalse(secondRentResult, "Second rental should return false");
        assertEquals(Vehicle.VehicleStatus.Rented, testVehicle.getStatus(), 
            "Vehicle should remain RENTED");
        System.out.println("✓ Second rental correctly failed, status: " + testVehicle.getStatus());
        
        // Step 4: First return - should succeed
        boolean firstReturnResult = rentalSystem.returnVehicle(testVehicle, testCustomer, LocalDate.now(), 0.0);
        assertTrue(firstReturnResult, "First return should return true");
        assertEquals(Vehicle.VehicleStatus.Available, testVehicle.getStatus(), 
            "Vehicle should be AVAILABLE after return");
        System.out.println("✓ First return successful, status: " + testVehicle.getStatus());
        
        // Step 5: Second return - should fail (not rented)
        boolean secondReturnResult = rentalSystem.returnVehicle(testVehicle, testCustomer, LocalDate.now(), 0.0);
        assertFalse(secondReturnResult, "Second return should return false");
        assertEquals(Vehicle.VehicleStatus.Available, testVehicle.getStatus(), 
            "Vehicle should remain AVAILABLE");
        System.out.println("✓ Second return correctly failed, status: " + testVehicle.getStatus());
    }

    @Test
    public void testLicensePlate() {
        System.out.println("Testing license plate validation...");
        
        // Test 1: Valid plates should work without exceptions
        Car car1 = new Car("Toyota", "Corolla", 2020, 5);
        assertDoesNotThrow(() -> car1.setLicensePlate("AAA100"));
        assertEquals("AAA100", car1.getLicensePlate());
        System.out.println("✓ AAA100 - Valid plate accepted");
        
        Car car2 = new Car("Honda", "Civic", 2021, 5);
        assertDoesNotThrow(() -> car2.setLicensePlate("ABC567"));
        assertEquals("ABC567", car2.getLicensePlate());
        System.out.println("✓ ABC567 - Valid plate accepted");
        
        Car car3 = new Car("Ford", "Focus", 2022, 5);
        assertDoesNotThrow(() -> car3.setLicensePlate("ZZZ999"));
        assertEquals("ZZZ999", car3.getLicensePlate());
        System.out.println("✓ ZZZ999 - Valid plate accepted");
        
        // Test 2: Invalid plates should throw IllegalArgumentException
        Car car4 = new Car("Invalid", "Car", 2023, 5);
        
        // Test empty string
        assertThrows(IllegalArgumentException.class, () -> car4.setLicensePlate(""));
        System.out.println("✓ Empty string - Correctly rejected");
        
        // Test null
        assertThrows(IllegalArgumentException.class, () -> car4.setLicensePlate(null));
        System.out.println("✓ Null - Correctly rejected");
        
        // Test AAA1000 (too long)
        assertThrows(IllegalArgumentException.class, () -> car4.setLicensePlate("AAA1000"));
        System.out.println("✓ AAA1000 (too long) - Correctly rejected");
        
        // Test ZZZ99 (too short)
        assertThrows(IllegalArgumentException.class, () -> car4.setLicensePlate("ZZZ99"));
        System.out.println("✓ ZZZ99 (too short) - Correctly rejected");
        
        // Test that license plate remains null after failed attempts
        assertNull(car4.getLicensePlate(), "License plate should remain null after invalid attempts");
        System.out.println("✓ License plate correctly remains null after invalid attempts");
    }
}