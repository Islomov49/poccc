package com.jim.pocketaccounter.credit.notificat;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.jim.pocketaccounter.PocketAccounter;
import com.jim.pocketaccounter.R;

public class AlarmReceiver extends BroadcastReceiver
{
    public static final int TO_DEBT=10,TO_CRIDET=11;
    public static int req = 0;
    @Override
    public void onReceive(Context context, Intent intent)
    {
        String message = "";
        String title="";
        int which_photo_will_choose=R.drawable.icons_4;
        String path = "";

        int tipFragment=0;
        if (intent != null) {
            message = intent.getStringExtra("msg");
            tipFragment=intent.getIntExtra("TIP", -1);
            title=intent.getStringExtra("title");
            path = intent.getStringExtra("photoPath");
        }
        else
            return;
        Uri alarmSound = Uri.parse("android.resource://" + context.getPackageName() + "/" + R.raw.notif);
        Class opActivity = PocketAccounter.class;
        Intent resultIntent=new Intent(context, opActivity);
        if(tipFragment==TO_DEBT){
            resultIntent.putExtra("TIP",TO_DEBT);
        }
        else if(tipFragment==TO_CRIDET){
            resultIntent.putExtra("TIP",TO_CRIDET);
            which_photo_will_choose=intent.getIntExtra("icon_number", -1);
        }

        Log.d("TIP", ""+resultIntent.getIntExtra("TIP", 0));
        PendingIntent pIntent=PendingIntent.getActivity(context,req++,resultIntent,0);

        Bitmap bitmap;

        if (intent.getStringExtra("photoPath") == null)
        bitmap = BitmapFactory.decodeResource(context.getResources(), which_photo_will_choose);
        else bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.no_photo);

        if (path != null && !path.matches("")) {
            try {
                bitmap = queryContactImage(context, Integer.parseInt(path));
            } catch (NumberFormatException e) {
                bitmap = BitmapFactory.decodeFile(path);
            }
        }

        NotificationCompat.Builder notif_builder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setLargeIcon(bitmap)
                .setAutoCancel(true)
                .setContentTitle(title)
                .setContentText(message)
                .setContentIntent(pIntent)
                .setLights(Color.GREEN, 500, 500)
                .setSound(alarmSound);
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);
        notificationManager.notify((int) (Math.random()*100), notif_builder.build());
    }

    private Bitmap queryContactImage(Context context, int imageDataRow) {
        Cursor c = context.getContentResolver().query(ContactsContract.Data.CONTENT_URI, new String[]{
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
}
