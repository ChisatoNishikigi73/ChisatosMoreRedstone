package org.lycorecocafe.cmrs.items;

import net.minecraft.ChatFormatting;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.lycorecocafe.cmrs.CMRS;
import org.lycorecocafe.cmrs.blockentity.holo.HoloDisplayTerminalBlockEntity;
import org.lycorecocafe.cmrs.network.HoloDisplayTerminalChangePaket;

import java.util.List;

public class EntityRecorderItem extends Item {
    public EntityRecorderItem(Item.Properties properties) {
        super(properties);

        // 注册属性覆盖，用于根据 NBT 数据动态切换材质
        ItemProperties.register(this, new ResourceLocation("filled"), (stack, level, entity, seed)
                -> hasStoredEntityData(stack) ? 1.0F : 0.0F);
    }

    // 检查是否有存储的实体数据
    public static boolean hasStoredEntityData(ItemStack stack) {
        if (stack.hasTag()) {
            CompoundTag tag = stack.getTag();
            return tag != null && tag.contains("StoredEntityData");
        }
        return false;
    }

    public static CompoundTag getStoredEntityData(ItemStack stack) {
        if (stack.hasTag()) {
            CompoundTag tag = stack.getTag();
            if (tag != null && tag.contains("StoredEntityData")) {
                return tag.getCompound("StoredEntityData"); // 读取存储的实体 NBT 数据
            }
        }
        return null;
    }

    @Override
    public InteractionResult interactLivingEntity(ItemStack stack, Player player, LivingEntity target, InteractionHand hand) {
        if (!player.level.isClientSide) { // 仅在服务器端执行逻辑
            CompoundTag entityTag = new CompoundTag();
            target.save(entityTag); // 保存实体的所有 NBT 数据

            CompoundTag tag = stack.getOrCreateTag();
            tag.put("StoredEntityData", entityTag); // 存储实体的 NBT 数据到物品中
            tag.putString("EntityID", EntityType.getKey(target.getType()).toString()); // 存储实体的 ID
            tag.putString("DisplayName", target.getDisplayName().getString());

            player.displayClientMessage(Component.literal("Recorded Entity: " + target.getDisplayName().getString()), true); // 向玩家发送消息
            return InteractionResult.SUCCESS; // 返回交互成功的结果
        }
        return InteractionResult.PASS;
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level world = context.getLevel();
        BlockPos pos = context.getClickedPos();
        Player player = context.getPlayer();
        ItemStack itemStack = context.getItemInHand();

        if (!world.isClientSide && player != null) {
            BlockEntity blockEntity = world.getBlockEntity(pos);

            if (blockEntity instanceof HoloDisplayTerminalBlockEntity holoEntity) {
                CompoundTag entityTag = itemStack.getTag();
                if (entityTag != null) {
                    holoEntity.setStoredEntityData(entityTag);
                    holoEntity.setMode(HoloDisplayTerminalBlockEntity.MODE.MODEL);
                    CMRS.CHANNEL.sendToServer(new HoloDisplayTerminalChangePaket(holoEntity));
                    player.displayClientMessage(Component.literal("Write Entity Data: " + entityTag.getString("DisplayName")), true);
                    return InteractionResult.SUCCESS; // 返回交互成功的结果
                } else {
                    player.displayClientMessage(Component.literal("No entity recorded in the item."), true);
                }
            }
        }

        return InteractionResult.PASS;
    }

    @Override
    public void appendHoverText(ItemStack stack, Level level, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);

        CompoundTag tag = stack.getTag();
//        Entity e = EntityHelper.getEntityByNBT(entityTag, level);
        if (tag != null) {
            tooltip.add(Component.literal("Stored Entity: " + tag.getString("DisplayName")).withStyle(ChatFormatting.BLUE, ChatFormatting.BOLD));
        } else {
//            tooltip.add(Component.literal("No entity recorded."));
        }
    }
}
