package stockly;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;

public class Sidebar extends JPanel {
    private JFrame currentPage;

    public Sidebar() {
        setPreferredSize(new Dimension(250, getHeight()));
        setBackground(Color.WHITE);
        setLayout(new BorderLayout());

        JPanel logoPanel = createLogoPanel();
        JPanel navigationPanel = createNavigationPanel();
        JPanel logoutPanel = createLogoutPanel();

        add(logoPanel, BorderLayout.NORTH);
        add(navigationPanel, BorderLayout.CENTER);
        add(logoutPanel, BorderLayout.SOUTH);
    }

    private JPanel createLogoPanel() {
        JPanel logoPanel = new JPanel(new GridBagLayout());
        logoPanel.setBackground(Color.WHITE);
        logoPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
    
        // Mendefinisikan GridBagConstraints untuk label dan ikon
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(0, 0, 10, 0); // Memberikan ruang di bawah label
    
        ImageIcon logoIcon = new ImageIcon("assets/stockly.png");
        Image scaledLogoImage = logoIcon.getImage().getScaledInstance(40, 40, Image.SCALE_SMOOTH);
        ImageIcon scaledLogoIcon = new ImageIcon(scaledLogoImage);
        JLabel logoLabel = new JLabel(scaledLogoIcon);
    
        logoPanel.add(logoLabel, gbc); // Menambahkan logo ke panel
    
        gbc.gridy++; // Pindah ke baris berikutnya untuk label "Stockly"
        JLabel stocklyLabel = new JLabel("Stockly");
        stocklyLabel.setFont(new Font("Arial", Font.BOLD, 24));
        stocklyLabel.setForeground(new Color(0, 123, 255)); // Warna teks biru
        logoPanel.add(stocklyLabel, gbc); // Menambahkan label "Stockly" ke panel
    
        return logoPanel;
    }
    

    private JPanel createNavigationPanel() {
        JPanel navigationPanel = new JPanel(new GridBagLayout());
        navigationPanel.setBackground(Color.WHITE);
        navigationPanel.setBorder(new EmptyBorder(10, 10, 10, 10)); // Memberikan padding pada panel
    
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.gridy = 0;
    
        String[] buttonLabels = {"List Produk", "List Pembelian", "List Penjualan", "Laporan Pembelian", "Laporan Penjualan", "Kartu Stock"};
        ImageIcon[] buttonIcons = {
            new ImageIcon("assets/list_produk.png"),
            new ImageIcon("assets/pembelian.png"),
            new ImageIcon("assets/penjualan.png"),
            new ImageIcon("assets/laporan_jual.png"),
            new ImageIcon("assets/laporan_beli.png"),
            new ImageIcon("assets/stock.png")
        };
    
        ActionListener navigationListener = e -> {
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
        };
    
        for (int i = 0; i < buttonLabels.length; i++) {
            JButton button = new JButton(buttonLabels[i], buttonIcons[i]);
            button.setFont(new Font("Arial", Font.PLAIN, 14));
            button.setHorizontalAlignment(SwingConstants.LEFT);
            button.setIconTextGap(10);
            button.setBackground(Color.WHITE);
            button.setForeground(Color.BLACK);
            button.setBorder(new CompoundBorder(new LineBorder(Color.WHITE, 2), new EmptyBorder(10, 20, 10, 20)));
            button.setFocusPainted(false);
            button.addActionListener(navigationListener);
            addHoverEffect(button);
    
            // Menyesuaikan ukuran tombol secara dinamis berdasarkan teks label terpanjang
            Dimension buttonSize = button.getPreferredSize();
            buttonSize = new Dimension(200, buttonSize.height); // Menentukan lebar minimum tombol
    
            button.setPreferredSize(buttonSize);
            button.setMinimumSize(buttonSize);
            button.setMaximumSize(buttonSize);
    
            gbc.gridy = i;
            navigationPanel.add(button, gbc);
        }
    
        return navigationPanel;
    }
    

    private JPanel createLogoutPanel() {
        JPanel logoutPanel = new JPanel(new BorderLayout());
        logoutPanel.setBackground(Color.WHITE);
        logoutPanel.setBorder(new EmptyBorder(10, 0, 10, 0));

        ImageIcon logoutIcon = new ImageIcon("assets/logout.png");
        Image scaledLogoutImage = logoutIcon.getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH);
        ImageIcon scaledLogoutIcon = new ImageIcon(scaledLogoutImage);
        JButton logoutButton = new JButton("Logout", scaledLogoutIcon);
        logoutButton.setFont(new Font("Arial", Font.BOLD, 16));
        logoutButton.setBackground(Color.WHITE);
        logoutButton.setForeground(Color.BLACK);
        logoutButton.setBorder(new CompoundBorder(new LineBorder(Color.WHITE, 2), new EmptyBorder(10, 20, 10, 20)));
        logoutButton.setFocusPainted(false);
        logoutButton.addActionListener(e -> {
            for (Window window : Window.getWindows()) {
                window.dispose();
            }
            new LoginPage();
        });
        addHoverEffect(logoutButton);
        logoutPanel.add(logoutButton, BorderLayout.CENTER);

        return logoutPanel;
    }

    private void addHoverEffect(JButton button) {
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(new Color(0, 123, 255));
                button.setForeground(Color.WHITE);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(Color.WHITE);
                button.setForeground(Color.BLACK);
            }
        });
    }
}
