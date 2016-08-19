package com.jim.pocketaccounter;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Environment;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SlidingPaneLayout;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.jim.pocketaccounter.finance.Account;
import com.jim.pocketaccounter.finance.Currency;
import com.jim.pocketaccounter.finance.CurrencyCost;
import com.jim.pocketaccounter.finance.FinanceManager;
import com.jim.pocketaccounter.finance.RootCategory;
import com.jim.pocketaccounter.finance.SubCategory;
import com.jim.pocketaccounter.syncbase.SyncBase;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import static android.graphics.Color.RED;


/**
 * Created by ismoi on 6/18/2016.
 */

public class SettingsActivity extends PreferenceActivity implements SharedPreferences.OnSharedPreferenceChangeListener {
    private final int PERMISSION_READ_STORAGE = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.layout.settings);
        ListPreference language = (ListPreference) findPreference("language");
        if (language.getValue().matches(getResources().getString(R.string.language_default))) {
            language.setValue(Locale.getDefault().getLanguage());
        }
        updatePrefs("language");
        Preference save = (Preference) findPreference("save");
        save.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                int permission = ContextCompat.checkSelfPermission(SettingsActivity.this,
                        android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
                if (permission != PackageManager.PERMISSION_GRANTED) {
                    if (ActivityCompat.shouldShowRequestPermissionRationale((SettingsActivity.this),
                            android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(SettingsActivity.this);
                        builder.setMessage("Permission to access the SD-CARD is required for this app to Download PDF.")
                                .setTitle("Permission required");
                        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                ActivityCompat.requestPermissions(SettingsActivity.this,
                                        new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                        PERMISSION_READ_STORAGE);
                            }
                        });
                        AlertDialog dialog = builder.create();
                        dialog.show();

                    } else {
                        ActivityCompat.requestPermissions(SettingsActivity.this,
                                new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                PERMISSION_READ_STORAGE);
                    }
                } else {
                    File direct = new File(Environment.getExternalStorageDirectory() + "/Pocket Accounter");
                    if(!direct.exists())
                    {
                        if(direct.mkdir())
                        {
                            exportDB();
                        }
                    } else {
                        exportDB();
                    }
                }
                return true;
            }
        });
        Preference load = (Preference) findPreference("load");
        load.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {

            @Override
            public boolean onPreferenceClick(Preference preference) {
                int permission = ContextCompat.checkSelfPermission(SettingsActivity.this,
                        android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
                if (permission != PackageManager.PERMISSION_GRANTED) {
                    if (ActivityCompat.shouldShowRequestPermissionRationale((SettingsActivity.this),
                            android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(SettingsActivity.this);
                        builder.setMessage("Permission to access the SD-CARD is required for this app to Download PDF.")
                                .setTitle("Permission required");
                        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                ActivityCompat.requestPermissions(SettingsActivity.this,
                                        new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                        PERMISSION_READ_STORAGE);
                            }
                        });
                        AlertDialog dialog = builder.create();
                        dialog.show();

                    } else {
                        ActivityCompat.requestPermissions(SettingsActivity.this,
                                new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                PERMISSION_READ_STORAGE);
                    }
                } else {
                    importDB();
                }
                return false;
            }
        });
        Preference googleBackup = (Preference) findPreference("backup");
        FirebaseUser auth=FirebaseAuth.getInstance().getCurrentUser();
        if(auth==null){googleBackup.setEnabled(false); }
        else
        googleBackup.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                lastUpload();
                return false;
            }
        });
        Preference googleLogout = (Preference) findPreference("logout");
        if(auth==null){googleLogout.setEnabled(false);  }
        else
        googleLogout.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                final android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(SettingsActivity.this);
                builder.setMessage(R.string.are_you_sure_for_log_out)
                        .setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                PocketAccounter.reg.revokeAccess();
                                setResult(RESULT_OK);
                                finish();
                            }
                        }) .setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
                builder.create().show();
                return false;
            }
        });




        Preference sbrosdannix = (Preference) findPreference("sbros");
        sbrosdannix.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {

                final android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(SettingsActivity.this);
                builder.setMessage(R.string.default_settings)
                        .setPositiveButton(getString(R.string.no), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                                dialog.cancel();

                            }
                        }) .setNegativeButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {


                        PocketAccounter.reg.revokeAccess();
                        String DB_PATH;


                        String packageName = getPackageName();
                        DB_PATH = String.format("//data//data//%s//databases//PocketAccounterDatabase", packageName);
                        File db_path=new File(DB_PATH);
                        if(db_path.exists()){
                            db_path.delete();
                        }


                        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(SettingsActivity.this);
                        prefs.edit().clear().apply();
                        getSharedPreferences("infoFirst", MODE_PRIVATE).edit().clear().apply();
                        setResult(1111);
                        finish();
                    }
                });
                builder.create().show();

                return false;
            }
        });


        CheckBoxPreference checkkSecure=(CheckBoxPreference)findPreference("secure");
        checkkSecure.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                if((Boolean)newValue&&PreferenceManager.getDefaultSharedPreferences(SettingsActivity.this).getBoolean("firstclick",true)) {
                    final Dialog dialog = new Dialog(SettingsActivity.this);
                    final View dialogView = getLayoutInflater().inflate(R.layout.password_layout_create, null);
                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    dialog.setContentView(dialogView);

                    final EditText myPassword1=(EditText)dialogView.findViewById(R.id.firstPassword);
                    final EditText myPassword2=(EditText)dialogView.findViewById(R.id.secondPassword);
                    final TextView myFourNumbers=(TextView)dialogView.findViewById(R.id.passwordTextShould);
                    final TextView myRepiatPassword=(TextView)dialogView.findViewById(R.id.passwordRepiat);
                    final TextView Titlee=(TextView)dialogView.findViewById(R.id.idtitle);
                    dialogView.findViewById(R.id.okbuttt).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            myRepiatPassword.setText(getString(R.string.repiat_yout_password));
                            myRepiatPassword.setTextColor(ContextCompat.getColor(SettingsActivity.this,R.color.black_for_secondary_text));

                            if(myPassword1.getText().toString().length()!=4){
                                myPassword1.setText("");
                                myFourNumbers.setTextColor(RED);
                                return;
                            }
                            else
                                myFourNumbers.setTextColor(ContextCompat.getColor(SettingsActivity.this,R.color.black_for_secondary_text));

                            if (myPassword2.getText().toString().length()!=4){
                                myPassword2.setText("");
                                myRepiatPassword.setTextColor(RED);
                                return;

                            }
                            else
                                myRepiatPassword.setTextColor(ContextCompat.getColor(SettingsActivity.this,R.color.black_for_secondary_text));

                            if (myPassword1.getText().toString().matches(myPassword2.getText().toString())){

                                PreferenceManager.getDefaultSharedPreferences(SettingsActivity.this).edit().putBoolean("firstclick",false  ).apply();
                                PreferenceManager.getDefaultSharedPreferences(SettingsActivity.this).edit().putString("password",myPassword2.getText().toString()  ).apply();

                                ((CheckBoxPreference) findPreference("secure")).setChecked(true);
                                dialog.dismiss();
                            }
                            else  {
                                myPassword2.setText("");
                                myRepiatPassword.setText(R.string.please_repait_correct);
                                myRepiatPassword.setTextColor(RED);
                                return;

                            }
                        }
                    });
                    DisplayMetrics dm = getResources().getDisplayMetrics();
                    int width = dm.widthPixels;
                    dialog.getWindow().setLayout(7*width/8, SlidingPaneLayout.LayoutParams.WRAP_CONTENT);
                    dialog.show();

                return false;
                }
                else if(!(Boolean)newValue)
                {
                    Log.d("keeee", "onPreferenceChange: fasleee");
                    final Dialog dialog = new Dialog(SettingsActivity.this);
                    final View dialogView = getLayoutInflater().inflate(R.layout.password_layout_turn_off, null);
                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    dialog.setContentView(dialogView);
                     final EditText myPassword1=(EditText)dialogView.findViewById(R.id.confirmpasword);
                    final  TextView myFourNumbers=(TextView) dialogView.findViewById(R.id.passwordTextShouldRepiat);
                    final Button okBut=(Button) dialogView.findViewById(R.id.okbuttt);
                    okBut.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if(myPassword1.getText().toString().length()!=4){
                                myPassword1.setText("");
                                myFourNumbers.setText(R.string.was_four_numbers);
                                myFourNumbers.setTextColor(RED);
                                return;
                            }
                            else
                                myFourNumbers.setTextColor(ContextCompat.getColor(SettingsActivity.this,R.color.black_for_secondary_text));

                            if (myPassword1.getText().toString().matches(PreferenceManager.getDefaultSharedPreferences(SettingsActivity.this).getString("password",""))){
                                PreferenceManager.getDefaultSharedPreferences(SettingsActivity.this).edit().putBoolean("secure",false  ).apply();
                                ((CheckBoxPreference) findPreference("secure")).setChecked(false);
                                dialog.dismiss();
                            }
                            else  {
                                myPassword1.setText("");
                                myFourNumbers.setText(R.string.try_one_more);
                                myFourNumbers.setTextColor(RED);
                                return;

                            }
                        }
                    });
                    DisplayMetrics dm = getResources().getDisplayMetrics();
                    int width = dm.widthPixels;
                    dialog.getWindow().setLayout(7*width/8, SlidingPaneLayout.LayoutParams.WRAP_CONTENT);
                    dialog.show();


                    return false;
                }
                else
                return true;
            }
        });







    }

    private void lastUpload(){

        final FirebaseUser userik= FirebaseAuth.getInstance().getCurrentUser();

        if(userik!=null){
            if(!SyncBase.isNetworkAvailable(this)){
                final android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(SettingsActivity.this);
                builder.setMessage(R.string.connection_faild)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                               dialog.dismiss();
                                return;
                            }
                        });
                builder.create().show();
                return;
            }
            showProgressDialog(getString(R.string.cheking_user));
            PocketAccounter.mySync.meta_Message(userik.getUid(), new SyncBase.ChangeStateLisMETA() {
                @Override
                public void onSuccses(final long inFormat) {
                    hideProgressDialog();
                    Date datee=new Date();
                    datee.setTime(inFormat);
                    final android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(SettingsActivity.this);
                    builder.setMessage(getString(R.string.sync_want_from_data) + (new SimpleDateFormat("dd.MM.yyyy kk:mm")).format(datee))
                            .setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                       showProgressDialog(getString(R.string.download));
                                    PocketAccounter.mySync.downloadLast(userik.getUid(), new SyncBase.ChangeStateLis() {
                                        @Override
                                        public void onSuccses() {
                                            runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    PocketAccounter.financeManager = new FinanceManager(SettingsActivity.this);
                                                    hideProgressDialog();
                                                    setResult(RESULT_OK);
                                                    finish();
                                                }
                                            });
                                        }
                                        @Override
                                        public void onFailed(String e) {
                                            hideProgressDialog();
                                            Toast.makeText(SettingsActivity.this, R.string.toast_error_connection, Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                            }) .setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            hideProgressDialog();
                            dialog.cancel();

                        }
                    });
                    builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
                        @Override
                        public void onCancel(DialogInterface dialog) {
                            hideProgressDialog();
                        }
                    });
                    builder.create().show();
                }
                @Override
                public void onFailed(Exception e) {
                }
            });
        }


        //google backup
    }

    private void importDB() {
        // TODO Auto-generated method stub
        try {
            File sd = Environment.getExternalStorageDirectory();
            File data  = Environment.getDataDirectory();


            if (sd.canWrite()) {
                String  currentDBPath= "//data//" + getPackageName().toString()
                        + "//databases//" + "PocketAccounterDatabase";
                String backupDBPath  = "/Pocket Accounter/" + "PocketAccounterDatabase";
                File   currentDB= new File(data, currentDBPath);
                File  backupDB = new File(sd, backupDBPath);
//                FileChannel src = new FileInputStream(currentDB).getChannel();
//                FileChannel dst = new FileOutputStream(backupDB).getChannel();
//                dst.transferFrom(src, 0, src.size());
//                src.close();
//                dst.close();
                Toast.makeText(getBaseContext(), backupDB.toString(), Toast.LENGTH_LONG).show();
                final SQLiteDatabase current = SQLiteDatabase.openDatabase(currentDB.getAbsolutePath(), null, SQLiteDatabase.OPEN_READONLY);
                SQLiteDatabase received = SQLiteDatabase.openDatabase(backupDB.getAbsolutePath(), null, SQLiteDatabase.OPEN_READWRITE);
                Log.e("ttt", received.getVersion()+" version");
                Log.e("ttt", current.getVersion()+" version");
                if (current.getVersion() > received.getVersion()) {
                    if (received.getVersion() == 2) {
                        Log.d("sss", "f2->3");
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
                    File currentDB1 = new File(backupDB.getAbsolutePath());
                    File backupDB1 = new File(currentDB.getAbsolutePath());
                    FileChannel src = null, dst = null;
                    try {
                        src = new FileInputStream(currentDB1).getChannel();
                        dst = new FileOutputStream(backupDB1).getChannel();
                        dst.transferFrom(src, 0, src.size());
                        src.close();
                        dst.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                else {
                    File currentDB1 = new File(backupDB.getAbsolutePath());
                    File backupDB1 = new File(currentDB.getAbsolutePath());
                    FileChannel src = null, dst = null;
                    try {
                        src = new FileInputStream(currentDB1).getChannel();
                        dst = new FileOutputStream(backupDB1).getChannel();
                        dst.transferFrom(src, 0, src.size());
                        src.close();
                        dst.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                PocketAccounter.financeManager = new FinanceManager(this);
            }
        } catch (Exception e) {
            Toast.makeText(getBaseContext(), e.toString(), Toast.LENGTH_LONG).show();
        }
    }
    //exporting database
    private void exportDB() {
        // TODO Auto-generated method stub
        try {
            File sd = Environment.getExternalStorageDirectory();
            File data = Environment.getDataDirectory();
            Log.d("sss", "after try before if");
            if (sd.canWrite()) {
                Log.d("sss", "after if");
                String  currentDBPath= "//data//" + getPackageName().toString()+ "//databases//" + "PocketAccounterDatabase";
                String backupDBPath  = "/Pocket Accounter/"+"PocketAccounterDatabase";
                File currentDB = new File(data, currentDBPath);
                File backupDB = new File(sd, backupDBPath);
                FileChannel src = new FileInputStream(currentDB).getChannel();
                FileChannel dst = new FileOutputStream(backupDB).getChannel();
                dst.transferFrom(src, 0, src.size());
                src.close();
                dst.close();
                Log.d("sss", "before toast");
                Toast.makeText(getBaseContext(), backupDB.toString(), Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            Toast.makeText(getBaseContext(), e.toString(), Toast.LENGTH_LONG).show();

        }
    }
    public boolean mListStyled;

    @Override
    public void onResume() {
        super.onResume();
        if (!mListStyled) {
            View rootView = findViewById(android.R.id.content).getRootView();
            if (rootView != null) {
                ListView list = (ListView) rootView.findViewById(android.R.id.list);
                list.setPadding(0, 0, 0, 0);
                list.setDivider(null);
                //any other styling call
                mListStyled = true;
            }
        }
        getPreferenceScreen().getSharedPreferences()
                .registerOnSharedPreferenceChangeListener(this);
    }



    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Unregister the listener whenever a key changes
        getPreferenceScreen().getSharedPreferences()
                .unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        PocketAccounter.openActivity=false;
    }

    private void updatePrefs(String key) {

        if (key.matches("language")) {
            ListPreference preference = (ListPreference) findPreference("language");
            CharSequence entry = ((ListPreference) preference).getEntry();
            preference.setTitle(entry);
        }
        if (key.matches("planningNotif")) {
            final ListPreference planningNotif = (ListPreference) findPreference("planningNotif");
            planningNotif.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {

                @Override
                public boolean onPreferenceClick(Preference preference) {

                    return false;
                }
            });
        }
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        // TODO Auto-generated method stub
        updatePrefs(key);
        if (key.matches("language")) {
            ListPreference preference = (ListPreference) findPreference("language");
            CharSequence entry = ((ListPreference) preference).getEntry();
            preference.setTitle(entry);
            setLocale((String) entry);
        }
    }

    public void setLocale(String lang) {
        Locale myLocale = new Locale(lang);
        Resources res = getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();
        conf.locale = myLocale;
        res.updateConfiguration(conf, dm);
        setResult(RESULT_OK);
        finish();
    }
    private ProgressDialog mProgressDialog;


    public void showProgressDialog(String message) {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setMessage(getString(R.string.download));
            mProgressDialog.setIndeterminate(true);
        }

        mProgressDialog.show();
    }

    public void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.hide();
        }
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
        Log.d("sss", "f3->4");
        String[] resCatsId = getResources().getStringArray(R.array.cat_values);
        String[] resCatIcons = getResources().getStringArray(R.array.cat_icons);
        String[] allIcons = getResources().getStringArray(R.array.icons);
        int[] allIconsId = new int[allIcons.length];
        for (int i=0; i<allIcons.length; i++)
            allIconsId[i] = getResources().getIdentifier(allIcons[i], "drawable", getPackageName());
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
                        int subcatIconArrayId = getResources().getIdentifier(id, "array", getPackageName());
                        if (subcatIconArrayId != 0) {
                            boolean q = false;
                            int s = 0;
                            String[] scn = getResources().getStringArray(subcatIconArrayId);
                            for (int i=0; i<scn.length; i++) {
                                if (scn[i].matches(subCatId)) {
                                    q = true;
                                    s = i;
                                    break;
                                }
                            }
                            if(q){
                                int h = getResources().getIdentifier(id+"_icons", "array", getPackageName());
                                String[] subCatsId = getResources().getStringArray(h);
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
                values.put("category_name", getResources().getString(R.string.no_category));
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
                values.put("category_name", getResources().getString(R.string.no_category));
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
