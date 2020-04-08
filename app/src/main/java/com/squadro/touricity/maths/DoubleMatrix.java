package com.squadro.touricity.maths;

public class DoubleMatrix {
	public double[][] m = new double[4][4];

	public DoubleMatrix() {

	}

	public DoubleMatrix(DoubleVector vec0, DoubleVector vec1, DoubleVector vec2, DoubleVector vec3) {
		m[0][0] = vec0.v[0];
		m[0][1] = vec1.v[0];
		m[0][2] = vec2.v[0];
		m[0][3] = vec3.v[0];

		m[1][0] = vec0.v[1];
		m[1][1] = vec1.v[1];
		m[1][2] = vec2.v[1];
		m[1][3] = vec3.v[1];

		m[2][0] = vec0.v[2];
		m[2][1] = vec1.v[2];
		m[2][2] = vec2.v[2];
		m[2][3] = vec3.v[2];

		m[3][0] = vec0.v[3];
		m[3][1] = vec1.v[3];
		m[3][2] = vec2.v[3];
		m[3][3] = vec3.v[3];
	}

	public static DoubleMatrix identity() {
		DoubleMatrix matrix = new DoubleMatrix();

		matrix.m[0][0] = 1;
		matrix.m[0][1] = 0;
		matrix.m[0][2] = 0;
		matrix.m[0][3] = 0;

		matrix.m[1][0] = 0;
		matrix.m[1][1] = 1;
		matrix.m[1][2] = 0;
		matrix.m[1][3] = 0;

		matrix.m[2][0] = 0;
		matrix.m[2][1] = 0;
		matrix.m[2][2] = 1;
		matrix.m[2][3] = 0;

		matrix.m[3][0] = 0;
		matrix.m[3][1] = 0;
		matrix.m[3][2] = 0;
		matrix.m[3][3] = 1;

		return matrix;
	}

	public static DoubleMatrix multiply(DoubleMatrix a, DoubleMatrix b) {
		DoubleMatrix matrix = new DoubleMatrix();

		matrix.m[0][0] = a.m[0][0] * b.m[0][0] + a.m[0][1] * b.m[1][0]  + a.m[0][2] * b.m[2][0] + a.m[0][3] * b.m[3][0];
		matrix.m[0][1] = a.m[0][0] * b.m[0][1] + a.m[0][1] * b.m[1][1]  + a.m[0][2] * b.m[2][1] + a.m[0][3] * b.m[3][1];
		matrix.m[0][2] = a.m[0][0] * b.m[0][2] + a.m[0][1] * b.m[1][2]  + a.m[0][2] * b.m[2][2] + a.m[0][3] * b.m[3][2];
		matrix.m[0][3] = a.m[0][0] * b.m[0][3] + a.m[0][1] * b.m[1][3]  + a.m[0][2] * b.m[2][3] + a.m[0][3] * b.m[3][3];

		matrix.m[1][0] = a.m[1][0] * b.m[0][0] + a.m[1][1] * b.m[1][0]  + a.m[1][2] * b.m[2][0] + a.m[1][3] * b.m[3][0];
		matrix.m[1][1] = a.m[1][0] * b.m[0][1] + a.m[1][1] * b.m[1][1]  + a.m[1][2] * b.m[2][1] + a.m[1][3] * b.m[3][1];
		matrix.m[1][2] = a.m[1][0] * b.m[0][2] + a.m[1][1] * b.m[1][2]  + a.m[1][2] * b.m[2][2] + a.m[1][3] * b.m[3][2];
		matrix.m[1][3] = a.m[1][0] * b.m[0][3] + a.m[1][1] * b.m[1][3]  + a.m[1][2] * b.m[2][3] + a.m[1][3] * b.m[3][3];

		matrix.m[2][0] = a.m[2][0] * b.m[0][0] + a.m[2][1] * b.m[1][0]  + a.m[2][2] * b.m[2][0] + a.m[2][3] * b.m[3][0];
		matrix.m[2][1] = a.m[2][0] * b.m[0][1] + a.m[2][1] * b.m[1][1]  + a.m[2][2] * b.m[2][1] + a.m[2][3] * b.m[3][1];
		matrix.m[2][2] = a.m[2][0] * b.m[0][2] + a.m[2][1] * b.m[1][2]  + a.m[2][2] * b.m[2][2] + a.m[2][3] * b.m[3][2];
		matrix.m[2][3] = a.m[2][0] * b.m[0][3] + a.m[2][1] * b.m[1][3]  + a.m[2][2] * b.m[2][3] + a.m[2][3] * b.m[3][3];

		matrix.m[3][0] = a.m[3][0] * b.m[0][0] + a.m[3][1] * b.m[1][0]  + a.m[3][2] * b.m[2][0] + a.m[3][3] * b.m[3][0];
		matrix.m[3][1] = a.m[3][0] * b.m[0][1] + a.m[3][1] * b.m[1][1]  + a.m[3][2] * b.m[2][1] + a.m[3][3] * b.m[3][1];
		matrix.m[3][2] = a.m[3][0] * b.m[0][2] + a.m[3][1] * b.m[1][2]  + a.m[3][2] * b.m[2][2] + a.m[3][3] * b.m[3][2];
		matrix.m[3][3] = a.m[3][0] * b.m[0][3] + a.m[3][1] * b.m[1][3]  + a.m[3][2] * b.m[2][3] + a.m[3][3] * b.m[3][3];

		return matrix;
	}

