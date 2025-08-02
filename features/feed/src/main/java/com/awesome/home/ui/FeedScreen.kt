import android.R.attr.end
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.awesome.home.presentation.FeedViewModel.FeedDetail
import com.awesome.home.presentation.FeedViewModel.FeedItem
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged

@Composable
fun FeedScreen(
    modifier: Modifier = Modifier,
    feedItemList: List<FeedItem>,
    onLoadVisibleItems: (end: Int) -> Unit,
) {
    Scaffold(
        modifier = modifier
    ) { paddingValues ->
        FeedItemList(
            modifier = Modifier.padding(paddingValues),
            feedItemList = feedItemList,
            onLoadVisibleItems = onLoadVisibleItems
        )
    }
}

@Composable
fun FeedItemList(
    modifier: Modifier = Modifier,
    feedItemList: List<FeedItem>,
    onLoadVisibleItems: (end: Int) -> Unit
) {
    val listState = rememberLazyListState()

    LazyColumn(
        modifier = modifier,
        state = listState
    ) {
        itemsIndexed(feedItemList, key = { _, item -> item.id }) { index, feedItem ->
            FeedItem(
                modifier = Modifier
                    .padding(4.dp)
                    .fillMaxWidth(),
                feedItem = feedItem
            )
        }
    }

    LaunchedEffect(listState) {
        snapshotFlow {
            listState.isScrollInProgress to listState.layoutInfo.visibleItemsInfo
        }
            .distinctUntilChanged()
            .debounce(300)
            .collectLatest { (isScrolling, visibleItems) ->
                if (!isScrolling && visibleItems.isNotEmpty()) {
                    val firstVisible = visibleItems.first().index
                    visibleItems.last().index
                    onLoadVisibleItems(firstVisible)
                }
            }
    }
}

@Composable
fun FeedItem(modifier: Modifier, feedItem: FeedItem) {
    Card(modifier = modifier, content = {
        Row(verticalAlignment = Alignment.CenterVertically) {
            AsyncImage(
                model = feedItem.detail?.url,
                contentDescription = "Feed image",
                placeholder = ColorPainter(MaterialTheme.colorScheme.surfaceVariant),
                error = ColorPainter(MaterialTheme.colorScheme.errorContainer),
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(64.dp)
            )
            Text(
                text = "${feedItem.id} ${feedItem.detail?.name ?: ""}",
                Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                color = Color.Black
            )
        }
    }, colors = CardDefaults.cardColors(containerColor = Color.White))

}

@Preview
@Composable
fun FeedItemListPreview() {
    val feedItemList = feedItems()
    FeedItemList(
        feedItemList = feedItemList,
        onLoadVisibleItems = { end },
    )
}

@Preview
@Composable
fun FeedScreenPreview() {
    val feedItemList = feedItems()
    FeedScreen(
        feedItemList = feedItemList,
        onLoadVisibleItems = { end }
    )
}

@Preview
@Composable
fun FeedItemPreview() {
    val feedItem = feedItems().first()
    FeedItem(Modifier.fillMaxWidth(), feedItem = feedItem)
}

private fun feedItems(): MutableList<FeedItem> {
    val feedItemList = mutableListOf<FeedItem>()
    for (i in 0..15) {
        feedItemList.add(
            FeedItem(
                i,
                FeedDetail(i.toString(), "https://feedapi.co/api/v2/feed/$i")
            )
        )
    }
    return feedItemList
}
