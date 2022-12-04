package mcvmcomputers.item;

import net.minecraft.item.Item;

public abstract class OrderableItem extends Item{
	private final int price;
	
	public OrderableItem(Settings settings, int price) {
		super(settings);
		this.price = price;
	}
	
	/**
	 * @return Price in Iron Ingots.
	 */
	public int getPrice() {
		return price;
	}

    public abstract boolean shouldSyncTagToClient();
}
