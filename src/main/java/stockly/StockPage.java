package stockly;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.border.EmptyBorder;

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
    public StockPage() {
        setTitle("Stockly - Stock Barang");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1300,850); 
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

        String[] columnNames = {"ID", "Nama Barang", "Jumlah", "Satuan", "Harga Pembelian", "Harga Penjualan", "Aksi"};
        Object[][] data = {
            {"P001", "Chocolatos", "10", "box", "Rp10.000", "Rp15.000", ""},
            {"P002", "Tango", "15", "box", "Rp20.000", "Rp25.000", ""},
            {"P003", "Momogi", "20", "box", "Rp5.000", "Rp7.000", ""}
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

        DefaultTableCellRenderer leftRenderer = new DefaultTableCellRenderer();
        leftRenderer.setHorizontalAlignment(JLabel.LEFT);
        table.getColumnModel().getColumn(1).setCellRenderer(leftRenderer);

        table.getColumnModel().getColumn(6).setCellRenderer((table1, value, isSelected, hasFocus, row, column) -> {
            JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 5)); 
            JButton editButton = new JButton("Edit");
            JButton deleteButton = new JButton("Hapus");
            panel.add(editButton);
            panel.add(deleteButton);
            return panel;
        });

        JScrollPane scrollPane = new JScrollPane(table);
        contentPanel.add(scrollPane, BorderLayout.CENTER);

        JButton addButton = new JButton("Tambah Produk");
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new AddProductFrame();
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
        new StockPage();
    }
}
