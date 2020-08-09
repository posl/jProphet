import json
import math
import csv
import autograd.numpy as np
from autograd import grad, elementwise_grad

trainingCases = []

def prophetModel():
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
        ret = f(parameter)
        gradF = grad(f)
        gradValue = gradF(parameter)

        parameter = parameter + learningRate * gradValue
        # print(parameter)

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
                # print("correct score: " + str(correctScore) + " all scores " + str(j) + ": " + str(allScores[j]))
                if correctScore > allScores[j]:
                    rank = j + 1
                    break
            rankRateSum = rankRateSum + (float(rank) / float(total)) / float(trainingCaseSize)
        print("rankRateSum: " + str(rankRateSum))
        if rankRateSum < bestRankRateSum:
            bestParameter = parameter
            bestRankRateSum = rankRateSum
            count = 0
            print("update")
        else:
            print("not update")
            count += 1
            if learningRate > 0.01:
                learningRate = 0.9 * learningRate
    return bestParameter

def exportAsCsv(res):
    with open('result/para.csv', 'w') as f:
        writer = csv.writer(f)
        writer.writerow(res)
    return

def g(vector, allVectors, parameter):
    dotRet = np.dot(vector, parameter)
    numerator = np.exp(dotRet)
    denominator = 0
    for vec in allVectors:
        denominator += np.exp(np.dot(vec, parameter))
    ret = numerator / denominator
    return ret

def f(parameter):
    size = len(trainingCases)
    sum = 0
    for case in trainingCases:
        sum += np.log(g(case['correct'], case['all'], parameter))
    ret = sum / size * 0.85
    return ret

if __name__ == '__main__':
    json_open = open('result/feature-vector.json', 'r')
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