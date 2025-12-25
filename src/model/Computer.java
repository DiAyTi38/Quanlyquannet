package model;
import java.math.*;

public class Computer {
    private int idMay;
    private String tenMay;
    private String loaiMay;
    private BigDecimal giaGio;
    private String trangThai;

    public Computer() {}

    public Computer(int idMay, String tenMay, String loaiMay, BigDecimal giaGio, String trangThai) {
        this.idMay = idMay;
        this.tenMay = tenMay;
        this.loaiMay = loaiMay;
        this.giaGio = giaGio;
        this.trangThai = trangThai;
    }

    // Tạo các setter và getter
    public int getIdMay() {
        return idMay;
    }
    public void setIdMay(int idMay) {
        this.idMay = idMay;
    }
    public String getTenMay() {
        return tenMay;
    }
    public void setTenMay(String tenMay) {
        this.tenMay = tenMay;
    }
    public String getLoaiMay() {
        return loaiMay;
    }
    public void setLoaiMay(String loaiMay) {
        this.loaiMay = loaiMay;
    }
    public BigDecimal getGiaGio() {
        return giaGio;
    }
    public void setGiaGio(BigDecimal giaGio) {
        this.giaGio = giaGio;
    }
    public String getTrangThai() {
        return trangThai;
    }
    public void setTrangThai(String trangThai) {
        this.trangThai = trangThai;
    }

    // Kiểm tra máy trống hay không
    public boolean isTrong() {
        return "trong".equalsIgnoreCase(trangThai);
    }
    // Kiểm tra máy đang sử dụng
    public boolean isDangSuDung() {
        return "dang_su_dung".equalsIgnoreCase(trangThai);
    }

    // Trạng thái hiển thị cho UI
    public String getTrangThaiHienThi() {
        switch (trangThai) {
            case "dang_su_dung":
                return "Đang sử dụng";
            case "bao_tri":
                return "Bảo trì";
            default:
                return "Trống";
        }
    }

    @Override
    public String toString() {
        return tenMay + " - " + getTrangThaiHienThi();
    }
}