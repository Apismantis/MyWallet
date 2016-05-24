package Model;

/**
 * Created by ApisMantis on 11/25/2015.
 */

public class Wallet {
    private int _id;
    private String TenVi;
    private long SoDuHienTai;
    private String DonViTienTe;

    public Wallet() {
        this._id = -1;
        TenVi = "My Wallet";
        SoDuHienTai = 0;
        DonViTienTe = "VNĐ";
    }

    public Wallet(int _id, String tenVi, long soDuHienTai, String donViTienTe) {
        this._id = _id;
        TenVi = tenVi;
        SoDuHienTai = soDuHienTai;
        DonViTienTe = donViTienTe;
    }

    public int get_id() {
        return _id;
    }

    public void set_id(int _id) {
        this._id = _id;
    }

    public String getTenVi() {
        return TenVi;
    }

    public long getSoDuHienTai() {
        return SoDuHienTai;
    }

    public String getDonViTienTe() {
        return DonViTienTe;
    }

    public void setTenVi(String tenVi) {
        TenVi = tenVi;
    }

    public void setSoDuHienTai(long soDuHienTai) {
        SoDuHienTai = soDuHienTai;
    }

    public void setDonViTienTe(String donViTienTe) {
        DonViTienTe = donViTienTe;
    }

}
