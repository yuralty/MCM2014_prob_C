import os
import json

rootDir = 'C:\Users\lenovo\IdeaProjects\Project3\data'
s1 = 0
s2 = 0
s3 = 0
for root,dirs,files in os.walk(rootDir):
    for filespath in files:
        path = os.path.join(root,filespath)
        print path
        file = open(path, 'r')
        rr = 0;
        
        for line in file:
            obj = json.loads(line)
            try:
                rr += 1.0 * obj["cites"] / obj["cited"]
                s1 += obj["cites"]
                s2 += obj["cited"]
                s3 += 1
            except:
                print type(obj["cites"]), obj["cites"]
        print rr
        
        print '-----------------------------------------------------'
print 1.0 * s1 / s3, 1.0 * s2 / s3;
