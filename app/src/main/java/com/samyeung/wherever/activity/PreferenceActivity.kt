package com.samyeung.wherever.activity

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.preference.ListPreference
import android.support.v4.content.ContextCompat
import android.support.v7.preference.Preference
import android.support.v7.preference.PreferenceFragmentCompat
import android.support.v7.preference.PreferenceManager
import android.util.Log
import android.view.MenuItem
import com.samyeung.wherever.MainActivity
import com.samyeung.wherever.R
import com.samyeung.wherever.api.AuthServiceAdapter
import com.samyeung.wherever.util.LoadingDialog
import com.samyeung.wherever.util.LoginManager
import com.samyeung.wherever.util.helper.ToolbarUtil
import com.samyeung.wherever.fragment.auth.ChangePasswordFragment
import kotlinx.android.synthetic.main.tool_bar.toolbar
import com.samyeung.wherever.util.helper.LanguageManager
import com.samyeung.wherever.fragment.main.FeedbackFragment


/**
 * A [PreferenceActivity] that presents a set of application settings. On
 * handset devices, settings are presented as a single list. On tablets,
 * settings are split by category, with category headers shown to the left of
 * the list of settings.
 *
 * See [Android Design: Settings](http://developer.android.com/design/patterns/settings.html)
 * for design guidelines and the [Settings API Guide](http://developer.android.com/guide/topics/ui/settings.html)
 * for more information on developing a Settings UI.
 */
class PreferenceActivity : ApplicationActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setting)
        setupActionBar()
        supportFragmentManager.beginTransaction()
            .replace(
                R.id.content,
                GeneralPreferenceFragment()
            ).commit()
    }

    /**
     * Set up the [android.app.ActionBar], if the API is available.
     */
    private fun setupActionBar() {
        ToolbarUtil.setUpToolbar(this, toolbar, resources.getString(R.string.setting), true)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return when (item!!.itemId) {
            android.R.id.home -> {
                finish()
                true
            }
            else -> {
                super.onOptionsItemSelected(item)
            }
        }
    }

    class GeneralPreferenceFragment : PreferenceFragmentCompat() {
        private lateinit var authService: AuthServiceAdapter
        private fun setUpService() {
            this.authService = object : AuthServiceAdapter(context!!) {
                override fun onLogout() {
                    LoginManager.logout(context)
                    MainActivity.start(activity!!)
                }

            }

        }

        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            addPreferencesFromResource(R.xml.pref_apps)
            setHasOptionsMenu(true)
            setUpService()
            val pm = activity!!.packageManager
            val verInfo = pm.getPackageInfo(activity!!.packageName, 0).versionName
            findPreference(resources.getString(R.string.pref_title_version)).summary = verInfo
            //bindPreferenceSummaryToValue(findPreference(AppSettingManager.IMAGE_QUALITY))
            bindPreferenceSummaryToValue(
                findPreference(LanguageManager.LANGUAGE_KEY),
                Preference.OnPreferenceChangeListener { preference, newValue ->
                    if (LanguageManager.findLanguageByKey(newValue.toString())!!.language != LanguageManager.getLanguage(
                            context!!
                        )
                    ) {
                        Log.d("Language", LanguageManager.findLanguageByKey(newValue.toString())!!.language)
                        setNewLocale(LanguageManager.findLanguageByKey(newValue.toString())!!, false)
                    }

                    true
                })
            LoginManager.isLogin(context!!).let {
                findPreference(resources.getString(R.string.pref_header_security)).isVisible = it
            }

            findPreference(resources.getString(R.string.pref_title_logout)).setOnPreferenceClickListener {
                LoadingDialog.show(childFragmentManager, "", "")
                this.authService.logout()
                true
            }
            findPreference(resources.getString(R.string.pref_title_change_password)).setOnPreferenceClickListener {
                ChangePasswordFragment.start(activity!!)
                true
            }
            findPreference(resources.getString(R.string.pref_title_feedback)).setOnPreferenceClickListener {
                FeedbackFragment.start(activity!!)
                true
            }
            /*
            findPreference(resources.getString(R.string.pref_title_open_source)).setOnPreferenceClickListener {
                ContextCompat.startActivity(context!!,Intent(context, OssLicensesMenuActivity::class.java),null)
                true
            }
            */
        }

        private fun setNewLocale(language: LanguageManager.Language, restartProcess: Boolean): Boolean {
            LanguageManager.setNewLocale(context!!, language)

            val i = Intent(context, MainActivity::class.java)
            startActivity(i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK))

            if (restartProcess) {
                System.exit(0)
            } else {
                //Toast.makeText(context, "Activity restarted", Toast.LENGTH_SHORT).show()
            }
            return true
        }
    }


    companion object {
        fun start(context: Context) {
            val intent = Intent(context, PreferenceActivity::class.java)
            ContextCompat.startActivity(context, intent, null)
        }

        /**
         * A preference value change listener that updates the preference's summary
         * to reflect its new value.
         */
        private val sBindPreferenceSummaryToValueListener = Preference.OnPreferenceChangeListener { preference, value ->
            val stringValue = value.toString()
            if (preference is ListPreference) {
                // For list preferences, look up the correct display value in
                // the preference's 'entries' list.
                val listPreference = preference
                val index = listPreference.findIndexOfValue(stringValue)

                // Set the summary to reflect the new value.
                preference.setSummary(
                    if (index >= 0)
                        listPreference.entries[index]
                    else
                        null
                )

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

         * @see .sBindPreferenceSummaryToValueListener
         */
        private fun bindPreferenceSummaryToValue(
            preference: Preference,
            preferenceChangeListener: Preference.OnPreferenceChangeListener? = null
        ) {
            // Set the listener to watch for value changes.
            if (preferenceChangeListener == null) {
                preference.onPreferenceChangeListener =
                    sBindPreferenceSummaryToValueListener

                // Trigger the listener immediately with the preference's
                // current value.
                sBindPreferenceSummaryToValueListener.onPreferenceChange(
                    preference,
                    PreferenceManager
                        .getDefaultSharedPreferences(preference.context)
                        .getString(preference.key, "")
                )
            } else {
                preference.onPreferenceChangeListener = preferenceChangeListener
                preferenceChangeListener.onPreferenceChange(
                    preference, PreferenceManager
                        .getDefaultSharedPreferences(preference.context)
                        .getString(preference.key, "")
                )
            }
        }
    }
}
