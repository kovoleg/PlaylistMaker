package com.example.playlistmaker

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import androidx.activity.enableEdgeToEdge
import androidx.core.net.toUri
import com.google.android.material.appbar.MaterialToolbar

class SettingsActivity : BasicActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_settings2)

        val rootLinearLayout = findViewById<LinearLayout>(R.id.rootView)
        setupEdgeToEdge(rootLinearLayout)

        val toolbar = findViewById<MaterialToolbar>(R.id.toolbar)
        toolbar.setNavigationOnClickListener {
            finish()
        }
        val shareAppButton: Button = findViewById(R.id.shareAppButton)
        val writeToSupportButton: Button = findViewById(R.id.writeToSupportButton)
        val usersAgreementButton: Button = findViewById(R.id.usersAgreementButton)


        shareAppButton.setOnClickListener{
            val message = getString(R.string.practicum_android_link)
            val shareIntent = Intent().apply {
                action = Intent.ACTION_SEND
                type = "text/plain"
            }
            shareIntent.putExtra(Intent.EXTRA_TEXT, message)
            startActivity(shareIntent)
        }

        writeToSupportButton.setOnClickListener {
            val shareIntent = Intent(Intent.ACTION_SENDTO)
            shareIntent.data = "mailto:".toUri()
            shareIntent.putExtra(Intent.EXTRA_EMAIL, arrayOf(getString(R.string.support_email)))
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.email_theme_support))
            shareIntent.putExtra(Intent.EXTRA_TEXT,getString(R.string.thx_for_the_app))
            startActivity(shareIntent)
        }

        usersAgreementButton.setOnClickListener {
            val browserIntent = Intent(Intent.ACTION_VIEW)
            browserIntent.data = Uri.parse(getString(R.string.prac_offer_agree))
            startActivity(browserIntent)
        }
    }
}