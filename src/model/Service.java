package model;
import java.math.*;

public class Service {
    private int idDv;
    private String tenDv;
    private BigDecimal gia;

    public Service() {}
    public Service(int idDv, String tenDv, BigDecimal gia) {
        this.idDv = idDv;
        this.tenDv = tenDv;
        this.gia = gia;
    }
    public Service(String tenDv, BigDecimal gia) {
        this.tenDv = tenDv;
        this.gia = gia;
    }

    public int getIdDv() {
        return idDv;
    }
    public void setIdDv(int idDv) {
        this.idDv = idDv;
    }
    public String getTenDv() {
        return tenDv;
    }
    public void setTenDv(String tenDv) {
        this.tenDv = tenDv;
    }
    public BigDecimal getGia() {
        return gia;
    }
    public void setGia(BigDecimal gia) {
        this.gia = gia;
    }

    // Tính tiền cho số lượng
    public BigDecimal tinhTien(int soLuong) {
        if(gia == null || soLuong <= 0) {
            return BigDecimal.ZERO;
        }
        return gia.multiply(BigDecimal.valueOf(soLuong));
    }

    // Hiển thị giá
    public String getGiaHienThi() {
        return gia == null ? "0" : gia.toPlainString();
    }

    @Override
    public String toString() {
        return tenDv + " - " + getGiaHienThi() + " VNĐ";
    }
}
