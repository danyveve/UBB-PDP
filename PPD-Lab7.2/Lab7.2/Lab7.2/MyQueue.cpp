#include "pch.h"
#include "MyQueue.h"

MyQueue::MyQueue(std::queue<int> queue, bool isClosedForWritting)
{
	this->queue = queue;
	this->isClosedForWritting = isClosedForWritting;
}

MyQueue::MyQueue()
{
	this->isClosedForWritting = false;
}

int MyQueue::pop()
{
	int elem = this->queue.front();
	this->queue.pop();
	return elem;
}

void MyQueue::push(int element)
{
	this->queue.push(element);
}

bool MyQueue::isEmpty()
{
	return this->queue.empty();
}

void MyQueue::closeForWritting()
{
	this->isClosedForWritting = true;
}

