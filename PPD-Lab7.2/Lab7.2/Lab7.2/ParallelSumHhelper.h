#pragma once
#include <vector>
#include <fstream>
#include <sstream>
#include <string>
#include "MyQueue.h"

class ParallelSumHelper{
public:
	static void computeSum(MyQueue* nr1, MyQueue* nr2, MyQueue* result);
	static void createThreadsAndStart(std::vector<std::queue<int>> numbers);
	static std::queue<int> doSumSequential(std::vector<std::queue<int>> numbers);
	static void startSequential(std::vector<std::queue<int>> numbers);
};