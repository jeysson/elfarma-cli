package hashtag.alldelivery.core.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity
class Address() {
    var title : String? = null
    var description: String? = null
    var description2 : String? = null

    @PrimaryKey(autoGenerate = true)
    var id: Int? = null
    @ColumnInfo
    var address : String? = null
    @ColumnInfo
    var number: String? = null
    @ColumnInfo
    var neighborhood : String? = null
    @ColumnInfo
    var city: String? = null
    @ColumnInfo
    var state: String? = null
    @ColumnInfo
    var complement: String? = null
    @ColumnInfo
    var landmark: String? = null
    @ColumnInfo
    var lat: Double? =null
    @ColumnInfo
    var longi: Double? = null
    @ColumnInfo
    var padrao: Int? = 0
}