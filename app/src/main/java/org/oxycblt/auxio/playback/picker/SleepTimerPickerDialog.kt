/*
 * Copyright (c) 2023 Auxio Project
 * SleepTimerPickerDialog.kt is part of Auxio.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
 
package org.oxycblt.auxio.playback.picker

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import org.oxycblt.auxio.R
import org.oxycblt.auxio.databinding.DialogSleepTimerBinding
import org.oxycblt.auxio.playback.system.SleepTimerReceiver

/**
 * A SleepTimer Dialog
 *
 * @author komine
 */
class SleepTimerPickerDialog : DialogFragment(), OnClickListener {
    private lateinit var binding: DialogSleepTimerBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DialogSleepTimerBinding.inflate(layoutInflater)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        val width = Resources.getSystem().displayMetrics.widthPixels
        val height = Resources.getSystem().displayMetrics.heightPixels / 2

        dialog?.window?.setLayout(width, height)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.sleepTimerDialogTvConfirm.setOnClickListener(this)
        binding.sleepTimerDialogTvCancel.setOnClickListener(this)
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.sleep_timer_dialog_tvConfirm -> {
                setSleepTime()
                dismiss()
            }
            R.id.sleep_timer_dialog_tvCancel -> {
                dismiss()
            }
        }
    }

    private fun setSleepTime() {
        val radioButton =
            view?.findViewById<View>(binding.sleepTimerDialogRgGroup.checkedRadioButtonId) ?: return
        val minute = (radioButton.tag as String).toInt()
        if (minute == 0) {
            return
        }

        val applicationContext = activity?.applicationContext ?: return
        val alarmManager =
            applicationContext.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val intent = Intent(applicationContext, SleepTimerReceiver::class.java)
        val pendingIntent =
            PendingIntent.getBroadcast(applicationContext, 0, intent, PendingIntent.FLAG_IMMUTABLE)

        val ms = System.currentTimeMillis() + ((60 * minute) * 1000)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, ms, pendingIntent)
        } else {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, ms, pendingIntent)
        }
    }
}
