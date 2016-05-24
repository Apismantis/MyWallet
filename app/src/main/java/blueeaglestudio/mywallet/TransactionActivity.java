package blueeaglestudio.mywallet;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import BusinessLayer.BLL;
import Model.Transaction;
import Model.Wallet;

public class TransactionActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    public static final int REQUEST_CODE_WALLET = 1;
    public static final int RESULT_CODE_WALLET_ID = 2;
    public static final int REQUEST_CODE_TRANSACTION = 3;

    private int WalletId;
    private Wallet wallet;
    private TextView tvTenViNavBar, tvTenViAppBa, tvSoDuHienTaiAppBar, tvThangHienTai;
    private TextView tvSoTienDaChi, tvSoTienDaThu, tvSoDuTongQuan;
    private ImageView ivThangTruoc, ivThangSau;
    private int TransID;
    private int NowMonth, NowYear;

    BLL bll;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // An ten activity
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        bll = new BLL(TransactionActivity.this);

        findElementId();

        // Lay thong tin vi tien
        getWallet();

        //Toast.makeText(getApplicationContext(), "WalletId: " + WalletId, Toast.LENGTH_LONG).show();

        // Lay thang va nam hien tai
        String[] ToDay = getToDay();

        // Cap nhat cac du lieu vao UI
        updateData(ToDay[0], ToDay[1]);

        // Su kien them giao dich
        FloatingActionButton fabThemGiaoDich = (FloatingActionButton) findViewById(R.id.fabThemGiaoDichMoi);
        fabThemGiaoDich.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(TransactionActivity.this, AddTransactionActivity.class);
                intent.putExtra("Wallet_Id", WalletId);
                startActivity(intent);
            }
        });

        // Thang truoc
        ivThangTruoc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String[] Time = getDay(NowMonth, NowYear, -1);
                updateData(Time[0], Time[1]);
            }
        });

        // Thang sau
        ivThangSau.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String[] Time = getDay(NowMonth, NowYear, 1);
                updateData(Time[0], Time[1]);
            }
        });


        // Drawer
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }


    private void findElementId() {
        // Lay id cac thanh phan UI
        tvTenViNavBar = (TextView) findViewById(R.id.tvTenViNavBar);
        tvTenViAppBa = (TextView) findViewById(R.id.tvTenViAppBar);
        tvSoDuHienTaiAppBar = (TextView) findViewById(R.id.tvSoDuHienTaiAppBar);
        tvThangHienTai = (TextView) findViewById(R.id.tvThangHienTai);
        LinearLayout vOverView = (LinearLayout) findViewById(R.id.vOverView);
        ivThangTruoc = (ImageView) findViewById(R.id.ivThangTruoc);
        ivThangSau = (ImageView) findViewById(R.id.ivThangSau);

        // Hien thi OverReview
        LayoutInflater li = (LayoutInflater) getBaseContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View OverViewView = li.inflate(R.layout.layout_overview_item, null);
        vOverView.removeAllViews();
        vOverView.addView(OverViewView);
        tvSoTienDaChi = (TextView) vOverView.findViewById(R.id.tvSoTienDaChi);
        tvSoTienDaThu = (TextView) vOverView.findViewById(R.id.tvSoTienDaThu);
        tvSoDuTongQuan = (TextView) vOverView.findViewById(R.id.tvSoDuTongQuan);
    }


    public void getWallet() {
        // Lay thong tin ma vi
        Intent intent = this.getIntent();

        if (intent.hasExtra("Wallet_Id")) {
            WalletId = intent.getIntExtra("Wallet_Id", -1);
            wallet = bll.getWallet(WalletId);
        }
    }


    // Lay thang, nam hien tai
    public String[] getToDay() {
        Calendar c = Calendar.getInstance();
        NowMonth = c.get(Calendar.MONTH) + 1;
        NowYear = c.get(Calendar.YEAR);

        String Year = Integer.toString(NowYear);
        String Month = Integer.toString(NowMonth);

        // Cac thong tin khac
        if (c.get(Calendar.MONTH) + 1 < 10) {
            Month = "0" + Month;
        }

        return new String[]{Month, Year};
    }

    // Lay thang, nam truoc hoac sau
    public String[] getDay(int Month, int Year, int cMonth) {
        Month += cMonth;

        if (Month == 13) {
            Month = 1;
            Year++;
        } else if (Month == 0) {
            Month = 12;
            Year--;
        }

        NowMonth = Month;
        NowYear = Year;

        String NewMonth = Month + "";
        String NewYear = Year + "";

        if (Month < 10) {
            NewMonth = "0" + NewMonth;
        }

        return new String[]{NewMonth, NewYear};
    }

    // Cap nhat cac thanh phan du lieu vao UI
    public void updateData(String Month, String Year) {

        if (wallet == null)
            return;
        ;

        // Cac thong tin co ban
        tvTenViNavBar.setText(wallet.getTenVi());
        tvTenViAppBa.setText(wallet.getTenVi());
        tvSoDuHienTaiAppBar.setText(String.format("%d %s", wallet.getSoDuHienTai(), wallet.getDonViTienTe()));
        tvThangHienTai.setText(String.format("Tháng %s/ %s", Month, Year));

        // Lay danh sach tat ca giao dich trong thang hien tai
        ArrayList<Transaction> AllTrans = bll.getListTransaction(WalletId, Month, Year);
        ArrayList<String> AllDay = new ArrayList<String>();
        LinearLayout TransGroupByDayView = (LinearLayout) findViewById(R.id.DanhSachGiaoDich);
        TransGroupByDayView.removeAllViews();

        // Hien thi danh sach cac giao dich gom nhom theo tung ngay
        for (Transaction trans : AllTrans) {
            String NgayGD = trans.getNgayGiaoDich();

            if (!AllDay.contains(NgayGD)) {
                AllDay.add(NgayGD);
                Log.d("Get Trans By Day: ", NgayGD);
                String[] NgayGDHienTai = NgayGD.split("-");

                LayoutInflater li = (LayoutInflater) getBaseContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                final View TGroupByDayView = li.inflate(R.layout.layout_transaction_by_day, null);
                TransGroupByDayView.addView(TGroupByDayView);

                TextView tvNgayGiaoDich = (TextView) TGroupByDayView.findViewById(R.id.tvNgayGiaoDich);
                TextView tvSoTienChiTieuTrongNgay = (TextView) TGroupByDayView.findViewById(R.id.tvSoTienChiTieuTrongNgay);
                ListView lvDanhSachGiaoDichTheoNgay = (ListView) TGroupByDayView.findViewById(R.id.lvDanhSachGiaoDichTheoNgay);

                // Hien thi cac thong tin
                tvNgayGiaoDich.setText(String.format("%s Tháng %s, %s", NgayGDHienTai[2], NgayGDHienTai[1], NgayGDHienTai[0]));

                // Lay danh sach cac giao dich trong ngay
                setAdapterForListTransInDay(lvDanhSachGiaoDichTheoNgay, NgayGDHienTai[2], NgayGDHienTai[1], NgayGDHienTai[0]);

                // Tong tien da chi la 1 con so am
                long SoTienChiTieuTrongNgay = getSumMoneyOnTheDay(NgayGDHienTai[2], NgayGDHienTai[1], NgayGDHienTai[0]);
                tvSoTienChiTieuTrongNgay.setText(String.format("%s", Long.toString(SoTienChiTieuTrongNgay)));
            }
        }

        // Lay thong tin tong so tien da thu, da chi
        long TongTienDaThu = 0;
        long TongTienDaChi = 0;
        for (Transaction trans : AllTrans) {
            if (trans.getLoaiGiaoDich().equals("THU")) {
                TongTienDaThu += trans.getSoTien();
            } else if (trans.getLoaiGiaoDich().equals("CHI")) {
                TongTienDaChi -= trans.getSoTien();
            }
        }

        tvSoTienDaChi.setText(String.format("%s", Long.toString(TongTienDaChi)));
        tvSoTienDaThu.setText(String.format("%s", Long.toString(TongTienDaThu)));
        tvSoDuTongQuan.setText(String.format("%s %s", Long.toString(TongTienDaThu + TongTienDaChi), wallet.getDonViTienTe()));
    }


    // Hien thi danh sach cac giao dich trong 1 ngay
    public void setAdapterForListTransInDay(ListView lvDanhSachGiaoDichTheoNgay, String Day, String Month, String Year) {

        String from[] = {"t_TenGiaoDich", "t_SoTienGiaoDich", "t_GhiChu"};
        int to[] = {R.id.tvTenGiaoDich, R.id.tvSoTienGiaoDich, R.id.tvGhiChuGiaoDich};

        final ArrayList<Transaction> arrTrans = bll.getListTransactionOneDay(WalletId, Day, Month, Year);
        ArrayList<HashMap<String, String>> array = new ArrayList<HashMap<String, String>>();

        for (int i = 0; i < arrTrans.size(); i++) {
            HashMap<String, String> hashMap = new HashMap<String, String>();

            hashMap.put("t_TenGiaoDich", arrTrans.get(i).getTenGiaoDich());
            hashMap.put("t_SoTienGiaoDich", Long.toString(arrTrans.get(i).getSoTien()));
            hashMap.put("t_GhiChu", arrTrans.get(i).getGhiChu());

            array.add(hashMap);
        }

        SimpleAdapter adapter = new SimpleAdapter(TransactionActivity.this, array, R.layout.layout_transaction_item, from, to);
        lvDanhSachGiaoDichTheoNgay.setAdapter(adapter);
        setListViewHeightBasedOnChildren(lvDanhSachGiaoDichTheoNgay);

        // Dang ky tao Context Menu
        registerForContextMenu(lvDanhSachGiaoDichTheoNgay);

        // Set su kien khi nguoi dung nhan vao 1 item
        // se chuyen qua giao dien xem giao dich
        lvDanhSachGiaoDichTheoNgay.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(TransactionActivity.this, EditTransactionActivity.class);
                intent.putExtra("Trans_Id", arrTrans.get(position).get_id());
                intent.putExtra("Wallet_Id", WalletId);
                startActivityForResult(intent, REQUEST_CODE_TRANSACTION);
            }
        });

        lvDanhSachGiaoDichTheoNgay.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                TransID = arrTrans.get(position).get_id();
                return false;
            }
        });
    }


    public long getSumMoneyOnTheDay(String Day, String Month, String Year) {
        long SoTienChiTieuTrongNgay = 0;
        final ArrayList<Transaction> arrTrans = bll.getListTransactionOneDay(WalletId, Day, Month, Year);

        for (int i = 0; i < arrTrans.size(); i++) {
            if (arrTrans.get(i).getLoaiGiaoDich().equals("THU")) {
                SoTienChiTieuTrongNgay += arrTrans.get(i).getSoTien();
            } else if (arrTrans.get(i).getLoaiGiaoDich().equals("CHI")) {
                SoTienChiTieuTrongNgay -= arrTrans.get(i).getSoTien();
            }
        }

        return SoTienChiTieuTrongNgay;
    }


    // Dat chieu cao cua ListView dua theo nhung item children
    public static void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null)
            return;

        int desiredWidth = View.MeasureSpec.makeMeasureSpec(listView.getWidth(), View.MeasureSpec.UNSPECIFIED);
        int totalHeight = 0;
        View view = null;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            view = listAdapter.getView(i, view, listView);
            if (i == 0)
                view.setLayoutParams(new ViewGroup.LayoutParams(desiredWidth, AbsListView.LayoutParams.WRAP_CONTENT));

            view.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
            totalHeight += view.getMeasuredHeight();
        }
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
    }


    // Phuong thuc khoi tao menu
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        //menu.setHeaderIcon(R.drawable.ui_icon);
        menu.setHeaderTitle("Giao Dịch");

        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.transaction_menu, menu);
    }

    // Xu ly khi lua chon tren menu
    @Override
    public boolean onContextItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.mTransXemChiTiet:
                Intent intent = new Intent(TransactionActivity.this, EditTransactionActivity.class);
                intent.putExtra("Trans_Id", TransID);
                intent.putExtra("Wallet_Id", WalletId);
                startActivityForResult(intent, REQUEST_CODE_TRANSACTION);
                break;

            case R.id.mTransSuaGiaoDich:
                Intent i = new Intent(TransactionActivity.this, EditTransactionActivity.class);
                i.putExtra("Trans_Id", TransID);
                i.putExtra("Wallet_Id", WalletId);
                i.putExtra("Is_Edit", true);
                startActivityForResult(i, REQUEST_CODE_TRANSACTION);
                break;

            case R.id.mTransXoaGiaoDich:
                new AlertDialog.Builder(TransactionActivity.this)
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
                                Transaction trans = bll.getTransaction(TransID);
                                WalletActivity.bll.deleteTransaction(TransID);

                                // Khoi phuc lai phan tien cua giao dich cu
                                if (trans.getLoaiGiaoDich().equals("THU")) {
                                    wallet.setSoDuHienTai(wallet.getSoDuHienTai() - trans.getSoTien());
                                } else if (trans.getLoaiGiaoDich().equals("CHI")) {
                                    wallet.setSoDuHienTai(wallet.getSoDuHienTai() + trans.getSoTien());
                                }

                                WalletActivity.bll.updateWallet(WalletId, wallet);

                                Intent intent = new Intent(TransactionActivity.this, TransactionActivity.class);
                                intent.putExtra("Wallet_Id", WalletId);
                                startActivity(intent);
                                finish();
                            }
                        })
                        .show();
                break;
        }

        return super.onContextItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
            finish();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_XemViKhac) {
            startActivity(new Intent("blueeaglestudio.mywallet.WalletActivity"));
            finish();

        } else if (id == R.id.nav_SuaThongTinVi) {
            Intent intent = new Intent(TransactionActivity.this, EditWalletActivity.class);
            intent.putExtra("Wallet_Id", wallet.get_id());
            startActivityForResult(intent, REQUEST_CODE_WALLET);

        } else if (id == R.id.nav_XoaViNay) {

            new AlertDialog.Builder(this)
                    .setTitle(R.string.xoa_vi_nay)
                    .setMessage(R.string.ban_co_muon_xoa_vi_nay)
                    .setPositiveButton(R.string.huy_bo, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    })
                    .setNegativeButton(R.string.xoa_vi, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            bll.deleteWallet(WalletId);
                            startActivity(new Intent("blueeaglestudio.mywallet.WalletActivity"));
                            finish();
                        }
                    })
                    .show();


        } else if (id == R.id.nav_DieuChinhSoDu) {
            Intent intent = new Intent(TransactionActivity.this, EditBalanceActivity.class);
            intent.putExtra("Wallet_Id", wallet.get_id());
            startActivityForResult(intent, REQUEST_CODE_WALLET);

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Log.d("Debug", "On activity result");

        if (requestCode == REQUEST_CODE_WALLET || requestCode == REQUEST_CODE_TRANSACTION) {
            Log.d("debug", "Back from Wallet");

            if (resultCode == Activity.RESULT_CANCELED) {
                WalletId = data.getIntExtra("Wallet_Id", -1);
                wallet = bll.getWallet(WalletId);

                Log.d("REQUEST_CODE", WalletId + "");
            }

        }
    }
}
