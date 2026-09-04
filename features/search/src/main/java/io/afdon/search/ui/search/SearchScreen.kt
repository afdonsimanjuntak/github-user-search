package io.afdon.search.ui.search

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import io.afdon.search.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    viewModel: SearchViewModel,
    onItemClick: (String) -> Unit,
    onFavouriteClick: (SearchResultAdapter.Item) -> Unit,
    onRefresh: () -> Unit,
    onScrollLoadMore: () -> Unit
) {
    val query by viewModel.queryLiveData.observeAsState("")
    val searchResult by viewModel.searchResultItems.observeAsState()
    val progressVisibility by viewModel.progressVisibility.observeAsState(android.view.View.GONE)
    val isRefreshing by viewModel.isSwipeRefreshing.observeAsState(false)
    
    val focusManager = LocalFocusManager.current
    val listState = rememberLazyListState()

    val items = searchResult?.getItems() ?: emptyList()

    LaunchedEffect(searchResult) {
        if (searchResult?.isNewSearch() == true) {
            listState.scrollToItem(0)
        }
    }

    Scaffold(
        topBar = {
            Column {
                TextField(
                    value = query ?: "",
                    onValueChange = { 
                        viewModel.queryLiveData.value = it
                        viewModel.newSearch()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    placeholder = { Text(stringResource(R.string.search_hint)) },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                    keyboardActions = KeyboardActions(onSearch = { focusManager.clearFocus() }),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent
                    )
                )
                if (progressVisibility == android.view.View.VISIBLE) {
                    LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                }
                HorizontalDivider(color = Color.LightGray)
            }
        }
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues)) {
            // SwipeRefresh equivalent in Material3 is PullToRefreshBox (experimental)
            // or we can use a custom implementation. For minimal changes, let's focus on the content.
            // Since we want minimal changes, we can just use a Column if we don't have pull-to-refresh library ready.
            // But let's use a simple Box for now.
            
            LazyColumn(
                state = listState,
                modifier = Modifier.fillMaxSize()
            ) {
                itemsIndexed(items) { index, item ->
                    if (item.type == R.layout.item_search_result) {
                        SearchResultItem(
                            item = item,
                            onItemClick = { item.user?.login?.let { onItemClick(it) } },
                            onFavouriteClick = { onFavouriteClick(item) }
                        )
                    } else if (item.type == io.afdon.core.R.layout.item_recyclerview_loading) {
                        Box(modifier = Modifier.fillMaxWidth().padding(16.dp), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator()
                        }
                    } else if (item.type == io.afdon.core.R.layout.item_recyclerview_load_more) {
                        Button(
                            onClick = { viewModel.onButtonLoadMore() },
                            modifier = Modifier.fillMaxWidth().padding(16.dp)
                        ) {
                            Text("Load More")
                        }
                    }

                    // Simple load more logic
                    if (index == items.size - 1 && items.size >= SearchViewModel.THRESHOLD_LOAD_NEXT) {
                        onScrollLoadMore()
                    }
                }
            }
        }
    }
}

@Composable
fun SearchResultItem(
    item: SearchResultAdapter.Item,
    onItemClick: () -> Unit,
    onFavouriteClick: () -> Unit
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
                model = item.user?.avatarUrl,
                contentDescription = null,
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = item.user?.login ?: "",
                modifier = Modifier.weight(1f),
                fontSize = 16.sp
            )
            IconButton(onClick = onFavouriteClick) {
                Icon(
                    imageVector = if (item.isFavourite) Icons.Filled.Star else Icons.Outlined.Star,
                    contentDescription = null,
                    tint = if (item.isFavourite) Color.Yellow else Color.Gray
                )
            }
        }
        HorizontalDivider(color = Color.LightGray, modifier = Modifier.padding(horizontal = 16.dp))
    }
}

private val Alignment.Companion.CenterVertations: Alignment.Vertical
    get() = Alignment.CenterVertically
