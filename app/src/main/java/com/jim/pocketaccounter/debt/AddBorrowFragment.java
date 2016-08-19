package com.jim.pocketaccounter.debt;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.jim.pocketaccounter.PocketAccounter;
import com.jim.pocketaccounter.R;
import com.jim.pocketaccounter.credit.CreditDetials;
import com.jim.pocketaccounter.credit.ReckingCredit;
import com.jim.pocketaccounter.finance.Account;
import com.jim.pocketaccounter.finance.Currency;
import com.jim.pocketaccounter.finance.FinanceManager;
import com.jim.pocketaccounter.finance.FinanceRecord;
import com.jim.pocketaccounter.helper.PocketAccounterGeneral;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.app.Activity.RESULT_OK;

/**
 * Created by user on 6/4/2016.
 */

public class AddBorrowFragment extends Fragment implements AdapterView.OnItemSelectedListener {
    private FrameLayout contactBtn;
    private CircleImageView imageView;
    private EditText PersonName;
    private EditText PersonNumber;
    private EditText PersonDataGet;
    private EditText PersonDataRepeat;
    private EditText PersonSumm;
    private Spinner PersonValyuta;
    private Spinner PersonAccount;
    private String photoPath = "";
    private Calendar getDate;
    private Calendar returnDate;
    private CheckBox calculate;
    private int TYPE = 0;
    private static final int REQUEST_SELECT_CONTACT = 2;
    private FinanceManager manager;
    public static int RESULT_LOAD_IMAGE = 1;
    private ImageView ivToolbarMostRight;
    private EditText firstPay;
    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd.MM.yyyy");
    private final int PERMISSION_REQUEST_CONTACT = 5;
    private int PICK_CONTACT = 10;
    private final int PERMISSION_READ_STORAGE = 6;
    private DebtBorrow currentDebtBorrow;
    private Spinner spNotifMode;
    private ArrayList<String> adapter;
    private FrameLayout btnDetalization;
    private String mode = PocketAccounterGeneral.EVERY_DAY, sequence = "";

    public static Fragment getInstance(int type) {
        AddBorrowFragment fragment = new AddBorrowFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("type", type);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TYPE = getArguments().getInt("type", 0);
        adapter = new ArrayList<>();
    }

    public void shareDetialDebtBorrow(DebtBorrow currentDebtBorrow) {
        this.currentDebtBorrow = currentDebtBorrow;
    }

    public DebtBorrow isEdit() {
        return currentDebtBorrow;
    }

