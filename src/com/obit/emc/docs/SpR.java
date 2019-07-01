//package com.obit.emc.docs;
//
//import com.bssys.server.Context;
//import com.bssys.server.UserException;
//import com.obit.emc.docs.additional.SpRLine;
//import com.obit.emc.general.emcCustomDocument;
//import org.w3c.dom.Element;
//
//import java.sql.SQLException;
//import java.util.ArrayList;
//
///**
// * Created with IntelliJ IDEA.
// * User: CherIA
// * Date: 26.04.13
// * Time: 12:40
// * To change this template use File | Settings | File Templates.
// */
//public class SpR extends emcCustomDocument {
//
//    private final String tableName = "PAYDETAIL";
//
//    public SpR(Element task, Context con) throws UserException, SQLException {
//        super(task, con);
//        setMainSQL(" select * from PAYDETAIL where id=" + id);
//        build();
//    }
//
//    @Override
//    protected void getData() throws UserException, SQLException {
//
//
////        _lines.add(new SpRLine(fTask, fcon,
////                mainRS.getDouble("AMOUNT")));
//        while (mainRS.next()) {
//            _ClassId = mainRS.getString("DOCUMENTCLASS_ID");
////            _lines.add(new SpRLine(fTask, fcon,
////                    mainRS.getDouble("AMOUNT")
////            ));
//        }
//
//    }
//
//    private String _ClassId = "0";
//    private ArrayList<SpRLine> _lines = new ArrayList<>();
//
//    public ArrayList<SpRLine> GetLines() {
//        return _lines;
//    }
//
//    public String getTableName() {
//        return tableName;
//    }
//}
