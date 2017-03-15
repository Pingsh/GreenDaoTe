package com.example.sphinx.greendaote;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sphinx.greendaote.adapter.NoteAdapter;
import com.example.sphinx.greendaote.entity.DaoMaster;
import com.example.sphinx.greendaote.entity.DaoSession;
import com.example.sphinx.greendaote.entity.Note;
import com.example.sphinx.greendaote.entity.NoteDao;
import com.example.sphinx.greendaote.entity.NoteType;

import net.sqlcipher.database.SQLiteException;

import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.query.Query;

import java.text.DateFormat;
import java.util.Date;
import java.util.List;

import static com.example.sphinx.greendaote.CommonApp.ENCRYPTED;

/**
 * DaoMaster：主管者.保存了sqlitedatebase对象以及操作DAO classes（注意：不是对象）.其提供了一些创建和删除table的静态方法，其内部类OpenHelper和DevOpenHelper实现了SQLiteOpenHelper并创建数据库的框架.
 * DaoSession：会话层.操作具体的DAO对象（注意：是对象),比如各种getter方法。
 * XXXDao：实际生成的某某DAO类，通常对应具体的java类，比如NoteDao等.其有更多的权限和方法来操作数据库元素。
 * XXXEntity：持久的实体对象.通常代表了一个数据库row的标准java properties.
 */
public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private EditText editText;
    private View addNoteButton;
    public boolean ENCRYPTED = true;

    private NoteDao noteDao;
    private NoteDao noteDao2;

    private Query<Note> notesQuery;
    private Query<Note> notesQuery2;

    private NoteAdapter notesAdapter;

    private final static String TAG = "MainActivity";

    private Button btnEnc;
    private Button btnDec;

    NoteAdapter.NoteClickListener noteClickListener = new NoteAdapter.NoteClickListener() {
        @Override
        public void onNoteClick(int position) {
            Note note = notesAdapter.getNote(position);
            Long noteId = note.getId();

            noteDao.deleteByKey(noteId);
            Log.d(TAG, "Deleted note, ID: " + noteId);

            updateNotes();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();

        // 获取数据库
        DaoSession daoSession = ((CommonApp) getApplication()).getDaoSession();
        noteDao = daoSession.getNoteDao();

        // 按照Text内容的自然排序,获取数据库中的内容
        notesQuery = noteDao.queryBuilder().orderAsc(NoteDao.Properties.Text).build();

        updateNotes();
    }

    private void initReadDao() {
        // 按照ID内容的自然排序,获取数据库中的内容
        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(this, "notes-db-encrypted");
        try {
            Database ds = ENCRYPTED ? helper.getEncryptedReadableDb("super--secret") : helper.getReadableDb();
            DaoSession deDaoSession = new DaoMaster(ds).newSession();
            noteDao2 = deDaoSession.getNoteDao();
            notesQuery2 = noteDao2.queryBuilder().orderAsc(NoteDao.Properties.Id).build();
        } catch (SQLiteException e) {
            //密码不对时无法读取数据库,提示信息
            Toast.makeText(MainActivity.this, "数据库被非法调用", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    /**
     * 刷新数据
     */
    private void updateNotes() {
        List<Note> notes = notesQuery.list();
        notesAdapter.setNotes(notes);
    }

    /**
     * 刷新数据
     */
    private void updateDecNotes() {
        initReadDao();
        if (notesQuery2 == null) {
            return;
        }
        List<Note> notes = notesQuery2.list();
        notesAdapter.setNotes(notes);
        Toast.makeText(MainActivity.this, "Id显示列表", Toast.LENGTH_SHORT).show();
    }

    private void initView() {
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerViewNotes);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        notesAdapter = new NoteAdapter(noteClickListener);
        recyclerView.setAdapter(notesAdapter);

        addNoteButton = findViewById(R.id.buttonAdd);
        //不允许被点击
        addNoteButton.setEnabled(false);

        editText = (EditText) findViewById(R.id.editTextNote);

        btnEnc = (Button) findViewById(R.id.btn_enc);
        btnDec = (Button) findViewById(R.id.btn_dec);

        initListener();

    }

    private void initListener() {
        //不是在我们点击EditText的时候触发，也不是在我们对EditText进行编辑时触发，而是在我们编辑完之后点击软键盘上的回车键才会触发。
        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    addNote();
                    return true;
                }
                return false;
            }
        });

        editText.addTextChangedListener(new TextWatcher() {

            //只在监测到数据变化时允许点击
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                boolean enable = s.length() != 0;
                addNoteButton.setEnabled(enable);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

        });

        addNoteButton.setOnClickListener(this);
        btnEnc.setOnClickListener(this);
        btnDec.setOnClickListener(this);
    }

    /**
     * 增加单条数据
     */
    private void addNote() {
        String noteText = editText.getText().toString();
        //复位,清空输入框
        editText.setText("");

        final DateFormat df = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.MEDIUM);
        String comment = TAG + "Added on " + df.format(new Date());

        Note note = new Note();
        note.setText(noteText);
        note.setComment(comment);
        note.setDate(new Date());
        note.setType(NoteType.TEXT);

        noteDao.insert(note);
        Log.d(TAG, "Inserted new note, ID: " + note.getId());

        updateNotes();
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.buttonAdd:
                addNote();
                break;
            case R.id.btn_dec:
                updateDecNotes();
                break;
            case R.id.btn_enc:
                updateNotes();
                Toast.makeText(MainActivity.this, "Text显示列表", Toast.LENGTH_SHORT).show();
                break;
            default:
                break;
        }
    }

    /**
     * 删除所有数据
     */
    public void deleteAllNote() {
        noteDao.deleteAll();
    }

    /**
     * 根据id,删除数据
     *
     * @param id 用户id
     */
    public void deleteNote(long id) {
        noteDao.deleteByKey(id);
        Log.i(TAG, "delete");
    }

    /**
     * 根据用户类,删除信息
     *
     * @param note 用户信息类
     */
    public void deleteNote(Note note) {
        noteDao.delete(note);
    }

    /**
     * 批量插入或修改信息
     *
     * @param list 信息列表
     */
    public void saveNoteLists(final List<Note> list) {
        if (list == null || list.isEmpty()) {
            return;
        }
        //与此相对应的还有callInTx(),返回结果码
        noteDao.getSession().runInTx(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < list.size(); i++) {
                    Note user = list.get(i);
                    noteDao.insertOrReplace(user);
                }
            }
        });

        updateNotes();
    }

}
