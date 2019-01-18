package org.tree.commons.support.mapper;

import java.util.ArrayList;
import java.util.List;

/**
 * @author er_dong_chen
 * @date 18-12-14
 */
public abstract class Args {
    protected List<Arg> args = new ArrayList<>();

//    protected void init0(String... args) {
//        for (String arg : args) {
//            for (Arg obj : this.args) {
//                if (obj.getJavaProperty().equals(arg)) {
//                    obj.setContained(true);
//                    break;
//                }
//            }
//        }
//    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (Arg arg : args) {
            if (arg.contained == false)
                continue;
            sb.append(arg.argName);
            sb.append(",");
        }
        if (sb.length() == 0)
            return "*";
        return sb.substring(0, sb.length() - 1);
    }

    public static class Arg {
        private boolean contained = false;
        private String argName;
//        private String javaProperty;

        public Arg(String argName) {
            this.argName = argName;
        }

//        public Arg(String argName, String javaProperty) {
//            this.argName = argName;
//            this.javaProperty = javaProperty;
//        }

//        public String getJavaProperty() {
//            return this.javaProperty;
//        }

        public void setContained(boolean contained) {
            this.contained = contained;
        }
    }
}
