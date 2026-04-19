package com.andika.newsreader.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.clickable
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.SubcomposeAsyncImage
import coil3.compose.SubcomposeAsyncImageContent
import com.andika.newsreader.model.NewsArticle

@Composable
fun NewsDetailScreen(
    article: NewsArticle,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 10.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .clickable(onClick = onBackClick)
                .padding(top = 6.dp, bottom = 14.dp),
        ) {
            Text(
                text = "<",
                style = MaterialTheme.typography.titleSmall.copy(
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                ),
                color = Color.Black,
            )

            Spacer(Modifier.width(6.dp))

            Text(
                text = "Back",
                style = MaterialTheme.typography.titleSmall.copy(
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                ),
                color = Color.Black,
            )
        }

        ArticleHeroImage(
            imageUrl = article.imageUrl,
            title = article.title,
            modifier = Modifier
                .fillMaxWidth()
                .height(220.dp),
        )

        Text(
            text = article.title,
            style = MaterialTheme.typography.headlineSmall.copy(
                fontSize = 30.sp,
                lineHeight = 38.sp,
            ),
            fontWeight = FontWeight.Bold,
            color = Color.Black,
        )

        Text(
            text = "by ${article.author}",
            style = MaterialTheme.typography.labelLarge.copy(
                fontSize = 15.sp,
            ),
            color = Color.Black,
        )

        article.articleUrl?.let {
            Text(
                text = it,
                style = MaterialTheme.typography.bodySmall.copy(
                    fontSize = 14.sp,
                ),
                color = Color(0xFF1565C0),
            )
        }

        Text(
            text = article.description,
            style = MaterialTheme.typography.bodyLarge.copy(
                fontSize = 18.sp,
                lineHeight = 27.sp,
            ),
            color = Color.Black,
        )

        Spacer(Modifier.height(6.dp))
    }
}

@Composable
private fun ArticleHeroImage(
    imageUrl: String?,
    title: String,
    modifier: Modifier = Modifier,
) {
    val shape = RoundedCornerShape(10.dp)

    if (imageUrl.isNullOrBlank()) {
        DetailImagePlaceholder(
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
            DetailImagePlaceholder(
                modifier = Modifier.fillMaxSize(),
                shape = shape,
                text = "Loading image...",
            )
        },
        error = {
            DetailImagePlaceholder(
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
private fun DetailImagePlaceholder(
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
