package techreborn.client.gui;

import java.io.IOException;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.translation.I18n;
import reborncore.client.gui.GuiUtil;
import reborncore.client.multiblock.MultiblockSet;
import reborncore.common.misc.Location;
import reborncore.common.multiblock.CoordTriplet;
import techreborn.client.ClientMultiBlocks;
import techreborn.client.container.ContainerVacuumFreezer;
import techreborn.proxies.ClientProxy;
import techreborn.tiles.TileVacuumFreezer;

public class GuiVacuumFreezer extends GuiContainer
{

	public static final ResourceLocation texture = new ResourceLocation("techreborn",
			"textures/gui/vacuum_freezer.png");

	TileVacuumFreezer crafter;
	ContainerVacuumFreezer containerVacuumFreezer;

	public GuiVacuumFreezer(EntityPlayer player, TileVacuumFreezer tilealloysmelter)
	{
		super(new ContainerVacuumFreezer(tilealloysmelter, player));
		this.xSize = 176;
		this.ySize = 167;
		crafter = tilealloysmelter;
		this.containerVacuumFreezer = (ContainerVacuumFreezer) this.inventorySlots;
	}

	@Override
	public void initGui()
	{
		int k = (this.width - this.xSize) / 2;
		int l = (this.height - this.ySize) / 2;
		GuiButton button = new GuiButton(212, k + this.xSize - 24, l + 4, 20, 20, "");
		buttonList.add(button);
		super.initGui();
		CoordTriplet coordinates = new CoordTriplet(crafter.getPos().getX(), crafter.getPos().getY() - 5,
				crafter.getPos().getZ());
		if (coordinates.equals(ClientProxy.multiblockRenderEvent.anchor))
		{
			ClientProxy.multiblockRenderEvent.setMultiblock(null);
			button.displayString = "B";
		} else
		{
			button.displayString = "A";
		}
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float p_146976_1_, int p_146976_2_, int p_146976_3_)
	{
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		this.mc.getTextureManager().bindTexture(texture);
		int k = (this.width - this.xSize) / 2;
		int l = (this.height - this.ySize) / 2;
		this.drawTexturedModalRect(k, l, 0, 0, this.xSize, this.ySize);

		int j = 0;

		j = crafter.getProgressScaled(24);
		if (j > 0)
		{
			this.drawTexturedModalRect(k + 79, l + 37, 176, 14, j + 1, 16);
		}

		j = (int)(crafter.getEnergy() * 12f / crafter.getMaxPower());
		if (j > 0)
		{
			this.drawTexturedModalRect(k + 26, l + 36 + 12 - j, 176, 12 - j, 14, j + 2);
		}

		if (containerVacuumFreezer.machineStatus == 0)
		{
			GuiUtil.drawTooltipBox(k + 30, l + 50 + 12 - 0, 114, 10);
			this.fontRendererObj.drawString(I18n.translateToLocal("techreborn.message.missingmultiblock"), k + 38,
					l + 52 + 12 - 0, -1);
		}
	}

	protected void drawGuiContainerForegroundLayer(int p_146979_1_, int p_146979_2_)
	{
		String name = I18n.translateToLocal("tile.techreborn.vacuumfreezer.name");
		this.fontRendererObj.drawString(name, this.xSize / 2 - this.fontRendererObj.getStringWidth(name) / 2, 6,
				4210752);
		this.fontRendererObj.drawString(I18n.translateToLocalFormatted("container.inventory", new Object[0]), 8,
				this.ySize - 96 + 2, 4210752);
	}

	@Override
	public void actionPerformed(GuiButton button) throws IOException
	{
		super.actionPerformed(button);
		if (button.id == 212)
		{
			if (ClientProxy.multiblockRenderEvent.currentMultiblock == null)
			{
				{// This code here makes a basic multiblock and then sets to the
					// selected one.
					MultiblockSet set = new MultiblockSet(ClientMultiBlocks.frezzer);
					ClientProxy.multiblockRenderEvent.setMultiblock(set);
					ClientProxy.multiblockRenderEvent.partent = new Location(crafter.getPos().getX(),
							crafter.getPos().getY(), crafter.getPos().getZ(), crafter.getWorld());
					ClientProxy.multiblockRenderEvent.anchor = new CoordTriplet(crafter.getPos().getX(),
							crafter.getPos().getY() - 5, crafter.getPos().getZ());
				}
				button.displayString = "A";
			} else
			{
				ClientProxy.multiblockRenderEvent.setMultiblock(null);
				button.displayString = "B";
			}
		}
	}
}
