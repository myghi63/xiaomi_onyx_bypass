# Onyx Bypass

Standalone system app for the Xiaomi onyx (POCO F7 / SM8735): bypass charging while
gaming + the MIUI gaming thermal scene, with the "Gaming & charging" page in
Settings > System. Extracted from the `parts/` app of `device/xiaomi/onyx`
(thermal/bypass work `97346a1..914f8b9`); `XiaomiParts` itself (display color service,
HDR) stays in the device tree.

## What it does

- `ThermalService` watches the foreground app (TaskStackListener) and writes the MIUI
  thermal scene to `/sys/class/thermal/thermal_message/sconfig`:
  18 (gaming) for apps in the game list, 15 camera, 5 dialer, 19 default. Scene maps
  are onyx/MIUI-specific.
- `BypassChargeUtils` arbitrates the MCA charger node
  `/sys/class/xm_power/charger/charge_interface/charge_enable` with the
  `onyx_parts buck 0/1` client protocol. Guards: plugged in and battery >= 20 %; a
  refusal by level re-arms itself through a battery broadcast watcher when the level
  crosses 20 % again.
- UI: `CollapsingToolbarBaseActivity` + androidx preferences (`SettingsBasePreferenceFragment`),
  same look as the LineageOS settings pages.
- Quick settings tiles (`BypassTileService`, `BypassGamingTileService`): mirror the "Bypass now"
  and "Bypass while gaming" switches, same guards and refusal toast; long press opens the page.

## Settings.System keys

| key | default | note |
|---|---|---|
| `onyx_bypass_gaming` | 1 | bypass while a game is foreground |
| `onyx_bypass_always` | 0 | forced bypass, cleared at boot |
| `onyx_thermal_gaming` | 1 | gaming scene for the game list |
| `onyx_thermal_always` | 0 | forced scene, cleared at boot |
| `onyx_game_list` / `onyx_game_list_excluded` / `onyx_game_list_auto` | - | list membership |

## Integration (soft, like a plugin)

Any device tree picks it up with one manifest entry and two lines — without them the
device builds clean and simply has no bypass:

```xml
<project name="xiaomi_onyx_bypass" path="vendor/onyxbypass" remote="myghi63" revision="main" />
```

```mk
# <device>.mk
$(call inherit-product-if-exists, vendor/onyxbypass/onyx_bypass.mk)

# BoardConfig.mk
-include vendor/onyxbypass/sepolicy/sepolicy.mk
```

Sepolicy: the repo adds only the private app domain `onyx_bypass_app`
(SYSTEM_EXT_PRIVATE_SEPOLICY_DIRS). The `vendor_sysfs_xm_power` type, the `genfscon`
labels of the `soc:mca_*` / `xm_power` nodes and the 202504 `private/compat`
ignore entry for that type belong to the device tree (shared charger
infrastructure); the app sees the type through the system_ext public API.

## Verifying on a live device (no root)

```sh
adb shell am start -n com.myghi63.onyxbypass/.charge.ChargeSettingsActivity
adb shell cat /sys/class/thermal/thermal_message/sconfig   # scene applied
adb shell dumpsys battery                                   # "Charge counter" frozen = bypass
adb shell dumpsys activity services com.myghi63.onyxbypass # ThermalService alive
```

## License

Apache-2.0, like the LineageOS device tree it came from.
