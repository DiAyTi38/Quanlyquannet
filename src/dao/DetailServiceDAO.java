package dao;
import model.DetailService;
import java.sql.*;
import java.util.ArrayList;

public class DetailServiceDAO {
    // Thêm chi tiết dịch vụ
    public int insert(Connection conn, DetailService ctdv) throws SQLException {
        String sql = "INSERT INTO CHI_TIET_DV (id_phien, id_dv, so_luong) VALUES (?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, ctdv.getIdPhien());
            ps.setInt(2, ctdv.getIdDv());
            ps.setInt(3, ctdv.getSoLuong());

            int affectedRows = ps.executeUpdate();
            if(affectedRows == 0) return -1;

            try(ResultSet rs = ps.getGeneratedKeys()) {
                if(rs.next()) return rs.getInt(1);
            }
        }
        return -1;
    }

    // Cập nhật chi tiết dịch vụ
    public boolean update(Connection conn, DetailService ctdv) throws SQLException {
        String sql = "UPDATE CHI_TIET_DV SET id_phien = ?, id_dv= ?, so_luong= ? WHERE id_ctdv=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1,ctdv.getIdPhien());
            ps.setInt(2,ctdv.getIdDv());
            ps.setInt(3, ctdv.getSoLuong());
            ps.setInt(4, ctdv.getIdCtdv());

            return ps.executeUpdate() > 0;
        }
    }

    // Xoá chi tiết dịch vụ
    public boolean delete(Connection conn, int idPhien, int idCtdv) throws SQLException {
        String sql = "DELETE FROM CHI_TIET_DV WHERE id_ctdv= ? AND id_phien = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idCtdv);
            ps.setInt(2, idPhien);
            return ps.executeUpdate() > 0;
        }
    }

    // Lấy tất cả chi tiết dịch vụ
    public ArrayList<DetailService> findAll(Connection conn) throws SQLException {
        ArrayList<DetailService> list = new ArrayList<>();
        String sql = "SELECT * FROM CHI_TIET_DV ORDER BY id_ctdv";
        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(mapRow(rs));
        }
        return list;
    }

    private DetailService mapRow(ResultSet rs) throws SQLException {
        return new DetailService(
            rs.getInt("id_ctdv"),
            rs.getInt("id_phien"),
            rs.getInt("id_dv"),
            rs.getInt("so_luong")
        );
    }
}
