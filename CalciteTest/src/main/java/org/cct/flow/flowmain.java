package org.cct.flow;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.calcite.adapter.java.JavaTypeFactory;
import org.apache.calcite.avatica.util.Casing;
import org.apache.calcite.config.CalciteConnectionConfig;
import org.apache.calcite.config.CalciteConnectionConfigImpl;
import org.apache.calcite.config.CalciteConnectionProperty;
import org.apache.calcite.config.Lex;
import org.apache.calcite.jdbc.CalciteSchema;
import org.apache.calcite.jdbc.JavaTypeFactoryImpl;
import org.apache.calcite.plan.RelOptCluster;
import org.apache.calcite.plan.hep.HepPlanner;
import org.apache.calcite.plan.hep.HepProgramBuilder;
import org.apache.calcite.prepare.CalciteCatalogReader;
import org.apache.calcite.prepare.Prepare;
import org.apache.calcite.rel.RelNode;
import org.apache.calcite.rel.RelRoot;
import org.apache.calcite.rel.rules.CoreRules;
import org.apache.calcite.rel.type.RelDataType;
import org.apache.calcite.rel.type.RelDataTypeFactory;
import org.apache.calcite.rel.type.RelDataTypeField;
import org.apache.calcite.rel.type.RelDataTypeFieldImpl;
import org.apache.calcite.rel.type.RelDataTypeSystem;
import org.apache.calcite.rel.type.RelRecordType;
import org.apache.calcite.rel.type.StructKind;
import org.apache.calcite.rex.RexBuilder;
import org.apache.calcite.schema.Schema;
import org.apache.calcite.schema.SchemaPlus;
import org.apache.calcite.sql.SqlNode;
import org.apache.calcite.sql.fun.SqlStdOperatorTable;
import org.apache.calcite.sql.parser.SqlParseException;
import org.apache.calcite.sql.parser.SqlParser;
import org.apache.calcite.sql.parser.impl.SqlParserImpl;
import org.apache.calcite.sql.type.SqlTypeFactoryImpl;
import org.apache.calcite.sql.type.SqlTypeName;
import org.apache.calcite.sql.validate.SqlValidator;
import org.apache.calcite.sql.validate.SqlValidatorUtil;
import org.apache.calcite.sql2rel.SqlToRelConverter;
import org.apache.calcite.sql2rel.StandardConvertletTable;
import org.apache.calcite.tools.FrameworkConfig;
import org.apache.calcite.tools.Frameworks;

public class flowmain {
	FrameworkConfig fconfig;
	Prepare.CatalogReader catalogReader;
	SqlValidator validator;
	HepPlanner planner;
	

	public String sql2plan(String sql, String name, Map<String, SqlTypeName> info) throws SqlParseException {
		SqlNode sn;
		RelNode rn;

		init(name, info);
		sn = parseSql(sql);
		sn = validatorSql(sn);
		rn = genPlan(sn);
		return rn.explain();
	}

	private void init(String name, Map<String, SqlTypeName> info) {
		SqlParser.Config parseConfig;
		SchemaPlus rootschema = Frameworks.createRootSchema(true);
		table t = new table(info);
		rootschema.add(name, t);
		parseConfig = SqlParser.config().withParserFactory(SqlParserImpl.FACTORY).withLex(Lex.MYSQL)
				.withCaseSensitive(false);
		fconfig = Frameworks.newConfigBuilder().defaultSchema(rootschema).parserConfig(parseConfig).build();

	}

	/*解析SQL*/
	private SqlNode parseSql(String sql) throws SqlParseException {
		SqlParser parser;
		SqlNode sn;

		parser = SqlParser.create(sql, fconfig.getParserConfig());
		sn = parser.parseStmt();
		return sn;
	}

	/*语句校验*/
	private SqlNode validatorSql(SqlNode sn) {

		SchemaPlus schema;
		Properties props;
		CalciteConnectionConfig config;
		RelDataTypeFactory typeFactory;
		SqlValidator.Config validatorConfig;
		SqlNode vn;

		schema = fconfig.getDefaultSchema();
		props = new Properties();
		props.put(CalciteConnectionProperty.CASE_SENSITIVE.camelName(), Boolean.TRUE.toString());
		props.put(CalciteConnectionProperty.UNQUOTED_CASING.camelName(), Casing.UNCHANGED.toString());
		props.put(CalciteConnectionProperty.QUOTED_CASING.camelName(), Casing.UNCHANGED.toString());
		config = new CalciteConnectionConfigImpl(props);

		typeFactory = new JavaTypeFactoryImpl();
		catalogReader = new CalciteCatalogReader(CalciteSchema.from(schema), CalciteSchema.from(schema).path(null),
				(JavaTypeFactory) typeFactory, config);
		validatorConfig = SqlValidator.Config.DEFAULT.withLenientOperatorLookup(config.lenientOperatorLookup())
				.withDefaultNullCollation(config.defaultNullCollation()).withIdentifierExpansion(true);

		validator = SqlValidatorUtil.newValidator(SqlStdOperatorTable.instance(), catalogReader, typeFactory,
				validatorConfig);
		vn = validator.validate(sn);
		return vn;

	}

	/*生成逻辑计划*/
	private RelNode genPlan(SqlNode vn) {
		HepProgramBuilder builder;
		RexBuilder rexBuilder;
		SqlToRelConverter.Config converterConfig;
		RelDataTypeFactory typeFactory;
		RelNode rn;
		
		typeFactory = new SqlTypeFactoryImpl(RelDataTypeSystem.DEFAULT);
		rexBuilder = new RexBuilder(typeFactory);
		builder = new HepProgramBuilder();

		/**/
		builder.addRuleInstance(CoreRules.FILTER_PROJECT_TRANSPOSE);

		planner = new HepPlanner(builder.build());

		RelOptCluster cluster = RelOptCluster.create(planner, rexBuilder);
		converterConfig = SqlToRelConverter.config().withTrimUnusedFields(true).withExpand(false);

		SqlToRelConverter converter = new SqlToRelConverter(null, validator, catalogReader, cluster,
				StandardConvertletTable.INSTANCE, converterConfig);
		RelRoot relroot = converter.convertQuery(vn, false, true);

		// remove field not used
		relroot = relroot.withRel(converter.trimUnusedFields(true, relroot.rel));
		planner.setRoot(relroot.rel);
		rn = planner.findBestExp();

		return rn;
	}

}
