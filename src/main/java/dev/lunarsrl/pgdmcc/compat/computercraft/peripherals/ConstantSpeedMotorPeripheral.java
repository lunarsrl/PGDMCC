package dev.lunarsrl.pgdmcc.compat.computercraft.peripherals;

import dan200.computercraft.api.lua.LuaFunction;
import dan200.computercraft.api.peripheral.IPeripheral;
import dev.lunarsrl.pgdmcc.mixInterfaces.pgdmccConstantSpeedMotorInterface;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.patryk3211.powergrid.kinetics.motor.ConstantSpeedMotorBlockEntity;

public class ConstantSpeedMotorPeripheral implements IPeripheral {

    private final ConstantSpeedMotorBlockEntity motor;


    public ConstantSpeedMotorPeripheral(ConstantSpeedMotorBlockEntity motor) {
        this.motor = motor;
    }


    @LuaFunction
    public final double getTargetSpeed() {
        pgdmccConstantSpeedMotorInterface motor1 = (pgdmccConstantSpeedMotorInterface) this.motor;
        return motor1.getPgmdTargetSpeed();
    }

    @LuaFunction(mainThread = true)
    public final boolean setTargetSpeed(double speed) {
        pgdmccConstantSpeedMotorInterface motor1 = (pgdmccConstantSpeedMotorInterface) this.motor;
        if (speed < 0) {
            throw new RuntimeException("Cannot be a value less than 0. To reverse rotation, reverse the current instead");
        }
        motor1.setPgmdTargetSpeed(speed);
        return true;

    }

    @NotNull
    @Override
    public String getType() {
        return "constant_speed_motor";
    }

    @Override
    public boolean equals(@Nullable IPeripheral other) {
        return other instanceof ConstantSpeedMotorPeripheral o && motor == o.motor;
    }

}
