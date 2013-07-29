package ato.markerinstaller;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.*;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

public class ContainerInstaller extends Container {

    private final IInventory sink;
    private final int xCoord, yCoord, zCoord, side;
    private int xRange, yRange, zRange;

    public ContainerInstaller(IInventory playerInventory, int x, int y, int z, int side, NBTTagCompound nbt) {
        this.xCoord = x;
        this.yCoord = y;
        this.zCoord = z;
        this.side = side;
        sink = new InventoryBasic("MarkerInstallerSink", false, 1);

        addSlotToContainer(new Slot(sink, 0, 100, 14));
        for (int i = 0; i < 3; ++i) {
            for (int j = 0; j < 9; ++j) {
                this.addSlotToContainer(new Slot(playerInventory, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
            }
        }
        for (int i = 0; i < 9; ++i) {
            this.addSlotToContainer(new Slot(playerInventory, i, 8 + i * 18, 142));
        }
        readFromNBT(nbt);
    }

    @Override
    public boolean canInteractWith(EntityPlayer var1) {
        return true;
    }

    /**
     * @see net.minecraft.inventory.ContainerWorkbench#onCraftGuiClosed(net.minecraft.entity.player.EntityPlayer)
     */
    @Override
    public void onCraftGuiClosed(EntityPlayer player) {
        super.onCraftGuiClosed(player);
        if (!player.worldObj.isRemote) {
            ItemStack is = sink.getStackInSlotOnClosing(0);
            if (is != null) {
                player.dropPlayerItem(is);
            }
        }
    }

    @Override
    public void updateProgressBar(int type, int value) {
        switch (type) {
            case 0:
                xRange = value;
                break;
            case 1:
                yRange = value;
                break;
            case 2:
                zRange = value;
                break;
        }
    }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer player, int index) {
        ItemStack stackBackup = null;
        Slot slot = (Slot) inventorySlots.get(index);

        if (slot != null && slot.getHasStack()) {
            ItemStack slotStack = slot.getStack();
            stackBackup = slotStack.copy();

            int border = inventorySlots.size() - 36;
            if (index < border) {
                if (!mergeItemStack(slotStack, border, inventorySlots.size(), false)) {
                    return null;
                }
            } else {
                Slot sinkSlot = (Slot)inventorySlots.get(0);
                if (sinkSlot != null && !sinkSlot.getHasStack()) {
                    sinkSlot.putStack(slotStack.copy());
                    slotStack.stackSize = 0;
                } else {
                    return null;
                }
            }
            if (slotStack.stackSize == 0) {
                slot.putStack(null);
            } else {
                slot.onSlotChanged();
            }
        }

        return stackBackup;
    }

    public void install(EntityPlayer player, World world, int x, int y, int z, int dir) {
        ItemStack is = sink.getStackInSlot(0);
        if (is != null && is.getItem() != null) {
            Item item = is.getItem();

            // 向いている方向に合わせる
            int diffX = 0, diffY = 0, diffZ = 0;
            switch (dir) {
                case 0: // 南向き
                    diffX = -x;
                    diffZ = z;
                    break;
                case 1: // 西向き
                    diffX = -z;
                    diffZ = -x;
                    break;
                case 2: // 北向き
                    diffX = x;
                    diffZ = -z;
                    break;
                case 3: // 東向き
                    diffX = z;
                    diffZ = x;
                    break;
            }
            diffY = y;

            // 値の補正（diff の絶対値が辺の長さとなるように）
            if (diffX < 0) {
                ++diffX;
            } else if (diffX > 0) {
                --diffX;
            }
            if (diffY < 0) {
                ++diffY;
            } else if (diffY > 0) {
                --diffY;
            }
            if (diffZ < 0) {
                ++diffZ;
            } else if (diffZ > 0) {
                --diffZ;
            }

            // 設置
            item.onItemUse(is, player, world, xCoord, yCoord, zCoord, side, 0, 0, 0);
            item.onItemUse(is, player, world, xCoord + diffX, yCoord, zCoord, side, 0, 0, 0);
            item.onItemUse(is, player, world, xCoord, yCoord + diffY, zCoord, side, 0, 0, 0);
            item.onItemUse(is, player, world, xCoord, yCoord, zCoord + diffZ, side, 0, 0, 0);

            if (is.stackSize == 0) {
                sink.setInventorySlotContents(0, null);
            }
        }
    }

    public void update(int x, int y, int z) {
        xRange = x;
        yRange = y;
        zRange = z;
        for (int i = 0; i < crafters.size(); ++i) {
            ICrafting crafter = (ICrafting) crafters.get(i);
            crafter.sendProgressBarUpdate(this, 0, x);
            crafter.sendProgressBarUpdate(this, 1, y);
            crafter.sendProgressBarUpdate(this, 2, z);
        }
    }

    public void save(ItemStack is) {
        if (!is.hasTagCompound()) {
            is.setTagCompound(new NBTTagCompound());
        }
        writeToNBT(is.getTagCompound());
    }

    private void readFromNBT(NBTTagCompound nbt) {
        if (nbt != null) {
            int x = nbt.getInteger("xRange");
            int y = nbt.getInteger("yRange");
            int z = nbt.getInteger("zRange");
            update(x, y, z);
        }
    }

    private void writeToNBT(NBTTagCompound nbt) {
        if (nbt != null) {
            nbt.setInteger("xRange", xRange);
            nbt.setInteger("yRange", yRange);
            nbt.setInteger("zRange", zRange);
        }
    }

    public int getxRange() {
        return xRange;
    }

    public int getzRange() {
        return zRange;
    }

    public int getyRange() {
        return yRange;
    }
}
