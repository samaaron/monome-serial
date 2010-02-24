#!/bin/sh

# A script to fix permissions for lock files on Mac OS X
# Contributed by Dmitry Markman <dimitry.markman@verizon.net>
# Fri Aug 23 15:46:46 MDT 2002
# Adapted for RXTX 2.1-7 by LA3HM 30 Jul 2006
# Replaced 'niutil' with 'dscl' for (Snow) Leopard

# You may need to run the following after:
# sudo dscl . -append /Groups/uucp GroupMembership <your usename>

curruser=`sudo id -p | grep 'login' | sed 's/login.//'`

if [ ! -d /var/lock ]
then
sudo mkdir /var/lock
fi

sudo chgrp uucp /var/lock
sudo chmod 775 /var/lock
if [ ! `sudo dscl . -read / /groups/_uucp users | grep $curruser > /dev/null` ]
then
  sudo dscl . -append /groups/_uucp GroupMembership $curruser
fi
