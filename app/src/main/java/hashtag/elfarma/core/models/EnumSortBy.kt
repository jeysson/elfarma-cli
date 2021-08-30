package hashtag.elfarma.core.models

enum class EnumSortBy {
    SORT_BY_A_Z{
        fun aToZ () = 0
    },
    SORT_BY_LOCALIZATION{
        fun location() = 1
    },
    SORT_BY_TIMER{
        fun time () = 2
    },
    SORT_BY_DELIVERY_FEE{
        fun deliveryFee() = 3
    }

}