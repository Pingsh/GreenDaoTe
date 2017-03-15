package com.example.sphinx.greendaote.dao;

import com.example.sphinx.greendaote.entity.NoteType;
import org.greenrobot.greendao.converter.PropertyConverter;

/**
 * Created by Sphinx on 2017/3/14.
 */

public class NoteTypeConverter implements PropertyConverter<NoteType, String> {
    @Override
    public NoteType convertToEntityProperty(String databaseValue) {
        return NoteType.valueOf(databaseValue);
    }

    @Override
    public String convertToDatabaseValue(NoteType entityProperty) {
        return entityProperty.name();
    }
}
