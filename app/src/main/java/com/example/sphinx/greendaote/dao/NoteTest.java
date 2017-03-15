package com.example.sphinx.greendaote.dao;

/**
 * 测试添加实体类
 * Created by Sphinx on 2017/3/14.
 */

import com.example.sphinx.greendaote.entity.Note;
import com.example.sphinx.greendaote.entity.NoteDao;

import org.greenrobot.greendao.test.AbstractDaoTestLongPk;

public class NoteTest extends AbstractDaoTestLongPk<NoteDao, Note> {

    public NoteTest() {
        super(NoteDao.class);
    }

    @Override
    protected Note createEntity(Long key) {
        Note entity = new Note();
        entity.setId(key);
        entity.setText("green note"); // Has to be set as it is "not null"
        return entity;
    }

}
