#include "pch.h"
#include "HamiltoneanParallel.h"
#include <iostream>
#include <stdio.h>
#include <stdlib.h>
#include <stdint.h>
#include <future>
#include <thread>
#include <vector>
#include <mutex>
#include <algorithm>
#include <string>



HamiltoneanParallel::HamiltoneanParallel()
{
}


HamiltoneanParallel::~HamiltoneanParallel()
{
}

void HamiltoneanParallel::startParallel(std::vector<std::vector<int>> graph)
{
	std::vector<int> cycle = std::vector<int>();

	for (int i = 0; i < this->nrVertices; i++) {
		cycle.push_back(-1);
	}
	cycle.at(0) = 0; //start from vertex 0

	this->found = 0;

	this->hammiltonParallel(graph, cycle, 1);

	if (this->found) {
	    std::cout << "There is at least the following hamiltonean cycle: \n";
		for (int node : this->finalCycle) {
			std::cout << node << " ";
		}
		std::cout << this->finalCycle.at(0) << "\n";
	}
	else {
		std::cout << "There is no Hamiltonean cycle in this graph!\n";
	}
}

void HamiltoneanParallel::hammiltonParallel(std::vector<std::vector<int>> graph, std::vector<int> cycle, int pos)
{
	if (this->found != 0) return;
	if (pos == this->nrVertices) {
		//have edge from last to first
		if (graph.at(cycle.at(pos - 1)).at(cycle.at(0) == 1)) {
			int isFound = this->found.fetch_or(1);
			if (isFound == 0) {
				this->finalCycle = cycle;
			}
		}
	}

	std::vector<std::future<void>> myFutures;
	for (int v = 1; v < this->nrVertices; v++) {
		if (this->addOk(v, cycle, pos, graph)) {
			cycle.at(pos) = v;

			this->nrThreads.fetch_sub(1);
			if (this->nrThreads >= 1) {
				myFutures.push_back(std::async([=]() {this->hammiltonParallel(graph, cycle, pos + 1);}));
			}
			else {
				this->hammiltonParallel(graph, cycle, pos + 1);
			}

			cycle.at(pos) = -1;
		}
	}
	
	
}

bool HamiltoneanParallel::addOk(int v, std::vector<int> cycle, int pos, std::vector<std::vector<int>> graph)
{
	if (graph.at(cycle.at(pos - 1)).at(v) == 0){
		return false;
	}

	for (int i = 0; i < pos; i++) {
		if (cycle.at(i) == v) {
			return false;
		}
	}
	return true;
}
