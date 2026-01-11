package ui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.util.List;
import java.math.BigDecimal;

import controller.DetailServiceControl;
import controller.ServiceControl;
import model.DetailService;
import model.Service;
import model.Session;
import util.connectdb;

public class DetailServiceUI extends JFrame {

    private JTable tableService; // danh sách dịch vụ
    private JTable tableDetail; // chi tiết dịch vụ của phiên
    private DefaultTableModel tableModelService;
    private DefaultTableModel tableModelDetail;
    private JButton btnAddService, btnRemoveService;
    private Connection conn;

    private ServiceControl serviceControl;
    private DetailServiceControl detailServiceControl;
    private Session currentSession;

    public DetailServiceUI(Session session) {
        setTitle("Chi tiết dịch vụ");
        setSize(700, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        currentSession = session;
        connectDB();
        serviceControl = new ServiceControl();
        // SessionControl để cập nhật tiền
        controller.SessionControl sessionControl = new controller.SessionControl();
        detailServiceControl = new DetailServiceControl(serviceControl, sessionControl);

        try {
            serviceControl.loadAll(conn);
            detailServiceControl.loadAll(conn);
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi tải dữ liệu dịch vụ!");
        }

        initUI();
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

    private void initUI() {
        setLayout(new BorderLayout());

        // Panel chính chứa 2 bảng
        JPanel panelTables = new JPanel(new GridLayout(1, 2, 10, 10));
        panelTables.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Bảng danh sách dịch vụ
        String[] colsService = { "ID", "Tên dịch vụ", "Giá" };
        tableModelService = new DefaultTableModel(colsService, 0);
        tableService = new JTable(tableModelService);
        // !!! Quan trọng: Không nên ẩn cột ID khi cần lấy giá trị ID từ TableModel !!!
        // tableService.removeColumn(tableService.getColumnModel().getColumn(0)); // ẩn
        // cột ID
        panelTables.add(new JScrollPane(tableService));

        // Bảng chi tiết dịch vụ của phiên
        String[] colsDetail = { "ID CTDV", "Tên dịch vụ", "Số lượng" };
        tableModelDetail = new DefaultTableModel(colsDetail, 0);
        tableDetail = new JTable(tableModelDetail);
        // !!! Quan trọng: Không nên ẩn cột ID khi cần lấy giá trị ID từ TableModel !!!
        // tableDetail.removeColumn(tableDetail.getColumnModel().getColumn(0)); // ẩn
        // cột ID
        panelTables.add(new JScrollPane(tableDetail));

        add(panelTables, BorderLayout.CENTER);

        // Panel nút giữa 2 bảng
        JPanel panelButtons = new JPanel(new FlowLayout());
        btnAddService = new JButton("Thêm >>");
        btnRemoveService = new JButton("<< Xóa");
        panelButtons.add(btnAddService);
        panelButtons.add(btnRemoveService);
        add(panelButtons, BorderLayout.SOUTH);

        // Xử lý sự kiện thêm dịch vụ
        btnAddService.addActionListener(e -> {
            int row = tableService.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn dịch vụ!");
                return;
            }

            int modelRow = tableService.convertRowIndexToModel(row);
            Object idObj = tableModelService.getValueAt(modelRow, 0);
            if (idObj == null) {
                JOptionPane.showMessageDialog(this, "Không lấy được ID dịch vụ!");
                return;
            }
            int idDv = -1;
            try {
                idDv = Integer.parseInt(idObj.toString());
            } catch (NumberFormatException nfe) {
                JOptionPane.showMessageDialog(this, "ID dịch vụ không hợp lệ!");
                return;
            }

            String soLuongStr = JOptionPane.showInputDialog(this, "Số lượng:");
            if (soLuongStr == null)
                return; // nhấn cancel

            try {
                int soLuong = Integer.parseInt(soLuongStr);
                Service sv = serviceControl.findById(idDv);
                if (sv == null) {
                    JOptionPane.showMessageDialog(this, "Dịch vụ không tồn tại!");
                    return;
                }

                BigDecimal tien = detailServiceControl.addServiceToSession(conn, currentSession, sv, soLuong);
                loadData();
                JOptionPane.showMessageDialog(this, "Thêm dịch vụ thành công!");
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Số lượng không hợp lệ!");
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Lỗi khi thêm dịch vụ!" + ex.getMessage());
            }
        });

        // Xử lý sự kiện xóa dịch vụ
        btnRemoveService.addActionListener(e -> {
            int row = tableDetail.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn dịch vụ cần xóa!");
                return;
            }

            int modelRow = tableDetail.convertRowIndexToModel(row);
            Object idObj = tableModelDetail.getValueAt(modelRow, 0);
            if (idObj == null) {
                JOptionPane.showMessageDialog(this, "Không lấy được ID dịch vụ để xóa!");
                return;
            }
            int idDv = -1;
            try {
                idDv = Integer.parseInt(idObj.toString());
            } catch (NumberFormatException nfe) {
                JOptionPane.showMessageDialog(this, "ID dịch vụ xóa không hợp lệ!");
                return;
            }

            try {
                detailServiceControl.removeServiceFromSession(conn, currentSession, idDv); // idDv ở đây thực chất là
                                                                                           // idCtdv
                loadData();
                JOptionPane.showMessageDialog(this, "Xóa dịch vụ thành công!");
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Lỗi khi xóa dịch vụ!");
            }
        });
    }

    // Load dữ liệu 2 bảng
    private void loadData() {
        // Lưu ý: Nên load lại cả 2 table (service và detail)
        tableModelService.setRowCount(0);
        tableModelDetail.setRowCount(0);
        try {
            // Lấy danh sách dịch vụ (nên có)
            List<Service> dsService = serviceControl.getAll();
            for (Service sv : dsService) {
                // Giữ lại ID để các thao tác thêm/select không lỗi vì mất ID
                tableModelService.addRow(new Object[] {
                        sv.getIdDv(), sv.getTenDv(), sv.getGia()
                });
            }

            // Lấy danh sách chi tiết dịch vụ của phiên
            List<DetailService> ds = detailServiceControl.getServicesBySession(currentSession);

            for (DetailService d : ds) {
                // Lấy Service từ idDv
                Service dv = detailServiceControl.getServiceControl().findById(d.getIdDv());
                String tenDv = (dv != null) ? dv.getTenDv() : "Unknown";

                // Thêm idCtdv vào cột 0 thay vì idDv
                tableModelDetail.addRow(new Object[] { d.getIdCtdv(), tenDv, d.getSoLuong() });
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

}
