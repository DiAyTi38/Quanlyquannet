package model;
import java.math.*;
import java.time.LocalDateTime;
import java.time.Duration;

public class Session {
    private int idPhien;
    private int idMay;
    private int idKhach;
    private LocalDateTime gioBatDau;
    private LocalDateTime gioKetThuc;
    private BigDecimal tienGio;
    private BigDecimal tienDichVu;
    private BigDecimal tongTien;

    public Session() {}
    public Session(int idPhien, int idMay, int idKhach, LocalDateTime gioBatDau, LocalDateTime gioKetThuc, BigDecimal tienGio, BigDecimal tienDichVu, BigDecimal tongTien) {
        this.idPhien = idPhien;
        this.gioBatDau = gioBatDau;
        this.gioKetThuc = gioKetThuc;
        this.tienGio = tienGio;
        this.tienDichVu = tienDichVu;
        this.tongTien = tongTien;
        this.idMay = idMay;
        this.idKhach = idKhach;
    }

    // Thêm phiên mới
    public Session(LocalDateTime gioBatDau, LocalDateTime gioKetThuc, BigDecimal tienGio, BigDecimal tienDichVu, BigDecimal tongTien) {
        this.gioBatDau = gioBatDau;
        this.gioKetThuc = gioKetThuc;
        this.tienGio = tienGio;
        this.tienDichVu = tienDichVu;
        this.tongTien = tongTien;
    }

    public int getIdMay() {
        return idMay;
    }
    public void setIdMay(int idMay) {
        this.idMay = idMay;
    }
    public int getIdKhach() {
        return idKhach;
    }
    public void setIdKhach(int idKhach) {
        this.idKhach = idKhach;
    }
    public int getIdPhien() {
        return idPhien;
    }
    public void setIdPhien(int idPhien) {
        this.idPhien = idPhien;
    }
    public LocalDateTime getGioBatDau() {
        return gioBatDau;
    }
    public void setGioBatDau(LocalDateTime gioBatDau) {
        this.gioBatDau = gioBatDau;
    }
    public LocalDateTime getGioKetThuc() {
        return gioKetThuc;
    }
    public void setGioKetThuc(LocalDateTime gioKetThuc) {
        this.gioKetThuc = gioKetThuc;
    }
    public BigDecimal getTienGio() {
        return tienGio;
    }
    public void setTienGio(BigDecimal tienGio) {
        this.tienGio = tienGio;
    }
    public BigDecimal getTienDichVu() {
        return tienDichVu;
    }
    public void setTienDichVu(BigDecimal tienDichVu) {
        this.tienDichVu = tienDichVu;
    }
    public BigDecimal getTongTien() {
        return tongTien;
    }
    public void setTongTien(BigDecimal tongTien) {
        this.tongTien = tongTien;
    }

    public boolean isDangChoi() {
        return gioBatDau != null && gioKetThuc == null;
    }

    // Phiên đã kết thúc chưa
    public boolean daKetThuc() {
        return gioKetThuc != null;
    }

    // Thời gian chơi (phút)
    public long getSoPhutChoi() {
        if(gioBatDau == null) return 0;
        LocalDateTime end = gioKetThuc != null ? gioKetThuc : LocalDateTime.now();
        return Duration.between(gioBatDau, end).toMinutes();
    }

    // Hiển thị thời gian chơi
    public String getThoiGianHienThi() {
        long phut = getSoPhutChoi();
        long gio = phut/60;
        long du = phut%60;
        return gio + "h " + du + "p";
    }
    // Cập nhật tổng tiền
    public void tinhTongTien() {
        this.tongTien = tienGio.add(tienDichVu);
    }

    public String getTongTienHienThi() {
        return tongTien == null ? "0" : tongTien.toPlainString() + " đ";
    }
}
