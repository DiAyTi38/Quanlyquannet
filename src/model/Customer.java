package model;
import java.math.*;

public class Customer {
    private int idKhach;
    private String tenKhach;
    private String userName;
    private String passWord;
    private BigDecimal soDu;

    public Customer() {}

    public Customer(int idKhach, String tenKhach, String userName, String passWord, BigDecimal soDu) {
        this.idKhach = idKhach;
        this.tenKhach = tenKhach;
        this.userName = userName;
        this.passWord = passWord;
        this.soDu = soDu;
    }

    // Constructor khi tạo khách mới (chưa có id)
    public Customer(String tenKhach, String userName, String passWord, BigDecimal soDu) {
        this.tenKhach = tenKhach;
        this.userName = userName;
        this.passWord = passWord;
        this.soDu = soDu;
    }

        // Getter & Setter
    public int getIdKhach() {
        return idKhach;
    }
    public void setIdKhach(int idKhach) {
        this.idKhach = idKhach;
    }
    public String getTenKhach() {
        return tenKhach;
    }
    public void setTenKhach(String tenKhach) {
        this.tenKhach = tenKhach;
    }
    public String getUserName() {
        return userName;
    }
    public void setUserName(String userName) {
        this.userName = userName;
    }
    public String getPassWord() {
        return passWord;
    }

    // ⚠️ Không nên expose setter password ra UI
    public void setPassWord(String passWord) {
        this.passWord = passWord;
    }
    public BigDecimal getSoDu() {
        return soDu;
    }
    public void setSoDu(BigDecimal soDu) {
        this.soDu = soDu;
    }

    // Kiểm tra còn tiền không
    public boolean conTien() {
        return soDu != null && soDu.compareTo(BigDecimal.ZERO) > 0; 
    }

    // Hiển thị số dư đẹp hơn
    public String getSoDuHienThi() {
        return soDu == null ? "0" : soDu.toPlainString();
    }

    @Override
    public String toString() {
        return tenKhach + " (" + userName + ")";
    }
}
