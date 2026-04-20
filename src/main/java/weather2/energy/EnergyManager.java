package weather2.energy;

import net.neoforged.neoforge.transfer.energy.SimpleEnergyHandler;

public class EnergyManager extends SimpleEnergyHandler {
    private boolean canExtract = true;

    public EnergyManager(int maxTransfer, int capacity) {
        super(capacity, maxTransfer, maxTransfer);
    }

    public int getMaxExtract() {
        return maxExtract;
    }

    public void setReceiveOnly() {
        canExtract = false;
    }/*

    @Override
    public void read(CompoundTag nbt) {
        setEnergyStored(nbt.getInt("Energy"));
    }

    @Override
    public CompoundTag write(CompoundTag nbt) {
        nbt.putInt("Energy", energy);
        return nbt;
    }*/

    public int getMaxEnergyReceived() {
        return this.maxInsert;
    }

    /**
     * Drains an amount of energy, due to decay from lack of work or other factors
     */
    public void drainEnergy(int amount) {
        setEnergyStored(energy - amount);
    }

    public void addEnergy(int amount) {
        setEnergyStored(energy + amount);
    }

    public int getEnergy() {
        return energy;
    }

    public void setEnergyStored(int energyStored) {
        this.energy = energyStored;
        if (this.energy > capacity) {
            this.energy = capacity;
        } else if (this.energy < 0) {
            this.energy = 0;
        }
    }

    /*public <T> LazyOptional<T> getCapability(Capability<T> capability) {
        if (capability == ForgeCapabilities.ENERGY) {
            //IEnergyStorage energyStorage = new EnergyStorageWrapper(this, canExtract);
            return LazyOptional.of(() -> this).cast();
        }

        return LazyOptional.empty();
    }*/
}
