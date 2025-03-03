package ua.nanit.limbo.model;

import java.util.function.DoubleUnaryOperator;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Vector implements Cloneable {

    public static final Vector ZERO = new Vector(0, 0, 0);
    public static final Vector ONE = new Vector(1, 1, 1);

    private double x;
    private double y;
    private double z;

    public Vector add(Vector vector) {
        x += vector.x;
        y += vector.y;
        z += vector.z;
        return this;
    }

    public Vector subtract(Vector vector) {
        x -= vector.x;
        y -= vector.y;
        z -= vector.z;
        return this;
    }

    public Vector multiply(Vector vector) {
        x *= vector.x;
        y *= vector.y;
        z *= vector.z;
        return this;
    }

    public Vector divide(Vector vector) {
        x /= vector.x;
        y /= vector.y;
        z /= vector.z;
        return this;
    }

    public Vector multiply(double multiplier) {
        x *= multiplier;
        y *= multiplier;
        z *= multiplier;
        return this;
    }

    public Vector x(DoubleUnaryOperator operator) {
        x = operator.applyAsDouble(x);
        return this;
    }

    public Vector y(DoubleUnaryOperator operator) {
        y = operator.applyAsDouble(y);
        return this;
    }

    public Vector z(DoubleUnaryOperator operator) {
        z = operator.applyAsDouble(z);
        return this;
    }

    public Vector normalize() {
        double length = length();
        x /= length;
        y /= length;
        z /= length;
        return this;
    }

    public double length() {
        return Math.sqrt(x * x + y * y + z * z);
    }

    public Location asLocation() {
        return new Location(x, y, z, 0, 0);
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        Vector vector = (Vector) super.clone();
        vector.x = x;
        vector.y = y;
        vector.z = z;
        return vector;
    }
}
