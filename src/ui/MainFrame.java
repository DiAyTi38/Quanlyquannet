package ui;
import controller.MainController;
import model.Customer;
import model.Service;

import javax.swing.*;
import java.awt.*;
import java.sql.Connection;

public class MainFrame extends JFrame {
    private final MainController controller;
    private final Customer currentCustomer;
    private final Connection conn;

    private JLabel lblWelcome;
    private JLabel lblBalance;

    private JComboBox<Service> cbService;
    private JButton btnBuy;
    private JButton btnLogout;

    public MainFrame(MainController controller) {
        if (controller == null) {
            throw new IllegalArgumentException("MainController is null");
        }
        this.controller = controller;
        this.currentCustomer = controller.getCurrentCustomer();
        this.conn = controller.getConnection();

        initUI();
        loadData();
    }

    private void initUI() {
        setTitle("Máy trạm");
        setSize(400, 250);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        lblWelcome = new JLabel();
        lblBalance = new JLabel();

        cbService = new JComboBox<>();
        btnBuy = new JButton("Mua dịch vụ");
        btnLogout = new JButton("Đăng xuất");

        JPanel topPanel = new JPanel(new GridLayout(2,1));
        topPanel.add(lblWelcome);
        topPanel.add(lblBalance);

        JPanel centerPanel = new JPanel();
        centerPanel.add(cbService);
        centerPanel.add(btnBuy);

        JPanel bottomPanel = new JPanel();
        bottomPanel.add(btnLogout);

        setLayout(new BorderLayout());
        add(topPanel, BorderLayout.NORTH);
        add(centerPanel, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);

        addEvents();
    }

    private void loadData() {
        Customer c = controller.getCurrentCustomer();

        lblWelcome.setText("Xin chào: " + c.getTenKhach());
        lblBalance.setText("Số dư: " + c.getSoDu() + " VNĐ");

        try {
            cbService.removeAllItems();
            for (Service dv : controller.getAllServices()) {
                cbService.addItem(dv);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Không tải được dịch vụ!");
        }
    }

    private void addEvents() {
        btnBuy.addActionListener(e -> {
            Service dv = (Service) cbService.getSelectedItem();
            if (dv == null) return;

            try {
                if (controller.buyService(dv)) {
                    lblBalance.setText(
                        "Số dư: " +
                        controller.getCurrentCustomer().getSoDu() + " VNĐ"
                    );
                    JOptionPane.showMessageDialog(this, "Mua hàng thành công!");
                } else {
                    JOptionPane.showMessageDialog(this, "Không đủ số dư");
                } 
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Lỗi hệ thống!");
            }
        });

        btnLogout.addActionListener(e -> {
        int confirm = JOptionPane.showConfirmDialog(this, "Bạn có chắc chắn muốn đăng xuất?", "Xác nhận", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;

        try {
            controller.logout();
            lblBalance.setText("Số dư: " + controller.getCurrentCustomer().getSoDu());
            JOptionPane.showMessageDialog(this, "Đăng xuất thành công!");
            dispose(); // đóng MainFrame

            // Mở lại LoginDialog modal, truyền 'this' làm parent
            LoginDialog loginDialog = new LoginDialog(this, conn); // nếu không có frame cha, dùng null
            loginDialog.setVisible(true);

            // Lấy khách đã đăng nhập
            Customer loggedCustomer = loginDialog.getLoggedCustomer();
            if (loggedCustomer != null) {
                MainController newController = new MainController(conn, loggedCustomer);
                new MainFrame(newController).setVisible(true);
            } else {
                JOptionPane.showMessageDialog(null, "Chưa đăng nhập khách!");
            }
        } catch (IllegalStateException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi hệ thống khi đăng xuất");
        }
    });

    }
}
