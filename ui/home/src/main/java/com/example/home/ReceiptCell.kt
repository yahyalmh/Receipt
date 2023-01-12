package com.example.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.data.common.model.dto.Photo
import com.example.data.common.model.dto.ReceiptModel
import com.example.ui.common.R
import com.example.ui.common.component.icon.AppIcons
import com.google.accompanist.placeholder.PlaceholderHighlight
import com.google.accompanist.placeholder.placeholder
import com.google.accompanist.placeholder.shimmer

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReceiptCell(
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
    receiptModel: ReceiptModel,
    leadingIcon: ImageVector? = null,
    onLeadingIconClick: (ReceiptModel) -> Unit
) {
    var isImageLoading by remember { mutableStateOf(true) }
    Card(
        modifier = modifier
            .height(IntrinsicSize.Max)
            .fillMaxWidth()
            .padding(8.dp),
        onClick = onClick,
        shape = RoundedCornerShape(15)
    ) {
        Row {
            AsyncImage(
                modifier = modifier
                    .size(82.dp)
                    .padding(5.dp)
                    .placeholder(
                        visible = isImageLoading,
                        color = Color.LightGray,
                        highlight = PlaceholderHighlight.shimmer(Color.DarkGray),
                        shape = RoundedCornerShape(15),
                    )
                    .clip(RoundedCornerShape(15)),
                model = receiptModel.photo?.remoteUri,
                contentDescription = "receipt image",
                contentScale = ContentScale.FillBounds,
                onSuccess = { isImageLoading = false }
            )

            Column(
                modifier = modifier
                    .padding(start = 8.dp)
                    .weight(1f)
                    .fillMaxHeight(),
                verticalArrangement = Arrangement.SpaceEvenly,
                horizontalAlignment = Alignment.Start
            ) {
                Text(text = "Language: ${receiptModel.languageId}")
                Text(text = "Captured At: ${receiptModel.photo?.timestamp}")
            }
            Column {
                if (leadingIcon != null) {
                    FilledTonalIconButton(
                        modifier = Modifier
                            .padding(2.dp)
                            .size(28.dp),
                        onClick = { onLeadingIconClick(receiptModel) },
                    ) {
                        Icon(
                            modifier = Modifier.padding(4.dp),
                            imageVector = leadingIcon,
                            contentDescription = stringResource(id = R.string.favoriteIconDescription),
                            tint = Color.Red
                        )
                    }
                }
                Spacer(modifier = Modifier.weight(1f))
            }

        }

    }
}

@Composable
fun ReceiptShimmerCell(
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier
            .height(IntrinsicSize.Max)
            .fillMaxWidth()
            .padding(8.dp),
        shape = RoundedCornerShape(15)
    ) {
        Row {
            Box(
                modifier = modifier
                    .size(82.dp)
                    .placeholder(
                        visible = true,
                        color = Color.LightGray,
                        highlight = PlaceholderHighlight.shimmer(Color.DarkGray),
                    ),
            )
            Column(
                modifier = modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .padding(start = 12.dp),
                verticalArrangement = Arrangement.SpaceEvenly,
                horizontalAlignment = Alignment.Start
            ) {
                Box(
                    modifier = modifier
                        .size(150.dp, 12.dp)
                        .clip(CircleShape)
                        .placeholder(
                            visible = true,
                            color = Color.LightGray,
                            highlight = PlaceholderHighlight.shimmer(Color.DarkGray)
                        ),
                )
                Box(
                    modifier = modifier
                        .size(100.dp, 10.dp)
                        .clip(CircleShape)
                        .placeholder(
                            visible = true,
                            color = Color.LightGray,
                            highlight = PlaceholderHighlight.shimmer(Color.DarkGray)
                        ),
                )
                Box(
                    modifier = modifier
                        .size(70.dp, 10.dp)
                        .clip(CircleShape)
                        .placeholder(
                            visible = true,
                            color = Color.LightGray,
                            highlight = PlaceholderHighlight.shimmer(Color.DarkGray)
                        ),
                )
            }
            Spacer(modifier = Modifier.weight(1f))
            Column {
                Icon(
                    modifier = Modifier
                        .padding(8.dp)
                        .size(24.dp),
                    imageVector = Icons.Default.FavoriteBorder,
                    contentDescription = null,
                    tint = Color.LightGray
                )
                Spacer(modifier = Modifier.weight(1f))
            }
        }
    }
}


@Composable
fun ReceiptModel.toCell(
    favoritesReceipts: List<ReceiptModel>,
    navigateToDetail: (String) -> Unit,
    onFavoriteClick: (ReceiptModel) -> Unit
): @Composable () -> Unit = {
    val leadingIcon = if (favoritesReceipts.any { it.id == this.id }) {
        AppIcons.Favorite
    } else {
        AppIcons.FavoriteBorder
    }
    ReceiptCell(
        receiptModel = this,
        leadingIcon = leadingIcon,
        onClick = { navigateToDetail(this.id) },
        onLeadingIconClick = onFavoriteClick
    )
}

@Composable
@Preview
fun ReceiptCellPreview() {
    ReceiptCell(
        onClick = {},
        receiptModel = ReceiptModel(
            languageId = "English",
            text = "Text",
            translate = "Translate",
            photo = Photo()
        ),
        leadingIcon = AppIcons.Favorite
    ) {}
}

@Composable
@Preview
fun ShimmerPreview() {
    ReceiptShimmerCell()
}