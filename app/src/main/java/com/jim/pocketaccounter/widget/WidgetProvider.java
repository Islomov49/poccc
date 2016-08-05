package com.jim.pocketaccounter.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.RectF;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.RemoteViews;

import com.jim.pocketaccounter.PocketAccounter;
import com.jim.pocketaccounter.R;
import com.jim.pocketaccounter.finance.FinanceManager;
import com.jim.pocketaccounter.finance.FinanceRecord;
import com.jim.pocketaccounter.finance.RootCategory;
import com.jim.pocketaccounter.helper.PocketAccounterGeneral;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by DEV on 09.07.2016.
 */

public class WidgetProvider extends AppWidgetProvider {

    @Override
    public void onEnabled(Context context) {
        super.onEnabled(context);
        SharedPreferences sPref;
        sPref = context.getSharedPreferences("infoFirst", MODE_PRIVATE);

    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        super.onDeleted(context, appWidgetIds);
        SharedPreferences sPref;
        sPref = context.getSharedPreferences("infoFirst", MODE_PRIVATE);
        sPref.edit().putInt(WidgetKeys.SPREF_WIDGET_ID,-1).apply();
    }

    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {


        super.onUpdate(context, appWidgetManager, appWidgetIds);
        for (int i : appWidgetIds) {
            updateWidget(context, appWidgetManager, i);
        }


    }


    static public void updateWidget(Context context, AppWidgetManager appWidgetManager,
                             int widgetID) {
        SharedPreferences sPref;
        ArrayList<RootCategory> listCategory;
        String butID_1, butID_2, butID_3, butID_4;
        sPref = context.getSharedPreferences("infoFirst", MODE_PRIVATE);
        FinanceManager financeManager = new FinanceManager(context);
        listCategory = financeManager.getCategories();
        sPref.edit().putInt(WidgetKeys.SPREF_WIDGET_ID,widgetID).apply();

        butID_1 = sPref.getString(WidgetKeys.BUTTON_1_ID, WidgetKeys.BUTTON_DISABLED);
        butID_2 = sPref.getString(WidgetKeys.BUTTON_2_ID, WidgetKeys.BUTTON_DISABLED);
        butID_3 = sPref.getString(WidgetKeys.BUTTON_3_ID, WidgetKeys.BUTTON_DISABLED);
        butID_4 = sPref.getString(WidgetKeys.BUTTON_4_ID, WidgetKeys.BUTTON_DISABLED);


        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.app_widget);
        Log.d(WidgetKeys.TAG, "UPDATE");


