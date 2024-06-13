package stockly;

import java.sql.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.RowFilter;
import javax.swing.border.EmptyBorder;
import javax.swing.table.TableRowSorter;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableCellEditor;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.EventObject;

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

        String[] columnNames = {"Nomor", "Kode Pembelian", "Tanggal Pembelian", "Nama Pemasok", "Aksi"};
        model = new DefaultTableModel(columnNames, 0);
        table = new JTable(model);
        table.setRowHeight(40);

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        table.setDefaultRenderer(Object.class, centerRenderer);

        table.getColumnModel().getColumn(4).setCellRenderer(new ButtonRenderer());
        table.getColumnModel().getColumn(4).setCellEditor(new ButtonEditor(new JCheckBox()));

        // Mengatur lebar kolom
        TableColumn column = table.getColumnModel().getColumn(0); // Kolom Nomor
        column.setPreferredWidth(50);

        column = table.getColumnModel().getColumn(4); // Kolom Aksi
        column.setPreferredWidth(200);

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
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new AddPurchaseFrame();
            }
        });
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
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
                            rs.getString("nama_pemasok"),
                            ""
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

    public static void main(String[] args) {
        new PurchaseListPage();
    }
}

class ButtonRenderer extends JPanel implements TableCellRenderer {

    public ButtonRenderer() {
        setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
        add(new JButton("Detail"));
        add(new JButton("Edit"));
        add(new JButton("Hapus"));
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        return this;
    }
}

class ButtonEditor extends AbstractCellEditor implements TableCellEditor, ActionListener {

    private JPanel panel;
    private JButton detailButton;
    private JButton editButton;
    private JButton deleteButton;
    private String label;

    public ButtonEditor(JCheckBox checkBox) {
        panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 5));

        detailButton = new JButton("Detail");
        editButton = new JButton("Edit");
        deleteButton = new JButton("Hapus");

        detailButton.addActionListener(this);
        editButton.addActionListener(this);
        deleteButton.addActionListener(this);

        panel.add(detailButton);
        panel.add(editButton);
        panel.add(deleteButton);
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
        return panel;
    }

    @Override
    public Object getCellEditorValue() {
        return label;
    }

    @Override
    public boolean stopCellEditing() {
        return super.stopCellEditing();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        fireEditingStopped();
        // Implement your button action here
        if (e.getSource() == detailButton) {
            System.out.println("Detail button clicked");
        } else if (e.getSource() == editButton) {
            System.out.println("Edit button clicked");
        } else if (e.getSource() == deleteButton) {
            System.out.println("Delete button clicked");
        }
    }
}