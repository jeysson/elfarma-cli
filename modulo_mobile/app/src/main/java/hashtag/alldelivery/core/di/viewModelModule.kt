 package hashtag.alldelivery.core.di

import hashtag.alldelivery.ui.home.HomeViewModel
import hashtag.alldelivery.ui.store.StoresViewModel
import hashtag.alldelivery.ui.products.ProductViewModel
import org.koin.android.viewmodel.ext.koin.viewModel
import org.koin.dsl.module.module

 val viewModelModule = module {
    viewModel { HomeViewModel() }
     viewModel { StoresViewModel(get()) }
     viewModel { ProductViewModel(get()) }
}