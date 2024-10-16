package com.example.notification_channel

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import io.flutter.embedding.android.FlutterActivity
import io.flutter.embedding.engine.FlutterEngine
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugins.GeneratedPluginRegistrant

import com.example.notification_channel.R

import androidx.media.app.NotificationCompat.MediaStyle

import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.MediaMetadataCompat

import android.graphics.Bitmap

import android.content.res.Configuration


class MainActivity : FlutterActivity() {
    private val CHANNEL = "flutter.native/helper"
    private lateinit var mediaSession: MediaSessionCompat

    override fun configureFlutterEngine(flutterEngine: FlutterEngine) {
        super.configureFlutterEngine(flutterEngine)
        GeneratedPluginRegistrant.registerWith(flutterEngine)

        mediaSession = MediaSessionCompat(this, "MediaSession")

        MethodChannel(flutterEngine.dartExecutor.binaryMessenger, CHANNEL).setMethodCallHandler { call, result ->
            when (call.method) {
                "showNotificationFromNative" -> {
                    val title = call.argument<String>("title") ?: "Empty"
                    val message = call.argument<String>("message") ?: "Empty"
                    val greetings = showSimpleNotification(title, message)
                    result.success(greetings)
                }
                "showChargingNotificationMethod" -> {
                    val args = call.arguments as? Map<String, Any>
                    if (args != null) {
                        val greetings = showChargingNotification(args)
                        result.success(greetings)
                    } else {
                        result.error("INVALID_ARGUMENTS", "Arguments must be a Map<String, Any>", null)
                    }
                }

                "showBikeMovementNotificationMethod" -> {
                    val args = call.arguments as? Map<String, Any>
                    if (args != null) {
                        val greetings = showBikeMovementNotification(args)
                        result.success(greetings)
                    } else {
                        result.error("INVALID_ARGUMENTS", "Arguments must be a Map<String, Any>", null)
                    }
                }

                "showBikeStatsNotificationMethod" -> {
                    val args = call.arguments as? Map<String, Any>
                    if (args != null) {
                        val greetings = showBikeStatsNotification(args)
                        result.success(greetings)
                    } else {
                        result.error("INVALID_ARGUMENTS", "Arguments must be a Map<String, Any>", null)
                    }
                }

                "showMediaNotification" -> {
                    val title = call.argument<String>("title") ?: ""
                    val artist = call.argument<String>("artist") ?: ""
                    val album = call.argument<String>("album") ?: ""
                    val duration = call.argument<Int>("duration") ?: 0
                    val position = call.argument<Int>("position") ?: 0
                    showMediaNotification(title, artist, album, duration, position)
                    result.success(null)
                }
                else -> {
                    result.notImplemented()
                }
            }
        }
    }

    private fun showSimpleNotification(title: String, message: String): String {
        val notificationManager: NotificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channelId = "i.apps.notifications"
        val description = "Simple Notification"

        val builder: NotificationCompat.Builder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(channelId, description, NotificationManager.IMPORTANCE_HIGH)
            notificationChannel.enableLights(true)
            notificationChannel.lightColor = Color.GREEN
            notificationChannel.enableVibration(false)
            notificationManager.createNotificationChannel(notificationChannel)
            NotificationCompat.Builder(this, channelId)
        } else {
            NotificationCompat.Builder(this)
        }

        builder.setContentTitle(title)
                .setContentText(message)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setLargeIcon(BitmapFactory.decodeResource(this.resources, R.mipmap.ic_launcher))

        notificationManager.notify(1234, builder.build())

