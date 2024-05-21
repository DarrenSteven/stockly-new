package stockly;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class AddProductFrame extends JFrame {
    private JTextField idField;
    private JTextField nameField;
    private JTextField priceField;
    private JTextField quantityField; 
    private JTextField unitField; 

    public AddProductFrame() {
        setTitle("Tambahkan Produk yang Akan Dijual"); 
        setSize(400, 300); 
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null); 

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20)); 

        // Panel untuk judul
        JPanel titlePanel = new JPanel();
        JLabel titleLabel = new JLabel("Tambahkan Produk yang Akan Dijual");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        titlePanel.add(titleLabel);

        // Panel untuk input
        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new GridLayout(5, 2, 5, 5)); 

        JLabel idLabel = new JLabel("ID:");
        inputPanel.add(idLabel);
        idField = new JTextField(15);
        idField.setPreferredSize(new Dimension(200, 25)); 
        inputPanel.add(idField);

        JLabel nameLabel = new JLabel("Nama Barang:");
        inputPanel.add(nameLabel);
        nameField = new JTextField(15);
        nameField.setPreferredSize(new Dimension(200, 25)); 
        inputPanel.add(nameField);

        JLabel priceLabel = new JLabel("Harga Jual:");
        inputPanel.add(priceLabel);
        priceField = new JTextField(15);
        priceField.setPreferredSize(new Dimension(200, 25)); 
        inputPanel.add(priceField);

        JLabel quantityLabel = new JLabel("Jumlah:");
        inputPanel.add(quantityLabel);
        quantityField = new JTextField(15);
        quantityField.setPreferredSize(new Dimension(200, 25)); 
        inputPanel.add(quantityField);

        JLabel unitLabel = new JLabel("Satuan:");
        inputPanel.add(unitLabel);
        unitField = new JTextField(15);
        unitField.setPreferredSize(new Dimension(200, 25)); 
        inputPanel.add(unitField);

        // Panel untuk tombol
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton cancelButton = new JButton("Batal");
        cancelButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                dispose(); 
            }
        });
        buttonPanel.add(cancelButton);

        JButton addButton = new JButton("Tambah");
        addButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });
        buttonPanel.add(addButton);

        mainPanel.add(titlePanel, BorderLayout.NORTH);
        mainPanel.add(inputPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel);

        setVisible(true);
    }

    public static void main(String[] args) {
        new AddProductFrame();
    }
}
