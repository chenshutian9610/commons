package org.tree.commons.support.mapper;

import java.util.ArrayList;
import java.util.List;

/**
 * @author er_dong_chen
 * @date 18-12-14
 */
public abstract class Args {
    protected List<Arg> args = new ArrayList<>();

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (Arg arg : args) {
            if (arg.contained == false)
                continue;
            sb.append(arg.argName);
            sb.append(",");
        }
        if(sb.length()==0)
            return "*";
        return sb.substring(0, sb.length() - 1);
    }

    public static class Arg {
        private boolean contained = false;
        private String argName;

        public Arg(String argName) {
            this.argName = argName;
        }

        public void setContained(boolean contained) {
            this.contained = contained;
        }
    }
}
