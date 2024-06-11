package stockly;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.border.EmptyBorder;
import java.sql.*;

public class SalesListPage extends JFrame {
    private DefaultTableModel model;
    private JComboBox<String> deleteComboBox;
    private JTable table;

    public SalesListPage() {
        setTitle("Stockly - List Penjualan");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1300, 850);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        Sidebar sidebar = new Sidebar();
        mainPanel.add(sidebar, BorderLayout.WEST);

        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        JLabel titleLabel = new JLabel("List Penjualan");
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

        String[] columnNames = {"Nomor", "Tanggal Penjualan", "Kode Penjualan", "Customer"};
        model = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        table = new JTable(model) {
            @Override
            public int getRowHeight(int row) {
                return 40;
            }
        };

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        table.setDefaultRenderer(Object.class, centerRenderer);

        JScrollPane scrollPane = new JScrollPane(table);
        contentPanel.add(scrollPane, BorderLayout.CENTER);

        JButton addButton = new JButton("Tambah Penjualan");
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new AddSaleFrame();
            }
        });

        // Create deleteComboBox
        deleteComboBox = new JComboBox<>();
        deleteComboBox.setPreferredSize(new Dimension(200, 30));

        // Create edit button
        JButton editButton = new JButton("Edit Penjualan");
        editButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showSaleSelectionDialog("Edit");
            }
        });

        // Create delete button
        JButton deleteButton = new JButton("Hapus Penjualan");
        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showSaleSelectionDialog("Delete");
            }
        });

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        contentPanel.add(buttonPanel, BorderLayout.SOUTH);

        mainPanel.add(contentPanel, BorderLayout.CENTER);

        add(mainPanel);

        // Load data from the database
        loadData();

        setVisible(true);
    }

    public void loadData() {
        model.setRowCount(0); // Clear existing rows
        deleteComboBox.removeAllItems(); // Clear existing items in the combo box
        try (Connection connection = Dbconnect.getConnect()) {
            if (connection != null) {
                String query = "SELECT id_penjualan, kode, tanggal FROM penjualan";
                Statement statement = connection.createStatement();
                ResultSet resultSet = statement.executeQuery(query);

                int rowNum = 1;
                while (resultSet.next()) {
                    String idPenjualan = resultSet.getString("kode");
                    String tanggal = resultSet.getString("tanggal");
                    String kode = resultSet.getString("kode");

                    model.addRow(new Object[]{rowNum++, tanggal, idPenjualan, "Cash"});
                    deleteComboBox.addItem(kode); // Add kode to the combo box
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void refreshTable() {
        loadData();
    }

    private void showSaleSelectionDialog(String action) {
        if (deleteComboBox.getItemCount() > 0) {
            String[] saleArray = new String[deleteComboBox.getItemCount()];
            for (int i = 0; i < deleteComboBox.getItemCount(); i++) {
                saleArray[i] = (String) deleteComboBox.getItemAt(i);
            }
            String selectedSale = (String) JOptionPane.showInputDialog(this,
                    "Pilih kode penjualan yang ingin " + action.toLowerCase() + ":",
                    action + " Penjualan",
                    JOptionPane.PLAIN_MESSAGE,
                    null,
                    saleArray,
                    saleArray[0]);
    
            if (selectedSale != null) {
                int row = findRowBySaleCode(selectedSale);
                if (action.equals("Edit")) {
                    editSale(row);
                } else if (action.equals("Delete")) {
                    deleteSale(row);
                }
            }
        } else {
            JOptionPane.showMessageDialog(this, "Tidak ada penjualan yang tersedia untuk " + action.toLowerCase() + ".", "Peringatan", JOptionPane.WARNING_MESSAGE);
        }
    }
    
    

    private int findRowBySaleCode(String saleCode) {
        for (int i = 0; i < model.getRowCount(); i++) {
            if (model.getValueAt(i, 2).equals(saleCode)) {
                return i;
            }
        }
        return -1; // Not found
    }

    private void editSale(int row) {
        String saleCode = model.getValueAt(row, 2).toString();
        String date = model.getValueAt(row, 1).toString();

        new EditSaleFrame(this, saleCode, date);
    }

    private void deleteSale(int row) {
        String saleCode = model.getValueAt(row, 2).toString();
        int confirmed = JOptionPane.showConfirmDialog(this, "Apakah Anda yakin ingin menghapus penjualan ini?", "Konfirmasi Hapus", JOptionPane.YES_NO_OPTION);
        if (confirmed == JOptionPane.YES_OPTION) {
            try (Connection conn = Dbconnect.getConnect()) {
                conn.setAutoCommit(false); // Start transaction
    
                // Get id_penjualan based on kode
                String getIdQuery = "SELECT id_penjualan FROM penjualan WHERE kode = ?";
                try (PreparedStatement getIdStmt = conn.prepareStatement(getIdQuery)) {
                    getIdStmt.setString(1, saleCode);
                    ResultSet rs = getIdStmt.executeQuery();
                    if (rs.next()) {
                        String idPenjualan = rs.getString("id_penjualan");
    
                        // Delete from detail_penjualan first
                        String deleteDetailQuery = "DELETE FROM detail_penjualan WHERE id_penjualan = ?";
                        try (PreparedStatement deleteDetailStmt = conn.prepareStatement(deleteDetailQuery)) {
                            deleteDetailStmt.setString(1, idPenjualan);
                            deleteDetailStmt.executeUpdate();
                        }
    
                        // Then delete from penjualan
                        String deleteSaleQuery = "DELETE FROM penjualan WHERE kode = ?";
                        try (PreparedStatement deleteSaleStmt = conn.prepareStatement(deleteSaleQuery)) {
                            deleteSaleStmt.setString(1, saleCode);
                            int rowsAffected = deleteSaleStmt.executeUpdate();
                            if (rowsAffected > 0) {
                                conn.commit(); // Commit transaction
                                JOptionPane.showMessageDialog(this, "Penjualan berhasil dihapus!");
                                loadData();
                            } else {
                                JOptionPane.showMessageDialog(this, "Gagal menghapus penjualan.");
                                conn.rollback(); // Rollback transaction
                            }
                        }
                    } else {
                        JOptionPane.showMessageDialog(this, "Penjualan tidak ditemukan.");
                        conn.rollback(); // Rollback transaction
                    }
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    conn.rollback(); // Rollback transaction
                    JOptionPane.showMessageDialog(this, "Terjadi kesalahan pada database: " + ex.getMessage());
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Terjadi kesalahan pada database: " + ex.getMessage());
            }
        }
    }
    

    public static void main(String[] args) {
        new SalesListPage();
    }
}