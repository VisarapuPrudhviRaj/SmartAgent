package nk.mobleprojects.smartagent.utils;

public class DBTables {

    public static class SmartProject {
        public static final String TABLE_NAME = "SmartProject";
        public static final String id = "id";
        public static final String name = "name";
        public static final String type = "type";
        public static final String sizeInBytes = "sizeInBytes";
        public static final String cdn_path = "cdn_path";
        public static final String filePath = "filePath";
        public static final String downloadStatus = "downloadStatus";


        public static final String[] cols = new String[]{id, name, type,
                sizeInBytes, cdn_path, filePath,downloadStatus};
    }


}
