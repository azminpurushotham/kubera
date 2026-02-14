package com.collection.kubera.ui.orderhistory

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.collection.kubera.R
import com.collection.kubera.data.repository.TransactionSortType
import com.collection.kubera.ui.theme.boxColorD
import com.collection.kubera.ui.theme.onprimaryD
import timber.log.Timber


@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun ShowCollectionSort(
    viewModel: CollectionViewModel,
    sheetState: SheetState,
    onDismiss: () -> Unit
) {
    Timber.tag("showCollectionSort").i("showBottomSheet")
    ModalBottomSheet(
        sheetState = sheetState,
        shape = RoundedCornerShape(topStart = 10.dp, topEnd = 10.dp),
        containerColor = boxColorD,
        content = {
            Column(
                Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, end = 16.dp),
            ) {
                Text(
                    "Sort By",
                    color = MaterialTheme.colorScheme.onPrimary,
                    fontWeight = FontWeight(600),
                    fontSize = MaterialTheme.typography.titleMedium.fontSize,
                )
                Spacer(modifier = Modifier.height(20.dp))
                Row(
                    modifier = Modifier
                        .align(Alignment.Start)
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Text(
                        "User Name",
                        color = MaterialTheme.colorScheme.onPrimary,
                        fontSize = MaterialTheme.typography.titleMedium.fontSize,
                    )

                    Spacer(modifier = Modifier.weight(1f)) // Spacer to fill the space
                    Row {
                        Button(onClick = {
                            viewModel.getCollectionHistory(TransactionSortType.USER_NAME_ASC)
                        }) {
                            Text(
                                "A - Z",
                                color = MaterialTheme.colorScheme.onPrimary,
                                fontSize = MaterialTheme.typography.titleSmall.fontSize,
                            )
                        }
                        Spacer(modifier = Modifier.width(60.dp))
                        Button(
                            onClick = {
                                viewModel.getCollectionHistory(TransactionSortType.USER_NAME_DESC)
                            }) {
                            Text(
                                "Z - A",
                                color = MaterialTheme.colorScheme.onPrimary,
                                fontSize = MaterialTheme.typography.titleSmall.fontSize,
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(20.dp))
                Row {
                    Text(
                        "Shop Name",
                        color = MaterialTheme.colorScheme.onPrimary,
                        fontSize = MaterialTheme.typography.titleMedium.fontSize,
                    )
                    Spacer(modifier = Modifier.weight(1f)) // Spacer to fill the space
                    Button(onClick = {
                        viewModel.getCollectionHistory(TransactionSortType.SHOP_NAME_ASC)
                    }) {
                        Text(
                            "A - Z",
                            color = MaterialTheme.colorScheme.onPrimary,
                            fontSize = MaterialTheme.typography.titleSmall.fontSize,
                        )
                    }
                    Spacer(modifier = Modifier.width(60.dp))
                    Button(
                        onClick = {
                            viewModel.getCollectionHistory(TransactionSortType.SHOP_NAME_DESC)
                        }) {
                        Text(
                            "Z - A",
                            color = MaterialTheme.colorScheme.onPrimary,
                            fontSize = MaterialTheme.typography.titleSmall.fontSize,
                        )
                    }
                }
                Spacer(modifier = Modifier.height(20.dp))
                Row(
                    modifier = Modifier
                        .align(Alignment.Start)
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Text(
                        "Entry Time",
                        color = MaterialTheme.colorScheme.onPrimary,
                        fontSize = MaterialTheme.typography.titleMedium.fontSize,
                    )
                    Spacer(modifier = Modifier.weight(1f)) // Spacer to fill the space
                    Button(
                        onClick = {
                            viewModel.getCollectionHistory(TransactionSortType.TIMESTAMP_ASC)
                        }) {
                        Image(
                            painter = painterResource(id = R.drawable.baseline_arrow_upward_24), // Replace with your drawable
                            contentDescription = stringResource(R.string.filter),
                            alignment = Alignment.CenterEnd,
                            contentScale = ContentScale.Crop,
                            colorFilter = ColorFilter.tint(onprimaryD)
                        )
                    }
                    Spacer(modifier = Modifier.width(60.dp))
                    Button(onClick = {
                        viewModel.getCollectionHistory(TransactionSortType.TIMESTAMP_DESC)
                    }) {
                        Image(
                            modifier = Modifier.rotate(180f),
                            painter = painterResource(id = R.drawable.baseline_arrow_upward_24), // Replace with your drawable
                            contentDescription = stringResource(R.string.filter),
                            alignment = Alignment.CenterEnd,
                            contentScale = ContentScale.Crop,
                            colorFilter = ColorFilter.tint(onprimaryD)
                        )
                    }
                }
            }
        },
        onDismissRequest = {
            onDismiss()
        },
    )
}
