#pragma once
#include <vector>

int main(int argc, char** argv);
std::vector<int> readPolinomFromFile(std::string fileName);
void multiplyO2Main(std::vector<int>& polinom1, std::vector<int>& polinom2, int nrProcs);
void multiplyO2Worker();
void printPolinom(std::vector<int> polinom);