import json

with open('swagger.json',) as f:
	data = json.load(f)

schemasAvailable = list()
schemasReferenced = set()
for k,v in data.items():
	if k=="components":
		for k2,v2 in v["schemas"].items():
			schemasAvailable.append(k2)
	elif k=="paths":
		for k2,v2 in v.items():
			for k3,v3 in v2.items():
				if "parameters" in v3:
					for x in v3["parameters"]:
						if "$ref" in x["schema"]:
							schemasReferenced.add(x["schema"]["$ref"][21:])
				for k4,v4 in v3["responses"].items():
					cur = v4["content"]["application/json"]["schema"]
					if "$ref" in cur:
						schemasReferenced.add(cur["$ref"][21:])
					if "properties" in cur:
						for x,y in cur["properties"].items():
							schemasReferenced.add(y["$ref"][21:])

schemasIndirectlyReferenced = set()
schemasIndirectlyReferenced.update(schemasReferenced)

schemasReferencedQueue = list(schemasReferenced)

while len(schemasReferencedQueue) > 0:
	ele = schemasReferencedQueue[0]
	schemasReferencedQueue.remove(ele)
	schemasIndirectlyReferenced.add(ele)
	for k,v in data["components"]["schemas"][ele]["properties"].items():
		if "$ref" in v:
			schemaName = v["$ref"][21:]
			if not (schemaName in schemasIndirectlyReferenced):
				schemasReferencedQueue.append(schemaName)
		if "items" in v and "$ref" in v["items"]:
			schemaName = v["items"]["$ref"][21:]
			if not (schemaName in schemasIndirectlyReferenced):
				schemasReferencedQueue.append(schemaName)

print("Schemas listed: ", len(schemasAvailable))
print("Schemas directly referenced: ", len(schemasReferenced))
print("Schemas (in)directly referenced in total: ", len(schemasIndirectlyReferenced))

for e in schemasIndirectlyReferenced:
	schemasAvailable.remove(e)

print("Found ", len(schemasAvailable), " unused schemas: ")
for s in schemasAvailable:
	print(s)