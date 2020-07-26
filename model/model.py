import json
import math

trainingCases = []

def prophetModel(trainingCases):
    vectorSize = len(trainingCases[0]['correct'])
    print("vectorSize: " + str(vectorSize))
    parameter = [0] * vectorSize
    ret = f(trainingCases, parameter)
    print("prophet ret: " + str(ret))
    return ret

def output(res):
    return


def dotProduct(vector1, vector2):
    if len(vector1) != len(vector2):
        raise Error("vector1 and vector2 must be the same length")
    sum = 0
    for i in range(len(vector1)):
        sum += vector1[i] * vector2[i]
    return sum

def g(vector, allVectors, parameter):
    numerator = math.exp(dotProduct(vector, parameter))
    denominator = 0
    for vec in allVectors:
        denominator += math.exp(dotProduct(vec, parameter))
    ret = numerator / denominator
    print("g ret: " + str(ret))
    return ret


def f(trainingCases, parameter):
    size = len(trainingCases)
    print("trainingCase size: " + str(size))
    sum = 0
    for case in trainingCases:
        sum += math.log(g(case['correct'], case['generated'], parameter))
    ret = sum / size * 0.85
    print("f ret: " + str(ret))
    return ret

if __name__ == '__main__':
    json_open = open('../result/feature-vector.json', 'r')
    trainingCases = json.load(json_open)
    print(trainingCases)
    prophetModel(trainingCases)