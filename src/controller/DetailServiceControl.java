package controller;
import model.DetailService;
import dao.DetailServiceDAO;
import model.Session;
import model.Service;

import java.sql.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.math.BigDecimal;

public class DetailServiceControl {
    private final DetailServiceDAO ctdvDAO;
    private final ServiceControl serviceControl;
    private final ArrayList<DetailService> dsCTDV;

    public DetailServiceControl(ServiceControl serviceControl) {
        this.ctdvDAO = new DetailServiceDAO();
        this.dsCTDV = new ArrayList<>();
        this.serviceControl = serviceControl;
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
        Connection conn, Session phien, Service dv, int soLuong
    ) throws SQLException {
        // Lưu chi tiết dịch vụ
        DetailService ctdv = new DetailService(
            0, phien.getIdPhien(), dv.getIdDv(), soLuong
        );

        int newId = ctdvDAO.insert(conn, ctdv);
        if (newId <= 0) return BigDecimal.ZERO;

        ctdv.setIdCtdv(newId);
        dsCTDV.add(ctdv);

        // Tính tiền dịch vụ
        return dv.getGia().multiply(BigDecimal.valueOf(soLuong));
    }

    public BigDecimal addServiceToSession(
        Connection conn, Session phien, int idDv, int soLuong
        ) throws SQLException {

        Service dv = serviceControl.findById(idDv);
        if (dv == null) {
            throw new IllegalArgumentException("Không tìm thấy dịch vụ!");
        }

        return addServiceToSession(conn, phien, dv, soLuong);
    }

    public boolean removeServiceFromSession(Connection conn, Session phien, int idDv) throws SQLException {
        boolean ok = ctdvDAO.delete(conn, phien.getIdPhien(), idDv);

        if (ok) {
            // Cập nhật dsCTDV trong bộ nhớ
            Iterator<DetailService> iter = dsCTDV.iterator();
            while (iter.hasNext()) {
                DetailService d = iter.next();
                if (d.getIdPhien() == phien.getIdPhien() && d.getIdDv() == idDv) {
                    iter.remove();
                }
            }
        }

        return ok;
    }
}
