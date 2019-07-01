package com.obit.emc.docs;

import com.bssys.server.Context;
import com.bssys.server.UserException;
import com.obit.emc.general.emcCustomDocument;
import org.w3c.dom.Element;

import java.sql.SQLException;

/**
 * Created by IntelliJ IDEA.
 * User: KanMA
 * Date: 30.01.2012
 * Time: 9:40:14
 * To change this template use File | Settings | File Templates.
 */
public class UobnOnlyLines extends emcCustomDocument {

    public UobnOnlyLines(Element task, Context con) throws SQLException {
        super(task, con);
        setMainSQL("select ex.id from expassignment ex \n" +
                "join estimate e on (ex.estimate_id=e.ID) \n" +
                "where (ex.TITLE_ID = " + id + " or ex.id = " + id + ") \n" +
                "and e.estkind_id = 2\n");
        buildNotDocument();
    }

    @Override
    protected void getData() throws UserException, SQLException {
        _hasRospisLine = mainRS != null;
    }

    private boolean _hasRospisLine = false;

    public boolean is_hasRospisLine() {
        return _hasRospisLine;
    }

}
