#pragma once
#include <vector>

std::vector<int> multiplyKaratsubaWrapper(std::vector<int> polinom1, std::vector<int> polinom2, int nrProcs);
std::vector<int> multiplyKaratsubaRecursive(std::vector<int> polinom1, std::vector<int> polinom2, int me, int nrProcs);
void multiplyKaratsubaWorker(int me);

std::vector<int> addVectors(std::vector<int> vector1, std::vector<int> vector2);
std::vector<int> substractVectors(std::vector<int> vector1, std::vector<int> vector2);