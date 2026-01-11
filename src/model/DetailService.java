package model;

import java.math.BigDecimal;

public class DetailService {
    private int idPhien;
    private int idDv;
    private int idCtdv;
    private int soLuong;
    private Service service;

    public DetailService() {
    }

    public DetailService(int idCtdv, int idPhien, int idDv, int soLuong) {
        this.idCtdv = idCtdv;
        this.idPhien = idPhien;
        this.idDv = idDv;
        this.soLuong = soLuong;
    }

    public int getIdPhien() {
        return idPhien;
    }

    public void setIdPhien(int idPhien) {
        this.idPhien = idPhien;
    }

    public int getIdDv() {
        return idDv;
    }

    public void setIdDv(int idDv) {
        this.idDv = idDv;
    }

    public int getIdCtdv() {
        return idCtdv;
    }

    public void setIdCtdv(int idCtdv) {
        this.idCtdv = idCtdv;
    }

    public int getSoLuong() {
        return soLuong;
    }

    public void setSoLuong(int soLuong) {
        this.soLuong = soLuong;
    }

    // Thành tiền cho dòng dịch vụ
    public BigDecimal getThanhTien() {
        if (service == null)
            return BigDecimal.ZERO;
        return service.tinhTien(soLuong);
    }

    // Hiển thị dòng dịch vụ
    public String getMoTa() {
        return service.getTenDv() + " x " + soLuong;
    }
}
