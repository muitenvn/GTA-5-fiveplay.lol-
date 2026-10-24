package com.example.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun GitHubApkGuideScreen(
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Code,
                contentDescription = "GitHub",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(36.dp)
            )
            Column {
                Text(
                    text = "GitHub Open Source & Auto Release",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "Tự động build APK và đăng lên GitHub Releases",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        // Card 1: Open Source & Repository Setup
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Icon(Icons.Default.Public, contentDescription = "Public", tint = MaterialTheme.colorScheme.primary)
                    Text(text = "1. Đẩy mã nguồn lên GitHub (Open Source)", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
                Text(
                    text = "Dự án đã được cấu hình sẵn sàng làm **Mã nguồn mở (Open Source)** để bất kỳ ai cũng có thể đóng góp hoặc tải về:\n\n" +
                            "• Tạo một Repository mới trên GitHub (chọn chế độ **Public**).\n" +
                            "• Mở terminal và chạy các lệnh sau để đẩy code lên:\n" +
                            "  `git init`\n" +
                            "  `git add .`\n" +
                            "  `git commit -m \"Initial commit: FivePlay Virtual Gamepad\"`\n" +
                            "  `git branch -M main`\n" +
                            "  `git remote add origin <URL_REPOSITORY_CUA_BAN>`\n" +
                            "  `git push -u origin main`",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }

        // Card 2: GitHub Actions Auto Release
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Icon(Icons.Default.AutoAwesome, contentDescription = "Actions", tint = MaterialTheme.colorScheme.primary)
                    Text(text = "2. Tự động đăng Release APK qua GitHub Actions", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
                Text(
                    text = "Hệ thống đã được cấu hình sẵn file **`.github/workflows/build-apk.yml`**.\n\n" +
                            "• Mỗi khi bạn đẩy code hoặc tạo một Tag mới (ví dụ: `git tag v1.0.0` và `git push origin v1.0.0`), GitHub Actions sẽ tự động:\n" +
                            "  1. Cài đặt môi trường Android SDK & Java 17.\n" +
                            "  2. Biên dịch file APK (`assembleDebug` và `assembleRelease`).\n" +
                            "  3. **Tự động tạo Release và đính kèm file APK** vào mục **Releases** trên GitHub để mọi người dễ dàng bấm tải về cài đặt!",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }

        // Card 3: FivePlay GTA V Tips
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Icon(Icons.Default.VideogameAsset, contentDescription = "Tips", tint = MaterialTheme.colorScheme.primary)
                    Text(text = "3. Trải nghiệm FivePlay Cloud Gaming", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
                Text(
                    text = "• Mở tab **FivePlay** để truy cập `fiveplay.lol`.\n" +
                            "• Bật bàn phím tay cầm ảo để chơi GTA V mượt mà trên điện thoại Android.\n" +
                            "• Chia sẻ link GitHub Open Source của bạn cho cộng đồng game thủ cùng sử dụng!",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}
