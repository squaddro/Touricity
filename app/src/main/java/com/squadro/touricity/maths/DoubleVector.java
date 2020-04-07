package com.squadro.touricity.maths;

public class DoubleVector {
	protected double[] v = new double[4];

	public DoubleVector() {
		this(0, 0, 0);
	}

	public DoubleVector(double x, double y, double z) {
		this(x, y, z, 1);
	}

	public DoubleVector(double x, double y, double z, double w) {
		v[0] = x;
		v[1] = y;
		v[2] = z;
		v[3] = w;
	}

	public DoubleVector(DoubleVector vector) {
		this(vector.v[0], vector.v[1], vector.v[2], vector.v[3]);
	}

	public double x() {
		return v[0];
	}

	public double y() {
		return v[1];
	}

	public double z() {
		return v[2];
	}

	public double w() {
		return v[3];
	}

	public double squaredMagnitude() {
		return v[0] * v[0] + v[1] * v[1] + v[2] * v[2];
	}

	public double magnitude() {
		return Math.sqrt(squaredMagnitude());
	}

	public static DoubleVector add(DoubleVector a, DoubleVector b) {
		return new DoubleVector(a.v[0] + b.v[0], a.v[1] + b.v[1], a.v[2] + b.v[2], a.v[3] + b.v[3]);
	}

	public static DoubleVector subtract(DoubleVector a, DoubleVector b) {
		return new DoubleVector(a.v[0] - b.v[0], a.v[1] - b.v[1], a.v[2] - b.v[2], a.v[3] - b.v[3]);
	}

	public static DoubleVector multiply(DoubleVector vec, double scalar) {
		return  new DoubleVector(vec.v[0] * scalar, vec.v[1] * scalar, vec.v[2] * scalar, vec.v[3]);
	}

	public static DoubleVector multiply(DoubleVector a, DoubleVector b) {
		return new DoubleVector(a.v[0] * b.v[0], a.v[1] * b.v[1], a.v[2] * b.v[2], a.v[3] * b.v[3]);
	}

	public static double dot(DoubleVector a, DoubleVector b) {
		return a.v[0] * b.v[0] + a.v[1] * b.v[1] + a.v[2] * b.v[2];
	}

	public static DoubleVector cross(DoubleVector a, DoubleVector b) {
		double x = a.v[1] * b.v[2] - a.v[2] * b.v[1];
		double y = a.v[2] * b.v[0] - a.v[0] * b.v[2];
		double z = a.v[0] * b.v[1] - a.v[1] * b.v[0];
		return new DoubleVector(x, y, z, 1);
	}

	public static double angleBetween(DoubleVector a, DoubleVector b) {
		double magA = a.magnitude();
		double magB = b.magnitude();
		if(magA == 0 || magB == 0)
			return 0;
		return Math.acos(dot(a, b) / (magA * magB));
	}

	public static double distanceBetween(DoubleVector a, DoubleVector b) {
		double x = a.v[0] - b.v[0];
		double y = a.v[1] - b.v[1];
		double z = a.v[2] - b.v[2];
		return Math.sqrt(x * x + y * y + z * z);
	}

	public static DoubleVector forward() {
		return new DoubleVector(0, 0, 1);
	}

	public static DoubleVector behind() {
		return new DoubleVector(0, 0, -1);
	}

	public static DoubleVector left() {
		return new DoubleVector(1, 0, 0);
	}

	public static DoubleVector right() {
		return new DoubleVector(-1, 0, 0);
	}

	public static DoubleVector up() {
		return new DoubleVector(0, 1, 0);
	}

	public static DoubleVector down() {
		return new DoubleVector(0, -1, 0);
	}

	public static DoubleVector one() {
		return new DoubleVector(1, 1, 1);
	}

	public static DoubleVector zero() {
		return new DoubleVector(0, 0, 0);
	}
}
