package stockly;

import java.sql.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.RowFilter;
import javax.swing.border.EmptyBorder;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class PurchaseListPage extends JFrame {
    
    private JTable table;
    private DefaultTableModel model;

    public PurchaseListPage() {
        setTitle("Stockly - List Pembelian");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1300, 850);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        Sidebar sidebar = new Sidebar();
        mainPanel.add(sidebar, BorderLayout.WEST);

        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        JLabel titleLabel = new JLabel("List Pembelian");
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

        String[] columnNames = {"Nomor", "Kode Pembelian", "Tanggal Pembelian", "Nama Pemasok"};
        model = new DefaultTableModel(columnNames, 0);
        table = new JTable(model);
        table.setRowHeight(40);

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        table.setDefaultRenderer(Object.class, centerRenderer);

        // Mengatur lebar kolom
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

        JButton addButton = new JButton("Tambah Pembelian");
        JButton detailButton = new JButton("Detail");
        JButton editButton = new JButton("Edit");
        JButton deleteButton = new JButton("Hapus");

        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new AddPurchaseFrame();
            }
        });

        detailButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = table.getSelectedRow();
                if (selectedRow != -1) {
                    System.out.println("Detail button clicked for row " + selectedRow);
                    // Implement detail action here
                } else {
                    JOptionPane.showMessageDialog(null, "Pilih baris yang ingin dilihat detailnya.");
                }
            }
        });

        editButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = table.getSelectedRow();
                if (selectedRow != -1) {
                    System.out.println("Edit button clicked for row " + selectedRow);
                    // Implement edit action here
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
        buttonPanel.add(detailButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(addButton);
        contentPanel.add(buttonPanel, BorderLayout.SOUTH);

        mainPanel.add(contentPanel, BorderLayout.CENTER);

        add(mainPanel);

        loadData();

        setVisible(true);
    }

    private void loadData() {
        try (Connection connection = Dbconnect.getConnect()) {
            String query = "SELECT p.id_pembelian, p.kode, p.tanggal, ps.nama AS nama_pemasok " +
                           "FROM pembelian p " +
                           "JOIN pemasok ps ON p.id_pemasok = ps.id_pemasok";
            try (Statement stmt = connection.createStatement();
                 ResultSet rs = stmt.executeQuery(query)) {
                while (rs.next()) {
                    Object[] row = {
                            rs.getString("id_pembelian"),
                            rs.getString("kode"),
                            rs.getString("tanggal"),
                            rs.getString("nama_pemasok")
                    };
                    model.addRow(row);
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

    public void deletePurchase(int row) {
        String idPembelian = model.getValueAt(row, 0).toString();
        System.out.println("Deleting purchase with id: " + idPembelian);  // Debug: Print id_pembelian

        try (Connection connection = Dbconnect.getConnect()) {
            // Delete from detail_pembelian
            String deleteDetailQuery = "DELETE FROM detail_pembelian WHERE id_pembelian = ?";
            try (PreparedStatement psDetail = connection.prepareStatement(deleteDetailQuery)) {
                psDetail.setString(1, idPembelian);
                int detailRowsDeleted = psDetail.executeUpdate();
                System.out.println("Deleted from detail_pembelian: " + detailRowsDeleted + " rows");  // Debug: Print rows deleted
            }

            // Delete from pembelian
            String deletePurchaseQuery = "DELETE FROM pembelian WHERE id_pembelian = ?";
            try (PreparedStatement psPurchase = connection.prepareStatement(deletePurchaseQuery)) {
                psPurchase.setString(1, idPembelian);
                int purchaseRowsDeleted = psPurchase.executeUpdate();
                System.out.println("Deleted from pembelian: " + purchaseRowsDeleted + " rows");  // Debug: Print rows deleted
            }

            // Remove row from table model
            model.removeRow(row);
            JOptionPane.showMessageDialog(this, "Data berhasil dihapus.", "Sukses", JOptionPane.INFORMATION_MESSAGE);
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error deleting data from database.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        new PurchaseListPage();
    }
}
