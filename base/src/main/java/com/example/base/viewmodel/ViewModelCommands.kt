package com.example.base.viewmodel

import android.content.Intent
import androidx.navigation.NavDirections

sealed class ViewModelCommands {

    data class NavigateToIntent(val intent: Intent) : ViewModelCommands()

    data class NavigateTo(val directions: NavDirections) : ViewModelCommands()

    data class ShowError(val text: String) : ViewModelCommands()

    object NavigateBack : ViewModelCommands()

    object HideKeyboard : ViewModelCommands()

    object DataStartLoading : ViewModelCommands()

    object DataLoaded : ViewModelCommands()

    object DataEmptyLoaded : ViewModelCommands()

}
