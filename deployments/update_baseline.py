import json, MySQLdb

db=MySQLdb.connect(host="cobalt.picsauditing.com",user="pics", passwd="@Irvine1",db="pics_stage")
c = db.cursor()
c.execute("""
	SELECT id, genid opID, subid conID, baselineFlagDetail
	FROM generalcontractors
	WHERE baselineFlagDetail IS NOT NULL
""")

baseline = {}
for row in c.fetchall():
	baseline[row[0]] = {'opID': row[1], 'conID': row[2], 'detail': json.loads(row[3])}

updates = []
for gcID, b in baseline.items():
	for criteriaID, data in b['detail'].items():
		updates.append((data['flag'], b['opID'], b['conID'], criteriaID))

for u in updates:
    update = """
        UPDATE flag_data
        SET baselineFlag = '%s'
        WHERE opID = %s AND conID = %s AND criteriaID = %s;
    """ % u
    c.execute(update)

c.close()

db.commit()

db.close()