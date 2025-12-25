package dao;
import model.Computer;
import java.sql.*;
import java.util.ArrayList;

public class ComputerDAO {
    // Lấy tất cả máy
    public ArrayList<Computer> findAll(Connection conn) throws SQLException {
        ArrayList<Computer> list = new ArrayList<>();
        String sql = "SELECT * FROM MAY ORDER BY id_may";
        try (PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(mapRow(rs));
            }
        }
        return list;
        
    }

    // Tìm máy theo id
    public Computer findById(Connection conn, int idMay) throws SQLException {
        String sql = "SELECT * FROM MAY WHERE id_may = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)){
            ps.setInt(1, idMay);
            try (ResultSet rs = ps.executeQuery()) {
                if(rs.next()) return mapRow(rs);
            }
        }
        return null;
    }

    // Cập nhật trạng thái
    public void updateTrangThai(Connection conn, int idMay, String trangThai) throws SQLException {
        String sql = "UPDATE MAY SET trang_thai = ? WHERE id_may = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, trangThai);
            ps.setInt(2, idMay);
            ps.executeUpdate();
        }
    }

    private Computer mapRow(ResultSet rs) throws SQLException {
        return new Computer(
            rs.getInt("id_may"),
            rs.getString("ten_may"),
            rs.getString("loai_may"),
            rs.getBigDecimal("gia_gio"),
            rs.getString("trang_thai")
        );
    }
}
