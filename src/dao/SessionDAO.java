package dao;

import model.Session;
import java.sql.*;
import java.util.ArrayList;
import java.time.LocalDateTime;

public class SessionDAO {
    // Thêm phiên chơi mới
    public int insert(Connection conn, Session phien) throws SQLException {
        String sql = "INSERT INTO PHIEN_CHOI (id_may, id_khach, gio_bat_dau, gio_ket_thuc, tien_gio, tien_dich_vu, tong_tien) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)){
            ps.setInt(1, phien.getIdMay());
            ps.setInt(2, phien.getIdKhach());
            ps.setTimestamp(3, phien.getGioBatDau() != null ? Timestamp.valueOf(phien.getGioBatDau()) : null);
            ps.setTimestamp(4, phien.getGioKetThuc() != null ? Timestamp.valueOf(phien.getGioKetThuc()) : null);
            ps.setBigDecimal(5, phien.getTienGio());
            ps.setBigDecimal(6, phien.getTienDichVu());
            ps.setBigDecimal(7, phien.getTongTien());
            int affectedRows = ps.executeUpdate();
            if(affectedRows == 0) return -1;
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) return rs.getInt(1);
            }
        }
        return -1;
    }

    // Cập nhật phiên chơi
    public boolean update(Connection conn, Session phien) throws SQLException {
        String sql = "UPDATE PHIEN_CHOI SET id_may = ?, id_khach = ?, gio_bat_dau = ?, gio_ket_thuc= ?, tien_gio = ?, tien_dich_vu = ?, tong_tien = ? WHERE id_phien = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, phien.getIdMay());
            ps.setInt(2, phien.getIdKhach());
            ps.setTimestamp(3, phien.getGioBatDau() != null ? Timestamp.valueOf(phien.getGioBatDau()) : null);
            ps.setTimestamp(4, phien.getGioKetThuc() != null ? Timestamp.valueOf(phien.getGioKetThuc()) : null);
            ps.setBigDecimal(5, phien.getTienGio());
            ps.setBigDecimal(6, phien.getTienDichVu());
            ps.setBigDecimal(7, phien.getTongTien());
            ps.setInt(8, phien.getIdPhien());

            return ps.executeUpdate() > 0;
        }
    }

    // Xoá phiên chơi theo id
    public boolean delete(Connection conn, int idPhien) throws SQLException {
        String sql = "DELETE FROM PHIEN_CHOI WHERE id_phien = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idPhien);
            return ps.executeUpdate() > 0;
        }
    }

    // Lấy tất cả phiên chơi
    public ArrayList<Session> findAll(Connection conn) throws SQLException {
        ArrayList<Session> list = new ArrayList<>();
        String sql = "SELECT * FROM PHIEN_CHOI ORDER BY id_phien";
        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(mapRow(rs));
            }
        }
        return list;
    }

    // Tìm phiên chơi theo id
    public Session findById(Connection conn, int idPhien) throws SQLException {
        String sql = "SELECT * FROM PHIEN_CHOI WHERE id_phien = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idPhien);
            try (ResultSet rs = ps.executeQuery()) {
                if(rs.next()) return mapRow(rs);
            }
        }
        return null;
    }

    // Tìm phiên chơi theo máy
    public Session findDangChoiByMay(Connection conn, int idMay) throws SQLException {
        String sql = """
            SELECT * FROM PHIEN_CHOI
            WHERE id_may = ?
              AND gio_ket_thuc IS NULL
              ORDER BY gio_bat_dau DESC
              LIMIT 1
        """;

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idMay);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return mapRow(rs);
            }
        }
        return null;
    }

    public Session findDangChoiByKhach(Connection conn, int idKhach) throws SQLException {
        String sql = """
            SELECT * FROM PHIEN_CHOI
            WHERE id_khach = ?
              AND gio_ket_thuc IS NULL
              ORDER BY gio_bat_dau DESC
              LIMIT 1
        """;
        
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idKhach);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return mapRow(rs);
            }
        }
        return null;
    }

    // Helper map ResultSet -> Session
    private Session mapRow(ResultSet rs) throws SQLException {
        Timestamp tsBatDau = rs.getTimestamp("gio_bat_dau");
        Timestamp tsKetThuc = rs.getTimestamp("gio_ket_thuc");

        LocalDateTime gioBatDau = tsBatDau != null ? tsBatDau.toLocalDateTime() : null;
        LocalDateTime gioKetThuc = tsKetThuc != null ? tsKetThuc.toLocalDateTime() : null;

        return new Session(
            rs.getInt("id_phien"),
            rs.getInt("id_may"),
            rs.getInt("id_khach"),
            gioBatDau,
            gioKetThuc,
            rs.getBigDecimal("tien_gio"),
            rs.getBigDecimal("tien_dich_vu"),
            rs.getBigDecimal("tong_tien")
        );
    }
}
