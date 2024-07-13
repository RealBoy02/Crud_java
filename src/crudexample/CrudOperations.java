package crudexample;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.InputMismatchException;
import java.util.Scanner;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

public class CrudOperations {
    private Connection connection;
    private Scanner scanner;

    public CrudOperations(Connection connection) {
        this.connection = connection;
        this.scanner = new Scanner(System.in);
    }

    public void performCrudOperations() {
        while (true) {
            System.out.println("Choose an operation:");
            System.out.println("1. Create");
            System.out.println("2. Read");
            System.out.println("3. Update");
            System.out.println("4. Delete");
            System.out.println("5. Exit");
            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            try {
                switch (choice) {
                    case 1:
                        createUser();
                        break;
                    case 2:
                        readUsers();
                        break;
                    case 3:
                        updateUser();
                        break;
                    case 4:
                        deleteUser();
                        break;
                    case 5:
                        System.out.println("Exiting...");
                        return;
                    default:
                        System.out.println("Invalid choice. Please try again.");
                        break;
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private void createUser() throws SQLException {
        System.out.println("Enter email:");
        String email = scanner.nextLine();
        
        // Validate email format
        if (!isValidEmail(email)) {
            System.out.println("Invalid email format. Please enter a valid email address.");
            return;
        }

        System.out.println("Enter password:");
        String pass = scanner.nextLine();

        if (isEmailTaken(email)) {
            System.out.println("Email is already taken. Please choose another email.");
        } else {
            String insertSQL = "INSERT INTO users (email, pass) VALUES (?, ?)";
            try (PreparedStatement pstmt = connection.prepareStatement(insertSQL)) {
                pstmt.setString(1, email);
                pstmt.setString(2, pass);
                pstmt.executeUpdate();
                System.out.println("User added successfully.");
            }
        }
    }

    // Method to validate email
    private boolean isValidEmail(String email) {
        String emailRegex = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$";
        Pattern pattern = Pattern.compile(emailRegex);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    private boolean isEmailTaken(String email) throws SQLException {
        String checkSQL = "SELECT COUNT(*) FROM users WHERE email = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(checkSQL)) {
            pstmt.setString(1, email);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }
        return false;
    }

    private boolean isUserIdExists(int userId) throws SQLException {
        String checkSQL = "SELECT COUNT(*) FROM users WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(checkSQL)) {
            pstmt.setInt(1, userId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }
        return false;
    }

    private void readUsers() throws SQLException {
        String selectSQL = "SELECT * FROM users";
        try (PreparedStatement pstmt = connection.prepareStatement(selectSQL);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                System.out.println("ID: " + rs.getInt("id") + ", Email: " + rs.getString("email") + ", Pass: " + rs.getString("pass"));
            }
        }
    }

    private void updateUser() throws SQLException {
        while (true) {
            System.out.println("Enter ID of user to update (or type 'back' to return to the menu):");
            String input = scanner.nextLine();
            if (input.equalsIgnoreCase("back")) {
                return;
            }

            try {
                int userId = Integer.parseInt(input);

                if (isUserIdExists(userId)) {
                    System.out.println("Do you want to update email or pass? (Enter 'email' or 'pass'):");
                    String field = scanner.nextLine();

                    if ("email".equalsIgnoreCase(field)) {
                        System.out.println("Enter new email:");
                        String newEmail = scanner.nextLine();
                        
                        // Validate new email format
                        if (!isValidEmail(newEmail)) {
                            System.out.println("Invalid email format. Please enter a valid email address.");
                            return;
                        }

                        if (isEmailTaken(newEmail)) {
                            System.out.println("Email is already taken. Please choose another email.");
                        } else {
                            String updateSQL = "UPDATE users SET email = ? WHERE id = ?";
                            try (PreparedStatement pstmt = connection.prepareStatement(updateSQL)) {
                                pstmt.setString(1, newEmail);
                                pstmt.setInt(2, userId);
                                pstmt.executeUpdate();
                                System.out.println("User's email updated successfully.");
                            }
                        }
                    } else if ("pass".equalsIgnoreCase(field)) {
                        System.out.println("Enter new password:");
                        String newPass = scanner.nextLine();
                        String updateSQL = "UPDATE users SET pass = ? WHERE id = ?";
                        try (PreparedStatement pstmt = connection.prepareStatement(updateSQL)) {
                            pstmt.setString(1, newPass);
                            pstmt.setInt(2, userId);
                            pstmt.executeUpdate();
                            System.out.println("User's password updated successfully.");
                        }
                    } else {
                        System.out.println("Invalid field. Please try again.");
                    }
                    return; // Exit after successful update
                } else {
                    System.out.println("Invalid ID. Please enter a valid user ID.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a numeric user ID.");
            }
        }
    }

    private void deleteUser() throws SQLException {
        while (true) {
            System.out.println("Enter ID of user to delete (or type 'back' to return to the menu):");
            String input = scanner.nextLine();
            if (input.equalsIgnoreCase("back")) {
                return;
            }

            try {
                int userId = Integer.parseInt(input);

                if (isUserIdExists(userId)) {
                    String deleteSQL = "DELETE FROM users WHERE id = ?";
                    try (PreparedStatement pstmt = connection.prepareStatement(deleteSQL)) {
                        pstmt.setInt(1, userId);
                        int rowsAffected = pstmt.executeUpdate();
                        if (rowsAffected > 0) {
                            System.out.println("User deleted successfully.");
                        } else {
                            System.out.println("User not found.");
                        }
                    }
                    return; // Exit after successful deletion
                } else {
                    System.out.println("Invalid ID. Please enter a valid user ID.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a numeric user ID.");
            }
        }
    }
}