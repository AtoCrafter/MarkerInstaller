package ato.markerinstaller;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;

public class ItemInstaller extends Item {

    public ItemInstaller(int id) {
        super(id);
        setCreativeTab(CreativeTabs.tabRedstone);
    }

    @Override
    public boolean onItemUse(ItemStack is, EntityPlayer player, World world, int x, int y, int z, int side,
                             float par8, float par9, float par10) {
        if (!world.isRemote) {
            player.openGui(MarkerInstaller.instance, (MarkerInstaller.guiIdInstaller << 3) + side, world, x, y, z);
        }
        return true;
    }

    @Override
    public String getTextureFile() {
        return "/ato/markerinstaller/item.png";
    }
}
