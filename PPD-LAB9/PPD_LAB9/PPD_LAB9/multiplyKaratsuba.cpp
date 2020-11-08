#include "pch.h"
#include "multiplyKaratsuba.h"
#include <functional> 
#include <algorithm>
#include "myutils.h"
#include "mpi.h"
#include <iostream>

std::vector<int> multiplyKaratsubaWrapper(std::vector<int> polinom1, std::vector<int> polinom2, int nrProcs) {
	return multiplyKaratsubaRecursive(polinom1, polinom2, 0, nrProcs);
}

std::vector<int> multiplyKaratsubaRecursive(std::vector<int> polinom1, std::vector<int> polinom2, int me, int nrProcs) {
	if (polinom1.size() == 1) {
		int scalar = polinom1[0];
		std::transform(polinom2.begin(), polinom2.end(), polinom2.begin(),
			std::bind(std::multiplies<int>(), std::placeholders::_1, scalar));
		return polinom2;
	}
	else if (polinom2.size() == 1) {
		int scalar = polinom2[0];
		std::transform(polinom1.begin(), polinom1.end(), polinom1.begin(),
			std::bind(std::multiplies<int>(), std::placeholders::_1, scalar));
		return polinom1;
	}

	int smallerDegree = std::min(polinom1.size(), polinom2.size());
	int middle = (int)(smallerDegree / 2);

	std::vector<int> low1 = std::vector<int>(polinom1.begin(), polinom1.begin() + middle);
	std::vector<int> high1 = std::vector<int>(polinom1.begin() + middle, polinom1.end());
	std::vector<int> low2 = std::vector<int>(polinom2.begin(), polinom2.begin() + middle);
	std::vector<int> high2 = std::vector<int>(polinom2.begin() + middle, polinom2.end());
	
	std::vector<int> product0;
	std::vector<int> product1;
	std::vector<int> product2;

	if (nrProcs >= 3) {
		//start sending data
		int sizes[3];
		sizes[0] = (int) (nrProcs / 3);
		sizes[1] = low1.size();
		sizes[2] = low2.size();
		MPI_Send(sizes, 3, MPI_INT, me + sizes[0], 1, MPI_COMM_WORLD);
		MPI_Send(low1.data(), low1.size(), MPI_INT, me + sizes[0], 2, MPI_COMM_WORLD);
		MPI_Send(low2.data(), low2.size(), MPI_INT, me + sizes[0], 3, MPI_COMM_WORLD);

		std::vector<int> low1high1 = addVectors(low1, high1);
		std::vector<int> low2high2 = addVectors(low2, high2);
		sizes[1] = low1high1.size();
		sizes[2] = low2high2.size();
		MPI_Send(sizes, 3, MPI_INT, me + 2 * sizes[0], 1, MPI_COMM_WORLD);
		MPI_Send(low1high1.data(), low1high1.size(), MPI_INT, me + 2 * sizes[0], 2, MPI_COMM_WORLD);
		MPI_Send(low2high2.data(), low2high2.size(), MPI_INT, me + 2 * sizes[0], 3, MPI_COMM_WORLD);


		product2 = multiplyKaratsubaRecursive(high1, high2, me, sizes[0]);

		//receive the results
		int prod0Size, prod1Size, prod2Size;
		MPI_Status status;
		MPI_Recv(&prod0Size, 1, MPI_INT, me + sizes[0], 4, MPI_COMM_WORLD, &status);
		MPI_Recv(&prod1Size, 1, MPI_INT, me + 2 * sizes[0], 4, MPI_COMM_WORLD, &status);
		product0.resize(prod0Size);
		product1.resize(prod1Size);
		MPI_Recv(product0.data(), prod0Size, MPI_INT, me + sizes[0], 5, MPI_COMM_WORLD, &status);
		MPI_Recv(product1.data(), prod1Size, MPI_INT, me + 2 * sizes[0], 5, MPI_COMM_WORLD, &status);
		nrProcs -= 2 * ((int)(nrProcs / 3));
	}
	else {
		product0 = multiplyKaratsubaRecursive(low1, low2, me, 1);
		product1 = multiplyKaratsubaRecursive(addVectors(low1, high1), addVectors(low2, high2), me, 1);
		product2 = multiplyKaratsubaRecursive(high1, high2, me, 1);
	}

	//combine the results
	std::vector<int> highPolinom = std::vector<int>(middle * 2, 0);
	highPolinom.insert(std::end(highPolinom), std::begin(product2), std::end(product2));

	std::vector<int> intermeiaryPolinom = std::vector<int>(middle, 0);
	std::vector<int> substractedVectors = substractVectors(substractVectors(product1, product2), product0);
	intermeiaryPolinom.insert(std::end(intermeiaryPolinom), std::begin(substractedVectors), std::end(substractedVectors));

	return addVectors(highPolinom, addVectors(intermeiaryPolinom, product0));
}




