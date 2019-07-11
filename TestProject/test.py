import subprocess
import os

os.chdir("./TestProject")

cmd = "./gradlew test"
result = subprocess.Popen(cmd.split(), stdout=subprocess.PIPE).communicate()[0]



if("SUCCESSFUL" in result):
    status = "TRUE"

if("FAILED" in result):
    status = "FALSE"


print(status)
