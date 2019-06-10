package com.sxt.chat.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.sxt.chat.App;
import com.sxt.chat.R;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public final class Prefs extends BasePrefs {

    public static final String PREF_SERVER = "server";
    public static final String KEY_USER_NAME = "UserName";
    public static final String KEY_TICKET = "Ticket";
    public static final String PREF_ACCOUNT_ID = "AccountId";
    //    public static final String DEFAULT_SERVER = "www.i-zhaohu.com";
    public static final String DEFAULT_SERVER = "ceshi.icloudcare.com";

    public static String KEY_SAVE_USER_DETAIL_INFO = "KEY_SAVE_USER_DETAIL_INFO";
    public static final String KEY_USER_ID = "KEY_USER_ID";
    public static final String KEY_USER_NIKNAME = "KEY_USER_NIKNAME";
    public static final String KEY_USER_GENDER = "KEY_USER_GENDER";
    public static final String KEY_Authorization = "Authorization";

    private static Prefs instance;
    public static String KEY_EXIT_ACTIVITY = "KEY_EXIT_ACTIVITY";
    public static final String KEY_IS_FIRST_ENTER = "KEY_IS_FIRST_ENTER";
    public static String KEY_IS_WEI_QUEZHEN = "KEY_IS_WEI_QUEZHEN";
    public static String KEY_HAVE_NEXT = "KEY_HAVE_NEXT";
    public static String KEY_COPD_INFO = "KEY_COPD_INFO";
    public static String KEY_COPD_INFO_TYPE = "KEY_COPD_INFO_TYPE";
    public static String KEY_COPD_INFO_INDEX = "KEY_COPD_INFO_INDEX";
    public static String KEY_CURRENT_USER_NAME = "KEY_CURRENT_USER_NAME";
    public static final String KEY_LAST_RESUME_MILLIS = "KEY_LAST_RESUME_MILLIS";
    public static final String KEY_LAST_RESUME_MILLIS_2 = "KEY_LAST_RESUME_MILLIS_2";
    public static final String KEY_USER_HEADER_IMAGE_FLAG = "KEY_USER_HEADER_IMAGE_FLAG";
    public static final String ROOM_INFO = "ROOM_INFO";
    public static final String KEY_BANNER_INFO = "KEY_BANNER_INFO";
    public static final String KEY_BITMAP = "KEY_BITMAP";
    public static final String KEY_FLOAT_X = "KEY_FLOAT_X";
    public static final String KEY_FLOAT_Y = "KEY_FLOAT_Y";


    //PATH
    public String KEY_APP_UPDATE_URL = "icare/upgrade?n=" + KEY_APP_NAME;
    public static String KEY_APP_NAME = "copd";
    public static final String KEY_PATH_CAPTURE_IMG = App.getCtx().getExternalCacheDir() + File.separator + "capture_img";
    public static final String KEY_PATH_CROP_IMG = App.getCtx().getExternalCacheDir() + File.separator + "crop_img";
    public static final String KEY_PATH_TAKE_PHOTO_IMG = App.getCtx().getExternalCacheDir() + File.separator + "take_photo_img";
    public static final String KEY_PATH_RECORD = "record";
    public static final String KEY_PATH_AUDIO = "audio";

    private Context context;
    private String userName;
    private String ticket;
    private int accountId;
    private String Authorization;
    private int appMode = -1;
    private int serverVersion;

    public String getRecordFolder() {
        return App.getCtx().getExternalCacheDir() + File.separator + KEY_PATH_RECORD;
    }

    public String getAudioFolder() {
        return App.getCtx().getExternalCacheDir() + File.separator + KEY_PATH_AUDIO;
    }

    private Prefs(Context context) {
        super(context);
        this.context = context.getApplicationContext();
    }

    public static synchronized Prefs getInstance(Context context) {
        if (instance == null) {
            instance = new Prefs(context);
        }
        return instance;
    }

    public String getServerHost() {
        String server = getString(PREF_SERVER, DEFAULT_SERVER);
        int pos = server.indexOf(':');
        if (pos > 0) {
            return server.substring(0, pos);
        }
        return server;
    }

    public String getServerUrl() {
        String server = getString(PREF_SERVER, DEFAULT_SERVER);
        return "https://" + server + "/";
    }

    public String getUserName() {
        if (userName == null) {
            return super.getString(KEY_USER_NAME, null);
        } else {
            return userName;
        }
    }

    public String getNikName() {
        return super.getString(KEY_USER_NIKNAME, App.getCtx().getResources().getString(R.string.displayName));
    }

    public int getUserId() {
        return getInt(Prefs.KEY_USER_ID, 0);
    }

    public String getAuthorization() {
        if (Authorization == null) {
            return super.getString(KEY_Authorization, null);
        } else {
            return Authorization;
        }
    }

    public String getTicket() {
        if (ticket == null) {
            return super.getString(KEY_TICKET, null);
        } else {
            return ticket;
        }
    }

    public int getAccountId() {
        if (accountId <= 0) {
            return super.getInt(PREF_ACCOUNT_ID, 0);
        } else {
            return accountId;
        }
    }

    public void setTicket(String userName, String ticket, int accountId) {
        this.userName = userName;
        this.ticket = ticket;
        this.accountId = accountId;
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(KEY_USER_NAME, userName);
        editor.putString(KEY_TICKET, ticket);
        editor.putInt(PREF_ACCOUNT_ID, accountId);
        editor.apply();
    }

    public void clearUserPrefs() {
        SharedPreferences sp = context.getSharedPreferences(getUserName(), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.clear(); // reset user data, eg: csn
        editor.apply();
    }

    public void putServerUrl(String url) {
        String user = getUserName();
        if (user != null) {
            SharedPreferences sp = context.getSharedPreferences(user, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sp.edit();
            editor.putString(PREF_SERVER, url);
            editor.apply();
        }
    }

    public List<String> getAll() {
        List<String> list = new ArrayList<>();
        String user = getUserName();
        if (user != null) {
            SharedPreferences sp = context.getSharedPreferences(user, Context.MODE_PRIVATE);
            Map<String, ?> allContent = sp.getAll();
            //注意遍历map的方法
            for (Map.Entry<String, ?> entry : allContent.entrySet()) {
                //content+=(entry.getKey()+entry.getValue());
                list.add(entry.getKey());
            }
        }
        return list;
    }

    public void putObject(String key, Object obj) {

        String user = getUserName();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            ObjectOutputStream oout = new ObjectOutputStream(out);
            oout.writeObject(obj);
            String value = new String(Base64.encode(out.toByteArray()));
            SharedPreferences sp = context.getSharedPreferences(user, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sp.edit();
            editor.putString(key, value);
            editor.apply();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public Object getObject(String key) {

        String user = getUserName();
        SharedPreferences sp = context.getSharedPreferences(user, Context.MODE_PRIVATE);
        String value = sp.getString(key, null);
        if (value != null) {
            byte[] valueBytes = Base64.decode(value);
            ByteArrayInputStream bin = new ByteArrayInputStream(valueBytes);
            try {
                ObjectInputStream oin = new ObjectInputStream(bin);

                return oin.readObject();
            } catch (Exception e) {
                return null;
            }
        }
        return null;
    }

    public int getServerVersion() {
        return serverVersion;
    }

    public void setServerVersion(int serverVersion) {
        this.serverVersion = serverVersion;
    }
}
