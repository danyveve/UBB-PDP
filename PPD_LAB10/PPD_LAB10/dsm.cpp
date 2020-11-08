#include "dsm.h"
Msg::Msg()
{
}

Msg::~Msg()
{
}

Msg::Msg(UpdateMsg updateMsg)
{
	this->updateMsg = updateMsg;
}

Msg::Msg(ChangeMsg changeMsg)
{
	this->changeMsg = changeMsg;
}

Msg::Msg(SubscribeMsg subscribeMsg)
{
	this->subscribeMsg = subscribeMsg;
}

Msg::Msg(bool exit)
{
	this->exit = exit;
}

SubscribeMsg::SubscribeMsg()
{
}

SubscribeMsg::~SubscribeMsg()
{
}

SubscribeMsg::SubscribeMsg(std::string var, int rank)
{
	this->var = var;
	this->rank = rank;
}

UpdateMsg::UpdateMsg()
{
}

UpdateMsg::~UpdateMsg()
{
}

UpdateMsg::UpdateMsg(std::string var, int val)
{
	this->var = var;
	this->val = val;
}

ChangeMsg::ChangeMsg()
{
}

ChangeMsg::~ChangeMsg()
{
}

ChangeMsg::ChangeMsg(std::string var, int oldVal, int newVal)
{
	this->var = var;
	this->oldVal = oldVal;
	this->newVal = newVal;
}

Dsm::Dsm()
{
	this->subscribers["a"] = std::vector<int>();
	this->subscribers["b"] = std::vector<int>();
	this->subscribers["c"] = std::vector<int>();
}

Dsm::~Dsm()
{
}

void Dsm::setVar(std::string var, int val)
{
	if (var == "a") {
		this->a = val;
	}
	if (var == "b") {
		b = val;
	}
	if (var == "c") {
		c = val;
	}
}

void Dsm::updateVar(std::string var, int val)
{
	this->setVar(var, val);
	UpdateMsg updateMsg = UpdateMsg(var, val);
	Msg msg = Msg(updateMsg);

	this->sendToSubscribers(var, msg);
}

void Dsm::sendAll(Msg msg)
{
	int rank;
	int size;
	MPI_Comm_rank(MPI_COMM_WORLD, &rank);
	MPI_Comm_size(MPI_COMM_WORLD, &size);
	for (int i = 0; i < size; i++) {
		if (rank == i) {
			continue;
		}
		MPI_Send(msg.)
	}
}

void Dsm::close()
{
	this->sendAll(Msg(true));
}