        // knopka instalizatsiya bloki
        if (!butID_1.matches(WidgetKeys.BUTTON_DISABLED) || !butID_2.matches(WidgetKeys.BUTTON_DISABLED)
                || !butID_3.matches(WidgetKeys.BUTTON_DISABLED) || !butID_4.matches(WidgetKeys.BUTTON_DISABLED))
            for (RootCategory temp : listCategory) {

                if (!butID_1.matches(WidgetKeys.BUTTON_DISABLED) && temp.getId().matches(butID_1)) {
                    //ustanovka ikonki
                    views.setImageViewResource(R.id.button_1_ramka, R.drawable.shape_for_widget_black);
                    int resId = context.getResources().getIdentifier(temp.getIcon(), "drawable", context.getPackageName());
                    views.setImageViewResource(R.id.button_1_icon, resId);
                    //ustanovka onclika
                    Intent button1 = new Intent(context, CalcActivity.class);
                    button1.putExtra(WidgetKeys.KEY_FOR_INTENT_ID, temp.getId());
                    button1.putExtra(WidgetKeys.ACTION_WIDGET_RECEIVER_CHANGE_DIAGRAM_ID, widgetID);
                    PendingIntent.getActivity(context, 1, button1, 0).cancel();
                    PendingIntent pendingIntent_button1 = PendingIntent.getActivity(context, 1, button1, 0);
                    views.setOnClickPendingIntent(R.id.button_1, pendingIntent_button1);

                }

                if (!butID_2.matches(WidgetKeys.BUTTON_DISABLED) && temp.getId().matches(butID_2)) {
                    //ustanovka ikonki
                    views.setImageViewResource(R.id.button_2_ramka, R.drawable.shape_for_widget_black);
                    int resId = context.getResources().getIdentifier(temp.getIcon(), "drawable", context.getPackageName());
                    views.setImageViewResource(R.id.button_2_icon, resId);
                    //ustanovka onclika
                    Intent button2 = new Intent(context, CalcActivity.class);
                    button2.putExtra(WidgetKeys.KEY_FOR_INTENT_ID, temp.getId());
                    button2.putExtra(WidgetKeys.ACTION_WIDGET_RECEIVER_CHANGE_DIAGRAM_ID, widgetID);
                    PendingIntent.getActivity(context, 2, button2, 0).cancel();
                    PendingIntent pendingIntent_button2 = PendingIntent.getActivity(context, 2, button2, 0);
                    views.setOnClickPendingIntent(R.id.button_2, pendingIntent_button2);


                }

                if (!butID_3.matches(WidgetKeys.BUTTON_DISABLED) && temp.getId().matches(butID_3)) {
                    //ustanovka ikonki
                    views.setImageViewResource(R.id.button_3_ramka, R.drawable.shape_for_widget_black);
                    int resId = context.getResources().getIdentifier(temp.getIcon(), "drawable", context.getPackageName());
                    views.setImageViewResource(R.id.button_3_icon, resId);
                    //ustanovka onclika
                    Intent button3 = new Intent(context, CalcActivity.class);
                    button3.putExtra(WidgetKeys.KEY_FOR_INTENT_ID, temp.getId());
                    button3.putExtra(WidgetKeys.ACTION_WIDGET_RECEIVER_CHANGE_DIAGRAM_ID, widgetID);
                    PendingIntent.getActivity(context, 3, button3, 0).cancel();
                    PendingIntent pendingIntent_button3 = PendingIntent.getActivity(context, 3, button3, 0);
                    views.setOnClickPendingIntent(R.id.button_3, pendingIntent_button3);

                }

                if (!butID_4.matches(WidgetKeys.BUTTON_DISABLED) && temp.getId().matches(butID_4)) {
                    //ustanovka ikonki
                    views.setImageViewResource(R.id.button_4_ramka, R.drawable.shape_for_widget_black);
                    int resId = context.getResources().getIdentifier(temp.getIcon(), "drawable", context.getPackageName());
                    views.setImageViewResource(R.id.button_4_icon, resId);
                    //ustanovka onclika
                    Intent button4 = new Intent(context, CalcActivity.class);
                    button4.putExtra(WidgetKeys.KEY_FOR_INTENT_ID, temp.getId());
                    button4.putExtra(WidgetKeys.ACTION_WIDGET_RECEIVER_CHANGE_DIAGRAM_ID, widgetID);
                    PendingIntent.getActivity(context, 4, button4, 0).cancel();
                    PendingIntent pendingIntent_button4 = PendingIntent.getActivity(context, 4, button4, 0);
                    views.setOnClickPendingIntent(R.id.button_4, pendingIntent_button4);

                }
            }
        if (butID_1.matches(WidgetKeys.BUTTON_DISABLED)) {
            views.setImageViewResource(R.id.button_1_ramka, R.drawable.shape_for_widget);
            views.setImageViewResource(R.id.button_1_icon, R.drawable.ic_add_widget);

            Intent button4 = new Intent(context, ChooseWidget.class);
            button4.putExtra(WidgetKeys.KEY_FOR_INTENT, WidgetKeys.BUTTON_1_ID);
            button4.putExtra(WidgetKeys.ACTION_WIDGET_RECEIVER_CHANGE_DIAGRAM_ID, widgetID);
            PendingIntent.getActivity(context, 1, button4, 0).cancel();
            PendingIntent pendingIntent_button4 = PendingIntent.getActivity(context, 1, button4, 0);
            views.setOnClickPendingIntent(R.id.button_1, pendingIntent_button4);

        }
        if (butID_2.matches(WidgetKeys.BUTTON_DISABLED)) {
            views.setImageViewResource(R.id.button_2_ramka, R.drawable.shape_for_widget);
            views.setImageViewResource(R.id.button_2_icon, R.drawable.ic_add_widget);
            Intent button4 = new Intent(context, ChooseWidget.class);
            button4.putExtra(WidgetKeys.KEY_FOR_INTENT, WidgetKeys.BUTTON_2_ID);
            button4.putExtra(WidgetKeys.ACTION_WIDGET_RECEIVER_CHANGE_DIAGRAM_ID, widgetID);
            PendingIntent.getActivity(context, 2, button4, 0).cancel();
            PendingIntent pendingIntent_button4 = PendingIntent.getActivity(context, 2, button4, 0);
            views.setOnClickPendingIntent(R.id.button_2, pendingIntent_button4);
        }
        if (butID_3.matches(WidgetKeys.BUTTON_DISABLED)) {
            views.setImageViewResource(R.id.button_3_ramka, R.drawable.shape_for_widget);
            views.setImageViewResource(R.id.button_3_icon, R.drawable.ic_add_widget);
            Intent button4 = new Intent(context, ChooseWidget.class);
            button4.putExtra(WidgetKeys.KEY_FOR_INTENT, WidgetKeys.BUTTON_3_ID);
            button4.putExtra(WidgetKeys.ACTION_WIDGET_RECEIVER_CHANGE_DIAGRAM_ID, widgetID);
            PendingIntent.getActivity(context, 3, button4, 0).cancel();
            PendingIntent pendingIntent_button4 = PendingIntent.getActivity(context, 3, button4, 0);
            views.setOnClickPendingIntent(R.id.button_3, pendingIntent_button4);
        }
        if (butID_4.matches(WidgetKeys.BUTTON_DISABLED)) {
            views.setImageViewResource(R.id.button_4_ramka, R.drawable.shape_for_widget);
            views.setImageViewResource(R.id.button_4_icon, R.drawable.ic_add_widget);
            Intent button4 = new Intent(context, ChooseWidget.class);
            button4.putExtra(WidgetKeys.KEY_FOR_INTENT, WidgetKeys.BUTTON_4_ID);
            button4.putExtra(WidgetKeys.ACTION_WIDGET_RECEIVER_CHANGE_DIAGRAM_ID, widgetID);
            PendingIntent.getActivity(context, 4, button4, 0).cancel();
            PendingIntent pendingIntent_button4 = PendingIntent.getActivity(context, 4, button4, 0);
            views.setOnClickPendingIntent(R.id.button_4, pendingIntent_button4);
        }

