package ui;
import model.Customer;
import controller.CustomerControl;
import util.connectdb;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class CustomerUI extends JFrame{
    private JTable table;
    private DefaultTableModel tableModel;
    private CustomerControl customerControl;
    private Connection conn;

    public CustomerUI() {
        setTitle("Quản lý khách hàng");
        setSize(700, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        try {
            conn = connectdb.getConnection();
            customerControl = new CustomerControl();
            initMenu();
            initUI();
            loadData();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Không khởi tạo được Customer UI: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // ===== MENU =====
    private void initMenu() {
        JMenuBar menuBar = new JMenuBar();

        JMenuItem mComputer = new JMenuItem("Quản lý máy");
        JMenuItem mCustomer = new JMenuItem("Quản lý khách");
        JMenuItem mService = new JMenuItem("Quản lý dịch vụ");

        menuBar.add(mComputer);
        menuBar.add(mCustomer);
        menuBar.add(mService);
        setJMenuBar(menuBar);

        mComputer.addActionListener(e -> {
            try {
                new ManageComputerUI().setVisible(true);
                dispose();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Lỗi mở giao diện quản lý máy: " + ex.getMessage());
                ex.printStackTrace();
            }
        });
        mService.addActionListener(e -> {
            try {
                new ServiceUI().setVisible(true);
                this.dispose();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Lỗi mở giao diện dịch vụ: " + ex.getMessage());
                ex.printStackTrace();
            }
        });
    }

    private void initUI() {
        setLayout(new BorderLayout());

        // TABLE
        String[] cols = { "ID", "Tên khách", "Username", "Password", "Số dư" };
        tableModel = new DefaultTableModel(cols, 0);
        table = new JTable(tableModel);
        try {
            table.removeColumn(table.getColumnModel().getColumn(0));
        } catch (Exception ex) {
            // Nếu có lỗi khi xoá cột ID, báo lỗi ra console (nhưng không dừng app)
            ex.printStackTrace();
        }
        add(new JScrollPane(table), BorderLayout.CENTER);

        // BUTTON
        JPanel panelBtn = new JPanel();

        JButton btnAdd = new JButton("Thêm");
        JButton btnUpdate = new JButton("Sửa");
        JButton btnDelete = new JButton("Xoá");
        JButton btnFind = new JButton("Tìm");
        JButton btnReload = new JButton("Làm mới");

        panelBtn.add(btnAdd);
        panelBtn.add(btnUpdate);
        panelBtn.add(btnDelete);
        panelBtn.add(btnFind);
        panelBtn.add(btnReload);

        add(panelBtn, BorderLayout.SOUTH);

        // ===== EVENT =====

        btnReload.addActionListener(e -> {
            try {
                loadData();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Lỗi tải lại danh sách khách: " + ex.getMessage());
                ex.printStackTrace();
            }
        });
        btnAdd.addActionListener(e -> {
            try {
                addCustomer();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Lỗi thêm khách: " + ex.getMessage());
                ex.printStackTrace();
            }
        });
        btnDelete.addActionListener(e -> {
            try {
                deleteCustomer();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Lỗi xoá khách: " + ex.getMessage());
                ex.printStackTrace();
            }
        });
        btnUpdate.addActionListener(e -> {
            try {
                updateCustomer();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Lỗi cập nhật khách: " + ex.getMessage());
                ex.printStackTrace();
            }
        });
        btnFind.addActionListener(e -> {
            try {
                findCustomer();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Lỗi tìm khách: " + ex.getMessage());
                ex.printStackTrace();
            }
        });
    }

    // Load
    private void loadData() {
        try {
            customerControl.loadAll(conn);
            tableModel.setRowCount(0);

            for (Customer c : customerControl.getAll()) {
                tableModel.addRow(new Object[]{
                        c.getIdKhach(),
                        c.getTenKhach(),
                        c.getUserName(),
                        c.getPassWord(),
                        c.getSoDu()
                });
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi tải danh sách khách: " + e.getMessage());
            e.printStackTrace();
        }
    }
    // Thêm
    private void addCustomer() {
        AddCustomerDialog dialog = new AddCustomerDialog(this);
        dialog.setVisible(true);

        if (dialog.isSaved()) {
            try {
                Customer c = customerControl.addCustomer(
                        conn,
                        dialog.getTenInput(),
                        dialog.getUserInput(),
                        dialog.getPassInput(),
                        dialog.getSo_DuInput()
                );

                tableModel.addRow(new Object[]{
                        c.getIdKhach(),
                        c.getTenKhach(),
                        c.getUserName(),
                        c.getPassWord(),
                        c.getSoDu()
                });

            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Thêm khách thất bại: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    // Xoá
    private void deleteCustomer() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Chọn khách cần xoá");
            return;
        }

        int modelRow = table.convertRowIndexToModel(row);
        if (modelRow < 0 || modelRow >= customerControl.getAll().size()) {
            JOptionPane.showMessageDialog(this, "Dữ liệu khách hàng bị lỗi hoặc vượt ngoài phạm vi danh sách!");
            return;
        }
        Customer c = customerControl.getAll().get(modelRow);

        if (JOptionPane.showConfirmDialog(this,
                "Xoá khách " + c.getTenKhach() + "?",
                "Xác nhận", JOptionPane.YES_NO_OPTION)
                == JOptionPane.YES_OPTION) {

            try {
                customerControl.deleteCustomer(conn, c);
                tableModel.removeRow(modelRow);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Không xoá được khách: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    // Sửa
    private void updateCustomer() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Chọn khách cần sửa");
            return;
        }

        int modelRow = table.convertRowIndexToModel(row);
        if (modelRow < 0 || modelRow >= customerControl.getAll().size()) {
            JOptionPane.showMessageDialog(this, "Dữ liệu khách hàng bị lỗi hoặc vượt ngoài phạm vi danh sách!");
            return;
        }
        Customer c = customerControl.getAll().get(modelRow);

        AddCustomerDialog dialog = new AddCustomerDialog(this);
        dialog.setTen(c.getTenKhach());
        dialog.setUser(c.getUserName());
        dialog.setPass(c.getPassWord());
        dialog.setSoDu(c.getSoDu());
        dialog.setVisible(true);

        if (dialog.isSaved()) {
            try {
                customerControl.updateCustomer(
                        conn,
                        c,
                        dialog.getTenInput(),
                        dialog.getUserInput(),
                        dialog.getPassInput(),
                        dialog.getSo_DuInput()
                );

                // Cập nhật object trong bộ nhớ
                c.setTenKhach(dialog.getTenInput());
                c.setUserName(dialog.getUserInput());
                c.setPassWord(dialog.getPassInput());
                c.setSoDu(dialog.getSo_DuInput());

                // Cập nhật dữ liệu mới sau khi sửa
                tableModel.setValueAt(c.getTenKhach(), modelRow, 1);
                tableModel.setValueAt(c.getUserName(), modelRow, 2);
                tableModel.setValueAt(c.getPassWord(), modelRow, 3);
                tableModel.setValueAt(c.getSoDu(), modelRow, 4);

            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Cập nhật thất bại: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    // Tìm
    private void findCustomer() {
        String key = JOptionPane.showInputDialog(this, "Nhập tên hoặc username");
        if (key == null || key.trim().isEmpty()) return;

        try {
            tableModel.setRowCount(0);
            for (Customer c : customerControl.find(conn, key)) {
                tableModel.addRow(new Object[]{
                        c.getIdKhach(),
                        c.getTenKhach(),
                        c.getUserName(),
                        c.getPassWord(),
                        c.getSoDu()
                });
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Không tìm được khách: " + e.getMessage());
            e.printStackTrace();
        }
    }

}
