// Lab3C++.cpp : This file contains the 'main' function. Program execution begins and ends there.
//

#include "pch.h"
#include <iostream>
#include "Read2MatrixResult.h"
#include "Lab3++AsyncFuture.h"
#include <fstream>
#define _CRTDBG_MAP_ALLOC
#include <stdlib.h>
#include <crtdbg.h>
#include <chrono>
#include <math.h>
#include <vector>
#include <future>

int main()
{
	//sumProgram();
	prodProgram();
	_CrtDumpMemoryLeaks();
}

static Read2MatrixResult* readTwoMatrixesFromFile(std::string fileName) {
	std::ifstream file;
	file.open(fileName);

	int n1, m1;
	file >> n1 >> m1;
	int** matrix1;
	matrix1 = new int*[n1];
	for (int i = 0; i < n1; i++) {
		matrix1[i] = new int[m1];
		for (int j = 0; j < m1; j++) {
			file >> matrix1[i][j];
		}
	}

	int n2, m2;
	file >> n2 >> m2;
	int** matrix2;
	matrix2 = new int*[n2];
	for (int i = 0; i < n2; i++) {
		matrix2[i] = new int[m2];
		for (int j = 0; j < m2; j++) {
			file >> matrix2[i][j];
		}
	}

	Read2MatrixResult* read2matrixresult = new Read2MatrixResult(matrix1, matrix2, n1, m1, n2, m2);

	return read2matrixresult;
}

void doSum(int** matrix1, int** matrix2, int**result, int nrCols, int elemStart, int elemEnd) {
	int lineStart = elemStart / nrCols;
	int lineEnd = elemEnd / nrCols;
	int columnStart = elemStart % nrCols;
	int columnEnd = elemEnd % nrCols;

	for (int i = lineStart; i <= lineEnd; i++) {
		int currentJstart = 0;
		int currentJend = nrCols - 1;

		if (i == lineEnd && i == lineStart) {
			currentJstart = columnStart;
			currentJend = columnEnd;
		}
		else if (i == lineStart) {
			currentJstart = columnStart;
			currentJend = nrCols - 1;
		}
		else if (i == lineEnd) {
			currentJend = columnEnd;
		}

		for (int j = currentJstart; j <= currentJend; j++) {
			result[i][j] = matrix1[i][j] + matrix2[i][j];
		}
	}
}

void doProd(int** matrix1, int** matrix2, int**result, int nrColsResult, int nrColsMatrix1, int elemStart, int elemEnd) {
	int lineStart = elemStart / nrColsResult;
	int lineEnd = elemEnd / nrColsResult;
	int columnStart = elemStart % nrColsResult;
	int columnEnd = elemEnd % nrColsResult;

	for (int i = lineStart; i <= lineEnd; i++) {
		int currentJstart = 0;
		int currentJend = nrColsResult - 1;

		if (i == lineEnd && i == lineStart) {
			currentJstart = columnStart;
			currentJend = columnEnd;
		}
		else if (i == lineStart) {
			currentJstart = columnStart;
			currentJend = nrColsResult - 1;
		}
		else if (i == lineEnd) {
			currentJend = columnEnd;
		}

		for (int j = currentJstart; j <= currentJend; j++) {
			int sum = 0;
			for (int k = 0; k < nrColsMatrix1; k++) {
				sum += matrix1[i][k] * matrix2[k][j];
			}
			result[i][j] = sum;
		}
	}
}

void sumProgram()
{
	Read2MatrixResult* sumMatrixes = readTwoMatrixesFromFile("sum_matrix.txt");
	int sumResultSize = sumMatrixes->getN1() * sumMatrixes->getM1();

	for (int nrThreads = 1; nrThreads <= sumResultSize; nrThreads++) {
		int nrThreadsCopy = nrThreads;
		int sumResultSizeCopy = sumResultSize;
		int startElement = 0;
		int** result;
		result = new int*[sumMatrixes->getN1()];
		for (int i = 0; i < sumMatrixes->getN1(); i++) {
			result[i] = new int[sumMatrixes->getM1()];
		}

		//start threads
		auto begin = std::chrono::high_resolution_clock::now();

		std::vector<std::future<void>> futures;
		for (int j = 1; j <= nrThreads; j++) {
			int threadShare;
			if (sumResultSizeCopy % nrThreadsCopy == 0) {
				threadShare = (int)((double)sumResultSizeCopy / nrThreadsCopy);
			}
			else {
				threadShare = (int)ceil((double)sumResultSizeCopy / nrThreadsCopy);
			}

			futures.push_back(std::async(doSum, sumMatrixes->getMatrix1(), sumMatrixes->getMatrix2(), result, sumMatrixes->getM1(), startElement, startElement + threadShare - 1));

			startElement += threadShare;
			nrThreadsCopy -= 1;
			sumResultSizeCopy -= threadShare;
		}

		//wait for threads to be done
		for (std::vector<std::future<void>>::iterator it = futures.begin(); it != futures.end(); ++it) {
			(*it).get();
		}

		//calculate elapsed time
		auto end = std::chrono::high_resolution_clock::now();
		auto dur = end - begin;
		auto ms = std::chrono::duration_cast<std::chrono::milliseconds>(dur).count();
		std::cout << "Nr. Threads: " << nrThreads << " ---> " << ms << " miliseconds." << std::endl;

		for (int i = 0; i < sumMatrixes->getN1(); i++) {
			delete[] result[i];
		}
		delete[] result;
	}


	delete sumMatrixes;
}

void prodProgram()
{
	Read2MatrixResult* prodMatrixes = readTwoMatrixesFromFile("prod_matrix.txt");
	int prodResultSize = prodMatrixes->getN1() * prodMatrixes->getM2();

	for (int nrThreads = 1; nrThreads <= prodResultSize; nrThreads++) {
		int nrThreadsCopy = nrThreads;
		int prodResultSizeCopy = prodResultSize;
		int startElement = 0;
		int** result;
		result = new int*[prodMatrixes->getN1()];
		for (int i = 0; i < prodMatrixes->getN1(); i++) {
			result[i] = new int[prodMatrixes->getM2()];
		}

		//start threads
		auto begin = std::chrono::high_resolution_clock::now();

		std::vector<std::future<void>> futures;

		for (int j = 1; j <= nrThreads; j++) {
			int threadShare;
			if (prodResultSizeCopy % nrThreadsCopy == 0) {
				threadShare = (int)((double)prodResultSizeCopy / nrThreadsCopy);
			}
			else {
				threadShare = (int)ceil((double)prodResultSizeCopy / nrThreadsCopy);
			}
			futures.push_back(std::async(doProd, prodMatrixes->getMatrix1(), prodMatrixes->getMatrix2(), result, prodMatrixes->getM2(), prodMatrixes->getM1(), startElement, startElement + threadShare - 1));


			startElement += threadShare;
			nrThreadsCopy -= 1;
			prodResultSizeCopy -= threadShare;
		}

		//wait for threads to be done
		for (std::vector<std::future<void>>::iterator it = futures.begin(); it != futures.end(); ++it) {
			(*it).get();
		}

		//calculate elapsed time
		auto end = std::chrono::high_resolution_clock::now();
		auto dur = end - begin;
		auto ms = std::chrono::duration_cast<std::chrono::milliseconds>(dur).count();
		std::cout << "Nr. Threads: " << nrThreads << " ---> " << ms << " miliseconds." << std::endl;

		for (int i = 0; i < prodMatrixes->getN1(); i++) {
			delete[] result[i];
		}
		delete[] result;
	}

	delete prodMatrixes;
}