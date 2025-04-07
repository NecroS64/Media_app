import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.PrintWriter
import java.net.Socket
import android.os.Handler
import android.os.Looper
import android.util.Log
import com.example.media_app.OnResponseReceivedListener

class TcpClient(private val serverIp: String, private val serverPort: Int) {

    private var socket: Socket? = null
    private var output: PrintWriter? = null
    private var input: BufferedReader? = null
    @Volatile private var isConnected = false


    private val mainHandler = Handler(Looper.getMainLooper())
    // Подключение к серверу
    fun connect() {
        Thread {
            try {
                socket = Socket(serverIp, serverPort)
                output = PrintWriter(socket?.getOutputStream(), true)
                input = BufferedReader(InputStreamReader(socket?.getInputStream()))
                isConnected = true
                Log.d("myTagTCP","connected")
                listenForResponses()
            } catch (e: Exception) {
                e.printStackTrace()
                Log.d("myTagTCP","not connected")
            }

        }.start()

    }


    var onResponseReceivedListener: OnResponseReceivedListener? = null

    private fun listenForResponses() {
        Thread {
            try {
                while (isConnected) {
                    val response = input?.readLine()
                    response?.let {
                        mainHandler.post {
                            Log.d("myTag_thread", it)
                            // Обновление UI через Handler
                            //onResponseReceived(it)
                            onResponseReceivedListener?.onResponseReceived(response)
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }.start()
    }
    fun isConnected(): Boolean {
        return isConnected
    }
    // Отправка сообщения
    fun sendMessage(message: String) {
        if (isConnected) {
            Thread {
                try {
                    output?.println(message)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }.start()
        } else {
            println("Нет подключения к серверу")
            Log.d("myTag_TCP", "Нет подключения к серверу");
        }
    }


    fun sendJson(message: String) {
        if (isConnected) {
            Thread {
                try {

                    output?.println(message)
                    Log.d("myTag_TCP", "отправка json");
                    Log.d("myTag_TCP", message);
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }.start()
        } else {
            Log.d("myTag_TCP", "Нет подключения к серверу");
        }
    }


    // Закрытие соединения
    fun disconnect() {
        try {
            socket?.shutdownOutput()
            socket?.shutdownInput()
            socket?.setSoLinger(true, 0)
            socket?.close()
            isConnected = false
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    var onResponseReceived: (String) -> Unit = {}

}
