#pragma once
#include <vector>
#include <thread>
#include <future>
#include <algorithm>
#include <stdio.h>
#include <stdlib.h>
#include <stdint.h>
#include <future>
#include <thread>
#include <vector>
#include <mutex>
#include <algorithm>
#include <chrono>

class HamiltoneanParallel
{
public:
	HamiltoneanParallel();
	~HamiltoneanParallel();

	int nrVertices;
	//std::vector<std::vector<int>> graph;

	std::atomic<int> nrThreads;
	std::atomic<int> found;
	std::vector<int> finalCycle;

	void startParallel(std::vector<std::vector<int>> graph);
	void hammiltonParallel(std::vector<std::vector<int>> graph, std::vector<int> cycle, int pos);
	bool addOk(int v, std::vector<int> cycle, int pos, std::vector<std::vector<int>> graph);
};

