/*
 * SPDX-FileCopyrightText: The LineageOS Project
 * SPDX-License-Identifier: Apache-2.0
 */

package com.myghi63.onyxbypass.charge

import android.database.ContentObserver
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.service.quicksettings.Tile
import android.service.quicksettings.TileService

/**
 * Quick settings tile mirroring one bypass charging Settings.System switch.
 * The observer keeps the tile in sync with changes made from the settings page,
 * and the value written here is the same one ThermalService reacts to.
 */
abstract class BypassToggleTileService : TileService() {

    protected abstract val settingKey: String
    protected abstract val defaultValue: Int
    protected abstract val labelRes: Int

    /** Runs before turning the switch on; true when the request was refused. */
    protected open fun refuseTurningOn(): Boolean = false

    private var observer: ContentObserver? = null

    override fun onStartListening() {
        val handler = Handler(Looper.getMainLooper())
        observer = object : ContentObserver(handler) {
            override fun onChange(selfChange: Boolean) = updateTile()
        }.also {
            contentResolver.registerContentObserver(
                Settings.System.getUriFor(settingKey),
                false,
                it,
            )
        }
        updateTile()
    }

    override fun onStopListening() {
        observer?.let { contentResolver.unregisterContentObserver(it) }
        observer = null
    }

    override fun onClick() {
        val enabled = Settings.System.getInt(contentResolver, settingKey, defaultValue) != 1
        if (enabled && refuseTurningOn()) {
            updateTile()
            return
        }
        Settings.System.putInt(contentResolver, settingKey, if (enabled) 1 else 0)
        updateTile()
    }

    private fun updateTile() {
        val enabled = Settings.System.getInt(contentResolver, settingKey, defaultValue) == 1
        qsTile?.apply {
            state = if (enabled) Tile.STATE_ACTIVE else Tile.STATE_INACTIVE
            label = getString(labelRes)
            updateTile()
        }
    }
}
