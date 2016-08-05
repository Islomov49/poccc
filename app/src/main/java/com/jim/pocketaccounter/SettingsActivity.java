package com.jim.pocketaccounter;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Environment;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.jim.pocketaccounter.PocketAccounter;
import com.jim.pocketaccounter.R;
import com.jim.pocketaccounter.finance.FinanceManager;
import com.jim.pocketaccounter.syncbase.SyncBase;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;


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
                return false;
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
                File  backupDB= new File(data, currentDBPath);
                File currentDB  = new File(sd, backupDBPath);
                FileChannel src = new FileInputStream(currentDB).getChannel();
                FileChannel dst = new FileOutputStream(backupDB).getChannel();
                dst.transferFrom(src, 0, src.size());
                src.close();
                dst.close();
                Toast.makeText(getBaseContext(), backupDB.toString(), Toast.LENGTH_LONG).show();
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
            if (sd.canWrite()) {
                String  currentDBPath= "//data//" + getPackageName().toString()+ "//databases//" + "PocketAccounterDatabase";
                String backupDBPath  = "/Pocket Accounter/"+"PocketAccounterDatabase";
                File currentDB = new File(data, currentDBPath);
                File backupDB = new File(sd, backupDBPath);
                FileChannel src = new FileInputStream(currentDB).getChannel();
                FileChannel dst = new FileOutputStream(backupDB).getChannel();
                dst.transferFrom(src, 0, src.size());
                src.close();
                dst.close();
                Toast.makeText(getBaseContext(), backupDB.toString(), Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            Toast.makeText(getBaseContext(), e.toString(), Toast.LENGTH_LONG).show();

        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        // Set up a listener whenever a key changes
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
}
