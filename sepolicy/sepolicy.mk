# Sepolicy for OnyxBypass. Soft-include from a device BoardConfig.mk:
#   -include vendor/onyxbypass/sepolicy/sepolicy.mk
# The vendor_sysfs_xm_power sysfs type and its node labels are defined by the
# device tree (charger infrastructure shared with other domains); this repo only
# adds the app domain, which sees the type through the system_ext public API.
SYSTEM_EXT_PRIVATE_SEPOLICY_DIRS += vendor/onyxbypass/sepolicy/private
