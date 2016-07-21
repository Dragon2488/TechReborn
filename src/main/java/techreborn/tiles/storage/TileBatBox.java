package techreborn.tiles.storage;

import reborncore.api.IListInfoProvider;
import reborncore.api.power.EnumPowerTier;
import techreborn.init.ModBlocks;

import java.util.List;

/**
 * Created by modmuss50 on 14/03/2016.
 */
public class TileBatBox extends TileEnergyStorage {

	public TileBatBox() {
		super("BatBox", 2, ModBlocks.batBox, EnumPowerTier.LOW, 32, 32, 40000);
	}

}
