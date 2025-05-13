package com.example.bakerytaskmanagementapp.ui.navigation

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.example.bakerytaskmanagementapp.R
import kotlinx.serialization.Serializable

enum class AppDestinations(
    @StringRes val label: Int,
    @DrawableRes val icon: Int,
    @StringRes val contentDescription: Int,
    val route: AppRoute,
) {
    TASK(
        R.string.tasks,
        R.drawable.ic_home,
        R.string.manage_tasks,
        AppRoute.Tasks
    ),
    HISTORY(
        R.string.history,
        R.drawable.ic_history,
        R.string.task_history,
        AppRoute.History
    ),
    INVENTORY(
        R.string.inventory,
        R.drawable.ic_inventory,
        R.string.manage_inventory,
        AppRoute.Inventory
    ),
    STAFF(
        R.string.staff,
        R.drawable.ic_staff,
        R.string.manage_staff,
        AppRoute.Staff
    )
}

@Serializable
sealed class AppRoute {
    @Serializable
    data object Inventory : AppRoute()
    @Serializable
    data object Tasks : AppRoute()
    @Serializable
    data object History : AppRoute()
    @Serializable
    data object Staff : AppRoute()
}