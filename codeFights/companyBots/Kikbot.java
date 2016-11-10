package codeFights.companyBots;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class Kikbot {
	

	public static boolean[] delivery(int[] order, int[][] shoppers) {
		boolean[] shopperDel = new boolean[shoppers.length];		
		double deliverTime = 0;
		int distance = order[0];
		
		for(int i=0;i<shoppers.length;i++) {
			deliverTime = (double)(distance + shoppers[i][0])/shoppers[i][1] + shoppers[i][2];
			if(deliverTime > (order[1] + order[2])) {
				shopperDel[i] = false;
			} else if(deliverTime < order[1]) {
				shopperDel[i] = false;
			} else {
				shopperDel[i] = true;
			}
		}
		
		return shopperDel;
	}
	
	public static boolean isAdmissibleOverpayment(double[] prices, String[] notes, double x) {
		
		double sum = 0;
		double price = 0;
		double percent = 0;
		String[] note = new String[2];
		java.text.NumberFormat nf = java.text.NumberFormat.getPercentInstance();
		
		for(int i=0;i<prices.length;i++) {
			price = prices[i];
			note = notes[i].split(" ");
			
			if(!note[0].toLowerCase().equals("same")) {
				try {
					percent = nf.parse(note[0]).doubleValue();
				} catch (ParseException e) {
					e.printStackTrace();
				}
			}
			
			if(note[1].toLowerCase().equals("higher")) {
				sum += price/(percent + 1)*percent;
			} else if(note[1].toLowerCase().equals("lower")) {
				sum -= price/(1 - percent)*percent;
			}
		}
		
		if(sum > x) {
			return false;
		} else {
			return true;
		}
	}

	public static boolean busyHolidays(String[][] shoppers, String[][] orders, int[] leadTime) throws ParseException {
			
		class toolClass {
								
			class Order {
				Date from, to, latestFrom;
				boolean deliverable = false;
				List<Integer> canShopper = new ArrayList<>();
								
				public void addShopper(int i) {
					canShopper.add(i);
				}
			}
			
			class Shopper {
				Date from, to;
				boolean used = false;;
			}
			
			Order[] orderList;
			Shopper[] shopperList;
			
			public toolClass(String[][] s, String[][] o, int[] l) throws ParseException {
				orderList = createOrders(o, l);
				shopperList = createShoppers(s);
			}
			
			public Order createOrder(String[] order, int leadTime) throws ParseException {
					
					Order o = new Order();
					
					SimpleDateFormat df = new SimpleDateFormat("HH:mm");
					Calendar cal = Calendar.getInstance();
					o.from = df.parse(order[0]);
					o.to = df.parse(order[1]);
					cal.setTime(o.to);
					cal.add(Calendar.MINUTE, leadTime);
					o.latestFrom = cal.getTime();
					
					return o;
				}
				
			public Order[] createOrders(String[][] orders, int[] leadTimes) throws ParseException {
					int orderNo = orders.length;
					Order[] orderList = new Order[orderNo];
					
					for(int i=0;i<orderNo;i++) {
						orderList[i] = this.createOrder(orders[i], leadTimes[i]);
					}
					
					return orderList;
				}
				
			public Shopper createShopper(String[] shoppers) throws ParseException {
					Shopper s = new Shopper();
					SimpleDateFormat df = new SimpleDateFormat("HH:mm");
					Calendar cal = Calendar.getInstance();
					s.from = df.parse(shoppers[0]);
					s.to = df.parse(shoppers[1]);
					
					return s;
				}
				
			public Shopper[] createShoppers(String[][] shoppers) throws ParseException {
					int shopperNo = shoppers.length;
					Shopper[] shopperList = new Shopper[shopperNo];
					
					for(int i=0;i<shopperNo;i++) {
						shopperList[i] = this.createShopper(shoppers[i]);
					}
					
					return shopperList;
				}
		}
		
		int orderNo = orders.length;
		int shopperNo = shoppers.length;
		boolean doable = true;
		toolClass tool = new toolClass(shoppers, orders, leadTime);
		
		for(int i=0;i<orderNo;i++) {
			for(int j=0;j<shopperNo;j++) {
				if(tool.shopperList[j].used == false) {
					if((tool.orderList[i].to.compareTo(tool.shopperList[j].to) <=0) && 
							(tool.orderList[i].latestFrom.compareTo(tool.shopperList[j].from) >= 0)) {
						tool.orderList[i].addShopper(j);
						tool.shopperList[j].used = true;
						tool.orderList[i].deliverable = true;
							break;
					} 
				}
			}
		}
		
		for(int i=0;i<tool.orderList.length;i++) {
			doable = tool.orderList[i].deliverable && doable;
		}
		
		return doable;
	}
	
	public static void main(String[] args) {

		String[][] orders = {{"17:30", "18:00"}, {"15:00", "15:45"}};
		String[][] shoppers = {{"15:10", "16:00"}, {"17:40", "22:30"}};
		int[] leadTime = {15, 30};
		try {
			System.out.println(busyHolidays(shoppers, orders, leadTime));
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
		
}
