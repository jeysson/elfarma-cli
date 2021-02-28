package hashtag.alldelivery.core.models

class Item {

    var produto: Product? = null
    var quantidade: Int? = null
    var valor: Double? = null

    constructor(prod: Product?, qtd: Int?, vlr: Double?){
        produto = prod
        quantidade = qtd
        valor = vlr
    }
}