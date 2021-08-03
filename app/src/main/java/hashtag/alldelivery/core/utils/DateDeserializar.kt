package hashtag.alldelivery.core.utils

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonParseException
import java.lang.reflect.Type
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class DateDeserializer : JsonDeserializer<Date?> {
    @Throws(JsonParseException::class)
    override fun deserialize(
        json: JsonElement,
        typeOfT: Type,
        context: JsonDeserializationContext
    ): Date? {
        var date = json.asString
        return try {
            var formatter: SimpleDateFormat? = null
            if (date.length <= 8) {
                date = date.substring(0, 8)
                date = date
                formatter = SimpleDateFormat("HH:mm:ss")
                formatter.timeZone = TimeZone.getTimeZone("UTC")
            } else if (date.length <= 14) {
                date = date.substring(0, 10)
                date = date
                formatter = SimpleDateFormat("yyyy-MM-dd")
                formatter.timeZone = TimeZone.getTimeZone("UTC")
            } else if (date.length <= 20) {
                formatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'")
                formatter.timeZone = TimeZone.getTimeZone("UTC")
            } else {
                formatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
                formatter.timeZone = TimeZone.getTimeZone("UTC")
            }
            formatter.parse(date)
        } catch (e: ParseException) {
            null
        }
    }
}