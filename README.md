# jProphet
Automatic Program Repair System


# 実装計画

## メインフロー草案

```python:flow.py
# Input:  original program p
#         test suite T
#         learned model parameter vector θ
# Output: list of validated patches



ranks = faultLocalization(p, T)

for line in p:
    abstCandidates = applyTemplate(line)
    sort(abstCandidates, ranks[line], θ)
    for ac in abstCandidates:
        condValues = searchCondValue(ac)
        concreteCandidates = generateCondition(ac, condValues)
        for cc in concreteCandidates:
            patch = test(cc, T)
        return patch
```

