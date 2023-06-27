package org.apache.calcite.sql.dql;

import java.util.List;
import org.apache.calcite.sql.SqlIdentifier;
import org.apache.calcite.sql.SqlCall;
import org.apache.calcite.sql.SqlSpecialOperator;
import org.apache.calcite.sql.parser.SqlParserPos;
import org.apache.calcite.sql.SqlNode;
import org.apache.calcite.sql.SqlKind;
import org.apache.calcite.sql.SqlOperator;
import org.apache.calcite.sql.SqlWriter;

public class SqlShowTables extends SqlCall{
	public List<SqlNode> dbList;
	private static final SqlSpecialOperator OPERATOR = 
			new SqlSpecialOperator("SHOW TABLES", SqlKind.OTHER);
	
	public SqlShowTables(SqlParserPos pos,List<SqlNode> id) {
		super(pos);
		this.dbList = id;
	}

	@Override
	public SqlOperator getOperator() {
		// TODO Auto-generated method stub
		return OPERATOR;
	}

	@Override
	public List<SqlNode> getOperandList() {
		// TODO Auto-generated method stub
		if(dbList.isEmpty())
			throw new NullPointerException("partyid should not null");
		return this.dbList;
	}

	@Override 
	public void unparse(SqlWriter writer, int leftPrec, int rightPrec) {
		writer.keyword("SHOW TABLES FROM "+ this.dbList);
	}
}