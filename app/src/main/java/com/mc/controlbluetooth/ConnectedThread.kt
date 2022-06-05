package com.mc.controlbluetooth

import android.bluetooth.BluetoothSocket
import android.content.Context
import android.os.Handler
import android.widget.Toast
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream

class ConnectedThread(
    private var context: Context,
    socket: BluetoothSocket,
    private var bluetoothIn: Handler,
    private var handlerState:Int

) : Thread()  {

    private val mmInStream: InputStream?
    private val mmOutStream: OutputStream?
    override fun run() {
        val buffer = ByteArray(256)
        var bytes: Int
        println("Preparando para escuchar")
        // Keep looping to listen for received messages
        while (true) {
            println("Escuchandoooo")
            try {
                bytes = mmInStream!!.read(buffer) //read bytes from input buffer
                val readMessage = String(buffer, 0, bytes)
                // Send the obtained bytes to the UI Activity via handler
                bluetoothIn.obtainMessage(handlerState, bytes, -1, readMessage).sendToTarget()
            } catch (e: IOException) {
                break
            }
        }
    }

    //write method
    fun write(input: String) {
        val msgBuffer = input.toByteArray() //converts entered String into bytes
        try {
            mmOutStream!!.write(msgBuffer) //write bytes over BT connection via outstream
        } catch (e: IOException) {
            //if you cannot write, close the application
            Toast.makeText(context, "La Conexión fallo", Toast.LENGTH_LONG).show()
            //finish()
        }
    }

    //creation of the connect thread
    init {
        var tmpIn: InputStream? = null
        var tmpOut: OutputStream? = null
        try {
            //Create I/O streams for connection
            tmpIn = socket.inputStream
            tmpOut = socket.outputStream
        } catch (e: IOException) {
        }
        mmInStream = tmpIn
        mmOutStream = tmpOut
    }
}