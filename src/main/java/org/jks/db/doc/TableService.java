package org.jks.db.doc;

import org.jks.db.doc.model.FieldModel;

import java.sql.SQLException;
import java.util.List;

public interface TableService {
    List<String> allTables() throws SQLException;

    List<FieldModel> allField(String tablename) throws SQLException;
}
