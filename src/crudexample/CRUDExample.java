package crudexample;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.SQLException;

public class CRUDExample {
    public static void main(String[] args) {
        JFrame frame = new JFrame("Database Login");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(300, 200);
        frame.setLayout(new GridLayout(4, 2));

        JLabel dbLabel = new JLabel("Database Type:");
        JComboBox<String> dbComboBox = new JComboBox<>(new String[]{"MySQL", "PostgreSQL"});
        JLabel userLabel = new JLabel("Username:");
        JTextField userField = new JTextField();
        JLabel passLabel = new JLabel("Password:");
        JPasswordField passField = new JPasswordField();
        JButton loginButton = new JButton("Login");

        frame.add(dbLabel);
        frame.add(dbComboBox);
        frame.add(userLabel);
        frame.add(userField);
        frame.add(passLabel);
        frame.add(passField);
        frame.add(new JLabel()); // Empty placeholder
        frame.add(loginButton);

        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String dbType = dbComboBox.getSelectedItem().toString().toLowerCase();
                String username = userField.getText();
                String password = new String(passField.getPassword());

                try {
                    Connection conn = null;
                    if ("mysql".equalsIgnoreCase(dbType)) {
                        conn = DatabaseConnection.getMySQLConnection(username, password);
                    } else if ("postgresql".equalsIgnoreCase(dbType)) {
                        conn = DatabaseConnection.getPostgreSQLConnection(username, password);
                    } else {
                        JOptionPane.showMessageDialog(frame, "Invalid database type.");
                        return;
                    }

                    CrudOperations crudOperations = new CrudOperations(conn);
                    crudOperations.performCrudOperations();
                    JOptionPane.showMessageDialog(frame, "CRUD operations performed successfully!");

                } catch (SQLException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(frame, "Error: " + ex.getMessage());
                }
            }
        });

        frame.setVisible(true);
    }
}
