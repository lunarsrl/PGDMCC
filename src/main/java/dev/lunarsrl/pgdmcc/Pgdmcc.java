package dev.lunarsrl.pgdmcc;

import com.mojang.logging.LogUtils;
import dan200.computercraft.api.peripheral.PeripheralCapability;
import dev.lunarsrl.pgdmcc.compat.computercraft.peripherals.ConstantSpeedMotorPeripheral;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import org.patryk3211.powergrid.collections.ModdedBlockEntities;
import org.slf4j.Logger;

// The value here should match an entry in the META-INF/neoforge.mods.toml file
@Mod(Pgdmcc.MODID)
public class Pgdmcc {
    // Define
    public static final String MODID = "pgdmcc";
    public static final Logger LOGGER = LogUtils.getLogger();

    public Pgdmcc(IEventBus modEventBus, ModContainer modContainer) {
        // Register the commonSetup method for modloading
        modEventBus.addListener(this::registerCapabilities);
    }

    private void registerCapabilities(RegisterCapabilitiesEvent event) {
        event.registerBlockEntity(
                PeripheralCapability.get(),
                ModdedBlockEntities.CONSTANT_SPEED_MOTOR.get(),
                (block_entity, side) -> new ConstantSpeedMotorPeripheral(block_entity)
        );
    }
}