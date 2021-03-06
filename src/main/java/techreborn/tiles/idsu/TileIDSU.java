package techreborn.tiles.idsu;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import org.apache.commons.lang3.StringUtils;
import reborncore.api.power.EnumPowerTier;
import reborncore.common.IWrenchable;
import reborncore.common.tile.TilePowerAcceptorProducer;
import techreborn.config.ConfigTechReborn;
import techreborn.init.ModBlocks;
import techreborn.power.EnergyUtils;

public class TileIDSU extends TilePowerAcceptorProducer implements IWrenchable {

	public String ownerUdid;
	public int tier;
	public int output;
	public double maxStorage;
	private double euLastTick = 0;
	private double euChange;
	private int ticks;

	public TileIDSU(int tier1, int output1, int maxStorage1) {
		this.tier = tier1;
		this.output = output1;
		this.maxStorage = maxStorage1;
	}

	public TileIDSU() {
		this(5, 2048, 100000000);
	}

	@Override
	public double getEnergy() {
		if (ownerUdid == null && StringUtils.isBlank(ownerUdid) || StringUtils.isEmpty(ownerUdid)) {
			return 0.0;
		}
		return IDSUManager.INSTANCE.getSaveDataForWorld(worldObj, ownerUdid).storedPower;
	}

	@Override
	public void setEnergy(double energy) {
		if (ownerUdid == null && StringUtils.isBlank(ownerUdid) || StringUtils.isEmpty(ownerUdid)) {
			return;
		}
		IDSUManager.INSTANCE.getSaveDataForWorld(worldObj, ownerUdid).storedPower = energy;
	}

	@Override
	public double getMaxPower() {
		return 1000000000;
	}

	@Override
	public EnumPowerTier getTier() {
		return EnumPowerTier.INSANE;
	}

	public float getChargeLevel() {
		float ret = (float) this.getEnergy() / (float) this.maxStorage;
		if (ret > 1.0F) {
			ret = 1.0F;
		}

		return ret;
	}

	public void readFromNBT(NBTTagCompound nbttagcompound) {
		super.readFromNBT(nbttagcompound);
		this.ownerUdid = nbttagcompound.getString("ownerUdid");
	}

	public NBTTagCompound writeToNBT(NBTTagCompound nbttagcompound) {
		super.writeToNBT(nbttagcompound);
		if (ownerUdid == null || StringUtils.isEmpty(ownerUdid)) {
			return nbttagcompound;
		}
		nbttagcompound.setString("ownerUdid", this.ownerUdid);
		return nbttagcompound;
	}

	public void update() {
		super.update();

		if (ticks == ConfigTechReborn.AverageEuOutTickTime) {
			euChange = -1;
			ticks = 0;

		} else {
			ticks++;
			euChange += getEnergy() - euLastTick;
			if (euLastTick == getEnergy()) {
				euChange = 0;
			}
		}

		euLastTick = getEnergy();

		if (!worldObj.isRemote && getEnergy() > 0) {
			double maxOutput = getEnergy() > getMaxOutput() ? getMaxOutput() : getEnergy();
			for(EnumFacing facing : EnumFacing.VALUES) {
				double disposed = emitEnergy(facing, maxOutput);
				if(disposed != 0) {
					maxOutput -= disposed;
					useEnergy(disposed);
					if (maxOutput == 0) return;
				}
			}
		}

	}

	//TODO move to RebornCore
	public double emitEnergy(EnumFacing enumFacing, double amount) {
		BlockPos pos = getPos().offset(enumFacing);
		EnergyUtils.PowerNetReceiver receiver = EnergyUtils.getReceiver(
				worldObj, enumFacing.getOpposite(), pos);
		if(receiver != null) {
			return receiver.receiveEnergy(amount, false);
		}
		return 0;
	}


	public boolean wrenchCanSetFacing(EntityPlayer entityPlayer, int side) {
		return false;
	}

	@Override
	public boolean wrenchCanSetFacing(EntityPlayer p0, EnumFacing p1) {
		return true;
	}

	@Override
	public EnumFacing getFacing() {
		return getFacingEnum();
	}

	@Override
	public boolean wrenchCanRemove(EntityPlayer p0) {
		return true;
	}

	@Override
	public float getWrenchDropRate() {
		return 1.0F;
	}

	public ItemStack getWrenchDrop(EntityPlayer entityPlayer) {
		NBTTagCompound tileEntity = new NBTTagCompound();
		ItemStack dropStack = new ItemStack(ModBlocks.Idsu, 1);
		writeToNBT(tileEntity);
		dropStack.setTagCompound(new NBTTagCompound());
		dropStack.getTagCompound().setTag("tileEntity", tileEntity);
		return dropStack;
	}

	public double getEuChange() {
		if (euChange == -1) {
			return -1;
		}
		return (euChange / ticks);
	}

	public void handleGuiInputFromClient(int id) {
		if (id == 0) {
			output += 256;
		}
		if (id == 1) {
			output += 64;
		}
		if (id == 2) {
			output -= 64;
		}
		if (id == 3) {
			output -= 256;
		}
		if (output > 4096) {
			output = 4096;
		}
		if (output <= -1) {
			output = 0;
		}
	}

}
