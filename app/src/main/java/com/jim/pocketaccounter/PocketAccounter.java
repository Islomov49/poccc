package com.jim.pocketaccounter;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.AnimationDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.jim.pocketaccounter.credit.notificat.AlarmReceiver;
import com.jim.pocketaccounter.credit.notificat.NotificationManagerCredit;
import com.jim.pocketaccounter.debt.DebtBorrowFragment;
import com.jim.pocketaccounter.finance.Account;
import com.jim.pocketaccounter.finance.FinanceManager;
import com.jim.pocketaccounter.finance.FinanceRecord;
import com.jim.pocketaccounter.helper.CircleImageView;
import com.jim.pocketaccounter.helper.FABIcon;
import com.jim.pocketaccounter.helper.LeftMenuAdapter;
import com.jim.pocketaccounter.helper.LeftMenuItem;
import com.jim.pocketaccounter.helper.LeftSideDrawer;
import com.jim.pocketaccounter.helper.PockerTag;
import com.jim.pocketaccounter.helper.PocketAccounterGeneral;
import com.jim.pocketaccounter.helper.record.RecordExpanseView;
import com.jim.pocketaccounter.helper.record.RecordIncomesView;
import com.jim.pocketaccounter.intropage.IntroIndicator;
import com.jim.pocketaccounter.syncbase.SignInGoogleMoneyHold;
import com.jim.pocketaccounter.syncbase.SyncBase;
import com.jim.pocketaccounter.widget.WidgetKeys;
import com.jim.pocketaccounter.widget.WidgetProvider;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

import static android.util.TypedValue.COMPLEX_UNIT_DIP;
import static com.jim.pocketaccounter.R.color.toolbar_text_color;

public class PocketAccounter extends AppCompatActivity {
    TextView userName, userEmail;
    CircleImageView userAvatar;
    public static Toolbar toolbar;
    public static LeftSideDrawer drawer;
    private ListView lvLeftMenu;
    public static FinanceManager financeManager;
    private FragmentManager fragmentManager;
    SharedPreferences spref;
    SharedPreferences.Editor ed;
    private RelativeLayout rlRecordsMain, rlRecordIncomes, rlRecordBalance;
    private TextView tvRecordIncome, tvRecordBalanse, tvRecordExpanse;
    private ImageView ivToolbarMostRight, ivToolbarExcel;
    private RecordExpanseView expanseView;
    private RecordIncomesView incomeView;
    private Calendar date;
    private Spinner spToolbar;
    public static SignInGoogleMoneyHold reg;
    boolean downloadnycCanRest = true;
    public static SyncBase mySync;
    Uri imageUri;
    ImageView fabIconFrame;
    public static final int key_for_restat = 10101;
    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference storageRef = storage.getReferenceFromUrl("gs://pocket-accounter.appspot.com");
    DownloadImageTask imagetask;
    View mainRoot;
    private AnimationDrawable mAnimationDrawable;
    private NotificationManagerCredit notific;

    public static boolean PRESSED = false;

    @Override
    protected void onStart() {
        super.onStart();


    }
    int WidgetID;
    public static boolean keyboardVisible=false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String lang = prefs.getString("language", getResources().getString(R.string.language_default));
        if (lang.matches(getResources().getString(R.string.language_default)))
            setLocale(Locale.getDefault().getLanguage());
        else
            setLocale(lang);
        setContentView(R.layout.pocket_accounter);
        spref = getSharedPreferences("infoFirst", MODE_PRIVATE);
        ed = spref.edit();
        if (spref.getBoolean("FIRST_KEY", true)) {
            try {
                Intent first = new Intent(this, IntroIndicator.class);
                startActivity(first);
                finish();
            } finally {

            }
        }
        financeManager = new FinanceManager(this);
        fragmentManager = getSupportFragmentManager();



