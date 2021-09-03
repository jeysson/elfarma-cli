package hashtag.elfarma.core.di

import hashtag.elfarma.core.repository.*
import org.koin.dsl.module.module

val repositoryModule = module {
    single { StoreRepository(get()) as IStoreRepository }
    single { ProductRepository(get()) as IProductRepository }
    single { OrderRepository(get()) as IOrderRepository }
    single { UserRepository(get()) as IUserRepository }
}
