package com.andika.newsreader.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.SubcomposeAsyncImage
import coil3.compose.SubcomposeAsyncImageContent
import com.andika.newsreader.model.NewsArticle

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewsListScreen(
    articles: List<NewsArticle>,
    isRefreshing: Boolean,
    onRefresh: () -> Unit,
    onArticleClick: (NewsArticle) -> Unit,
    modifier: Modifier = Modifier,
) {
    PullToRefreshBox(
        isRefreshing = isRefreshing,
        onRefresh = onRefresh,
        modifier = modifier,
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            item("studentInfo") {
                Text(
                    text = "Nama: Andika Dinata\nNIM: 123140096\nKelas: RB",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontSize = 18.sp,
                        lineHeight = 27.sp,
                        fontWeight = FontWeight.Medium,
                    ),
                    color = Color.Black,
                    modifier = Modifier.padding(top = 6.dp, bottom = 14.dp),
                )
            }

            items(articles, key = { it.id }) { article ->
                ArticleCard(
                    article = article,
                    onClick = { onArticleClick(article) },
                )
            }
        }
    }
}

@Composable
private fun ArticleCard(
    article: NewsArticle,
    onClick: () -> Unit,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(14.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White,
        ),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
        ) {
            ArticleImage(
                imageUrl = article.imageUrl,
                title = article.title,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp),
            )

            Spacer(Modifier.height(10.dp))

            Text(
                text = article.title,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontSize = 22.sp,
                    lineHeight = 28.sp,
                ),
                fontWeight = FontWeight.SemiBold,
                color = Color.Black,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )

            Spacer(Modifier.height(6.dp))

            Text(
                text = article.description,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontSize = 16.sp,
                    lineHeight = 22.sp,
                ),
                color = Color.Black,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis,
            )

            Spacer(Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(
                    text = "by ${article.author}",
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontSize = 14.sp,
                    ),
                    color = Color.Black,
                )
            }
        }
    }
}

@Composable
private fun ArticleImage(
    imageUrl: String?,
    title: String,
    modifier: Modifier = Modifier,
) {
    val shape = RoundedCornerShape(10.dp)

    if (imageUrl.isNullOrBlank()) {
        ImagePlaceholder(
            modifier = modifier,
            shape = shape,
        )
        return
    }

    SubcomposeAsyncImage(
        model = imageUrl,
        contentDescription = title,
        modifier = modifier.clip(shape),
        contentScale = ContentScale.Crop,
        loading = {
            ImagePlaceholder(
                modifier = Modifier.fillMaxSize(),
                shape = shape,
                text = "Loading image...",
            )
        },
        error = {
            ImagePlaceholder(
                modifier = Modifier.fillMaxSize(),
                shape = shape,
            )
        },
        success = {
            SubcomposeAsyncImageContent()
        },
    )
}

@Composable
private fun ImagePlaceholder(
    modifier: Modifier = Modifier,
    shape: RoundedCornerShape,
    text: String = "No image",
) {
    Box(
        modifier = modifier
            .clip(shape)
            .background(Color(0xFFE3E8E5)),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodySmall.copy(
                fontSize = 12.sp,
            ),
            color = Color(0xFF5F6368),
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 12.dp),
        )
    }
}
