import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import com.awesome.home.FeedRepository
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map

@Composable
fun FeedScreen(
    modifier: Modifier = Modifier,
    feedItemList: List<FeedRepository.FeedItem>,
    onLoadMore: () -> Unit
) {
    Scaffold { paddingValues ->
        FeedItemList(
            modifier = modifier
                .padding(paddingValues)
                .background(color = Color.Blue)
                .padding(4.dp),
            feedItemList = feedItemList,
            onLoadMore = onLoadMore
        )
    }
}


@Composable
fun FeedItemList(
    modifier: Modifier = Modifier,
    feedItemList: List<FeedRepository.FeedItem>,
    onLoadMore: () -> Unit
) {
    val listState = rememberLazyListState()

    LazyColumn(
        modifier = modifier,
        state = listState
    ) {
        items(feedItemList) { pokemon ->
            FeedItem(
                modifier = Modifier
                    .padding(4.dp)
                    .fillMaxWidth(),
                feedItem = pokemon
            )
        }
    }

    LaunchedEffect(listState) {
        snapshotFlow { listState.layoutInfo.visibleItemsInfo }
            .map { visibleItems ->
                val lastVisibleItem = visibleItems.lastOrNull()?.index ?: 0
                val totalItems = listState.layoutInfo.totalItemsCount
                lastVisibleItem >= totalItems - 3
            }
            .distinctUntilChanged()
            .collect { shouldLoadMore ->
                if (shouldLoadMore) {
                    onLoadMore()
                }
            }
    }
}


@Composable
fun FeedItem(modifier: Modifier, feedItem: FeedRepository.FeedItem) {
    Card(modifier = modifier, content = {
        Row(verticalAlignment = Alignment.CenterVertically) {
            AsyncImage(
                model = "https://picsum.photos/seed/${(0..100).random()}/200/200",
                contentDescription = "Feed image",
                placeholder = ColorPainter(MaterialTheme.colorScheme.surfaceVariant),
                error = ColorPainter(MaterialTheme.colorScheme.errorContainer),
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(64.dp)
            )
            Text(
                text = feedItem.title,
                Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                color = Color.White
            )
        }
    }, colors = CardDefaults.cardColors(containerColor = Color.Red))

}



@Preview
@Composable
fun FeedItemListPreview() {
    val feedItemList = feedRepositoryFeedItems()
    FeedItemList(
        feedItemList = feedItemList,
        onLoadMore = {},
    )
}

@Preview
@Composable
fun FeedScreenPreview() {
    val feedItemList = feedRepositoryFeedItems()
    FeedScreen(
        feedItemList = feedItemList,
        onLoadMore = {},
    )
}

@Preview
@Composable
fun FeedStemPreview() {
    val feedItem = feedRepositoryFeedItems().first()
    FeedItem(Modifier.fillMaxWidth(), feedItem = feedItem)
}

private fun feedRepositoryFeedItems(): MutableList<FeedRepository.FeedItem> {
    val feedItemList = mutableListOf<FeedRepository.FeedItem>()
    for (i in 0..15) {
        feedItemList.add(
            FeedRepository.FeedItem(
                i,
                "Feed $i",
                "https://feedapi.co/api/v2/feed/$i"
            )
        )
    }
    return feedItemList
}

