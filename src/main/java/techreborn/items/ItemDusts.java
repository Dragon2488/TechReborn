package techreborn.items;

import java.util.List;

import techreborn.Core;
import techreborn.client.TechRebornCreativeTab;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;

public class ItemDusts extends ItemTR
{
	public static final String[] types = new String[] 
	{
		"Almandine", "Aluminium", "Andradite", "Ashes", "Basalt", "Bauxite", "Brass", "Bronze",
		"Calcite","Charcoal", "Chrome", "Cinnabar", "Clay", "Coal", "Copper", "Dark Ashes", "Diamond",
		"Electrum","Emerald", "Ender Eye", "Ender Pearl", "Endstone", "Flint", "Gold", "Green Sapphire", "Grossular",
		"Invar", "Iron", "Lazurite", "Lead", "Magnesium", "Marble", "Netherrack", "Nickel", "Obsidian",
		"Olivine","Phosphor", "Platinum", "Pyrite", "Pyrope", "Red Garnet", "Redrock", "Ruby", "Saltpeter", "Sapphire",
		"Silver", "Sodalite", "Spessartine", "Sphalerite", "Steel", "Sulfur", "Tin", "Titanium", "Tungsten", "Uranium", 
		"Uvarovite", "Yellow Garnet", "Zinc" 
	};

	private IIcon[] textures;
	
	public ItemDusts()
	{
		setUnlocalizedName("dust");
		setHasSubtypes(true);
		setCreativeTab(TechRebornCreativeTab.instance);
	}
	
	@Override
	// Registers Textures For All Dusts
	public void registerIcons(IIconRegister iconRegister)
	{
		textures = new IIcon[types.length];

		for (int i = 0; i < types.length; ++i) 
		{
			textures[i] = iconRegister.registerIcon("techreborn" + "dust");
		}
	}
	
	@Override
	// Adds Texture what match's meta data
	public IIcon getIconFromDamage(int meta)
	{
		if (meta < 0 || meta >= textures.length) 
		{
			meta = 0;
		}

		return textures[meta];
	}

	@Override
	// gets Unlocalized Name depending on meta data
	public String getUnlocalizedName(ItemStack itemStack)
	{
		int meta = itemStack.getItemDamage();
		if (meta < 0 || meta >= types.length) 
		{
			meta = 0;
		}

		return super.getUnlocalizedName() + "." + types[meta];
	}

	// Adds Dusts SubItems To Creative Tab
	public void getSubItems(Item item, CreativeTabs creativeTabs, List list)
	{
		for (int meta = 0; meta < types.length; ++meta) 
		{
			list.add(new ItemStack(item, 1, meta));
		}
	}
	
}