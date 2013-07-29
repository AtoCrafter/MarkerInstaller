package ato.markerinstaller;

import cpw.mods.fml.common.network.PacketDispatcher;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.inventory.Container;
import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraft.util.MathHelper;
import org.lwjgl.opengl.GL11;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import static ato.markerinstaller.GuiButtonArrow.Type;
import static ato.markerinstaller.PacketHandler.PacketType;

public class GuiInstaller extends GuiContainer {

    private static final int TOP_MARGIN = 14;
    private static final int LEFT_MARGIN = 7;
    private static final int GRID_SIZE = 20;
    private static final int TEXT_WIDTH = 60;
    private static final int BUTTON_SIZE_SMALL = 10;
    private static final int BUTTON_WIDTH_INSTALL = 40;
    private static final int BUTTON_WIDTH_SAVE = BUTTON_WIDTH_INSTALL * 8 / 10;
    private static final int BUTTON_HEIGHT = 18;
    private static final int BUTTON_ID_INSTALL = 1;
    private static final int BUTTON_ID_SAVE = 2;
    private static final int BUTTON_ID_BASE_RANGES = 100;

    public GuiInstaller(Container container) {
        super(container);
    }

    @Override
    public void initGui() {
        super.initGui();
        // 座標の計算
        int ox = (width - xSize) / 2;
        int oy = (height - ySize) / 2;
        int left = ox + LEFT_MARGIN;
        // ボタンの生成
        for (int i = 0; i < 3; ++i) {
            int line = oy + TOP_MARGIN + (GRID_SIZE - BUTTON_SIZE_SMALL) / 2 + GRID_SIZE * i;
            GuiButton butHalf = new GuiButtonArrow(BUTTON_ID_BASE_RANGES + i * 4 + 0, Type.HALF, left, line);
            GuiButton butDecrement = new GuiButtonArrow(BUTTON_ID_BASE_RANGES + i * 4 + 1, Type.DECREMENT, left + BUTTON_SIZE_SMALL, line);
            GuiButton butIncrement = new GuiButtonArrow(BUTTON_ID_BASE_RANGES + i * 4 + 2, Type.INCREMENT, left + BUTTON_SIZE_SMALL + TEXT_WIDTH, line);
            GuiButton butDouble = new GuiButtonArrow(BUTTON_ID_BASE_RANGES + i * 4 + 3, Type.DOUBLE, left + TEXT_WIDTH + BUTTON_SIZE_SMALL * 2, line);
            buttonList.add(butIncrement);
            buttonList.add(butDecrement);
            buttonList.add(butDouble);
            buttonList.add(butHalf);
        }
        int right = ox + xSize - LEFT_MARGIN;
        GuiButton butInstall = new GuiButton(BUTTON_ID_INSTALL, right - BUTTON_WIDTH_INSTALL, oy + TOP_MARGIN, BUTTON_WIDTH_INSTALL, BUTTON_HEIGHT, "Install");
        GuiButton butSave = new GuiButton(BUTTON_ID_SAVE, right - BUTTON_WIDTH_SAVE, oy + TOP_MARGIN + GRID_SIZE * 2, BUTTON_WIDTH_SAVE, BUTTON_HEIGHT, "Save");
        buttonList.add(butInstall);
        buttonList.add(butSave);
    }

    /**
     * @see net.minecraft.client.gui.inventory.GuiChest#drawGuiContainerBackgroundLayer(float, int, int)
     */
    @Override
    protected void drawGuiContainerBackgroundLayer(float var1, int var2, int var3) {
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        mc.renderEngine.bindTexture("/mods/markerinstaller/textures/gui/installer.png");
        int var5 = (width - xSize) / 2;
        int var6 = (height - ySize) / 2;
        drawTexturedModalRect(var5, var6, 0, 0, xSize, ySize);
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int par1, int par2) {
        GL11.glDisable(GL11.GL_LIGHTING);

        ContainerInstaller con = (ContainerInstaller) this.inventorySlots;
        String[] text = new String[]{"Right:" + con.getxRange(), "Up:" + con.getyRange(), "Front:" + con.getzRange()};
        for (int i = 0; i < text.length; ++i) {
            int line = TOP_MARGIN + GRID_SIZE * i;
            fontRenderer.drawString(text[i],
                    LEFT_MARGIN + BUTTON_SIZE_SMALL * 2 + 4,
                    line + (GRID_SIZE - fontRenderer.FONT_HEIGHT) / 2,
                    0x000000);
        }

        GL11.glEnable(GL11.GL_LIGHTING);
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        ContainerInstaller con = (ContainerInstaller) this.inventorySlots;
        int x = con.getxRange();
        int y = con.getyRange();
        int z = con.getzRange();
        if (button.id == BUTTON_ID_INSTALL) {
            int dir = MathHelper.floor_double((double) (this.mc.thePlayer.rotationYaw * 4.0F / 360.0F) + 0.5D) & 3;
            sendPakcet(PacketType.INSTALL, x, y, z, dir);
        } else if (button.id == BUTTON_ID_SAVE) {
            sendPakcet(PacketType.SAVE, x, y, z, 0);
        } else if (BUTTON_ID_BASE_RANGES <= button.id) {
            int xyz = (button.id - 100) / 4;
            int operation = (button.id - 100) % 4;
            int[] target = new int[]{x, y, z};

            switch (operation) {
                case 0: // half
                    target[xyz] -= 10;
                    break;
                case 1: // decrement
                    --target[xyz];
                    break;
                case 2: // increment
                    ++target[xyz];
                    break;
                case 3: // double
                    target[xyz] += 10;
                    break;
            }

            sendPakcet(PacketType.UPDATE, target[0], target[1], target[2], 0);
        }
    }

    private void sendPakcet(PacketType type, int x, int y, int z, int dir) {
        ByteArrayOutputStream data = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream(data);
        try {
            out.writeInt(type.ordinal());
            out.writeInt(x);
            out.writeInt(y);
            out.writeInt(z);
            out.writeByte(dir);
            PacketDispatcher.sendPacketToServer(new Packet250CustomPayload("GUI_INSTALLER", data.toByteArray()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
