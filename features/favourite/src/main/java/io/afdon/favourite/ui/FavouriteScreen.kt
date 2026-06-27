package io.afdon.favourite.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage

@Composable
fun FavouriteScreen(
    viewModel: FavouriteViewModel,
    onItemClick: (String) -> Unit
) {
    val favouriteUsers by viewModel.favouriteUsers.observeAsState(emptyList())
    val progressVisibility by viewModel.progressVisibility.collectAsState()

    Column(modifier = Modifier.fillMaxSize()) {
        if (progressVisibility == android.view.View.VISIBLE) {
            LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
        }

        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(favouriteUsers) { item ->
                FavouriteItem(
                    item = item,
                    onItemClick = { item.user.login?.let { onItemClick(it) } },
                    onDeleteClick = { viewModel.deleteFavourite(item) }
                )
            }
        }
    }
}

@Composable
fun FavouriteItem(
    item: FavouriteAdapter.Item,
    onItemClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onItemClick() }
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = item.user.avatarUrl,
                contentDescription = null,
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = item.user.login ?: "",
                modifier = Modifier.weight(1f),
                fontSize = 16.sp
            )
            IconButton(onClick = onDeleteClick) {
                Icon(
                    imageVector = Icons.Filled.Delete,
                    contentDescription = null,
                    tint = Color.Gray
                )
            }
        }
        HorizontalDivider(color = Color.LightGray, modifier = Modifier.padding(horizontal = 16.dp))
    }
}
