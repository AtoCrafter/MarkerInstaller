package ato.markerinstaller;

import cpw.mods.fml.common.network.IPacketHandler;
import cpw.mods.fml.common.network.Player;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet250CustomPayload;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;

public class PacketHandler implements IPacketHandler {

    @Override
    public void onPacketData(INetworkManager manager, Packet250CustomPayload packet, Player player) {
        DataInputStream in = new DataInputStream(new ByteArrayInputStream(packet.data));
        try {
            int type = in.readInt();
            int diffX = in.readInt();
            int diffY = in.readInt();
            int diffZ = in.readInt();
            int dir = in.readByte();
            if (player instanceof EntityPlayer) {
                EntityPlayer entityPlayer = (EntityPlayer) player;
                Container container = entityPlayer.openContainer;
                if (container instanceof ContainerInstaller) {
                    ContainerInstaller ci = (ContainerInstaller) container;
                    if (type == PacketType.INSTALL.ordinal()) {
                        ci.install(entityPlayer, entityPlayer.worldObj, diffX, diffY, diffZ, dir);
                    } else if (type == PacketType.SAVE.ordinal()) {
                        ci.save(entityPlayer.getCurrentEquippedItem());
                    } else if (type == PacketType.UPDATE.ordinal()) {
                        ci.update(diffX, diffY, diffZ);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public enum PacketType {
        INSTALL,
        SAVE,
        UPDATE,
    }
}