        mainRoot =findViewById(R.id.main);
        mainRoot.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                int heightDiff = mainRoot.getRootView().getHeight() - mainRoot.getHeight();
                if (heightDiff > dpToPx(PocketAccounter.this, 200)) { // if more than 200 dp, it's probably a keyboard...
                    keyboardVisible=true;
                }
                else {
                    keyboardVisible=false;
                }
            }
        });


        mySync = new SyncBase(storageRef, this, "PocketAccounterDatabase");
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitleTextColor(ContextCompat.getColor(this, toolbar_text_color));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        drawer = new LeftSideDrawer(this);
        drawer.setLeftBehindContentView(R.layout.activity_behind_left_simple);
        lvLeftMenu = (ListView) findViewById(R.id.lvLeftMenu);
        fillLeftMenu();

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            userName.setText(user.getDisplayName());
            userEmail.setText(user.getEmail());
            try{
                if (user.getPhotoUrl() != null) {
                    imagetask = new DownloadImageTask(userAvatar);
                    imagetask.execute(user.getPhotoUrl().toString());
                    imageUri = user.getPhotoUrl();
                }
          }
            catch (Exception o){

            }
        }
        rlRecordsMain = (RelativeLayout) findViewById(R.id.rlRecordExpanses);
        tvRecordIncome = (TextView) findViewById(R.id.tvRecordIncome);
        tvRecordBalanse = (TextView) findViewById(R.id.tvRecordBalanse);
        rlRecordIncomes = (RelativeLayout) findViewById(R.id.rlRecordIncomes);
        ivToolbarMostRight = (ImageView) findViewById(R.id.ivToolbarMostRight);
        spToolbar = (Spinner) toolbar.findViewById(R.id.spToolbar);
        ivToolbarExcel = (ImageView) findViewById(R.id.ivToolbarExcel);
        rlRecordBalance = (RelativeLayout) findViewById(R.id.rlRecordBalance);
        rlRecordBalance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (PRESSED) return;
                replaceFragment(new RecordDetailFragment(date));
                PRESSED = true;
            }
        });
        tvRecordExpanse = (TextView) findViewById(R.id.tvRecordExpanse);
        date = Calendar.getInstance();
        initialize(date);
        notific = new NotificationManagerCredit(PocketAccounter.this);

        switch (getIntent().getIntExtra("TIP", 0)) {
            case AlarmReceiver.TO_DEBT:
                replaceFragment(new DebtBorrowFragment(), PockerTag.DEBTS);
                break;
            case AlarmReceiver.TO_CRIDET:
                replaceFragment(new CreditTabLay(), PockerTag.CREDITS);
                break;
        }
        boolean notif = prefs.getBoolean("general_notif", true);
        if (!notif) {
            (new Thread(new Runnable() {
                @Override
                public void run() {
                    try {

                        notific.cancelAllNotifs();
                    } catch (Exception o) {
                    }
                }
            })).start();
        }
    }

    public void setLocale(String lang) {
        Locale myLocale = new Locale(lang);
        Resources res = getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();
        conf.locale = myLocale;
        res.updateConfiguration(conf, dm);
    }

    public Calendar getDate() {
        return date;
    }

    public static float dpToPx(Context context, float valueInDp) {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        return TypedValue.applyDimension(COMPLEX_UNIT_DIP, valueInDp, metrics);
    }

    public void initialize(final Calendar date) {
        if(keyboardVisible){

            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(mainRoot.getWindowToken(), 0);
            mainRoot.postDelayed(new Runnable() {
                @Override
                public void run() {
                    keyboardVisible=false;
                    initialize(new GregorianCalendar());

                }
            },100);

        }

        PRESSED = false;
        toolbar.setTitle(getResources().getString(R.string.app_name));
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_drawer);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawer.openLeftSide();
            }
        });
        spToolbar.setVisibility(View.GONE);
        ivToolbarMostRight.setImageResource(R.drawable.finance_calendar);
        ivToolbarMostRight.setVisibility(View.VISIBLE);
        ivToolbarExcel.setImageResource(R.drawable.finance_trans);
        ivToolbarExcel.setVisibility(View.VISIBLE);
        ivToolbarExcel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                replaceFragment(new AccountManagementFragment(date));
            }
        });
        ivToolbarMostRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog dialog = new Dialog(PocketAccounter.this);
                View dialogView = getLayoutInflater().inflate(R.layout.date_picker, null);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(dialogView);
                final DatePicker dp = (DatePicker) dialogView.findViewById(R.id.dp);
                ImageView ivDatePickOk = (ImageView) dialogView.findViewById(R.id.ivDatePickOk);
                ivDatePickOk.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Calendar calendar = Calendar.getInstance();
                        calendar.set(Calendar.YEAR, dp.getYear());
                        calendar.set(Calendar.MONTH, dp.getMonth());
                        calendar.set(Calendar.DAY_OF_MONTH, dp.getDayOfMonth());
                        PocketAccounter.this.date = (Calendar) calendar.clone();
                        initialize(PocketAccounter.this.date);
                        dialog.dismiss();
                    }
                });
                ImageView ivDatePickCancel = (ImageView) dialogView.findViewById(R.id.ivDatePickCancel);
                ivDatePickCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
                dialog.show();
            }
        });
        calculateBalance(date);
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd,LLL yyyy");
        toolbar.setSubtitle(dateFormat.format(date.getTime()));
        DisplayMetrics dm = getResources().getDisplayMetrics();
        int width = dm.widthPixels;
        int height = dm.heightPixels;
        int side = 0;
        if (height * 0.55 > width)
            side = width;
        else
            side = (int) (height * 0.55);
        expanseView = new RecordExpanseView(this, date);
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(side, side);
        lp.addRule(RelativeLayout.CENTER_HORIZONTAL);
        expanseView.setLayoutParams(lp);
        rlRecordsMain.removeAllViews();
        rlRecordsMain.addView(expanseView);
        incomeView = new RecordIncomesView(this, date);
        RelativeLayout.LayoutParams lpIncomes = new RelativeLayout.LayoutParams(side, side / 4 + (int) (getResources().getDimension(R.dimen.thirty_dp)));
        lpIncomes.addRule(RelativeLayout.CENTER_HORIZONTAL);
        incomeView.setLayoutParams(lpIncomes);
        rlRecordIncomes.removeAllViews();
        rlRecordIncomes.addView(incomeView);
    }

    public void calculateBalance(Calendar date) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String balanceSolve = prefs.getString("balance_solve", "0");
        String whole = "0", currentDay = "1";
        Calendar beginTime = (Calendar) date.clone();
        beginTime.set(Calendar.HOUR_OF_DAY, 0);
        beginTime.set(Calendar.MINUTE, 0);
        beginTime.set(Calendar.SECOND, 0);
        beginTime.set(Calendar.MILLISECOND, 0);
        Calendar endTime = (Calendar) date.clone();
        endTime.set(Calendar.HOUR_OF_DAY, 23);
        endTime.set(Calendar.MINUTE, 59);
        endTime.set(Calendar.SECOND, 59);
        endTime.set(Calendar.MILLISECOND, 59);
        ArrayList<FinanceRecord> records = new ArrayList<FinanceRecord>();
        if (balanceSolve.matches(whole)) {
            for (int i = 0; i < PocketAccounter.financeManager.getRecords().size(); i++) {
                if (PocketAccounter.financeManager.getRecords().get(i).getDate().compareTo(endTime) <= 0)
                    records.add(PocketAccounter.financeManager.getRecords().get(i));
            }
        }
        else {
            for (int i = 0; i < PocketAccounter.financeManager.getRecords().size(); i++) {
                if (PocketAccounter.financeManager.getRecords().get(i).getDate().compareTo(beginTime)>=0 &&
                        PocketAccounter.financeManager.getRecords().get(i).getDate().compareTo(endTime) <= 0)
                    records.add(PocketAccounter.financeManager.getRecords().get(i));
            }
        }
        double income = 0.0, expanse = 0.0, balance = 0.0;
        for (int i = 0; i < records.size(); i++) {
            if (records.get(i).getCategory().getType() == PocketAccounterGeneral.INCOME)
                income = income + PocketAccounterGeneral.getCost(records.get(i));
            else
                expanse = expanse + PocketAccounterGeneral.getCost(records.get(i));
        }
        for (Account account:PocketAccounter.financeManager.getAccounts())
            income = income + PocketAccounterGeneral.getCost(date, account.getCurrency(), account.getAmount());
        balance = income - expanse;
        String mainCurrencyAbbr = PocketAccounter.financeManager.getMainCurrency().getAbbr();
        DecimalFormat decFormat = new DecimalFormat("0.00");
        tvRecordIncome.setText(decFormat.format(income) + mainCurrencyAbbr);
        tvRecordExpanse.setText(decFormat.format(expanse) + mainCurrencyAbbr);
        tvRecordBalanse.setText(decFormat.format(balance) + mainCurrencyAbbr);
    }

    @Override
    protected void onStop() {
        super.onStop();
        financeManager.saveRecords();
        SharedPreferences sPref;
        sPref = getSharedPreferences("infoFirst", MODE_PRIVATE);
        WidgetID=sPref.getInt(WidgetKeys.SPREF_WIDGET_ID,-1);
        if(WidgetID>=0){
            if(AppWidgetManager.INVALID_APPWIDGET_ID!=WidgetID)
                WidgetProvider.updateWidget(this, AppWidgetManager.getInstance(this),
                        WidgetID);
        }
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        boolean notif = prefs.getBoolean("general_notif", true);
        if (notif) {
            try {
                notific.cancelAllNotifs();
                notific.notificSetDebt();
                notific.notificSetCredit();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        else{
            notific.cancelAllNotifs();
        }

        try{

            if (imagetask != null)
                imagetask.cancel(true);
            if (imagetask != null) {
                imagetask.cancel(true);
                imagetask = null;
            }
        }
        catch (Exception o){

        }
    }

    private void fillLeftMenu() {
        String[] cats = getResources().getStringArray(R.array.drawer_cats);
        String[] financeSubItemTitles = getResources().getStringArray(R.array.finance_subitems);
        String[] financeSubItemIcons = getResources().getStringArray(R.array.finance_subitem_icons);
        String[] statisticsSubItemTitles = getResources().getStringArray(R.array.statistics_subitems);
        String[] statisticsSubItemIcons = getResources().getStringArray(R.array.statistics_subitems_icons);
        String[] debtSubItemTitles = getResources().getStringArray(R.array.debts_subitems);
        String[] debtSubItemIcons = getResources().getStringArray(R.array.debts_subitem_icons);
        ArrayList<LeftMenuItem> items = new ArrayList<LeftMenuItem>();

        userAvatar = (CircleImageView) findViewById(R.id.userphoto);
        userName = (TextView) findViewById(R.id.tvToolbarName);
        userEmail = (TextView) findViewById(R.id.tvGoogleMail);

        FABIcon fabIcon = (FABIcon) findViewById(R.id.fabDrawerNavIcon);
        fabIconFrame = (ImageView) findViewById(R.id.iconFrameForAnim);

        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            fabIconFrame.setBackgroundResource(R.drawable.cloud_anim);
            mAnimationDrawable = (AnimationDrawable) fabIconFrame.getBackground();

        } else
            fabIconFrame.setBackgroundResource(R.drawable.cloud_sign_in);


        reg = new SignInGoogleMoneyHold(PocketAccounter.this, new SignInGoogleMoneyHold.UpdateSucsess() {
            @Override
            public void updateToSucsess() {
                final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if (user != null) {
                    imagetask = new DownloadImageTask(userAvatar);
                    userName.setText(user.getDisplayName());
                    userEmail.setText(user.getEmail());
                    if (user.getPhotoUrl() != null) {
                        try{
                            imagetask.execute(user.getPhotoUrl().toString());

                        }catch (Exception o){}
                        imageUri = user.getPhotoUrl();
                    }

                    showProgressDialog(getString(R.string.cheking_user));
                    PocketAccounter.mySync.meta_Message(user.getUid(), new SyncBase.ChangeStateLisMETA() {
                        @Override
                        public void onSuccses(final long inFormat) {
                            hideProgressDialog();
                            Date datee = new Date();
                            fabIconFrame.setBackgroundResource(R.drawable.cloud_anim);
                            mAnimationDrawable = (AnimationDrawable) fabIconFrame.getBackground();
                            datee.setTime(inFormat);
                            final android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(PocketAccounter.this);
                            builder.setMessage(getString(R.string.sync_last_data_sign_up) + (new SimpleDateFormat("dd.MM.yyyy kk:mm")).format(datee))
                                    .setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            showProgressDialog(getString(R.string.download));
                                            PocketAccounter.mySync.downloadLast(user.getUid(), new SyncBase.ChangeStateLis() {
                                                @Override
                                                public void onSuccses() {
                                                    runOnUiThread(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            hideProgressDialog();
                                                            PocketAccounter.financeManager = new FinanceManager(PocketAccounter.this);
                                                            initialize(new GregorianCalendar());
                                                            if (!drawer.isClosed()) {
                                                                drawer.close();
                                                            }
                                                        }
                                                    });
                                                }

                                                @Override
                                                public void onFailed(String e) {
                                                    hideProgressDialog();
                                                }
                                            });
                                        }
                                    }).setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {
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
                            hideProgressDialog();
                            fabIconFrame.setBackgroundResource(R.drawable.cloud_anim);
                            mAnimationDrawable = (AnimationDrawable) fabIconFrame.getBackground();

                        }
                    });
                }
            }

            @Override
            public void updateToFailed() {
                userName.setText(R.string.try_later);
                userEmail.setText(R.string.err_con);
            }
        });

        fabIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final FirebaseUser userim = FirebaseAuth.getInstance().getCurrentUser();
                if (userim != null) {

                    (new Handler()).postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            final android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(PocketAccounter.this);
                            builder.setMessage(R.string.sync_message)
                                    .setPositiveButton(R.string.sync_short, new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            mAnimationDrawable.start();
                                            financeManager.saveAllDatas();
                                            mySync.uploadBASE(userim.getUid(), new SyncBase.ChangeStateLis() {
                                                @Override
                                                public void onSuccses() {
                                                    mAnimationDrawable.stop();
                                                    fabIconFrame.setBackgroundResource(R.drawable.cloud_sucsess);
                                                    (new Handler()).postDelayed(new Runnable() {
                                                        @Override
                                                        public void run() {

                                                            fabIconFrame.setBackgroundResource(R.drawable.cloud_anim);
                                                            mAnimationDrawable = (AnimationDrawable) fabIconFrame.getBackground();

                                                        }
                                                    }, 2000);
                                                }

                                                @Override
                                                public void onFailed(String e) {
                                                    mAnimationDrawable.stop();
                                                    fabIconFrame.setBackgroundResource(R.drawable.cloud_error);
                                                    (new Handler()).postDelayed(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            fabIconFrame.setBackgroundResource(R.drawable.cloud_anim);
                                                            mAnimationDrawable = (AnimationDrawable) fabIconFrame.getBackground();

                                                        }
                                                    }, 2000);
                                                }
                                            });

                                        }
                                    }).setNegativeButton(getString(R.string.cancel1), new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            });
                            builder.create().show();
                        }
                    }, 150);
                } else {
                    drawer.close();
                    (new Handler()).postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (spref.getBoolean("FIRSTSYNC", true)) {
                                reg.openDialog();
                            } else
                                reg.regitUser();
                        }
                    }, 150);
                }
            }
        });
        LeftMenuItem main = new LeftMenuItem(cats[0], R.drawable.drawer_home);
        main.setGroup(true);
        items.add(main);
        LeftMenuItem finance = new LeftMenuItem(cats[1], R.drawable.drawer_finance);
        finance.setGroup(true);
        items.add(finance);
        for (int i = 0; i < financeSubItemTitles.length; i++) {
            int resId = getResources().getIdentifier(financeSubItemIcons[i], "drawable", getPackageName());
            LeftMenuItem subItem = new LeftMenuItem(financeSubItemTitles[i], resId);
            subItem.setGroup(false);
            items.add(subItem);
        }
        LeftMenuItem debts = new LeftMenuItem(cats[3], R.drawable.drawer_debts);
        debts.setGroup(true);
        items.add(debts);
        for (int i = 0; i < debtSubItemTitles.length; i++) {
            int resId = getResources().getIdentifier(debtSubItemIcons[i], "drawable", getPackageName());
            LeftMenuItem subItem = new LeftMenuItem(debtSubItemTitles[i], resId);
            subItem.setGroup(false);
            items.add(subItem);
        }
        LeftMenuItem statistics = new LeftMenuItem(cats[2], R.drawable.drawer_statistics);
        statistics.setGroup(true);
        items.add(statistics);
        for (int i = 0; i < statisticsSubItemTitles.length; i++) {
            int resId = getResources().getIdentifier(statisticsSubItemIcons[i], "drawable", getPackageName());
            LeftMenuItem subItem = new LeftMenuItem(statisticsSubItemTitles[i], resId);
            subItem.setGroup(false);
            items.add(subItem);
        }
        LeftMenuItem smsParse = new LeftMenuItem(cats[4], R.drawable.drawer_sms);
        smsParse.setGroup(true);
        items.add(smsParse);
        LeftMenuItem settings = new LeftMenuItem(cats[5], R.drawable.drawer_settings);
        settings.setGroup(true);
        items.add(settings);
        LeftMenuItem rateApp = new LeftMenuItem(cats[6], R.drawable.drawer_rate);
        rateApp.setGroup(true);
        items.add(rateApp);
        LeftMenuItem share = new LeftMenuItem(cats[7], R.drawable.drawer_share);
        share.setGroup(true);
        items.add(share);
        LeftMenuItem writeToUs = new LeftMenuItem(cats[8], R.drawable.drawer_letter_us);
        writeToUs.setGroup(true);
        items.add(writeToUs);
        LeftMenuAdapter adapter = new LeftMenuAdapter(this, items);
        lvLeftMenu.setAdapter(adapter);
        lvLeftMenu.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                if (getSupportFragmentManager().getBackStackEntryCount() == 0 && position == 0) {
                    findViewById(R.id.change).setVisibility(View.VISIBLE);
                } else {
                    findViewById(R.id.change).setVisibility(View.GONE);
                }
                drawer.closeLeftSide();
                drawer.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        switch (position) {
                            case 0:
                                findViewById(R.id.change).setVisibility(View.VISIBLE);
                                PRESSED = false;
                                if (getSupportFragmentManager().getBackStackEntryCount() != 0) {
                                    FragmentManager fm = getSupportFragmentManager();
                                    for (int i = 0; i < fm.getBackStackEntryCount(); i++)
                                        fm.popBackStack();
                                    initialize(date);
                                }
                                break;
                            case 1:
                                  if (getSupportFragmentManager().getBackStackEntryCount() == 1
                                        && getSupportFragmentManager().findFragmentById(R.id.flMain).getTag()
                                        .matches(com.jim.pocketaccounter.debt.PockerTag.CURRENCY))
                                    return;
                                replaceFragment(new CurrencyFragment(), PockerTag.CURRENCY);
                                break;
                            case 2:

                                if (getSupportFragmentManager().getBackStackEntryCount() == 1
                                        && getSupportFragmentManager().findFragmentById(R.id.flMain).getTag()
                                        .matches(com.jim.pocketaccounter.debt.PockerTag.CURRENCY))
                                    return;
                                replaceFragment(new CurrencyFragment(), PockerTag.CURRENCY);
                                //Currency management
                                break;
                            case 3:

                                if (getSupportFragmentManager().getBackStackEntryCount() == 1
                                        && getSupportFragmentManager().findFragmentById(R.id.flMain).getTag()
                                        .matches(com.jim.pocketaccounter.debt.PockerTag.CATEGORY))
                                    return;
                                replaceFragment(new CategoryFragment(), PockerTag.CATEGORY);
                                //Category management
                                break;
                            case 4:

                                if (getSupportFragmentManager().getBackStackEntryCount() == 1
                                        && getSupportFragmentManager().findFragmentById(R.id.flMain).getTag()
                                        .matches(com.jim.pocketaccounter.debt.PockerTag.ACCOUNT))
                                    return;
                                replaceFragment(new AccountFragment(), PockerTag.ACCOUNT);
                                //Accounting management
                                break;
                            case 5:

                                if (getSupportFragmentManager().getBackStackEntryCount() == 1
                                        && getSupportFragmentManager().findFragmentById(R.id.flMain).getTag()
                                        .matches(com.jim.pocketaccounter.debt.PockerTag.CREDITS))
                                    return;
                                replaceFragment(new CreditTabLay(), PockerTag.CREDITS);
                                break;
                            case 6:

                                if (getSupportFragmentManager().getBackStackEntryCount() == 1
                                        && getSupportFragmentManager().findFragmentById(R.id.flMain).getTag()
                                        .matches(com.jim.pocketaccounter.debt.PockerTag.CREDITS))
                                    return;
                                replaceFragment(new CreditTabLay(), PockerTag.CREDITS);
                                //Statistics by account
                                break;
                            case 7:

                                if (getSupportFragmentManager().getBackStackEntryCount() == 1
                                        && getSupportFragmentManager().findFragmentById(R.id.flMain).getTag()
                                        .matches(com.jim.pocketaccounter.debt.PockerTag.DEBTS))
                                    return;
                                replaceFragment(new DebtBorrowFragment(), PockerTag.DEBTS);
                                //Statistics by income/expanse
                                break;
                            case 8:

                                if (getSupportFragmentManager().getBackStackEntryCount() == 1
                                        && getSupportFragmentManager().findFragmentById(R.id.flMain).getTag()
                                        .matches(com.jim.pocketaccounter.debt.PockerTag.REPORT_ACCOUNT))
                                    return;
                                replaceFragment(new ReportByAccountFragment(), PockerTag.REPORT_ACCOUNT);
                                break;
                            case 9:

                                if (getSupportFragmentManager().getBackStackEntryCount() == 1
                                        && getSupportFragmentManager().findFragmentById(R.id.flMain).getTag()
                                        .matches(com.jim.pocketaccounter.debt.PockerTag.REPORT_ACCOUNT))
                                    return;
                                replaceFragment(new ReportByAccountFragment(), PockerTag.REPORT_ACCOUNT);
                                // accounting debt
                                break;
                            case 10:

                                if (getSupportFragmentManager().getBackStackEntryCount() == 1
                                        && getSupportFragmentManager().findFragmentById(R.id.flMain).getTag()
                                        .matches(com.jim.pocketaccounter.debt.PockerTag.REPORT_INCOM_EXPENSE))
                                    return;
                                replaceFragment(new TableBarFragment(), PockerTag.REPORT_INCOM_EXPENSE);
                                break;
                            case 11:

                                if (getSupportFragmentManager().getBackStackEntryCount() == 1
                                        && getSupportFragmentManager().findFragmentById(R.id.flMain).getTag()
                                        .matches(com.jim.pocketaccounter.debt.PockerTag.REPORT_CATEGORY))
                                    return;
                                replaceFragment(new ReportByCategory(), PockerTag.REPORT_CATEGORY);
                                break;
                            case 12:


                                if (getSupportFragmentManager().getBackStackEntryCount() == 1
                                        && getSupportFragmentManager().findFragmentById(R.id.flMain).getTag()
                                        .matches(com.jim.pocketaccounter.debt.PockerTag.ACCOUNT_MANAGEMENT))
                                    return;
                                replaceFragment(new SMSParseFragment(), com.jim.pocketaccounter.debt.PockerTag.ACCOUNT_MANAGEMENT);
                                break;
                            case 13:

                                Intent settings = new Intent(PocketAccounter.this, SettingsActivity.class);
                                for (int i = 0; i < fragmentManager.getBackStackEntryCount(); i++) {
                                    fragmentManager.popBackStack();
                                }
                                startActivityForResult(settings, key_for_restat);
                                break;
                            case 14:
                                if(keyboardVisible){
                                    InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                                    imm.hideSoftInputFromWindow(mainRoot.getWindowToken(), 0);}

                                findViewById(R.id.change).setVisibility(View.VISIBLE);
                                Intent rate_app_web = new Intent(Intent.ACTION_VIEW);
                                rate_app_web.setData(Uri.parse(getString(R.string.rate_app_web)));
                                startActivity(rate_app_web);
                                break;
                            case 15:
                                if(keyboardVisible){
                                    InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                                    imm.hideSoftInputFromWindow(mainRoot.getWindowToken(), 0);}

                                findViewById(R.id.change).setVisibility(View.VISIBLE);
                                Intent Email = new Intent(Intent.ACTION_SEND);
                                Email.setType("text/email");
                                Email.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.share_app));
                                Email.putExtra(Intent.EXTRA_TEXT, getString(R.string.share_app_text));
                                startActivity(Intent.createChooser(Email, getString(R.string.share_app)));
                                break;
                            case 16:
                                if(keyboardVisible){
                                    InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                                    imm.hideSoftInputFromWindow(mainRoot.getWindowToken(), 0);}

                                findViewById(R.id.change).setVisibility(View.VISIBLE);
                                openGmail(PocketAccounter.this, new String[]{getString(R.string.to_email)}, getString(R.string.feedback_subject), getString(R.string.feedback_content));
                                break;
                        }

                    }
                }, 170);
            }
        });
    }

    public static void openGmail(Activity activity, String[] email, String subject, String content) {
        Intent emailIntent = new Intent(Intent.ACTION_SEND);
        emailIntent.putExtra(Intent.EXTRA_EMAIL, email);
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
        emailIntent.setType("text/plain");
        emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, content);
        final PackageManager pm = activity.getPackageManager();
        final List<ResolveInfo> matches = pm.queryIntentActivities(emailIntent, 0);
        ResolveInfo best = null;
        for (final ResolveInfo info : matches)
            if (info.activityInfo.packageName.endsWith(".gm") || info.activityInfo.name.toLowerCase().contains("gmail"))
                best = info;
        if (best != null)
            emailIntent.setClassName(best.activityInfo.packageName, best.activityInfo.name);
        activity.startActivity(emailIntent);
    }

    @Override
    public void onBackPressed() {
        PRESSED = false;
        android.support.v4.app.Fragment temp00 = getSupportFragmentManager().
                findFragmentById(R.id.flMain);
        if (!drawer.isClosed()) {
            drawer.closeLeftSide();
        } else if (getSupportFragmentManager().getBackStackEntryCount() == 0) {
            final AlertDialog.Builder builder = new AlertDialog.Builder(PocketAccounter.this);
            builder.setMessage(getString(R.string.dou_you_want_quit))
                    .setPositiveButton(getString(R.string.no), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog,  int id) {
                        }
                    }).setNegativeButton(getString(R.string.exit), new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    dialog.cancel();
                    finish();
                }
            });
            builder.create().show();
        } else {
            if (temp00.getTag() != null) {
                if (temp00.getTag().equals(AddCreditFragment.OPENED_TAG) && AddCreditFragment.to_open_dialog) {
                    //Sardor
                    final AlertDialog.Builder builder = new AlertDialog.Builder(PocketAccounter.this);
                    builder.setMessage(getString(R.string.dou_you_want_discard))
                            .setPositiveButton(getString(R.string.cancel1), new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                }
                            }).setNegativeButton(getString(R.string.discard), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                            getSupportFragmentManager().popBackStack();

                        }
                    });
                    builder.create().show();
                } else {
                    AddCreditFragment.to_open_dialog = true;
                    getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                    initialize(date);
                    String tag = getSupportFragmentManager().findFragmentById(R.id.flMain).getTag();
                    if (tag.matches("Addcredit")
                            || tag.matches("InfoFragment")) {
                        replaceFragment(new CreditTabLay(), com.jim.pocketaccounter.debt.PockerTag.CREDITS);
                    }
                    switch (tag) {
                        case com.jim.pocketaccounter.debt.PockerTag.ACCOUNT_MANAGEMENT:
                        case PockerTag.ACCOUNT:
                        case PockerTag.CATEGORY:
                        case PockerTag.CURRENCY:
                        case PockerTag.CREDITS:
                        case PockerTag.REPORT_ACCOUNT:
                        case PockerTag.REPORT_INCOM_EXPENSE:
                        case PockerTag.REPORT_CATEGORY:
                        case PockerTag.DEBTS: {
                            findViewById(R.id.change).setVisibility(View.VISIBLE);
                            initialize(date);
                            break;
                        }
                    }
                }
            } else {
                getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                if (getSupportFragmentManager().findFragmentById(R.id.flMain) != null) {
                    if (fragmentManager.findFragmentById(R.id.flMain).getTag() == null) {
                        switch (fragmentManager.findFragmentById(R.id.flMain).getClass().getName()) {
                            case "com.jim.pocketaccounter.RecordEditFragment": {
                                if (getSupportFragmentManager().getBackStackEntryCount() == 2) {
                                    replaceFragment(new RecordDetailFragment(date));
                                    break;
                                }
                            }
                            case "com.jim.pocketaccounter.AccountManagementFragment":
                            case "com.jim.pocketaccounter.RecordDetailFragment":
                                initialize(date);
                                break;
                            case "com.jim.pocketaccounter.CurrencyEditFragment":
                            case "com.jim.pocketaccounter.CurrencyChooseFragment":
                                findViewById(R.id.change).setVisibility(View.VISIBLE);
                                replaceFragment(new CurrencyFragment(), PockerTag.CURRENCY);
                                break;
                            case "com.jim.pocketaccounter.RootCategoryEditFragment": {
                                replaceFragment(new CategoryFragment(), PockerTag.CATEGORY);
                                break;
                            }
                            case "com.jim.pocketaccounter.debt.InfoDebtBorrowFragment":
                            case "com.jim.pocketaccounter.debt.AddBorrowFragment": {
                                DebtBorrowFragment fragment = new DebtBorrowFragment();
                                fragment.setArguments(fragmentManager.findFragmentById(R.id.flMain).getArguments());
                                replaceFragment(fragment, PockerTag.DEBTS);
                                break;
                            }
                            case "com.jim.pocketaccounter.SMSParseEditFragment": {
                                replaceFragment(new SMSParseFragment(), PockerTag.DEBTS);
                            }
                        }
                        return;
                    }
                    if (fragmentManager.findFragmentById(R.id.flMain).getTag().matches(PockerTag.ACCOUNT)) {
                        replaceFragment(new AccountFragment(), PockerTag.ACCOUNT);
                    } else if (fragmentManager.findFragmentById(R.id.flMain).getTag().matches(PockerTag.DEBTS)) {
                        replaceFragment(new DebtBorrowFragment(), PockerTag.DEBTS);
                    } else if (fragmentManager.findFragmentById(R.id.flMain).getTag().matches(PockerTag.CURRENCY)) {
                        replaceFragment(new CurrencyFragment(), PockerTag.CURRENCY);
                    } else if (fragmentManager.findFragmentById(R.id.flMain).getTag().matches(PockerTag.CATEGORY)) {
                        replaceFragment(new CategoryFragment(), PockerTag.CATEGORY);
                    } else if (fragmentManager.findFragmentById(R.id.flMain).getTag().matches(PockerTag.ACCOUNT)) {
                        replaceFragment(new AccountFragment(), PockerTag.ACCOUNT);
                    }
                }
            }
        }
    }

    public void replaceFragment(Fragment fragment) {
        if (fragment != null) {
            PRESSED = true;
            getSupportFragmentManager()
                    .beginTransaction()
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                    .addToBackStack(null)
                    .add(R.id.flMain, fragment)
                    .commit();
        }
    }

    public void replaceFragment(final Fragment fragment, final String tag) {
        if (fragment != null) {
            int size = fragmentManager.getBackStackEntryCount();
            for (int i = 0; i < size; i++) {
                fragmentManager.popBackStack();
            }

            findViewById(R.id.change).setVisibility(View.INVISIBLE);
            fragmentManager.beginTransaction()
                    .addToBackStack(null)
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE)
                    .add(R.id.flMain, fragment, tag)
                    .commit();
        }
    }

    @Override
    public void onRestart() {
        super.onRestart();
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            try {
                if (downloadnycCanRest && imageUri != null) {
                    imagetask = new DownloadImageTask(userAvatar);
                    imagetask.execute(imageUri.toString());
                }
            } catch (Exception o) {
            }
        } else {
            userAvatar.setImageResource(R.drawable.no_photo);
            userName.setText(R.string.please_sign);
            userEmail.setText(R.string.and_sync_your_data);

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        findViewById(R.id.change).setVisibility(View.VISIBLE);
        if (requestCode == SignInGoogleMoneyHold.RC_SIGN_IN) {
            reg.regitRequstGet(data);
        }
        if (requestCode == key_for_restat && resultCode == RESULT_OK) {
            initialize(date);
            if (!drawer.isClosed()) {
                drawer.close();
            }
            if (FirebaseAuth.getInstance().getCurrentUser() == null) {
                userAvatar.setImageResource(R.drawable.no_photo);
                userName.setText(R.string.please_sign);
                userEmail.setText(R.string.and_sync_your_data);
                fabIconFrame.setBackgroundResource(R.drawable.cloud_sign_in);
            }
        }
        if (resultCode != RESULT_OK && requestCode == key_for_restat && resultCode != 1111) {
            initialize(date);
            for (int i = 0; i < fragmentManager.getBackStackEntryCount(); i++) {
                fragmentManager.popBackStack();
            }

        }

        if (requestCode == key_for_restat && resultCode == 1111) {
            if(WidgetID>=0){
                if(AppWidgetManager.INVALID_APPWIDGET_ID!=WidgetID)
                    WidgetProvider.updateWidget(this, AppWidgetManager.getInstance(this),
                            WidgetID);
            }

            finish();
        }

    }

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        CircleImageView bmImage;

        public DownloadImageTask(CircleImageView bmImage) {
            this.bmImage = bmImage;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            File file = new File(getFilesDir(), "userphoto.jpg");
            if (file.exists()) {
                bmImage.setImageURI(Uri.parse(file.getAbsolutePath()));
            }
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;

            for (; true; ) {
                try {
                    InputStream in = new java.net.URL(urldisplay).openStream();
                    mIcon11 = BitmapFactory.decodeStream(in);
                    break;
                } catch (Exception e) {
                    e.printStackTrace();
                    try {
                        Thread.sleep(10000);
                    } catch (InterruptedException e1) {
                        e1.printStackTrace();
                    }
                }
                if (isCancelled()) break;
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            if (result != null) {
                downloadnycCanRest = false;
                bmImage.setImageBitmap(result);
                File file = new File(getFilesDir(), "userphoto.jpg");
                FileOutputStream out = null;

                try {
                    out = new FileOutputStream(file);
                    result.compress(Bitmap.CompressFormat.JPEG, 100, out);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } finally {
                    try {
                        if (out != null) {
                            out.close();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    private ProgressDialog mProgressDialog;


    public void showProgressDialog(String message) {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setMessage(message);
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