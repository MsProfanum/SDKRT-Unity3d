import 'package:flutter/foundation.dart';
import 'package:flutter/gestures.dart';
import 'package:flutter/material.dart';
import 'package:flutter/rendering.dart';
import 'package:flutter/services.dart';

class SdkMethodChannel {
  final String _CHANNEL = "flutter.plugins.sdkretest";
  late final MethodChannel platform;

  SdkMethodChannel() {
    platform = MethodChannel(_CHANNEL);
  }

  Future<Widget> getRandomNumber() async {
    Widget randomNumberWidget = SizedBox.shrink();

    try {
      final int viewId = await platform.invokeMethod("getRandomNumber");
      randomNumberWidget = SizedBox(
        width: 200,
        height: 200,
        child: PlatformViewLink(
          viewType: 'compose-view',
          surfaceFactory: (
            BuildContext context,
            PlatformViewController controller,
          ) {
            return AndroidViewSurface(
              controller: controller as AndroidViewController,
              gestureRecognizers:
                  const <Factory<OneSequenceGestureRecognizer>>{},
              hitTestBehavior: PlatformViewHitTestBehavior.opaque,
            );
          },
          onCreatePlatformView: (PlatformViewCreationParams params) {
            return PlatformViewsService.initSurfaceAndroidView(
                id: params.id,
                viewType: 'compose-view',
                layoutDirection: TextDirection.ltr,
                creationParams: {'id': viewId},
                creationParamsCodec: const StandardMessageCodec(),
              )
              ..addOnPlatformViewCreatedListener(params.onPlatformViewCreated)
              ..create();
          },
        ),
      );
    } on PlatformException catch (e) {
      print('Failed to get random number: ${e.message}');
    }

    return randomNumberWidget;
  }

  Future<Widget> loadBannerAd() async {
    Widget loadBannerWidget = SizedBox.shrink();

    try {
      final int viewId = await platform.invokeMethod("loadBannerAd");
      loadBannerWidget = SizedBox(
        width: 200,
        height: 200,
        child: AndroidView(
          viewType: "linear-layout-view",
          creationParams: {'id': viewId},
          creationParamsCodec: StandardMessageCodec(),
        ),
      );
    } on PlatformException catch (e) {
      print('Failed to load a banner ad: ${e.message}');
    }

    return loadBannerWidget;
  }
}
