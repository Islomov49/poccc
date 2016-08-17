package com.jim.pocketaccounter.widget;


    import android.app.Dialog;
    import android.appwidget.AppWidgetManager;
    import android.content.Context;
    import android.content.DialogInterface;
    import android.content.Intent;
    import android.content.SharedPreferences;
    import android.content.pm.PackageManager;
    import android.content.res.Resources;
    import android.database.Cursor;
    import android.graphics.Bitmap;
    import android.graphics.BitmapFactory;
    import android.graphics.Matrix;
    import android.media.ExifInterface;
    import android.net.Uri;
    import android.os.Build;
    import android.os.Bundle;
    import android.os.Handler;
    import android.preference.PreferenceManager;
    import android.provider.MediaStore;
    import android.support.v4.app.ActivityCompat;
    import android.support.v4.app.FragmentManager;
    import android.support.v4.content.ContextCompat;
    import android.support.v7.app.AlertDialog;
    import android.support.v7.app.AppCompatActivity;
    import android.support.v7.widget.ActionBarOverlayLayout;
    import android.support.v7.widget.LinearLayoutManager;
    import android.support.v7.widget.RecyclerView;
    import android.support.v7.widget.Toolbar;
    import android.util.DisplayMetrics;
    import android.util.Log;
    import android.view.View;
    import android.view.ViewTreeObserver;
    import android.view.Window;
    import android.view.animation.Animation;
    import android.view.animation.AnimationUtils;
    import android.view.inputmethod.InputMethodManager;
    import android.widget.AdapterView;
    import android.widget.ArrayAdapter;
    import android.widget.EditText;
    import android.widget.ImageView;
    import android.widget.LinearLayout;
    import android.widget.ListView;
    import android.widget.RelativeLayout;
    import android.widget.Spinner;
    import android.widget.TextView;
    import android.widget.Toast;

    import com.jim.pocketaccounter.R;
    import com.jim.pocketaccounter.RecordEditFragment;
    import com.jim.pocketaccounter.SettingsActivity;
    import com.jim.pocketaccounter.credit.CreditDetials;
    import com.jim.pocketaccounter.credit.ReckingCredit;
    import com.jim.pocketaccounter.debt.DebtBorrow;
    import com.jim.pocketaccounter.debt.Recking;
    import com.jim.pocketaccounter.finance.Account;
    import com.jim.pocketaccounter.finance.Currency;
    import com.jim.pocketaccounter.finance.FinanceManager;
    import com.jim.pocketaccounter.finance.FinanceRecord;
    import com.jim.pocketaccounter.finance.RecordAccountAdapter;
    import com.jim.pocketaccounter.finance.RecordCategoryAdapter;
    import com.jim.pocketaccounter.finance.RecordSubCategoryAdapter;
    import com.jim.pocketaccounter.finance.RootCategory;
    import com.jim.pocketaccounter.finance.SubCategory;
    import com.jim.pocketaccounter.helper.PocketAccounterGeneral;
    import com.jim.pocketaccounter.photocalc.PhotoAdapter;
    import com.jim.pocketaccounter.photocalc.PhotoDetails;
    import com.transitionseverywhere.AutoTransition;
    import com.transitionseverywhere.Transition;
    import com.transitionseverywhere.TransitionManager;

    import net.objecthunter.exp4j.Expression;
    import net.objecthunter.exp4j.ExpressionBuilder;


    import java.io.File;
    import java.io.FileInputStream;
    import java.io.FileNotFoundException;
    import java.io.FileOutputStream;
    import java.io.IOException;
    import java.io.OutputStream;
    import java.text.DecimalFormat;
    import java.text.DecimalFormatSymbols;
    import java.text.ParseException;
    import java.text.SimpleDateFormat;
    import java.util.ArrayList;
    import java.util.Calendar;
    import java.util.UUID;

    import static android.app.Activity.RESULT_OK;
    import static com.jim.pocketaccounter.debt.AddBorrowFragment.RESULT_LOAD_IMAGE;
    import static com.jim.pocketaccounter.photocalc.PhotoAdapter.BEGIN_DELETE_TICKKETS_PATH;
    import static com.jim.pocketaccounter.photocalc.PhotoAdapter.BEGIN_DELETE_TICKKETS_PATH_CACHE;
    import static com.jim.pocketaccounter.photocalc.PhotoAdapter.COUNT_DELETES;
    import static com.jim.pocketaccounter.photocalc.PhotoAdapter.REQUEST_DELETE_PHOTOS;

    public class CalcActivity extends AppCompatActivity implements View.OnClickListener {
        private boolean keyforback=false;
        private TextView tvRecordEditDisplay;
        private ImageView ivRecordEditCategory, ivRecordEditSubCategory;
        private Spinner spRecordEdit, spToolbar;
        private RootCategory category;
        private SubCategory subCategory;
        private Currency currency;
        private Account account;
        private Calendar date;
        private int parent;
        private int[] numericButtons = {R.id.rlZero, R.id.rlOne, R.id.rlTwo, R.id.rlThree, R.id.rlFour, R.id.rlFive, R.id.rlSix, R.id.rlSeven, R.id.rlEight, R.id.rlNine};
        private int[] operatorButtons = {R.id.rlPlusSign, R.id.rlMinusSign, R.id.rlMultipleSign, R.id.rlDivideSign};
        private boolean lastNumeric = true;
        private boolean stateError;
        private boolean lastDot;
        private boolean lastOperator;
        private DecimalFormat decimalFormat = null;
        private RelativeLayout rlCategory, rlSubCategory;
        private Animation buttonClick;
        private TextView comment;
        private EditText comment_add;
        private String oraliqComment="";
        boolean keykeboard=false;
        private final int PERMISSION_READ_STORAGE = 6;
        private boolean keyForDeleteAllPhotos=true;
        boolean isCalcLayoutOpen=false;
        static final int REQUEST_IMAGE_CAPTURE = 112;
        private String uid_code;
        RecyclerView myListPhoto;
        ArrayList<PhotoDetails> myTickets;
        ArrayList<PhotoDetails> myTicketsFromBackRoll;
        PhotoAdapter myTickedAdapter;
        boolean openAddingDialog=false;
        FinanceManager financeManager;
        private int WIDGET_ID;
        LinearLayout mainView;
        public static String KEY_FOR_INSTALAZING="key_for_init";

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_calc);
            mainView=(LinearLayout) findViewById(R.id.llRoot);
            financeManager=new FinanceManager(this);
            comment = (TextView) findViewById(R.id.textView18);
            comment_add = (EditText) findViewById(R.id.comment_add);
            date=Calendar.getInstance();
            DecimalFormatSymbols otherSymbols = new DecimalFormatSymbols();
            otherSymbols.setDecimalSeparator('.');
            otherSymbols.setGroupingSeparator('.');
            decimalFormat = new DecimalFormat("0.##", otherSymbols);
            uid_code= "record_" + UUID.randomUUID().toString();
            category = new RootCategory();
            String catId = getIntent().getStringExtra(WidgetKeys.KEY_FOR_INTENT_ID);
            WIDGET_ID = getIntent().getIntExtra(WidgetKeys.ACTION_WIDGET_RECEIVER_CHANGE_DIAGRAM_ID,  AppWidgetManager.INVALID_APPWIDGET_ID);
            for (int i=0; i<financeManager.getCategories().size(); i++) {
                if (financeManager.getCategories().get(i).getId().matches(catId)) {
                    category = financeManager.getCategories().get(i);
                    break;
                }
            }



            buttonClick = AnimationUtils.loadAnimation(CalcActivity.this, R.anim.button_click);
            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            toolbar.setTitle("");
            toolbar.setSubtitle("");
            spRecordEdit = (Spinner) findViewById(R.id.spRecordEdit);
            spToolbar = (Spinner) toolbar.findViewById(R.id.spToolbar);
            spToolbar.setVisibility(View.VISIBLE);
            RecordAccountAdapter accountAdapter = new RecordAccountAdapter(CalcActivity.this, financeManager.getAccounts());
            spToolbar.setAdapter(accountAdapter);
            spToolbar.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    account = financeManager.getAccounts().get(position);
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                }
            });
            final String[] currencies = new String[financeManager.getCurrencies().size()];
            for (int i = 0; i < financeManager.getCurrencies().size(); i++)
                currencies[i] = financeManager.getCurrencies().get(i).getAbbr();
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(CalcActivity.this, R.layout.spinner_single_item_calc, currencies);
            spRecordEdit.setAdapter(adapter);
            for (int i = 0; i < financeManager.getCurrencies().size(); i++) {
                if (financeManager.getCurrencies().get(i).getId().matches(financeManager.getMainCurrency().getId())) {
                    spRecordEdit.setSelection(i);
                    break;
                }
            }
            spRecordEdit.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    currency = financeManager.getCurrencies().get(position);
                }
                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                }
            });


            ivRecordEditCategory = (ImageView) findViewById(R.id.ivRecordEditCategory);
            ivRecordEditSubCategory = (ImageView) findViewById(R.id.ivRecordEditSubCategory);
            tvRecordEditDisplay = (TextView) findViewById(R.id.tvRecordEditDisplay);
            rlCategory = (RelativeLayout) findViewById(R.id.rlCategory);
            rlCategory.setOnClickListener(this);
            rlSubCategory = (RelativeLayout) findViewById(R.id.rlSubcategory);
            rlSubCategory.setOnClickListener(this);
            setNumericOnClickListener();
            setOperatorOnClickListener();
            if (category != null) {
                ivRecordEditSubCategory.setImageResource(R.drawable.category_not_selected);
                int resId = getResources().getIdentifier(category.getIcon(), "drawable", CalcActivity.this.getPackageName());

                ivRecordEditCategory.setImageResource(resId);
            }

            if(myTickets==null)
                myTickets=new ArrayList<>();
            if(myTicketsFromBackRoll==null)
                myTicketsFromBackRoll=new ArrayList<>();
            LinearLayoutManager layoutManager
                    = new LinearLayoutManager(CalcActivity.this, LinearLayoutManager.HORIZONTAL, false);
            myListPhoto = (RecyclerView) findViewById(R.id.recycler_calc);
            myListPhoto.setLayoutManager(layoutManager);

            myTickedAdapter =new PhotoAdapter(myTickets,CalcActivity.this, new RecordEditFragment.OpenIntentFromAdapter() {
                @Override
                public void startActivityFromFragmentForResult(Intent intent) {

                    startActivityForResult(intent,REQUEST_DELETE_PHOTOS);
                }
            });
            myListPhoto.setAdapter(myTickedAdapter);


        }

        private void setNumericOnClickListener() {
            View.OnClickListener listener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    v.startAnimation(buttonClick);
                    if (tvRecordEditDisplay.getText().toString().length() >= 14) return;
                    String text = "";
                    switch (v.getId()) {
                        case R.id.rlZero:
                            text = "0";
                            break;
                        case R.id.rlOne:
                            text = "1";
                            break;
                        case R.id.rlTwo:
                            text = "2";
                            break;
                        case R.id.rlThree:
                            text = "3";
                            break;
                        case R.id.rlFour:
                            text = "4";
                            break;
                        case R.id.rlFive:
                            text = "5";
                            break;
                        case R.id.rlSix:
                            text = "6";
                            break;
                        case R.id.rlSeven:
                            text = "7";
                            break;
                        case R.id.rlEight:
                            text = "8";
                            break;
                        case R.id.rlNine:
                            text = "9";
                            break;
                    }
                    if (stateError) {
                        tvRecordEditDisplay.setText(text);
                        stateError = false;
                    } else {
                        String displayText = tvRecordEditDisplay.getText().toString();
                        if (displayText.matches("") || displayText.matches("0"))
                            tvRecordEditDisplay.setText(text);
                        else
                            tvRecordEditDisplay.append(text);
                    }
                    lastNumeric = true;
                    lastOperator = false;
                    lastDot = false;
                }
            };
            for (int id:numericButtons)
               findViewById(id).setOnClickListener(listener);
        }

        private void setOperatorOnClickListener() {
            View.OnClickListener listener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    v.startAnimation(buttonClick);
                    if (tvRecordEditDisplay.getText().toString().length() >= 14) return;
                    String text = "";
                    switch (v.getId()) {
                        case R.id.rlPlusSign:
                            text = "+";
                            break;
                        case R.id.rlMinusSign:
                            text = "-";
                            break;
                        case R.id.rlDivideSign:
                            text = "/";
                            break;
                        case R.id.rlMultipleSign:
                            text = "*";
                            break;
                    }
                    if (lastNumeric && !stateError) {
                        tvRecordEditDisplay.append(text);
                        lastNumeric = false;
                        lastDot = false;
                        lastOperator = true;
                    }
                    if (lastOperator) {
                        String dispText = tvRecordEditDisplay.getText().toString();
                        dispText = dispText.substring(0, dispText.length() - 1) + text;
                        tvRecordEditDisplay.setText(dispText);
                    }
                }
            };
            for (int id : operatorButtons)
                findViewById(id).setOnClickListener(listener);
           findViewById(R.id.rlDot).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    v.startAnimation(buttonClick);
                    if (tvRecordEditDisplay.getText().toString().length() >= 14) return;
                    if (lastNumeric && !stateError && !lastDot && !lastOperator) {
                        tvRecordEditDisplay.append(".");
                        lastNumeric = false;
                        lastDot = true;
                    }
                }
            });
            findViewById(R.id.choose_photo).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(CalcActivity.this);
                    builder.setTitle("Choose type adding")
                            .setItems(R.array.adding_ticket_type, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    if(which==0){
                                        int permission = ContextCompat.checkSelfPermission(CalcActivity.this,
                                                android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
                                        if (permission != PackageManager.PERMISSION_GRANTED) {
                                            if (ActivityCompat.shouldShowRequestPermissionRationale(( CalcActivity.this),
                                                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                                                android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(CalcActivity.this);
                                                builder.setMessage("Permission to access the SD-CARD is required for this app to Download PDF.")
                                                        .setTitle("Permission required");

                                                builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                                                    public void onClick(DialogInterface dialog, int id) {
                                                        ActivityCompat.requestPermissions( CalcActivity.this,
                                                                new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                                                PERMISSION_READ_STORAGE);
                                                    }
                                                });
                                                android.app.AlertDialog dialogik = builder.create();
                                                dialogik.show();

                                            } else {
                                                ActivityCompat.requestPermissions( CalcActivity.this,
                                                        new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                                        PERMISSION_READ_STORAGE);
                                            }
                                        } else {
                                            getPhoto();
                                        }
                                    }
                                    else if(which==1){
                                        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                        if (takePictureIntent.resolveActivity(CalcActivity.this.getPackageManager()) != null) {

                                            File f = new File(CalcActivity.this.getExternalFilesDir(null),"temp.jpg");

                                            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
                                            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);

                                        }


                                    }
                                }
                            });
                    builder.create().show();

                }
            });
            findViewById(R.id.rlBackspaceSign).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    v.startAnimation(buttonClick);
                    String dispText = tvRecordEditDisplay.getText().toString();
                    char lastChar = dispText.charAt(dispText.length() - 1);
                    char[] opers = {'+', '-', '*', '/'};
                    for (int i = 0; i < opers.length; i++) {
                        if (opers[i] == lastChar) {
                            lastOperator = false;
                            lastNumeric = true;
                        }
                    }
                    if (lastChar == '.') {
                        lastDot = false;
                        lastNumeric = true;
                    }
                    if (tvRecordEditDisplay.getText().toString().length() == 1)
                        tvRecordEditDisplay.setText("0");
                    else {
                        dispText = dispText.substring(0, dispText.length() - 1);
                        tvRecordEditDisplay.setText(dispText);
                    }
                }
            });

            comment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    LinearLayout linbutview=(LinearLayout) findViewById(R.id.numbersbut);
                    TransitionManager.beginDelayedTransition(linbutview);
                    linbutview.setVisibility(View.GONE);
                    keyforback=false;
                    openAddingDialog=true;
                    isCalcLayoutOpen=true;
                    (new Handler()).postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            comment_add.setFocusableInTouchMode(true);
                            comment_add.requestFocus();
                            final InputMethodManager inputMethodManager = (InputMethodManager) CalcActivity.this
                                    .getSystemService(CalcActivity.this.INPUT_METHOD_SERVICE);
                            if(inputMethodManager==null)
                                return;
                            inputMethodManager.showSoftInput(comment_add, InputMethodManager.SHOW_IMPLICIT);
                        }
                    },200);


                }
            });
            findViewById(R.id.savesecbut).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View v) {
                    keyForDeleteAllPhotos=false;
                    if(keykeboard){
                        InputMethodManager imm = (InputMethodManager)CalcActivity.this.getSystemService(CalcActivity.this.INPUT_METHOD_SERVICE);

                        imm.hideSoftInputFromWindow(v.getWindowToken(), InputMethodManager.HIDE_IMPLICIT_ONLY);
                        isCalcLayoutOpen=false;

                        (new Handler()).postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                v.startAnimation(buttonClick);
                                if (lastDot || lastOperator) {
                                    return;
                                }
                                createNewRecord();
                            }
                        },300);
                    }
                    else{
                        v.startAnimation(buttonClick);
                        if (lastDot || lastOperator) {
                            return;
                        }
                        createNewRecord();

                    }
                }
            });


            mainView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    int heightDiff = mainView.getRootView().getHeight() - mainView.getHeight();
                    if (heightDiff > convertDpToPixel( 200,CalcActivity.this)) { // if more than 200 dp, it's probably a keyboard...
                        if(!keykeboard){
                            Log.d("test", "onGlobalLayout: KeyBoardOpen");

                            keykeboard=true;

                            final Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {


                                    RelativeLayout headermain= (RelativeLayout) findViewById(R.id.headermain);
                                    AutoTransition cus=new AutoTransition();
                                    cus.setDuration(200);
                                    cus.setStartDelay(0);
                                    TransitionManager.beginDelayedTransition(headermain,cus);

                                    headermain.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));


                                    comment.setVisibility(View.GONE);
                                    findViewById(R.id.addphotopanel).setVisibility(View.GONE);
                                    findViewById(R.id.pasdigi).setVisibility(View.GONE);
                                    myListPhoto.setVisibility(View.GONE);

                                    findViewById(R.id.scroleditext).setVisibility(View.VISIBLE);
                                    findViewById(R.id.commenee).setVisibility(View.VISIBLE);
                                    findViewById(R.id.savepanel).setVisibility(View.VISIBLE);
                                }
                            }, 50);




                        }
                    }
                    else {
                        if(keykeboard){
                            Log.d("test", "onGlobalLayout: KeyBoardClose");
                            keykeboard=false;

                            if (keyforback){

                                final Handler handler = new Handler();
                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        AutoTransition cus=new AutoTransition();
                                        cus.setDuration(300);
                                        cus.setStartDelay(0);
                                        LinearLayout linbutview=(LinearLayout) findViewById(R.id.numbersbut);
                                        TransitionManager.beginDelayedTransition(linbutview,cus);
                                        TransitionManager.beginDelayedTransition(myListPhoto);
                                        myListPhoto.setVisibility(View.VISIBLE);
                                        linbutview.setVisibility(View.VISIBLE);

                                    }
                                }, 200);

                            }



                        }


                    }
                }
            });



            findViewById(R.id.addcomment).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openAddingDialog=false;
                    if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.HONEYCOMB) {
                        if(keykeboard) {
                            RelativeLayout headermain = (RelativeLayout) findViewById(R.id.headermain);
                            keyforback = true;

                            (new Handler()).postDelayed(new Runnable() {
                                @Override
                                public void run() {

                                    try {
                                        InputMethodManager imm = (InputMethodManager) CalcActivity.this.getSystemService(CalcActivity.this.INPUT_METHOD_SERVICE);
                                        if(imm==null)
                                            return;
                                        imm.hideSoftInputFromWindow(mainView.getWindowToken(), 0);
                                    }
                                    catch (Exception o){
                                        o.printStackTrace();
                                    }

                                }
                            }, 120);

                            isCalcLayoutOpen=false;
                            comment.setVisibility(View.VISIBLE);
                            findViewById(R.id.addphotopanel).setVisibility(View.VISIBLE);
                            findViewById(R.id.pasdigi).setVisibility(View.VISIBLE);

                            findViewById(R.id.scroleditext).setVisibility(View.GONE);
                            findViewById(R.id.commenee).setVisibility(View.GONE);
                            findViewById(R.id.savepanel).setVisibility(View.GONE);
                            oraliqComment = comment_add.getText().toString();
                            if (!oraliqComment.matches("")) {
                                comment.setText(oraliqComment);
                            } else {
                                comment.setText(getString(R.string.add_comment));
                            }

                            headermain.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, (int) convertDpToPixel((getResources().getDimension(R.dimen.hundred_fivety_four) / getResources().getDisplayMetrics().density), CalcActivity.this)));
                        }
                        else{
                            RelativeLayout headermain = (RelativeLayout) findViewById(R.id.headermain);
                            keyforback = true;


                            (new Handler()).postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    try {

                                        LinearLayout linbutview=(LinearLayout) findViewById(R.id.numbersbut);
                                        myListPhoto.setVisibility(View.VISIBLE);
                                        linbutview.setVisibility(View.VISIBLE);
                                    }
                                    catch (Exception o){
                                        o.printStackTrace();
                                    }

                                }
                            }, 120);



                            comment.setVisibility(View.VISIBLE);
                            findViewById(R.id.addphotopanel).setVisibility(View.VISIBLE);
                            findViewById(R.id.pasdigi).setVisibility(View.VISIBLE);


                            findViewById(R.id.scroleditext).setVisibility(View.GONE);
                            findViewById(R.id.commenee).setVisibility(View.GONE);
                            findViewById(R.id.savepanel).setVisibility(View.GONE);
                            oraliqComment = comment_add.getText().toString();
                            if (!oraliqComment.matches("")) {
                                comment.setText(oraliqComment);
                            } else {
                                comment.setText(getString(R.string.add_comment));
                            }

                            headermain.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, (int) convertDpToPixel((getResources().getDimension(R.dimen.hundred_fivety_four) / getResources().getDisplayMetrics().density), CalcActivity.this)));

                        }
                    }
                    else
                    if(keykeboard) {
                        Log.d("testtt", "onClick: ");
                        RelativeLayout headermain = (RelativeLayout) findViewById(R.id.headermain);
                        AutoTransition cus = new AutoTransition();
                        keyforback = true;

                        isCalcLayoutOpen=false;
                        cus.addListener(new Transition.TransitionListener() {
                            @Override
                            public void onTransitionStart(Transition transition) {

                            }

                            @Override
                            public void onTransitionEnd(Transition transition) {
//                                if(mainView==null){
//                                    return;
//                                }
                                Log.d("testtt", "onClick: pip");

                                (new Handler()).postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        try {
                                            InputMethodManager imm = (InputMethodManager) CalcActivity.this.getSystemService(CalcActivity.this.INPUT_METHOD_SERVICE);
                                            if(imm==null)
                                                return;
                                            imm.hideSoftInputFromWindow(mainView.getWindowToken(), 0);

                                        }
                                        catch (Exception o){
                                            o.printStackTrace();
                                        }


                                    }
                                }, 120);
                            }

                            @Override
                            public void onTransitionCancel(Transition transition) {

                            }

                            @Override
                            public void onTransitionPause(Transition transition) {

                            }

                            @Override
                            public void onTransitionResume(Transition transition) {

                            }
                        });
                        cus.setDuration(200);
                        cus.setStartDelay(0);
                        TransitionManager.beginDelayedTransition(headermain, cus);
                        comment.setVisibility(View.VISIBLE);
                        findViewById(R.id.addphotopanel).setVisibility(View.VISIBLE);
                        findViewById(R.id.pasdigi).setVisibility(View.VISIBLE);

                        findViewById(R.id.scroleditext).setVisibility(View.GONE);
                        findViewById(R.id.commenee).setVisibility(View.GONE);
                        findViewById(R.id.savepanel).setVisibility(View.GONE);
                        oraliqComment = comment_add.getText().toString();
                        if (!oraliqComment.matches("")) {
                            comment.setText(oraliqComment);
                        } else {
                            comment.setText(getString(R.string.add_comment));
                        }

                        headermain.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, (int) convertDpToPixel((getResources().getDimension(R.dimen.hundred_fivety_four) / getResources().getDisplayMetrics().density), CalcActivity.this)));
                    }
                    else{
                        RelativeLayout headermain = (RelativeLayout) findViewById(R.id.headermain);
                        AutoTransition cus = new AutoTransition();
                        keyforback = true;
                        cus.addListener(new Transition.TransitionListener() {
                            @Override
                            public void onTransitionStart(Transition transition) {

                            }

                            @Override
                            public void onTransitionEnd(Transition transition) {
//                                if(mainView==null){
//                                    return;
//                                }
                                try {

                                    AutoTransition cus=new AutoTransition();
                                    cus.setDuration(300);
                                    cus.setStartDelay(0);
                                    LinearLayout linbutview=(LinearLayout) findViewById(R.id.numbersbut);
                                    TransitionManager.beginDelayedTransition(myListPhoto);
                                    myListPhoto.setVisibility(View.VISIBLE);
                                    TransitionManager.beginDelayedTransition(linbutview,cus);
                                    linbutview.setVisibility(View.VISIBLE);
                                }
                                catch (Exception o){
                                    o.printStackTrace();
                                }

                            }

                            @Override
                            public void onTransitionCancel(Transition transition) {

                            }

                            @Override
                            public void onTransitionPause(Transition transition) {

                            }

                            @Override
                            public void onTransitionResume(Transition transition) {

                            }
                        });
                        cus.setDuration(200);
                        cus.setStartDelay(0);
                        TransitionManager.beginDelayedTransition(headermain, cus);
                        comment.setVisibility(View.VISIBLE);
                        findViewById(R.id.addphotopanel).setVisibility(View.VISIBLE);
                        findViewById(R.id.pasdigi).setVisibility(View.VISIBLE);


                        findViewById(R.id.scroleditext).setVisibility(View.GONE);
                        findViewById(R.id.commenee).setVisibility(View.GONE);
                        findViewById(R.id.savepanel).setVisibility(View.GONE);
                        oraliqComment = comment_add.getText().toString();
                        if (!oraliqComment.matches("")) {
                            comment.setText(oraliqComment);
                        } else {
                            comment.setText(getString(R.string.add_comment));
                        }

                        headermain.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, (int) convertDpToPixel((getResources().getDimension(R.dimen.hundred_fivety_four) / getResources().getDisplayMetrics().density), CalcActivity.this)));

                    }


                }
            });

            findViewById(R.id.rlBackspaceSign).setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    v.startAnimation(buttonClick);
                    tvRecordEditDisplay.setText("0");
                    lastNumeric = false;
                    stateError = false;
                    lastDot = false;
                    lastOperator = false;
                    return true;
                }
            });
            findViewById(R.id.imOK).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View v) {
                    keyForDeleteAllPhotos=false;
                    if(keykeboard){
                        InputMethodManager imm = (InputMethodManager)CalcActivity.this.getSystemService(CalcActivity.this.INPUT_METHOD_SERVICE);
                        if(imm==null)
                            return;
                        imm.hideSoftInputFromWindow(v.getWindowToken(), InputMethodManager.HIDE_IMPLICIT_ONLY);

                        (new Handler()).postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                v.startAnimation(buttonClick);
                                if (lastDot || lastOperator) {
                                    return;
                                }
                                createNewRecord();
                            }
                        },300);
                    }
                    else{
                        v.startAnimation(buttonClick);
                        if (lastDot || lastOperator) {
                            return;
                        }
                        createNewRecord();

                    }

                }
            });
            findViewById(R.id.rlEqualSign).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    v.startAnimation(buttonClick);
                    onEqual();
                }
            });
        }

        private void onEqual() {
            if (lastNumeric && !stateError) {
                String txt = tvRecordEditDisplay.getText().toString();
                Expression expression = new ExpressionBuilder(txt).build();
                try {
                    double result = expression.evaluate();
                    tvRecordEditDisplay.setText(decimalFormat.format(result));
                } catch (ArithmeticException ex) {
                    tvRecordEditDisplay.setText(getResources().getString(R.string.error));
                    stateError = true;
                    lastNumeric = false;
                }
            }
        }

        private void getPhoto() {
            Intent i = new Intent(
                    Intent.ACTION_PICK,
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(i, RESULT_LOAD_IMAGE);
        }



        @Override
        public void onActivityResult(int requestCode, int resultCode, final Intent data) {
//        super.onActivityResult(requestCode,resultCode,data);
            Log.d("resulttt", "onActivityResult: Keldi "+((data!=null)?"PUSTOY":"NIMADIRLA"));
            if(requestCode==REQUEST_DELETE_PHOTOS&&data!=null&&resultCode==RESULT_OK){
                Log.d("resulttt", "onActivityResult: "+(int)data.getExtras().get(COUNT_DELETES));
                if((int)data.getExtras().get(COUNT_DELETES)!=0){
                    for (int i = 0; i < (int)data.getExtras().get(COUNT_DELETES); i++) {

                        for (int j = myTickets.size()-1; j>=0; j--) {
                            if(myTickets.get(j).getPhotopath().matches((String) data.getExtras().get(BEGIN_DELETE_TICKKETS_PATH+i))){
                                myTicketsFromBackRoll.remove(myTickets.get(j));
                                myTickets.remove(j);
                                myTickedAdapter.notifyItemRemoved(j);
                            }
                        }
                    }

                    (new Thread(new Runnable() {
                        @Override
                        public void run() {
                            for (int i = 0; i < (int)data.getExtras().get(COUNT_DELETES); i++) {
                                File fileForDelete=new File((String) data.getExtras().get(BEGIN_DELETE_TICKKETS_PATH+i));
                                File fileForDeleteCache=new File((String) data.getExtras().get(BEGIN_DELETE_TICKKETS_PATH_CACHE+i));

                                try {
                                    fileForDelete.delete();
                                    fileForDeleteCache.delete();
                                }
                                catch (Exception o){
                                    o.printStackTrace();
                                }
                            }

                        }
                    })).start();


                }


            }
            if (requestCode == RESULT_LOAD_IMAGE && null != data) {
                Uri selectedImage = data.getData();
                String[] filePathColumn = {MediaStore.Images.Media.DATA};
                Cursor cursor = CalcActivity.this.getContentResolver().query(selectedImage,
                        filePathColumn, null, null, null);
                cursor.moveToFirst();

                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                String picturePath = cursor.getString(columnIndex);
                cursor.close();

                File fileDir = new File(picturePath);

                if(!fileDir.exists()){

                    return;

                }

                try {

                    Bitmap bitmap;
                    Bitmap bitmapCache;



                    bitmap = decodeFile(fileDir) ;
                    bitmapCache=decodeFileToCache(fileDir);

                    Matrix m = new Matrix();
                    m.postRotate( neededRotation(fileDir) );

                    bitmap = Bitmap.createBitmap(bitmap,
                            0, 0, bitmap.getWidth(), bitmap.getHeight(),
                            m, true);

                    bitmapCache=Bitmap.createBitmap(bitmapCache,
                            0, 0, bitmapCache.getWidth(), bitmapCache.getHeight(),
                            m, true);

                    String path = android.os.Environment

                            .getExternalStorageDirectory()

                            + File.separator

                            + "MoneyHolder" + File.separator + "Tickets";

                    String path_cache = android.os.Environment

                            .getExternalStorageDirectory()

                            + File.separator

                            + "MoneyHolder" + File.separator + ".cache";




                    File pathik=new File(path);
                    if(!pathik.exists()){
                        pathik.mkdirs();
                        File file = new File(pathik,".nomedia");
                        file.createNewFile();
                    }

                    File path_cache_file=new File(path_cache);
                    if(!path_cache_file.exists()){
                        path_cache_file.mkdirs();
                        File file = new File(path_cache_file,".nomedia");
                        file.createNewFile();
                    }


                    OutputStream outFile = null;
                    OutputStream outFileCache = null;

                    SimpleDateFormat sp=new SimpleDateFormat("dd-MM-yyyy-HH-mm-ss");
                    String filename="ticket-"+sp.format(System.currentTimeMillis())  + ".jpg";

                    File file = new File(path,filename);
                    File fileTocache = new File(path_cache,filename);


                    try {

                        outFile = new FileOutputStream(file);
                        outFileCache=new FileOutputStream(fileTocache);

                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outFile);
                        bitmapCache.compress(Bitmap.CompressFormat.JPEG, 100, outFileCache);


                        outFile.flush();
                        outFileCache.flush();

                        outFile.close();
                        outFileCache.close();

                        PhotoDetails temp=new PhotoDetails(file.getAbsolutePath(),fileTocache.getAbsolutePath(),uid_code);
                        myTickets.add(temp);
                        myTickedAdapter.notifyDataSetChanged();


                    } catch (FileNotFoundException e) {

                        e.printStackTrace();

                    } catch (IOException e) {

                        e.printStackTrace();

                    } catch (Exception e) {

                        e.printStackTrace();

                    }

                } catch (Exception e) {

                    e.printStackTrace();
                }


            }
            if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {

                File fileDir = new File(CalcActivity.this.getExternalFilesDir(null),"temp.jpg");

                if(!fileDir.exists()){
                    return;

                }

                try {

                    Bitmap bitmap;
                    Bitmap bitmapCache;



                    bitmap = decodeFile(fileDir) ;
                    bitmapCache=decodeFileToCache(fileDir);

                    Matrix m = new Matrix();
                    m.postRotate( neededRotation(fileDir) );

                    bitmap = Bitmap.createBitmap(bitmap,
                            0, 0, bitmap.getWidth(), bitmap.getHeight(),
                            m, true);

                    bitmapCache=Bitmap.createBitmap(bitmapCache,
                            0, 0, bitmapCache.getWidth(), bitmapCache.getHeight(),
                            m, true);

                    String path = android.os.Environment

                            .getExternalStorageDirectory()

                            + File.separator

                            + "MoneyHolder" + File.separator + "Tickets";

                    String path_cache = android.os.Environment

                            .getExternalStorageDirectory()

                            + File.separator

                            + "MoneyHolder" + File.separator + ".cache";


                    fileDir.delete();


                    File pathik=new File(path);
                    if(!pathik.exists()){
                        pathik.mkdirs();
                        File file = new File(pathik,".nomedia");
                        file.createNewFile();
                    }

                    File path_cache_file=new File(path_cache);
                    if(!path_cache_file.exists()){
                        path_cache_file.mkdirs();
                        File file = new File(path_cache_file,".nomedia");
                        file.createNewFile();
                    }


                    OutputStream outFile = null;
                    OutputStream outFileCache = null;

                    SimpleDateFormat sp=new SimpleDateFormat("dd-MM-yyyy-HH:mm:ss");
                    String filename="ticket-"+sp.format(System.currentTimeMillis())  + ".jpg";

                    File file = new File(path,filename);
                    File fileTocache = new File(path_cache,filename);


                    try {

                        outFile = new FileOutputStream(file);
                        outFileCache=new FileOutputStream(fileTocache);

                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outFile);
                        bitmapCache.compress(Bitmap.CompressFormat.JPEG, 100, outFileCache);


                        outFile.flush();
                        outFileCache.flush();

                        outFile.close();
                        outFileCache.close();

                        PhotoDetails temp=new PhotoDetails(file.getAbsolutePath(),fileTocache.getAbsolutePath(),uid_code);
                        myTickets.add(temp);
                        myTickedAdapter.notifyDataSetChanged();


                    } catch (FileNotFoundException e) {

                        e.printStackTrace();

                    } catch (IOException e) {

                        e.printStackTrace();

                    } catch (Exception e) {

                        e.printStackTrace();

                    }

                } catch (Exception e) {

                    e.printStackTrace();
                }

            }


        }


        @Override
        public void onClick(View view) {
            view.startAnimation(buttonClick);
            switch (view.getId()) {
                case R.id.rlCategory:
                    final Dialog dialog = new Dialog(CalcActivity.this);
                    View dialogView = CalcActivity.this.getLayoutInflater().inflate(R.layout.category_choose_list, null);
                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    dialog.setContentView(dialogView);
                    ListView lvCategoryChoose = (ListView) dialogView.findViewById(R.id.lvCategoryChoose);
                    String expanse = getResources().getString(R.string.expanse);
                    String income = getResources().getString(R.string.income);
                    String[] items = new String[2];
                    items[0] = expanse;
                    items[1] = income;
                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(CalcActivity.this, android.R.layout.simple_list_item_1, items);
                    lvCategoryChoose.setAdapter(adapter);
                    lvCategoryChoose.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            ArrayList<RootCategory> categories = new ArrayList<RootCategory>();
                            if (position == 0) {
                                for (int i = 0; i < financeManager.getCategories().size(); i++) {
                                    if (financeManager.getCategories().get(i).getType() == PocketAccounterGeneral.EXPENSE)
                                        categories.add(financeManager.getCategories().get(i));
                                }
                            } else {
                                for (int i = 0; i < financeManager.getCategories().size(); i++) {
                                    if (financeManager.getCategories().get(i).getType() == PocketAccounterGeneral.INCOME)
                                        categories.add(financeManager.getCategories().get(i));
                                }
                            }
                            dialog.dismiss();
                            openCategoryDialog(categories);
                        }
                    });
                    dialog.show();
                    break;
                case R.id.rlSubcategory:
                    openSubCategoryDialog();
                    break;

            }
        }

        private void createNewRecord() {
            onEqual();
            String value = tvRecordEditDisplay.getText().toString();
            if (value.length() > 14)
                value = value.substring(0, 14);
            if (account.isLimited()) {
                double limit = account.getLimitSum();
                double accounted = account.getAmount();
                for (int i = 0; i < financeManager.getRecords().size(); i++) {
                    if (financeManager.getRecords().get(i).getAccount().getId().matches(account.getId())) {

                        if (financeManager.getRecords().get(i).getCategory().getType() == PocketAccounterGeneral.INCOME)
                            accounted = accounted + PocketAccounterGeneral.getCost(financeManager.getRecords().get(i));
                        else
                            accounted = accounted - PocketAccounterGeneral.getCost(financeManager.getRecords().get(i));
                    }
                }
                try {
                    if (category.getType() == PocketAccounterGeneral.INCOME)
                        accounted = accounted + PocketAccounterGeneral.getCost(date, currency, Double.parseDouble(tvRecordEditDisplay.getText().toString()));
                    else
                        accounted = accounted - PocketAccounterGeneral.getCost(date, currency, Double.parseDouble(tvRecordEditDisplay.getText().toString()));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                for (DebtBorrow debtBorrow : financeManager.getDebtBorrows()) {
                    if (debtBorrow.isCalculate()) {
                        if (debtBorrow.getAccount().getId().matches(account.getId())) {
                            if (debtBorrow.getType() == DebtBorrow.BORROW) {
                                accounted = accounted + PocketAccounterGeneral.getCost(debtBorrow.getTakenDate(), debtBorrow.getCurrency(), debtBorrow.getAmount());
                            }
                            else {
                                accounted = accounted - PocketAccounterGeneral.getCost(debtBorrow.getTakenDate(), debtBorrow.getCurrency(), debtBorrow.getAmount());
                            }
                            for (Recking recking:debtBorrow.getReckings()) {
                                SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy");
                                Calendar cal = Calendar.getInstance();
                                try {
                                    cal.setTime(format.parse(recking.getPayDate()));
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                                if (debtBorrow.getType() == DebtBorrow.BORROW) {
                                    accounted = accounted - PocketAccounterGeneral.getCost(cal, debtBorrow.getCurrency(), debtBorrow.getAmount());
                                }
                                else {
                                    accounted = accounted + PocketAccounterGeneral.getCost(debtBorrow.getTakenDate(), debtBorrow.getCurrency(), debtBorrow.getAmount());
                                }
                            }
                        }
                    }
                }
                for (CreditDetials creditDetials : financeManager.getCredits()) {
                    if (creditDetials.isKey_for_include()) {
                        for (ReckingCredit reckingCredit : creditDetials.getReckings()) {
                            if (reckingCredit.getAccountId().matches(account.getId())) {
                                Calendar cal = Calendar.getInstance();
                                cal.setTimeInMillis(reckingCredit.getPayDate());
                                accounted = accounted - PocketAccounterGeneral.getCost(cal, creditDetials.getValyute_currency(), reckingCredit.getAmount());
                            }
                        }
                    }
                }
                if (-limit > accounted) {
                    Toast.makeText(CalcActivity.this, R.string.limit_exceed, Toast.LENGTH_SHORT).show();
                    return;
                }
            }
            if (Double.parseDouble(value) != 0) {

                    FinanceRecord newRecord = new FinanceRecord();
                    newRecord.setCategory(category);
                    newRecord.setSubCategory(subCategory);
                    newRecord.setDate(date);
                    newRecord.setAccount(account);
                    newRecord.setCurrency(currency);
                    newRecord.setAmount(Double.parseDouble(tvRecordEditDisplay.getText().toString()));
                    newRecord.setRecordId(uid_code);
                    newRecord.setAllTickets(myTickets);
                    newRecord.setComment(comment_add.getText().toString());
                    financeManager.getRecords().add(newRecord);
                    financeManager.saveRecords();
                PreferenceManager.getDefaultSharedPreferences(CalcActivity.this).edit().putBoolean(KEY_FOR_INSTALAZING,true).apply();
                if(AppWidgetManager.INVALID_APPWIDGET_ID!=WIDGET_ID)
                    WidgetProvider.updateWidget(getApplicationContext(), AppWidgetManager.getInstance(getApplicationContext()),
                            WIDGET_ID);

                finish();

            }
            else {

                (new Thread(new Runnable() {
                    @Override
                    public void run() {
                        for (PhotoDetails temp:myTickets) {
                            File forDeleteTicket=new File(temp.getPhotopath());
                            File forDeleteTicketCache=new File(temp.getPhotopathCache());
                            try {
                                forDeleteTicket.delete();
                                forDeleteTicketCache.delete();
                            }
                            catch (Exception o){
                                o.printStackTrace();
                            }
                        }
                    }
                })).start();
            }

        }

        private void openCategoryDialog(final ArrayList<RootCategory> categories) {
            final Dialog dialog = new Dialog(CalcActivity.this);
            View dialogView = CalcActivity.this.getLayoutInflater().inflate(R.layout.category_choose_list, null);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(dialogView);
            ListView lvCategoryChoose = (ListView) dialogView.findViewById(R.id.lvCategoryChoose);
            RecordCategoryAdapter adapter = new RecordCategoryAdapter(CalcActivity.this, categories);
            lvCategoryChoose.setAdapter(adapter);
            lvCategoryChoose.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    int resId = getResources().getIdentifier(categories.get(position).getIcon(), "drawable", CalcActivity.this.getPackageName());
                    ivRecordEditCategory.setImageResource(resId);
                    ivRecordEditSubCategory.setImageResource(R.drawable.category_not_selected);
                    category = categories.get(position);
                    dialog.dismiss();
                }
            });
            DisplayMetrics dm = getResources().getDisplayMetrics();
            int width = dm.widthPixels;
            dialog.getWindow().setLayout(8 * width / 9, ActionBarOverlayLayout.LayoutParams.MATCH_PARENT);
            dialog.show();
        }

        private void openSubCategoryDialog() {
            final Dialog dialog = new Dialog(CalcActivity.this);
            View dialogView = CalcActivity.this.getLayoutInflater().inflate(R.layout.category_choose_list, null);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(dialogView);
            ListView lvCategoryChoose = (ListView) dialogView.findViewById(R.id.lvCategoryChoose);
            final ArrayList<SubCategory> subCategories = new ArrayList<SubCategory>();
            SubCategory noSubCategory = new SubCategory();
            noSubCategory.setIcon("category_not_selected");
            noSubCategory.setName(getResources().getString(R.string.no_category_name));
            noSubCategory.setId(getResources().getString(R.string.no_category));
            subCategories.add(noSubCategory);
            for (int i = 0; i < category.getSubCategories().size(); i++)
                subCategories.add(category.getSubCategories().get(i));
            RecordSubCategoryAdapter adapter = new RecordSubCategoryAdapter(CalcActivity.this, subCategories);
            lvCategoryChoose.setAdapter(adapter);
            lvCategoryChoose.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    if (subCategories.get(position).getId().matches(getResources().getString(R.string.no_category)))
                        subCategory = null;
                    else
                        subCategory = subCategories.get(position);
                    int resId = getResources().getIdentifier(subCategories.get(position).getIcon(), "drawable", CalcActivity.this.getPackageName());
                    ivRecordEditSubCategory.setImageResource(resId);
                    dialog.dismiss();
                }
            });
            DisplayMetrics dm = getResources().getDisplayMetrics();
            int width = dm.widthPixels;
            dialog.getWindow().setLayout(8 * width / 9, ActionBarOverlayLayout.LayoutParams.MATCH_PARENT);
            dialog.show();
        }

        @Override
        public void onDestroy(){
            super.onDestroy();
            if(keyForDeleteAllPhotos){
                (new Thread(new Runnable() {
                    @Override
                    public void run() {
                        for (PhotoDetails temp:myTickets) {
                            File forDeleteTicket=new File(temp.getPhotopath());
                            File forDeleteTicketCache=new File(temp.getPhotopathCache());
                            try {
                                forDeleteTicket.delete();
                                forDeleteTicketCache.delete();
                            }
                            catch (Exception o){
                                o.printStackTrace();
                            }
                        }
                    }
                })).start();
            }
            isCalcLayoutOpen=false;
        }
        public void closeLayout(){
            openAddingDialog=false;

            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.HONEYCOMB) {
                if(keykeboard) {
                    RelativeLayout headermain = (RelativeLayout) findViewById(R.id.headermain);
                    keyforback = true;
                    isCalcLayoutOpen=false;


                    (new Handler()).postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                InputMethodManager imm = (InputMethodManager) CalcActivity.this.getSystemService(CalcActivity.this.INPUT_METHOD_SERVICE);
                                if(imm==null)
                                    return;
                                imm.hideSoftInputFromWindow(mainView.getWindowToken(), 0);

                            }
                            catch (Exception o){
                                o.printStackTrace();
                            }


                        }
                    }, 300);

                    comment.setVisibility(View.VISIBLE);
                    findViewById(R.id.addphotopanel).setVisibility(View.VISIBLE);
                    findViewById(R.id.pasdigi).setVisibility(View.VISIBLE);


                    findViewById(R.id.scroleditext).setVisibility(View.GONE);
                    findViewById(R.id.commenee).setVisibility(View.GONE);
                    findViewById(R.id.savepanel).setVisibility(View.GONE);
                    comment_add.setText(oraliqComment);
                    if (!oraliqComment.matches("")) {
                        comment.setText(oraliqComment);
                    } else {
                        comment.setText(getString(R.string.add_comment));
                    }

                    headermain.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, (int) convertDpToPixel((getResources().getDimension(R.dimen.hundred_fivety_four) / getResources().getDisplayMetrics().density), CalcActivity.this)));
                }
                else{
                    RelativeLayout headermain = (RelativeLayout) findViewById(R.id.headermain);
                    keyforback = true;
                    isCalcLayoutOpen=false;


                    (new Handler()).postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            try{
                                LinearLayout linbutview=(LinearLayout) findViewById(R.id.numbersbut);
                                myListPhoto.setVisibility(View.VISIBLE);
                                linbutview.setVisibility(View.VISIBLE);
                            }

                            catch (Exception o){
                                o.printStackTrace();
                            }

                        }
                    }, 300);

                    comment.setVisibility(View.VISIBLE);
                    findViewById(R.id.addphotopanel).setVisibility(View.VISIBLE);
                    findViewById(R.id.pasdigi).setVisibility(View.VISIBLE);


                    findViewById(R.id.scroleditext).setVisibility(View.GONE);
                    findViewById(R.id.commenee).setVisibility(View.GONE);
                    findViewById(R.id.savepanel).setVisibility(View.GONE);

                    comment_add.setText(oraliqComment);
                    if (!oraliqComment.matches("")) {
                        comment.setText(oraliqComment);
                    } else {
                        comment.setText(getString(R.string.add_comment));
                    }

                    headermain.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, (int) convertDpToPixel((getResources().getDimension(R.dimen.hundred_fivety_four) / getResources().getDisplayMetrics().density), CalcActivity.this)));

                }
            }
            else {
                if(keykeboard) {
                    RelativeLayout headermain = (RelativeLayout) findViewById(R.id.headermain);
                    AutoTransition cus = new AutoTransition();
                    keyforback = true;
                    isCalcLayoutOpen=false;
                    cus.addListener(new Transition.TransitionListener() {
                        @Override
                        public void onTransitionStart(Transition transition) {

                        }

                        @Override
                        public void onTransitionEnd(Transition transition) {
//                            if(mainView==null){
//                                return;
//                            }


                            (new Handler()).postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    try {

                                        InputMethodManager imm = (InputMethodManager) CalcActivity.this.getSystemService(CalcActivity.this.INPUT_METHOD_SERVICE);
                                        if(imm==null)
                                            return;
                                        imm.hideSoftInputFromWindow(mainView.getWindowToken(), 0);
                                    }
                                    catch (Exception o){
                                        o.printStackTrace();
                                    }

                                }
                            }, 100);

                        }

                        @Override
                        public void onTransitionCancel(Transition transition) {

                        }

                        @Override
                        public void onTransitionPause(Transition transition) {

                        }

                        @Override
                        public void onTransitionResume(Transition transition) {

                        }
                    });
                    cus.setDuration(200);
                    cus.setStartDelay(0);
                    TransitionManager.beginDelayedTransition(headermain, cus);
                    comment.setVisibility(View.VISIBLE);
                    findViewById(R.id.addphotopanel).setVisibility(View.VISIBLE);
                    findViewById(R.id.pasdigi).setVisibility(View.VISIBLE);


                    findViewById(R.id.scroleditext).setVisibility(View.GONE);
                    findViewById(R.id.commenee).setVisibility(View.GONE);
                    findViewById(R.id.savepanel).setVisibility(View.GONE);
                    comment_add.setText(oraliqComment);
                    if (!oraliqComment.matches("")) {
                        comment.setText(oraliqComment);
                    } else {
                        comment.setText(getString(R.string.add_comment));
                    }

                    headermain.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, (int) convertDpToPixel((getResources().getDimension(R.dimen.hundred_fivety_four) / getResources().getDisplayMetrics().density), CalcActivity.this)));
                }
                else{
                    RelativeLayout headermain = (RelativeLayout) findViewById(R.id.headermain);
                    AutoTransition cus = new AutoTransition();
                    keyforback = true;
                    isCalcLayoutOpen=false;

                    cus.addListener(new Transition.TransitionListener() {
                        @Override
                        public void onTransitionStart(Transition transition) {

                        }

                        @Override
                        public void onTransitionEnd(Transition transition) {
//                            if(mainView==null){
//                                return;
//                            }
                            try{
                                AutoTransition cus=new AutoTransition();
                                cus.setDuration(300);
                                cus.setStartDelay(0);
                                LinearLayout linbutview=(LinearLayout) findViewById(R.id.numbersbut);
                                TransitionManager.beginDelayedTransition(myListPhoto);
                                myListPhoto.setVisibility(View.VISIBLE);
                                TransitionManager.beginDelayedTransition(linbutview,cus);
                                linbutview.setVisibility(View.VISIBLE);
                            }
                            catch (Exception o){
                                o.printStackTrace();
                            }
                        }

                        @Override
                        public void onTransitionCancel(Transition transition) {
                        }

                        @Override
                        public void onTransitionPause(Transition transition) {

                        }

                        @Override
                        public void onTransitionResume(Transition transition) {

                        }
                    });
                    cus.setDuration(200);
                    cus.setStartDelay(0);
                    TransitionManager.beginDelayedTransition(headermain, cus);
                    comment.setVisibility(View.VISIBLE);
                    findViewById(R.id.addphotopanel).setVisibility(View.VISIBLE);
                    findViewById(R.id.pasdigi).setVisibility(View.VISIBLE);


                    findViewById(R.id.scroleditext).setVisibility(View.GONE);
                    findViewById(R.id.commenee).setVisibility(View.GONE);
                    findViewById(R.id.savepanel).setVisibility(View.GONE);

                    comment_add.setText(oraliqComment);
                    if (!oraliqComment.matches("")) {
                        comment.setText(oraliqComment);
                    } else {
                        comment.setText(getString(R.string.add_comment));
                    }

                    headermain.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, (int) convertDpToPixel((getResources().getDimension(R.dimen.hundred_fivety_four) / getResources().getDisplayMetrics().density), CalcActivity.this)));

                }
            }



        }



        private Bitmap decodeFile(File f) {
            try {
//            Decode image size
                BitmapFactory.Options o = new BitmapFactory.Options();
                o.inJustDecodeBounds = true;
                BitmapFactory.decodeStream(new FileInputStream(f), null, o);
//            The new size we want to scale to
                final int REQUIRED_SIZE = 350;
//            Find the correct scale value. It should be the power of 2.
                int scale = 1;
                while (o.outWidth / scale / 2 >= REQUIRED_SIZE && o.outHeight / scale / 2 >= REQUIRED_SIZE)
                    scale *= 2;
                //Decode with inSampleSize
                BitmapFactory.Options o2 = new BitmapFactory.Options();
                o2.inSampleSize = scale;
                return BitmapFactory.decodeStream(new FileInputStream(f), null, o2);
            } catch (FileNotFoundException e) {
            }
            return null;
        }
        private Bitmap decodeFileToCache(File f) {
            try {
//            Decode image size
                BitmapFactory.Options o = new BitmapFactory.Options();
                o.inJustDecodeBounds = true;
                BitmapFactory.decodeStream(new FileInputStream(f), null, o);
//            The new size we want to scale to
                final int REQUIRED_SIZE = 64;
//            Find the correct scale value. It should be the power of 2.
                int scale = 1;
                while (o.outWidth / scale / 2 >= REQUIRED_SIZE && o.outHeight / scale / 2 >= REQUIRED_SIZE)
                    scale *= 2;
                //Decode with inSampleSize
                BitmapFactory.Options o2 = new BitmapFactory.Options();
                o2.inSampleSize = scale;
                return BitmapFactory.decodeStream(new FileInputStream(f), null, o2);
            } catch (FileNotFoundException e) {
            }
            return null;
        }

        public static int neededRotation(File ff)
        {
            try
            {

                ExifInterface exif = new ExifInterface(ff.getAbsolutePath());
                int orientation = exif.getAttributeInt(
                        ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

                if (orientation == ExifInterface.ORIENTATION_ROTATE_270)
                { return 270; }
                if (orientation == ExifInterface.ORIENTATION_ROTATE_180)
                { return 180; }
                if (orientation == ExifInterface.ORIENTATION_ROTATE_90)
                { return 90; }
                return 0;

            } catch (FileNotFoundException e)
            {
                e.printStackTrace();
            } catch (IOException e)
            {
                e.printStackTrace();
            }
            return 0;
        }
        public  float convertDpToPixel(float dp, Context context) {
            Resources resources = context.getResources();
            DisplayMetrics metrics = resources.getDisplayMetrics();
            float px = dp * ((float) metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
            return px;
        }

        @Override
        public void onBackPressed() {
            if (isCalcLayoutOpen ) {
                Log.d("kakdi", "onBackPressed: ");
                closeLayout();
                return;

            }
            super.onBackPressed();
        }
    }