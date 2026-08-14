package dev.lunarsrl.pgdmcc.mixin;

import com.simibubi.create.content.kinetics.base.GeneratingKineticBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;

import com.simibubi.create.infrastructure.config.AllConfigs;
import dev.lunarsrl.pgdmcc.mixInterfaces.pgdmccConstantSpeedMotorInterface;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import org.patryk3211.powergrid.electricity.base.ElectricBehaviour;
import org.patryk3211.powergrid.electricity.base.IElectricEntity;
import org.patryk3211.powergrid.electricity.base.ThermalBehaviour;
import org.patryk3211.powergrid.electricity.sim.ElectricWire;
import org.patryk3211.powergrid.kinetics.motor.ConstantSpeedMotorBlockEntity;
import org.patryk3211.powergrid.kinetics.motor.ElectricMotorBlock;
import org.patryk3211.powergrid.kinetics.motor.SpeedScrollValueBehaviour;
import org.patryk3211.powergrid.utility.Lang;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import java.util.List;

import static org.patryk3211.powergrid.kinetics.motor.ElectricMotorBlockEntity.CONVERSION_CONSTANT;

@Mixin(ConstantSpeedMotorBlockEntity.class)
public abstract class pgdmccConstantSpeedMotorMixin
        extends GeneratingKineticBlockEntity
        implements IElectricEntity, pgdmccConstantSpeedMotorInterface
{
    @Shadow
    public static final int AVERAGING_TICKS = 5;
    @Shadow
    protected ElectricBehaviour electricBehaviour;
    @Nullable @Shadow
    protected ThermalBehaviour thermalBehaviour;
    @Shadow
    private SpeedScrollValueBehaviour scrollValue;
    @Shadow
    private ElectricWire coil;
    @Shadow
    private float generatedSU = 0;
    @Shadow
    private float avgSpeed;

    public pgdmccConstantSpeedMotorMixin(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Shadow
    public abstract float torque();


    @Unique
    public double pgmdTargetSpeed;


    public void setPgmdTargetSpeed(double speed) {
        this.pgmdTargetSpeed = speed;
        this.updateGeneratedRotation();
    }

    public double getPgmdTargetSpeed() {
        return this.pgmdTargetSpeed;
    }

    /**
     * @PGDMCC
     * @Needed to create a generic interface for speed, and reconnect scroll value to it
     */
    @Overwrite
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
        super.addBehaviours(behaviours);
        electricBehaviour = new ElectricBehaviour(this);
        behaviours.add(electricBehaviour);

        var maxPower = 256 * torque() / CONVERSION_CONSTANT;
        var baseFactor = ThermalBehaviour.dissipationFactor(maxPower, 150);
        thermalBehaviour = ThermalBehaviour.simple(this, 3.5f, baseFactor);
        if(thermalBehaviour != null)
            behaviours.add(thermalBehaviour);

        Integer max = AllConfigs.server().kinetics.maxRotationSpeed.get();
        scrollValue = new SpeedScrollValueBehaviour(Lang.translateDirect("devices.motor.speed"), this, new ConstantSpeedMotorBlockEntity.Box());
        scrollValue.between(0, max);
        scrollValue.value = 16;
        pgmdTargetSpeed = scrollValue.value;
        scrollValue.withCallback(i -> this.setPgmdTargetSpeed(scrollValue.value));
        behaviours.add(scrollValue);
    }

    /**
     * @PGDMCC
     * @Needed to replace check for scroll value with a check for the new generic speed interface
     */
    @Overwrite
    public float getGeneratedSpeed() {
        if(Math.abs(generatedSU) < 64)
            return 0;
        return convertToDirection((float) pgmdTargetSpeed * (generatedSU < 0 ? -1 : 1), getBlockState().getValue(ElectricMotorBlock.FACING));
    }

    /**
     * @PGDMCC
     * @Needed to replace check for scroll value with a check for the new generic speed interface
     */
    @Overwrite
    public float calculateAddedStressCapacity() {
        if(Math.abs(generatedSU) < 64)
            return 0;
        return Math.abs(generatedSU) / (float) pgmdTargetSpeed;
    }

}
