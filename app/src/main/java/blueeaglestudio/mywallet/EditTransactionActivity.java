package blueeaglestudio.mywallet;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;

import BusinessLayer.BLL;
import Model.Transaction;
import Model.Wallet;

public class EditTransactionActivity extends AppCompatActivity {

    Spinner spnLoaiGiaoDich;
    EditText etTenGiaoDich, etSoTienGiaoDich, etGhiChu, etNgayGiaoDich;
    FloatingActionButton fabXacNhan, fabHuyBo;
    TextView tvDonViTienTe;
    ImageButton ibChonNgayGiaoDich;
    LinearLayout lnSuaGiaoDich;
    int WalletId, TransID;
    Transaction trans;
    String NgayGD, LoaiGiaoDichCu;
    long SoTienGDCu = 0;
    boolean IsEdit = false;
    Wallet wallet;
    ArrayAdapter<CharSequence> adapter;

    BLL bll;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_transaction);
        Toolbar toolbar = (Toolbar) findViewById(R.id.tbEditTransaction);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        bll = new BLL(EditTransactionActivity.this);

        findIdElement();

        getWallet();

        getTransaction();

        // Set thong tin cho Spinner loai don vi tien te
        adapter = ArrayAdapter.createFromResource(this,
                R.array.strarr_loai_giao_dich, R.layout.spinner_style);
        adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        spnLoaiGiaoDich.setAdapter(adapter);

        loadData();

        // Ngan khong cho sua
        if (IsEdit) {
            enableDisableEditView(lnSuaGiaoDich, true);
            fabXacNhan.setImageResource(R.mipmap.ic_done);
            fabHuyBo.setImageResource(R.drawable.abc_ic_clear_mtrl_alpha);
        } else {
            enableDisableEditView(lnSuaGiaoDich, false);
        }

        // Set su kien chon ngay giao dich
        ibChonNgayGiaoDich.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(999);
            }
        });

        // Xu ly su kien cho button xac nhan
        fabXacNhan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!lnSuaGiaoDich.isEnabled()) {
                    enableDisableEditView(lnSuaGiaoDich, true);
                    fabXacNhan.setImageResource(R.mipmap.ic_done);
                    fabHuyBo.setImageResource(R.drawable.abc_ic_clear_mtrl_alpha);
                    getSupportActionBar().setTitle(R.string.title_activity_edit_transaction);

                } else {

                    trans = new Transaction();
                    if (getNewTransaction()) {
                        boolean isSuccessful = bll.updateTransaction(TransID, trans);

                        if (isSuccessful) {
                            // Khoi phuc phan tien cua giao dich cu
                            restoreMoney(SoTienGDCu, LoaiGiaoDichCu);

                            // Cap nhat so du cua vi tien
                            if (trans.getLoaiGiaoDich().equals("THU")) {
                                wallet.setSoDuHienTai(wallet.getSoDuHienTai() + trans.getSoTien());
                                bll.updateWallet(WalletId, wallet);
                            } else if (trans.getLoaiGiaoDich().equals("CHI")) {
                                wallet.setSoDuHienTai(wallet.getSoDuHienTai() - trans.getSoTien());
                                bll.updateWallet(WalletId, wallet);
                            }

                            Toast.makeText(EditTransactionActivity.this, "Cập nhật giao dịch thành công", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(EditTransactionActivity.this, "Cập nhật giao dịch thất bại. Vui lòng thử lại", Toast.LENGTH_LONG).show();
                        }

                        Intent intent = new Intent(EditTransactionActivity.this, TransactionActivity.class);
                        intent.putExtra("Wallet_Id", WalletId);
                        startActivity(intent);

                        finish();
                    }

                    // Cap nhat khong thanh cong
                    // Toast thong bao
                }
            }
        });

        // Xu ly su kien cho button huy bo
        fabHuyBo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!lnSuaGiaoDich.isEnabled()) {

                    new AlertDialog.Builder(EditTransactionActivity.this)
                            .setTitle(R.string.xoa_giao_dich)
                            .setMessage("Bạn có thực sự muốn xóa giao dịch này không?")
                            .setPositiveButton(R.string.huy_bo, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                }
                            })
                            .setNegativeButton(R.string.xoa_giao_dich, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    bll.deleteTransaction(TransID);

                                    // Khoi phuc lai phan tien cua giao dich cu
                                    restoreMoney(SoTienGDCu, LoaiGiaoDichCu);
                                    bll.updateWallet(WalletId, wallet);

                                    Intent intent = new Intent(EditTransactionActivity.this, TransactionActivity.class);
                                    intent.putExtra("Wallet_Id", WalletId);
                                    startActivity(intent);
                                    finish();
                                }
                            })
                            .show();
                } else {
                    Intent intent = new Intent(EditTransactionActivity.this, TransactionActivity.class);
                    intent.putExtra("Wallet_Id", WalletId);
                    startActivity(intent);

                    finish();
                }
            }
        });
    }


    public void findIdElement() {
        etTenGiaoDich = (EditText) findViewById(R.id.etTenGiaoDich);
        etSoTienGiaoDich = (EditText) findViewById(R.id.etSoTienGiaoDich);
        etGhiChu = (EditText) findViewById(R.id.etGhiChu);
        tvDonViTienTe = (TextView) findViewById(R.id.tvDonViTienTe);
        ibChonNgayGiaoDich = (ImageButton) findViewById(R.id.ibChonNgayGiaoDich);
        spnLoaiGiaoDich = (Spinner) findViewById(R.id.spnLoaiGiaoDich);
        etNgayGiaoDich = (EditText) findViewById(R.id.etNgayGiaoDich);
        ibChonNgayGiaoDich = (ImageButton) findViewById(R.id.ibChonNgayGiaoDich);
        lnSuaGiaoDich = (LinearLayout) findViewById(R.id.lnSuaGiaoDich);
        fabXacNhan = (FloatingActionButton) findViewById(R.id.fabXacNhan);
        fabHuyBo = (FloatingActionButton) findViewById(R.id.fabHuyBo);
    }


    public void enableDisableEditView(View view, boolean enabled) {

        view.setEnabled(enabled);

        // Neu view la mot viewgroup
        if (view instanceof ViewGroup) {
            ViewGroup viewGroup = (ViewGroup) view;
            for (int i = 0; i < viewGroup.getChildCount(); i++) {
                enableDisableEditView(viewGroup.getChildAt(i), enabled);
            }
        }
    }


    public void getWallet() {
        // Lay thong tin ma vi
        Intent intent = this.getIntent();

        if (intent.hasExtra("Wallet_Id")) {
            WalletId = intent.getIntExtra("Wallet_Id", -1);
            wallet = bll.getWallet(WalletId);
            tvDonViTienTe.setText(wallet.getDonViTienTe());
        }
    }


    public void getTransaction() {
        // Lay thong tin ma giao dich
        Intent intent = this.getIntent();

        if (intent.hasExtra("Trans_Id")) {
            TransID = intent.getIntExtra("Trans_Id", -1);
            trans = bll.getTransaction(TransID);
            SoTienGDCu = trans.getSoTien();
            LoaiGiaoDichCu = trans.getLoaiGiaoDich();
        }

        if (intent.hasExtra("Is_Edit")) {
            IsEdit = intent.getBooleanExtra("Is_Edit", false);
        } else {
            IsEdit = false;
        }
    }


    // Loa du lieu vao cac view
    public void loadData() {

        if (trans != null) {
            etTenGiaoDich.setText(trans.getTenGiaoDich());
            etSoTienGiaoDich.setText(String.format(Long.toString(trans.getSoTien())));
            etGhiChu.setText(trans.getGhiChu());

            String[] NgayGDHT = trans.getNgayGiaoDich().split("-");
            etNgayGiaoDich.setText(String.format("%s, Tháng %s, %s", NgayGDHT[2], NgayGDHT[1], NgayGDHT[0]));
            NgayGD = trans.getNgayGiaoDich();

            // Set loai giao dich
            int posItem = adapter.getPosition(trans.getLoaiGiaoDich());
            spnLoaiGiaoDich.setSelection(posItem);

            // Đat lai title cho activity
            getSupportActionBar().setTitle(trans.getTenGiaoDich());
        }
    }


    public boolean getNewTransaction() {
        String TenGiaoDich = etTenGiaoDich.getText().toString();
        String GhiChu = etGhiChu.getText().toString();
        String LoaiGiaoDich = spnLoaiGiaoDich.getSelectedItem().toString();

        if (TenGiaoDich.isEmpty()) {
            Toast.makeText(EditTransactionActivity.this, "Tên giao dịch không được để trống", Toast.LENGTH_LONG).show();
            return false;
        } else {
            try {
                long SoTienGiaoDich = Long.parseLong(etSoTienGiaoDich.getText().toString());

                trans.setTenGiaoDich(TenGiaoDich);
                trans.setLoaiGiaoDich(LoaiGiaoDich);
                trans.setSoTien(SoTienGiaoDich);
                trans.setGhiChu(GhiChu);
                trans.setNgayGiaoDich(NgayGD);
                trans.setMaViTien(WalletId);

            } catch (NumberFormatException e) {
                Toast.makeText(EditTransactionActivity.this, "Vui lòng nhập số tiền giao dịch", Toast.LENGTH_LONG).show();
                return false;
            }
        }

        return true;
    }


    public void restoreMoney(long money, String LoaiGiaoDichCu) {
        if (LoaiGiaoDichCu.equals("THU")) {
            wallet.setSoDuHienTai(wallet.getSoDuHienTai() - money);
        } else if (LoaiGiaoDichCu.equals("CHI")) {
            wallet.setSoDuHienTai(wallet.getSoDuHienTai() + money);
        }
    }


    @Override
    protected Dialog onCreateDialog(int id) {
        if (id == 999) {
            Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);
            return new DatePickerDialog(this, DateSetListerner, year, month, day);
        }
        return super.onCreateDialog(id);
        //return null;
    }


    private DatePickerDialog.OnDateSetListener DateSetListerner = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            etNgayGiaoDich.setText(new StringBuilder().append(dayOfMonth).append(" Tháng ").append(monthOfYear + 1).append(", ").append(year));

            // Tinh ngay giao dich do nguoi dung nhap vao
            String Month = (monthOfYear + 1) + "";
            String Day = dayOfMonth + "";

            if (monthOfYear + 1 < 10)
                Month = "0" + Month;
            if (dayOfMonth < 10)
                Day = "0" + Day;

            NgayGD = year + "-" + Month + "-" + Day;
        }
    };


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            finish();
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void finish() {
        Intent intent = new Intent();
        intent.putExtra("Wallet_Id", WalletId);
        setResult(TransactionActivity.RESULT_CODE_WALLET_ID, intent);

        super.finish();
    }

}
