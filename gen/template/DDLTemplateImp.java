package template;

import java.io.File;

import org.apache.commons.lang3.StringUtils;

import context.Context;
import context.ContextKey;
import database.ColumnInfo;
import database.TableInfo;
import xml.Xml;

public class DDLTemplateImp extends TemplateAbstract{
	
	public DDLTemplateImp(TableInfo _tableInfo,String _path,String _workspace){
		super(_tableInfo,_path,_workspace,null); 
		Context.set(ContextKey.DDL_CLASSES_COMMENT,_tableInfo.getTableDesc());
	} 
	@Override
	protected String nameJavaFile() { 
		return naming()+"DDL.java";
	}

	@Override
	protected String path() {
		return path+File.separator+"ddl";
	}

	@Override
	protected String nameJavaClass() {
		String name = naming()+"DDL";
		Context.set(ContextKey.DDL_CLASS_NAME, name);
		return name;
	} 
	
	private String packingName(){
		String pName = packaging()+".ddl";
		Context.set(ContextKey.DDL_PACKAGE_NAME,pName);
		return pName;
	}
	
	@Override
	protected String coding() throws Exception { 
		StringBuffer sb = new StringBuffer();
		sb.append("package ").append(packingName()).append(";\n\n");
		//import
		sb.append("import jws.dal.annotation.Column;").append("\n");
		sb.append("import jws.dal.annotation.GeneratedValue;").append("\n");
		sb.append("import jws.dal.annotation.GenerationType;").append("\n");
		sb.append("import jws.dal.annotation.Id;").append("\n");
		sb.append("import jws.dal.annotation.Table;").append("\n");
		sb.append("import jws.dal.common.DbType;").append("\n");
		 
		
		//comment
		sb.append("/**").append("\n");
		sb.append(" * ").append(tableInfo.getTableDesc()).append("\n");
		sb.append(" * ").append("@author auto").append("\n");
		sb.append(" * ").append("@createDate ").append(codingDate()).append("\n");
		sb.append(" **/").append("\n");
		
		//class start
		sb.append("@Table(name=").append("\"").append(tableInfo.getTableName()).append("\"").append(")").append("\n");
		sb.append("public class ").append(nameJavaClass()).append("{").append("\n");
		
		//getter setter
		for(ColumnInfo column:tableInfo.getColumnInfos()){
			if(column.isPk()){
				sb.append("\t@Id").append("\n");
			}
			if(column.isAutoIncr()){
				sb.append("\t@GeneratedValue(generationType= GenerationType.Auto)").append("\n");
			}
			String varName = varNaming(column.getColumnName());
			String dbType=column.getJwsDbType();
			String javaType=column.getJavaType(); 
			
			String defValue = column.getDefValue();
			
			String defValueCode = "";
			String varDefVCode="";
			
			
			
			
 			if(defValue == null){
 				//var
 				sb.append("\t@Column(name=\"").append(column.getColumnName()).append("\", type=").append(dbType).append(")").append("\n");
 				sb.append("\tprivate ").append(javaType).append(" ").append(varName).append(";\n");
				//getter
				sb.append("\tpublic ").append(javaType).append(" ").append(getterMethodNaming(column.getColumnName())).append("() {\n");
				sb.append("\t\treturn ").append(varName).append(";\n");
				sb.append("\t}\n");
				
				//setter
				sb.append("\tpublic void ").append(setterMethodNaming(column.getColumnName())).append("(").append(javaType).append(" ").append(varName).append("){\n");
				sb.append("\t\tthis.").append(varName).append("=").append(varName).append(";\n");;
				sb.append("\t}\n\n");
			}else{
				switch(column.getType()){
				case java.sql.Types.BIT:
				case java.sql.Types.TINYINT:
				case java.sql.Types.SMALLINT:
				case java.sql.Types.INTEGER:
					defValueCode = "Integer.parseInt(\""+defValue+"\")";
					varDefVCode = ""+Integer.parseInt(defValue);
					break;
				case java.sql.Types.BIGINT:
					defValueCode = "Long.parseLong(\""+defValue+"\")";
					varDefVCode = ""+Long.parseLong(defValue);
					break;
				case java.sql.Types.FLOAT:
					defValueCode = "Float.parseFloat(\""+defValue+"\")";
					varDefVCode = ""+Float.parseFloat(defValue)+"f";
					break;
					
				case java.sql.Types.NUMERIC:
				case java.sql.Types.DECIMAL:
				case java.sql.Types.DOUBLE:
					defValueCode = "Double.parseDouble(\""+defValue+"\")";
					varDefVCode = ""+Double.parseDouble(defValue)+"d";
					break;
					
				case java.sql.Types.CHAR:
					defValueCode = "'"+defValue+"'";
					varDefVCode = "'"+defValue+"'";
					break;
					
				case java.sql.Types.VARCHAR:
				case java.sql.Types.LONGVARCHAR:
					defValueCode = "String.valueOf(\""+defValue+"\")";
					varDefVCode = "\""+defValue+"\"";
					break;
					
				case java.sql.Types.DATE:
				case java.sql.Types.TIME:
				case java.sql.Types.TIMESTAMP:
					defValueCode = "Long.parseLong(\""+defValue+"\")";
					varDefVCode = ""+Long.parseLong(defValue);
					break; 
				/*case java.sql.Types.BLOB:
					column.setJwsDbType("DbType.Blob");
					column.setJavaType("java.sql.Blob");
					break;*/
					
				/*	default:
						dbType = "DbType.Blob";
						javaType = "Object";*/
				} 	
				
				//var
				sb.append("\t@Column(name=\"").append(column.getColumnName()).append("\", type=").append(dbType).append(")").append("\n");
				sb.append("\tprivate ").append(javaType).append(" ").append(varName).append("="+varDefVCode+";\n");
				//getter
				sb.append("\tpublic ").append(javaType).append(" ").append(getterMethodNaming(column.getColumnName())).append("() {\n");
				sb.append("\t\treturn ").append(varName).append("==null?"+defValueCode+":"+varName+";\n");
				sb.append("\t}\n");
				
				//setter
				sb.append("\tpublic void ").append(setterMethodNaming(column.getColumnName())).append("(").append(javaType).append(" ").append(varName).append("){\n");
				sb.append("\t\tthis.").append(varName).append("=").append(varName).append("==null?"+defValueCode+":"+varName+";\n");
				sb.append("\t}\n\n");
			}
		}
		//Example
		sb.append("\tpublic static ").append(nameJavaClass()).append(" newExample(){").append("\n");
		sb.append("\t\t").append(nameJavaClass()).append(" object=new ").append(nameJavaClass()).append("();\n");
		for(ColumnInfo column:tableInfo.getColumnInfos()){
			sb.append("\t\tobject").append(".").append(setterMethodNaming(column.getColumnName())).append("(null);\n");
		}
		sb.append("\t\treturn object;\n");
		sb.append("\t}").append("\n");
		
		//class end
		sb.append("}\n");
		Xml.configDDLConfig();
		return sb.toString();
	}
}
