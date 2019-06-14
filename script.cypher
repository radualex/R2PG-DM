USING PERIODIC COMMIT
LOAD CSV WITH HEADERS FROM "file:///nodes.csv" AS row
CALL apoc.create.node([row.label], {id: row.id}) yield node
return node;

USING PERIODIC COMMIT
LOAD CSV WITH HEADERS FROM "file:///properties.csv" AS row
MATCH (n {id: row.id})
CALL apoc.create.setProperty(n, row.key, row.value) yield node
return node;

USING PERIODIC COMMIT
LOAD CSV WITH HEADERS FROM "file:///edges.csv" AS row
MATCH (n1), (n2)
WHERE n1.id = row.srcid
AND n2.id = row.tgtid
CALL apoc.create.relationship(n1, row.label, {id: row.id}, n2) yield rel
return rel;