/*
 * SPDX-FileCopyrightText: The LineageOS Project
 * SPDX-License-Identifier: Apache-2.0
 */

package com.myghi63.onyxbypass.charge

import android.widget.Toast

import com.myghi63.onyxbypass.R

/**
 * Quick settings tile for "Bypass now": same guards and refusal message as the
 * settings switch; the one-shot semantics (cleared on reboot) are unchanged.
 */
class BypassTileService : BypassToggleTileService() {

    override val settingKey = BypassChargeUtils.SETTING_BYPASS_ALWAYS
    override val defaultValue = 0
    override val labelRes = R.string.bypass_now_title

    override fun refuseTurningOn(): Boolean {
        val bypass = BypassChargeUtils.getInstance(this)
        bypass.guardFailure()?.let {
            Toast.makeText(this, bypass.refusalText(it), Toast.LENGTH_LONG).show()
            return true
        }
        return false
    }
}
