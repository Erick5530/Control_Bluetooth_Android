package com.mc.controlbluetooth

import android.annotation.SuppressLint
import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothSocket
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.mc.controlbluetooth.AnalogueView.OnMoveListener
import com.mc.controlbluetooth.databinding.ActivityMainBinding
import java.io.IOException
import java.util.*


@SuppressLint("MissingPermission")
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    val handlerState = 0 //used to identify handler message

    private var btAdapter: BluetoothAdapter? = null
    private var btSocket: BluetoothSocket? = null
    private var recDataString = StringBuilder()

    private var mConnectedThread: ConnectedThread? = null
    private var bluetoothIn: Handler? = null

    // SPP UUID service - this should work for most devices
    private val BTMODULEUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")

    // String for MAC address
    private var address: String? = null

    private var resultBluetooth =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {

                //  val data: Intent? = result.data
                println("resultBluetooth============>")

            }
        }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initBinding()
        //initHandler()
        initObjects()
        checkBTState()
        initListeners()
    }

    private fun initListeners() {
        with(binding) {
            titleInfo.setOnClickListener {
                checkBTState()
            }
            analogueUpDown.setOnMoveListener(object : OnMoveListener {

                override fun onMaxMoveInDirection(polarAngle: Double) {


                    if (polarAngle in -3.0..0.0) {
                        textUD.text = getString(R.string.arriba)
                        mConnectedThread?.write("2")
                    } else {
                        textUD.text = getString(R.string.abajo)
                        mConnectedThread?.write("3")
                    }


                }

                override fun onHalfMoveInDirection(polarAngle: Double) {
                    //txtTipoTarjeta.text = "half move in $polarAngle direction"
                }

                override fun onCenter() {
                    textUD.text = getString(R.string.cent)
                    mConnectedThread?.write("0")
                }
            })

            analogueLeftRigth.setOnMoveListener(object : OnMoveListener {

                override fun onMaxMoveInDirection(polarAngle: Double) {


                    if (polarAngle in -1.0..1.0) {
                        textLR.text = getString(R.string.derecha)
                        mConnectedThread?.write("4")
                    } else {
                        textLR.text = getString(R.string.izquierda)
                        mConnectedThread?.write("5")
                    }


                }

                override fun onHalfMoveInDirection(polarAngle: Double) {
                    //txtTipoTarjeta.text = "half move in $polarAngle direction"
                }

                override fun onCenter() {
                    textLR.text = getString(R.string.cent)
                    mConnectedThread?.write("6")
                }

            })
        }
    }

    private fun initBinding() {
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
    }


    private fun initHandler() {

        bluetoothIn = object : Handler(Looper.myLooper()!!) {
            override fun handleMessage(msg: Message) {
                if (msg.what == handlerState) {          //if message is what we want
                    val readMessage = msg.obj as String // msg.arg1 = bytes from connect thread
                    recDataString.append(readMessage) //keep appending to string until ~
                    val endOfLineIndex = recDataString.indexOf("~") // determine the end-of-line
                    if (endOfLineIndex > 0) {                                           // make sure there data before ~
                        if (recDataString[0] == '#') //if it starts with # we know it is what we are looking for
                        {
                            val data = recDataString.split("+")
                            runOnUiThread {
                                binding.txtNombreCarro.text = data[0]
                                binding.txtTipoTarjeta.text = data[1]
                            }
                        }
                        recDataString.delete(0, recDataString.length) //clear all string data

                    }
                }
            }
        }

    }

    private fun initObjects() {
        val bluetoothManager = getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        btAdapter = bluetoothManager.adapter
    }

    //Checks that the Android device Bluetooth is available and prompts to be turned on if off
    private fun checkBTState() {
        if (btAdapter == null) {
            Toast.makeText(baseContext, "El dispositivo no soporta bluetooth", Toast.LENGTH_LONG)
                .show()
        } else {
            if (!btAdapter!!.isEnabled) {
                openBluetoothEnable()
            }
        }
    }

    private fun openBluetoothEnable() {
        val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
        resultBluetooth.launch(enableBtIntent)
    }

    @Throws(IOException::class)
    private fun createBluetoothSocket(device: BluetoothDevice): BluetoothSocket? {
        return device.createRfcommSocketToServiceRecord(BTMODULEUUID)
        //creates secure outgoing connecetion with BT device using UUID
    }


    override fun onResume() {
        super.onResume()

        try {

            val intent = intent

            address = intent.getStringExtra(ActDeviceActivity.EXTRA_DEVICE_ADDRESS)

            val device = btAdapter!!.getRemoteDevice(address)
            btSocket = createBluetoothSocket(device)
        } catch (e: Exception) {
            Toast.makeText(baseContext, "La creación del Socket fallo", Toast.LENGTH_LONG).show()
        }
        // Establish the Bluetooth socket connection.
        try {
            btSocket!!.connect()

            mConnectedThread = ConnectedThread(
                this,
                btSocket!!,
                bluetoothIn!!,
                handlerState
            )
            mConnectedThread!!.start()

            mConnectedThread!!.write("6")
            mConnectedThread!!.write("0")
        } catch (e: java.lang.Exception) {
            try {
                btSocket!!.close()
            } catch (e2: Exception) {
                e2.printStackTrace()
            }
        }

    }

    override fun onPause() {
        super.onPause()
        try {
            //Don't leave Bluetooth sockets open when leaving activity
            btSocket!!.close()
        } catch (e2: Exception) {
            //insert code to deal with thise2.
            e2.printStackTrace()
        }


    }

}