            //income diagramma
        Bitmap bitmap = makeDiagram(context);
        views.setImageViewBitmap(R.id.diagramma_widget, bitmap);

        Intent active = new Intent(context, WidgetProvider.class);
        active.setAction(WidgetKeys.ACTION_WIDGET_RECEIVER_CHANGE_DIAGRAM);
        active.putExtra(WidgetKeys.ACTION_WIDGET_RECEIVER_CHANGE_DIAGRAM_ID,
                widgetID);
        Log.d(WidgetKeys.TAG, widgetID + "");
        PendingIntent actionPendingIntent = PendingIntent.getBroadcast(context, widgetID
                , active, 0);
        views.setOnClickPendingIntent(R.id.diagramma_widget, actionPendingIntent);


        Intent active_s = new Intent(context, SettingsWidget.class);
        active_s.setAction(WidgetKeys.ACTION_WIDGET_RECEIVER_CHANGE_DIAGRAM_set);
        active_s.putExtra(WidgetKeys.ACTION_WIDGET_RECEIVER_CHANGE_DIAGRAM_ID,
                widgetID);
        PendingIntent actionPendingIntent_s = PendingIntent.getActivity(context, widgetID
                , active_s, 0);
        views.setOnClickPendingIntent(R.id.settings_widget, actionPendingIntent_s);
        DecimalFormat format = new DecimalFormat("0.##");
        FinanceManager manager = new FinanceManager(context);
        double income = 0.0, expense = 0.0, balance = 0.0;
        for (int i=0; i<manager.getRecords().size(); i++) {
            if (manager.getRecords().get(i).getCategory().getType() == PocketAccounterGeneral.INCOME) {
                income += PocketAccounterGeneral.getCost(manager.getRecords().get(i));
            }
            else
                expense += PocketAccounterGeneral.getCost(manager.getRecords().get(i));
        }
        balance = income - expense;
        String abbr = manager.getMainCurrency().getAbbr();
        //balanceni berisiz
        views.setTextViewText(R.id.balance_widget, format.format(balance)+abbr);
        //rasxodni berisiz
        views.setTextViewText(R.id.expence_widget, format.format(expense)+abbr);

