package Model;

/**
 * Created by ApisMantis on 11/25/2015.
 */
public class Transaction {
    private int _id;
    private String TenGiaoDich;
    private String LoaiGiaoDich;
    private long SoTien;
    private String NgayGiaoDich;
    private String GhiChu;
    private int MaViTien;

    public Transaction() {
    }

    public Transaction(int _id, String tenGiaoDich, String loaiGiaoDich, long soTien, String ngayGiaoDich, String ghiChu, int maViTien) {
        this._id = _id;
        TenGiaoDich = tenGiaoDich;
        LoaiGiaoDich = loaiGiaoDich;
        SoTien = soTien;
        NgayGiaoDich = ngayGiaoDich;
        GhiChu = ghiChu;
        MaViTien = maViTien;
    }

    public int getMaViTien() {
        return MaViTien;
    }

    public void setMaViTien(int maViTien) {
        MaViTien = maViTien;
    }

    public int get_id() {
        return _id;
    }

    public void set_id(int _id) {
        this._id = _id;
    }

    public String getTenGiaoDich() {
        return TenGiaoDich;
    }

    public String getLoaiGiaoDich() {
        return LoaiGiaoDich;
    }

    public long getSoTien() {
        return SoTien;
    }

    public String getNgayGiaoDich() {
        return NgayGiaoDich;
    }

    public String getGhiChu() {
        return GhiChu;
    }

    public void setTenGiaoDich(String tenGiaoDich) {
        TenGiaoDich = tenGiaoDich;
    }

    public void setLoaiGiaoDich(String loaiGiaoDich) {
        LoaiGiaoDich = loaiGiaoDich;
    }

    public void setSoTien(long soTien) {
        SoTien = soTien;
    }

    public void setNgayGiaoDich(String ngayGiaoDich) {
        NgayGiaoDich = ngayGiaoDich;
    }

    public void setGhiChu(String ghiChu) {
        GhiChu = ghiChu;
    }
}
