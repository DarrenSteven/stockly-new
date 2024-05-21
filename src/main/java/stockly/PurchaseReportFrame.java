package stockly;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class PurchaseReportFrame extends JFrame {
    public PurchaseReportFrame() {
        setTitle("Laporan Pembelian");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1300, 850);
        setLocationRelativeTo(null);

        // Panel utama untuk konten dan sidebar
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        // Sidebar
        Sidebar sidebar = new Sidebar();
        mainPanel.add(sidebar, BorderLayout.WEST);

        // Panel untuk konten utama
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        // Panel untuk section 1: Tanggal Mulai dan Tanggal Akhir
        JPanel dateRangePanel = new JPanel(new GridLayout(2, 1, 5, 5));
        dateRangePanel.setBorder(BorderFactory.createTitledBorder("Rentang Tanggal"));
        dateRangePanel.setPreferredSize(new Dimension(300, 100));

        JPanel startDatePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel startDateLabel = new JLabel("Tanggal Mulai:");
        JTextField startDateTextField = new JTextField(10);
        startDatePanel.add(startDateLabel);
        startDatePanel.add(startDateTextField);

        JPanel endDatePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel endDateLabel = new JLabel("Tanggal Akhir:");
        JTextField endDateTextField = new JTextField(10);
        endDatePanel.add(endDateLabel);
        endDatePanel.add(endDateTextField);

        dateRangePanel.add(startDatePanel);
        dateRangePanel.add(endDatePanel);

        // Panel untuk section 2: Cari Nama / ID Barang
        JPanel searchPanel = new JPanel(new GridLayout(2, 1, 5, 5));
        searchPanel.setBorder(BorderFactory.createTitledBorder("Cari"));
        searchPanel.setPreferredSize(new Dimension(300, 100));

        JPanel searchItemPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel searchItemLabel = new JLabel("Nama / ID Barang:");
        JTextField searchItemTextField = new JTextField(10);
        searchItemPanel.add(searchItemLabel);
        searchItemPanel.add(searchItemTextField);

        JPanel searchVendorPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel searchVendorLabel = new JLabel("Nama / ID Vendor:");
        JTextField searchVendorTextField = new JTextField(10);
        searchVendorPanel.add(searchVendorLabel);
        searchVendorPanel.add(searchVendorTextField);

        searchPanel.add(searchItemPanel);
        searchPanel.add(searchVendorPanel);

        // Panel untuk input (section 1 dan section 2)
        JPanel inputPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        inputPanel.add(dateRangePanel);
        inputPanel.add(searchPanel);

        // Panel judul dan tanggal
        JPanel titleDatePanel = new JPanel(new BorderLayout());

        JLabel tableTitleLabel = new JLabel("Laporan Pembelian");
        tableTitleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        tableTitleLabel.setHorizontalAlignment(SwingConstants.CENTER); 
        titleDatePanel.add(tableTitleLabel, BorderLayout.NORTH);

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        String currentDate = dateFormat.format(new Date());
        JLabel dateRangeLabel = new JLabel("Tanggal " + currentDate + " s.d. " + currentDate);
        dateRangeLabel.setHorizontalAlignment(SwingConstants.CENTER); 
        titleDatePanel.add(dateRangeLabel, BorderLayout.CENTER);

        // Panel untuk tombol Generate dan Reset
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton generateButton = new JButton("Generate");
        JButton resetButton = new JButton("Reset");

        JButton printButton = new JButton("Print");
        buttonPanel.add(generateButton);
        buttonPanel.add(resetButton);
        buttonPanel.add(printButton);

        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));
        topPanel.setPreferredSize(new Dimension(1300, 200)); 
        topPanel.add(inputPanel);
        topPanel.add(buttonPanel);
        topPanel.add(titleDatePanel);

        contentPanel.add(topPanel, BorderLayout.NORTH);

        // Panel untuk tabel laporan penjualan
        JPanel tablePanel = new JPanel(new BorderLayout());

        String[] columnNames = {"Tgl", "ID Transaksi", "Nama Barang", "Nama Vendor", "Total"};
        Object[][] data = {
                {"01-04-2024", "ID001", "Chocolatos", "Nikolas", "Rp100.000"},
                {"02-04-2024", "ID002", "Tango", "Nikolas", "Rp10.000"},
                {"03-04-2024", "ID003", "Beng Beng", "Nikolas", "Rp20.000"},
                {"04-04-2024", "ID004", "Nabati", "Nikolas", "Rp80.000"}
        };

        DefaultTableModel model = new DefaultTableModel(data, columnNames);
        JTable table = new JTable(model);
        table.setFont(new Font("Arial", Font.PLAIN, 12));
        table.setRowHeight(30);

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        table.setDefaultRenderer(Object.class, centerRenderer);

        table.setIntercellSpacing(new Dimension(15, 15));

        JScrollPane scrollPane = new JScrollPane(table);
        tablePanel.add(scrollPane, BorderLayout.CENTER);

        contentPanel.add(tablePanel, BorderLayout.CENTER);

        mainPanel.add(contentPanel, BorderLayout.CENTER);

        add(mainPanel);
        setVisible(true);
    }

    public static void main(String[] args) {
        new PurchaseReportFrame();
    }
}
