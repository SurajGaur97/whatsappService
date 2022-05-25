package com.example.whatsappservice;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.content.Context;
import android.content.Intent;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.util.Log;

public class NotificationService extends NotificationListenerService {
    Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
    }

    // This method invokes when a notification arrive.
    @SuppressLint("LongLogTag")
    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        for (StatusBarNotification sbm : this.getActiveNotifications()) {
            if (sbm != null && sbm.getNotification() != null && sbm.getNotification().extras != null && sbm.getNotification().extras.getString("android.title") != null) {
                String packageName = sbm.getPackageName();
                if (packageName.equals("com.whatsapp")) {

                    String title = sbm.getNotification().extras.getString("android.title");
                    if (title.contains(Constants.GET_FROM)) {

                        //Checking the msg is not empty. when bundle of msgs get by whatsapp.
                        if (sbm.getNotification().extras.get(Notification.EXTRA_TEXT_LINES) != null) {
                            //Getting all msgs arrives to the whatsapp in the notification.
                            CharSequence[] textMsgs = (CharSequence[]) sbm.getNotification().extras.get(Notification.EXTRA_TEXT_LINES);
                            if (textMsgs != null) {
                                String[] msg = textMsgs[textMsgs.length - 1].toString().split(":");
                                sendMessage(msg[1]);
                            }
                        }

                        //When single msg arrive then this android.msg's text will send to me.
                        if (sbm.getNotification().extras.getString("android.text") != null || sbm.getNotification().extras.get(Notification.EXTRA_TEXT) != null) {
                            //Getting all msgs arrives to the whatsapp in the notification.
                            String textMsg = sbm.getNotification().extras.getString("android.text") != null ? sbm.getNotification().extras.getString("android.text") : sbm.getNotification().extras.get(Notification.EXTRA_TEXT).toString();
                            sendMessage(textMsg);
                        }
                    }
                }
            }
        }
    }

    private void sendMessage(String message) {
        //Creating object of PrefManager for Enabling the Accessibility Service.
        PrefManager prefManager = new PrefManager(getApplicationContext());
        prefManager.setIsON(true);

        //Setting up our msg and sending it to the user through our accessibility Service.
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.setType("text/plain");
        sendIntent.setPackage("com.whatsapp");
        sendIntent.putExtra(Intent.EXTRA_SUBJECT, "Message from Mammy:");
        sendIntent.putExtra(Intent.EXTRA_TEXT, message);
        sendIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        getApplicationContext().startActivity(sendIntent);
    }
}
