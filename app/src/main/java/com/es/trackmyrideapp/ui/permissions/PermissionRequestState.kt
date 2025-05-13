package com.es.trackmyrideapp.ui.permissions

data class PermissionRequestState(
    val shouldShowSystemDialog: Boolean = false,
    val shouldShowRationaleDialog: Boolean = false,
    val shouldShowBlockedDialog: Boolean = false,
    val isGranted: Boolean = false
)