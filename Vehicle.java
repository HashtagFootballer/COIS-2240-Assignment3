public abstract class Vehicle {
    private String licensePlate;
    private String make;
    private String model;
    private int year;
    private VehicleStatus status;

    public enum VehicleStatus { Available, Held, Rented, UnderMaintenance, OutOfService }

    public Vehicle(String make, String model, int year) {
    	this.make = capitalize(make);//added for task 1.5
    	this.model = capitalize(model);//added for task 1.5
    	this.year = year;
        this.status = VehicleStatus.Available;
        this.licensePlate = null;
    }
//Edited as per task 1.5
	private String capitalize(String input) {//deletes use of repeated logic
		if (input == null || input.isEmpty())
    		return null;
    	else
    		return input.substring(0, 1).toUpperCase() + input.substring(1).toLowerCase();
	}

    public Vehicle() {
        this(null, null, 0);
    }

    public void setLicensePlate(String plate) {
        this.licensePlate = plate == null ? null : plate.toUpperCase();
    }

    public void setStatus(VehicleStatus status) {
    	this.status = status;
    }

    public String getLicensePlate() { return licensePlate; }

    public String getMake() { return make; }

    public String getModel() { return model;}

    public int getYear() { return year; }

    public VehicleStatus getStatus() { return status; }

    public String getInfo() {
        return "| " + licensePlate + " | " + make + " | " + model + " | " + year + " | " + status + " |";
    }

}