        //doxoddi berisiz
        views.setTextViewText(R.id.income_widget, format.format(income)+abbr);
        appWidgetManager.updateAppWidget(widgetID, views);

    }

    static Bitmap makeDiagram(Context context) {
        FinanceManager manager = new FinanceManager(context);
        Calendar begin = Calendar.getInstance();
        begin.set(Calendar.HOUR_OF_DAY, 0);
        begin.set(Calendar.MINUTE, 0);
        begin.set(Calendar.SECOND, 0);
        begin.set(Calendar.MILLISECOND, 0);
        Calendar end = Calendar.getInstance();
        end.set(Calendar.HOUR_OF_DAY, 23);
        end.set(Calendar.MINUTE, 59);
        end.set(Calendar.SECOND, 59);
        end.set(Calendar.MILLISECOND, 59);
        SharedPreferences prefs = context.getSharedPreferences("infoFirst", MODE_PRIVATE);
        int type = prefs.getInt(WidgetKeys.SETTINGS_WIDGET_PERIOD_TYPE, WidgetKeys.SETTINGS_WIDGET_PERIOD_TYPE_MONTH);
        int h = (int) context.getResources().getDimension(R.dimen.seventy_dp), w = (int)(4.5 * h);
        int distance = 0;
        int countOfDays = 0;
        if (type == WidgetKeys.SETTINGS_WIDGET_PERIOD_TYPE_MONTH)
            begin.add(Calendar.MONTH, -1);
        else
            begin.add(Calendar.DAY_OF_MONTH, -6);
        countOfDays = countOfDays(begin, end);
        distance = (int)(w/(countOfDays-0.8));
        ArrayList<FinanceRecord> tempInc = new ArrayList<>();
        ArrayList<FinanceRecord> tempExp = new ArrayList<>();
        for (int i=0; i<manager.getRecords().size(); i++) {
            if (begin.compareTo(manager.getRecords().get(i).getDate()) <= 0 &&
                    end.compareTo(manager.getRecords().get(i).getDate()) >= 0) {
                if (manager.getRecords().get(i).getCategory().getType() == PocketAccounterGeneral.INCOME)
                    tempInc.add(manager.getRecords().get(i));
                else
                    tempExp.add(manager.getRecords().get(i));
            }
        }
        ArrayList<Double> incomes = new ArrayList<>();
        ArrayList<Double> expenses = new ArrayList<>();
        while(begin.compareTo(end) <= 0) {
            Calendar first = (Calendar)begin.clone();
            Calendar second = (Calendar)begin.clone();
            second.set(Calendar.HOUR_OF_DAY, 23);
            second.set(Calendar.MINUTE, 59);
            second.set(Calendar.SECOND, 59);
            second.set(Calendar.MILLISECOND, 59);
            double incAmount = 0.0, expAmount = 0.0;
            for (int i=0; i<tempInc.size(); i++) {
                if (first.compareTo(tempInc.get(i).getDate()) <= 0 &&
                        second.compareTo(tempInc.get(i).getDate()) >= 0) {
                    incAmount = incAmount + PocketAccounterGeneral.getCost(tempInc.get(i));
                }
            }
            for (int i=0; i<tempExp.size(); i++) {
                if (first.compareTo(tempExp.get(i).getDate()) <= 0 &&
                        second.compareTo(tempExp.get(i).getDate()) >= 0) {
                    expAmount = expAmount + PocketAccounterGeneral.getCost(tempExp.get(i));
                }
            }
            incomes.add(incAmount);
            expenses.add(expAmount);
            begin.add(Calendar.DAY_OF_YEAR, 1);
        }
        double max = 0.0;
        for (int i=0; i<incomes.size(); i++) {
            if (incomes.get(i) >= max)
                max = incomes.get(i);
        }
        for (int i=0; i<expenses.size(); i++) {
            if (expenses.get(i) >= max)
                max = expenses.get(i);
        }
        PointF[] incPoints = new PointF[countOfDays];
        PointF[] expPoints = new PointF[countOfDays];
        for (int i=0; i<countOfDays; i++) {
            incPoints[i] = new PointF();
            expPoints[i] = new PointF();
        }
        Bitmap.Config conf = Bitmap.Config.ARGB_8888; // see other conf types
        Bitmap bmp = Bitmap.createBitmap(w, h, conf); // this creates a MUTABLE bitmap
        Canvas canvas = new Canvas(bmp);
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(Color.WHITE);
        canvas.drawRect(new RectF(0, 0, w, h), paint);
        //TODO
        paint.setColor(ContextCompat.getColor(context, R.color.info_header_lines));
        float one_dp = context.getResources().getDimension(R.dimen.one_dp);
        RectF container = new RectF(3*one_dp, 3*one_dp, w-3*one_dp, h-3*one_dp);
        float margin = container.height()/5;
        for (int i=0; i<6; i++)
            canvas.drawLine(container.left, i*margin+container.top, container.right, i*margin + container.top, paint);
        double amount = 0.0;
        for (int i=0; i<incPoints.length; i++) {
            if (max == 0.0) amount = 0.0;
            else amount = container.height()*incomes.get(i)/max;
            incPoints[i].set(i*distance+container.left, container.bottom - (float) amount);
        }
        for (int i=0; i<expPoints.length; i++) {
            if (max == 0.0) amount = 0.0;
            else amount = container.height()*expenses.get(i)/max;
            expPoints[i].set(i*distance+container.left, container.bottom - (float) amount);
        }
        paint.setAlpha(0xAA);
        paint.setStrokeWidth(1.2f*one_dp);
        paint.setColor(ContextCompat.getColor(context,R.color.green_light_darker_transparent));
        for (int i=0; i<incPoints.length; i++) {
            if (i == 0) continue;
            canvas.drawCircle(incPoints[i-1].x, incPoints[i-1].y, one_dp*1.6f, paint);
            canvas.drawLine(incPoints[i-1].x, incPoints[i-1].y, incPoints[i].x, incPoints[i].y, paint);
            canvas.drawCircle(incPoints[i].x, incPoints[i].y, one_dp*1.6f, paint);
        }
        paint.setColor(ContextCompat.getColor(context, R.color.red_green_darker_monoxrom_transparent ));
        for (int i=0; i<expPoints.length; i++) {
            if (i == 0) continue;
            canvas.drawCircle(expPoints[i-1].x, expPoints[i-1].y, one_dp*1.6f, paint);
            canvas.drawLine(expPoints[i-1].x, expPoints[i-1].y, expPoints[i].x, expPoints[i].y, paint);
            canvas.drawCircle(expPoints[i].x, expPoints[i].y, one_dp*1.6f, paint);
        }
        return bmp;
    }
    static int countOfDays(Calendar beg, Calendar e) {
        Calendar begin = (Calendar) beg.clone();
        Calendar end = (Calendar) e.clone();
        int countOfDays = 0;
        while(begin.compareTo(end) <= 0) {
            countOfDays++;
            begin.add(Calendar.DAY_OF_YEAR, 1);
        }
        return countOfDays;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        final String action = intent.getAction();

        if (WidgetKeys.ACTION_WIDGET_RECEIVER_CHANGE_DIAGRAM.equals(action)) {
            SharedPreferences sPref;
            Log.d(WidgetKeys.TAG, "coming");

            int mAppWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;
            Bundle extras = intent.getExtras();
            if (extras != null) {
                Log.d(WidgetKeys.TAG, "bundle_not_null");
                mAppWidgetId = intent.getIntExtra(
                        WidgetKeys.ACTION_WIDGET_RECEIVER_CHANGE_DIAGRAM_ID,
                        AppWidgetManager.INVALID_APPWIDGET_ID);

            }
            Log.d(WidgetKeys.TAG, mAppWidgetId + "");

           Intent Intik=new Intent(context, PocketAccounter.class);
            Intik.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(Intik);
            if(AppWidgetManager.INVALID_APPWIDGET_ID!=mAppWidgetId)
                WidgetProvider.updateWidget(context, AppWidgetManager.getInstance(context),
                        mAppWidgetId);
        }
        super.onReceive(context, intent);
    }
}
