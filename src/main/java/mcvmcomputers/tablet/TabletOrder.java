package mcvmcomputers.tablet;

import java.util.List;
import java.util.UUID;

import mcvmcomputers.item.OrderableItem;

public class TabletOrder {
	public List<OrderableItem> items;
	public OrderStatus currentStatus = OrderStatus.PAYMENT_CHEST_ARRIVAL_SOON;
	public int price;
	public boolean entitySpawned;
	public final String orderUUID;
	
	public TabletOrder() {
		orderUUID = UUID.randomUUID().toString();
	}
	
	public enum OrderStatus{
		PAYMENT_CHEST_ARRIVAL_SOON,
		PAYMENT_CHEST_ARRIVED,
		PAYMENT_CHEST_RECEIVING,
		ORDER_CHEST_ARRIVAL_SOON,
		ORDER_CHEST_ARRIVED,
		ORDER_CHEST_RECEIVED
	}
}
