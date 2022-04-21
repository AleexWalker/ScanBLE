package com.example.scanble

import android.Manifest
import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanFilter
import android.bluetooth.le.ScanResult
import android.bluetooth.le.ScanSettings
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import com.example.scanble.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    companion object{
        private val TAG = "TWESBTSCANNER"
        val BLUETOOTH_REQUESTCODE = 1
    }

    private val bluetoothAdapter : BluetoothAdapter by lazy {
        (getSystemService(BLUETOOTH_SERVICE) as BluetoothManager).adapter
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        if(!bluetoothAdapter.isEnabled){
            binding.btInfoTextView.text = "BT is disabled"
            openBtActivity()
        }else{
            binding.btInfoTextView.text = "BT is ready"
        }

        binding.startScanButton.setOnClickListener{
            startBLEScan()
            bleScanCallback
        }

        binding.openBluetooth.setOnClickListener {
            openBtActivity()
        }
    }

    var btRequestActivity = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data: Intent? = result.data
            Log.v(TAG, "btRequestActivity RESULT OK")
            binding.btInfoTextView.text = "BT is ready"
        } else {
            Log.v(TAG, "btRequestActivity RESULT NOT OK")
            binding.btInfoTextView.text = "BT is disabled"
        }
    }

    fun openBtActivity() {
        val intent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
        btRequestActivity.launch(intent)
    }

    fun startBLEScan(){
        Log.v(TAG, "StartBLEScan")

        var scanFilter = ScanFilter.Builder().build()

        var scanFilters : MutableList<ScanFilter> = mutableListOf()
        scanFilters.add(scanFilter)

        var scanSettings = ScanSettings.Builder().setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY).build();

        Log.v(TAG, "Start Scan")
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.BLUETOOTH_SCAN
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        bluetoothAdapter.bluetoothLeScanner.startScan(scanFilters,scanSettings,bleScanCallback)
    }

    private val bleScanCallback : ScanCallback by lazy {
        object : ScanCallback() {

            override fun onScanResult(callbackType: Int, result: ScanResult?) {
                super.onScanResult(callbackType, result)
                Log.v(TAG, "onScanResult")

                val bluetoothDevice = result?.device

                if (bluetoothDevice != null) {
                    if (ActivityCompat.checkSelfPermission(
                            this@MainActivity,
                            Manifest.permission.BLUETOOTH_CONNECT
                        ) != PackageManager.PERMISSION_GRANTED
                    ) {
                        return
                    }
                    Log.v(
                        TAG, "Device Name ${bluetoothDevice.name} Device Address ${bluetoothDevice.uuids}"
                    )
                }
            }
        }
    }
}