        return "Success"
    }

    // Type 2 and Type 3 Notification

    private fun showChargingNotification(args: Map<String, Any>): String {
    val notificationManager: NotificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    val channelId = "i.apps.custom.notification"
    val notificationId = args["notification_id"] as Int
    val description = "Custom Charging Notification"
    val isFirstUpdate = args["is_first_update"] as Boolean
    val isStillCharging = args["isStillCharging"] as Boolean
    val bikeName = args["bike_name"] as String

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && isFirstUpdate) {
        val notificationChannel = NotificationChannel(channelId, description, NotificationManager.IMPORTANCE_HIGH)
        notificationChannel.enableVibration(false)
        notificationManager.createNotificationChannel(notificationChannel)
    }

    val isDarkTheme = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES
    val smallIcon = if (isDarkTheme) R.drawable.bolt_icon_dark else R.drawable.bolt_icon_light

    val isType2 = args["isType2"] as Boolean

    val builder = NotificationCompat.Builder(this, channelId)
        .setSmallIcon(smallIcon)
        .setOngoing(isStillCharging)
        .setPriority(NotificationCompat.PRIORITY_LOW)
        .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)

    val maxProgress = args["estimate_percentage"].toString().toInt()
    val currentProgress = args["current_percentage"].toString().toInt()

    val expandedLayout = getTypeofChargerLayout(true, isDarkTheme, isType2)
    
    expandedLayout.setTextViewText(R.id.charging_title, "$bikeName")
    expandedLayout.setTextViewText(R.id.charging_estimate, "${args["estimate_percentage"]}% by ${args["estimate_time"]}")
    expandedLayout.setProgressBar(R.id.charging_progress, maxProgress, currentProgress, false)
    expandedLayout.setTextViewText(R.id.charging_percentage, "${args["current_percentage"]}")   

    val collapsedLayout = getTypeofChargerLayout(false, isDarkTheme, isType2)
    
    collapsedLayout.setTextViewText(R.id.charging_title, "Charging $bikeName")
    collapsedLayout.setProgressBar(R.id.charging_progress, maxProgress, currentProgress, false)
    collapsedLayout.setTextViewText(R.id.charging_percentage, "${args["current_percentage"]}/${args["estimate_percentage"]}%")
    
    builder.setCustomContentView(collapsedLayout)
    builder.setCustomBigContentView(expandedLayout)
    builder.setStyle(NotificationCompat.DecoratedCustomViewStyle())

    builder.setColor(Color.WHITE)
    builder.setColorized(true)

    notificationManager.notify(notificationId, builder.build())
    return "Success"
}

    private fun getTypeofChargerLayout(isExpandedView: Boolean, isDarkTheme: Boolean, isType2: Boolean): RemoteViews {
    return RemoteViews(packageName, when {
        isType2 -> when {
            isDarkTheme && isExpandedView -> R.layout.type2_dark_notification_expanded
            !isDarkTheme && isExpandedView -> R.layout.type2_light_notification_expanded
            isDarkTheme && !isExpandedView -> R.layout.type2_notification_collapsed
            else -> R.layout.type2_notification_collapsed
        }
        else -> when {
            isDarkTheme && isExpandedView -> R.layout.type3_dark_notification_expanded
            !isDarkTheme && isExpandedView -> R.layout.type3_light_notification_expanded
            isDarkTheme && !isExpandedView -> R.layout.type3_notification_collapsed
            else -> R.layout.type3_notification_collapsed
        }
    })
}

    // Show bike Movement Detected Notification
    private fun showBikeMovementNotification(args: Map<String, Any>): String {
    val notificationManager: NotificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    val channelId = "i.apps.custom.notification"
    val description = "Bike Movement Notification"
    val isFirstUpdate = args["is_first_update"] as Boolean
    val isMoving = args["is_moving"] as Boolean
    val bikeName = args["bike_name"] as String

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && isFirstUpdate) {
        val notificationChannel = NotificationChannel(channelId, description, NotificationManager.IMPORTANCE_HIGH)
        notificationChannel.enableVibration(false)
        notificationManager.createNotificationChannel(notificationChannel)
    }

    val isDarkTheme = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES
    val smallIcon = if (isDarkTheme) R.drawable.bolt_icon_dark else R.drawable.bolt_icon_light

    val builder = NotificationCompat.Builder(this, channelId)
        .setSmallIcon(smallIcon)
        .setContentTitle("$bikeName Movement Detected") // Simple title for collapsed view
        .setOngoing(isMoving)
        .setPriority(NotificationCompat.PRIORITY_LOW)
        .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)

    // Expanded layout
    val expandedLayout = getBikeMovementLayout(isDarkTheme)

    builder.setCustomBigContentView(expandedLayout)
    builder.setStyle(NotificationCompat.DecoratedCustomViewStyle())
    val BIKE_MOVEMENT_NOTIFICATION_ID = 3333
    notificationManager.notify(BIKE_MOVEMENT_NOTIFICATION_ID, builder.build())
    return "Success"
}

    private fun getBikeMovementLayout(isDarkTheme: Boolean): RemoteViews {
    return RemoteViews(packageName, if (isDarkTheme) R.layout.movement_detection_expanded_dark else R.layout.movement_detection_expanded_light)
}

    // Show Bike Stats Notification
    private fun showBikeStatsNotification(args: Map<String, Any>): String {
    val notificationManager: NotificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    val channelId = "i.apps.custom.notification"
    val description = "Bike Movement Notification"
    val bikeName = args["bike_name"] as String

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val notificationChannel = NotificationChannel(channelId, description, NotificationManager.IMPORTANCE_HIGH)
        notificationChannel.enableVibration(false)
        notificationManager.createNotificationChannel(notificationChannel)
    }

    val isDarkTheme = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES
    val smallIcon = if (isDarkTheme) R.drawable.bolt_icon_dark else R.drawable.bolt_icon_light

    val builder = NotificationCompat.Builder(this, channelId)
        .setSmallIcon(smallIcon)
        .setContentTitle("$bikeName Stats Updated") // Simple title for collapsed view
        .setOngoing(false)
        .setPriority(NotificationCompat.PRIORITY_LOW)
        .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)

    // Expanded layout
    val expandedLayout = getBikeStatsLayout(isDarkTheme)
    expandedLayout.setTextViewText(R.id.charge_percentage, "${args["current_percentage"]}")
    expandedLayout.setTextViewText(R.id.distance_travelled, "${args["total_distance"]}")

    builder.setCustomBigContentView(expandedLayout)
    builder.setStyle(NotificationCompat.DecoratedCustomViewStyle())
    val BIKE_MOVEMENT_NOTIFICATION_ID = 4444
    notificationManager.notify(BIKE_MOVEMENT_NOTIFICATION_ID, builder.build())
    return "Success"
}

    private fun getBikeStatsLayout(isDarkTheme: Boolean): RemoteViews {
    return RemoteViews(packageName, if (isDarkTheme) R.layout.bike_stats_dark else R.layout.bike_stats_light)
}

    // show Media Notification
    private fun showMediaNotification(title: String, artist: String, album: String, duration: Int, position: Int) {
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channelId = "media_playback_channel"

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId, "Media Playback", NotificationManager.IMPORTANCE_LOW)
            notificationManager.createNotificationChannel(channel)
        }

        val mediaStyle = MediaStyle()
            .setMediaSession(mediaSession.sessionToken)
            .setShowActionsInCompactView(0, 1, 2)

        val albumArt: Bitmap = BitmapFactory.decodeResource(resources, R.drawable.charging_image)

        val notification = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_charging)
            .setLargeIcon(albumArt)
            .setContentTitle(title)
            .setContentText(artist)
            .setSubText(album)
            .addAction(R.drawable.ic_charging, "Previous", null)
            .addAction(R.drawable.ic_charging, "Pause", null)
            .addAction(R.drawable.ic_charging, "Next", null)
            .setStyle(mediaStyle)
            .setProgress(duration, position, false)
            .build()

        notificationManager.notify(1, notification)
    }
}