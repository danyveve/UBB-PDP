#include "pch.h"
#include <iostream>
#include "ParallelSumHhelper.h"
#include <queue>
#include "Lab7.2.h"
#include <future>

int main()
{
	std::vector<std::vector<int>> numbers = readNumbersFromFile("numbers.txt");

	//transform numbers into queues of digits
	std::vector<std::queue<int>> numbersQueues;
	for (std::vector<int> number : numbers) {
		std::reverse(number.begin(), number.end());
		std::queue<int, std::deque<int>> qNr(std::deque<int>(number.begin(),
			number.end()));
		numbersQueues.push_back(qNr);
	}

	//start sequential version first
	ParallelSumHelper::startSequential(numbersQueues);

	//create threads and arrange in binaryTree
	ParallelSumHelper::createThreadsAndStart(numbersQueues);


}

std::vector<std::vector<int>> readNumbersFromFile(std::string fileName)
{
	std::vector<std::vector<int>> numbers;
	std::ifstream file(fileName);
	std::string line;
	while (std::getline(file, line))
	{
		std::vector<int> number;
		std::istringstream numberStream(line);
		int digit;
		while (numberStream >> digit) {
			number.push_back(digit);
		}
		numbers.push_back(number);
	}

	return numbers;
}