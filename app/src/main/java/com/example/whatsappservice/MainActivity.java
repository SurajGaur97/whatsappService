package com.example.whatsappservice;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationManagerCompat;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.Toast;

import java.util.Set;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //App Notification Enabler.
        if (!checkNotificationPermission(getApplicationContext())) {
            Intent intent = new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS");
            startActivity(intent);
        }

        //Checking that Accessibility Service enabled or not
        if (!checkAccessibilityPermission()) {
            Toast.makeText(MainActivity.this, "Permission denied", Toast.LENGTH_SHORT).show();
            //label.setText("Permission Granted");
        }
    }

    //Method to check is the user permitted Notification Access.
    // if not then prompt user to the system's Settings activity
    private static boolean checkNotificationPermission(Context context) {
        Set<String> enabledListeners = NotificationManagerCompat.getEnabledListenerPackages(context);
        boolean isAccess = false;
        if (enabledListeners != null) {
            isAccess = enabledListeners.contains(context.getPackageName());
        }
        return isAccess;
    }

    // Method to check is the user permitted the accessibility permission. This code from GeeksForGeeks
    // if not then prompt user to the system's Settings activity
    public boolean checkAccessibilityPermission() {
        int accessEnabled = 0;
        try {
            accessEnabled = Settings.Secure.getInt(this.getContentResolver(), Settings.Secure.ACCESSIBILITY_ENABLED);
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
        }
        if (accessEnabled == 0) {
            // if not construct intent to request permission
            Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            // request permission via start activity for result
            startActivity(intent);
            return false;
        } else {
            return true;
        }
    }
}