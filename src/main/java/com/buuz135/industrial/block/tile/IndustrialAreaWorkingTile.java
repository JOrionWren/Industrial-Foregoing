package com.buuz135.industrial.block.tile;

import com.buuz135.industrial.item.RangeAddonItem;
import com.buuz135.industrial.proxy.client.IndustrialAssetProvider;
import com.buuz135.industrial.utils.BlockUtils;
import com.hrznstudio.titanium.annotation.Save;
import com.hrznstudio.titanium.api.IFactory;
import com.hrznstudio.titanium.api.augment.IAugment;
import com.hrznstudio.titanium.api.client.IGuiAddon;
import com.hrznstudio.titanium.block.BlockTileBase;
import com.hrznstudio.titanium.block.tile.button.PosButton;
import com.hrznstudio.titanium.client.gui.addon.StateButtonAddon;
import com.hrznstudio.titanium.client.gui.addon.StateButtonInfo;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.VoxelShape;

import java.util.ArrayList;
import java.util.List;

public abstract class IndustrialAreaWorkingTile extends IndustrialWorkingTile {

    @Save
    private int pointer;
    @Save
    private boolean showingArea;
    private PosButton areaButton;
    private RangeManager.RangeType type;

    public IndustrialAreaWorkingTile(BlockTileBase blockTileBase, RangeManager.RangeType type) {
        super(blockTileBase);
        this.pointer = 0;
        this.showingArea = false;
        addButton(areaButton = new PosButton(176 - 22, 84, 14, 14) {
            @Override
            public List<IFactory<? extends IGuiAddon>> getGuiAddons() {
                List<IFactory<? extends IGuiAddon>> addons = new ArrayList<>();
                addons.add(() -> new StateButtonAddon(areaButton, new StateButtonInfo(0, IndustrialAssetProvider.BUTTON_SHOW_AREA, "text.industrialforegoing.button.show_area"), new StateButtonInfo(1, IndustrialAssetProvider.BUTTON_HIDE_AREA, "text.industrialforegoing.button.hide_area")) {
                    @Override
                    public int getState() {
                        return showingArea ? 1 : 0;
                    }
                });
                return addons;
            }
        }.setPredicate((playerEntity, compoundNBT) -> this.showingArea = !this.showingArea));
        this.type = type;
    }

    public VoxelShape getWorkingArea() {
        return new RangeManager(this.pos, this.getFacingDirection(), this.type).get(hasAugmentInstalled(RangeAddonItem.RANGE) ? ((int) getInstalledAugments(RangeAddonItem.RANGE).get(0).getAugmentRatio() + 1) : 0);
    }

    public BlockPos getPointedBlockPos() {
        List<BlockPos> blockPosList = BlockUtils.getBlockPosInAABB(getWorkingArea().getBoundingBox());
        pointer = safetyPointerCheck(blockPosList);
        return blockPosList.get(pointer);
    }

    private int safetyPointerCheck(List<BlockPos> blockPosList) {
        return pointer < blockPosList.size() ? pointer : 0;
    }

    public void increasePointer() {
        ++pointer;
    }

    public boolean isShowingArea() {
        return showingArea;
    }

    public boolean isLoaded(BlockPos pos) {
        return world.isAreaLoaded(pos, pos);
    }

    @Override
    public boolean canAcceptAugment(IAugment augment) {
        return super.canAcceptAugment(augment) && augment.getAugmentType().equals(RangeAddonItem.RANGE);
    }

    @Override
    public AxisAlignedBB getRenderBoundingBox() {
        return getWorkingArea().getBoundingBox();
    }
}
