package blueeaglestudio.mywallet;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.Calendar;

import BusinessLayer.BLL;
import Model.Transaction;
import Model.Wallet;

public class EditWalletActivity extends AppCompatActivity {

    EditText etTenVi;
    EditText etSoDuHienTai;
    Spinner spnDonviTienTe;
    int WalletId;
    Wallet wallet;
    long SoDuHT;

    BLL bll;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_wallet);
        Toolbar toolbar = (Toolbar) findViewById(R.id.tbEditWallet);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        bll = new BLL(EditWalletActivity.this);

        // Lay thong tin vi
        getWallet();

        // Xu ly su kien khi nhan vao EditText etTenVi
        etTenVi = (EditText) findViewById(R.id.etTenVi);
        etTenVi.setText(wallet.getTenVi());

        etSoDuHienTai = (EditText) findViewById(R.id.etSoDuHienTai);
        etSoDuHienTai.setText(Long.toString(wallet.getSoDuHienTai()));

        // Xu ly su kien cho button xac nhan
        FloatingActionButton fabXacNhan = (FloatingActionButton) findViewById(R.id.fabXacNhan);
        fabXacNhan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (getNewWallet()) {
                    boolean isSuccessful = bll.updateWallet(WalletId, wallet);

                    if (isSuccessful) {
                        Toast.makeText(EditWalletActivity.this, "Cập nhật ví thành công", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(EditWalletActivity.this, "Cập nhật ví thất bại. Vui lòng thử lại", Toast.LENGTH_LONG).show();
                    }

                    Intent intent = new Intent(EditWalletActivity.this, TransactionActivity.class);
                    intent.putExtra("Wallet_Id", wallet.get_id());
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
                Intent intent = new Intent(EditWalletActivity.this, TransactionActivity.class);
                intent.putExtra("Wallet_Id", wallet.get_id());
                startActivity(intent);

                finish();
            }
        });


        // Xu ly su kien chon loai don vi tien te
        spnDonviTienTe = (Spinner) findViewById(R.id.spnDonViTienTe);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.strarr_don_vi_tien_te, R.layout.spinner_style);
        adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        spnDonviTienTe.setAdapter(adapter);
    }


    // Lay thong tin cua vi tien
    public void getWallet() {
        // Lay thong tin ma vi
        Intent intent = this.getIntent();

        if (intent.hasExtra("Wallet_Id")) {
            WalletId = intent.getIntExtra("Wallet_Id", -1);
            wallet = bll.getWallet(WalletId);
            SoDuHT = wallet.getSoDuHienTai();
        }
    }


    // Lay thong tin cua vi tien moi
    public boolean getNewWallet() {
        String TenVi = etTenVi.getText().toString();
        String SoDuHienTai = etSoDuHienTai.getText().toString();

        if (TenVi.isEmpty()) {
            Toast.makeText(EditWalletActivity.this, "Tên ví không được để trống", Toast.LENGTH_LONG).show();
            return false;

        } else if (SoDuHienTai.isEmpty()) {
            Toast.makeText(EditWalletActivity.this, "Vui lòng nhập số dư cho ví", Toast.LENGTH_LONG).show();
            return false;

        } else {
            wallet.setTenVi(TenVi);
            wallet.setDonViTienTe(spnDonviTienTe.getSelectedItem().toString());

            try {
                long SoDuMoi = Long.parseLong(etSoDuHienTai.getText().toString());
                wallet.setSoDuHienTai(SoDuMoi);

                // Them mot giao dich moi neu so du moi khac so du hien tai
                if (SoDuMoi != SoDuHT) {
                    Transaction trans = new Transaction();
                    trans.setGhiChu("Điều chỉnh số dư");
                    trans.setNgayGiaoDich(getDay());
                    trans.setMaViTien(WalletId);

                    if (SoDuMoi > SoDuHT) {
                        trans.setTenGiaoDich("Giao Dịch Thu");
                        trans.setLoaiGiaoDich("THU");
                        trans.setSoTien(SoDuMoi - SoDuHT);
                    } else if (SoDuMoi < SoDuHT) {
                        trans.setTenGiaoDich("Giao Dịch Chi");
                        trans.setLoaiGiaoDich("CHI");
                        trans.setSoTien(SoDuHT - SoDuMoi);
                    }

                    bll.addTransaction(trans);
                }
            } catch (NumberFormatException e) {
                Toast.makeText(EditWalletActivity.this, "Vui lòng nhập số dư cho ví", Toast.LENGTH_LONG).show();
                wallet.setSoDuHienTai(0);
                return false;

            }
        }

        return true;
    }


    public String getDay() {
        Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        // Tinh ngay giao dich hom nay
        String Month = (month + 1) + "";
        String Day = day + "";

        if (month + 1 < 10)
            Month = "0" + Month;
        if (day < 10)
            Day = "0" + Day;

        return year + "-" + Month + "-" + Day;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            //onBackPressed();
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
