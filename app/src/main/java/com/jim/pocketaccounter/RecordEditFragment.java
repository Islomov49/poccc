package com.jim.pocketaccounter;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.ActionBarOverlayLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
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

import com.jim.pocketaccounter.credit.CreditDetials;
import com.jim.pocketaccounter.credit.ReckingCredit;
import com.jim.pocketaccounter.debt.DebtBorrow;
import com.jim.pocketaccounter.debt.Recking;
import com.jim.pocketaccounter.finance.Account;
import com.jim.pocketaccounter.finance.Currency;
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

@SuppressLint("ValidFragment")
public class RecordEditFragment extends Fragment implements OnClickListener {
    private boolean keyforback=false;
    private TextView tvRecordEditDisplay;
    private ImageView ivToolbarMostRight, ivRecordEditCategory, ivRecordEditSubCategory, ivToolbaExcel;
    private Spinner spRecordEdit, spToolbar;
    private RootCategory category;
    private SubCategory subCategory;
    private FinanceRecord record;
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
    Context contex;
    private boolean keyForDeleteAllPhotos=true;
    static final int REQUEST_IMAGE_CAPTURE = 112;
    private String uid_code;
    RecyclerView myListPhoto;
    ArrayList<PhotoDetails> myTickets;
    ArrayList<PhotoDetails> myTicketsFromBackRoll;
    PhotoAdapter myTickedAdapter;
    boolean fromEdit=false;
    boolean openAddingDialog=false;
    View mainView;


    @SuppressLint("ValidFragment")

    public RecordEditFragment(RootCategory category, Calendar date, FinanceRecord record, int parent) {
        this.parent = parent;
        DecimalFormatSymbols otherSymbols = new DecimalFormatSymbols();
        otherSymbols.setDecimalSeparator('.');
        otherSymbols.setGroupingSeparator('.');
        decimalFormat = new DecimalFormat("0.00##", otherSymbols);
        if (category != null) {
            for (int i = 0; i < PocketAccounter.financeManager.getCategories().size(); i++) {
                if (category.getId().matches(PocketAccounter.financeManager.getCategories().get(i).getId()))
                    this.category = PocketAccounter.financeManager.getCategories().get(i);
            }
            this.subCategory = null;
        } else {
            this.category = record.getCategory();
            this.subCategory = record.getSubCategory();
        }
        this.date = (Calendar) date.clone();
        this.record = record;

    }
    public  interface OpenIntentFromAdapter{
        void startActivityFromFragmentForResult(Intent intent);
    }
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setRetainInstance(true);
        mainView= inflater.inflate(R.layout.record_edit_modern, container, false);
        contex=getActivity();
        comment = (TextView) mainView.findViewById(R.id.textView18);
        comment_add = (EditText) mainView.findViewById(R.id.comment_add);



        uid_code= "record_" + UUID.randomUUID().toString();

