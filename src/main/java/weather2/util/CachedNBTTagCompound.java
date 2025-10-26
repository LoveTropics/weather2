package weather2.util;

import net.minecraft.nbt.CompoundTag;

/**
 * Caches nbt data to remove redundant data sending over network
 *
 * @author cosmicdan
 *
 * revisions made to further integrate it into the newer design of WeatherObjects
 */
public class CachedNBTTagCompound {
	private CompoundTag newData;
	private CompoundTag cachedData;
	private boolean forced = false;

	public CachedNBTTagCompound() {
		this.newData = new CompoundTag();
		this.cachedData = new CompoundTag();
	}

	public void setCachedNBT(CompoundTag cachedData) {
		if (cachedData == null)
			cachedData = new CompoundTag();
		this.cachedData = cachedData;
	}

	public CompoundTag getCachedNBT() {
		return cachedData;
	}

	public CompoundTag getNewNBT() {
		return newData;
	}

	public void setNewNBT(CompoundTag newData) {
		this.newData = newData;
	}

	public void setUpdateForced(boolean forced) {
		this.forced = forced;
	}

	public long getLong(String key) {
		if (!newData.contains(key))
			newData.putLong(key, cachedData.getLongOr(key, 0));
		return newData.getLongOr(key, 0);
	}

	public void putLong(String key, long newVal) {
		if (!cachedData.contains(key) || cachedData.getLongOr(key, 0) != newVal || forced) {
			newData.putLong(key, newVal);
		}
		cachedData.putLong(key, newVal);
	}

	public int getInt(String key) {
		if (!newData.contains(key))
			newData.putInt(key, cachedData.getIntOr(key, 0));
		return newData.getIntOr(key, 0);
	}

	public void putInt(String key, int newVal) {
		if (!cachedData.contains(key) || cachedData.getIntOr(key, 0) != newVal || forced) {
			newData.putInt(key, newVal);
		}
		cachedData.putInt(key, newVal);
	}

	public short getShort(String key) {
		if (!newData.contains(key))
			newData.putShort(key, cachedData.getShortOr(key, (short) 0));
		return newData.getShortOr(key, (short) 0);
	}

	public void putShort(String key, short newVal) {
		if (!cachedData.contains(key) || cachedData.getShortOr(key, (short) 0) != newVal || forced) {
			newData.putShort(key, newVal);
		}
		cachedData.putShort(key, newVal);
	}

	public String getString(String key) {
		if (!newData.contains(key))
			newData.putString(key, cachedData.getStringOr(key, ""));
		return newData.getStringOr(key, "");
	}

	public void putString(String key, String newVal) {
		if (!cachedData.contains(key) || !cachedData.getStringOr(key, "").equals(newVal) || forced) {
			newData.putString(key, newVal);
		}
		cachedData.putString(key, newVal);
	}

	public boolean getBoolean(String key) {
		if (!newData.contains(key))
			newData.putBoolean(key, cachedData.getBooleanOr(key, false));
		return newData.getBooleanOr(key, false);
	}

	public void putBoolean(String key, boolean newVal) {
		if (!cachedData.contains(key) || cachedData.getBooleanOr(key, false) != newVal || forced) {
			newData.putBoolean(key, newVal);
		}
		cachedData.putBoolean(key, newVal);
	}

	public float getFloat(String key) {
		if (!newData.contains(key))
			newData.putFloat(key, cachedData.getFloatOr(key, 0));
		return newData.getFloatOr(key, 0);
	}

	public void putFloat(String key, float newVal) {
		if (!cachedData.contains(key) || cachedData.getFloatOr(key, 0) != newVal || forced) {
			newData.putFloat(key, newVal);
		}
		cachedData.putFloat(key, newVal);
	}

	public double getDouble(String key) {
		if (!newData.contains(key))
			newData.putDouble(key, cachedData.getDoubleOr(key, 0));
		return newData.getDoubleOr(key, 0);
	}

	public void putDouble(String key, double newVal) {
		if (!cachedData.contains(key) || cachedData.getDoubleOr(key, 0) != newVal || forced) {
			newData.putDouble(key, newVal);
		}
		cachedData.putDouble(key, newVal);
	}

	public CompoundTag get(String key) {
		return newData.getCompoundOrEmpty(key);
	}

	/** warning, not cached **/
	public void put(String key, CompoundTag tag) {
		newData.put(key, tag);
		cachedData.put(key, tag);
	}

	public boolean contains(String key) {
		return newData.contains(key);
	}

	public void updateCacheFromNew() {
		this.cachedData = this.newData;
	}

}