    private DatePickerDialog.OnDateSetListener getDatesetListener = new DatePickerDialog.OnDateSetListener() {
        public void onDateSet(DatePicker arg0, int arg1, int arg2, int arg3) {
            getDate.set(arg1, arg2, arg3);
            if (returnDate != null && getDate.compareTo(returnDate) > 0) {
                returnDate = getDate;
                PersonDataRepeat.setText(simpleDateFormat.format(returnDate.getTime()));
            }
            PersonDataGet.setText(simpleDateFormat.format(getDate.getTime()));
        }
    };
    private DatePickerDialog.OnDateSetListener returnDatesetListener = new DatePickerDialog.OnDateSetListener() {
        public void onDateSet(DatePicker arg0, int arg1, int arg2, int arg3) {
            returnDate = (Calendar) getDate.clone();
            returnDate.set(arg1, arg2, arg3);
            if (returnDate.compareTo(getDate) < 0) {
                returnDate = getDate;
            }
            PersonDataRepeat.setText(simpleDateFormat.format(returnDate.getTime()));
            adapter.clear();
            int countOfDays = (int) Math.ceil((returnDate.getTimeInMillis() - getDate.getTimeInMillis()) / (1000 * 60 * 60 * 24));
            if (countOfDays < 7) {
                adapter.add(getResources().getString(R.string.notif_everyday));
            } else if (countOfDays <= 30) {
                adapter.add(getResources().getString(R.string.notif_everyday));
                adapter.add(getResources().getString(R.string.notif_weekly));
            } else if (countOfDays > 30) {
                adapter.add(getResources().getString(R.string.notif_everyday));
                adapter.add(getResources().getString(R.string.notif_weekly));
                adapter.add(getResources().getString(R.string.notif_monthly));
            }
            ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, adapter);
            spNotifMode.setAdapter(adapter1);
        }
    };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.add_borrow_fragment_layout_mod, container, false);
        spNotifMode = (Spinner) view.findViewById(R.id.spNotifMode);
        btnDetalization = (FrameLayout) view.findViewById(R.id.btnDetalization);
        btnDetalization.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openNotifSettingDialog();
            }
        });
        adapter.add(getResources().getString(R.string.notif_everyday));
        adapter.add(getResources().getString(R.string.notif_weekly));
        adapter.add(getResources().getString(R.string.notif_monthly));
        ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, adapter);
        spNotifMode.setAdapter(adapter1);
        spNotifMode.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        mode = PocketAccounterGeneral.EVERY_DAY;
                        btnDetalization.setVisibility(View.GONE);
                        break;
                    case 1:
                        mode = PocketAccounterGeneral.EVERY_WEEK;
                        btnDetalization.setVisibility(View.VISIBLE);
                        break;
                    case 2:
                        sequence = "1";
                        mode = PocketAccounterGeneral.EVERY_MONTH;
                        btnDetalization.setVisibility(View.VISIBLE);
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        contactBtn = (FrameLayout) view.findViewById(R.id.btBorrowAddPopupContact);
        imageView = (CircleImageView) view.findViewById(R.id.ivBorrowAddPopup);
        PersonName = (EditText) view.findViewById(R.id.etBorrowAddPopupName);
        PersonNumber = (EditText) view.findViewById(R.id.etBorrowAddPopupNumber);
        PersonDataGet = (EditText) view.findViewById(R.id.etBorrowAddPopupDataGet);
        PersonDataRepeat = (EditText) view.findViewById(R.id.etBorrowAddPopupDataRepeat);
        PersonSumm = (EditText) view.findViewById(R.id.etBorrowAddPopupSumm);
        firstPay = (EditText) view.findViewById(R.id.etDebtBorrowFirstPay);
        PersonValyuta = (Spinner) view.findViewById(R.id.spBorrowAddPopupValyuta);
        PersonAccount = (Spinner) view.findViewById(R.id.spBorrowAddPopupAccount);
        calculate = (CheckBox) view.findViewById(R.id.chbAddDebtBorrowCalculate);
        getDate = Calendar.getInstance();
        if (TYPE == DebtBorrow.DEBT) {
            PersonSumm.setHint(getResources().getString(R.string.enter_borrow_amoount));
            ((TextView) view.findViewById(R.id.summ_zayma)).setText(R.string.amount_borrow);
        }
        manager = PocketAccounter.financeManager;

        PersonAccount.setOnItemSelectedListener(this);
        PersonValyuta.setOnItemSelectedListener(this);
        String[] accaounts = new String[manager.getAccounts().size()];
        for (int i = 0; i < accaounts.length; i++) {
            accaounts[i] = manager.getAccounts().get(i).getName();
        }
        String[] valyuts = new String[manager.getCurrencies().size()];
        for (int i = 0; i < valyuts.length; i++) {
            valyuts[i] = manager.getCurrencies().get(i).getAbbr();
        }

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(
                getContext(), R.layout.spiner_gravity_right, accaounts);

        ArrayAdapter<String> arrayValyuAdapter = new ArrayAdapter<>(
                getContext(), R.layout.spiner_gravity_right, valyuts);

        arrayAdapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item);
        PersonAccount.setAdapter(arrayAdapter);

        arrayValyuAdapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item);
        PersonValyuta.setAdapter(arrayValyuAdapter);
        PersonDataGet.setText(simpleDateFormat.format(getDate.getTime()));
        PersonDataGet.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    Calendar calender = Calendar.getInstance();
                    Dialog mDialog = new DatePickerDialog(getContext(),
                            getDatesetListener, calender.get(Calendar.YEAR),
                            calender.get(Calendar.MONTH), calender
                            .get(Calendar.DAY_OF_MONTH));
                    mDialog.show();
                }
                return true;
            }
        });

        PersonDataRepeat.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    Calendar calender = Calendar.getInstance();
                    Dialog mDialog = new DatePickerDialog(getContext(),
                            returnDatesetListener, calender.get(Calendar.YEAR),
                            calender.get(Calendar.MONTH), calender
                            .get(Calendar.DAY_OF_MONTH));
                    mDialog.show();
                    return true;
                }
                return false;
            }
        });

        ivToolbarMostRight = (ImageView) PocketAccounter.toolbar.findViewById(R.id.ivToolbarMostRight);
        ivToolbarMostRight.setImageResource(R.drawable.check_sign);
        ivToolbarMostRight.setVisibility(View.VISIBLE);

        calculate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (calculate.isChecked()) {
                    PersonAccount.setVisibility(View.VISIBLE);
                } else {
                    PersonAccount.setVisibility(View.GONE);
                }
            }
        });

        ivToolbarMostRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                saveAndExit();

            }
        });

        contactBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                askForContactPermission();
            }
        });

        imageView.setImageResource(R.drawable.no_photo);

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int permission = ContextCompat.checkSelfPermission(getContext(),
                        Manifest.permission.WRITE_EXTERNAL_STORAGE);
                if (permission != PackageManager.PERMISSION_GRANTED) {
                    if (ActivityCompat.shouldShowRequestPermissionRationale(((PocketAccounter) getContext()),
                            Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                        builder.setMessage("Permission to access the SD-CARD is required for this app to Download PDF.")
                                .setTitle("Permission required");

                        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                ActivityCompat.requestPermissions((PocketAccounter) getContext(),
                                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                        PERMISSION_READ_STORAGE);
                            }
                        });
                        AlertDialog dialog = builder.create();
                        dialog.show();

                    } else {
                        ActivityCompat.requestPermissions((PocketAccounter) getContext(),
                                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                PERMISSION_READ_STORAGE);
                    }
                } else {
                    getPhoto();
                }
            }
        });

        if (currentDebtBorrow != null) {
            PersonName.setText(currentDebtBorrow.getPerson().getName());
            PersonNumber.setText(currentDebtBorrow.getPerson().getPhoneNumber());
            for (int i = 0; i < valyuts.length; i++) {
                if (valyuts[i].matches(currentDebtBorrow.getCurrency().getAbbr())) {
                    PersonValyuta.setSelection(i);
                    break;
                }
            }
            if (currentDebtBorrow.isCalculate())
                for (int i = 0; i < accaounts.length; i++) {
                    if (accaounts[i].matches(currentDebtBorrow.getAccount().getName())) {
                        PersonAccount.setSelection(i);
                        break;
                    }
                }
            calculate.setChecked(currentDebtBorrow.isCalculate());
            PersonSumm.setText(String.valueOf(currentDebtBorrow.getAmount()));
            PersonDataGet.setText(simpleDateFormat.format(currentDebtBorrow.getTakenDate().getTime()));
            getDate = (Calendar) currentDebtBorrow.getTakenDate().clone();
            if (currentDebtBorrow.getReturnDate() != null) {
                returnDate = (Calendar) currentDebtBorrow.getReturnDate().clone();
                PersonDataRepeat.setText(simpleDateFormat.format(currentDebtBorrow.getReturnDate().getTime()));
            }
            if (!currentDebtBorrow.getPerson().getPhoto().isEmpty()) {
                imageView.setImageBitmap(decodeFile(new File(currentDebtBorrow.getPerson().getPhoto())));
                photoPath = currentDebtBorrow.getPerson().getPhoto();
            }
            if (!currentDebtBorrow.getReckings().isEmpty() && currentDebtBorrow.getReckings().get(0).getPayDate().matches(simpleDateFormat.format(getDate.getTime())))
                firstPay.setText("" + currentDebtBorrow.getReckings().get(0).getAmount());
        }
        return view;
    }

    private void saveAndExit() {
        if (PersonName.getText().toString().equals("")) {
            PersonName.setError(getString(R.string.enter_name_error));
        } else {
            if (PersonSumm.getText().toString().equals("") || Double.parseDouble(PersonSumm.getText().toString()) == 0) {
                PersonSumm.setError(getString(R.string.enter_amount_error));
            } else {
                if (PersonDataGet.getText().toString().matches("")) {
                    PersonDataGet.setError(getString(R.string.enter_takendate_error));
                } else {
                    ArrayList<DebtBorrow> list = manager.getDebtBorrows();
                    Currency currency = manager.getCurrencies().get(PersonValyuta.getSelectedItemPosition());
                    ArrayList<Recking> reckings = new ArrayList<Recking>();
                    Account account = manager.getAccounts().get(PersonAccount.getSelectedItemPosition());
                    File file = null;

                    if (!photoPath.matches("")) {
                        try {
                            Integer.parseInt(photoPath);
                        } catch (Exception e) {
                            Bitmap bitmap = decodeFile(new File(photoPath));
                            Bitmap C;

                            if (bitmap.getWidth() >= bitmap.getHeight()) {
                                C = Bitmap.createBitmap(
                                        bitmap,
                                        bitmap.getWidth() / 2 - bitmap.getHeight() / 2,
                                        0,
                                        bitmap.getHeight(),
                                        bitmap.getHeight()
                                );
                            } else {
                                C = Bitmap.createBitmap(
                                        bitmap,
                                        0,
                                        bitmap.getHeight() / 2 - bitmap.getWidth() / 2,
                                        bitmap.getWidth(),
                                        bitmap.getWidth()
                                );
                            }
                            try {
                                file = new File(getContext().getFilesDir(), Uri.parse(photoPath).getLastPathSegment());
                                FileOutputStream outputStream = new FileOutputStream(file.getAbsoluteFile());
                                C.compress(Bitmap.CompressFormat.JPEG, 30, outputStream);
                                outputStream.flush();
                                outputStream.close();
                            } catch (IOException e1) {
                                e1.printStackTrace();
                            }
                        }
                    }

                    DebtBorrowFragment fragment = new DebtBorrowFragment();
                    if (currentDebtBorrow != null) {
                        if (calculate.isChecked())
                            currentDebtBorrow.setAccount(account);
                        currentDebtBorrow.setPerson(new Person(PersonName.getText().toString(),
                                PersonNumber.getText().toString(), file != null ? file.getAbsolutePath() : photoPath == "" ? "" : photoPath));
                        currentDebtBorrow.setAmount(Double.parseDouble(PersonSumm.getText().toString()));
                        currentDebtBorrow.setCurrency(currency);
                        currentDebtBorrow.setCalculate(calculate.isChecked());
                        currentDebtBorrow.setInfo(mode + ":" + sequence);
                        currentDebtBorrow.setReturnDate(returnDate);
                        currentDebtBorrow.setTakenDate(getDate);
                        if (!isMumkin(currentDebtBorrow)) {
                            return;
                        }
                        if (!firstPay.getText().toString().isEmpty()) {
                            SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
                            if (!currentDebtBorrow.getReckings().isEmpty() && currentDebtBorrow.getReckings().get(0)
                                    .getPayDate().matches(simpleDateFormat.format(currentDebtBorrow.getTakenDate().getTime()))) {
                                currentDebtBorrow.getReckings().get(0).setAmount(Double.parseDouble(firstPay.getText().toString()));
                            } else {
                                currentDebtBorrow.getReckings().add(0, new Recking(
                                        dateFormat.format(Calendar.getInstance().getTime()),
                                        Double.parseDouble(firstPay.getText().toString()), currentDebtBorrow.getId(),
                                        account.getId(), ""));
                            }
                        }

                        Bundle bundle = new Bundle();
                        bundle.putInt("pos", currentDebtBorrow.getType());
                        fragment.setArguments(bundle);
                    } else {
                        final DebtBorrow debtBorrow = new DebtBorrow(new Person(PersonName.getText().toString(),
                                PersonNumber.getText().toString(), file != null ? file.getAbsolutePath() : photoPath == "" ? "" : photoPath),
                                getDate,
                                returnDate,
                                "borrow_" + UUID.randomUUID().toString(),
                                account,
                                currency,
                                Double.parseDouble(PersonSumm.getText().toString()),
                                TYPE, calculate.isChecked()
                        );
                        if (!isMumkin(debtBorrow)) {
                            return;
                        }
                        if (!firstPay.getText().toString().isEmpty()) {
                            SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
                            reckings.add(new Recking(
                                    dateFormat.format(Calendar.getInstance().getTime()),
                                    Double.parseDouble(firstPay.getText().toString()), debtBorrow.getId(),
                                    debtBorrow.getAccount().getId(), ""));
                        }
                        debtBorrow.setInfo(mode + ":" + sequence);
                        Log.d("sss", mode + ":" + sequence);
                        debtBorrow.setReckings(reckings);
                        list.add(0, debtBorrow);
                        Bundle bundle = new Bundle();
                        bundle.putInt("pos", debtBorrow.getType());
                        fragment.setArguments(bundle);
                    }
                    ivToolbarMostRight.setVisibility(View.INVISIBLE);
                    manager.setDebtBorrows(list);
                    manager.saveDebtBorrows();
                    ((PocketAccounter) getContext()).replaceFragment(fragment, PockerTag.DEBTS);
                }
            }
        }
    }

    private boolean isMumkin(DebtBorrow debt) {
        Account account = manager.getAccounts().get(PersonAccount.getSelectedItemPosition());
        if (account.isLimited() && calculate.isChecked()) {
            double limit =  account.getLimitSum();
            double accounted = PocketAccounterGeneral.getCost(debt.getTakenDate(),account.getStartMoneyCurrency(),account.getLimitCurrency(),account.getAmount());
            for (int i = 0; i < PocketAccounter.financeManager.getRecords().size(); i++) {
                FinanceRecord tempac=PocketAccounter.financeManager.getRecords().get(i);
                if (tempac.getAccount().getId().matches(account.getId())) {
                    if (tempac.getCategory().getType() == PocketAccounterGeneral.INCOME)
                        accounted = accounted + PocketAccounterGeneral.getCost(tempac.getDate(),tempac.getCurrency(),account.getLimitCurrency(),tempac.getAmount());
                    else
                        accounted = accounted - PocketAccounterGeneral.getCost(tempac.getDate(),tempac.getCurrency(),account.getLimitCurrency(),tempac.getAmount());
                }
            }
            for (DebtBorrow debtBorrow : PocketAccounter.financeManager.getDebtBorrows()) {
                if (debtBorrow.isCalculate() && (currentDebtBorrow != null && !debtBorrow.getId().matches(currentDebtBorrow.getId()) || currentDebtBorrow == null)) {
                    if (debtBorrow.getAccount().getId().matches(account.getId())) {
                        if (debtBorrow.getType() == DebtBorrow.BORROW) {
                            accounted = accounted - PocketAccounterGeneral.getCost(debtBorrow.getTakenDate(), debtBorrow.getCurrency(),account.getLimitCurrency(), debtBorrow.getAmount());
                        } else {
                            accounted = accounted + PocketAccounterGeneral.getCost(debtBorrow.getTakenDate(), debtBorrow.getCurrency(),account.getLimitCurrency(), debtBorrow.getAmount());
                        }
                        for (Recking recking : debtBorrow.getReckings()) {
                            SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy");
                            Calendar cal = Calendar.getInstance();
                            try {
                                cal.setTime(format.parse(recking.getPayDate()));
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                            if (debtBorrow.getType() == DebtBorrow.DEBT) {
                                accounted = accounted - PocketAccounterGeneral.getCost(cal, debtBorrow.getCurrency(),account.getLimitCurrency(), recking.getAmount());
                            } else {
                                accounted = accounted + PocketAccounterGeneral.getCost(cal, debtBorrow.getCurrency(),account.getLimitCurrency(), recking.getAmount());
                            }
                        }
                    } else {
                        for (Recking recking : debtBorrow.getReckings()) {
                            SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy");
                            Calendar cal = Calendar.getInstance();
                            if (recking.getAccountId().matches(account.getId())) {
                                try {
                                    cal.setTime(format.parse(recking.getPayDate()));
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                                if (debtBorrow.getType() == DebtBorrow.BORROW) {
                                    accounted = accounted + PocketAccounterGeneral.getCost(cal, debtBorrow.getCurrency(),account.getLimitCurrency(), recking.getAmount());
                                } else {
                                    accounted = accounted - PocketAccounterGeneral.getCost(cal, debtBorrow.getCurrency(),account.getLimitCurrency(), recking.getAmount());
                                }
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
                            accounted = accounted - PocketAccounterGeneral.getCost(cal, creditDetials.getValyute_currency(),account.getLimitCurrency(), reckingCredit.getAmount());
                        }
                    }
                }
            }
            if (debt.getType() == DebtBorrow.DEBT) {
                if (currentDebtBorrow != null && !currentDebtBorrow.getReckings().isEmpty() &&
                        simpleDateFormat.format(currentDebtBorrow.getTakenDate().getTime()).matches(currentDebtBorrow.getReckings().get(0).getPayDate())) {
                    accounted = accounted + PocketAccounterGeneral.getCost(Calendar.getInstance(), debt.getCurrency(),account.getLimitCurrency(),
                            Double.parseDouble(PersonSumm.getText().toString()) - (!firstPay.getText().toString().isEmpty() ? Double.parseDouble(firstPay.getText().toString()) : 0));
                    accounted = accounted - currentDebtBorrow.getReckings().get(0).getAmount();
                } else {
                    accounted = accounted + PocketAccounterGeneral.getCost(Calendar.getInstance(), debt.getCurrency(),account.getLimitCurrency(),
                            Double.parseDouble(PersonSumm.getText().toString()) - (!firstPay.getText().toString().isEmpty() ? Double.parseDouble(firstPay.getText().toString()) : 0));
                }
            } else {
                if (currentDebtBorrow != null && !currentDebtBorrow.getReckings().isEmpty() &&
                        simpleDateFormat.format(currentDebtBorrow.getTakenDate().getTime()).matches(currentDebtBorrow.getReckings().get(0).getPayDate())) {
                    accounted = accounted - PocketAccounterGeneral.getCost(Calendar.getInstance(), debt.getCurrency(),account.getLimitCurrency(),
                            Double.parseDouble(PersonSumm.getText().toString()) - (!firstPay.getText().toString().isEmpty() ? Double.parseDouble(firstPay.getText().toString()) : 0));
                    accounted = accounted + currentDebtBorrow.getReckings().get(0).getAmount();
                } else {
                    accounted = accounted - PocketAccounterGeneral.getCost(Calendar.getInstance(), debt.getCurrency(),account.getLimitCurrency(),
                            Double.parseDouble(PersonSumm.getText().toString()) - (!firstPay.getText().toString().isEmpty() ? Double.parseDouble(firstPay.getText().toString()) : 0));
                }
            }

            if (-limit > accounted) {
                Toast.makeText(getContext(), R.string.limit_exceed, Toast.LENGTH_SHORT).show();
                return false;
            }
            return true;
        }
        return true;
    }

    private void openNotifSettingDialog() {
        final Dialog dialog = new Dialog(getActivity());
        final View dialogView = getActivity().getLayoutInflater().inflate(R.layout.notif_settings, null);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(dialogView);
        LinearLayout llNotifSettingBody = (LinearLayout) dialogView.findViewById(R.id.llNotifSettingBody);
        llNotifSettingBody.removeAllViews();
        final Spinner sp = new Spinner(getContext());
        final ArrayList<CheckBox> chbs = new ArrayList<>();
        switch (mode) {
            case PocketAccounterGeneral.EVERY_WEEK:
                String[] weekdays = getResources().getStringArray(R.array.week_days);
                for (int i = 0; i < weekdays.length; i++) {
                    CheckBox chb = new CheckBox(getContext());
                    if (i == 0) chb.setChecked(true);
                    chb.setText(weekdays[i]);
                    chb.setTextSize(getResources().getDimension(R.dimen.five_dp));
                    chb.setTextColor(ContextCompat.getColor(getContext(), R.color.toolbar_text_color));
                    chb.setPadding(0, 0, (int) getResources().getDimension(R.dimen.ten_sp), 0);
                    chbs.add(chb);
                    llNotifSettingBody.addView(chb);
                }
                break;
            case PocketAccounterGeneral.EVERY_MONTH:
                LinearLayout ll = new LinearLayout(getContext());
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                ll.setOrientation(LinearLayout.HORIZONTAL);
                ll.setLayoutParams(lp);

                String[] days = new String[31];
                for (int i = 0; i < 31; i++) {
                    days[i] = Integer.toString(i + 1);
                }
                ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, days);
                sp.setAdapter(adapter1);
                TextView tv = new TextView(getContext());
                tv.setText(getResources().getString(R.string.choose_date) + ": ");
                tv.setTextSize(getResources().getDimension(R.dimen.five_dp));
                tv.setTextColor(ContextCompat.getColor(getContext(), R.color.toolbar_text_color));
                LinearLayout.LayoutParams tvlp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                tvlp.setMargins(0, 0, (int) getResources().getDimension(R.dimen.ten_sp), 0);
                tv.setLayoutParams(tvlp);
                ll.addView(tv);
                ll.addView(sp);
                llNotifSettingBody.addView(ll);
                break;
        }
        ImageView btnYes = (ImageView) dialogView.findViewById(R.id.ivAccountSave);
        btnYes.setOnClickListener(new View.OnClickListener() {
                                      @Override
                                      public void onClick(View v) {
                                          String text = "";
                                          if (mode.matches(PocketAccounterGeneral.EVERY_WEEK)) {
                                              for (int i = 0; i < chbs.size(); i++) {
                                                  if (chbs.get(i).isChecked()) {
                                                      text = text + i + ",";
                                                  }
                                              }
                                          }
                                          if (mode.matches(PocketAccounterGeneral.EVERY_MONTH)) {
                                              text = sp.getSelectedItem() + "";
                                          }
                                          if (!text.matches("") && text.endsWith(","))
                                              sequence = text.substring(0, text.length() - 1);

                                          dialog.dismiss();
                                      }
                                  }
        );
        ImageView btnNo = (ImageView) dialogView.findViewById(R.id.ivAccountClose);
        btnNo.setOnClickListener(new View.OnClickListener()

                                 {
                                     @Override
                                     public void onClick(View v) {
                                         dialog.dismiss();
                                     }
                                 }

        );
        dialog.show();
    }

    private Bitmap decodeFile(File f) {
        try {
//            Decode image size
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(new FileInputStream(f), null, o);
//            The new size we want to scale to
            final int REQUIRED_SIZE = 128;
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PICK_CONTACT && resultCode == RESULT_OK) {
//             Get the URI and query the content provider for the phone number
            Uri contactUri = data.getData();
            String[] projection = new String[]{ContactsContract.CommonDataKinds.Phone.NUMBER,
                    ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                    ContactsContract.CommonDataKinds.Phone.PHOTO_ID
            };
            Cursor cursor = getContext().getContentResolver().query(contactUri, projection,
                    null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                int numberIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
                int nameIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);
                int photoIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.PHOTO_ID);
                String number = cursor.getString(numberIndex);
                String name = cursor.getString(nameIndex);
                photoPath = String.valueOf(cursor.getInt(photoIndex));
                if (queryContactImage(cursor.getInt(photoIndex)) != null)
                    imageView.setImageBitmap(queryContactImage(cursor.getInt(photoIndex)));
                else
                    imageView.setImageResource(R.drawable.no_photo);
                PersonName.setText(name);
                PersonNumber.setText(number);
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
            photoPath = picturePath;
            imageView.setImageBitmap(decodeFile(new File(photoPath)));
        }
    }

    private Bitmap queryContactImage(int imageDataRow) {
        Cursor c = getContext().getContentResolver().query(ContactsContract.Data.CONTENT_URI, new String[]{
                ContactsContract.CommonDataKinds.Photo.PHOTO
        }, ContactsContract.Data._ID + "=?", new String[]{
                Integer.toString(imageDataRow)
        }, null);
        byte[] imageBytes = null;
        if (c != null) {
            if (c.moveToFirst()) {
                imageBytes = c.getBlob(0);
            }
            c.close();
        }
        if (imageBytes != null) {
            return BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
        } else {
            return null;
        }
    }

    public void askForContactPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                        Manifest.permission.READ_CONTACTS)) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setTitle("Contacts access needed");
                    builder.setPositiveButton(android.R.string.ok, null);
                    builder.setMessage("please confirm Contacts access");//TODO put real question
                    builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @TargetApi(Build.VERSION_CODES.M)
                        @Override
                        public void onDismiss(DialogInterface dialog) {
                            requestPermissions(
                                    new String[]
                                            {Manifest.permission.READ_CONTACTS}
                                    , PERMISSION_REQUEST_CONTACT);
                        }
                    });
                    builder.show();
                } else {
                    ActivityCompat.requestPermissions(getActivity(),
                            new String[]{Manifest.permission.READ_CONTACTS},
                            PERMISSION_REQUEST_CONTACT);
                }
            } else {
                getContact();
            }
        } else {
            getContact();
        }
    }

    private void getContact() {
        PocketAccounter.openActivity = true;
        Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
        intent.setType(ContactsContract.Contacts.CONTENT_TYPE);
        intent.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE);
        startActivityForResult(intent, PICK_CONTACT);
    }

    private void getPhoto() {
        PocketAccounter.openActivity = true;
        Intent i = new Intent(
                Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(i, RESULT_LOAD_IMAGE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CONTACT: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getContact();
                } else {
                }
                return;
            }
            case PERMISSION_READ_STORAGE: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getPhoto();
                }
                break;
            }
        }
    }

    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
    }

    public void onNothingSelected(AdapterView<?> parent) {
    }
}