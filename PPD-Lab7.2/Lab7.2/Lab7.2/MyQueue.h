#pragma once
#include <queue>
#include <condition_variable>
#include <mutex>
class MyQueue {
public:
	std::queue<int> queue;
	std::condition_variable cv;
	bool isClosedForWritting;
	std::mutex lock;


	MyQueue(std::queue<int> queue, bool closedForWritting);
	MyQueue();
	int pop();
	void push(int element);
	bool isEmpty();
	void closeForWritting();
};