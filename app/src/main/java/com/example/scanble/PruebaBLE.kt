package com.example.scanble

import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.BluetoothManager
import android.bluetooth.le.BluetoothLeScanner
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.PermissionChecker
import com.example.scanble.databinding.ActivityPruebaBleBinding

class PruebaBLE : AppCompatActivity() {

    private lateinit var binding: ActivityPruebaBleBinding
    private var listaMutable = hashMapOf<String, Any>()

    val bleScanner = object : ScanCallback() {
        @RequiresApi(Build.VERSION_CODES.R)
        @SuppressLint("MissingPermission")
        override fun onScanResult(callbackType: Int, result: ScanResult?) {
            super.onScanResult(callbackType, result)
            if (result != null) {
                Log.d("DeviceListActivity","onScanResult: ${result.device?.address} - ${result.device?.name}")
                //ListDevicesAdapter(this@PruebaBLE, )
            }
            /**if (result!!.device.address.equals("28:DE:65:40:F3:47")) {
            Log.d("DeviceListActivity","onScanResult: ${result.device?.address} - ${result.device?.name}")
            listaMutable["28:DE:65:40:F3:47"] = "28:DE:65:40:F3:47"
            }*/
        }

        override fun onBatchScanResults(results: MutableList<ScanResult>?) {
            super.onBatchScanResults(results)
            Log.d("DeviceListActivity","onBatchScanResults:${results.toString()}")
        }

        override fun onScanFailed(errorCode: Int) {
            super.onScanFailed(errorCode)
            Log.d("DeviceListActivity", "onScanFailed: $errorCode")
        }
    }

    private val bluetoothLeScanner: BluetoothLeScanner
        get() {
            val bluetoothManager = applicationContext.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
            val bluetoothAdapter = bluetoothManager.adapter
            return bluetoothAdapter.bluetoothLeScanner
        }

    class ListDevicesAdapter(context: Context?, resource: Int) : ArrayAdapter<String>(context!!, resource)

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d("DeviceListActivity", "onCreate()")
        super.onCreate(savedInstanceState)
        binding = ActivityPruebaBleBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onStart() {
        Log.d("ScanDeviceActivity", "onStart()")
        super.onStart()

        binding.searchDevices.setOnClickListener {
            when (PermissionChecker.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)) {
                PackageManager.PERMISSION_GRANTED -> bluetoothLeScanner.startScan(bleScanner)
                else -> requestPermissions(arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION), 1)
            }
        }

        binding.listDevices.setOnClickListener {
            Log.v("DeviceListActivity", listaMutable.toString())
        }
    }

    override fun onStop() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.BLUETOOTH_SCAN
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        bluetoothLeScanner.stopScan(bleScanner)
        super.onStop()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when (requestCode) {
            1 -> when (grantResults) {
                intArrayOf(PackageManager.PERMISSION_GRANTED) -> {
                    Log.d("ScanDevices", "onRequestPermissionsResult(PERMISSION_GRANTED)")
                    if (ActivityCompat.checkSelfPermission(
                            this,
                            Manifest.permission.BLUETOOTH_SCAN
                        ) != PackageManager.PERMISSION_GRANTED
                    ) {
                        return
                    }
                    bluetoothLeScanner.startScan(bleScanner)
                }
                else -> {
                    Log.d("ScanDevices", "onRequestPermissionsResult(not PERMISSION_GRANTED)")
                }
            }
            else -> super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }
}