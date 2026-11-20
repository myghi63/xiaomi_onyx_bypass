/*
 * SPDX-FileCopyrightText: The LineageOS Project
 * SPDX-License-Identifier: Apache-2.0
 */

package com.myghi63.onyxbypass

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.myghi63.onyxbypass.charge.BypassChargeUtils
import com.myghi63.onyxbypass.thermal.ThermalUtils

class BootCompletedReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        // The forced bypass and the forced thermal scene are requests for the current
        // session only: they must not survive a reboot.
        BypassChargeUtils.getInstance(context).clearAlwaysRequest()
        ThermalUtils.getInstance(context).clearAlwaysProfile()
        ThermalUtils.startService(context)
    }
}
