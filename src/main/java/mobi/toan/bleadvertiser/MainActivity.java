package mobi.toan.bleadvertiser;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.AdvertiseCallback;
import android.bluetooth.le.AdvertiseData;
import android.bluetooth.le.AdvertiseSettings;
import android.bluetooth.le.BluetoothLeAdvertiser;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.UUID;

import mobi.toan.bleadvertiser.utils.BleUtils;


public class MainActivity extends Activity {
    private static final String TAG = "MainActivity";

    private Button mAdvertiseButton;
    private TextView mStatusTextView;
    BluetoothLeAdvertiser mBluetoothLeAdvertiser;
    BluetoothAdapter mBluetoothAdapter;
    BluetoothManager mBluetoothManager;

    private boolean mIsAdvertising = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mBluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = mBluetoothManager.getAdapter();
        mAdvertiseButton = (Button) findViewById(R.id.advertise_button);
        mStatusTextView = (TextView) findViewById(R.id.status_text_view);
        mBluetoothLeAdvertiser = mBluetoothAdapter.getBluetoothLeAdvertiser();

        mAdvertiseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mIsAdvertising) {
                    stopAdvertising();
                }else {
                    startAdvertising();
                }
            }
        });
    }

    private AdvertiseCallback mAdvCallback = new AdvertiseCallback() {
        public void onStartSuccess(android.bluetooth.le.AdvertiseSettings settingsInEffect) {
            if (settingsInEffect != null) {
                Log.d(TAG, "onStartSuccess TxPowerLv="
                        + settingsInEffect.getTxPowerLevel()
                        + " mode=" + settingsInEffect.getMode()
                        + " timeout=" + settingsInEffect.getTimeout());
            } else {
                Log.d(TAG, "onStartSuccess, settingInEffect is null");
            }
        }

        public void onStartFailure(int errorCode) {
            Log.d(TAG, "onStartFailure errorCode=" + errorCode);
        };
    };


    @Override
    protected void onResume() {
        super.onResume();
        checkBleAdvertiseModeAbility();
    }

    private void checkBleAdvertiseModeAbility() {
        if (mBluetoothAdapter.isMultipleAdvertisementSupported()) {
            mStatusTextView.setText(getString(R.string.msg_ble_ads_mode_supported));
        }else {
            mStatusTextView.setText(getString(R.string.msg_ble_ads_mode_not_supported));
        }
    }

    private void startAdvertising() {
        mBluetoothLeAdvertiser.startAdvertising(
                BleUtils.createAdvSettings(true, 0),
                BleUtils.createIBeaconAdvertiseData(
                        UUID.fromString("01020304-0506-0708-1112-131415161718"),
                        (short) 257, (short) 514, (byte) 0xc5),
                mAdvCallback);
        mIsAdvertising = true;
    }

    private void stopAdvertising() {
        if (mBluetoothLeAdvertiser != null) {
            mBluetoothLeAdvertiser.stopAdvertising(mAdvCallback);
        }
        mIsAdvertising = false;
    }
}
