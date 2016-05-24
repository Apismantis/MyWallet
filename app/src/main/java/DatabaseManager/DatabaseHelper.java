package DatabaseManager;

import android.annotation.TargetApi;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Build;
import android.util.Log;

import java.io.File;
import java.util.ArrayList;

import Model.Transaction;
import Model.Wallet;


/**
 * Created by ApisMantis on 11/28/2015.
 */

public class DatabaseHelper extends SQLiteOpenHelper {

    // Su dung mau SingleTon de tao ra 1 doi tuong DatabaseHelper duy nhat
    private static DatabaseHelper sInstances;
    private static Context mContext;
    private final String TAG = "[DATABASEHELPER]";

    // Ten Database
    // Path Database
    public static final String DB_NAME = "MyWalletDB.db";
    //private final String DB_PATH = "/data/data/" + WalletActivity.PACKGAE_NAME + "/databases/";

    // Ten cac bang
    private final String TABLE_VI_TIEN = "ViTien";
    private final String TABLE_GIAO_DICH = "GiaoDich";

    // Ten cac thuoc tinh cua bang ViTien
    private final String KEY_ID_VI_TIEN = "_id";
    private final String KEY_TEN_VI = "TenVi";
    private final String KEY_SO_DU_HIEN_TAI = "SoDuHienTai";
    private final String KEY_DON_VI_TIEN_TE = "DonViTienTe";

    private final String KEY_ID_GIAO_DICH = "_id";
    private final String KEY_TEN_GIAO_DICH = "TenGiaoDich";
    private final String KEY_LOAI_GIAO_DICH = "LoaiGiaoDich";
    private final String KEY_NGAY_GIAO_DICH = "NgayGiaoDich";
    private final String KEY_SO_TIEN_GIAO_DICH = "SoTien";
    private final String KEY_GHI_CHU_GIAO_DICH = "GhiChu";
    private final String KEY_MA_VI_TIEN = "MaViTien";

    // Lenh tao 2 bang
    private final String CREATE_TABLE_VI_TIEN = "CREATE TABLE " + TABLE_VI_TIEN + " "
            + "(" + KEY_ID_VI_TIEN + " INTEGER PRIMARY KEY AUTOINCREMENT, " + KEY_TEN_VI + " TEXT, "
            + KEY_SO_DU_HIEN_TAI + " BIGINT, " + KEY_DON_VI_TIEN_TE + " TEXT)";

    private final String CREATE_TABLE_GIAO_DICH = "CREATE TABLE " + TABLE_GIAO_DICH
            + " (" + KEY_ID_GIAO_DICH + " INTEGER PRIMARY KEY AUTOINCREMENT, " + KEY_TEN_GIAO_DICH + " TEXT, "
            + KEY_LOAI_GIAO_DICH + " TEXT, " + KEY_NGAY_GIAO_DICH + " DATE, " + KEY_SO_TIEN_GIAO_DICH + " BIGINT, "
            + KEY_GHI_CHU_GIAO_DICH + " TEXT, " + KEY_MA_VI_TIEN + " INTEGER" + ")";


    // Phuong thuc khoi tao
    private DatabaseHelper(Context context) {
        super(context, DB_NAME, null, 1);
        this.mContext = context;
    }


    // Get sInstances
    public static synchronized DatabaseHelper getsInstances(Context context) {
        if (sInstances == null) {
            sInstances = new DatabaseHelper(context.getApplicationContext());
        }

        return sInstances;
    }

