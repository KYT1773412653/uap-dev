#!/bin/bash

# code compile
# mvn clean install -Dmaven.test.skip=true

# define obfuscation packages and configuration files
UAP_VERSION=2.0.0.0-DEV

ROOT_PATH=`pwd`

PACKAGE_LIST=(
	"uap-main/uap-app"
	"uap-bpm/uap-bpm-app"
	"uap-schedule/uap-schedule-app"
	"uap-message/uap-msg-app"
	"uap-monitor/uap-monitor-app"
	"uap-main/uap-app-ms"
	"uap-bpm/uap-bpm-app-ms"
	"uap-schedule/uap-schedule-app-ms"
	"uap-message/uap-msg-app-ms"
	"uap-monitor/uap-monitor-app-ms"
)

PACKAGE_NAME_LIST=(
	"uap.war"
	"uap-bpm.war"
	"uap-schedule.war"
	"uap-msg.war"
	"uap-monitor.war"
	"uap-ms.jar"
	"uap-bpm-ms.jar"
	"uap-schedule-ms.jar"
	"uap-msg-ms.jar"
	"uap-monitor-ms.jar"
)

PACKAGE_CONF_LIST=(
	"proguard-main.conf"
	"proguard-bpm.conf"
	"proguard-schedule.conf"
	"proguard-msg.conf"
	"proguard-monitor.conf"
	"proguard-main-ms.conf"
	"proguard-bpm-ms.conf"
	"proguard-schedule-ms.conf"
	"proguard-msg-ms.conf"
	"proguard-monitor-ms.conf"
)

# for each target package
for (( i = 0; i < ${#PACKAGE_LIST[*]}; i++ ));do
	echo "#############################################################################"
	echo "#################Start obfuscation ${PACKAGE_NAME_LIST[i]}###################"
	echo "#############################################################################"
	cd $ROOT_PATH
	mkdir ${PACKAGE_LIST[i]}/target/uappg
	cp ${PACKAGE_LIST[i]}/${PACKAGE_CONF_LIST[i]} ${PACKAGE_LIST[i]}/target/uappg
	cp ${PACKAGE_LIST[i]}/target/${PACKAGE_NAME_LIST[i]} ${PACKAGE_LIST[i]}/target/uappg
	cd ${PACKAGE_LIST[i]}/target/uappg
	jar -xf ${PACKAGE_NAME_LIST[i]}
	sed -i "s/UAP_VERSION/${UAP_VERSION}/g" ${PACKAGE_CONF_LIST[i]}
	sh $PROGUARD_HOME/bin/proguard.sh @${PACKAGE_CONF_LIST[i]} -dontnote
	if [[ ${PACKAGE_NAME_LIST[i]} =~ "war" ]]
          then
            PACKAGE_INF=WEB-INF
            \cp -r pg/* $PACKAGE_INF/lib
            rm -rf ${PACKAGE_NAME_LIST[i]} pg/ ${PACKAGE_CONF_LIST[i]}
            jar -cf ${PACKAGE_NAME_LIST[i]} *
          else
            PACKAGE_INF=BOOT-INF
            \cp -r pg/* $PACKAGE_INF/lib
            rm -rf ${PACKAGE_NAME_LIST[i]} pg/ ${PACKAGE_CONF_LIST[i]}
            jar cf0m ${PACKAGE_NAME_LIST[i]} ./META-INF/MANIFEST.MF ./*
	fi
        \cp -rf ${PACKAGE_NAME_LIST[i]} ..
	cd ..
	rm -rf uappg
	echo "#############################################################################"
	echo "#################End of obfuscation ${PACKAGE_NAME_LIST[i]}##################"
	echo "#############################################################################"
done