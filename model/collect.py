from github import Github
import requests
import re
import os

token = 'b994d276624b9069ddf6bc190618fc4cdcc0d648'

g = Github(token)

# for repo in g.get_user().get_repos():
#     # print(repo.name)
#     if repo.name == 'jProphet':
#         print(repo.full_name)

print("-------------------------------------")
jpro = g.get_repo('posl/jProphet')
commitDicts = []
commits = jpro.get_commits()
for commit in commits[0:500]:
    message: str = commit.commit.message
    if 'fix' in message or 'Fix' in message:
        if 'Merge pull' not in message and 'Merge branch' not in message: 
            print(commit.sha)
            commitDict = {"sha": commit.sha, "files": []}
            # print(message)
            for file in commit.files:
                print(file.raw_url)
                print(file.filename)
                for i in range(commits.totalCount):
                    if commits[i].sha == commit.sha:
                        prevCommit = commits[i + 1]
                        prevFileUrl: str = "https://github.com/posl/jProphet/raw/" + str(prevCommit.sha) + "/" + str(file.filename);
                        commitDict["files"].append({"filename": file.filename, "fixed_url": file.raw_url, "original_url": prevFileUrl})
                        break

            commitDicts.append(commitDict)
print(commitDicts)

for commitDict in commitDicts:
    for fileDict in commitDict['files']:
        originalUrl = fileDict['original_url']
        fixedUrl = fileDict['fixed_url']
        try:
            originalFileString = requests.get(originalUrl).text
            fixedFileString = requests.get(fixedUrl).text

            pattern = '.+\/(.+\.java)$'
            result = re.match(pattern, fileDict['filename'])
            if result:
                fileName = result.group(1)
                originalDirPath = "result/cases/" + commitDict['sha'][0:5] + fileName + "/original/"
                fixedDirPath = "result/cases/" + commitDict['sha'][0:5] + fileName + "/fixed/"
                originalPath = originalDirPath + fileName
                fixedPath = fixedDirPath + fileName
                os.makedirs(originalDirPath, exist_ok=True)
                os.makedirs(fixedDirPath, exist_ok=True)
                with open(originalPath, mode='w') as f:
                    f.write(originalFileString)
                with open(fixedPath, mode='w') as f:
                    f.write(fixedFileString)
        except requests.exceptions.RequestException as err:
            print(err)



# repos = g.search_repositories("Java", sort='stars', order='desc')
# for repo in repos[0: 1]:
#     print("name: " + str(repo.name))
#     for comment in repo.get_comments()[0:3]:
#         print(comment.body)