void multiplyKaratsubaWorker(int me) {
	//first receive the shares and the size of polinom2
	int sizes[3];
	MPI_Status status;
	MPI_Recv(sizes, 3, MPI_INT, MPI_ANY_SOURCE, 1, MPI_COMM_WORLD, &status);

	int parent = status.MPI_SOURCE;


	std::vector<int> polinom1;
	polinom1.resize(sizes[1]);
	std::vector<int> polinom2;
	polinom2.resize(sizes[2]);
	MPI_Recv(polinom1.data(), sizes[1], MPI_INT, parent, 2, MPI_COMM_WORLD, &status);
	MPI_Recv(polinom2.data(), sizes[2], MPI_INT, parent, 3, MPI_COMM_WORLD, &status);

	std::vector<int> result = multiplyKaratsubaRecursive(polinom1, polinom2, me, sizes[0]);
	int resultSize = result.size();

	MPI_Send(&resultSize, 1, MPI_INT, parent, 4, MPI_COMM_WORLD);
	MPI_Send(result.data(), resultSize, MPI_INT, parent, 5, MPI_COMM_WORLD);
}
std::vector<int> addVectors(std::vector<int> vector1, std::vector<int> vector2) {
	int i = 0;
	std::vector<int> result;
	int smallestSize = std::min(vector1.size(), vector2.size());
	while (i < smallestSize) {
		result.push_back(vector1[i] + vector2[i]);
		i++;
	}

	if (vector1.size() == smallestSize) {
		std::vector<int> vector2Remainder = std::vector<int>(vector2.begin() + smallestSize, vector2.end());
		result.insert(std::end(result), std::begin(vector2Remainder), std::end(vector2Remainder));
	}
	else {
		std::vector<int> vector1Remainder = std::vector<int>(vector1.begin() + smallestSize, vector1.end());
		result.insert(std::end(result), std::begin(vector1Remainder), std::end(vector1Remainder));
	}

	return result;
}
std::vector<int> substractVectors(std::vector<int> vector1, std::vector<int> vector2) {
	int i = 0;
	std::vector<int> result;
	int smallestSize = std::min(vector1.size(), vector2.size());
	while (i < smallestSize) {
		result.push_back(vector1[i] - vector2[i]);
		i++;
	}

	if (vector1.size() == smallestSize) {
		std::vector<int> vector2Remainder = std::vector<int>(vector2.begin() + smallestSize, vector2.end());
		std::transform(vector2Remainder.begin(), vector2Remainder.end(), vector2Remainder.begin(),
			std::bind(std::multiplies<int>(), std::placeholders::_1, -1));
		result.insert(std::end(result), std::begin(vector2Remainder), std::end(vector2Remainder));
	}
	else {
		std::vector<int> vector1Remainder = std::vector<int>(vector1.begin() + smallestSize, vector1.end());
		result.insert(std::end(result), std::begin(vector1Remainder), std::end(vector1Remainder));
	}

	return result;
}
