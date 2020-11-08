#pragma once
#include <vector>
#include "MyNode.h"
class MyGraph
{
public:
	std::vector<MyNode> nodes;
	MyGraph();
	~MyGraph();
	void addNode(MyNode);
	std::vector<MyNode> getNodes();
};

