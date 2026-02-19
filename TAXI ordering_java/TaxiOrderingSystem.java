/******************************************************************************
 *                            Taxi Ordering System                            *
 * ----------------------------------------------------------------------------
 * Team Members:                                                              *
 * 1. Goitom Shumey        | ID: DBU1501638                                   *
 * 2. Muaz Muhammed        | ID: DBU1501382                                   *
 * 3. Kidus Misaw          | ID: DBU1501305                                   *
 * 4. Nahusenay Simegn     | ID: DBU1501390                                   *
 * ----------------------------------------------------------------------------
 * Project Description:                                                       *
 * This project is a Taxi Ordering System that allows admin to add taxis,     *
 * assign passengers to taxis, and manage taxi departures. The system also    *
 * interacts with a text file handling to store taxi and passenger information*
 * ----------------------------------------------------------------------------
 * ----------------------------------------------------------------------------
 *                                       submission Date: February 22 2025 G.C *
 *****************************************************************************/
import javax.swing.*;
import java.awt.*;
// import java.awt.event.*;
import java.io.*;
import java.util.*;

class Taxi {
    private static int idCounter = 1;
    private int id;
    private String licensePlate;
    private String destination;
    private final int capacity = 16;
    private int passengersServed = 0;

    public Taxi(String licensePlate, String destination) {
        this.id = idCounter++;
        this.licensePlate = licensePlate;
        this.destination = destination;
    }

    public int getId() { return id; }
    public String getLicensePlate() { return licensePlate; }
    public String getDestination() { return destination; }
    public int getCapacity() { return capacity; }
    public int getPassengersServed() { return passengersServed; }
    public void servePassenger() { passengersServed++; }

