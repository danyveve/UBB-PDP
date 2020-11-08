#include "pch.h"
#include "HamiltoneanHelper.h"
#include <iostream>


HamiltoneanHelper::HamiltoneanHelper()
{
}


HamiltoneanHelper::~HamiltoneanHelper()
{
}

void HamiltoneanHelper::startSequential()
{
	this->cycle = std::vector<int>();

	for (int i = 0; i < this->nrVertices; i++) {
		this->cycle.push_back(-1);
	}
	this->cycle.at(0) = 0; //start from vertex 0

	if (this->hammiltonSequential(1) == false) {
		std::cout << "No Hamiltonean cycle found!";
		return;
	}

	std::cout << "There is at least the following Hamiltonean cycle: \n";
	for (int node : this->cycle) {
		std::cout << node << " ";
	}
	std::cout << this->cycle.at(0) << "\n";
	return;
}

bool HamiltoneanHelper::hammiltonSequential(int pos)
{
	if (pos == this->nrVertices) {
		//have edge from last to first
		if (this->graph.at(this->cycle.at(pos - 1)).at(this->cycle.at(0) == 1)) {
			return true;
		}
		else {
			return false;
		}
	}

	for (int v = 1; v < this->nrVertices; v++) {
		if (this->addOk(v, pos)) {
			this->cycle.at(pos) = v;
			
			if (this->hammiltonSequential(pos + 1) == true) {
				return true;
			}

			this->cycle.at(pos) = -1;
		}
	}

	return false;
}

bool HamiltoneanHelper::addOk(int v, int pos)
{
	if (this->graph.at(this->cycle.at(pos - 1)).at(v) == 0){
		return false;
	}

	for (int i = 0; i < pos; i++) {
		if (this->cycle.at(i) == v) {
			return false;
		}
	}
	return true;
}
