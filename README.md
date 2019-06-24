# R2PG-DM

This is an application for constructing property graphs from relational databases. 

## Setup config.ini
In order to use the application, a config.ini file is necessary for specifying the input/output databases connections. The format is the following:

```
[input]
driver=
host=
database=
user=
password=

[output]
driver=
host=
database=
user=
password=
```

## Neo4j

The application will generate the graph into the output database under the tables: *node*, *edge* and *property*. Also, 3 CSV files are generated in the root folder called: **nodes.csv**, **edges.csv** and **properties.csv**. You can use these files as input for a Neo4j instance by running the script presented in file **script.cypher** located in this repository. 

Use Neo4j for visualization. Need plugin APOC installed (dynamic labels) -> https://neo4j-contrib.github.io/neo4j-apoc-procedures/

