import json
import math
import csv
import autograd.numpy as np
from autograd import grad, elementwise_grad

# Autogradでは関数に引数に対して偏微分を行う
# parameterの偏微分を行うために引数でtrainingCaseを受け渡さずにグローバル変数にしている
# Autogradで特定の引数に対して偏微分を行うことができれば解決する
trainingCases = []

def prophetModel():
    """
    以下の論文掲載のProphetの学習アルゴリズムを元にして修正パッチの学習を行う

    Long, Fan, and Martin Rinard. 
    'Automatic patch generation by learning correct code.' 
    Proceedings of the 43rd Annual ACM SIGPLAN-SIGACT Symposium on Principles of Programming Languages. 2016.

    Raises:
        Exception: トレーニングケースが空の場合

    Returns:
        ndarray: 学習済みパラメータ 
    """
    trainingCaseSize = len(trainingCases)
    print("trainingCaseSize: " + str(trainingCaseSize))
    if trainingCaseSize == 0:
        raise Exception("The training cases is empty.")
    vectorSize = len(trainingCases[0]['correct'])
    print("vectorSize: " + str(vectorSize))
    parameter = np.zeros(vectorSize)
    bestParameter = parameter
    learningRate = 1
    bestRankRateSum = 1
    count = 0
    while count < 200:
        # 勾配を求める
        gradF = grad(f)
        gradValue = gradF(parameter)

        # パラメータ更新
        parameter = parameter + learningRate * gradValue

        rankRateSum = 0
        for i in range(trainingCaseSize): 
            total = len(trainingCases[i]['all'])
            correctScore = g(trainingCases[i]['correct'], trainingCases[i]['all'], parameter)
            allScores = []
            for j in range(len(trainingCases[i]['all'])):
                allScores.append(g(trainingCases[i]['all'][j], trainingCases[i]['all'], parameter))
            allScores.sort(reverse=True)
            rank = len(allScores)
            for j in range(len(allScores)):
                if correctScore > allScores[j]:
                    rank = j + 1
                    break
            rankRateSum = rankRateSum + (float(rank) / float(total)) / float(trainingCaseSize)
        print("rankRateSum: " + str(rankRateSum))
        if rankRateSum < bestRankRateSum:
            print("update")
            bestParameter = parameter
            bestRankRateSum = rankRateSum
            count = 0
        else:
            print("no update")
            count += 1
            if learningRate > 0.01:
                learningRate = 0.9 * learningRate
    return bestParameter

def exportAsCsv(res):
    """学習済みパラメータをCSVファイルとして書き出す

    Args:
        res (ndarray): 学習済みパラメータ
    """
    with open('../result/para.csv', 'w') as f:
        writer = csv.writer(f)
        writer.writerow(res)
    return

def g(vector, allVectors, parameter):
    """ 関数f中で用いられる関数

    Args:
        vector (ndarray): 開発者の行った正しいパッチの特徴ベクトル（正例パッチ）
        allVectors (ndarray of ndarray): jProphetの出力しうる全てのパッチ候補
        parameter (ndarray): 学習パラメータ

    Returns:
        [type]: [description]
    """
    dotRet = np.dot(vector, parameter)
    numerator = np.exp(dotRet)
    denominator = 0
    for vec in allVectors:
        denominator += np.exp(np.dot(vec, parameter))
    return numerator / denominator

def f(parameter):
    """この関数の値を最大化することを考える

    Args:
        parameter (ndarray): 学習パラメータ

    Returns:
        float: 確率表現
    """
    size = len(trainingCases)
    sum = 0
    for case in trainingCases:
        sum += np.log(g(case['correct'], case['all'], parameter))
    return sum / size * 0.85

if __name__ == '__main__':
    json_open = open('../result/feature-vector.json', 'r')
    cases = json.load(json_open)
    caseNps = np.empty(0)
    for case in cases:
        if len(case['generated']) == 0:
            continue
        caseNp = {}
        caseNp['correct'] = np.asarray(case['correct'])
        caseNp['all'] = np.empty([0, len(case['generated'][0])])
        caseNp['all'] = np.append(caseNp['all'], [caseNp['correct']], axis=0)
        for generatedVector in case['generated']:
            caseNp['all'] = np.append(caseNp['all'], [generatedVector], axis=0)
        caseNps = np.append(caseNps, caseNp)
            
    trainingCases = caseNps
    ret = prophetModel()
    exportAsCsv(ret)