	public static DoubleVector multiply(DoubleMatrix a, DoubleVector b) {
		double x = a.m[0][0] * b.v[0] + a.m[0][1] * b.v[1] + a.m[0][2] * b.v[2]  + a.m[0][3] * b.v[3];
		double y = a.m[1][0] * b.v[0] + a.m[1][1] * b.v[1] + a.m[1][2] * b.v[2]  + a.m[1][3] * b.v[3];
		double z = a.m[2][0] * b.v[0] + a.m[2][1] * b.v[1] + a.m[2][2] * b.v[2]  + a.m[2][3] * b.v[3];
		double w = a.m[3][0] * b.v[0] + a.m[3][1] * b.v[1] + a.m[3][2] * b.v[2]  + a.m[3][3] * b.v[3];

		return new DoubleVector(x, y, z, w);
	}

	public static DoubleMatrix RotateX(double degree) {
		double radians = Math.toRadians(degree);
		DoubleMatrix matrix = new DoubleMatrix();

		matrix.m[0][0] = 1;
		matrix.m[0][1] = 0;
		matrix.m[0][2] = 0;
		matrix.m[0][3] = 0;

		matrix.m[1][0] = 0;
		matrix.m[1][1] = Math.cos(radians);
		matrix.m[1][2] = Math.sin(radians);
		matrix.m[1][3] = 0;

		matrix.m[2][0] = 0;
		matrix.m[2][1] = -Math.sin(radians);
		matrix.m[2][2] = Math.cos(radians);
		matrix.m[2][3] = 0;

		matrix.m[3][0] = 0;
		matrix.m[3][1] = 0;
		matrix.m[3][2] = 0;
		matrix.m[3][3] = 1;

		return matrix;
	}

	public static DoubleMatrix RotateY(double degree) {
		double radians = Math.toRadians(degree);
		DoubleMatrix matrix = new DoubleMatrix();

		matrix.m[0][0] = Math.cos(radians);
		matrix.m[0][1] = 0;
		matrix.m[0][2] = -Math.sin(radians);
		matrix.m[0][3] = 0;

		matrix.m[1][0] = 0;
		matrix.m[1][1] = 1;
		matrix.m[1][2] = 0;
		matrix.m[1][3] = 0;

		matrix.m[2][0] = Math.sin(radians);
		matrix.m[2][1] = 0;
		matrix.m[2][2] = Math.cos(radians);
		matrix.m[2][3] = 0;

		matrix.m[3][0] = 0;
		matrix.m[3][1] = 0;
		matrix.m[3][2] = 0;
		matrix.m[3][3] = 1;

		return matrix;
	}

	public static DoubleMatrix RotateZ(double degree) {
		double radians = Math.toRadians(degree);
		DoubleMatrix matrix = new DoubleMatrix();

		matrix.m[0][0] = Math.cos(radians);
		matrix.m[0][1] = -Math.sin(radians);
		matrix.m[0][2] = 0;
		matrix.m[0][3] = 0;

		matrix.m[1][0] = Math.sin(radians);
		matrix.m[1][1] = Math.cos(radians);
		matrix.m[1][2] = 0;
		matrix.m[1][3] = 0;

		matrix.m[2][0] = 0;
		matrix.m[2][1] = 0;
		matrix.m[2][2] = 1;
		matrix.m[2][3] = 0;

		matrix.m[3][0] = 0;
		matrix.m[3][1] = 0;
		matrix.m[3][2] = 0;
		matrix.m[3][3] = 1;

		return matrix;
	}
}
