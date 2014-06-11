package edu.upc.fib.ossim.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StringToMap {
	String pageOrders;
	
	public StringToMap(String pageOrders){
		this.pageOrders = pageOrders;
	}
	
	public Map<Integer,List<Integer>>transformToMap() {
		Map<Integer,List<Integer>> res = new HashMap<Integer,List<Integer>>();
		String[] ss = pageOrders.split(";");
		for(int i=0;i<ss.length;i++){
			List<Integer>orders = new ArrayList<Integer>();
			String[]str = ss[i].split(",");
			for(int j=0;j<str.length;j++) orders.add(Integer.parseInt(str[j]));
			res.put(i, orders);
		}
		return res;
		
	}
	


}
