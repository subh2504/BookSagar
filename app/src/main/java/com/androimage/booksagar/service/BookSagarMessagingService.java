package com.androimage.booksagar.service;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;

import com.androimage.booksagar.R;
import com.androimage.booksagar.activity.ChatActivity;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

public class BookSagarMessagingService extends com.google.firebase.messaging.FirebaseMessagingService {
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        showNotification(remoteMessage.getData());
    }

    private void showNotification(Map<String, String> m) {
        Intent i = new Intent(this, ChatActivity.class);

        i.putExtra("id", m.get("buyer_id"));
        i.putExtra("book_id", m.get("book_id"));
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, i, PendingIntent.FLAG_UPDATE_CURRENT);
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentTitle("BookSagar")
                .setContentText(m.get("message"))
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentIntent(pendingIntent);

        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        manager.notify(0, builder.build());
    }

}
