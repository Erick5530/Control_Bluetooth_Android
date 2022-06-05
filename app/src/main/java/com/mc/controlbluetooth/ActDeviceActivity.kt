package com.mc.controlbluetooth

import android.annotation.SuppressLint
import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView.OnItemClickListener
import android.widget.ArrayAdapter
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.mc.controlbluetooth.databinding.ActDeviceBinding

@SuppressLint("MissingPermission")
class ActDeviceActivity : AppCompatActivity() {

    companion object{
        var EXTRA_DEVICE_ADDRESS = "device_address"
    }

    private lateinit var binding: ActDeviceBinding

    // Debugging for LOGCAT
    private val TAG = "DeviceListActivity"
    private val D = true



    // Member fields
    private var mBtAdapter: BluetoothAdapter? = null
    private var mPairedDevicesArrayAdapter: ArrayAdapter<String>? = null

    private var resultBluetooth =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {

                //  val data: Intent? = result.data
                println("resultBluetooth(2)============>")

            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initBinding()
        checkPermision()
    }

    private fun initBinding() {
        binding = ActDeviceBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
    }

    private fun checkPermision(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            Permission(this@ActDeviceActivity).checkPermissions()
        }

    }

    // Set up on-click listener for the list (nicked this - unsure)
    @SuppressLint("SetTextI18n")
    private val mDeviceClickListener =
        OnItemClickListener { av, v, arg2, arg3 ->
            binding.connecting.text = "Conectando..."
            // Get the device MAC address, which is the last 17 chars in the View
            val info = (v as TextView).text.toString()
            val address = info.substring(info.length - 17)

            // Make an intent to start next activity while taking an extra which is the MAC address.
            val i = Intent(this@ActDeviceActivity, MainActivity::class.java)
            i.putExtra(EXTRA_DEVICE_ADDRESS, address)
            finish()
            startActivity(i)
        }



    override fun onResume() {
        super.onResume()

        checkBTState()
        binding.connecting.textSize = 20F
        binding.connecting.text = " "

        mPairedDevicesArrayAdapter = ArrayAdapter<String>(this, R.layout.device_name)

        binding.pairedDevices.adapter = mPairedDevicesArrayAdapter
        binding.pairedDevices.onItemClickListener = mDeviceClickListener

        val bluetoothManager = getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        mBtAdapter =  bluetoothManager.adapter

        // Get a set of currently paired devices and append to 'pairedDevices'
        val pairedDevices: Set<BluetoothDevice> = mBtAdapter!!.bondedDevices


        // Add previosuly paired devices to the array
        if (pairedDevices.isNotEmpty()) {
            binding.titlePairedDevices.visibility =
                View.VISIBLE //make title viewable
            for (device in pairedDevices) {
                mPairedDevicesArrayAdapter!!.add(device.name + "\n" + device.address)
            }
        } else {
            val noDevices = "Ningun dispositivo pudo ser emparejado"
            mPairedDevicesArrayAdapter!!.add(noDevices)
        }

    }

    private fun checkBTState() {
        // Check device has Bluetooth and that it is turned on
        val bluetoothManager = getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        mBtAdapter =  bluetoothManager.adapter
        if (mBtAdapter == null) {
            Toast.makeText(baseContext, "El dispositivo no soporta Bluetooth", Toast.LENGTH_SHORT)
                .show()
        } else {
            if (mBtAdapter!!.isEnabled) {
                Log.d(TAG, "...Bluetooth Activado...")
            } else {
                //Prompt user to turn on Bluetooth
                openBluetoothEnable()
            }
        }
    }

    private fun openBluetoothEnable() {
        val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
        resultBluetooth.launch(enableBtIntent)
    }
}