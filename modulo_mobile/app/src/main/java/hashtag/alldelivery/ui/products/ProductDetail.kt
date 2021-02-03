package hashtag.alldelivery.ui.products

import android.app.ActivityOptions
import android.content.Intent
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import hashtag.alldelivery.AllDeliveryApplication
import hashtag.alldelivery.R
import hashtag.alldelivery.core.models.Product
import kotlinx.android.synthetic.main.button_minus_plus.*
import kotlinx.android.synthetic.main.cart_button.*
import kotlinx.android.synthetic.main.common_toolbar.*
import kotlinx.android.synthetic.main.product_details_content.*
import kotlinx.android.synthetic.main.product_item_info.*
import java.text.NumberFormat
import java.util.*

class ProductDetail : AppCompatActivity() {

    private lateinit var product: Product
    private var quantity: Int = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        setContentView(R.layout.product_details_content)
        product = AllDeliveryApplication.PRODUCT!!
        topbar_title.text = getString(R.string.product_details)

        back_button.setOnClickListener {
            finish()
        }

        if(product.productImages?.size!! > 0) {
            val imageBytes = android.util.Base64.decode(product.productImages!![0].fotoBase64, 0)
            val image = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
            item_image.setImageBitmap(image)
            item_image.setOnClickListener {
                val intent = Intent(this, ProductView::class.java)
                startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(this, item_image, "robot").toBundle())
            }
        }

        item_title.text = product.nome
        item_unit_description.text = product.descricao
        txt_quantity.text = quantity.toString()
        item_weighable_price.text = getFormatedPrice(product.preco)
        calcPrice()
        bt_minus.bringToFront()
        bt_minus.setOnClickListener {
            if(quantity > 1) {
                quantity -= 1
                calcPrice()
            }
        }

        bt_plus.bringToFront()
        bt_plus.setOnClickListener {
            quantity += 1
            calcPrice()
        }
    }

    fun getFormatedPrice(valor: Double?): String{
        return NumberFormat.getCurrencyInstance(
            Locale(
                getString(R.string.language),
                getString(R.string.country)
            )
        ).format(valor)
    }

    fun calcPrice(){
        txt_price.text = getFormatedPrice(product.preco?.times(quantity))
        txt_quantity.text = quantity.toString()
    }
}