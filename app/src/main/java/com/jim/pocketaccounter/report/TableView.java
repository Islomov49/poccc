package com.jim.pocketaccounter.report;

import android.annotation.TargetApi;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jim.pocketaccounter.PocketAccounter;
import com.jim.pocketaccounter.R;
import com.jim.pocketaccounter.helper.PocketAccounterGeneral;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class TableView extends LinearLayout {
    private Bitmap operation;
    private TextView tvFirstTitle, tvSecondTitle, tvThirdTitle, tvFourthTitle;
    private RecyclerView rvTable;
    private boolean isFirstBitmap;
    private ArrayList<? extends Object> datas;
    private LinearLayoutManager lm;

    public TableView(Context context) {
        super(context);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.table_layout, this);
        init();
    }

    public TableView(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.table_layout, this);
        init();
    }

    public TableView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.table_layout, this);
        init();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public TableView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        rvTable = (RecyclerView) findViewById(R.id.rvTable);
        DisplayMetrics dm = getResources().getDisplayMetrics();
        int width = dm.widthPixels;
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(width / 5, (int) getResources().getDimension(R.dimen.thirtyfive_dp));
        lp.setMargins((int) getResources().getDimension(R.dimen.eight_dp), (int) getResources().getDimension(R.dimen.five_dp),
                (int) getResources().getDimension(R.dimen.five_dp), (int) getResources().getDimension(R.dimen.five_dp));
        tvFirstTitle = (TextView) findViewById(R.id.tvFirstTitle);
        tvFirstTitle.setLayoutParams(lp);
        tvSecondTitle = (TextView) findViewById(R.id.tvSecondTitle);
        tvSecondTitle.setLayoutParams(lp);
        tvThirdTitle = (TextView) findViewById(R.id.tvThirdTitle);
        tvThirdTitle.setLayoutParams(lp);
        tvFourthTitle = (TextView) findViewById(R.id.tvFourthTitle);
        tvFourthTitle.setLayoutParams(lp);
        lm = new LinearLayoutManager(getContext());
        rvTable.setLayoutManager(lm);
    }

    public void setTitle(String[] titles, boolean isFirstBitmap) {
        this.isFirstBitmap = isFirstBitmap;
        tvFirstTitle.setText(titles[0]);
        tvSecondTitle.setText(titles[1]);
        tvThirdTitle.setText(titles[2]);
        tvFourthTitle.setText(titles[3]);
    }

    public void setDatas(ArrayList<? extends Object> datas) {
        this.datas = datas;
        MyAdapter adapter = new MyAdapter(datas);
        rvTable.setAdapter(adapter);
    }

    private class MyAdapter extends RecyclerView.Adapter<TableView.ViewHolder> {
        private ArrayList<? extends Object> result;

        public MyAdapter(ArrayList<? extends Object> result) {
            this.result = result;
        }

        public int getItemCount() {


            return result.size();
        }

        public TableView.ViewHolder onCreateViewHolder(ViewGroup parent, int var2) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.table_item, parent, false);
            DisplayMetrics dm = getResources().getDisplayMetrics();
            int width = dm.widthPixels;
            LinearLayout.LayoutParams lp = new LayoutParams(width / 5, ViewGroup.LayoutParams.WRAP_CONTENT);
            lp.setMargins((int) getResources().getDimension(R.dimen.eight_dp), (int) getResources().getDimension(R.dimen.five_dp),
                    (int) getResources().getDimension(R.dimen.five_dp), (int) getResources().getDimension(R.dimen.five_dp));
            lp.gravity = Gravity.CENTER;
            RelativeLayout rlFirstCol = (RelativeLayout) view.findViewById(R.id.rlFirstCol);
            rlFirstCol.setLayoutParams(lp);
            TextView tvTableSecondCol = (TextView) view.findViewById(R.id.tvTableSecondCol);
            tvTableSecondCol.setLayoutParams(lp);
            TextView tvTableThirdCol = (TextView) view.findViewById(R.id.tvTableThirdCol);
            tvTableThirdCol.setLayoutParams(lp);
            TextView tvTableFourthCol = (TextView) view.findViewById(R.id.tvTableFourthCol);
            lp.setMargins((int) getResources().getDimension(R.dimen.eight_dp), (int) getResources().getDimension(R.dimen.five_dp),
                    (int) getResources().getDimension(R.dimen.eight_dp), (int) getResources().getDimension(R.dimen.five_dp));
            tvTableFourthCol.setLayoutParams(lp);
            return new TableView.ViewHolder(view);
        }

        public void setPosition(int position) {
            View view = rvTable.getChildAt(position);
            if (view != null) {
                if (position % 2 == 0)
                    view.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.table_double));
                else
                    view.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.table_odd));
            }
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, final int position) {
            if (position % 2 == 0)
                holder.view.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.table_double));
            else
                holder.view.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.table_odd));
            holder.view.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    unselectAll();
                    v.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.table_selected));
                    if (!isFirstBitmap) {
                        IncomeExpanseDataRow row = (IncomeExpanseDataRow) datas.get(position);
                        if (row.getTotalIncome() == 0 && row.getTotalExpanse() == 0)
                            return;
                        final Dialog dialog = new Dialog(getContext());
                        View dialogView = ((PocketAccounter) getContext()).getLayoutInflater().inflate(R.layout.report_by_income_expanse_info, null);
                        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                        dialog.setContentView(dialogView);
                        TextView tvReportByIncomeExpanseDate = (TextView) dialogView.findViewById(R.id.tvReportByIncomeExpanseDate);
                        SimpleDateFormat format = new SimpleDateFormat("dd LLL, yyyy");
                        tvReportByIncomeExpanseDate.setText(format.format(row.getDate().getTime()));
                        ListView lvReportByIncomeExpanseInfo = (ListView) dialogView.findViewById(R.id.lvReportByIncomeExpanseInfo);
                        ImageView ivReportByCategoryClose = (ImageView) dialogView.findViewById(R.id.ivReportByCategoryClose);
                        ivReportByCategoryClose.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();
                            }
                        });
                        ReportByIncomeExpanseDialogAdapter adapter = new ReportByIncomeExpanseDialogAdapter(getContext(), row.getDetails());
                        lvReportByIncomeExpanseInfo.setAdapter(adapter);
                        TextView tvReportByIncomeExpanseTotalIncome = (TextView) dialogView.findViewById(R.id.tvReportByIncomeExpanseTotalIncome);
                        DecimalFormat decimalFormat = new DecimalFormat("0.00##");
                        tvReportByIncomeExpanseTotalIncome.setText(decimalFormat.format(row.getTotalExpanse()) + PocketAccounter.financeManager.getMainCurrency().getAbbr());
                        TextView tvReportByIncomeExpanseExpanse = (TextView) dialogView.findViewById(R.id.tvReportByIncomeExpanseExpanse);
                        tvReportByIncomeExpanseExpanse.setText(decimalFormat.format(row.getTotalIncome()) + PocketAccounter.financeManager.getMainCurrency().getAbbr());
                        TextView tvReportByIncomeExpanseProfit = (TextView) dialogView.findViewById(R.id.tvReportByIncomeExpanseProfit);
                        tvReportByIncomeExpanseProfit.setText(decimalFormat.format(row.getTotalProfit()) + PocketAccounter.financeManager.getMainCurrency().getAbbr());
                        dialog.show();
                    }
                }
            });
            SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy");
            DecimalFormat decimalFormat = new DecimalFormat("0.00");
            String abbr = PocketAccounter.financeManager.getMainCurrency().getAbbr();
            if (isFirstBitmap) {
                AccountDataRow row = (AccountDataRow) result.get(position);
                holder.tvTableFirstCol.setVisibility(GONE);
                holder.ivTableItem.setVisibility(VISIBLE);
                if (row.getType() == PocketAccounterGeneral.INCOME) {
                    operation = BitmapFactory.decodeResource(getResources(), R.drawable.add_green);
                    operation = Bitmap.createScaledBitmap(operation, (int) getResources().getDimension(R.dimen.thirteen_dp), (int) getResources().getDimension(R.dimen.thirteen_dp), true);
                    holder.ivTableItem.setImageBitmap(operation);
                } else {
                    operation = BitmapFactory.decodeResource(getResources(), R.drawable.remove_red);
                    operation = Bitmap.createScaledBitmap(operation, (int) getResources().getDimension(R.dimen.thirteen_dp), (int) getResources().getDimension(R.dimen.thirteen_dp), true);
                    holder.ivTableItem.setImageBitmap(operation);
                }
                holder.tvTableSecondCol.setText(format.format(row.getDate().getTime()));
                abbr = row.getCurrency().getAbbr();
                holder.tvTableThirdCol.setText(decimalFormat.format(row.getAmount()) + abbr);
                String text = row.getCategory().getName();
                if (row.getSubCategory() != null)
                    text = text + ", " + row.getSubCategory().getName();
                holder.tvTableFourthCol.setText(text);
            } else {
                IncomeExpanseDataRow row = (IncomeExpanseDataRow) result.get(position);
                holder.tvTableFirstCol.setVisibility(VISIBLE);
                holder.ivTableItem.setVisibility(GONE);
                holder.tvTableFirstCol.setText(format.format(row.getDate().getTime()));
                holder.tvTableSecondCol.setText(decimalFormat.format(row.getTotalIncome()) + abbr);
                holder.tvTableThirdCol.setText(decimalFormat.format(row.getTotalExpanse()) + abbr);
                holder.tvTableFourthCol.setText(decimalFormat.format(row.getTotalProfit()) + abbr);
            }
        }
    }

    public void unselectAll() {
        RecyclerView.Adapter adapter = rvTable.getAdapter();
        for (int i = 0; i < adapter.getItemCount(); i++) {
            if (rvTable.getChildAt(i) != null) {
                if (i % 2 == 0)
                    rvTable.getChildAt(i).setBackgroundColor(ContextCompat.getColor(getContext(), R.color.table_double));
                else
                    rvTable.getChildAt(i).setBackgroundColor(ContextCompat.getColor(getContext(), R.color.table_odd));
            }
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView tvTableFirstCol, tvTableSecondCol, tvTableThirdCol, tvTableFourthCol;
        public ImageView ivTableItem;
        public View view;

        public ViewHolder(View view) {
            super(view);
            tvTableFirstCol = (TextView) view.findViewById(R.id.tvTableFirstCol);
            tvTableSecondCol = (TextView) view.findViewById(R.id.tvTableSecondCol);
            tvTableThirdCol = (TextView) view.findViewById(R.id.tvTableThirdCol);
            tvTableFourthCol = (TextView) view.findViewById(R.id.tvTableFourthCol);
            ivTableItem = (ImageView) view.findViewById(R.id.ivTableItem);
            this.view = view;
        }
    }
}
