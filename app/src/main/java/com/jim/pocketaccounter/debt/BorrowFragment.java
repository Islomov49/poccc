package com.jim.pocketaccounter.debt;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.jim.pocketaccounter.PocketAccounter;
import com.jim.pocketaccounter.R;
import com.jim.pocketaccounter.finance.Account;
import com.jim.pocketaccounter.finance.FinanceManager;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by user on 6/4/2016.
 */

public class BorrowFragment extends Fragment {
    private RecyclerView recyclerView;
    private LinearLayoutManager mLayoutManager;
    private MyAdapter myAdapter;
    private FinanceManager financeManager;
    private DebtBorrowFragment debtBorrowFragment;
    private int TYPE = 0;

    public static BorrowFragment getInstance(int type) {
        BorrowFragment fragment = new BorrowFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("type", type);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TYPE = getArguments().getInt("type", 0);
        financeManager = PocketAccounter.financeManager;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.borrow_fragment_layout, container, false);
        recyclerView = (RecyclerView) view.findViewById(R.id.lvBorrowFragment);
        myAdapter = new MyAdapter();
        mLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setAdapter(myAdapter);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                try {
                    debtBorrowFragment.onScrolledList(dy > 0);
                } catch (NullPointerException e) {
                }
            }
        });
        return view;
    }

    public void setDebtBorrowFragment(DebtBorrowFragment debtBorrowFragment) {
        this.debtBorrowFragment = debtBorrowFragment;
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    private class MyAdapter extends RecyclerView.Adapter<ViewHolder> {
        private ArrayList<DebtBorrow> persons;
        private ArrayList<DebtBorrow> allPersons;

        public MyAdapter() {
            try {
                allPersons = financeManager.getDebtBorrows();
                persons = new ArrayList<>();
                for (DebtBorrow person : financeManager.getDebtBorrows()) {
                    if (!person.isTo_archive() && person.getType() == TYPE) {
                        persons.add(person);
                    } else {
                        if (person.isTo_archive() && TYPE == 2) {
                            persons.add(person);
                        }
                    }
                }
            } catch (NullPointerException e) {
                recyclerView.setAdapter(myAdapter);
            }
        }

        public int getItemCount() {
            return persons.size();
        }

        public void onBindViewHolder(final ViewHolder view, final int position) {
            final int t = 0;
            final DebtBorrow person = persons.get(Math.abs(t - position));
            if (person.getType() == DebtBorrow.DEBT) {
                view.rl.setBackgroundResource(R.color.grey_light_red);
                view.fl.setBackgroundResource(R.color.grey_light_red);
            }
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd.MM.yyyy");
            view.BorrowPersonName.setText(person.getPerson().getName());
            view.BorrowPersonNumber.setText(person.getPerson().getPhoneNumber());
            view.BorrowPersonDateGet.setText(simpleDateFormat.format(person.getTakenDate().getTime()));
            if (person.getReturnDate() == null) {
                view.BorrowPersonDateRepeat.setText(R.string.no_date_selected);
            } else {
                view.BorrowPersonDateRepeat.setText(simpleDateFormat.format(person.getReturnDate().getTime()));
            }
            double qq = 0;
            if (person.getReckings() != null) {
                for (Recking rk : person.getReckings()) {
                    qq += rk.getAmount();
                }
            }
            String ss = (person.getAmount() - qq) == (int) (person.getAmount() - qq) ? "" + (int) (person.getAmount() - qq) : "" + (person.getAmount() - qq);
            if (person.isTo_archive() || qq >= person.getAmount()) {
                view.BorrowPersonSumm.setText(getResources().getString(R.string.repaid));
            } else
                view.BorrowPersonSumm.setText(ss + person.getCurrency().getAbbr());
            if (person.getPerson().getPhoto().matches("") || person.getPerson().getPhoto().matches("0")) {
                view.BorrowPersonPhotoPath.setImageResource(R.drawable.no_photo);
            } else {
                try {
                    view.BorrowPersonPhotoPath.setImageBitmap(queryContactImage(Integer.parseInt(person.getPerson().getPhoto())));
                } catch (Exception e) {
                    Bitmap bit = BitmapFactory.decodeFile(person.getPerson().getPhoto());
                    view.BorrowPersonPhotoPath.setImageBitmap(bit);
                }
            }
            view.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (((PocketAccounter) getContext()).getSupportFragmentManager().findFragmentById(R.id.flMain).getTag() != null) {
                        Fragment fragment = InfoDebtBorrowFragment.getInstance(persons.get(Math.abs(t - position)).getId(), TYPE);
                        ((PocketAccounter) getContext()).replaceFragment(fragment);
                    }
                }
            });
            if (TYPE == 2) {
                view.pay.setVisibility(View.GONE);
                view.call.setVisibility(View.GONE);
            } else {
                double total = 0;
                for (Recking rec : person.getReckings()) {
                    total += rec.getAmount();
                }
                if (total >= person.getAmount()) {
                    view.pay.setText(getString(R.string.archive));
                } else view.pay.setText(getString(R.string.payy));
            }

            if (person.getPerson().getPhoneNumber().matches("")) {
                view.call.setVisibility(View.INVISIBLE);
            }


            view.call.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(Intent.ACTION_DIAL);
                    intent.setData(Uri.parse("tel:" + person.getPerson().getPhoneNumber()));
                    if (intent.resolveActivity(getContext().getPackageManager()) != null) {
                        startActivity(intent);
                    }
                }
            });

            view.pay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!view.pay.getText().toString().matches(getString(R.string.archive))) {
                        final Dialog dialog = new Dialog(getActivity());
                        View dialogView = getActivity().getLayoutInflater().inflate(R.layout.add_pay_debt_borrow_info_mod, null);
                        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                        dialog.setContentView(dialogView);
                        final EditText enterDate = (EditText) dialogView.findViewById(R.id.etInfoDebtBorrowDate);
                        final EditText enterPay = (EditText) dialogView.findViewById(R.id.etInfoDebtBorrowPaySumm);
                        final EditText comment = (EditText) dialogView.findViewById(R.id.etInfoDebtBorrowPayComment);
                        final Spinner accountSp = (Spinner) dialogView.findViewById(R.id.spInfoDebtBorrowAccount);

                        String[] accaounts = new String[financeManager.getAccounts().size()];
                        for (int i = 0; i < accaounts.length; i++) {
                            accaounts[i] = financeManager.getAccounts().get(i).getName();
                        }

                        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(
                                getContext(), R.layout.spiner_gravity_right, accaounts);

                        accountSp.setAdapter(arrayAdapter);

                        ImageView cancel = (ImageView) dialogView.findViewById(R.id.ivInfoDebtBorrowCancel);
                        ImageView save = (ImageView) dialogView.findViewById(R.id.ivInfoDebtBorrowSave);
                        final Calendar date = Calendar.getInstance();
                        final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd.MM.yyyy");
                        enterDate.setText(simpleDateFormat.format(date.getTime()));
                        cancel.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();
                            }
                        });
                        final DatePickerDialog.OnDateSetListener getDatesetListener = new DatePickerDialog.OnDateSetListener() {
                            public void onDateSet(DatePicker arg0, int arg1, int arg2, int arg3) {
                                date.set(arg1, arg2, arg3);
                                if (date.compareTo(person.getTakenDate()) < 0) {
                                    date.setTime(person.getTakenDate().getTime());
                                    enterDate.setError(getString(R.string.incorrect_date));
                                } else {
                                    enterDate.setError(null);
                                }
                                enterDate.setText(simpleDateFormat.format(date.getTime()));
                            }
                        };
                        enterDate.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Calendar calendar = Calendar.getInstance();
                                Dialog mDialog = new DatePickerDialog(getContext(),
                                        getDatesetListener, calendar.get(Calendar.YEAR),
                                        calendar.get(Calendar.MONTH), calendar
                                        .get(Calendar.DAY_OF_MONTH));
                                mDialog.show();
                            }
                        });
                        save.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                final SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy");
                                String ac = "";
                                for (Account account : financeManager.getAccounts()) {
                                    if (account.getName().matches(accountSp.getSelectedItem().toString())) {
                                        ac = account.getId();
                                        break;
                                    }
                                }
                                if (!enterPay.getText().toString().isEmpty()) {
                                    int len = person.getCurrency().getAbbr().length();
                                    if (Double.parseDouble(view.BorrowPersonSumm.getText().toString().substring(0, view.BorrowPersonSumm.getText().toString().length() - len))
                                            - Double.parseDouble(enterPay.getText().toString()) < 0) {
                                        final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                                        final String finalAc = ac;
                                        builder.setMessage(getResources().getString(R.string.incorrect_pay))
                                                .setPositiveButton(getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
                                                    public void onClick(DialogInterface d, int id) {
                                                        d.dismiss();
                                                        dialog.dismiss();
                                                    }
                                                }).setNegativeButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface d, int id) {
                                                d.cancel();
                                                Recking recking = new Recking(format.format(date.getTime()),
                                                        Double.parseDouble(enterPay.getText().toString()),
                                                        persons.get(position).getId(), finalAc,
                                                        comment.getText().toString());

                                                persons.get(position).getReckings().add(0, recking);
                                                double total = 0;
                                                for (Recking recking1 : persons.get(position).getReckings()) {
                                                    total += recking1.getAmount();
                                                }
                                                if (persons.get(position).getAmount() <= total) {
                                                    view.pay.setText(getString(R.string.archive));
                                                }
                                                view.BorrowPersonSumm.setText(getResources().getString(R.string.repaid));
                                                dialog.dismiss();
                                            }
                                        });
                                        builder.create().show();
                                    } else {
                                        Recking recking = new Recking(format.format(date.getTime()),
                                                Double.parseDouble(enterPay.getText().toString()),
                                                persons.get(position).getId(), ac,
                                                comment.getText().toString());

                                        persons.get(position).getReckings().add(0, recking);
                                        double total = 0;
                                        for (Recking recking1 : persons.get(position).getReckings()) {
                                            total += recking1.getAmount();
                                        }
                                        if (persons.get(position).getAmount() <= total) {
                                            view.pay.setText(getString(R.string.archive));
                                        }
                                        view.BorrowPersonSumm.setText("" + ((persons.get(position).getAmount() - total) ==
                                                ((int) (persons.get(position).getAmount() - total)) ?
                                                ("" + (int) (persons.get(position).getAmount() - total)) :
                                                ("" + (persons.get(position).getAmount() - total))) + person.getCurrency().getAbbr());
                                    }
                                    dialog.dismiss();
                                } else {
                                    enterPay.setError(getString(R.string.enter_pay_value));
                                }
                            }
                        });
                        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
                        int width = displayMetrics.widthPixels;
                        dialog.getWindow().setLayout(7 * width / 8, RelativeLayout.LayoutParams.WRAP_CONTENT);
                        dialog.show();
                    } else {
                        for (int i = 0; i < financeManager.getDebtBorrows().size(); i++) {
                            if (financeManager.getDebtBorrows().get(i).getId().matches(person.getId())) {
                                financeManager.getDebtBorrows().get(i).setTo_archive(true);
                                allPersons.get(i).setTo_archive(true);
                                persons.remove(position);
                                break;
                            }
                        }
                        financeManager.saveDebtBorrows();
                        financeManager.loadDebtBorrows();
                        notifyItemRemoved(position);
                    }
                }
            });
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

        public ViewHolder onCreateViewHolder(ViewGroup parent, int var2) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_borrow_fragment_mod, parent, false);
            return new ViewHolder(view);
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView BorrowPersonName;
        public TextView BorrowPersonNumber;
        public TextView BorrowPersonSumm;
        public TextView BorrowPersonDateGet;
        public TextView BorrowPersonDateRepeat;
        public CircleImageView BorrowPersonPhotoPath;
        public TextView pay;
        public TextView call;
        public RelativeLayout rl;
        public LinearLayout fl;

        public ViewHolder(View view) {
            super(view);
            BorrowPersonName = (TextView) view.findViewById(R.id.tvBorrowPersonName);
            BorrowPersonNumber = (TextView) view.findViewById(R.id.tvBorrowPersonNumber);
            BorrowPersonSumm = (TextView) view.findViewById(R.id.tvBorrowPersonSumm);
            BorrowPersonDateGet = (TextView) view.findViewById(R.id.tvBorrowPersonDateGet);
            BorrowPersonDateRepeat = (TextView) view.findViewById(R.id.tvBorrowPersonDateRepeat);
            BorrowPersonPhotoPath = (CircleImageView) view.findViewById(R.id.imBorrowPerson);
            pay = (TextView) view.findViewById(R.id.btBorrowPersonPay);
            call = (TextView) view.findViewById(R.id.call_person_debt_borrow);
            rl = (RelativeLayout) view.findViewById(R.id.rlDebtBorrowTop);
            fl = (LinearLayout) view.findViewById(R.id.frameLayout);
        }
    }

    public void changeList() {
        MyAdapter adapter = new MyAdapter();
        recyclerView.setAdapter(adapter);
    }
}