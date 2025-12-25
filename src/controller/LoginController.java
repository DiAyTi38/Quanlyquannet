package controller;
import dao.CustomerDAO;
import model.Customer;
import java.sql.*;

public class LoginController {
    private final CustomerDAO customerDAO;

    public LoginController() {
        this.customerDAO = new CustomerDAO();
    }

    public Customer login(Connection conn, String username, String password) throws SQLException {
        if (username == null || username.isBlank() || password == null || password.isBlank()) {
            return null;
        }

        return customerDAO.login(conn, username, password);
    }
}
