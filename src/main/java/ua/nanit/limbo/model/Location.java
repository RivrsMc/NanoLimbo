package ua.nanit.limbo.model;

import java.util.function.DoubleUnaryOperator;
import java.util.function.UnaryOperator;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Location implements Cloneable {

    public static final Location EMPTY = new Location(0, 0, 0, 0, 0);

    private double x;
    private double y;
    private double z;
    private float yaw;
    private float pitch;

    public Location add(double x, double y, double z) {
        this.x += x;
        this.y += y;
        this.z += z;
        return this;
    }

    public Location add(Location location) {
        this.x += location.getX();
        this.y += location.getY();
        this.z += location.getZ();
        return this;
    }

    public Location subtract(double x, double y, double z) {
        this.x -= x;
        this.y -= y;
        this.z -= z;
        return this;
    }

    public Location subtract(Location location) {
        this.x -= location.getX();
        this.y -= location.getY();
        this.z -= location.getZ();
        return this;
    }

    public Location multiply(double m) {
        this.x *= m;
        this.y *= m;
        this.z *= m;
        return this;
    }

    public Location divide(double d) {
        this.x /= d;
        this.y /= d;
        this.z /= d;
        return this;
    }

    public Location x(DoubleUnaryOperator operator) {
        this.x = operator.applyAsDouble(x);
        return this;
    }

    public Location y(DoubleUnaryOperator operator) {
        this.y = operator.applyAsDouble(y);
        return this;
    }

    public Location z(DoubleUnaryOperator operator) {
        this.z = operator.applyAsDouble(z);
        return this;
    }

    public Location yaw(UnaryOperator<Float> operator) {
        this.yaw = operator.apply(yaw);
        return this;
    }

    public Location pitch(UnaryOperator<Float> operator) {
        this.pitch = operator.apply(pitch);
        return this;
    }

    public Location add(Vector vector) {
        this.x += vector.getX();
        this.y += vector.getY();
        this.z += vector.getZ();
        return this;
    }

    public Location subtract(Vector vector) {
        this.x -= vector.getX();
        this.y -= vector.getY();
        this.z -= vector.getZ();
        return this;
    }

    public Location multiply(Vector vector) {
        this.x *= vector.getX();
        this.y *= vector.getY();
        this.z *= vector.getZ();
        return this;
    }

    public Location divide(Vector vector) {
        this.x /= vector.getX();
        this.y /= vector.getY();
        this.z /= vector.getZ();
        return this;
    }

    public double distance(Location location) {
        return Math.sqrt(distanceSquared(location));
    }

    public double distanceSquared(Location location) {
        return Math.pow(location.getX() - x, 2) + Math.pow(location.getY() - y, 2) + Math.pow(location.getZ() - z, 2);
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        Location location = (Location) super.clone();
        location.setX(x);
        location.setY(y);
        location.setZ(z);
        location.setYaw(yaw);
        location.setPitch(pitch);
        return location;
    }
}
