package hashtag.elfarma.core.models

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
    @SerializedName("id") var id: Int? = null

    @ColumnInfo
    @SerializedName("ender") var address : String? = null

    @ColumnInfo
    @SerializedName("numero") var number: String? = null

    @ColumnInfo
    @SerializedName("bairro") var neighborhood : String? = null

    @ColumnInfo
    @SerializedName("cidade") var city: String? = null

    @ColumnInfo
    @SerializedName("estado") var state: String? = null

    @ColumnInfo
    @SerializedName("complemento") var complement: String? = null

    @ColumnInfo
    var landmark: String? = null

    @ColumnInfo
    @SerializedName("lat") var lat: Double? =null

    @ColumnInfo
    @SerializedName("longi") var longi: Double? = null

    @ColumnInfo
    var padrao: Int? = 0
}