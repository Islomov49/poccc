package com.jim.pocketaccounter;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
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
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.jim.pocketaccounter.credit.CreditDetials;
import com.jim.pocketaccounter.credit.ReckingCredit;
import com.jim.pocketaccounter.finance.Account;
import com.jim.pocketaccounter.finance.FinanceManager;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;


public class InfoCreditFragment extends Fragment {
    ImageView expandableBut;
    FrameLayout expandablePanel;
    FrameLayout expandableLiniya;
    FrameLayout ifHaveItem;
    RecyclerView tranact_recyc;
    CreditDetials currentCredit;
    boolean toArcive=false;
    TextView myCreditName;
    TextView myLefAmount;
    TextView myProcent;
    TextView myLefDate;
    TextView myPeriodOfCredit;
    TextView myTakedCredTime;
    TextView myTakedValue;
    TextView myReturnValue;
    TextView myTotalPaid;
    TextView calculeted;
    ImageView icon_credit;
    ConWithFragments A1;
    PaysCreditAdapter adapRecyc;
    ArrayList<ReckingCredit> rcList;
    boolean delete_flag=false;
    int currentPOS=0;
    final static long forDay=1000L*60L*60L*24L;
    final static long forMoth=1000L*60L*60L*24L*30L;
    final static long forWeek=1000L*60L*60L*24L*7L;
    final static long forYear=1000L*60L*60L*24L*365L;
    SimpleDateFormat dateformarter;
    boolean isExpandOpen = false;
    private FinanceManager manager;
    private Context context;
    DecimalFormat formater;
    TextView myPay,myDelete;

    private boolean[] isCheks;

    public InfoCreditFragment() {
        // Required empty public constructor
    }

    public void setConteent(CreditDetials temp, int currentPOS, ConWithFragments A1) {
        currentCredit = temp;
        this.A1 = A1;
        this.currentPOS = currentPOS;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        manager = PocketAccounter.financeManager;
        dateformarter = new SimpleDateFormat("dd.MM.yyyy");
        formater = new DecimalFormat("0.##");

        context = getActivity();
    }

    ImageView ivToolbarMostRight;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View V=inflater.inflate(R.layout.fragment_info_credit, container, false);
        Date dateForSimpleDate = (new Date());
        expandableBut=(ImageView) V.findViewById(R.id.wlyuzik_opener);
        expandablePanel=(FrameLayout) V.findViewById(R.id.shlyuzik);
        expandableLiniya=(FrameLayout) V.findViewById(R.id.with_wlyuzik);
        ifHaveItem=(FrameLayout) V.findViewById(R.id.ifListHave);
        myCreditName=(TextView) V.findViewById(R.id.name_of_credit);
        myLefAmount=(TextView) V.findViewById(R.id.value_credit_all);
        myProcent=(TextView) V.findViewById(R.id.procentCredInfo);
        myLefDate=(TextView) V.findViewById(R.id.leftDateInfo);
        myPeriodOfCredit=(TextView) V.findViewById(R.id.intervalCreditInfo);
        myTakedCredTime=(TextView) V.findViewById(R.id.takedtimeInfo);
        myTakedValue=(TextView) V.findViewById(R.id.takedValueInfo);
        myReturnValue=(TextView) V.findViewById(R.id.totalReturnValueInfo);
        myTotalPaid=(TextView) V.findViewById(R.id.total_transaction);
        calculeted=(TextView) V.findViewById(R.id.it_is_include_balance);
        tranact_recyc=(RecyclerView) V.findViewById(R.id.recycler_for_transactions);
        icon_credit=(ImageView) V.findViewById(R.id.icon_creditt);
        rcList= currentCredit.getReckings();
        adapRecyc=new PaysCreditAdapter(rcList);
        myPay=(TextView)  V.findViewById(R.id.paybut);
        myDelete=(TextView)  V.findViewById(R.id.deleterbut);
        LinearLayoutManager llm = new LinearLayoutManager(context);
        tranact_recyc.setLayoutManager(llm);

