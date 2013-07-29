package ato.markerinstaller;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.Init;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.Mod.PreInit;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkMod;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.LanguageRegistry;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.Configuration;

@Mod(
        modid = "MarkerInstaller",
        name = "Marker Installer",
        version = "@VERSION@"
)
@NetworkMod(
        serverSideRequired = true,
        clientSideRequired = true,
        channels = {"GUI_INSTALLER"},
        packetHandler = PacketHandler.class
)
public class MarkerInstaller {

    public static final int guiIdInstaller = 1;
    @Instance("MarkerInstaller")
    public static MarkerInstaller instance;
    private int itemIDInstaller;

    @PreInit
    public void preLoad(FMLPreInitializationEvent event) {
        Configuration config = new Configuration(event.getSuggestedConfigurationFile());
        itemIDInstaller = config.getItem("MarkerInstaller", 19102).getInt();
    }

    @Init
    public void load(FMLInitializationEvent event) {
        Item installer = new ItemInstaller(itemIDInstaller).setUnlocalizedName("MarkerInstaller");
        LanguageRegistry.addName(installer, "Marker Installer");
        NetworkRegistry.instance().registerGuiHandler(instance, new GuiHandler());
        // 制作レシピ
        GameRegistry.addRecipe(new ItemStack(installer), new Object[]{
                "o",
                "|",
                Character.valueOf('o'), Item.enderPearl,
                Character.valueOf('|'), Item.stick
        });
        // リセットレシピ
        GameRegistry.addShapelessRecipe(new ItemStack(installer), new Object[]{
                new ItemStack(installer)
        });
    }
}
