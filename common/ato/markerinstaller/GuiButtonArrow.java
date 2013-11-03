package ato.markerinstaller;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiBeacon;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

public class GuiButtonArrow extends GuiButton {

    public enum Type {
        INCREMENT,
        DOUBLE,
        DECREMENT,
        HALF,
    }

    private final Type type;

    public GuiButtonArrow(int id, Type type, int x, int y) {
        super(id, x, y, 10, 10, "");
        this.type = type;
    }

    @Override
    public void drawButton(Minecraft mc, int par2, int par3) {
        if (this.drawButton) {
            mc.getTextureManager().bindTexture(new ResourceLocation("/mods/markerinstaller/textures/gui/installer.png"));
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            int i = type.ordinal();
            int dx = 10 * (i % 2);
            int dy = 10 * (i / 2);
            this.drawTexturedModalRect(xPosition, yPosition, 176 + dx, 0 + dy, this.width, this.height);
        }
    }
}