    // Save taxi information to file
    public static void saveTaxiToFile(Queue<Taxi> taxiQueue) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("Taxi.txt"))) {
            for (Taxi taxi : taxiQueue) {
                writer.write(taxi.getLicensePlate() + "," + taxi.getDestination() + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Load taxi information from file
    public static Queue<Taxi> loadTaxisFromFile() {
        Queue<Taxi> taxis = new LinkedList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader("Taxi.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                String licensePlate = parts[0];
                String destination = parts[1];
                taxis.offer(new Taxi(licensePlate, destination));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return taxis;
    }
}

class Passenger {
    private String name;
    private String destination;

    public Passenger(String name, String destination) {
        this.name = name;
        this.destination = destination;
    }

    public String getDestination() { return destination; }
    public String toString() { return name + " (Destination: " + destination + ")"; }

    // Save passenger information to file
    public static void savePassengerToFile(Queue<Passenger> passengerQueue) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("Passenger.txt"))) {
            for (Passenger passenger : passengerQueue) {
                writer.write(passenger.name + "," + passenger.destination + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Load passenger information from file
    public static Queue<Passenger> loadPassengersFromFile() {
        Queue<Passenger> passengers = new LinkedList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader("Passenger.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                String name = parts[0];
                String destination = parts[1];
                passengers.offer(new Passenger(name, destination));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return passengers;
    }
}

class TaxiOrderingSystemGUI {
    private Queue<Taxi> taxiQueue;
    private Queue<Passenger> passengerQueue;
    private JFrame frame;
    private JTextArea displayArea;
    private JButton addTaxiButton, addPassengerButton, manageButton, showQueuesButton;

    // Hardcoded credentials for authentication
    private static final String USERNAME = "admin";
    private static final String PASSWORD = "password123";
    private static final int MAX_ATTEMPTS = 3; // Maximum login attempts

    public TaxiOrderingSystemGUI() {
        taxiQueue = Taxi.loadTaxisFromFile();  // Load taxis from file
        passengerQueue = Passenger.loadPassengersFromFile();  // Load passengers from file

        frame = new JFrame("🚖 Taxi Ordering System");
        frame.setSize(500, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        displayArea = new JTextArea(10, 40);
        displayArea.setEditable(false);
        displayArea.setFont(new Font("Arial", Font.PLAIN, 14));
        displayArea.setBackground(Color.LIGHT_GRAY);
        frame.add(new JScrollPane(displayArea), BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(3, 2, 10, 10));

        addTaxiButton = createButton("🚕 Add Taxi", new Color(70, 130, 180));
        addTaxiButton.addActionListener(e -> addTaxi());
        buttonPanel.add(addTaxiButton);

        addPassengerButton = createButton("🧑‍✈️ Add Passenger", new Color(50, 205, 50));
        addPassengerButton.addActionListener(e -> addPassenger());
        buttonPanel.add(addPassengerButton);

        manageButton = createButton("📋 Manage Taxis", new Color(200, 200, 200));
        manageButton.setEnabled(false);
        manageButton.addActionListener(e -> manageTaxiDeparture());
        buttonPanel.add(manageButton);

        showQueuesButton = createButton("📊 Show Queues", new Color(200, 200, 200));
        showQueuesButton.setEnabled(false);
        showQueuesButton.addActionListener(e -> showQueues());
        buttonPanel.add(showQueuesButton);

        frame.add(buttonPanel, BorderLayout.SOUTH);
        frame.setVisible(true);

        if (!authenticate()) {
            JOptionPane.showMessageDialog(frame, "Invalid login credentials. Exiting application.", "Authentication Failed", JOptionPane.ERROR_MESSAGE);
            System.exit(0); // Exit the program if authentication fails
        }
    }

    private JButton createButton(String text, Color color) {
        JButton button = new JButton(text);
        button.setBackground(color);
        button.setFocusPainted(false);
        button.setFont(new Font("Arial", Font.BOLD, 12));
        button.setOpaque(true);
        return button;
    }

    // Authentication method with a maximum of 3 attempts
    private boolean authenticate() {
        int attemptCount = 0;
        while (attemptCount < MAX_ATTEMPTS) {
            JTextField usernameField = new JTextField(10);
            JPasswordField passwordField = new JPasswordField(10);

            JPanel panel = new JPanel(new GridLayout(2, 2, 10, 10));
            panel.add(new JLabel("Username:"));
            panel.add(usernameField);
            panel.add(new JLabel("Password:"));
            panel.add(passwordField);

            int option = JOptionPane.showConfirmDialog(frame, panel, "Login", JOptionPane.OK_CANCEL_OPTION);
            if (option == JOptionPane.OK_OPTION) {
                String username = usernameField.getText().trim();
                String password = new String(passwordField.getPassword()).trim();

                // Validate credentials
                if (USERNAME.equals(username) && PASSWORD.equals(password)) {
                    return true; // Successful authentication
                }
            }

            attemptCount++;
            if (attemptCount < MAX_ATTEMPTS) {
                JOptionPane.showMessageDialog(frame, "Invalid credentials. Attempt " + attemptCount + " of " + MAX_ATTEMPTS, "Authentication Failed", JOptionPane.WARNING_MESSAGE);
            }
        }

        return false; // Authentication failed after 3 attempts
    }

    private void addTaxi() {
        JTextField licenseField = new JTextField(10);
        JTextField destinationField = new JTextField(10);

        JPanel panel = new JPanel(new GridLayout(2, 2, 10, 10));
        panel.add(new JLabel("🚖 License Plate:"));
        panel.add(licenseField);
        panel.add(new JLabel("📍 Destination:"));
        panel.add(destinationField);

        int option = JOptionPane.showConfirmDialog(frame, panel, "Enter Taxi Details", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            String licensePlate = licenseField.getText().trim();
            String destination = destinationField.getText().trim();

            if (!licensePlate.isEmpty() && !destination.isEmpty()) {
                taxiQueue.offer(new Taxi(licensePlate, destination));
                Taxi.saveTaxiToFile(taxiQueue);  // Save taxis to file
                displayArea.setText("✅ Added Taxi " + licensePlate + " to queue.");
                manageButton.setEnabled(true);
                showQueuesButton.setEnabled(true);
            }
        }
    }

    private void addPassenger() {
        JTextField nameField = new JTextField(10);
        JTextField destinationField = new JTextField(10);

        JPanel panel = new JPanel(new GridLayout(2, 2, 10, 10));
        panel.add(new JLabel("🧑 Name:"));
        panel.add(nameField);
        panel.add(new JLabel("📍 Destination:"));
        panel.add(destinationField);

        int option = JOptionPane.showConfirmDialog(frame, panel, "Enter Passenger Details", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            String name = nameField.getText().trim();
            String destination = destinationField.getText().trim();

            if (!name.isEmpty() && !destination.isEmpty()) {
                passengerQueue.offer(new Passenger(name, destination));
                Passenger.savePassengerToFile(passengerQueue);  // Save passengers to file
                displayArea.setText("✅ Added Passenger " + name + " to queue.");
                manageButton.setEnabled(true);
            }
        }
    }

    private void manageTaxiDeparture() {
        if (taxiQueue.isEmpty()) {
            displayArea.setText("⚠️ No taxis available.");
            return;
        }

        Taxi taxi = taxiQueue.poll();
        int served = 0;
        Queue<Passenger> tempQueue = new LinkedList<>();
        StringBuilder assignedPassengers = new StringBuilder();

        for (Passenger passenger : passengerQueue) {
            if (passenger.getDestination().equalsIgnoreCase(taxi.getDestination()) && served < taxi.getCapacity()) {
                assignedPassengers.append("- ").append(passenger).append("\n");
                served++;
            } else {
                tempQueue.offer(passenger);
            }
        }
        passengerQueue = tempQueue;

        if (served == 0) {
            displayArea.setText("🚖 No passengers for Taxi " + taxi.getLicensePlate() + " (Destination: " + taxi.getDestination() + ")");
            return;
        }

        int choice = JOptionPane.showConfirmDialog(frame, "🚖 Taxi " + taxi.getLicensePlate() + " is ready to depart with:\n" + assignedPassengers.toString() + "\nProceed?", "Confirm Departure", JOptionPane.YES_NO_OPTION);
        if (choice == JOptionPane.YES_OPTION) {
            displayArea.setText("🚖 Taxi " + taxi.getLicensePlate() + " departed with " + served + " passengers.");
            Taxi.saveTaxiToFile(taxiQueue);  // Save taxis to file after departure
            Passenger.savePassengerToFile(passengerQueue);  // Save remaining passengers to file
        } else {
            taxiQueue.offer(taxi); // Return taxi to the queue if user decides to wait
        }
    }

    private void showQueues() {
        StringBuilder sb = new StringBuilder();
        sb.append("🚖 Taxis in Queue:\n");
        for (Taxi taxi : taxiQueue) {
            sb.append("Taxi " + taxi.getLicensePlate() + " (Destination: " + taxi.getDestination() + ")\n");
        }
        sb.append("\n🧑‍✈️ Passengers in Queue:\n");
        for (Passenger passenger : passengerQueue) {
            sb.append(passenger.toString() + "\n");
        }

        displayArea.setText(sb.toString());
    }

    public static void main(String[] args) {
        new TaxiOrderingSystemGUI();
    }
}
