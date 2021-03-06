package lastkilometer.BerkeleyDB;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import com.sleepycat.je.DatabaseException;

import lastkilometer.readCSV.ReadAssignedOrder_id;
import lastkilometer.readCSV.ReadCSV;

import java.util.Map.Entry;

public class FindNearstSite {
	//读取已经配送的订单id
	ReadAssignedOrder_id readAssignedOrder_id =new ReadAssignedOrder_id(); 
	
//	public static void main(String[] args) throws DatabaseException, IOException {
//		ReadCSV readCSV=new ReadCSV();
//		Map<String,Map<String,Map<String,Integer>>> eso=readCSV.readElectricitySuppliersOrder();
//		Map<String,Map<String, Map<String, List<Object>>>> OO=readCSV.readO2OOrder();
//		
//		Map<String, List<String>>  nearestSite=new FindNearstSite().find();
//		for(Entry<String, List<String>> entry:nearestSite.entrySet()){
//			String Site_id=entry.getKey();
//			List<String> Site_id_value=entry.getValue();
//			int num=0;
//			for(String Site_id_cache:Site_id_value){
//				//根据网点id计算订单数量
//				Map<String, Map<String, Integer>> eso_two=eso.get(Site_id_cache);
//				for(Entry<String, Map<String, Integer>> eso_two_entry:eso_two.entrySet()){
//					num+=eso_two_entry.getValue().size();
//				}
//				Map<String, Map<String, List<Object>>> OO_two=OO.get(Site_id_cache);
//				if(OO_two!=null){
//					for(Entry<String, Map<String, List<Object>>> OO_two_entry:OO_two.entrySet()){
//						num+=OO_two_entry.getValue().size();
//					}
//				}
//			}
//			
//			System.out.println(Site_id+"--->"+num);
//		}
//	}
	
    public Map<String, List<String>> find() throws DatabaseException, IOException{
		
		List<String> assignedOrder_idWithAll=readAssignedOrder_id.readAssignedOrder_idWithAll();
		
		ReadCSV readCSV=new ReadCSV();
		Map<String,Map<String,Map<String,Integer>>> ESO=readCSV.readElectricitySuppliersOrder();// Map<Site_id,Map<Spot_id,Map<Order_id,Num>>>
		Map<String,Map<String, Map<String, List<Object>>>> OO=readCSV.readO2OOrder();//Map<Shop_id,Map<Spot_id,Map<Order_id,List>>> List.get(0)=Pickup_time List.get(1)=Delivery_time List.get(2)=Num
		
		BerkeleyDB read_Site_Site = new BerkeleyDB("distance/site_site_distance");//格式：Map<Site_id,Map<Site_id,distance>>
		Map<String, List<String>>  nearestSite=new TreeMap<>();
		Set<Entry<String, Object>> set=read_Site_Site.iteration();
		for(Entry<String, Object> entry:set){
			String Site_id_key=entry.getKey();
			@SuppressWarnings("unchecked")
			Map<String, Double> value=(Map<String, Double>) entry.getValue();
			int num=0;
			List<String> Site_id_value=new ArrayList<>();
			for(Entry<String, Double> e:value.entrySet()){
				//根据网点id计算订单数量
				Map<String, Map<String, Integer>> eso_two=ESO.get(e.getKey());
				for(Entry<String, Map<String, Integer>> eso_two_entry:eso_two.entrySet()){
					Map<String, Integer> eso_three=eso_two_entry.getValue();
					for(Entry<String, Integer> eso_three_entry:eso_three.entrySet()){
						if(!assignedOrder_idWithAll.contains(eso_three_entry.getKey())){
							num++;
						}
					}
				}
				Map<String, Map<String, List<Object>>> OO_two=OO.get(e.getKey());
				if(OO_two!=null){
					for(Entry<String, Map<String, List<Object>>> OO_two_entry:OO_two.entrySet()){
						num+=OO_two_entry.getValue().size();
						Map<String, List<Object>> OO_three=OO_two_entry.getValue();
						for(Entry<String, List<Object>> OO_three_entry:OO_three.entrySet()){
							if(!assignedOrder_idWithAll.contains(OO_three_entry.getKey())){
								num++;
							}
						}
					}
				}
				if(num<4500){//4500个订单，按最近网点取
					Site_id_value.add(e.getKey());
				}else{
					break;
				}
			}
			nearestSite.put(Site_id_key, Site_id_value);
		}
		return nearestSite;
	}
	
//	public Map<String, List<String>> find() throws DatabaseException, FileNotFoundException{
//		BerkeleyDB read_Site_Site = new BerkeleyDB("distance/site_site_distance");//格式：Map<Site_id,Map<Site_id,distance>>
//		Map<String, List<String>>  nearestSite=new TreeMap<>();
//		Set<Entry<String, Object>> set=read_Site_Site.iteration();
//		for(Entry<String, Object> entry:set){
//			String Site_id_key=entry.getKey();
//			@SuppressWarnings("unchecked")
//			Map<String, Double> value=(Map<String, Double>) entry.getValue();
//			int n=0;
//			List<String> Site_id_value=new ArrayList<>();
//			for(Entry<String, Double> e:value.entrySet()){
//				if(n<80){//十个最近网点
//					Site_id_value.add(e.getKey());
//				}else{
//					break;
//				}
//				n++;
//			}
//			nearestSite.put(Site_id_key, Site_id_value);
//		}
//		return nearestSite;
//	}
}
