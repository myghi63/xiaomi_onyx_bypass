# Product-scoped additions. Soft-inherit from a device mk:
#   $(call inherit-product-if-exists, vendor/onyxbypass/onyx_bypass.mk)
PRODUCT_PACKAGES += \
    OnyxBypass