        ivToolbarMostRight = (ImageView) PocketAccounter.toolbar.findViewById(R.id.ivToolbarMostRight);
        ivToolbarMostRight.setImageResource(R.drawable.ic_delete_black);
        ivToolbarMostRight.setVisibility(View.VISIBLE);
        ivToolbarMostRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                final AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setMessage(getString(R.string.delete_credit))
                        .setPositiveButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialoge, int id) {
                                dialoge.cancel();
                            }
                        }).setNegativeButton(getString(R.string.delete_anyway), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        A1.delete_item(currentPOS);
                        getActivity().getSupportFragmentManager().popBackStack();

                    }
                });
                builder.create().show();
            }
        });

        isCheks = new boolean[currentCredit.getReckings().size()];

        tranact_recyc.setAdapter(adapRecyc);
        if (rcList.size() == 0) {
            ifHaveItem.setVisibility(View.GONE);
        }else{
            ifHaveItem.setVisibility(View.VISIBLE);

        }
        double total_paid=0;
        for(ReckingCredit item:rcList){
            total_paid+=item.getAmount();
        }

        adapRecyc.notifyDataSetChanged();
        V.findViewById(R.id.frameLayout2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(toArcive&&!delete_flag){

                    A1.to_Archive(currentPOS);
                    ((PocketAccounter)context).getSupportFragmentManager().popBackStack ();
                }
                else if(!delete_flag)
                {
                    openDialog();
                }
                else if(toArcive&&delete_flag){
                    delete_flag=false;
                    myPay.setText(R.string.archive);
                    adapRecyc.notifyDataSetChanged();
                }
                else {
                    delete_flag=false;
                    myPay.setText(R.string.pay);
                    for (int i = 0; i < isCheks.length; i ++) {
                        isCheks[i] = false;
                    }
                    adapRecyc.notifyDataSetChanged();
                }
            }
        });

        myTakedValue.setText(parseToWithoutNull(currentCredit.getValue_of_credit())+currentCredit.getValyute_currency().getAbbr());
        myReturnValue.setText(parseToWithoutNull(currentCredit.getValue_of_credit_with_procent())+currentCredit.getValyute_currency().getAbbr());
        icon_credit.setImageResource(currentCredit.getIcon_ID());
        dateForSimpleDate.setTime(currentCredit.getTake_time().getTimeInMillis());
        myTakedCredTime.setText(dateformarter.format(dateForSimpleDate));
        myCreditName.setText(currentCredit.getCredit_name());
        calculeted.setText((currentCredit.isKey_for_include())?getString(R.string.calculaed):getString(R.string.not_calc));

        myTotalPaid.setText(parseToWithoutNull(total_paid)+currentCredit.getValyute_currency().getAbbr());
        if(currentCredit.getValue_of_credit_with_procent()-total_paid<=0){
            myLefAmount.setText(getString(R.string.repaid));
            toArcive=true;
            myPay.setText(getString(R.string.archive));
        } else
            myLefAmount.setText(parseToWithoutNull(currentCredit.getValue_of_credit_with_procent()-total_paid)+currentCredit.getValyute_currency().getAbbr());

        String suffix="";
        if(currentCredit.getProcent_interval()==forDay){
            suffix=getString(R.string.per_day);
        }
        else if(currentCredit.getProcent_interval()==forWeek){
            suffix=getString(R.string.per_week);
        }
        else if(currentCredit.getProcent_interval()==forMoth){
            suffix=getString(R.string.per_month);
        }
        else {
            suffix=getString(R.string.per_year);
        }

        myProcent.setText(parseToWithoutNull(currentCredit.getProcent())+"%"+" "+suffix);
        V.findViewById(R.id.frameLayout3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(delete_flag){
                    delete_checked_items();

                }
                else{
                    delete_flag=true;
                    adapRecyc.notifyDataSetChanged();
                    myPay.setText(getString(R.string.cancel));
                }
            }
        });

        Calendar to= (Calendar) currentCredit.getTake_time().clone();
        long period_tip=currentCredit.getPeriod_time_tip();
        long period_voqt=currentCredit.getPeriod_time();

        int voqt_soni= (int) (period_voqt/period_tip);

        if(period_tip==forDay){
            suffix=getString(R.string.dayy);
            to.add(Calendar.DAY_OF_YEAR, (int) voqt_soni);
        }
        else if(period_tip==forWeek){
            suffix=getString(R.string.weekk);
            to.add(Calendar.WEEK_OF_YEAR, (int) voqt_soni);
        }
        else if(period_tip==forMoth){
            suffix=getString(R.string.mont);
            to.add(Calendar.MONTH, (int) voqt_soni);

        }
        else {
            suffix=getString(R.string.yearr);
            to.add(Calendar.YEAR, (int) voqt_soni);

        }

        myPeriodOfCredit.setText(Integer.toString(voqt_soni)+" "+suffix);

        V.findViewById(R.id.infoooc).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isExpandOpen){
                    expandablePanel.setVisibility(View.GONE);
                    expandableLiniya.setVisibility(View.GONE);
                    expandableBut.setImageResource(R.drawable.infoo);
                    isExpandOpen = false;
                } else {
                    expandablePanel.setVisibility(View.VISIBLE);
                    expandableLiniya.setVisibility(View.VISIBLE);
                    expandableBut.setImageResource(R.drawable.pasga_ochil);
                    isExpandOpen=true;
                }
            }
        });

        long for_compute_interval=currentCredit.getTake_time().getTimeInMillis()+currentCredit.getPeriod_time()-System.currentTimeMillis();

        Date from=new Date();
        int t[]=getDateDifferenceInDDMMYYYY(from,to.getTime());
        if(t[0]*t[1]*t[2]<0&&(t[0]+t[1]+t[2])!=0){
            myLefDate.setText(R.string.ends);
            myLefDate.setTextColor(Color.parseColor("#832e1c"));
        } else {
            String left_date_string = "";
            if (t[0] != 0) {
                if (t[0] > 1) {
                    left_date_string += Integer.toString(t[0]) + " " + getString(R.string.years);
                } else {
                    left_date_string += Integer.toString(t[0]) + " " + getString(R.string.year);
                }

            }
            if(t[1]!=0){
                if(!left_date_string.matches("")){
                    left_date_string+=" ";
                }
                if(t[1]>1){
                    left_date_string+=Integer.toString(t[1])+" "+getString(R.string.moths);
                }
                else{
                    left_date_string+=Integer.toString(t[1])+" "+getString(R.string.moth);
                }
            }
            if(t[2]!=0){
                if(!left_date_string.matches("")){
                    left_date_string+=" ";
                }
                if(t[2]>1){
                    left_date_string+=Integer.toString(t[2])+" "+getString(R.string.days);

                }
                else{
                    left_date_string+=Integer.toString(t[2])+" "+getString(R.string.day);
                }
            }
            myLefDate.setText(left_date_string);
        }



        V.findViewById(R.id.pustooyy).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });


        return V;
    }

    public String parseToWithoutNull(double A) {
        if (A == (int) A)
            return Integer.toString((int) A);
        else
            return formater.format(A);

    }

    public static int[] getDateDifferenceInDDMMYYYY(Date from, Date to) {
        Calendar fromDate = Calendar.getInstance();
        Calendar toDate = Calendar.getInstance();
        fromDate.setTime(from);
        toDate.setTime(to);
        int increment = 0;
        int year, month, day;
        if (fromDate.get(Calendar.DAY_OF_MONTH) > toDate.get(Calendar.DAY_OF_MONTH)) {
            increment = fromDate.getActualMaximum(Calendar.DAY_OF_MONTH);
        }
        if (increment != 0) {
            day = (toDate.get(Calendar.DAY_OF_MONTH) + increment) - fromDate.get(Calendar.DAY_OF_MONTH);
            increment = 1;
        } else {
            day = toDate.get(Calendar.DAY_OF_MONTH) - fromDate.get(Calendar.DAY_OF_MONTH);
        }

        if ((fromDate.get(Calendar.MONTH) + increment) > toDate.get(Calendar.MONTH)) {
            month = (toDate.get(Calendar.MONTH) + 12) - (fromDate.get(Calendar.MONTH) + increment);
            increment = 1;
        } else {
            month = (toDate.get(Calendar.MONTH)) - (fromDate.get(Calendar.MONTH) + increment);
            increment = 0;
        }

        year = toDate.get(Calendar.YEAR) - (fromDate.get(Calendar.YEAR) + increment);
        return   new int[]{year, month, day};
    }

    public interface ConWithFragments {
        void change_item(CreditDetials changed_item, int position);

        void to_Archive(int position);

        void delete_item(int position);
    }

    ArrayList<Account> accaunt_AC;

    private void openDialog() {
        final Dialog dialog = new Dialog(context);
        View dialogView = ((PocketAccounter) context).getLayoutInflater().inflate(R.layout.add_pay_debt_borrow_info_mod, null);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(dialogView);
        final EditText enterDate = (EditText) dialogView.findViewById(R.id.etInfoDebtBorrowDate);
        final EditText enterPay = (EditText) dialogView.findViewById(R.id.etInfoDebtBorrowPaySumm);
        final EditText comment = (EditText) dialogView.findViewById(R.id.etInfoDebtBorrowPayComment);
        final Spinner accountSp = (Spinner) dialogView.findViewById(R.id.spInfoDebtBorrowAccount);
        accaunt_AC = null;
        if (currentCredit.isKey_for_include()) {
            accaunt_AC = PocketAccounter.financeManager.getAccounts();
            String[] accaounts = new String[accaunt_AC.size()];
            for (int i = 0; i < accaounts.length; i++) {
                accaounts[i] = accaunt_AC.get(i).getName();
            }
            ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(
                    context, R.layout.spiner_gravity_right, accaounts);

            accountSp.setAdapter(arrayAdapter);

        } else {
            dialogView.findViewById(R.id.is_calc).setVisibility(View.GONE);
        }
        final Calendar date = Calendar.getInstance();
        enterDate.setText(dateformarter.format(date.getTime()));
        ImageView cancel = (ImageView) dialogView.findViewById(R.id.ivInfoDebtBorrowCancel);
        ImageView save = (ImageView) dialogView.findViewById(R.id.ivInfoDebtBorrowSave);

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        final DatePickerDialog.OnDateSetListener getDatesetListener = new DatePickerDialog.OnDateSetListener() {
            public void onDateSet(DatePicker arg0, int arg1, int arg2, int arg3) {

                enterDate.setText(dateformarter.format((new GregorianCalendar(arg1, arg2, arg3)).getTime()));
                date.set(arg1, arg2, arg3);
            }
        };
        enterDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();
                Dialog mDialog = new DatePickerDialog(context,
                        getDatesetListener, calendar.get(Calendar.YEAR),
                        calendar.get(Calendar.MONTH), calendar
                        .get(Calendar.DAY_OF_MONTH));
                mDialog.show();
            }
        });
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String amount=enterPay.getText().toString();
                double total_paid=0;
                for(ReckingCredit item:rcList)
                    total_paid+=item.getAmount();
                if(!amount.matches("")){
                    if(Double.parseDouble(amount)>currentCredit.getValue_of_credit_with_procent()-total_paid){

                        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
                        builder.setMessage(context.getString(R.string.payment_balans)+parseToWithoutNull(currentCredit.getValue_of_credit_with_procent()-total_paid)+
                                currentCredit.getValyute_currency().getAbbr()+"."+context.getString(R.string.payment_balance2)+
                                parseToWithoutNull(Double.parseDouble(amount)-(currentCredit.getValue_of_credit_with_procent()-total_paid))+
                                currentCredit.getValyute_currency().getAbbr())
                                .setPositiveButton(context.getString(R.string.imsure), new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialoge, int id) {
                                        ReckingCredit rec=null;
                                        if(!amount.matches("")&&currentCredit.isKey_for_include())
                                            rec=new ReckingCredit(date.getTimeInMillis(),Double.parseDouble(amount),accaunt_AC.get(accountSp.getSelectedItemPosition()).getId(),
                                                    currentCredit.getMyCredit_id(),comment.getText().toString());
                                        else
                                            rec=new ReckingCredit(date.getTimeInMillis(),Double.parseDouble(amount),"",
                                                    currentCredit.getMyCredit_id(),comment.getText().toString());
                                        rcList.add(rec);
                                        currentCredit.setReckings(rcList);

                                        A1.change_item(currentCredit,currentPOS);
                                        updateDate();
                                        isCheks = new boolean[rcList.size()];
                                        for (int i = 0; i < isCheks.length; i++) {
                                            isCheks[i] = false;
                                        }
                                        dialog.dismiss();
                                    }
                                }).setNegativeButton(context.getString(R.string.cancel1), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
                        builder.create().show();

                    } else {
                        ReckingCredit rec = null;
                        if (!amount.matches("") && currentCredit.isKey_for_include())
                            rec = new ReckingCredit(date.getTimeInMillis(), Double.parseDouble(amount), accaunt_AC.get(accountSp.getSelectedItemPosition()).getId(), currentCredit.getMyCredit_id(), comment.getText().toString());
                        else
                            rec = new ReckingCredit(date.getTimeInMillis(), Double.parseDouble(amount), "", currentCredit.getMyCredit_id(), comment.getText().toString());
                        currentCredit.getReckings().add(rec);
                        Log.d("objectTest", "Info Object :" + currentCredit);
                        isCheks = new boolean[rcList.size()];
                        for (int i = 0; i < isCheks.length; i++) {
                            isCheks[i] = false;
                        }
                        A1.change_item(currentCredit, currentPOS);
                        updateDate();
                        dialog.dismiss();
                    }
                }

            }
        });
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        int width = displayMetrics.widthPixels;
        dialog.getWindow().setLayout(7 * width / 8, RelativeLayout.LayoutParams.WRAP_CONTENT);
        dialog.show();
    }
    public void updateDate(){

        double total_paid=0;
        for(ReckingCredit item:rcList)
            total_paid+=item.getAmount();
        if(currentCredit.getValue_of_credit_with_procent()-total_paid<=0){
            myLefAmount.setText(getString(R.string.repaid));
            myPay.setText(getString(R.string.archive));
            toArcive=true;
        }
        else {
            toArcive=false;
            myLefAmount.setText(parseToWithoutNull(currentCredit.getValue_of_credit_with_procent()-total_paid)+currentCredit.getValyute_currency().getAbbr());
        }
        if(rcList.size()==0){
            ifHaveItem.setVisibility(View.GONE);}
        else{
            ifHaveItem.setVisibility(View.VISIBLE);}
        myTotalPaid.setText(parseToWithoutNull(total_paid)+currentCredit.getValyute_currency().getAbbr());
        //TODO update recycler
    }

    public void delete_checked_items() {
        boolean keyfor = false;
        final int lenght = rcList.size() - 1;
        for (boolean isChek : isCheks) {
            if (isChek) {
                keyfor = true;
                break;
            }
        }
        delete_flag = false;
        if (keyfor) {
            final AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setMessage(getString(R.string.accept_delete_reck))
                    .setPositiveButton(getString(R.string.accept), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            for (int t = isCheks.length - 1; t >= 0; t--) {
                                if (isCheks[t]) {
                                    rcList.remove(t);
                                    adapRecyc.notifyItemRemoved(t);
                                } else adapRecyc.notifyItemChanged(t);
                            }
                            isCheks = new boolean[rcList.size()];
                            for (int i = 0; i < isCheks.length; i++) {
                                isCheks[i] = false;
                            }
                        }
                    }).setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    dialog.cancel();
                    adapRecyc.notifyDataSetChanged();
                    updateDate();
                }
            });
            builder.create().show();
        } else {
            adapRecyc.notifyDataSetChanged();
        }
        myPay.setText(getString(R.string.pay));
    }

    @Override
    public void onStop(){
        super.onStop();
    }

    @Override
    public void onDetach(){
        super.onDetach();
        ivToolbarMostRight.setVisibility(View.GONE);
    }

    private class PaysCreditAdapter extends RecyclerView.Adapter<InfoCreditFragment.ViewHolder> {
        private ArrayList<ReckingCredit> list;

        public PaysCreditAdapter(ArrayList<ReckingCredit> list) {
            this.list = list;
        }

        public int getItemCount() {
            return list.size();
        }

        public void onBindViewHolder(final InfoCreditFragment.ViewHolder view, final int position) {
            ReckingCredit item = list.get(position);
            view.infoDate.setText(getString(R.string.date_of_pay) + ": " + dateformarter.format(item.getPayDate()));
            view.infoSumm.setText(parseToWithoutNull(item.getAmount()) + currentCredit.getValyute_currency().getAbbr());
            if (currentCredit.isKey_for_include()) {
                ArrayList<Account> accounts = manager.getAccounts();
                String accs = accounts.get(0).getName();
                for (int i = 0; i < accounts.size(); i++) {
                    if (item.getAccountId().equals(accounts.get(i).getId())) {
                        accs = accounts.get(i).getName();
                    }
                }
                view.infoAccount.setText(getString(R.string.via) + ": " + accs);
            } else {
                view.infoAccount.setVisibility(View.GONE);
            }
            if(!item.getComment().matches(""))
                view.comment.setText(getString(R.string.comment)+": " + item.getComment());
            else
                view.comment.setVisibility(View.GONE);
            if(delete_flag){
                view.forDelete.setVisibility(View.VISIBLE);
                view.forDelete.setChecked(isCheks[position]);
                view.glav.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (view.forDelete.isChecked())
                            view.forDelete.setChecked(false);
                        else view.forDelete.setChecked(true);
                        isCheks[position] = !isCheks[position];
                    }
                });
            } else {
                view.forDelete.setChecked(false);
                view.forDelete.setVisibility(View.GONE);
            }
        }

        public InfoCreditFragment.ViewHolder onCreateViewHolder(ViewGroup parent, int var2) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.payed_item, parent, false);
            return new ViewHolder(view);
        }
    }

    public class ViewHolder extends android.support.v7.widget.RecyclerView.ViewHolder {
        public TextView infoDate;
        public TextView infoSumm;
        public TextView infoAccount;
        public TextView comment;
        public CheckBox forDelete;
        public View glav;

        public ViewHolder(View view) {
            super(view);
            infoDate = (TextView) view.findViewById(R.id.date_of_trans);
            infoAccount = (TextView) view.findViewById(R.id.via_acount);
            comment = (TextView) view.findViewById(R.id.comment_trans);
            infoSumm = (TextView) view.findViewById(R.id.paid_value);
            forDelete = (CheckBox) view.findViewById(R.id.for_delete_check_box);
            glav=view;
        }
    }
}
