package jp.gr.java_conf.dhun.split.persistence.dao;

public class WhereClause {
    private String selection;
    private String[] selectionArgs;

    /**
     * selectionを取得します。
     * 
     * @return selection
     */
    public String getSelection() {
        return selection;
    }

    /**
     * selectionを設定します。
     * 
     * @param selection selection
     */
    public void setSelection(String selection) {
        this.selection = selection;
    }

    /**
     * selectionArgsを取得します。
     * 
     * @return selectionArgs
     */
    public String[] getSelectionArgs() {
        return selectionArgs;
    }

    /**
     * selectionArgsを設定します。
     * 
     * @param selectionArgs selectionArgs
     */
    public void setSelectionArgs(String[] selectionArgs) {
        this.selectionArgs = selectionArgs;
    }

    @Override
    public String toString() {
        String format = selection.replaceAll("\\?", "%s");
        return String.format(format, (Object[]) selectionArgs);
    }
}
