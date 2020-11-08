#pragma once
#include <string>
#include <vector>
#include <map>
#include <mpi.h>
class SubscribeMsg
{
public:
	SubscribeMsg();
	~SubscribeMsg();

	std::string var;
	int rank;

	SubscribeMsg(std::string var, int rank);
};

class UpdateMsg
{
public:
	UpdateMsg();
	~UpdateMsg();

	std::string var;
	int val;

	UpdateMsg(std::string var, int val);
};

class ChangeMsg
{
public:
	ChangeMsg();
	~ChangeMsg();

	std::string var;
	int oldVal;
	int newVal;

	ChangeMsg(std::string var, int oldVal, int newVal);
};

class Msg
{
public:
	Msg();
	~Msg();

	UpdateMsg updateMsg;
	ChangeMsg changeMsg;
	SubscribeMsg subscribeMsg;

	bool exit = false;

	Msg(UpdateMsg updateMsg);
	Msg(ChangeMsg changeMsg);
	Msg(SubscribeMsg subscribeMsg);
	Msg(bool exit);
};

class Dsm
{
public:
	Dsm();
	~Dsm();

	int a = 1;
	int b = 2;
	int c = 3;
	std::map<std::string, std::vector<int>> subscribers;

	void setVar(std::string var, int val);
	void updateVar(std::string var, int val);
	void sendAll(Msg msg);
	void close();
};