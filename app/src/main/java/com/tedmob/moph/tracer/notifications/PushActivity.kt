package com.tedmob.moph.tracer.notifications

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Html
import android.text.method.LinkMovementMethod
import androidx.appcompat.app.AppCompatActivity
import com.tedmob.moph.tracer.R
import com.tedmob.moph.tracer.SplashActivity
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.activity_push.*
import kotlinx.android.synthetic.main.toolbar_default.*

class PushActivity : AppCompatActivity() {

    private var disposeObj: Disposable? = null

    companion object {
        private const val KEY__DATA = "notification_data"

        fun newIntent(context: Context?, data: NotificationData): Intent =
            Intent(context, PushActivity::class.java).apply {
                putExtra(KEY__DATA, data)
            }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_push)
        setSupportActionBar(toolbar)

        val notificationData = intent.getParcelableExtra<NotificationData>(KEY__DATA)!!
        showNotification(notificationData)
        message.movementMethod = LinkMovementMethod.getInstance()

        closeButton.setOnClickListener { finish() }
        goToAppButton.setOnClickListener {
            startActivity(
                Intent(this, SplashActivity::class.java)
                    .apply {
                        flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or
                                Intent.FLAG_ACTIVITY_CLEAR_TASK or
                                Intent.FLAG_ACTIVITY_NEW_TASK
                    }
            )
        }
    }

    private fun showNotification(notificationData: NotificationData) {
        supportActionBar?.title = notificationData.title
        /*if (notificationData.image().isNullOrEmpty()) {
            image.visibility = View.GONE
        } else {
            image.setImageUriFrom(notificationData)
        }*/
        message.text = notificationData.message?.let { Html.fromHtml(it) }
    }
}