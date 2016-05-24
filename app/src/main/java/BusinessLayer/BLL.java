package BusinessLayer;

import android.content.Context;

import java.util.ArrayList;

import DatabaseManager.DatabaseHelper;
import Model.Transaction;
import Model.Wallet;

public class BLL {
    DatabaseHelper DBHelper;

    public BLL(Context context) {
        DBHelper = DatabaseHelper.getsInstances(context);
    }

    public ArrayList<Wallet> getAllWallet() {
        return DBHelper.getListWallet();
    }

    public Wallet getWallet(int _id) {
        return DBHelper.getWallet(_id);
    }

    public boolean addWallet(Wallet wallet) {
        return DBHelper.addWallet(wallet);
    }

    public boolean updateWallet(int _id, Wallet wallet) {
        return DBHelper.updateWallet(_id, wallet);
    }

    public boolean deleteWallet(int _id) {
        return DBHelper.deleteWallet(_id);
    }

    public Transaction getTransaction(int _id) {
        return DBHelper.getTransaction(_id);
    }

    public boolean addTransaction(Transaction trans) {
        return DBHelper.addTransaction(trans);
    }

    public boolean updateTransaction(int _id, Transaction trans) {
        return DBHelper.updateTransaction(_id, trans);
    }

    public boolean deleteTransaction(int _id) {
        return DBHelper.deleteTransaction(_id);
    }

    public ArrayList<Transaction> getListTransaction(int _WalletId, String Month, String Year) {
        return DBHelper.getListTransaction(_WalletId, Month, Year);
    }

    public ArrayList<Transaction> getListTransactionOneDay(int _WalletId, String Day, String Month, String Year) {
        return DBHelper.getListTransactionOneDay(_WalletId, Day, Month, Year);
    }
}
