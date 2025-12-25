package ui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.sql.*;
import util.connectdb;
import model.Service;
import controller.ServiceControl;

public class ServiceUI extends JFrame {
    private JTable tableService;
    private DefaultTableModel tableModelService;
    private Connection conn;
    private JButton btnAdd, btnUpdate, btnDelete, btnReload;

    private ServiceControl serviceControl;

    public ServiceUI() {
        setTitle("Quản lý dịch vụ");
        setSize(600, 350);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        connectDB();
        serviceControl = new ServiceControl();

        initMenuBar();
        initUI();

        try {
            serviceControl.loadAll(conn); // <-- nạp dữ liệu từ DB vào controller
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi khi nạp dữ liệu từ DB!");
        }
        loadData();
    }

    private void connectDB() {
        try {
            conn = connectdb.getConnection();
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Không kết nối được database!");
        }
    }

    private void initMenuBar() {
        JMenuBar menuBar = new JMenuBar();

        JMenuItem itemManageComputer = new JMenuItem("Quản lý máy");
        JMenuItem itemCustomer = new JMenuItem("Quản lý khách hàng");
        JMenuItem itemService = new JMenuItem("Quản lý dịch vụ");

        menuBar.add(itemManageComputer);
        menuBar.add(itemCustomer);
        menuBar.add(itemService);

        setJMenuBar(menuBar);

        // Chuyển UI
        itemManageComputer.addActionListener(e -> new ManageComputerUI().setVisible(true));
        itemCustomer.addActionListener(e -> new CustomerUI().setVisible(true));
    }

    private void initUI() {
        setLayout(new BorderLayout());

        // Bảng hiển thị dịch vụ
        String[] columns = { "id_dv", "Tên dịch vụ", "Giá" };
        tableModelService = new DefaultTableModel(columns, 0);
        tableService = new JTable(tableModelService);
        tableService.removeColumn(tableService.getColumnModel().getColumn(0)); // ẩn id_dv

        add(new JScrollPane(tableService), BorderLayout.CENTER);

        // Panel nút bấm
        JPanel panelButton = new JPanel(new FlowLayout());
        btnAdd = new JButton("Thêm");
        btnUpdate = new JButton("Sửa");
        btnDelete = new JButton("Xoá");
        btnReload = new JButton("Làm mới");

        panelButton.add(btnAdd);
        panelButton.add(btnUpdate);
        panelButton.add(btnDelete);
        panelButton.add(btnReload);

        add(panelButton, BorderLayout.SOUTH);

        // Xử lý sự kiện
        btnReload.addActionListener(e -> loadData());

        btnAdd.addActionListener(e -> {
            String name = JOptionPane.showInputDialog(this, "Tên dịch vụ:");
            String priceStr = JOptionPane.showInputDialog(this, "Giá dịch vụ:");
            if (name != null && priceStr != null) {
                try {
                    BigDecimal price = new BigDecimal(priceStr);

                    // Gọi trực tiếp serviceControl, không tạo sv bên ngoài
                    Service sv = serviceControl.addService(conn, name, price);
                    if (sv != null) {
                        loadData(); // Hàm load lại dữ liệu lên table
                        JOptionPane.showMessageDialog(this, "Thêm dịch vụ thành công!");
                    } else {
                        JOptionPane.showMessageDialog(this, "Thêm dịch vụ thất bại!");
                    }

                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(this, "Giá nhập không hợp lệ!");
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(this, "Lỗi cơ sở dữ liệu!");
                }
            }
        });

        btnUpdate.addActionListener(e -> {
            int row = tableService.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn dịch vụ!");
                return;
            }
            int id = (int) tableModelService.getValueAt(row, 0);
            String name = JOptionPane.showInputDialog(this, "Tên dịch vụ:", tableModelService.getValueAt(row, 1));
            String priceStr = JOptionPane.showInputDialog(this, "Giá dịch vụ:", tableModelService.getValueAt(row, 2).toString());

            if (name != null && priceStr != null) {
                try {
                    BigDecimal price = new BigDecimal(priceStr);
                    Service sv = new Service(id, name, price);
                    serviceControl.updateService(conn, sv);
                    loadData();
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Giá nhập không hợp lệ!");
                }
            }
        });

        btnDelete.addActionListener(e -> {
            int row = tableService.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn dịch vụ!");
                return;
            }

            // Lấy id từ bảng
            int id = (int) tableModelService.getValueAt(row, 0);

            // Lấy đối tượng Service từ Controller
            Service dv = serviceControl.findById(id);
            if (dv == null) {
                JOptionPane.showMessageDialog(this, "Dịch vụ không tồn tại!");
                return;
            }

            // Xác nhận xoá
            int confirm = JOptionPane.showConfirmDialog(
                    this,
                    "Bạn có chắc muốn xoá dịch vụ \"" + dv.getTenDv() + "\"?",
                    "Xác nhận",
                    JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {
                try {
                    boolean ok = serviceControl.deleteService(conn, dv);
                    if (ok) {
                        tableModelService.removeRow(row); // xoá row khỏi JTable
                        JOptionPane.showMessageDialog(this, "Xoá thành công!");
                    } else {
                        JOptionPane.showMessageDialog(this, "Xoá thất bại!");
                    }
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(this, "Lỗi khi xoá dịch vụ!");
                }
            }
        });

    }

    private void loadData() {
        try {
            serviceControl.loadAll(conn);
            tableModelService.setRowCount(0);
            for (Service sv : serviceControl.getAll()) {
                tableModelService.addRow(new Object[] { sv.getIdDv(), sv.getTenDv(), sv.getGia() });
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
