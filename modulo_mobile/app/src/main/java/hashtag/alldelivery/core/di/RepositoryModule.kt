package hashtag.alldelivery.core.di

import hashtag.alldelivery.core.repository.IProductRepository
import hashtag.alldelivery.core.repository.IStoreRepository
import hashtag.alldelivery.core.repository.ProductRepository
import hashtag.alldelivery.core.repository.StoreRepository
import org.koin.dsl.module.module

val repositoryModule = module {
    single { StoreRepository(get()) as IStoreRepository }
    single { ProductRepository(get()) as IProductRepository }

}
