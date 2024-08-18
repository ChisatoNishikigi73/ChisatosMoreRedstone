package org.lycorecocafe.cmrs.items;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.RedStoneWireBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.lycorecocafe.cmrs.mixin.mixins.RedstoneWireBlockMixin;

public class RedstoneDetectorItem extends Item {

    private static final int DETECTION_RANGE = 32;
    private static boolean signalMode = true;  // 定义当前模式，默认 true 表示 findRedstoneSignalSource 模式
    public RedstoneDetectorItem(Properties properties) {
        super(properties);
    }

    @Override
    public @NotNull InteractionResult useOn(@NotNull UseOnContext context) {
        Level world = context.getLevel();
        BlockPos pos = context.getClickedPos();
        BlockState state = world.getBlockState(pos);

        if (state.getBlock() instanceof RedStoneWireBlock) {
            RedstoneWireBlockMixin.SignalSource signalSource;
            if (signalMode) {
                signalSource = RedstoneWireBlockMixin.findRedstoneSignalSource(world, pos, DETECTION_RANGE);
            } else {
                signalSource = RedstoneWireBlockMixin.findRedstoneSignalSourceWithoutSignal(world, pos, DETECTION_RANGE);
            }
            StringBuilder messageBuilder = new StringBuilder();

            if (signalSource != null && !signalSource.signalSources.isEmpty()) {
                messageBuilder.append("Redstone network at ").append(pos).append(" is powered by ").append(signalSource.signalSources.size()).append(" following sources:\n");
                for (RedstoneWireBlockMixin.SignalSource.BlockData sourceData : signalSource.signalSources) {
                    messageBuilder.append(" - ")
                            .append(sourceData.pos)
                            .append(" (")
                            .append(sourceData.state.getBlock().getName().getString())
                            .append(")\n");
                }
            } else {
                messageBuilder.append("Redstone network at ").append(pos).append(" is not powered");
            }

            String message = messageBuilder.toString();

            if (!world.isClientSide && context.getPlayer() != null) {
                context.getPlayer().sendSystemMessage(Component.literal(message));
            }
            return InteractionResult.sidedSuccess(world.isClientSide);
        }

        return InteractionResult.PASS;
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level world, @NotNull Player player, @NotNull InteractionHand hand) {
        ItemStack itemStack = player.getItemInHand(hand);

        // 只在服务器端切换模式
        if (!world.isClientSide && player.isShiftKeyDown() && !player.isUsingItem() && !player.isSpectator()) {
            signalMode = !signalMode;
            String modeMessage = signalMode ? "findRedstoneSignalSource mode" : "findRedstoneSignalSourceWithoutSignal mode";
            player.displayClientMessage(Component.literal("Switched to " + modeMessage), true);
            return InteractionResultHolder.success(itemStack);
        }

        return InteractionResultHolder.pass(itemStack);
    }
}