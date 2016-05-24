package blueeaglestudio.mywallet;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import java.util.ArrayList;
import java.util.HashMap;

import BusinessLayer.BLL;
import Model.Wallet;

public class WalletActivity extends AppCompatActivity {

    public static BLL bll;
    private ArrayList<Wallet> arrWallet;
    private ListView lvDanhSachViTien;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wallet);
        Toolbar toolbar = (Toolbar) findViewById(R.id.tbWallet);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        // Load du lieu vao ListView
        lvDanhSachViTien = (ListView) findViewById(R.id.lvDanhSachViTien);
        bll = new BLL(WalletActivity.this);
        loadListWallet();

        // Xu ly su kien khi nhan vao item vi tien
        lvDanhSachViTien.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(WalletActivity.this, TransactionActivity.class);
                intent.putExtra("Wallet_Id", arrWallet.get(position).get_id());

                startActivity(intent);
            }
        });


        // Xu ly su kien khi nhan them vi moi
        FloatingActionButton fabThemViMoi = (FloatingActionButton) findViewById(R.id.fabThemViMoi);
        fabThemViMoi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent("blueeaglestudio.mywallet.AddWalletActivity"));
            }
        });
    }


    // Load danh sach vi tien
    public void loadListWallet() {

        String from[] = {"w_TenVi", "w_SoDuHienTai"};
        int to[] = {R.id.tvTenViItem, R.id.tvSoDuHienTaiItem};

        arrWallet = bll.getAllWallet();
        ArrayList<HashMap<String, String>> array = new ArrayList<HashMap<String, String>>();

        for (int i = 0; i < arrWallet.size(); i++) {
            HashMap<String, String> hashMap = new HashMap<String, String>();

            hashMap.put("w_TenVi", arrWallet.get(i).getTenVi());
            hashMap.put("w_SoDuHienTai", Long.toString(arrWallet.get(i).getSoDuHienTai())
                    + " " + arrWallet.get(i).getDonViTienTe());

            array.add(hashMap);
        }

        SimpleAdapter adapter = new SimpleAdapter(WalletActivity.this, array, R.layout.layout_wallet_item, from, to);
        lvDanhSachViTien.setAdapter(adapter);
    }

}
