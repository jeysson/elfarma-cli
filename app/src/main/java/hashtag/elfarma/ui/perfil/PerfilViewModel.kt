package hashtag.elfarma.ui.perfil

import androidx.lifecycle.ViewModel
import hashtag.elfarma.core.models.User
import hashtag.elfarma.core.repository.IUserRepository

class PerfilViewModel(private val _userRep: IUserRepository): ViewModel() {

    fun update(user: User){
        _userRep.update(user).subscribe({

        },{

        })
    }

}