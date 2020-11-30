package org.oxycblt.auxio.settings

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.activityViewModels
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceGroupAdapter
import androidx.preference.PreferenceScreen
import androidx.preference.PreferenceViewHolder
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.customview.customView
import org.oxycblt.auxio.MainActivity
import org.oxycblt.auxio.R
import org.oxycblt.auxio.settings.adapters.AccentAdapter
import org.oxycblt.auxio.ui.ACCENTS
import org.oxycblt.auxio.ui.accent
import org.oxycblt.auxio.ui.getDetailedAccentSummary

class SettingsListFragment : PreferenceFragmentCompat() {
    private val settingsModel: SettingsViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // --- PREFERENCE ITEM SETUP ---

        val themePref = findPreference<Preference>(SettingsManager.Keys.KEY_THEME)?.apply {
            setIcon(
                when (AppCompatDelegate.getDefaultNightMode()) {
                    AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM -> R.drawable.ic_auto
                    AppCompatDelegate.MODE_NIGHT_NO -> R.drawable.ic_day
                    AppCompatDelegate.MODE_NIGHT_YES -> R.drawable.ic_night

                    else -> R.drawable.ic_auto
                }
            )
        }

        val accentPref = findPreference<Preference>(SettingsManager.Keys.KEY_ACCENT)?.apply {
            onPreferenceClickListener = Preference.OnPreferenceClickListener {
                showAccentDialog()
                true
            }

            summary = getDetailedAccentSummary(requireActivity(), accent)
        }

        // --- VIEWMODEL SETUP ---

        settingsModel.theme.observe(viewLifecycleOwner) {
            if (it != null) {
                themePref?.setIcon(
                    when (it) {
                        AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM -> R.drawable.ic_auto
                        AppCompatDelegate.MODE_NIGHT_NO -> R.drawable.ic_day
                        AppCompatDelegate.MODE_NIGHT_YES -> R.drawable.ic_night

                        else -> R.drawable.ic_auto
                    }
                )

                (requireActivity() as MainActivity).doThemeRecreate(it)

                settingsModel.doneWithThemeUpdate()
            }
        }

        settingsModel.accent.observe(viewLifecycleOwner) {
            if (it != null) {
                accentPref?.summary = getDetailedAccentSummary(requireActivity(), it)

                requireActivity().recreate()

                settingsModel.doneWithAccentUpdate()
            }
        }

        settingsModel.edge.observe(viewLifecycleOwner) {
            if (it != null) {
                requireActivity().recreate()

                settingsModel.doneWithEdgeUpdate()
            }
        }

        Log.d(this::class.simpleName, "Fragment created.")
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.prefs_main, rootKey)
    }

    // Forcefully override the adapter creation process so I can get rid of the ugly
    // colorPrimary ripples.
    @SuppressLint("RestrictedApi")
    override fun onCreateAdapter(preferenceScreen: PreferenceScreen?): RecyclerView.Adapter<*> {
        return object : PreferenceGroupAdapter(preferenceScreen) {
            override fun onCreateViewHolder(
                parent: ViewGroup,
                viewType: Int
            ): PreferenceViewHolder {
                val holder = super.onCreateViewHolder(parent, viewType)

                if (holder.itemView.id != android.R.id.title) {
                    holder.itemView.setBackgroundResource(R.drawable.ui_ripple)
                }

                return holder
            }
        }
    }

    private fun offLoad(something: String) {
        Log.d(this::class.simpleName, something)
    }

    private fun showAccentDialog() {
        MaterialDialog(requireActivity()).show {
            title(R.string.setting_accent)

            // Roll my own RecyclerView since [To no surprise whatsoever] Material Dialogs
            // has a bug where ugly dividers will show with the RecyclerView even if you disable them.
            // This is why I hate using third party libraries.
            val recycler = RecyclerView(requireContext()).apply {
                adapter = AccentAdapter {
                    if (it.first != accent.first) {
                        SettingsManager.getInstance().setAccent(it)
                    }

                    this@show.dismiss()
                }

                post {
                    // Combine the width of the recyclerview with the width of an item in order
                    // to center the currently selected accent.
                    val childWidth = getChildAt(0).width / 2

                    (layoutManager as LinearLayoutManager)
                        .scrollToPositionWithOffset(
                            ACCENTS.indexOf(accent),
                            (width / 2) - childWidth
                        )
                }

                layoutManager = LinearLayoutManager(
                    requireContext()
                ).also { it.orientation = LinearLayoutManager.HORIZONTAL }
            }

            customView(view = recycler)

            view.invalidateDividers(showTop = false, showBottom = false)

            negativeButton(android.R.string.cancel)

            show()
        }
    }
}