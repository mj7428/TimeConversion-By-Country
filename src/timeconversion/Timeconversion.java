package timeconversion;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.time.LocalDateTime;
import javax.swing.border.EmptyBorder;

public class Timeconversion extends JFrame implements ActionListener {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/Timeconversion";
    private static final String DB_USERNAME = "root";
    private static final String DB_PASSWORD = "1234";

    private JLabel currentTimeLabel;
    private JLabel convertedTimeLabel;
    private JComboBox<String> countryComboBox;
   

    public Timeconversion() {
        super("Time Conversion Clock");

        // Create and configure the GUI components
        currentTimeLabel = new JLabel();
        convertedTimeLabel = new JLabel();
        countryComboBox = new JComboBox<>();
        currentTimeLabel.setForeground(Color.BLUE);
        convertedTimeLabel.setForeground(Color.GREEN);
        convertedTimeLabel.setOpaque(true);
        convertedTimeLabel.setBackground(Color.BLACK);
        

        JButton convertButton = new JButton("Convert");
        convertButton.addActionListener(this);
        
        currentTimeLabel.setBorder(new EmptyBorder(0, 0, 0, 20));
        convertedTimeLabel.setBorder(new EmptyBorder(0, 0, 0, 20));

        // Create a JPanel for the "Converted Time" label and center align it
        

        // Set up the layout with GridBagLayout
        setLayout(new GridBagLayout());

        GridBagConstraints constraints = new GridBagConstraints();
        constraints.insets = new Insets(10, 10, 10, 10);

        constraints.gridx = 0;
        constraints.gridy = 0;
        add(new JLabel("Current Time:"), constraints);

        constraints.gridx = 1;
        add(currentTimeLabel, constraints);

        constraints.gridx = 0;
        constraints.gridy = 1;
        add(new JLabel("Select Country:"), constraints);

        constraints.gridx = 1;
        add(countryComboBox, constraints);

        constraints.gridx = 0;
        constraints.gridy = 2;
        constraints.gridwidth = 2;
        constraints.anchor = GridBagConstraints.CENTER;
        add(convertButton, constraints);

        constraints.gridx = 0;
        constraints.gridy = 3;
        constraints.anchor = GridBagConstraints.WEST;
        add(new JLabel("Converted Time:"), constraints);

        constraints.gridx = 1;
        add(convertedTimeLabel, constraints);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 200);
        setLocationRelativeTo(null);
        setVisible(true);

        // Populate the countryComboBox with data from the database
        populateCountryComboBox();

        // Update the current time label initially and every second
        Timer timer = new Timer(1000, e -> displayCurrentTime());
        timer.start();
    }

    private void populateCountryComboBox() {
        // Connect to the database and fetch country names
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD)) {
            String query = "SELECT name FROM countries";
            try (Statement statement = connection.createStatement();
                 ResultSet resultSet = statement.executeQuery(query)) {
                while (resultSet.next()) {
                    String countryName = resultSet.getString("name");
                    countryComboBox.addItem(countryName);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void displayCurrentTime() {
        // Get the current time and display it
        LocalDateTime currentTime = LocalDateTime.now();
        currentTimeLabel.setText(currentTime.toString());
    }

    private void convertTime() {
        // Get the selected country from the combobox
        String selectedCountry = (String) countryComboBox.getSelectedItem();

        // Connect to the database and fetch the offset for the selected country
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD)) {
            String query = "SELECT offset FROM countries WHERE name = ?";
            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setString(1, selectedCountry);
                try (ResultSet resultSet = statement.executeQuery()) {
                    if (resultSet.next()) {
                        int offset = resultSet.getInt("offset");

                        // Calculate the converted time
                        LocalDateTime currentTime = LocalDateTime.now();
                        LocalDateTime convertedTime = currentTime.plusHours(offset);

                        // Display the converted time
                        convertedTimeLabel.setText(convertedTime.toString());
                    } else {
                        convertedTimeLabel.setText("Country not found in the database.");
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getActionCommand().equals("Convert")) {
            convertTime();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Timeconversion::new);
    }
}


