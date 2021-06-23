package com.farmmanager.farmmanager.services;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.farmmanager.farmmanager.models.Sale;
import com.farmmanager.farmmanager.models.Status;

@Service
public class SalesService {

	public static List<Status> notFinishedList() {
		List<Status> list = new ArrayList<>();
		list.add(Status.COMPLETED);
		list.add(Status.CANCELLED);
		
		return list;
	}
	
	public Map<String, Object> convertToMap(Sale sale) throws IllegalArgumentException, IllegalAccessException {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("id", sale.getId());
		map.put("created_at", sale.getCreated_at());
		map.put("updated_at", sale.getUpdated_at());
		map.put("createdBy", sale.getCreatedBy().getUsername());
		map.put("updatedBy", sale.getUpdatedBy().getUsername());
		map.put("deleted_at", sale.getDeleted_at());
		for (Field field : sale.getClass().getDeclaredFields()) {
			field.setAccessible(true);
			map.put(field.getName(), field.get(sale));
		}
		
		return map;
	}
	
	public List<Map<String, Object>> convertObjectListToMapList(List<Sale> sales) {
		List<Map<String, Object>> mapList = new ArrayList<Map<String,Object>>();
		
		sales.stream().forEach(sale -> {
			try {
				mapList.add(this.convertToMap(sale));
			} catch (IllegalArgumentException | IllegalAccessException e) {
				e.printStackTrace();
			}
		});
		
		return mapList;
	}
}
