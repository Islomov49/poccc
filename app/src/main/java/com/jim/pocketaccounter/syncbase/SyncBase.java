package com.jim.pocketaccounter.syncbase;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StreamDownloadTask;
import com.google.firebase.storage.UploadTask;
import com.jim.pocketaccounter.R;
import com.jim.pocketaccounter.SettingsActivity;
import com.jim.pocketaccounter.finance.Account;
import com.jim.pocketaccounter.finance.Currency;
import com.jim.pocketaccounter.finance.CurrencyCost;
import com.jim.pocketaccounter.finance.RootCategory;
import com.jim.pocketaccounter.finance.SubCategory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.FileChannel;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by DEV on 10.06.2016.
 */

public class SyncBase {
    private static String DB_PATH;
    private static String DB_NAME;
    private static String PATH_FOR_INPUT;
    private static String META_KEY="CreatAT";
    StorageReference refStorage;
    Context context;
    ChangeStateLis eventer;
    void SyncBase(){

    }
    public SyncBase(StorageReference refStorage, Context context,String databsename) {
        this.refStorage = refStorage;
        this.context = context;
        String packageName = context.getPackageName();
        DB_PATH = String.format("//data//data//%s//databases//", packageName);
        DB_NAME=databsename;
        PATH_FOR_INPUT=DB_PATH+DB_NAME;
        }
    public void uploadBASE(String auth_uid, final ChangeStateLis even){
        if(!isNetworkAvailable()){
            final android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(context);
            builder.setMessage(R.string.connection_faild)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {

                            dialog.dismiss();
                        }
                    });
            builder.create().show();
            even.onFailed("NotNetworkAvailable");
            return;
        }
        try {
            StorageMetadata metadata = new StorageMetadata.Builder()
                    .setContentType("sqlite/db")
                    .setCustomMetadata(META_KEY, Long.toString(System.currentTimeMillis()))
                    .build();
            InputStream stream = new FileInputStream(new File(PATH_FOR_INPUT));
            refStorage.child(auth_uid + "/" + DB_NAME).putStream(stream, metadata).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    even.onSuccses();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    even.onFailed(e.getMessage());

                }
            });
        }
        catch (Exception o){
            even.onFailed(o.getMessage());
        }



    }
   public boolean downloadLast(String auth_uid, final ChangeStateLis even){
       // TODO tekwirib kor dialog ustma ust bob qomayaptimi

       final ProgressDialog A1=new ProgressDialog(context);
       A1.setMessage(context.getString(R.string.please_wait));
       A1.show();

       try {
           final File file = new File(PATH_FOR_INPUT);
           final File fileDirectory = new File(context.getFilesDir(),DB_NAME) ;
           final SQLiteDatabase current = SQLiteDatabase.openDatabase(file.getAbsolutePath(), null, SQLiteDatabase.OPEN_READONLY);

          refStorage.child(auth_uid+"/"+DB_NAME).getFile(fileDirectory).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
              @Override
              public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                  SQLiteDatabase received = SQLiteDatabase.openDatabase(fileDirectory.getAbsolutePath(), null, SQLiteDatabase.OPEN_READWRITE);
                  Log.e("ttt", received.getVersion()+" version");
                  if (current.getVersion() > received.getVersion()) {
                      if (received.getVersion() == 2) {
                          received.execSQL("CREATE TABLE sms_parsing_table ("
                                  + "_id INTEGER PRIMARY KEY AUTOINCREMENT,"
                                  + "number TEXT,"
                                  + "income_words TEXT,"
                                  + "expense_words TEXT,"
                                  + "amount_words TEXT,"
                                  + "account_id TEXT,"
                                  + "currency_id TEXT,"
                                  + "type INTEGER,"
                                  + "empty TEXT"
                                  + ");");
                      }
                      if (received.getVersion() == 3)
                          upgradeFromThreeToFour(received);
                      if (received.getVersion() == 4)
                          upgradeFromFourToFive(received);
                      received.setVersion(current.getVersion());
                      File currentDB = new File(fileDirectory.getAbsolutePath());
                      File backupDB = new File(file.getAbsolutePath());
                      FileChannel src = null, dst = null;
                      try {
                          src = new FileInputStream(currentDB).getChannel();
                          dst = new FileOutputStream(backupDB).getChannel();
                          dst.transferFrom(src, 0, src.size());
                          src.close();
                          dst.close();
                      } catch (IOException e) {
                          e.printStackTrace();
                      }
                  }
                  else {
                      File currentDB = new File(fileDirectory.getAbsolutePath());
                      File backupDB = new File(file.getAbsolutePath());
                      FileChannel src = null, dst = null;
                      try {
                          src = new FileInputStream(currentDB).getChannel();
                          dst = new FileOutputStream(backupDB).getChannel();
                          dst.transferFrom(src, 0, src.size());
                          src.close();
                          dst.close();
                      } catch (IOException e) {
                          e.printStackTrace();
                      }
                  }
                  even.onSuccses();
                  A1.dismiss();
              }
          }).addOnFailureListener(new OnFailureListener() {
              @Override
              public void onFailure(@NonNull Exception e) {

              }
          });




       } catch (Exception e) {
           even.onFailed(e.getMessage());
           e.printStackTrace();
           A1.dismiss();
       }
       return false;
   }
    public void meta_Message(String auth_uid, final ChangeStateLisMETA even){
         refStorage.child(auth_uid+"/"+DB_NAME).getMetadata().addOnSuccessListener(new OnSuccessListener<StorageMetadata>() {
             @Override
             public void onSuccess(StorageMetadata storageMetadata) {
                 even.onSuccses(Long.parseLong(storageMetadata.getCustomMetadata(META_KEY)));
             }
         }).addOnFailureListener(new OnFailureListener() {
             @Override
             public void onFailure(@NonNull Exception e) {
                 even.onFailed(e);
             }
         });
    }

    public interface ChangeStateLis {
        void onSuccses();
        void onFailed(String e);


    }
    public interface ChangeStateLisMETA {
        void onSuccses(long inFormat);
        void onFailed(Exception e);


    }
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
    private void upgradeFromFourToFive(SQLiteDatabase db) {
        Log.d("sss", "f4->5");
        upgradeFromThreeToFour(db);
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
        ArrayList<Account> result = new ArrayList<Account>();
        Cursor curCursor = db.query("currency_table", null, null, null, null, null, null, null);
        Cursor curCostCursor = db.query("currency_costs_table", null, null, null, null, null, null, null);
        ArrayList<Currency> currencies = new ArrayList<Currency>();
        curCursor.moveToFirst();
        while (!curCursor.isAfterLast()) {
            Currency newCurrency = new Currency(curCursor.getString(curCursor.getColumnIndex("currency_name")));
            newCurrency.setAbbr(curCursor.getString(curCursor.getColumnIndex("currency_sign")));
            String currId = curCursor.getString(curCursor.getColumnIndex("currency_id"));
            newCurrency.setId(currId);
            newCurrency.setMain(curCursor.getInt(curCursor.getColumnIndex("currency_main"))!=0);
            curCostCursor.moveToFirst();
            while(!curCostCursor.isAfterLast()) {
                if (curCostCursor.getString(curCostCursor.getColumnIndex("currency_id")).matches(currId)) {
                    CurrencyCost newCurrencyCost = new CurrencyCost();
                    try {
                        Calendar day = Calendar.getInstance();
                        day.setTime(dateFormat.parse(curCostCursor.getString(curCostCursor.getColumnIndex("date"))));
                        newCurrencyCost.setDay(day);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    newCurrencyCost.setCost(curCostCursor.getDouble(curCostCursor.getColumnIndex("cost")));
                    newCurrency.getCosts().add(newCurrencyCost);
                }
                curCostCursor.moveToNext();
            }
            currencies.add(newCurrency);
            curCursor.moveToNext();
        }
        Cursor cursor = db.query("account_table", null, null, null, null, null, null);
        cursor.moveToFirst();
        while(!cursor.isAfterLast()) {
            Account newAccount = new Account();
            newAccount.setName(cursor.getString(cursor.getColumnIndex("account_name")));
            newAccount.setId(cursor.getString(cursor.getColumnIndex("account_id")));
            newAccount.setIcon(cursor.getInt(cursor.getColumnIndex("icon")));
            newAccount.setLimitCurrency(null);
            newAccount.setStartMoneyCurrency(null);
            newAccount.setAmount(0);
            newAccount.setLimited(false);
            newAccount.setLimitSum(0);
            result.add(newAccount);
            cursor.moveToNext();
        }
        db.execSQL("DROP TABLE account_table");
        //account table
        db.execSQL("CREATE TABLE account_table ("
                + "_id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "account_name TEXT,"
                + "account_id TEXT,"
                + "icon INTEGER,"
                + "start_amount REAL,"
                + "start_money_currency_id TEXT,"
                + "limit_currency_id TEXT,"
                + "is_limited INTEGER,"
                + "limit_amount REAL"
                + ");");
        for (Account account : result) {
            Log.d("sss", account.getName());
        }
        Log.d("sss", "account table created");
        ContentValues values = new ContentValues();
        for (Account account : result) {
            values.put("account_name", account.getName());
            values.put("account_id", account.getId());
            values.put("icon", account.getIcon());
            values.put("start_amount", account.getAmount());
            values.put("start_money_currency_id", currencies.get(0).getId());
            values.put("limit_currency_id", currencies.get(0).getId());
            values.put("is_limited", account.isLimited());
            values.put("limit_amount", account.getLimitSum());
            db.insert("account_table", null, values);
        }

        db.execSQL("CREATE TABLE record_photo_table ("
                + "_id INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "photopath TEXT,"
                + "photopathCache TEXT,"
                + "record_id TEXT"
                + ");");

    }
    private void upgradeFromThreeToFour(SQLiteDatabase db) {
        String[] resCatsId = context.getResources().getStringArray(R.array.cat_values);
        String[] resCatIcons = context.getResources().getStringArray(R.array.cat_icons);
        String[] allIcons = context.getResources().getStringArray(R.array.icons);
        int[] allIconsId = new int[allIcons.length];
        for (int i=0; i<allIcons.length; i++)
            allIconsId[i] = context.getResources().getIdentifier(allIcons[i], "drawable", context.getPackageName());
        Cursor catsCursor = db.query("category_table", null, null, null, null, null, null);
        Cursor subCatsCursor = db.query("subcategory_table", null, null, null, null, null, null);
        catsCursor.moveToFirst();
        ArrayList<RootCategory> categories = new ArrayList<>();
        while (!catsCursor.isAfterLast()) {
            RootCategory category = new RootCategory();
            category.setName(catsCursor.getString(catsCursor.getColumnIndex("category_name")));
            category.setType(catsCursor.getInt(catsCursor.getColumnIndex("category_type")));
            String id = catsCursor.getString(catsCursor.getColumnIndex("category_id"));
            boolean catIdFound = false;
            int pos = 0;
            for (int i=0; i<resCatsId.length; i++) {
                if (resCatsId[i].matches(id)) {
                    catIdFound = true;
                    pos = i;
                    break;
                }
            }
            ArrayList<SubCategory> subCategories = new ArrayList<>();
            if (catIdFound) {
                category.setIcon(resCatIcons[pos]);
                subCatsCursor.moveToFirst();
                while(!subCatsCursor.isAfterLast()) {
                    if (id.matches(subCatsCursor.getString(subCatsCursor.getColumnIndex("category_id")))) {
                        Log.d("sss", id);
                        SubCategory subCategory = new SubCategory();
                        subCategory.setName(subCatsCursor.getString(subCatsCursor.getColumnIndex("subcategory_name")));
                        String subCatId = subCatsCursor.getString(subCatsCursor.getColumnIndex("subcategory_id"));
                        Log.d("sss", subCatId);
                        int subcatIconArrayId = context.getResources().getIdentifier(id, "array", context.getPackageName());
                        if (subcatIconArrayId != 0) {
                            boolean q = false;
                            int s = 0;
                            String[] scn = context.getResources().getStringArray(subcatIconArrayId);
                            for (int i=0; i<scn.length; i++) {
                                if (scn[i].matches(subCatId)) {
                                    q = true;
                                    s = i;
                                    break;
                                }
                            }
                            if(q){
                                int h = context.getResources().getIdentifier(id+"_icons", "array", context.getPackageName());
                                String[] subCatsId = context.getResources().getStringArray(h);
                                subCategory.setIcon(subCatsId[s]);
                            }
                            else {
                                int subCatIconId = subCatsCursor.getInt(subCatsCursor.getColumnIndex("icon"));
                                boolean f = false;
                                int p = 0;
                                for (int i=0; i<allIconsId.length; i++) {
                                    if (subCatIconId == allIconsId[i]) {
                                        f = true;
                                        p = i;
                                        break;
                                    }
                                }
                                if (f)
                                    subCategory.setIcon(allIcons[p]);
                                else
                                    subCategory.setIcon("category_not_selected");
                            }
                        } else {
                            boolean s = false;
                            int a = 0;
                            for (int i=0; i<allIconsId.length; i++) {
                                if (allIconsId[i] == subCatsCursor.getInt(subCatsCursor.getColumnIndex("icon"))) {
                                    s = true;
                                    a = i;
                                    break;
                                }
                            }
                            if (s)
                                subCategory.setIcon(allIcons[a]);
                            else
                                subCategory.setIcon("category_not_selected");
                        }
                        subCategory.setId(subCatId);
                        subCategories.add(subCategory);
                    }
                    subCatsCursor.moveToNext();
                }
            }
            else {
                int iconId = catsCursor.getInt(catsCursor.getColumnIndex("icon"));
                boolean found = false;
                pos = 0;
                for (int i=0; i<allIconsId.length; i++) {
                    if (allIconsId[i] == iconId) {
                        found = true;
                        pos = i;
                        break;
                    }
                }
                if (found)
                    category.setIcon(allIcons[pos]);
                else
                    category.setIcon("category_not_selected");
                subCatsCursor.moveToFirst();
                while (!subCatsCursor.isAfterLast()) {
                    if (id.matches(subCatsCursor.getString(subCatsCursor.getColumnIndex("category_id")))) {
                        SubCategory subCategory = new SubCategory();
                        subCategory.setName(subCatsCursor.getString(subCatsCursor.getColumnIndex("subcategory_name")));
                        String subCatId = subCatsCursor.getString(subCatsCursor.getColumnIndex("subcategory_id"));
                        subCategory.setId(subCatId);
                        iconId = subCatsCursor.getInt(subCatsCursor.getColumnIndex("icon"));
                        found = false;
                        pos = 0;
                        for (int i = 0; i < allIconsId.length; i++) {
                            if (allIconsId[i] == iconId) {
                                found = true;
                                pos = i;
                                break;
                            }
                        }
                        if (found)
                            subCategory.setIcon(allIcons[pos]);
                        else
                            subCategory.setIcon("category_not_selected");
                        subCategories.add(subCategory);
                    }
                    subCatsCursor.moveToNext();
                }
            }
            category.setId(id);
            category.setSubCategories(subCategories);
            categories.add(category);
            catsCursor.moveToNext();
        }
        db.execSQL("DROP TABLE category_table");
        db.execSQL("DROP TABLE subcategory_table");
        db.execSQL("create table category_table ("
                + "_id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "category_name TEXT,"
                + "category_id TEXT,"
                + "category_type INTEGER,"
                + "icon TEXT,"
                + "empty TEXT"
                + ");");
        //subcategries table
        db.execSQL("create table subcategory_table ("
                + "_id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "subcategory_name TEXT,"
                + "subcategory_id TEXT,"
                + "category_id TEXT,"
                + "icon TEXT,"
                + "empty TEXT"
                + ");");
        //saving categories begin
        for (int i=0; i<categories.size(); i++) {
            ContentValues values = new ContentValues();
            values.put("category_name", categories.get(i).getName());
            values.put("category_id", categories.get(i).getId());
            values.put("category_type", categories.get(i).getType());
            values.put("icon", categories.get(i).getIcon());
            db.insert("category_table", null, values);
            for (int j=0; j<categories.get(i).getSubCategories().size(); j++) {
                values.clear();
                values.put("subcategory_name", categories.get(i).getSubCategories().get(j).getName());
                values.put("subcategory_id", categories.get(i).getSubCategories().get(j).getId());
                values.put("category_id", categories.get(i).getId());
                values.put("icon", categories.get(i).getSubCategories().get(j).getIcon());
                db.insert("subcategory_table", null, values);
            }
        }
        //saving categories end
        Cursor incomesCursor = db.query("incomes_table", null, null, null, null, null, null);
        Log.d("sss", incomesCursor.getCount()+"");
        ArrayList<String> incomesId = new ArrayList<>();
        incomesCursor.moveToFirst();
        while (!incomesCursor.isAfterLast()) {
            incomesId.add(incomesCursor.getString(incomesCursor.getColumnIndex("category_id")));
            incomesCursor.moveToNext();
        }
        db.execSQL("DROP TABLE incomes_table");
        db.execSQL("create table incomes_table ("
                + "_id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "category_name TEXT,"
                + "category_id TEXT,"
                + "category_type INTEGER,"
                + "icon TEXT,"
                + "empty TEXT"
                + ");");
        for (int i=0; i<incomesId.size(); i++) {
            ContentValues values = new ContentValues();
            if (incomesId.get(i) == null) {
                values.put("category_name", context.getResources().getString(R.string.no_category));
                db.insert("incomes_table", null, values);
                continue;
            }
            for (int j=0; j<categories.size(); j++) {
                if (incomesId.get(i).matches(categories.get(j).getId())) {
                    values.put("category_name", categories.get(j).getName());
                    values.put("category_id", categories.get(j).getId());
                    values.put("category_type", categories.get(j).getType());
                    values.put("icon", categories.get(j).getIcon());
                    db.insert("incomes_table", null, values);
                    break;
                }
            }
        }
        Cursor expensesCursor = db.query("expanses_table", null, null, null, null, null, null);
        ArrayList<String> expensesId = new ArrayList<>();
        expensesCursor.moveToFirst();
        while (!expensesCursor.isAfterLast()) {
            expensesId.add(expensesCursor.getString(incomesCursor.getColumnIndex("category_id")));
            expensesCursor.moveToNext();
        }

        db.execSQL("DROP TABLE expanses_table");
        db.execSQL("create table expanses_table ("
                + "_id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "category_name TEXT,"
                + "category_id TEXT,"
                + "category_type INTEGER,"
                + "icon TEXT,"
                + "empty TEXT"
                + ");");
        for (int i=0; i<expensesId.size(); i++) {
            ContentValues values = new ContentValues();
            if (expensesId.get(i) == null) {
                values.put("category_name", context.getResources().getString(R.string.no_category));
                db.insert("expanses_table", null, values);
                continue;
            }
            for (int j=0; j<categories.size(); j++) {
                if (expensesId.get(i).matches(categories.get(j).getId())) {
                    values.put("category_name", categories.get(j).getName());
                    values.put("category_id", categories.get(j).getId());
                    values.put("category_type", categories.get(j).getType());
                    values.put("icon", categories.get(j).getIcon());
                    db.insert("expanses_table", null, values);
                    break;
                }
            }
        }
    }
}
