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

	/**
	 * @param shoppers
	 * @param orders
	 * @param leadTime
	 * @return
	 * @throws ParseException
	 */
	public static boolean busyHolidays(String[][] shoppers, String[][] orders, int[] leadTime) throws ParseException {
			
		class toolClass {
								
			class Order {
				Date from, to, latestFrom, earliestTo;
				boolean deliverable = false;
				List<Integer> canShopper = new ArrayList<>();
								
				public void addShopper(int i) {
					canShopper.add(i);
				}
			}
			
			class Shopper {
				Date from, to;
				int workMinutes = 0;
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
					cal.add(Calendar.MINUTE, 0-leadTime);
					o.latestFrom = cal.getTime();
					
					cal.setTime(o.from);
					cal.add(Calendar.MINUTE, leadTime);
					o.earliestTo = cal.getTime();
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
					s.workMinutes = (int)(s.to.getTime() - s.from.getTime())/60000;
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
		
		toolClass tool = new toolClass(shoppers, orders, leadTime);
		
		for(int i=0;i<orders.length;i++) {
			for(int j=0;j<shoppers.length;j++) {			
				if((tool.shopperList[j].to.compareTo(tool.orderList[i].earliestTo) >=0) && 
				(tool.shopperList[j].from.compareTo(tool.orderList[i].latestFrom) <= 0) &&
				tool.shopperList[j].workMinutes >= 30) {
				tool.orderList[i].addShopper(j);
				tool.orderList[i].deliverable = true;
			} 
//				if(tool.shopperList[j].used == false) {
//					if((tool.shopperList[j].to.compareTo(tool.orderList[i].earliestTo) >=0) && 
//						(tool.shopperList[j].from.compareTo(tool.orderList[i].latestFrom) <= 0) &&
//						tool.shopperList[j].workMinutes >= 30) {
//						tool.orderList[i].addShopper(j);
//						tool.shopperList[j].used = true;
//						tool.orderList[i].deliverable = true;
//						break;
//					} 
//				}
			}
		}
		
		for(int i=0;i<tool.orderList.length;i++) {
			if(tool.orderList[i].deliverable == false) {
				return false;
			}
		}
		return true;
	}
	
	public static void main(String[] args) {

		String[][] shoppers = {{"23:00","23:59"}, {"22:30","23:30"}};
		String[][] orders = {{"23:15","23:35"}, {"23:00","23:31"}};
//		String[][] orders = {{"14:30","15:00"}};
//		String[][] shoppers = {{"15:10", "16:00"}, {"17:50", "22:30"}, {"13:00","14:40"}};
		int[] leadTime = {20, 31};
		try {
			System.out.println(busyHolidays(shoppers, orders, leadTime));
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
		
}
