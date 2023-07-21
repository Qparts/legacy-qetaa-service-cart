package qetaa.service.cart.model.reports;

public class MonthOrders {
	private int month;
	private String monthName;
	private int carts;
	private int partOrders;
	
	public int getMonth() {
		return month;
	}
	
	public void initMonthName() {
		switch(month) {
		case 0:
			monthName = "Jan";
			break;
		case 1:
			monthName = "Feb";
			break;
		case 2:
			monthName = "Mar";
			break;
		case 3:
			monthName = "Apr";
			break;
		case 4:
			monthName = "May";
			break;
		case 5:
			monthName = "Jun";
			break;
		case 6:
			monthName = "Jul";
			break;
		case 7:
			monthName = "Aug";
			break;
		case 8:
			monthName = "Sep";
			break;
		case 9:
			monthName = "Oct";
			break;
		case 10:
			monthName = "Nov";
			break;
		case 11:
			monthName = "Dec";
			break;
		}
	}
	
	
	public void setMonth(int month) {
		this.month = month;
	}
	public String getMonthName() {
		return monthName;
	}
	public void setMonthName(String monthName) {
		this.monthName = monthName;
	}
	public int getCarts() {
		return carts;
	}
	public void setCarts(int count) {
		this.carts = count;
	}

	public int getPartOrders() {
		return partOrders;
	}

	public void setPartOrders(int partOrders) {
		this.partOrders = partOrders;
	}
	
	
	
	
}
