package com.obit.emc.func;

import java.text.ParseException;

/**
 * Created with IntelliJ IDEA.
 * User: OvsAN
 * Date: 29.01.15
 * Time: 13:37
 * To change this template use File | Settings | File Templates.
 */
public class func {

    /**
     * @param inputFormat  -формат строки _inputStringData как у SimpleDateFormat
     * @param outFormat    -формат выходящей строки как у SimpleDateFormat
     * @param _inputStringData
     * @return
     * Переводит строку _inputStringData из формата inputFormat -> outFormat , если перевод не возможен то возвращает строку _inputStringData не изменённой назад
     */
    public static String DateConvert (String inputFormat, String outFormat, String _inputStringData)
    {
        try {
            return new java.text.SimpleDateFormat(outFormat).format(new java.text.SimpleDateFormat(inputFormat).parse(_inputStringData));
        }
        catch (ParseException e)
        {
            return _inputStringData;
        }

    }

    public static String DateConvert (String _inputStringData) {
        try {
            return new java.text.SimpleDateFormat("dd.MM.yyyy").format(new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S").parse(_inputStringData));
        }
        catch (ParseException e)
        {
            return _inputStringData;
        }

    }

    public static double toDouble(String value){
        return Double.parseDouble(value);
    }
}
