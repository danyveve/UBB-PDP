#include "pch.h"
#include <iostream>
#include "mpi.h"
#include <vector>
#include <fstream>
#include <sstream>
#include "PPD_LAB9.h"
#include <math.h>
#include <algorithm>
#include <chrono>
#include "multiplyO2.h"
#include "multiplyKaratsuba.h"

int main(int argc, char** argv)
{
	MPI_Init(0, 0);
	int me;
	int nrMachines;
	MPI_Comm_size(MPI_COMM_WORLD, &nrMachines);
	MPI_Comm_rank(MPI_COMM_WORLD, &me);

	if (me == 0) { //main PC
		std::vector<int> polinom1 = readPolinomFromFile("polinom1.txt");
		std::vector<int> polinom2 = readPolinomFromFile("polinom2.txt");
		
		auto begin = std::chrono::high_resolution_clock::now();

		//<<<<<<<<<<<<<<<<<<<<<<<<<>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
		//multiplyO2Main(polinom1, polinom2, nrMachines); //THE CALLLLLLLLLLLLLLLLLL
		std::vector<int> result = multiplyKaratsubaWrapper(polinom1, polinom2, nrMachines);
		printPolinom(result);

		auto end = std::chrono::high_resolution_clock::now();
		auto dur = end - begin;
		auto ms = std::chrono::duration_cast<std::chrono::milliseconds>(dur).count();
		std::cout << " ---> " << ms << " miliseconds." << std::endl;
	}
	else {
		//multiplyO2Worker();
		multiplyKaratsubaWorker(me);
	}

	MPI_Finalize();
}