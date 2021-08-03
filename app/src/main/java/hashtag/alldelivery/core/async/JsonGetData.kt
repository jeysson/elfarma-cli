package hashtag.alldelivery.core.async

import android.os.AsyncTask
import android.util.Log
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import hashtag.alldelivery.core.utils.OnTaskCompleted
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL

class JsonGetData(
    private val urlAddress: String,
    private val completion: OnTaskCompleted?,
    Token: String
) :
    AsyncTask<Void?, Void?, Array<Any?>?>() {
    private var url: URL? = null
    private val gson: Gson
    private val token: String
    override fun onPreExecute() {
        try {
            url = URL(urlAddress)
            Log.d(TAG, "Connecting to: " + url.toString())
        } catch (e: MalformedURLException) {
            Log.e(TAG, e.message, e)
        }
    }

     override fun doInBackground(vararg params: Void?): Array<Any?>? {
        return try {
            val connection = url!!.openConnection() as HttpURLConnection
            val streamReader: InputStreamReader
            val rawResponse = StringBuilder()
            var buffer: String?
            val reader: BufferedReader
            val jsonData: JSONObject
            // Token
            if (!token.isEmpty()) connection.setRequestProperty("Authorization", token)
            // Método GET
            connection.requestMethod = "GET"

            // Se CONN == OK
            if (connection.responseCode == HttpURLConnection.HTTP_OK) {
                // Abre canal de leitura
                streamReader = InputStreamReader(connection.inputStream)
                // Cria buffer de leitura
                reader = BufferedReader(streamReader)
                // Lê resposta do buffer
                while (reader.readLine().also { buffer = it } != null) {
                    rawResponse.append(buffer)
                }
                // Cria objeto JSON a partir do buffer
                jsonData = JSONObject(rawResponse.toString())
                // Log
                Log.d(
                    TAG,
                    connection.responseCode.toString() + ": " + connection.responseMessage + " [Response: " + rawResponse.toString() + "]"
                )
                // Retorna array com dados de conexão e dados obtidos
                arrayOf(jsonData, connection, streamReader)
            } else {
                streamReader = InputStreamReader(connection.errorStream)
                reader = BufferedReader(streamReader)
                while (reader.readLine().also { buffer = it } != null) {
                    rawResponse.append(buffer)
                }
                Log.e(
                    TAG,
                    connection.responseCode.toString() + ": " + connection.responseMessage + " [Response: " + rawResponse.toString() + "]"
                )
                null
            }
        } catch (e: Exception) {
            Log.e(TAG, e.message, e)
            null
        }
    }

    override fun onPostExecute(objects: Array<Any?>?) {
        try {
            completion?.onTaskCompleted(objects!![0] as JSONObject?)
            if (objects!![2] != null) {
                (objects[2] as InputStreamReader?)!!.close()
            }
            if (objects[1] != null) {
                (objects[1] as HttpURLConnection?)!!.disconnect()
            }
        } catch (e: Exception) {
            Log.e(TAG, e.message)
            completion!!.onTaskCompleted(null)
        }
    }

    companion object {
        private const val TAG = "[ALLDELIVERY][ConnAsync]"
    }

    init {
        gson = GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
            .excludeFieldsWithoutExposeAnnotation().create()
        token = Token
    }
}
