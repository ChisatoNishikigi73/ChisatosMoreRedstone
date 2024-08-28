package org.lycorecocafe.cmrs.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.model.data.ModelData;
import net.minecraftforge.client.model.data.ModelProperty;
import org.lycorecocafe.cmrs.blockentity.holo.HoloDisplayTerminalBlockEntity;
import org.lycorecocafe.cmrs.utils.game.entity.EntityHelper;

import java.util.List;

import static org.lycorecocafe.cmrs.blocks.HoloDisplayTerminalBlock.FACING;

@OnlyIn(Dist.CLIENT)
public class HoloDisplayTerminalEntityRenderer implements BlockEntityRenderer<HoloDisplayTerminalBlockEntity> {

    public static final ModelProperty<BlockState> DISPLAY_BLOCK_STATE = new ModelProperty<>();
    private float rotateSpeed = 0f;

    public HoloDisplayTerminalEntityRenderer(BlockEntityRendererProvider.Context context) {

    }

    //TODO: 超过基岩之后基岩之上的方块也不会渲染
    //TODO: 剔除剔除面
    @Override
    public void render(HoloDisplayTerminalBlockEntity blockEntity, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int combinedLight, int combinedOverlay) {
        // 获取要显示的方块区域状态
        this.rotateSpeed = blockEntity.getRotateSpeed();
        BlockState[][][] blockStates = blockEntity.getDisplayBlockStates();
        HoloDisplayTerminalBlockEntity.MODE mode = blockEntity.getMode();

        if (mode.equals(HoloDisplayTerminalBlockEntity.MODE.OFFLINE)) {
            renderTextAboveBlock(blockEntity, partialTicks, "Player Offline...", poseStack, buffer, combinedLight);
            return;
        }

        if (mode.equals(HoloDisplayTerminalBlockEntity.MODE.MODEL)) {
            renderModel(blockEntity, poseStack, buffer, partialTicks, combinedLight);
            return;
        }

        if (blockStates.length == 0) {
            return;
        }

        // 定义放大比例
        double width = blockEntity.getDisplayRange().getXsize();
        double depth = blockEntity.getDisplayRange().getZsize();

        // 确保最大边长不超过一个方块
        double maxDimension = Math.max(width, depth);
        float scale = 0.8f;

        if (maxDimension > 0) {
            scale = (1.0f / (float) (maxDimension + 1f)) * scale;
        }

        // 使用游戏内时间和 partialTicks 来平滑旋转
        float rotation = (blockEntity.getLevel().getGameTime() + partialTicks) * rotateSpeed % 360;

        // 应用整体旋转和缩放
        poseStack.pushPose();
        poseStack.translate(0.5, (double) 5 / 16, 0.5); // 提升全息投影的位置
        poseStack.mulPose(Vector3f.YP.rotationDegrees(rotation));
        poseStack.mulPose(Vector3f.YP.rotationDegrees(blockEntity.getBlockState().getValue(FACING).getStepX() * 90));
        poseStack.scale(scale, scale, scale);

        // 预先计算半长，以优化计算
        double halfX = blockStates.length / 2.0;
        double halfZ = blockStates[0][0].length / 2.0;

        // 渲染区域内的所有方块
        BlockRenderDispatcher blockRenderer = Minecraft.getInstance().getBlockRenderer();
        for (int x = 0; x < blockStates.length; x++) {
            for (int y = 0; y < blockStates[0].length; y++) {
                for (int z = 0; z < blockStates[0][0].length; z++) {
                    BlockState blockState = blockStates[x][y][z];
                    if (blockState != null) {
                        poseStack.pushPose();
                        poseStack.translate(x - halfX, y, z - halfZ);
                        blockRenderer.renderSingleBlock(blockState, poseStack, buffer, LightTexture.FULL_BRIGHT, OverlayTexture.NO_OVERLAY);
                        poseStack.popPose();
                    }
                }
            }
        }

        // 渲染范围内的所有实体
        AABB displayRange = blockEntity.getDisplayRange();
        List<Entity> entities = blockEntity.getLevel().getEntities(null, displayRange);
        EntityRenderDispatcher renderer = Minecraft.getInstance().getEntityRenderDispatcher();
        Vec3 center = displayRange.getCenter(); // 获取显示区域的中心点
        for (Entity entity : entities) {
            poseStack.pushPose();
            poseStack.translate(entity.getX() - center.x, entity.getY() - center.y + 3 + displayRange.getYsize() / 2 - 3, entity.getZ() - center.z);
            renderer.render(entity, 0, 0, 0, 0, partialTicks, poseStack, buffer, LightTexture.FULL_BRIGHT);
            poseStack.popPose();
        }

        poseStack.popPose(); // 恢复原始变换
    }

