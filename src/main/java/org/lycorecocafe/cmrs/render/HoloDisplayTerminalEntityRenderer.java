package org.lycorecocafe.cmrs.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Vector3f;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.model.data.ModelData;
import org.jetbrains.annotations.NotNull;
import org.lycorecocafe.cmrs.blockentity.holo.HoloDisplayTerminalBlockEntity;
import org.lycorecocafe.cmrs.utils.game.entity.EntityHelper;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import static net.minecraft.client.renderer.LightTexture.FULL_BRIGHT;
import static org.lycorecocafe.cmrs.blocks.HoloDisplayTerminalBlock.FACING;

@OnlyIn(Dist.CLIENT)
public class HoloDisplayTerminalEntityRenderer implements BlockEntityRenderer<HoloDisplayTerminalBlockEntity> {

    public static final RenderType HOLO_DISPLAY = RenderType.entityTranslucent(InventoryMenu.BLOCK_ATLAS);
    private float rotateSpeed = 0f;

    public HoloDisplayTerminalEntityRenderer(BlockEntityRendererProvider.Context context) {

    }

    //TODO: 超过基岩之后基岩之上的方块也不会渲染
    //TODO: 追踪生物
    @Override
    public void render(HoloDisplayTerminalBlockEntity blockEntity, float partialTicks, @NotNull PoseStack poseStack, @NotNull MultiBufferSource buffer, int combinedLight, int combinedOverlay) {
        // 获取要显示的方块区域状态
        this.rotateSpeed = blockEntity.getRotateSpeed();
        HoloDisplayTerminalBlockEntity.MODE mode = blockEntity.getMode();
        switch (mode) {
            case AREA, TRACKER -> renderArea(blockEntity, poseStack, buffer, partialTicks, FULL_BRIGHT);
            case MODEL -> renderModel(blockEntity, poseStack, buffer, partialTicks, FULL_BRIGHT);
            case OFFLINE ->
                    renderTextAboveBlock(blockEntity, partialTicks, "Player Offline...", poseStack, buffer, FULL_BRIGHT);
        }
    }

    private void renderArea(HoloDisplayTerminalBlockEntity blockEntity, PoseStack poseStack, MultiBufferSource buffer, float partialTicks, int combinedLight) {
        BlockState[][][] blockStates = blockEntity.getDisplayBlockStates();
        if (blockStates.length == 0) return;

        double width = blockEntity.getDisplayRange().getXsize();
        double depth = blockEntity.getDisplayRange().getZsize();

        // 确保最大边长不超过一个方块
        double maxDimension = Math.max(width, depth);
        float scale = 0.8f;

        if (maxDimension > 0) {
            scale = (1.0f / (float) (maxDimension + 1f)) * scale;
        }

        // 使用游戏内时间和 partialTicks 来平滑旋转
        float rotation = (Objects.requireNonNull(blockEntity.getLevel()).getGameTime() + partialTicks) * rotateSpeed % 360;

        // 应用整体旋转和缩放
        poseStack.pushPose();
        poseStack.translate(0.5, (double) 5 / 16, 0.5);
        poseStack.mulPose(Vector3f.YP.rotationDegrees(rotation));
        poseStack.mulPose(Vector3f.YP.rotationDegrees(blockEntity.getBlockState().getValue(FACING).getStepX() * 90));
        poseStack.scale(scale, scale, scale);

        // 预先计算半长，以优化计算
        double halfX = blockStates.length / 2.0;
        double halfZ = blockStates[0][0].length / 2.0;

        BlockRenderDispatcher blockRenderer = Minecraft.getInstance().getBlockRenderer();
        VertexConsumer buffer2 = buffer.getBuffer(HOLO_DISPLAY);

        for (int x = 0; x < blockStates.length; x++) {
            for (int y = 0; y < blockStates[0].length; y++) {
                for (int z = 0; z < blockStates[0][0].length; z++) {
                    BlockState blockState = blockStates[x][y][z];
                    if (blockState != null && !blockState.isAir()) {
                        RandomSource random = RandomSource.create(639);
                        BlockPos relativePos = new BlockPos(x - halfX, y, z - halfZ);
                        BakedModel model = blockRenderer.getBlockModel(blockState);

                        poseStack.pushPose();
                        poseStack.translate(relativePos.getX(), relativePos.getY(), relativePos.getZ());

                        Set<Direction> renderedFaces = new HashSet<>();

                        for (Direction direction : Direction.values()) {
                            if (shouldRenderFace(blockStates, x, y, z, direction) && !renderedFaces.contains(direction)) {
                                List<BakedQuad> quads = model.getQuads(blockState, direction, random, ModelData.EMPTY, RenderType.cutout());
                                for (BakedQuad quad : quads) {
                                    renderSignalQuad(poseStack, combinedLight, buffer2, blockState, quad);
                                }
                                renderedFaces.add(direction);
                            }
                        }

                        // 渲染没有特定方向的 quads（通常是方块的主体部分）
                        List<BakedQuad> generalQuads = model.getQuads(blockState, null, random, ModelData.EMPTY, RenderType.cutout());
                        for (BakedQuad quad : generalQuads) {
                            renderSignalQuad(poseStack, combinedLight, buffer2, blockState, quad);
                        }

                        poseStack.popPose();
                    }
                }
            }
        }

        // 渲染范围内的所有实体
        AABB displayRange = blockEntity.getDisplayRange();
        List<Entity> entities = blockEntity.getLevel().getEntities(null, displayRange);
        EntityRenderDispatcher renderer = Minecraft.getInstance().getEntityRenderDispatcher();
        Vec3 center = displayRange.getCenter();
        for (Entity entity : entities) {
            poseStack.pushPose();
            poseStack.translate(entity.getX() - center.x, entity.getY() - center.y + 3 + displayRange.getYsize() / 2 - 3, entity.getZ() - center.z);
            poseStack.translate(-1, 0, -1);
            renderer.render(entity, 0, 0, 0, 0, partialTicks, poseStack, buffer, combinedLight);
            poseStack.popPose();
        }

        poseStack.popPose();
    }

