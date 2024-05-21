package stockly;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;

public class AddPurchaseFrame extends JFrame {
    public AddPurchaseFrame() {
        setTitle("Tambah Pembelian");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(1300, 850);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        // Sidebar
        Sidebar sidebar = new Sidebar();
        mainPanel.add(sidebar, BorderLayout.WEST);

        // Panel untuk konten utama
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        // Panel input
        JPanel inputPanel = new JPanel(new BorderLayout());
        inputPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Panel judul
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        titlePanel.setBorder(new EmptyBorder(0, 0, 10, 0));
        JLabel titleLabel = new JLabel("Tambah Pembelian");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titlePanel.add(titleLabel);
        inputPanel.add(titlePanel, BorderLayout.NORTH);

        // Panel untuk section 1 dan section 2
        JPanel sectionsPanel = new JPanel(new GridLayout(2, 1, 0, 20)); 
        sectionsPanel.setBorder(new CompoundBorder(new LineBorder(Color.BLACK), new EmptyBorder(10, 10, 10, 10))); 

        // Section 1: Tanggal, ID Transaksi, dan Supplier
        JPanel dateIdSupplierPanel = new JPanel(new GridLayout(3, 2, 5, 5)); 

        JLabel dateLabel = new JLabel("Tanggal:");
        JTextField dateField = new JTextField(15);
        JLabel idLabel = new JLabel("ID Transaksi:");
        JTextField idField = new JTextField(15);
        JLabel supplierLabel = new JLabel("Supplier:");
        JComboBox<String> supplierComboBox = new JComboBox<>(new String[]{"Steven", "Darren", "William"}); 

        dateIdSupplierPanel.add(dateLabel);
        dateIdSupplierPanel.add(dateField);
        dateIdSupplierPanel.add(idLabel);
        dateIdSupplierPanel.add(idField);
        dateIdSupplierPanel.add(supplierLabel);
        dateIdSupplierPanel.add(supplierComboBox);

        sectionsPanel.add(dateIdSupplierPanel);

        // Section 2: Pencarian barang, jumlah, dan harga
        JPanel itemPanel = new JPanel(new GridLayout(3, 2, 5, 5));

        JLabel itemLabel = new JLabel("Cari Barang/ID Barang:");
        JTextField itemField = new JTextField(10); 
        JLabel qtyLabel = new JLabel("Jumlah:");
        JTextField qtyField = new JTextField(5);
        JLabel priceLabel = new JLabel("Harga:");
        JTextField priceField = new JTextField(5); 
        priceField.setEnabled(false); 
        itemPanel.add(itemLabel);
        itemPanel.add(itemField);
        itemPanel.add(qtyLabel);
        itemPanel.add(qtyField);
        itemPanel.add(priceLabel);
        itemPanel.add(priceField);
        sectionsPanel.add(itemPanel);

        inputPanel.add(sectionsPanel, BorderLayout.CENTER);

        // Panel untuk tombol Tambah
        JPanel buttonPanel = new JPanel(new BorderLayout());
        JButton tambahButton = new JButton("Tambah");
        buttonPanel.add(tambahButton, BorderLayout.EAST); 

        inputPanel.add(buttonPanel, BorderLayout.SOUTH);

        contentPanel.add(inputPanel, BorderLayout.NORTH);

        // Tabel untuk menampilkan data
        String[] columnNames = {"ID", "Nama Barang", "Harga", "Jumlah", "Total Harga"};
        Object[][] data = {
                {"P001", "Surya", "Rp19.000", 20, "Rp380.000"},
                {"P002", "L.A.", "Rp16.000", 10, "Rp160.000"},
                {"P003", "Gudang Garam", "Rp18.000", 10, "Rp180.000"}
        };

        DefaultTableModel model = new DefaultTableModel(data, columnNames);
        JTable table = new JTable(model) {
            @Override
            public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
                Component component = super.prepareRenderer(renderer, row, column);
                if (!(renderer instanceof HeaderRenderer)) {
                    ((JComponent) component).setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
                    if (column != 1) {
                        ((JLabel) component).setHorizontalAlignment(SwingConstants.CENTER);
                    }
                }
                return component;
            }
        };
        table.setRowHeight(30); 
        JScrollPane scrollPane = new JScrollPane(table);
        contentPanel.add(scrollPane, BorderLayout.CENTER);

        // Panel untuk subtotal, diskon, dan total
        JPanel bottomPanel = new JPanel(new BorderLayout());

        JPanel subtotalPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JLabel subtotalLabel = new JLabel("Subtotal:");
        JTextField subtotalTextField = new JTextField("Rp720.000", 10); 
        subtotalTextField.setEditable(false); 
        JLabel diskonLabel = new JLabel("Diskon:");
        JTextField diskonTextField = new JTextField("Rp0", 10); 
        diskonTextField.setEditable(false); 
        JLabel totalLabel = new JLabel("Total:");
        JTextField totalTextField = new JTextField("Rp720.000", 10); 
        totalTextField.setEditable(false); 
        subtotalPanel.add(subtotalLabel);
        subtotalPanel.add(subtotalTextField);
        subtotalPanel.add(diskonLabel);
        subtotalPanel.add(diskonTextField);
        subtotalPanel.add(totalLabel);
        subtotalPanel.add(totalTextField);

        bottomPanel.add(subtotalPanel, BorderLayout.NORTH);

        // Panel untuk tombol Hapus, Batal, Simpan
        JPanel actionButtonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton hapusButton = new JButton("Hapus");
        JButton batalButton = new JButton("Batal");
        JButton simpanButton = new JButton("Simpan");
        actionButtonPanel.add(hapusButton);
        actionButtonPanel.add(batalButton);
        actionButtonPanel.add(simpanButton);

        bottomPanel.add(actionButtonPanel, BorderLayout.SOUTH);

        contentPanel.add(bottomPanel, BorderLayout.SOUTH);

        mainPanel.add(contentPanel, BorderLayout.CENTER);

        add(mainPanel);
        setVisible(true);
    }

    private static class HeaderRenderer extends DefaultTableCellRenderer {
        public HeaderRenderer() {
            setHorizontalAlignment(SwingConstants.CENTER);
        }
    }

    public static void main(String[] args) {
        new AddPurchaseFrame();
    }
}
