package com.sxt.chat.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.DhcpInfo;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;

import com.sxt.chat.App;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.List;

@SuppressLint("WifiManagerLeak")
public class WifiUtils {

    private static WifiUtils WifiUtils = new WifiUtils();
    private static WifiManager mWifiManager;
    private WifiTask wifiTask;
    private WifiStateListener wifiStateListener;

    private WifiUtils() {
    }

    public static WifiUtils getInstance() {
        if (mWifiManager == null) {
            mWifiManager = (WifiManager) App.getCtx().getSystemService(Context.WIFI_SERVICE);
        }
        return WifiUtils;
    }

    public WifiManager getmWifiManager() {
        return mWifiManager;
    }

    public static final int WIFICIPHER_NOPASS = 0;
    public static final int WIFICIPHER_WEP = 1;
    public static final int WIFICIPHER_WPA = 2;

    /**
     * 判断是否连接上wifi
     *
     * @return boolean值(isConnect), 对应已连接(true)和未连接(false)
     */
    public boolean isWifiConnect() {
        NetworkInfo mNetworkInfo = ((ConnectivityManager) App.getCtx().getSystemService(Context.CONNECTIVITY_SERVICE)).getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        return mNetworkInfo.isConnected();
    }

    public boolean isWifiEnabled() {
        return mWifiManager.isWifiEnabled();
    }

    public void openWifi() {
        if (!mWifiManager.isWifiEnabled())
            mWifiManager.setWifiEnabled(true);
    }

    public void closeWifi() {
        mWifiManager.setWifiEnabled(false);
    }

    public void addNetwork(WifiConfiguration paramWifiConfiguration) {
        int i = mWifiManager.addNetwork(paramWifiConfiguration);
        mWifiManager.enableNetwork(i, true);
    }

    public void removeNetwork(int netId) {
        if (mWifiManager != null) {
            mWifiManager.removeNetwork(netId);
            mWifiManager.saveConfiguration();
        }
    }

    public void disconnectWifi(int paramInt) {
        mWifiManager.disableNetwork(paramInt);
    }

    public void startScan() {
        mWifiManager.startScan();
    }

    /**
     * 不主动连接之前的wifi , 因为断开Wi-Fi重新连接时 , 系统会自动重连 , 速度比自己手动连接更快 ,
     * 会导致自己手动操作 无法执行 最终导致Wi-Fi连接失败
     * <p>
     * 所以 , 这里将其他Wi-Fi的自动重连屏蔽掉 , 保留当前的
     *
     * @param SSID
     */
    private void disableAllWifi(String SSID) {
        if (mWifiManager != null) {
            List<WifiConfiguration> configuredNetworks = mWifiManager.getConfiguredNetworks();
            if (configuredNetworks != null) {
                for (WifiConfiguration config : configuredNetworks) {
                    boolean disableNetwork = mWifiManager.disableNetwork(config.networkId);
                    Log.i("wifi", "SSID --> " + config.SSID + " disableNetwork = " + disableNetwork);
                    if (initSSID(config.SSID) != null && !initSSID(config.SSID).equals(initSSID(SSID))) {
                        boolean removeNetwork = mWifiManager.removeNetwork(config.networkId);
                        Log.i("wifi", "SSID --> " + config.SSID + "removeNetwork = " + removeNetwork);
                    }
                    boolean saveConfiguration = mWifiManager.saveConfiguration();
                    Log.i("wifi", "SSID --> " + config.SSID + "saveConfiguration = " + saveConfiguration);
                }
            }
        }
    }

    private WifiConfiguration getConfigBySSID(String SSID) {
        SSID = initSSID(SSID);
        WifiConfiguration config = null;
        if (mWifiManager != null) {
            List<WifiConfiguration> existingConfigs = mWifiManager.getConfiguredNetworks();
            if (existingConfigs != null) {
                for (WifiConfiguration existingConfig : existingConfigs) {
                    if (existingConfig == null) continue;
                    if (initSSID(SSID).equals(initSSID(existingConfig.SSID))) {
                        config = existingConfig;
                        break;
                    }
                }
            }
        }
        return config;
    }

