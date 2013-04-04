package ato.markerinstaller;

import cpw.mods.fml.common.network.IGuiHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

public class GuiHandler implements IGuiHandler {

    @Override
    public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        switch (ID >> 3) {
            case MarkerInstaller.guiIdInstaller:
                return getContainer(player, x, y, z, ID & 7);
            default:
                return null;
        }
    }

    @Override
    public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        switch (ID >> 3) {
            case MarkerInstaller.guiIdInstaller:
                return new GuiInstaller(getContainer(player, x, y, z, ID & 7));
            default:
                return null;
        }
    }

    private ContainerInstaller getContainer(EntityPlayer player, int x, int y, int z, int side) {
        return new ContainerInstaller(
                player.inventory,
                x, y, z, side,
                player.getCurrentEquippedItem().getTagCompound());
    }
}
