package com.jim.pocketaccounter;

import android.annotation.SuppressLint;
import android.content.Context;
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
import android.widget.Spinner;
import android.widget.TextView;

import com.jim.pocketaccounter.finance.FinanceRecord;
import com.jim.pocketaccounter.helper.PocketAccounterGeneral;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

@SuppressLint("ValidFragment")
public class RecordDetailFragment extends Fragment implements OnClickListener {
    private Calendar date;
    private RecyclerView rvRecordDetail;
    private ImageView ivToolbarMostRight, ivToolbarExcel;
    private int mode = PocketAccounterGeneral.NORMAL_MODE;
    private ArrayList<FinanceRecord> records;
    private boolean[] selections;
    @SuppressLint("ValidFragment")
    public RecordDetailFragment(Calendar date) {
        this.date = (Calendar) date.clone();
    }
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.record_detail_layout, container, false);
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
        ((Spinner) PocketAccounter.toolbar.findViewById(R.id.spToolbar)).setVisibility(View.GONE);
        PocketAccounter.toolbar.setNavigationOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                ((PocketAccounter)getContext()).getSupportFragmentManager().popBackStack();
                ((PocketAccounter)getContext()).getSupportFragmentManager().popBackStack();
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
                holder.llSubCategories.setVisibility(View.GONE);
                holder.ivRecordDetailBorder.setVisibility(View.GONE);
            }
            else {
                holder.llSubCategories.setVisibility(View.VISIBLE);
                holder.ivRecordDetailBorder.setVisibility(View.VISIBLE);
                resId = context.getResources().getIdentifier(result.get(position).getSubCategory().getIcon(), "drawable", context.getPackageName());
                holder.ivRecordDetailSubCategory.setImageResource(resId);
                holder.tvRecordDetailSubCategory.setText(result.get(position).getSubCategory().getName());
            }
            if (mode == PocketAccounterGeneral.NORMAL_MODE) {
                holder.chbRecordDetail.setVisibility(View.GONE);
            }
            else {
                holder.chbRecordDetail.setVisibility(View.VISIBLE);
                holder.chbRecordDetail.setChecked(selections[position]);
                Log.d("sss", "pos"+position);
            }
            final FinanceRecord financeRecord= result.get(position);
            holder.ivRecordDetailBorder.setVisibility(View.GONE);
            holder.root.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (mode == PocketAccounterGeneral.NORMAL_MODE)
                        ((PocketAccounter)context).replaceFragment(new RecordEditFragment(null, financeRecord.getDate(), financeRecord, PocketAccounterGeneral.DETAIL));
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
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.record_detail_list_item, parent, false);
            RecordDetailAdapter.DetailViewHolder viewHolder = new RecordDetailAdapter.DetailViewHolder(v);
            return viewHolder;
        }

        public class DetailViewHolder extends RecyclerView.ViewHolder {
            public ImageView ivRecordDetail;
            public TextView tvRecordDetailCategoryName;
            public TextView tvRecordDetailCategoryAmount;
            public LinearLayout llSubCategories;
            public ImageView ivRecordDetailBorder;
            public TextView tvRecordDetailSubCategory;
            public ImageView ivRecordDetailSubCategory;
            public CheckBox chbRecordDetail;
            public View root;
            public DetailViewHolder(View view) {
                super(view);
                ivRecordDetail = (ImageView) view.findViewById(R.id.ivRecordDetail);
                tvRecordDetailCategoryName = (TextView) view.findViewById(R.id.tvRecordDetailCategoryName);
                tvRecordDetailCategoryAmount = (TextView) view.findViewById(R.id.tvRecordDetailCategoryAmount);
                llSubCategories = (LinearLayout) view.findViewById(R.id.llSubCategories);
                ivRecordDetailBorder = (ImageView) view.findViewById(R.id.ivRecordDetailBorder);
                tvRecordDetailSubCategory = (TextView) view.findViewById(R.id.tvRecordDetailSubCategory);
                ivRecordDetailSubCategory = (ImageView) view.findViewById(R.id.ivRecordDetailSubCategory);
                chbRecordDetail = (CheckBox) view.findViewById(R.id.chbRecordFragmentDetail);
                root = view;
            }
        }
        public void setMode(int mode) {
            this.mode = mode;
        }
        public void removeItems() {
            for (int i = selections.length-1; i >= 0; i--) {
                if (selections[i]) {
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