package com.diamondgoobird.trialchambertimer.mixins;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.VaultBlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.registry.DynamicRegistryManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.function.BiFunction;

@Mixin(VaultBlockEntity.class)
public class VaultBlockEntityMixin {
    // Change the contents of the vaultblockentity packet to contain the vault config
    // so that we can get the key item on the client (if the mod is also installed on the server)
    @Redirect(method = "toUpdatePacket", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/packet/s2c/play/BlockEntityUpdateS2CPacket;create(Lnet/minecraft/block/entity/BlockEntity;)Lnet/minecraft/network/packet/s2c/play/BlockEntityUpdateS2CPacket;"))
    public BlockEntityUpdateS2CPacket redirectCreatePacket(BlockEntity blockEntity) {
        VaultBlockEntity e = (VaultBlockEntity) blockEntity;
        BiFunction<BlockEntity, DynamicRegistryManager, NbtCompound> getter; // define this to include the information we want to pass to the client
        return BlockEntityUpdateS2CPacket.create(e, getter);
    }
}
