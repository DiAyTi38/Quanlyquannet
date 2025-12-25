package controller;
import dao.ServiceDAO;
import model.Service;

import java.sql.*;
import java.util.ArrayList;
import java.math.BigDecimal;

public class ServiceControl {
    private final ServiceDAO serviceDAO;
    private final ArrayList<Service> dsDichVu;

    public ServiceControl() {
        serviceDAO = new ServiceDAO();
        dsDichVu = new ArrayList<>();
    }

    public void loadAll(Connection conn) throws SQLException {
        dsDichVu.clear();
        dsDichVu.addAll(serviceDAO.findAll(conn));
    }

    public ArrayList<Service> getAll() {
        return dsDichVu;
    }

    public Service addService(Connection conn, String ten, BigDecimal gia) throws SQLException {
        Service dv = new Service(0, ten, gia);
        int newId = serviceDAO.insert(conn, dv);
        if(newId <= 0) return null;

        dv.setIdDv(newId);
        dsDichVu.add(dv);
        return dv;
    }

    public boolean updateService(Connection conn, Service dv) throws SQLException {
        return serviceDAO.update(conn, dv);
    }

    public boolean deleteService(Connection conn, Service dv) throws SQLException {
        boolean ok = serviceDAO.delete(conn, dv.getIdDv());
        if(ok) dsDichVu.remove(dv);
        return ok;
    }

    public Service findById(int id) {
        for (Service s : dsDichVu) {
            if (s.getIdDv() == id) return s;
        }
        return null;
    }

}
