package blueeaglestudio.mywallet;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;

import BusinessLayer.BLL;
import Model.Transaction;
import Model.Wallet;

public class AddTransactionActivity extends AppCompatActivity {

    Spinner spnLoaiGiaoDich;
    EditText etTenGiaoDich, etSoTienGiaoDich, etGhiChu, etNgayGiaoDich;
    TextView tvDonViTienTe;
    ImageButton ibChonNgayGiaoDich;
    int WalletId;
    Transaction trans;
    int day, month, year;
    String NgayGD, Month, Day;
    Wallet wallet;

    BLL bll;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_transaction);
        Toolbar toolbar = (Toolbar) findViewById(R.id.tbAddTransaction);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        bll = new BLL(AddTransactionActivity.this);

        findIdElement();

        getWallet();

        // Set su kien chon ngay giao dich
        ibChonNgayGiaoDich.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(999);
            }
        });

        // Set thong tin cho Spinner loai don vi tien te
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.strarr_loai_giao_dich, R.layout.spinner_style);
        adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        spnLoaiGiaoDich.setAdapter(adapter);

        // Xu ly su kien cho button xac nhan
        FloatingActionButton fabXacNhan = (FloatingActionButton) findViewById(R.id.fabXacNhan);
        fabXacNhan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                trans = new Transaction();
                if (getTransaction()) {
                    boolean isSuccessful = bll.addTransaction(trans);

                    if (isSuccessful) {
                        // Cap nhat so du cua vi tien
                        if (trans.getLoaiGiaoDich().equals("THU")) {
                            wallet.setSoDuHienTai(wallet.getSoDuHienTai() + trans.getSoTien());
                            bll.updateWallet(WalletId, wallet);
                        } else if (trans.getLoaiGiaoDich().equals("CHI")) {
                            wallet.setSoDuHienTai(wallet.getSoDuHienTai() - trans.getSoTien());
                            bll.updateWallet(WalletId, wallet);
                        }
                        Toast.makeText(AddTransactionActivity.this, "Thêm giao dịch thành công", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(AddTransactionActivity.this, "Thêm giao dịch thất bại. Vui lòng thử lại", Toast.LENGTH_LONG).show();
                    }

                    Intent intent = new Intent(AddTransactionActivity.this, TransactionActivity.class);
                    intent.putExtra("Wallet_Id", WalletId);
                    startActivity(intent);

                    finish();
                }
            }
        });

        // Xu ly su kien cho button huy bo
        FloatingActionButton fabHuyBo = (FloatingActionButton) findViewById(R.id.fabHuyBo);
        fabHuyBo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AddTransactionActivity.this, TransactionActivity.class);
                intent.putExtra("Wallet_Id", WalletId);
                startActivity(intent);

                finish();
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
        etNgayGiaoDich.setText(getDay());
        ibChonNgayGiaoDich = (ImageButton) findViewById(R.id.ibChonNgayGiaoDich);
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


    public String getDay() {
        Calendar c = Calendar.getInstance();
        year = c.get(Calendar.YEAR);
        month = c.get(Calendar.MONTH);
        day = c.get(Calendar.DAY_OF_MONTH);

        // Tinh ngay giao dich hom nay
        Month = (month + 1) + "";
        Day = day + "";

        if (month + 1 < 10)
            Month = "0" + Month;
        if (day < 10)
            Day = "0" + Day;

        NgayGD = year + "-" + Month + "-" + Day;

        return day + ", Tháng " + (month + 1) + ", " + year;
    }


    public boolean getTransaction() {
        String TenGiaoDich = etTenGiaoDich.getText().toString();
        String GhiChu = etGhiChu.getText().toString();
        String LoaiGiaoDich = spnLoaiGiaoDich.getSelectedItem().toString();

        if (TenGiaoDich.isEmpty()) {
            Toast.makeText(AddTransactionActivity.this, "Tên giao dịch không được để trống", Toast.LENGTH_LONG).show();
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
                Toast.makeText(AddTransactionActivity.this, "Vui lòng nhập số tiền giao dịch", Toast.LENGTH_LONG).show();
                return false;
            }
        }

        return true;
    }


    @Override
    protected Dialog onCreateDialog(int id) {
        if (id == 999)
            return new DatePickerDialog(this, DateSetListerner, year, month, day);
        return super.onCreateDialog(id);
        //return null;
    }


    private DatePickerDialog.OnDateSetListener DateSetListerner = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            etNgayGiaoDich.setText(new StringBuilder().append(dayOfMonth).append(", Tháng ").append(monthOfYear + 1).append(", ").append(year));

            // Tinh ngay giao dich do nguoi dung nhap vao
            Month = (monthOfYear + 1) + "";
            Day = dayOfMonth + "";

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
