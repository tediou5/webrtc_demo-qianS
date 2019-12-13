/*
 * [Theodore: 2016-09-04] SQL to check the database.
 * 
 */

USE db_st_ha







/*
 * SIP records used by clients
 */
SET @rank=0;
SELECT @rank:=@rank+1 as 'No', hex(id), sip_id, hex(flag) 
FROM tb_sip_acct 
WHERE id IN (
    SELECT sip_acct_id FROM tb_client
);



/*
 * SIP records used by clients and assign flag is set
 */
SET @rank=0;
SELECT @rank:=@rank+1 as 'No', hex(id), sip_id, hex(flag) 
FROM tb_sip_acct 
WHERE id IN (
    SELECT sip_acct_id FROM tb_client
) AND flag>0;



/*
 * SIP records with assign flag 
 */
SET @rank=0;
SELECT @rank:=@rank+1 as 'No', hex(id), sip_id, hex(flag) 
FROM tb_sip_acct 
WHERE flag>0;



SELECT "INF: Records Summary                                                " AS "";
SELECT 
    ( SELECT count(*) FROM tb_client  )                 AS "Client Num",
    ( SELECT count(*) FROM tb_sip_acct WHERE flag>0)    AS "Assigned SIP",
    ( SELECT count(*) FROM tb_sip_acct)                 AS "Total SIP" ,
    ( SELECT count(*) FROM tb_client_has)               AS "Relations" 
    FROM dual;


SELECT "INF: If correctly referenced, following counts are same!            "  AS "";
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