    private void renderSignalQuad(PoseStack poseStack, int combinedLight, VertexConsumer buffer2, BlockState blockState, BakedQuad quad) {
        int color = quad.getTintIndex() != -1 ?
                Minecraft.getInstance().getBlockColors().getColor(blockState, null, null, quad.getTintIndex()) :
                0xFFFFFF;
        float r = (color >> 16 & 255) / 255.0F;
        float g = (color >> 8 & 255) / 255.0F;
        float b = (color & 255) / 255.0F;
        buffer2.putBulkData(poseStack.last(), quad, r, g, b, combinedLight, OverlayTexture.NO_OVERLAY);
    }

    private boolean shouldRenderFace(BlockState[][][] blockStates, int x, int y, int z, Direction direction) {
        int nx = x + direction.getStepX();
        int ny = y + direction.getStepY();
        int nz = z + direction.getStepZ();

        if (nx < 0 || nx >= blockStates.length || ny < 0 || ny >= blockStates[0].length || nz < 0 || nz >= blockStates[0][0].length) {
            return true;
        }

        BlockState neighborState = blockStates[nx][ny][nz];
        return neighborState == null || neighborState.isAir() || !neighborState.isSolidRender(null, null);
    }

    private void renderTextAboveBlock(BlockEntity blockEntity, float partialTicks, String text, PoseStack poseStack, MultiBufferSource bufferSource, int light) {
        Minecraft minecraft = Minecraft.getInstance();
        Font fontRenderer = minecraft.font;

        poseStack.pushPose();

        // 使用游戏内时间和 partialTicks 来平滑旋转
        float rotation = (Objects.requireNonNull(blockEntity.getLevel()).getGameTime() + partialTicks) * 0.25f % 360;

        poseStack.translate(0.5, (double) 10 / 16, 0.5);
        poseStack.mulPose(Vector3f.YP.rotationDegrees(rotation));
        poseStack.scale(-0.012F, -0.012F, 0.012F); // 使用负值以反转文本，使得正面朝上

        int textWidth = fontRenderer.width(text);

        // 渲染文本正面
        poseStack.pushPose();
        fontRenderer.drawInBatch(text, -textWidth / 2.0F, 0, 0xFFFFFF, false, poseStack.last().pose(), bufferSource, false, 0, light);
        poseStack.popPose(); // 恢复Pose状态以准备渲染背面

        // 渲染文本背面
        poseStack.mulPose(Vector3f.YP.rotationDegrees(180));
        poseStack.scale(1.0F, 1.0F, -1.0F);
        fontRenderer.drawInBatch(text, -textWidth / 2.0F, 0, 0xFFFFFF, false, poseStack.last().pose(), bufferSource, false, 0, light);

        poseStack.popPose();
    }

    private void renderModel(HoloDisplayTerminalBlockEntity blockEntity, PoseStack poseStack, MultiBufferSource buffer, float partialTicks, int combinedLight) {
        CompoundTag entityData = blockEntity.getStoredEntityData();

        if (entityData == null || entityData.isEmpty()) {
            renderTextAboveBlock(blockEntity, partialTicks, "Stored Entity: N/A", poseStack, buffer, combinedLight);
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

        float rotation = (Objects.requireNonNull(blockEntity.getLevel()).getGameTime() + partialTicks) * rotateSpeed % 360;
        poseStack.mulPose(Vector3f.YP.rotationDegrees(rotation));

        entity.setYRot(0);
        entity.setYBodyRot(0);
        entity.setYHeadRot(0);

        float scale = 0.5f;
        poseStack.scale(scale, scale, scale);

        renderer.render(entity, 0, 0, 0, 0, partialTicks, poseStack, buffer, combinedLight);

        poseStack.popPose();
    }
}

