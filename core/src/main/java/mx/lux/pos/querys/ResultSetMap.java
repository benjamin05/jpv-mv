package mx.lux.pos.querys;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;

import org.apache.commons.beanutils.BeanUtils;

/**
 * Created by magno on 17/09/14.
 */
public class ResultSetMap<T> {

    private HashMap tipoDato;

    public List<T> mapRersultSetToObject(ResultSet rs, Class outputClass) {
        List<T> outputList = null;
        try {
            if ( rs != null ) if (outputClass.isAnnotationPresent(Entity.class)) {

                ResultSetMetaData rsmd = rs.getMetaData();
                Field[] fields = outputClass.getDeclaredFields();

                while (rs.next()) {


                    T bean = (T) outputClass.newInstance();

                    for (int _iterator = 0; _iterator < rsmd.getColumnCount(); _iterator++) {

                        String columnName = rsmd.getColumnName(_iterator + 1);
                        Object columnValue;

                        String tipo = getTipoDato(rsmd.getColumnType(_iterator + 1));

                        if ( tipo.equals("money") ) {
                            Double money = 0.00;
                            String str = rs.getString(columnName);
                            str = str != null ? str.replace("$", "") : "0.00";
                            str = str != null ? str.replace(",", "") : "0.00";
                            columnValue = (Object) str;
                        } else if ( tipo.equals("character")) {
                            String str = rs.getString(columnName);
                            if ( str != null )
                                str = str.trim();
                            columnValue = (Object) str;
                        } else {
                            columnValue = rs.getObject(columnName);
                        }

                        for (Field field : fields) {
                            if (field.isAnnotationPresent(Column.class)) {
                                Column column = field.getAnnotation(Column.class);
                                column.columnDefinition();

                                if (column.name().equalsIgnoreCase(columnName) && columnValue != null) {
                                    BeanUtils.setProperty(bean, field.getName(), columnValue);
                                    break;
                                }
                            }
                        }
                    }

                    if (outputList == null) {
                        outputList = new ArrayList<T>();
                    }
                    outputList.add(bean);
                }

            } else {
                //
            }
            else {
                return null;
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return outputList;
    }


    public String getTipoDato (Integer tipo) {

        tipoDato =  new HashMap();;

        // Put elements to the map
        tipoDato.put(8, "money");
        tipoDato.put(12, "text");
        tipoDato.put(4, "integer");
        tipoDato.put(1, "character");
        tipoDato.put(-7, "boolean");
        tipoDato.put(93, "timestamp");
        tipoDato.put(91, "date");

        String value = null;

        value = ((String)tipoDato.get(tipo)).toString();

        return value;
    }
}
