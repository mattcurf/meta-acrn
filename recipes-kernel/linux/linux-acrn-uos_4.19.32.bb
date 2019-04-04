require linux-acrn-common.inc

PV = "4.19.32-acrn-uos"

SRC_URI += "file://defconfig \
"

KERNEL_PACKAGE_NAME = "uos-kernel"
