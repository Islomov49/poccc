package com.jim.pocketaccounter.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.jim.pocketaccounter.PocketAccounter;
import com.jim.pocketaccounter.R;
import com.jim.pocketaccounter.finance.Category;
import com.jim.pocketaccounter.finance.FinanceManager;
import com.jim.pocketaccounter.finance.RootCategory;

import java.util.ArrayList;

public class SettingsWidget extends AppCompatActivity {
    ImageView imageView_button_1;
    ImageView imageView_button_2;
    ImageView imageView_button_3;
    ImageView imageView_button_4;

    ImageView delete_button_1;
    ImageView delete_button_2;
    ImageView delete_button_3;
    ImageView delete_button_4;

    ImageView change_button_1;
    ImageView change_button_2;
    ImageView change_button_3;
    ImageView change_button_4;



    TextView category_name_1;
    TextView category_name_2;
    TextView category_name_3;
    TextView category_name_4;
    boolean is_from_widget=false;
    Spinner diagram_period;
    SharedPreferences sPref;
    SharedPreferences.Editor edit;
    ArrayList<RootCategory> listCategory;
    int mAppWidgetId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings_widget);
        sPref = getSharedPreferences("infoFirst", MODE_PRIVATE);
        edit = sPref.edit();
        //initsialization

        mAppWidgetId= AppWidgetManager.INVALID_APPWIDGET_ID;

        String myAction= getIntent().getAction();
        if (WidgetKeys.ACTION_WIDGET_RECEIVER_CHANGE_DIAGRAM_set.equals(myAction)) {
            is_from_widget=true;
            Bundle extras = getIntent().getExtras();
            if (extras != null) {
                Log.d(WidgetKeys.TAG, "bundle_not_null");
                mAppWidgetId = getIntent().getIntExtra(
                        WidgetKeys.ACTION_WIDGET_RECEIVER_CHANGE_DIAGRAM_ID,
                        AppWidgetManager.INVALID_APPWIDGET_ID);

            }
        }

        imageView_button_1=(ImageView) findViewById(R.id.first_but_icon);
        imageView_button_2=(ImageView) findViewById(R.id.second_but_icon);
        imageView_button_3=(ImageView) findViewById(R.id.thirt_but_icon);
        imageView_button_4=(ImageView) findViewById(R.id.four_but_icon);

        delete_button_1=(ImageView) findViewById(R.id.delete_but1);
        delete_button_2=(ImageView) findViewById(R.id.delete_but2);
        delete_button_3=(ImageView) findViewById(R.id.delete_but3);
        delete_button_4=(ImageView) findViewById(R.id.delete_but4);


        change_button_1=(ImageView) findViewById(R.id.change_ed_but1);
        change_button_2=(ImageView) findViewById(R.id.change_ed_but2);
        change_button_3=(ImageView) findViewById(R.id.change_ed_but3);
        change_button_4=(ImageView) findViewById(R.id.change_ed_but4);


        category_name_1=(TextView) findViewById(R.id.category_name_1);
        category_name_2=(TextView) findViewById(R.id.category_name_2);
        category_name_3=(TextView) findViewById(R.id.category_name_3);
        category_name_4=(TextView) findViewById(R.id.category_name_4);

        diagram_period=(Spinner) findViewById(R.id.perid_diagram);


        ArrayAdapter<String> period_adap = new ArrayAdapter<String>(this,
                R.layout.adapter_spiner, new String[] {
                getString(R.string.mont),getString(R.string.weekk)
        });
        diagram_period.setAdapter(period_adap);
        int a=sPref.getInt(WidgetKeys.SETTINGS_WIDGET_PERIOD_TYPE,0);
        switch (a){
            case 111 :
                diagram_period.setSelection(0);
                break;
            case 100 :
                diagram_period.setSelection(1);
                break;

        }
        diagram_period.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position==0)
                    edit.putInt(WidgetKeys.SETTINGS_WIDGET_PERIOD_TYPE,WidgetKeys.SETTINGS_WIDGET_PERIOD_TYPE_MONTH).commit();
                else if(position==1)
                    edit.putInt(WidgetKeys.SETTINGS_WIDGET_PERIOD_TYPE,WidgetKeys.SETTINGS_WIDGET_PERIOD_TYPE_WEEK).commit();

               if(AppWidgetManager.INVALID_APPWIDGET_ID!=mAppWidgetId)
                WidgetProvider.updateWidget(getApplicationContext(), AppWidgetManager.getInstance(getApplicationContext()),
                        mAppWidgetId);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        RefreshList();



        delete_button_1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edit.putString(WidgetKeys.BUTTON_1_ID,WidgetKeys.BUTTON_DISABLED).commit();
                if(AppWidgetManager.INVALID_APPWIDGET_ID!=mAppWidgetId)
                    WidgetProvider.updateWidget(getApplicationContext(), AppWidgetManager.getInstance(getApplicationContext()),
                            mAppWidgetId);
                RefreshList();
            }
        });
        delete_button_2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edit.putString(WidgetKeys.BUTTON_2_ID,WidgetKeys.BUTTON_DISABLED).commit();;
                if(AppWidgetManager.INVALID_APPWIDGET_ID!=mAppWidgetId)
                    WidgetProvider.updateWidget(getApplicationContext(), AppWidgetManager.getInstance(getApplicationContext()),
                            mAppWidgetId);
                RefreshList();
            }
        });
        delete_button_3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edit.putString(WidgetKeys.BUTTON_3_ID,WidgetKeys.BUTTON_DISABLED).commit();;
                if(AppWidgetManager.INVALID_APPWIDGET_ID!=mAppWidgetId)
                    WidgetProvider.updateWidget(getApplicationContext(), AppWidgetManager.getInstance(getApplicationContext()),
                            mAppWidgetId);
                RefreshList();
            }
        });
        delete_button_4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edit.putString(WidgetKeys.BUTTON_4_ID,WidgetKeys.BUTTON_DISABLED).commit();;
                if(AppWidgetManager.INVALID_APPWIDGET_ID!=mAppWidgetId)
                    WidgetProvider.updateWidget(getApplicationContext(), AppWidgetManager.getInstance(getApplicationContext()),
                            mAppWidgetId);
                RefreshList();
            }
        });

        change_button_1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               Intent chooseint=new Intent(SettingsWidget.this,ChooseWidget.class);
                chooseint.putExtra(WidgetKeys.KEY_FOR_INTENT,WidgetKeys.BUTTON_1_ID);
                chooseint.putExtra(WidgetKeys.INTENT_FOR_BACK_SETTINGS,WidgetKeys.INTENT_GO_BACK);
                startActivityForResult(chooseint,WidgetKeys.REQUET_CODE_INTENT);

            }
        });
        change_button_2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent chooseint=new Intent(SettingsWidget.this,ChooseWidget.class);
                chooseint.putExtra(WidgetKeys.KEY_FOR_INTENT,WidgetKeys.BUTTON_2_ID);
                chooseint.putExtra(WidgetKeys.INTENT_FOR_BACK_SETTINGS,WidgetKeys.INTENT_GO_BACK);
                startActivityForResult(chooseint,WidgetKeys.REQUET_CODE_INTENT);
            }
        });
        change_button_3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent chooseint=new Intent(SettingsWidget.this,ChooseWidget.class);
                chooseint.putExtra(WidgetKeys.KEY_FOR_INTENT,WidgetKeys.BUTTON_3_ID);
                chooseint.putExtra(WidgetKeys.INTENT_FOR_BACK_SETTINGS,WidgetKeys.INTENT_GO_BACK);
                startActivityForResult(chooseint,WidgetKeys.REQUET_CODE_INTENT);;
            }
        });
        change_button_4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent chooseint=new Intent(SettingsWidget.this,ChooseWidget.class);

                chooseint.putExtra(WidgetKeys.KEY_FOR_INTENT,WidgetKeys.BUTTON_4_ID);
                chooseint.putExtra(WidgetKeys.INTENT_FOR_BACK_SETTINGS,WidgetKeys.INTENT_GO_BACK);
                startActivityForResult(chooseint,WidgetKeys.REQUET_CODE_INTENT);
            }
        });

    }
    private void RefreshList(){
        String butID_1,butID_2,butID_3,butID_4;
        FinanceManager financeManager=new FinanceManager(this);
        listCategory=financeManager.getCategories();

        butID_1=sPref.getString(WidgetKeys.BUTTON_1_ID,WidgetKeys.BUTTON_DISABLED);
        butID_2=sPref.getString(WidgetKeys.BUTTON_2_ID,WidgetKeys.BUTTON_DISABLED);
        butID_3=sPref.getString(WidgetKeys.BUTTON_3_ID,WidgetKeys.BUTTON_DISABLED);
        butID_4=sPref.getString(WidgetKeys.BUTTON_4_ID,WidgetKeys.BUTTON_DISABLED);

        for (RootCategory temp:listCategory) {

            if(!butID_1.matches(WidgetKeys.BUTTON_DISABLED)&&temp.getId().matches(butID_1)){
                //ustanovka ikonki
                imageView_button_1.setImageResource(R.drawable.shape_for_widget_black);
                int resId = getResources().getIdentifier(temp.getIcon(), "drawable", getPackageName());
                imageView_button_1.setImageResource(resId);
                category_name_1.setText(temp.getName());
                delete_button_1.setVisibility(View.VISIBLE);
                change_button_1.setVisibility(View.VISIBLE);
                imageView_button_1.setOnClickListener(null);
            }

            if(!butID_2.matches(WidgetKeys.BUTTON_DISABLED)&&temp.getId().matches(butID_2)){
                //ustanovka ikonki
                imageView_button_2.setImageResource(R.drawable.shape_for_widget_black);
                int resId = getResources().getIdentifier(temp.getIcon(), "drawable", getPackageName());
                imageView_button_2.setImageResource(resId);
                category_name_2.setText(temp.getName());
                delete_button_2.setVisibility(View.VISIBLE);
                change_button_2.setVisibility(View.VISIBLE);
                imageView_button_2.setOnClickListener(null);
            }

            if(!butID_3.matches(WidgetKeys.BUTTON_DISABLED)&&temp.getId().matches(butID_3)){
                //ustanovka ikonki
                imageView_button_3.setImageResource(R.drawable.shape_for_widget_black);
                int resId = getResources().getIdentifier(temp.getIcon(), "drawable", getPackageName());
                imageView_button_3.setImageResource(resId);
                category_name_3.setText(temp.getName());
                delete_button_3.setVisibility(View.VISIBLE);
                change_button_3.setVisibility(View.VISIBLE);
                imageView_button_3.setOnClickListener(null);
            }

            if(!butID_4.matches(WidgetKeys.BUTTON_DISABLED)&&temp.getId().matches(butID_4)){
                //ustanovka ikonki
                imageView_button_4.setImageResource(R.drawable.shape_for_widget_black);
                int resId = getResources().getIdentifier(temp.getIcon(), "drawable", getPackageName());
                imageView_button_4.setImageResource(resId);
                category_name_4.setText(temp.getName());
                delete_button_4.setVisibility(View.VISIBLE);
                change_button_4.setVisibility(View.VISIBLE);
                imageView_button_4.setOnClickListener(null);
            }
        }

        if(butID_1.matches(WidgetKeys.BUTTON_DISABLED)){
            imageView_button_1.setImageResource(R.drawable.ic_add_black_24dp);
            category_name_1.setText(R.string.ch_cat);
            delete_button_1.setVisibility(View.GONE);
            change_button_1.setVisibility(View.GONE);
            imageView_button_1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent chooseint=new Intent(SettingsWidget.this,ChooseWidget.class);
                    chooseint.putExtra(WidgetKeys.KEY_FOR_INTENT,WidgetKeys.BUTTON_1_ID);
                    chooseint.putExtra(WidgetKeys.INTENT_FOR_BACK_SETTINGS,WidgetKeys.INTENT_GO_BACK);
                    chooseint.putExtra(WidgetKeys.ACTION_WIDGET_RECEIVER_CHANGE_DIAGRAM_ID, mAppWidgetId);
                    startActivityForResult(chooseint,WidgetKeys.REQUET_CODE_INTENT);
                }
            });
        }
        if(butID_2.matches(WidgetKeys.BUTTON_DISABLED)){
            imageView_button_2.setImageResource(R.drawable.ic_add_black_24dp);
            category_name_2.setText(R.string.ch_cat);
            delete_button_2.setVisibility(View.GONE);
            change_button_2.setVisibility(View.GONE);
            imageView_button_2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent chooseint=new Intent(SettingsWidget.this,ChooseWidget.class);
                    chooseint.putExtra(WidgetKeys.KEY_FOR_INTENT,WidgetKeys.BUTTON_2_ID);
                    chooseint.putExtra(WidgetKeys.INTENT_FOR_BACK_SETTINGS,WidgetKeys.INTENT_GO_BACK);
                    chooseint.putExtra(WidgetKeys.ACTION_WIDGET_RECEIVER_CHANGE_DIAGRAM_ID, mAppWidgetId);
                    startActivityForResult(chooseint,WidgetKeys.REQUET_CODE_INTENT);
                }
            });
        }
        if(butID_3.matches(WidgetKeys.BUTTON_DISABLED)){

            imageView_button_3.setImageResource(R.drawable.ic_add_black_24dp);
            category_name_3.setText(R.string.ch_cat);
            delete_button_3.setVisibility(View.GONE);
            change_button_3.setVisibility(View.GONE);
            imageView_button_3.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent chooseint=new Intent(SettingsWidget.this,ChooseWidget.class);
                    chooseint.putExtra(WidgetKeys.KEY_FOR_INTENT,WidgetKeys.BUTTON_3_ID);
                    chooseint.putExtra(WidgetKeys.INTENT_FOR_BACK_SETTINGS,WidgetKeys.INTENT_GO_BACK);
                    chooseint.putExtra(WidgetKeys.ACTION_WIDGET_RECEIVER_CHANGE_DIAGRAM_ID, mAppWidgetId);
                    startActivityForResult(chooseint,WidgetKeys.REQUET_CODE_INTENT);
                }
            });

        }
        if(butID_4.matches(WidgetKeys.BUTTON_DISABLED)){
            imageView_button_4.setImageResource(R.drawable.ic_add_black_24dp);
            category_name_4.setText(R.string.ch_cat);
            delete_button_4.setVisibility(View.GONE);
            change_button_4.setVisibility(View.GONE);

            imageView_button_4.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent chooseint=new Intent(SettingsWidget.this,ChooseWidget.class);
                    chooseint.putExtra(WidgetKeys.KEY_FOR_INTENT,WidgetKeys.BUTTON_4_ID);
                    chooseint.putExtra(WidgetKeys.INTENT_FOR_BACK_SETTINGS,WidgetKeys.INTENT_GO_BACK);
                    chooseint.putExtra(WidgetKeys.ACTION_WIDGET_RECEIVER_CHANGE_DIAGRAM_ID, mAppWidgetId);
                    startActivityForResult(chooseint,WidgetKeys.REQUET_CODE_INTENT);
                }
            });
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==WidgetKeys.REQUET_CODE_INTENT&& resultCode == RESULT_OK){
            if(AppWidgetManager.INVALID_APPWIDGET_ID!=mAppWidgetId)
                WidgetProvider.updateWidget(getApplicationContext(), AppWidgetManager.getInstance(getApplicationContext()),
                        mAppWidgetId);
            RefreshList();
        }
    }
}
