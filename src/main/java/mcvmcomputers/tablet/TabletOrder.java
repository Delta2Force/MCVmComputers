package mcvmcomputers.tablet;

import java.util.List;

import net.minecraft.item.Item;

public class TabletOrder {
	public List<Item> items;
	public OrderStatus currentStatus = OrderStatus.PAYMENT_CHEST_ARRIVAL_SOON;
	public int price;
	
	public enum OrderStatus{
		PAYMENT_CHEST_ARRIVAL_SOON,
		PAYMENT_CHEST_ARRIVED,
		PAYMENT_CHEST_RECEIVED,
		ORDER_CHEST_ARRIVAL_SOON,
		ORDER_CHEAT_ARRIVED,
		ORDER_CHEST_RECEIVED
	}
}
