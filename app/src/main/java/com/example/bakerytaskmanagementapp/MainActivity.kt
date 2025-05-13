package com.example.bakerytaskmanagementapp

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBarItemColors
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.NavigationRailItemColors
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteColors
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteDefaults
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteItemColors
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.bakerytaskmanagementapp.data.database.OperationState
import com.example.bakerytaskmanagementapp.ui.AdminAccessDialogState
import com.example.bakerytaskmanagementapp.ui.AdminViewModel
import com.example.bakerytaskmanagementapp.ui.history.HistoryScreen
import com.example.bakerytaskmanagementapp.ui.inventory.InventoryScreen
import com.example.bakerytaskmanagementapp.ui.navigation.AppDestinations
import com.example.bakerytaskmanagementapp.ui.navigation.AppRoute
import com.example.bakerytaskmanagementapp.ui.staff.StaffScreen
import com.example.bakerytaskmanagementapp.ui.task.TaskScreen
import com.example.bakerytaskmanagementapp.ui.theme.BakeryTaskManagementAppTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BakeryTaskManagementAppTheme {
                BakeryTaskManagementApp()
            }
        }
    }
}


@Composable
private fun BakeryTaskManagementApp(
    viewModel: AdminViewModel = hiltViewModel()
) {
    val adminUiState by viewModel.adminUiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val navController = rememberNavController()
    var currentDestination by rememberSaveable { mutableStateOf(AppDestinations.TASK) }

    val navSuiteItemColors = getCustomNavSuiteItemColors(adminUiState.isAdmin)

    NavigationSuiteScaffold(
        navigationSuiteItems = {
            AppDestinations.entries.forEach {
                item(
                    icon = {
                        Icon(
                            painter = painterResource(it.icon),
                            contentDescription = stringResource(id = it.contentDescription)
                        )
                    },
//                    label = {
//                        Text( text = stringResource(id = it.label))
//                    },
                    selected = currentDestination == it ,
                    onClick = {
                        currentDestination = it
                        navController.navigate(it.route) {
                            popUpTo(navController.graph.id) {
                                inclusive = true
                            }
                        }
                    },
                    colors = navSuiteItemColors
                )
            }
        },
        navigationSuiteColors = getCustomNavSuiteColors(adminUiState.isAdmin)
    ) {
        // The following block is for displaying toast messages
        LaunchedEffect(adminUiState.operationState) {
            when (val state = adminUiState.operationState) {
                is OperationState.Success -> {
                    Toast.makeText(context, state.message, Toast.LENGTH_SHORT).show()
                    viewModel.resetOperationState() // Clear after showing
                }
                is OperationState.Error -> {
                    Toast.makeText(context, state.message, Toast.LENGTH_SHORT).show()
                    viewModel.resetOperationState() // Clear after showing
                }
                else -> { /* No action needed */ }
            }
        }

        if(adminUiState.adminAccessDialogState != null) {
            AdminAccessDialog(
                dialogState = adminUiState.adminAccessDialogState!!,
            )
        }

        NavHost(
            navController = navController,
            startDestination = AppRoute.Tasks,
            enterTransition = { fadeIn(tween(1)) },
            exitTransition = { fadeOut(tween(1)) },
        ) {
            composable<AppRoute.Tasks> { TaskScreen(adminViewModel = viewModel) }
            composable<AppRoute.History> { HistoryScreen(adminViewModel = viewModel) }
            composable<AppRoute.Inventory> { InventoryScreen(adminViewModel = viewModel) }
            composable<AppRoute.Staff> { StaffScreen(adminViewModel = viewModel) }
        }
    }
}

