import json 
import time
from random import *



with open ('data.json') as f:
    data = json.load(f)

geos =[]

begin = randint(0, len(data["features"]))
print(begin)
eind = randint(begin, len(data["features"]))

for x in range(begin, eind):
    poly = {"type": "Feature","geometry":{
        'type': 'Point',
        'coordinates': data["features"][x]["geometry"]["coordinates"]
    }, "properties":{"userID":str(x)}}
    geos.append(poly)
        
    cs = data["features"][x]["geometry"]["coordinates"]
    print(cs)

geometries = {
    'type': 'FeatureCollection',
    'features': geos,
}

geo_str = json.dumps(geometries, indent=4)

    
k = open("geodata.geojson", "w")
k.write(geo_str)


