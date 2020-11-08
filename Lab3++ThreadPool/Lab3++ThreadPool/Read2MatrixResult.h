#pragma once
class Read2MatrixResult
{
private:
	int** matrix1;
	int** matrix2;
	int n1;
	int m1;
	int n2;
	int m2;
public:
	Read2MatrixResult();
	Read2MatrixResult(int** matrix1, int** matrix2, int n1, int m1, int n2, int m2);
	~Read2MatrixResult();
	int** getMatrix1();
	int** getMatrix2();
	int getN1();
	int getN2();
	int getM1();
	int getM2();
};

