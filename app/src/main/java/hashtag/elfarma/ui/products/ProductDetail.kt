package hashtag.elfarma.ui.products

import android.app.ActivityOptions
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.jaeger.library.StatusBarUtil
import hashtag.elfarma.AllDeliveryApplication
import hashtag.elfarma.AllDeliveryApplication.Companion.PRODUCT
import hashtag.elfarma.AllDeliveryApplication.Companion.Pedido
import hashtag.elfarma.MainActivity
import hashtag.elfarma.R
import hashtag.elfarma.component.ButtonMinusPlus
import hashtag.elfarma.core.models.Product
import hashtag.elfarma.core.utils.OnBackPressedListener
import hashtag.elfarma.core.utils.OnChangedValueListener
import kotlinx.android.synthetic.main.common_toolbar.*
import kotlinx.android.synthetic.main.product_details_content.*
import kotlinx.android.synthetic.main.product_item_info.*
import org.koin.android.viewmodel.ext.android.sharedViewModel
import java.text.NumberFormat
import java.util.*

class ProductDetail : Fragment(), OnBackPressedListener, OnChangedValueListener {

    private lateinit var product: Product
    private var quantity: Int = 1

    val viewModelProduct: ProductViewModel by sharedViewModel()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.product_details_content, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        AllDeliveryApplication.fragmentoAnterior = AllDeliveryApplication.fragmento
        AllDeliveryApplication.fragmento = this.javaClass.simpleName
        product = PRODUCT!!

        StatusBarUtil.setLightMode(activity)

        btMinusPlus.open = true
        btMinusPlus.animado = false
        btMinusPlus.produto = product
        btMinusPlus.addOnChangeValueListener(this)

        if(Pedido != null) {
            var item = Pedido?.itens?.firstOrNull { p -> p?.produto?.id!! == product.id }

            if(item != null)
                btMinusPlus.total = item.quantity!!
        }

        topbar_title.text = getString(R.string.product_details)

        back_button.setOnClickListener {
            back()
        }

        if(product.productImages != null && product.productImages?.size!! > 0) {
            val imageBytes = android.util.Base64.decode(product.productImages!![0].fotoBase64, 0)
            val image = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
            item_image.setImageBitmap(image)
            item_image.setOnClickListener {
                val intent = Intent(activity, ProductView::class.java)
                startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(activity,
                    item_image, "robot").toBundle())
            }
        }else{
            item_image.setImageResource(R.mipmap.ic_box_medicine)
        }

        item_title.text = product.nome
        item_unit_description.text = product.descricao
        //
        if(product?.precoPromocional != null && product?.precoPromocional!! > 0){
            item_weighable_price_promotional.visibility = View.VISIBLE
            item_weighable_price_promotional.text = getFormatedPrice(product.precoPromocional)
            item_weighable_price2.visibility = View.VISIBLE
            item_weighable_price2.text = getFormatedPrice(product.preco)
            item_weighable_price.visibility = View.INVISIBLE
        }
        //
        item_weighable_price.text = getFormatedPrice(product.preco)

        if(product?.retemReceita)
        {
            info_retem_receita_titulo.visibility = View.VISIBLE
            info_retem_receita.visibility = View.VISIBLE
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


    override fun OnChangedValue(obj: ButtonMinusPlus, prod: Product, value: Int){
        (activity as MainActivity).changeValueBag(obj, prod, value)
    }

    override fun onBackPressed() {
        back()
    }

    private fun back(){
        StatusBarUtil.setLightMode(activity)
        activity!!.supportFragmentManager.popBackStackImmediate()
        activity!!.supportFragmentManager.beginTransaction()
            .remove(this).commit()
    }
}