#!/bin/bash


mysql -t -u root -p -D db_st_ha << EOF

SELECT "" AS "";
SELECT "==== assigned sip account ====" AS "";
SET @rank=0;
SELECT @rank:=@rank+1 as 'No.', hex(id), sip_id, hex(flag) FROM tb_sip_acct WHERE id IN (
    SELECT sip_acct_id FROM tb_client
);

/*
 * For correct reference of SIP accounts, following number should same!
 */
SELECT 
    (   SELECT count(*) FROM tb_client
    ) AS "Clients", 
    (   SELECT count(*) FROM tb_sip_acct 
        WHERE flag>0
    ) AS "Assinged SIP",
    (   SELECT count(*) FROM tb_sip_acct 
        WHERE id IN ( SELECT sip_acct_id FROM tb_client)
    ) AS "Used SIP", 
    (   SELECT count(*) FROM tb_sip_acct 
        WHERE id IN ( SELECT sip_acct_id FROM tb_client) AND flag>0
    ) AS "Used and Assigned SIP"
    FROM dual;

EOF

