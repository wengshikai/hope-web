#!/usr/bin/env bash

base=$(dirname $0)/../hope-web
cd $base
pidfile=RUNNING_PID

if [ -f "$pidfile" ];then
    pid=$(cat $pidfile)
    if [ "$pid" != "" ] ; then
        echo -e "stopping hope-web $pid ..."
        kill $pid
    fi
    rm -rf $pidfile
fi

sleep 3
nohup ./bin/hope-web -J-Xms4096m -J-Xmx4096m &
sleep 5
echo "系统重启成功..."
exit 0
