package controller;
import dao.ComputerDAO;
import model.Computer;

import java.sql.*;
import java.util.ArrayList;

public class ComputerControl {
    private final ComputerDAO computerDAO;
    private final ArrayList<Computer> dsMay;

    public ComputerControl() {
        computerDAO = new ComputerDAO();
        dsMay = new ArrayList<>();
    }

    // Load tất cả máy
    public void loadAll(Connection conn) throws SQLException {
        dsMay.clear();
        dsMay.addAll(computerDAO.findAll(conn));
    }
    public ArrayList<Computer> getAll() {
        return dsMay;
    }

    // Tìm máy theo id máy
    public Computer findById(Connection conn, int idMay) throws SQLException {
        return computerDAO.findById(conn, idMay);
    }

    // update trạng thái
    public void updateTrangThai(Connection conn, int idMay, String trangThaiMoi)
            throws Exception {

        // 1. Update DB
        computerDAO.updateTrangThai(conn, idMay, trangThaiMoi);

        // 2. Update trong danh sách
        for (Computer c : dsMay) {
            if (c.getIdMay() == idMay) {
                c.setTrangThai(trangThaiMoi);
                break;
            }
        }
    }

    public void setDangSuDung(Computer may) {
        may.setTrangThai("dang_su_dung");
    }

    public void setTrong(Computer may) {
        may.setTrangThai("trong");
    }

}
