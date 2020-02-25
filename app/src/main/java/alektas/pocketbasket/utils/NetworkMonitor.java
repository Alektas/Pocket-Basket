package alektas.pocketbasket.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkRequest;
import android.util.Log;

import androidx.annotation.NonNull;

public class NetworkMonitor {
    private final ConnectivityManager mConnectivityManager;
    private NetworkRequest mRequest;
    private ConnectivityManager.NetworkCallback mCallback;
    private NetworkListener mListener;


    public interface NetworkListener {
        void onChange(boolean isAvailable);
    }

    public NetworkMonitor(Context context) {
        mConnectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (mConnectivityManager == null) return;

        mRequest = new NetworkRequest.Builder()
                .addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR)
                .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
                .build();

        mCallback = new ConnectivityManager.NetworkCallback() {
            @Override
            public void onAvailable(@NonNull Network network) {
                Log.d("[Network Connectivity]", "Available");
                if (mListener != null) mListener.onChange(true);
                super.onAvailable(network);
            }

            @Override
            public void onLost(@NonNull Network network) {
                Log.d("[Network Connectivity]", "Lost");
                if (mListener != null) mListener.onChange(false);
                super.onLost(network);
            }
        };
    }

    public void setNetworkListener(NetworkListener listener) {
        mListener = listener;
        mConnectivityManager.requestNetwork(mRequest, mCallback);
    }

    public void removeNetworkListener() {
        mConnectivityManager.unregisterNetworkCallback(mCallback);
        mListener = null;
    }
}
