package com.margin.calculate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

@SpringBootApplication
@RestController
public class CalculateApplication {

	@Autowired
	private JdbcTemplate jdbcTemplate;
	@RequestMapping("/test")
	public String getMarginById(){
		return "hello docker";
	}
	@RequestMapping("/getMarginById")
	public Map<String, Object> getMarginById(String fundId){
		String sql = "select * from margins.Positions as A \n" +
				"\t\t\tinner join margins.PMC B \n" +
				"            inner join margins.SMC C \n" +
				"            inner join margins.Fund D\n" +
				"            inner join margins.MarginConfiguration M\n" +
				"            on A.securityId = B.securityId && B.securityId = C.securityId && A.fundId = D.fundId && A.fundId=M.fundId&& C.rateType = M.rateType\n" +
				"           where A.fundId ="+fundId+";";
		List<Map<String, Object>> list =  jdbcTemplate.queryForList(sql);
		List<Float> markValuesList = new ArrayList<>();
		double margins=0.0;
		String fundName="";
		for (Map<String, Object> map : list) {
		    fundName = map.get("name").toString();
			float markValues = (int)map.get("netPosition") * (float)map.get("price");
            markValuesList.add(markValues);
            margins += markValues*(float)map.get("rate");
		}
		markValuesList.sort(Float::compareTo);
        margins += markValuesList.get(markValuesList.size()-1)*0.2 + markValuesList.get(markValuesList.size()-2)*0.1;
        Map<String, Object> result = new HashMap<>();
        result.put("fundId",fundId);
        result.put("fundName",fundName);
        result.put("margins",margins);
		return result;
	}

	public static void main(String[] args) {
		SpringApplication.run(CalculateApplication.class, args);
	}
}
