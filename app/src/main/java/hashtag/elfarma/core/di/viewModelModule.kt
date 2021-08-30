 package hashtag.elfarma.core.di

import hashtag.elfarma.ui.home.HomeViewModel
import hashtag.elfarma.ui.order.OrderViewModel
import hashtag.elfarma.ui.paymentmethod.PaymentMethodViewModel
import hashtag.elfarma.ui.store.StoresViewModel
import hashtag.elfarma.ui.products.ProductViewModel
import org.koin.android.viewmodel.ext.koin.viewModel
import org.koin.dsl.module.module

 val viewModelModule = module {
    viewModel { HomeViewModel() }
     viewModel { StoresViewModel(get()) }
     viewModel { ProductViewModel(get()) }
     viewModel{PaymentMethodViewModel(get())}
     viewModel{ OrderViewModel(get())}
}