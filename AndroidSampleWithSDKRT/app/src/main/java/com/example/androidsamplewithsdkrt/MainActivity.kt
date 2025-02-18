package com.example.androidsamplewithsdkrt

import android.os.Bundle
import android.util.Log
import android.widget.LinearLayout
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.example.androidsamplewithsdkrt.ui.theme.AndroidSampleWithSDKRTTheme
import com.existing.sdk.BannerAd
import com.existing.sdk.ExistingSdk
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

private lateinit var bannerAd: BannerAd

class MainActivity : AppCompatActivity() {

    private val runtimeAwareSdk = ExistingSdk(this)

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        setContent {
            AndroidSampleWithSDKRTTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize() // Fill the Scaffold's content
                            .padding(innerPadding),
                        contentAlignment = Alignment.Center // Center the content
                    ) {
                        Column(
                            modifier = Modifier
                                .padding(innerPadding),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            AndroidView(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(50.dp), //adjust height as needed
                                factory = { ctx ->
                                    LinearLayout(ctx).apply {
                                        bannerAd = BannerAd(ctx) // Instantiate your BannerAd
                                        addView(bannerAd)
                                    }

                                }
                            )
//                            bannerAd = BannerAd(context = LocalContext.current)

                            BannerButton(context = this@MainActivity)

                        }
                    }
                }
            }
        }
    }
}

@Composable
fun BannerButton(context: AppCompatActivity, modifier: Modifier = Modifier) {

    val lifecycleOwner = LocalLifecycleOwner.current
    val coroutineScope = rememberCoroutineScope()
    // If you're using a ViewModel:
    // val viewModel: MyViewModel = viewModel()

    Box {
        Button(onClick = {
            CoroutineScope(Dispatchers.Main).launch {
                try {
                    Log.d("MainActivity", "Button clicked")
                    bannerAd.loadAd(
                        context, // Use the context
                        "com.example.androidsamplewithsdkrt",
                        shouldStartActivityPredicate(),
                        true,
                        "NONE",
                    )
                } catch (e: Exception) {
                    // Handle exceptions (log, show a snackbar, etc.)
                    println("Error loading banner ad: ${e.message}")
                }
            }
            Log.d("MainActivity", "After clicking button")
        }) {
            Text("Request Banner")
        }
    }

    // This is how you observe the lifecycle in compose
    androidx.compose.runtime.DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_DESTROY) {
                // Handle lifecycle event, e.g., cancel coroutines or clean up resources
                println("On Destroy triggered")
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }
}

private fun shouldStartActivityPredicate(): () -> Boolean {
    return { true }
}

