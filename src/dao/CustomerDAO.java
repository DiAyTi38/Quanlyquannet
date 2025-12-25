package dao;
import model.Customer;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;

public class CustomerDAO {
    // Thêm khách
    public int insert(Connection conn, Customer khach) throws SQLException {
        String sql = "INSERT INTO KHACH (ten_khach, username, password, so_du) VALUES (?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, khach.getTenKhach());
            ps.setString(2, khach.getUserName());
            ps.setString(3, khach.getPassWord());
            ps.setBigDecimal(4, khach.getSoDu());

            int affectedRows = ps.executeUpdate();
            if(affectedRows == 0) return -1;

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if(rs.next()) return rs.getInt(1);
            }
        }
        return -1;
    }

    // Cập nhật khách
    public boolean update(Connection conn, Customer khach, String ten, String username, String password, BigDecimal soDu) throws SQLException {
        String sql = "UPDATE KHACH SET ten_khach = ?, username= ?, password = ?, so_du = ? WHERE id_khach = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, ten);
            ps.setString(2, username);
            ps.setString(3, password);
            ps.setBigDecimal(4, soDu);
            ps.setInt(5, khach.getIdKhach());
            return ps.executeUpdate() > 0;
        }
    }

    // Xoá khách
    public boolean delete(Connection conn, int idKhach) throws SQLException {
        String sql = "DELETE FROM KHACH WHERE id_khach = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idKhach);
            return ps.executeUpdate() > 0;
        }
    }

    // Lấy tất cả khách
    public ArrayList<Customer> findAll(Connection conn) throws SQLException {
        ArrayList<Customer> list = new ArrayList<>();
        String sql = "SELECT * FROM KHACH ORDER BY id_khach";
        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while(rs.next()) list.add(mapRow(rs));
        }
        return list;
    }

    // Đăng nhập
    public Customer login(Connection conn, String username, String password)
            throws SQLException {

        String sql = """
            SELECT id_khach, ten_khach, username, so_du
            FROM KHACH
            WHERE username = ? AND password = ?
        """;

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            ps.setString(2, password);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Customer c = new Customer();
                    c.setIdKhach(rs.getInt("id_khach"));
                    c.setTenKhach(rs.getString("ten_khach"));
                    c.setUserName(rs.getString("username"));
                    c.setSoDu(rs.getBigDecimal("so_du"));
                    return c;
                }
            }
        }
        return null; // sai tài khoản / mật khẩu
    }

    // Cập nhật số dư
    public boolean updateBalance(Connection conn, int customerId, BigDecimal newBalance)
        throws SQLException {

        String sql = "UPDATE KHACH SET so_du = ? WHERE id_khach = ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setBigDecimal(1, newBalance);
            ps.setInt(2, customerId);

            return ps.executeUpdate() > 0;
        }
    }


    // Tìm khách
    public ArrayList<Customer> findByKey(Connection conn, String key)
        throws SQLException {

    ArrayList<Customer> list = new ArrayList<>();

    String sql = """
        SELECT * FROM KHACH
        WHERE ten_khach LIKE ?
           OR username LIKE ?
    """;

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            String keyword = "%" + key + "%";
            ps.setString(1, keyword);
            ps.setString(2, keyword);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new Customer(
                            rs.getInt("id_khach"),
                            rs.getString("ten_khach"),
                            rs.getString("username"),
                            rs.getString("password"),
                            rs.getBigDecimal("so_du")
                    ));
                }
            }
        }
        return list;
    }

    private Customer mapRow(ResultSet rs) throws SQLException {
        return new Customer(
            rs.getInt("id_khach"),
            rs.getString("ten_khach"),
            rs.getString("username"),
            rs.getString("password"),
            rs.getBigDecimal("so_du")
        );
    }
}
