#include "pch.h"
#include "Read2MatrixResult.h"
#include <iostream>


Read2MatrixResult::Read2MatrixResult()
{
}

Read2MatrixResult::Read2MatrixResult(int ** matrix1, int ** matrix2, int n1, int m1, int n2, int m2)
{
	this->matrix1 = matrix1;
	this->matrix2 = matrix2;
	this->n1 = n1;
	this->m1 = m1;
	this->n2 = n2;
	this->m2 = m2;
}


Read2MatrixResult::~Read2MatrixResult()
{
	for (int i = 0; i < n1; i++) {
		delete[] matrix1[i];
	}
	delete[] matrix1;

	for (int i = 0; i < n2; i++) {
		delete[] matrix2[i];
	}
	delete[] matrix2;
}

int ** Read2MatrixResult::getMatrix1()
{
	return this->matrix1;
}

int ** Read2MatrixResult::getMatrix2()
{
	return this->matrix2;
}

int Read2MatrixResult::getN1()
{
	return this->n1;
}

int Read2MatrixResult::getN2()
{
	return this->n2;
}

int Read2MatrixResult::getM1()
{
	return this->m1;
}

int Read2MatrixResult::getM2()
{
	return this->m2;
}