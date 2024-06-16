package stockly;

import javax.swing.*;
import java.awt.*;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class Sidebar extends JPanel {
    private JFrame currentPage;

    public Sidebar() {
        setPreferredSize(new Dimension(250, getHeight()));
        setBackground(Color.WHITE); // Set background to white

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        ImageIcon logoIcon = new ImageIcon("assets/stockly.png");
        Image logoImage = logoIcon.getImage();
        Image scaledLogoImage = logoImage.getScaledInstance(40, 40, Image.SCALE_SMOOTH);
        ImageIcon scaledLogoIcon = new ImageIcon(scaledLogoImage);
        JLabel logoLabel = new JLabel(scaledLogoIcon);
        JLabel stocklyLabel = new JLabel("Stockly");
        stocklyLabel.setFont(new Font("Arial", Font.BOLD, 24));
        stocklyLabel.setForeground(new Color(0, 123, 255)); // Blue text color
        JPanel logoPanel = new JPanel();
        logoPanel.setBackground(Color.WHITE); // White background
        logoPanel.setPreferredSize(new Dimension(250, 150));
        logoPanel.add(logoLabel);
        logoPanel.add(stocklyLabel);

        JButton[] navigationButtons = new JButton[6];
        String[] buttonLabels = {"List Produk", "List Pembelian", "List Penjualan", "Laporan Pembelian", "Laporan Penjualan", "Kartu Stock"};
        ImageIcon[] buttonIcons = {new ImageIcon("assets/list_produk.png"), new ImageIcon("assets/pembelian.png"), new ImageIcon("assets/penjualan.png"), new ImageIcon("assets/laporan_jual.png"), new ImageIcon("assets/laporan_beli.png"), new ImageIcon("assets/stock.png")};

        JPanel navigationPanel = new JPanel();
        navigationPanel.setBackground(Color.WHITE);
        navigationPanel.setLayout(new GridLayout(6, 1));
        navigationPanel.setPreferredSize(new Dimension(250, 300));

        ActionListener navigationListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JButton button = (JButton) e.getSource();
                String buttonText = button.getText();
                switch (buttonText) {
                    case "List Produk":
                        new StockPage().setVisible(true);
                        break;
                    case "List Pembelian":
                        new PurchaseListPage().setVisible(true);
                        break;
                    case "List Penjualan":
                        new SalesListPage().setVisible(true);
                        break;
                    case "Laporan Pembelian":
                        new PurchaseReportFrame().setVisible(true);
                        break;
                    case "Laporan Penjualan":
                        new SalesReportFrame().setVisible(true);
                        break;
                    case "Kartu Stock":
                        new StockCardFrame().setVisible(true);
                        break;
                }
                
                SwingUtilities.getWindowAncestor(Sidebar.this).dispose();
            }
        };

        for (int i = 0; i < 6; i++) {
            navigationButtons[i] = new JButton(buttonLabels[i], buttonIcons[i]);
            navigationButtons[i].setFont(new Font("Arial", Font.PLAIN, 14));
            navigationButtons[i].setHorizontalAlignment(SwingConstants.LEFT);
            navigationButtons[i].setIconTextGap(10);
            navigationButtons[i].setBackground(Color.WHITE); // White button background
            navigationButtons[i].setForeground(Color.BLACK); // Black text color
            navigationButtons[i].setBorder(createRoundedBorder()); // Rounded border with white grid
            navigationButtons[i].setFocusPainted(false); // Remove focus border
            navigationButtons[i].addActionListener(navigationListener);
            addHoverEffect(navigationButtons[i]); // Add hover effect
            navigationPanel.add(navigationButtons[i]);
        }

        ImageIcon logoutIcon = new ImageIcon("assets/logout.png");
        Image scaledLogoutImage = logoutIcon.getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH);
        ImageIcon scaledLogoutIcon = new ImageIcon(scaledLogoutImage);
        JButton logoutButton = new JButton("Logout", scaledLogoutIcon);
        logoutButton.setFont(new Font("Arial", Font.BOLD, 16));
        logoutButton.setBackground(Color.WHITE); // White button background
        logoutButton.setForeground(Color.BLACK); // Black text color
        logoutButton.setBorder(createRoundedBorder()); // Rounded border with white grid
        logoutButton.setFocusPainted(false); // Remove focus border
        logoutButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                for (Window window : Window.getWindows()) {
                    window.dispose();
                }
                new LoginPage();
            }
        });
        addHoverEffect(logoutButton); // Add hover effect

        JPanel logoutPanel = new JPanel();
        logoutPanel.setBackground(Color.WHITE);
        logoutPanel.setPreferredSize(new Dimension(250, 50));
        logoutPanel.add(logoutButton);

        add(logoPanel);
        add(navigationPanel);
        add(logoutPanel);
    }

    private Border createRoundedBorder() {
        return new CompoundBorder(
            new LineBorder(Color.WHITE, 2), // White grid border
            new EmptyBorder(10, 20, 10, 20) // Padding
        );
    }

    private void addHoverEffect(JButton button) {
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(new Color(0, 123, 255)); // Blue background on hover
                button.setForeground(Color.WHITE); // White text on hover
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(Color.WHITE); // White background on exit
                button.setForeground(Color.BLACK); // Black text on exit
            }
        });
    }

    private static class RoundedBorder implements Border {
        private int radius;

        RoundedBorder(int radius) {
            this.radius = radius;
        }

        public Insets getBorderInsets(Component c) {
            return new Insets(this.radius + 1, this.radius + 1, this.radius + 1, this.radius + 1);
        }

        public boolean isBorderOpaque() {
            return true;
        }

        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            g.setColor(c.getBackground());
            g.drawRoundRect(x, y, width - 1, height - 1, radius, radius);
        }
    }
}
