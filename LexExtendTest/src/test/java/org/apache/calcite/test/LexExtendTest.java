package test.java.org.apache.calcite.test;


import org.apache.calcite.config.Lex;
import org.apache.calcite.sql.parser.SqlParser;
import org.apache.calcite.sql.parser.impl.GSqlParserImpl;
import org.junit.Test;
import org.apache.calcite.sql.SqlNode;
import org.apache.calcite.sql.parser.SqlParseException;


public class LexExtendTest{
	
	@Test
	public void lexExtend() throws SqlParseException {
		String sql = "show tables from testA,testB";
		SqlParser.Config parseConfig;
		SqlParser parser;
		SqlNode sqlparser;
		
		
		
		parseConfig = SqlParser.config().withParserFactory(GSqlParserImpl.FACTORY).withLex(Lex.MYSQL)
				.withCaseSensitive(false);
		
		parser = SqlParser.create(sql, parseConfig);
		sqlparser = parser.parseStmt();
		System.out.println("LexExtend success!");
		
		
	
	}
	
	
	
}