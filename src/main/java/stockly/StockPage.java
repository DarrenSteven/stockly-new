package stockly;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

class PlaceholderTextField extends JTextField {
    private String placeholder;

    public PlaceholderTextField(String placeholder) {
        this.placeholder = placeholder;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (getText().isEmpty()) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setColor(Color.GRAY);
            g2.setFont(getFont().deriveFont(Font.ITALIC));
            int x = getInsets().left;
            int y = (getHeight() - g2.getFontMetrics().getHeight()) / 2 + g2.getFontMetrics().getAscent();
            g2.drawString(placeholder, x, y);
            g2.dispose();
        }
    }
}

public class StockPage extends JFrame {
    private DefaultTableModel tableModel;
    private JTable table;
    private List<String> productNames;

    public StockPage() {
        setTitle("Stockly - Stock Barang");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1300, 850);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        Sidebar sidebar = new Sidebar();
        mainPanel.add(sidebar, BorderLayout.WEST);

        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        JLabel titleLabel = new JLabel("List Produk");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));

        PlaceholderTextField searchBar = new PlaceholderTextField("Search...");
        searchBar.setFont(new Font("Arial", Font.PLAIN, 14));
        searchBar.setPreferredSize(new Dimension(searchBar.getPreferredSize().width, 40));
        searchBar.setBorder(BorderFactory.createCompoundBorder(
                searchBar.getBorder(),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)));

        JPanel titleSearchPanel = new JPanel(new BorderLayout());
        titleSearchPanel.add(titleLabel, BorderLayout.NORTH);
        titleSearchPanel.add(searchBar, BorderLayout.SOUTH);
        contentPanel.add(titleSearchPanel, BorderLayout.NORTH);

        String[] columnNames = {"Kode", "Nama Barang", "Jumlah", "Satuan", "Harga Pembelian", "Harga Penjualan"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;  // Make all cells not editable
            }
        };

        table = new JTable(tableModel) {
            @Override
            public int getRowHeight(int row) {
                return 40;
            }
        };

        // Set alignment to center for all columns
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(DefaultTableCellRenderer.CENTER);
        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

        JScrollPane scrollPane = new JScrollPane(table);
        contentPanel.add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        
        JButton editButton = new JButton("Edit");
        editButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showProductSelectionDialog("Edit");
            }
        });
        buttonPanel.add(editButton);

        JButton deleteButton = new JButton("Hapus");
        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showProductSelectionDialog("Delete");
            }
        });
        buttonPanel.add(deleteButton);

        JButton addButton = new JButton("Tambah");
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new AddProductFrame(StockPage.this);  // Pass reference of StockPage
            }
        });
        buttonPanel.add(addButton);

        contentPanel.add(buttonPanel, BorderLayout.SOUTH);

        mainPanel.add(contentPanel, BorderLayout.CENTER);

        add(mainPanel);

        loadDataFromDatabase();

        setVisible(true);
    }

    public void refreshTable() {
        tableModel.setRowCount(0);  // Clear existing data
        loadDataFromDatabase();     // Load new data
    }

    private void loadDataFromDatabase() {
        productNames = new ArrayList<>();
        try (Connection conn = Dbconnect.getConnect();
             PreparedStatement pstmt = conn.prepareStatement("SELECT kode, nama, stock, satuan, harga_beli, harga_jual FROM list_produk");
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                Object[] row = {
                    rs.getString("kode"),
                    rs.getString("nama"),
                    rs.getInt("stock"),
                    rs.getString("satuan"),
                    rs.getDouble("harga_beli"),
                    rs.getDouble("harga_jual")
                };
                tableModel.addRow(row);
                productNames.add(rs.getString("nama"));
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Terjadi kesalahan pada database: " + ex.getMessage());
        }
    }

    private void showProductSelectionDialog(String action) {
        String[] productArray = productNames.toArray(new String[0]);
        String selectedProduct = (String) JOptionPane.showInputDialog(this,
                "Pilih produk yang ingin " + action.toLowerCase() + ":",
                action + " Produk",
                JOptionPane.PLAIN_MESSAGE,
                null,
                productArray,
                productArray[0]);

        if (selectedProduct != null) {
            int row = findRowByProductName(selectedProduct);
            if (action.equals("Edit")) {
                editProduct(row);
            } else if (action.equals("Delete")) {
                deleteProduct(row);
            }
        }
    }

    private int findRowByProductName(String productName) {
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            if (tableModel.getValueAt(i, 1).equals(productName)) {
                return i;
            }
        }
        return -1;  // Not found
    }

    private void editProduct(int row) {
        String productCode = tableModel.getValueAt(row, 0).toString();
        String name = tableModel.getValueAt(row, 1).toString();
        int quantity = Integer.parseInt(tableModel.getValueAt(row, 2).toString());
        String unit = tableModel.getValueAt(row, 3).toString();
        double purchasePrice = Double.parseDouble(tableModel.getValueAt(row, 4).toString());
        double sellingPrice = Double.parseDouble(tableModel.getValueAt(row, 5).toString());

        new EditProductFrame(this, productCode, name, quantity, unit, purchasePrice, sellingPrice);
    }

    private void deleteProduct(int row) {
        String productCode = tableModel.getValueAt(row, 0).toString();
        String productName = tableModel.getValueAt(row, 1).toString();
        String productId = getProductIdByCode(productCode);

        if (hasTransactions(productId)) {
            JOptionPane.showMessageDialog(this, "Produk tidak dapat dihapus karena memiliki transaksi terkait.");
            return;
        }

        int confirmed = JOptionPane.showConfirmDialog(this, "Apakah Anda yakin ingin menghapus produk ini?", "Konfirmasi Hapus", JOptionPane.YES_NO_OPTION);
        if (confirmed == JOptionPane.YES_OPTION) {
            try (Connection conn = Dbconnect.getConnect();
                 PreparedStatement pstmt = conn.prepareStatement("DELETE FROM list_produk WHERE kode = ?")) {
                pstmt.setString(1, productCode);
                int rowsAffected = pstmt.executeUpdate();
                if (rowsAffected > 0) {
                    JOptionPane.showMessageDialog(this, "Produk berhasil dihapus!");
                    refreshTable();
                } else {
                    JOptionPane.showMessageDialog(this, "Gagal menghapus produk.");
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Terjadi kesalahan pada database: " + ex.getMessage());
            }
        }
    }

    private boolean hasTransactions(String productId) {
        try (Connection conn = Dbconnect.getConnect()) {
            // Check if there are transactions in detail_penjualan
            try (PreparedStatement pstmt = conn.prepareStatement("SELECT COUNT(*) FROM detail_penjualan WHERE id_produk = ?")) {
                pstmt.setString(1, productId);
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next() && rs.getInt(1) > 0) {
                        return true;
                    }
                }
            }

            // Check if there are transactions in detail_pembelian
            try (PreparedStatement pstmt = conn.prepareStatement("SELECT COUNT(*) FROM detail_pembelian WHERE id_produk = ?")) {
                pstmt.setString(1, productId);
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next() && rs.getInt(1) > 0) {
                        return true;
                    }
                }
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Terjadi kesalahan pada database: " + ex.getMessage());
        }
        return false;
    }

    private String getProductIdByCode(String productCode) {
        try (Connection conn = Dbconnect.getConnect();
             PreparedStatement pstmt = conn.prepareStatement("SELECT id_list_produk FROM list_produk WHERE kode = ?")) {
            pstmt.setString(1, productCode);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("id_list_produk");
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Terjadi kesalahan pada database: " + ex.getMessage());
        }
        return null;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new StockPage());
    }
}
