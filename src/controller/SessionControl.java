package controller;
import dao.SessionDAO;
import model.Session;
import model.Computer;

import java.sql.*;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.math.*;

public class SessionControl {
    private final SessionDAO sessionDAO;
    private final ArrayList<Session> dsPhien;

    public SessionControl() {
        sessionDAO = new SessionDAO();
        dsPhien = new ArrayList<>();
    }

    public void loadAll(Connection conn) throws SQLException {
        dsPhien.clear();
        dsPhien.addAll(sessionDAO.findAll(conn));
    }

    public ArrayList<Session> getAll() {
        return dsPhien;
    }

    // Bắt đầu phiên chơi
    public Session startSession(Connection conn, int idMay, int idKhach) throws SQLException {
        Session phien = new Session(
            0, idMay, idKhach, LocalDateTime.now(), null, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO
        );

        int newId = sessionDAO.insert(conn, phien);
        if(newId <= 0) return null;

        phien.setIdPhien(newId);
        dsPhien.add(phien);
        return phien;
    }

    // Kết thúc phiên chơi
    public boolean endSession(Connection conn, Session phien, Computer may) throws SQLException {
        BigDecimal giaGio = may.getGiaGio();
        LocalDateTime endTime = LocalDateTime.now();
        phien.setGioKetThuc(endTime);

        // Tính tiền giờ
        long minutes = Duration.between(phien.getGioBatDau(), endTime).toMinutes();
        if(minutes <= 0) minutes = 1;
        BigDecimal tienGio = giaGio.multiply(BigDecimal.valueOf(minutes)).divide(BigDecimal.valueOf(60), 2, RoundingMode.HALF_UP);
        phien.setTienGio(tienGio);
        phien.tinhTongTien();
        
        return sessionDAO.update(conn, phien);
    }

    // Update tiền dịch vụ
    public void congTienDichVu(Session phien, BigDecimal tienDV) {
        phien.setTienDichVu(phien.getTienDichVu().add(tienDV));
        phien.tinhTongTien();
    }

    // Tìm
    public Session findDangChoiByMay(Connection conn, int idMay) throws SQLException {
        return sessionDAO.findDangChoiByMay(conn, idMay);
    }
    public Session findDangChoiByKhach(Connection conn, int idKhach) throws SQLException {
        return sessionDAO.findDangChoiByKhach(conn, idKhach);
    }
}
