#include "pch.h"
#include "myutils.h"
#include <vector>
#include <fstream>
#include <sstream>
#include <iostream>

std::vector<int> readPolinomFromFile(std::string fileName) {
	std::vector<int> polinom;
	std::ifstream file(fileName);
	std::string line;

	int coeff;
	while (file >> coeff) {
		polinom.push_back(coeff);
	}

	return polinom;
}

void printPolinom(std::vector<int> polinom) {
	for (int i = 0; i < polinom.size(); i++) {
		std::cout << polinom[i] << "x^" << i;
		if (i != polinom.size() - 1) {
			std::cout << " + ";
		}
	}
	std::cout << "\n";
}