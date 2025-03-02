package com.project.json;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 时间自定义序列化器
 */
public class CustomDateSerializer extends JsonSerializer<Date> {

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm");

    @Override
    public void serialize(Date date, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        // 将 Date 格式化为 yyyy-MM-dd 字符串
        String formattedDate = DATE_FORMAT.format(date);
        // 写入 JSON
        jsonGenerator.writeString(formattedDate);
    }
}
