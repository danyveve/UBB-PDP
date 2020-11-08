#include "pch.h"
#include "multiplyO2.h"
#include <vector>
#include <algorithm>
#include <math.h>
#include "mpi.h"
#include "myutils.h"
#include <iostream>


void multiplyO2Main(std::vector<int>& polinom1, std::vector<int>& polinom2, int nrProcs) {
	int nrProcsCopy = nrProcs;
	//split the first vector into the number of procs, then send parts of vector 1 and vector 2 to the workers do to some computations
	int elementsRemaining = polinom1.size();
	int polinom2Size = polinom2.size();
	int startShare = 0;
	int child = 1;

	int mainShare = (int)ceil(elementsRemaining / nrProcsCopy);
	startShare += mainShare;
	elementsRemaining -= mainShare;
	nrProcsCopy -= 1;

	//start children
	while (elementsRemaining != 0 && nrProcsCopy != 0) {
		int share = (int)ceil(elementsRemaining / nrProcsCopy);
		int shares[3];
		shares[0] = startShare;
		shares[1] = startShare + share; //endShare
		shares[2] = polinom2Size;
		MPI_Send(shares, 3, MPI_INT, child, 1, MPI_COMM_WORLD);
		MPI_Send(polinom1.data() + startShare, share, MPI_INT, child, 2, MPI_COMM_WORLD);
		MPI_Send(polinom2.data(), polinom2Size, MPI_INT, child, 3, MPI_COMM_WORLD);

		startShare += share;
		elementsRemaining -= share;
		nrProcsCopy -= 1;
		child += 1;
	}

	//compute the part of the main
	std::vector<int> mainPartResult(mainShare + polinom2.size() - 1, 0);
	for (int i = 0; i < mainShare; i++) {
		for (int j = 0; j < polinom2.size(); j++) {
			mainPartResult[i + j] = mainPartResult[i + j] + polinom1[i] * polinom2[j];
		}
	}

	//compute the final result
	std::vector<std::vector<int>> finalResultMatrix(nrProcs, std::vector<int>(polinom1.size() + polinom2.size() - 1, 0));
	std::copy(std::begin(mainPartResult), std::end(mainPartResult), std::begin(finalResultMatrix[0]));

	//receive result from all children
	for (int children = 1; children < nrProcs; children++) {
		int sharesChildren[2];
		MPI_Status status;
		MPI_Recv(sharesChildren, 2, MPI_INT, children, 4, MPI_COMM_WORLD, &status);

		std::vector<int> resultChildrenChunk;
		resultChildrenChunk.resize(sharesChildren[1]);
		MPI_Recv(resultChildrenChunk.data(), sharesChildren[1], MPI_INT, children, 5, MPI_COMM_WORLD, &status);

		std::copy(std::begin(resultChildrenChunk), std::end(resultChildrenChunk), std::begin(finalResultMatrix[children]) + sharesChildren[0]);
	}

	std::vector<int> finalPolinom;


	for (int i = 0; i < finalResultMatrix.size(); i++) {
		for (int j = 0; j < finalResultMatrix[0].size(); j++)
			std::cout << finalResultMatrix[i][j] << " ";
		std::cout << "\n";
	}

	for (int j = 0; j < finalResultMatrix[0].size(); j++) {
		int coeff = 0;
		for (int i = 0; i < finalResultMatrix.size(); i++)
			coeff += finalResultMatrix[i][j];
		finalPolinom.push_back(coeff);
	}

	printPolinom(finalPolinom);
}

void multiplyO2Worker() {
	//first receive the shares and the size of polinom2
	int shares[3];
	MPI_Status status;
	MPI_Recv(shares, 3, MPI_INT, 0, 1, MPI_COMM_WORLD, &status);

	std::vector<int> polinom1Chunk;
	polinom1Chunk.resize(shares[1] - shares[0]);
	std::vector<int> polinom2;
	polinom2.resize(shares[2]);
	MPI_Recv(polinom1Chunk.data(), shares[1] - shares[0], MPI_INT, 0, 2, MPI_COMM_WORLD, &status);
	MPI_Recv(polinom2.data(), shares[2], MPI_INT, 0, 3, MPI_COMM_WORLD, &status);

	std::vector<int> result(polinom1Chunk.size() + polinom2.size() - 1, 0);

	//compute the chunk received multiplied with the polinom2
	for (int i = 0; i < polinom1Chunk.size(); i++) {
		for (int j = 0; j < polinom2.size(); j++) {
			result[i + j] = result[i + j] + polinom1Chunk[i] * polinom2[j];
		}
	}

	shares[1] = result.size();
	MPI_Send(shares, 2, MPI_INT, 0, 4, MPI_COMM_WORLD);
	MPI_Send(result.data(), result.size(), MPI_INT, 0, 5, MPI_COMM_WORLD);
}
