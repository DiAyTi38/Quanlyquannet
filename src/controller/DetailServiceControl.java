package controller;

import model.DetailService;
import dao.DetailServiceDAO;
import model.Session;
import model.Service;

import java.sql.*;
import java.util.ArrayList;
import java.math.BigDecimal;

public class DetailServiceControl {
    private final DetailServiceDAO ctdvDAO;
    private final ServiceControl serviceControl;
    private final SessionControl sessionControl;
    private final ArrayList<DetailService> dsCTDV;

    public DetailServiceControl(ServiceControl serviceControl, SessionControl sessionControl) {
        this.ctdvDAO = new DetailServiceDAO();
        this.dsCTDV = new ArrayList<>();
        this.serviceControl = serviceControl;
        this.sessionControl = sessionControl;
    }

    // Load
    public void loadAll(Connection conn) throws SQLException {
        dsCTDV.clear();
        dsCTDV.addAll(ctdvDAO.findAll(conn));
    }

    public ServiceControl getServiceControl() {
        return serviceControl;
    }

    // Lọc danh sách chi tiết dịch vụ theo phiên chơi cụ thể
    public ArrayList<DetailService> getServicesBySession(Session phien) {
        ArrayList<DetailService> ds = new ArrayList<>();
        for (DetailService d : dsCTDV) {
            if (d.getIdPhien() == phien.getIdPhien()) {
                ds.add(d);
            }
        }
        return ds;
    }

    public ArrayList<DetailService> getAll() {
        return dsCTDV;
    }

    // thêm dịch vụ vào phiên
    public BigDecimal addServiceToSession(
            Connection conn, Session phien, Service dv, int soLuong) throws SQLException {
        // Lưu chi tiết dịch vụ
        DetailService ctdv = new DetailService(
                0, phien.getIdPhien(), dv.getIdDv(), soLuong);

        int newId = ctdvDAO.insert(conn, ctdv);
        if (newId <= 0)
            return BigDecimal.ZERO;

        ctdv.setIdCtdv(newId);
        dsCTDV.add(ctdv);

        // Tính tiền dịch vụ
        BigDecimal tongTien = dv.getGia().multiply(BigDecimal.valueOf(soLuong));

        // Cập nhật session
        sessionControl.congTienDichVu(conn, phien, tongTien);

        return tongTien;
    }

    public BigDecimal addServiceToSession(
            Connection conn, Session phien, int idDv, int soLuong) throws SQLException {

        Service dv = serviceControl.findById(idDv);
        if (dv == null) {
            throw new IllegalArgumentException("Không tìm thấy dịch vụ!");
        }

        return addServiceToSession(conn, phien, dv, soLuong);
    }

    // Fix: Xoá theo idCtdv (để chính xác record) và trừ tiền
    public boolean removeServiceFromSession(Connection conn, Session phien, int idCtdv) throws SQLException {
        // Tìm record trong memory để biết giá tiền mà trừ
        DetailService target = null;
        for (DetailService d : dsCTDV) {
            if (d.getIdCtdv() == idCtdv && d.getIdPhien() == phien.getIdPhien()) {
                target = d;
                break;
            }
        }

        if (target == null)
            return false;

        // Xoá DB
        boolean ok = ctdvDAO.delete(conn, phien.getIdPhien(), idCtdv);

        if (ok) {
            dsCTDV.remove(target);

            // Trừ tiền
            Service dv = serviceControl.findById(target.getIdDv());
            if (dv != null) {
                BigDecimal tien = dv.getGia().multiply(BigDecimal.valueOf(target.getSoLuong()));
                sessionControl.congTienDichVu(conn, phien, tien.negate());
            }
        }

        return ok;
    }
}
