package com.example.flutter_sample_with_sdkrt

import android.content.Context
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.unit.dp
import com.existing.sdk.BannerAd
import io.flutter.embedding.android.FlutterFragmentActivity
import io.flutter.embedding.engine.FlutterEngine
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.StandardMessageCodec
import io.flutter.plugin.platform.PlatformView
import io.flutter.plugin.platform.PlatformViewFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.concurrent.atomic.AtomicInteger
import kotlin.random.Random

// Have to use the FlutterFragmentActivity instead of FlutterActivity
// because we only want to show the view on the part of the screen.
class MainActivity : FlutterFragmentActivity() {
    val composeViewIdCounter = AtomicInteger(0)
    val composeViews = mutableMapOf<Int, ComposeView>()
    var bannerAdLinearLayoutId = 0
    private lateinit var bannerAd: BannerAd

    override fun configureFlutterEngine(flutterEngine: FlutterEngine) {
        super.configureFlutterEngine(flutterEngine)

        MethodChannel(
            flutterEngine.dartExecutor.binaryMessenger,
            CHANNEL
        ).setMethodCallHandler { call, result ->
            when (call.method) {
                "getRandomNumber" -> {
                    val composeView = getRandomNumber()
                    composeViews[composeView.id] = composeView
                    result.success(composeView.id)
                }

                "loadBannerAd" -> {
                    var linearLayout = createLinearLayout(this)
                    bannerAdLinearLayoutId = linearLayout.id
                    setContentView(linearLayout)

                    bannerAd = BannerAd(this)

                    bannerAd.layoutParams = LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                    )

                    linearLayout.addView(bannerAd)

                    CoroutineScope(Dispatchers.Main).launch {

                        bannerAd.loadAd(
                            this@MainActivity as AppCompatActivity,
                            PACKAGE_NAME,
                            shouldStartActivityPredicate(),
                            false,
                            "NONE"
                        )
                        result.success(bannerAdLinearLayoutId)
                    }
                }

                else -> result.notImplemented()
            }
        }

        flutterEngine.platformViewsController.registry.registerViewFactory(
            "compose-view",
            ComposeViewFactory(this)
        )
        flutterEngine.platformViewsController.registry.registerViewFactory(
            "linear-layout-view", LinearLayoutViewFactory(this)
        )
    }

    private fun shouldStartActivityPredicate(): () -> Boolean {
        return { true }
    }

    private fun createLinearLayout(context: Context): LinearLayout {
        val linearLayout = LinearLayout(context)

        // Set layout orientation (e.g., vertical or horizontal)
        linearLayout.orientation = LinearLayout.VERTICAL // Or LinearLayout.HORIZONTAL

        // Set layout width and height
        linearLayout.layoutParams = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT, // Or a specific width
            400  // Or a specific height
        )

        // Set top margin (100 pixels)
        val topMarginPx = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP, // Use DIPs for density independence
            100f, // 100 dp
            context.resources.displayMetrics
        ).toInt()

        val params = linearLayout.layoutParams as LinearLayout.LayoutParams
        params.topMargin = topMarginPx
        linearLayout.layoutParams = params


        //  Optional: Add some styling (example)
        linearLayout.setBackgroundColor(android.graphics.Color.LTGRAY)
        linearLayout.setPadding(16, 16, 16, 16) // Example padding

        return linearLayout
    }


    private fun getRandomNumber(): ComposeView {
        val randomNumber = Random.nextInt(0, 100)
        val viewId = composeViewIdCounter.incrementAndGet()
        return ComposeView(this).apply {
            id = viewId // Set the view
            setContent {
                Box(
                    modifier = Modifier
                        .size(200.dp)
                        .background(Color.Red),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "Random Number: $randomNumber")
                }
            }
        }
    }

    companion object {
        private const val CHANNEL = "flutter.plugins.sdkretest"
        private const val PACKAGE_NAME = "com.example.flutter_sample_with_sdkrt"
    }
}


class ComposeViewFactory(private val activity: MainActivity) :
    PlatformViewFactory(StandardMessageCodec.INSTANCE) {
    override fun create(context: Context, viewId: Int, args: Any?): PlatformView {
        val id = (args as Map<String, Any?>)["id"] as Int
        val composeView = activity.composeViews[id]
            ?: throw IllegalArgumentException("No ComposeView found with id: $id")

        return ComposeViewAndroid(composeView)
    }
}

class ComposeViewAndroid(private val composeView: ComposeView) : PlatformView {
    override fun getView(): View = composeView

    override fun dispose() {}

}


class LinearLayoutViewFactory(private val activity: MainActivity) :
    PlatformViewFactory(StandardMessageCodec.INSTANCE) {
    override fun create(context: Context, viewId: Int, args: Any?): PlatformView {
        val id = (args as Map<String, Any?>)["id"] as Int
        val linearLayout = activity.findViewById<LinearLayout>(id)
        return LinearLayoutViewAndroid(linearLayout)
    }
}

class LinearLayoutViewAndroid(private val linearLayout: LinearLayout) : PlatformView {
    override fun getView(): View = linearLayout

    override fun dispose() {}
}
