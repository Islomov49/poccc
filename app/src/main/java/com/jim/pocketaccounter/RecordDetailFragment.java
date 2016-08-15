package com.jim.pocketaccounter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.jim.pocketaccounter.finance.FinanceRecord;
import com.jim.pocketaccounter.helper.PocketAccounterGeneral;
import com.jim.pocketaccounter.photocalc.PhotoAdapter;
import com.jim.pocketaccounter.photocalc.PhotoDetails;

import java.io.File;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static com.jim.pocketaccounter.photocalc.PhotoAdapter.REQUEST_DELETE_PHOTOS;

@SuppressLint("ValidFragment")
public class RecordDetailFragment extends Fragment implements OnClickListener {
    private Calendar date;
    private RecyclerView rvRecordDetail;
    private ImageView ivToolbarMostRight, ivToolbarExcel;
    private int mode = PocketAccounterGeneral.NORMAL_MODE;
    private ArrayList<FinanceRecord> records;
    private boolean[] selections;
    Context context;
    public RecordDetailFragment() {
    }

    @SuppressLint("ValidFragment")
    public RecordDetailFragment(Calendar date) {
        this.date = (Calendar) date.clone();
    }
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.record_detail_layout, container, false);
        context=getActivity();
        ivToolbarMostRight = (ImageView) PocketAccounter.toolbar.findViewById(R.id.ivToolbarMostRight);
        ivToolbarMostRight.setVisibility(View.VISIBLE);
        ivToolbarExcel = (ImageView) PocketAccounter.toolbar.findViewById(R.id.ivToolbarExcel);
        ivToolbarExcel.setVisibility(View.GONE);
        ivToolbarMostRight.setOnClickListener(this);
        ((PocketAccounter) getContext()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((PocketAccounter) getContext()).getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_back_button);
        PocketAccounter.toolbar.setTitle(getResources().getString(R.string.records));
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd,LLL yyyy");
        PocketAccounter.toolbar.setSubtitle(dateFormat.format(date.getTime()));
        ( PocketAccounter.toolbar.findViewById(R.id.spToolbar)).setVisibility(View.GONE);
        PocketAccounter.toolbar.setNavigationOnClickListener(new OnClickListener() {
                @Override
            public void onClick(View v) {
                int size = 0;
                size = ((PocketAccounter)getContext()).getSupportFragmentManager().getBackStackEntryCount();
                Log.d("sss", "" + size);
                for (int i = 0; i < size; i++) {
                    ((PocketAccounter)getContext()).getSupportFragmentManager().popBackStack();
                }
                ((PocketAccounter) getContext()).initialize(date);
            }
        });
        rvRecordDetail = (RecyclerView) rootView.findViewById(R.id.rvRecordDetail);
        refreshList();
        setMode(mode);
        return rootView;
    }

    private void refreshList() {
        records = new ArrayList<FinanceRecord>();
        int size = PocketAccounter.financeManager.getRecords().size();
        Calendar begin = (Calendar) date.clone();
        begin.set(Calendar.HOUR_OF_DAY, 0);
        begin.set(Calendar.MINUTE, 0);
        begin.set(Calendar.SECOND, 0);
        begin.set(Calendar.MILLISECOND, 0);
        Calendar end = (Calendar) date.clone();
        end.set(Calendar.HOUR_OF_DAY, 23);
        end.set(Calendar.MINUTE, 59);
        end.set(Calendar.SECOND, 59);
        end.set(Calendar.MILLISECOND, 59);
        for (int i = 0; i < size; i++) {
            if (PocketAccounter.financeManager.getRecords().get(i).getDate().compareTo(begin) >= 0 &&
                    PocketAccounter.financeManager.getRecords().get(i).getDate().compareTo(end) <= 0)
                records.add(PocketAccounter.financeManager.getRecords().get(i));
        }
        RecordDetailAdapter adapter = new RecordDetailAdapter(getContext(), records, mode);
        LinearLayoutManager llm = new LinearLayoutManager(getContext());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        rvRecordDetail.setLayoutManager(llm);
        rvRecordDetail.setAdapter(adapter);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ivToolbarMostRight:
                mode = (mode == PocketAccounterGeneral.NORMAL_MODE ? PocketAccounterGeneral.EDIT_MODE : PocketAccounterGeneral.NORMAL_MODE);
                setMode(mode);
                break;
        }
    }

    private void setMode(int mode) {
        RecordDetailAdapter adapter = (RecordDetailAdapter) rvRecordDetail.getAdapter();
        adapter.setMode(mode);
        if (mode == PocketAccounterGeneral.NORMAL_MODE) {
            ivToolbarMostRight.setImageResource(R.drawable.pencil);
            adapter.removeItems();
        } else {
            ivToolbarMostRight.setImageResource(R.drawable.ic_delete_black);
            adapter.listenChanges();
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, final Intent data) {
        PocketAccounter.openActivity=false;
        Log.d("resulttt", "onActivityResult: Otlab ketib qoldi");
    }


    public class RecordDetailAdapter extends RecyclerView.Adapter<RecordDetailAdapter.DetailViewHolder>{
        List<FinanceRecord> result;
        Context context;
        int mode = PocketAccounterGeneral.NORMAL_MODE;
        public RecordDetailAdapter(Context context, List<FinanceRecord> result, int mode){
            this.context = context;
            this.result = result;
            this.mode = mode;
            selections = new boolean[result.size()];
        }

        @Override
        public void onBindViewHolder(final RecordDetailAdapter.DetailViewHolder holder, final int position) {
            int resId = context.getResources().getIdentifier(result.get(position).getCategory().getIcon(), "drawable", context.getPackageName());
            holder.ivRecordDetail.setImageResource(resId);
            holder.tvRecordDetailCategoryName.setText(result.get(position).getCategory().getName());
            DecimalFormat decimalFormat = new DecimalFormat("0.00");
            String sign = "";

            if (result.get(position).getComment()==null||result.get(position).getComment().matches("")){
                holder.rlVisibleWhenHaveComment.setVisibility(View.GONE);
            }
            else {
                holder.tvRecordComment.setText(result.get(position).getComment());
                holder.rlVisibleWhenHaveComment.setVisibility(View.VISIBLE);
            }

            if(result.get(position).getAllTickets().size()==0){
                holder.rlVisibleWhenHaveTickets.setVisibility(View.GONE);
            }
            else{
                holder.rlVisibleWhenHaveTickets.setVisibility(View.VISIBLE);

            }

            LinearLayoutManager layoutManager
                    = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
            holder.rvTickets.setLayoutManager(layoutManager);
            PhotoAdapter myTickedAdapter =new PhotoAdapter(result.get(position).getAllTickets(),context, new RecordEditFragment.OpenIntentFromAdapter() {
                @Override
                public void startActivityFromFragmentForResult(Intent intent) {
                    PocketAccounter.openActivity=true;
                    startActivity(intent);
                }
            },true);
            holder.rvTickets.hasFixedSize();
            holder.rvTickets.setAdapter(myTickedAdapter);

            if (result.get(position).getCategory().getType() == PocketAccounterGeneral.EXPENSE) {
                holder.tvRecordDetailCategoryAmount.setTextColor(ContextCompat.getColor(context, R.color.red));
                sign = "-";
            }
            else {
                holder.tvRecordDetailCategoryAmount.setTextColor(ContextCompat.getColor(context, R.color.green_just));
                sign = "+";
            }
            holder.tvRecordDetailCategoryAmount.setText(sign + decimalFormat.format(result.get(position).getAmount())+result.get(position).getCurrency().getAbbr());
            boolean subCatIsNull = (result.get(position).getSubCategory() == null);
            if (subCatIsNull) {
               // holder.llSubCategories.setVisibility(View.GONE);
                holder.tvRecordDetailSubCategory.setVisibility(View.GONE);

            }
            else {
                resId = context.getResources().getIdentifier(result.get(position).getSubCategory().getIcon(), "drawable", context.getPackageName());
                holder.tvRecordDetailSubCategory.setVisibility(View.VISIBLE);
                holder.tvRecordDetailSubCategory.setText(result.get(position).getSubCategory().getName());
            }
            if (mode == PocketAccounterGeneral.NORMAL_MODE) {
                holder.chbRecordDetail.setVisibility(View.GONE);
                holder.ivRecordDetail.setVisibility(View.VISIBLE);
            }
            else {
                holder.chbRecordDetail.setVisibility(View.VISIBLE);
                holder.ivRecordDetail.setVisibility(View.GONE);
                holder.chbRecordDetail.setChecked(selections[position]);
                Log.d("sss", "pos"+position);
            }
            final FinanceRecord financeRecord= result.get(position);
            holder.root.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (mode == PocketAccounterGeneral.NORMAL_MODE) {
                        Log.d("sss", "der");
                        ((PocketAccounter) context).replaceFragment(new RecordEditFragment(null, financeRecord.getDate(), financeRecord, PocketAccounterGeneral.DETAIL));
                    }
                    else {
                        holder.chbRecordDetail.setChecked(!holder.chbRecordDetail.isChecked());
                    }


                }
            });
            holder.chbRecordDetail.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    selections[position] = isChecked;
                }
            });
        }

        @Override
        public int getItemCount() {
            return result.size();
        }
        @Override
        public RecordDetailAdapter.DetailViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.record_detail_list_item_modern, parent, false);
            RecordDetailAdapter.DetailViewHolder viewHolder = new RecordDetailAdapter.DetailViewHolder(v);
            return viewHolder;
        }

        public class DetailViewHolder extends RecyclerView.ViewHolder {
            public ImageView ivRecordDetail;
            public TextView tvRecordDetailCategoryName;
            public TextView tvRecordDetailCategoryAmount;
            public TextView tvRecordDetailSubCategory;
            public TextView tvRecordComment;
            public CheckBox chbRecordDetail;
            public RelativeLayout rlVisibleWhenHaveComment;
            public LinearLayout rlVisibleWhenHaveTickets;
            public RecyclerView rvTickets;
            public View root;
            public DetailViewHolder(View view) {
                super(view);
                ivRecordDetail = (ImageView) view.findViewById(R.id.ivRecordDetail);
                tvRecordDetailCategoryName = (TextView) view.findViewById(R.id.tvRecordDetailCategoryName);
                tvRecordComment = (TextView) view.findViewById(R.id.tvComment);
                rvTickets = (RecyclerView) view.findViewById(R.id.rvTickets);
                tvRecordDetailCategoryAmount = (TextView) view.findViewById(R.id.tvRecordDetailCategoryAmount);
                tvRecordDetailSubCategory = (TextView) view.findViewById(R.id.tvRecordDetailSubCategory);
                chbRecordDetail = (CheckBox) view.findViewById(R.id.chbRecordFragmentDetail);
                rlVisibleWhenHaveComment = (RelativeLayout) view.findViewById(R.id.visibleIfCommentHave);
                rlVisibleWhenHaveTickets = (LinearLayout) view.findViewById(R.id.visibleIfTicketHave);
                root = view;
            }
        }
        public void setMode(int mode) {
            this.mode = mode;
        }
        public void removeItems() {
            for (int i = selections.length-1; i >= 0; i--) {
                if (selections[i]) {
                    final ArrayList<PhotoDetails> tempok=result.get(i).getAllTickets();


                    (new Thread(new Runnable() {
                        @Override
                        public void run() {
                            if(tempok!=null)
                            for (PhotoDetails temp:tempok) {
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


                    PocketAccounter.financeManager.getRecords().remove(result.get(i));

                    result.remove(i);
                }
             }
            notifyDataSetChanged();
            selections = new boolean[result.size()];
            for (int i = 0; i < selections.length; i++) {
                selections[i] = false;
            }
        }
        public void listenChanges() {
            for (int i = 0; i < selections.length; i++) {
                notifyItemChanged(i);
            }
        }
    }
}