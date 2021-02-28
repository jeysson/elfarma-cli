package hashtag.alldelivery.core.models

class Order {

    var loja: Int? = null

    var itens: ArrayList<Item>? = null

    constructor(){
        itens = ArrayList<Item>()
    }

}