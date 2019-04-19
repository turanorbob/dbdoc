package org.jks.db.doc.model;

import lombok.Builder;
import lombok.Data;

/**
 * @Description
 * @Author legend <liaojian.2008.ok@163.com>
 * @Version v1.0.0
 * @Since 1.0
 * @Date 2019/4/17
 */
@Data
@Builder
public class FieldModel {
    // only transfer for tablename
    private String tablename;
    @Name("File Name")
    private String fieldName;
    @Name("Type")
    private String type;
    @Name("Is Nullable")
    private String isNullable;
    @Name("Desc")
    private String desc;
    @Name("Memo")
    private String memo;
}
