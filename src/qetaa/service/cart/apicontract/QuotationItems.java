package qetaa.service.cart.apicontract;

import java.io.Serializable;
import java.util.Map;

public class QuotationItems implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
	private long id;
	private int quantity;
	private double price;
	private Map<String,Object> product;
}
