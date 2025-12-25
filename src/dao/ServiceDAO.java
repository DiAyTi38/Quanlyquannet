package dao;

import java.sql.*;
import model.Service;
import java.util.ArrayList;

public class ServiceDAO {
    // Thêm dịch vụ
    public int insert(Connection conn, Service dv) throws SQLException {
        String sql = "INSERT INTO DICH_VU (ten_dv, gia) VALUE (?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)){
            ps.setString(1, dv.getTenDv());
            ps.setBigDecimal(2, dv.getGia());
            int affectedRows = ps.executeUpdate();
            if(affectedRows == 0) return -1;
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if(rs.next()) return rs.getInt(1);
            }
        }
        return -1;
    }

    // Cập nhật dịch vụ
    public boolean update(Connection conn, Service dv) throws SQLException {
        String sql = "UPDATE DICH_VU SET ten_dv = ?, gia = ? WHERE id_dv = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)){
            ps.setString(1, dv.getTenDv());
            ps.setBigDecimal(2, dv.getGia());
            ps.setInt(3, dv.getIdDv());
            return ps.executeUpdate() > 0;
        }
    }

    // Xoá dịch vụ
    public boolean delete(Connection conn, int idDv) throws SQLException {
        String sql = "DELETE FROM DICH_VU WHERE id_dv = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idDv);
            return ps.executeUpdate() > 0;
        }
    }

    // Lấy tất cả dịch vụ 
    public ArrayList<Service> findAll(Connection conn) throws SQLException {
        ArrayList<Service> list = new ArrayList<>();
        String sql = "SELECT * FROM DICH_VU ORDER BY id_dv";
        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(mapRow(rs));
            }
        }
        return list;
    }

    // Tìm dịch vụ theo id
    public Service findById(Connection conn, int idDv) throws SQLException {
        String sql = "SELECT * FROM DICH_VU WHERE id_dv = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idDv);
            try (ResultSet rs = ps.executeQuery()) {
                if(rs.next()) return mapRow(rs);
            }
        }
        return null;
    }

    // Helper map ResultSet -> Service
    private Service mapRow(ResultSet rs) throws SQLException {
        return new Service(
            rs.getInt("id_dv"),
            rs.getString("ten_dv"),
            rs.getBigDecimal("gia")
        );
    }
}
