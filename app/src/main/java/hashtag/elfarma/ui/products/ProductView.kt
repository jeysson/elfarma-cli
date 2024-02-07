package hashtag.elfarma.ui.products

import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import hashtag.elfarma.AllDeliveryApplication
import hashtag.elfarma.R
import kotlinx.android.synthetic.main.product_view.*

class ProductView : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.product_view)
        supportActionBar?.hide()
        var product = AllDeliveryApplication.PRODUCT
        product_title.text = product?.nome
        product_description.text = product?.descricao
        //
        if(product?.productImages?.size!! > 0) {
            val imageBytes = android.util.Base64.decode(product.productImages!![0].fotoBase64, 0)
            val image = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
            product_image.setImageBitmap(image)
        }
        //
        back_button.setOnClickListener {
            finish()
        }
    }
}