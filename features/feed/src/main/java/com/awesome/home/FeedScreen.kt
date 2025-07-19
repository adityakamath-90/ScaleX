import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.awesome.home.FeedRepository

@Composable
fun FeedScreen(
    modifier: Modifier = Modifier,
    feedItemList: List<FeedRepository.FeedItem>
) {
    Scaffold(
        modifier = modifier.fillMaxSize()
    ) { paddingValues ->
        FeedItemList(
            modifier = Modifier.padding(paddingValues),
            feedItemList = feedItemList
        )
    }
}


@Composable
fun FeedItem(modifier: Modifier, feedItem: FeedRepository.FeedItem) {
    Text(text = feedItem.name, modifier.fillMaxSize())
}


@Composable
fun FeedItemList(modifier: Modifier = Modifier, feedItemList: List<FeedRepository.FeedItem>) {
    LazyColumn(modifier = modifier) {
        items(feedItemList) { pokemon ->
            FeedItem(modifier = modifier.wrapContentSize(), feedItem = pokemon)
        }
    }
}

@Preview
@Composable
fun FeedItemListPreview() {
    val feedItemList = mutableListOf<FeedRepository.FeedItem>()
    for (i in 0..15) {
        feedItemList.add(
            FeedRepository.FeedItem(
                "Pokemon $i",
                "https://pokeapi.co/api/v2/pokemon/$i"
            )
        )
    }
    FeedItemList(feedItemList = feedItemList)
}

