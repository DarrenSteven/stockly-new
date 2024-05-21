package stockly;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.border.EmptyBorder;

public class SalesListPage extends JFrame {
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

        String[] columnNames = {"Nomor", "Tanggal Penjualan", "ID Penjualan", "Harga", "Customer", "Aksi"}; 
        Object[][] data = {
                {"1", "01/04/2024", "PJ001", "Rp100.000", "Cash", ""},
                {"2", "02/04/2024", "PJ002", "Rp150.000", "Cash", ""},
                {"3", "03/04/2024", "PJ003", "Rp200.000", "Cash", ""}
        };
        DefaultTableModel model = new DefaultTableModel(data, columnNames) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        JTable table = new JTable(model) {
            @Override
            public int getRowHeight(int row) {
                return 40; 
            }
        };

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        table.setDefaultRenderer(Object.class, centerRenderer);

        table.getColumnModel().getColumn(5).setCellRenderer((table1, value, isSelected, hasFocus, row, column) -> {
            JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 5)); 
            JButton editButton = new JButton("Edit");
            JButton deleteButton = new JButton("Hapus");
            panel.add(editButton);
            panel.add(deleteButton);
            return panel;
        });

        JScrollPane scrollPane = new JScrollPane(table);
        contentPanel.add(scrollPane, BorderLayout.CENTER);

        JButton addButton = new JButton("Tambah Penjualan");
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new AddSaleFrame();
            }
        });
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(addButton);
        contentPanel.add(buttonPanel, BorderLayout.SOUTH);

        mainPanel.add(contentPanel, BorderLayout.CENTER);

        add(mainPanel);

        setVisible(true);
    }

    public static void main(String[] args) {
        new SalesListPage();
    }
}
