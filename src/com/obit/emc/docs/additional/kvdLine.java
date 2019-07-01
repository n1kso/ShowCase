package com.obit.emc.docs.additional;

import com.bssys.server.Context;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;

public class kvdLine {

    private String id;
    private String code;
    private LocalDate beginDate;
    private LocalDate endDate;

    private kvdLine(String id, String code, Date beginDate, Date endDate) {
        this.id = id;
        this.code = code;
        this.beginDate = beginDate.toLocalDate();
        this.endDate = endDate != null ? endDate.toLocalDate() : null;
    }

    public String getId() {
        return id == null ? "" : id;
    }

    public LocalDate getBeginDate() {
        return beginDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public String getCode() {
        return code;
    }

    public static ArrayList<kvdLine> getKvdLinesWithField(String field, Context con, String piBudgetCode) throws SQLException {

        ArrayList<kvdLine> lines = new ArrayList<>();

        String sql = "SELECT kl.id, k.code, kl.begin_date, kl.end_date FROM KD k \n" +
                "INNER JOIN KD_lines kl ON k.id = kl.master_id\n" +
                "WHERE " + field + " = " + piBudgetCode.substring(3);
        PreparedStatement ps = con.prepareStatement(sql);
        ResultSet resultSet = ps.executeQuery();

        while (resultSet.next()) {
            lines.add(new kvdLine(resultSet.getString("ID"),
                    resultSet.getString("CODE"),
                    resultSet.getDate("BEGIN_DATE"),
                    resultSet.getDate("END_DATE")));
        }

        return lines;
    }
}
