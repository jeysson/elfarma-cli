package hashtag.alldelivery.core.di

import hashtag.alldelivery.core.repository.*
import org.koin.dsl.module.module

val repositoryModule = module {
    single { StoreRepository(get()) as IStoreRepository }
    single { ProductRepository(get()) as IProductRepository }
    single { OrderRepository(get()) as IOrderRepository }
}
