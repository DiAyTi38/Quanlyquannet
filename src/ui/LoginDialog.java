package ui;

import javax.swing.*;
import model.Customer;
import util.connectdb;
import controller.LoginController;
import java.sql.*;
import java.awt.*;
import controller.MainController;

public class LoginDialog extends JDialog {
    private JTextField txtUserName;
    private JPasswordField txtPassword;
    private JButton btnLogin;
    private Customer loggedCustomer;

    private LoginController loginController;
    private Connection conn;

    public LoginDialog(Frame parent, Connection conn) {
        super(parent, "Đăng nhập khách hàng", true); // true = modal
        this.conn = conn;
        this.loginController = new LoginController();
        initUI();
    }

    public Customer getLoggedCustomer() {
        return loggedCustomer;
    }

    private void initUI() {
        setSize(300, 200);
        setLocationRelativeTo(getParent());
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

        txtUserName = new JTextField(15);
        txtPassword = new JPasswordField(15);
        btnLogin = new JButton("Đăng nhập");

        JPanel panel = new JPanel(new GridLayout(3, 2, 5, 5));
        panel.add(new JLabel("Tài khoản: "));
        panel.add(txtUserName);
        panel.add(new JLabel("Mật khẩu: "));
        panel.add(txtPassword);
        panel.add(new JLabel());
        panel.add(btnLogin);

        add(panel);
        addEvents();
    }

    private void addEvents() {
        btnLogin.addActionListener(e -> {
            try {
                String username = txtUserName.getText();
                String password = new String(txtPassword.getPassword());

                if (username.isEmpty() || password.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Không được để trống!");
                    return;
                }

                Customer c;
                try {
                    c = loginController.login(conn, username, password);
                    if(c != null) {
                        this.loggedCustomer = c;  
                        dispose();                
                    } else {
                        JOptionPane.showMessageDialog(this, "Sai tài khoản hoặc mật khẩu!");
                    }
                } catch (SQLException sqlEx) {
                    sqlEx.printStackTrace();
                    JOptionPane.showMessageDialog(this, "Lỗi truy vấn SQL: " + sqlEx.getMessage());
                    return;
                } catch (Exception otherEx) {
                    otherEx.printStackTrace();
                    JOptionPane.showMessageDialog(this, "Lỗi trong đăng nhập: " + otherEx.getMessage());
                    return;
                }

                if (c != null) {
                    loggedCustomer = c; // Lưu khách đăng nhập
                    dispose(); // đóng dialog
                } else {
                    JOptionPane.showMessageDialog(this, "Sai tài khoản hoặc mật khẩu!");
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Lỗi hệ thống: " + ex.getMessage());
            }
        });
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Connection conn;
            try {
                conn = connectdb.getConnection();
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(null, "Không thể kết nối tới cơ sở dữ liệu: " + ex.getMessage());
                return;
            }
            LoginDialog dialog = new LoginDialog(null, conn); // null = không có parent frame
            dialog.setVisible(true);

            Customer c = dialog.getLoggedCustomer();
            if (c != null) {
                try {
                    MainController mainController = new MainController(conn, c);
                    new MainFrame(mainController).setVisible(true);
                } catch (Exception ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(null, "Lỗi khởi tạo MainFrame: " + ex.getMessage());
                }
            } else {
                JOptionPane.showMessageDialog(null, "Chưa đăng nhập khách!");
            }
        });
    }
}