    private void renderTextAboveBlock(BlockEntity blockEntity, float partialTicks, String text, PoseStack poseStack, MultiBufferSource bufferSource, int light) {
        // 获取Minecraft实例和Font渲染器
        Minecraft minecraft = Minecraft.getInstance();
        Font fontRenderer = minecraft.font;

        // 推送当前的Pose状态
        poseStack.pushPose();

        // 使用游戏内时间和 partialTicks 来平滑旋转
        float rotation = (blockEntity.getLevel().getGameTime() + partialTicks) * 0.25f % 360;

        // 将文本位置平移到方块上方
        poseStack.translate(0.5, (double) 10 / 16, 0.5); // 平移到方块的中心上方

        // 设置文本沿Y轴旋转，并确保旋转中心在文本的中心位置
        poseStack.mulPose(Vector3f.YP.rotationDegrees(rotation));

        // 设置缩放因子
        poseStack.scale(-0.012F, -0.012F, 0.012F); // 使用负值以反转文本，使得正面朝上

        // 文本的宽度，用于居中
        int textWidth = fontRenderer.width(text);

        // 渲染文本正面
        poseStack.pushPose(); // 推送当前的Pose状态以便在旋转背面时恢复
        fontRenderer.drawInBatch(text, -textWidth / 2.0F, 0, 0xFFFFFF, false, poseStack.last().pose(), bufferSource, false, 0, light);
        poseStack.popPose(); // 恢复Pose状态以准备渲染背面

        // 渲染文本背面
        poseStack.mulPose(Vector3f.YP.rotationDegrees(180)); // 旋转180度以渲染背面
        poseStack.scale(1.0F, 1.0F, -1.0F); // 翻转Z轴以确保背面文字的正确方向
        fontRenderer.drawInBatch(text, -textWidth / 2.0F, 0, 0xFFFFFF, false, poseStack.last().pose(), bufferSource, false, 0, light);

        // 恢复Pose状态
        poseStack.popPose();
    }

    private void renderModel(HoloDisplayTerminalBlockEntity blockEntity, PoseStack poseStack, MultiBufferSource buffer, float partialTicks, int combinedLight) {
        CompoundTag entityData = blockEntity.getStoredEntityData();

        if (entityData == null || entityData.isEmpty()) {
            return;
        }

        EntityRenderDispatcher renderer = Minecraft.getInstance().getEntityRenderDispatcher();

        Entity entity = EntityHelper.getEntityByNBT(entityData, blockEntity.getLevel());

        if (entity == null) {
            return;
        }

        Vec3 blockPos = new Vec3(blockEntity.getBlockPos().getX() + 0.5, blockEntity.getBlockPos().getY() + 1.0, blockEntity.getBlockPos().getZ() + 0.5);
        entity.setPos(blockPos.x, blockPos.y, blockPos.z);

        poseStack.pushPose();

        poseStack.translate(0.5, 5.5 / 16, 0.5); // 提升全息投影的位置

        float rotation = (blockEntity.getLevel().getGameTime() + partialTicks) * rotateSpeed % 360;
        poseStack.mulPose(Vector3f.YP.rotationDegrees(rotation));

        entity.setYRot(0);
        entity.setYBodyRot(0);
        entity.setYHeadRot(0);

        float scale = 0.5f; // 根据需要调整缩放比例
        poseStack.scale(scale, scale, scale);

        renderer.render(entity, 0, 0, 0, 0, partialTicks, poseStack, buffer, LightTexture.FULL_BRIGHT);

        poseStack.popPose();
    }

    private ModelData getModelDataForBlock(BlockState state) {
        return ModelData.builder().with(DISPLAY_BLOCK_STATE, state).build();
    }
}
