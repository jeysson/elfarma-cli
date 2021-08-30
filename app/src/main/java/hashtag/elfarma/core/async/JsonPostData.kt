package hashtag.elfarma.core.async

import android.os.AsyncTask
import android.util.Log
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import hashtag.elfarma.core.utils.OnTaskCompleted
import org.json.JSONObject
import java.io.*
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL

class JsonPostData(
    private val urlAddress: String,
    private val data: Any,
    private val completion: OnTaskCompleted?,
    Token: String
) :
    AsyncTask<Void?, Void?, Array<Any?>?>() {

    private val TAG = "[ALLDELIVERY][ConnAsync]"
    private var url: URL? = null
    private val gson: Gson
    private val token: String
    override fun onPreExecute() {
        try {
            url = URL(urlAddress)
            Log.d(TAG, "Connecting to: " + url.toString())
            Log.d(TAG, "JSON to send: " + gson.toJson(data))
        } catch (e: MalformedURLException) {
            Log.e(TAG, e.message, e)
        }
    }

    override fun doInBackground(vararg params: Void?): Array<Any?>? {
        return try {
            val connection = url!!.openConnection() as HttpURLConnection
            val streamReader: InputStreamReader
            val streamWriter: OutputStreamWriter
            val rawResponse = StringBuilder()
            var buffer: String?
            val reader: BufferedReader
            val jsonData: JSONObject

            // Entrada de dados
            connection.doInput = true
            // Saída de dados
            connection.doOutput = true
            // Timeout (1 minuto)
            connection.connectTimeout = 60 * 1000
            // Tipo JSON
            connection.setRequestProperty("Content-Type", "application/json; charset=utf-8")
            connection.setRequestProperty("charset", "utf-8");
            connection.setRequestProperty("Cache-Control", "no-cache"); //even less cache
            //
            connection.setUseCaches(false);
            // Token
            if (!token.isEmpty()) connection.setRequestProperty("Authorization", token)
            // Método POST
            connection.requestMethod = "POST"

            // Abre canal de escrita
            streamWriter = OutputStreamWriter(connection.outputStream)
            // Escreve JSON
            streamWriter.write(gson.toJson(data))
            // Descarrega canal de escrita
            streamWriter.flush()

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
                arrayOf(jsonData, connection, streamWriter, streamReader)
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
            if (objects!![3] != null) {
                (objects[3] as InputStreamReader?)!!.close()
            }
            if (objects[2] != null) {
                (objects[2] as OutputStreamWriter?)!!.close()
            }
            if (objects[1] != null) {
                (objects[1] as HttpURLConnection?)!!.disconnect()
            }
        } catch (e: Exception) {
            //Log.e(TAG, e.message)
            completion!!.onTaskCompleted(null)
        }
    }

    init {
        gson = GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
            .excludeFieldsWithoutExposeAnnotation().create()
        token = Token
    }
}