import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import java.time.LocalDate;

public class RentalSystemGUI extends Application {
    private RentalSystem rentalSystem = RentalSystem.getInstance();
    private ObservableList<Vehicle> allVehicles = FXCollections.observableArrayList();
    private ObservableList<Customer> allCustomers = FXCollections.observableArrayList();
    private ObservableList<RentalRecord> rentalHistory = FXCollections.observableArrayList();

    // UI Components
    private TableView<Vehicle> vehicleTable = new TableView<>();
    private TableView<Customer> customerTable = new TableView<>();
    private TableView<RentalRecord> rentalTable = new TableView<>();

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Vehicle Rental System - JavaFX GUI");

        TabPane tabPane = new TabPane();
        tabPane.getTabs().addAll(
            createVehicleManagementTab(),
            createCustomerManagementTab(),
            createRentalManagementTab(),
            createDisplayTab()
        );

        Scene scene = new Scene(tabPane, 1000, 700);
        primaryStage.setScene(scene);
        primaryStage.show();

        refreshData();
    }

    private Tab createVehicleManagementTab() {
        Tab tab = new Tab("Vehicle Management");
        tab.setClosable(false);

        VBox mainLayout = new VBox(15);
        mainLayout.setPadding(new Insets(15));

        // Add Vehicle Form
        VBox addForm = new VBox(10);
        addForm.setStyle("-fx-border-color: #cccccc; -fx-border-width: 1; -fx-padding: 10;");
        
        Label formTitle = new Label("Add New Vehicle");
        formTitle.setStyle("-fx-font-weight: bold; -fx-font-size: 14;");

        GridPane formGrid = new GridPane();
        formGrid.setHgap(10);
        formGrid.setVgap(10);

        // Form fields
        ComboBox<String> typeCombo = new ComboBox<>();
        typeCombo.getItems().addAll("Car", "Minibus", "Pickup Truck");
        typeCombo.setPromptText("Select Vehicle Type");

        TextField makeField = new TextField();
        makeField.setPromptText("Make (e.g., Toyota)");

        TextField modelField = new TextField();
        modelField.setPromptText("Model (e.g., Corolla)");

        TextField yearField = new TextField();
        yearField.setPromptText("Year (e.g., 2023)");

        TextField licenseField = new TextField();
        licenseField.setPromptText("License Plate (e.g., ABC123)");

        TextField seatsField = new TextField();
        seatsField.setPromptText("Number of Seats (for Cars)");
        seatsField.setDisable(true);

        TextField cargoField = new TextField();
        cargoField.setPromptText("Cargo Size (for Pickup Trucks)");
        cargoField.setDisable(true);

        CheckBox accessibleCheck = new CheckBox("Accessible");
        accessibleCheck.setDisable(true);

        CheckBox trailerCheck = new CheckBox("Has Trailer");
        trailerCheck.setDisable(true);

        // Enable/disable fields based on vehicle type
        typeCombo.setOnAction(e -> {
            String type = typeCombo.getValue();
            seatsField.setDisable(type == null || !type.equals("Car"));
            cargoField.setDisable(type == null || !type.equals("Pickup Truck"));
            trailerCheck.setDisable(type == null || !type.equals("Pickup Truck"));
            accessibleCheck.setDisable(type == null || !type.equals("Minibus"));
        });

        // Add form fields to grid
        formGrid.add(new Label("Type:"), 0, 0);
        formGrid.add(typeCombo, 1, 0);
        formGrid.add(new Label("Make:"), 0, 1);
        formGrid.add(makeField, 1, 1);
        formGrid.add(new Label("Model:"), 0, 2);
        formGrid.add(modelField, 1, 2);
        formGrid.add(new Label("Year:"), 0, 3);
        formGrid.add(yearField, 1, 3);
        formGrid.add(new Label("License Plate:"), 0, 4);
        formGrid.add(licenseField, 1, 4);
        formGrid.add(new Label("Seats:"), 2, 0);
        formGrid.add(seatsField, 3, 0);
        formGrid.add(new Label("Cargo Size:"), 2, 1);
        formGrid.add(cargoField, 3, 1);
        formGrid.add(accessibleCheck, 2, 2);
        formGrid.add(trailerCheck, 3, 2);

        Button addButton = new Button("Add Vehicle");
        addButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");

        addButton.setOnAction(e -> {
            try {
                if (validateVehicleInput(typeCombo, makeField, modelField, yearField, licenseField)) {
                    addVehicle(typeCombo.getValue(), makeField.getText(), modelField.getText(),
                              yearField.getText(), licenseField.getText(), seatsField.getText(),
                              cargoField.getText(), accessibleCheck.isSelected(), trailerCheck.isSelected());
                    
                    // Clear form
                    typeCombo.setValue(null);
                    makeField.clear();
                    modelField.clear();
                    yearField.clear();
                    licenseField.clear();
                    seatsField.clear();
                    cargoField.clear();
                    accessibleCheck.setSelected(false);
                    trailerCheck.setSelected(false);
                    
                    refreshData();
                    showAlert(Alert.AlertType.INFORMATION, "Success", "Vehicle added successfully!");
                }
            } catch (Exception ex) {
                showAlert(Alert.AlertType.ERROR, "Error", ex.getMessage());
            }
        });

        addForm.getChildren().addAll(formTitle, formGrid, addButton);

        // Vehicle Table
        setupVehicleTable();
        VBox tableSection = new VBox(10);
        tableSection.getChildren().addAll(new Label("All Vehicles"), vehicleTable);

        mainLayout.getChildren().addAll(addForm, tableSection);
        VBox.setVgrow(vehicleTable, Priority.ALWAYS);
        tab.setContent(mainLayout);

        return tab;
    }

    private Tab createCustomerManagementTab() {
        Tab tab = new Tab("Customer Management");
        tab.setClosable(false);

        VBox mainLayout = new VBox(15);
        mainLayout.setPadding(new Insets(15));

        // Add Customer Form
        VBox addForm = new VBox(10);
        addForm.setStyle("-fx-border-color: #cccccc; -fx-border-width: 1; -fx-padding: 10;");

        Label formTitle = new Label("Add New Customer");
        formTitle.setStyle("-fx-font-weight: bold; -fx-font-size: 14;");

        HBox formRow = new HBox(10);
        TextField idField = new TextField();
        idField.setPromptText("Customer ID");

        TextField nameField = new TextField();
        nameField.setPromptText("Customer Name");

        Button addButton = new Button("Add Customer");
        addButton.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white;");

        addButton.setOnAction(e -> {
            try {
                if (idField.getText().isEmpty() || nameField.getText().isEmpty()) {
                    showAlert(Alert.AlertType.ERROR, "Error", "Please fill all fields");
                    return;
                }

                int customerId = Integer.parseInt(idField.getText());
                boolean success = rentalSystem.addCustomer(new Customer(customerId, nameField.getText()));

                if (success) {
                    idField.clear();
                    nameField.clear();
                    refreshData();
                    showAlert(Alert.AlertType.INFORMATION, "Success", "Customer added successfully!");
                } else {
                    showAlert(Alert.AlertType.ERROR, "Error", "Customer with this ID already exists");
                }
            } catch (NumberFormatException ex) {
                showAlert(Alert.AlertType.ERROR, "Error", "Please enter a valid customer ID (numbers only)");
            }
        });

        formRow.getChildren().addAll(new Label("ID:"), idField, new Label("Name:"), nameField, addButton);
        addForm.getChildren().addAll(formTitle, formRow);

        // Customer Table
        setupCustomerTable();
        VBox tableSection = new VBox(10);
        tableSection.getChildren().addAll(new Label("All Customers"), customerTable);

        mainLayout.getChildren().addAll(addForm, tableSection);
        VBox.setVgrow(customerTable, Priority.ALWAYS);
        tab.setContent(mainLayout);

        return tab;
    }

    private Tab createRentalManagementTab() {
        Tab tab = new Tab("Rental Management");
        tab.setClosable(false);

        VBox mainLayout = new VBox(15);
        mainLayout.setPadding(new Insets(15));

        // Rent Vehicle Section
        VBox rentSection = new VBox(10);
        rentSection.setStyle("-fx-border-color: #cccccc; -fx-border-width: 1; -fx-padding: 10;");

        Label rentTitle = new Label("Rent Vehicle");
        rentTitle.setStyle("-fx-font-weight: bold; -fx-font-size: 14;");

        HBox rentForm = new HBox(10);
        ComboBox<Vehicle> availableVehiclesCombo = new ComboBox<>();
        ComboBox<Customer> customersCombo = new ComboBox<>();
        TextField amountField = new TextField();
        amountField.setPromptText("Rental Amount");

        Button rentButton = new Button("Rent Vehicle");
        rentButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");

        rentForm.getChildren().addAll(
            new Label("Vehicle:"), availableVehiclesCombo,
            new Label("Customer:"), customersCombo,
            new Label("Amount:"), amountField, rentButton
        );

        rentButton.setOnAction(e -> {
            try {
                Vehicle vehicle = availableVehiclesCombo.getValue();
                Customer customer = customersCombo.getValue();
                double amount = Double.parseDouble(amountField.getText());

                if (vehicle == null || customer == null) {
                    showAlert(Alert.AlertType.ERROR, "Error", "Please select both vehicle and customer");
                    return;
                }

                rentalSystem.rentVehicle(vehicle, customer, LocalDate.now(), amount);
                refreshData();
                updateAvailableVehiclesCombo(availableVehiclesCombo);
                amountField.clear();
                showAlert(Alert.AlertType.INFORMATION, "Success", "Vehicle rented successfully!");
            } catch (NumberFormatException ex) {
                showAlert(Alert.AlertType.ERROR, "Error", "Please enter a valid rental amount");
            }
        });

        rentSection.getChildren().addAll(rentTitle, rentForm);

        // Return Vehicle Section
        VBox returnSection = new VBox(10);
        returnSection.setStyle("-fx-border-color: #cccccc; -fx-border-width: 1; -fx-padding: 10;");

        Label returnTitle = new Label("Return Vehicle");
        returnTitle.setStyle("-fx-font-weight: bold; -fx-font-size: 14;");

        HBox returnForm = new HBox(10);
        ComboBox<Vehicle> rentedVehiclesCombo = new ComboBox<>();
        ComboBox<Customer> returnCustomerCombo = new ComboBox<>();
        TextField feesField = new TextField();
        feesField.setPromptText("Additional Fees");

        Button returnButton = new Button("Return Vehicle");
        returnButton.setStyle("-fx-background-color: #FF9800; -fx-text-fill: white;");

        returnForm.getChildren().addAll(
            new Label("Vehicle:"), rentedVehiclesCombo,
            new Label("Customer:"), returnCustomerCombo,
            new Label("Fees:"), feesField, returnButton
        );

        returnButton.setOnAction(e -> {
            try {
                Vehicle vehicle = rentedVehiclesCombo.getValue();
                Customer customer = returnCustomerCombo.getValue();
                double fees = feesField.getText().isEmpty() ? 0.0 : Double.parseDouble(feesField.getText());

                if (vehicle == null || customer == null) {
                    showAlert(Alert.AlertType.ERROR, "Error", "Please select both vehicle and customer");
                    return;
                }

                rentalSystem.returnVehicle(vehicle, customer, LocalDate.now(), fees);
                refreshData();
                updateRentedVehiclesCombo(rentedVehiclesCombo);
                updateAvailableVehiclesCombo(availableVehiclesCombo);
                feesField.clear();
                showAlert(Alert.AlertType.INFORMATION, "Success", "Vehicle returned successfully!");
            } catch (NumberFormatException ex) {
                showAlert(Alert.AlertType.ERROR, "Error", "Please enter valid fees amount");
            }
        });

        returnSection.getChildren().addAll(returnTitle, returnForm);

        // Update combo boxes
        updateAvailableVehiclesCombo(availableVehiclesCombo);
        updateCustomerCombo(customersCombo);
        updateRentedVehiclesCombo(rentedVehiclesCombo);
        updateCustomerCombo(returnCustomerCombo);

        mainLayout.getChildren().addAll(rentSection, returnSection);
        tab.setContent(mainLayout);

        return tab;
    }

    private Tab createDisplayTab() {
        Tab tab = new Tab("View Data");
        tab.setClosable(false);

        TabPane displayTabs = new TabPane();

        // Available Vehicles Tab
        Tab availableTab = new Tab("Available Vehicles");
        TableView<Vehicle> availableTable = createAvailableVehicleTable();
        availableTable.setItems(getAvailableVehicles());
        availableTab.setContent(availableTable);

        // All Vehicles Tab
        Tab allVehiclesTab = new Tab("All Vehicles");
        setupVehicleTable();
        allVehiclesTab.setContent(vehicleTable);

        // Customers Tab
        Tab customersTab = new Tab("Customers");
        setupCustomerTable();
        customersTab.setContent(customerTable);

        // Rental History Tab
        Tab historyTab = new Tab("Rental History");
        setupRentalTable();
        historyTab.setContent(rentalTable);

        displayTabs.getTabs().addAll(availableTab, allVehiclesTab, customersTab, historyTab);
        tab.setContent(displayTabs);

        return tab;
    }

    // Helper Methods
    private boolean validateVehicleInput(ComboBox<String> type, TextField make, TextField model, 
                                       TextField year, TextField license) {
        if (type.getValue() == null || make.getText().isEmpty() || model.getText().isEmpty() ||
            year.getText().isEmpty() || license.getText().isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Error", "Please fill all required fields");
            return false;
        }

        try {
            Integer.parseInt(year.getText());
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Please enter a valid year");
            return false;
        }

        return true;
    }

    private void addVehicle(String type, String make, String model, String yearStr, 
                          String license, String seatsStr, String cargoStr, 
                          boolean isAccessible, boolean hasTrailer) {
        int year = Integer.parseInt(yearStr);
        Vehicle vehicle = null;

        try {
            switch (type) {
                case "Car":
                    int seats = seatsStr.isEmpty() ? 5 : Integer.parseInt(seatsStr);
                    vehicle = new Car(make, model, year, seats);
                    break;
                case "Minibus":
                    vehicle = new Minibus(make, model, year, isAccessible);
                    break;
                case "Pickup Truck":
                    double cargoSize = cargoStr.isEmpty() ? 1000.0 : Double.parseDouble(cargoStr);
                    vehicle = new PickupTruck(make, model, year, cargoSize, hasTrailer);
                    break;
            }

            if (vehicle != null) {
                vehicle.setLicensePlate(license);
                boolean success = rentalSystem.addVehicle(vehicle);
                if (!success) {
                    throw new IllegalArgumentException("Vehicle with this license plate already exists");
                }
            }
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            throw new IllegalArgumentException("Error creating vehicle: " + e.getMessage());
        }
    }

    private void setupVehicleTable() {
        vehicleTable.getColumns().clear();

        TableColumn<Vehicle, String> typeCol = new TableColumn<>("Type");
        typeCol.setCellValueFactory(cellData -> {
            Vehicle vehicle = cellData.getValue();
            if (vehicle instanceof Car) return new javafx.beans.property.SimpleStringProperty("Car");
            if (vehicle instanceof Minibus) return new javafx.beans.property.SimpleStringProperty("Minibus");
            if (vehicle instanceof PickupTruck) return new javafx.beans.property.SimpleStringProperty("Pickup Truck");
            return new javafx.beans.property.SimpleStringProperty("Unknown");
        });

        TableColumn<Vehicle, String> plateCol = new TableColumn<>("License Plate");
        plateCol.setCellValueFactory(new PropertyValueFactory<>("licensePlate"));

        TableColumn<Vehicle, String> makeCol = new TableColumn<>("Make");
        makeCol.setCellValueFactory(new PropertyValueFactory<>("make"));

        TableColumn<Vehicle, String> modelCol = new TableColumn<>("Model");
        modelCol.setCellValueFactory(new PropertyValueFactory<>("model"));

        TableColumn<Vehicle, Integer> yearCol = new TableColumn<>("Year");
        yearCol.setCellValueFactory(new PropertyValueFactory<>("year"));

        TableColumn<Vehicle, String> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));

        vehicleTable.getColumns().addAll(typeCol, plateCol, makeCol, modelCol, yearCol, statusCol);
        vehicleTable.setItems(allVehicles);
    }

    private void setupCustomerTable() {
        customerTable.getColumns().clear();

        TableColumn<Customer, Integer> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("customerId"));

        TableColumn<Customer, String> nameCol = new TableColumn<>("Name");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("customerName"));

        customerTable.getColumns().addAll(idCol, nameCol);
        customerTable.setItems(allCustomers);
    }

    private void setupRentalTable() {
        rentalTable.getColumns().clear();

        TableColumn<RentalRecord, String> typeCol = new TableColumn<>("Type");
        typeCol.setCellValueFactory(new PropertyValueFactory<>("recordType"));

        TableColumn<RentalRecord, String> plateCol = new TableColumn<>("License Plate");
        plateCol.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty(cellData.getValue().getVehicle().getLicensePlate()));

        TableColumn<RentalRecord, String> customerCol = new TableColumn<>("Customer");
        customerCol.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty(cellData.getValue().getCustomer().getCustomerName()));

        TableColumn<RentalRecord, String> dateCol = new TableColumn<>("Date");
        dateCol.setCellValueFactory(new PropertyValueFactory<>("recordDate"));

        TableColumn<RentalRecord, Double> amountCol = new TableColumn<>("Amount");
        amountCol.setCellValueFactory(new PropertyValueFactory<>("totalAmount"));

        rentalTable.getColumns().addAll(typeCol, plateCol, customerCol, dateCol, amountCol);
        rentalTable.setItems(rentalHistory);
    }

    private TableView<Vehicle> createAvailableVehicleTable() {
        TableView<Vehicle> table = new TableView<>();

        TableColumn<Vehicle, String> typeCol = new TableColumn<>("Type");
        typeCol.setCellValueFactory(cellData -> {
            Vehicle vehicle = cellData.getValue();
            if (vehicle instanceof Car) return new javafx.beans.property.SimpleStringProperty("Car");
            if (vehicle instanceof Minibus) return new javafx.beans.property.SimpleStringProperty("Minibus");
            if (vehicle instanceof PickupTruck) return new javafx.beans.property.SimpleStringProperty("Pickup Truck");
            return new javafx.beans.property.SimpleStringProperty("Unknown");
        });

        TableColumn<Vehicle, String> plateCol = new TableColumn<>("License Plate");
        plateCol.setCellValueFactory(new PropertyValueFactory<>("licensePlate"));

        TableColumn<Vehicle, String> makeCol = new TableColumn<>("Make");
        makeCol.setCellValueFactory(new PropertyValueFactory<>("make"));

        TableColumn<Vehicle, String> modelCol = new TableColumn<>("Model");
        modelCol.setCellValueFactory(new PropertyValueFactory<>("model"));

        table.getColumns().addAll(typeCol, plateCol, makeCol, modelCol);
        return table;
    }

    private ObservableList<Vehicle> getAvailableVehicles() {
        ObservableList<Vehicle> available = FXCollections.observableArrayList();
        for (Vehicle vehicle : allVehicles) {
            if (vehicle.getStatus() == Vehicle.VehicleStatus.Available) {
                available.add(vehicle);
            }
        }
        return available;
    }

    private void updateAvailableVehiclesCombo(ComboBox<Vehicle> combo) {
        ObservableList<Vehicle> available = getAvailableVehicles();
        combo.setItems(available);
    }

    private void updateRentedVehiclesCombo(ComboBox<Vehicle> combo) {
        ObservableList<Vehicle> rented = FXCollections.observableArrayList();
        for (Vehicle vehicle : allVehicles) {
            if (vehicle.getStatus() == Vehicle.VehicleStatus.Rented) {
                rented.add(vehicle);
            }
        }
        combo.setItems(rented);
    }

    private void updateCustomerCombo(ComboBox<Customer> combo) {
        combo.setItems(allCustomers);
    }

    private void refreshData() {
        // Refresh from RentalSystem (which loads from files)
        rentalSystem = RentalSystem.getInstance();
        
        // Update observable lists
        allVehicles.setAll(rentalSystem.findVehicleByPlate("")); // Get all vehicles
        allCustomers.clear(); 
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}