    private WifiConfiguration createWifiInfo(String SSID, String Password, int Type) {
        WifiConfiguration config = getConfigBySSID(SSID);
        if (config == null) {
            config = new WifiConfiguration();
            Log.i("wifi", "新创建");
        } else {
//            removeNetwork(config.networkId);
//            Log.i("wifi", "已有wifi, 现在删除...");
        }
        config.SSID = initSSID(SSID);
        config.allowedAuthAlgorithms.clear();
        config.allowedGroupCiphers.clear();
        config.allowedKeyManagement.clear();
        config.allowedPairwiseCiphers.clear();
        config.allowedProtocols.clear();

        if (Type == WIFICIPHER_NOPASS) {
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
            config.wepTxKeyIndex = 0;
            config.priority = 20000;
            config.wepKeys[0] = "\"" + "\"";
        }
        if (Type == WIFICIPHER_WEP) {
            config.hiddenSSID = true;
            config.wepKeys[0] = "\"" + Password + "\"";
            config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.SHARED);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP104);
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
        }
        if (Type == WIFICIPHER_WPA) {

            config.preSharedKey = "\"" + Password + "\"";
            config.hiddenSSID = true;
            config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
            config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
            // config.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
            config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
            config.status = WifiConfiguration.Status.ENABLED;

        } else {
            return null;
        }

        Log.i("wifi", " Type == " + Type);
        return config;
    }

    public String getApSSID() {
        try {
            Method localMethod = mWifiManager.getClass().getDeclaredMethod("getWifiApConfiguration", new Class[0]);
            if (localMethod == null)
                return null;
            Object localObject1 = localMethod.invoke(mWifiManager, new Object[0]);
            if (localObject1 == null)
                return null;
            WifiConfiguration localWifiConfiguration = (WifiConfiguration) localObject1;
            if (localWifiConfiguration.SSID != null)
                return localWifiConfiguration.SSID;
            Field localField1 = WifiConfiguration.class.getDeclaredField("mWifiApProfile");
            if (localField1 == null)
                return null;
            localField1.setAccessible(true);
            Object localObject2 = localField1.get(localWifiConfiguration);
            localField1.setAccessible(false);
            if (localObject2 == null)
                return null;
            Field localField2 = localObject2.getClass().getDeclaredField("SSID");
            localField2.setAccessible(true);
            Object localObject3 = localField2.get(localObject2);
            if (localObject3 == null)
                return null;
            localField2.setAccessible(false);
            String str = (String) localObject3;
            return str;
        } catch (Exception localException) {
        }
        return null;
    }

    public String getBSSID() {
        WifiInfo mWifiInfo = mWifiManager.getConnectionInfo();
        return mWifiInfo.getBSSID();
    }

    public String getSSID() {
        WifiInfo mWifiInfo = mWifiManager.getConnectionInfo();
        return mWifiInfo.getSSID();
    }

    public String initSSID(String SSID) {
        if (SSID != null) {
            if (SSID.startsWith("\"") && SSID.endsWith("\"")) {
                return SSID;
            }
            if (SSID.startsWith("\"")) {
                return SSID + "\"";
            }
            if (SSID.endsWith("\"")) {
                return "\"" + SSID;
            }
            return "\"" + SSID + "\"";
        }
        return null;
    }

    public String getLocalIPAddress() {
        WifiInfo wifiInfo = mWifiManager.getConnectionInfo();
        return intToIp(wifiInfo.getIpAddress());
    }

    public String getServerIPAddress() {
        DhcpInfo mDhcpInfo = mWifiManager.getDhcpInfo();
        return intToIp(mDhcpInfo.gateway);
    }

    public String getBroadcastAddress() {
        System.setProperty("java.net.preferIPv4Stack", "true");
        try {
            for (Enumeration<NetworkInterface> niEnum = NetworkInterface.getNetworkInterfaces(); niEnum.hasMoreElements(); ) {
                NetworkInterface ni = niEnum.nextElement();
                if (!ni.isLoopback()) {
                    for (InterfaceAddress interfaceAddress : ni.getInterfaceAddresses()) {
                        if (interfaceAddress.getBroadcast() != null) {
                            //                            LogUtils.d(TAG, interfaceAddress.getBroadcast().toString().substring(1));
                            return interfaceAddress.getBroadcast().toString().substring(1);
                        }
                    }
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }

        return null;
    }

    public String getMacAddress() {
        WifiInfo mWifiInfo = mWifiManager.getConnectionInfo();
        if (mWifiInfo == null)
            return "NULL";
        return mWifiInfo.getMacAddress();
    }

    public int getNetworkId() {
        WifiInfo mWifiInfo = mWifiManager.getConnectionInfo();
        if (mWifiInfo == null)
            return 0;
        return mWifiInfo.getNetworkId();
    }

    public WifiInfo getWifiInfo() {
        return mWifiManager.getConnectionInfo();
    }

    public List<ScanResult> getScanResults() {
        return mWifiManager.getScanResults();
    }

    // 查看以前是否也配置过这个网络
    private WifiConfiguration isExsits(String SSID) {
        if (mWifiManager == null || SSID == null) {
            return null;
        }
        SSID = initSSID(SSID);
        List<WifiConfiguration> existingConfigs = mWifiManager.getConfiguredNetworks();
        if (existingConfigs == null) {
            return null;
        }
        for (WifiConfiguration existingConfig : existingConfigs) {
            if (existingConfig != null && SSID.equals(existingConfig.SSID)) {
                return existingConfig;
            }
        }
        return null;
    }

    private String intToIp(int i) {
        return (i & 0xFF) + "." + ((i >> 8) & 0xFF) + "." + ((i >> 16) & 0xFF) + "." + ((i >> 24) & 0xFF);
    }

    /**
     * 获取网络的加密方式
     */
//    public int getSecurity(WifiConfiguration config) {
//        if (config.allowedKeyManagement.get(WifiConfiguration.KeyMgmt.WPA_PSK)) {
//            return WIFICIPHER_WPA;
//        }
//        if (config.allowedKeyManagement.get(WifiConfiguration.KeyMgmt.WPA_EAP) || config.allowedKeyManagement.get(WifiConfiguration.KeyMgmt.IEEE8021X)) {
//            return WIFICIPHER_WEP;
//        }
//        return (config.wepKeys[0] != null) ? WIFICIPHER_WEP : WIFICIPHER_NOPASS;
//    }
    public String getSecurity(WifiConfiguration config) {
        if (config.allowedKeyManagement.get(WifiConfiguration.KeyMgmt.WPA_PSK)) {
            return "[WPA]";
        }
        if (config.allowedKeyManagement.get(WifiConfiguration.KeyMgmt.WPA_EAP) || config.allowedKeyManagement.get(WifiConfiguration.KeyMgmt.IEEE8021X)) {
            return "WEP";
        }
        return (config.wepKeys[0] != null) ? "WEP" : "[ESS]";
    }

    @SuppressLint("ParcelCreator")
    public static class WifiScanResult {

        public String BSSID;
        public String SSID;
        public String capabilities;
        public int level;
        public String PWD;

        public WifiScanResult(String BSSID, String SSID, String capabilities, int level) {
            this.BSSID = BSSID;
            this.SSID = SSID;
            this.capabilities = capabilities;
            this.level = level;
        }
    }

    public WifiUtils connectWifi(String SSID, String pwd, int type) {
        if (wifiTask != null && !wifiTask.isCancelled()) {
            wifiTask.cancel(true);
        }
        wifiTask = new WifiTask();
        wifiTask.execute(SSID, pwd, String.valueOf(type));

        return this;
    }

    class WifiTask extends AsyncTask<String, Void, Boolean> {
        private String SSID;
        private String pwd;
        private boolean flag;
        private boolean updateFailed;

        private long time = 16 * 1000L;

        public WifiTask() {
            if (wifiStateListener != null) {
                wifiStateListener.onStart();
            }
        }

        @Override
        protected Boolean doInBackground(String... strings) {
            if (strings != null && strings.length == 3) {
                this.SSID = strings[0];
                this.pwd = strings[1];

                if (!isWifiEnabled()) {
                    openWifi();
                }
                // 开启wifi需要一段时间,要等到wifi状态变成WIFI_STATE_ENABLED
                while ((mWifiManager.getWifiState() == WifiManager.WIFI_STATE_ENABLING)) {
                    try {
                        Thread.currentThread();
                        Thread.sleep(500);
                    } catch (Exception ie) {
                        ie.printStackTrace();
                    }
                }

                WifiConfiguration wifiConfig = createWifiInfo(strings[0], strings[1], Integer.parseInt(strings[2]));
                if (wifiConfig == null) {
                    mWifiManager.reconnect();
                    return false;
                }
                disableAllWifi(strings[0]);

                int netID = wifiConfig.networkId;
                if (netID < 0) {
                    netID = mWifiManager.addNetwork(wifiConfig);
                    Log.i("wifi", "新添加WIFI  add后的 netID = " + netID);

                } else {
                    //已保存过,更新本地的配置
                    int updateNetwork = mWifiManager.updateNetwork(wifiConfig);
                    Log.i("wifi", "已有WIFI, 更新配置 updateNetwork = " + updateNetwork);
                    if (updateNetwork < 0) {//更新失败
                        updateFailed = true;
                        mWifiManager.reconnect();
                        try {
                            Thread.sleep(2000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        return false;
                    }
                }
                boolean network = mWifiManager.enableNetwork(netID, true);
                if (network) {//保持WIFI配置
                    boolean flag = false;
                    updateFailed = false;
                    mWifiManager.saveConfiguration();
                    Log.i("wifi", "network == true");
                    while (time >= 0) {
                        Log.i("wifi", "SSID = " + SSID + " ☎ " + mWifiManager.getConnectionInfo().getSSID());
                        time -= 500;
                        try {
                            Thread.currentThread();
                            Thread.sleep(500);
                        } catch (Exception ie) {
                            ie.printStackTrace();
                        }

                        if (mWifiManager.getConnectionInfo() != null && !TextUtils.isEmpty(mWifiManager.getConnectionInfo().getSSID())
                                && (mWifiManager.getConnectionInfo().getSSID().contains(SSID))) {

                            if (Utils.isWifi(App.getCtx()) && Utils.isNetworkAvailable(App.getCtx())) {
                                flag = true;
                                break;
                            }
                            try {
                                Thread.sleep(8000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            if (Utils.isWifi(App.getCtx()) && Utils.isNetworkAvailable(App.getCtx())) {
                                flag = true;
                            }
                            break;
                        }
                    }
                    Log.i("wifi", "SSID = " + SSID + " ☎ " + mWifiManager.getConnectionInfo().getSSID());
                    if (!flag) {
                        try {
                            Thread.sleep(2000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    return flag;
                } else {
                    mWifiManager.reconnect();
                    Log.i("wifi", "失败 : SSID = " + SSID + " ☎ " + mWifiManager.getConnectionInfo().getSSID());
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    return false;
                }
            }
            mWifiManager.reconnect();
            Log.i("wifi", "失败 : SSID = " + SSID + " ☎ " + mWifiManager.getConnectionInfo().getSSID());
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return false;
        }

        @Override
        protected void onPostExecute(Boolean successful) {
            super.onPostExecute(successful);
            if (successful) {
                if (wifiStateListener != null) {
                    wifiStateListener.success(this.SSID, this.pwd);
                }
            } else {
                if (wifiStateListener != null) {
                    wifiStateListener.onFailed(new Exception("connect wifi failed ..."),SSID,updateFailed);
                }
            }
        }
    }

    public WifiUtils setWifiStateListener(WifiStateListener wifiStateListener) {
        this.wifiStateListener = wifiStateListener;
        return this;
    }

    public interface WifiStateListener {

        void onStart();

        void onFailed(Exception e, String SSID, boolean updateFailed);

        void success(String SSID, String pwd);
    }

    public void cancel() {
        if (wifiTask != null && !wifiTask.isCancelled()) {
            wifiTask.cancel(true);
        }
    }

}
