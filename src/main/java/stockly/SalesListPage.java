package stockly;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import javax.swing.table.TableColumn;
import java.awt.event.ActionEvent;
import javax.swing.table.TableRowSorter;
import java.awt.event.ActionListener;
import javax.swing.border.EmptyBorder;
import java.sql.*;

public class SalesListPage extends JFrame {
    private DefaultTableModel model;
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

        JTextField searchBar = new JTextField();
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
        model = new DefaultTableModel(columnNames, 0);
        table = new JTable(model);
        table.setRowHeight(40);

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        table.setDefaultRenderer(Object.class, centerRenderer);

        TableColumn column = table.getColumnModel().getColumn(0); // Kolom Nomor
        column.setPreferredWidth(50);

        JScrollPane scrollPane = new JScrollPane(table);
        contentPanel.add(scrollPane, BorderLayout.CENTER);

        searchBar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String searchText = searchBar.getText().trim();
                filterData(searchText);
            }
        });

        JButton addButton = new JButton("Tambah");
        JButton editButton = new JButton("Edit");
        JButton deleteButton = new JButton("Hapus");

        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new AddSaleFrame();
            }
        });

        editButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = table.getSelectedRow();
                if (selectedRow != -1) {
                    String saleCode = model.getValueAt(selectedRow, 2).toString();
                    String date = model.getValueAt(selectedRow, 1).toString();
                    new EditSaleFrame(SalesListPage.this, saleCode, date);
                } else {
                    JOptionPane.showMessageDialog(null, "Pilih baris yang ingin di edit.");
                }
            }
        });

        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = table.getSelectedRow();
                if (selectedRow != -1) {
                    deletePurchase(selectedRow);
                } else {
                    JOptionPane.showMessageDialog(null, "Pilih baris yang ingin dihapus.");
                }
            }
        });

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(addButton);
        contentPanel.add(buttonPanel, BorderLayout.SOUTH);

        mainPanel.add(contentPanel, BorderLayout.CENTER);

        add(mainPanel);

        loadData();

        setVisible(true);
    }

    public void loadData() {
        model.setRowCount(0); // Bersihkan semua baris dalam model sebelum memuat data baru
        int rowNum = 1; // Inisialisasi nomor baris
        try (Connection connection = Dbconnect.getConnect()) {
            String query = "SELECT id_penjualan, kode, tanggal FROM penjualan";
            try (Statement stmt = connection.createStatement();
                 ResultSet rs = stmt.executeQuery(query)) {
                while (rs.next()) {
                    String kode = rs.getString("kode");
                    String tanggal = rs.getString("tanggal");
                    int idPenjualan = rs.getInt("id_penjualan");
                    model.addRow(new Object[]{rowNum++, tanggal, kode, "Cash"});
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading data from database.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    

    private void filterData(String searchText) {
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(model);
        table.setRowSorter(sorter);
        RowFilter<DefaultTableModel, Object> rowFilter = RowFilter.regexFilter("(?i)" + searchText, 1); // 1 adalah indeks kolom kode pembelian
        sorter.setRowFilter(rowFilter);
    }

    private void deletePurchase(int row) {
        String idPembelian = model.getValueAt(row, 0).toString();
        if (JOptionPane.showConfirmDialog(this, "Apakah Anda yakin ingin menghapus pembelian ini?", "Konfirmasi", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            try (Connection connection = Dbconnect.getConnect()) {
                connection.setAutoCommit(false);  // Start transaction

                // Delete from detail_pembelian
                String deleteDetailQuery = "DELETE FROM detail_penjualan WHERE id_penjualan = ?";
                try (PreparedStatement psDetail = connection.prepareStatement(deleteDetailQuery)) {
                    psDetail.setString(1, idPembelian);
                    psDetail.executeUpdate();
                }

                // Delete from pembelian
                String deletePurchaseQuery = "DELETE FROM penjualan WHERE id_penjualan = ?";
                try (PreparedStatement psPurchase = connection.prepareStatement(deletePurchaseQuery)) {
                    psPurchase.setString(1, idPembelian);
                    psPurchase.executeUpdate();
                }

                connection.commit();  // Commit transaction

                // Remove row from table model
                model.removeRow(row);
                JOptionPane.showMessageDialog(this, "Data berhasil dihapus.", "Sukses", JOptionPane.INFORMATION_MESSAGE);
            } catch (SQLException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error deleting data from database.", "Error", JOptionPane.ERROR_MESSAGE);
                try (Connection connection = Dbconnect.getConnect()) {
                    connection.rollback();  // Rollback transaction on error
                } catch (SQLException rollbackEx) {
                    rollbackEx.printStackTrace();
                }
            }
        }
    }

    public static void main(String[] args) {
        new SalesListPage();
    }
}