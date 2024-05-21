package stockly;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class LoginPage extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;

    public LoginPage() {
        setTitle("Stockly - Sign In");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(350, 250);
        setLocationRelativeTo(null); 

        JPanel panel = new JPanel();
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20)); 

        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        JLabel titleLabel = new JLabel("Sign In");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(titleLabel);
        panel.add(Box.createRigidArea(new Dimension(0, 20))); 

        JLabel usernameLabel = new JLabel("Username:");
        usernameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(usernameLabel);

        usernameField = new JTextField(15);
        usernameField.setMaximumSize(new Dimension(250, 30)); 
        usernameField.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(usernameField);

        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(passwordLabel);

        passwordField = new JPasswordField(15);
        passwordField.setMaximumSize(new Dimension(250, 30)); 
        passwordField.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(passwordField);

        panel.add(Box.createRigidArea(new Dimension(0, 20))); 

        JButton loginButton = new JButton("Login");
        loginButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        loginButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String username = usernameField.getText();
                String password = new String(passwordField.getPassword());

                // Convert password to MD5
                String passwordMD5 = md5(password);
                System.out.println(passwordMD5);
                // Get connection to the database
                Connection connection = Dbconnect.getConnect();
                if (connection != null) {
                    try {
                        // Query to check if the username and password match
                        String query = "SELECT * FROM login WHERE username = ? AND password = ?";
                        PreparedStatement statement = connection.prepareStatement(query);
                        statement.setString(1, username);
                        statement.setString(2, passwordMD5);
                        ResultSet resultSet = statement.executeQuery();

                        if (resultSet.next()) {
                            // If there is a match, login successful
                            JOptionPane.showMessageDialog(null, "Login successful!");
                            new StockPage();
                            dispose();
                        } else {
                            // If no match found, show error message
                            JOptionPane.showMessageDialog(null, "Invalid username or password. Please try again.");
                        }

                        // Close resources
                        resultSet.close();
                        statement.close();
                        connection.close();
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                        JOptionPane.showMessageDialog(null, "Error: Unable to execute query.");
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "Error: Unable to connect to database.");
                }
            }
        });
        panel.add(loginButton);

        add(panel);

        setVisible(true);
    }

    // Function to convert password to MD5
    private String md5(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] messageDigest = md.digest(password.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : messageDigest) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) {
        new LoginPage();
    }
}
