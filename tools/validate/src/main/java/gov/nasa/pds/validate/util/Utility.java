package gov.nasa.pds.validate.util;

import java.util.List;

public class Utility {
    public static List<String> removeQuotes(List<String> list) {
        for(int i=0; i < list.size(); i++) {
            list.set(i, list.get(i).toString().replace('"', ' ')
                    .trim());
        }
        return list;
    }
}
