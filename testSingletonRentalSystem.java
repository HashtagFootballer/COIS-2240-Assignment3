import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;
import java.time.LocalDate;
import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;

public class testSingletonRentalSystem {
    
    private RentalSystem rentalSystem;
    private Car testVehicle;
    private Customer testCustomer;

    @BeforeEach
    public void setUp() {
        rentalSystem = RentalSystem.getInstance();
        
        // Create test vehicle with valid license plate
        testVehicle = new Car("Toyota", "Camry", 2022, 5);
        testVehicle.setLicensePlate("ABC123");
        rentalSystem.addVehicle(testVehicle);
        
        // Create test customer
        testCustomer = new Customer(100, "Test Customer");
        rentalSystem.addCustomer(testCustomer);
    }

    @Test
    public void testLicensePlate() {
        // Test valid plates
        Car car1 = new Car("Toyota", "Corolla", 2020, 5);
        assertDoesNotThrow(() -> car1.setLicensePlate("AAA100"));
        assertEquals("AAA100", car1.getLicensePlate());
        
        Car car2 = new Car("Honda", "Civic", 2021, 5);
        assertDoesNotThrow(() -> car2.setLicensePlate("ABC567"));
        assertEquals("ABC567", car2.getLicensePlate());
        
        Car car3 = new Car("Ford", "Focus", 2022, 5);
        assertDoesNotThrow(() -> car3.setLicensePlate("ZZZ999"));
        assertEquals("ZZZ999", car3.getLicensePlate());
        
        // Test invalid plates
        Car car4 = new Car("Invalid", "Car", 2023, 5);
        assertThrows(IllegalArgumentException.class, () -> car4.setLicensePlate(""));
        assertThrows(IllegalArgumentException.class, () -> car4.setLicensePlate(null));
        assertThrows(IllegalArgumentException.class, () -> car4.setLicensePlate("AAA1000"));
        assertThrows(IllegalArgumentException.class, () -> car4.setLicensePlate("ZZZ99"));
    }

    @Test
    public void testRentAndReturnVehicle() {
        // Ensure vehicle is initially available
        assertEquals(Vehicle.VehicleStatus.Available, testVehicle.getStatus());
        
        // First rental - should succeed
        boolean firstRentResult = rentalSystem.rentVehicle(testVehicle, testCustomer, LocalDate.now(), 50.0);
        assertTrue(firstRentResult);
        assertEquals(Vehicle.VehicleStatus.Rented, testVehicle.getStatus());
        
        // Second rental - should fail
        boolean secondRentResult = rentalSystem.rentVehicle(testVehicle, testCustomer, LocalDate.now(), 50.0);
        assertFalse(secondRentResult);
        assertEquals(Vehicle.VehicleStatus.Rented, testVehicle.getStatus());
        
        // First return - should succeed
        boolean firstReturnResult = rentalSystem.returnVehicle(testVehicle, testCustomer, LocalDate.now(), 0.0);
        assertTrue(firstReturnResult);
        assertEquals(Vehicle.VehicleStatus.Available, testVehicle.getStatus());
        
        // Second return - should fail
        boolean secondReturnResult = rentalSystem.returnVehicle(testVehicle, testCustomer, LocalDate.now(), 0.0);
        assertFalse(secondReturnResult);
        assertEquals(Vehicle.VehicleStatus.Available, testVehicle.getStatus());
    }

    @Test
    public void testSingletonRentalSystem() throws Exception {
        // Use Constructor<RentalSystem> constructor = RentalSystem.class.getDeclaredConstructor();
        Constructor<RentalSystem> constructor = RentalSystem.class.getDeclaredConstructor();
        
        // Use the getModifiers method to get the constructor's modifiers
        int modifiers = constructor.getModifiers();
        
        // Use proper assertion to validate if the returned value equals Modifier.PRIVATE
        assertEquals(Modifier.PRIVATE, modifiers, 
            "RentalSystem constructor should be private to enforce Singleton pattern");
        
        // Use RentalSystem.getInstance() to obtain an instance and assert it is not null
        RentalSystem instance = RentalSystem.getInstance();
        assertNotNull(instance, "getInstance() should return a non-null instance");
        
        // Additional Singleton validation: multiple calls should return same instance
        RentalSystem instance2 = RentalSystem.getInstance();
        assertSame(instance, instance2, 
            "Multiple calls to getInstance() should return the same instance");
    }
}