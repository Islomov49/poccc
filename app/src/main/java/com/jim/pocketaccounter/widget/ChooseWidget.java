package com.jim.pocketaccounter.widget;

import android.app.Dialog;
import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.jim.pocketaccounter.PocketAccounter;
import com.jim.pocketaccounter.R;
import com.jim.pocketaccounter.RootCategoryEditFragment;
import com.jim.pocketaccounter.finance.CategoryAdapter;
import com.jim.pocketaccounter.finance.FinanceManager;
import com.jim.pocketaccounter.finance.RootCategory;
import com.jim.pocketaccounter.helper.FloatingActionButton;
import com.jim.pocketaccounter.helper.PocketAccounterGeneral;
import com.jim.pocketaccounter.helper.ScrollDirectionListener;

import java.util.ArrayList;

public class ChooseWidget extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemClickListener, CompoundButton.OnCheckedChangeListener {
    private SharedPreferences sPref;

    private ListView lvCategories;
    private CheckBox chbCatIncomes, chbCatExpanses;
    private int mode = PocketAccounterGeneral.NORMAL_MODE;
    private boolean[] selected;
    private FinanceManager financeManager;
    private String BUTTON_ID;
    int mAppWidgetId;
    String GOBACK="s";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_widget);
        sPref = getSharedPreferences("infoFirst", MODE_PRIVATE);
        mAppWidgetId= AppWidgetManager.INVALID_APPWIDGET_ID;


            Bundle extras = getIntent().getExtras();
            if (extras != null) {
                Log.d(WidgetKeys.TAG, "bundle_not_null");
                mAppWidgetId = getIntent().getIntExtra(
                        WidgetKeys.ACTION_WIDGET_RECEIVER_CHANGE_DIAGRAM_ID,
                        AppWidgetManager.INVALID_APPWIDGET_ID);
                BUTTON_ID=getIntent().getStringExtra(WidgetKeys.KEY_FOR_INTENT);
            }
        try {
        GOBACK=getIntent().getStringExtra(WidgetKeys.INTENT_FOR_BACK_SETTINGS);
        }
        catch (Exception o){

        }




        financeManager=new FinanceManager(this);
        
        lvCategories = (ListView) findViewById(R.id.lvAccounts);
        lvCategories.setOnItemClickListener(this);

        chbCatIncomes = (CheckBox) findViewById(R.id.chbCatIncomes);
        chbCatIncomes.setOnCheckedChangeListener(this);
        chbCatExpanses = (CheckBox) findViewById(R.id.chbCatExpanses);
        chbCatExpanses.setOnCheckedChangeListener(this);
        setMode(mode);
        refreshList(mode);


    }

    private void refreshList(int mode) {
        ArrayList<RootCategory> categories = new ArrayList<RootCategory>();
        for (int i = 0; i< financeManager.getCategories().size(); i++) {
            if (chbCatIncomes.isChecked()) {
                if (financeManager.getCategories().get(i).getType() == PocketAccounterGeneral.INCOME)
                    categories.add(financeManager.getCategories().get(i));
            }
            if(chbCatExpanses.isChecked()) {
                if (financeManager.getCategories().get(i).getType() == PocketAccounterGeneral.EXPENSE)
                    categories.add(financeManager.getCategories().get(i));
            }
        }
        CategoryAdapter adapter = new CategoryAdapter(this, categories, selected, mode);
        lvCategories.setAdapter(adapter);
    }
    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.ivToolbarMostRight:
                if (mode == PocketAccounterGeneral.NORMAL_MODE) {
                    mode = PocketAccounterGeneral.EDIT_MODE;
                    setMode(mode);
                }
                else {
                    mode = PocketAccounterGeneral.NORMAL_MODE;
                    boolean isAnySelection = false;
                    for (int i=0; i<selected.length; i++) {
                        if (selected[i]) {
                            isAnySelection = true;
                            break;
                        }
                    }
                    if (isAnySelection) {
                        final Dialog dialog=new Dialog(ChooseWidget.this);
                        View dialogView = getLayoutInflater().inflate(R.layout.warning_dialog, null);
                        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                        dialog.setContentView(dialogView);
                        TextView tv = (TextView) dialogView.findViewById(R.id.tvWarningText);
                        tv.setText(R.string.category_delete_warning);
                        Button btnYes = (Button) dialogView.findViewById(R.id.btnWarningYes);
                        btnYes.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                deleteCategories();
                                setMode(mode);
                                dialog.dismiss();
                            }
                        });
                        Button btnNo = (Button) dialogView.findViewById(R.id.btnWarningNo);
                        btnNo.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();
                            }
                        });
                        dialog.show();
                    }
                    else
                        setMode(mode);
                }

                break;
        }
    }
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (mode == PocketAccounterGeneral.NORMAL_MODE) {
            if (chbCatExpanses.isChecked() && chbCatIncomes.isChecked()){

                sPref.edit().putString(BUTTON_ID,financeManager.getCategories().get(position).getId()).apply();
                setResult(RESULT_OK);
                if(AppWidgetManager.INVALID_APPWIDGET_ID!=mAppWidgetId)
                    WidgetProvider.updateWidget(getApplicationContext(), AppWidgetManager.getInstance(getApplicationContext()),
                            mAppWidgetId);
                if(GOBACK!=null){
                    Intent aint=new Intent(ChooseWidget.this,SettingsWidget.class);
                    aint.setAction(WidgetKeys.ACTION_WIDGET_RECEIVER_CHANGE_DIAGRAM_set);
                    aint.putExtra(WidgetKeys.ACTION_WIDGET_RECEIVER_CHANGE_DIAGRAM_ID,
                            mAppWidgetId);
                    startActivity(aint);
                }
                finish();
            }

            else if (chbCatExpanses.isChecked() && !chbCatIncomes.isChecked()) {
                ArrayList<RootCategory> categories = new ArrayList<>();
                for (int i=0; i < financeManager.getCategories().size(); i++) {
                    if (financeManager.getCategories().get(i).getType() == PocketAccounterGeneral.EXPENSE)
                        categories.add(financeManager.getCategories().get(i));
                }
                sPref.edit().putString(BUTTON_ID,categories.get(position).getId()).apply();
                setResult(RESULT_OK);
                if(AppWidgetManager.INVALID_APPWIDGET_ID!=mAppWidgetId)
                    WidgetProvider.updateWidget(getApplicationContext(), AppWidgetManager.getInstance(getApplicationContext()),
                            mAppWidgetId);
                if(GOBACK!=null){
                    Intent aint=new Intent(ChooseWidget.this,SettingsWidget.class);
                    aint.setAction(WidgetKeys.ACTION_WIDGET_RECEIVER_CHANGE_DIAGRAM_set);
                    aint.putExtra(WidgetKeys.ACTION_WIDGET_RECEIVER_CHANGE_DIAGRAM_ID,
                            mAppWidgetId);
                    startActivity(aint);
                }
                finish();

            }
            else if (chbCatIncomes.isChecked() && !chbCatExpanses.isChecked()) {
                ArrayList<RootCategory> categories = new ArrayList<>();
                for (int i=0; i < financeManager.getCategories().size(); i++) {
                    if (financeManager.getCategories().get(i).getType() == PocketAccounterGeneral.INCOME)
                        categories.add(financeManager.getCategories().get(i));
                }
                sPref.edit().putString(BUTTON_ID,categories.get(position).getId()).apply();
                setResult(RESULT_OK);
                if(AppWidgetManager.INVALID_APPWIDGET_ID!=mAppWidgetId)
                    WidgetProvider.updateWidget(getApplicationContext(), AppWidgetManager.getInstance(getApplicationContext()),
                            mAppWidgetId);
                if(GOBACK!=null){
                    Intent aint=new Intent(ChooseWidget.this,SettingsWidget.class);
                    aint.setAction(WidgetKeys.ACTION_WIDGET_RECEIVER_CHANGE_DIAGRAM_set);
                    aint.putExtra(WidgetKeys.ACTION_WIDGET_RECEIVER_CHANGE_DIAGRAM_ID,
                            mAppWidgetId);
                    startActivity(aint);
                }
                finish();

            }
        }
        else {
            CheckBox chbCatListItem = (CheckBox) view.findViewById(R.id.chbAccountListItem);
            chbCatListItem.setChecked(!chbCatListItem.isChecked());
            selected[position] = chbCatListItem.isChecked();
        }
    }
    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        refreshList(mode);
    }
    private void setMode(int mode) {
        if (mode == PocketAccounterGeneral.NORMAL_MODE) {
            selected = null;

        }
        else {
            selected = new boolean[financeManager.getCategories().size()];

        }
        refreshList(mode);
    }
    private void deleteCategories() {
        //delete from all categories
        if (chbCatIncomes.isChecked() && chbCatExpanses.isChecked()) {
            for (int i=0; i<selected.length; i++) {
                if (selected[i]) {
                    String id = financeManager.getCategories().get(i).getId();
                    for (int j=0; j<financeManager.getExpanses().size(); j++) {
                        if (financeManager.getExpanses().get(j) == null)	continue;
                        if (financeManager.getExpanses().get(j).getId().matches(id))
                            financeManager.getExpanses().set(j, null);
                    }
                    for (int j=0; j<financeManager.getIncomes().size(); j++) {
                        if (financeManager.getIncomes().get(j) == null)	continue;
                        if (financeManager.getIncomes().get(j).getId().matches(id))
                            financeManager.getIncomes().set(j, null);
                    }
                    for (int j=0; j<financeManager.getRecords().size(); j++) {
                        if (financeManager.getRecords().get(j).getCategory().getId().matches(id)) {
                            financeManager.getRecords().remove(j);
                            j--;
                        }
                    }
                    financeManager.getCategories().set(i, null);
                }
            }
        } else if (chbCatIncomes.isChecked() && !chbCatExpanses.isChecked()) {
            ArrayList<RootCategory> categories = new ArrayList<>();
            for (int i=0; i < financeManager.getCategories().size(); i++) {
                if (financeManager.getCategories().get(i).getType() == PocketAccounterGeneral.INCOME)
                    categories.add(financeManager.getCategories().get(i));
            }
            for (int i=0; i<selected.length; i++) {
                if (selected[i]) {
                    String id = categories.get(i).getId();
                    for (int j=0; j<financeManager.getIncomes().size(); j++) {
                        if (financeManager.getIncomes().get(j) == null)	continue;
                        if (financeManager.getIncomes().get(j).getId().matches(id))
                            financeManager.getIncomes().set(j, null);
                    }
                    for (int j=0; j<financeManager.getRecords().size(); j++) {
                        if (financeManager.getRecords().get(j).getCategory().getId().matches(id)) {
                            financeManager.getRecords().remove(j);
                            j--;
                        }
                    }
                    for (int j=0; j<financeManager.getCategories().size(); j++) {
                        if (financeManager.getCategories().get(j).getId().matches(id))
                            financeManager.getCategories().set(j, null);
                    }
                }
            }
        } else if (!chbCatIncomes.isChecked() && chbCatExpanses.isChecked()) {
            ArrayList<RootCategory> categories = new ArrayList<>();
            for (int i=0; i < financeManager.getCategories().size(); i++) {
                if (financeManager.getCategories().get(i).getType() == PocketAccounterGeneral.EXPENSE)
                    categories.add(financeManager.getCategories().get(i));
            }
            for (int i=0; i<selected.length; i++) {
                if (selected[i]) {
                    String id = categories.get(i).getId();
                    for (int j=0; j<financeManager.getExpanses().size(); j++) {
                        if (financeManager.getExpanses().get(j) == null)	continue;
                        if (financeManager.getExpanses().get(j).getId().matches(id))
                            financeManager.getExpanses().set(j, null);
                    }
                    for (int j=0; j<financeManager.getRecords().size(); j++) {
                        if (financeManager.getRecords().get(j).getCategory().getId().matches(id)) {
                            financeManager.getRecords().remove(j);
                            j--;
                        }
                    }
                    for (int j=0; j<financeManager.getCategories().size(); j++) {
                        if (financeManager.getCategories().get(j).getId().matches(id))
                            financeManager.getCategories().set(j, null);
                    }
                }
            }
        }
        for (int i = 0; i< financeManager.getCategories().size(); i++) {
            if (financeManager.getCategories().get(i) == null) {
                financeManager.getCategories().remove(i);
                i--;
            }
        }
    }
    @Override
    public void onStop() {
        super.onStop();

    }

    @Override
    public void onBackPressed(){
        try{

            if(GOBACK!=null){
                Intent aint=new Intent(ChooseWidget.this,SettingsWidget.class);
                aint.setAction(WidgetKeys.ACTION_WIDGET_RECEIVER_CHANGE_DIAGRAM_set);
                aint.putExtra(WidgetKeys.ACTION_WIDGET_RECEIVER_CHANGE_DIAGRAM_ID,
                        mAppWidgetId);
                startActivity(aint);
            }
        }
        finally {
            super.onBackPressed();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