@Composable
fun AdminAccessButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    isAdmin: Boolean = false
) {
    Button(
        modifier = modifier,
        shape = MaterialTheme.shapes.medium,
        onClick = onClick
    ) {
        Row(
            modifier = Modifier.padding(6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = if(isAdmin) stringResource(R.string.switch_to_staff_mode)
                    else stringResource(R.string.switch_to_admin_mode),
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun AdminAccessDialog(
    modifier: Modifier = Modifier,
    dialogState: AdminAccessDialogState
) {
    val callbacks = dialogState.dialogCallbacks

    Dialog(
        onDismissRequest = callbacks::onDismiss
    ) {
        Card(
            modifier = modifier.width(300.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = stringResource(R.string.enable_admin_mode),
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )

                OutlinedTextField(
                    value = dialogState.password,
                    onValueChange = {
                        callbacks.onValueChange(
                            dialogState.copy(
                                password = it
                            )
                        ) },
                    label = {
                        Text(text = stringResource(R.string.admin_password))
                    }
                )

                // Bottom Dialog Buttons
                Row(
                    modifier = Modifier.padding(top = 4.dp),
                    horizontalArrangement = Arrangement.End
                ) {

                    // Cancellation Button
                    TextButton(
                        onClick = callbacks::onDismiss
                    ) {
                        Text(
                            text = stringResource(R.string.cancel),
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.weight(1f))

                    // Confirmation Button
                    TextButton(
                        onClick = callbacks::onConfirm,
                        enabled = dialogState.isDataValid
                    ) {
                        Text(
                            text = stringResource(R.string.confirm),
                            fontWeight = FontWeight.Bold,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun getCustomNavSuiteColors(isAdmin: Boolean): NavigationSuiteColors {
    val containerColor = if(!isAdmin) MaterialTheme.colorScheme.surfaceDim
        else MaterialTheme.colorScheme.primary

    val contentColor = if(!isAdmin) MaterialTheme.colorScheme.surfaceDim
        else MaterialTheme.colorScheme.primary

    return NavigationSuiteDefaults.colors(
        navigationRailContainerColor = containerColor,
        navigationRailContentColor = contentColor,
        navigationDrawerContainerColor = containerColor,
        navigationDrawerContentColor = contentColor,
        navigationBarContainerColor = containerColor,
        navigationBarContentColor = contentColor
    )
}

@Composable
private fun getCustomNavSuiteItemColors(isAdmin: Boolean): NavigationSuiteItemColors {
    val selectedColor = if (!isAdmin) MaterialTheme.colorScheme.inverseOnSurface
        else MaterialTheme.colorScheme.onPrimaryContainer

    val unselectedColor = if (!isAdmin) MaterialTheme.colorScheme.onSurfaceVariant
        else MaterialTheme.colorScheme.surfaceVariant

    val selectedIndicatorColor = if (!isAdmin) MaterialTheme.colorScheme.primary
        else MaterialTheme.colorScheme.surface

    val unselectedIndicatorColor = if (!isAdmin) MaterialTheme.colorScheme.surfaceDim
        else MaterialTheme.colorScheme.primary

    return NavigationSuiteItemColors(
        navigationRailItemColors = NavigationRailItemColors(
            selectedIconColor = selectedColor,
            selectedTextColor = selectedColor,
            selectedIndicatorColor = selectedIndicatorColor,
            unselectedIconColor = unselectedColor,
            unselectedTextColor = unselectedColor,
            disabledIconColor = Color.Unspecified,
            disabledTextColor = Color.Unspecified
        ),
        navigationDrawerItemColors = NavigationDrawerItemDefaults.colors(
            selectedContainerColor = selectedIndicatorColor,
            selectedIconColor = selectedColor,
            selectedTextColor = selectedColor,
            unselectedContainerColor = unselectedIndicatorColor,
            unselectedIconColor = unselectedColor,
        ),
        navigationBarItemColors = NavigationBarItemColors(
            selectedIconColor = selectedColor,
            selectedTextColor = selectedColor,
            selectedIndicatorColor = selectedIndicatorColor,
            unselectedIconColor = unselectedColor,
            unselectedTextColor = unselectedColor,
            disabledIconColor = Color.Unspecified,
            disabledTextColor = Color.Unspecified
        )
    )
}