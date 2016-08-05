package com.jim.pocketaccounter.credit;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.jim.pocketaccounter.InfoCreditFragmentForArchive;
import com.jim.pocketaccounter.PocketAccounter;
import com.jim.pocketaccounter.R;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by developer on 02.06.2016.
 */

public class AdapterCridetArchive extends RecyclerView.Adapter<AdapterCridetArchive.myViewHolder>{
   List<CreditDetials> cardDetials;

    SimpleDateFormat dateformarter;
    Context context;
    long forDay=1000L*60L*60L*24L;
    long forMoth=1000L*60L*60L*24L*30L;
    long forYear=1000L*60L*60L*24L*365L;
    final static long forWeek=1000L*60L*60L*24L*7L;

    DecimalFormat formater;
    public AdapterCridetArchive( Context This){
        this.cardDetials=PocketAccounter.financeManager.getArchiveCredits();
        this.context=This;
        dateformarter=new SimpleDateFormat("dd.MM.yyyy");
        formater=new DecimalFormat("0.##");


    }
    AdapterCridetArchive.GoCredFragForNotify svyazForNotifyFromArchAdap;
    public void setSvyazToAdapter(AdapterCridetArchive.GoCredFragForNotify goNotify){
        svyazForNotifyFromArchAdap=goNotify;
    }
    public interface ListnerDel{
         void delete_item(int position);
    }
    public interface GoCredFragForNotify{
        void notifyCredFrag();
    }

