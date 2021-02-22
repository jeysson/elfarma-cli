package hashtag.alldelivery.core.models

class Item {

    var produto: Int? = null
    var quantidade: Int? = null

    constructor(id: Int?, qtd: Int?){
        produto = id
        quantidade = qtd
    }
}