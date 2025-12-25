package ui;
import javax.swing.*;
import java.awt.*;
import java.math.*;

public class AddCustomerDialog extends JDialog {
    private JTextField txtTen, txtUser, txtSo_Du;
    private JPasswordField txtPass;
    private JButton btnSave, btnCancel;
    private boolean saved = false; // biến để biết người dùng có bấm lưu ko

    public AddCustomerDialog(JFrame parent) {
        super(parent, "Cập nhật thông tin", true);

        setLayout(new BorderLayout(10,10));
        JPanel form = new JPanel(new GridLayout(4,2,10,10));
        form.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));

        form.add(new JLabel("Nhập tên khách: "));
        txtTen = new JTextField();
        form.add(txtTen);

        form.add(new JLabel("Nhập username: "));
        txtUser = new JTextField();
        form.add(txtUser);

        form.add(new JLabel("Nhập password: "));
        txtPass = new JPasswordField();
        form.add(txtPass);

        form.add(new JLabel("Nhập số dư: "));
        txtSo_Du = new JTextField();
        form.add(txtSo_Du);

        JPanel buttonPanel = new JPanel(new FlowLayout());
        btnSave = new JButton("Lưu");
        btnCancel = new JButton("Huỷ");

        buttonPanel.add(btnSave);
        buttonPanel.add(btnCancel);

        add(form, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        btnSave.addActionListener(e -> {
            // Validation
            String ten = txtTen.getText().trim();
            String user = txtUser.getText().trim();
            String pass = new String(txtPass.getPassword());
            String soDuText = txtSo_Du.getText().trim();

            StringBuilder errorMsg = new StringBuilder();
            if (ten.isEmpty()) {
                errorMsg.append("Tên khách không được bỏ trống.\n");
            }
            if (user.isEmpty()) {
                errorMsg.append("Username không được bỏ trống.\n");
            }
            if (pass.isEmpty()) {
                errorMsg.append("Password không được bỏ trống.\n");
            }
            if (soDuText.isEmpty()) {
                errorMsg.append("Số dư không được bỏ trống.\n");
            } else {
                try {
                    new BigDecimal(soDuText);
                } catch (NumberFormatException ex) {
                    errorMsg.append("Số dư phải là số hợp lệ.\n");
                }
            }

            if (errorMsg.length() > 0) {
                JOptionPane.showMessageDialog(this, errorMsg.toString(), "Lỗi nhập liệu", JOptionPane.ERROR_MESSAGE);
                saved = false;
            } else {
                saved = true; // Đánh dấu lưu dữ liệu
                dispose();
            }
        });

        btnCancel.addActionListener(e -> {
            saved = false;
            dispose();
        });

        pack();
        setLocationRelativeTo(parent);
    }

    public boolean isSaved() {
        return saved;
    }

    public String getTenInput() {
        return txtTen.getText();
    }
    public String getUserInput() {
        return txtUser.getText();
    }
    public String getPassInput() {
        return new String(txtPass.getPassword());
    }
    public BigDecimal getSo_DuInput() {
        try {
            return new BigDecimal(txtSo_Du.getText().trim());
        } catch (NumberFormatException e) {
            return BigDecimal.ZERO;
        }
    }
    public void setTen(String ten) {
        txtTen.setText(ten);
    }

    public void setUser(String user) {
        txtUser.setText(user);
    }

    public void setPass(String pass) {
        txtPass.setText(pass);
    }

    public void setSoDu(BigDecimal soDu) {
        txtSo_Du.setText(soDu.toString());
    }

}
