package hashtag.alldelivery.core.utils

import org.json.JSONObject

interface OnTaskCompleted {
    fun onTaskCompleted(data: JSONObject?)
}