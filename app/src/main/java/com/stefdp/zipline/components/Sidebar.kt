//package com.stefdp.zipline.components
//
//import androidx.compose.foundation.layout.padding
//import androidx.compose.material3.ModalDrawerSheet
//import androidx.compose.material3.NavigationDrawerItem
//import androidx.compose.material3.NavigationDrawerItemDefaults
//import androidx.compose.material3.Text
//import androidx.compose.runtime.Composable
//import androidx.compose.ui.Modifier
//import com.stefdp.zipline.screens.*
//
//@Composable
//fun Sidebar(onItemClick: (AppScreen) -> Unit) {
//    ModalDrawerSheet {
////        Spacer(Modifier.height(12.dp))
////        Text("Sidebar Menu", modifier = Modifier.padding(16.dp), style = MaterialTheme.typography.titleMedium)
////        HorizontalDivider()
//
//        NavigationDrawerItem(
//            label = { Text("Home") },
//            selected = false,
//            onClick = { onItemClick(HomeScreen) },
//            modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
//        )
//
//        NavigationDrawerItem(
//            label = { Text("Metrics") },
//            selected = false,
//            onClick = { onItemClick(MetricsScreen) },
//            modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
//        )
//
//        NavigationDrawerItem(
//            label = { Text("Files") },
//            selected = false,
//            onClick = { onItemClick(FilesScreen()) },
//            modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
//        )
//
//        NavigationDrawerItem(
//            label = { Text("Folders") },
//            selected = false,
//            onClick = { onItemClick(FoldersScreen) },
//            modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
//        )
//
//        // UPLOAD
//
//        NavigationDrawerItem(
//            label = { Text("URLs") },
//            selected = false,
//            onClick = { onItemClick(UrlsScreen) },
//            modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
//        )
//
//        // ADMIN
//    }
//}

package com.stefdp.zipline.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.stefdp.zipline.BASE_CORNER_RADIUS
import com.stefdp.zipline.DebugWrapper
import com.stefdp.zipline.R
import com.stefdp.zipline.screens.*

const val DRAWER_CORNER_RADIUS = BASE_CORNER_RADIUS + 5

