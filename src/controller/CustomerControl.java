package controller;
import model.Customer;
import dao.CustomerDAO;

import java.sql.*;
import java.util.ArrayList;
import java.math.BigDecimal;

public class CustomerControl {
    private final CustomerDAO customerDAO;
    private final ArrayList<Customer> dsKhach;

    public CustomerControl() {
        customerDAO = new CustomerDAO();
        dsKhach = new ArrayList<>();
    }

    // Lấy tất cả dữ liệu bảng khách
    public void loadAll(Connection conn) throws SQLException {
        dsKhach.clear();
        dsKhach.addAll(customerDAO.findAll(conn));
    }
    public ArrayList<Customer> getAll() {
        return dsKhach;
    }

    // Thêm khách
    public Customer addCustomer(Connection conn, String ten, String username, String password, BigDecimal soDu) throws SQLException {
        Customer khach = new Customer(0, ten, username, password, soDu);
        int newId = customerDAO.insert(conn, khach);
        if (newId <= 0) return null;

        khach.setIdKhach(newId);
        dsKhach.add(khach);

        return khach;
    }

    // Sửa khách
    public boolean updateCustomer(Connection conn, Customer khach, String ten, String username, String password, BigDecimal soDu) throws SQLException {
        boolean ok = customerDAO.update(conn, khach, ten, username, password, soDu);
        if (!ok) return false;

        return true;
    }

    // Xoá khách
    public boolean deleteCustomer(Connection conn, Customer khach) throws SQLException {
        boolean ok = customerDAO.delete(conn, khach.getIdKhach());
        if(!ok) return false;

        dsKhach.remove(khach);
        return true;
    }

    // Tìm khách
    public ArrayList<Customer> find(Connection conn, String key) throws Exception {
        if (key == null || key.trim().isEmpty()) {
            return new ArrayList<>();
        }
        return customerDAO.findByKey(conn, key.trim());
    }

}
