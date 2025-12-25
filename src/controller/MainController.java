package controller;
import model.Computer;
import model.Customer;
import dao.CustomerDAO;
import dao.ServiceDAO;  
import model.Service;
import model.Session;

import java.sql.*;
import java.util.ArrayList;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public class MainController {
    private final Customer currentCustomer;
    private final ServiceDAO serviceDAO;
    private final Connection conn;
    private final CustomerDAO customerDAO;
    private final LocalDateTime loginTime;
    private final SessionControl sessionControl;
    private final ComputerControl computerControl;

    public MainController(Connection conn, Customer customer) {
        if (conn == null) {
            throw new IllegalArgumentException("Connection is null");
        }
        if (customer == null) {
            throw new IllegalArgumentException("Customer is null");
        }
        this.conn = conn;
        this.currentCustomer = customer;
        this.serviceDAO = new ServiceDAO();
        this.customerDAO = new CustomerDAO();
        this.sessionControl = new SessionControl();        
        this.computerControl = new ComputerControl();      
        this.loginTime = LocalDateTime.now();
    }

    public Connection getConnection() {
        return this.conn;
    }

    // Khách đang đăng nhập
    public Customer getCurrentCustomer() {
        if (currentCustomer == null) {
            throw new IllegalStateException("No customer logged in");
        }
        return currentCustomer;
    }

    public BigDecimal getBalance() {
        if (currentCustomer == null) {
            throw new IllegalStateException("No customer logged in");
        }
        BigDecimal soDu = currentCustomer.getSoDu();
        if (soDu == null) {
            return BigDecimal.ZERO;
        }
        return soDu;
    }

    // Dịch vụ
    public ArrayList<Service> getAllServices() throws SQLException {
        if (conn == null) {
            throw new SQLException("Database connection is not available.");
        }
        try {
            return serviceDAO.findAll(conn);
        } catch (SQLException ex) {
            throw new SQLException("Lỗi khi lấy danh sách dịch vụ: " + ex.getMessage(), ex);
        }
    }

    public boolean buyService(Service dv) throws SQLException {
        if (currentCustomer == null) {
            throw new IllegalStateException("No customer logged in");
        }
        if (dv == null) {
            throw new IllegalArgumentException("Service is null");
        }
        BigDecimal balance = currentCustomer.getSoDu();
        if (balance == null) {
            balance = BigDecimal.ZERO;
        }
        if (dv.getGia() == null) {
            throw new IllegalArgumentException("Service price is null");
        }
        if (balance.compareTo(dv.getGia()) < 0) {
            return false;
        }
        BigDecimal newBalance = balance.subtract(dv.getGia());
        try {
            customerDAO.updateBalance(conn, currentCustomer.getIdKhach(), newBalance);
            currentCustomer.setSoDu(newBalance);
        } catch (SQLException e) {
            throw new SQLException("Lỗi khi mua dịch vụ: " + e.getMessage(), e);
        }
        return true;
    }

    // Thời gian chơi
    public LocalDateTime getLoginTime() {
        return loginTime;
    }

    public long getPlayedMinutes() {
        if (loginTime == null) {
            throw new IllegalStateException("Login time is not set");
        }
        return java.time.Duration.between(loginTime, LocalDateTime.now()).toMinutes();
    }

    // Đăng xuất
    public void logout() throws Exception {
        Session phien = sessionControl.findDangChoiByKhach(conn, currentCustomer.getIdKhach());
        if (phien == null) {
            throw new IllegalStateException("Khách không có phiên chơi nào");
        }

        // Load máy
        Computer may = computerControl.findById(conn, phien.getIdMay());
        if (may == null) {
            throw new IllegalStateException("Không tìm thấy máy nào");
        }
        // Kết thúc phiên
        sessionControl.endSession(conn, phien, may);

        // Trừ tiền giờ vào số dư khách
        BigDecimal tongTien = phien.getTongTien();
        BigDecimal soDuMoi = currentCustomer.getSoDu().subtract(tongTien);
        if (soDuMoi.compareTo(BigDecimal.ZERO) < 0) {
            soDuMoi = BigDecimal.ZERO;
        }

        customerDAO.updateBalance(conn, currentCustomer.getIdKhach(), soDuMoi);
        currentCustomer.setSoDu(soDuMoi);

        // trả máy
        computerControl.updateTrangThai(conn, may.getIdMay(), "trong");
    } 
}