    @Override
    public void onBindViewHolder(myViewHolder holder, final int position) {
    final CreditDetials itemCr= cardDetials.get(position);
        holder.credit_procent.setText(parseToWithoutNull(itemCr.getProcent())+"%");
        holder.total_value.setText(parseToWithoutNull(itemCr.getValue_of_credit_with_procent())+itemCr.getValyute_currency().getAbbr());

        double total_paid=0;
        for(ReckingCredit item:itemCr.getReckings())
            total_paid+=item.getAmount();

        holder.total_paid.setText(parseToWithoutNull(total_paid)+itemCr.getValyute_currency().getAbbr());
        holder.nameCredit.setText(itemCr.getCredit_name());

        Date AAa = (new Date());
        AAa.setTime(itemCr.getTake_time().getTimeInMillis());
        holder.taken_credit_date.setText(dateformarter.format(AAa));
        holder.iconn.setImageResource(itemCr.getIcon_ID());

        Calendar to= (Calendar) itemCr.getTake_time().clone();
        long period_tip=itemCr.getPeriod_time_tip();
        long period_voqt=itemCr.getPeriod_time();
        int voqt_soni= (int) (period_voqt/period_tip);

        if(period_tip==forDay){
            to.add(Calendar.DAY_OF_YEAR, (int) voqt_soni);
        }
        else if(period_tip==forWeek){
            to.add(Calendar.WEEK_OF_YEAR, (int) voqt_soni);
        }
        else if(period_tip==forMoth){
            to.add(Calendar.MONTH, (int) voqt_soni);

        }
        else {
            to.add(Calendar.YEAR, (int) voqt_soni);

        }

        Date from=new Date();
        int t[]=getDateDifferenceInDDMMYYYY(from,to.getTime());
        if(t[0]*t[1]*t[2]<0&&(t[0]+t[1]+t[2])!=0){
            holder.left_date.setText(R.string.ends);
        }
        else {
            String left_date_string="";
            if(t[0]!=0){
                if(t[0]>1){
                    left_date_string+=Integer.toString(t[0])+" "+context.getString(R.string.years);
                }
                else{
                    left_date_string+=Integer.toString(t[0])+" "+context.getString(R.string.year);
                }

            }
            if(t[1]!=0){
                if(!left_date_string.matches("")){
                    left_date_string+=" ";
                }
                if(t[1]>1){
                    left_date_string+=Integer.toString(t[1])+" "+context.getString(R.string.moths);
                }
                else{
                    left_date_string+=Integer.toString(t[1])+" "+context.getString(R.string.moth);
                }
            }
            if(t[2]!=0){
                if(!left_date_string.matches("")){
                    left_date_string+=" ";
                }
                if(t[2]>1){
                    left_date_string+=Integer.toString(t[2])+" "+context.getString(R.string.days);

                }
                else{
                    left_date_string+=Integer.toString(t[2])+" "+context.getString(R.string.day);
                }
            }
            holder.left_date.setText(left_date_string);
        }

            AAa.setTime(itemCr.getMyCredit_id());
            holder.overall_amount.setText(dateformarter.format(AAa));

        holder.glav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                InfoCreditFragmentForArchive temp=new InfoCreditFragmentForArchive();
                int pos=cardDetials.indexOf(itemCr);
                temp.setConteent(itemCr, pos, new ListnerDel() {
                    @Override
                    public void delete_item(int position) {
                        CreditDetials Az=cardDetials.get(position);
                        if(Az.isKey_for_include()){
                        ArrayList<CreditDetials>  credList=PocketAccounter.financeManager.getCredits();
                            for (CreditDetials creditDetials : credList) {
                                if(creditDetials.getTake_time().getTimeInMillis()==Az.getTake_time().getTimeInMillis()){
                                    credList.remove(creditDetials);
                                    PocketAccounter.financeManager.saveCredits();
                                    svyazForNotifyFromArchAdap.notifyCredFrag();
                                    break;
                                }
                            }
                        }
                        cardDetials.remove(position);
                        PocketAccounter.financeManager.saveArchiveCredits();
                        notifyItemRemoved(position);
                    }
                });
                openFragment(temp,"InfoFragment");
            }
        });

    }
    public static int [] getDateDifferenceInDDMMYYYY(Date from, Date to) {
        Calendar fromDate=Calendar.getInstance();
        Calendar toDate=Calendar.getInstance();
        fromDate.setTime(from);
        toDate.setTime(to);
        int increment = 0;
        int year,month,day;
       if (fromDate.get(Calendar.DAY_OF_MONTH) > toDate.get(Calendar.DAY_OF_MONTH)) {
            increment =fromDate.getActualMaximum(Calendar.DAY_OF_MONTH);
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

    @Override
    public int getItemCount() {
        return cardDetials.size();
    }

    public String parseToWithoutNull(double A){
        if(A==(int)A)
            return Integer.toString((int)A);
        else
            return formater.format(A);

    }
    @Override
    public myViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.moder_titem_arch, parent, false);
        // set the view's size, margins, paddings and layout parameters

        myViewHolder vh = new myViewHolder(v);
        return vh;
    }

    public static class myViewHolder extends RecyclerView.ViewHolder {
        TextView credit_procent;
        TextView total_value;
        TextView total_paid;
        TextView taken_credit_date;
        TextView left_date;
        TextView overall_amount;
        TextView nameCredit;
        View glav;
        ImageView iconn;
        public myViewHolder(View v) {
            super(v);
            credit_procent=(TextView) v.findViewById(R.id.procent_of_credit);
            total_value=(TextView) v.findViewById(R.id.total_value);
            total_paid=(TextView) v.findViewById(R.id.totalpayd);
            taken_credit_date=(TextView) v.findViewById(R.id.date_start);
            left_date=(TextView) v.findViewById(R.id.left_date);
            overall_amount=(TextView) v.findViewById(R.id.overallpay);
            nameCredit=(TextView) v.findViewById(R.id.NameCr);
            iconn=(ImageView) v.findViewById(R.id.iconaaa);
            glav=v;
        }
    }


    public void openFragment(Fragment fragment, String tag) {
        if (fragment != null) {
            final android.support.v4.app.FragmentTransaction ft = ((PocketAccounter)context).getSupportFragmentManager().beginTransaction().addToBackStack(tag).setTransition(android.support.v4.app.FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
            ft.add(R.id.flMain, fragment,tag);
            ft.commit();
        }
    }


}
