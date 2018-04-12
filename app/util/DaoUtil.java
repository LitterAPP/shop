package util;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;

import jws.Logger;
import jws.dal.annotation.Column;
import jws.dal.annotation.Id;
import jws.dal.exception.DalException;
import jws.dal.sqlbuilder.Condition;

public class DaoUtil {

	/**
	 * 根据对象信息生成对象所有字段的查询语句，如Guild.id,Guild.name,Guild.summary
	 * @param o
	 * @return
	 */
	public static String genAllFields(Object o) {
		return genAllFields(o.getClass());
	}
	
	/**
	 * 根据对象类型生成对象所有字段的查询语句，如Guild.id,Guild.name,Guild.summary
	 * @param o
	 * @return
	 */
	public static String genAllFields(Class c) {
		String className = c.getSimpleName();
		StringBuffer fields = new StringBuffer();
		
		Field[] fs = c.getDeclaredFields();
		
		int len = fs.length;
		for(int i=0; i<len; i++) {
			if(!fs[i].isAnnotationPresent(Column.class)) {
				continue;
			}
			if(i != 0) {
				fields.append(",");
			}
			fields.append(className).append(".").append(fs[i].getName());
		}
		return fields.toString();
	}

    /**
     * 根据对象生成所有字段的属性字符串，可以传入不希望在字符串中的属性
     * @param c             类
     * @param exceptField   需要去除的属性，在返回的字符串中不包括该属性，多个通过,号进行分割，这里只需要属性名称，如name只需要name，不需要类名.name
     * @return
     */
    public static String genAllFields(Class c, String exceptField) {
        String className = c.getSimpleName();
        StringBuffer fields = new StringBuffer();

        Field[] fs = c.getDeclaredFields();

        List<String> exceptFieldList = ListUtil.splitWithDelimeter(exceptField, ",", "");

        int len = fs.length;
		int count = 0;
        for(int i=0; i<len; i++) {
            if(!fs[i].isAnnotationPresent(Column.class)) {
                continue;
            }

            if (exceptFieldList.contains(fs[i].getName())) {
                continue;
            }

            if(count++ > 0) {
                fields.append(",");
            }
            fields.append(className).append(".").append(fs[i].getName());
        }
        return fields.toString();
    }
	
	/**
	 * 根据对象字段信息，生成查询条件(精确查询)
 	 * @param o
 	 * @return
 	 */
	public static Condition buildCondition(Object o) {
		return buildCondition(o, null);
	}
	
	/**
	 * 根据对象字段信息，生成查询条件
	 * @param o
	 * @param likeField 需要使用模糊查询的字段，如Guild.name,Guild.summary
	 * @return
	 */
	public static Condition buildCondition(Object o, String likeField) {
		String className = o.getClass().getSimpleName();
		Condition cond = null;
		
		Field[] fs = o.getClass().getDeclaredFields();
		int len = fs.length;
		for(int i=0; i<len; i++) {
			String fieldName = fs[i].getName();
			Object fieldValue = null;
			Method m;
			try {
				m = o.getClass().getMethod(getGetMethodName(fieldName));
				if(m != null) {
					fieldValue = m.invoke(o);
				} else {
					fieldValue = fs[i].get(o);
				}
			} catch (SecurityException e) {
				Logger.error(e, "");
			} catch (NoSuchMethodException e) {
				throw new DalException("Field " + fieldName + "must be public or have get method");
			} catch (IllegalArgumentException e) {
				Logger.error(e, "");
			} catch (IllegalAccessException e) {
				throw new DalException("Field " + fieldName + "must be public or have get method");
			} catch (InvocationTargetException e) {
				Logger.error(e, "");
			}
			
			if (fieldValue == null || StringUtils.isEmpty(fieldValue.toString())) {
				continue;
			}
			
			Condition tmp = null;
			
			String objStr = className + "." + fieldName;
			if(StringUtils.isNotEmpty(likeField) && likeField.indexOf(objStr) != -1) {
				tmp = new Condition(objStr, "like", "%" + StringEscapeUtils.escapeSql(fieldValue.toString()) + "%");
			} else {
				tmp = new Condition(objStr, "=", fieldValue);
			}
			
			if (cond == null) {
				cond = tmp;
			} else {
				cond.add(tmp, "and");
			}
		}
		return cond;
	}
	
	/**
	 * 根据字段名获取get方法名称
	 * @param fieldName
	 * @return
	 */
	private static String getGetMethodName(String fieldName) {
		String firstLetter = fieldName.substring(0, 1).toUpperCase();
        return "get" + firstLetter + fieldName.substring(1);
	}
	
	/**
	 * 取得由ID注解的字段名称
	 * @param className	类名
	 * @return
	 * 	字段名称
	 */
	public static String getIdField(Class clazz) {
		Field[] fields = clazz.getDeclaredFields();
		for (Field field:fields) {
			if (field.isAnnotationPresent(Id.class)) {
				return field.getName();
			}
		}
		return null;
	}
}
