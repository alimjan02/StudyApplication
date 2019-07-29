package com.sxt.chat.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.sxt.chat.App;
import com.sxt.chat.activity.LoginActivity;
import com.sxt.chat.utils.Prefs;
import com.sxt.chat.ws.TJProtocol;

import cn.bmob.v3.BmobUser;

/**
 * Created by izhaohu on 2018/1/16.
 */

public class WatchDogReceiver extends BroadcastReceiver {

    public static final String ACTION_LOGOUT = App.getCtx().getPackageName() + ".receiver.LOGOUT";
    public static String ACTION_CHANGE_SERVER = App.getCtx().getPackageName() + ".receiver.CHANGE_SERVER";
    public static String ACTION_BOOT_COMPLETED = "android.intent.action.BOOT_COMPLETED";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (ACTION_CHANGE_SERVER.equals(intent.getAction())) {
            changeServer();
        } else if (ACTION_LOGOUT.equals(intent.getAction())) {
            logout(context);
        } else if (ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
            Intent in = new Intent(context, LoginActivity.class);
            in.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(in);
        }
    }

    private void changeServer() {
        Prefs prefs = Prefs.getInstance(App.getCtx());
        //清空ticket
        prefs = Prefs.getInstance(App.getCtx());
        prefs.setTicket(null, null, 0);
        TJProtocol.getInstance(App.getCtx()).onDestroy();
    }

    public void logout(Context context) {
        Prefs prefs = Prefs.getInstance(App.getCtx());
        //清空ticket
        prefs = Prefs.getInstance(App.getCtx());
        String userName = prefs.getUserName();
        prefs.setTicket(null, null, 0);

        TJProtocol.getInstance(App.getCtx()).onDestroy();

        BmobUser.logOut();   //清除缓存用户对象
//        BmobUser currentUser = BmobUser.getCurrentUser(); // 现在的currentUser是null了

        Intent intent = new Intent(App.getCtx(), LoginActivity.class);
        intent.putExtra(Prefs.KEY_CURRENT_USER_NAME, userName);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        App.getCtx().startActivity(intent);
    }
}
