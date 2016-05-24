package blueeaglestudio.mywallet;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import BusinessLayer.BLL;
import Model.Wallet;

public class AddWalletActivity extends AppCompatActivity {

    EditText etTenVi;
    EditText etSoDuHienTai;
    Spinner spnDonviTienTe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_wallet);
        Toolbar toolbar = (Toolbar) findViewById(R.id.tbAddWallet);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Xu ly su kien khi nhan vao EditText etTenVi
        etTenVi = (EditText) findViewById(R.id.etTenVi);

        etSoDuHienTai = (EditText) findViewById(R.id.etSoDuHienTai);
        etSoDuHienTai.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (etSoDuHienTai.getText().toString().isEmpty())
                    etSoDuHienTai.setText("0");
            }
        });

        // Xu ly su kien cho button xac nhan
        FloatingActionButton fabXacNhan = (FloatingActionButton) findViewById(R.id.fabXacNhan);
        fabXacNhan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String TenVi = etTenVi.getText().toString();
                String SoDuHienTai = etSoDuHienTai.getText().toString();

                if (TenVi.isEmpty()) {
                    Toast.makeText(AddWalletActivity.this, "Tên ví không được để trống", Toast.LENGTH_LONG).show();
                } else if (SoDuHienTai.isEmpty()) {
                    Toast.makeText(AddWalletActivity.this, "Vui lòng nhập số dư cho ví", Toast.LENGTH_LONG).show();
                } else {

                    BLL bll = new BLL(AddWalletActivity.this);
                    Wallet wallet = new Wallet();

                    // Thiet lap thong tin vi
                    wallet.setTenVi(TenVi);
                    wallet.setDonViTienTe(spnDonviTienTe.getSelectedItem().toString());

                    try {
                        wallet.setSoDuHienTai(Long.parseLong(etSoDuHienTai.getText().toString()));
                    } catch (NumberFormatException e) {
                        wallet.setSoDuHienTai(0);
                    }

                    // Thong bao ket qua them vi
                    boolean isSuccessful = bll.addWallet(wallet);
                    if (isSuccessful) {
                        Toast.makeText(AddWalletActivity.this, "Thêm ví thành công", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(AddWalletActivity.this, "Thêm ví thất bại. Vui lòng thử lại", Toast.LENGTH_LONG).show();
                    }

                    startActivity(new Intent("blueeaglestudio.mywallet.WalletActivity"));
                    finish();
                }
            }
        });

        // Xu ly su kien cho button huy bo
        FloatingActionButton fabHuyBo = (FloatingActionButton) findViewById(R.id.fabHuyBo);
        fabHuyBo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent("blueeaglestudio.mywallet.WalletActivity"));
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

}
