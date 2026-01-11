package ui;

import model.Computer;
import model.Customer;
import controller.ComputerControl;
import controller.MainController;
import util.connectdb;
import controller.SessionControl;
import model.Session;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class ManageComputerUI extends JFrame {
    private JTable tbeMay;
    private Connection conn;
    private ComputerControl computerControl;
    private DefaultTableModel tableModelComputer;
    private SessionControl sessionControl;

    public ManageComputerUI() {
        setTitle("Quản lý máy");
        setSize(700, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        try {
            conn = connectdb.getConnection();
            computerControl = new ComputerControl();
            sessionControl = new SessionControl();

            initMenu();
            initUI();
            loadData();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi khởi tạo chương trình: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    // Menu
    private void initMenu() {
        JMenuBar menuBar = new JMenuBar();

        JMenuItem itemManageComputer = new JMenuItem("Quản lý máy");
        JMenuItem itemCustomer = new JMenuItem("Quản lý khách hàng");
        JMenuItem itemService = new JMenuItem("Quản lý dịch vụ");

        menuBar.add(itemManageComputer);
        menuBar.add(itemCustomer);
        menuBar.add(itemService);

        setJMenuBar(menuBar);

        itemCustomer.addActionListener(e -> {
            try {
                new CustomerUI().setVisible(true);
                this.dispose();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Lỗi mở giao diện khách hàng: " + ex.getMessage());
                ex.printStackTrace();
            }
        });

        itemService.addActionListener(e -> {
            try {
                new ServiceUI().setVisible(true);
                this.dispose();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Lỗi mở giao diện dịch vụ: " + ex.getMessage());
                ex.printStackTrace();
            }
        });
    }

    // UI
    private void initUI() {
        setLayout(new BorderLayout());
        // Panel chứa các ảnh, 2 hàng, 5 cột
        JPanel panelImage = new JPanel(new GridLayout(2, 5, 10, 10));
        panelImage.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Đường dẫn ảnh trong resources
        String[] imagePaths = {
                "/images/computer-icon.jpg", "/images/computer-icon.jpg",
                "/images/computer-icon.jpg", "/images/computer-icon.jpg",
                "/images/computer-icon.jpg", "/images/computer-icon.jpg",
                "/images/computer-icon.jpg", "/images/computer-icon.jpg",
                "/images/computer-icon.jpg", "/images/computer-icon.jpg",
        };

        int width = 80;
        int height = 80;
        for (String path : imagePaths) {
            try {
                java.net.URL imgUrl = getClass().getResource(path);
                if (imgUrl == null)
                    throw new Exception("Không tìm thấy ảnh: " + path);
                ImageIcon originalIcon = new ImageIcon(imgUrl);
                Image img = originalIcon.getImage();
                Image scaledImg = img.getScaledInstance(width, height, Image.SCALE_SMOOTH);
                ImageIcon scaledIcon = new ImageIcon(scaledImg);

                JLabel label = new JLabel(scaledIcon);
                label.setHorizontalAlignment(JLabel.CENTER);
                label.setVerticalAlignment(JLabel.CENTER);

                panelImage.add(label);
            } catch (Exception ex) {
                JLabel errorLabel = new JLabel("Ảnh lỗi");
                errorLabel.setHorizontalAlignment(JLabel.CENTER);
                errorLabel.setVerticalAlignment(JLabel.CENTER);
                errorLabel.setForeground(Color.RED);
                panelImage.add(errorLabel);
                System.err.println("Lỗi tải ảnh: " + path);
                ex.printStackTrace();
            }
        }

        add(panelImage, BorderLayout.NORTH);
        String[] columns = {
                "ID", "Tên máy", "Loại máy", "Giá / giờ", "Trạng thái"
        };
        tableModelComputer = new DefaultTableModel(columns, 0);
        tbeMay = new JTable(tableModelComputer);

        // Ẩn cột ID, kiểm tra trước khi xoá tránh lỗi out-of-bounds
        if (tbeMay.getColumnCount() > 0) {
            try {
                tbeMay.removeColumn(tbeMay.getColumnModel().getColumn(0));
            } catch (Exception ex) {
                System.err.println("Lỗi ẩn cột ID: " + ex.getMessage());
                ex.printStackTrace();
            }
        }

        add(new JScrollPane(tbeMay), BorderLayout.CENTER);

        // Panel nút
        JPanel panelBtn = new JPanel();

        JButton btnReload = new JButton("Làm mới");
        JButton btnBaoTri = new JButton("Bảo trì");
        JButton btnMoMay = new JButton("Mở máy");
        JButton btnStart = new JButton("Bắt đầu chơi");

        panelBtn.add(btnReload);
        panelBtn.add(btnBaoTri);
        panelBtn.add(btnMoMay);
        panelBtn.add(btnStart);

        add(panelBtn, BorderLayout.SOUTH);

        // Sự kiện
        btnReload.addActionListener(e -> loadData());
        btnBaoTri.addActionListener(e -> doiTrangThai("bao_tri"));
        btnMoMay.addActionListener(e -> doiTrangThai("trong"));

        // [NEW] Nút Dịch vụ
        JButton btnDichVu = new JButton("Dịch vụ");
        panelBtn.add(btnDichVu);

        btnDichVu.addActionListener(e -> {
            int row = tbeMay.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn máy đang sử dụng!");
                return;
            }

            int modelRow = tbeMay.convertRowIndexToModel(row);
            Object idMayObj = tableModelComputer.getValueAt(modelRow, 0);
            if (idMayObj == null)
                return;

            try {
                int idMay = Integer.parseInt(idMayObj.toString());

                // Kiểm tra xem máy có đang online không
                Computer c = computerControl.findById(conn, idMay);
                if (c == null || !c.isDangSuDung()) {
                    JOptionPane.showMessageDialog(this, "Máy này không đang hoạt động!");
                    return;
                }

                // Tìm session đang chơi
                Session currentSession = sessionControl.findDangChoiByMay(conn, idMay);
                if (currentSession == null) {
                    JOptionPane.showMessageDialog(this, "Không tìm thấy phiên chơi!");
                    return;
                }

                // Mở UI chi tiết dịch vụ
                new DetailServiceUI(currentSession).setVisible(true);

            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Lỗi khi mở dịch vụ: " + ex.getMessage());
            }
        });

        btnStart.addActionListener(e -> {
            int row = tbeMay.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn máy");
                return;
            }

            int modelRow = tbeMay.convertRowIndexToModel(row);
            Object idMayObj = tableModelComputer.getValueAt(modelRow, 0);
            if (idMayObj == null) {
                JOptionPane.showMessageDialog(this, "Không lấy được ID máy, hãy tải lại danh sách.");
                return;
            }

            // [NEW] Check maintenance in UI
            Object statusObj = tableModelComputer.getValueAt(modelRow, 4); // "Trạng thái" column
            if (statusObj != null && "Bảo trì".equals(statusObj.toString())) {
                JOptionPane.showMessageDialog(this, "Máy đang bảo trì, không thể sử dụng!");
                return;
            }

            int idMay;
            try {
                idMay = Integer.parseInt(idMayObj.toString());
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "ID máy không hợp lệ: " + idMayObj);
                return;
            }

            // Mở LoginDialog modal để khách đăng nhập
            LoginDialog loginDialog = new LoginDialog(this, conn); // "this" là parent
            loginDialog.setModal(true);
            loginDialog.setVisible(true);

            // Lấy khách sau khi dialog đóng
            Customer customer = loginDialog.getLoggedCustomer();
            if (customer == null) {
                JOptionPane.showMessageDialog(this, "Chưa đăng nhập khách!");
                return;
            }

            int idKhach = customer.getIdKhach();

            try {
                // Bắt đầu session cho khách
                Session dangChoi = sessionControl.startSession(conn, idMay, idKhach);

                if (dangChoi != null) {
                    // Cập nhật trạng thái máy
                    computerControl.updateTrangThai(conn, idMay, "dang_su_dung");
                    loadData();

                    // Tạo MainController và MainFrame khách ngay lập tức
                    MainController mainController = new MainController(conn, customer);
                    MainFrame mainFrame = new MainFrame(mainController);
                    mainFrame.setVisible(true); // hiển thị MainFrame khách

                    JOptionPane.showMessageDialog(this,
                            "Khách " + customer.getUserName() + " đã đăng nhập và bắt đầu phiên chơi.");
                } else {
                    JOptionPane.showMessageDialog(this, "Máy đã có khách!");
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Không thể bắt đầu phiên chơi: " + ex.getMessage());
                ex.printStackTrace();
            }
        });
    }

    // Load data
    private void loadData() {
        try {
            computerControl.loadAll(conn);
            tableModelComputer.setRowCount(0);

            for (Computer c : computerControl.getAll()) {
                tableModelComputer.addRow(new Object[] {
                        c.getIdMay(),
                        c.getTenMay(),
                        c.getLoaiMay(),
                        c.getGiaGio(),
                        c.getTrangThaiHienThi()
                });
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi tải danh sách máy: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    // Đổi trạng thái
    private void doiTrangThai(String trangThaiMoi) {
        int row = tbeMay.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn máy");
            return;
        }

        int modelRow = tbeMay.convertRowIndexToModel(row);
        Object idMayObj = tableModelComputer.getValueAt(modelRow, 0);
        if (idMayObj == null) {
            JOptionPane.showMessageDialog(this, "Không lấy được ID máy.");
            return;
        }
        int idMay;
        try {
            idMay = Integer.parseInt(idMayObj.toString());
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "ID máy không hợp lệ: " + idMayObj);
            return;
        }

        try {
            Computer c = computerControl.findById(conn, idMay);
            if (c == null) {
                JOptionPane.showMessageDialog(this, "Không tìm thấy thông tin máy!");
                return;
            }

            // Fix lỗi: Đang chơi thì không được bảo trì
            if ("bao_tri".equals(trangThaiMoi) && c.isDangSuDung()) {
                JOptionPane.showMessageDialog(this, "Máy đang có khách, không thể bảo trì!");
                return;
            }

            computerControl.updateTrangThai(conn, idMay, trangThaiMoi);
            loadData();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Không cập nhật được trạng thái: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                new ManageComputerUI().setVisible(true);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(null, "Lỗi khởi động ứng dụng: " + ex.getMessage());
                ex.printStackTrace();
            }
        });
    }
}
