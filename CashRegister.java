import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.util.ArrayList;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class CashRegister {
    // Fönster och kvittoruta
    JFrame frame;
    JTextArea receipt;

    // Produktknappar
    JButton kaffeButton;
    JButton nalleButton;
    JButton muggButton;
    JButton chipsButton;
    JButton vaniljYoghurtButton;
    JButton daimButton;

    // Inmatning
    JTextArea inputProductName;
    JTextArea inputCount;

    // Funktionsknappar
    JButton addToReceiptButton;
    JButton payButton;

    // Produkter
    ArrayList<Product> allProducts = new ArrayList<>();
    Product kaffe;
    Product daim;
    Product nalle;
    Product mugg;
    Product chips;
    Product yoghurt;

    // Tillstånd under ett kvitto
    Product lastClickProduct = null;
    double totalSumma = 0.0;
    int receiptNo = 0;

    // Kvittoheader
    private static final String STORE_NAME = "MARUFS LIVS";
    private static final DateTimeFormatter TS_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public CashRegister() {
        frame = new JFrame("IOT25 POS");

        // Bygger upp allt i samma ordning som jag tänker på det
        createReceiptArea();
        addProducts();
        createQuickButtonsArea();
        createAddArea();

        // Grundinställningar för fönstret
        frame.getContentPane().setBackground(Color.BLACK);
        frame.setSize(1000, 1000);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(null);
        frame.setLocationRelativeTo(null);

        // Startar alltid med ett nytt kvitto
        startNewReceipt();

        frame.setVisible(true);
    }

    // Lägger in de produkter jag använder i knapparna
    private void addProducts() {
        kaffe = new Product();
        kaffe.setName("Kaffe");
        kaffe.setPrice(32);
        allProducts.add(kaffe);

        daim = new Product();
        daim.setName("Daim");
        daim.setPrice(10);
        allProducts.add(daim);

        nalle = new Product();
        nalle.setName("Nalle");
        nalle.setPrice(119);
        allProducts.add(nalle);

        mugg = new Product();
        mugg.setName("Mugg");
        mugg.setPrice(89);
        allProducts.add(mugg);

        chips = new Product();
        chips.setName("Chips");
        chips.setPrice(25);
        allProducts.add(chips);

        yoghurt = new Product();
        yoghurt.setName("Yoghurt");
        yoghurt.setPrice(13);
        allProducts.add(yoghurt);
    }

    // Inmatningsdelen längst ner, produktnamn visas och antal skrivs
    private void createAddArea() {
        inputProductName = new JTextArea();
        inputProductName.setBounds(20, 600, 300, 30);
        inputProductName.setFont(new Font("Arial Black", Font.BOLD, 18));
        inputProductName.setEditable(false); // jag väljer via knapparna
        frame.add(inputProductName);

        JLabel label = new JLabel("Antal");
        label.setBounds(340, 600, 300, 30);
        label.setForeground(Color.WHITE);
        frame.add(label);

        inputCount = new JTextArea("1"); // börjar på 1 för att det går snabbare
        inputCount.setBounds(330, 600, 50, 30);
        inputCount.setFont(new Font("Arial Black", Font.BOLD, 18));
        frame.add(inputCount);

        addToReceiptButton = new JButton("Add");
        addToReceiptButton.setBounds(400, 600, 70, 50);
        addToReceiptButton.setFont(new Font("Arial Black", Font.PLAIN, 14));
        frame.add(addToReceiptButton);

        // När jag trycker Add ska en rad läggas till på kvittot
        addToReceiptButton.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                if (lastClickProduct == null) {
                    JOptionPane.showMessageDialog(frame, "Välj en produkt via knapparna.");
                    return;
                }

                String countStr = inputCount.getText().trim();
                if (countStr.isEmpty()) {
                    JOptionPane.showMessageDialog(frame, "Ange antal.");
                    return;
                }

                int count;
                try {
                    count = Integer.parseInt(countStr);
                    if (count <= 0) {
                        JOptionPane.showMessageDialog(frame, "Antalet måste vara större än 0.");
                        return;
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(frame, "Ogiltigt antal. Skriv ett heltal.");
                    return;
                }

                // Beräknar radtotal och uppdaterar totalsumman
                double lineTotal = lastClickProduct.getPrice() * count;
                totalSumma += lineTotal;

                // Skriver ut raden, håller kolumnerna någorlunda raka
                receipt.append(String.format("%-20s %3d * %7.2f = %8.2f\n",
                        lastClickProduct.getName(), count, (double) lastClickProduct.getPrice(), lineTotal));

                // Förbereder för nästa rad
                inputProductName.setText("");
                inputCount.setText("1");
                lastClickProduct = null;
            }
        });

        payButton = new JButton("Pay");
        payButton.setBounds(480, 600, 70, 50);
        payButton.setFont(new Font("Arial Black", Font.PLAIN, 14));
        frame.add(payButton);

        // Avslutar kvittot, visar total och börjar ett nytt
        payButton.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                receipt.append("Total                                        ------\n");
                receipt.append(String.format("%46s\n", String.format("%.2f", totalSumma)));
                receipt.append("TACK FÖR DITT KÖP\n");

                JOptionPane.showMessageDialog(frame, "Betalning registrerad. Kvittot rensas nu.");
                startNewReceipt();
            }
        });
    }

    // Vänstersidan med snabbknappar för produkterna
    private void createQuickButtonsArea() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 50, 50));
        panel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        panel.setBackground(Color.GREEN);
        panel.setPreferredSize(new Dimension(500, 500));
        Dimension PRODUCT_BIN_SIZE = new Dimension(190, 90);

        // En knapp per produkt, sätter vald produkt och fokuserar antal
        kaffeButton = new JButton("Kaffe");
        kaffeButton.setPreferredSize(PRODUCT_BIN_SIZE);
        kaffeButton.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                selectProduct(kaffe);
            }
        });
        panel.add(kaffeButton);

        daimButton = new JButton("Daim");
        daimButton.setPreferredSize(PRODUCT_BIN_SIZE);
        daimButton.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                selectProduct(daim);
            }
        });
        panel.add(daimButton);

        nalleButton = new JButton("Nalle");
        nalleButton.setPreferredSize(PRODUCT_BIN_SIZE);
        nalleButton.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                selectProduct(nalle);
            }
        });
        panel.add(nalleButton);

        muggButton = new JButton("Mugg");
        muggButton.setPreferredSize(PRODUCT_BIN_SIZE);
        muggButton.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                selectProduct(mugg);
            }
        });
        panel.add(muggButton);

        chipsButton = new JButton("Chips");
        chipsButton.setPreferredSize(PRODUCT_BIN_SIZE);
        chipsButton.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                selectProduct(chips);
            }
        });
        panel.add(chipsButton);

        vaniljYoghurtButton = new JButton("Yoghurt");
        vaniljYoghurtButton.setPreferredSize(PRODUCT_BIN_SIZE);
        vaniljYoghurtButton.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                selectProduct(yoghurt);
            }
        });
        panel.add(vaniljYoghurtButton);

        panel.setBounds(0, 0, 600, 600);
        frame.add(panel);
    }

    // När jag väljer produkt via knapp
    private void selectProduct(Product p) {
        lastClickProduct = p;
        inputProductName.setText(p.getName());
        inputCount.requestFocus();
        inputCount.selectAll();
    }

    // Skapar kvittodelen och scrollen
    private void createReceiptArea() {
        receipt = new JTextArea();
        receipt.setSize(400, 400);
        receipt.setLineWrap(true);               // jag vill hellre bryta rad än klippa text
        receipt.setEditable(false);
        receipt.setVisible(true);
        receipt.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));

        JScrollPane scroll = new JScrollPane(receipt);
        scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        scroll.setBounds(600, 0, 400, 1000);

        frame.add(scroll);
    }

    // Startar ett nytt kvitto och skriver headern
    public void startNewReceipt() {
        totalSumma = 0;
        lastClickProduct = null;
        receiptNo++;

        String header =
                String.format("%s\n", STORE_NAME) +
                String.format("Kvitto nr: %d\n", receiptNo) +
                String.format("Datum: %s\n", LocalDateTime.now().format(TS_FMT)) +
                "--------------------------------------------\n";

        receipt.setText(header);

        // Nollställer inmatningen
        inputProductName.setText("");
        inputCount.setText("1");
    }
}
