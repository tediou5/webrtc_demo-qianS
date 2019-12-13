#!/bin/bash


echo "INF: restore DB and init it. (Just press ENTER on macbook)"

echo "INF: 1. delete & create DB"
mysql -t -u root -p < db_create_cn.sql

echo "INF: 2. insert SIP accounts"
mysql -t -u root -p -D db_st_ha < db_sip_account.sql

echo "INF: 3. insert test clients and relationships"
mysql -t -u root -p -D db_st_ha < db_init_test.sql



# [Theodore: 2016-11-11] Perf Testing
# add >10,1000 virtual clients, without SIP account
# 
# mysql -t -u root -p -D db_st_ha < db_add_10000clients.sql


if [ $? -ne 0 ];then
    echo -e "ERROR: SQL execution error! \n\n"
    exit 1
fi

echo -e "\n\nINF: DONE! Database is restored ^_^ \n\n"


