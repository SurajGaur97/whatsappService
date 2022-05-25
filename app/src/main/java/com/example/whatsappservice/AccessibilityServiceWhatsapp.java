package com.example.whatsappservice;

import android.accessibilityservice.AccessibilityService;
import android.os.Bundle;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import androidx.annotation.NonNull;
import androidx.core.view.accessibility.AccessibilityNodeInfoCompat;

import java.util.List;

public class AccessibilityServiceWhatsapp extends AccessibilityService {
    @Override
    public void onAccessibilityEvent(AccessibilityEvent accessibilityEvent) {
        if (getRootInActiveWindow() == null) {
            return;
        }

        //Managing when to perform accessibility Service.
        PrefManager prefManager = new PrefManager(getApplicationContext());
        if (!prefManager.getIsON()) {
            return;
        }
        prefManager.setIsON(false);


        //Getting Node info through Accessibility.
        AccessibilityNodeInfoCompat rootNodeInfo = AccessibilityNodeInfoCompat.wrap(getRootInActiveWindow());

        //Our Main Code:::::::::::::::::
        //Search for the contact and check if the search icon is on the screen and it is visible to user and click on it.
        //Get 'Search Icon' in Whatsapp.
        searchPanel(rootNodeInfo);

        //Now we will get our group in contact list, and now we will click on it to send the msg.
        //Get Contact List.
        contactPicker(rootNodeInfo);

        //Check if send button is on the screen and it is visible to user. If this condition is met, click the send button.
        //Just Clicking on the proceed button in whatsapp.
        proceedButton(rootNodeInfo);

        // pressing the send button in chat window.
        sendMessage();

        //Performing back operations.
        globalBackOperation();
    }

    private void searchPanel(@NonNull AccessibilityNodeInfoCompat rootNodeInfo) {
        List<AccessibilityNodeInfoCompat> searchNodeList = rootNodeInfo.findAccessibilityNodeInfosByViewId("com.whatsapp:id/menuitem_search");
        if (searchNodeList != null && !searchNodeList.isEmpty()) {
            //If search Button CLick on it.
            Log.e("FromWhatsAppAcsblty:", "SearchNodeList is not Null");
            AccessibilityNodeInfoCompat searchIcon = searchNodeList.get(0);
            if (searchIcon.isVisibleToUser()) {
                //Do nothing.
                searchIcon.performAction(AccessibilityNodeInfo.ACTION_CLICK);

                //Check if search text field is on the screen and then type group name automatically in search box
                List<AccessibilityNodeInfoCompat> searchTextNodeList = rootNodeInfo.findAccessibilityNodeInfosByViewId("com.whatsapp:id/search_src_text");
                if (searchTextNodeList != null && !searchTextNodeList.isEmpty()) {
                    //Get Search Text
                    AccessibilityNodeInfoCompat searchText = searchTextNodeList.get(0);
                    if (searchText != null) {
                        //Set text of Search Field
                        Bundle arguments = new Bundle();
                        //GroupName or Contact name here.
                        arguments.putCharSequence(AccessibilityNodeInfo.ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE, Constants.SEND_TO);
                        searchText.performAction(AccessibilityNodeInfo.ACTION_SET_TEXT, arguments);
                    } else {
                        performGlobalAction(GLOBAL_ACTION_BACK);
                    }
                } else {
                    performGlobalAction(GLOBAL_ACTION_BACK);
                }
            } else {
                performGlobalAction(GLOBAL_ACTION_BACK);
            }
        } else {
            performGlobalAction(GLOBAL_ACTION_BACK);
        }

        //Sleep Accessibility until Searching is perform for 2 seconds.
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void contactPicker(@NonNull AccessibilityNodeInfoCompat rootNodeInfo) {
        List<AccessibilityNodeInfoCompat> contactPickerRowNode = rootNodeInfo.findAccessibilityNodeInfosByViewId("com.whatsapp:id/contactpicker_row_name");
        if (contactPickerRowNode != null && !contactPickerRowNode.isEmpty()) {
            //Opening Search Contact
            AccessibilityNodeInfoCompat contactPickerRow = contactPickerRowNode.get(0);
            if (contactPickerRow != null) {
                Log.e("FromWhatsAppAcsblty:", "ContactPickerRow" + contactPickerRow.getParent().isClickable());
                if (contactPickerRow.getParent().isClickable()) {
                    //Clicking on Contact
                    contactPickerRow.getParent().performAction(AccessibilityNodeInfo.ACTION_CLICK);
                } else {
                    performGlobalAction(GLOBAL_ACTION_BACK);
                }
            } else {
                performGlobalAction(GLOBAL_ACTION_BACK);
            }
        } else {
            performGlobalAction(GLOBAL_ACTION_BACK);
        }

        //Some Device can not handle instant back button. So Waiting for 2 sec.
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void proceedButton(@NonNull AccessibilityNodeInfoCompat rootNodeInfo) {
        List<AccessibilityNodeInfoCompat> sendMessageNodeList = rootNodeInfo.findAccessibilityNodeInfosByViewId("com.whatsapp:id/send");
        if (sendMessageNodeList != null && !sendMessageNodeList.isEmpty()) {
            AccessibilityNodeInfoCompat proceed = sendMessageNodeList.get(0);
            if (proceed.isVisibleToUser()) {
                //'Send' button found Click on it.
                proceed.performAction(AccessibilityNodeInfo.ACTION_CLICK);
            }
            //Fire send Button.
        } else {
            Log.e("FromWhatsAppAcsblty:", "No send Button");
        }

        //Waiting 2 sec for the window open of Whatsapp Group Chat.
        try {
            //Some Device can not handle instant back button. So Waiting for 1 sec.
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void sendMessage() {
        AccessibilityNodeInfo node = getRootInActiveWindow();
        if (node != null) {
            for (int i = node.getChildCount() - 1; i >= 0; i--)   {
                AccessibilityNodeInfo childNode = node.getChild(i);
                if (childNode != null && childNode.getContentDescription() != null) {
                    if (childNode.getContentDescription().equals("Send")) {
                        childNode.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                        Log.i("childNode", "-----getText->" + childNode.getText() + "---getContentDescription-->" + childNode.getContentDescription());
                        Log.e("FromWhatsAppAcsblty", "Message send Successfully.");
                        break;
                    }
                }
            }
        }
    }

    private void globalBackOperation() {
        try {
            //Some Device can not handle instant back button. So Waiting for 1 sec.
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        performGlobalAction(GLOBAL_ACTION_BACK);

        try {
            //Some Device can not handle instant back button. So Waiting for 1 sec.
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        performGlobalAction(GLOBAL_ACTION_BACK);

        try {
            //Some Device can not handle instant back button. So Waiting for 1 sec.
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        performGlobalAction(GLOBAL_ACTION_BACK);
    }

    @Override
    public void onInterrupt() {

    }
}
