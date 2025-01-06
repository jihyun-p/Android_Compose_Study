package com.jihyun.compose_study

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import coil.compose.AsyncImage
import com.jihyun.compose_study.viewmodel.MediaViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val mediaViewModel: MediaViewModel by viewModels()

        setContent {
            MediaListScreen(mediaViewModel)
        }
    }
}

@Composable
fun MediaListScreen(mediaViewModel: MediaViewModel) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 16.dp)
    ) {
        // 검색 바 추가
        SearchBar { query ->
            mediaViewModel.fetchMedia(query) // 이미지 검색
            mediaViewModel.fetchVideos(query) // 동영상 검색
        }

        // 이미지와 동영상 데이터를 관찰
        val imageItems by mediaViewModel.imageItems.collectAsState()
        val videoItems by mediaViewModel.videoItems.collectAsState()

        // 로딩 중 상태 처리
        if (imageItems.isNullOrEmpty() && videoItems.isNullOrEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "로딩 중입니다...")
            }
        } else {
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                // 이미지 출력
                imageItems?.let { items ->
                    items(items.size) { index ->
                        val item = items[index]
                        MediaItemView(
                            thumbnailUrl = item.thumbnail_url,
                            displaySite = item.display_sitename ?: "출처 없음"
                        )
                    }
                }

                // 동영상 출력
                videoItems?.let { items ->
                    items(items.size) { index ->
                        val item = items[index]
                        if (!item.url.isNullOrEmpty()) {
                            VideoItemView(
                                videoUrl = item.url,
                                videoTitle = item.title ?: item.author ?: "출처 없음"
                            )
                        } else {
                            Text(
                                text = "유효한 동영상 URL이 없습니다.",
                                modifier = Modifier.padding(8.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MediaItemView(thumbnailUrl: String, displaySite: String) {
    Row(modifier = Modifier.padding(8.dp)) {
        AsyncImage(
            model = thumbnailUrl,
            contentDescription = null,
            modifier = Modifier.size(100.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = displaySite)
    }
}

@Composable
fun VideoItemView(videoUrl: String?, videoTitle: String?) {
    Column(modifier = Modifier.padding(8.dp)) {
        val context = LocalContext.current
        val title = videoTitle ?: "제목 없음" // null 처리
        var isPlaying by remember { mutableStateOf(false) } // 재생 상태 관리

        if (!videoUrl.isNullOrEmpty()) {
            // YouTubePlayer 초기화
            AndroidView(
                factory = { context ->
                    com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView(context).apply {
                        addYouTubePlayerListener(object : com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener() {
                            override fun onReady(youTubePlayer: com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer) {
                                val videoId = videoUrl.substringAfter("v=") // YouTube 동영상 ID 추출
                                youTubePlayer.cueVideo(videoId, 0f) // 동영상을 준비 상태로 설정

                            }
                        })
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
            )

        } else {
            Text("유효한 동영상 URL이 없습니다.")
        }

        Spacer(modifier = Modifier.height(8.dp))
        Text(text = title)
    }
}

@Composable
fun SearchBar(onSearch: (String) -> Unit) {
    var query by remember { mutableStateOf("") }

    Row(modifier = Modifier.padding(8.dp)) {
        TextField(
            value = query,
            onValueChange = { query = it },
            label = { Text("검색어를 입력하세요") },
            modifier = Modifier.weight(1f)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Button(onClick = { onSearch(query) }) {
            Text("검색")
        }
    }
}
