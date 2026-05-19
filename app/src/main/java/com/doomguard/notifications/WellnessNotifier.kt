package com.doomguard.notifications

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.doomguard.R
import kotlin.random.Random

object WellnessNotifier {
    private const val CHANNEL = "doomguard_wellness"

    fun ensureChannel(context: Context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return
        val mgr = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val ch = NotificationChannel(
            CHANNEL,
            "Wellness nudges",
            NotificationManager.IMPORTANCE_DEFAULT,
        )
        mgr.createNotificationChannel(ch)
    }

    fun canNotify(context: Context): Boolean {
        if (Build.VERSION.SDK_INT >= 33) {
            return ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS,
            ) == PackageManager.PERMISSION_GRANTED
        }
        return true
    }

    fun show(context: Context, title: String, text: String) {
        ensureChannel(context)
        if (!canNotify(context)) return
        val notif = NotificationCompat.Builder(context, CHANNEL)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(title)
            .setContentText(text)
            .setStyle(NotificationCompat.BigTextStyle().bigText(text))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()
        NotificationManagerCompat.from(context).notify(Random.nextInt(), notif)
    }
}
