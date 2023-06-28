package org.cct.flow;

import java.util.HashMap;
import java.util.Map;

import org.apache.calcite.sql.type.SqlTypeName;
import org.apache.calcite.sql.parser.SqlParseException;
import org.junit.Test;

public class flowTest{
	
	@Test
	public void flowTest() throws SqlParseException{
		String sql = "select id, name from testA where id <100";
		String name = "testA";
		Map<String, SqlTypeName> info = new HashMap<>();
		info.put("id", SqlTypeName.BIGINT);
		info.put("name", SqlTypeName.VARCHAR);
		info.put("age", SqlTypeName.INTEGER);
		flowmain fm = new flowmain();
		String rs = fm.sql2plan(sql,name,info);
		//String rs = sql2plan(sql,name,info);
		System.out.println("logical plan: \n"+rs);
		
	}
	
}
