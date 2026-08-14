package dev.lunarsrl.pgdmcc.mixInterfaces;

public interface pgdmccConstantSpeedMotorInterface {

    public abstract void setPgmdTargetSpeed(double speed);
    public abstract double getPgmdTargetSpeed();
}