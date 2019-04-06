require acrn-common.inc

inherit python3native cml1 deploy

DEPENDS = "python3-pip-native python3-kconfiglib-native gnu-efi"

S = "${WORKDIR}/git"
B = "${S}/build"

EXTRA_OEMAKE += "SYSROOT=${STAGING_DIR_TARGET}"

KCONFIG_CONFIG_COMMAND = "-C ${S} menuconfig"

PACKAGE_ARCH = "${MACHINE_ARCH}"

FILES_${PN} += "${libdir}/acrn/* ${datadir}/acrn/*"

do_configure () {
    sed -i -e 's#org.clearlinux#${ACRN_UEFI_OS_LOADER_DIR}#g' ${S}/efi-stub/boot.c
    sed -i -e 's#bootloaderx64.efi#${ACRN_UEFI_OS_LOADER_NAME}.efi#g' ${S}/efi-stub/boot.c
}

do_compile () {
    cd ${S}
    oe_runmake hypervisor BOARD=${MACHINE} PLATFORM=${ACRN_PLATFORM} 
}

do_install () {
    cd ${S}
    oe_runmake hypervisor-install BOARD=${MACHINE} PLATFORM=${ACRN_PLATFORM} DESTDIR=${D}
}

do_deploy () {
    if [ "${ACRN_PLATFORM}" = "uefi" ]; then
        install -m 0755 ${D}${libdir}/acrn/acrn.efi ${DEPLOYDIR}/acrn-${ACRN_UEFI_IMAGE_NAME}-${MACHINE}-${DATETIME}.efi
        ln -sf acrn-${ACRN_UEFI_IMAGE_NAME}-${MACHINE}-${DATETIME}.efi ${DEPLOYDIR}/acrn-${ACRN_UEFI_IMAGE_NAME}-${MACHINE}.efi
        ln -sf acrn-${ACRN_UEFI_IMAGE_NAME}-${MACHINE}-${DATETIME}.efi ${DEPLOYDIR}/acrn-${ACRN_UEFI_IMAGE_NAME}.efi
    fi
}
do_deploy[vardepsexclude] = "DATETIME"

addtask deploy after do_install before do_build
