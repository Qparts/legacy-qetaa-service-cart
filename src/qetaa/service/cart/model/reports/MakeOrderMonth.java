package qetaa.service.cart.model.reports;

import java.util.List;
import java.util.Map;

public class MakeOrderMonth {

	private int makeId;
	private Map<String,Object> make;
	private List<MonthOrders> monthOrders;
	
	
	public int getMakeId() {
		return makeId;
	}
	public void setMakeId(int makeId) {
		this.makeId = makeId;
	}
	public List<MonthOrders> getMonthOrders() {
		return monthOrders;
	}
	public void setMonthOrders(List<MonthOrders> monthOrders) {
		this.monthOrders = monthOrders;
	}
	public Map<String, Object> getMake() {
		return make;
	}
	public void setMake(Map<String, Object> make) {
		this.make = make;
	}
	
	

}
