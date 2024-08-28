package org.lycorecocafe.cmrs.utils.game.entity;

import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;

public class EntityHelper {

    /**
     * 通过实体的 NBT 数据和 ID 恢复实体
     *
     * @param tag   实体的 NBT 数据，包括所有属性
     * @param level 实体所属的世界
     * @return 实例化的实体对象，如果 NBT 数据无效或创建失败，返回 null
     */
    public static Entity getEntityByNBT(CompoundTag tag, Level level) {

        CompoundTag entityData = null;
        if (tag != null && tag.contains("StoredEntityData")) {
            entityData = tag.getCompound("StoredEntityData");
        }

        if (entityData == null || !entityData.contains("id")) {
            return null; // 确保 NBT 包含实体类型 ID
        }

        // 通过 ID 获取实体类型
        String entityId = entityData.getString("id");
        ResourceLocation entityResourceLocation = new ResourceLocation(entityId);
        EntityType<?> entityType = Registry.ENTITY_TYPE.get(entityResourceLocation);

        // 检查 EntityType 是否存在
        if (entityType == null) {
            return null;
        }

        // 实例化实体对象
        Entity entity = entityType.create(level);
        if (entity == null) {
            return null;
        }

        // 使用 NBT 数据初始化实体状态
        try {
            entity.load(entityData);
        } catch (Exception e) {
            return null;
        }

        return entity;
    }
}
