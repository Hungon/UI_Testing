/*
 * Modified by Denys Zelenchuk on 1.6.2018.
 */

package com.example.android.architecture.blueprints.todoapp.settings

import android.annotation.TargetApi
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.preference.*
import android.support.annotation.VisibleForTesting
import android.support.v4.app.NotificationCompat
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import com.example.android.architecture.blueprints.todoapp.R
import com.example.android.architecture.blueprints.todoapp.tasks.TasksActivity

/**
 * A [PreferenceActivity] that presents a set of application settings. On
 * handset devices, settings are presented as a single list. On tablets,
 * settings are split by category, with category headers shown to the left of
 * the list of settings.
 *
 *
 * See [
 * Android Design: Settings](http://developer.android.com/design/patterns/settings.html) for design guidelines and the [Settings
 * API Guide](http://developer.android.com/guide/topics/ui/settings.html) for more information on developing a Settings UI.
 */
class SettingsActivity : AppCompatPreferenceActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupActionBar()
    }

    /**
     * Set up the [android.app.ActionBar], if the API is available.
     */
    private fun setupActionBar() {
        val ab = supportActionBar
        ab!!.setDisplayHomeAsUpEnabled(true)
    }

    /**
     * {@inheritDoc}
     */
    override fun onIsMultiPane(): Boolean {
        return isXLargeTablet(this)
    }

    /**
     * {@inheritDoc}
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    override fun onBuildHeaders(target: List<Header>) {
        loadHeadersFromResource(R.xml.pref_headers, target)
    }

    /**
     * This method stops fragment injection in malicious applications.
     * Make sure to deny any unknown fragments here.
     */
    override fun isValidFragment(fragmentName: String): Boolean {
        return (PreferenceFragment::class.java.name == fragmentName
                || GeneralPreferenceFragment::class.java.name == fragmentName
                || DataSyncPreferenceFragment::class.java.name == fragmentName
                || NotificationPreferenceFragment::class.java.name == fragmentName
                || WebViewFragment::class.java.name == fragmentName)
    }

    /**
     * This fragment shows general preferences only. It is used when the
     * activity is showing a two-pane settings UI.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    class GeneralPreferenceFragment : PreferenceFragment() {
        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            addPreferencesFromResource(R.xml.pref_general)
            setHasOptionsMenu(true)

            // Bind the summaries of EditText/List/Dialog/Ringtone preferences
            // to their values. When their values change, their summaries are
            // updated to reflect the new value, per the Android Design
            // guidelines.
            bindPreferenceSummaryToValue(findPreference("email_edit_text"))
            bindPreferenceSummaryToValue(findPreference("example_list"))
        }

        override fun onOptionsItemSelected(item: MenuItem): Boolean {
            val id = item.itemId
            if (id == android.R.id.home) {
                startActivity(Intent(activity, SettingsActivity::class.java))
                return true
            }
            return super.onOptionsItemSelected(item)
        }
    }

    /**
     * This fragment shows notification preferences only. It is used when the
     * activity is showing a two-pane settings UI.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    class NotificationPreferenceFragment : PreferenceFragment() {

        internal var preferenceScreen: PreferenceScreen? = null
        private var sendNotificationPreference: Preference? = null
        private var sendNotificationWithDelayPreference: Preference? = null
        private var switchPreference: SwitchPreference? = null
        private var ringtonePreference: RingtonePreference? = null
        private var vibratePreference: CheckBoxPreference? = null
        private var notificationsSlider: SliderPreference? = null

        private val sendNotificationWithDelayListener = Preference.OnPreferenceClickListener { preference ->
            if (preference.key == sendNotificationWithDelayPreference?.key) {
                sendNotificationWithDelay()
            }
            true
        }

        private val sendNotificationListener = Preference.OnPreferenceClickListener { preference ->
            if (preference.key == sendNotificationPreference?.key) {
                sendNotification()
            }
            true
        }

        private val switchPreferenceListener = Preference.OnPreferenceClickListener {
            setNotificationPreferences()
            true
        }

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            addPreferencesFromResource(R.xml.pref_notification)
            setHasOptionsMenu(true)

            // Bind the summaries of EditText/List/Dialog/Ringtone preferences
            // to their values. When their values change, their summaries are
            // updated to reflect the new value, per the Android Design
            // guidelines.
            bindPreferenceSummaryToValue(findPreference("notifications_new_message_ringtone"))
            preferenceScreen = getPreferenceScreen()
            sendNotificationPreference = this.findPreference("notifications_send") as Preference
            sendNotificationWithDelayPreference = this.findPreference("notifications_with_delay_send") as Preference
            switchPreference = this.findPreference("notifications_new_message") as SwitchPreference
            ringtonePreference = this.findPreference("notifications_new_message_ringtone") as RingtonePreference
            vibratePreference = this.findPreference("notifications_new_message_vibrate") as CheckBoxPreference
            notificationsSlider = this.findPreference("notifications_slider") as SliderPreference
            setNotificationPreferences()
            sendNotificationPreference?.onPreferenceClickListener = sendNotificationListener
            sendNotificationWithDelayPreference?.onPreferenceClickListener = sendNotificationWithDelayListener
            switchPreference?.onPreferenceClickListener = switchPreferenceListener
        }

        private fun sendNotificationWithDelay() {

            val channel = NotificationChannel("777", "ToDo", NotificationManager.IMPORTANCE_HIGH)
            channel.description = "ToDo app channel"
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            val notificationManager = this.context.getSystemService<NotificationManager>(NotificationManager::class.java)
            assert(notificationManager != null)
            notificationManager!!.createNotificationChannel(channel)

            // Create an explicit intent for an Activity in your app
            val intent = Intent(this.context, TasksActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            val pendingIntent = PendingIntent.getActivity(this.context, 0, intent, 0)

            val mBuilder = NotificationCompat.Builder(this.context, "777")
                    .setSmallIcon(R.drawable.logo)
                    .setContentTitle("My notification")
                    .setContentText("Much longer text that cannot fit one line...")
                    .setStyle(NotificationCompat.BigTextStyle()
                            .bigText("Much longer text that cannot fit one line..."))
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .setContentIntent(pendingIntent)
                    .setAutoCancel(true)

            Handler(Looper.getMainLooper()).postDelayed({ notificationManager.notify(777, mBuilder.build()) }, 5000)
        }

        private fun sendNotification() {

            val channel = NotificationChannel("777", "ToDo", NotificationManager.IMPORTANCE_HIGH)
            channel.description = "ToDo app channel"
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            val notificationManager = this.context.getSystemService<NotificationManager>(NotificationManager::class.java)
            notificationManager!!.createNotificationChannel(channel)

            // Create an explicit intent for an Activity in your app
            val intent = Intent(this.context, TasksActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            val pendingIntent = PendingIntent.getActivity(this.context, 0, intent, 0)

            val mBuilder = NotificationCompat.Builder(this.context, "777")
                    .setSmallIcon(R.drawable.logo)
                    .setContentTitle("My notification")
                    .setContentText("Much longer text that cannot fit one line...")
                    .setStyle(NotificationCompat.BigTextStyle()
                            .bigText("Much longer text that cannot fit one line..."))
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .setContentIntent(pendingIntent)
                    .setAutoCancel(true)

            // notificationId is a unique int for each notification that you must define
            notificationManager.notify(777, mBuilder.build())
        }

        private fun setNotificationPreferences() {
            if (switchPreference?.isChecked == true) {
                preferenceScreen?.run {
                    addPreference(ringtonePreference)
                    addPreference(vibratePreference)
                    addPreference(notificationsSlider)
                }
            } else {
                preferenceScreen?.run {
                    removePreference(ringtonePreference)
                    removePreference(vibratePreference)
                    removePreference(notificationsSlider)
                }
            }
        }

        override fun onOptionsItemSelected(item: MenuItem): Boolean {
            val id = item.itemId
            if (id == android.R.id.home) {
                startActivity(Intent(activity, SettingsActivity::class.java))
                return true
            }
            return super.onOptionsItemSelected(item)
        }
    }

    /**
     * This fragment shows data and sync preferences only. It is used when the
     * activity is showing a two-pane settings UI.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    class DataSyncPreferenceFragment : PreferenceFragment() {
        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            addPreferencesFromResource(R.xml.pref_data_sync)
            setHasOptionsMenu(true)

            // Bind the summaries of EditText/List/Dialog/Ringtone preferences
            // to their values. When their values change, their summaries are
            // updated to reflect the new value, per the Android Design
            // guidelines.
            bindPreferenceSummaryToValue(findPreference("sync_frequency"))
        }

        override fun onOptionsItemSelected(item: MenuItem): Boolean {
            val id = item.itemId
            if (id == android.R.id.home) {
                startActivity(Intent(activity, SettingsActivity::class.java))
                return true
            }
            return super.onOptionsItemSelected(item)
        }
    }

    class WebViewFragment : PreferenceFragment() {

        override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                                  savedInstanceState: Bundle?): View? {

            val v = inflater.inflate(R.layout.webview_act, container, false)
            val mWebView = v.findViewById<WebView>(R.id.web_view)

            // Enable Javascript
            val webSettings = mWebView.settings
            webSettings.javaScriptEnabled = true
            mWebView.loadUrl(WEB_FORM_URL)
            // Force links and redirects to open in the WebView instead of in a browser
            mWebView.webViewClient = object : WebViewClient() {
                override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
                    return false
                }
            }

            return v
        }

        companion object {

            @VisibleForTesting
            val WEB_FORM_URL = "file:///android_asset/web_form.html"
        }
    }

    companion object {

        /**
         * A preference value change listener that updates the preference's summary
         * to reflect its new value.
         */
        private val sBindPreferenceSummaryToValueListener = Preference.OnPreferenceChangeListener { preference, value ->
            val stringValue = value.toString()

            if (preference is ListPreference) {
                // For list preferences, look up the correct display value in
                // the preference's 'entries' list.
                val index = preference.findIndexOfValue(stringValue)

                // Set the summary to reflect the new value.
                preference.setSummary(
                        if (index >= 0)
                            preference.entries[index]
                        else
                            null)

            } else if (preference is RingtonePreference) {
                // For ringtone preferences, look up the correct display value
                // using RingtoneManager.
                if (TextUtils.isEmpty(stringValue)) {
                    // Empty values correspond to 'silent' (no ringtone).
                    preference.setSummary(R.string.pref_ringtone_silent)

                } else {
                    val ringtone = RingtoneManager.getRingtone(
                            preference.getContext(), Uri.parse(stringValue))

                    if (ringtone == null) {
                        // Clear the summary if there was a lookup error.
                        preference.setSummary(null)
                    } else {
                        // Set the summary to reflect the new ringtone display
                        // name.
                        val name = ringtone.getTitle(preference.getContext())
                        preference.setSummary(name)
                    }
                }

            } else {
                // For all other preferences, set the summary to the value's
                // simple string representation.
                preference.summary = stringValue
            }
            true
        }

        /**
         * Helper method to determine if the device has an extra-large screen. For
         * example, 10" tablets are extra-large.
         */
        private fun isXLargeTablet(context: Context): Boolean {
            return context.resources.configuration.screenLayout and Configuration.SCREENLAYOUT_SIZE_MASK >= Configuration.SCREENLAYOUT_SIZE_XLARGE
        }

        /**
         * Binds a preference's summary to its value. More specifically, when the
         * preference's value is changed, its summary (line of text below the
         * preference title) is updated to reflect the value. The summary is also
         * immediately updated upon calling this method. The exact display format is
         * dependent on the type of preference.
         *
         * @see .sBindPreferenceSummaryToValueListener
         */
        private fun bindPreferenceSummaryToValue(preference: Preference) {
            // Set the listener to watch for value changes.
            preference.onPreferenceChangeListener = sBindPreferenceSummaryToValueListener

            // Trigger the listener immediately with the preference's
            // current value.
            sBindPreferenceSummaryToValueListener.onPreferenceChange(preference,
                    PreferenceManager
                            .getDefaultSharedPreferences(preference.context)
                            .getString(preference.key, ""))
        }
    }
}
