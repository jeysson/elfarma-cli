package hashtag.elfarma.core.utils

import org.json.JSONObject

interface OnTaskCompleted {
    fun onTaskCompleted(data: JSONObject?)
}