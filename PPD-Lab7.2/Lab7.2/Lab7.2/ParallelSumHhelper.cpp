#include "pch.h"
#include "ParallelSumHhelper.h"
#include <iostream>
#include <future>

void ParallelSumHelper::computeSum(MyQueue* nr1, MyQueue* nr2, MyQueue* result)
{
	bool nr1Done = false;
	bool nr2Done = false;
	bool mustAddCarry = false;

	while (true) {
		int digit1;
		int digit2;

		std::unique_lock<std::mutex> lck1(nr1->lock);
		if (!nr1Done) 
		{
			//queue empty and closed to writing, so no more digits coming from thisi queue
			if (nr1->isClosedForWritting && nr1->isEmpty()) {
				nr1Done = true;
				digit1 = 0;
			}
			// there is still something to process or there will still be something to process
			else{
				//queue is empty but sleep because more stuff will come
				if (nr1->isEmpty()) {
					nr1->cv.wait(lck1);
				}

				if (!nr1->isEmpty()) {
					//queue has a digit in it, so pop it and use it
					digit1 = nr1->pop();
				}
				else {
					nr1Done = true;
					digit1 = 0;
				}
				
			}
		}
		else {
			digit1 = 0;
		}
		lck1.unlock();
		//@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@222
		std::unique_lock<std::mutex> lck2(nr2->lock);
		if (!nr2Done)
		{
			//queue empty and closed to writing, so no more digits coming from thisi queue
			if (nr2->isClosedForWritting && nr2->isEmpty()) {
				nr2Done = true;
				digit2 = 0;
			}
			// there is still something to process or there will still be something to process
			else {
				//queue is empty but sleep because more stuff will come
				if (nr2->isEmpty()) {
					nr2->cv.wait(lck2);
				}
				if (!nr2->isEmpty()) {
					//queue has a digit in it, so pop it and use it
					digit2 = nr2->pop();
				}
				else {
					nr2Done = true;
					digit2 = 0;
				}
			}
		}
		else {
			digit2 = 0;
		}
		lck2.unlock();

		if (nr1Done && nr2Done) {
			std::unique_lock<std::mutex> lckRes(result->lock);
			result->isClosedForWritting = true;
			if (mustAddCarry) {
				result->push(1);
			}
			result->cv.notify_one();
			return;
		}
		else {
			int digitResult = digit1 + digit2;
			if (mustAddCarry) {
				digitResult += 1;
				mustAddCarry = false;
			}
			if (digitResult > 9) {
				digitResult = digitResult % 10;
				mustAddCarry = true;
			}
			std::unique_lock<std::mutex> lckRes(result->lock);
			result->push(digitResult);
			result->cv.notify_one();
		}
	}
}

void ParallelSumHelper::createThreadsAndStart(std::vector<std::queue<int>> numbers)
{
	auto begin = std::chrono::high_resolution_clock::now();
	std::vector<std::future<void>> myThreads;
	std::vector<MyQueue*> queuesToUse;
	for (std::queue<int> number : numbers) {
		queuesToUse.push_back(new MyQueue(number, true));
	}

	while (queuesToUse.size() != 1) {
		std::queue<int> q;
		MyQueue* oneResult = new MyQueue(q, false);
		myThreads.push_back(std::async(ParallelSumHelper::computeSum, queuesToUse.at(0), queuesToUse.at(1), oneResult));
		queuesToUse.push_back(oneResult);
		queuesToUse.erase(queuesToUse.begin(), queuesToUse.begin() + 2);
	}
	if (myThreads.size() > 0) {
		myThreads.at(myThreads.size() - 1).get();
	}
	std::string result = "";
	while (!queuesToUse.at(0)->isEmpty()) {
		result = std::to_string(queuesToUse.at(0)->pop()) + result;
	}
	auto end = std::chrono::high_resolution_clock::now();
	auto dur = end - begin;
	auto ms = std::chrono::duration_cast<std::chrono::milliseconds>(dur).count();
	//std::cout << "Result: " << result; 
	std::cout << " ---> " << ms << " miliseconds." << std::endl;
}

std::queue<int> ParallelSumHelper::doSumSequential(std::vector<std::queue<int>> numbers)
{
	std::queue<int> result(numbers.at(0));
	numbers.erase(numbers.begin());

	while (!numbers.empty()) {
		std::queue<int> copyResult(result);
		result = std::queue<int>();

		std::queue<int> nr = numbers.at(0);
		numbers.erase(numbers.begin());

		bool mustAddCarry = false;
		while (!nr.empty() || !copyResult.empty()) {
			int digit1;
			int digit2;
			if (nr.empty()) {
				digit1 = 0;
			}
			else {
				digit1 = nr.front();
				nr.pop();
			}

			if (copyResult.empty()) {
				digit2 = 0;
			}
			else {
				digit2 = copyResult.front();
				copyResult.pop();
			}

			int nrToPush = digit1 + digit2;
			if (mustAddCarry) {
				nrToPush += 1;
				mustAddCarry = false;
			}
			if (nrToPush > 9) {
				nrToPush = nrToPush % 10;
				mustAddCarry = true;
			}
			result.push(nrToPush);
		}
		if (mustAddCarry) {
			result.push(1);
		}
	}
	return result;
}

void ParallelSumHelper::startSequential(std::vector<std::queue<int>> numbers)
{
	auto begin = std::chrono::high_resolution_clock::now();
	std::queue<int> totalSum = ParallelSumHelper::doSumSequential(numbers);
	std::string result = "";
	while (!totalSum.empty()) {
		result = std::to_string(totalSum.front()) + result;
		totalSum.pop();
	}
	auto end = std::chrono::high_resolution_clock::now();
	auto dur = end - begin;
	auto ms = std::chrono::duration_cast<std::chrono::milliseconds>(dur).count();
	//std::cout << "Result: " << result;
	std::cout << " ---> " << ms << " miliseconds." << std::endl;
}
