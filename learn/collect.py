from github import Github
import requests
import re
import os
import json
import random

def collectFixCommits(token):
    """GitHubのリポジトリから修正コミットの情報を収集する

    Args:
        token (str): GitHubのパーソナルアクセストークン

    Returns:
        dict: 修正コミットの情報（sha, ファイルURLなど）
    """
    g = Github(token)
    repoNames = []
    with open('model/repoNames.txt', mode='r') as f:
        repoNames = list(map(lambda name: name.rstrip('\n'), f.readlines()))
    print(f'repos: {repoNames}')

    fixCommits = []
    for repoName in repoNames:
        repo = g.get_repo(repoName)
        commits = repo.get_commits()
        for commit in commits:
            message: str = commit.commit.message
            isFixCommit = 'fix' in message or 'Fix' in message
            isNotMergeCommit = 'Merge pull' not in message and 'Merge branch' not in message
            if isFixCommit and isNotMergeCommit:
                print(commit.sha)
                fixCommit = {"repoName": repoName, "sha": commit.sha, "files": []}
                for file in commit.files:
                    print(file.raw_url)
                    print(file.filename)
                    for i in range(commits.totalCount):
                        if commits[i].sha == commit.sha:
                            prevCommit = commits[i + 1]
                            prevFileUrl = f'https://github.com/{repoName}/raw/{prevCommit.sha}/{file.filename}'
                            fixCommit["files"].append({"filename": file.filename, "fixed_url": file.raw_url, "original_url": prevFileUrl})
                            break
                fixCommits.append(fixCommit)
    return fixCommits

def export(fixCommits, genSize, exportPath):
    """修正コミットの修正前後のファイルを出力する

    Args:
        fixCommits (list of dict): 修正コミットのリスト
        genSize (int): 出力する修正パッチの数の上限(対象コミットからランダムに選定)
        exportPath (str): 出力先パス
    """
    # シャッフルして先頭からgenSize分だけ出力
    random.shuffle(fixCommits)

    for fixCommit in fixCommits[0:genSize]:
        for fileDict in fixCommit['files']:
            originalUrl = fileDict['original_url']
            fixedUrl = fileDict['fixed_url']
            try:
                originalFileString = requests.get(originalUrl).text
                fixedFileString = requests.get(fixedUrl).text

                pattern = '.+\/(.+\.java)$'
                isJavaFile = re.match(pattern, fileDict['filename'])
                if isJavaFile:
                    fileName = isJavaFile.group(1)
                    originalDirPath = f'{exportPath}/{fixCommit["repoName"].replace("/", "_")}-{fixCommit["sha"][0:5]}-{fileName}/original/'
                    fixedDirPath    = f'{exportPath}/{fixCommit["repoName"].replace("/", "_")}-{fixCommit["sha"][0:5]}-{fileName}/fixed/'
                    originalPath = originalDirPath + fileName
                    fixedPath    = fixedDirPath + fileName
                    os.makedirs(originalDirPath, exist_ok=True)
                    os.makedirs(fixedDirPath, exist_ok=True)
                    with open(originalPath, mode='w') as f:
                        f.write(originalFileString)
                    with open(fixedPath, mode='w') as f:
                        f.write(fixedFileString)
            except requests.exceptions.RequestException as err:
                print(err)

if __name__ == '__main__':
    json_open = open('model/collect-config.json', 'r')
    config = json.load(json_open)
    token = config['token']
    genSize = config['maxGenSize']
    outputPath = config['outputPath']
    fixCommits = collectFixCommits(token)
    export(fixCommits, genSize, outputPath)