        buttonClick = AnimationUtils.loadAnimation(getContext(), R.anim.button_click);
        ((PocketAccounter) getContext()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((PocketAccounter) getContext()).getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_back_button);
        PocketAccounter.toolbar.setNavigationOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (openAddingDialog){
                    closeLayout();
                    return;
                }
                else {
                    InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    if(imm==null)
                        return;
                    imm.hideSoftInputFromWindow(mainView.getWindowToken(), 0);

                    (new Handler()).postDelayed(new Runnable() {
                        @Override
                        public void run() {

                            ((PocketAccounter) getContext()).getSupportFragmentManager().popBackStack();
                            if (parent == PocketAccounterGeneral.MAIN) {
                                ((PocketAccounter) getContext()).initialize(date);
                            } else {
                                ((PocketAccounter) getContext()).replaceFragment(new RecordDetailFragment(date));
                            }

                        }
                    }, 100);
                }
            }
        });
        PocketAccounter.toolbar.setTitle("");
        PocketAccounter.toolbar.setSubtitle("");
        spRecordEdit = (Spinner) mainView.findViewById(R.id.spRecordEdit);
        spToolbar = (Spinner) PocketAccounter.toolbar.findViewById(R.id.spToolbar);
        spToolbar.setVisibility(View.VISIBLE);
        RecordAccountAdapter accountAdapter = new RecordAccountAdapter(getContext(), PocketAccounter.financeManager.getAccounts());
        spToolbar.setAdapter(accountAdapter);
        spToolbar.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                account = PocketAccounter.financeManager.getAccounts().get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        final String[] currencies = new String[PocketAccounter.financeManager.getCurrencies().size()];
        for (int i = 0; i < PocketAccounter.financeManager.getCurrencies().size(); i++)
            currencies[i] = PocketAccounter.financeManager.getCurrencies().get(i).getAbbr();
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), R.layout.spinner_single_item_calc, currencies);
        spRecordEdit.setAdapter(adapter);
        for (int i = 0; i < PocketAccounter.financeManager.getCurrencies().size(); i++) {
            if (PocketAccounter.financeManager.getCurrencies().get(i).getId().matches(PocketAccounter.financeManager.getMainCurrency().getId())) {
                spRecordEdit.setSelection(i);
                break;
            }
        }
        spRecordEdit.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                currency = PocketAccounter.financeManager.getCurrencies().get(position);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        ivToolbarMostRight = (ImageView) PocketAccounter.toolbar.findViewById(R.id.ivToolbarMostRight);
        ivToolbarMostRight.setVisibility(View.GONE);


        ivToolbaExcel = (ImageView) PocketAccounter.toolbar.findViewById(R.id.ivToolbarExcel);
        ivToolbaExcel.setVisibility(View.GONE);
        ivRecordEditCategory = (ImageView) mainView.findViewById(R.id.ivRecordEditCategory);
        ivRecordEditSubCategory = (ImageView) mainView.findViewById(R.id.ivRecordEditSubCategory);
        tvRecordEditDisplay = (TextView) mainView.findViewById(R.id.tvRecordEditDisplay);
        rlCategory = (RelativeLayout) mainView.findViewById(R.id.rlCategory);
        rlCategory.setOnClickListener(this);
        rlSubCategory = (RelativeLayout) mainView.findViewById(R.id.rlSubcategory);
        rlSubCategory.setOnClickListener(this);
        setNumericOnClickListener(mainView);
        setOperatorOnClickListener(mainView);
        DecimalFormatSymbols otherSymbols = new DecimalFormatSymbols();
        otherSymbols.setDecimalSeparator('.');
        otherSymbols.setGroupingSeparator('.');
        DecimalFormat decimalFormat = new DecimalFormat("0.00", otherSymbols);
        if (category != null) {
            ivRecordEditSubCategory.setImageResource(R.drawable.category_not_selected);
            int resId = getResources().getIdentifier(category.getIcon(), "drawable", getContext().getPackageName());

            ivRecordEditCategory.setImageResource(resId);
        }
        if (record != null) {
            fromEdit=true;

            int resId = getResources().getIdentifier(record.getCategory().getIcon(), "drawable", getContext().getPackageName());
            ivRecordEditCategory.setImageResource(resId);
            if (record.getSubCategory() != null) {
                resId = getResources().getIdentifier(record.getSubCategory().getIcon(), "drawable", getContext().getPackageName());
                ivRecordEditSubCategory.setImageResource(resId);
            }
            else
                ivRecordEditSubCategory.setImageResource(R.drawable.category_not_selected);
            tvRecordEditDisplay.setText(decimalFormat.format(record.getAmount()));
            for (int i = 0; i < PocketAccounter.financeManager.getCurrencies().size(); i++) {
                if (PocketAccounter.financeManager.getCurrencies().get(i).getId().matches(record.getCurrency().getId())) {
                    spRecordEdit.setSelection(i);
                    break;
                }
            }
            for (int i = 0; i < PocketAccounter.financeManager.getAccounts().size(); i++) {
                if (PocketAccounter.financeManager.getAccounts().get(i).getId().matches(record.getAccount().getId())) {
                    spToolbar.setSelection(i);
                    break;
                }
            }
            myTickets= (ArrayList<PhotoDetails>) record.getAllTickets().clone();
            myTicketsFromBackRoll= (ArrayList<PhotoDetails>) myTickets.clone();
            if(record.getComment()!=null&&!record.getComment().matches("")){
                oraliqComment=record.getComment();
                comment.setText(oraliqComment);
                comment_add.setText(oraliqComment);
            }
        }
        if(myTickets==null)
        myTickets=new ArrayList<>();
        if(myTicketsFromBackRoll==null)
            myTicketsFromBackRoll=new ArrayList<>();
        LinearLayoutManager layoutManager
                = new LinearLayoutManager(contex, LinearLayoutManager.HORIZONTAL, false);
        myListPhoto = (RecyclerView) mainView.findViewById(R.id.recycler_calc);
        myListPhoto.setLayoutManager(layoutManager);
        myTickedAdapter =new PhotoAdapter(myTickets,contex, new OpenIntentFromAdapter() {
            @Override
            public void startActivityFromFragmentForResult(Intent intent) {
                PocketAccounter.openActivity=true;
                startActivityForResult(intent,REQUEST_DELETE_PHOTOS);
            }
        });
        myListPhoto.setAdapter(myTickedAdapter);


        return mainView;
    }

    private void setNumericOnClickListener(View view) {
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
            view.findViewById(id).setOnClickListener(listener);
    }

    private void setOperatorOnClickListener(final View view) {
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
            view.findViewById(id).setOnClickListener(listener);
        view.findViewById(R.id.rlDot).setOnClickListener(new View.OnClickListener() {
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
        view.findViewById(R.id.choose_photo).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Choose type adding")
                        .setItems(R.array.adding_ticket_type, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                               if(which==0){
                                   int permission = ContextCompat.checkSelfPermission(getContext(),
                                           android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
                                   if (permission != PackageManager.PERMISSION_GRANTED) {
                                       if (ActivityCompat.shouldShowRequestPermissionRationale(((PocketAccounter) getContext()),
                                               android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                                           android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(getContext());
                                           builder.setMessage("Permission to access the SD-CARD is required for this app to Download PDF.")
                                                   .setTitle("Permission required");

                                           builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                                               public void onClick(DialogInterface dialog, int id) {
                                                   ActivityCompat.requestPermissions((PocketAccounter) getContext(),
                                                           new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                                           PERMISSION_READ_STORAGE);
                                               }
                                           });
                                           android.app.AlertDialog dialogik = builder.create();
                                           dialogik.show();

                                       } else {
                                           ActivityCompat.requestPermissions((PocketAccounter) getContext(),
                                                   new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                                   PERMISSION_READ_STORAGE);
                                       }
                                   } else {
                                       getPhoto();
                                   }
                               }
                                else if(which==1){
                                   Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                   if (takePictureIntent.resolveActivity(contex.getPackageManager()) != null) {

                                           File f = new File(contex.getExternalFilesDir(null),"temp.jpg");

                                           takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
                                       PocketAccounter.openActivity=true;
                                           startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);

                                   }


                               }
                            }
                        });
                 builder.create().show();

            }
        });
        view.findViewById(R.id.rlBackspaceSign).setOnClickListener(new OnClickListener() {
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

        comment.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                LinearLayout linbutview=(LinearLayout) view.findViewById(R.id.numbersbut);
                TransitionManager.beginDelayedTransition(linbutview);
                linbutview.setVisibility(View.GONE);
                keyforback=false;
                openAddingDialog=true;
                PocketAccounter.isCalcLayoutOpen=true;
                (new Handler()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        comment_add.setFocusableInTouchMode(true);
                        comment_add.requestFocus();
                        final InputMethodManager inputMethodManager = (InputMethodManager) getContext()
                                .getSystemService(Context.INPUT_METHOD_SERVICE);
                        if(inputMethodManager==null)
                            return;
                        inputMethodManager.showSoftInput(comment_add, InputMethodManager.SHOW_IMPLICIT);
                    }
                },200);


            }
        });
        view.findViewById(R.id.savesecbut).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(final View v) {
                keyForDeleteAllPhotos=false;
                if(keykeboard){
                    InputMethodManager imm = (InputMethodManager)getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    if(imm==null)
                        return;
                    imm.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_IMPLICIT_ONLY);
                    PocketAccounter.isCalcLayoutOpen=false;

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



                view.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        int heightDiff = view.getRootView().getHeight() - view.getHeight();
                        if (heightDiff > PocketAccounter.convertDpToPixel( 200,contex)) { // if more than 200 dp, it's probably a keyboard...
                            if(keykeboard!=true){

                                keykeboard=true;

                                final Handler handler = new Handler();
                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {


                                        RelativeLayout headermain= (RelativeLayout) view.findViewById(R.id.headermain);
                                        AutoTransition cus=new AutoTransition();
                                        cus.setDuration(200);
                                        cus.setStartDelay(0);
                                        TransitionManager.beginDelayedTransition(headermain,cus);

                                        headermain.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));


                                        comment.setVisibility(View.GONE);
                                        view.findViewById(R.id.addphotopanel).setVisibility(View.GONE);
                                        view.findViewById(R.id.pasdigi).setVisibility(View.GONE);
                                        myListPhoto.setVisibility(View.GONE);

                                        view.findViewById(R.id.scroleditext).setVisibility(View.VISIBLE);
                                        view.findViewById(R.id.commenee).setVisibility(View.VISIBLE);
                                        view.findViewById(R.id.savepanel).setVisibility(View.VISIBLE);
                                    }
                                }, 50);




                            }
                        }
                        else {
                            if(keykeboard){
                                keykeboard=false;

                                if (keyforback){

                                    final Handler handler = new Handler();
                                    handler.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            AutoTransition cus=new AutoTransition();
                                            cus.setDuration(300);
                                            cus.setStartDelay(0);
                                            LinearLayout linbutview=(LinearLayout) view.findViewById(R.id.numbersbut);
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



        view.findViewById(R.id.addcomment).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                openAddingDialog=false;
                if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.HONEYCOMB) {
                    if(keykeboard) {
                        RelativeLayout headermain = (RelativeLayout) view.findViewById(R.id.headermain);
                        keyforback = true;

                            (new Handler()).postDelayed(new Runnable() {
                                @Override
                                public void run() {

                                    try {
                                    InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                                    if(imm==null)
                                        return;
                                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                                    }
                                    catch (Exception o){
                                        o.printStackTrace();
                                    }

                                }
                            }, 120);

                        PocketAccounter.isCalcLayoutOpen=false;
                        comment.setVisibility(View.VISIBLE);
                        view.findViewById(R.id.addphotopanel).setVisibility(View.VISIBLE);
                        view.findViewById(R.id.pasdigi).setVisibility(View.VISIBLE);

                        view.findViewById(R.id.scroleditext).setVisibility(View.GONE);
                        view.findViewById(R.id.commenee).setVisibility(View.GONE);
                        view.findViewById(R.id.savepanel).setVisibility(View.GONE);
                        oraliqComment = comment_add.getText().toString();
                        if (!oraliqComment.matches("")) {
                            comment.setText(oraliqComment);
                        } else {
                            comment.setText(getString(R.string.add_comment));
                        }

                        headermain.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, (int) PocketAccounter.convertDpToPixel((getResources().getDimension(R.dimen.hundred_fivety_four) / getResources().getDisplayMetrics().density), contex)));
                    }
                    else{
                        RelativeLayout headermain = (RelativeLayout) view.findViewById(R.id.headermain);
                        keyforback = true;


                            (new Handler()).postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    try {

                                    LinearLayout linbutview=(LinearLayout) view.findViewById(R.id.numbersbut);
                                    myListPhoto.setVisibility(View.VISIBLE);
                                    linbutview.setVisibility(View.VISIBLE);
                                    }
                                    catch (Exception o){
                                        o.printStackTrace();
                                    }

                                }
                            }, 120);



                        comment.setVisibility(View.VISIBLE);
                        view.findViewById(R.id.addphotopanel).setVisibility(View.VISIBLE);
                        view.findViewById(R.id.pasdigi).setVisibility(View.VISIBLE);


                        view.findViewById(R.id.scroleditext).setVisibility(View.GONE);
                        view.findViewById(R.id.commenee).setVisibility(View.GONE);
                        view.findViewById(R.id.savepanel).setVisibility(View.GONE);
                        oraliqComment = comment_add.getText().toString();
                        if (!oraliqComment.matches("")) {
                            comment.setText(oraliqComment);
                        } else {
                            comment.setText(getString(R.string.add_comment));
                        }

                        headermain.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, (int) PocketAccounter.convertDpToPixel((getResources().getDimension(R.dimen.hundred_fivety_four) / getResources().getDisplayMetrics().density), contex)));

                    }
                }
                else
                if(keykeboard) {
                    RelativeLayout headermain = (RelativeLayout) view.findViewById(R.id.headermain);
                    AutoTransition cus = new AutoTransition();
                    keyforback = true;

                    PocketAccounter.isCalcLayoutOpen=false;
                    cus.addListener(new Transition.TransitionListener() {
                        @Override
                        public void onTransitionStart(Transition transition) {

                        }

                        @Override
                        public void onTransitionEnd(Transition transition) {
                            if(mainView==null){
                                return;
                            }


                               (new Handler()).postDelayed(new Runnable() {
                                   @Override
                                   public void run() {
                                       try {
                                           InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                                           if(imm==null)
                                               return;
                                           imm.hideSoftInputFromWindow(view.getWindowToken(), 0);

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
                    view.findViewById(R.id.addphotopanel).setVisibility(View.VISIBLE);
                    view.findViewById(R.id.pasdigi).setVisibility(View.VISIBLE);

                    view.findViewById(R.id.scroleditext).setVisibility(View.GONE);
                    view.findViewById(R.id.commenee).setVisibility(View.GONE);
                    view.findViewById(R.id.savepanel).setVisibility(View.GONE);
                    oraliqComment = comment_add.getText().toString();
                    if (!oraliqComment.matches("")) {
                        comment.setText(oraliqComment);
                    } else {
                        comment.setText(getString(R.string.add_comment));
                    }

                    headermain.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, (int) PocketAccounter.convertDpToPixel((getResources().getDimension(R.dimen.hundred_fivety_four) / getResources().getDisplayMetrics().density), contex)));
                }
                else{
                    RelativeLayout headermain = (RelativeLayout) view.findViewById(R.id.headermain);
                    AutoTransition cus = new AutoTransition();
                    keyforback = true;
                    cus.addListener(new Transition.TransitionListener() {
                        @Override
                        public void onTransitionStart(Transition transition) {

                        }

                        @Override
                        public void onTransitionEnd(Transition transition) {
                            if(mainView==null){
                                return;
                            }
                            try {

                                AutoTransition cus=new AutoTransition();
                                cus.setDuration(300);
                                cus.setStartDelay(0);
                                LinearLayout linbutview=(LinearLayout) view.findViewById(R.id.numbersbut);
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
                    view.findViewById(R.id.addphotopanel).setVisibility(View.VISIBLE);
                    view.findViewById(R.id.pasdigi).setVisibility(View.VISIBLE);


                    view.findViewById(R.id.scroleditext).setVisibility(View.GONE);
                    view.findViewById(R.id.commenee).setVisibility(View.GONE);
                    view.findViewById(R.id.savepanel).setVisibility(View.GONE);
                    oraliqComment = comment_add.getText().toString();
                    if (!oraliqComment.matches("")) {
                        comment.setText(oraliqComment);
                    } else {
                        comment.setText(getString(R.string.add_comment));
                    }

                    headermain.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, (int) PocketAccounter.convertDpToPixel((getResources().getDimension(R.dimen.hundred_fivety_four) / getResources().getDisplayMetrics().density), contex)));

                }


            }
        });

        view.findViewById(R.id.rlBackspaceSign).setOnLongClickListener(new View.OnLongClickListener() {
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
        view.findViewById(R.id.imOK).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(final View v) {
                keyForDeleteAllPhotos=false;
                if(keykeboard){
                    InputMethodManager imm = (InputMethodManager)getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    if(imm==null)
                        return;
                    imm.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_IMPLICIT_ONLY);

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
        view.findViewById(R.id.rlEqualSign).setOnClickListener(new View.OnClickListener() {
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
        PocketAccounter.openActivity=true;
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
            Cursor cursor = getActivity().getContentResolver().query(selectedImage,
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

            File fileDir = new File(contex.getExternalFilesDir(null),"temp.jpg");

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
                final Dialog dialog = new Dialog(getActivity());
                View dialogView = getActivity().getLayoutInflater().inflate(R.layout.category_choose_list, null);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(dialogView);
                ListView lvCategoryChoose = (ListView) dialogView.findViewById(R.id.lvCategoryChoose);
                String expanse = getResources().getString(R.string.expanse);
                String income = getResources().getString(R.string.income);
                String[] items = new String[2];
                items[0] = expanse;
                items[1] = income;
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, items);
                lvCategoryChoose.setAdapter(adapter);
                lvCategoryChoose.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        ArrayList<RootCategory> categories = new ArrayList<RootCategory>();
                        if (position == 0) {
                            for (int i = 0; i < PocketAccounter.financeManager.getCategories().size(); i++) {
                                if (PocketAccounter.financeManager.getCategories().get(i).getType() == PocketAccounterGeneral.EXPENSE)
                                    categories.add(PocketAccounter.financeManager.getCategories().get(i));
                            }
                        } else {
                            for (int i = 0; i < PocketAccounter.financeManager.getCategories().size(); i++) {
                                if (PocketAccounter.financeManager.getCategories().get(i).getType() == PocketAccounterGeneral.INCOME)
                                    categories.add(PocketAccounter.financeManager.getCategories().get(i));
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
            for (int i = 0; i < PocketAccounter.financeManager.getRecords().size(); i++) {
                if (PocketAccounter.financeManager.getRecords().get(i).getAccount().getId().matches(account.getId())) {
                    if (record != null && PocketAccounter.financeManager.getRecords().get(i).getRecordId().matches(record.getRecordId()))
                        continue;
                    if (PocketAccounter.financeManager.getRecords().get(i).getCategory().getType() == PocketAccounterGeneral.INCOME)
                        accounted = accounted + PocketAccounterGeneral.getCost(PocketAccounter.financeManager.getRecords().get(i));
                    else
                        accounted = accounted - PocketAccounterGeneral.getCost(PocketAccounter.financeManager.getRecords().get(i));
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
            for (DebtBorrow debtBorrow : PocketAccounter.financeManager.getDebtBorrows()) {
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
            for (CreditDetials creditDetials : PocketAccounter.financeManager.getCredits()) {
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
                Toast.makeText(getContext(), R.string.limit_exceed, Toast.LENGTH_SHORT).show();
                return;
            }
        }
        if (Double.parseDouble(value) != 0) {
            if (record != null) {
                record.setCategory(category);
                record.setSubCategory(subCategory);
                record.setDate(date);
                record.setAccount(account);
                record.setCurrency(currency);
                record.setAllTickets(myTickets);
                record.setComment(comment_add.getText().toString());
                record.setAmount(Double.parseDouble(tvRecordEditDisplay.getText().toString()));
            } else {
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
                PocketAccounter.financeManager.getRecords().add(newRecord);
            }
        }
        else {
            if(fromEdit){
                record.setAllTickets(myTicketsFromBackRoll);
                for (PhotoDetails temp:myTicketsFromBackRoll) {
                    myTickets.remove(temp);
                }
            }
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
        if (parent != PocketAccounterGeneral.MAIN) {
            if (((PocketAccounter) getContext()).getSupportFragmentManager().getBackStackEntryCount() != 0) {
                FragmentManager fm = ((PocketAccounter) getContext()).getSupportFragmentManager();
                for (int i = 0; i < fm.getBackStackEntryCount(); i++)
                    fm.popBackStack();
                ((PocketAccounter) getContext()).replaceFragment(new RecordDetailFragment(date));
            }
        }
        else {
            ((PocketAccounter) getContext()).initialize(date);
            ((PocketAccounter) getContext()).getSupportFragmentManager().popBackStack();
        }
    }

    private void openCategoryDialog(final ArrayList<RootCategory> categories) {
        final Dialog dialog = new Dialog(getActivity());
        View dialogView = getActivity().getLayoutInflater().inflate(R.layout.category_choose_list, null);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(dialogView);
        ListView lvCategoryChoose = (ListView) dialogView.findViewById(R.id.lvCategoryChoose);
        RecordCategoryAdapter adapter = new RecordCategoryAdapter(getContext(), categories);
        lvCategoryChoose.setAdapter(adapter);
        lvCategoryChoose.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                int resId = getResources().getIdentifier(categories.get(position).getIcon(), "drawable", getContext().getPackageName());
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
        final Dialog dialog = new Dialog(getActivity());
        View dialogView = getActivity().getLayoutInflater().inflate(R.layout.category_choose_list, null);
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
        RecordSubCategoryAdapter adapter = new RecordSubCategoryAdapter(getContext(), subCategories);
        lvCategoryChoose.setAdapter(adapter);
        lvCategoryChoose.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (subCategories.get(position).getId().matches(getResources().getString(R.string.no_category)))
                    subCategory = null;
                else
                    subCategory = subCategories.get(position);
                int resId = getResources().getIdentifier(subCategories.get(position).getIcon(), "drawable", getContext().getPackageName());
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
            if(fromEdit){
            for (PhotoDetails temp:myTicketsFromBackRoll) {
                myTickets.remove(temp);
            }
                record.setAllTickets(myTicketsFromBackRoll);
            }
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
        PocketAccounter.isCalcLayoutOpen=false;
        mainView=null;
    }
    public void closeLayout(){
        openAddingDialog=false;

        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.HONEYCOMB) {
            if(keykeboard) {
                RelativeLayout headermain = (RelativeLayout) mainView.findViewById(R.id.headermain);
                keyforback = true;
                PocketAccounter.isCalcLayoutOpen=false;


                    (new Handler()).postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
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
                mainView.findViewById(R.id.addphotopanel).setVisibility(View.VISIBLE);
                mainView.findViewById(R.id.pasdigi).setVisibility(View.VISIBLE);


                mainView.findViewById(R.id.scroleditext).setVisibility(View.GONE);
                mainView.findViewById(R.id.commenee).setVisibility(View.GONE);
                mainView.findViewById(R.id.savepanel).setVisibility(View.GONE);
                comment_add.setText(oraliqComment);
                if (!oraliqComment.matches("")) {
                    comment.setText(oraliqComment);
                } else {
                    comment.setText(getString(R.string.add_comment));
                }

                headermain.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, (int) PocketAccounter.convertDpToPixel((getResources().getDimension(R.dimen.hundred_fivety_four) / getResources().getDisplayMetrics().density), contex)));
            }
            else{
                RelativeLayout headermain = (RelativeLayout) mainView.findViewById(R.id.headermain);
                keyforback = true;
                PocketAccounter.isCalcLayoutOpen=false;


                    (new Handler()).postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            try{
                            LinearLayout linbutview=(LinearLayout) mainView.findViewById(R.id.numbersbut);
                            myListPhoto.setVisibility(View.VISIBLE);
                            linbutview.setVisibility(View.VISIBLE);
                            }

                                catch (Exception o){
                                    o.printStackTrace();
                                }

                            }
                    }, 300);

                comment.setVisibility(View.VISIBLE);
                mainView.findViewById(R.id.addphotopanel).setVisibility(View.VISIBLE);
                mainView.findViewById(R.id.pasdigi).setVisibility(View.VISIBLE);


                mainView.findViewById(R.id.scroleditext).setVisibility(View.GONE);
                mainView.findViewById(R.id.commenee).setVisibility(View.GONE);
                mainView.findViewById(R.id.savepanel).setVisibility(View.GONE);

                comment_add.setText(oraliqComment);
                if (!oraliqComment.matches("")) {
                    comment.setText(oraliqComment);
                } else {
                    comment.setText(getString(R.string.add_comment));
                }

                headermain.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, (int) PocketAccounter.convertDpToPixel((getResources().getDimension(R.dimen.hundred_fivety_four) / getResources().getDisplayMetrics().density), contex)));

            }
        }
        else {
            if(keykeboard) {
                RelativeLayout headermain = (RelativeLayout) mainView.findViewById(R.id.headermain);
                AutoTransition cus = new AutoTransition();
                keyforback = true;
                PocketAccounter.isCalcLayoutOpen=false;
                cus.addListener(new Transition.TransitionListener() {
                    @Override
                    public void onTransitionStart(Transition transition) {

                    }

                    @Override
                    public void onTransitionEnd(Transition transition) {
                        if(mainView==null){
                            return;
                        }


                            (new Handler()).postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    try {

                                        InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
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
                mainView.findViewById(R.id.addphotopanel).setVisibility(View.VISIBLE);
                mainView.findViewById(R.id.pasdigi).setVisibility(View.VISIBLE);


                mainView.findViewById(R.id.scroleditext).setVisibility(View.GONE);
                mainView.findViewById(R.id.commenee).setVisibility(View.GONE);
                mainView.findViewById(R.id.savepanel).setVisibility(View.GONE);
                comment_add.setText(oraliqComment);
                if (!oraliqComment.matches("")) {
                    comment.setText(oraliqComment);
                } else {
                    comment.setText(getString(R.string.add_comment));
                }

                headermain.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, (int) PocketAccounter.convertDpToPixel((getResources().getDimension(R.dimen.hundred_fivety_four) / getResources().getDisplayMetrics().density), contex)));
            }
            else{
                RelativeLayout headermain = (RelativeLayout) mainView.findViewById(R.id.headermain);
                AutoTransition cus = new AutoTransition();
                keyforback = true;
                PocketAccounter.isCalcLayoutOpen=false;

                cus.addListener(new Transition.TransitionListener() {
                    @Override
                    public void onTransitionStart(Transition transition) {

                    }

                    @Override
                    public void onTransitionEnd(Transition transition) {
                        if(mainView==null){
                            return;
                        }
                        try{
                            AutoTransition cus=new AutoTransition();
                            cus.setDuration(300);
                            cus.setStartDelay(0);
                            LinearLayout linbutview=(LinearLayout) mainView.findViewById(R.id.numbersbut);
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
                mainView.findViewById(R.id.addphotopanel).setVisibility(View.VISIBLE);
                mainView.findViewById(R.id.pasdigi).setVisibility(View.VISIBLE);


                mainView.findViewById(R.id.scroleditext).setVisibility(View.GONE);
                mainView.findViewById(R.id.commenee).setVisibility(View.GONE);
                mainView.findViewById(R.id.savepanel).setVisibility(View.GONE);

                comment_add.setText(oraliqComment);
                if (!oraliqComment.matches("")) {
                    comment.setText(oraliqComment);
                } else {
                    comment.setText(getString(R.string.add_comment));
                }

                headermain.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, (int) PocketAccounter.convertDpToPixel((getResources().getDimension(R.dimen.hundred_fivety_four) / getResources().getDisplayMetrics().density), contex)));

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
}