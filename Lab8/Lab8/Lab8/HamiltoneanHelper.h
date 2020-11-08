#pragma once
#include <vector>
class HamiltoneanHelper
{
public:
	HamiltoneanHelper();
	~HamiltoneanHelper();
	int nrVertices;
	std::vector<std::vector<int>> graph;
	std::vector<int> cycle;
	void startSequential();
	bool hammiltonSequential(int pos);
	bool addOk(int v, int pos);
};

