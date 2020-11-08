#include "pch.h"
#include <iostream>
#include "Lab8.h"
#include <fstream>
#include <sstream>
#include "HamiltoneanHelper.h"
#include "HamiltoneanParallel.h"
#include <cstdlib>
#include <ctime>

int main()
{
	generateHugeGraph(20, "graph.txt");

	std::vector<std::vector<int>> graph = readGraphFromFile("graph.txt");

	auto begin = std::chrono::high_resolution_clock::now();
	HamiltoneanParallel hamiltoneanParallel1;
	hamiltoneanParallel1.nrVertices = graph.size();
	hamiltoneanParallel1.nrThreads = 1;
	hamiltoneanParallel1.startParallel(graph);
	auto end = std::chrono::high_resolution_clock::now();
	auto dur = end - begin;
	auto ms = std::chrono::duration_cast<std::chrono::milliseconds>(dur).count();
	//std::cout << "Result: " << result;
	std::cout << " ---> " << ms << " miliseconds." << std::endl;

	
	begin = std::chrono::high_resolution_clock::now();
	HamiltoneanParallel hamiltoneanParallel;
	hamiltoneanParallel.nrVertices = graph.size();
	hamiltoneanParallel.nrThreads = 3;
	hamiltoneanParallel.startParallel(graph);
	end = std::chrono::high_resolution_clock::now();
	dur = end - begin;
	ms = std::chrono::duration_cast<std::chrono::milliseconds>(dur).count();
	//std::cout << "Result: " << result;
	std::cout << " ---> " << ms << " miliseconds." << std::endl;

	
}

std::vector<std::vector<int>> readGraphFromFile(std::string fileName)
{
	std::vector<std::vector<int>> graph;
	std::ifstream file(fileName);
	std::string line;
	while (std::getline(file, line))
	{
		std::vector<int> oneNode;
		std::istringstream neighboursStream(line);
		int neighbour;
		while (neighboursStream >> neighbour) {
			oneNode.push_back(neighbour);
		}
		graph.push_back(oneNode);
	}

	file.close();
	return graph;
}

void generateHugeGraph(int nrVertexes, std::string fileName)
{
	srand(time(NULL));
	std::ofstream file(fileName);
	for (int i = 0; i < nrVertexes; i++) {
		for (int j = 0; j < nrVertexes; j++) {
			if (j == i) {
				file << "0 ";
			}
			else {
				int digit = rand() % 10;
				if (digit <= 3) {
					file << "1 ";
				}
				else {
					file << "0 ";
				}
			}
		}
		file << "\n";
	}
	
	file.close();
}