    // Configure
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
        db.setForeignKeyConstraintsEnabled(true);
    }

    // Khoi tao du Database
    // Neu da co Database cung ten thi khong thuc thi
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_VI_TIEN);
        db.execSQL(CREATE_TABLE_GIAO_DICH);
    }

    // Cap nhat Database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion != newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_VI_TIEN);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_GIAO_DICH);
            onCreate(db);
        }
    }


    // Kiem tra database co ton tai hay khong
    public boolean isExist(Context context) {

        try {
            File dbFile = context.getDatabasePath(DB_NAME);
            return dbFile.exists();

        } catch (Exception e) {
            Log.d(TAG, e.getMessage());
            return false;
        }
    }


    // Them mot vi tien
    public boolean addWallet(Wallet wallet) {
        SQLiteDatabase db = this.getWritableDatabase();

        // Thuc hien thuc them vi trong Transaction
        db.beginTransaction();
        try {
            ContentValues values = new ContentValues();
            values.put(KEY_TEN_VI, wallet.getTenVi());
            values.put(KEY_SO_DU_HIEN_TAI, wallet.getSoDuHienTai());
            values.put(KEY_DON_VI_TIEN_TE, wallet.getDonViTienTe());

            // Chen vao database
            db.insertOrThrow(TABLE_VI_TIEN, null, values);

            db.setTransactionSuccessful();
            return true;

        } catch (Exception e) {
            Log.d(TAG, "ERROR: CAN'T ADD WALLET...");
            return false;
            // Toast len thong bao tai day

        } finally {
            db.endTransaction();

        }
    }


    // Lay ra 1 vi tien
    public Wallet getWallet(int _id) {
        Wallet wallet = new Wallet();
        SQLiteDatabase db = this.getReadableDatabase();

        String sql = "Select * from " + TABLE_VI_TIEN + " where " + KEY_ID_VI_TIEN + " = " + _id;

        // Lay thong tin cua vi lay duoc
        Cursor c = db.rawQuery(sql, null);
        if (c != null) {
            c.moveToFirst();

            wallet.set_id(c.getInt(c.getColumnIndex(KEY_ID_VI_TIEN)));
            wallet.setTenVi(c.getString(c.getColumnIndex(KEY_TEN_VI)));
            wallet.setDonViTienTe(c.getString(c.getColumnIndex(KEY_DON_VI_TIEN_TE)));
            wallet.setSoDuHienTai(c.getLong(c.getColumnIndex(KEY_SO_DU_HIEN_TAI)));
        }

        return wallet;
    }


    // Lay danh sach cac vi tien
    public ArrayList<Wallet> getListWallet() {
        ArrayList<Wallet> arrWallet = new ArrayList<Wallet>();
        try {
            SQLiteDatabase db = this.getReadableDatabase();

            String sql = "Select * from " + TABLE_VI_TIEN;

            // Lay thong tin cac vi tien
            Cursor c = db.rawQuery(sql, null);
            if (c != null) {
                c.moveToFirst();
                do {
                    Wallet wallet = new Wallet();

                    wallet.set_id(c.getInt(c.getColumnIndex(KEY_ID_VI_TIEN)));
                    wallet.setTenVi(c.getString(c.getColumnIndex(KEY_TEN_VI)));
                    wallet.setDonViTienTe(c.getString(c.getColumnIndex(KEY_DON_VI_TIEN_TE)));
                    wallet.setSoDuHienTai(c.getLong(c.getColumnIndex(KEY_SO_DU_HIEN_TAI)));

                    Log.d(TAG, Integer.toString(wallet.get_id()) + " - " + wallet.getTenVi());

                    arrWallet.add(wallet);
                } while (c.moveToNext());
            }

        } catch (Exception e) {
            Log.d(TAG, e.getMessage());
        }

        return arrWallet;
    }


    // Cap nhat 1 vi tien
    public boolean updateWallet(int _id, Wallet wallet) {
        SQLiteDatabase db = this.getWritableDatabase();

        db.beginTransaction();
        try {
            ContentValues values = new ContentValues();

            values.put(KEY_TEN_VI, wallet.getTenVi());
            values.put(KEY_SO_DU_HIEN_TAI, wallet.getSoDuHienTai());
            values.put(KEY_DON_VI_TIEN_TE, wallet.getDonViTienTe());

            db.update(TABLE_VI_TIEN, values, KEY_ID_VI_TIEN + "=" + _id, null);
            db.setTransactionSuccessful();
            return true;

        } catch (Exception e) {
            Log.d(TAG, "ERROR: CAN'T UPADTE WALLET");
            return false;

        } finally {
            db.endTransaction();

        }

    }


    // Xoa 1 vi tien
    public boolean deleteWallet(int _id) {
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            db.delete(TABLE_VI_TIEN, KEY_ID_VI_TIEN + " = " + _id, null);
            return true;

        } catch (Exception e) {
            return false;
        }
    }


    // Them mot giao dich
    public boolean addTransaction(Transaction trans) {
        SQLiteDatabase db = this.getWritableDatabase();

        // Thuc hien thuc them vi trong Transaction
        db.beginTransaction();
        try {
            ContentValues values = new ContentValues();
            values.put(KEY_TEN_GIAO_DICH, trans.getTenGiaoDich());
            values.put(KEY_LOAI_GIAO_DICH, trans.getLoaiGiaoDich());
            values.put(KEY_NGAY_GIAO_DICH, trans.getNgayGiaoDich());
            values.put(KEY_SO_TIEN_GIAO_DICH, trans.getSoTien());
            values.put(KEY_GHI_CHU_GIAO_DICH, trans.getGhiChu());
            values.put(KEY_MA_VI_TIEN, trans.getMaViTien());

            // Chen vao database
            db.insertOrThrow(TABLE_GIAO_DICH, null, values);

            db.setTransactionSuccessful();
            return true;

        } catch (Exception e) {
            Log.d(TAG, "ERROR: CAN'T ADD TRANSACTION...");
            return false;

        } finally {
            db.endTransaction();

        }
    }


    // Lay ra 1 giao dich
    public Transaction getTransaction(int _id) {
        Transaction trans = new Transaction();
        SQLiteDatabase db = this.getReadableDatabase();

        String sql = "Select * from " + TABLE_GIAO_DICH + " where " + KEY_ID_GIAO_DICH + " = " + _id;

        // Lay thong tin cua vi lay duoc
        Cursor c = db.rawQuery(sql, null);
        if (c != null) {
            c.moveToFirst();

            trans.set_id(c.getInt(c.getColumnIndex(KEY_ID_GIAO_DICH)));
            trans.setTenGiaoDich(c.getString(c.getColumnIndex(KEY_TEN_GIAO_DICH)));
            trans.setLoaiGiaoDich(c.getString(c.getColumnIndex(KEY_LOAI_GIAO_DICH)));
            trans.setSoTien(c.getLong(c.getColumnIndex(KEY_SO_TIEN_GIAO_DICH)));
            trans.setGhiChu(c.getString(c.getColumnIndex(KEY_GHI_CHU_GIAO_DICH)));
            trans.setNgayGiaoDich(c.getString(c.getColumnIndex(KEY_NGAY_GIAO_DICH)));
            trans.setMaViTien(c.getInt(c.getColumnIndex(KEY_MA_VI_TIEN)));
        }

        return trans;
    }


    // Lay danh sach cac giao dich
    public ArrayList<Transaction> getListTransaction(int _WalletId, String Month, String Year) {
        ArrayList<Transaction> arrTrans = new ArrayList<Transaction>();
        try {
            SQLiteDatabase db = this.getReadableDatabase();

            String sql = "Select * from " + TABLE_GIAO_DICH
                    + " Where " + KEY_MA_VI_TIEN + " = " + _WalletId
                    + " And strftime('%m', " + KEY_NGAY_GIAO_DICH + ") = " + "'" + Month + "'"
                    + " And strftime('%Y', " + KEY_NGAY_GIAO_DICH + ") = " + "'" + Year + "'"
                    + " ORDER BY " + KEY_NGAY_GIAO_DICH + " DESC";

            // Lay thong tin cac vi tien
            Cursor c = db.rawQuery(sql, null);
            if (c != null) {
                c.moveToFirst();
                do {
                    Transaction trans = new Transaction();

                    trans.set_id(c.getInt(c.getColumnIndex(KEY_ID_GIAO_DICH)));
                    trans.setTenGiaoDich(c.getString(c.getColumnIndex(KEY_TEN_GIAO_DICH)));
                    trans.setLoaiGiaoDich(c.getString(c.getColumnIndex(KEY_LOAI_GIAO_DICH)));
                    trans.setSoTien(c.getLong(c.getColumnIndex(KEY_SO_TIEN_GIAO_DICH)));
                    trans.setGhiChu(c.getString(c.getColumnIndex(KEY_GHI_CHU_GIAO_DICH)));
                    trans.setNgayGiaoDich(c.getString(c.getColumnIndex(KEY_NGAY_GIAO_DICH)));
                    trans.setMaViTien(c.getInt(c.getColumnIndex(KEY_MA_VI_TIEN)));

                    arrTrans.add(trans);

                } while (c.moveToNext());
            }

        } catch (Exception e) {
            Log.d(TAG, e.getMessage());
        }

        return arrTrans;
    }


    // Lay danh sach cac giao dich trong 1 ngay
    public ArrayList<Transaction> getListTransactionOneDay(int _WalletId, String Day, String Month, String Year) {
        ArrayList<Transaction> arrTrans = new ArrayList<Transaction>();
        try {
            SQLiteDatabase db = this.getReadableDatabase();

            String sql = "Select * from " + TABLE_GIAO_DICH
                    + " Where " + KEY_MA_VI_TIEN + " = " + _WalletId
                    + " And strftime('%d', " + KEY_NGAY_GIAO_DICH + ") = " + "'" + Day + "'"
                    + " And strftime('%m', " + KEY_NGAY_GIAO_DICH + ") = " + "'" + Month + "'"
                    + " And strftime('%Y', " + KEY_NGAY_GIAO_DICH + ") = " + "'" + Year + "'"
                    + " ORDER BY " + KEY_ID_GIAO_DICH + " DESC";

            // Lay thong tin cac vi tien
            Cursor c = db.rawQuery(sql, null);
            if (c != null) {
                c.moveToFirst();
                do {
                    Transaction trans = new Transaction();

                    trans.set_id(c.getInt(c.getColumnIndex(KEY_ID_GIAO_DICH)));
                    trans.setTenGiaoDich(c.getString(c.getColumnIndex(KEY_TEN_GIAO_DICH)));
                    trans.setLoaiGiaoDich(c.getString(c.getColumnIndex(KEY_LOAI_GIAO_DICH)));
                    trans.setSoTien(c.getLong(c.getColumnIndex(KEY_SO_TIEN_GIAO_DICH)));
                    trans.setGhiChu(c.getString(c.getColumnIndex(KEY_GHI_CHU_GIAO_DICH)));
                    trans.setNgayGiaoDich(c.getString(c.getColumnIndex(KEY_NGAY_GIAO_DICH)));
                    trans.setMaViTien(c.getInt(c.getColumnIndex(KEY_MA_VI_TIEN)));

                    arrTrans.add(trans);

                } while (c.moveToNext());
            }

        } catch (Exception e) {
            Log.d(TAG, e.getMessage());
        }

        return arrTrans;
    }


    // Cap nhat 1 vi tien
    public boolean updateTransaction(int _id, Transaction trans) {
        SQLiteDatabase db = this.getWritableDatabase();

        db.beginTransaction();
        try {
            ContentValues values = new ContentValues();

            values.put(KEY_TEN_GIAO_DICH, trans.getTenGiaoDich());
            values.put(KEY_LOAI_GIAO_DICH, trans.getLoaiGiaoDich());
            values.put(KEY_NGAY_GIAO_DICH, trans.getNgayGiaoDich());
            values.put(KEY_SO_TIEN_GIAO_DICH, trans.getSoTien());
            values.put(KEY_GHI_CHU_GIAO_DICH, trans.getGhiChu());
            values.put(KEY_MA_VI_TIEN, trans.getMaViTien());

            db.update(TABLE_GIAO_DICH, values, KEY_ID_GIAO_DICH + "=" + _id, null);
            db.setTransactionSuccessful();
            return true;

        } catch (Exception e) {
            Log.d(TAG, "ERROR: CAN'T UPADTE TRANSACTION");
            return false;

        } finally {
            db.endTransaction();

        }

    }


    // Xoa 1 giao dich
    public boolean deleteTransaction(int _id) {
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            db.delete(TABLE_GIAO_DICH, KEY_ID_GIAO_DICH + " = " + _id, null);
            return true;

        } catch (Exception e) {
            return false;
        }
    }
}
