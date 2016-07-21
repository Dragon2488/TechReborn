package techreborn.tiles.generator;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fluids.*;
import reborncore.api.power.EnumPowerTier;
import reborncore.api.tile.IInventoryProvider;
import reborncore.common.IWrenchable;
import reborncore.common.tile.TilePowerProducer;
import reborncore.common.util.FluidUtils;
import reborncore.common.util.Inventory;
import reborncore.common.util.Tank;
import techreborn.init.ModBlocks;
import techreborn.power.EnergyUtils;

import java.util.HashMap;
import java.util.Map;

public class TileSemifluidGenerator extends TilePowerProducer implements IWrenchable, IFluidHandler,IInventoryProvider {

	// TODO: run this off config
	public static final int euTick = 8;
	public Tank tank = new Tank("TileSemifluidGenerator", FluidContainerRegistry.BUCKET_VOLUME * 10, this);
	public Inventory inventory = new Inventory(3, "TileSemifluidGenerator", 64, this);
	Map<String, Integer> fluids = new HashMap<>();

	// We use this to keep track of fractional millibuckets, allowing us to hit
	// our eu/bucket targets while still only ever removing integer millibucket
	// amounts.
	double pendingWithdraw = 0.0;

	public TileSemifluidGenerator() {
		// TODO: fix this to have SemiFluid generator values

		fluids.put("creosote", 3000);
		fluids.put("biomass", 8000);
		fluids.put("oil", 64000);
		fluids.put("fluidsodium", 30000);
		fluids.put("fluidlithium", 60000);
		fluids.put("biofuel", 32000);
		fluids.put("bioethanol", 32000);
		fluids.put("fuel", 128000);
	}

	@Override
	public double emitEnergy(EnumFacing enumFacing, double amount) {
		BlockPos pos = getPos().offset(enumFacing);
		EnergyUtils.PowerNetReceiver receiver = EnergyUtils.getReceiver(
				worldObj, enumFacing.getOpposite(), pos);
		if(receiver != null) {
			addEnergy(amount - receiver.receiveEnergy(amount, false));
		} else addEnergy(amount);
		return 0; //Temporary hack die to my bug RebornCore
	}

	@Override
	public boolean wrenchCanSetFacing(EntityPlayer entityPlayer, EnumFacing side) {
		return false;
	}

	@Override
	public EnumFacing getFacing() {
		return getFacingEnum();
	}

	@Override
	public boolean wrenchCanRemove(EntityPlayer entityPlayer) {
		return entityPlayer.isSneaking();
	}

	@Override
	public float getWrenchDropRate() {
		return 1.0F;
	}

	@Override
	public ItemStack getWrenchDrop(EntityPlayer entityPlayer) {
		return new ItemStack(ModBlocks.Semifluidgenerator, 1);
	}

	@Override
	public int fill(EnumFacing from, FluidStack resource, boolean doFill) {
		int fill = tank.fill(resource, doFill);
		tank.compareAndUpdate();
		return fill;
	}

	@Override
	public FluidStack drain(EnumFacing from, FluidStack resource, boolean doDrain) {
		FluidStack drain = tank.drain(resource.amount, doDrain);
		tank.compareAndUpdate();
		return drain;
	}

	@Override
	public FluidStack drain(EnumFacing from, int maxDrain, boolean doDrain) {
		FluidStack drain = tank.drain(maxDrain, doDrain);
		tank.compareAndUpdate();
		return drain;
	}

	@Override
	public boolean canFill(EnumFacing from, Fluid fluid) {
		if (fluid != null) {
			return fluids.containsKey(FluidRegistry.getFluidName(fluid));
		}
		return false;
	}

	@Override
	public boolean canDrain(EnumFacing from, Fluid fluid) {
		return tank.getFluid() == null || tank.getFluid().getFluid() == fluid;
	}

	@Override
	public FluidTankInfo[] getTankInfo(EnumFacing from) {
		return new FluidTankInfo[]{tank.getInfo()};
	}

	@Override
	public void readFromNBT(NBTTagCompound tagCompound) {
		super.readFromNBT(tagCompound);
		tank.readFromNBT(tagCompound);
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound tagCompound) {
		super.writeToNBT(tagCompound);
		tank.writeToNBT(tagCompound);
		return tagCompound;
	}

	@Override
	public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity packet) {
		worldObj.markBlockRangeForRenderUpdate(getPos().getX(), getPos().getY(), getPos().getZ(), getPos().getX(),
				getPos().getY(), getPos().getZ());
		readFromNBT(packet.getNbtCompound());
	}

	@Override
	public void update() {
		super.update();
		if (!worldObj.isRemote)
			FluidUtils.drainContainers(this, inventory, 0, 1);

		if (tank.getFluidAmount() > 0 && getMaxPower() - getEnergy() >= euTick) {
			Integer euPerBucket = fluids.get(tank.getFluidType().getName());
			// float totalTicks = (float)euPerBucket / 8f; //x eu per bucket / 8
			// eu per tick
			// float millibucketsPerTick = 1000f / totalTicks;
			float millibucketsPerTick = 8000f / (float) euPerBucket;
			pendingWithdraw += millibucketsPerTick;

			int currentWithdraw = (int) pendingWithdraw; // float --> int
			// conversion floors
			// the float
			pendingWithdraw -= currentWithdraw;

			tank.drain(currentWithdraw, true);
			addEnergy(euTick);
		}
		if (tank.getFluidType() != null && getStackInSlot(2) == null) {
			inventory.setInventorySlotContents(2, new ItemStack(tank.getFluidType().getBlock()));
		} else if (tank.getFluidType() == null && getStackInSlot(2) != null) {
			setInventorySlotContents(2, null);
		}

	}

	@Override
	public double getMaxPower() {
		return 16000;
	}

	@Override
	public EnumPowerTier getTier() {
		return EnumPowerTier.LOW;
	}

	@Override
	public Inventory getInventory() {
		return inventory;
	}

}
