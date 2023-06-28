package org.cct.flow;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.calcite.adapter.java.JavaTypeFactory;
import org.apache.calcite.rel.type.RelDataType;
import org.apache.calcite.rel.type.RelDataTypeFactory;
import org.apache.calcite.rel.type.RelDataTypeField;
import org.apache.calcite.rel.type.RelDataTypeFieldImpl;
import org.apache.calcite.rel.type.RelRecordType;
import org.apache.calcite.rel.type.StructKind;
import org.apache.calcite.schema.impl.AbstractTable;
import org.apache.calcite.sql.type.SqlTypeName;

public class table extends AbstractTable{
	private RelDataType rowType;
	private Map<String,SqlTypeName> info;
	
	table(Map<String,SqlTypeName> info){
		this.info = info;
	}
	
	@Override
	public RelDataType getRowType(RelDataTypeFactory typeFactory) {
		// TODO Auto-generated method stub
		return deduceRowType(typeFactory, info);
	}
	
	private RelDataType deduceRowType(RelDataTypeFactory typeFactory, Map<String,SqlTypeName> info) {
		int i =0;
		List<RelDataTypeField> fields = new ArrayList<>(info.size());
		for(Map.Entry<String,SqlTypeName> entry: info.entrySet()) {
			RelDataType type = typeFactory.createSqlType(entry.getValue());
			RelDataTypeField field = new RelDataTypeFieldImpl(entry.getKey(),i,type);
			fields.add(field);
			i++;
		}
		RelDataType rdt = new RelRecordType(StructKind.FULLY_QUALIFIED,fields,false);
		return rdt;
	}
}