@Composable
fun Sidebar(
    onItemClick: (AppScreen) -> Unit
) {
    ModalDrawerSheet(
        drawerContainerColor = MaterialTheme.colorScheme.surface,
        drawerShape = RoundedCornerShape(
            topEnd = DRAWER_CORNER_RADIUS.dp,
            bottomEnd = DRAWER_CORNER_RADIUS.dp
        ),
    ) {
        val scrollState = rememberScrollState()

        Column(
            modifier = Modifier
                .fillMaxHeight()
                .verticalScroll(scrollState)
        ) {
            NavigationDrawerItem(
                label = {
                    Row {
                        Icon(
                            painter = painterResource(R.drawable.home),
                            contentDescription = "Home Screen"
                        )

                        Spacer(
                            modifier = Modifier.width(6.dp)
                        )

                        Text("Home")
                    }
                },
                selected = false,
                onClick = { onItemClick(HomeScreen) },
                modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding),
                shape = RoundedCornerShape(BASE_CORNER_RADIUS.dp)
            )

            NavigationDrawerItem(
                label = {
                    Row {
                        Icon(
                            painter = painterResource(R.drawable.bar_chart),
                            contentDescription = "Metrics Screen"
                        )

                        Spacer(
                            modifier = Modifier.width(6.dp)
                        )

                        Text("Metrics")
                    }
                },
                selected = false,
                onClick = { onItemClick(MetricsScreen) },
                modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding),
                shape = RoundedCornerShape(BASE_CORNER_RADIUS.dp)
            )

            NavigationDrawerItem(
                label = {
                    Row {
                        Icon(
                            painter = painterResource(R.drawable.draft),
                            contentDescription = "Files Screen"
                        )

                        Spacer(
                            modifier = Modifier.width(6.dp)
                        )

                        Text("Files")
                    }
                },
                selected = false,
                onClick = { onItemClick(FilesScreen()) },
                modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding),
                shape = RoundedCornerShape(BASE_CORNER_RADIUS.dp)
            )

            NavigationDrawerItem(
                label = {
                    Row {
                        Icon(
                            painter = painterResource(R.drawable.folder),
                            contentDescription = "Folders Screen"
                        )

                        Spacer(
                            modifier = Modifier.width(6.dp)
                        )

                        Text("Folders")
                    }
                },
                selected = false,
                onClick = { onItemClick(FoldersScreen) },
                modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding),
                shape = RoundedCornerShape(BASE_CORNER_RADIUS.dp)
            )

            ExpandableDrawerSection(
                label = {
                    Row {
                        Icon(
                            painter = painterResource(R.drawable.upload),
                            contentDescription = "Upload Menu"
                        )

                        Spacer(
                            modifier = Modifier.width(6.dp)
                        )

                        Text("Upload")
                    }
                },
            ) {
                NavigationDrawerItem(
                    label = {
                        Row {
                            Icon(
                                painter = painterResource(R.drawable.upload_file),
                                contentDescription = "Upload File Screen"
                            )

                            Spacer(
                                modifier = Modifier.width(6.dp)
                            )

                            Text("File")
                        }
                    },
                    selected = false,
                    onClick = { onItemClick(UploadFileScreen) },
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding),
                    shape = RoundedCornerShape(BASE_CORNER_RADIUS.dp)
                )

                NavigationDrawerItem(
                    label = {
                        Row {
                            Icon(
                                painter = painterResource(R.drawable.text_fields),
                                contentDescription = "Upload Text Screen"
                            )

                            Spacer(
                                modifier = Modifier.width(6.dp)
                            )

                            Text("Text")
                        }
                    },
                    selected = false,
                    onClick = { onItemClick(UploadTextScreen) },
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding),
                    shape = RoundedCornerShape(BASE_CORNER_RADIUS.dp)
                )
            }

            NavigationDrawerItem(
                label = {
                    Row {
                        Icon(
                            painter = painterResource(R.drawable.link_2),
                            contentDescription = "URLs Screen"
                        )

                        Spacer(
                            modifier = Modifier.width(6.dp)
                        )

                        Text("URLs")
                    }
                },
                selected = false,
                onClick = { onItemClick(UrlsScreen) },
                modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding),
                shape = RoundedCornerShape(BASE_CORNER_RADIUS.dp)
            )

            ExpandableDrawerSection(
                label = {
                    Row {
                        Icon(
                            painter = painterResource(R.drawable.home),
                            contentDescription = "Administration Menu"
                        )

                        Spacer(
                            modifier = Modifier.width(6.dp)
                        )

                        Text("Administrator")
                    }
                },
            ) {
                NavigationDrawerItem(
                    label = {
                        Row {
                            Icon(
                                painter = painterResource(R.drawable.settings),
                                contentDescription = "Administrator Settings Screen"
                            )

                            Spacer(
                                modifier = Modifier.width(6.dp)
                            )

                            Text("Settings")
                        }
                    },
                    selected = false,
                    onClick = { onItemClick(AdminSettingsScreen) },
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding),
                    shape = RoundedCornerShape(BASE_CORNER_RADIUS.dp)
                )

                NavigationDrawerItem(
                    label = {
                        Row {
                            Icon(
                                painter = painterResource(R.drawable.group),
                                contentDescription = "Administrator Users Screen"
                            )

                            Spacer(
                                modifier = Modifier.width(6.dp)
                            )

                            Text("Users")
                        }
                    },
                    selected = false,
                    onClick = { onItemClick(AdminUsersScreen) },
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding),
                    shape = RoundedCornerShape(BASE_CORNER_RADIUS.dp)
                )

                NavigationDrawerItem(
                    label = {
                        Row {
                            Icon(
                                painter = painterResource(R.drawable.mail),
                                contentDescription = "Administrator Invites Screen"
                            )

                            Spacer(
                                modifier = Modifier.width(6.dp)
                            )

                            Text("Invites")
                        }
                    },
                    selected = false,
                    onClick = { onItemClick(AdminInvitesScreen) },
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding),
                    shape = RoundedCornerShape(BASE_CORNER_RADIUS.dp)
                )
            }

            DebugWrapper {
                ExpandableDrawerSection(
                    label = {
                        Text("Debug")
                    },
                ) {
                    NavigationDrawerItem(
                        label = {
                            Text("Login")
                        },
                        selected = false,
                        onClick = { onItemClick(LoginScreen) },
                        modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding),
                        shape = RoundedCornerShape(BASE_CORNER_RADIUS.dp)
                    )

                    NavigationDrawerItem(
                        label = {
                            Text("Loading")
                        },
                        selected = false,
                        onClick = { onItemClick(LoadingScreen) },
                        modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding),
                        shape = RoundedCornerShape(BASE_CORNER_RADIUS.dp)
                    )

                    NavigationDrawerItem(
                        label = {
                            Text("Biometric Authentication")
                        },
                        selected = false,
                        onClick = { onItemClick(BiometricAuthScreen) },
                        modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding),
                        shape = RoundedCornerShape(BASE_CORNER_RADIUS.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun ExpandableDrawerSection(
    label: @Composable () -> Unit,
    content: @Composable () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    val rotationState by animateFloatAsState(
        targetValue = if (expanded) 180f else 0f,
        label = "ArrowRotation"
    )

    Column {
        NavigationDrawerItem(
            label = label,
            selected = false,
            onClick = { expanded = !expanded },
            badge = {
                Icon(
                    painter = painterResource(R.drawable.keyboard_arrow_down),
                    contentDescription = if (expanded) "Collapse" else "Expand",
                    modifier = Modifier.rotate(rotationState)
                )
            },
            modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding),
            shape = RoundedCornerShape(BASE_CORNER_RADIUS.dp)
        )

        AnimatedVisibility(
            visible = expanded,
            enter = expandVertically() + fadeIn(),
            exit = shrinkVertically() + fadeOut()
        ) {
            Column(
                modifier = Modifier.padding(start = 24.dp)
            ) {
                content()
            }
        }
    }
}